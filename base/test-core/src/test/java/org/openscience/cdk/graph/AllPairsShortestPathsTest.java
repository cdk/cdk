/*
 * Copyright (C) 2012 John May <jwmay@users.sf.net>
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
package org.openscience.cdk.graph;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.templates.TestMoleculeFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 * @cdk.module test-core
 */
public class AllPairsShortestPathsTest {

    @Test(expected = NullPointerException.class)
    public void testConstruction_Null() {
        new AllPairsShortestPaths(null);
    }

    @Test
    public void testConstruction_Empty() {

        AllPairsShortestPaths asp = new AllPairsShortestPaths(new AtomContainer());

        // all vs all fro -10 -> 10
        for (int i = -10; i < 10; i++) {
            for (int j = -10; j < 10; j++) {

                assertArrayEquals(new int[0][0], asp.from(i).pathsTo(0));
                assertArrayEquals(new int[0], asp.from(i).pathTo(0));
                assertArrayEquals(new IAtom[0], asp.from(i).atomsTo(0));

                assertThat(asp.from(i).nPathsTo(j), is(0));
                assertThat(asp.from(i).distanceTo(j), is(Integer.MAX_VALUE));

            }
        }

    }

    @Test
    public void testFrom_Atom_Benzene() throws Exception {

        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        AllPairsShortestPaths asp = new AllPairsShortestPaths(benzene);

        IAtom c1 = benzene.getAtom(0);
        IAtom c2 = benzene.getAtom(1);
        IAtom c3 = benzene.getAtom(2);
        IAtom c4 = benzene.getAtom(3);
        IAtom c5 = benzene.getAtom(4);
        IAtom c6 = benzene.getAtom(5);

        //    c2 - c3
        //  /        \
        // c1         c4
        //  \        /
        //    c6 - c5

        assertNotNull(asp.from(c1));
        assertNotNull(asp.from(c2));
        assertNotNull(asp.from(c3));
        assertNotNull(asp.from(c4));
        assertNotNull(asp.from(c5));
        assertNotNull(asp.from(c6));

        {
            IAtom[] expected = new IAtom[]{c1, c2, c3};
            IAtom[] actual = asp.from(c1).atomsTo(c3);
            assertArrayEquals(expected, actual);
        }

        {
            IAtom[] expected = new IAtom[]{c3, c2, c1};
            IAtom[] actual = asp.from(c3).atomsTo(c1);
            assertArrayEquals(expected, actual);
        }

        {
            IAtom[] expected = new IAtom[]{c1, c6, c5};
            IAtom[] actual = asp.from(c1).atomsTo(c5);
            assertArrayEquals(expected, actual);
        }

        {
            IAtom[] expected = new IAtom[]{c5, c6, c1};
            IAtom[] actual = asp.from(c5).atomsTo(c1);
            assertArrayEquals(expected, actual);
        }

    }

    @Test
    public void testFrom_Int_Benzene() throws Exception {

        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        AllPairsShortestPaths asp = new AllPairsShortestPaths(benzene);

        //    1 - 2
        //  /       \
        // 0         3
        //  \       /
        //    5 - 4

        assertNotNull(asp.from(0));
        assertNotNull(asp.from(1));
        assertNotNull(asp.from(2));
        assertNotNull(asp.from(3));
        assertNotNull(asp.from(4));
        assertNotNull(asp.from(5));

        {
            int[] expected = new int[]{0, 1, 2};
            int[] actual = asp.from(0).pathTo(2);
            assertArrayEquals(expected, actual);
        }

        {
            int[] expected = new int[]{2, 1, 0};
            int[] actual = asp.from(2).pathTo(0);
            assertArrayEquals(expected, actual);
        }

        {
            int[] expected = new int[]{0, 5, 4};
            int[] actual = asp.from(0).pathTo(4);
            assertArrayEquals(expected, actual);
        }

        {
            int[] expected = new int[]{4, 5, 0};
            int[] actual = asp.from(4).pathTo(0);
            assertArrayEquals(expected, actual);
        }

    }
}
