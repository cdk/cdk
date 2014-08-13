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
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.SymbolVisibility;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.color.UniColor;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
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
 * diagram. These are generated together allowing the bonds to drawn cleanly without overlap. The
 * generate is heavily based on ideas documented in {@cdk.cite Brecher08} and {@cdk.cite Clark13}.
 *
 * <p/>
 *
 * Atom symbols are provided as {@link GeneralPath} outlines. This allows the depiction to be
 * independent of the system used to view the diagram (primarily important for vector graphic
 * depictions). The font used to generate the diagram must be provided to the constructor. <p/>
 *
 * Atoms and bonds can be highlighted by setting the {@link #HIGHLIGHT_COLOR}. The style of 
 * highlight is set with the {@link Highlighting} parameter.
 *
 * @author John May
 */
public final class StandardGenerator implements IGenerator<IAtomContainer> {


    /**
     * Defines that a chem object should be highlighted in a depiction. Only atom symbols that are
     * displayed are highlighted, the visibility of symbols can be modified with {@link
     * SymbolVisibility}.
     *
     * <pre>{@code
     * atom.setProperty(CDKConstants.HIGHLIGHT_COLOR, Color.RED);
     * }</pre>
     */
    public final static String HIGHLIGHT_COLOR = "stdgen.highlight.color";

    private final Font                  font;
    private final StandardAtomGenerator atomGenerator;

    /**
     * Enumeration of highlight style.
     */
    public static enum HighlightStyle {

        /**
         * Ignore highlight hints.
         */
        None,

        /**
         * Displayed atom symbols and bonds are coloured.
         */
        Colored,

        /**
         * An outer glow is placed in the background behind the depiction.
         * @see StandardGenerator.OuterGlowWidth
         */
        OuterGlow
    }

    private final IGeneratorParameter<?> atomColor = new AtomColor(),
            visibility                             = new Visibility(),
            strokeRatio                            = new StrokeRatio(),
            separationRatio                        = new BondSeparation(),
            wedgeRatio                             = new WedgeRatio(),
            marginRatio                            = new SymbolMarginRatio(),
            hatchSections                          = new HashSpacing(),
            dashSections                           = new DashSection(),
            waveSections                           = new WaveSpacing(),
            fancyBoldWedges                        = new FancyBoldWedges(),
            fancyHashedWedges                      = new FancyHashedWedges(),
            highlighting                           = new Highlighting(),
            glowWidth                              = new OuterGlowWidth();

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

        final HighlightStyle style = parameters.get(Highlighting.class);
        final double glowWidth = parameters.get(OuterGlowWidth.class);

        ElementGroup backLayer = new ElementGroup();
        ElementGroup middleLayer = new ElementGroup();
        ElementGroup frontLayer = new ElementGroup();

        // bond elements can simply be added to the element group
        for (int i = 0; i < container.getBondCount(); i++) {

            IBond bond = container.getBond(i);

            Color highlight = getColorProperty(bond, HIGHLIGHT_COLOR);
            if (highlight != null && style == HighlightStyle.OuterGlow) {
                backLayer.add(outerGlow(bondElements[i], highlight, glowWidth, stroke));
            }
            if (highlight != null && style == HighlightStyle.Colored) {
                frontLayer.add(recolor(bondElements[i], highlight));
            }
            else {
                middleLayer.add(bondElements[i]);
            }
        }

        // convert the atom symbols to IRenderingElements
        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtom atom = container.getAtom(i);

            if (symbols[i] == null) {
                updateBounds(bounds, atom.getPoint2d().x, atom.getPoint2d().y);
                continue;
            }

            Color highlight = getColorProperty(atom, HIGHLIGHT_COLOR);
            Color color = highlight != null && style == HighlightStyle.Colored ? highlight 
                                                                               : coloring.getAtomColor(atom);

            ElementGroup symbolElements = new ElementGroup();
            for (Shape shape : symbols[i].getOutlines()) {
                GeneralPath path = GeneralPath.shapeOf(shape, color);
                updateBounds(bounds, path);
                symbolElements.add(path);
            }

            if (highlight != null && style == HighlightStyle.OuterGlow) {
                backLayer.add(outerGlow(symbolElements, highlight, glowWidth, stroke));
            }

            if (highlight != null && style == HighlightStyle.Colored) {
                frontLayer.add(symbolElements);
            }
            else {
                middleLayer.add(symbolElements);
            }
        }

        ElementGroup group = new ElementGroup();

        group.add(new Bounds(bounds.getMinX(), bounds.getMinY(),
                             bounds.getMaxX(), bounds.getMaxY()));
        group.add(backLayer);
        group.add(middleLayer);
        group.add(frontLayer);

        return group;
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
        return Arrays.asList(atomColor,
                             visibility,
                             strokeRatio,
                             separationRatio,
                             wedgeRatio,
                             marginRatio,
                             hatchSections,
                             dashSections,
                             waveSections,
                             fancyBoldWedges,
                             fancyHashedWedges,
                             highlighting,
                             glowWidth);
    }


    /**
     * Safely access a chem object color property for a chem object.
     *
     * @param object chem object
     * @return the highlight color
     * @throws java.lang.IllegalArgumentException the highlight property was set but was not a
     *                                            {@link Color} instance
     */
    static Color getColorProperty(IChemObject object, String key) {
        Object value = object.getProperty(key);
        if (value instanceof Color)
            return (Color) value;
        if (value != null)
            throw new IllegalArgumentException(key + " property should be a java.awt.Color");
        return null;
    }

    /**
     * Recolor a rendering element after it has been generated. Since rendering elements are
     * immutable, the input element remains unmodified.
     *
     * @param element the rendering element
     * @param color   the new color
     * @return recolored rendering element
     */
    private static IRenderingElement recolor(IRenderingElement element, Color color) {
        if (element instanceof ElementGroup) {
            ElementGroup orgGroup = (ElementGroup) element;
            ElementGroup newGroup = new ElementGroup();
            for (IRenderingElement child : orgGroup) {
                newGroup.add(recolor(child, color));
            }
            return newGroup;
        }
        else if (element instanceof LineElement) {
            LineElement lineElement = (LineElement) element;
            return new LineElement(lineElement.firstPointX,
                                   lineElement.firstPointY,
                                   lineElement.secondPointX,
                                   lineElement.secondPointY,
                                   lineElement.width,
                                   color);
        }
        else if (element instanceof GeneralPath) {
            return ((GeneralPath) element).recolor(color);
        }
        throw new IllegalArgumentException("Cannot highlight rendering element, " + element.getClass());
    }

    /**
     * Generate an outer glow for the provided rendering element. The glow is defined by the glow
     * width and the stroke size.
     *
     * @param element   rendering element
     * @param color     color of the glow
     * @param glowWidth the width of the glow
     * @param stroke    the stroke width
     * @return generated outer glow
     */
    private static IRenderingElement outerGlow(IRenderingElement element, Color color, double glowWidth, double stroke) {
        if (element instanceof ElementGroup) {
            ElementGroup orgGroup = (ElementGroup) element;
            ElementGroup newGroup = new ElementGroup();
            for (IRenderingElement child : orgGroup) {
                newGroup.add(outerGlow(child, color, glowWidth, stroke));
            }
            return newGroup;
        }
        else if (element instanceof LineElement) {
            LineElement lineElement = (LineElement) element;
            return new LineElement(lineElement.firstPointX,
                                   lineElement.firstPointY,
                                   lineElement.secondPointX,
                                   lineElement.secondPointY,
                                   stroke + (2 * (glowWidth * stroke)),
                                   color);
        }
        else if (element instanceof GeneralPath) {
            GeneralPath org = (GeneralPath) element;
            if (org.fill) {
                return org.outline(2 * (glowWidth * stroke)).recolor(color);
            }
            else {
                return org.outline(stroke + (2 * (glowWidth * stroke))).recolor(color);
            }
        }
        throw new IllegalArgumentException("Cannot generate glow for rendering element, " + element.getClass());
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
     * SymbolVisibility#iupacRecommendationsWithoutTerminalCarbon()} wrapped with {@link
     * SelectionVisibility#disconnected(SymbolVisibility)}.
     */
    public static final class Visibility extends AbstractGeneratorParameter<SymbolVisibility> {
        /**
         * @inheritDoc
         */
        @Override public SymbolVisibility getDefault() {
            return SelectionVisibility.disconnected(SymbolVisibility.iupacRecommendationsWithoutTerminalCarbon());
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
     * Defines the ratio of the separation between lines in double bonds as a percentage of length
     * ({@link BasicSceneGenerator.BondLength}). The default value is 18% (0.18).
     */
    public static final class BondSeparation extends AbstractGeneratorParameter<Double> {
        /**
         * @inheritDoc
         */
        @Override public Double getDefault() {
            return 0.18;
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
            return 6d;
        }
    }

    /**
     * The preferred spacing between lines in hashed bonds. The number of hashed sections displayed
     * is then {@link BasicSceneGenerator.BondLength} / spacing. The default value is 5.
     */
    public static final class HashSpacing extends AbstractGeneratorParameter<Double> {
        /**
         * @inheritDoc
         */
        @Override public Double getDefault() {
            return 5d;
        }
    }

    /**
     * The spacing of waves (semi circles) drawn in wavy bonds with. Default = 5.
     */
    public static final class WaveSpacing extends AbstractGeneratorParameter<Double> {
        /**
         * @inheritDoc
         */
        @Override public Double getDefault() {
            return 5d;
        }
    }

    /**
     * The number of sections to render in a dashed 'unknown' bond, default = 4;
     */
    public static final class DashSection extends AbstractGeneratorParameter<Integer> {
        /**
         * @inheritDoc
         */
        @Override public Integer getDefault() {
            return 8;
        }
    }

    /**
     * Modify bold wedges to be flush with adjacent bonds, default = true.
     */
    public static final class FancyBoldWedges extends AbstractGeneratorParameter<Boolean> {
        /** @inheritDoc */
        @Override public Boolean getDefault() {
            return true;
        }
    }

    /**
     * Modify hashed wedges to be flush when there is a single adjacent bond, default = true.
     */
    public static final class FancyHashedWedges extends AbstractGeneratorParameter<Boolean> {
        /** @inheritDoc */
        @Override public Boolean getDefault() {
            return true;
        }
    }


    /**
     * The width of outer glow as a percentage of stroke width. The default value is 200% (2.0d).
     * This means the bond outer glow, is 5 times the width as the glow extends to twice the width
     * on each side.
     */
    public static final class OuterGlowWidth extends AbstractGeneratorParameter<Double> {
        /** @inheritDoc */
        @Override public Double getDefault() {
            return 2d;
        }
    }


    /**
     * Parameter defines the style of highlight used to emphasis atoms and bonds. The
     * default option is to color the atom and bond symbols ({@link HighlightStyle#Colored}).
     */
    public static final class Highlighting extends AbstractGeneratorParameter<HighlightStyle> {
        /** @inheritDoc */
        @Override public HighlightStyle getDefault() {
            return HighlightStyle.Colored;
        }
    }

}
