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

/**
 * A utility for ranking indices by invariants. The ranking is built around
 * a merge/insertion sort with the primary interaction through {@link #rank}.
 *
 * @author John May
 * @cdk.module standard
 * @see <a href="http://algs4.cs.princeton.edu/22mergesort/">Mergesort</a>
 * @see Canon
 */
final class InvariantRanker {

    /** Auxiliary array for merge sort. */
    private final int[]      aux;

    /**
     * Length at which the sub-array should be sorted using insertion sort. As
     * insertion sort is adaptive and in-place it's advantageous to use a high
     * threshold for this use-case. Once we do the first sort, the invariants
     * will always be 'almost' sorted which is the best case for the insertion
     * sort.
     */
    private static final int INSERTION_SORT_THRESHOLD = 42;

    /**
     * Create an invariant ranker for {@code n} invariants.
     * @param n number of values
     */
    InvariantRanker(int n) {
        this.aux = new int[n];
    }

    /**
     * Given an array of equivalent indices (currEq) and their values (curr)
     * assign a rank to the values. The values are sorted using 'prev' and
     * 'curr' invariants and once ranked the new ranks placed in 'prev'. The
     * values which are still equivalent are placed in 'nextEq' and terminated
     * by a '-1'.
     *
     * @param currEq currently equivalent vertices (initially identity)
     * @param nextEq equivalent vertices (to refine) will be set by this method
     * @param n      the number of currently equivalent vertices
     * @param curr   the current invariants
     * @param prev   the prev invariants (initially = curr) used to sort and
     *               then store ranks (set by this method)
     * @return the number of ranks
     */
    int rank(int[] currEq, int[] nextEq, int n, long[] curr, long[] prev) {

        sortBy(currEq, 0, n, curr, prev);

        // with the values sorted we now partition the values in to those
        // which are unique and aren't unique.

        // we use the aux array memory but to make it easier to read we alias
        // nu: number unique, nnu: number non-unique
        int nEquivalent = 0;

        // values are partitioned we now need to assign the new ranks. unique
        // values are assigned first then the non-unique ranks. we know which
        // rank to start at by seeing how many have already been assigned. this
        // is given by (|V| - |current non unique|).
        int nRanks = 1 + curr.length - n;

        int[] tmp = aux;

        int u = currEq[0];
        int labelTick = tmp[u] = (int) prev[u];
        long label = labelTick;

        for (int i = 1; i < n; i++) {
            int v = currEq[i];

            if (prev[v] != tmp[u])
                labelTick = (int) prev[v];
            else
                labelTick++;

            if (curr[v] != curr[u] || prev[v] != tmp[u]) {
                tmp[v] = (int) prev[v];
                prev[v] = labelTick;
                label = labelTick;
                nRanks++;
            } else {
                if (nEquivalent == 0 || nextEq[nEquivalent - 1] != u) nextEq[nEquivalent++] = u;
                nextEq[nEquivalent++] = v;
                tmp[v] = (int) prev[v];
                prev[v] = label;
            }

            u = v;
        }

        if (nEquivalent < nextEq.length) nextEq[nEquivalent] = -1;

        return nRanks;
    }

    /**
     * Sort the values (using merge sort) in {@code vs} from {@code lo} (until
     * {@code len}) by the {@code prev[]} and then {@code curr[]} invariants to
     * determine rank. The values in {@code vs} are indices into the invariant
     * arrays.
     *
     * @param vs   values (indices)
     * @param lo   the first value to start sorting from
     * @param len  the len of values to consider
     * @param curr the current invariants
     * @param prev the previous invariants
     */
    void sortBy(int[] vs, int lo, int len, long[] curr, long[] prev) {

        if (len < INSERTION_SORT_THRESHOLD) {
            insertionSortBy(vs, lo, len, curr, prev);
            return;
        }

        int split = len / 2;

        sortBy(vs, lo, split, curr, prev);
        sortBy(vs, lo + split, len - split, curr, prev);

        // sub arrays already sorted, no need to merge
        if (!less(vs[lo + split], vs[lo + split - 1], curr, prev)) return;

        merge(vs, lo, split, len, curr, prev);
    }

    /**
     * Merge the values which are sorted between {@code lo} - {@code split} and
     * {@code split} - {@code len}.
     *
     * @param vs    vertices
     * @param lo    start index
     * @param split the middle index (partition)
     * @param len   the range to merge
     * @param curr  the current invariants
     * @param prev  the previous invariants
     */
    private void merge(int[] vs, int lo, int split, int len, long[] curr, long[] prev) {
        System.arraycopy(vs, lo, aux, lo, len);

        int i = lo, j = lo + split;
        int iMax = lo + split, jMax = lo + len;
        for (int k = lo, end = lo + len; k < end; k++) {
            if (i == iMax)
                vs[k] = aux[j++];
            else if (j == jMax)
                vs[k] = aux[i++];
            else if (less(aux[i], aux[j], curr, prev))
                vs[k] = aux[i++];
            else
                vs[k] = aux[j++];
        }
    }

    /**
     * Sort the values (using insertion sort) in {@code vs} from {@code lo}
     * (until {@code len}) by the {@code prev[]} and then {@code curr[]}
     * invariants to determine rank. The values in {@code vs} are indices into
     * the invariant arrays.
     *
     * @param vs   values (indices)
     * @param lo   the first value to start sorting from
     * @param len  the len of values to consider
     * @param curr the current invariants
     * @param prev the previous invariants
     */
    static void insertionSortBy(int[] vs, int lo, int len, long[] curr, long[] prev) {
        for (int j = lo + 1, hi = lo + len; j < hi; j++) {
            int v = vs[j];
            int i = j - 1;
            while ((i >= lo) && less(v, vs[i], curr, prev))
                vs[i + 1] = vs[i--];
            vs[i + 1] = v;
        }
    }

    /**
     * Using the {@code prev} and {@code curr} invariants is value in index i
     * less than j. Value i is less than j if it was previously less than j
     * (prev[]) or it was equal and it is now (curr[]) less than j.
     *
     * @param i    an index
     * @param j    an index
     * @param curr current invariants
     * @param prev previous invariants
     * @return is the value in index i less than j
     */
    static boolean less(int i, int j, long[] curr, long[] prev) {
        return prev[i] < prev[j] || prev[i] == prev[j] && curr[i] < curr[j];
    }
}
