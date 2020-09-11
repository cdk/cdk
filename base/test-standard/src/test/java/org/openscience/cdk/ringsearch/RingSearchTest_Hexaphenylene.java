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
import static org.junit.Assert.assertTrue;

/**
 * ring search unit tests for a hexaphenylene (ChEBI:33157)
 *
 * @author John May
 * @cdk.module test-standard
 */
public final class RingSearchTest_Hexaphenylene {

    private final IAtomContainer hexaphenylene = hexaphenylene();

    @Test
    public void testCyclic() {
        assertThat(new RingSearch(hexaphenylene).cyclic().length, is(hexaphenylene.getAtomCount()));
    }

    @Test
    public void testCyclic_Int() {
        int n = hexaphenylene.getAtomCount();
        RingSearch ringSearch = new RingSearch(hexaphenylene);
        for (int i = 0; i < n; i++) {
            assertTrue(ringSearch.cyclic(i));
        }
    }

    @Test
    public void testIsolated() {
        RingSearch search = new RingSearch(hexaphenylene);
        int[][] isolated = search.isolated();
        assertThat(isolated.length, is(0));
    }

    @Test
    public void testFused() {
        int[][] fused = new RingSearch(hexaphenylene).fused();
        assertThat(fused.length, is(1));
        assertThat(fused[0].length, is(hexaphenylene.getAtomCount()));
    }

    @Test
    public void testRingFragments() {
        IAtomContainer fragment = new RingSearch(hexaphenylene).ringFragments();
        assertThat(fragment.getAtomCount(), is(hexaphenylene.getAtomCount()));
        assertThat(fragment.getBondCount(), is(hexaphenylene.getBondCount()));
    }

    @Test
    public void testIsolatedRingFragments() {
        RingSearch search = new RingSearch(hexaphenylene);
        List<IAtomContainer> isolated = search.isolatedRingFragments();
        assertThat(isolated.size(), is(0));
    }

    @Test
    public void testFusedRingFragments() {
        RingSearch search = new RingSearch(hexaphenylene);
        List<IAtomContainer> fused = search.fusedRingFragments();
        assertThat(fused.size(), is(1));
        assertThat(fused.get(0).getAtomCount(), is(hexaphenylene.getAtomCount()));
        assertThat(fused.get(0).getBondCount(), is(hexaphenylene.getBondCount()));
    }

    /**
     * @cdk.inchi InChI=1S/C36H24/c1-2-14-26-25(13-1)27-15-3-4-17-29(27)31-19-7-8-21-33(31)35-23-11-12-24-36(35)34-22-10-9-20-32(34)30-18-6-5-16-28(26)30/h1-24H/b27-25-,28-26-,31-29-,32-30-,35-33-,36-34-
     */
    public static IAtomContainer hexaphenylene() {
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
        IAtom a14 = new Atom("C");
        mol.addAtom(a14);
        IAtom a15 = new Atom("C");
        mol.addAtom(a15);
        IAtom a16 = new Atom("C");
        mol.addAtom(a16);
        IAtom a17 = new Atom("C");
        mol.addAtom(a17);
        IAtom a18 = new Atom("C");
        mol.addAtom(a18);
        IAtom a19 = new Atom("C");
        mol.addAtom(a19);
        IAtom a20 = new Atom("C");
        mol.addAtom(a20);
        IAtom a21 = new Atom("C");
        mol.addAtom(a21);
        IAtom a22 = new Atom("C");
        mol.addAtom(a22);
        IAtom a23 = new Atom("C");
        mol.addAtom(a23);
        IAtom a24 = new Atom("C");
        mol.addAtom(a24);
        IAtom a25 = new Atom("C");
        mol.addAtom(a25);
        IAtom a26 = new Atom("C");
        mol.addAtom(a26);
        IAtom a27 = new Atom("C");
        mol.addAtom(a27);
        IAtom a28 = new Atom("C");
        mol.addAtom(a28);
        IAtom a29 = new Atom("C");
        mol.addAtom(a29);
        IAtom a30 = new Atom("C");
        mol.addAtom(a30);
        IAtom a31 = new Atom("C");
        mol.addAtom(a31);
        IAtom a32 = new Atom("C");
        mol.addAtom(a32);
        IAtom a33 = new Atom("C");
        mol.addAtom(a33);
        IAtom a34 = new Atom("C");
        mol.addAtom(a34);
        IAtom a35 = new Atom("C");
        mol.addAtom(a35);
        IAtom a36 = new Atom("C");
        mol.addAtom(a36);
        IBond b1 = new Bond(a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = new Bond(a1, a6, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = new Bond(a2, a3, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = new Bond(a3, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = new Bond(a4, a5, IBond.Order.DOUBLE);
        mol.addBond(b5);
        IBond b6 = new Bond(a4, a36, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = new Bond(a5, a6, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = new Bond(a5, a7, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = new Bond(a7, a8, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = new Bond(a7, a12, IBond.Order.DOUBLE);
        mol.addBond(b10);
        IBond b11 = new Bond(a8, a9, IBond.Order.DOUBLE);
        mol.addBond(b11);
        IBond b12 = new Bond(a9, a10, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = new Bond(a10, a11, IBond.Order.DOUBLE);
        mol.addBond(b13);
        IBond b14 = new Bond(a11, a12, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = new Bond(a12, a13, IBond.Order.SINGLE);
        mol.addBond(b15);
        IBond b16 = new Bond(a13, a14, IBond.Order.SINGLE);
        mol.addBond(b16);
        IBond b17 = new Bond(a13, a18, IBond.Order.DOUBLE);
        mol.addBond(b17);
        IBond b18 = new Bond(a14, a15, IBond.Order.DOUBLE);
        mol.addBond(b18);
        IBond b19 = new Bond(a15, a16, IBond.Order.SINGLE);
        mol.addBond(b19);
        IBond b20 = new Bond(a16, a17, IBond.Order.DOUBLE);
        mol.addBond(b20);
        IBond b21 = new Bond(a17, a18, IBond.Order.SINGLE);
        mol.addBond(b21);
        IBond b22 = new Bond(a18, a19, IBond.Order.SINGLE);
        mol.addBond(b22);
        IBond b23 = new Bond(a19, a20, IBond.Order.SINGLE);
        mol.addBond(b23);
        IBond b24 = new Bond(a19, a24, IBond.Order.DOUBLE);
        mol.addBond(b24);
        IBond b25 = new Bond(a20, a21, IBond.Order.DOUBLE);
        mol.addBond(b25);
        IBond b26 = new Bond(a21, a22, IBond.Order.SINGLE);
        mol.addBond(b26);
        IBond b27 = new Bond(a22, a23, IBond.Order.DOUBLE);
        mol.addBond(b27);
        IBond b28 = new Bond(a23, a24, IBond.Order.SINGLE);
        mol.addBond(b28);
        IBond b29 = new Bond(a24, a25, IBond.Order.SINGLE);
        mol.addBond(b29);
        IBond b30 = new Bond(a25, a26, IBond.Order.SINGLE);
        mol.addBond(b30);
        IBond b31 = new Bond(a25, a30, IBond.Order.DOUBLE);
        mol.addBond(b31);
        IBond b32 = new Bond(a26, a27, IBond.Order.DOUBLE);
        mol.addBond(b32);
        IBond b33 = new Bond(a27, a28, IBond.Order.SINGLE);
        mol.addBond(b33);
        IBond b34 = new Bond(a28, a29, IBond.Order.DOUBLE);
        mol.addBond(b34);
        IBond b35 = new Bond(a29, a30, IBond.Order.SINGLE);
        mol.addBond(b35);
        IBond b36 = new Bond(a30, a31, IBond.Order.SINGLE);
        mol.addBond(b36);
        IBond b37 = new Bond(a31, a32, IBond.Order.SINGLE);
        mol.addBond(b37);
        IBond b38 = new Bond(a31, a36, IBond.Order.DOUBLE);
        mol.addBond(b38);
        IBond b39 = new Bond(a32, a33, IBond.Order.DOUBLE);
        mol.addBond(b39);
        IBond b40 = new Bond(a33, a34, IBond.Order.SINGLE);
        mol.addBond(b40);
        IBond b41 = new Bond(a34, a35, IBond.Order.DOUBLE);
        mol.addBond(b41);
        IBond b42 = new Bond(a35, a36, IBond.Order.SINGLE);
        mol.addBond(b42);
        return mol;
    }

}
