/*
 * Copyright (c) 2014  European Bioinformatics Institute (EMBL-EBI)
 *                     John May <jwmay@users.sf.net>
 *               2014  Mark B Vine (orcid:0000-0002-7794-0426)
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

import com.google.common.primitives.Ints;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.MarkedElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.path.Close;
import org.openscience.cdk.renderer.elements.path.CubicTo;
import org.openscience.cdk.renderer.elements.path.LineTo;
import org.openscience.cdk.renderer.elements.path.MoveTo;
import org.openscience.cdk.renderer.elements.path.PathElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.openscience.cdk.interfaces.IBond.Order.SINGLE;
import static org.openscience.cdk.interfaces.IBond.Order.UNSET;
import static org.openscience.cdk.interfaces.IBond.Stereo.NONE;
import static org.openscience.cdk.renderer.generators.BasicSceneGenerator.BondLength;
import static org.openscience.cdk.renderer.generators.standard.StandardGenerator.*;
import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.*;

/**
 * Generates {@link IRenderingElement}s for bonds. The generator is internal and called by the
 * {@link org.openscience.cdk.renderer.generators.standard.StandardGenerator}. A new bond generator
 * is required for each container instance.
 *
 *  The bonds generated are: <ul> <li> {@link #generateSingleBond} - delegates to one of the
 * following types: <ul> <li>{@link #generatePlainSingleBond} - single line between two atoms</li>
 * <li>{@link #generateBoldWedgeBond} - wedged up stereo </li> <li>{@link #generateHashedWedgeBond}
 * - wedged down stereo bond </li> <li>{@link #generateWavyBond} - up or down bond </li> </ul> </li>
 * <li> {@link #generateDoubleBond} - delegates to one of the following types: <ul> <li>{@link
 * #generateOffsetDoubleBond} - one line rests on the center between the atoms</li> <li>{@link
 * #generateCenteredDoubleBond} - both lines rest equidistant from the center between the atoms</li>
 * <li>{@link #generateCrossedDoubleBond} - unknown double stereochemistry </li> </ul> </li>
 * <li>{@link #generateTripleBond} - composes a single and double bond</li> <li>{@link
 * #generateDashedBond} - the unknown bond type</li> </ul>
 *
 * @author John May
 */
final class StandardBondGenerator {

    private final IAtomContainer         container;
    private final AtomSymbol[]           symbols;
    private final RendererModel          parameters;
    private final StandardDonutGenerator donutGenerator;

    // logging
    private final ILoggingTool               logger       = LoggingToolFactory.createLoggingTool(getClass());

    // indexes of atoms and rings
    private final Map<IAtom, Integer>        atomIndexMap = new HashMap<IAtom, Integer>();
    private final Map<IBond, IAtomContainer> ringMap;

    // parameters
    private final double                     scale;
    private final double                     stroke;
    private final double                     separation;
    private final double                     backOff;
    private final double                     wedgeWidth;
    private final double                     hashSpacing;
    private final double                     waveSpacing;
    private final Color                      foreground, annotationColor;
    private final boolean                    fancyBoldWedges, fancyHashedWedges;
    private final double                     annotationDistance, annotationScale;
    private final Font         font;
    private final ElementGroup annotations;
    private final boolean      forceDelocalised;

    /**
     * Create a new standard bond generator for the provided structure (container) with the laid out
     * atom symbols. The parameters of the bond generation are also provided and the scaled 'stroke'
     * width which is used to scale all other parameters.
     *
     * @param container  structure representation
     * @param symbols    generated atom symbols
     * @param parameters rendering options
     * @param stroke     scaled stroke width
     */
    private StandardBondGenerator(IAtomContainer container,
                                  AtomSymbol[] symbols,
                                  RendererModel parameters,
                                  ElementGroup annotations,
                                  Font font,
                                  double stroke,
                                  StandardDonutGenerator donutGen) {
        this.container = container;
        this.symbols = symbols;
        this.parameters = parameters;
        this.annotations = annotations;
        this.donutGenerator = donutGen;

        // index atoms and rings
        for (int i = 0; i < container.getAtomCount(); i++)
            atomIndexMap.put(container.getAtom(i), i);
        ringMap = ringPreferenceMap(container, donutGenerator.smallest);

        // set parameters
        this.scale = parameters.get(BasicSceneGenerator.Scale.class);
        this.stroke = stroke;
        double length = parameters.get(BondLength.class) / scale;
        this.separation = (parameters.get(BondSeparation.class) * parameters.get(BondLength.class)) / scale;
        this.backOff = parameters.get(StandardGenerator.SymbolMarginRatio.class) * stroke;
        this.wedgeWidth = parameters.get(StandardGenerator.WedgeRatio.class) * stroke;
        this.hashSpacing = parameters.get(HashSpacing.class) / scale;
        this.waveSpacing = parameters.get(WaveSpacing.class) / scale;
        this.fancyBoldWedges = parameters.get(StandardGenerator.FancyBoldWedges.class);
        this.fancyHashedWedges = parameters.get(StandardGenerator.FancyHashedWedges.class);
        this.annotationDistance = parameters.get(StandardGenerator.AnnotationDistance.class)
                * (parameters.get(BondLength.class) / scale);
        this.annotationScale = (1 / scale) * parameters.get(StandardGenerator.AnnotationFontScale.class);
        this.annotationColor = parameters.get(StandardGenerator.AnnotationColor.class);
        this.forceDelocalised = parameters.get(StandardGenerator.ForceDelocalisedBondDisplay.class);
        this.font = font;

        // foreground is based on the carbon color
        this.foreground = parameters.get(StandardGenerator.AtomColor.class).getAtomColor(
                container.getBuilder().newInstance(IAtom.class, "C"));
    }

    /**
     * Generates bond elements for the provided structure (container) with the laid out atom
     * symbols. The parameters of the bond generation are also provided and the scaled 'stroke'
     * width which is used to scale all other parameters.
     *
     * @param container  structure representation
     * @param symbols    generated atom symbols
     * @param parameters rendering options
     * @param stroke     scaled stroke width
     */
    static IRenderingElement[] generateBonds(IAtomContainer container,
                                             AtomSymbol[] symbols,
                                             RendererModel parameters,
                                             double stroke,
                                             Font font,
                                             ElementGroup annotations,
                                             StandardDonutGenerator donutGen) {
        StandardBondGenerator bondGenerator;
        bondGenerator = new StandardBondGenerator(container, symbols,
                                                  parameters, annotations,
                                                  font, stroke, donutGen);
        IRenderingElement[] elements = new IRenderingElement[container.getBondCount()];
        for (int i = 0; i < container.getBondCount(); i++) {
            final IBond bond = container.getBond(i);
            if (!StandardGenerator.isHidden(bond)) {
                elements[i] = bondGenerator.generate(bond);
            }
        }
        return elements;
    }

    /**
     * Generate a rendering element for a given bond.
     *
     * @param bond a bond
     * @return rendering element
     */
    IRenderingElement generate(IBond bond) {
        final IAtom atom1 = bond.getBegin();
        final IAtom atom2 = bond.getEnd();

        IBond.Order order = bond.getOrder();

        if (order == null) order = IBond.Order.UNSET;

        IRenderingElement elem;

        switch (order) {
            case SINGLE:
                // TODO check small ring!
                if (bond.isAromatic()) {
                    if (donutGenerator.isDelocalised(bond))
                        elem = generateSingleBond(bond, atom1, atom2);
                    else if (forceDelocalised && bond.isInRing())
                        elem = generateDoubleBond(bond, forceDelocalised);
                    else
                        elem = generateSingleBond(bond, atom1, atom2);
                } else
                    elem = generateSingleBond(bond, atom1, atom2);
                break;
            case DOUBLE:
                if (bond.isAromatic()) {
                    if (donutGenerator.isDelocalised(bond))
                        elem = generateSingleBond(bond, atom1, atom2);
                    else
                        elem = generateDoubleBond(bond, forceDelocalised);
                } else
                    elem = generateDoubleBond(bond, false);
                break;
            case TRIPLE:
                elem =  generateTripleBond(bond, atom1, atom2);
                break;
            default:
                if (bond.isAromatic() && order == UNSET) {
                    if (donutGenerator.isDelocalised(bond))
                        elem = generateSingleBond(bond, atom1, atom2);
                    else
                        elem = generateDoubleBond(bond, true);
                } else {
                    // bond orders > 3 not supported
                    elem = generateDashedBond(atom1, atom2);
                }
                break;
        }

        // attachment point drawing, in future we could also draw the attach point
        // number, typically within a circle
        if (isAttachPoint(atom1)) {
            ElementGroup elemGrp = new ElementGroup();
            elemGrp.add(elem);
            elemGrp.add(generateAttachPoint(atom1, bond));
            elem = elemGrp;
        }
        if (isAttachPoint(atom2)) {
            ElementGroup elemGrp = new ElementGroup();
            elemGrp.add(elem);
            elemGrp.add(generateAttachPoint(atom2, bond));
            elem = elemGrp;
        }

        return elem;
    }

    /**
     * Generate a rendering element for single bond with the provided stereo type.
     *
     * @param bond   the bond to render
     * @param from   an atom
     * @param to     another atom
     * @return bond rendering element
     */
    private IRenderingElement generateSingleBond(IBond bond, IAtom from, IAtom to) {
        IBond.Display display = bond.getDisplay();
        if (display == null || display == IBond.Display.Solid)
            return generatePlainSingleBond(from, to);

        List<IBond> fromBonds = container.getConnectedBondsList(from);
        List<IBond> toBonds = container.getConnectedBondsList(to);

        fromBonds.remove(bond);
        toBonds.remove(bond);

        // add annotation label
        String label = StandardGenerator.getAnnotationLabel(bond);
        if (label != null) addAnnotation(from, to, label);

        switch (display) {
            case WedgedHashBegin:
                return generateHashedWedgeBond(from, to, toBonds);
            case WedgedHashEnd:
                return generateHashedWedgeBond(to, from, fromBonds);
            case WedgeBegin:
                return generateBoldWedgeBond(from, to, toBonds);
            case WedgeEnd:
                return generateBoldWedgeBond(to, from, fromBonds);
            case Wavy:
                return generateWavyBond(from, to);
            case Dash:
                return generateDashedBond(from, to);
            case ArrowEnd:
                return generateArrowBond(from, to);
            case ArrowBeg:
                return generateArrowBond(to, from);
            case Bold:
                return generateBoldBond(from, to, fromBonds, toBonds);
            case Hash:
                return generateHashBond(from, to, fromBonds, toBonds);
            case Dot:
                return generateDotBond(from, to);
            default:
                logger.warn("Unknown single bond display=", display, " is not displayed");
                return generatePlainSingleBond(from, to);
        }
    }

    /**
     * Generate a plain single bond between two atoms accounting for displayed symbols.
     *
     * @param from one atom
     * @param to   the other atom
     * @return rendering element
     */
    IRenderingElement generatePlainSingleBond(final IAtom from, final IAtom to) {
        return newLineElement(backOffPoint(from, to), backOffPoint(to, from));
    }

    /**
     * Generates a rendering element for a bold wedge bond (i.e. up) from one atom to another.
     *
     * @param from narrow end of the wedge
     * @param to   bold end of the wedge
     * @param toBonds bonds connected to the 'to atom'
     * @return the rendering element
     */
    IRenderingElement generateBoldWedgeBond(IAtom from, IAtom to, List<IBond> toBonds) {

        final Point2d fromPoint = from.getPoint2d();
        final Point2d toPoint = to.getPoint2d();

        final Point2d fromBackOffPoint = backOffPoint(from, to);
        final Point2d toBackOffPoint = backOffPoint(to, from);

        final Vector2d unit = newUnitVector(fromPoint, toPoint);
        final Vector2d perpendicular = newPerpendicularVector(unit);

        final double halfNarrowEnd = stroke / 2;
        final double halfWideEnd = wedgeWidth / 2;

        final double opposite = halfWideEnd - halfNarrowEnd;
        final double adjacent = fromPoint.distance(toPoint);

        final double fromOffset = halfNarrowEnd + opposite / adjacent * fromBackOffPoint.distance(fromPoint);
        final double toOffset = halfNarrowEnd + opposite / adjacent * toBackOffPoint.distance(fromPoint);

        // four points of the trapezoid
        Tuple2d a = sum(fromBackOffPoint, scale(perpendicular, fromOffset));
        Tuple2d b = sum(fromBackOffPoint, scale(perpendicular, -fromOffset));
        Tuple2d c = sum(toBackOffPoint, scale(perpendicular, -toOffset));
        Tuple2d e = toBackOffPoint;
        Tuple2d d = sum(toBackOffPoint, scale(perpendicular, toOffset));

        // don't adjust wedge if the angle is shallow than this amount
        final double threshold = Math.toRadians(15);

        // if the symbol at the wide end of the wedge is not displayed, we can improve
        // the aesthetics by adjusting the endpoints based on connected bond angles.
        if (fancyBoldWedges && !hasDisplayedSymbol(to)) {

            // slanted wedge
            if (toBonds.size() == 1) {

                final IBond toBondNeighbor = toBonds.get(0);
                final IAtom toNeighbor = toBondNeighbor.getOther(to);

                Vector2d refVector = newUnitVector(toPoint, toNeighbor.getPoint2d());
                boolean wideToWide = false;

                // special case when wedge bonds are in a bridged ring, wide-to-wide end we
                // don't want to slant as normal but rather butt up against each wind end
                if (atWideEndOfWedge(to, toBondNeighbor)) {
                    refVector = sum(refVector, negate(unit));
                    wideToWide = true;
                }

                final double theta = refVector.angle(unit);

                if (theta > threshold && theta + threshold + threshold < Math.PI) {
                    c = intersection(b, newUnitVector(b, c), toPoint, refVector);
                    d = intersection(a, newUnitVector(a, d), toPoint, refVector);

                    // the points c, d, and e lie on the center point of the line between
                    // the 'to' and 'toNeighbor'. Since the bond is drawn with a stroke and
                    // has a thickness we need to move these points slightly to be flush
                    // with the bond depiction, we only do this if the bond is not
                    // wide-on-wide with another bold wedge
                    if (!wideToWide) {
                        final double nudge = (stroke / 2) / Math.sin(theta);
                        c = sum(c, scale(unit, nudge));
                        d = sum(d, scale(unit, nudge));
                        e = sum(e, scale(unit, nudge));
                    }
                }
            }

            // bifurcated (forked) wedge
            else if (toBonds.size() > 1) {

                Vector2d refVectorA = getNearestVector(perpendicular, to, toBonds);
                Vector2d refVectorB = getNearestVector(negate(perpendicular), to, toBonds);

                if (refVectorB.angle(unit) > threshold) c = intersection(b, newUnitVector(b, c), toPoint, refVectorB);
                if (refVectorA.angle(unit) > threshold) d = intersection(a, newUnitVector(a, d), toPoint, refVectorA);
            }
        }

        return new GeneralPath(Arrays.asList(new MoveTo(new Point2d(a)), new LineTo(new Point2d(b)), new LineTo(
                new Point2d(c)), new LineTo(new Point2d(e)), new LineTo(new Point2d(d)), new Close()), foreground);
    }

    /**
     * Generates a rendering element for a hashed wedge bond (i.e. down) from one atom to another.
     *
     * @param from narrow end of the wedge
     * @param to   bold end of the wedge
     * @param toBonds bonds connected to
     * @return the rendering element
     */
    IRenderingElement generateHashedWedgeBond(IAtom from, IAtom to, List<IBond> toBonds) {
        final Point2d fromPoint = from.getPoint2d();
        final Point2d toPoint = to.getPoint2d();

        final Point2d fromBackOffPoint = backOffPoint(from, to);
        final Point2d toBackOffPoint = backOffPoint(to, from);

        final Vector2d unit = newUnitVector(fromPoint, toPoint);
        final Vector2d perpendicular = newPerpendicularVector(unit);

        final double halfNarrowEnd = stroke / 2;
        final double halfWideEnd = wedgeWidth / 2;

        final double opposite = halfWideEnd - halfNarrowEnd;
        double adjacent = fromPoint.distance(toPoint);

        final int nSections = (int) (adjacent / hashSpacing);
        final double step = adjacent / (nSections - 1);

        final ElementGroup group = new ElementGroup();

        final double start = hasDisplayedSymbol(from) ? fromPoint.distance(fromBackOffPoint) : Double.NEGATIVE_INFINITY;
        final double end = hasDisplayedSymbol(to) ? fromPoint.distance(toBackOffPoint) : Double.POSITIVE_INFINITY;

        // don't adjust wedge if the angle is shallow than this amount
        final double threshold = Math.toRadians(35);

        Vector2d hatchAngle = perpendicular;

        // fancy hashed wedges with slanted hatch sections aligned with neighboring bonds
        if (canDrawFancyHashedWedge(to, toBonds, adjacent)) {
            final IBond toBondNeighbor = toBonds.get(0);
            final IAtom toNeighbor = toBondNeighbor.getOther(to);

            Vector2d refVector = newUnitVector(toPoint, toNeighbor.getPoint2d());

            // special case when wedge bonds are in a bridged ring, wide-to-wide end we
            // don't want to slant as normal but rather butt up against each wind end
            if (atWideEndOfWedge(to, toBondNeighbor)) {
                refVector = sum(refVector, negate(unit));
                refVector.normalize();
            }

            // only slant if the angle isn't shallow
            double theta = refVector.angle(unit);
            if (theta > threshold && theta + threshold + threshold < Math.PI) {
                hatchAngle = refVector;
            }
        }

        for (int i = 0; i < nSections; i++) {
            final double distance = i * step;

            // don't draw if we're within an atom symbol
            if (distance < start || distance > end) continue;

            final double offset = halfNarrowEnd + opposite / adjacent * distance;
            Tuple2d interval = sum(fromPoint, scale(unit, distance));
            group.add(newLineElement(sum(interval, scale(hatchAngle, offset)),
                    sum(interval, scale(hatchAngle, -offset))));
        }

        return group;
    }

    /**
     * A fancy hashed wedge can be drawn if the following conditions are met:
     *      (1) {@link StandardGenerator.FancyHashedWedges} is enabled
     *      (2) Bond is of 'normal' length
     *      (3) The atom at the wide has one other neighbor and no symbol displayed
     *
     * @param to       the target atom
     * @param toBonds  bonds to the target atom (excluding the hashed wedge)
     * @param length   the length of the bond (unscaled)
     * @return a fancy hashed wedge can be rendered
     */
    private boolean canDrawFancyHashedWedge(IAtom to, List<IBond> toBonds, double length) {
        // a bond is long if is more than 4 units larger that the desired 'BondLength'
        final boolean longBond = (length * scale) - parameters.get(BondLength.class) > 4;
        return fancyHashedWedges && !longBond && !hasDisplayedSymbol(to) && toBonds.size() == 1;
    }

    /**
     * Generates a wavy bond (up or down stereo) between two atoms.
     *
     * @param from drawn from this atom
     * @param to   drawn to this atom
     * @return generated rendering element
     */
    IRenderingElement generateWavyBond(final IAtom from, final IAtom to) {

        final Point2d fromPoint = from.getPoint2d();
        final Point2d toPoint = to.getPoint2d();

        final Point2d fromBackOffPoint = backOffPoint(from, to);
        final Point2d toBackOffPoint = backOffPoint(to, from);

        final Vector2d unit = newUnitVector(fromPoint, toPoint);
        final Vector2d perpendicular = newPerpendicularVector(unit);

        final double length = fromPoint.distance(toPoint);

        // 2 times the number of wave sections because each semi circle is drawn with two parts
        final int nCurves = 2 * (int) (length / waveSpacing);
        final double step = length / nCurves;

        Vector2d peak = scale(perpendicular, step);

        boolean started = false;

        final double start = fromPoint.equals(fromBackOffPoint) ? Double.MIN_VALUE : fromPoint
                .distance(fromBackOffPoint);
        final double end = toPoint.equals(toBackOffPoint) ? Double.MAX_VALUE : fromPoint.distance(toBackOffPoint);

        List<PathElement> path = new ArrayList<PathElement>();
        if (start == Double.MIN_VALUE) {
            path.add(new MoveTo(fromPoint.x, fromPoint.y));
            started = true;
        }

        // the wavy bond is drawn using Bezier curves, removing the control points each
        // first 'endPoint' of the iteration forms a zig-zag pattern. The second 'endPoint'
        // lies on the central line between the atoms.

        // the following may help to visualise what we're doing,
        // s  = start (could be any end point)
        // e  = end point
        // cp = control points 1 and 2
        //
        //     cp2 e cp1                   cp2 e cp1
        //  cp1          cp2           cp1           cp2
        //  s ---------- e ----------- e ----------- e ------------ center line
        //               cp1           cp2           cp1
        //                   cp2 e cp1                   cp2 e
        //  |            |
        //  --------------
        //   one iteration
        //
        //  |     |
        //  -------
        //   one curveTo / 'step' distance

        // for the back off on atom symbols, the start position is the first end point after
        // the backed off point. Similarly, the curve is only drawn if the end point is
        // before the 'toBackOffPoint'

        for (int i = 1; i < nCurves; i += 2) {

            peak = negate(peak); // alternate wave side

            // curving away from the center line
            {
                double dist = i * step;

                if (dist >= start && dist <= end) {

                    // first end point
                    final Tuple2d endPoint = sum(sum(fromPoint, scale(unit, dist)), peak);
                    if (started) {

                        final Tuple2d controlPoint1 = sum(sum(fromPoint, scale(unit, (i - 1) * step)), scale(peak, 0.5));
                        final Tuple2d controlPoint2 = sum(sum(fromPoint, scale(unit, (i - 0.5) * step)), peak);
                        path.add(new CubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y,
                                endPoint.x, endPoint.y));

                    } else {
                        path.add(new MoveTo(endPoint.x, endPoint.y));
                        started = true;
                    }
                }
            }

            // curving towards the center line
            {
                double dist = (i + 1) * step;

                if (dist >= start && dist <= end) {

                    // second end point
                    final Tuple2d endPoint = sum(fromPoint, scale(unit, dist));

                    if (started) {
                        final Tuple2d controlPoint1 = sum(sum(fromPoint, scale(unit, (i + 0.5) * step)), peak);
                        final Tuple2d controlPoint2 = sum(sum(fromPoint, scale(unit, dist)), scale(peak, 0.5));
                        path.add(new CubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y,
                                endPoint.x, endPoint.y));
                    } else {
                        path.add(new MoveTo(endPoint.x, endPoint.y));
                        started = true;
                    }
                }
            }
        }

        return new GeneralPath(path, foreground).outline(stroke);
    }

    /**
     * Generates a double bond rendering element by deciding how best to display it.
     *
     * @param bond the bond to render
     * @param arom the second line should be dashed
     * @return rendering element
     */
    private IRenderingElement generateDoubleBond(IBond bond, boolean arom) {

        final boolean cyclic = ringMap.containsKey(bond);

        // select offset bonds from either the preferred ring or the whole structure
        final IAtomContainer refContainer = cyclic ? ringMap.get(bond) : container;

        final int length = refContainer.getAtomCount();
        final int index1 = refContainer.indexOf(bond.getBegin());
        final int index2 = refContainer.indexOf(bond.getEnd());

        // if the bond is in a cycle we are using ring bonds to determine offset, since rings
        // have been normalised and ordered to wind anti-clockwise we want to get the atoms
        // in the order they are in the ring.
        final boolean outOfOrder = cyclic && index1 == (index2 + 1) % length;

        final IAtom atom1 = outOfOrder ? bond.getEnd() : bond.getBegin();
        final IAtom atom2 = outOfOrder ? bond.getBegin() : bond.getEnd();

        if (IBond.Stereo.E_OR_Z.equals(bond.getStereo())) return generateCrossedDoubleBond(atom1, atom2);

        final List<IBond> atom1Bonds = refContainer.getConnectedBondsList(atom1);
        final List<IBond> atom2Bonds = refContainer.getConnectedBondsList(atom2);

        atom1Bonds.remove(bond);
        atom2Bonds.remove(bond);

        if (cyclic) {
            // get the winding relative to the ring
            final int wind1 = winding(atom1Bonds.get(0), bond);
            final int wind2 = winding(bond, atom2Bonds.get(0));
            if (wind1 > 0) {
                return generateOffsetDoubleBond(bond, atom1, atom2, atom1Bonds.get(0), atom2Bonds, arom);
            } else if (wind2 > 0) {
                return generateOffsetDoubleBond(bond, atom2, atom1, atom2Bonds.get(0), atom1Bonds, arom);
            } else {
                // special case, offset line is drawn on the opposite side for
                // when concave in macro cycle
                //
                //           ---
                //         a --- b
                //        /       \
                //    -- x         x --
                return generateOffsetDoubleBond(bond, atom1, atom2, atom1Bonds.get(0), atom2Bonds, true, arom);
            }
        } else if (!(hasDisplayedSymbol(atom1) && hasDisplayedSymbol(atom2))) {
            if (atom1Bonds.size() == 1 && atom2Bonds.isEmpty())
                return generateOffsetDoubleBond(bond, atom1, atom2, atom1Bonds.get(0), atom2Bonds, arom);
            else if (atom2Bonds.size() == 1 && atom1Bonds.isEmpty())
                return generateOffsetDoubleBond(bond, atom2, atom1, atom2Bonds.get(0), atom1Bonds, arom);
            else if (specialOffsetBondNextToWedge(atom1, atom1Bonds))
                return generateOffsetDoubleBond(bond, atom1, atom2, selectPlainSingleBond(atom1Bonds), atom2Bonds, arom);
            else if (specialOffsetBondNextToWedge(atom2, atom2Bonds))
                return generateOffsetDoubleBond(bond, atom2, atom1, selectPlainSingleBond(atom2Bonds), atom1Bonds, arom);
            else if (atom1Bonds.size() == 1)
                return generateOffsetDoubleBond(bond, atom1, atom2, atom1Bonds.get(0), atom2Bonds, arom);
            else if (atom2Bonds.size() == 1)
                return generateOffsetDoubleBond(bond, atom2, atom1, atom2Bonds.get(0), atom1Bonds, arom);
            else
                return generateCenteredDoubleBond(bond, atom1, atom2, atom1Bonds, atom2Bonds);
        } else {
            if (arom) {
                return generateDashedBond(atom1, atom2);
            } else {
                return generateCenteredDoubleBond(bond, atom1, atom2, atom1Bonds, atom2Bonds);
            }
        }
    }

    /**
     * Special condition for drawing offset bonds. If the double bond is adjacent to two bonds
     * and one of those bonds is wedge (with this atom at the wide end) and the other is plain
     * single bond, we can improve aesthetics by offsetting the double bond.
     *
     * @param atom an atom
     * @param bonds bonds connected to 'atom'
     * @return special case
     */
    private boolean specialOffsetBondNextToWedge(IAtom atom, List<IBond> bonds) {
        if (bonds.size() != 2) return false;
        if (atWideEndOfWedge(atom, bonds.get(0)) && isPlainBond(bonds.get(1))) return true;
        if (atWideEndOfWedge(atom, bonds.get(1)) && isPlainBond(bonds.get(0))) return true;
        return false;
    }

    /**
     * Select a plain bond from a list of bonds. If no bond was found, the first
     * is returned.
     *
     * @param bonds list of bonds
     * @return a plain bond
     * @see #isPlainBond(org.openscience.cdk.interfaces.IBond)
     */
    private IBond selectPlainSingleBond(List<IBond> bonds) {
        for (IBond bond : bonds) {
            if (isPlainBond(bond)) return bond;
        }
        return bonds.get(0);
    }

    /**
     * A plain bond is a single bond with no stereochemistry type.
     *
     * @param bond the bond to check
     * @return the bond is plain
     */
    private static boolean isPlainBond(IBond bond) {
        return SINGLE.equals(bond.getOrder()) && (bond.getStereo() == null || bond.getStereo() == NONE);
    }

    /**
     * Check if the provided bond is a wedge (bold or hashed) and whether the atom is at the wide
     * end.
     *
     * @param atom atom to check
     * @param bond bond to check
     * @return the atom is at the wide end of the wedge in the provided bond
     */
    private boolean atWideEndOfWedge(final IAtom atom, final IBond bond) {
        if (bond.getStereo() == null) return false;
        switch (bond.getDisplay()) {
            case Bold:
            case Hash:
                return true;
            case WedgeBegin:
            case WedgedHashBegin:
                return bond.getEnd().equals(atom);
            case WedgeEnd:
            case WedgedHashEnd:
                return bond.getBegin().equals(atom);
            default:
                return false;
        }
    }

    /**
     * Displays an offset double bond as per the IUPAC recomendation (GR-1.10) {@cdk.cite
     * Brecher08}. An offset bond has one line drawn between the two atoms and other draw to one
     * side. The side is determined by the 'atom1Bond' parameter. The first atom should not have a
     * displayed symbol.
     *
     * @param atom1      first atom
     * @param atom2      second atom
     * @param atom1Bond  the reference bond used to decide which side the bond is offset
     * @param atom2Bonds the bonds connected to atom 2
     * @return the rendered bond element
     */
    private IRenderingElement generateOffsetDoubleBond(IBond bond, IAtom atom1, IAtom atom2, IBond atom1Bond,
            List<IBond> atom2Bonds, boolean dashed) {
        return generateOffsetDoubleBond(bond, atom1, atom2, atom1Bond, atom2Bonds, false, dashed);
    }

    /**
     * Displays an offset double bond as per the IUPAC recomendation (GR-1.10) {@cdk.cite
     * Brecher08}. An offset bond has one line drawn between the two atoms and other draw to one
     * side. The side is determined by the 'atom1Bond' parameter. The first atom should not have a
     * displayed symbol.
     *
     * @param atom1      first atom
     * @param atom2      second atom
     * @param atom1Bond  the reference bond used to decide which side the bond is offset
     * @param atom2Bonds the bonds connected to atom 2
     * @param invert     invert the offset (i.e. opposite to reference bond)
     * @return the rendered bond element
     */
    private IRenderingElement generateOffsetDoubleBond(IBond bond, IAtom atom1, IAtom atom2, IBond atom1Bond,
            List<IBond> atom2Bonds, boolean invert, boolean dashed) {

        assert atom1Bond != null;

        final Point2d atom1Point = atom1.getPoint2d();
        final Point2d atom2Point = atom2.getPoint2d();

        final Point2d atom1BackOffPoint = backOffPoint(atom1, atom2);
        final Point2d atom2BackOffPoint = backOffPoint(atom2, atom1);

        final Vector2d unit = newUnitVector(atom1Point, atom2Point);
        Vector2d perpendicular = newPerpendicularVector(unit);

        final Vector2d reference = newUnitVector(atom1.getPoint2d(), atom1Bond.getOther(atom1).getPoint2d());

        // there are two perpendicular vectors, this check ensures we have one on the same side as
        // the reference
        if (reference.dot(perpendicular) < 0) perpendicular = negate(perpendicular);
        // caller requested inverted drawing
        if (invert) perpendicular = negate(perpendicular);

        // when the symbol is terminal, we move it such that it is between the two lines
        if (atom2Bonds.isEmpty() && hasDisplayedSymbol(atom2)) {
            final int atom2index = atomIndexMap.get(atom2);
            final Tuple2d nudge = scale(perpendicular, separation / 2);
            symbols[atom2index] = symbols[atom2index].translate(nudge.x, nudge.y);
        }

        // the offset line isn't drawn the full length and is backed off more depending on the
        // angle of adjacent bonds, see GR-1.10 in the IUPAC recommendations
        double atom1Offset = 0;
        double atom2Offset = 0;

        if (dashed || !hasDisplayedSymbol(atom1)) {
            atom1Offset = adjacentLength(sum(reference, unit), perpendicular, separation);
        }

        // reference bond may be on the other side (invert specified) - the offset needs negating
        if (reference.dot(perpendicular) < 0) atom1Offset = -atom1Offset;

        // the second atom may have zero or more bonds which we can use to get the offset
        // we find the one which is closest to the perpendicular vector
        if (!atom2Bonds.isEmpty() && (dashed || !hasDisplayedSymbol(atom2))) {
            Vector2d closest = getNearestVector(perpendicular, atom2, atom2Bonds);
            atom2Offset = adjacentLength(sum(closest, negate(unit)), perpendicular, separation);

            // closest bond may still be on the other side, if so the offset needs
            // negating
            if (closest.dot(perpendicular) < 0) atom2Offset = -atom2Offset;
        }

        final double halfBondLength = atom1Point.distance(atom2BackOffPoint) / 2;
        if (atom1Offset > halfBondLength || atom1Offset < 0) atom1Offset = 0;
        if (atom2Offset > halfBondLength || atom2Offset < 0) atom2Offset = 0;

        final ElementGroup group = new ElementGroup();

        // first of offset double bond may have some style
        switch (bond.getDisplay()) {
            case Bold:
                group.add(generateBoldBond(atom1, atom2,
                                           Collections.singletonList(atom1Bond), atom2Bonds));
                break;
            case Hash:
                group.add(generateHashBond(atom1, atom2,
                                           Collections.singletonList(atom1Bond), atom2Bonds));
                break;
            case Dash:
                group.add(generateDashedBond(atom1, atom2));
                break;
            case Dot:
                group.add(generateDashedBond(atom1, atom2));
                break;
            default: // solid
                group.add(newLineElement(atom1BackOffPoint, atom2BackOffPoint));
                break;
        }

        if (dashed) {
            Point2d beg = new Point2d(sum(atom1Point, scale(perpendicular, separation)));
            Point2d end = new Point2d(sum(atom2Point, scale(perpendicular, separation)));
            group.add(generateDashedBond(beg, end,
                                         atom1Offset,
                                         beg.distance(end) - atom2Offset));
        } else {
            Point2d beg = new Point2d(sum(atom1BackOffPoint, scale(perpendicular, separation)));
            Point2d end = new Point2d(sum(atom2BackOffPoint, scale(perpendicular, separation)));
            group.add(newLineElement(sum(beg, scale(unit, atom1Offset)),
                                     sum(end, scale(unit, -atom2Offset))));
        }

        // add annotation label on the opposite side
        String label = StandardGenerator.getAnnotationLabel(bond);
        if (label != null) addAnnotation(atom1, atom2, label, VecmathUtil.negate(perpendicular));

        return group;
    }

    /**
     * Generates a centered double bond. Here the lines are depicted each side and equidistant from
     * the line that travel through the two atoms.
     *
     * @param atom1      an atom
     * @param atom2      the other atom
     * @param atom1Bonds bonds to the first atom (excluding that being rendered)
     * @param atom2Bonds bonds to the second atom (excluding that being rendered)
     * @return the rendering element
     */
    private IRenderingElement generateCenteredDoubleBond(IBond bond, IAtom atom1, IAtom atom2, List<IBond> atom1Bonds,
            List<IBond> atom2Bonds) {

        final Point2d atom1BackOffPoint = backOffPoint(atom1, atom2);
        final Point2d atom2BackOffPoint = backOffPoint(atom2, atom1);

        final Vector2d unit = newUnitVector(atom1BackOffPoint, atom2BackOffPoint);
        final Vector2d perpendicular1 = newPerpendicularVector(unit);
        final Vector2d perpendicular2 = negate(perpendicular1);

        final double halfBondLength = atom1BackOffPoint.distance(atom2BackOffPoint) / 2;
        final double halfSeparation = separation / 2;

        ElementGroup group = new ElementGroup();

        Tuple2d line1Atom1Point = sum(atom1BackOffPoint, scale(perpendicular1, halfSeparation));
        Tuple2d line1Atom2Point = sum(atom2BackOffPoint, scale(perpendicular1, halfSeparation));
        Tuple2d line2Atom1Point = sum(atom1BackOffPoint, scale(perpendicular2, halfSeparation));
        Tuple2d line2Atom2Point = sum(atom2BackOffPoint, scale(perpendicular2, halfSeparation));

        // adjust atom 1 lines to be flush with adjacent bonds
        if (!hasDisplayedSymbol(atom1) && atom1Bonds.size() > 1) {
            Vector2d nearest1 = getNearestVector(perpendicular1, atom1, atom1Bonds);
            Vector2d nearest2 = getNearestVector(perpendicular2, atom1, atom1Bonds);

            double line1Adjust = adjacentLength(nearest1, perpendicular1, halfSeparation);
            double line2Adjust = adjacentLength(nearest2, perpendicular2, halfSeparation);

            // don't adjust beyond half the bond length
            if (line1Adjust > halfBondLength || line1Adjust < 0) line1Adjust = 0;
            if (line2Adjust > halfBondLength || line2Adjust < 0) line2Adjust = 0;

            // corner case when the adjacent bonds are acute to the double bond,
            if (nearest1.dot(unit) > 0) line1Adjust = -line1Adjust;
            if (nearest2.dot(unit) > 0) line2Adjust = -line2Adjust;

            line1Atom1Point = sum(line1Atom1Point, scale(unit, -line1Adjust));
            line2Atom1Point = sum(line2Atom1Point, scale(unit, -line2Adjust));
        }

        // adjust atom 2 lines to be flush with adjacent bonds
        if (!hasDisplayedSymbol(atom2) && atom2Bonds.size() > 1) {
            Vector2d nearest1 = getNearestVector(perpendicular1, atom2, atom2Bonds);
            Vector2d nearest2 = getNearestVector(perpendicular2, atom2, atom2Bonds);

            double line1Adjust = adjacentLength(nearest1, perpendicular1, halfSeparation);
            double line2Adjust = adjacentLength(nearest2, perpendicular2, halfSeparation);

            // don't adjust beyond half the bond length
            if (line1Adjust > halfBondLength || line1Adjust < 0) line1Adjust = 0;
            if (line2Adjust > halfBondLength || line2Adjust < 0) line2Adjust = 0;

            // corner case when the adjacent bonds are acute to the double bond
            if (nearest1.dot(unit) < 0) line1Adjust = -line1Adjust;
            if (nearest2.dot(unit) < 0) line2Adjust = -line2Adjust;

            line1Atom2Point = sum(line1Atom2Point, scale(unit, line1Adjust));
            line2Atom2Point = sum(line2Atom2Point, scale(unit, line2Adjust));
        }

        group.add(newLineElement(line1Atom1Point, line1Atom2Point));
        group.add(newLineElement(line2Atom1Point, line2Atom2Point));

        // add annotation label
        String label = StandardGenerator.getAnnotationLabel(bond);
        if (label != null) addAnnotation(atom1, atom2, label);

        return group;
    }

    /**
     * The crossed bond defines unknown geometric isomerism on a double bond. The cross is
     * displayed for {@link IBond.Stereo#E_OR_Z}.
     *
     * @param from drawn from this atom
     * @param to drawn to this atom
     * @return generated rendering element
     */
    private IRenderingElement generateCrossedDoubleBond(IAtom from, IAtom to) {

        final Point2d atom1BackOffPoint = backOffPoint(from, to);
        final Point2d atom2BackOffPoint = backOffPoint(to, from);

        final Vector2d unit = newUnitVector(atom1BackOffPoint, atom2BackOffPoint);
        final Vector2d perpendicular1 = newPerpendicularVector(unit);
        final Vector2d perpendicular2 = negate(perpendicular1);

        final double halfSeparation = separation / 2;

        // same as centered double bond, this could be improved by interpolating the points
        // during back off
        Tuple2d line1Atom1Point = sum(atom1BackOffPoint, scale(perpendicular1, halfSeparation));
        Tuple2d line1Atom2Point = sum(atom2BackOffPoint, scale(perpendicular1, halfSeparation));
        Tuple2d line2Atom1Point = sum(atom1BackOffPoint, scale(perpendicular2, halfSeparation));
        Tuple2d line2Atom2Point = sum(atom2BackOffPoint, scale(perpendicular2, halfSeparation));

        // swap end points to generate a cross
        ElementGroup group = new ElementGroup();
        group.add(newLineElement(line1Atom1Point, line2Atom2Point));
        group.add(newLineElement(line2Atom1Point, line1Atom2Point));
        return group;
    }

    /**
     * Generate a triple bond rendering, the triple is composed of a plain single bond and a
     * centered double bond.
     *
     * @param atom1 an atom
     * @param atom2 the other atom
     * @return triple bond rendering element
     */
    private IRenderingElement generateTripleBond(IBond bond, IAtom atom1, IAtom atom2) {
        ElementGroup group = new ElementGroup();

        final Point2d p1 = backOffPoint(atom1, atom2);
        final Point2d p2 = backOffPoint(atom2, atom1);

        final Vector2d perp = VecmathUtil.newPerpendicularVector(VecmathUtil.newUnitVector(p1, p2));
        perp.scale(separation);

        group.add(new LineElement(p1.x, p1.y,
                                  p2.x, p2.y,
                                  stroke, foreground));
        group.add(new LineElement(p1.x + perp.x, p1.y + perp.y,
                                  p2.x + perp.x, p2.y + perp.y,
                                  stroke, foreground));
        group.add(new LineElement(p1.x - perp.x, p1.y - perp.y,
                                  p2.x - perp.x, p2.y - perp.y,
                                  stroke, foreground));

        // add annotation label
        String label = StandardGenerator.getAnnotationLabel(bond);
        if (label != null) addAnnotation(atom1, atom2, label);

        return group;
    }

    /**
     * Draws a crossing wavy line at the end of a bond to indicate a point of attachment.
     *
     * @param atom atom that is an attachment point
     * @param bond bond that is an attachment point
     * @return the rendering element
     */
    private IRenderingElement generateAttachPoint(IAtom atom, IBond bond) {

        final Point2d  mid     = atom.getPoint2d();
        final Vector2d bndVec  = VecmathUtil.newUnitVector(atom, bond);
        final Vector2d bndXVec = VecmathUtil.newPerpendicularVector(bndVec);

        final double length = atom.getPoint2d().distance(bond.getOther(atom).getPoint2d());
        bndXVec.scale(length /2);
        final Tuple2d beg = VecmathUtil.sum(atom.getPoint2d(), bndXVec);
        bndXVec.scale(-1);
        final Tuple2d end = VecmathUtil.sum(atom.getPoint2d(), bndXVec);

        // wavy line between beg and end, see generateWavyBond for explanation

        final int nCurves = (int) (2 * Math.ceil(length / waveSpacing));
        final double step = length / nCurves;

        bndXVec.normalize();
        Vector2d peak = scale(bndVec, step);
        Vector2d unit = VecmathUtil.newUnitVector(beg, end);

        List<PathElement> path = new ArrayList<PathElement>();

        int halfNCurves = nCurves / 2;
        // one half
        path.add(new MoveTo(mid.x, mid.y));
        for (int i = 1; i < halfNCurves; i += 2) {
            peak.negate(); // alternate wave side

            // curving away from the center line
            {
                double dist = i * step;
                // first end point
                final Tuple2d endPoint = sum(sum(mid, scale(unit, dist)), peak);
                final Tuple2d controlPoint1 = sum(sum(mid, scale(unit, (i - 1) * step)), scale(peak, 0.5));
                final Tuple2d controlPoint2 = sum(sum(mid, scale(unit, (i - 0.5) * step)), peak);
                path.add(new CubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y,
                                     endPoint.x, endPoint.y));
            }

            // curving towards the center line
            {
                double dist = (i + 1) * step;
                // second end point
                final Tuple2d endPoint = sum(mid, scale(unit, dist));

                final Tuple2d controlPoint1 = sum(sum(mid, scale(unit, (i + 0.5) * step)), peak);
                final Tuple2d controlPoint2 = sum(sum(mid, scale(unit, dist)), scale(peak, 0.5));
                path.add(new CubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y,
                                     endPoint.x, endPoint.y));
            }
        }
        // other half
        unit.negate();
        peak.negate();
        path.add(new MoveTo(mid.x, mid.y));
        for (int i = 1; i < halfNCurves; i += 2) {
            peak.negate(); // alternate wave side

            // curving away from the center line
            {
                double dist = i * step;
                // first end point
                final Tuple2d endPoint = sum(sum(mid, scale(unit, dist)), peak);
                final Tuple2d controlPoint1 = sum(sum(mid, scale(unit, (i - 1) * step)), scale(peak, 0.5));
                final Tuple2d controlPoint2 = sum(sum(mid, scale(unit, (i - 0.5) * step)), peak);
                path.add(new CubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y,
                                     endPoint.x, endPoint.y));
            }

            // curving towards the center line
            {
                double dist = (i + 1) * step;
                // second end point
                final Tuple2d endPoint = sum(mid, scale(unit, dist));

                final Tuple2d controlPoint1 = sum(sum(mid, scale(unit, (i + 0.5) * step)), peak);
                final Tuple2d controlPoint2 = sum(sum(mid, scale(unit, dist)), scale(peak, 0.5));
                path.add(new CubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y,
                                     endPoint.x, endPoint.y));
            }
        }

        return new GeneralPath(path, foreground).outline(stroke);
    }

    /**
     * Determine if an atom is an attach point.
     *
     * @param atom potential attach point atom
     * @return the atom is an attachment point
     */
    private boolean isAttachPoint(IAtom atom) {
        return atom instanceof IPseudoAtom && ((IPseudoAtom) atom).getAttachPointNum() > 0;
    }

    /**
     * Add an annotation label for the bond between the two atoms. The side of the bond that
     * is chosen is arbitrary.
     *
     * @param atom1 first atom
     * @param atom2 second atom
     * @param label annotation label
     * @see #addAnnotation(IAtom, IAtom, String, Vector2d)
     */
    private void addAnnotation(IAtom atom1, IAtom atom2, String label) {
        Vector2d perpendicular = VecmathUtil.newPerpendicularVector(VecmathUtil.newUnitVector(atom1.getPoint2d(),
                atom2.getPoint2d()));
        addAnnotation(atom1, atom2, label, perpendicular);
    }

    /**
     * Add an annotation label for the bond between the two atoms on the specified 'side' (providied
     * as a the perpendicular directional vector).
     *
     * @param atom1 first atom
     * @param atom2 second atom
     * @param label annotation label
     * @param perpendicular the vector along which to place the annotation (starting from the midpoint)
     */
    private void addAnnotation(IAtom atom1, IAtom atom2, String label, Vector2d perpendicular) {
        Point2d midPoint = VecmathUtil.midpoint(atom1.getPoint2d(), atom2.getPoint2d());

        TextOutline outline = StandardGenerator.generateAnnotation(midPoint, label, perpendicular, annotationDistance,
                annotationScale, font, null);
        annotations.add(MarkedElement.markup(GeneralPath.shapeOf(outline.getOutline(), annotationColor), "annotation"));
    }

    /**
     * Generates a rendering element for displaying an 'unknown' bond type.
     *
     * @param fromPoint drawn from this point
     * @param toPoint drawn to this point
     * @param start only start drawing dashes after this point
     * @param end stop drawing dashes after this point
     * @return rendering of unknown bond
     */
    IRenderingElement generateDashedBond(Point2d fromPoint, Point2d toPoint, double start, double end) {

        final Vector2d unit = newUnitVector(fromPoint, toPoint);

        final int nDashes = parameters.get(StandardGenerator.DashSection.class);

        final double step = fromPoint.distance(toPoint) / ((3 * nDashes) - 2);

        ElementGroup group = new ElementGroup();

        double distance = 0;

        for (int i = 0; i < nDashes; i++) {

            // draw a full dash section
            if (distance > start && distance + step < end) {
                group.add(newLineElement(sum(fromPoint, scale(unit, distance)),
                        sum(fromPoint, scale(unit, distance + step))));
            }
            // draw a dash section that starts late
            else if (distance + step > start && distance + step < end) {
                group.add(newLineElement(sum(fromPoint, scale(unit, start)),
                        sum(fromPoint, scale(unit, distance + step))));
            }
            // draw a dash section that stops early
            else if (distance > start && distance < end) {
                group.add(newLineElement(sum(fromPoint, scale(unit, distance)), sum(fromPoint, scale(unit, end))));
            }

            distance += step;
            distance += step; // the gap
            distance += step; // the gap
        }

        return group;
    }

    /**
     * Dashed bond, {@link IBond.Display#Dash}.
     * @param from start atom
     * @param to end atom
     * @return the bond glyph
     */
    IRenderingElement generateDashedBond(IAtom from, IAtom to) {
        final Point2d fromPoint = from.getPoint2d();
        final Point2d toPoint = to.getPoint2d();

        final double start = hasDisplayedSymbol(from) ? fromPoint.distance(backOffPoint(from, to))
                                                      : Double.NEGATIVE_INFINITY;
        final double end = hasDisplayedSymbol(to) ? fromPoint.distance(backOffPoint(to, from))
                                                  : Double.POSITIVE_INFINITY;

        return generateDashedBond(fromPoint, toPoint, start, end);
    }

    /**
     * Arrow bond, {@link IBond.Display#ArrowBeg}
     * and {@link {@link IBond.Display#ArrowBeg}.
     * @param from start atom
     * @param to end atom (arrow points here)
     * @return the bond glyph
     */
    IRenderingElement generateArrowBond(IAtom from, IAtom to) {

        ElementGroup group = new ElementGroup();

        Point2d fromPoint = backOffPoint(from, to);
        Point2d toPoint = backOffPoint(to, from);

        Vector2d unit          = newUnitVector(fromPoint, toPoint);
        Vector2d perpendicular = newPerpendicularVector(unit);

        Vector2d arrowHeadLen    = scale(unit, -1.25*wedgeWidth);
        Vector2d arrowHeadIndent = scale(unit, -wedgeWidth);

        // four points of the trapezoid
        Tuple2d a = toPoint;
        Tuple2d b = sum(sum(toPoint, arrowHeadLen), scale(perpendicular, 0.6*wedgeWidth));
        Tuple2d c = sum(toPoint, arrowHeadIndent);
        Tuple2d d = sum(sum(toPoint, arrowHeadLen), scale(perpendicular, -0.6*wedgeWidth));

        group.add(newLineElement(fromPoint, sum(toPoint, arrowHeadIndent)));
        group.add(newPolygon(foreground,a,b,c,d));

        return group;
    }

    /**
     * Bold bond, {@link org.openscience.cdk.interfaces.IBond.Display#Bold}
     * @param from start atom
     * @param to end atom
     * @return the bond glyph
     */
    IRenderingElement generateBoldBond(IAtom from, IAtom to,
                                       List<IBond> fromBonds,
                                       List<IBond> toBonds) {

        Point2d fromPoint = backOffPoint(from, to);
        Point2d toPoint = backOffPoint(to, from);

        Vector2d unit          = newUnitVector(fromPoint, toPoint);
        Vector2d perpendicular = newPerpendicularVector(unit);

        final double halfWideEnd = wedgeWidth / 2;

        // four points of the trapezoid
        Tuple2d a = sum(fromPoint, scale(perpendicular, halfWideEnd));
        Tuple2d b = sum(fromPoint, scale(perpendicular, -halfWideEnd));
        Tuple2d c = sum(toPoint, scale(perpendicular, -halfWideEnd));
        Tuple2d d = sum(toPoint, scale(perpendicular, halfWideEnd));

        // don't adjust wedge if the angle is shallow than this amount
        final double threshold = Math.toRadians(15);

        // if the symbol at the wide end of the wedge is not displayed, we can improve
        // the aesthetics by adjusting the endpoints based on connected bond angles.
        if (fancyBoldWedges) {
            if (!hasDisplayedSymbol(to)) {

                // slanted wedge
                if (toBonds.size() == 1) {

                    final IBond toBondNeighbor = toBonds.get(0);
                    final IAtom toNeighbor     = toBondNeighbor.getOther(to);

                    Vector2d refVector  = newUnitVector(toPoint, toNeighbor.getPoint2d());
                    boolean  wideToWide = false;

                    // special case when wedge bonds are in a bridged ring, wide-to-wide end we
                    // don't want to slant as normal but rather butt up against each wind end
                    if (atWideEndOfWedge(to, toBondNeighbor)) {
                        refVector = sum(refVector, negate(unit));
                        wideToWide = true;
                    }

                    final double theta = refVector.angle(unit);

                    if (theta > threshold && theta + threshold + threshold < Math.PI) {
                        c = intersection(b, newUnitVector(b, c), toPoint, refVector);
                        d = intersection(a, newUnitVector(a, d), toPoint, refVector);

                        // the points c, d, and e lie on the center point of the line between
                        // the 'to' and 'toNeighbor'. Since the bond is drawn with a stroke and
                        // has a thickness we need to move these points slightly to be flush
                        // with the bond depiction, we only do this if the bond is not
                        // wide-on-wide with another bold wedge
                        if (!wideToWide) {
                            final double nudge = (stroke / 2) / Math.sin(theta);
                            c = sum(c, scale(unit, nudge));
                            d = sum(d, scale(unit, nudge));
                        }
                    }
                }
            }

            if (!hasDisplayedSymbol(from)) {

                unit = negate(unit);

                // slanted wedge
                if (fromBonds.size() == 1) {

                    final IBond fromNbrBond = fromBonds.get(0);
                    final IAtom fromNbr     = fromNbrBond.getOther(from);

                    Vector2d refVector  = newUnitVector(fromPoint, fromNbr.getPoint2d());
                    boolean  wideToWide = false;

                    // special case when wedge bonds are in a bridged ring, wide-to-wide end we
                    // don't want to slant as normal but rather butt up against each wind end
                    if (atWideEndOfWedge(from, fromNbrBond)) {
                        refVector = sum(refVector, negate(unit));
                        wideToWide = true;
                    }

                    final double theta = refVector.angle(unit);

                    if (theta > threshold && theta + threshold + threshold < Math.PI) {
                        b = intersection(c, newUnitVector(c, b), fromPoint, refVector);
                        a = intersection(d, newUnitVector(d, a), fromPoint, refVector);

                        // the points c, d, and e lie on the center point of the line between
                        // the 'to' and 'toNeighbor'. Since the bond is drawn with a stroke and
                        // has a thickness we need to move these points slightly to be flush
                        // with the bond depiction, we only do this if the bond is not
                        // wide-on-wide with another bold wedge
                        if (!wideToWide) {
                            final double nudge = (stroke / 2) / Math.sin(theta);
                            a = sum(a, scale(unit, nudge));
                            b = sum(b, scale(unit, nudge));
                        }
                    }
                }
            }
        }
        return newPolygon(foreground,a,b,c,d);
    }

    /**
     * Hashed bond, {@link org.openscience.cdk.interfaces.IBond.Display#Hash}
     * @param from start atom
     * @param to end atom
     * @return the bond glyph
     */
    IRenderingElement generateHashBond(IAtom from, IAtom to,
                                       List<IBond> fromBonds,
                                       List<IBond> toBonds) {

        final Point2d fromPoint = from.getPoint2d();
        final Point2d toPoint = to.getPoint2d();

        final Point2d fromBackOffPoint = backOffPoint(from, to);
        final Point2d toBackOffPoint = backOffPoint(to, from);

        final Vector2d unit = newUnitVector(fromPoint, toPoint);
        final Vector2d perpendicular = newPerpendicularVector(unit);

        final double halfWideEnd = wedgeWidth / 2;

        double adjacent = fromPoint.distance(toPoint);

        final int nSections = (int) (adjacent / hashSpacing);
        final double step = adjacent / (nSections - 1);

        final ElementGroup group = new ElementGroup();

        final double start = hasDisplayedSymbol(from) ? fromPoint.distance(fromBackOffPoint) : Double.NEGATIVE_INFINITY;
        final double end = hasDisplayedSymbol(to) ? fromPoint.distance(toBackOffPoint) : Double.POSITIVE_INFINITY;

        // don't adjust wedge if the angle is shallow than this amount
        final double threshold = Math.toRadians(35);

        for (int i = 0; i < nSections; i++) {
            final double distance = i * step;

            // don't draw if we're within an atom symbol
            if (distance < start || distance > end) continue;

            Tuple2d interval = sum(fromPoint, scale(unit, distance));
            group.add(newLineElement(sum(interval, scale(perpendicular, halfWideEnd)),
                                     sum(interval, scale(perpendicular, -halfWideEnd))));
        }

        return group;
    }


    /**
     * Dotted bond, {@link org.openscience.cdk.interfaces.IBond.Display#Dot}
     * @param from start atom
     * @param to end atom
     * @return the bond glyph
     */
    IRenderingElement generateDotBond(IAtom from, IAtom to) {

        final Point2d fromPoint = from.getPoint2d();
        final Point2d toPoint = to.getPoint2d();

        final Point2d fromBackOffPoint = backOffPoint(from, to);
        final Point2d toBackOffPoint = backOffPoint(to, from);

        final Vector2d unit = newUnitVector(fromPoint, toPoint);
        final Vector2d perpendicular = newPerpendicularVector(unit);

        double adjacent = fromPoint.distance(toPoint);

        final int nSections = (int) (adjacent / (3*stroke));
        final double step = adjacent / (nSections - 1);

        final ElementGroup group = new ElementGroup();

        final double start = hasDisplayedSymbol(from) ? fromPoint.distance(fromBackOffPoint) : Double.NEGATIVE_INFINITY;
        final double end = hasDisplayedSymbol(to) ? fromPoint.distance(toBackOffPoint) : Double.POSITIVE_INFINITY;

        for (int i = 0; i < nSections; i++) {
            final double distance = i * step;

            // don't draw if we're within an atom symbol
            if (distance < start || distance > end) continue;

            Tuple2d interval = sum(fromPoint, scale(unit, distance));
            group.add(new OvalElement(interval.x, interval.y, 0.75*stroke, foreground));
        }

        return group;
    }

    /**
     * Create a new line element between two points. The line has the specified stroke and
     * foreground color.
     *
     * @param a start of the line
     * @param b end of the line
     * @return line rendering element
     */
    IRenderingElement newLineElement(Tuple2d a, Tuple2d b) {
        return new LineElement(a.x, a.y, b.x, b.y, stroke, foreground);
    }

    /**
     * Utility to create a filled polygon
     * @param c color
     * @param points the points, last point will be closed to first
     * @return the polygon
     */
    GeneralPath newPolygon(Color c, Tuple2d ... points) {
        List<PathElement> elements = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            Tuple2d point = points[i];
            if (i == 0)
                elements.add(new MoveTo(new Point2d(point)));
            else
                elements.add(new LineTo(new Point2d(point)));
        }
        elements.add(new Close());
        return new GeneralPath(elements, c);
    }

    /**
     * Determine the backed off (start) point of the 'from' atom for the line between 'from' and
     * 'to'.
     *
     * @param from start atom
     * @param to   end atom
     * @return the backed off point of 'from' atom
     */
    Point2d backOffPoint(IAtom from, IAtom to) {
        return backOffPointOf(symbols[atomIndexMap.get(from)], from.getPoint2d(), to.getPoint2d(), backOff);
    }

    /**
     * Check if an atom has a displayed symbol.
     *
     * @param atom the atom to check
     * @return the atom has a displayed symbol
     */
    boolean hasDisplayedSymbol(IAtom atom) {
        return symbols[atomIndexMap.get(atom)] != null;
    }

    /**
     * Determine the backed off (start) point of the 'from' atom for the line between 'from' and
     * 'to' given the symbol present at the 'from' point and the back off amount.
     *
     * @param symbol    the symbol present at the 'fromPoint' atom, may be null
     * @param fromPoint the location of the from atom
     * @param toPoint   the location of the to atom
     * @param backOff   the amount to back off from the symbol
     * @return the backed off (start) from point
     */
    static Point2d backOffPointOf(AtomSymbol symbol, Point2d fromPoint, Point2d toPoint, double backOff) {

        // no symbol
        if (symbol == null) return fromPoint;

        final Point2d intersect = toVecmathPoint(symbol.getConvexHull().intersect(toAwtPoint(fromPoint),
                toAwtPoint(toPoint)));

        // does not intersect
        if (intersect == null) return fromPoint;

        // move the point away from the intersect by the desired back off amount
        final Vector2d unit = newUnitVector(fromPoint, toPoint);
        return new Point2d(sum(intersect, scale(unit, backOff)));
    }

    /**
     * Determine the winding of two bonds. The winding is > 0 for anti clockwise and < 0
     * for clockwise and is relative to bond 1.
     *
     * @param bond1 first bond
     * @param bond2 second bond
     * @return winding relative to bond
     * @throws java.lang.IllegalArgumentException bonds share no atoms
     */
    static int winding(IBond bond1, IBond bond2) {
        final IAtom atom1 = bond1.getBegin();
        final IAtom atom2 = bond1.getEnd();
        if (bond2.contains(atom1)) {
            return winding(atom2.getPoint2d(), atom1.getPoint2d(), bond2.getOther(atom1).getPoint2d());
        } else if (bond2.contains(atom2)) {
            return winding(atom1.getPoint2d(), atom2.getPoint2d(), bond2.getOther(atom2).getPoint2d());
        } else {
            throw new IllegalArgumentException("Bonds do not share any atoms");
        }
    }

    /**
     * Creates a mapping of bonds to preferred rings (stored as IAtomContainers).
     *
     * @param container structure representation
     * @param smallest smallest ring set to use (e.g. through each bond)
     * @return bond to ring map
     */
    static Map<IBond, IAtomContainer> ringPreferenceMap(IAtomContainer container,
                                                        IRingSet smallest) {

        if (smallest == null)
            smallest = Cycles.edgeShort(container).toRingSet();

        final List<IAtomContainer> rings = AtomContainerSetManipulator.getAllAtomContainers(smallest);

        Collections.sort(rings, new RingBondOffsetComparator(container));

        final Map<IBond, IAtomContainer> ringMap = new HashMap<IBond, IAtomContainer>();

        // index bond -> ring based on the first encountered bond
        for (IAtomContainer ring : rings) {
            normalizeRingWinding(ring);
            for (IBond bond : ring.bonds()) {
                if (ringMap.containsKey(bond)) continue;
                ringMap.put(bond, ring);
            }
        }

        return Collections.unmodifiableMap(ringMap);
    }

    /**
     * Creates a mapping of bonds to preferred rings (stored as IAtomContainers).
     *
     * @param container structure representation
     * @return bond to ring map
     */
    static Map<IBond, IAtomContainer> ringPreferenceMap(IAtomContainer container) {
        return ringPreferenceMap(container, Cycles.edgeShort(container).toRingSet());
    }

    /**
     * Normalise the ring ordering in a ring such that the overall winding is anti clockwise.
     * The normalisation exploits the fact that (most) rings will be drawn with more convex
     * turns (i.e. close to 30 degrees). This not bullet proof, consider a hexagon drawn as
     * a three point star.
     *
     * @param container the ring to normalize
     */
    static void normalizeRingWinding(IAtomContainer container) {

        int prev = container.getAtomCount() - 1;
        int curr = 0;
        int next = 1;

        int n = container.getAtomCount();

        int winding = 0;

        while (curr < n) {
            winding += winding(container.getAtom(prev).getPoint2d(), container.getAtom(curr).getPoint2d(), container
                    .getAtom(next % n).getPoint2d());
            prev = curr;
            curr = next;
            next = next + 1;
        }

        if (winding < 0) {
            IAtom[] atoms = new IAtom[n];
            for (int i = 0; i < n; i++)
                atoms[n - i - 1] = container.getAtom(i);
            container.setAtoms(atoms);
        }
    }

    /**
     * Determine the winding of three points using the determinant.
     *
     * @param a first point
     * @param b second point
     * @param c third point
     * @return < 0 = clockwise, 0 = linear, > 0 anti-clockwise
     */
    static int winding(Point2d a, Point2d b, Point2d c) {
        return (int) Math.signum((b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x));
    }

    /**
     * Order rings by preference of double bond offset. Rings that appear first have preference of
     * the double bond.
     *
     * 1. rings of size 6, 5, 7, 4, 3 are preferred (in that order) 2. rings with more double bonds
     * are preferred 3. rings with a higher carbon count are preferred
     */
    static final class RingBondOffsetComparator implements Comparator<IAtomContainer> {

        private static final int[] PREFERENCE_INDEX = new int[8];

        static {
            int preference = 0;
            for (int size : new int[]{6, 5, 7, 4, 3}) {
                PREFERENCE_INDEX[size] = preference++;
            }
        }

        private final boolean hasMetal;

        /**
         * Create a new comparator.
         */
        RingBondOffsetComparator(IAtomContainer mol) {
            hasMetal = hasMetal(mol);
        }

        RingBondOffsetComparator() {
            hasMetal = false;
        }

        private static boolean hasMetal(IAtomContainer mol) {
            for (IAtom atom : mol.atoms())
                if (Elements.isMetal(atom))
                    return true;
            return false;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public int compare(IAtomContainer ringa, IAtomContainer ringb) {

            // non-metal rings (e.g. carbo/hetro cycles first)
            if (hasMetal) {
                int cmp = Boolean.compare(hasMetal(ringa),
                                          hasMetal(ringb));
                if (cmp != 0)
                    return cmp;
            }

            // order by size 6,5,7,4,3,rest
            int sizeCmp = Ints.compare(sizePreference(ringa.getAtomCount()),
                    sizePreference(ringb.getAtomCount()));
            if (sizeCmp != 0) return sizeCmp;

            // now order by number of double bonds
            int piBondCmp = Ints.compare(nDoubleBonds(ringa), nDoubleBonds(ringb));
            if (piBondCmp != 0) return -piBondCmp;

            // order by element frequencies, all carbon rings are preferred
            int[] freqA = countLightElements(ringa);
            int[] freqB = countLightElements(ringb);

            for (Elements element : Arrays.asList(Elements.Carbon, Elements.Nitrogen, Elements.Oxygen, Elements.Sulfur,
                    Elements.Phosphorus)) {
                int elemCmp = Ints.compare(freqA[element.number()], freqB[element.number()]);
                if (elemCmp != 0) return -elemCmp;
            }

            return 0;
        }

        /**
         * Convert an absolute size value into the size preference.
         *
         * @param size number of atoms or bonds in a ring
         * @return size preference
         */
        static int sizePreference(int size) {
            if (size < 3) throw new IllegalArgumentException("a ring must have at least 3 atoms");
            if (size > 7) return size;
            return PREFERENCE_INDEX[size];
        }

        /**
         * Count the number of double bonds in a container.
         *
         * @param container structure representation
         * @return number of double bonds
         */
        static int nDoubleBonds(IAtomContainer container) {
            int count = 0;
            for (IBond bond : container.bonds())
                if (IBond.Order.DOUBLE.equals(bond.getOrder())) count++;
            return count;
        }

        /**
         * Count the light elements (atomic number < 19) in an atom container. The count is provided
         * as a frequency vector indexed by atomic number.
         *
         * @param container structure representation
         * @return frequency vector of atomic numbers 0-18
         */
        static int[] countLightElements(IAtomContainer container) {
            // count elements up to Argon (number=18)
            int[] freq = new int[19];
            for (IAtom atom : container.atoms()) {
                if (atom.getAtomicNumber() >= 0 && atom.getAtomicNumber() < 19)
                    freq[atom.getAtomicNumber()]++;
            }
            return freq;
        }
    }
}
