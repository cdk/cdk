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
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * benzylbenzene ring search unit tests
 *
 * @author John May
 * @cdk.module test-standard
 * */
public final class RingSearchTest_BenzylBenzene {

    private final IAtomContainer benzylbenzene = benzylbenzene();

    @Test
    public void testCyclic() {
        assertThat(new RingSearch(benzylbenzene).cyclic().length, is(benzylbenzene.getAtomCount() - 1));
    }

    @Test
    public void testCyclic_Int() {
        int n = benzylbenzene.getAtomCount();
        RingSearch ringSearch = new RingSearch(benzylbenzene);

        int cyclic = 0, acyclic = 0;
        for (int i = 0; i < n; i++) {
            if (ringSearch.cyclic(i))
                cyclic++;
            else
                acyclic++;
        }

        // single atom not in a ring
        assertThat(acyclic, is(1));
        assertThat(cyclic, is(n - 1));

    }

    @Test
    public void testIsolated() {
        RingSearch search = new RingSearch(benzylbenzene);
        int[][] isolated = search.isolated();
        assertThat(isolated.length, is(2));
        assertThat(isolated[0].length, is(6));
        assertThat(isolated[1].length, is(6));
    }

    @Test
    public void testFused() {
        assertThat(new RingSearch(benzylbenzene).fused().length, is(0));
    }

    @Test
    public void testRingFragments() {
        IAtomContainer fragment = new RingSearch(benzylbenzene).ringFragments();
        assertThat(fragment.getAtomCount(), is(benzylbenzene.getAtomCount() - 1));
        assertThat(fragment.getBondCount(), is(benzylbenzene.getBondCount() - 2));
    }

    @Test
    public void testIsolatedRingFragments() {
        RingSearch search = new RingSearch(benzylbenzene);
        List<IAtomContainer> isolated = search.isolatedRingFragments();
        assertThat(isolated.size(), is(2));
        assertThat(isolated.get(0).getAtomCount(), is(6));
        assertThat(isolated.get(0).getBondCount(), is(6));
        assertThat(isolated.get(1).getAtomCount(), is(6));
        assertThat(isolated.get(1).getBondCount(), is(6));
    }

    @Test
    public void testFusedRingFragments() {
        RingSearch search = new RingSearch(benzylbenzene);
        List<IAtomContainer> fused = search.fusedRingFragments();
        assertThat(fused.size(), is(0));
    }

    /**
     * @cdk.inchi InChI=1S/C13H12/c1-3-7-12(8-4-1)11-13-9-5-2-6-10-13/h1-10H,11H2
     */
    public static IAtomContainer benzylbenzene() {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = new Atom("C");
        mol.addAtom(a1);
        IAtom a2 = new Atom("C");
        mol.addAtom(a2);
        IAtom a3 = new Atom("C");
        mol.addAtom(a3);
        IAtom a4 = new Atom("C");
        mol.addAtom(a4);
        IAtom a5 = new Atom("C");
        mol.addAtom(a5);
        IAtom a6 = new Atom("C");
        mol.addAtom(a6);
        IAtom a7 = new Atom("C");
        mol.addAtom(a7);
        IAtom a8 = new Atom("C");
        mol.addAtom(a8);
        IAtom a9 = new Atom("C");
        mol.addAtom(a9);
        IAtom a10 = new Atom("C");
        mol.addAtom(a10);
        IAtom a11 = new Atom("C");
        mol.addAtom(a11);
        IAtom a12 = new Atom("C");
        mol.addAtom(a12);
        IAtom a13 = new Atom("C");
        mol.addAtom(a13);
        IBond b1 = new Bond(a6, a7, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = new Bond(a7, a8, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = new Bond(a6, a5, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = new Bond(a5, a4, IBond.Order.DOUBLE);
        mol.addBond(b4);
        IBond b5 = new Bond(a4, a3, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = new Bond(a3, a2, IBond.Order.DOUBLE);
        mol.addBond(b6);
        IBond b7 = new Bond(a6, a1, IBond.Order.DOUBLE);
        mol.addBond(b7);
        IBond b8 = new Bond(a2, a1, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = new Bond(a10, a11, IBond.Order.DOUBLE);
        mol.addBond(b9);
        IBond b10 = new Bond(a10, a9, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = new Bond(a9, a8, IBond.Order.DOUBLE);
        mol.addBond(b11);
        IBond b12 = new Bond(a8, a13, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = new Bond(a13, a12, IBond.Order.DOUBLE);
        mol.addBond(b13);
        IBond b14 = new Bond(a12, a11, IBond.Order.SINGLE);
        mol.addBond(b14);
        return mol;
    }

}
