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
import java.util.BitSet;
import java.util.List;

/**
 * CyclicVertexSearch for graphs with more then 64 vertices.
 *
 * @author John May
 * @cdk.module core
 */
class JumboCyclicVertexSearch implements CyclicVertexSearch {

    /* graph representation */
    private final int[][] g;

    /* set of known cyclic vertices */
    private final BitSet  cyclic;

    /* cycle systems as they are discovered */
    private List<BitSet>  cycles = new ArrayList<BitSet>(1);

    /* indicates if the 'cycle' at 'i' in 'cycles' is fused */
    private List<Boolean> fused  = new ArrayList<Boolean>(1);

    /* set of visited vertices */
    private BitSet        visited;

    /* the vertices in our path at a given vertex index */
    private BitSet[]      state;

    /** vertex colored by each component. */
    private int[]         colors;

    /**
     * Create a new cyclic vertex search for the provided graph.
     *
     * @param graph adjacency list representation of a graph
     */
    JumboCyclicVertexSearch(int[][] graph) {
        this.g = graph;
        final int n = graph.length;

        cyclic = new BitSet(n);

        if (n == 0) return;

        state = new BitSet[n];
        visited = new BitSet(n);

        BitSet empty = new BitSet(n);

        // start from vertex 0
        search(0, copy(empty), copy(empty));

        // if g is a fragment we will not have visited everything
        int v = 0;
        while (visited.cardinality() != n) {
            v++;
            // each search expands to the whole fragment, as we
            // may have fragments we need to visit 0 and then
            // check every other vertex
            if (!visited.get(v)) {
                search(v, copy(empty), copy(empty));
            }
        }

        // allow the states to be collected
        state = null;
        visited = null;

    }

    /**
     * Perform a depth first search from the vertex <i>v</i>.
     *
     * @param v    vertex to search from
     * @param prev the state before we vistaed our parent (previous state)
     * @param curr the current state (including our parent)
     */
    private void search(int v, BitSet prev, BitSet curr) {

        state[v] = curr; // set the state before we visit v
        curr = copy(curr); // include v in our current state (state[v] is unmodified)
        curr.set(v);
        visited.or(curr); // mark v as visited (or being visited)

        // for each neighbor w of v
        for (int w : g[v]) {

            // if w is in our prev state we have a cycle of size >3.
            // we don't check out current state as this will always
            // include w - they are adjacent
            if (prev.get(w)) {
                // we have a cycle, xor the state when we last visited 'w'
                // with our current state. this set is all the vertices
                // we visited since then
                add(xor(state[w], curr));
            }

            // check w hasn't been visited or isn't being visited further up the stack.
            // this mainly stops us re-visiting the vertex we came from
            else if (!visited.get(w)) {
                // recursively call for the neighbor 'w'
                search(w, state[v], curr);
            }
        }

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
        for (BitSet cycle : cycles) {
            for (int i = cycle.nextSetBit(0); i >= 0; i = cycle.nextSetBit(i + 1)) {
                color[i] = color[i] < 0 ? n : 0;
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
        return cyclic.get(v);
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
            for (final BitSet cycle : cycles) {
                if (cycle.get(u) && cycle.get(v)) return true;
            }
            return false;
        }

        // vertex is not shared between components
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
     * Add the cycle vertices to our discovered cycles. The cycle is first
     * checked to see if it is isolated (shares at most one vertex) or
     * <i>potentially</i> fused.
     *
     * @param cycle newly discovered cyclic vertex set
     */
    private void add(BitSet cycle) {

        BitSet intersect = and(cycle, cyclic);

        if (intersect.cardinality() > 1) {
            addFused(cycle);
        } else {
            addIsolated(cycle);
        }

        cyclic.or(cycle);

    }

    /**
     * Add an a new isolated cycle which is currently edge disjoint with all
     * other cycles.
     *
     * @param cycle newly discovered cyclic vertices
     */
    private void addIsolated(BitSet cycle) {
        cycles.add(cycle);
        fused.add(false);
    }

    /**
     * Adds a <i>potentially</i> fused cycle. If the cycle is discovered not be
     * fused it will still be added as isolated.
     *
     * @param cycle vertex set of a potentially fused cycle, indicated by the
     *              set bits
     */
    private void addFused(BitSet cycle) {

        int i = indexOfFused(0, cycle);

        if (i != -1) {
            // add new cycle and mark as fused
            cycles.get(i).or(cycle);
            fused.set(i, true);
            int j = i;

            // merge other cycles we could be fused with into 'i'
            while ((j = indexOfFused(j + 1, cycle)) != -1) {
                cycles.get(i).or(cycles.remove(j));
                fused.remove(j);
                j--;
            }
        } else {
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
    private int indexOfFused(int start, BitSet cycle) {
        for (int i = start; i < cycles.size(); i++) {
            if (and(cycles.get(i), cycle).cardinality() > 1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Convert the set bits of a BitSet to an int[].
     *
     * @param set input with 0 or more set bits
     * @return the bits which are set in the input
     */
    static int[] toArray(BitSet set) {
        int[] vertices = new int[set.cardinality()];
        int i = 0;

        // fill the cyclic vertices with the bits that have been set
        for (int v = 0; i < vertices.length; v++) {
            if (set.get(v)) {
                vertices[i++] = v;
            }
        }

        return vertices;
    }

    /**
     * XOR the to bit sets together and return the result. Neither input is
     * modified.
     *
     * @param x first bit set
     * @param y second bit set
     * @return the XOR of the two bit sets
     */
    static BitSet xor(BitSet x, BitSet y) {
        BitSet z = copy(x);
        z.xor(y);
        return z;
    }

    /**
     * AND the to bit sets together and return the result. Neither input is
     * modified.
     *
     * @param x first bit set
     * @param y second bit set
     * @return the AND of the two bit sets
     */
    static BitSet and(BitSet x, BitSet y) {
        BitSet z = copy(x);
        z.and(y);
        return z;
    }

    /**
     * Copy the original bit set.
     *
     * @param org input bit set
     * @return copy of the input
     */
    static BitSet copy(BitSet org) {
        BitSet cpy = (BitSet) org.clone();
        return cpy;
    }

}
