/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import static java.util.Arrays.copyOf;

/**
 * Collection of static utilities for manipulating adjacency list
 * representations stored as a {@literal int[][]}. May well be replaced in
 * future with a <i>Graph</i> data type.
 *
 * @author John May
 * @cdk.module core
 * @see ShortestPaths
 * @see org.openscience.cdk.ringsearch.RingSearch
 */
@TestClass("org.openscience.cdk.graph.GraphUtilTest")
public class GraphUtil {

    private static final int DEFAULT_DEGREE = 4;

    private GraphUtil() {
    }

    /**
     * Create an adjacent list representation of the {@literal container}.
     *
     * @param container the molecule
     * @return adjacency list representation stored as an {@literal int[][]}.
     * @throws NullPointerException     the container was null
     * @throws IllegalArgumentException a bond was found which contained atoms
     *                                  not in the molecule
     */
    @TestMethod("testToAdjList,testToAdjList_resize,testToAdjList_missingAtom," +
                        "testToAdjList_Empty,testToAdjList_Null")
    public static int[][] toAdjList(IAtomContainer container) {

        if (container == null)
            throw new NullPointerException("atom container was null");

        int n = container.getAtomCount();

        int[][] graph = new int[n][DEFAULT_DEGREE];
        int[] degree = new int[n];

        for (IBond bond : container.bonds()) {

            int v = container.getAtomNumber(bond.getAtom(0));
            int w = container.getAtomNumber(bond.getAtom(1));

            if (v < 0 || w < 0)
                throw new IllegalArgumentException("bond at index " + container
                        .getBondNumber(bond)
                                                           + " contained an atom not pressent in molecule");

            graph[v][degree[v]++] = w;
            graph[w][degree[w]++] = v;

            // if the vertex degree of v or w reaches capacity, double the size
            if (degree[v] == graph[v].length)
                graph[v] = copyOf(graph[v], degree[v] * 2);
            if (degree[w] == graph[w].length)
                graph[w] = copyOf(graph[w], degree[w] * 2);
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
     * <blockquote><pre>
     * int[][] g  = toAdjList(naphthalene);
     * int[]   vs = new int[]{0, 1, 2, 3, 4, 5};
     *
     * int[][] h = subgraph(g, vs);
     * // for the vertices in h, the provided 'vs' gives the original index
     * for(int v = 0; v < h.length; v++) {
     *     // vs[v] is 'v' in 'g'
     * }
     * </pre></blockquote>
     *
     * @param graph   adjacency list graph
     * @param include the vertices of he graph to include in the subgraph
     * @return the subgraph
     */
    @TestMethod("sequentialSubgraph,intermittentSubgraph,resizeSubgraph")
    public static int[][] subgraph(int[][] graph, int[] include) {

        // number of vertices in the graph and the subgraph
        int n = graph.length;
        int m = include.length;

        // lookup from graph to subgraph
        int[] lookup = new int[n];
        for (int i = 0; i < m; i++) {
            lookup[include[i]] = i + 1;
        }

        // initialise the subgraph
        int[] degree = new int[m];
        int[][] subgraph = new int[m][DEFAULT_DEGREE];

        // build the subgraph, in the subgraph we denote to adjacent
        // vertices p and q. If p or q is less then 0 then it is not
        // in the subgraph
        for (int v = 0; v < n; v++) {
            int p = lookup[v] - 1;
            if (p < 0)
                continue;

            for (int w : graph[v]) {
                int q = lookup[w] - 1;
                if (q < 0)
                    continue;
                if (degree[p] == subgraph[p].length)
                    subgraph[p] = copyOf(subgraph[p], 2 * subgraph[p].length);
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
     * @return
     * @throws IllegalArgumentException thrown if the vertices do not form a
     *                                  cycle
     * @see org.openscience.cdk.ringsearch.RingSearch#isolated()
     */
    @TestMethod("testCycle,testAcyclic,testAcyclic2")
    public static int[] cycle(int[][] graph, int[] vertices) {

        int n = graph.length;
        int m = vertices.length;

        // mark vertices
        boolean[] marked = new boolean[n];
        for (int v : vertices) {
            marked[v] = true;
        }

        int[] path = new int[m];

        path[0] = vertices[0];
        marked[vertices[0]] = false;

        for (int i = 1; i < path.length; i++) {
            int w = firstMarked(graph[path[i - 1]], marked);
            if (w < 0)
                throw new IllegalArgumentException("broken path");
            path[i] = w;
            marked[w] = false;
        }

        // the path is a cycle if the start and end are adjacent, if this is
        // the case return the path
        for (int w : graph[path[m - 1]]) {
            if (w == path[0])
                return path;
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
    @TestMethod("firstMarked")
    static int firstMarked(int[] xs, boolean[] marked) {
        for (int x : xs)
            if (marked[x]) return x;
        return -1;
    }

}


