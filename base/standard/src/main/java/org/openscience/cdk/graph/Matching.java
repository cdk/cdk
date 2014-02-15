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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.util.Arrays;

/**
 * A matching is an independent edge set of a graph. This is a set of edges that
 * share no common vertices. A matching is perfect if every vertex in the graph
 * is matched. Each vertex can be matched with exactly one other vertex.<p/>
 *
 * This class provides storage and manipulation of a matching. A new match is
 * added with {@link #match(int, int)}, any existing match for the newly matched
 * vertices is no-longer available. The status of a vertex can be queried with
 * {@link #matched(int)} and the matched vertex obtained with {@link
 * #other(int)}. <p/>
 *
 * @author John May
 * @cdk.module standard
 * @see <a href="http://en.wikipedia.org/wiki/Matching_(graph_theory)">Matching
 * (graph theory), Wikipedia</a>
 */
@TestClass("org.openscience.cdk.graph.MatchingTest")
public final class Matching {

    /** Indicate an unmatched vertex. */
    private static final int Nil = -1;

    /** Match storage. */
    private final int[] match;

    /**
     * Create a matching of the given size.
     *
     * @param n number of items
     */
    private Matching(final int n) {
        this.match = new int[n];
        Arrays.fill(match, Nil);
    }

    /**
     * Add the edge '{u,v}' to the matched edge set. Any existing matches for
     * 'u' or 'v' are removed from the matched set.
     *
     * @param u a vertex
     * @param v another vertex
     */
    @TestMethod("match") public void match(final int u, final int v) {
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
    @TestMethod("other") public int other(final int v) {
        if (unmatched(v))
            throw new IllegalArgumentException(v + " is not matched");
        return match[v];
    }

    /**
     * Remove a matching for the specified vertex.
     *
     * @param v vertex
     */
    @TestMethod("unmatch") public void unmatch(final int v) {
        match[v] = Nil;
    }

    /**
     * Determine if a vertex has a match.
     *
     * @param v vertex
     * @return the vertex is matched
     */
    @TestMethod("nop") public boolean matched(final int v) {
        return !unmatched(v);
    }

    /**
     * Determine if a vertex is not matched. 
     *
     * @param v a vertex
     * @return the vertex has no matching
     */
    @TestMethod("nop") public boolean unmatched(final int v) {
        return match[v] == Nil || match[match[v]] != v;
    }

    /**
     * Create an empty matching with the specified capacity.
     *
     * @param capacity maxmium number of vertices
     * @return empty matching
     */
    @TestMethod("nop")
    public static Matching withCapacity(final int capacity) {
        return new Matching(capacity);
    }

    /** @inheritDoc */
    @TestMethod("string")
    @Override public String toString() {
        StringBuilder sb = new StringBuilder(4 * match.length);
        sb.append('[');
        for (int u = 0; u < match.length; u++) {
            int v = match[u];
            if (v > u && match[v] == u) {
                if (sb.length() > 1)
                    sb.append(", ");
                sb.append(u)
                  .append('=')
                  .append(v);
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
