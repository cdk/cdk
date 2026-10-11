/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.graph;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.openscience.cdk.graph.InitialCyclesTest.anthracene;
import static org.openscience.cdk.graph.InitialCyclesTest.bicyclo;
import static org.openscience.cdk.graph.InitialCyclesTest.cyclophane_even;
import static org.openscience.cdk.graph.InitialCyclesTest.naphthalene;
import static org.openscience.cdk.graph.InitialCyclesTest.norbornane;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.Intractable;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.math.BigInteger;

/**
 * @author John May
 */
class RelevantCyclesTest {

    @Test
    void noGraph() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> {
                                    new RelevantCycles((int[][]) null);
                                });
    }

    @Test
    void noInitialCycles() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> {
                                    new RelevantCycles((InitialCycles) null);
                                });
    }

    @Test
    void paths_norbornane() {
        int[][] norbornane = norbornane();
        RelevantCycles relevant = new RelevantCycles(norbornane);
        int[][] paths = relevant.paths();
        assertThat(paths.length, is(2));
        int[][] expected = new int[][]{{5, 6, 2, 1, 0, 5}, {5, 6, 2, 3, 4, 5}};
        assertThat(paths, is(expected));
    }

    @Test
    void paths_bicyclo() {
        int[][] bicyclo = bicyclo();
        RelevantCycles relevant = new RelevantCycles(bicyclo);
        int[][] paths = relevant.paths();
        assertThat(paths.length, is(3));
        int[][] expected = new int[][]{{5, 0, 1, 2, 3, 4, 5}, {5, 0, 1, 2, 7, 6, 5}, {5, 4, 3, 2, 7, 6, 5}};
        assertThat(paths, is(expected));
    }

    @Test
    void paths_napthalene() {
        int[][] napthalene = naphthalene();
        RelevantCycles relevant = new RelevantCycles(napthalene);
        int[][] paths = relevant.paths();
        assertThat(paths.length, is(2));
        int[][] expected = new int[][]{{5, 0, 1, 2, 3, 4, 5}, {5, 4, 7, 8, 9, 6, 5}};
        assertThat(paths, is(expected));
    }

    @Test
    void paths_anthracene() {
        int[][] anthracene = anthracene();
        RelevantCycles relevant = new RelevantCycles(anthracene);
        int[][] paths = relevant.paths();
        assertThat(paths.length, is(3));
        int[][] expected = new int[][]{{5, 0, 1, 2, 3, 4, 5}, {9, 6, 5, 4, 7, 8, 9}, {9, 8, 10, 11, 12, 13, 9}};
        assertThat(paths, is(expected));
    }

    @Test
    void paths_cyclophane_even() {
        int[][] cyclophane_even = cyclophane_even();
        RelevantCycles relevant = new RelevantCycles(cyclophane_even);
        int[][] paths = relevant.paths();
        assertThat(paths.length, is(3));
        int[][] expected = new int[][]{{3, 2, 1, 0, 5, 4, 3}, {3, 6, 7, 8, 9, 10, 11, 0, 1, 2, 3},
                {3, 6, 7, 8, 9, 10, 11, 0, 5, 4, 3}};
        assertThat(paths, is(expected));
    }

    @Test
    void paths_cyclophane_odd() {
        int[][] cyclophane_even = cyclophane_even();
        RelevantCycles relevant = new RelevantCycles(cyclophane_even);
        int[][] paths = relevant.paths();
        assertThat(paths.length, is(3));
        int[][] expected = new int[][]{{3, 2, 1, 0, 5, 4, 3}, {3, 6, 7, 8, 9, 10, 11, 0, 1, 2, 3},
                {3, 6, 7, 8, 9, 10, 11, 0, 5, 4, 3}};
        assertThat(paths, is(expected));
    }

    @Test
    void size_norbornane() {
        int[][] norbornane = norbornane();
        RelevantCycles relevant = new RelevantCycles(norbornane);
        assertThat(relevant.size(), is(2));
    }

    @Test
    void size_bicyclo() {
        int[][] bicyclo = bicyclo();
        RelevantCycles relevant = new RelevantCycles(bicyclo);
        assertThat(relevant.size(), is(3));
    }

    @Test
    void size_napthalene() {
        int[][] napthalene = naphthalene();
        RelevantCycles relevant = new RelevantCycles(napthalene);
        assertThat(relevant.size(), is(2));
    }

    @Test
    void size_anthracene() {
        int[][] anthracene = anthracene();
        RelevantCycles relevant = new RelevantCycles(anthracene);
        assertThat(relevant.size(), is(3));
    }

    @Test
    void size_cyclophane_even() {
        int[][] cyclophane_even = cyclophane_even();
        RelevantCycles relevant = new RelevantCycles(cyclophane_even);
        assertThat(relevant.size(), is(3));
    }

    @Test
    void size_cyclophane_odd() {
        int[][] cyclophane_even = cyclophane_even();
        RelevantCycles relevant = new RelevantCycles(cyclophane_even);
        assertThat(relevant.size(), is(3));
    }

    // [k]cycloparaphenylene has 2^k + k relevant cycles.
    @Test
    void size_cycloparaphenylene() {
        for (int k : new int[]{4, 8, 30, 31, 32, 64}) {
            BigInteger expected = BigInteger.ONE.shiftLeft(k).add(BigInteger.valueOf(k));
            int count = expected.min(BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
            Assertions.assertEquals(count, new RelevantCycles(cycloparaphenylene(k)).size());
        }
    }

    @Test
    void paths_cycloparaphenylene() {
        int[][] graph = cycloparaphenylene(32);
        RelevantCycles relevant = new RelevantCycles(graph);
        // Check the count first so the regression never enumerates an overflowing family.
        Assertions.assertEquals(Integer.MAX_VALUE, relevant.size());
        Assertions.assertThrows(IllegalStateException.class, relevant::paths);
        Assertions.assertThrows(IllegalStateException.class, () -> new EssentialCycles(graph));
    }

    @Test
    void intractable_cycloparaphenylene() {
        for (int k : new int[]{31, 32, 64}) {
            int[][] graph = cycloparaphenylene(k);
            Assertions.assertEquals(Integer.MAX_VALUE, new RelevantCycles(graph).size());
            Assertions.assertThrows(Intractable.class,
                                    () -> Cycles.relevant().find(mock(IAtomContainer.class), graph, graph.length));
            Assertions.assertThrows(Intractable.class,
                                    () -> Cycles.essential().find(mock(IAtomContainer.class), graph, graph.length));
        }
    }

    // Six-membered rings joined from the 4-position to the 1-position of the next ring.
    static int[][] cycloparaphenylene(int k) {
        int[][] graph = new int[6 * k][];
        for (int i = 0; i < k; i++) {
            int a = 6 * i;
            graph[a] = new int[]{a + 1, a + 5, 6 * ((i + k - 1) % k) + 3};
            graph[a + 1] = new int[]{a, a + 2};
            graph[a + 2] = new int[]{a + 1, a + 3};
            graph[a + 3] = new int[]{a + 2, a + 4, 6 * ((i + 1) % k)};
            graph[a + 4] = new int[]{a + 3, a + 5};
            graph[a + 5] = new int[]{a + 4, a};
        }
        return graph;
    }
}
