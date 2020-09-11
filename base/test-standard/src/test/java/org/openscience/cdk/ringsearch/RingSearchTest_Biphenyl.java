/*
 * Copyright (C) 2012 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version. All we ask is that proper credit is given for our
 * work, which includes - but is not limited to - adding the above copyright
 * notice to the beginning of your source code files, and to any copyright
 * notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.ringsearch;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.TestMoleculeFactory;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * biphenyl ring search unit tests
 *
 * @author John May
 * @cdk.module test-standard
 */
public final class RingSearchTest_Biphenyl {

    private final IAtomContainer biphenyl = TestMoleculeFactory.makeBiphenyl();

    @Test
    public void testCyclic() {
        assertThat(new RingSearch(biphenyl).cyclic().length, is(biphenyl.getAtomCount()));
    }

    @Test
    public void testCyclic_Int() {
        int n = biphenyl.getAtomCount();
        RingSearch ringSearch = new RingSearch(biphenyl);
        for (int i = 0; i < n; i++) {
            assertTrue(ringSearch.cyclic(i));
        }
    }

    @Test
    public void testIsolated() {
        RingSearch search = new RingSearch(biphenyl);
        int[][] isolated = search.isolated();
        assertThat(isolated.length, is(2));
        assertThat(isolated[0].length, is(6));
        assertThat(isolated[1].length, is(6));
    }

    @Test
    public void testFused() {
        assertThat(new RingSearch(biphenyl).fused().length, is(0));
    }

    @Test
    public void testRingFragments() {
        IAtomContainer fragment = new RingSearch(biphenyl).ringFragments();
        assertThat(fragment.getAtomCount(), is(biphenyl.getAtomCount()));
        assertThat(fragment.getBondCount(), is(biphenyl.getBondCount() - 1));
    }

    @Test
    public void testIsolatedRingFragments() {
        RingSearch search = new RingSearch(biphenyl);
        List<IAtomContainer> isolated = search.isolatedRingFragments();
        assertThat(isolated.size(), is(2));
        assertThat(isolated.get(0).getAtomCount(), is(6));
        assertThat(isolated.get(0).getBondCount(), is(6));
        assertThat(isolated.get(1).getAtomCount(), is(6));
        assertThat(isolated.get(1).getBondCount(), is(6));
    }

    @Test
    public void testFusedRingFragments() {
        RingSearch search = new RingSearch(biphenyl);
        List<IAtomContainer> fused = search.fusedRingFragments();
        assertThat(fused.size(), is(0));
    }

}
