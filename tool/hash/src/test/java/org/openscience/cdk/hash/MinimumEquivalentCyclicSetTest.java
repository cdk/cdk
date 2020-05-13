/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class MinimumEquivalentCyclicSetTest {

    /**
     * @cdk.inchi InChI=1S/C8H16/c1-7-3-5-8(2)6-4-7/h7-8H,3-6H2,1-2H3
     */
    @Test
    public void testFind_OneChoice() throws Exception {

        IAtomContainer dummy = mock(IAtomContainer.class);
        int[][] g = new int[][]{{1, 5, 6}, {0, 2}, {1, 3}, {2, 4, 7}, {3, 5}, {0, 4}, {0}, {3}};

        // this mock the invariants
        long[] values = new long[]{1, 0, 0, 1, 0, 0, 2, 2};

        EquivalentSetFinder finder = new MinimumEquivalentCyclicSet();
        Set<Integer> set = finder.find(values, dummy, g);

        assertThat(set.size(), is(2));
        assertTrue(set.contains(0));
        assertTrue(set.contains(3));

    }

    /**
     * @cdk.inchi InChI=1S/C24H36/c1-2-13(1)19-20(14-3-4-14)22(16-7-8-16)24(18-11-12-18)23(17-9-10-17)21(19)15-5-6-15/h13-24H,1-12H2
     */
    @Test
    public void testFind_TwoChoices() throws Exception {

        IAtomContainer dummy = mock(IAtomContainer.class);
        int[][] g = new int[][]{{1, 5, 6}, {0, 2, 8}, {1, 3, 9}, {2, 4, 7}, {3, 5, 10}, {0, 4, 11}, {0, 14, 15},
                {3, 20, 21}, {1, 12, 13}, {2, 22, 23}, {4, 18, 19}, {5, 16, 17}, {8, 13}, {8, 12}, {6, 15}, {14, 6},
                {11, 17}, {16, 11}, {10, 19}, {10, 18}, {7, 21}, {7, 20}, {9, 23}, {22, 9}};

        // this mock the invariants
        long[] values = new long[]{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

        EquivalentSetFinder finder = new MinimumEquivalentCyclicSet();
        Set<Integer> set = finder.find(values, dummy, g);

        assertThat(set.size(), is(6));
        assertTrue(set.contains(0));
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
        assertTrue(set.contains(4));
        assertTrue(set.contains(5));

        // invert values, we should now get the other set
        values = new long[]{1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

        set = finder.find(values, dummy, g);

        assertThat(set.size(), is(6));
        assertTrue(set.contains(6));
        assertTrue(set.contains(7));
        assertTrue(set.contains(8));
        assertTrue(set.contains(9));
        assertTrue(set.contains(10));
        assertTrue(set.contains(11));
    }

    /**
     * @cdk.inchi InChI=1S/C10H22/c1-7(2)10(8(3)4)9(5)6/h7-10H,1-6H3
     */
    @Test
    public void testFind_NoChoice() throws Exception {

        IAtomContainer dummy = mock(IAtomContainer.class);
        int[][] g = new int[][]{{1, 2, 3}, {0, 4, 9}, {0, 5, 6}, {0, 7, 8}, {1}, {2}, {2}, {3}, {3}, {1}};

        // this mock the invariants
        long[] values = new long[]{1, 2, 2, 2, 3, 3, 3, 3, 3, 3};

        EquivalentSetFinder finder = new MinimumEquivalentCyclicSet();
        Set<Integer> set = finder.find(values, dummy, g);

        assertThat(set.size(), is(0));
    }

}
