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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @cdk.module test-signature
 * @author maclean
 *
 */
public class OrbitTest {

    private String orbitLabel;

    private Orbit  orbit;

    private Orbit  unsortedOrbit;

    @Before
    public void setUp() {

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
    public void iteratorTest() {
        int count = 0;
        List<Integer> indices = orbit.getAtomIndices();
        for (Integer i : orbit) {
            Assert.assertEquals(i, indices.get(count));
            count++;
        }
        Assert.assertEquals(indices.size(), count);
    }

    @Test
    public void testClone() {
        Orbit clonedOrbit = (Orbit) orbit.clone();
        List<Integer> indices = orbit.getAtomIndices();
        List<Integer> clonedIndices = clonedOrbit.getAtomIndices();
        Assert.assertEquals(indices, clonedIndices);
        Assert.assertEquals(orbit.getLabel(), clonedOrbit.getLabel());
    }

    @Test
    public void isEmptyTest() {
        Assert.assertFalse("The setUp method should have made an orbit with " + "some indices in it", orbit.isEmpty());
        List<Integer> indices = new ArrayList<Integer>();
        for (int index : orbit) {
            indices.add(index);
        }
        for (int index : indices) {
            orbit.remove(index);
        }
        Assert.assertTrue("Orbit should now be empty", orbit.isEmpty());
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
    public void sortTest() {
        Assert.assertFalse("Unsorted orbit is actually sorted", isSorted(unsortedOrbit));
        unsortedOrbit.sort();
        Assert.assertTrue("Orbit is not sorted after sort called", isSorted(unsortedOrbit));
    }

    @Test
    public void getHeightTest() {
        Assert.assertEquals(2, orbit.getHeight());
    }

    @Test
    public void getAtomIndicesTest() {
        Assert.assertNotNull(orbit.getAtomIndices());
    }

    @Test
    public void addAtomTest() {
        Assert.assertEquals(4, orbit.getAtomIndices().size());
        orbit.addAtom(4);
        Assert.assertEquals(5, orbit.getAtomIndices().size());
    }

    @Test
    public void hasLabelTest() {
        Assert.assertTrue(orbit.hasLabel(orbitLabel));
    }

    @Test
    public void getFirstAtomTest() {
        Assert.assertEquals(0, orbit.getFirstAtom());
    }

    @Test
    public void removeTest() {
        Assert.assertEquals(4, orbit.getAtomIndices().size());
        orbit.remove(0);
        Assert.assertEquals(3, orbit.getAtomIndices().size());
    }

    @Test
    public void getLabelTest() {
        Assert.assertEquals(orbitLabel, orbit.getLabel());
    }

    @Test
    public void containsTest() {
        for (int index : orbit) {
            Assert.assertTrue("Index " + index + " not in orbit", orbit.contains(index));
        }
    }

    @Test
    public void toStringTest() {
        Assert.assertEquals("ORBIT [0, 1, 2, 3]", orbit.toString());
    }

}
