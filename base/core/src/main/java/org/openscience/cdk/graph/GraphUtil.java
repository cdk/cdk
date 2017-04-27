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

import com.google.common.collect.Maps;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.copyOf;

/**
 * Collection of static utilities for manipulating adjacency list
 * representations stored as a {@literal int[][]}. May well be replaced in
 * future with a <i>Graph</i> data type.
 *
 * @author John May
 * @cdk.module core
 * @cdk.githash
 * @see ShortestPaths
 * @see org.openscience.cdk.ringsearch.RingSearch
 */
public class GraphUtil {

    private static final int DEFAULT_DEGREE = 4;

    private GraphUtil() {}

    /**
     * Create an adjacent list representation of the {@literal container}.
     *
     * @param container the molecule
     * @return adjacency list representation stored as an {@literal int[][]}.
     * @throws NullPointerException     the container was null
     * @throws IllegalArgumentException a bond was found which contained atoms
     *                                  not in the molecule
     */
    public static int[][] toAdjList(IAtomContainer container) {

        if (container == null) throw new NullPointerException("atom container was null");

        int n = container.getAtomCount();

        int[][] graph = new int[n][DEFAULT_DEGREE];
        int[] degree = new int[n];

        for (IBond bond : container.bonds()) {

            int v = container.indexOf(bond.getBeg());
            int w = container.indexOf(bond.getEnd());

            if (v < 0 || w < 0)
                throw new IllegalArgumentException("bond at index " + container.indexOf(bond)
                        + " contained an atom not pressent in molecule");

            graph[v][degree[v]++] = w;
            graph[w][degree[w]++] = v;

            // if the vertex degree of v or w reaches capacity, double the size
            if (degree[v] == graph[v].length) graph[v] = copyOf(graph[v], degree[v] * 2);
            if (degree[w] == graph[w].length) graph[w] = copyOf(graph[w], degree[w] * 2);
        }

        for (int v = 0; v < n; v++) {
            graph[v] = copyOf(graph[v], degree[v]);
        }

        return graph;
    }

    /**
     * Create an adjacent list representation of the {@literal container} that only
     * includes bonds that are in the set provided as an argument.
     *
     * @param container the molecule
     * @return adjacency list representation stored as an {@literal int[][]}.
     * @throws NullPointerException     the container was null
     * @throws IllegalArgumentException a bond was found which contained atoms
     *                                  not in the molecule
     */
    public static int[][] toAdjListSubgraph(IAtomContainer container, Set<IBond> include) {

        if (container == null) throw new NullPointerException("atom container was null");

        int n = container.getAtomCount();

        int[][] graph = new int[n][DEFAULT_DEGREE];
        int[] degree = new int[n];

        for (IBond bond : container.bonds()) {

            if (!include.contains(bond))
                continue;

            int v = container.indexOf(bond.getBeg());
            int w = container.indexOf(bond.getEnd());

            if (v < 0 || w < 0)
                throw new IllegalArgumentException("bond at index " + container.indexOf(bond)
                                                   + " contained an atom not pressent in molecule");

            graph[v][degree[v]++] = w;
            graph[w][degree[w]++] = v;

            // if the vertex degree of v or w reaches capacity, double the size
            if (degree[v] == graph[v].length) graph[v] = copyOf(graph[v], degree[v] * 2);
            if (degree[w] == graph[w].length) graph[w] = copyOf(graph[w], degree[w] * 2);
        }

        for (int v = 0; v < n; v++) {
            graph[v] = copyOf(graph[v], degree[v]);
        }

        return graph;
    }

    /**
     * Create an adjacent list representation of the {@code container} and
     * fill in the {@code bondMap} for quick lookup.
     *
     * @param container the molecule
     * @param bondMap a map to index the bonds into
     * @return adjacency list representation stored as an {@literal int[][]}.
     * @throws NullPointerException     the container was null
     * @throws IllegalArgumentException a bond was found which contained atoms
     *                                  not in the molecule
     */
    public static int[][] toAdjList(IAtomContainer container, EdgeToBondMap bondMap) {

        if (container == null) throw new NullPointerException("atom container was null");

        int n = container.getAtomCount();

        int[][] graph = new int[n][DEFAULT_DEGREE];
        int[] degree = new int[n];

        for (IBond bond : container.bonds()) {

            int v = container.indexOf(bond.getBeg());
            int w = container.indexOf(bond.getEnd());

            if (v < 0 || w < 0)
                throw new IllegalArgumentException("bond at index " + container.indexOf(bond)
                        + " contained an atom not pressent in molecule");

            graph[v][degree[v]++] = w;
            graph[w][degree[w]++] = v;

            // if the vertex degree of v or w reaches capacity, double the size
            if (degree[v] == graph[v].length) graph[v] = copyOf(graph[v], degree[v] * 2);
            if (degree[w] == graph[w].length) graph[w] = copyOf(graph[w], degree[w] * 2);

            bondMap.put(v, w, bond);
        }

        for (int v = 0; v < n; v++) {
            graph[v] = copyOf(graph[v], degree[v]);
        }

        return graph;
    }

    /**
     * Create a subgraph by specifying the vertices from the original {@literal
     * graph} to {@literal include} in the subgraph. The provided vertices also
     * provide the mapping between vertices in the subgraph and the original.
     *
     * <blockquote><pre>{@code
     * int[][] g  = toAdjList(naphthalene);
     * int[]   vs = new int[]{0, 1, 2, 3, 4, 5};
     *
     * int[][] h = subgraph(g, vs);
     * // for the vertices in h, the provided 'vs' gives the original index
     * for(int v = 0; v < h.length; v++) {
     *     // vs[v] is 'v' in 'g'
     * }
     * }</pre></blockquote>
     *
     * @param graph   adjacency list graph
     * @param include the vertices of he graph to include in the subgraph
     * @return the subgraph
     */
    public static int[][] subgraph(int[][] graph, int[] include) {

        // number of vertices in the graph and the subgraph
        int n = graph.length;
        int m = include.length;

        // mapping from vertex in 'graph' to 'subgraph'
        int[] mapping = new int[n];
        for (int i = 0; i < m; i++) {
            mapping[include[i]] = i + 1;
        }

        // initialise the subgraph
        int[] degree = new int[m];
        int[][] subgraph = new int[m][DEFAULT_DEGREE];

        // build the subgraph, in the subgraph we denote to adjacent
        // vertices p and q. If p or q is less then 0 then it is not
        // in the subgraph
        for (int v = 0; v < n; v++) {
            int p = mapping[v] - 1;
            if (p < 0) continue;

            for (int w : graph[v]) {
                int q = mapping[w] - 1;
                if (q < 0) continue;
                if (degree[p] == subgraph[p].length) subgraph[p] = copyOf(subgraph[p], 2 * subgraph[p].length);
                subgraph[p][degree[p]++] = q;
            }
        }

        // truncate excess storage
        for (int p = 0; p < m; p++) {
            subgraph[p] = copyOf(subgraph[p], degree[p]);
        }

        return subgraph;
    }

    /**
     * Arrange the {@literal vertices} in a simple cyclic path. If the vertices
     * do not form such a path an {@link IllegalArgumentException} is thrown.
     *
     * @param graph    a graph
     * @param vertices set of vertices
     * @return vertices in a walk which makes a cycle (first and last are the
     *         same)
     * @throws IllegalArgumentException thrown if the vertices do not form a
     *                                  cycle
     * @see org.openscience.cdk.ringsearch.RingSearch#isolated()
     */
    public static int[] cycle(int[][] graph, int[] vertices) {

        int n = graph.length;
        int m = vertices.length;

        // mark vertices
        boolean[] marked = new boolean[n];
        for (int v : vertices) {
            marked[v] = true;
        }

        int[] path = new int[m + 1];

        path[0] = path[m] = vertices[0];
        marked[vertices[0]] = false;

        for (int i = 1; i < m; i++) {
            int w = firstMarked(graph[path[i - 1]], marked);
            if (w < 0) throw new IllegalArgumentException("broken path");
            path[i] = w;
            marked[w] = false;
        }

        // the path is a cycle if the start and end are adjacent, if this is
        // the case return the path
        for (int w : graph[path[m - 1]]) {
            if (w == path[0]) return path;
        }

        throw new IllegalArgumentException("path does not make a cycle");
    }

    /**
     * Find the first value in {@literal ws} which is {@literal marked}.
     *
     * @param xs     array of values
     * @param marked marked values
     * @return first marked value, -1 if none found
     */
    static int firstMarked(int[] xs, boolean[] marked) {
        for (int x : xs)
            if (marked[x]) return x;
        return -1;
    }

    /** Utility for storing {@link IBond}s indexed by vertex end points. */
    public static final class EdgeToBondMap {

        /**
         * Internal map.
         */
        private final Map<Tuple, IBond> lookup;

        /**
         * Internal constructor - create with enough space for, n bonds.
         * @param n number of bonds expected
         */
        private EdgeToBondMap(int n) {
            this.lookup = Maps.newHashMapWithExpectedSize(n);
        }

        /**
         * Index a bond by the endpoints.
         *
         * @param v    an endpoint
         * @param w    another endpoint
         * @param bond the bond value
         * @return the previous bond value
         */
        private IBond put(int v, int w, IBond bond) {
            return lookup.put(new Tuple(v, w), bond);
        }

        /**
         * Access the bond store at the end points v and w. If no bond is
         * store, null is returned.
         *
         * @param v an endpoint
         * @param w another endpoint
         * @return the bond stored for the endpoints
         */
        public IBond get(int v, int w) {
            return lookup.get(new Tuple(v, w));
        }

        /**
         * Create a map with enough space for all the bonds in the molecule,
         * {@code container}. Note - the map is not filled by this method.
         *
         * @param container the container
         * @return a map with enough space for the container
         */
        public static EdgeToBondMap withSpaceFor(IAtomContainer container) {
            return new EdgeToBondMap(container.getBondCount());
        }
    }

    /**
     * Unordered storage of two int values. Mainly useful to index bonds by
     * it's vertex end points.
     */
    private static final class Tuple {

        private final int u, v;

        /**
         * Create a new tuple with the specified values.
         * @param u a value
         * @param v another value
         */
        private Tuple(int u, int v) {
            this.u = u;
            this.v = v;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tuple that = (Tuple) o;

            return this.u == that.u && this.v == that.v || this.u == that.v && this.v == that.u;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public int hashCode() {
            return u ^ v;
        }
    }
}
