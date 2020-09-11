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
package org.openscience.cdk.graph;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openscience.cdk.graph.BitMatrixTest.toBitSet;

import java.util.Arrays;
import java.util.BitSet;

import org.openscience.cdk.graph.InitialCycles.Cycle;

import org.junit.Test;

/**
 * @author John May
 * @cdk.module test-core
 */
public class GreedyBasisTest {

    @Test
    public void add() {
        Cycle c1 = mock(Cycle.class);
        Cycle c2 = mock(Cycle.class);
        when(c1.edgeVector()).thenReturn(new BitSet());
        when(c2.edgeVector()).thenReturn(new BitSet());
        GreedyBasis basis = new GreedyBasis(2, 0);
        assertTrue(basis.members().isEmpty());
        basis.add(c1);
        assertThat(basis.members(), hasItem(c1));
        basis.add(c2);
        assertThat(basis.members(), hasItems(c1, c2));
    }

    @Test
    public void addAll() {
        Cycle c1 = mock(Cycle.class);
        Cycle c2 = mock(Cycle.class);
        when(c1.edgeVector()).thenReturn(new BitSet());
        when(c2.edgeVector()).thenReturn(new BitSet());
        GreedyBasis basis = new GreedyBasis(2, 0);
        assertTrue(basis.members().isEmpty());
        basis.addAll(Arrays.asList(c1, c2));
        assertThat(basis.members(), hasItems(c1, c2));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unmodifiableMembers() {
        Cycle c1 = mock(Cycle.class);
        when(c1.edgeVector()).thenReturn(new BitSet());
        GreedyBasis basis = new GreedyBasis(2, 0);
        basis.members().add(c1);
    }

    @Test
    public void subsetOfBasis() {
        Cycle c1 = mock(Cycle.class);
        Cycle c2 = mock(Cycle.class);
        Cycle c3 = mock(Cycle.class);
        when(c1.edgeVector()).thenReturn(toBitSet("111000000000"));
        when(c2.edgeVector()).thenReturn(toBitSet("000111000000"));
        when(c3.edgeVector()).thenReturn(toBitSet("011110000000"));
        when(c1.length()).thenReturn(3);
        when(c2.length()).thenReturn(3);
        when(c3.length()).thenReturn(4);
        GreedyBasis basis = new GreedyBasis(3, 12);
        assertFalse(basis.isSubsetOfBasis(c3));
        basis.add(c1);
        assertFalse(basis.isSubsetOfBasis(c3));
        basis.add(c2);
        assertTrue(basis.isSubsetOfBasis(c3));

    }

    @Test
    public void independence() {
        Cycle c1 = mock(Cycle.class);
        Cycle c2 = mock(Cycle.class);
        Cycle c3 = mock(Cycle.class);
        when(c1.edgeVector()).thenReturn(toBitSet("111000000000"));
        when(c2.edgeVector()).thenReturn(toBitSet("000111000000"));
        when(c3.edgeVector()).thenReturn(toBitSet("111111000000"));
        when(c1.length()).thenReturn(3);
        when(c2.length()).thenReturn(3);
        when(c3.length()).thenReturn(6);
        GreedyBasis basis = new GreedyBasis(3, 12);
        assertTrue(basis.isIndependent(c1));
        assertTrue(basis.isIndependent(c2));
        assertTrue(basis.isIndependent(c3));
        basis.add(c1);
        assertTrue(basis.isIndependent(c2));
        assertTrue(basis.isIndependent(c2));
        basis.add(c2);
        assertFalse(basis.isIndependent(c3));
    }

    @Test
    public void size() {
        GreedyBasis basis = new GreedyBasis(3, 12);
        assertThat(basis.size(), is(0));
        Cycle c1 = mock(Cycle.class);
        Cycle c2 = mock(Cycle.class);
        Cycle c3 = mock(Cycle.class);
        when(c1.edgeVector()).thenReturn(toBitSet("111000000000"));
        when(c2.edgeVector()).thenReturn(toBitSet("000111000000"));
        when(c3.edgeVector()).thenReturn(toBitSet("111111000000"));
        basis.add(c1);
        assertThat(basis.size(), is(1));
        basis.add(c2);
        assertThat(basis.size(), is(2));
        basis.add(c3);
        assertThat(basis.size(), is(3));
    }

}
