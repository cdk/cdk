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
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Mocking for {@link RingSearch}. Please refer to RingSearchTest_* for
 * situation unit tests.
 *
 * @author John May
 * @cdk.module test-core
 */
public class RingSearchTest {

    @Test(expected = NullPointerException.class)
    public void testNull() {
        new RingSearch(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullContainer() {
        new RingSearch(null, mock(CyclicVertexSearch.class));
    }

    @Test(expected = NullPointerException.class)
    public void testNullCyclicSearch() {
        new RingSearch(mock(IAtomContainer.class), (CyclicVertexSearch) null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullGraph() {
        new RingSearch(mock(IAtomContainer.class), (int[][]) null);
    }

    @Test
    public void testMatch() {
        assertTrue(RingSearch.match(0, 0));
        assertTrue(RingSearch.match(0, 1));
        assertTrue(RingSearch.match(1, 0));
        assertTrue(RingSearch.match(5, 0));
        assertTrue(RingSearch.match(0, 5));
        assertTrue(RingSearch.match(5, 5));

        assertFalse(RingSearch.match(-1, -1));
        assertFalse(RingSearch.match(6, 5));
        assertFalse(RingSearch.match(5, 6));
        assertFalse(RingSearch.match(-1, 5));
        assertFalse(RingSearch.match(5, -1));
        assertFalse(RingSearch.match(-1, 0));
        assertFalse(RingSearch.match(0, -1));
    }

    @Test
    public void testCyclic() throws Exception {

        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);

        RingSearch ringSearch = new RingSearch(container, cyclicSearch);
        ringSearch.cyclic();

        verify(cyclicSearch, times(1)).cyclic();
    }

    @Test
    public void testCyclic_Int() throws Exception {

        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);

        RingSearch ringSearch = new RingSearch(container, cyclicSearch);
        ringSearch.cyclic(1);

        verify(cyclicSearch, times(1)).cyclic(1);
    }

    @Test
    public void testCyclic_IntInt() throws Exception {

        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);

        RingSearch ringSearch = new RingSearch(container, cyclicSearch);
        ringSearch.cyclic(2, 4);

        verify(cyclicSearch, times(1)).cyclic(2, 4);
    }

    @Test
    public void testCyclic_Atom() throws Exception {

        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);

        when(container.indexOf(any(IAtom.class))).thenReturn(42);

        RingSearch ringSearch = new RingSearch(container, cyclicSearch);
        ringSearch.cyclic(atom);

        // verify the number returned from getAtomNumber is passed on
        verify(container, times(1)).indexOf(atom);
        verify(cyclicSearch, times(1)).cyclic(42);
    }

    @Test
    public void testCyclic_Bond() throws Exception {

        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom a1 = mock(IAtom.class);
        IAtom a2 = mock(IAtom.class);
        IBond bond = mock(IBond.class);

        when(container.indexOf(a1)).thenReturn(42);
        when(container.indexOf(a2)).thenReturn(43);
        when(bond.getBegin()).thenReturn(a1);
        when(bond.getEnd()).thenReturn(a2);

        RingSearch ringSearch = new RingSearch(container, cyclicSearch);
        ringSearch.cyclic(bond);

        // verify the number returned from getAtomNumber is passed on
        verify(container, times(1)).indexOf(a1);
        verify(container, times(1)).indexOf(a2);
        verify(cyclicSearch, times(1)).cyclic(42, 43);
    }

    @Test(expected = NoSuchAtomException.class)
    public void testCyclic_Atom_NotFound() throws Exception {

        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);

        when(container.indexOf(any(IAtom.class))).thenReturn(-1);

        RingSearch ringSearch = new RingSearch(container, cyclicSearch);

        ringSearch.cyclic(atom);
    }

    @Test
    public void testIsolated() throws Exception {
        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);

        RingSearch ringSearch = new RingSearch(container, cyclicSearch);

        ringSearch.isolated();

        verify(cyclicSearch, times(1)).isolated();
    }

    @Test
    public void testFused() throws Exception {
        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);

        RingSearch ringSearch = new RingSearch(container, cyclicSearch);

        ringSearch.fused();

        verify(cyclicSearch, times(1)).fused();
    }

    @Test
    public void testRingFragments() throws Exception {
        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);
        IChemObjectBuilder builder = mock(IChemObjectBuilder.class);
        IAtom atom = mock(IAtom.class);

        RingSearch ringSearch = new RingSearch(container, cyclicSearch);

        when(cyclicSearch.cyclic()).thenReturn(new int[]{0, 1, 2});
        when(cyclicSearch.isolated()).thenReturn(new int[][]{{0, 1, 2}});
        when(cyclicSearch.fused()).thenReturn(new int[0][0]);
        when(container.getAtomCount()).thenReturn(3);
        when(container.getBuilder()).thenReturn(builder);
        when(builder.newInstance(IAtomContainer.class, 0, 0, 0, 0)).thenReturn(mock(IAtomContainer.class));
        when(container.bonds()).thenReturn(new Iterable<IBond>() {

            @Override
            public Iterator<IBond> iterator() {
                return new Iterator<IBond>() {

                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public IBond next() {
                        return null;
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        });

        ringSearch.ringFragments();

        verify(cyclicSearch, times(1)).cyclic();

        // atoms were accessed
        verify(container, times(1)).getAtom(0);
        verify(container, times(1)).getAtom(1);
        verify(container, times(1)).getAtom(2);

        // builder was invoked
        verify(builder, times(1)).newInstance(IAtomContainer.class, 0, 0, 0, 0);
    }

    @Test
    public void testIsolatedRingFragments() throws Exception {
        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);
        IChemObjectBuilder builder = mock(IChemObjectBuilder.class);
        IAtom atom = mock(IAtom.class);

        RingSearch ringSearch = new RingSearch(container, cyclicSearch);

        when(cyclicSearch.isolated()).thenReturn(new int[][]{{0, 1}, {2}});
        when(container.getBuilder()).thenReturn(builder);
        when(container.bonds()).thenReturn(new Iterable<IBond>() {

            @Override
            public Iterator<IBond> iterator() {
                return new Iterator<IBond>() {

                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public IBond next() {
                        return null;
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        });
        when(container.getAtom(anyInt())).thenReturn(mock(IAtom.class));
        when(builder.newInstance(IAtomContainer.class, 0, 0, 0, 0)).thenReturn(mock(IAtomContainer.class));

        ringSearch.isolatedRingFragments();

        verify(cyclicSearch, times(1)).isolated();

        // atoms were accessed
        verify(container, times(1)).getAtom(0);
        verify(container, times(1)).getAtom(1);
        verify(container, times(1)).getAtom(2);

        // builder was invoked
        verify(builder, times(2)).newInstance(IAtomContainer.class, 0, 0, 0, 0);

    }

    @Test
    public void testFusedRingFragments() throws Exception {
        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);
        IChemObjectBuilder builder = mock(IChemObjectBuilder.class);
        IAtom atom = mock(IAtom.class);

        RingSearch ringSearch = new RingSearch(container, cyclicSearch);

        when(cyclicSearch.fused()).thenReturn(new int[][]{{0, 1}, {2}});
        when(container.getBuilder()).thenReturn(builder);
        when(builder.newInstance(IAtomContainer.class, 0, 0, 0, 0)).thenReturn(mock(IAtomContainer.class));
        when(container.bonds()).thenReturn(new Iterable<IBond>() {

            @Override
            public Iterator<IBond> iterator() {
                return new Iterator<IBond>() {

                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public IBond next() {
                        return null;
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        });
        when(container.getAtom(anyInt())).thenReturn(mock(IAtom.class));

        ringSearch.fusedRingFragments();

        verify(cyclicSearch, times(1)).fused();

        // atoms were accessed
        verify(container, times(1)).getAtom(0);
        verify(container, times(1)).getAtom(1);
        verify(container, times(1)).getAtom(2);

        // builder was invoked
        verify(builder, times(2)).newInstance(IAtomContainer.class, 0, 0, 0, 0);
    }

    @Test
    public void connectingEdge1() {
        IAtomContainer mol = diSpiroPentane();
        RingSearch rs = new RingSearch(mol);
        IAtomContainer frag = rs.ringFragments();
        assertThat(rs.numRings(), is(4));
        assertThat(mol.getBondCount(), is(frag.getBondCount() + 1));
    }

    @Test
    public void connectingEdge2() {
        IAtomContainer mol = triSpiroPentane();
        RingSearch rs = new RingSearch(mol);
        IAtomContainer frag = rs.ringFragments();
        assertThat(rs.numRings(), is(5));
        assertThat(mol.getBondCount(), is(frag.getBondCount()));
    }

    /**
     * Hypothetial molecule - C1C[C]11(CC1)[C]123CC1.C2C3
     *
     * @cdk.inchi InChI=1/C10H16/c1-2-9(1,3-4-9)10(5-6-10)7-8-10/h1-8H2
     */
    public static IAtomContainer diSpiroPentane() {

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "C");
        a8.setFormalCharge(0);
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "C");
        a9.setFormalCharge(0);
        mol.addAtom(a9);
        IAtom a10 = builder.newInstance(IAtom.class, "C");
        a10.setFormalCharge(0);
        mol.addAtom(a10);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a3, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a4, a5, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a3, a5, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a3, a6, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a6, a7, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a7, a8, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = builder.newInstance(IBond.class, a6, a8, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = builder.newInstance(IBond.class, a6, a9, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = builder.newInstance(IBond.class, a9, a10, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = builder.newInstance(IBond.class, a6, a10, IBond.Order.SINGLE);
        mol.addBond(b13);
        return mol;
    }

    /**
     * Hypothetial molecule - C1C[C]1123CC1.C1C[C]211(CC1)C3
     *
     * @cdk.inchi InChI=1/C11H18/c1-2-10(1,3-4-10)9-11(10,5-6-11)7-8-11/h1-9H2
     */
    public static IAtomContainer triSpiroPentane() {

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "C");
        a8.setFormalCharge(0);
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "C");
        a9.setFormalCharge(0);
        mol.addAtom(a9);
        IAtom a10 = builder.newInstance(IAtom.class, "C");
        a10.setFormalCharge(0);
        mol.addAtom(a10);
        IAtom a11 = builder.newInstance(IAtom.class, "C");
        a11.setFormalCharge(0);
        mol.addAtom(a11);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a3, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a4, a5, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a3, a5, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a6, a7, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a7, a8, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a3, a8, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = builder.newInstance(IBond.class, a6, a8, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = builder.newInstance(IBond.class, a8, a9, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = builder.newInstance(IBond.class, a9, a10, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = builder.newInstance(IBond.class, a8, a10, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = builder.newInstance(IBond.class, a8, a11, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = builder.newInstance(IBond.class, a3, a11, IBond.Order.SINGLE);
        mol.addBond(b15);
        return mol;
    }

}
