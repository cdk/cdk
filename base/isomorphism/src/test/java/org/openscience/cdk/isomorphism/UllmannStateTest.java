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

package org.openscience.cdk.isomorphism;

import org.junit.Test;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.TestMoleculeFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-isomorphism
 */
public class UllmannStateTest {

    @Test
    public void testNextN() throws Exception {
        UllmannState state = createBenzeneToNaphthalene(AtomMatcher.forAny(), BondMatcher.forAny());
        assertThat(state.nextN(0), is(0));
        state.size = 1;
        assertThat(state.nextN(0), is(1));
        state.size = 2;
        assertThat(state.nextN(0), is(2));
    }

    @Test
    public void testNextM() throws Exception {
        UllmannState state = createBenzeneToNaphthalene(AtomMatcher.forAny(), BondMatcher.forAny());
        assertThat(state.nextM(0, -1), is(0));
        assertThat(state.nextM(0, 0), is(1));
        assertThat(state.nextM(0, 1), is(2));
        state.m2[1] = 0; // 1 has been mapped and should be skipped over
        assertThat(state.nextM(0, 0), is(2));
    }

    @Test
    public void add() throws Exception {
        UllmannState state = createBenzeneToNaphthalene(AtomMatcher.forAny(), BondMatcher.forAny());
        assertThat(state.matrix.fix(), is(new int[][]{{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}}));
        assertTrue(state.add(0, 0));
        assertThat(state.matrix.fix(), is(new int[][]{{1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, 1}, {1, -1, 1, -1, -1, -1, -1, -1, 1, -1},
                {-1, 1, -1, 1, -1, -1, -1, 1, -1, 1}, {1, -1, 1, -1, -1, -1, -1, -1, 1, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, 1}}));
        assertTrue(state.add(1, 9));
        assertThat(state.matrix.fix(), is(new int[][]{{1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, -2, -1, -1, -1, -1, -1, -1, -1, 1}, {1, -1, -2, -1, -1, -1, -1, -1, 1, -1},
                {-1, 1, -1, -2, -1, -1, -1, 1, -1, 1}, {1, -1, 1, -1, -1, -1, -1, -1, 1, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, 1}}));
        assertTrue(state.add(2, 8));
        assertThat(state.matrix.fix(), is(new int[][]{{1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, -2, -1, -1, -1, -1, -1, -1, -1, 1}, {-3, -1, -2, -1, -1, -1, -1, -1, 1, -1},
                {-1, -3, -1, -2, -1, -1, -1, 1, -1, 1}, {1, -1, 1, -1, -1, -1, -1, -1, 1, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, 1}}));
        assertTrue(state.add(3, 7));
        assertThat(state.matrix.fix(), is(new int[][]{{1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, -2, -1, -1, -1, -1, -1, -1, -1, 1}, {-3, -1, -2, -1, -1, -1, -1, -1, 1, -1},
                {-1, -3, -1, -2, -1, -1, -1, 1, -1, -4}, {-4, -1, 1, -1, -1, -1, -1, -1, 1, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, 1}}));
        assertTrue(state.add(4, 2));
        assertThat(state.matrix.fix(), is(new int[][]{{1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, -2, -1, -1, -1, -1, -1, -1, -1, 1}, {-3, -1, -2, -1, -1, -1, -1, -1, 1, -1},
                {-1, -3, -1, -2, -1, -1, -1, 1, -1, -4}, {-4, -1, 1, -1, -1, -1, -1, -1, -5, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, -5}}));
        assertTrue(state.add(5, 1));
        assertThat(state.matrix.fix(), is(new int[][]{{1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, -2, -1, -1, -1, -1, -1, -1, -1, 1}, {-3, -1, -2, -1, -1, -1, -1, -1, 1, -1},
                {-1, -3, -1, -2, -1, -1, -1, 1, -1, -4}, {-4, -1, 1, -1, -1, -1, -1, -1, -5, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, -5}}));
    }

    @Test
    public void remove() throws Exception {
        UllmannState state = createBenzeneToNaphthalene(AtomMatcher.forAny(), BondMatcher.forAny());
        assertThat(state.matrix.fix(), is(new int[][]{{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}}));
        assertTrue(state.add(0, 0));
        assertTrue(state.add(1, 9));
        assertTrue(state.add(2, 8));
        assertTrue(state.add(3, 7));
        assertTrue(state.add(4, 2));
        assertTrue(state.add(5, 1));
        assertThat(state.matrix.fix(), is(new int[][]{{1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, -2, -1, -1, -1, -1, -1, -1, -1, 1}, {-3, -1, -2, -1, -1, -1, -1, -1, 1, -1},
                {-1, -3, -1, -2, -1, -1, -1, 1, -1, -4}, {-4, -1, 1, -1, -1, -1, -1, -1, -5, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, -5}}));
        state.remove(5, 1);
        assertThat(state.matrix.fix(), is(new int[][]{{1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, -2, -1, -1, -1, -1, -1, -1, -1, 1}, {-3, -1, -2, -1, -1, -1, -1, -1, 1, -1},
                {-1, -3, -1, -2, -1, -1, -1, 1, -1, -4}, {-4, -1, 1, -1, -1, -1, -1, -1, -5, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, -5}}));
        state.remove(4, 2);
        assertThat(state.matrix.fix(), is(new int[][]{{1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, -2, -1, -1, -1, -1, -1, -1, -1, 1}, {-3, -1, -2, -1, -1, -1, -1, -1, 1, -1},
                {-1, -3, -1, -2, -1, -1, -1, 1, -1, -4}, {-4, -1, 1, -1, -1, -1, -1, -1, 1, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, 1}}));
        state.remove(3, 7);
        assertThat(state.matrix.fix(), is(new int[][]{{1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, -2, -1, -1, -1, -1, -1, -1, -1, 1}, {-3, -1, -2, -1, -1, -1, -1, -1, 1, -1},
                {-1, -3, -1, -2, -1, -1, -1, 1, -1, 1}, {1, -1, 1, -1, -1, -1, -1, -1, 1, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, 1}}));
        state.remove(2, 8);
        assertThat(state.matrix.fix(), is(new int[][]{{1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, -2, -1, -1, -1, -1, -1, -1, -1, 1}, {1, -1, -2, -1, -1, -1, -1, -1, 1, -1},
                {-1, 1, -1, -2, -1, -1, -1, 1, -1, 1}, {1, -1, 1, -1, -1, -1, -1, -1, 1, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, 1}}));
        state.remove(1, 9);
        assertThat(state.matrix.fix(), is(new int[][]{{1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, 1}, {1, -1, 1, -1, -1, -1, -1, -1, 1, -1},
                {-1, 1, -1, 1, -1, -1, -1, 1, -1, 1}, {1, -1, 1, -1, -1, -1, -1, -1, 1, -1},
                {-1, 1, -1, -1, -1, -1, -1, -1, -1, 1}}));
        state.remove(0, 0);
        assertThat(state.matrix.fix(), is(new int[][]{{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}}));
    }

    @Test
    public void mapping() throws Exception {
        UllmannState state = createBenzeneToNaphthalene(AtomMatcher.forAny(), BondMatcher.forAny());
        state.m1[0] = 1;
        state.m1[1] = 2;
        assertThat(state.mapping(), is(state.m1));
        assertThat(state.mapping(), is(not(sameInstance(state.m1))));
    }

    @Test
    public void accessors() throws Exception {
        UllmannState state = createBenzeneToNaphthalene(AtomMatcher.forAny(), BondMatcher.forAny());
        state.size = 1;
        assertThat(state.size(), is(1));
        assertThat(state.nMax(), is(state.g1.length));
        assertThat(state.mMax(), is(state.g2.length));
    }

    /**
     * Create a state for matching benzene to naphthalene Benzene:
     * InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H Naphthalene: InChI=1/C10H8/c1-2-6-10-8-4-3-7-9(10)5-1/h1-8H
     */
    UllmannState createBenzeneToNaphthalene(AtomMatcher atomMatcher, BondMatcher bondMatcher) throws Exception {
        IAtomContainer container1 = TestMoleculeFactory.makeBenzene();
        IAtomContainer container2 = TestMoleculeFactory.makeNaphthalene();
        GraphUtil.EdgeToBondMap bonds1 = GraphUtil.EdgeToBondMap.withSpaceFor(container1);
        GraphUtil.EdgeToBondMap bonds2 = GraphUtil.EdgeToBondMap.withSpaceFor(container2);
        int[][] g1 = GraphUtil.toAdjList(container1, bonds1);
        int[][] g2 = GraphUtil.toAdjList(container2, bonds2);
        return new UllmannState(container1, container2, g1, g2, bonds1, bonds2, atomMatcher, bondMatcher);
    }
}
