/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
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


import java.util.Arrays;
import java.util.BitSet;

/**
 * A matching is an independent edge set of a graph. This is a set of edges that
 * share no common vertices. A matching is perfect if every vertex in the graph
 * is matched. Each vertex can be matched with exactly one other vertex.
 *
 * This class provides storage and manipulation of a matching. A new match is
 * added with {@link #match(int, int)}, any existing match for the newly matched
 * vertices is no-longer available. The status of a vertex can be queried with
 * {@link #matched(int)} and the matched vertex obtained with {@link
 * #other(int)}. 
 *
 * @author John May
 * @cdk.module standard
 * @see <a href="http://en.wikipedia.org/wiki/Matching_(graph_theory)">Matching
 * (graph theory), Wikipedia</a>
 */
public final class Matching {

    /** Indicate an unmatched vertex. */
    private static final int NIL = -1;

    /** Match storage. */
    private final int[]      match;

    /**
     * Create a matching of the given size.
     *
     * @param n number of items
     */
    private Matching(final int n) {
        this.match = new int[n];
        Arrays.fill(match, NIL);
    }

    /**
     * Add the edge '{u,v}' to the matched edge set. Any existing matches for
     * 'u' or 'v' are removed from the matched set.
     *
     * @param u a vertex
     * @param v another vertex
     */
    public void match(final int u, final int v) {
        // set the new match, don't need to update existing - we only provide
        // access to bidirectional mappings
        match[u] = v;
        match[v] = u;
    }

    /**
     * Access the vertex matched with 'v'.
     *
     * @param v vertex
     * @return matched vertex
     * @throws IllegalArgumentException the vertex is currently unmatched
     */
    public int other(final int v) {
        if (unmatched(v)) throw new IllegalArgumentException(v + " is not matched");
        return match[v];
    }

    /**
     * Remove a matching for the specified vertex.
     *
     * @param v vertex
     */
    public void unmatch(final int v) {
        match[v] = NIL;
    }

    /**
     * Determine if a vertex has a match.
     *
     * @param v vertex
     * @return the vertex is matched
     */
    public boolean matched(final int v) {
        return !unmatched(v);
    }

    /**
     * Determine if a vertex is not matched.
     *
     * @param v a vertex
     * @return the vertex has no matching
     */
    public boolean unmatched(final int v) {
        return match[v] == NIL || match[match[v]] != v;
    }

    /**
     * Attempt to augment the matching such that it is perfect over the subset
     * of vertices in the provided graph.
     *
     * @param graph  adjacency list representation of graph
     * @param subset subset of vertices
     * @return the matching was perfect
     * @throws IllegalArgumentException the graph was a different size to the
     *                                  matching capacity
     */
    public boolean perfect(int[][] graph, BitSet subset) {

        if (graph.length != match.length || subset.cardinality() > graph.length)
            throw new IllegalArgumentException("graph and matching had different capacity");

        // and odd set can never provide a perfect matching
        if ((subset.cardinality() & 0x1) == 0x1) return false;

        // arbitrary matching was perfect
        if (arbitaryMatching(graph, subset)) return true;

        EdmondsMaximumMatching.maxamise(this, graph, subset);

        // the matching is imperfect if any vertex was
        for (int v = subset.nextSetBit(0); v >= 0; v = subset.nextSetBit(v + 1))
            if (unmatched(v)) return false;

        return true;
    }

    /**
     * Assign an arbitrary matching that covers the subset of vertices.
     *
     * @param graph  adjacency list representation of graph
     * @param subset subset of vertices in the graph
     * @return the matching was perfect
     */
    boolean arbitaryMatching(final int[][] graph, final BitSet subset) {

        final BitSet unmatched = new BitSet();

        // indicates the deg of each vertex in unmatched subset
        final int[] deg = new int[graph.length];

        // queue/stack of vertices with deg1 vertices
        final int[] deg1 = new int[graph.length];
        int nd1 = 0, nMatched = 0;

        for (int v = subset.nextSetBit(0); v >= 0; v = subset.nextSetBit(v + 1)) {
            if (matched(v)) {
                assert subset.get(other(v));
                nMatched++;
                continue;
            }
            unmatched.set(v);
            for (int w : graph[v])
                if (subset.get(w) && unmatched(w)) deg[v]++;
            if (deg[v] == 1) deg1[nd1++] = v;
        }

        while (!unmatched.isEmpty()) {

            int v = -1;

            // attempt to select a vertex with degree = 1 (in matched set)
            while (nd1 > 0) {
                v = deg1[--nd1];
                if (unmatched.get(v)) break;
            }

            // no unmatched degree 1 vertex, select the first unmatched
            if (v < 0 || unmatched.get(v)) v = unmatched.nextSetBit(0);

            unmatched.clear(v);

            // find a unmatched edge and match it, adjacent degrees are updated
            for (final int w : graph[v]) {
                if (unmatched.get(w)) {
                    match(v, w);
                    nMatched += 2;
                    unmatched.clear(w);
                    // update neighbors of w and v (if needed)
                    for (final int u : graph[w])
                        if (--deg[u] == 1 && unmatched.get(u)) deg1[nd1++] = u;

                    // if deg == 1, w is the only neighbor
                    if (deg[v] > 1) {
                        for (final int u : graph[v])
                            if (--deg[u] == 1 && unmatched.get(u)) deg1[nd1++] = u;
                    }
                    break;
                }
            }
        }

        return nMatched == subset.cardinality();
    }

    /**
     * Create an empty matching with the specified capacity.
     *
     * @param capacity maximum number of vertices
     * @return empty matching
     */
    public static Matching withCapacity(final int capacity) {
        return new Matching(capacity);
    }

    /**{@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(4 * match.length);
        sb.append('[');
        for (int u = 0; u < match.length; u++) {
            int v = match[u];
            if (v > u && match[v] == u) {
                if (sb.length() > 1) sb.append(", ");
                sb.append(u).append('=').append(v);
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
