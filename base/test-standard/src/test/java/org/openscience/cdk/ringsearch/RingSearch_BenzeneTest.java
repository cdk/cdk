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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.TestMoleculeFactory;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * ring search unit tests for benzene
 *
 * @author John May
 */
final class RingSearch_BenzeneTest {

    private final IAtomContainer benzene = TestMoleculeFactory.makeBenzene();

    @Test
    void testCyclic() {
        assertThat(new RingSearch(benzene).cyclic().length, is(benzene.getAtomCount()));
    }

    @Test
    void testCyclic_Int() {
        int n = benzene.getAtomCount();
        RingSearch ringSearch = new RingSearch(benzene);
        for (int i = 0; i < n; i++) {
            Assertions.assertTrue(ringSearch.cyclic(i));
        }
    }

    @Test
    void testIsolated() {
        RingSearch search = new RingSearch(benzene);
        int[][] isolated = search.isolated();
        assertThat(isolated.length, is(1));
        assertThat(isolated[0].length, is(6));
    }

    @Test
    void testFused() {
        assertThat(new RingSearch(benzene).fused().length, is(0));
    }

    @Test
    void testRingFragments() {
        IAtomContainer fragment = new RingSearch(benzene).ringFragments();
        assertThat(fragment.getAtomCount(), is(benzene.getAtomCount()));
        assertThat(fragment.getBondCount(), is(benzene.getBondCount()));
    }

    @Test
    void testIsolatedRingFragments() {
        RingSearch search = new RingSearch(benzene);
        List<IAtomContainer> isolated = search.isolatedRingFragments();
        assertThat(isolated.size(), is(1));
        assertThat(isolated.get(0).getAtomCount(), is(6));
        assertThat(isolated.get(0).getBondCount(), is(6));
    }

    @Test
    void testFusedRingFragments() {
        RingSearch search = new RingSearch(benzene);
        List<IAtomContainer> fused = search.fusedRingFragments();
        assertThat(fused.size(), is(0));
    }

}
