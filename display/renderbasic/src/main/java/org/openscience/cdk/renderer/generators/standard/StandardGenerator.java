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
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.SymbolVisibility;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.color.UniColor;
import org.openscience.cdk.renderer.elements.Bounds;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.MarkedElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openscience.cdk.renderer.generators.standard.HydrogenPosition.Left;

/**
 * The standard generator creates {@link IRenderingElement}s for the atoms and bonds of a structure
 * diagram. These are generated together allowing the bonds to drawn cleanly without overlap. The
 * generate is heavily based on ideas documented in {@cdk.cite Brecher08} and {@cdk.cite Clark13}.
 *
 * 
 *
 * Atom symbols are provided as {@link GeneralPath} outlines. This allows the depiction to be
 * independent of the system used to view the diagram (primarily important for vector graphic
 * depictions). The font used to generate the diagram must be provided to the constructor. 
 *
 * Atoms and bonds can be highlighted by setting the {@link #HIGHLIGHT_COLOR}. The style of
 * highlight is set with the {@link Highlighting} parameter.
 *
 * 
 *
 * The <a href="https://github.com/cdk/cdk/wiki/Standard-Generator">Standard Generator - CDK Wiki
 * page</a> provides extended details of using and configuring this generator.
 *
 * @author John May
 * @see <a href="https://github.com/cdk/cdk/wiki/Standard-Generator">Standard Generator - CDK
 * Wiki</a>
 */
public final class StandardGenerator implements IGenerator<IAtomContainer> {

    /**
     * Defines that a chem object should be highlighted in a depiction. Only atom symbols that are
     * displayed are highlighted, the visibility of symbols can be modified with {@link
     * SymbolVisibility}.
     *
     * <pre>{@code
     * atom.setProperty(StandardGenerator.HIGHLIGHT_COLOR, Color.RED);
     * }</pre>
     */
    public final static String          HIGHLIGHT_COLOR       = "stdgen.highlight.color";

    /**
     * Defines the annotation label(s) of a chem object in a depiction. The annotation
     * must be a string.
     *
     * <pre>{@code
     * String number = Integer.toString(1 + container.indexOf(atom));
     * atom.setProperty(CDKConstants.ANNOTATION_LABEL, number);
     * }</pre>
     */
    public final static String          ANNOTATION_LABEL      = "stdgen.annotation.label";

    /**
     * A special markup for annotation labels that hints the generator to renderer
     * the annotation label in italic. The primary use case is for Cahn-Ingold-Prelog
     * descriptors.
     *
     * <pre>{@code
     * String cipLabel = "R";
     * atom.setProperty(CDKConstants.ANNOTATION_LABEL,
     *                  StandardGenerator.ITALIC_DISPLAY_PREFIX + cipLabel);
     * }</pre>
     */
    public final static String          ITALIC_DISPLAY_PREFIX = "std.itl:";

    /**
     * Marks atoms and bonds as being hidden from the actual depiction. Set this
     * property to non-null to indicate this.
     */
    public final static String          HIDDEN       = "stdgen.hidden";
    public final static String          HIDDEN_FULLY = "stdgen.hidden.fully";

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
         *
         * @see StandardGenerator.OuterGlowWidth
         */
        OuterGlow,

        /**
         * Same as outer glow but puts a white edge around element symbols.
         * This is useful if color atoms are used in combination with an
         * outer glow highlight.
         *
         * @see StandardGenerator.OuterGlowWidth
         */
        OuterGlowWhiteEdge
    }

    private final IGeneratorParameter<?> atomColor = new AtomColor(), visibility = new Visibility(),
            strokeRatio = new StrokeRatio(), separationRatio = new BondSeparation(), wedgeRatio = new WedgeRatio(),
            marginRatio = new SymbolMarginRatio(), hatchSections = new HashSpacing(), dashSections = new DashSection(),
            waveSections      = new WaveSpacing(), fancyBoldWedges = new FancyBoldWedges(),
            fancyHashedWedges = new FancyHashedWedges(), highlighting = new Highlighting(),
            glowWidth         = new OuterGlowWidth(), annCol = new AnnotationColor(), annDist = new AnnotationDistance(),
            annFontSize       = new AnnotationFontScale(), sgroupBracketDepth = new SgroupBracketDepth(),
            sgroupFontScale   = new SgroupFontScale(), omitMajorIsotopes = new OmitMajorIsotopes(),
            forceDelocalised  = new ForceDelocalisedBondDisplay(), delocaliseDonuts = new DelocalisedDonutsBondDisplay();

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
     *{@inheritDoc}
     */
    @Override
    public IRenderingElement generate(IAtomContainer container, RendererModel parameters) {

        if (container.getAtomCount() == 0) return new ElementGroup();

        Map<IAtom,String> symbolRemap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, symbolRemap);

        final double scale = parameters.get(BasicSceneGenerator.Scale.class);

        final SymbolVisibility visibility = parameters.get(Visibility.class);
        final IAtomColorer coloring = parameters.get(AtomColor.class);
        final Color annotationColor = parameters.get(AnnotationColor.class);
        final Color foreground = coloring.getAtomColor(container.getBuilder().newInstance(IAtom.class, "C"));


        // the stroke width is based on the font. a better method is needed to get
        // the exact font stroke but for now we use the width of the pipe character.
        final double fontStroke = new TextOutline("|", font).resize(1 / scale, 1 / scale).getBounds().getWidth();
        final double stroke = parameters.get(StrokeRatio.class) * fontStroke;

        ElementGroup annotations = new ElementGroup();

        StandardDonutGenerator donutGenerator;
        donutGenerator = new StandardDonutGenerator(container, font, parameters,
                                                    stroke);
        IRenderingElement donuts = donutGenerator.generate();

        AtomSymbol[] symbols = generateAtomSymbols(container, symbolRemap,
                                                   visibility, parameters,
                                                   annotations, foreground,
                                                   stroke, donutGenerator);
        IRenderingElement[] bondElements;
        bondElements = StandardBondGenerator.generateBonds(container, symbols,
                                                           parameters, stroke,
                                                           font, annotations,
                                                           donutGenerator);

        final HighlightStyle style = parameters.get(Highlighting.class);
        final double glowWidth = parameters.get(OuterGlowWidth.class);

        ElementGroup backLayer = new ElementGroup();
        ElementGroup middleLayer = new ElementGroup();
        ElementGroup frontLayer = new ElementGroup();

        // bond elements can simply be added to the element group
        for (int i = 0; i < container.getBondCount(); i++) {

            IBond bond = container.getBond(i);

            if (isHidden(bond))
                continue;

            Color highlight = getHighlightColor(bond, parameters);
            if (highlight != null && (style == HighlightStyle.OuterGlow || style == HighlightStyle.OuterGlowWhiteEdge)) {
                backLayer.add(MarkedElement.markup(outerGlow(bondElements[i], highlight, glowWidth, stroke), "outerglow"));
            }
            if (highlight != null && style == HighlightStyle.Colored) {
                frontLayer.add(MarkedElement.markupBond(recolor(bondElements[i], highlight), bond));
            } else {
                middleLayer.add(MarkedElement.markupBond(bondElements[i], bond));
            }
        }

        // bonds for delocalised aromatic
        frontLayer.add(donuts);

        // convert the atom symbols to IRenderingElements
        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtom atom = container.getAtom(i);

            if (isHidden(atom))
                continue;

            Color highlight = getHighlightColor(atom, parameters);
            Color color = getColorOfAtom(symbolRemap, coloring, foreground, style, atom, highlight);

            if (symbols[i] == null) {
                // we add a 'ball' around atoms with no symbols (e.g. carbons)
                if (highlight != null && (style == HighlightStyle.OuterGlow || style == HighlightStyle.OuterGlowWhiteEdge)) {
                	double glowWidthExt = glowWidth;
                	if (style == HighlightStyle.OuterGlowWhiteEdge && highlight.equals(Color.WHITE)) {
                		glowWidthExt *= 1.75;
                	}
                    backLayer.add(MarkedElement.markup(new OvalElement(atom.getPoint2d().x, atom.getPoint2d().y,1.75 * glowWidthExt * stroke, true, highlight),
                                                       "outerglow"));
                }
                continue;
            }

            ElementGroup symbolElements = new ElementGroup();
            for (Shape shape : symbols[i].getOutlines()) {
                GeneralPath path = GeneralPath.shapeOf(shape, color);
                symbolElements.add(path);
            }

            // add the annotations of the symbol to the annotations ElementGroup
            for (Shape shape : symbols[i].getAnnotationOutlines()) {
                annotations.add(MarkedElement.markup(GeneralPath.shapeOf(shape, annotationColor), "annotation"));
            }

            if (highlight != null && (style == HighlightStyle.OuterGlow || style == HighlightStyle.OuterGlowWhiteEdge)) {
            	double glowWidthExt = glowWidth;
            	if (style == HighlightStyle.OuterGlowWhiteEdge && highlight.equals(Color.WHITE)) {
            		glowWidthExt *= 1.75;
            	}
                backLayer.add(MarkedElement.markup(outerGlow(symbolElements, highlight, glowWidthExt, stroke), "outerglow"));
            }

            if (highlight != null && style == HighlightStyle.Colored) {
                frontLayer.add(MarkedElement.markupAtom(symbolElements, atom));
            } else {
                middleLayer.add(MarkedElement.markupAtom(symbolElements, atom));
            }
        }
        
        if (style == HighlightStyle.OuterGlowWhiteEdge) {
	        for (int i = 0; i < container.getAtomCount(); i++) {
	            IAtom atom = container.getAtom(i);
	
	            if (isHidden(atom))
	                continue;
	
	            Color highlight = getHighlightColor(atom, parameters);
	            Color color = highlight != null ? highlight : coloring.getAtomColor(atom);

	            if (symbols[i] == null) {
	                continue;
	            }
	
	            ElementGroup symbolElements = new ElementGroup();
	            for (Shape shape : symbols[i].getOutlines()) {
	                GeneralPath path = GeneralPath.shapeOf(shape, color);
	                symbolElements.add(path);
	            }
	
	            if (highlight != null && !highlight.equals(Color.WHITE)) {
	            	backLayer.add(MarkedElement.markup(outerGlow(symbolElements, Color.WHITE, 10*stroke, stroke), "outerglow"));
	            }
	        }  
        }

        // Add the Sgroups display elements to the front layer
        IRenderingElement sgroups = StandardSgroupGenerator.generate(parameters, stroke, font, foreground, atomGenerator, symbols, container);
        frontLayer.add(sgroups);

        // Annotations are added to the front layer.
        frontLayer.add(annotations);

        ElementGroup group = new ElementGroup();

        group.add(backLayer);
        group.add(middleLayer);
        group.add(frontLayer);

        return MarkedElement.markupMol(group, container);
    }

    private Color getColorOfAtom(Map<IAtom, String> symbolRemap, IAtomColorer coloring, Color foreground,
                                 HighlightStyle style, IAtom atom, Color highlight) {
        // atom is highlighted...?
        if (highlight != null && style == HighlightStyle.Colored)
            return highlight;
        // abbreviations default to foreground color
        if (symbolRemap.containsKey(atom))
            return foreground;
        // use the atom colorer
        return coloring.getAtomColor(atom);
    }

    /**
     * Generate the intermediate {@link AtomSymbol} instances.
     *
     * @param container  structure representation
     * @param symbolRemap use alternate symbols (used for Sgroup shortcuts)
     * @param visibility defines whether an atom symbol is displayed
     * @param parameters render model parameters   @return generated atom symbols (can contain null)
     */
    private AtomSymbol[] generateAtomSymbols(IAtomContainer container,
                                             Map<IAtom, String> symbolRemap,
                                             SymbolVisibility visibility,
                                             RendererModel parameters,
                                             ElementGroup annotations,
                                             Color foreground,
                                             double stroke,
                                             StandardDonutGenerator donutGen) {

        final double scale = parameters.get(BasicSceneGenerator.Scale.class);
        final double annDist = parameters.get(AnnotationDistance.class)
                               * (parameters.get(BasicSceneGenerator.BondLength.class) / scale);
        final double annScale = (1 / scale) * parameters.get(AnnotationFontScale.class);
        final Color annColor = parameters.get(AnnotationColor.class);
        final double halfStroke = stroke / 2;

        AtomSymbol[] symbols = new AtomSymbol[container.getAtomCount()];
        IChemObjectBuilder builder = container.getBuilder();

        // check if we should annotate attachment point numbers (maxAttach>1)
        // and queue them all up for processing
        List<IPseudoAtom> attachPoints = new ArrayList<>();
        int maxAttach = 0;

        for (int i = 0; i < container.getAtomCount(); i++) {

            final IAtom atom = container.getAtom(i);

            if (isHiddenFully(atom))
                continue;

            if (atom instanceof IPseudoAtom) {
                int attachNum = ((IPseudoAtom) atom).getAttachPointNum();
                if (attachNum > 0)
                    attachPoints.add((IPseudoAtom) atom);
                if (attachNum > maxAttach)
                    maxAttach = attachNum;
            }

            boolean remapped = symbolRemap.containsKey(atom);
            final List<IBond> bonds     = container.getConnectedBondsList(atom);
            final List<IAtom> neighbors = container.getConnectedAtomsList(atom);
            final List<IAtom> visNeighbors = new ArrayList<>();

            // if a symbol is remapped we only want to consider
            // visible neighbors in the alignment calc, otherwise
            // we include all neighbors
            for (IAtom neighbor : neighbors) {
                if (!remapped || !isHidden(neighbor))
                    visNeighbors.add(neighbor);
            }

            final List<Vector2d> auxVectors = new ArrayList<>(1);

            // only generate if the symbol is visible
            if (visibility.visible(atom, bonds, parameters) || remapped) {

                final HydrogenPosition hPosition = HydrogenPosition.position(atom, visNeighbors);

                if (atom.getImplicitHydrogenCount() != null && atom.getImplicitHydrogenCount() > 0)
                    auxVectors.add(hPosition.vector());

                if (remapped) {
                    symbols[i] = atomGenerator.generateAbbreviatedSymbol(symbolRemap.get(atom), hPosition);
                } else if (donutGen.isChargeDelocalised(atom)) {
                    Integer charge = atom.getFormalCharge();
                    atom.setFormalCharge(0);
                    // can't think of a better way to handle this without API
                    // change to symbol visibility
                    if (atom.getAtomicNumber() != 6)
                        symbols[i] = atomGenerator.generateSymbol(container, atom, hPosition, parameters);
                    atom.setFormalCharge(charge);
                } else
                    symbols[i] = atomGenerator.generateSymbol(container, atom, hPosition, parameters);

                if (symbols[i] != null) {

                    // defines how the element is aligned on the atom point, when
                    // aligned to the left, the first character 'e.g. Cl' is used.
                    if (visNeighbors.size() < 4) {
                        if (hPosition == Left) {
                            symbols[i] = symbols[i].alignTo(AtomSymbol.SymbolAlignment.Right);
                        } else {
                            symbols[i] = symbols[i].alignTo(AtomSymbol.SymbolAlignment.Left);
                        }
                    }

                    final Point2d p = atom.getPoint2d();

                    if (p == null) throw new IllegalArgumentException("Atom did not have 2D coordinates");

                    // center and scale the symbol, y-axis scale is inverted because CDK y-axis
                    // is inverse of Java 2D
                    symbols[i] = symbols[i].resize(1 / scale, 1 / -scale).center(p.x, p.y);
                }
            }

            String label = getAnnotationLabel(atom);

            if (label != null) {

                // to ensure consistent draw distance we need to adjust the annotation distance
                // depending on whether we are drawing next to an atom symbol or not.
                final double strokeAdjust = symbols[i] != null ? -halfStroke : 0;

                final Vector2d vector = newAtomAnnotationVector(atom, bonds, auxVectors);
                final TextOutline annOutline = generateAnnotation(atom.getPoint2d(), label, vector, annDist
                                                                                                    + strokeAdjust, annScale, font, symbols[i]);

                // the AtomSymbol may migrate during bond generation and therefore the annotation
                // needs to be tied to the symbol. If no symbol is available the annotation is
                // fixed and we can add it to the annotation ElementGroup right away.
                if (symbols[i] != null) {
                    symbols[i] = symbols[i].addAnnotation(annOutline);
                } else {
                    annotations.add(GeneralPath.shapeOf(annOutline.getOutline(), annColor));
                }
            }
        }

        // label attachment points
        if (maxAttach > 1) {

            List<TextOutline> attachNumOutlines = new ArrayList<>();
            double maxRadius = 0;

            for (IPseudoAtom atom : attachPoints) {
                int attachNum = atom.getAttachPointNum();

                // to ensure consistent draw distance we need to adjust the annotation distance
                // depending on whether we are drawing next to an atom symbol or not.
                final double strokeAdjust = -halfStroke;

                final Vector2d vector = newAttachPointAnnotationVector(atom,
                                                                       container.getConnectedBondsList(atom),
                                                                       new ArrayList<Vector2d>());

                final TextOutline outline = generateAnnotation(atom.getPoint2d(),
                                                                  Integer.toString(attachNum),
                                                                  vector,
                                                                  1.75 * annDist + strokeAdjust,
                                                                  annScale,
                                                                  font.deriveFont(Font.BOLD),
                                                                  null);

                attachNumOutlines.add(outline);

                double w = outline.getBounds().getWidth();
                double h = outline.getBounds().getHeight();
                double r = Math.sqrt(w * w + h * h)/2;
                if (r > maxRadius)
                    maxRadius = r;
            }

            for (TextOutline outline : attachNumOutlines) {
                ElementGroup group = new ElementGroup();
                double radius = 2*stroke + maxRadius;
                Shape shape = new Ellipse2D.Double(outline.getCenter().getX() - radius,
                                                   outline.getCenter().getY() - radius,
                                                   2*radius,
                                                   2*radius);
                Area area1 = new Area(shape);
                area1.subtract(new Area(outline.getOutline()));
                group.add(GeneralPath.shapeOf(area1, foreground));
                annotations.add(group);
            }

        }

        return symbols;
    }

    /**
     * Generate an annotation 'label' for an atom (located at 'basePoint'). The label is offset from
     * the basePoint by the provided 'distance' and 'direction'.
     *
     * @param basePoint the relative (0,0) reference
     * @param label     the annotation text
     * @param direction the direction along which the label is laid out
     * @param distance  the distance along the direct to travel
     * @param scale     the font scale of the label
     * @param font      the font to use
     * @param symbol    the atom symbol to avoid overlap with
     * @return the position text outline for the annotation
     */
    static TextOutline generateAnnotation(Point2d basePoint, String label, Vector2d direction, double distance,
            double scale, Font font, AtomSymbol symbol) {

        boolean italicHint = label.startsWith(ITALIC_DISPLAY_PREFIX);

        label = italicHint ? label.substring(ITALIC_DISPLAY_PREFIX.length()) : label;

        Font annFont = italicHint ? font.deriveFont(Font.ITALIC) : font;

        final TextOutline annOutline = new TextOutline(label, annFont).resize(scale, -scale);

        // align to the first or last character of the annotation depending on the direction
        final Point2D center = direction.x > 0.3 ? annOutline.getFirstGlyphCenter() : direction.x < -0.3 ? annOutline
                .getLastGlyphCenter() : annOutline.getCenter();

        // Avoid atom symbol
        if (symbol != null) {
            Point2D intersect = symbol.getConvexHull().intersect(VecmathUtil.toAwtPoint(basePoint),
                    VecmathUtil.toAwtPoint(new Point2d(VecmathUtil.sum(basePoint, direction))));
            // intersect should never be null be check against this
            if (intersect != null) basePoint = VecmathUtil.toVecmathPoint(intersect);
        }

        direction.scale(distance);
        direction.add(basePoint);

        // move to position
        return annOutline.translate(direction.x - center.getX(), direction.y - center.getY());
    }


    /**
     * Make an embedded text label for display in a CDK renderer. If a piece of text contains newlines
     * they are centred aligned below each other with a line height of 1.4.
     *
     * @param font  the font to embedded
     * @param text  the text label
     * @param color the color
     * @param scale the resize, should include the model scale
     * @return pre-rendered element
     */
    public static IRenderingElement embedText(Font font, String text, Color color, double scale) {

        final String[] lines = text.split("\n");

        ElementGroup group = new ElementGroup();

        double yOffset = 0;
        double lineHeight = 1.4d;

        for (String line : lines) {
            TextOutline outline = new TextOutline(line, font).resize(scale, -scale);
            Point2D center = outline.getCenter();
            outline = outline.translate(-center.getX(), -(center.getY() + yOffset));

            yOffset += lineHeight * outline.getBounds().getHeight();

            group.add(GeneralPath.shapeOf(outline.getOutline(), color));
            Rectangle2D logicalBounds = outline.getLogicalBounds();
            group.add(new Bounds(logicalBounds.getMinX(), logicalBounds.getMinY(),
                                 logicalBounds.getMaxX(), logicalBounds.getMaxY()));
        }

        return group;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(atomColor, visibility, strokeRatio, separationRatio, wedgeRatio, marginRatio,
                             hatchSections, dashSections, waveSections, fancyBoldWedges, fancyHashedWedges, highlighting, glowWidth,
                             annCol, annDist, annFontSize, sgroupBracketDepth, sgroupFontScale, omitMajorIsotopes, forceDelocalised, delocaliseDonuts);
    }

    static String getAnnotationLabel(IChemObject chemObject) {
        Object obj = chemObject.getProperty(ANNOTATION_LABEL);
        return obj != null ? obj.toString() : null;
    }

    private Color getHighlightColor(IChemObject bond, RendererModel parameters) {
        Color propCol = getColorProperty(bond, HIGHLIGHT_COLOR);

        if (propCol != null) {
            return propCol;
        }

        if (parameters.getSelection() != null && parameters.getSelection().contains(bond)) {
            return parameters.get(RendererModel.SelectionColor.class);
        }

        return null;
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
        if (value instanceof Color) return (Color) value;
        if (value != null) throw new IllegalArgumentException(key + " property should be a java.awt.Color");
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
        } else if (element instanceof LineElement) {
            LineElement lineElement = (LineElement) element;
            return new LineElement(lineElement.firstPointX, lineElement.firstPointY, lineElement.secondPointX,
                    lineElement.secondPointY, lineElement.width, color);
        } else if (element instanceof GeneralPath) {
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
    static IRenderingElement outerGlow(IRenderingElement element, Color color, double glowWidth, double stroke) {
        if (element instanceof ElementGroup) {
            ElementGroup orgGroup = (ElementGroup) element;
            ElementGroup newGroup = new ElementGroup();
            for (IRenderingElement child : orgGroup) {
                newGroup.add(outerGlow(child, color, glowWidth, stroke));
            }
            return newGroup;
        } else if (element instanceof LineElement) {
            LineElement lineElement = (LineElement) element;
            return new LineElement(lineElement.firstPointX, lineElement.firstPointY, lineElement.secondPointX,
                    lineElement.secondPointY, stroke + (2 * (glowWidth * stroke)), color);
        } else if (element instanceof GeneralPath) {
            GeneralPath org = (GeneralPath) element;
            if (org.fill) {
                return org.outline(2 * (glowWidth * stroke)).recolor(color);
            } else {
                return org.outline(stroke + (2 * (glowWidth * stroke))).recolor(color);
            }
        }
        throw new IllegalArgumentException("Cannot generate glow for rendering element, " + element.getClass());
    }

    /**
     * Generate a new annotation vector for an atom using the connected bonds and any other occupied
     * space (auxiliary vectors). The fall back method is to use the largest available space but
     * some common cases are handled differently. For example, when the number of bonds is two
     * the annotation is placed in the acute angle of the bonds (providing there is space). This
     * improves labelling of atoms saturated rings. When there are three bonds and two are 'plain'
     * the label is again placed in the acute section of the plain bonds.
     *
     * @param atom       the atom having an annotation
     * @param bonds      the bonds connected to the atom
     * @param auxVectors additional vectors to avoid (filled spaced)
     * @return unit vector along which the annotation should be placed.
     * @see #isPlainBond(org.openscience.cdk.interfaces.IBond)
     * @see VecmathUtil#newVectorInLargestGap(java.util.List)
     */
    static Vector2d newAtomAnnotationVector(IAtom atom, List<IBond> bonds, List<Vector2d> auxVectors) {

        final List<Vector2d> vectors = new ArrayList<Vector2d>(bonds.size());
        for (IBond bond : bonds)
            vectors.add(VecmathUtil.newUnitVector(atom, bond));

        if (vectors.size() == 0) {
            // no bonds, place below
            if (auxVectors.size() == 0) return new Vector2d(0, -1);
            if (auxVectors.size() == 1) return VecmathUtil.negate(auxVectors.get(0));
            return VecmathUtil.newVectorInLargestGap(auxVectors);
        } else if (vectors.size() == 1) {
            // 1 bond connected
            // H0, then label simply appears on the opposite side
            if (auxVectors.size() == 0) return VecmathUtil.negate(vectors.get(0));
            // !H0, then place it in the largest gap
            vectors.addAll(auxVectors);
            return VecmathUtil.newVectorInLargestGap(vectors);
        } else if (vectors.size() == 2 && auxVectors.size() == 0) {
            // 2 bonds connected to an atom with no hydrogen labels

            // sum the vectors such that the label appears in the acute/nook of the two bonds
            Vector2d combined = VecmathUtil.sum(vectors.get(0), vectors.get(1));

            // shallow angle (< 30 deg) means the label probably won't fit
            if (vectors.get(0).angle(vectors.get(1)) < Math.toRadians(65))
                combined.negate();

            // flip vector if either bond is a non-single bond or a wedge, this will
            // place the label in the largest space.
            // However - when both bonds are wedged (consider a bridging system) to
            // keep the label in the nook of the wedges
            else if ((!isPlainBond(bonds.get(0)) || !isPlainBond(bonds.get(1)))
                    && !(isWedged(bonds.get(0)) && isWedged(bonds.get(1)))) combined.negate();

            combined.normalize();

            // did we divide by 0? whoops - this happens when the bonds are collinear
            if (Double.isNaN(combined.length())) return VecmathUtil.newVectorInLargestGap(vectors);

            return combined;
        } else {
            if (vectors.size() == 3 && auxVectors.size() == 0) {
                // 3 bonds connected to an atom with no hydrogen label

                // the easy and common case is to check when two bonds are plain
                // (i.e. non-stereo sigma bonds) and use those. This gives good
                // placement for fused conjugated rings

                List<Vector2d> plainVectors = new ArrayList<Vector2d>();
                List<Vector2d> wedgeVectors = new ArrayList<Vector2d>();

                for (IBond bond : bonds) {
                    if (isPlainBond(bond)) plainVectors.add(VecmathUtil.newUnitVector(atom, bond));
                    if (isWedged(bond)) wedgeVectors.add(VecmathUtil.newUnitVector(atom, bond));
                }

                if (plainVectors.size() == 2) {
                    return VecmathUtil.sum(plainVectors.get(0), plainVectors.get(1));
                } else if (plainVectors.size() + wedgeVectors.size() == 2) {
                    plainVectors.addAll(wedgeVectors);
                    return VecmathUtil.sum(plainVectors.get(0), plainVectors.get(1));
                }
            }

            // the default option is to find the largest gap
            if (auxVectors.size() > 0) vectors.addAll(auxVectors);
            return VecmathUtil.newVectorInLargestGap(vectors);
        }
    }

    static Vector2d newAttachPointAnnotationVector(IAtom atom, List<IBond> bonds, List<Vector2d> auxVectors) {
        if (bonds.isEmpty())
            return new Vector2d(0, -1);
        else if (bonds.size() > 1)
            return newAtomAnnotationVector(atom, bonds, auxVectors);

        // only one bond (as expected)
        Vector2d bondVector = VecmathUtil.newUnitVector(atom, bonds.get(0));
        Vector2d perpVector = VecmathUtil.newPerpendicularVector(bondVector);

        // want the annotation below
        if (perpVector.y > 0)
            perpVector.negate();

        Vector2d vector = new Vector2d((bondVector.x + perpVector.x) / 2,
                                       (bondVector.y + perpVector.y) / 2);
        vector.normalize();
        return vector;
    }

    /**
     * A plain bond is a non-stereo sigma bond that is displayed simply as a line.
     *
     * @param bond a non-null bond
     * @return the bond is plain
     */
    static boolean isPlainBond(IBond bond) {
        return bond.getOrder() == IBond.Order.SINGLE
                && (bond.getStereo() == IBond.Stereo.NONE || bond.getStereo() == null);
    }

    /**
     * A bond is wedge if it points up or down.
     *
     * @param bond a non-null bond
     * @return the bond is wedge (bold or hashed)
     */
    static boolean isWedged(IBond bond) {
        return (bond.getStereo() == IBond.Stereo.UP || bond.getStereo() == IBond.Stereo.DOWN
                || bond.getStereo() == IBond.Stereo.UP_INVERTED || bond.getStereo() == IBond.Stereo.DOWN_INVERTED);
    }

    /**
     * Is the chem object hidden?
     * @param chemobj a chem object
     * @return whether it is hidden
     */
    static boolean isHidden(IChemObject chemobj) {
        return Boolean.TRUE.equals(chemobj.getProperty(HIDDEN));
    }

    /**
     * Is the chem object hidden fully?
     * @param chemobj a chem object
     * @return whether it is hidden
     */
    static boolean isHiddenFully(IChemObject chemobj) {
        return Boolean.TRUE.equals(chemobj.getProperty(HIDDEN_FULLY));
    }

    /**
     * Hide the specified chemobj, if an atom still use the bounds of its
     * symbol.
     *
     * @param chemobj a chem obj (atom or bond) to hide
     */
    static void hide(IChemObject chemobj) {
        chemobj.setProperty(HIDDEN, true);
    }

    /**
     * Hide the specified chemobj and don't use the bounds of its symbol.
     * @param chemobj a chem obj (atom or bond) to hide
     */
    static void hideFully(IChemObject chemobj) {
        chemobj.setProperty(HIDDEN_FULLY, true);
    }

    /**
     * Unhide the specified chemobj.
     * @param chemobj a chem obj (atom or bond) to unhide
     */
    static void unhide(IChemObject chemobj) {
        chemobj.setProperty(HIDDEN, false);
        chemobj.setProperty(HIDDEN_FULLY, false);
    }

    /**
     * Defines the color of unselected atoms (and bonds). Bonds colored is defined by the carbon
     * color. The default option is uniform black coloring as recommended by IUPAC.
     */
    public static final class AtomColor extends AbstractGeneratorParameter<IAtomColorer> {

        /**
         *{@inheritDoc}
         */
        @Override
        public IAtomColorer getDefault() {
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
         *{@inheritDoc}
         */
        @Override
        public SymbolVisibility getDefault() {
            return SelectionVisibility.disconnected(SymbolVisibility.iupacRecommendationsWithoutTerminalCarbon());
        }
    }

    /**
     * Defines the ratio of the stroke to the width of the stroke of the font used to depict atom
     * symbols. Default = 1.
     */
    public static final class StrokeRatio extends AbstractGeneratorParameter<Double> {

        /**
         *{@inheritDoc}
         */
        @Override
        public Double getDefault() {
            return 1d;
        }
    }

    /**
     * Defines the ratio of the separation between lines in double bonds as a percentage of length
     * ({@link BasicSceneGenerator.BondLength}). The default value is 18% (0.18).
     */
    public static final class BondSeparation extends AbstractGeneratorParameter<Double> {

        /**
         *{@inheritDoc}
         */
        @Override
        public Double getDefault() {
            return 0.16;
        }
    }

    /**
     * Defines the margin between an atom symbol and a connected bond based on the stroke width.
     * Default = 2.
     */
    public static final class SymbolMarginRatio extends AbstractGeneratorParameter<Double> {

        /**
         *{@inheritDoc}
         */
        @Override
        public Double getDefault() {
            return 2d;
        }
    }

    /**
     * Ratio of the wide end of wedge compared to the narrow end (stroke width). Default = 8.
     */
    public static final class WedgeRatio extends AbstractGeneratorParameter<Double> {

        /**
         *{@inheritDoc}
         */
        @Override
        public Double getDefault() {
            return 6d;
        }
    }

    /**
     * The preferred spacing between lines in hashed bonds. The number of hashed sections displayed
     * is then {@link BasicSceneGenerator.BondLength} / spacing. The default value is 5.
     */
    public static final class HashSpacing extends AbstractGeneratorParameter<Double> {

        /**
         *{@inheritDoc}
         */
        @Override
        public Double getDefault() {
            return 5d;
        }
    }

    /**
     * The spacing of waves (semi circles) drawn in wavy bonds with. Default = 5.
     */
    public static final class WaveSpacing extends AbstractGeneratorParameter<Double> {

        /**
         *{@inheritDoc}
         */
        @Override
        public Double getDefault() {
            return 5d;
        }
    }

    /**
     * The number of sections to render in a dashed 'unknown' bond, default = 4;
     */
    public static final class DashSection extends AbstractGeneratorParameter<Integer> {

        /**
         *{@inheritDoc}
         */
        @Override
        public Integer getDefault() {
            return 8;
        }
    }

    /**
     * Modify bold wedges to be flush with adjacent bonds, default = true.
     */
    public static final class FancyBoldWedges extends AbstractGeneratorParameter<Boolean> {

        /**{@inheritDoc} */
        @Override
        public Boolean getDefault() {
            return true;
        }
    }

    /**
     * Modify hashed wedges to be flush when there is a single adjacent bond, default = true.
     */
    public static final class FancyHashedWedges extends AbstractGeneratorParameter<Boolean> {

        /**{@inheritDoc} */
        @Override
        public Boolean getDefault() {
            return true;
        }
    }

    /**
     * The width of outer glow as a percentage of stroke width. The default value is 200% (2.0d).
     * This means the bond outer glow, is 5 times the width as the glow extends to twice the width
     * on each side.
     */
    public static final class OuterGlowWidth extends AbstractGeneratorParameter<Double> {

        /**{@inheritDoc} */
        @Override
        public Double getDefault() {
            return 2d;
        }
    }

    /**
     * Parameter defines the style of highlight used to emphasis atoms and bonds. The default option
     * is to color the atom and bond symbols ({@link HighlightStyle#Colored}).
     */
    public static final class Highlighting extends AbstractGeneratorParameter<HighlightStyle> {

        /**{@inheritDoc} */
        @Override
        public HighlightStyle getDefault() {
            return HighlightStyle.Colored;
        }
    }

    /**
     * The color of the atom numbers. The the parameter value is null, the color of the symbol
     * {@link AtomColor} is used. The default color is red to distinguish from normal atom symbols.
     */
    public static final class AnnotationColor extends AbstractGeneratorParameter<Color> {

        /**{@inheritDoc} */
        @Override
        public Color getDefault() {
            return Color.RED;
        }
    }

    /**
     * The distance of atom numbers from their parent atom as a percentage of bond length, default
     * value is 0.25 (25%)
     */
    public static final class AnnotationDistance extends AbstractGeneratorParameter<Double> {

        /**{@inheritDoc} */
        @Override
        public Double getDefault() {
            return 0.25;
        }
    }

    /**
     * Annotation font size relative to element symbols, default = 0.4 (40%).
     */
    public static final class AnnotationFontScale extends AbstractGeneratorParameter<Double> {

        /**{@inheritDoc} */
        @Override
        public Double getDefault() {
            return 0.5;
        }
    }

    /**
     * How "deep" are brackets drawn. The value is relative to bond length.
     */
    public static final class SgroupBracketDepth extends AbstractGeneratorParameter<Double> {

        /**{@inheritDoc} */
        @Override
        public Double getDefault() {
            return 0.18;
        }
    }

    /**
     * Scale Sgroup annotations relative to the normal font size (atom symbol).
     */
    public static final class SgroupFontScale extends AbstractGeneratorParameter<Double> {

        /**{@inheritDoc} */
        @Override
        public Double getDefault() {
            return 0.6;
        }
    }

    /**
     * Whether Major Isotopes e.g. 12C, 16O should be omitted.
     */
    public static final class OmitMajorIsotopes extends AbstractGeneratorParameter<Boolean> {

        /**{@inheritDoc} */
        @Override
        public Boolean getDefault() {
            return false;
        }
    }

    /**
     * Indicate delocalised/aromatic bonds should always be rendered, even when
     * there is a valid Kekule structure. Delocalised bonds will either be
     * rendered as a dashed bond to the side or as a circle/donut/life buoy
     * inside small rings. This depiction is used by default when a bond does
     * not have an order assigned (e.g. null/unset), for example: c1cccc1.
     * Turning this option on means all delocalised bonds will be rendered this
     * way even when they have bond orders correctly assigned: e.g. c1ccccc1,
     * [cH-]1cccc1.
     * <br>
     * <b>As recommended by IUPAC, their usage is discouraged and the Kekule
     * representation is more clear.</b>
     */
    public static final class ForceDelocalisedBondDisplay
            extends AbstractGeneratorParameter<Boolean> {

        /**{@inheritDoc} */
        @Override
        public Boolean getDefault() {
            return false;
        }
    }

    /**
     * Render small delocalised rings as bonds/life buoys? This can sometimes
     * be misleading for fused rings but is commonly used.
     */
    public static final class DelocalisedDonutsBondDisplay
            extends AbstractGeneratorParameter<Boolean> {

        /**{@inheritDoc} */
        @Override
        public Boolean getDefault() {
            return true;
        }
    }
}
