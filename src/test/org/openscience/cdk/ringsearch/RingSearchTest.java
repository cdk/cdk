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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertFalse;
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
    public void testCyclic_Atom() throws Exception {

        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);

        when(container.getAtomNumber(any(IAtom.class))).thenReturn(42);

        RingSearch ringSearch = new RingSearch(container, cyclicSearch);
        ringSearch.cyclic(atom);

        // verify the number returned from getAtomNumber is passed on
        verify(container, times(1)).getAtomNumber(atom);
        verify(cyclicSearch, times(1)).cyclic(42);
    }

    @Test(expected = NoSuchElementException.class)
    public void testCyclic_Atom_NotFound() throws Exception {

        CyclicVertexSearch cyclicSearch = mock(CyclicVertexSearch.class);
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);

        when(container.getAtomNumber(any(IAtom.class))).thenReturn(-1);

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
        when(builder.newInstance(IAtomContainer.class, 0, 0, 0, 0))
                .thenReturn(mock(IAtomContainer.class));
        when(container.bonds()).thenReturn(new Iterable<IBond>() {
            @Override public Iterator<IBond> iterator() {
                return new Iterator<IBond>() {
                    @Override public boolean hasNext() {
                        return false;
                    }

                    @Override public IBond next() {
                        return null;
                    }

                    @Override public void remove() {

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
            @Override public Iterator<IBond> iterator() {
                return new Iterator<IBond>() {
                    @Override public boolean hasNext() {
                        return false;
                    }

                    @Override public IBond next() {
                        return null;
                    }

                    @Override public void remove() {

                    }
                };
            }
        });
        when(container.getAtom(anyInt())).thenReturn(mock(IAtom.class));
        when(builder.newInstance(IAtomContainer.class, 0, 0, 0, 0))
                .thenReturn(mock(IAtomContainer.class));

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
        when(builder.newInstance(IAtomContainer.class, 0, 0, 0, 0))
                .thenReturn(mock(IAtomContainer.class));
        when(container.bonds()).thenReturn(new Iterable<IBond>() {
            @Override public Iterator<IBond> iterator() {
                return new Iterator<IBond>() {
                    @Override public boolean hasNext() {
                        return false;
                    }

                    @Override public IBond next() {
                        return null;
                    }

                    @Override public void remove() {

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
}
