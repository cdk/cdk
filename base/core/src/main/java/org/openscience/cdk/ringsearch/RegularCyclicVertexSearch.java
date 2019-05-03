/*
 * Copyright (C) 2012 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version. All we ask is that proper credit is given for our
 * work, which includes - but is not limited to - adding the above copyright
 * notice to the beginning of your source code files, and to any copyright
 * notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.ringsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CyclicVertexSearch for graphs with 64 vertices or less. This search is
 * optimised using primitive {@literal long} values to represent vertex sets.
 *
 * @author John May
 * @cdk.module core
 */
class RegularCyclicVertexSearch implements CyclicVertexSearch {

    /* graph representation */
    private final int[][]  g;

    /* set of known cyclic vertices */
    private long           cyclic;

    /* cycle systems as they are discovered */
    private List<Long>     cycles = new ArrayList<Long>(1);

    /* indicates if the 'cycle' at 'i' in 'cycles' is fused */
    private List<Boolean>  fused  = new ArrayList<Boolean>(1);

    /* set of visited vertices */
    private long           visited;

    /* the vertices in our path at a given vertex index */
    private long[]         state;

    /** Vertex colors - which component does each vertex belong. */
    private volatile int[] colors;

    private int numCycles = 0;

    /**
     * Create a new cyclic vertex search for the provided graph.
     *
     * @param graph adjacency list representation of a graph
     */
    RegularCyclicVertexSearch(int[][] graph) {

        this.g = graph;
        final int n = graph.length;

        // skip search if empty graph
        if (n == 0) return;

        state = new long[n];

        // start from vertex 0
        search(0, 0L, 0L);

        // if disconnected we have not visited all vertices
        int v = 1;
        while (Long.bitCount(visited) != n) {

            // haven't visited v, start a new search from there
            if (!visited(v)) {
                search(v, 0L, 0L);
            }
            v++;
        }

        // no longer needed for the lifetime of the object
        state = null;

    }

    /**
     * Perform a depth first search from the vertex <i>v</i>.
     *
     * @param v    vertex to search from
     * @param prev the state before we vistaed our parent (previous state)
     * @param curr the current state (including our parent)
     */
    private void search(int v, long prev, long curr) {

        state[v] = curr; // store the state before we visited v
        curr = setBit(curr, v); // include v in our current state (state[v] is unmodified)
        visited |= curr; // mark v as visited (or being visited)

        // neighbors of v
        for (int w : g[v]) {

            // w has been visited or is partially visited further up stack
            if (visited(w)) {

                // if w is in our prev state we have a cycle of size >2.
                // we don't check out current state as this will always
                // include w - they are adjacent
                if (isBitSet(prev, w)) {
                    numCycles++;

                    // xor the state when we last visited 'w' with our current
                    // state. this set is all the vertices we visited since then
                    // and are all in a cycle
                    add(state[w] ^ curr);
                }
            } else {
                // recursively call for the unvisited neighbor w
                search(w, state[v], curr);
            }
        }
    }

    @Override
    public int numCycles() {
        return numCycles;
    }

    /**
     * Returns whether the vertex 'v' has been visited.
     *
     * @param v a vertex
     * @return whether the vertex has been visited
     */
    private boolean visited(int v) {
        return isBitSet(visited, v);
    }

    /**
     * Add the cycle vertices to our discovered cycles. The cycle is first
     * checked to see if it is isolated (shares at most one vertex) or
     * <i>potentially</i> fused.
     *
     * @param cycle newly discovered cyclic vertex set
     */
    private void add(long cycle) {

        long intersect = cyclic & cycle;

        // intersect by more then 1 vertex, we 'may' have a fused cycle
        if (intersect != 0 && Long.bitCount(intersect) > 1) {
            addFused(cycle);
        } else {
            addIsolated(cycle);
        }

        cyclic |= cycle;

    }

    /**
     * Add an a new isolated cycle which is currently edge disjoint with all
     * other cycles.
     *
     * @param cycle newly discovered cyclic vertices
     */
    private void addIsolated(long cycle) {
        cycles.add(cycle);
        fused.add(Boolean.FALSE);
    }

    /**
     * Adds a <i>potentially</i> fused cycle. If the cycle is discovered not be
     * fused it will still be added as isolated.
     *
     * @param cycle vertex set of a potentially fused cycle, indicated by the
     *              set bits
     */
    private void addFused(long cycle) {

        // find index of first fused cycle
        int i = indexOfFused(0, cycle);

        if (i != -1) {

            // include the new cycle vertices and mark as fused
            cycles.set(i, cycle | cycles.get(i));
            fused.set(i, Boolean.TRUE);

            // merge other cycles we are share an edge with
            int j = i;
            while ((j = indexOfFused(j + 1, cycles.get(i))) != -1) {
                cycles.set(i, cycles.remove(j) | cycles.get(i));
                fused.remove(j);
                j--; // removed a vertex, need to move back one
            }
        } else {
            // edge disjoint
            addIsolated(cycle);
        }
    }

    /**
     * Find the next index that the <i>cycle</i> intersects with by at least two
     * vertices. If the intersect of a vertex set with another contains more
     * then two vertices it cannot be edge disjoint.
     *
     * @param start start searching from here
     * @param cycle test whether any current cycles are fused with this one
     * @return the index of the first fused after 'start', -1 if none
     */
    private int indexOfFused(int start, long cycle) {
        for (int i = start; i < cycles.size(); i++) {
            long intersect = cycles.get(i) & cycle;
            if (intersect != 0 && Long.bitCount(intersect) > 1) {
                return i;
            }
        }
        return -1;
    }

    /** Synchronisation lock. */
    private final Object lock = new Object();

    /**
     * Lazily build an indexed lookup of vertex color. The vertex color
     * indicates which cycle a given vertex belongs. If a vertex belongs to more
     * then one cycle it is colored '0'. If a vertex belongs to no cycle it is
     * colored '-1'.
     *
     * @return vertex colors
     */
    @Override
    public int[] vertexColor() {
        int[] result = colors;
        if (result == null) {
            synchronized (this) {
                result = colors;
                if (result == null) {
                    colors = result = buildVertexColor();
                }
            }
        }
        return result;
    }

    /**
     * Build an indexed lookup of vertex color. The vertex color indicates which
     * cycle a given vertex belongs. If a vertex belongs to more then one cycle
     * it is colored '0'. If a vertex belongs to no cycle it is colored '-1'.
     *
     * @return vertex colors
     */
    private int[] buildVertexColor() {
        int[] color = new int[g.length];

        int n = 1;
        Arrays.fill(color, -1);
        for (long cycle : cycles) {
            for (int i = 0; i < g.length; i++) {
                if ((cycle & 0x1) == 0x1) color[i] = color[i] < 0 ? n : 0;
                cycle >>= 1;
            }
            n++;
        }

        return color;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean cyclic(int v) {
        return isBitSet(cyclic, v);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean cyclic(int u, int v) {

        final int[] colors = vertexColor();

        // if either vertex has no color then the edge can not
        // be cyclic
        if (colors[u] < 0 || colors[v] < 0) return false;

        // if the vertex color is 0 it is shared between
        // two components (i.e. spiro-rings) we need to
        // check each component
        if (colors[u] == 0 || colors[v] == 0) {
            // either vertices are shared - need to do the expensive check
            for (final long cycle : cycles) {
                if (isBitSet(cycle, u) && isBitSet(cycle, v)) {
                    return true;
                }
            }
            return false;
        }

        // vertex is not shared between components check the colors match (i.e.
        // in same component)
        return colors[u] == colors[v];
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int[] cyclic() {
        return toArray(cyclic);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int[][] isolated() {
        List<int[]> isolated = new ArrayList<int[]>(cycles.size());
        for (int i = 0; i < cycles.size(); i++) {
            if (!fused.get(i)) isolated.add(toArray(cycles.get(i)));
        }
        return isolated.toArray(new int[isolated.size()][]);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int[][] fused() {
        List<int[]> fused = new ArrayList<int[]>(cycles.size());
        for (int i = 0; i < cycles.size(); i++) {
            if (this.fused.get(i)) fused.add(toArray(cycles.get(i)));
        }
        return fused.toArray(new int[fused.size()][]);
    }

    /**
     * Convert the bits of a {@code long} to an array of integers. The size of
     * the output array is the number of bits set in the value.
     *
     * @param set value to convert
     * @return array of the set bits in the long value
     */
    static int[] toArray(long set) {

        int[] vertices = new int[Long.bitCount(set)];
        int i = 0;

        // fill the cyclic vertices with the bits that have been set
        for (int v = 0; i < vertices.length; v++) {
            if (isBitSet(set, v)) vertices[i++] = v;
        }

        return vertices;
    }

    /**
     * Determine if the specified bit on the value is set.
     *
     * @param value bits indicate that vertex is in the set
     * @param bit   bit to test
     * @return whether the specified bit is set
     */
    static boolean isBitSet(long value, int bit) {
        return (value & 1L << bit) != 0;
    }

    /**
     * Set the specified bit on the value and return the modified value.
     *
     * @param value the value to set the bit on
     * @param bit   the bit to set
     * @return modified value
     */
    static long setBit(long value, int bit) {
        return value | 1L << bit;
    }

}
