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
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates {@link IRenderingElement}s for bonds. The generator is internal and called by the
 * {@link org.openscience.cdk.renderer.generators.standard.StandardGenerator}. A new bond generator
 * is required for each container instance.
 *
 * @author John May
 */
final class StandardBondGenerator {

    /**
     * Generate a rendering element for a given bond.
     *
     * @param bond a bond
     * @return rendering element
     */
    IRenderingElement generate(IBond bond) {
        // TODO
        return new ElementGroup();
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
