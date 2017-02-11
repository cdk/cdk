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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.openscience.cdk.graph.InitialCycles.Cycle;

/**
 * Compute the relevant cycles (<i>C<sub>R</sub></i>) of a graph. A cycle is
 * relevant if it cannot be represented as the &oplus;-sum (xor) of strictly
 * shorter cycles {@cdk.cite Berger04}. This is the smallest set of short cycles
 * which is <i>uniquely</i> defined for a graph. The set can also be thought of
 * as the union of all minimum cycle bases. The set of cycles may be exponential
 * in number but can be checked (see {@link #size()}) before construction
 * {@cdk.cite Vismara97}.
 *
 * <blockquote><pre>{@code
 * // import static org.openscience.cdk.graph.GraphUtil.*;
 * IAtomContainer m = ...;
 *
 * // compute on the whole graph
 * RelevantCycles relevant = new RelevantCycles(toAdjList(m));
 *
 * // it is much faster to compute on the separate ring systems of the molecule
 * int[][]    graph      = toAdjList(m);
 * RingSearch ringSearch = new RingSearch(m, graph);
 *
 * // all isolated cycles are relevant
 * for (int[] isolated : ringSearch.isolated()){
 *     int[] path = cycle(graph, isolated);
 * }
 *
 * // compute the relevant cycles for each system
 * for (int[] fused : ringSearch.fused()){
 *
 *     int[][] subgraph = subgraph(graph, fused);
 *     RelevantCycles relevant = new RelevantCycles(subgraph);
 *
 *     for(int[] path : relevant.paths()){
 *         // convert the sub graph vertices back to the super graph indices
 *         for(int i = 0; i < path.length; i++) {
 *             path[i] = fused[path[i];
 *         }
 *     }
 * }
 * }
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module core
 * @cdk.keyword relevant cycles
 * @cdk.keyword relevant rings
 * @cdk.keyword R(G)
 * @cdk.keyword union of all minimum cycles bases
 * @cdk.keyword cycle
 * @cdk.keyword ring
 * @cdk.keyword ring perception
 * @cdk.githash
 * @see org.openscience.cdk.ringsearch.RingSearch
 * @see org.openscience.cdk.ringsearch.SSSRFinder#findRelevantRings()
 * @see GreedyBasis
 */
public final class RelevantCycles {

    /** The relevant cycle basis. */
    private final GreedyBasis basis;

    /**
     * Generate the relevant cycle basis for a graph.
     *
     * @param graph undirected adjacency list
     * @see org.openscience.cdk.ringsearch.RingSearch#fused()
     * @see GraphUtil#subgraph(int[][], int[])
     */
    public RelevantCycles(final int[][] graph) {
        this(new InitialCycles(graph));
    }

    /**
     * Generate the relevant cycle basis from a precomputed set of initial
     * cycles.
     *
     * @param initial set of initial cycles.
     * @throws NullPointerException null InitialCycles provided
     */
    RelevantCycles(final InitialCycles initial) {

        checkNotNull(initial, "No InitialCycles provided");

        this.basis = new GreedyBasis(initial.numberOfCycles(), initial.numberOfEdges());

        // processing by size add cycles which are independent of smaller cycles
        for (final int length : initial.lengths()) {
            basis.addAll(independent(initial.cyclesOfLength(length)));
        }
    }

    /**
     * Given a list of cycles return those which are independent (&oplus;-sum)
     * from the current basis.
     *
     * @param cycles cycles of a given length
     * @return cycles which were independent
     */
    private List<Cycle> independent(final Collection<Cycle> cycles) {
        final List<Cycle> independent = new ArrayList<Cycle>(cycles.size());
        for (final Cycle cycle : cycles) {
            if (basis.isIndependent(cycle)) independent.add(cycle);
        }
        return independent;
    }

    /**
     * Reconstruct the paths of all relevant cycles.
     *
     * <blockquote><pre>{@code
     * RelevantCycles relevant = ...
     *
     * // ensure the number is manageable
     * if(relevant.size() < 100){
     *   for(int[] path : relevant.paths()){
     *     // process the path
     *   }
     * }
     * }</pre></blockquote>
     *
     * @return array of vertex paths
     */
    public int[][] paths() {
        final int[][] paths = new int[size()][0];
        int i = 0;
        for (final Cycle c : basis.members()) {
            for (final int[] path : c.family())
                paths[i++] = path;
        }
        return paths;
    }

    /**
     * The number of the relevant cycles.
     *
     * @return size of relevant cycle set
     */
    public int size() {
        int size = 0;
        for (final Cycle c : basis.members())
            size += c.sizeOfFamily();
        return size;
    }
}
