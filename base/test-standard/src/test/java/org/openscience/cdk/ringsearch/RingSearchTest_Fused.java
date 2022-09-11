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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.templates.TestMoleculeFactory;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * ring search unit tests for a fused system
 *
 * @author John May
 * @cdk.module test-standard
 */
public class RingSearchTest_Fused {

    private final IAtomContainer fusedRings = TestMoleculeFactory.makeFusedRings();

    @Test
    public void testCyclic_Int() {
        RingSearch ringSearch = new RingSearch(fusedRings);
        for (int i = 0; i < fusedRings.getAtomCount(); i++)
            Assertions.assertTrue(ringSearch.cyclic(i));
    }

    @Test
    public void testCyclic() {
        RingSearch ringSearch = new RingSearch(fusedRings);
        assertThat(ringSearch.cyclic().length, is(fusedRings.getAtomCount()));
    }

    @Test
    public void testFused() {
        RingSearch ringSearch = new RingSearch(fusedRings);
        assertThat(ringSearch.fused().length, is(1));
    }

    @Test
    public void testIsolated() {
        RingSearch ringSearch = new RingSearch(fusedRings);
        assertThat(ringSearch.isolated().length, is(0));
    }

    @Test
    public void testRingFragments() {
        RingSearch ringSearch = new RingSearch(fusedRings);
        IAtomContainer fragment = ringSearch.ringFragments();
        for (IAtom atom : fusedRings.atoms()) {
            Assertions.assertTrue(fragment.contains(atom));
        }
        for (IBond bond : fusedRings.bonds()) {
            Assertions.assertTrue(fragment.contains(bond));
        }
    }

    @Test
    public void testFusedRingFragments() {
        RingSearch ringSearch = new RingSearch(fusedRings);
        List<IAtomContainer> fragments = ringSearch.fusedRingFragments();
        assertThat(fragments.size(), is(1));
        IAtomContainer fragment = fragments.get(0);
        for (IAtom atom : fusedRings.atoms()) {
            Assertions.assertTrue(fragment.contains(atom));
        }
        for (IBond bond : fusedRings.bonds()) {
            Assertions.assertTrue(fragment.contains(bond));
        }
    }

    @Test
    public void testIsolatedRingFragments() {
        RingSearch ringSearch = new RingSearch(fusedRings);
        List<IAtomContainer> fragments = ringSearch.isolatedRingFragments();
        assertThat(fragments.size(), is(0));
    }

}
