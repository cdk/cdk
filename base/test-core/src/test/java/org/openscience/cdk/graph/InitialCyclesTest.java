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

import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-core
 */
public class InitialCyclesTest {

    @Test
    public void lengths_empty() {
        assertFalse(new InitialCycles(new int[0][0]).lengths().iterator().hasNext());
    }

    @Test
    public void cyclesOfLength_empty() {
        assertTrue(new InitialCycles(new int[0][0]).cyclesOfLength(0).isEmpty());
    }

    @Test
    public void graph() {
        int[][] g = new int[0][0];
        assertThat(new InitialCycles(g).graph(), is(sameInstance(g)));
    }

    @Test
    public void lengths_K1() {
        assertFalse(new InitialCycles(k1()).lengths().iterator().hasNext());
    }

    @Test
    public void cycles_K1() {
        InitialCycles initial = new InitialCycles(k1());
        assertThat(initial.cycles().size(), is(0));
    }

    @Test
    public void lengths_K4() {
        assertThat(new InitialCycles(k4()).lengths(), hasItem(3));
    }

    @Test
    public void cycles_K4() {
        InitialCycles initial = new InitialCycles(k4());
        // todo replace with hasSize (hamcrest)
        assertThat(initial.cycles().size(), is(4));
        assertThat(initial.cyclesOfLength(3).size(), is(4));
    }

    @Test
    public void numberOfCycles_K4() {
        assertThat(new InitialCycles(k4()).numberOfCycles(), is(4));
    }

    @Test
    public void numberOfEdges_K4() {
        assertThat(new InitialCycles(k4()).numberOfEdges(), is(6));
    }

    @Test
    public void indexOfEdge_K4() {
        InitialCycles initial = new InitialCycles(k4());
        assertThat(initial.indexOfEdge(0, 1), is(0));
        assertThat(initial.indexOfEdge(1, 0), is(0));
        assertThat(initial.indexOfEdge(0, 2), is(1));
        assertThat(initial.indexOfEdge(2, 0), is(1));
        assertThat(initial.indexOfEdge(0, 3), is(2));
        assertThat(initial.indexOfEdge(3, 0), is(2));
        assertThat(initial.indexOfEdge(1, 2), is(3));
        assertThat(initial.indexOfEdge(1, 2), is(3));
        assertThat(initial.indexOfEdge(1, 3), is(4));
        assertThat(initial.indexOfEdge(1, 3), is(4));
        assertThat(initial.indexOfEdge(2, 3), is(5));
        assertThat(initial.indexOfEdge(2, 3), is(5));
    }

    @Test
    public void edge_K4() {
        InitialCycles initial = new InitialCycles(k4());
        assertThat(initial.edge(0), is(new InitialCycles.Edge(0, 1)));
        assertThat(initial.edge(1), is(new InitialCycles.Edge(0, 2)));
        assertThat(initial.edge(2), is(new InitialCycles.Edge(0, 3)));
        assertThat(initial.edge(3), is(new InitialCycles.Edge(1, 2)));
        assertThat(initial.edge(4), is(new InitialCycles.Edge(1, 3)));
        assertThat(initial.edge(5), is(new InitialCycles.Edge(2, 3)));
    }

    @Test
    public void toEdgeVector_K4() {
        InitialCycles initial = new InitialCycles(k4());
        assertThat(initial.toEdgeVector(new int[]{0, 1, 2, 3, 0}), is(BitMatrixTest.toBitSet("101101")));
        assertThat(initial.toEdgeVector(new int[]{0, 1, 2, 0}), is(BitMatrixTest.toBitSet("110100")));
    }

    @Test
    public void lengths_naphthalene() throws IOException {
        assertThat(new InitialCycles(naphthalene()).lengths(), hasItem(6));
    }

    @Test
    public void cycles_naphthalene() throws IOException {
        InitialCycles initial = new InitialCycles(naphthalene());
        List<InitialCycles.Cycle> cycles = Lists.newArrayList(initial.cycles());
        assertThat(cycles.size(), is(2));
        assertThat(cycles.get(0).path(), is(new int[]{5, 0, 1, 2, 3, 4, 5}));
        assertThat(cycles.get(1).path(), is(new int[]{5, 4, 7, 8, 9, 6, 5}));
    }

    @Test
    public void lengths_anthracene() throws IOException {
        assertThat(new InitialCycles(anthracene()).lengths(), hasItem(6));
    }

    @Test
    public void cycles_anthracene() throws IOException {
        InitialCycles initial = new InitialCycles(anthracene());
        List<InitialCycles.Cycle> cycles = Lists.newArrayList(initial.cycles());
        assertThat(cycles.size(), is(3));
        assertThat(cycles.get(0).path(), is(new int[]{5, 0, 1, 2, 3, 4, 5}));
        assertThat(cycles.get(1).path(), is(new int[]{9, 6, 5, 4, 7, 8, 9}));
        assertThat(cycles.get(2).path(), is(new int[]{9, 8, 10, 11, 12, 13, 9}));
    }

    @Test
    public void lengths_bicyclo() throws IOException {
        assertThat(new InitialCycles(bicyclo()).lengths(), hasItem(6));
    }

    @Test
    public void cycles_bicyclo() throws IOException {
        InitialCycles initial = new InitialCycles(bicyclo());
        List<InitialCycles.Cycle> cycles = Lists.newArrayList(initial.cycles());
        assertThat(cycles.size(), is(3));
        assertThat(cycles.get(0).path(), is(new int[]{5, 0, 1, 2, 3, 4, 5}));
        assertThat(cycles.get(1).path(), is(new int[]{5, 0, 1, 2, 7, 6, 5}));
        assertThat(cycles.get(2).path(), is(new int[]{5, 4, 3, 2, 7, 6, 5}));
    }

    @Test
    public void lengths_cyclophane() throws IOException {
        InitialCycles initial = new InitialCycles(cyclophane_odd());
        assertThat(initial.lengths(), hasItems(6, 9));
        Iterator<Integer> it = initial.lengths().iterator();
        assertThat(it.next(), is(6));
        assertThat(it.next(), is(9));
        assertFalse(it.hasNext());
    }

    @Test
    public void cycles_cyclophane() throws IOException {
        InitialCycles initial = new InitialCycles(cyclophane_odd());
        List<InitialCycles.Cycle> cycles = Lists.newArrayList(initial.cycles());
        assertThat(cycles.size(), is(2));
        assertThat(cycles.get(0).path(), is(new int[]{3, 2, 1, 0, 5, 4, 3}));
        assertThat(cycles.get(1).path(), is(new int[]{3, 2, 1, 0, 10, 9, 8, 7, 6, 3}));
    }

    @Test
    public void cycles_cyclophane_odd_limit_5() throws IOException {
        InitialCycles initial = new InitialCycles(cyclophane_odd(), 5);
        List<InitialCycles.Cycle> cycles = Lists.newArrayList(initial.cycles());
        assertThat(cycles.size(), is(0));
    }

    @Test
    public void cycles_cyclophane_odd_limit_6() throws IOException {
        InitialCycles initial = new InitialCycles(cyclophane_odd(), 6);
        List<InitialCycles.Cycle> cycles = Lists.newArrayList(initial.cycles());
        assertThat(cycles.size(), is(1));
        assertThat(cycles.get(0).path(), is(new int[]{3, 2, 1, 0, 5, 4, 3}));
    }

    @Test
    public void cycles_cyclophane_odd_limit_7() throws IOException {
        InitialCycles initial = new InitialCycles(cyclophane_odd(), 7);
        List<InitialCycles.Cycle> cycles = Lists.newArrayList(initial.cycles());
        assertThat(cycles.size(), is(1));
        assertThat(cycles.get(0).path(), is(new int[]{3, 2, 1, 0, 5, 4, 3}));
    }

    @Test
    public void cycles_family_odd() {
        InitialCycles initial = new InitialCycles(cyclophane_odd());
        List<InitialCycles.Cycle> cycles = Lists.newArrayList(initial.cycles());
        assertThat(cycles.get(1).path(), is(new int[]{3, 2, 1, 0, 10, 9, 8, 7, 6, 3}));
        int[][] family = cycles.get(1).family();
        assertThat(family.length, is(2));
        assertThat(family[0], is(new int[]{3, 2, 1, 0, 10, 9, 8, 7, 6, 3}));
        assertThat(family[1], is(new int[]{3, 4, 5, 0, 10, 9, 8, 7, 6, 3}));
    }

    @Test
    public void cycles_family_even() {
        InitialCycles initial = new InitialCycles(cyclophane_even());
        List<InitialCycles.Cycle> cycles = Lists.newArrayList(initial.cycles());
        assertThat(cycles.get(1).path(), is(new int[]{3, 6, 7, 8, 9, 10, 11, 0, 1, 2, 3}));
        int[][] family = cycles.get(1).family();
        assertThat(family.length, is(2));
        assertThat(family[0], is(new int[]{3, 6, 7, 8, 9, 10, 11, 0, 1, 2, 3}));
        assertThat(family[1], is(new int[]{3, 6, 7, 8, 9, 10, 11, 0, 5, 4, 3}));
    }

    // ensure using the biconnected optimisation will still find the cycle in
    // a simple cycle, cylcohexane (there are no vertices with deg 3)
    @Test
    public void bioconnected_simpleCycle() {
        InitialCycles ic = InitialCycles.ofBiconnectedComponent(cyclohexane());
        assertThat(ic.numberOfCycles(), is(1));
    }

    @Test
    public void bioconnected_simpleCycle_limit_5() {
        InitialCycles ic = InitialCycles.ofBiconnectedComponent(cyclohexane(), 5);
        assertThat(ic.numberOfCycles(), is(0));
    }

    @Test
    public void bioconnected_simpleCycle_limit_6() {
        InitialCycles ic = InitialCycles.ofBiconnectedComponent(cyclohexane(), 6);
        assertThat(ic.numberOfCycles(), is(1));
    }

    @Test
    public void join() {
        int[] a = new int[]{0, 1, 2};
        int[] b = new int[]{0, 3, 4};
        assertThat(InitialCycles.join(a, b), is(new int[]{0, 1, 2, 4, 3, 0}));
    }

    @Test
    public void joinWith() {
        int[] a = new int[]{0, 1, 2};
        int[] b = new int[]{0, 3, 4};
        assertThat(InitialCycles.join(a, 5, b), is(new int[]{0, 1, 2, 5, 4, 3, 0}));
    }

    @Test
    public void singleton() {
        int[] a = new int[]{0, 1, 3, 5, 7, 9};
        int[] b = new int[]{0, 2, 4, 6, 8, 10};
        assertTrue(InitialCycles.singletonIntersect(a, b));
    }

    @Test
    public void startOverlap() {
        int[] a = new int[]{0, 1, 2, 3, 4, 6};
        int[] b = new int[]{0, 1, 2, 3, 5, 7};
        assertFalse(InitialCycles.singletonIntersect(a, b));
    }

    @Test
    public void middleOverlap() {
        int[] a = new int[]{0, 1, 3, 5, 6, 7, 9};
        int[] b = new int[]{0, 2, 4, 5, 6, 8, 10};
        assertFalse(InitialCycles.singletonIntersect(a, b));
    }

    @Test
    public void endOverlap() {
        int[] a = new int[]{0, 1, 3, 5, 7, 9, 10};
        int[] b = new int[]{0, 2, 4, 6, 8, 9, 10};
        assertFalse(InitialCycles.singletonIntersect(a, b));
    }

    @Test
    public void edgeIsTransitive() {
        InitialCycles.Edge p = new InitialCycles.Edge(0, 1);
        InitialCycles.Edge q = new InitialCycles.Edge(1, 0);
        assertThat(p.hashCode(), is(q.hashCode()));
        assertThat(p, is(q));
    }

    @Test
    public void edgeToString() {
        InitialCycles.Edge p = new InitialCycles.Edge(0, 1);
        assertThat(p.toString(), is("{0, 1}"));
    }

    static int[][] k1() {
        return new int[1][0];
    }

    /**
     * Simple undirected graph where every pair of of the four vertices is
     * connected. The graph is known as a complete graph and is referred to as
     * K<sub>4</sub>.
     *
     * @return adjacency list of K<sub>4</sub>
     */
    static int[][] k4() {
        return new int[][]{{1, 2, 3}, {0, 2, 3}, {0, 1, 3}, {0, 1, 2}};
    }

    /** benzene/cyclohexane graph */
    static int[][] cyclohexane() {
        return new int[][]{{1, 5}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 0}};
    }

    /** @cdk.inchi InChI=1S/C10H8/c1-2-6-10-8-4-3-7-9(10)5-1/h1-8H */
    static int[][] naphthalene() {
        return new int[][]{{1, 5}, {0, 2}, {1, 3}, {2, 4}, {3, 5, 7}, {0, 6, 4}, {5, 9}, {4, 8}, {7, 9}, {6, 8}};
    }

    /** @cdk.inchi InChI=1S/C14H10/c1-2-6-12-10-14-8-4-3-7-13(14)9-11(12)5-1/h1-10H */
    static int[][] anthracene() {
        return new int[][]{{1, 5}, {0, 2}, {1, 3}, {2, 4}, {3, 5, 7}, {0, 6, 4}, {5, 9}, {4, 8}, {7, 10, 9},
                {6, 8, 13}, {8, 11}, {10, 12}, {11, 13}, {9, 12}};
    }

    /** @cdk.inchi InChI=1S/C8H14/c1-2-8-5-3-7(1)4-6-8/h7-8H,1-6H2 */
    static int[][] bicyclo() {
        return new int[][]{{1, 5}, {0, 2}, {1, 3, 7}, {2, 4}, {3, 5}, {0, 4, 6}, {5, 7}, {2, 6}};
    }

    /** @cdk.inchi InChI=1S/C7H12/c1-2-7-4-3-6(1)5-7/h6-7H,1-5H2 */
    static int[][] norbornane() {
        return new int[][]{{1, 5}, {0, 2}, {1, 3, 6}, {2, 4}, {3, 5}, {0, 4, 6}, {2, 5},};
    }

    /** @cdk.inchi InChI=1S/C11H14/c1-2-4-10-6-8-11(5-3-1)9-7-10/h6-9H,1-5H2 */
    static int[][] cyclophane_odd() {
        return new int[][]{{1, 5, 10}, {0, 2}, {1, 3}, {2, 4, 6}, {3, 5}, {0, 4}, {3, 7}, {6, 8}, {7, 9}, {8, 10},
                {0, 9},};
    }

    /**
     * Same as above but generate an even cycle so we can also test {@link
     * org.openscience.cdk.graph.InitialCycles.Cycle#family()} method.
     */
    static int[][] cyclophane_even() {
        return new int[][]{{1, 5, 11}, {0, 2}, {1, 3}, {2, 4, 6}, {3, 5}, {0, 4}, {3, 7}, {6, 8}, {7, 9}, {8, 10},
                {9, 11}, {0, 10}};
    }

}
