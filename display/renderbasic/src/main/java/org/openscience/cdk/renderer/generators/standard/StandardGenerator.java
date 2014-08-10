/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *   
 * Contact: cdk-devel@lists.sourceforge.net
 *   
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above 
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.renderer.generators.standard;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.SymbolVisibility;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.color.UniColor;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.path.PathElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;

import javax.vecmath.Point2d;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

import static org.openscience.cdk.renderer.generators.standard.HydrogenPosition.Left;

/**
 * The standard generator creates {@link IRenderingElement}s for the atoms and bonds of a structure
 * diagram. These are generated together allowing the bonds to drawn cleanly without overlap. <p/>
 *
 * Atom symbols are provided as {@link GeneralPath} outlines. This allows the depiction to be
 * independent of the system used to view the diagram (primarily important for vector graphic
 * depictions). The font used to generate the diagram must be provided to the constructor. <p/>
 *
 * @author John May
 */
public final class StandardGenerator implements IGenerator<IAtomContainer> {

    private final Font                  font;
    private final StandardAtomGenerator atomGenerator;

    private final IGeneratorParameter<?> atomColor = new AtomColor(),
            visibility                             = new Visibility(),
            strokeRatio                            = new StrokeRatio(),
            separationRatio                        = new SeparationRatio(),
            wedgeRatio                             = new WedgeRatio(),
            marginRatio                            = new SymbolMarginRatio(),
            hatchSections                          = new HatchSections();

    /**
     * Create a new standard generator that utilises the specified font to display atom symbols.
     *
     * @param font the font family, size, and style
     */
    public StandardGenerator(Font font) {
        this.font = font;
        this.atomGenerator = new StandardAtomGenerator(font);
    }

    /**
     * @inheritDoc
     */
    @Override public IRenderingElement generate(IAtomContainer container, RendererModel parameters) {

        if (container.getAtomCount() == 0)
            return new ElementGroup();

        final double scale = parameters.get(BasicSceneGenerator.Scale.class);

        final SymbolVisibility visibility = parameters.get(Visibility.class);
        final IAtomColorer coloring = parameters.get(AtomColor.class);

        // the stroke width is based on the font. a better method is needed to get
        // the exact font stroke but for now we use the width of the pipe character.
        final double fontStroke = new TextOutline("|", font).resize(1 / scale, 1 / scale).getBounds().getWidth();
        final double stroke = parameters.get(StrokeRatio.class) * fontStroke;

        AtomSymbol[] symbols = generateAtomSymbols(container, visibility, scale);
        IRenderingElement[] bondElements = StandardBondGenerator.generateBonds(container, symbols, parameters, stroke);

        Rectangle2D bounds = new Rectangle2D.Double(container.getAtom(0).getPoint2d().x,
                                                    container.getAtom(0).getPoint2d().y,
                                                    0, 0);

        ElementGroup elements = new ElementGroup();

        // bond elements can simply be added to the element group
        for (IRenderingElement bondElement : bondElements) {
            elements.add(bondElement);
        }

        // convert the atom symbols to IRenderingElements
        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtom atom = container.getAtom(i);

            if (symbols[i] == null) {
                updateBounds(bounds, atom.getPoint2d().x, atom.getPoint2d().y);
                continue;
            }

            Color color = coloring.getAtomColor(atom);
            ElementGroup symbolElements = new ElementGroup();
            for (Shape shape : symbols[i].getOutlines()) {
                GeneralPath path = GeneralPath.shapeOf(shape, color);
                updateBounds(bounds, path);
                symbolElements.add(path);
            }
            elements.add(symbolElements);
        }

        elements.add(new Bounds(bounds.getMinX(), bounds.getMinY(),
                                bounds.getMaxX(), bounds.getMaxY()));

        return elements;
    }

    /**
     * Generate the intermediate {@link AtomSymbol} instances.
     *
     * @param container  structure representation
     * @param visibility defines whether an atom symbol is displayed
     * @param scale      the CDK scaling value
     * @return generated atom symbols (can contain null)
     */
    private AtomSymbol[] generateAtomSymbols(IAtomContainer container, SymbolVisibility visibility, double scale) {

        AtomSymbol[] symbols = new AtomSymbol[container.getAtomCount()];

        for (int i = 0; i < container.getAtomCount(); i++) {

            final IAtom atom = container.getAtom(i);
            final List<IBond> bonds = container.getConnectedBondsList(atom);
            final List<IAtom> neighbors = container.getConnectedAtomsList(atom);

            // only generate if the symbol is visible
            if (!visibility.visible(atom, bonds))
                continue;

            final HydrogenPosition hPosition = HydrogenPosition.position(atom, neighbors);

            symbols[i] = atomGenerator.generateSymbol(container, atom, hPosition);

            // defines how the element is aligned on the atom point, when
            // aligned to the left, the first character 'e.g. Cl' is used.
            if (neighbors.size() == 1) {
                if (hPosition == Left)
                    symbols[i] = symbols[i].alignTo(AtomSymbol.SymbolAlignment.Right);
                else
                    symbols[i] = symbols[i].alignTo(AtomSymbol.SymbolAlignment.Left);
            }

            final Point2d p = atom.getPoint2d();

            if (p == null)
                throw new IllegalArgumentException("Atom did not have 2D coordinates");

            // center and scale the symbol, y-axis scale is inverted because CDK y-axis
            // is inverse of Java 2D
            symbols[i] = symbols[i].resize(1 / scale, 1 / -scale)
                                   .center(p.x, p.y);
        }

        return symbols;
    }

    /**
     * @inheritDoc
     */
    @Override public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.<IGeneratorParameter<?>>asList(atomColor,
                                                     visibility,
                                                     strokeRatio,
                                                     separationRatio,
                                                     wedgeRatio,
                                                     marginRatio,
                                                     hatchSections);
    }

    /**
     * Updating the bounds such that it contains every point in the provided path.
     *
     * @param bounds bounding box
     * @param path   the path
     */
    private static void updateBounds(Rectangle2D bounds, GeneralPath path) {

        double minX = Integer.MAX_VALUE;
        double maxX = Integer.MIN_VALUE;
        double minY = Integer.MAX_VALUE;
        double maxY = Integer.MIN_VALUE;

        double[] points = new double[6];
        double x = 0, y = 0;
        for (PathElement element : path.elements) {
            element.points(points);

            switch (element.type()) {
                case MoveTo:
                case LineTo:
                    x = points[0];
                    y = points[1];
                    break;
                case QuadTo:
                    x = points[2];
                    y = points[3];
                    break;
                case CubicTo:
                    x = points[4];
                    y = points[5];
                    break;
            }
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        updateBounds(bounds, minX, minY);
        updateBounds(bounds, maxX, maxY);
    }

    /**
     * Updating the bounds such that it contains the point {x, y}.
     *
     * @param bounds bounding box
     * @param x      x-axis coordinate
     * @param y      y-axis coordinate
     */
    private static void updateBounds(Rectangle2D bounds, double x, double y) {
        double minX = Math.min(x, bounds.getMinX());
        double maxX = Math.max(x, bounds.getMaxX());
        double minY = Math.min(y, bounds.getMinY());
        double maxY = Math.max(y, bounds.getMaxY());
        bounds.setRect(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Defines the color of unselected atoms (and bonds). Bonds colored is defined by the carbon
     * color. The default option is uniform black coloring as recommended by IUPAC.
     */
    public static final class AtomColor extends AbstractGeneratorParameter<IAtomColorer> {
        /**
         * @inheritDoc
         */
        @Override public IAtomColorer getDefault() {
            // off black
            return new UniColor(new Color(0x444444));
        }
    }

    /**
     * Defines which atoms have their symbol displayed. The default option is {@link
     * SymbolVisibility#iupacRecommendations()}
     */
    public static final class Visibility extends AbstractGeneratorParameter<SymbolVisibility> {
        /**
         * @inheritDoc
         */
        @Override public SymbolVisibility getDefault() {
            return SymbolVisibility.iupacRecommendations();
        }
    }

    /**
     * Defines the ratio of the stroke to the width of the stroke of the font used to depict atom
     * symbols. Default = 1.
     */
    public static final class StrokeRatio extends AbstractGeneratorParameter<Double> {
        /**
         * @inheritDoc
         */
        @Override public Double getDefault() {
            return 1d;
        }
    }

    /**
     * Defines the ratio of the separation between lines in double bonds compared to the stroke.
     * Default = 5.
     */
    public static final class SeparationRatio extends AbstractGeneratorParameter<Double> {
        /**
         * @inheritDoc
         */
        @Override public Double getDefault() {
            return 5d;
        }
    }

    /**
     * Defines the margin between an atom symbol and a connected bond based on the stroke width.
     * Default = 2.
     */
    public static final class SymbolMarginRatio extends AbstractGeneratorParameter<Double> {
        /**
         * @inheritDoc
         */
        @Override public Double getDefault() {
            return 2d;
        }
    }

    /**
     * Ratio of the wide end of wedge compared to the narrow end (stroke width). Default = 8.
     */
    public static final class WedgeRatio extends AbstractGeneratorParameter<Double> {
        /**
         * @inheritDoc
         */
        @Override public Double getDefault() {
            return 8d;
        }
    }


    /**
     * The number of sections to render a hatch bond of default bond length. Default = 8.
     */
    public static final class HatchSections extends AbstractGeneratorParameter<Integer> {
        /**
         * @inheritDoc
         */
        @Override public Integer getDefault() {
            return 8;
        }
    }

}
