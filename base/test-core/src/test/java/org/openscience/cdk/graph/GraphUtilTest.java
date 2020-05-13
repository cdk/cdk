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
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import org.junit.Test;

/**
 * @author John May
 * @cdk.module test-core
 */
public class GraphUtilTest {

    @Test
    public void sequentialSubgraph() throws Exception {
        int[][] graph = new int[][]{{1, 2}, {0, 2}, {0, 1}};
        int[][] subgraph = GraphUtil.subgraph(graph, new int[]{0, 1});
        int[][] expected = new int[][]{{1}, {0}};
        assertThat(subgraph, is(expected));
    }

    @Test
    public void intermittentSubgraph() throws Exception {
        int[][] graph = new int[][]{{1, 2}, {0, 2, 3}, {0, 1}, {1}};
        int[][] subgraph = GraphUtil.subgraph(graph, new int[]{0, 2, 3});
        int[][] expected = new int[][]{{1}, {0}, {}};
        assertThat(subgraph, is(expected));
    }

    @Test
    public void resizeSubgraph() throws Exception {
        int[][] graph = new int[][]{{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14}, {0}, {0}, {0}, {0}, {0}, {0}, {0},
                {0}, {0}, {0}, {0}, {0}, {0}, {0}};
        int[][] subgraph = GraphUtil.subgraph(graph, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        int[][] expected = new int[][]{{1, 2, 3, 4, 5, 6, 7, 8, 9}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0},};
        assertThat(subgraph, is(expected));
    }

    @Test
    public void testCycle() {
        // 0-1-2-3-4-5-
        int[][] g = new int[][]{{1, 5}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 0}};
        int[] path = GraphUtil.cycle(g, new int[]{0, 3, 4, 1, 5, 2});
        assertThat(path, is(new int[]{0, 1, 2, 3, 4, 5, 0}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAcyclic() {
        // 0-1-2-3-4-5 (5 and 0 not connected)
        int[][] g = new int[][]{{1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4}};
        GraphUtil.cycle(g, new int[]{0, 3, 4, 1, 5, 2});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAcyclic2() {
        // 0-1-2 3-4-5- (2 and 3) not connected
        int[][] g = new int[][]{{1}, {0, 2}, {1}, {4}, {3, 5}, {4}};
        GraphUtil.cycle(g, new int[]{0, 3, 4, 1, 5, 2});
    }

    @Test
    public void firstMarked() {
        assertThat(GraphUtil.firstMarked(new int[]{0, 1, 2}, new boolean[]{false, true, false}), is(1));
        assertThat(GraphUtil.firstMarked(new int[]{2, 1, 0}, new boolean[]{true, false, false}), is(0));
        assertThat(GraphUtil.firstMarked(new int[]{2, 1, 0}, new boolean[]{false, false, false}), is(-1));
    }

    @Test
    public void testToAdjList() throws Exception {

        IAtomContainer container = simple();

        int[][] adjacent = GraphUtil.toAdjList(container);

        assertThat("adjacency list should have 5 vertices", adjacent.length, is(5));

        assertThat("vertex 'a' should have degree 1", adjacent[0].length, is(1));
        assertThat("vertex 'b' should have degree 3", adjacent[1].length, is(3));
        assertThat("vertex 'c' should have degree 2", adjacent[2].length, is(2));
        assertThat("vertex 'd' should have degree 1", adjacent[3].length, is(1));
        assertThat("vertex 'e' should have degree 1", adjacent[4].length, is(1));

        assertArrayEquals(new int[]{1}, adjacent[0]);
        assertArrayEquals(new int[]{0, 2, 4}, adjacent[1]);
        assertArrayEquals(new int[]{1, 3}, adjacent[2]);
        assertArrayEquals(new int[]{2}, adjacent[3]);
        assertArrayEquals(new int[]{1}, adjacent[4]);

    }

    @Test
    public void testToAdjList_withMap() throws Exception {

        IAtomContainer container = simple();

        GraphUtil.EdgeToBondMap map = GraphUtil.EdgeToBondMap.withSpaceFor(container);
        int[][] adjacent = GraphUtil.toAdjList(container, map);

        assertThat("adjacency list should have 5 vertices", adjacent.length, is(5));

        assertThat("vertex 'a' should have degree 1", adjacent[0].length, is(1));
        assertThat("vertex 'b' should have degree 3", adjacent[1].length, is(3));
        assertThat("vertex 'c' should have degree 2", adjacent[2].length, is(2));
        assertThat("vertex 'd' should have degree 1", adjacent[3].length, is(1));
        assertThat("vertex 'e' should have degree 1", adjacent[4].length, is(1));

        assertArrayEquals(new int[]{1}, adjacent[0]);
        assertArrayEquals(new int[]{0, 2, 4}, adjacent[1]);
        assertArrayEquals(new int[]{1, 3}, adjacent[2]);
        assertArrayEquals(new int[]{2}, adjacent[3]);
        assertArrayEquals(new int[]{1}, adjacent[4]);

        assertNotNull(map.get(0, 1));
        assertNotNull(map.get(1, 2));

        assertThat(map.get(0, 1), is(sameInstance(map.get(1, 0))));
        assertThat(map.get(1, 2), is(sameInstance(map.get(2, 1))));
    }

    @Test
    public void testToAdjList_resize() throws Exception {

        IAtomContainer container = new AtomContainer();

        IAtom a = new Atom("C");
        container.addAtom(a);

        // add 50 neighbour to 'a'
        for (int i = 0; i < 50; i++) {
            IAtom neighbour = new Atom("C");
            IBond bond = new Bond(a, neighbour);

            container.addAtom(neighbour);
            container.addBond(bond);
        }

        int[][] adjacent = GraphUtil.toAdjList(container);

        assertThat("vertex 'a' should have degree 50", adjacent[0].length, is(50));

        for (int i = 1; i < 51; i++) {
            assertThat("connected vertex should have degree of 1", adjacent[i].length, is(1));
        }

        // check adjacent neighbours are not empty
        for (int i = 0; i < adjacent[0].length; i++) {
            assertThat(adjacent[0][i], is(i + 1));
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testToAdjList_missingAtom() throws Exception {

        IAtomContainer container = simple();

        container.removeAtomOnly(4); // remove 'e'

        GraphUtil.toAdjList(container);

    }

    @Test
    public void testToAdjList_Empty() throws Exception {
        int[][] adjacent = GraphUtil.toAdjList(new AtomContainer());
        assertThat(adjacent.length, is(0));
    }

    @Test(expected = NullPointerException.class)
    public void testToAdjList_Null() throws Exception {
        GraphUtil.toAdjList(null);
    }

    /**
     * 2,2-dimethylpropane
     * @cdk.inchi InChI=1S/C5H12/c1-5(2,3)4/h1-4H3
     */
    private static IAtomContainer simple() {

        IAtomContainer container = new AtomContainer();

        IAtom a = new Atom("C");
        IAtom b = new Atom("C");
        IAtom c = new Atom("C");
        IAtom d = new Atom("C");
        IAtom e = new Atom("C");

        IBond ab = new Bond(a, b);
        IBond bc = new Bond(b, c);
        IBond cd = new Bond(c, d);
        IBond be = new Bond(b, e);

        container.addAtom(a);
        container.addAtom(b);
        container.addAtom(c);
        container.addAtom(d);
        container.addAtom(e);

        container.addBond(ab);
        container.addBond(bc);
        container.addBond(cd);
        container.addBond(be);

        return container;

    }

}
