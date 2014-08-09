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

import com.google.common.primitives.Ints;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.adjacentLength;
import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.negate;
import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.newPerpendicularVector;
import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.newUnitVector;
import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.scale;
import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.sum;
import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.toAwtPoint;
import static org.openscience.cdk.renderer.generators.standard.VecmathUtil.toVecmathPoint;

/**
 * Generates {@link IRenderingElement}s for bonds. The generator is internal and called by the
 * {@link org.openscience.cdk.renderer.generators.standard.StandardGenerator}. A new bond generator
 * is required for each container instance.
 *
 * @author John May
 */
final class StandardBondGenerator {

    private final IAtomContainer container;
    private final AtomSymbol[]   symbols;
    private final RendererModel  parameters;

    // logging
    private final ILoggingTool logger = LoggingToolFactory.createLoggingTool(getClass());

    // indexes of atoms and rings
    private final Map<IAtom, Integer> atomIndexMap = new HashMap<IAtom, Integer>();
    private final Map<IBond, IAtomContainer> ringMap;

    // parameters
    private final double stroke;
    private final double separation;
    private final double backOff;
    private final double wedgeWidth;
    private final double hatchSections;
    private final Color  foreground;


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
    private StandardBondGenerator(IAtomContainer container, AtomSymbol[] symbols, RendererModel parameters, double stroke) {
        this.container = container;
        this.symbols = symbols;
        this.parameters = parameters;

        // index atoms and rings
        for (int i = 0; i < container.getAtomCount(); i++)
            atomIndexMap.put(container.getAtom(i), i);
        ringMap = ringPreferenceMap(container);

        // set parameters (TODO need parameters from RendererModel)
        this.stroke = stroke;
        this.separation = 5 * stroke;
        this.backOff = 2 * stroke;
        this.wedgeWidth = 7 * stroke;
        this.hatchSections = 16;
        this.foreground = Color.BLACK;
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
    static IRenderingElement[] generateBonds(IAtomContainer container, AtomSymbol[] symbols, RendererModel parameters, double stroke) {
        StandardBondGenerator bondGenerator = new StandardBondGenerator(container, symbols, parameters, stroke);
        IRenderingElement[] elements = new IRenderingElement[container.getBondCount()];
        for (int i = 0; i < container.getBondCount(); i++) {
            elements[i] = bondGenerator.generate(container.getBond(i));
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
        final IAtom atom1 = bond.getAtom(0);
        final IAtom atom2 = bond.getAtom(1);

        final IBond.Order order = bond.getOrder();
        final IBond.Stereo stereo = bond.getStereo();

        if (order == null)
            return generateDashedBond(atom1, atom2);

        switch (order) {
            case SINGLE:
                return generateSingleBond(atom1, atom2, stereo);
            case DOUBLE:
                return generateDoubleBond(bond);
            case TRIPLE:
                return generateTripleBond(atom1, atom2);
        }

        // bond order > 3 not supported
        return generateDashedBond(atom1, atom2);
    }

    /**
     * Generate a rendering element for single bond with the provided stereo type.
     *
     * @param from   an atom
     * @param to     another atom
     * @param stereo the stereo type of the bond
     * @return bond rendering element
     */
    private IRenderingElement generateSingleBond(IAtom from, IAtom to, IBond.Stereo stereo) {
        if (stereo == null)
            return generatePlainSingleBond(from, to);
        switch (stereo) {
            case NONE:
                return generatePlainSingleBond(from, to);
            case DOWN:
                return generateHashedWedgeBond(from, to);
            case DOWN_INVERTED:
                return generateHashedWedgeBond(to, from);
            case UP:
                return generateBoldWedgeBond(from, to);
            case UP_INVERTED:
                return generateBoldWedgeBond(to, from);
            case UP_OR_DOWN:
            case UP_OR_DOWN_INVERTED: // up/down is undirected
                return generateWavyBond(to, from);
            default:
                logger.warn("Unknown single bond stereochemistry ", stereo, " is not displayed");
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

    IRenderingElement generateBoldWedgeBond(IAtom from, IAtom to) {
        return new ElementGroup();
    }

    IRenderingElement generateHashedWedgeBond(IAtom from, IAtom to) {
        return new ElementGroup();
    }

    IRenderingElement generateWavyBond(IAtom from, IAtom to) {
        return new ElementGroup();
    }

    /**
     * Generates a double bond rendering element by deciding how best to display it.
     *
     * @param bond the bond to render
     * @return rendering element
     */
    private IRenderingElement generateDoubleBond(IBond bond) {

        // select offset bonds from either the preferred ring or the whole structure
        final IAtomContainer refContainer = ringMap.containsKey(bond) ? ringMap.get(bond)
                                                                      : container;

        final IAtom atom1 = bond.getAtom(0);
        final IAtom atom2 = bond.getAtom(1);

        if (IBond.Stereo.E_OR_Z.equals(bond.getStereo()))
            return generateCrossedDoubleBond(atom1, atom2);

        final List<IBond> atom1Bonds = refContainer.getConnectedBondsList(atom1);
        final List<IBond> atom2Bonds = refContainer.getConnectedBondsList(atom2);

        atom1Bonds.remove(bond);
        atom2Bonds.remove(bond);

        if (atom1Bonds.size() == 1 && !hasDisplayedSymbol(atom1)) {
            return generateOffsetDoubleBond(atom1, atom2, atom1Bonds.get(0), atom2Bonds);
        }
        else if (atom2Bonds.size() == 1 && !hasDisplayedSymbol(atom2)) {
            return generateOffsetDoubleBond(atom2, atom1, atom2Bonds.get(0), atom1Bonds);
        }
        else {
            return generateCenteredDoubleBond(atom1, atom2, atom1Bonds, atom2Bonds);
        }
    }

    /**
     * Displays an offset double bond as per the IUPAC recomendation (GR-1.10) {@cdk.cite
     * Brecher08}. An offset bond has one line drawn between the two atoms and other draw to one
     * side. The side is determined by the 'atom1Bond' parameter. The first atom should not have
     * a displayed symbol.
     *
     * @param atom1      first atom
     * @param atom2      second atom
     * @param atom1Bond  the reference bond used to decide which side the bond is offset
     * @param atom2Bonds the bonds connected to atom 2
     * @return the rendered bond element
     */
    private IRenderingElement generateOffsetDoubleBond(IAtom atom1, IAtom atom2, IBond atom1Bond, List<IBond> atom2Bonds) {

        assert !hasDisplayedSymbol(atom1);
        assert atom1Bond != null;
        
        final Point2d atom1Point = atom1.getPoint2d();
        final Point2d atom2Point = atom2.getPoint2d();

        final Point2d atom2BackOffPoint = backOffPoint(atom2, atom1);

        final Vector2d unit = newUnitVector(atom1Point, atom2Point);
        Vector2d perpendicular = newPerpendicularVector(unit);

        final Vector2d reference = newUnitVector(atom1.getPoint2d(), atom1Bond.getConnectedAtom(atom1).getPoint2d());

        // there are two perpendicular vectors, this check ensures we have one on the same side as
        // the reference 
        if (reference.dot(perpendicular) < 0)
            perpendicular = negate(perpendicular);

        // when the symbol is terminal, we move it such that it is between the two lines
        if (atom2Bonds.isEmpty() && hasDisplayedSymbol(atom2)) {
            final int atom2index = atomIndexMap.get(atom2);
            final Tuple2d nudge = scale(perpendicular, separation / 2);
            symbols[atom2index] = symbols[atom2index].translate(nudge.x, nudge.y);
        }
        
        // the offset line isn't drawn the full length and is backed off more depending on the
        // angle of adjacent bonds, see GR-1.10 in the IUPAC recommendations
        double atom1Offset = adjacentLength(reference, perpendicular, separation);
        double atom2Offset = 0;

        // the second atom may have zero or more bonds which we can use to get the offset
        // we find the one which is closest to the perpendicular vector
        if (!atom2Bonds.isEmpty() && !hasDisplayedSymbol(atom2)) {
            Vector2d closest = VecmathUtil.getNearestVector(perpendicular, atom2, atom2Bonds);
            atom2Offset = adjacentLength(closest, perpendicular, separation);
            
            // closest bond may still be on the other side, if so the offset needs
            // negating
            if (closest.dot(perpendicular) < 0)
                atom2Offset = -atom2Offset;  
        }

        final ElementGroup group = new ElementGroup();

        group.add(newLineElement(atom1Point, atom2BackOffPoint));
        group.add(newLineElement(sum(sum(atom1Point,
                                         scale(perpendicular, separation)),
                                     scale(unit, atom1Offset)),
                                 sum(sum(atom2BackOffPoint,
                                         scale(perpendicular, separation)),
                                     scale(unit, -atom2Offset))));

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
    private IRenderingElement generateCenteredDoubleBond(IAtom atom1, IAtom atom2, List<IBond> atom1Bonds, List<IBond> atom2Bonds) {

        final Point2d atom1BackOffPoint = backOffPoint(atom1, atom2);
        final Point2d atom2BackOffPoint = backOffPoint(atom2, atom1);

        final Vector2d unit = newUnitVector(atom1BackOffPoint, atom2BackOffPoint);
        final Vector2d perpendicular1 = newPerpendicularVector(unit);
        final Vector2d perpendicular2 = negate(perpendicular1);

        final double halfSeparation = separation / 2;

        ElementGroup group = new ElementGroup();
        
        Tuple2d line1Atom1Point = sum(atom1BackOffPoint, scale(perpendicular1, halfSeparation));
        Tuple2d line1Atom2Point = sum(atom2BackOffPoint, scale(perpendicular1, halfSeparation));
        Tuple2d line2Atom1Point = sum(atom1BackOffPoint, scale(perpendicular2, halfSeparation));
        Tuple2d line2Atom2Point = sum(atom2BackOffPoint, scale(perpendicular2, halfSeparation));
        
        if (!hasDisplayedSymbol(atom1) && atom1Bonds.size() > 1) {
            // adjust atom 1            
        }
        
        if (!hasDisplayedSymbol(atom2) && atom2Bonds.size() > 1) {
            // adjust atom 2            
        }
        
        group.add(newLineElement(line1Atom1Point, line1Atom2Point));
        group.add(newLineElement(line2Atom1Point, line2Atom2Point));

        return group;
    }

    private IRenderingElement generateCrossedDoubleBond(IAtom atom1, IAtom atom2) {
        return new ElementGroup();
    }

    /**
     * Generate a triple bond rendering, the triple is composed of a plain single bond and a
     * centered double bond.
     *
     * @param atom1 an atom
     * @param atom2 the other atom
     * @return triple bond rendering element
     */
    private IRenderingElement generateTripleBond(IAtom atom1, IAtom atom2) {
        ElementGroup group = new ElementGroup();
        group.add(generatePlainSingleBond(atom1, atom2));
        group.add(generateCenteredDoubleBond(atom1, atom2,
                                             Collections.<IBond>emptyList(), Collections.<IBond>emptyList()));
        return group;
    }

    IRenderingElement generateDashedBond(IAtom atom1, IAtom atom2) {
        return new ElementGroup();
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
        if (symbol == null)
            return fromPoint;

        final Point2d intersect = toVecmathPoint(symbol.getConvexHull()
                                                       .intersect(toAwtPoint(fromPoint),
                                                                  toAwtPoint(toPoint)));

        // does not intersect
        if (intersect == null)
            return fromPoint;


        // move the point away from the intersect by the desired back off amount
        final Vector2d unit = newUnitVector(fromPoint, toPoint);
        return new Point2d(sum(intersect, scale(unit, backOff)));
    }


    /**
     * Creates a mapping of bonds to preferred rings (stored as IAtomContainers).
     *
     * @param container structure representation
     * @return bond to ring map
     */
    static Map<IBond, IAtomContainer> ringPreferenceMap(IAtomContainer container) {

        final IRingSet relevantRings = Cycles.relevant(container).toRingSet();
        final List<IAtomContainer> rings = AtomContainerSetManipulator.getAllAtomContainers(relevantRings);

        Collections.sort(rings, new RingBondOffsetComparator());

        final Map<IBond, IAtomContainer> ringMap = new HashMap<IBond, IAtomContainer>();

        // index bond -> ring based on the first encountered bond
        for (IAtomContainer ring : rings) {
            for (IBond bond : ring.bonds()) {
                if (ringMap.containsKey(bond))
                    continue;
                ringMap.put(bond, ring);
            }
        }

        return Collections.unmodifiableMap(ringMap);
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

        /**
         * Create a new comparator.
         */
        RingBondOffsetComparator() {
        }

        /**
         * @inheritDoc
         */
        @Override public int compare(IAtomContainer containerA, IAtomContainer containerB) {

            // first order by size
            int sizeCmp = Ints.compare(sizePreference(containerA.getAtomCount()),
                                       sizePreference(containerB.getAtomCount()));
            if (sizeCmp != 0)
                return sizeCmp;

            // now order by number of double bonds
            int piBondCmp = Ints.compare(nDoubleBonds(containerA), nDoubleBonds(containerB));
            if (piBondCmp != 0)
                return -piBondCmp;

            // order by element frequencies, all carbon rings are preferred 
            int[] freqA = countLightElements(containerA);
            int[] freqB = countLightElements(containerB);

            for (Elements element : Arrays.asList(Elements.Carbon,
                                                  Elements.Nitrogen,
                                                  Elements.Oxygen,
                                                  Elements.Sulfur,
                                                  Elements.Phosphorus)) {
                int elemCmp = Ints.compare(freqA[element.number()], freqB[element.number()]);
                if (elemCmp != 0)
                    return -elemCmp;
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
            if (size < 3)
                throw new IllegalArgumentException("a ring must have at least 3 atoms");
            if (size > 7)
                return size;
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
                if (IBond.Order.DOUBLE.equals(bond.getOrder()))
                    count++;
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
                freq[atom.getAtomicNumber()]++;
            }
            return freq;
        }
    }
}
