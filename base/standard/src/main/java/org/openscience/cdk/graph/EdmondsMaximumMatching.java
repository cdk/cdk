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

import org.openscience.cdk.group.DisjointSetForest;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Maximum matching in general graphs using Edmond's Blossom Algorithm
 * {@cdk.cite Edmonds65}. <p/>
 *
 * This implementation was adapted from D Eppstein's python implementation (<a
 * href="http://www.ics.uci.edu/~eppstein/PADS/CardinalityMatching.py">src</a>)
 * providing efficient tree traversal and handling of blossoms. <p/>
 *
 * @author John May
 * @see <a href="http://en.wikipedia.org/wiki/Blossom_algorithm">Blossom
 * algorithm, Wikipedia</a>
 * @see <a href="http://research.microsoft.com/apps/video/dl.aspx?id=171055">Presentation
 * from Vazirani on his and Micali O(|E| * sqrt(|V|)) algorithm</a>
 *
 * @cdk.module standard
 */
final class EdmondsMaximumMatching {

    /** The graph we are matching on. */
    private final int[][]             graph;

    /** The current matching. */
    private final Matching            matching;

    /** Subset of vertices to be matched. */
    private final BitSet              subset;

    /* Algorithm data structures below. */

    /** Storage of the forest, even and odd levels */
    private final int[]               even, odd;

    /** Special 'nil' vertex. */
    private static final int          NIL     = -1;

    /** Queue of 'even' (free) vertices to start paths from. */
    private final List<Integer>       queue;

    /** Union-Find to store blossoms. */
    private DisjointSetForest         dsf;

    /**
     * Map stores the bridges of the blossom - indexed by with support
     * vertices.
     */
    private final Map<Integer, Tuple> bridges = new HashMap<Integer, Tuple>();

    /** Temporary array to fill with path information. */
    private final int[]               path;

    /**
     * Temporary bit sets when walking down 'trees' to check for
     * paths/blossoms.
     */
    private final BitSet              vAncestors, wAncestors;

    /**
     * Internal constructor.
     *
     * @param graph    adjacency list graph representation
     * @param matching the matching of the graph
     * @param subset   subset a subset of vertices
     */
    private EdmondsMaximumMatching(int[][] graph, Matching matching, BitSet subset) {

        this.graph = graph;
        this.matching = matching;
        this.subset = subset;

        this.even = new int[graph.length];
        this.odd = new int[graph.length];

        this.queue = new LinkedList<Integer>();
        this.dsf = new DisjointSetForest(graph.length);

        // tmp storage of paths in the algorithm
        path = new int[graph.length];
        vAncestors = new BitSet(graph.length);
        wAncestors = new BitSet(graph.length);

        // continuously augment while we find new paths
        while (augment());
    }

    /**
     * Find an augmenting path an alternate it's matching. If an augmenting path
     * was found then the search must be restarted. If a blossom was detected
     * the blossom is contracted and the search continues.
     *
     * @return an augmenting path was found
     */
    private boolean augment() {

        // reset data structures
        Arrays.fill(even, NIL);
        Arrays.fill(odd, NIL);
        dsf = new DisjointSetForest(graph.length);
        bridges.clear();
        queue.clear();

        // enqueue every unmatched vertex and place in the
        // even level (level = 0)
        for (int v = 0; v < graph.length; v++) {
            if (subset.get(v) && matching.unmatched(v)) {
                even[v] = v;
                queue.add(v);
            }
        }

        // for each 'free' vertex, start a bfs search
        while (!queue.isEmpty()) {
            int v = queue.remove(0);

            for (int w : graph[v]) {

                if (!subset.get(w)) continue;

                // the endpoints of the edge are both at even levels in the
                // forest - this means it is either an augmenting path or
                // a blossom
                if (even[dsf.getRoot(w)] != NIL) {
                    if (check(v, w)) return true;
                }

                // add the edge to the forest if is not already and extend
                // the tree with this matched edge
                else if (odd[w] == NIL) {
                    odd[w] = v;
                    int u = matching.other(w);
                    // add the matched edge (potential though a blossom) if it
                    // isn't in the forest already
                    if (even[dsf.getRoot(u)] == NIL) {
                        even[u] = w;
                        queue.add(u);
                    }
                }
            }
        }

        // no augmenting paths, matching is maximum
        return false;
    }

    /**
     * An edge was found which connects two 'even' vertices in the forest. If
     * the vertices have the same root we have a blossom otherwise we have
     * identified an augmenting path. This method checks for these cases and
     * responds accordingly. <p/>
     *
     * If an augmenting path was found - then it's edges are alternated and the
     * method returns true. Otherwise if a blossom was found - it is contracted
     * and the search continues.
     *
     * @param v endpoint of an edge
     * @param w another endpoint of an edge
     * @return a path was augmented
     */
    private boolean check(int v, int w) {

        // self-loop (within blossom) ignored
        if (dsf.getRoot(v) == dsf.getRoot(w)) return false;

        vAncestors.clear();
        wAncestors.clear();
        int vCurr = v;
        int wCurr = w;

        // walk back along the trees filling up 'vAncestors' and 'wAncestors'
        // with the vertices in the tree -  vCurr and wCurr are the 'even' parents
        // from v/w along the tree
        while (true) {

            vCurr = parent(vAncestors, vCurr);
            wCurr = parent(wAncestors, wCurr);

            // v and w lead to the same root - we have found a blossom. We
            // traveled all the way down the tree thus vCurr (and wCurr) are
            // the base of the blossom
            if (vCurr == wCurr) {
                blossom(v, w, vCurr);
                return false;
            }

            // we are at the root of each tree and the roots are different, we
            // have found and augmenting path
            if (dsf.getRoot(even[vCurr]) == vCurr && dsf.getRoot(even[wCurr]) == wCurr) {
                augment(v);
                augment(w);
                matching.match(v, w);
                return true;
            }

            // the current vertex in 'v' can be found in w's ancestors they must
            // share a root - we have found a blossom whose base is 'vCurr'
            if (wAncestors.get(vCurr)) {
                blossom(v, w, vCurr);
                return false;
            }

            // the current vertex in 'w' can be found in v's ancestors they must
            // share a root, we have found a blossom whose base is 'wCurr'
            if (vAncestors.get(wCurr)) {
                blossom(v, w, wCurr);
                return false;
            }
        }
    }

    /**
     * Access the next ancestor in a tree of the forest. Note we go back two
     * places at once as we only need check 'even' vertices.
     *
     * @param ancestors temporary set which fills up the path we traversed
     * @param curr      the current even vertex in the tree
     * @return the next 'even' vertex
     */
    private int parent(BitSet ancestors, int curr) {
        curr = dsf.getRoot(curr);
        ancestors.set(curr);
        int parent = dsf.getRoot(even[curr]);
        if (parent == curr) return curr; // root of tree
        ancestors.set(parent);
        return dsf.getRoot(odd[parent]);
    }

    /**
     * Create a new blossom for the specified 'bridge' edge.
     *
     * @param v    adjacent to w
     * @param w    adjacent to v
     * @param base connected to the stem (common ancestor of v and w)
     */
    private void blossom(int v, int w, int base) {
        base = dsf.getRoot(base);
        int[] supports1 = blossomSupports(v, w, base);
        int[] supports2 = blossomSupports(w, v, base);

        for (int i = 0; i < supports1.length; i++)
            dsf.makeUnion(supports1[i], supports1[0]);
        for (int i = 0; i < supports2.length; i++)
            dsf.makeUnion(supports2[i], supports2[0]);

        even[dsf.getRoot(base)] = even[base];
    }

    /**
     * Creates the blossom 'supports' for the specified blossom 'bridge' edge
     * (v, w). We travel down each side to the base of the blossom ('base')
     * collapsing vertices and point any 'odd' vertices to the correct 'bridge'
     * edge. We do this by indexing the birdie to each vertex in the 'bridges'
     * map.
     *
     * @param v    an endpoint of the blossom bridge
     * @param w    another endpoint of the blossom bridge
     * @param base the base of the blossom
     */
    private int[] blossomSupports(int v, int w, int base) {

        int n = 0;
        path[n++] = dsf.getRoot(v);
        Tuple b = new Tuple(v, w);
        while (path[n - 1] != base) {
            int u = even[path[n - 1]];
            path[n++] = u;
            this.bridges.put(u, b);
            // contracting the blossom allows us to continue searching from odd
            // vertices (any odd vertices are now even - part of the blossom set)
            queue.add(u);
            path[n++] = dsf.getRoot(odd[u]);
        }

        return Arrays.copyOf(path, n);
    }

    /**
     * Augment all ancestors in the tree of vertex 'v'.
     *
     * @param v the leaf to augment from
     */
    private void augment(int v) {
        int n = buildPath(path, 0, v, NIL);
        for (int i = 2; i < n; i += 2) {
            matching.match(path[i], path[i - 1]);
        }
    }

    /**
     * Builds the path backwards from the specified 'start' vertex until the
     * 'goal'. If the path reaches a blossom then the path through the blossom
     * is lifted to the original graph.
     *
     * @param path  path storage
     * @param i     offset (in path)
     * @param start start vertex
     * @param goal  end vertex
     * @return the number of items set to the path[].
     */
    private int buildPath(int[] path, int i, int start, int goal) {
        while (true) {

            // lift the path through the contracted blossom
            while (odd[start] != NIL) {

                Tuple bridge = bridges.get(start);

                // add to the path from the bridge down to where 'start'
                // is - we need to reverse it as we travel 'up' the blossom
                // and then...
                int j = buildPath(path, i, bridge.first, start);
                reverse(path, i, j - 1);
                i = j;

                // ... we travel down the other side of the bridge
                start = bridge.second;
            }
            path[i++] = start;

            // root of the tree
            if (matching.unmatched(start)) return i;

            path[i++] = matching.other(start);

            // end of recursive
            if (path[i - 1] == goal) return i;

            start = odd[path[i - 1]];
        }
    }

    /**
     * Reverse a section of a fixed size array.
     *
     * @param path a path
     * @param i    start index
     * @param j    end index
     */
    private static void reverse(int[] path, int i, int j) {
        while (i < j) {
            int tmp = path[i];
            path[i] = path[j];
            path[j] = tmp;
            i++;
            j--;
        }
    }

    /**
     * Attempt to maximise the provided matching over a subset of vertices in a
     * graph.
     *
     * @param matching the independent edge set to maximise
     * @param graph    adjacency list graph representation
     * @param subset   subset of vertices
     * @return the matching
     */
    static Matching maxamise(Matching matching, int[][] graph, BitSet subset) {
        new EdmondsMaximumMatching(graph, matching, subset);
        return matching;
    }

    /**
     * Storage and indexing of a two int values.
     */
    private static final class Tuple {

        /** Values. */
        private final int first, second;

        /**
         * Create a new tuple.
         *
         * @param first  a value
         * @param second another value
         */
        private Tuple(int first, int second) {
            this.first = first;
            this.second = second;
        }

        /**{@inheritDoc} */
        @Override
        public int hashCode() {
            return 31 * first + second;
        }

        /**{@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tuple that = (Tuple) o;
            return this.first == that.first && this.second == that.second;
        }
    }
}
