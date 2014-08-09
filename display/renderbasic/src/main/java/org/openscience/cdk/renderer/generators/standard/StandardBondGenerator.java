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
import javax.vecmath.Vector2d;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private IRenderingElement generateDoubleBond(IBond bond) {
        return new ElementGroup();
    }

    // GR-1.10 Sidedness of double bonds
    private IRenderingElement generateOffsetDoubleBond(IAtom atom1, IAtom atom2, IBond atom1Bond, List<IBond> atom2Bonds) {
        return new ElementGroup();
    }

    private IRenderingElement generateCenteredDoubleBond(IAtom atom1, IAtom atom2, List<IBond> atom1Bonds, List<IBond> atom2Bonds) {
        return new ElementGroup();
    }

    private IRenderingElement generateCrossedDoubleBond(IAtom atom1, IAtom atom2) {
        return new ElementGroup();
    }

    private IRenderingElement generateTripleBond(IAtom atom1, IAtom atom2) {
        return new ElementGroup();
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
    IRenderingElement newLineElement(Point2d a, Point2d b) {
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
