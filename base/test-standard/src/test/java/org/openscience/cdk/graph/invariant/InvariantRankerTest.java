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

package org.openscience.cdk.graph.invariant;

import com.google.common.primitives.Longs;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-standard
 */
public class InvariantRankerTest {

    @Test
    public void rank() {

        InvariantRanker ranker = new InvariantRanker(6);
        long[] prev = new long[]{1, 1, 1, 1, 1, 1};
        long[] curr = new long[]{50, 100, 25, 100, 50, 90};

        // no we leave extra space
        int[] vs = new int[]{0, 1, 2, 3, 4, 5};
        int[] ws = new int[6];

        int ranks = ranker.rank(vs, ws, 6, curr, prev);

        assertThat(ranks, is(4));

        // assigned ranks (note: unique assigned first)
        assertThat(prev, is(new long[]{2, 5, 1, 5, 2, 4}));

        // remaining non-unique vertices
        assertThat(ws, is(new int[]{0, 4, 1, 3, -1, 0}));
    }

    @Test
    public void rank_all_equiv() {

        InvariantRanker ranker = new InvariantRanker(6);
        long[] prev = new long[]{1, 1, 1, 1, 1, 1};
        long[] curr = new long[]{42, 42, 42, 42, 42, 42};

        // no we leave extra space
        int[] vs = new int[]{0, 1, 2, 3, 4, 5};
        int[] ws = new int[6];

        int ranks = ranker.rank(vs, ws, 6, curr, prev);

        assertThat(ranks, is(1));

        // assigned ranks (note: unique assigned first)
        assertThat(prev, is(new long[]{1, 1, 1, 1, 1, 1}));

        // remaining non-unique vertices
        assertThat(ws, is(new int[]{0, 1, 2, 3, 4, 5}));
    }

    @Test
    public void rank_all_unique() {

        InvariantRanker ranker = new InvariantRanker(7);
        long[] prev = new long[]{1, 1, 1, 1, 1, 1, 1};
        long[] curr = new long[]{7, 3, 1, 0, 91, 32, 67};

        // no we leave extra space
        int[] vs = new int[]{0, 1, 2, 3, 4, 5, 6};
        int[] ws = new int[7];

        int ranks = ranker.rank(vs, ws, 7, curr, prev);

        assertThat(ranks, is(7));

        // assigned ranks (note: unique assigned first)
        assertThat(prev, is(new long[]{4, 3, 2, 1, 7, 5, 6}));

        // no non-unique vertices
        assertThat(ws, is(new int[]{-1, 0, 0, 0, 0, 0, 0}));
    }

    @Test
    public void mergeSort() {

        int n = 100;

        // random (unique) values in random order
        Random rnd = new Random();
        Set<Long> values = new HashSet<Long>();
        while (values.size() < n)
            values.add(rnd.nextLong());

        long[] prev = Longs.toArray(values);

        // ident array
        int[] vs = new int[n];
        for (int i = 0; i < n; i++)
            vs[i] = i;

        InvariantRanker invRanker = new InvariantRanker(n);
        invRanker.sortBy(vs, 0, n, prev, prev);

        // check they are sorted
        for (int i = 1; i < n; i++)
            assertTrue(prev[vs[i]] > prev[vs[i - 1]]);
    }

    @Test
    public void mergeSort_range() {

        int n = 100;

        // random (unique) values in random order
        Random rnd = new Random();
        Set<Long> values = new HashSet<Long>();
        while (values.size() < n)
            values.add(rnd.nextLong());

        long[] prev = Longs.toArray(values);

        // ident array
        int[] vs = new int[n];
        for (int i = 0; i < n; i++)
            vs[i] = i;

        InvariantRanker invRanker = new InvariantRanker(n);
        invRanker.sortBy(vs, 10, n - 20, prev, prev);

        // check they are sorted
        for (int i = 11; i < (n - 20); i++)
            assertTrue(prev[vs[i]] > prev[vs[i - 1]]);

        // other values weren't touched
        for (int i = 0; i < 10; i++)
            assertThat(vs[i], is(i));
        for (int i = n - 10; i < n; i++)
            assertThat(vs[i], is(i));
    }

    @Test
    public void insertionSort() {
        long[] prev = new long[]{11, 10, 9, 8, 7};
        long[] curr = new long[]{11, 10, 9, 8, 7};
        int[] vs = new int[]{0, 1, 2, 3, 4};
        InvariantRanker.insertionSortBy(vs, 0, 5, curr, prev);
        assertThat(vs, is(new int[]{4, 3, 2, 1, 0}));
    }

    @Test
    public void insertionSort_duplicate() {
        long[] prev = new long[]{11, 10, 10, 9, 8, 7};
        long[] curr = new long[]{11, 10, 10, 9, 8, 7};
        int[] vs = new int[]{0, 1, 2, 3, 4, 5};
        InvariantRanker.insertionSortBy(vs, 0, 6, curr, prev);
        assertThat(vs, is(new int[]{5, 4, 3, 1, 2, 0}));
    }

    @Test
    public void insertionSort_range() {
        long[] prev = new long[]{12, 11, 10, 9, 8, 7};
        long[] curr = new long[]{12, 11, 10, 9, 8, 7};
        int[] vs = new int[]{0, 1, 2, 3, 4, 5};
        InvariantRanker.insertionSortBy(vs, 2, 3, curr, prev);
        assertThat(vs, is(new int[]{0, 1, 4, 3, 2, 5}));
    }

    @Test
    public void less() throws Exception {
        long[] prev = new long[]{1, 1, 2, 2};
        long[] curr = new long[]{1, 1, 2, 2};
        assertFalse(InvariantRanker.less(0, 1, curr, prev));
        assertFalse(InvariantRanker.less(2, 3, curr, prev));
        assertTrue(InvariantRanker.less(0, 2, curr, prev));
        assertTrue(InvariantRanker.less(0, 3, curr, prev));
        assertTrue(InvariantRanker.less(1, 2, curr, prev));
        assertTrue(InvariantRanker.less(1, 3, curr, prev));
    }

    @Test
    public void lessUsingPrev() throws Exception {
        long[] prev = new long[]{1, 1, 2, 2};
        long[] curr = new long[]{1, 2, 1, 2};
        // 0,1 and 2,3 are only less is we inspect the 'curr' invariants
        assertTrue(InvariantRanker.less(0, 1, curr, prev));
        assertTrue(InvariantRanker.less(2, 3, curr, prev));
        // these values are only less inspecting the first invariants
        assertTrue(InvariantRanker.less(0, 2, curr, prev));
        assertTrue(InvariantRanker.less(0, 3, curr, prev));
        assertTrue(InvariantRanker.less(1, 2, curr, prev));
        assertTrue(InvariantRanker.less(1, 3, curr, prev));
    }
}
