/* Copyright (C) 2009-2010 maclean {gilleain.torrance@gmail.com}
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.signature;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @cdk.module test-signature
 * @author maclean
 *
 */
class OrbitTest {

    private String orbitLabel;

    private Orbit  orbit;

    private Orbit  unsortedOrbit;

    @BeforeEach
    void setUp() {

        // make a test orbit instance, with a nonsense
        // string label, and some number of 'indices'
        orbitLabel = "ORBIT";
        int height = 2;
        orbit = new Orbit(orbitLabel, height);
        int[] atomIndices = new int[]{0, 1, 2, 3};
        for (int atomIndex : atomIndices) {
            orbit.addAtom(atomIndex);
        }

        // also make an unsorted orbit
        String unsortedOrbitLabel = "UNSORTED_ORBIT";
        int unsortedHeight = 2;
        unsortedOrbit = new Orbit(unsortedOrbitLabel, unsortedHeight);
        int[] unsortedAtomIndices = new int[]{3, 1, 0, 2};
        for (int atomIndex : unsortedAtomIndices) {
            unsortedOrbit.addAtom(atomIndex);
        }

    }

    @Test
    void iteratorTest() {
        int count = 0;
        List<Integer> indices = orbit.getAtomIndices();
        for (Integer i : orbit) {
            Assertions.assertEquals(i, indices.get(count));
            count++;
        }
        Assertions.assertEquals(indices.size(), count);
    }

    @Test
    void testClone() {
        Orbit clonedOrbit = (Orbit) orbit.clone();
        List<Integer> indices = orbit.getAtomIndices();
        List<Integer> clonedIndices = clonedOrbit.getAtomIndices();
        Assertions.assertEquals(indices, clonedIndices);
        Assertions.assertEquals(orbit.getLabel(), clonedOrbit.getLabel());
    }

    @Test
    void isEmptyTest() {
        Assertions.assertFalse(orbit.isEmpty(), "The setUp method should have made an orbit with " + "some indices in it");
        List<Integer> indices = new ArrayList<>();
        for (int index : orbit) {
            indices.add(index);
        }
        for (int index : indices) {
            orbit.remove(index);
        }
        Assertions.assertTrue(orbit.isEmpty(), "Orbit should now be empty");
    }

    private boolean isSorted(Orbit orbit) {
        int prev = -1;
        for (int index : orbit) {
            if (prev == -1 || index > prev) {
                prev = index;
            } else {
                return false;
            }
        }
        return true;
    }

    @Test
    void sortTest() {
        Assertions.assertFalse(isSorted(unsortedOrbit), "Unsorted orbit is actually sorted");
        unsortedOrbit.sort();
        Assertions.assertTrue(isSorted(unsortedOrbit), "Orbit is not sorted after sort called");
    }

    @Test
    void getHeightTest() {
        Assertions.assertEquals(2, orbit.getHeight());
    }

    @Test
    void getAtomIndicesTest() {
        Assertions.assertNotNull(orbit.getAtomIndices());
    }

    @Test
    void addAtomTest() {
        Assertions.assertEquals(4, orbit.getAtomIndices().size());
        orbit.addAtom(4);
        Assertions.assertEquals(5, orbit.getAtomIndices().size());
    }

    @Test
    void hasLabelTest() {
        Assertions.assertTrue(orbit.hasLabel(orbitLabel));
    }

    @Test
    void getFirstAtomTest() {
        Assertions.assertEquals(0, orbit.getFirstAtom());
    }

    @Test
    void removeTest() {
        Assertions.assertEquals(4, orbit.getAtomIndices().size());
        orbit.remove(0);
        Assertions.assertEquals(3, orbit.getAtomIndices().size());
    }

    @Test
    void getLabelTest() {
        Assertions.assertEquals(orbitLabel, orbit.getLabel());
    }

    @Test
    void containsTest() {
        for (int index : orbit) {
            Assertions.assertTrue(orbit.contains(index), "Index " + index + " not in orbit");
        }
    }

    @Test
    void toStringTest() {
        Assertions.assertEquals("ORBIT [0, 1, 2, 3]", orbit.toString());
    }

}
