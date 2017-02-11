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

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A path graph (<b>P-Graph</b>) for graphs with less than 64 vertices - the
 * P-Graph provides efficient generation of all simple cycles in a graph
 * {@cdk.cite HAN96}. Vertices are sequentially removed from the graph by
 * reducing incident edges and forming new 'path edges'. The order in which the
 * vertices are to be removed should be pre-defined in the constructor as the
 * {@code rank[]} parameter.
 *
 * @author John May
 * @author Till Schäfer (predefined vertex ordering)
 * @cdk.module core
 * @cdk.githash
 * @see org.openscience.cdk.ringsearch.RingSearch
 * @see GraphUtil
 * @see <a href="http://en.wikipedia.org/wiki/Biconnected_component">Wikipedia:
 *      Biconnected Component</a>
 */
final class RegularPathGraph extends PathGraph {

    /** Path edges, indexed by their end points (incidence list). */
    private final List<PathEdge>[] graph;

    /** Limit on the maximum length of cycle to be found. */
    private final int              limit;

    /** Indicates when each vertex will be removed, '0' = first, '|V|' = last. */
    private final int[]            rank;

    /**
     * Create a regular path graph (<b>P-Graph</b>) for the given molecule graph
     * (<b>M-Graph</b>).
     *
     * @param mGraph The molecule graph (M-Graph) in adjacency list
     *               representation.
     * @param rank   Unique rank of each vertex - indicates when it will be
     *               removed.
     * @param limit  Limit for size of cycles found, to find all cycles specify
     *               the limit as the number of vertices in the graph.
     * @throws IllegalArgumentException limit was invalid or the graph was too
     *                                  large
     * @throws NullPointerException     the molecule graph was not provided
     */
    @SuppressWarnings("unchecked")
    RegularPathGraph(final int[][] mGraph, final int[] rank, final int limit) {

        checkNotNull(mGraph, "no molecule graph");
        checkNotNull(rank, "no rank provided");

        this.graph = new List[mGraph.length];
        this.rank = rank;
        this.limit = limit + 1; // first/last vertex repeats
        int ord = graph.length;

        // check configuration
        checkArgument(ord > 2, "graph was acyclic");
        checkArgument(limit >= 3 && limit <= ord, "limit should be from 3 to |V|");
        checkArgument(ord < 64, "graph has 64 or more atoms, use JumboPathGraph");

        for (int v = 0; v < ord; v++)
            graph[v] = Lists.newArrayList();

        // construct the path-graph
        for (int v = 0; v < ord; v++) {
            for (final int w : mGraph[v]) {
                if (w > v) add(new SimpleEdge(v, w));
            }
        }
    }

    /**
     * Add a path-edge to the path-graph. Edges are only added to the vertex of
     * lowest rank (see. constructor).
     *
     * @param edge path edge
     */
    private void add(final PathEdge edge) {
        int u = edge.either();
        int v = edge.other(u);
        if (rank[u] < rank[v])
            graph[u].add(edge);
        else
            graph[v].add(edge);
    }

    /**{@inheritDoc} */
    @Override
    public int degree(final int x) {
        return graph[x].size();
    }

    /**
     * Access edges which are incident to <i>x</i> and remove them from the
     * graph.
     *
     * @param x a vertex
     * @return vertices incident to x
     */
    private List<PathEdge> remove(final int x) {
        final List<PathEdge> edges = graph[x];
        graph[x] = Collections.emptyList();
        return edges;
    }

    /**
     * Pairwise combination of all disjoint <i>edges</i> incident to a vertex
     * <i>x</i>.
     *
     * @param edges edges which are currently incident to <i>x</i>
     * @param x     a vertex in the graph
     * @return reduced edges
     */
    private List<PathEdge> combine(final List<PathEdge> edges, final int x) {

        final int n = edges.size();
        final List<PathEdge> reduced = new ArrayList<PathEdge>(n);

        for (int i = 0; i < n; i++) {
            PathEdge e = edges.get(i);
            for (int j = i + 1; j < n; j++) {
                PathEdge f = edges.get(j);
                if (e.disjoint(f)) reduced.add(new ReducedEdge(e, f, x));
            }
        }

        return reduced;
    }

    /**{@inheritDoc} */
    @Override
    void remove(final int x, final List<int[]> cycles) {

        final List<PathEdge> edges = remove(x);
        final List<PathEdge> reduced = combine(edges, x);

        for (final PathEdge e : reduced) {
            if (e.len() <= limit) {
                if (e.loop())
                    cycles.add(e.path());
                else
                    add(e);
            }
        }
    }

    /** Empty bit set. */
    private static final long EMPTY_SET = 0;

    /**
     * An abstract path edge. A path edge has two end points and 0 or more
     * reduced vertices which represent a path between those endpoints.
     */
    static abstract class PathEdge {

        /** Endpoints of the edge. */
        final int  u, v;

        /** Bits indicate reduced vertices between endpoints (exclusive). */
        final long xs;

        /**
         * A new edge specified by two endpoints and a bit set indicating which
         * vertices have been reduced.
         *
         * @param u  an endpoint
         * @param v  the other endpoint
         * @param xs reduced vertices between endpoints
         */
        PathEdge(int u, int v, long xs) {
            this.u = u;
            this.v = v;
            this.xs = xs;
        }

        /**
         * Check if the edges are disjoint with respect to their reduced
         * vertices. That is, excluding the endpoints, no reduced vertices are
         * shared.
         *
         * @param other another edge
         * @return the edges reduced vertices are disjoint.
         */
        final boolean disjoint(final PathEdge other) {
            return (this.xs & other.xs) == EMPTY_SET;
        }

        /**
         * Is the edge a loop and connects a vertex to its self.
         *
         * @return whether the edge is a loop
         */
        final boolean loop() {
            return u == v;
        }

        /**
         * Access either endpoint of the edge.
         *
         * @return either endpoint.
         */
        final int either() {
            return u;
        }

        /**
         * Given one endpoint, retrieve the other endpoint.
         *
         * @param x an endpoint
         * @return the other endpoint.
         */
        final int other(final int x) {
            return u == x ? v : u;
        }

        /**
         * Total length of the path formed by this edge. The value includes
         * endpoints and reduced vertices.
         *
         * @return length of path
         */
        abstract int len();

        /**
         * Reconstruct the path through the edge by appending vertices to a
         * mutable {@link ArrayBuilder}.
         *
         * @param ab array builder to append vertices to
         * @return the array builder parameter for convenience
         */
        abstract ArrayBuilder reconstruct(ArrayBuilder ab);

        /**
         * The path stored by the edge as a fixed size array of vertices.
         *
         * @return fixed size array of vertices which are in the path.
         */
        final int[] path() {
            return reconstruct(new ArrayBuilder(len()).append(either())).xs;
        }
    }

    /** A simple non-reduced edge, just the two end points. */
    static final class SimpleEdge extends PathEdge {

        /**
         * A new simple edge, with two endpoints.
         *
         * @param u an endpoint
         * @param v another endpoint
         */
        SimpleEdge(int u, int v) {
            super(u, v, EMPTY_SET);
        }

        /**{@inheritDoc} */
        @Override
        ArrayBuilder reconstruct(ArrayBuilder ab) {
            return ab.append(other(ab.prev()));
        }

        /**{@inheritDoc} */
        @Override
        int len() {
            return 2;
        }
    }

    /**
     * A reduced edge, made from two existing path edges and an endpoint they
     * have in common.
     */
    static final class ReducedEdge extends PathEdge {

        /** Reduced edges. */
        private final PathEdge e, f;

        /**
         * Create a new reduced edge from two existing edges and vertex they
         * have in common.
         *
         * @param e an edge
         * @param f another edge
         * @param x a common vertex
         */
        ReducedEdge(PathEdge e, PathEdge f, int x) {
            super(e.other(x), f.other(x), e.xs | f.xs | 1L << x);
            this.e = e;
            this.f = f;
        }

        /**{@inheritDoc} */
        @Override
        ArrayBuilder reconstruct(ArrayBuilder ab) {
            return u == ab.prev() ? f.reconstruct(e.reconstruct(ab)) : e.reconstruct(f.reconstruct(ab));
        }

        /**{@inheritDoc} */
        @Override
        int len() {
            return Long.bitCount(xs) + 2;
        }
    }

    /**
     * A simple helper class for constructing a fixed size int[] array and
     * sequentially appending vertices.
     */
    static final class ArrayBuilder {

        private int i = 0;
        final int[] xs;

        /**
         * A new array builder of fixed size.
         *
         * @param n size of the array
         */
        ArrayBuilder(final int n) {
            xs = new int[n];
        }

        /**
         * Append a value to the end of the sequence.
         *
         * @param x a new value
         * @return self-reference for chaining
         */
        ArrayBuilder append(int x) {
            xs[i++] = x;
            return this;
        }

        /**
         * Previously value in the sequence.
         *
         * @return previous value
         */
        int prev() {
            return xs[i - 1];
        }
    }
}
