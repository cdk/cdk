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

import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author John May
 * @cdk.module test-isomorphism
 */
public class StateStreamTest {

    @Test
    public void hasNext() throws Exception {
        VFSubState state = createNaphthaleneToBenzene(AtomMatcher.forAny(), BondMatcher.forAny());
        Iterator<int[]> it = new StateStream(state);
        assertFalse(it.hasNext());
    }

    @Test
    public void hasNext2() throws Exception {
        VFSubState state = createBenzeneToNaphthalene(AtomMatcher.forAny(), BondMatcher.forAny());
        int cnt = 0;
        Iterator<int[]> it = new StateStream(state);
        while (it.hasNext()) {
            assertNotNull(it.next());
            cnt++;
        }
        assertThat(cnt, is(24));
    }

    @Test
    public void next() throws Exception {
        VFSubState state = createBenzeneToNaphthalene(AtomMatcher.forAny(), BondMatcher.forAny());
        Iterator<int[]> it = new StateStream(state);
        assertThat(it.next(), is(new int[]{0, 1, 2, 7, 8, 9}));
        assertThat(it.next(), is(new int[]{0, 9, 8, 7, 2, 1}));
        assertThat(it.next(), is(new int[]{1, 0, 9, 8, 7, 2}));
        assertThat(it.next(), is(new int[]{1, 2, 7, 8, 9, 0}));
        assertThat(it.next(), is(new int[]{2, 1, 0, 9, 8, 7}));
        assertThat(it.next(), is(new int[]{2, 3, 4, 5, 6, 7}));
        assertThat(it.next(), is(new int[]{2, 7, 6, 5, 4, 3}));
        assertThat(it.next(), is(new int[]{2, 7, 8, 9, 0, 1}));
        assertThat(it.next(), is(new int[]{3, 2, 7, 6, 5, 4}));
        assertThat(it.next(), is(new int[]{3, 4, 5, 6, 7, 2}));
        assertThat(it.next(), is(new int[]{4, 3, 2, 7, 6, 5}));
        assertThat(it.next(), is(new int[]{4, 5, 6, 7, 2, 3}));
        assertThat(it.next(), is(new int[]{5, 4, 3, 2, 7, 6}));
        assertThat(it.next(), is(new int[]{5, 6, 7, 2, 3, 4}));
        assertThat(it.next(), is(new int[]{6, 5, 4, 3, 2, 7}));
        assertThat(it.next(), is(new int[]{6, 7, 2, 3, 4, 5}));
        assertThat(it.next(), is(new int[]{7, 2, 1, 0, 9, 8}));
        assertThat(it.next(), is(new int[]{7, 2, 3, 4, 5, 6}));
        assertThat(it.next(), is(new int[]{7, 6, 5, 4, 3, 2}));
        assertThat(it.next(), is(new int[]{7, 8, 9, 0, 1, 2}));
        assertThat(it.next(), is(new int[]{8, 7, 2, 1, 0, 9}));
        assertThat(it.next(), is(new int[]{8, 9, 0, 1, 2, 7}));
        assertThat(it.next(), is(new int[]{9, 0, 1, 2, 7, 8}));
        assertThat(it.next(), is(new int[]{9, 8, 7, 2, 1, 0}));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void remove() throws Exception {
        VFSubState state = createBenzeneToNaphthalene(AtomMatcher.forAny(), BondMatcher.forAny());
        Iterator<int[]> it = new StateStream(state);
        it.remove();
    }

    /**
     * Create a sub state for matching benzene to naphthalene
     *
     *
     * Benzene: InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H
     *
     * Naphthalene: InChI=1/C10H8/c1-2-6-10-8-4-3-7-9(10)5-1/h1-8H
     */
    VFSubState createBenzeneToNaphthalene(AtomMatcher atomMatcher, BondMatcher bondMatcher) throws Exception {
        IAtomContainer container1 = TestMoleculeFactory.makeBenzene();
        IAtomContainer container2 = TestMoleculeFactory.makeNaphthalene();
        GraphUtil.EdgeToBondMap bonds1 = GraphUtil.EdgeToBondMap.withSpaceFor(container1);
        GraphUtil.EdgeToBondMap bonds2 = GraphUtil.EdgeToBondMap.withSpaceFor(container2);
        int[][] g1 = GraphUtil.toAdjList(container1, bonds1);
        int[][] g2 = GraphUtil.toAdjList(container2, bonds2);
        return new VFSubState(container1, container2, g1, g2, bonds1, bonds2, atomMatcher, bondMatcher);
    }

    /**
     * Create a sub state for matching naphthalene to benzene
     *
     * Benzene: InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H
     *
     * Naphthalene: InChI=1/C10H8/c1-2-6-10-8-4-3-7-9(10)5-1/h1-8H
     */
    VFSubState createNaphthaleneToBenzene(AtomMatcher atomMatcher, BondMatcher bondMatcher) throws Exception {
        IAtomContainer container1 = TestMoleculeFactory.makeNaphthalene();
        IAtomContainer container2 = TestMoleculeFactory.makeBenzene();
        GraphUtil.EdgeToBondMap bonds1 = GraphUtil.EdgeToBondMap.withSpaceFor(container1);
        GraphUtil.EdgeToBondMap bonds2 = GraphUtil.EdgeToBondMap.withSpaceFor(container2);
        int[][] g1 = GraphUtil.toAdjList(container1, bonds1);
        int[][] g2 = GraphUtil.toAdjList(container2, bonds2);
        return new VFSubState(container1, container2, g1, g2, bonds1, bonds2, atomMatcher, bondMatcher);
    }
}
