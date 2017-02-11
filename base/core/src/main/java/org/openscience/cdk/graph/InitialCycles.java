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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.google.common.primitives.Ints;

import java.util.BitSet;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.copyOf;

/**
 * Compute the set of initial cycles (<i>C'<sub>I</sub></i>) in a graph. The
 * super-set contains the minimum cycle basis (<i>C<sub>B</sub></i>) and the
 * relevant cycles (<i>C<sub>R</sub></i>) of the provided graph {@cdk.cite
 * Vismara97}. This class is intend for internal use by other cycle processing
 * algorithms.
 *
 * @author John May
 * @cdk.module core
 * @see RelevantCycles
 */
final class InitialCycles {

    /** Adjacency list representation of a chemical graph. */
    private final int[][]                  graph;

    /** Vertex ordering. */
    private final int[]                    ordering;

    /** Cycle prototypes indexed by their length. */
    private final Multimap<Integer, Cycle> cycles         = TreeMultimap.create();

    /** Index of edges in the graph */
    private final BiMap<Edge, Integer>     edges;

    /**
     * Initial array size for 'ordering()'. This method sorts vertices by degree
     * by counting how many of each degree there is then putting values in place
     * directly. This is known as key-value counting and is used in radix
     * sorts.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Radix_sort#Least_significant_digit_radix_sorts">Radix
     *      Sort</a>
     */
    private final static int               DEFAULT_DEGREE = 4;

    /** Number of vertices which have degree 2. */
    private int                            nDeg2Vertices;

    /** Limit the size of cycles discovered. */
    private final int                      limit;

    /**
     * Is the graph known to be a biconnected component. This allows a small
     * optimisation.
     */
    private final boolean                  biconnected;

    /**
     * Create a set of initial cycles for the provided graph.
     *
     * @param graph input graph
     * @throws NullPointerException the graph was null
     */
    InitialCycles(final int[][] graph) {
        this(graph, graph.length, false);
    }

    /**
     * Create a set of initial cycles for the provided graph.
     *
     * @param graph input graph
     * @param limit the maximum size of cycle found
     * @throws NullPointerException the graph was null
     */
    InitialCycles(final int[][] graph, int limit) {
        this(graph, limit, false);
    }

    /**
     * Internal constructor - takes a graph and a flag that the graph is a
     * biconnected component. This allows a minor optimisation to trigger.
     *
     * @param graph input graph
     * @param biconnected the graph is known to be biconnected
     * @throws NullPointerException the graph was null
     */
    private InitialCycles(final int[][] graph, final int limit, boolean biconnected) {
        this.graph = checkNotNull(graph, "no graph provided");

        // ordering ensures the number of initial cycles is polynomial
        this.biconnected = biconnected;
        this.limit = limit;
        this.ordering = ordering(graph);

        // index the edges to allow us to jump between edge and path representation
        // - edge representation: binary vector indicates whether an edge
        //                        is present or
        // - path representation: sequential list vertices forming the cycle
        edges = HashBiMap.create(graph.length);
        int n = graph.length;
        for (int v = 0; v < n; v++) {
            for (int w : graph[v]) {
                if (w > v) {
                    Edge edge = new Edge(v, w);
                    edges.put(edge, edges.size());
                }
            }
        }

        // compute the initial set of cycles
        compute();
    }

    /**
     * Access to graph used to calculate the initial cycle set.
     *
     * @return the graph
     */
    int[][] graph() {
        return graph;
    }

    /**
     * Unique lengths of all cycles found in natural order.
     *
     * @return lengths of the discovered cycles
     */
    Iterable<Integer> lengths() {
        return cycles.keySet();
    }

    /**
     * Access all the prototype cycles of the given length. If no cycles were
     * found of given length an empty list is returned.
     *
     * @param length desired length of cycles
     * @return cycles of the given length
     * @see #lengths()
     */
    Collection<Cycle> cyclesOfLength(int length) {
        return cycles.get(length);
    }

    /**
     * Construct a list of all cycles.
     *
     * @return list of cycles
     */
    Collection<Cycle> cycles() {
        return cycles.values();
    }

    /**
     * Number of cycles in the initial set.
     *
     * @return number of cycles
     */
    int numberOfCycles() {
        return cycles.size();
    }

    /**
     * The number of edges (<i>m</i>) in the graph.
     *
     * @return number of edges
     */
    int numberOfEdges() {
        return edges.size();
    }

    /**
     * Access the {@link Edge} at the given index.
     *
     * @param i index of edge
     * @return the edge at the given index
     */
    Edge edge(int i) {
        return edges.inverse().get(i);
    }

    /**
     * Lookup the index of the edge formed by the vertices <i>u</i> and
     * <i>v</i>.
     *
     * @param u a vertex adjacent to <i>v</i>
     * @param v a vertex adjacent to <i>u</i>
     * @return the index of the edge
     */
    int indexOfEdge(final int u, final int v) {
        return edges.get(new Edge(u, v));
    }

    /**
     * Convert a path of vertices to a binary vector of edges. It is possible to
     * convert the vector back to the path using {@see #edge}.
     *
     * @param path the vertices which define the cycle
     * @return vector edges which make up the path
     * @see #indexOfEdge(int, int)
     */
    BitSet toEdgeVector(final int[] path) {
        final BitSet incidence = new BitSet(edges.size());
        int len = path.length - 1;
        for (int i = 0; i < len; i++) {
            incidence.set(indexOfEdge(path[i], path[i + 1]));
        }
        return incidence;
    }

    /**
     * Compute the initial cycles. The code corresponds to algorithm 1 from
     * {@cdk.cite Vismara97}, where possible the variable names have been kept
     * the same.
     */
    private void compute() {

        int n = graph.length;

        // the set 'S' contains the pairs of vertices adjacent to 'y'
        int[] s = new int[n];
        int sizeOfS;

        // order the vertices by degree
        int[] vertices = new int[n];
        for (int v = 0; v < n; v++) {
            vertices[ordering[v]] = v;
        }

        // if the graph is known to be a biconnected component (prepossessing)
        // and there is at least one vertex with a degree > 2 we can skip all
        // vertices of degree 2.
        //
        // otherwise the smallest possible cycle is {0,1,2} (no parallel edges
        // or loops) we can therefore don't need to do the first two shortest
        // paths calculations
        int first = biconnected && nDeg2Vertices < n ? nDeg2Vertices : 2;

        for (int i = first; i < n; i++) {
            final int r = vertices[i];

            ShortestPaths pathsFromR = new ShortestPaths(graph, null, r, limit / 2, ordering);

            // we only check the vertices which belong to the set Vr. this
            // set is vertices reachable from 'r' by only travelling though
            // vertices smaller then r. In the ShortestPaths API this is
            // name 'isPrecedingPathTo'.
            //
            // using Vr allows us to prune the number of vertices to check and
            // discover each cycle exactly once. This is possible as for each
            // simple cycle there is only one vertex with a maximum ordering.
            for (int j = 0; j < i; j++) {
                final int y = vertices[j];
                if (!pathsFromR.isPrecedingPathTo(y)) continue;

                // start refilling set 's' by resetting it's size
                sizeOfS = 0;

                // z is adjacent to y and belong to Vr
                for (final int z : graph[y]) {
                    if (!pathsFromR.isPrecedingPathTo(z)) continue;

                    final int distToZ = pathsFromR.distanceTo(z);
                    final int distToY = pathsFromR.distanceTo(y);

                    // the distance of the path to z is one less then the
                    // path to y. the vertices are adjacent, therefore z must
                    // also belong to the shortest path from r to y.
                    //
                    // we queue up (in 's') all the vertices adjacent to y for
                    // which this holds and then check these once we've processed
                    // all adjacent vertices
                    //
                    //  / ¯ ¯ z1 \          z1 and z2 are added to 's' and
                    // r          y - z3    checked later as p and q (see below)
                    //  \ _ _ z2 /
                    //
                    if (distToZ + 1 == distToY) {
                        s[sizeOfS++] = z;
                    }

                    // if the distances are equal we could have an odd cycle
                    // but we need to check the paths only intersect at 'r'.
                    //
                    // we check the intersect for cases like this, shortest
                    // cycle here is {p .. y, z .. p} not {r .. y, z .. r}
                    //
                    //           / ¯ ¯ y         / ¯ ¯ \ / ¯ ¯ y
                    //  r - - - p      |    or  r       p      |
                    //           \ _ _ z         \ _ _ / \ _ _ z
                    //
                    // if it's the shortest cycle then the intersect is just {r}
                    //
                    //  / ¯ ¯ y
                    // r      |
                    //  \ _ _ z
                    //
                    else if (distToZ == distToY && ordering[z] < ordering[y]) {
                        final int[] pathToY = pathsFromR.pathTo(y);
                        final int[] pathToZ = pathsFromR.pathTo(z);
                        if (singletonIntersect(pathToZ, pathToY)) {
                            Cycle cycle = new OddCycle(pathsFromR, pathToY, pathToZ);
                            add(cycle);
                        }
                    }
                }

                // check each pair vertices adjacent to 'y' for an
                // even cycle, as with the odd cycle we ensure the intersect
                // of the paths {r .. p} and {r .. q} is {r}.
                //
                //  / ¯ ¯ p \
                // r         y
                //  \ _ _ q /
                //
                for (int k = 0; k < sizeOfS; k++) {
                    for (int l = k + 1; l < sizeOfS; l++) {
                        int[] pathToP = pathsFromR.pathTo(s[k]);
                        int[] pathToQ = pathsFromR.pathTo(s[l]);
                        if (singletonIntersect(pathToP, pathToQ)) {
                            Cycle cycle = new EvenCycle(pathsFromR, pathToP, y, pathToQ);
                            add(cycle);
                        }
                    }
                }
            }
        }
    }

    /**
     * Add a newly discovered initial cycle.
     *
     * @param cycle the cycle to add
     */
    private void add(Cycle cycle) {
        if (cycle.length() <= limit) cycles.put(cycle.length(), cycle);
    }

    /**
     * Compute the vertex ordering (π). The ordering is based on the vertex
     * degree and {@literal π(x) < π(y) => deg(x) ≤ deg(y)}. The ordering
     * guarantees the number of elements in <i>C<sub>I</sub></i> is <
     * <i>2m<sup>2</sup> + vn</i>. See Lemma 3 of {@cdk.cite Vismara97}.
     *
     * @return the order of each vertex
     */
    private int[] ordering(final int[][] graph) {

        final int n = graph.length;

        int[] order = new int[n];
        int[] count = new int[DEFAULT_DEGREE + 1];

        // count the occurrences of each key (degree)
        for (int v = 0; v < n; v++) {
            int key = graph[v].length + 1;
            if (key >= count.length) count = copyOf(count, key * 2);
            count[key]++;
        }
        // cumulated degree counts
        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }
        // store the location each vertex would occur
        for (int v = 0; v < n; v++) {
            order[v] = count[graph[v].length]++;
        }
        nDeg2Vertices = count[2];
        return order;
    }

    /**
     * Given two paths from a common start vertex <i>r</i> check whether there
     * are any intersects. If the paths are different length the shorter of the
     * two should be given as <i>p</i>.
     *
     * @param p a path from <i>r</i>
     * @param q a path from <i>r</i>
     * @return whether the only intersect is <i>r</i>
     */
    static boolean singletonIntersect(final int[] p, final int[] q) {
        int n = p.length;
        for (int i = 1; i < n; i++)
            if (p[i] == q[i]) return false;
        return true;
    }

    /**
     * Join the two paths end on end and ignore the first vertex of the second
     * path. {0, 1, 2} and {0, 3, 4} becomes {0, 1, 2, 4, 3}.
     *
     * @param pathToY first path
     * @param pathToZ second path
     * @return the paths joined end on end and the last vertex truncated
     */
    static int[] join(int[] pathToY, int[] pathToZ) {
        int[] path = copyOf(pathToY, pathToY.length + pathToZ.length);
        int j = path.length - 1;
        for (int i = 0; i < pathToZ.length; i++) {
            path[j--] = pathToZ[i];
        }
        return path;
    }

    /**
     * Join the two paths end on end using 'y'. The first vertex of the second
     * path is truncated. {0, 1, 2}, {5} and {0, 3, 4} becomes {0, 1, 2, 5, 4,
     * 3}.
     *
     * @param pathToP first path
     * @param y       how to join the two paths
     * @param pathToQ second path
     * @return the paths joined end on end and the last vertex truncated
     */
    static int[] join(int[] pathToP, int y, int[] pathToQ) {
        int[] path = copyOf(pathToP, 1 + pathToQ.length + pathToQ.length);
        path[pathToP.length] = y;
        int j = path.length - 1;
        for (int i = 0; i < pathToQ.length; i++) {
            path[j--] = pathToQ[i];
        }
        return path;
    }

    /**
     * Compute the initial cycles of a biconnected graph.
     *
     * @param graph the biconnected graph
     * @return computed initial cycles
     * @throws NullPointerException the graph was null
     */
    static InitialCycles ofBiconnectedComponent(int[][] graph) {
        return ofBiconnectedComponent(graph, graph.length);
    }

    /**
     * Compute the initial cycles of a biconnected graph.
     *
     * @param graph the biconnected graph
     * @param limit maximum size of the cycle to find
     * @return computed initial cycles
     * @throws NullPointerException the graph was null
     */
    static InitialCycles ofBiconnectedComponent(int[][] graph, int limit) {
        return new InitialCycles(graph, limit, true);
    }

    /**
     * Abstract description of a cycle. Stores the path and computes the edge
     * vector representation.
     */
    static abstract class Cycle implements Comparable<Cycle> {

        private int[] path;
        ShortestPaths paths;
        BitSet        edgeVector;

        Cycle(final ShortestPaths paths, final int[] path) {
            this.path = path;
            this.paths = paths;
            this.edgeVector = edges(path); // XXX allows static Cycle
        }

        /**
         * Provides the edges of <i>path</i>, this method only exists so we can
         * refer to the class in a static context.
         *
         * @param path path of vertices
         * @return set of edges
         */
        abstract BitSet edges(int[] path);

        /**
         * Access the edge vector for this cycle.
         *
         * @return edge vector
         */
        BitSet edgeVector() {
            return edgeVector;
        }

        /**
         * Access the path of this cycle.
         *
         * @return the path of the cycle
         */
        int[] path() {
            return path;
        }

        /**
         * Reconstruct the entire cycle family (may be exponential).
         *
         * @return all cycles in this family.
         */
        abstract int[][] family();

        /**
         * The number of cycles in this prototypes family. This method be used
         * to avoid the potentially exponential reconstruction of all the cycles
         * using {@link #family()}.
         *
         * @return number of cycles
         */
        abstract int sizeOfFamily();

        /**
         * The length of the cycles (number of vertices in the path).
         *
         * @return cycle length
         */
        int length() {
            return path.length - 1; // first/last vertex repeats
        }

        @Override
        public int compareTo(Cycle that) {
            return Ints.lexicographicalComparator().compare(this.path, that.path);
        }
    }

    /**
     * An even cycle is formed from two shortest paths of the same length
     * and 'two' edges to a common vertex. The cycle formed by these is
     * even, 2n + 2 = even.
     * @see #compute()
     */
    class EvenCycle extends Cycle {

        int p, q, y;

        EvenCycle(ShortestPaths paths, int[] pathToP, int y, int[] pathToQ) {
            super(paths, join(pathToP, y, pathToQ));
            this.p = pathToP[pathToP.length - 1];
            this.q = pathToQ[pathToQ.length - 1];
            this.y = y;
        }

        /**{@inheritDoc} */
        @Override
        BitSet edges(int[] path) {
            return toEdgeVector(path);
        }

        /**{@inheritDoc} */
        @Override
        int[][] family() {

            int[][] pathsToP = paths.pathsTo(p);
            int[][] pathsToQ = paths.pathsTo(q);

            int[][] paths = new int[sizeOfFamily()][0];
            int i = 0;
            for (int[] pathToP : pathsToP) {
                for (int[] pathToQ : pathsToQ) {
                    paths[i++] = join(pathToP, y, pathToQ);
                }
            }
            return paths;
        }

        /**{@inheritDoc} */
        @Override
        int sizeOfFamily() {
            return paths.nPathsTo(p) * paths.nPathsTo(q);
        }
    }

    /**
     * An odd cycle is formed from two shortest paths of the same length
     * and 'one' edge to a common vertex. The cycle formed by these is odd,
     * 2n + 1 = odd.
     * @see #compute()
     */
    class OddCycle extends Cycle {

        int y, z;

        OddCycle(ShortestPaths paths, int[] pathToY, int[] pathToZ) {
            super(paths, join(pathToY, pathToZ));
            y = pathToY[pathToY.length - 1];
            z = pathToZ[pathToY.length - 1];
        }

        /**{@inheritDoc} */
        @Override
        BitSet edges(int[] path) {
            return toEdgeVector(path);
        }

        /**{@inheritDoc} */
        @Override
        int[][] family() {
            int[][] pathsToY = paths.pathsTo(y);
            int[][] pathsToZ = paths.pathsTo(z);

            int[][] paths = new int[sizeOfFamily()][0];
            int i = 0;
            for (int[] pathToY : pathsToY) {
                for (int[] pathToZ : pathsToZ) {
                    paths[i++] = join(pathToY, pathToZ);
                }
            }
            return paths;
        }

        /**{@inheritDoc} */
        @Override
        int sizeOfFamily() {
            return paths.nPathsTo(y) * paths.nPathsTo(z);
        }
    }

    /**
     * A simple value which acts as an immutable unordered tuple for two
     * primitive integers. This allows to index edges of a graph.
     */
    static final class Edge {

        private final int v, w;

        Edge(int v, int w) {
            this.v = v;
            this.w = w;
        }

        @Override
        public boolean equals(Object o) {
            Edge that = (Edge) o;
            return (this.v == that.v && this.w == that.w) || (this.v == that.w && this.w == that.v);
        }

        @Override
        public int hashCode() {
            return v ^ w;
        }

        @Override
        public String toString() {
            return "{" + v + ", " + w + "}";
        }
    }
}
