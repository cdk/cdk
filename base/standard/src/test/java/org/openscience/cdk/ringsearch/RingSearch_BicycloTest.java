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
 * Unit tests for ring search. These unit tests ensure bicyclo rings (a bridged
 * system) is found correctly.
 *
 * @author John May
 */
final class RingSearch_BicycloTest {

    private static final IAtomContainer bicyclo = TestMoleculeFactory.makeBicycloRings();

    @Test
    void testCyclic() {
        int n = bicyclo.getAtomCount();
        assertThat("cyclic vertices should be invariant for any ordering", new RingSearch(bicyclo).cyclic().length,
                is(n));
    }

    @Test
    void testCyclic_Int() {
        int n = bicyclo.getAtomCount();

        RingSearch ringSearch = new RingSearch(bicyclo);
        for (int i = 0; i < n; i++)
            Assertions.assertTrue(ringSearch.cyclic(i), "all atoms should be cyclic");

    }

    @Test
    void testIsolated() {
        assertThat("no isolated cycle should be found", new RingSearch(bicyclo).isolated().length, is(0));

    }

    @Test
    void testFused() {
        assertThat("one fused cycle should be found", new RingSearch(bicyclo).fused().length, is(1));

    }

    @Test
    void testRingFragments() {
        int n = bicyclo.getAtomCount();

        IAtomContainer fragment = new RingSearch(bicyclo).ringFragments();
        assertThat(fragment.getAtomCount(), is(bicyclo.getAtomCount()));
        assertThat(fragment.getBondCount(), is(bicyclo.getBondCount()));

    }

    @Test
    void testIsolatedRingFragments() {
        int n = bicyclo.getAtomCount();

        List<IAtomContainer> fragments = new RingSearch(bicyclo).isolatedRingFragments();
        Assertions.assertTrue(fragments.isEmpty());

    }

    @Test
    void testFusedRingFragments() {

        List<IAtomContainer> fragments = new RingSearch(bicyclo).fusedRingFragments();
        assertThat(fragments.size(), is(1));
        IAtomContainer fragment = fragments.get(0);
        assertThat(fragment.getAtomCount(), is(bicyclo.getAtomCount()));
        assertThat(fragment.getBondCount(), is(bicyclo.getBondCount()));

    }

}
