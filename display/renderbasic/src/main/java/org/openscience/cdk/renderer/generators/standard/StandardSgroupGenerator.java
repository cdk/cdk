/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.renderer.generators.standard;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.MarkedElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupBracket;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Internal class, call exclusively from StandardGenerator - separate purely for code organisation only.
 */
final class StandardSgroupGenerator {

    public static final double          EQUIV_THRESHOLD = 0.1;
    public static final char            INTERPUNCT      = '·';
    private final double                stroke;
    private final double                scale;
    private final double                bracketDepth;
    private final Font                  font;
    private final Color                 foreground;
    private final double                labelScale;
    private final StandardAtomGenerator atomGenerator;
    private final RendererModel         parameters;

    private StandardSgroupGenerator(RendererModel parameters, StandardAtomGenerator atomGenerator, double stroke,
                                    Font font, Color foreground) {
        this.font = font;
        this.scale = parameters.get(BasicSceneGenerator.Scale.class);
        this.stroke = stroke;
        double length = parameters.get(BasicSceneGenerator.BondLength.class) / scale;
        this.bracketDepth = parameters.get(StandardGenerator.SgroupBracketDepth.class) * length;
        this.labelScale = parameters.get(StandardGenerator.SgroupFontScale.class);

        // foreground is based on the carbon color
        this.foreground = foreground;
        this.atomGenerator = atomGenerator;
        this.parameters = parameters;
    }

    static IRenderingElement generate(RendererModel parameters, double stroke, Font font, Color foreground,
                                      StandardAtomGenerator atomGenerator, AtomSymbol[] symbols,
                                      IAtomContainer container) {
        return new StandardSgroupGenerator(parameters, atomGenerator, stroke, font, foreground).generateSgroups(container, symbols);
    }


    /**
     * If the molecule has display shortcuts (abbreviations or multiple group sgroups) certain parts
     * of the structure are hidden from display. This method marks the parts to hide and in the case
     * of abbreviations, remaps atom symbols. Appart from additional property flags, the molecule
     * is unchanged by this method.
     *
     * @param container   molecule input
     * @param symbolRemap a map that will hold symbol remapping
     */
    static void prepareDisplayShortcuts(IAtomContainer container, Map<IAtom, String> symbolRemap) {

        List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null || sgroups.isEmpty())
            return;

        // select abbreviations that should be contracted
        for (Sgroup sgroup : sgroups) {
            if (sgroup.getType() == SgroupType.CtabAbbreviation) {
                Boolean expansion = sgroup.getValue(SgroupKey.CtabExpansion);
                // abbreviation is displayed as expanded
                if (expansion != null && expansion)
                    continue;
                // no or empty label, skip it
                if (sgroup.getSubscript() == null || sgroup.getSubscript().isEmpty())
                    continue;

                // only contract if the atoms are either partially or fully highlighted
                if (checkAbbreviationHighlight(container, sgroup))
                    contractAbbreviation(container, symbolRemap, sgroup);
            } else if (sgroup.getType() == SgroupType.CtabMultipleGroup) {
                hideMultipleParts(container, sgroup);
            } else if (sgroup.getType() == SgroupType.ExtMulticenter) {
                Set<IAtom> atoms = sgroup.getAtoms();
                // should only be one bond
                for (IBond bond : sgroup.getBonds()) {
                    IAtom beg = bond.getBegin();
                    IAtom end = bond.getEnd();
                    if (atoms.contains(beg)) {
                        StandardGenerator.hideFully(beg);
                    } else {
                        StandardGenerator.hideFully(end);
                    }
                }
            }
        }
    }

    /**
     * Checks whether the given abbreviation Sgroup either has no highlight or is fully highlighted. If an
     * abbreviation is partially highlighted we don't want to contract it as this would hide the part
     * being highlighted.
     *
     * @param container molecule
     * @param sgroup abbreviation Sgroup
     * @return the abbreviation can be contracted
     */
    private static boolean checkAbbreviationHighlight(IAtomContainer container, Sgroup sgroup) {
        assert sgroup.getType() == SgroupType.CtabAbbreviation;

        Set<IAtom> sgroupAtoms = sgroup.getAtoms();
        int atomHighlight = 0;
        int bondHighlight = 0;
        int numSgroupAtoms = sgroupAtoms.size();
        int numSgroupBonds = 0;

        Color color = null;
        Color refcolor = null;

        for (IAtom atom : sgroupAtoms) {
            if ((color = atom.getProperty(StandardGenerator.HIGHLIGHT_COLOR)) != null) {
                atomHighlight++;
                if (refcolor == null)
                    refcolor = color;
                else if (!color.equals(refcolor))
                    return false; // multi-colored
            } else if (atomHighlight != 0) {
                return false; // fail fast
            }
        }
        for (IBond bond : container.bonds()) {
            IAtom beg = bond.getBegin();
            IAtom end = bond.getEnd();
            if (sgroupAtoms.contains(beg) && sgroupAtoms.contains(end)) {
                numSgroupBonds++;
                if ((color = bond.getProperty(StandardGenerator.HIGHLIGHT_COLOR)) != null) {
                    bondHighlight++;
                    if (refcolor == null)
                        refcolor = color;
                    else if (!color.equals(refcolor))
                        return false; // multi-colored
                } else if (bondHighlight != 0) {
                    return false; // fail fast
                }
            }
        }
        return atomHighlight + bondHighlight == 0 || (atomHighlight == numSgroupAtoms &&
                                                      bondHighlight == numSgroupBonds);
    }

    /**
     * Hide the repeated atoms and bonds of a multiple group. We hide al atoms that
     * belong to the group unless they are defined in the parent atom list. Any
     * bond to those atoms that is not a crossing bond or one connecting atoms in
     * the parent list is hidden.
     *
     * @param container molecule
     * @param sgroup    multiple group display shortcut
     */
    private static void hideMultipleParts(IAtomContainer container, Sgroup sgroup) {

        final Set<IBond> crossing = sgroup.getBonds();
        final Set<IAtom> atoms = sgroup.getAtoms();
        final Set<IAtom> parentAtoms = sgroup.getValue(SgroupKey.CtabParentAtomList);

        for (IBond bond : container.bonds()) {
            if (parentAtoms.contains(bond.getBegin()) && parentAtoms.contains(bond.getEnd()))
                continue;
            if (atoms.contains(bond.getBegin()) || atoms.contains(bond.getEnd()))
                StandardGenerator.hide(bond);
        }
        for (IAtom atom : atoms) {
            if (!parentAtoms.contains(atom))
                StandardGenerator.hide(atom);
        }
        for (IBond bond : crossing) {
            StandardGenerator.unhide(bond);
        }
    }

    /**
     * Hide the atoms and bonds of a contracted abbreviation. If the abbreviations is attached
     * we remap the attachment symbol to display the name. If there are no attachments the symbol
     * we be added later ({@see #generateSgroups}).
     *
     * @param container molecule
     * @param sgroup    abbreviation group display shortcut
     */
    private static void contractAbbreviation(IAtomContainer container, Map<IAtom, String> symbolRemap,
                                             Sgroup sgroup) {

        final Set<IBond> crossing = sgroup.getBonds();
        final Set<IAtom> atoms = sgroup.getAtoms();

        // only do 0,1 attachments for now unless they're all connected to the same atom
        if (crossing.size() > 1) {
            IAtom internal = null;
            for (IBond bond : crossing) {
                IAtom beg = bond.getBegin();
                IAtom end = bond.getEnd();
                if (atoms.contains(beg)) {
                    if (internal != null && !internal.equals(beg)) return; // can't do it
                    internal = beg;
                } else if (atoms.contains(end)) {
                    if (internal != null && !internal.equals(end)) return; // can't do it
                    internal = end;
                }
            }
        }

        for (IAtom atom : atoms) {
            StandardGenerator.hide(atom);
        }
        for (IBond bond : container.bonds()) {
            if (atoms.contains(bond.getBegin()) ||
                atoms.contains(bond.getEnd()))
                StandardGenerator.hide(bond);
        }
        for (IBond bond : crossing) {
            StandardGenerator.unhide(bond);
            IAtom a1 = bond.getBegin();
            IAtom a2 = bond.getEnd();
            StandardGenerator.unhide(a1);
            if (atoms.contains(a1))
                symbolRemap.put(a1, sgroup.getSubscript());
            StandardGenerator.unhide(a2);
            if (atoms.contains(a2))
                symbolRemap.put(a2, sgroup.getSubscript());
        }
    }

    /**
     * Generate the Sgroup elements for the provided atom contains.
     *
     * @param container molecule
     * @return Sgroup rendering elements
     */
    IRenderingElement generateSgroups(IAtomContainer container, AtomSymbol[] symbols) {

        ElementGroup result = new ElementGroup();
        List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);

        if (sgroups == null || sgroups.isEmpty())
            return result;

        Map<IAtom, AtomSymbol> symbolMap = new HashMap<>();
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i] != null)
                symbolMap.put(container.getAtom(i), symbols[i]);
        }

        for (Sgroup sgroup : sgroups) {

            switch (sgroup.getType()) {
                case CtabAbbreviation:
                    result.add(generateAbbreviationSgroup(container, sgroup));
                    break;
                case CtabMultipleGroup:
                    result.add(generateMultipleSgroup(sgroup, symbolMap));
                    break;
                case CtabAnyPolymer:
                case CtabMonomer:
                case CtabCrossLink:
                case CtabCopolymer:
                case CtabStructureRepeatUnit:
                case CtabMer:
                case CtabGraft:
                case CtabModified:
                    result.add(generatePolymerSgroup(sgroup, symbolMap));
                    break;
                case CtabComponent:
                case CtabMixture:
                case CtabFormulation:
                    result.add(generateMixtureSgroup(sgroup));
                    break;
                case CtabGeneric:
                    // not strictly a polymer but okay to draw as one
                    result.add(generatePolymerSgroup(sgroup, null));
                    break;
            }
        }

        return result;
    }

    private IRenderingElement generateMultipleSgroup(Sgroup sgroup, Map<IAtom, AtomSymbol> symbolMap) {
        // just draw the brackets - multiplied group parts have already been hidden in prep phase
        List<SgroupBracket> brackets = sgroup.getValue(SgroupKey.CtabBracket);
        if (brackets != null) {
            return generateSgroupBrackets(sgroup,
                                          brackets,
                                          symbolMap,
                                          (String) sgroup.getValue(SgroupKey.CtabSubScript),
                                          null);
        } else {
            return new ElementGroup();
        }
    }

    private IRenderingElement generateAbbreviationSgroup(IAtomContainer mol, Sgroup sgroup) {
        String label = sgroup.getSubscript();
        // already handled by symbol remapping
        if (sgroup.getBonds().size() > 0 || label == null || label.isEmpty()) {
            return new ElementGroup();
        }
        if (!checkAbbreviationHighlight(mol, sgroup))
            return new ElementGroup();
        // we're showing a label where there were no atoms before, we put it in the
        // middle of all of those which were hidden
        Set<IAtom> sgroupAtoms = sgroup.getAtoms();
        assert !sgroupAtoms.isEmpty();

        Color highlight = sgroupAtoms.iterator().next().getProperty(StandardGenerator.HIGHLIGHT_COLOR);
        final StandardGenerator.HighlightStyle style = parameters.get(StandardGenerator.Highlighting.class);
        final double glowWidth = parameters.get(StandardGenerator.OuterGlowWidth.class);

        final Point2d labelLocation;
        if (mol.getAtomCount() == sgroup.getAtoms().size()) {
            labelLocation = GeometryUtil.get2DCenter(sgroupAtoms);
        } else {
            // contraction of part of a fragment, e.g. SALT
            // here we work out the point we want to place the contract relative
            // to the SGroup Atoms
            labelLocation = new Point2d();
            final Point2d sgrpCenter = GeometryUtil.get2DCenter(sgroupAtoms);
            final Point2d molCenter  = GeometryUtil.get2DCenter(mol);
            final double[] minMax    = GeometryUtil.getMinMax(sgroupAtoms);
            double xDiff = sgrpCenter.x - molCenter.x;
            double yDiff = sgrpCenter.y - molCenter.y;
            if (xDiff > 0.1) {
                labelLocation.x = minMax[0]; // min x
                label = INTERPUNCT + label;
            }
            else if (xDiff < -0.1) {
                labelLocation.x = minMax[2]; // max x
                label = label + INTERPUNCT;
            }
            else {
                labelLocation.x = sgrpCenter.x;
                label = INTERPUNCT + label;
            }
            if (yDiff > 0.1)
                labelLocation.y = minMax[1]; // min y
            else if (yDiff < -0.1)
                labelLocation.y = minMax[3]; // max y
            else
                labelLocation.y = sgrpCenter.y;
        }

        ElementGroup labelgroup = new ElementGroup();
        for (Shape outline : atomGenerator.generateAbbreviatedSymbol(label, HydrogenPosition.Right)
                                          .center(labelLocation.x, labelLocation.y)
                                          .resize(1 / scale, 1 / -scale)
                                          .getOutlines()) {
            if (highlight != null && style == StandardGenerator.HighlightStyle.Colored) {
                labelgroup.add(GeneralPath.shapeOf(outline, highlight));
            } else {
                labelgroup.add(GeneralPath.shapeOf(outline, foreground));
            }
        }

        if (highlight != null && style == StandardGenerator.HighlightStyle.OuterGlow) {
            ElementGroup group = new ElementGroup();
            // outer glow needs to be being the label
            group.add(StandardGenerator.outerGlow(labelgroup, highlight, glowWidth, stroke));
            group.add(labelgroup);
            return group;
        } else {
            return MarkedElement.markupAtom(labelgroup, null);
        }
    }

    /**
     * Generates polymer Sgroup elements.
     *
     * @param sgroup the Sgroup
     * @return the rendered elements (empty if no brackets defined)
     */
    private IRenderingElement generatePolymerSgroup(Sgroup sgroup, Map<IAtom, AtomSymbol> symbolMap) {
        // draw the brackets
        List<SgroupBracket> brackets = sgroup.getValue(SgroupKey.CtabBracket);
        if (brackets != null) {

            SgroupType type = sgroup.getType();

            String subscript = sgroup.getValue(SgroupKey.CtabSubScript);
            String connectivity = sgroup.getValue(SgroupKey.CtabConnectivity);

            switch (type) {
                case CtabCopolymer:
                    subscript = "co";
                    String subtype = sgroup.getValue(SgroupKey.CtabSubType);
                    if ("RAN".equals(subtype))
                        subscript = "ran";
                    else if ("BLK".equals(subtype))
                        subscript = "blk";
                    else if ("ALT".equals(subtype))
                        subscript = "alt";
                    break;
                case CtabCrossLink:
                    subscript = "xl";
                    break;
                case CtabAnyPolymer:
                    subscript = "any";
                    break;
                case CtabGraft:
                    subscript = "grf";
                    break;
                case CtabMer:
                    subscript = "mer";
                    break;
                case CtabMonomer:
                    subscript = "mon";
                    break;
                case CtabModified:
                    subscript = "mod";
                    break;
                case CtabStructureRepeatUnit:
                    if (subscript == null)
                        subscript = "n";
                    if (connectivity == null)
                        connectivity = "eu";
                    break;
            }

            // connectivity doesn't matter if symmetric... which is hard to test
            // here but we can certainly ignore it for single atoms (e.g. methylene)
            // also when we see brackets we presume head-to-tail repeating
            if ("ht".equals(connectivity) || sgroup.getAtoms().size() == 1)
                connectivity = null;

            return generateSgroupBrackets(sgroup,
                                          brackets,
                                          symbolMap,
                                          subscript,
                                          connectivity);
        } else {
            return new ElementGroup();
        }
    }

    private IRenderingElement generateMixtureSgroup(Sgroup sgroup) {
        // draw the brackets
        // TODO - mixtures normally have attached Sgroup data
        // TODO - e.g. COMPONENT_FRACTION, ACTIVITY_TYPE, WEIGHT_PERCENT
        List<SgroupBracket> brackets = sgroup.getValue(SgroupKey.CtabBracket);
        if (brackets != null) {

            SgroupType type = sgroup.getType();
            String subscript = "?";
            switch (type) {
                case CtabComponent:
                    Integer compNum = sgroup.getValue(SgroupKey.CtabComponentNumber);
                    if (compNum != null)
                        subscript = "c" + Integer.toString(compNum);
                    else
                        subscript = "c";
                    break;
                case CtabMixture:
                    subscript = "mix";
                    break;
                case CtabFormulation:
                    subscript = "f";
                    break;
            }

            return generateSgroupBrackets(sgroup,
                                          brackets,
                                          null,
                                          subscript,
                                          null);
        } else {
            return new ElementGroup();
        }
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isUnsignedInt(String str) {
        int pos = 0;
        if (str == null)
            return false;
        int len = str.length();
        while (pos < len)
            if (!isDigit(str.charAt(pos++)))
                return false;
        return true;
    }

    private IRenderingElement generateSgroupBrackets(Sgroup sgroup,
                                                     List<SgroupBracket> brackets,
                                                     Map<IAtom, AtomSymbol> symbols,
                                                     String subscriptSuffix,
                                                     String superscriptSuffix) {

        // brackets are square by default (style:0)
        Integer style = sgroup.getValue(SgroupKey.CtabBracketStyle);
        boolean round = style != null && style == 1;
        ElementGroup result = new ElementGroup();

        Set<IAtom> atoms  = sgroup.getType() == SgroupType.CtabMultipleGroup ?
                                (Set<IAtom>) sgroup.getValue(SgroupKey.CtabParentAtomList) :
                                sgroup.getAtoms();

        Set<IBond> crossingBonds = sgroup.getBonds();

        // easy to depict in correct orientation, we just
        // point each bracket at the atom of a crossing
        // bond that is 'in' the group - this scales
        // to more than two brackets

        // first we need to pair the brackets with the bonds
        Map<SgroupBracket, IBond> pairs = crossingBonds.size() == brackets.size() ? bracketBondPairs(brackets, crossingBonds)
                                                                                  : Collections.<SgroupBracket, IBond>emptyMap();

        // override bracket layout around single atoms to bring them in closer
        if (atoms.size() == 1) {

            IAtom atom = atoms.iterator().next();

            // e.g. 2 HCL, 8 H2O etc.
            if (isUnsignedInt(subscriptSuffix) &&
                crossingBonds.isEmpty() &&
                symbols.containsKey(atom)) {
                TextOutline prefix = new TextOutline(INTERPUNCT + subscriptSuffix, font).resize(1/scale,1/-scale);
                Rectangle2D prefixBounds = prefix.getLogicalBounds();

                AtomSymbol symbol = symbols.get(atom);

                Rectangle2D bounds = symbol.getConvexHull().outline().getBounds2D();

                // make slightly large
                bounds.setRect(bounds.getMinX() - 2 * stroke,
                               bounds.getMinY() - 2 * stroke,
                               bounds.getWidth() + 4 * stroke,
                               bounds.getHeight() + 4 * stroke);

                prefix = prefix.translate(bounds.getMinX() - prefixBounds.getMaxX(),
                                          symbol.getAlignmentCenter().getY() - prefixBounds.getCenterY());

                result.add(GeneralPath.shapeOf(prefix.getOutline(), foreground));
            }
            // e.g. CC(O)nCC
            else if (crossingBonds.size()>0) {

                double scriptscale = labelScale;

                TextOutline leftBracket = new TextOutline("(", font).resize(1 / scale, 1 / -scale);
                TextOutline rightBracket = new TextOutline(")", font).resize(1 / scale, 1 / -scale);

                Point2D leftCenter = leftBracket.getCenter();
                Point2D rightCenter = rightBracket.getCenter();

                if (symbols.containsKey(atom)) {
                    AtomSymbol symbol = symbols.get(atom);

                    Rectangle2D bounds = symbol.getConvexHull().outline().getBounds2D();

                    // make slightly large
                    bounds.setRect(bounds.getMinX() - 2 * stroke,
                                   bounds.getMinY() - 2 * stroke,
                                   bounds.getWidth() + 4 * stroke,
                                   bounds.getHeight() + 4 * stroke);

                    leftBracket = leftBracket.translate(bounds.getMinX() - 0.1 - leftCenter.getX(),
                                                        symbol.getAlignmentCenter().getY() - leftCenter.getY());
                    rightBracket = rightBracket.translate(bounds.getMaxX() + 0.1 - rightCenter.getX(),
                                                          symbol.getAlignmentCenter().getY() - rightCenter.getY());
                } else {
                    Point2d p = atoms.iterator().next().getPoint2d();
                    leftBracket = leftBracket.translate(p.x - 0.2 - leftCenter.getX(),
                                                        p.y - leftCenter.getY());
                    rightBracket = rightBracket.translate(p.x + 0.2 - rightCenter.getX(),
                                                          p.y - rightCenter.getY());
                }

                result.add(GeneralPath.shapeOf(leftBracket.getOutline(), foreground));
                result.add(GeneralPath.shapeOf(rightBracket.getOutline(), foreground));

                Rectangle2D rightBracketBounds = rightBracket.getBounds();

                // subscript/superscript suffix annotation
                if (subscriptSuffix != null && !subscriptSuffix.isEmpty()) {
                    TextOutline subscriptOutline = leftAlign(makeText(subscriptSuffix.toLowerCase(Locale.ROOT),
                                                                      new Point2d(rightBracketBounds.getMaxX(),
                                                                                  rightBracketBounds.getMinY() - 0.1),
                                                                      new Vector2d(-0.5 * rightBracketBounds.getWidth(), 0),
                                                                      scriptscale));
                    result.add(GeneralPath.shapeOf(subscriptOutline.getOutline(), foreground));
                }
                if (superscriptSuffix != null && !superscriptSuffix.isEmpty()) {
                    TextOutline superscriptOutline = leftAlign(makeText(superscriptSuffix.toLowerCase(Locale.ROOT),
                                                                        new Point2d(rightBracketBounds.getMaxX(),
                                                                                    rightBracketBounds.getMaxY() + 0.1),
                                                                        new Vector2d(-rightBracketBounds.getWidth(), 0),
                                                                        scriptscale));
                    result.add(GeneralPath.shapeOf(superscriptOutline.getOutline(), foreground));
                }
            }
        } else if (!pairs.isEmpty()) {

            SgroupBracket suffixBracket = null;
            Vector2d suffixBracketPerp = null;

            for (Map.Entry<SgroupBracket, IBond> e : pairs.entrySet()) {

                final SgroupBracket bracket = e.getKey();
                final IBond bond = e.getValue();
                final IAtom inGroupAtom = atoms.contains(bond.getBegin()) ? bond.getBegin() : bond.getEnd();

                final Point2d p1 = bracket.getFirstPoint();
                final Point2d p2 = bracket.getSecondPoint();

                final Vector2d perp = VecmathUtil.newPerpendicularVector(VecmathUtil.newUnitVector(p1, p2));

                // point the vector at the atom group
                Point2d midpoint = VecmathUtil.midpoint(p1, p2);
                if (perp.dot(VecmathUtil.newUnitVector(midpoint,
                                                       inGroupAtom.getPoint2d())) < 0) {
                    perp.negate();
                }
                perp.scale(bracketDepth);

                if (round)
                    result.add(createRoundBracket(p1, p2, perp, midpoint));
                else
                    result.add(createSquareBracket(p1, p2, perp));

                if (suffixBracket == null) {
                    suffixBracket = bracket;
                    suffixBracketPerp = perp;
                } else {
                    // is this bracket better as a suffix?
                    Point2d sp1 = suffixBracket.getFirstPoint();
                    Point2d sp2 = suffixBracket.getSecondPoint();
                    double bestMaxX = Math.max(sp1.x,
                                               sp2.x);
                    double thisMaxX = Math.max(p1.x,
                                               p2.x);
                    double bestMaxY = Math.max(sp1.y,
                                               sp2.y);
                    double thisMaxY = Math.max(p1.y,
                                               p2.y);

                    // choose the most eastern or.. the most southern
                    double xDiff = thisMaxX - bestMaxX;
                    double yDiff = thisMaxY - bestMaxY;
                    if (xDiff > EQUIV_THRESHOLD || (xDiff > -EQUIV_THRESHOLD && yDiff < -EQUIV_THRESHOLD)) {
                        suffixBracket = bracket;
                        suffixBracketPerp = perp;
                    }
                }
            }

            // write the labels
            if (suffixBracket != null) {

                Point2d subSufPnt = suffixBracket.getFirstPoint();
                Point2d supSufPnt = suffixBracket.getSecondPoint();

                // try to put the subscript on the bottom
                double xDiff = subSufPnt.x - supSufPnt.x;
                double yDiff = subSufPnt.y - supSufPnt.y;
                if (yDiff > EQUIV_THRESHOLD || (yDiff > -EQUIV_THRESHOLD && xDiff > EQUIV_THRESHOLD)) {
                    Point2d tmpP = subSufPnt;
                    subSufPnt = supSufPnt;
                    supSufPnt = tmpP;
                }

                // subscript/superscript suffix annotation
                if (subscriptSuffix != null && !subscriptSuffix.isEmpty()) {
                    TextOutline subscriptOutline = leftAlign(makeText(subscriptSuffix.toLowerCase(Locale.ROOT),
                                                                      subSufPnt, suffixBracketPerp, labelScale));
                    result.add(GeneralPath.shapeOf(subscriptOutline.getOutline(), foreground));
                }
                if (superscriptSuffix != null && !superscriptSuffix.isEmpty()) {
                    TextOutline superscriptOutline = leftAlign(makeText(superscriptSuffix.toLowerCase(Locale.ROOT),
                                                                        supSufPnt, suffixBracketPerp, labelScale));
                    result.add(GeneralPath.shapeOf(superscriptOutline.getOutline(), foreground));
                }

            }
        } else if (brackets.size() == 2) {

            final Point2d b1p1 = brackets.get(0).getFirstPoint();
            final Point2d b1p2 = brackets.get(0).getSecondPoint();
            final Point2d b2p1 = brackets.get(1).getFirstPoint();
            final Point2d b2p2 = brackets.get(1).getSecondPoint();

            final Vector2d b1vec = VecmathUtil.newUnitVector(b1p1, b1p2);
            final Vector2d b2vec = VecmathUtil.newUnitVector(b2p1, b2p2);

            final Vector2d b1pvec = VecmathUtil.newPerpendicularVector(b1vec);
            final Vector2d b2pvec = VecmathUtil.newPerpendicularVector(b2vec);

            // Point the vectors at each other
            if (b1pvec.dot(VecmathUtil.newUnitVector(b1p1, b2p1)) < 0)
                b1pvec.negate();
            if (b2pvec.dot(VecmathUtil.newUnitVector(b2p1, b1p1)) < 0)
                b2pvec.negate();

            // scale perpendicular vectors by how deep the brackets need to be
            b1pvec.scale(bracketDepth);
            b2pvec.scale(bracketDepth);

            // bad brackets
            if (Double.isNaN(b1pvec.x) || Double.isNaN(b1pvec.y) ||
                Double.isNaN(b2pvec.x) || Double.isNaN(b2pvec.y))
                return result;

            Path2D path = new Path2D.Double();

            if (round) {
                // bracket 1 (cp: control point)
                path.moveTo(b1p1.x + b1pvec.x, b1p1.y + b1pvec.y);
                Point2d cpb1 = VecmathUtil.midpoint(b1p1, b1p2);
                cpb1.add(VecmathUtil.negate(b1pvec));
                path.quadTo(cpb1.x, cpb1.y,
                            b1p2.x + b1pvec.x, b1p2.y + b1pvec.y);

                // bracket 2 (cp: control point)
                path.moveTo(b2p1.x + b2pvec.x, b2p1.y + b2pvec.y);
                Point2d cpb2 = VecmathUtil.midpoint(b2p1, b2p2);
                cpb2.add(VecmathUtil.negate(b2pvec));
                path.quadTo(cpb2.x, cpb2.y,
                            b2p2.x + b2pvec.x, b2p2.y + b2pvec.y);
            } else {
                // bracket 1
                path.moveTo(b1p1.x + b1pvec.x, b1p1.y + b1pvec.y);
                path.lineTo(b1p1.x, b1p1.y);
                path.lineTo(b1p2.x, b1p2.y);
                path.lineTo(b1p2.x + b1pvec.x, b1p2.y + b1pvec.y);

                // bracket 2
                path.moveTo(b2p1.x + b2pvec.x, b2p1.y + b2pvec.y);
                path.lineTo(b2p1.x, b2p1.y);
                path.lineTo(b2p2.x, b2p2.y);
                path.lineTo(b2p2.x + b2pvec.x, b2p2.y + b2pvec.y);
            }

            result.add(GeneralPath.outlineOf(path, stroke, foreground));

            // work out where to put the suffix labels (e.g. ht/hh/eu) superscript
            // and (e.g. n, xl, c, mix) subscript
            // TODO: could be improved
            double b1MaxX = Math.max(b1p1.x, b1p2.x);
            double b2MaxX = Math.max(b2p1.x, b2p2.x);
            double b1MaxY = Math.max(b1p1.y, b1p2.y);
            double b2MaxY = Math.max(b2p1.y, b2p2.y);

            Point2d subSufPnt = b2p2;
            Point2d supSufPnt = b2p1;
            Vector2d subpvec = b2pvec;

            double bXDiff = b1MaxX - b2MaxX;
            double bYDiff = b1MaxY - b2MaxY;

            if (bXDiff > EQUIV_THRESHOLD || (bXDiff > -EQUIV_THRESHOLD && bYDiff < -EQUIV_THRESHOLD)) {
                subSufPnt = b1p2;
                supSufPnt = b1p1;
                subpvec = b1pvec;
            }

            double xDiff = subSufPnt.x - supSufPnt.x;
            double yDiff = subSufPnt.y - supSufPnt.y;

            if (yDiff > EQUIV_THRESHOLD || (yDiff > -EQUIV_THRESHOLD && xDiff > EQUIV_THRESHOLD)) {
                Point2d tmpP = subSufPnt;
                subSufPnt = supSufPnt;
                supSufPnt = tmpP;
            }

            // subscript/superscript suffix annotation
            if (subscriptSuffix != null && !subscriptSuffix.isEmpty()) {
                TextOutline subscriptOutline = leftAlign(makeText(subscriptSuffix.toLowerCase(Locale.ROOT),
                                                                  subSufPnt, subpvec, labelScale));
                result.add(GeneralPath.shapeOf(subscriptOutline.getOutline(), foreground));
            }
            if (superscriptSuffix != null && !superscriptSuffix.isEmpty()) {
                TextOutline superscriptOutline = leftAlign(makeText(superscriptSuffix.toLowerCase(Locale.ROOT),
                                                                    supSufPnt, subpvec, labelScale));
                result.add(GeneralPath.shapeOf(superscriptOutline.getOutline(), foreground));
            }

        }
        return result;
    }

    private GeneralPath createRoundBracket(Point2d p1, Point2d p2, Vector2d perp, Point2d midpoint) {
        Path2D path = new Path2D.Double();
        // bracket 1 (cp: control point)
        path.moveTo(p1.x + perp.x, p1.y + perp.y);
        Point2d cpb1 = new Point2d(midpoint);
        cpb1.add(VecmathUtil.negate(perp));
        path.quadTo(cpb1.x, cpb1.y,
                    p2.x + perp.x, p2.y + perp.y);
        return GeneralPath.outlineOf(path, stroke, foreground);
    }

    private GeneralPath createSquareBracket(Point2d p1, Point2d p2, Vector2d perp) {
        Path2D path = new Path2D.Double();
        path.moveTo(p1.x + perp.x, p1.y + perp.y);
        path.lineTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p2.x + perp.x, p2.y + perp.y);
        return GeneralPath.outlineOf(path, stroke, foreground);
    }

    private static Map<SgroupBracket, IBond> bracketBondPairs(Collection<SgroupBracket> brackets,
                                                              Collection<IBond> bonds) {
        Map<SgroupBracket, IBond> pairs = new HashMap<>();

        for (SgroupBracket bracket : brackets) {
            IBond crossingBond = null;
            for (IBond bond : bonds) {
                IAtom a1 = bond.getBegin();
                IAtom a2 = bond.getEnd();
                if (Line2D.linesIntersect(bracket.getFirstPoint().x, bracket.getFirstPoint().y,
                                          bracket.getSecondPoint().x, bracket.getSecondPoint().y,
                                          a1.getPoint2d().x, a1.getPoint2d().y,
                                          a2.getPoint2d().x, a2.getPoint2d().y)) {
                    // more than one... not good
                    if (crossingBond != null)
                        return new HashMap<>();
                    crossingBond = bond;
                }
            }
            if (crossingBond == null)
                return new HashMap<>();
            pairs.put(bracket, crossingBond);
        }

        return pairs;
    }

    private TextOutline makeText(String subscriptSuffix, Point2d b1p2, Vector2d b1pvec, double labelScale) {
        return StandardGenerator.generateAnnotation(b1p2,
                                                    subscriptSuffix,
                                                    VecmathUtil.negate(b1pvec), 1,
                                                    labelScale, font, null)
                                .resize(1 / scale, 1 / scale);
    }

    private TextOutline leftAlign(TextOutline outline) {
        Point2D center = outline.getCenter();
        Point2D first = outline.getFirstGlyphCenter();
        return outline.translate(center.getX() - first.getX(),
                                 center.getY() - first.getY());
    }
}
