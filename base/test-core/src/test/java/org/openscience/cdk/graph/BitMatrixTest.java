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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openscience.cdk.graph.InitialCycles.Cycle;

/**
 * @author John May
 * @cdk.module test-core
 */
public class BitMatrixTest {

    @Test
    public void swap_basic() {
        BitMatrix m = new BitMatrix(0, 4);
        List<BitSet> rows = new ArrayList<BitSet>();
        for (int i = 0; i < 4; i++) {
            BitSet row = new BitSet();
            rows.add(row);
            m.add(row);
        }
        m.swap(0, 1);
        m.swap(2, 3);
        m.swap(0, 2);
        // check when we access by index we get back the exact same instance
        for (int i = 0; i < 4; i++) {
            assertThat(m.row(i), is(sameInstance(rows.get(i))));
        }
    }

    // ensure we can access the rows by with original index even after swapping
    @Test
    public void swap() {
        BitMatrix m = new BitMatrix(0, 100);
        List<BitSet> rows = new ArrayList<BitSet>();
        for (int i = 0; i < 100; i++) {
            BitSet row = new BitSet();
            rows.add(row);
            m.add(row);
        }

        // randomly swap rows
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            m.swap(random.nextInt(100), random.nextInt(100));
        }

        // check when we access by index we get back the exact same instance
        for (int i = 0; i < 100; i++) {
            assertThat(m.row(i), is(sameInstance(rows.get(i))));
        }
    }

    @Test
    public void clear() {
        BitMatrix m = new BitMatrix(9, 3);
        BitSet r1 = toBitSet("110000000");
        BitSet r2 = toBitSet("100000000");
        BitSet r3 = toBitSet("010000000");
        m.add(r1);
        m.add(r2);
        m.add(r3);
        assertThat(m.row(0), is(sameInstance(r1)));
        assertThat(m.row(1), is(sameInstance(r2)));
        assertThat(m.row(2), is(sameInstance(r3)));
        m.clear();
        m.add(r3);
        m.add(r2);
        m.add(r1);
        assertThat(m.row(0), is(sameInstance(r3)));
        assertThat(m.row(1), is(sameInstance(r2)));
        assertThat(m.row(2), is(sameInstance(r1)));
    }

    @Test
    public void indexOf() {
        BitMatrix m = new BitMatrix(9, 3);
        BitSet r1 = toBitSet("010000000");
        BitSet r2 = toBitSet("100001000");
        BitSet r3 = toBitSet("010000000");
        m.add(r1);
        m.add(r2);
        m.add(r3);
        assertThat(m.indexOf(0, 0), is(1));
        assertThat(m.indexOf(0, 1), is(1));
        assertThat(m.indexOf(0, 2), is(-1));
        assertThat(m.indexOf(1, 0), is(0));
        assertThat(m.indexOf(1, 1), is(2));
        assertThat(m.indexOf(1, 2), is(2));
        assertThat(m.indexOf(2, 0), is(-1));
        assertThat(m.indexOf(2, 1), is(-1));
        assertThat(m.indexOf(2, 2), is(-1));
    }

    @Test
    public void string() {
        BitMatrix m = new BitMatrix(9, 3);
        m.add(toBitSet("110000000"));
        m.add(toBitSet("110011000"));
        m.add(toBitSet("000011000"));
        String str = m.toString();
        assertThat(str, is("0: 11-------\n" + "1: 11--11---\n" + "2: ----11---\n"));
    }

    @Test
    public void eliminate1() throws Exception {
        // vectors[0] = vectors[1] ^ vectors[2] (xor)
        BitMatrix m = new BitMatrix(9, 3);
        m.add(toBitSet("110000000"));
        m.add(toBitSet("110011000"));
        m.add(toBitSet("000011000"));
        assertThat(m.eliminate(), is(2));
        assertFalse(m.eliminated(0));
        assertFalse(m.eliminated(1));
        assertTrue(m.eliminated(2));
    }

    @Test
    public void eliminate2() throws Exception {
        // vectors[2] = vectors[0] ^ vectors[1] (xor)
        BitMatrix m = new BitMatrix(9, 3);
        m.add(toBitSet("110011000"));
        m.add(toBitSet("001000110"));
        m.add(toBitSet("111011110"));
        assertThat(m.eliminate(), is(2));
        assertFalse(m.eliminated(0));
        assertFalse(m.eliminated(1));
        assertTrue(m.eliminated(2));
    }

    @Test
    public void eliminate3() throws Exception {
        // all vectors are independent
        BitMatrix m = new BitMatrix(15, 4);

        // 1-3 can all be made from each other
        m.add(toBitSet("111111000000000"));
        m.add(toBitSet("000111111000000"));
        m.add(toBitSet("111000111000000"));

        // 4 cannot
        m.add(toBitSet("111000000111100"));

        // 1,2 or 3 was eliminated
        assertThat(m.eliminate(), is(3));

        // 4 was not
        assertFalse(m.eliminated(3));
    }

    @Test
    public void independent1() throws Exception {
        BitMatrix m = new BitMatrix(9, 3);
        m.add(toBitSet("010011000"));
        m.add(toBitSet("001000110"));
        m.add(toBitSet("111011110"));
        assertThat(m.eliminate(), is(3));
        assertFalse(m.eliminated(0));
        assertFalse(m.eliminated(1));
        assertFalse(m.eliminated(2));
    }

    @Test
    public void independent2() throws Exception {
        // all vectors are independent
        BitMatrix m = new BitMatrix(9, 3);
        m.add(toBitSet("110011000"));
        m.add(toBitSet("110011011"));
        m.add(toBitSet("110011010"));
        assertThat(m.eliminate(), is(3));
        assertFalse(m.eliminated(0));
        assertFalse(m.eliminated(1));
        assertFalse(m.eliminated(2));
    }

    @Test
    public void duplicates() throws Exception {
        // ensure duplicates are handled
        BitMatrix m = new BitMatrix(9, 3);
        m.add(toBitSet("110000000"));
        m.add(toBitSet("110000000"));
        m.add(toBitSet("001100000"));
        assertThat(m.eliminate(), is(2));
    }

    /**
     * Convert a string of binary 1's and 0's to a bitset.
     *
     * @param binary a string of 1's and 0's
     * @return the bit set
     */
    public static BitSet toBitSet(String binary) {
        BitSet s = new BitSet(binary.length());
        char[] cs = binary.toCharArray();
        for (int i = 0; i < binary.length(); i++) {
            if (cs[i] == '1') {
                s.set(i);
            }
        }
        return s;
    }

    @Test
    public void xor() {
        BitSet s = toBitSet("00011");
        BitSet t = toBitSet("10010");
        BitSet u = BitMatrix.xor(s, t);
        assertThat(s, is(not(sameInstance(u))));
        assertThat(t, is(not(sameInstance(u))));
        assertThat(u, is(toBitSet("10001")));
    }

    @Test
    public void from_cycles() {
        Cycle c1 = mock(Cycle.class);
        Cycle c2 = mock(Cycle.class);
        Cycle c3 = mock(Cycle.class);
        BitSet s1 = toBitSet("010011000");
        BitSet s2 = toBitSet("110011011");
        BitSet s3 = toBitSet("110011010");
        when(c1.edgeVector()).thenReturn(s1);
        when(c2.edgeVector()).thenReturn(s2);
        when(c3.edgeVector()).thenReturn(s3);
        BitMatrix m = BitMatrix.from(Arrays.asList(c1, c2, c3));
        assertThat(m.row(0), is(sameInstance(s1)));
        assertThat(m.row(1), is(sameInstance(s2)));
        assertThat(m.row(2), is(sameInstance(s3)));
    }

    @Test
    public void from_cycles_cycle() {
        Cycle c1 = mock(Cycle.class);
        Cycle c2 = mock(Cycle.class);
        Cycle last = mock(Cycle.class);
        BitSet s1 = toBitSet("010011000");
        BitSet s2 = toBitSet("110011011");
        BitSet s3 = toBitSet("110011010");
        when(c1.edgeVector()).thenReturn(s1);
        when(c2.edgeVector()).thenReturn(s2);
        when(last.edgeVector()).thenReturn(s3);
        BitMatrix m = BitMatrix.from(Arrays.asList(c1, c2), last);
        assertThat(m.row(0), is(sameInstance(s1)));
        assertThat(m.row(1), is(sameInstance(s2)));
        assertThat(m.row(2), is(sameInstance(s3)));
    }

}
