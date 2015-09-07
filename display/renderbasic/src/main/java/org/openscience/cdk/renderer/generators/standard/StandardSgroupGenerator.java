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
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupBracket;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Internal class, call exclusively from StandardGenerator - separate purely for code organisation only.
 */
final class StandardSgroupGenerator {

    public static final double EQUIV_THRESHOLD = 0.1;
    private final double        stroke;
    private final double        scale;
    private final double        bracketDepth;
    private final Font          font;
    private final Color         foreground;
    private final double labelScale;

    private StandardSgroupGenerator(RendererModel parameters, double stroke, Font font, Color foreground) {
        this.font = font;
        this.scale = parameters.get(BasicSceneGenerator.Scale.class);
        this.stroke = stroke;
        double length = parameters.get(BasicSceneGenerator.BondLength.class) / scale;
        this.bracketDepth = parameters.get(StandardGenerator.SgroupBracketDepth.class) * length;
        this.labelScale   = parameters.get(StandardGenerator.SgroupFontScale.class);

        // foreground is based on the carbon color
        this.foreground = foreground;
    }

    static IRenderingElement generate(RendererModel parameters, double stroke, Font font, Color foreground, IAtomContainer container) {
        return new StandardSgroupGenerator(parameters, stroke, font, foreground).generateSgroups(container);
    }


    /**
     * If the molecule has display shortcuts (abbreviations or multiple group sgroups) certain parts
     * of the structure are hidden from display. This method marks the parts to hide and in the case
     * of abbreviations, remaps atom symbols. Appart from additional property flags, the molecule
     * is unchanged by this method.
     *
     * @param container molecule input
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
                if (expansion != null && expansion == Boolean.TRUE)
                    continue;
                // no or empty label, skip it
                if (sgroup.getSubscript() == null || sgroup.getSubscript().isEmpty())
                    continue;
                contractAbbreviation(container, symbolRemap, sgroup);
            }
            else if (sgroup.getType() == SgroupType.CtabMultipleGroup) {
                hideMultipleParts(container, sgroup);
            }
        }
    }

    /**
     * Hide the repeated atoms and bonds of a multiple group. We hide al atoms that
     * belong to the group unless they are defined in the parent atom list. Any
     * bond to those atoms that is not a crossing bond or one connecting atoms in
     * the parent list is hidden.
     *
     * @param container molecule
     * @param sgroup multiple group display shortcut
     */
    private static void hideMultipleParts(IAtomContainer container, Sgroup sgroup) {

        final Set<IBond> crossing = sgroup.getBonds();
        final Set<IAtom> atoms = sgroup.getAtoms();
        final Set<IAtom> parentAtoms = sgroup.getValue(SgroupKey.CtabParentAtomList);

        for (IBond bond : container.bonds()) {
            if (parentAtoms.contains(bond.getAtom(0)) && parentAtoms.contains(bond.getAtom(1)))
                continue;
            if (atoms.contains(bond.getAtom(0)) || atoms.contains(bond.getAtom(1)))
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
     * @param sgroup abbreviation group display shortcut
     */
    private static void contractAbbreviation(IAtomContainer container, Map<IAtom, String> symbolRemap,
                                             Sgroup sgroup) {

        final Set<IBond> crossing = sgroup.getBonds();
        final Set<IAtom> atoms = sgroup.getAtoms();

        // only do 0,1 attachments for now
        if (crossing.size() > 1)
            return;

        for (IAtom atom : atoms) {
            StandardGenerator.hide(atom);
        }
        for (IBond bond : container.bonds()) {
            if (atoms.contains(bond.getAtom(0)) ||
                atoms.contains(bond.getAtom(1)))
                StandardGenerator.hide(bond);
        }
        for (IBond bond : crossing) {
            StandardGenerator.unhide(bond);
            IAtom a1 = bond.getAtom(0);
            IAtom a2 = bond.getAtom(1);
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
    IRenderingElement generateSgroups(IAtomContainer container) {

        ElementGroup result = new ElementGroup();
        List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);

        if (sgroups == null || sgroups.isEmpty())
            return result;

        for (Sgroup sgroup : sgroups) {

            switch (sgroup.getType()) {
                case CtabAbbreviation:
                    result.add(generateAbbreviationSgroup(sgroup));
                    break;
                case CtabMultipleGroup:
                    result.add(generateMultipleSgroup(sgroup));
                    break;
                case CtabAnyPolymer:
                case CtabMonomer:
                case CtabCrossLink:
                case CtabCopolymer:
                case CtabStructureRepeatUnit:
                case CtabMer:
                case CtabGraft:
                    result.add(generatePolymerSgroup(sgroup));
                    break;
                case CtabComponent:
                case CtabMixture:
                case CtabFormulation:
                    // Todo
                    break;
            }
        }

        return result;
    }

    private IRenderingElement generateMultipleSgroup(Sgroup sgroup) {
        // just draw the brackets - multiplied group parts have already been hidden in prep phase
        List<SgroupBracket> brackets = sgroup.getValue(SgroupKey.CtabBracket);
        if (brackets != null) {
            return generateSgroupBrackets(sgroup, brackets,
                                          (String) sgroup.getValue(SgroupKey.CtabSubScript),
                                          null);
        } else {
            return new ElementGroup();
        }
    }

    private IRenderingElement generateAbbreviationSgroup(Sgroup sgroup) {
        String label = sgroup.getSubscript();
        // already handled by symbol remapping
        if (sgroup.getBonds().size() > 0 || label == null || label.isEmpty()) {
            return new ElementGroup();
        }
        // we're showing a label where there were no atoms before, we put it in the
        // middle of all of those which were hidden
        final Point2d labelCoords = GeometryUtil.get2DCenter(sgroup.getAtoms());
        return GeneralPath.shapeOf(makeText(label,
                                            labelCoords,
                                            new Vector2d(0,0), 1).getOutline(),
                                   foreground);
    }

    /**
     * Generates polymer Sgroup elements.
     *
     * @param sgroup the Sgroup
     * @return the rendered elements (empty if no brackets defined)
     */
    private IRenderingElement generatePolymerSgroup(Sgroup sgroup) {
        // draw the brackets
        List<SgroupBracket> brackets = sgroup.getValue(SgroupKey.CtabBracket);
        if (brackets != null) {
            return generateSgroupBrackets(sgroup, brackets,
                                          (String) sgroup.getValue(SgroupKey.CtabSubScript),
                                          (String) sgroup.getValue(SgroupKey.CtabConnectivity));
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

            return generateSgroupBrackets(sgroup, brackets,
                                          subscript,
                                          null);
        } else {
            return new ElementGroup();
        }
    }

    private IRenderingElement generateSgroupBrackets(Sgroup sgroup, List<SgroupBracket> brackets,
                                                     String subscriptSuffix, String superscriptSuffix) {

        // brackets are square by default (style:0)
        Integer style = sgroup.getValue(SgroupKey.CtabBracketStyle);
        boolean round = style != null && style == 1;
        ElementGroup result = new ElementGroup();

        // < 2 brackets - unpaired, > 2 ... possible but difficult and probably incorrect
        if (brackets.size() != 2) {
            return result;
        }

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

        return result;
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
        Point2D first  = outline.getFirstGlyphCenter();
        return outline.translate(center.getX() - first.getX(),
                                 center.getY() - first.getY());
    }
}
