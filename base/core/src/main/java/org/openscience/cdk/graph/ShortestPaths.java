/*
 * Copyright (C) 2012 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.graph;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.Arrays;

/**
 * Find and reconstruct the shortest paths from a given start atom to any other
 * connected atom. The number of shortest paths ({@link #nPathsTo(int)}) and the
 * distance ({@link #distanceTo(int)}) can be accessed before reconstructing all
 * the paths. When no path is found (i.e. not-connected) an empty path is always
 * returned. <p/>
 *
 * <blockquote><pre>
 * IAtomContainer benzene = MoleculeFactory.makeBenzene();
 *
 * IAtom c1 = benzene.getAtom(0);
 * IAtom c4 = benzene.getAtom(3);
 *
 * // shortest paths from C1
 * ShortestPaths sp = new ShortestPaths(benzene, c1);
 *
 * // number of paths from C1 to C4
 * int nPaths = sp.nPathsTo(c4);
 *
 * // distance between C1 to C4
 * int distance = sp.distanceTo(c4);
 *
 * // reconstruct a path to the C4 - determined by storage order
 * int[] path = sp.pathTo(c4);
 *
 * // reconstruct all paths
 * int[][] paths = sp.pathsTo(c4);
 * int[] org = paths[0];  // paths[0] == path
 * int[] alt = paths[1];
 * </pre></blockquote>
 *
 * <p/> If shortest paths from multiple start atoms are required {@link
 * AllPairsShortestPaths} will have a small performance advantage. Please use
 * {@link org.openscience.cdk.graph.matrix.TopologicalMatrix} if only the
 * shortest distances between atoms is required.
 *
 * @author John May
 * @cdk.module core
 * @cdk.githash
 * @see AllPairsShortestPaths
 * @see org.openscience.cdk.graph.matrix.TopologicalMatrix
 */
public final class ShortestPaths {

    /* empty path when no valid path was found */
    private static final int[]   EMPTY_PATH  = new int[0];

    /* empty paths when no valid path was found */
    private static final int[][] EMPTY_PATHS = new int[0][];

    /* route to each vertex */
    private final Route[]        routeTo;

    /* distance to each vertex */
    private final int[]          distTo;

    /* number of paths to each vertex */
    private final int[]          nPathsTo;

    /* low order paths */
    private final boolean[]      precedes;

    private final int            start, limit;
    private final IAtomContainer container;

    /**
     * Create a new shortest paths tool for a single start atom. If shortest
     * paths from multiple start atoms are required {@link
     * AllPairsShortestPaths} will have a small performance advantage.
     *
     * @param container an atom container to find the paths of
     * @param start     the start atom to which all shortest paths will be
     *                  computed
     * @see AllPairsShortestPaths
     */
    public ShortestPaths(IAtomContainer container, IAtom start) {
        this(GraphUtil.toAdjList(container), container, container.getAtomNumber(start));
    }

    /**
     * Internal constructor for use by {@link AllPairsShortestPaths}. This
     * constructor allows the passing of adjacency list directly so the
     * representation does not need to be rebuilt for a different start atom.
     *
     * @param adjacent  adjacency list representation - built from {@link
     *                  GraphUtil#toAdjList(IAtomContainer)}
     * @param container container used to access atoms and their indices
     * @param start     the start atom index of the shortest paths
     */
    ShortestPaths(int[][] adjacent, IAtomContainer container, int start) {
        this(adjacent, container, start, null);
    }

    /**
     * Create a new shortest paths search for the given graph from the {@literal
     * start} vertex. The ordering for use by {@link #isPrecedingPathTo(int)}
     * can also be specified.
     *
     * @param adjacent  adjacency list representation - built from {@link
     *                  GraphUtil#toAdjList(IAtomContainer)}
     * @param container container used to access atoms and their indices
     * @param start     the start atom index of the shortest paths
     * @param ordering  vertex ordering for preceding path (null = don't use)
     */
    ShortestPaths(int[][] adjacent, IAtomContainer container, int start, int[] ordering) {
        this(adjacent, container, start, adjacent.length, ordering);
    }

    /**
     * Create a new shortest paths search for the given graph from the {@literal
     * start} vertex. The ordering for use by {@link #isPrecedingPathTo(int)}
     * can also be specified.
     *
     * @param adjacent  adjacency list representation - built from {@link
     *                  GraphUtil#toAdjList(IAtomContainer)}
     * @param container container used to access atoms and their indices
     * @param start     the start atom index of the shortest paths
     * @param limit     the maximum length path to find
     * @param ordering  vertex ordering for preceding path (null = don't use)
     */
    ShortestPaths(int[][] adjacent, IAtomContainer container, int start, int limit, int[] ordering) {

        int n = adjacent.length;

        this.container = container;
        this.start = start;
        this.limit = limit;

        this.distTo = new int[n];
        this.routeTo = new Route[n];
        this.nPathsTo = new int[n];
        this.precedes = new boolean[n];

        // skip computation for empty molecules
        if (n == 0) return;
        if (start == -1) throw new IllegalArgumentException("invalid vertex start - atom not found container");

        for (int i = 0; i < n; i++) {
            distTo[i] = Integer.MAX_VALUE;
        }

        // initialise source vertex
        distTo[start] = 0;
        routeTo[start] = new Source(start);
        nPathsTo[start] = 1;
        precedes[start] = true;

        if (ordering != null) {
            compute(adjacent, ordering);
        } else {
            compute(adjacent);
        }
    }

    /**
     * Perform a breath-first-search (BFS) from the start atom. The distanceTo[]
     * is updated on each iteration. The routeTo[] keeps track of our route back
     * to the source. The method has aspects similar to Dijkstra's shortest path
     * but we are working with vertices and thus our edges are unweighted and is
     * more similar to a simple BFS.
     */
    private void compute(final int[][] adjacent) {
        // queue is filled as we process each vertex
        int[] queue = new int[adjacent.length];
        queue[0] = start;
        int n = 1;

        for (int i = 0; i < n; i++) {

            int v = queue[i];
            int dist = distTo[v] + 1;
            for (int w : adjacent[v]) {

                if (dist > limit) continue;

                // distance is less then the current closest distance
                if (dist < distTo[w]) {
                    distTo[w] = dist;
                    routeTo[w] = new SequentialRoute(routeTo[v], w);
                    nPathsTo[w] = nPathsTo[v];
                    queue[n++] = w;
                } else if (distTo[w] == dist) {
                    routeTo[w] = new Branch(routeTo[w], new SequentialRoute(routeTo[v], w));
                    nPathsTo[w] += nPathsTo[v];
                }
            }
        }
    }

    /**
     * Perform a breath-first-search (BFS) from the start atom. The distanceTo[]
     * is updated on each iteration. The routeTo[] keeps track of our route back
     * to the source. The method has aspects similar to Dijkstra's shortest path
     * but we are working with vertices and thus our edges are unweighted and is
     * more similar to a simple BFS. The ordering limits the paths found to only
     * those in which all vertices precede the 'start' in the given ordering.
     * This ordering limits ensure we only generate paths in one direction.
     */
    private void compute(final int[][] adjacent, final int[] ordering) {

        // queue is filled as we process each vertex
        int[] queue = new int[adjacent.length];
        queue[0] = start;
        int n = 1;

        for (int i = 0; i < n; i++) {

            int v = queue[i];
            int dist = distTo[v] + 1;
            for (int w : adjacent[v]) {

                // distance is less then the current closest distance
                if (dist < distTo[w]) {
                    distTo[w] = dist;
                    routeTo[w] = new SequentialRoute(routeTo[v], w); // append w to the route to v
                    nPathsTo[w] = nPathsTo[v];
                    precedes[w] = precedes[v] && ordering[w] < ordering[start];
                    queue[n++] = w;
                } else if (distTo[w] == dist) {
                    // shuffle paths around depending on whether there is
                    // already a preceding path
                    if (precedes[v] && ordering[w] < ordering[start]) {
                        if (precedes[w]) {
                            routeTo[w] = new Branch(routeTo[w], new SequentialRoute(routeTo[v], w));
                            nPathsTo[w] += nPathsTo[v];
                        } else {
                            precedes[w] = true;
                            routeTo[w] = new SequentialRoute(routeTo[v], w);
                        }
                    }
                }
            }
        }

    }

    /**
     * Reconstruct a shortest path to the provided <i>end</i> vertex. The path
     * is an inclusive fixed size array of vertex indices. If there are multiple
     * shortest paths the first shortest path is determined by vertex storage
     * order. When there is no path an empty array is returned. It is considered
     * there to be no path if the end vertex belongs to the same container but
     * is a member of a different fragment, or the vertex is not present in the
     * container at all.
     *
     * <pre>
     * ShortestPaths sp = ...;
     *
     * // reconstruct first path
     * int[] path = sp.pathTo(5);
     *
     * // check there is only one path
     * if(sp.nPathsTo(5) == 1){
     *     int[] path = sp.pathTo(5); // reconstruct the path
     * }
     * </pre>
     *
     * @param end the <i>end</i> vertex to find a path to
     * @return path from the <i>start</i> to the <i>end</i> vertex
     * @see #pathTo(org.openscience.cdk.interfaces.IAtom)
     * @see #atomsTo(int)
     * @see #atomsTo(org.openscience.cdk.interfaces.IAtom)
     */
    public int[] pathTo(int end) {

        if (end < 0 || end >= routeTo.length) return EMPTY_PATH;

        return routeTo[end] != null ? routeTo[end].toPath(distTo[end] + 1) : EMPTY_PATH;
    }

    /**
     * Reconstruct a shortest path to the provided <i>end</i> atom. The path is
     * an inclusive fixed size array of vertex indices. If there are multiple
     * shortest paths the first shortest path is determined by vertex storage
     * order. When there is no path an empty array is returned. It is considered
     * there to be no path if the end atom belongs to the same container but is
     * a member of a different fragment, or the atom is not present in the
     * container at all.<p/>
     *
     * <pre>
     * ShortestPaths sp   = ...;
     * IAtom         end  = ...;
     *
     * // reconstruct first path
     * int[] path = sp.pathTo(end);
     *
     * // check there is only one path
     * if(sp.nPathsTo(end) == 1){
     *     int[] path = sp.pathTo(end); // reconstruct the path
     * }
     * </pre>
     *
     * @param end the <i>end</i> vertex to find a path to
     * @return path from the <i>start</i> to the <i>end</i> vertex
     * @see #atomsTo(org.openscience.cdk.interfaces.IAtom)
     * @see #atomsTo(int)
     * @see #pathTo(int)
     */
    public int[] pathTo(IAtom end) {
        return pathTo(container.getAtomNumber(end));
    }

    /**
     * Returns whether the first shortest path from the <i>start</i> to a given
     * <i>end</i> vertex which only passed through vertices smaller then
     * <i>start</i>. This is useful for reducing the search space, the idea is
     * used by {@cdk.cite Vismara97} in the computation of cycle prototypes.
     *
     * @param end the end vertex
     * @return whether the path to the <i>end</i> only passed through vertices
     *         preceding the <i>start</i>
     */
    public boolean isPrecedingPathTo(int end) {
        return (end >= 0 || end < routeTo.length) && precedes[end];
    }

    /**
     * Reconstruct all shortest paths to the provided <i>end</i> vertex. The
     * paths are <i>n</i> (where n is {@link #nPathsTo(int)}) inclusive fixed
     * size arrays of vertex indices. When there is no path an empty array is
     * returned. It is considered there to be no path if the end vertex belongs
     * to the same container but is a member of a different fragment, or the
     * vertex is not present in the container at all.<p/>
     *
     * <b>Important:</b> for every possible branch the number of possible paths
     * doubles and could be in the order of tens of thousands. Although the
     * chance of finding such a molecule is highly unlikely (C720 fullerene has
     * at maximum 1024 paths). It is safer to check the number of paths ({@link
     * #nPathsTo(int)}) before attempting to reconstruct all shortest paths.
     *
     * <pre>
     * int           threshold = 20;
     * ShortestPaths sp        = ...;
     *
     * // reconstruct shortest paths
     * int[][] paths = sp.pathsTo(5);
     *
     * // only reconstruct shortest paths below a threshold
     * if(sp.nPathsTo(5) < threshold){
     *     int[][] path = sp.pathsTo(5); // reconstruct shortest paths
     * }
     * </pre>
     *
     * @param end the end vertex
     * @return all shortest paths from the start to the end vertex
     */
    public int[][] pathsTo(int end) {

        if (end < 0 || end >= routeTo.length) return EMPTY_PATHS;

        return routeTo[end] != null ? routeTo[end].toPaths(distTo[end] + 1) : EMPTY_PATHS;
    }

    /**
     * Reconstruct all shortest paths to the provided <i>end</i> vertex. The
     * paths are <i>n</i> (where n is {@link #nPathsTo(int)}) inclusive fixed
     * size arrays of vertex indices. When there is no path an empty array is
     * returned. It is considered there to be no path if the end vertex belongs
     * to the same container but is a member of a different fragment, or the
     * vertex is not present in the container at all. <p/>
     *
     * <b>Important:</b> for every possible branch the number of possible paths
     * doubles and could be in the order of tens of thousands. Although the
     * chance of finding such a molecule is highly unlikely (C720 fullerene has
     * at maximum 1024 paths). It is safer to check the number of paths ({@link
     * #nPathsTo(int)}) before attempting to reconstruct all shortest paths.
     *
     * <pre>
     * int           threshold = 20;
     * ShortestPaths sp        = ...;
     * IAtom         end       = ...;
     *
     * // reconstruct all shortest paths
     * int[][] paths = sp.pathsTo(end);
     *
     * // only reconstruct shortest paths below a threshold
     * if(sp.nPathsTo(end) < threshold){
     *     int[][] path = sp.pathsTo(end); // reconstruct shortest paths
     * }
     * </pre>
     *
     * @param end the end atom
     * @return all shortest paths from the start to the end vertex
     */
    public int[][] pathsTo(IAtom end) {
        return pathsTo(container.getAtomNumber(end));
    }

    /**
     * Reconstruct a shortest path to the provided <i>end</i> vertex. The path
     * is an inclusive fixed size array {@link IAtom}s. If there are multiple
     * shortest paths the first shortest path is determined by vertex storage
     * order. When there is no path an empty array is returned. It is considered
     * there to be no path if the end vertex belongs to the same container but
     * is a member of a different fragment, or the vertex is not present in the
     * container at all.
     *
     * <pre>
     * ShortestPaths sp = ...;
     *
     * // reconstruct a shortest path
     * IAtom[] path = sp.atomsTo(5);
     *
     * // ensure single shortest path
     * if(sp.nPathsTo(5) == 1){
     *     IAtom[] path = sp.atomsTo(5); // reconstruct shortest path
     * }
     * </pre>
     *
     * @param end the <i>end</i> vertex to find a path to
     * @return path from the <i>start</i> to the <i>end</i> atoms as fixed size
     *         array of {@link org.openscience.cdk.interfaces.IAtom}s
     * @see #atomsTo(int)
     * @see #pathTo(int)
     * @see #pathTo(org.openscience.cdk.interfaces.IAtom)
     */
    public IAtom[] atomsTo(int end) {

        int[] path = pathTo(end);
        IAtom[] atoms = new IAtom[path.length];

        // copy the atoms from the path indices to the array of atoms
        for (int i = 0, n = path.length; i < n; i++)
            atoms[i] = container.getAtom(path[i]);

        return atoms;

    }

    /**
     * Reconstruct a shortest path to the provided <i>end</i> atom. The path is
     * an inclusive fixed size array {@link IAtom}s. If there are multiple
     * shortest paths the first shortest path is determined by vertex storage
     * order. When there is no path an empty array is returned. It is considered
     * there to be no path if the end atom belongs to the same container but is
     * a member of a different fragment, or the atom is not present in the
     * container at all.
     *
     *
     * <pre>
     * ShortestPaths sp   = ...;
     * IAtom         end  = ...;
     *
     * // reconstruct a shortest path
     * IAtom[] path = sp.atomsTo(end);
     *
     * // ensure single shortest path
     * if(sp.nPathsTo(end) == 1){
     *     IAtom[] path = sp.atomsTo(end); // reconstruct shortest path
     * }
     * </pre>
     *
     * @param end the <i>end</i> atom to find a path to
     * @return path from the <i>start</i> to the <i>end</i> atoms as fixed size
     *         array of {@link org.openscience.cdk.interfaces.IAtom}s.
     * @see #atomsTo(int)
     * @see #pathTo(int)
     * @see #pathTo(org.openscience.cdk.interfaces.IAtom)
     */
    public IAtom[] atomsTo(IAtom end) {
        return atomsTo(container.getAtomNumber(end));
    }

    /**
     * Access the number of possible paths to the <i>end</i> vertex. When there
     * is no path 0 is returned. It is considered there to be no path if the end
     * vertex belongs to the same container but is a member of a different
     * fragment, or the vertex is not present in the container at all.<p/>
     *
     * <pre>
     * ShortestPaths sp   = ...;
     *
     * sp.nPathsTo(5); // number of paths
     *
     * sp.nPathsTo(-1); // returns 0 - there are no paths
     * </pre>
     *
     * @param end the <i>end</i> vertex to which the number of paths will be
     *            returned
     * @return the number of paths to the end vertex
     */
    public int nPathsTo(int end) {
        return (end < 0 || end >= nPathsTo.length) ? 0 : nPathsTo[end];
    }

    /**
     * Access the number of possible paths to the <i>end</i> atom. When there is
     * no path 0 is returned. It is considered there to be no path if the end
     * atom belongs to the same container but is a member of a different
     * fragment, or the atom is not present in the container at all.<p/>
     *
     * <pre>
     * ShortestPaths sp   = ...;
     * IAtom         end  = ...l
     *
     * sp.nPathsTo(end); // number of paths
     *
     * sp.nPathsTo(null);           // returns 0 - there are no paths
     * sp.nPathsTo(new Atom("C"));  // returns 0 - there are no paths
     * </pre>
     *
     * @param end the <i>end</i> vertex to which the number of paths will be
     *            returned
     * @return the number of paths to the end vertex
     */
    public int nPathsTo(IAtom end) {
        return nPathsTo(container.getAtomNumber(end));
    }

    /**
     * Access the distance to the provided <i>end</i> vertex. If the two are not
     * connected the distance is returned as {@link Integer#MAX_VALUE}.
     * Formally, there is a path if the distance is less then the number of
     * vertices.
     *
     * <pre>
     * IAtomContainer container = ...;
     * ShortestPaths  sp        = ...; // start = 0
     *
     * int n = container.getAtomCount();
     *
     * if(sp.distanceTo(5) < n) {
     *     // these is a path from 0 to 5
     * }
     * </pre>
     *
     * Conveniently the distance is also the index of the last vertex in the
     * path.
     *
     * <pre>
     * IAtomContainer container = ...;
     * ShortestPaths  sp        = ...;  // start = 0
     *
     * int path = sp.pathTo(5);
     *
     * int start = path[0];
     * int end   = path[sp.distanceTo(5)];
     *
     * </pre>
     *
     * @param end vertex to measure the distance to
     * @return distance to this vertex
     * @see #distanceTo(org.openscience.cdk.interfaces.IAtom)
     */
    public int distanceTo(int end) {
        return (end < 0 || end >= nPathsTo.length) ? Integer.MAX_VALUE : distTo[end];
    }

    /**
     * Access the distance to the provided <i>end</i> atom. If the two are not
     * connected the distance is returned as {@link Integer#MAX_VALUE}.
     * Formally, there is a path if the distance is less then the number of
     * atoms.
     *
     * <pre>
     * IAtomContainer container = ...;
     * ShortestPaths  sp        = ...; // start atom
     * IAtom          end       = ...;
     *
     * int n = container.getAtomCount();
     *
     * if( sp.distanceTo(end) < n ) {
     *     // these is a path from start to end
     * }
     *
     * </pre>
     *
     * Conveniently the distance is also the index of the last vertex in the
     * path.
     *
     * <pre>
     * IAtomContainer container = ...;
     * ShortestPaths  sp        = ...; // start atom
     * IAtom          end       = ...;
     *
     * int atoms = sp.atomsTo(end);
     * // end == atoms[sp.distanceTo(end)];
     *
     * </pre>
     *
     * @param end atom to measure the distance to
     * @return distance to the given atom
     * @see #distanceTo(int)
     */
    public int distanceTo(IAtom end) {
        return distanceTo(container.getAtomNumber(end));
    }

    /** Helper class for building a route to the shortest path */
    private static interface Route {

        /**
         * Recursively convert this route to all possible shortest paths. The length
         * is passed down the methods until the source is reached and the first path
         * created
         *
         * @param n length of the path
         * @return 2D array of all shortest paths
         */
        int[][] toPaths(int n);

        /**
         * Recursively convert this route to the first shortest path. The length is
         * passed down the methods until the source is reached and the first path
         * created
         *
         * @param n length of the path
         * @return first shortest path
         */
        int[] toPath(int n);
    }

    /** The source of a route, the source is always the start atom. */
    private static class Source implements Route {

        private final int v;

        /**
         * Create new source with a given vertex.
         *
         * @param v start vertex
         */
        public Source(int v) {
            this.v = v;
        }

        /** @inheritDoc */
        @Override
        public int[][] toPaths(int n) {
            // only every one shortest path at source
            return new int[][]{toPath(n)};
        }

        /** @inheritDoc */
        @Override
        public int[] toPath(int n) {
            // create the path of the given length
            // and set the vertex at the first index
            int[] path = new int[n];
            path[0] = v;
            return path;
        }

    }

    /** A sequential route is vertex appended to a parent route. */
    private class SequentialRoute implements Route {

        private final int   v;
        private final Route parent;

        /**
         * Create a new sequential route from the parent and include the new vertex
         * <i>v</i>.
         *
         * @param parent parent route
         * @param v      additional vertex
         */
        private SequentialRoute(Route parent, int v) {
            this.v = v;
            this.parent = parent;
        }

        /** @inheritDoc */
        @Override
        public int[][] toPaths(int n) {

            int[][] paths = parent.toPaths(n);
            int i = distTo[v];

            // for all paths from the parent set the vertex at the given index
            for (int[] path : paths)
                path[i] = v;

            return paths;

        }

        /** @inheritDoc */
        @Override
        public int[] toPath(int n) {
            int[] path = parent.toPath(n);
            // for all paths from the parent set vertex at the correct index (given by distance)
            path[distTo[v]] = v;
            return path;
        }

    }

    /**
     * A more complex route which represents a branch in our path. A branch is
     * composed of a left and a right route. A n-way branches can be constructed by
     * simply nesting a branch within a branch.
     */
    private class Branch implements Route {

        private final Route left, right;

        /**
         * Create a branch with a left and right
         *
         * @param left  route to the left
         * @param right route to the right
         */
        private Branch(Route left, Route right) {
            this.left = left;
            this.right = right;
        }

        /** @inheritDoc */
        @Override
        public int[][] toPaths(int n) {

            // get all shortest paths from the left and right
            int[][] leftPaths = left.toPaths(n);
            int[][] rightPaths = right.toPaths(n);

            // expand the left paths to a capacity which can also accommodate the right paths
            int[][] paths = Arrays.copyOf(leftPaths, leftPaths.length + rightPaths.length);

            // copy the right paths in to the expanded left paths
            System.arraycopy(rightPaths, 0, paths, leftPaths.length, rightPaths.length);

            return paths;
        }

        /** @inheritDoc */
        @Override
        public int[] toPath(int n) {
            // use the left as the first path
            return left.toPath(n);
        }

    }
}
