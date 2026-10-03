/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 * You should have received index copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import org.openscience.cdk.smsd.global.TimeOut;
import org.openscience.cdk.smsd.tools.TimeManager;

/**
 * Finds maximum cliques connected by C-edges in a compatibility graph.
 * <p>Every pair in a clique must have a C-edge or D-edge, and C-edges must connect
 *  its vertices. Bron-Kerbosch enumerates maximal cliques in the combined graph;
 *  their C-edge components contain every maximum connected clique. Distinct
 *  vertex-ID sets remain separate results. This helper does not evaluate chemical
 *  predicates or mapping-level stereochemistry.
 * <p>Construction validates and copies graph indices, then performs the search.
 *  It resets the calling thread's {@link TimeOut} flag and captures its cutoff.
 *  On cooperative cancellation, stored cliques may be suboptimal or incomplete.
 *  The search is recursive and is not guaranteed to handle arbitrarily deep
 *  compatibility graphs. It is separate from the iterative VF-backed MCS engine.
 * <p>Result access returns mutable snapshots, including independent clique lists.
 *  Do not use this instance concurrently or recursively during construction.
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD is deprecated in CDK. See the separate
 *             <a href="https://github.com/asad/smsd">SMSD implementation</a>.
 */
@Deprecated
public class BKKCKCF {

    private final int[] nodeIds;
    private final int[][] neighbors;
    private final int[][] cNeighbors;
    private final List<List<Integer>> maxCliquesSet = new ArrayList<>();
    private final Set<List<Integer>> uniqueCliques = new HashSet<>();
    private int bestCliqueSize;
    private final TimeManager searchClock = new TimeManager();
    private final double searchTimeout;

    /**
     * Validate a compatibility graph and enumerate its largest C-connected cliques.
     * <p>Vertex IDs are arbitrary distinct integers. C-edges and D-edges are undirected
     *  endpoint pairs; a pair present in both is treated as a C-edge for connectivity.
     *  Source/target values in each node triple are integer payloads and are not used
     *  to enforce molecular index bounds or injectivity in this graph helper.
     *
     * @param compGraphNodes source/target/vertex-ID triples
     * @param cEdges vertex-ID pairs representing common compatible bonds
     * @param dEdges vertex-ID pairs allowing clique compatibility without a common bond
     * @throws NullPointerException if any input list is null
     * @throws IllegalArgumentException if triples/pairs are incomplete, entries are not
     *                                  integers, IDs repeat, or an edge has equal or unknown endpoints
     */
    protected BKKCKCF(List<Integer> compGraphNodes, List<Integer> cEdges, List<Integer> dEdges) {
        Objects.requireNonNull(compGraphNodes, "compatibility nodes");
        Objects.requireNonNull(cEdges, "C edges");
        Objects.requireNonNull(dEdges, "D edges");
        if (compGraphNodes.size() % 3 != 0) {
            throw new IllegalArgumentException("Compatibility nodes must contain triples");
        }
        searchTimeout = TimeOut.getInstance().getTimeOut();
        int size = compGraphNodes.size() / 3;
        nodeIds = new int[size];
        Map<Integer, Integer> indices = new HashMap<>();
        for (int i = 0; i < size; i++) {
            integerValue(compGraphNodes.get(i * 3));
            integerValue(compGraphNodes.get(i * 3 + 1));
            nodeIds[i] = integerValue(compGraphNodes.get(i * 3 + 2));
            if (indices.put(nodeIds[i], i) != null) {
                throw new IllegalArgumentException("Duplicate compatibility vertex: " + nodeIds[i]);
            }
        }
        cNeighbors = indexNeighbors(size, indices, cEdges, new ArrayList<>());
        neighbors = indexNeighbors(size, indices, cEdges, dEdges);
        BitSet candidates = new BitSet();
        candidates.set(0, size);
        // This is a new search; an earlier MCSPlus clock/flag must not cancel it.
        TimeOut.getInstance().setTimeOutFlag(false);
        enumerateCliques(new BitSet(), candidates, new BitSet());
    }

    private boolean hasTimedOut() {
        TimeOut timeout = TimeOut.getInstance();
        if (timeout.isTimeOutFlag()) return true;
        if (searchTimeout >= 0 && searchClock.getElapsedTimeInMinutes() > searchTimeout) {
            timeout.setTimeOutFlag(true);
            return true;
        }
        return false;
    }

    private static int integerValue(Object value) {
        if (!(value instanceof Integer)) throw new IllegalArgumentException("Graph indices must be integers");
        return (Integer) value;
    }

    // Store adjacency in O(V + E) space; bit sets are confined to search states.
    private int[][] indexNeighbors(int size, Map<Integer, Integer> indices,
                                   List<Integer> firstEdges, List<Integer> secondEdges) {
        int[] degrees = new int[size];
        countEdges(indices, firstEdges, degrees);
        countEdges(indices, secondEdges, degrees);
        int[][] adjacency = new int[size][];
        for (int i = 0; i < size; i++) adjacency[i] = new int[degrees[i]];
        int[] offsets = new int[size];
        addEdges(indices, firstEdges, adjacency, offsets);
        addEdges(indices, secondEdges, adjacency, offsets);
        return adjacency;
    }

    private void countEdges(Map<Integer, Integer> indices, List<Integer> edges, int[] degrees) {
        if (edges.size() % 2 != 0) throw new IllegalArgumentException("Edges must contain endpoint pairs");
        for (int i = 0; i < edges.size(); i += 2) {
            Integer first = indices.get(integerValue(edges.get(i)));
            Integer second = indices.get(integerValue(edges.get(i + 1)));
            if (first == null || second == null || first.equals(second)) {
                throw new IllegalArgumentException("Edge endpoints must be distinct known vertices");
            }
            degrees[first]++;
            degrees[second]++;
        }
    }

    private void addEdges(Map<Integer, Integer> indices, List<Integer> edges,
                          int[][] adjacency, int[] offsets) {
        for (int i = 0; i < edges.size(); i += 2) {
            int first = indices.get(edges.get(i));
            int second = indices.get(edges.get(i + 1));
            adjacency[first][offsets[first]++] = second;
            adjacency[second][offsets[second]++] = first;
        }
    }

    private BitSet intersect(BitSet vertices, int[] adjacent) {
        BitSet intersection = new BitSet();
        for (int vertex : adjacent) {
            if (vertices.get(vertex)) intersection.set(vertex);
        }
        return intersection;
    }

    private int pivot(BitSet candidates, BitSet excluded) {
        BitSet choices = (BitSet) candidates.clone();
        choices.or(excluded);
        int pivot = -1;
        int mostNeighbors = -1;
        for (int vertex = choices.nextSetBit(0); vertex >= 0; vertex = choices.nextSetBit(vertex + 1)) {
            int count = 0;
            for (int neighbor : neighbors[vertex]) {
                if (candidates.get(neighbor)) count++;
            }
            if (count > mostNeighbors) {
                mostNeighbors = count;
                pivot = vertex;
            }
        }
        return pivot;
    }

    private void enumerateCliques(BitSet clique, BitSet candidates, BitSet excluded) {
        if (hasTimedOut() || clique.cardinality() + candidates.cardinality() < bestCliqueSize) return;
        if (candidates.isEmpty()) {
            if (excluded.isEmpty()) saveConnectedComponents(clique);
            return;
        }
        BitSet branches = (BitSet) candidates.clone();
        int pivot = pivot(candidates, excluded);
        if (pivot >= 0) {
            for (int neighbor : neighbors[pivot]) branches.clear(neighbor);
        }
        for (int vertex = branches.nextSetBit(0); vertex >= 0; vertex = branches.nextSetBit(vertex + 1)) {
            if (hasTimedOut()) return;
            clique.set(vertex);
            enumerateCliques(clique, intersect(candidates, neighbors[vertex]),
                    intersect(excluded, neighbors[vertex]));
            clique.clear(vertex);
            candidates.clear(vertex);
            excluded.set(vertex);
        }
    }

    private void saveConnectedComponents(BitSet clique) {
        BitSet remaining = (BitSet) clique.clone();
        int[] pending = new int[clique.cardinality()];
        while (!remaining.isEmpty()) {
            BitSet component = new BitSet();
            int first = remaining.nextSetBit(0);
            remaining.clear(first);
            int count = 1;
            pending[0] = first;
            while (count > 0) {
                int vertex = pending[--count];
                component.set(vertex);
                for (int neighbor : cNeighbors[vertex]) {
                    if (remaining.get(neighbor)) {
                        remaining.clear(neighbor);
                        pending[count++] = neighbor;
                    }
                }
            }
            int size = component.cardinality();
            if (size < bestCliqueSize) continue;
            if (size > bestCliqueSize) {
                bestCliqueSize = size;
                maxCliquesSet.clear();
                uniqueCliques.clear();
            }
            List<Integer> mapping = new ArrayList<>(size);
            for (int vertex = component.nextSetBit(0); vertex >= 0; vertex = component.nextSetBit(vertex + 1)) {
                mapping.add(nodeIds[vertex]);
            }
            if (uniqueCliques.add(mapping)) maxCliquesSet.add(mapping);
        }
    }

    /**
     * Return the largest C-connected clique size found before cancellation.
     *
     * @return largest retained clique cardinality, or zero if none was found
     */
    protected int getBestCliqueSize() {
        return bestCliqueSize;
    }

    /**
     * Copy the retained largest C-connected clique vertex-ID sets.
     * <p>The returned stack and every inner list are independent mutable snapshots.
     *  Results may be suboptimal or incomplete on timeout, and iteration order is
     *  unspecified. An empty compatibility graph produces an empty stack.
     *
     * @return mutable stack of mutable vertex-ID list snapshots
     */
    protected Stack<List<Integer>> getMaxCliqueSet() {
        Stack<List<Integer>> solution = new Stack<>();
        for (List<Integer> clique : maxCliquesSet) solution.add(new ArrayList<>(clique));
        return solution;
    }
}
