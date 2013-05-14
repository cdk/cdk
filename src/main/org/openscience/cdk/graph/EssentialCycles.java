package org.openscience.cdk.graph;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.openscience.cdk.graph.InitialCycles.Cycle;

/**
 * Determine the uniquely defined essential cycles of a graph. A cycle is
 * essential if it a member of all minimum cycle bases. If a graph has a single
 * minimum cycle basis (MCB) then all of its cycles are essential. Unlikely the
 * {@link RelevantCycles} the number of essential cycles is always polynomial
 * however may not be able generate the cycle space of a graph.
 *
 * @author John May
 * @cdk.module core
 * @cdk.keyword Essential Rings
 * @cdk.keyword Essential Cycles
 * @cdk.keyword Graph
 * @cdk.keyword Cycles
 * @cdk.keyword Rings
 * @see RelevantCycles
 * @see MinimumCycleBasis
 * @see org.openscience.cdk.ringsearch.SSSRFinder#findEssentialRings()
 * @see GraphUtil
 */
@TestClass("org.openscience.cdk.graph.EssentialCyclesTest")
public final class EssentialCycles {

    /** Cycles which are essential. */
    private final List<Cycle> essential;

    /** Initial cycles. */
    private final InitialCycles initial;

    /** An MCB extracted from the relevant cycles. */
    private final GreedyBasis basis;

    /**
     * Determine the essential cycles given a graph. Adjacency list
     * representation. For maximum performance the graph should be preprocessed
     * and run on separate biconnected components or fused cycles (see. {@link
     * org.openscience.cdk.ringsearch.RingSearch}.
     *
     * @param graph a molecule graph
     * @see GraphUtil#toAdjList(org.openscience.cdk.interfaces.IAtomContainer)
     * @see org.openscience.cdk.ringsearch.RingSearch
     */
    public EssentialCycles(final int[][] graph) {
        this(new InitialCycles(graph));
    }

    /**
     * Determine the essential cycles from a precomputed set of initial cycles.
     *
     * @param initial a molecule graph
     */
    EssentialCycles(final InitialCycles initial) {
        this(new RelevantCycles(initial), initial);
    }

    /**
     * Determine the essential cycles from a precomputed set of initial cycles
     * and relevant cycles.
     *
     * @param initial a molecule graph
     */
    EssentialCycles(final RelevantCycles relevant, final InitialCycles initial) {

        this.initial = initial;
        this.basis = new GreedyBasis(initial.numberOfCycles(),
                                     initial.numberOfEdges());
        this.essential = new ArrayList<Cycle>();

        // for each cycle added to the basis, if it can be
        // replaced with one of equal size it is non-essential
        for (final List<Cycle> cycles : groupByLength(relevant)) {
            for (final Cycle c : membersOfBasis(cycles)) {
                if (isEssential(c, cycles))
                    essential.add(c);
            }
        }
    }

    /**
     * The paths for each essential cycle.
     *
     * @return array of vertex paths
     */
    @TestMethod("paths_bicyclo,paths_napthalene,paths_anthracene," +
                        "paths_cyclophane_odd,paths_cyclophane_even")
    public int[][] paths() {
        final int[][] paths = new int[size()][];
        for (int i = 0; i < paths.length; i++)
            paths[i] = essential.get(i).path();
        return paths;
    }

    /**
     * Number of essential cycles.
     *
     * @return number of cycles
     */
    @TestMethod("size_bicyclo,size_napthalene,size_anthracene," +
                        "size_cyclophane_odd,size_cyclophane_even")
    public int size() {
        return essential.size();
    }

    /**
     * Reconstruct all relevant cycles and group then by length.
     *
     * @param relevant precomputed relevant cycles
     * @return all relevant cycles groped by weight
     */
    private List<List<Cycle>> groupByLength(final RelevantCycles relevant) {
        LinkedList<List<Cycle>> cyclesByLength = new LinkedList<List<Cycle>>();
        for (int[] path : relevant.paths()) {
            if (cyclesByLength.isEmpty()
                    || path.length > cyclesByLength.getLast().get(0)
                                                   .length()) {
                cyclesByLength.add(new ArrayList<Cycle>());
            }
            cyclesByLength.getLast().add(new MyCycle(path));
        }
        return cyclesByLength;
    }

    /**
     * For a list of equal length cycles return those which are members of the
     * minimum cycle basis.
     *
     * @param cycles cycles to add to the basis
     * @return cycles which were added to the basis
     */
    private List<Cycle> membersOfBasis(final List<Cycle> cycles) {
        int start = basis.size();
        for (final Cycle c : cycles) {
            if (basis.isIndependent(c))
                basis.add(c);
        }
        return basis.members().subList(start, basis.size());
    }

    /**
     * Determines whether the <i>cycle</i> is essential.
     *
     * @param candidate a cycle which is a member of the MCB
     * @param relevant  relevant cycles of the same length as <i>cycle</i>
     * @return whether the candidate is essential
     */
    private boolean isEssential(final Cycle candidate,
                                final Collection<Cycle> relevant) {

        // construct an alternative basis with all equal weight relevant cycles
        final List<Cycle> alternate
                = new ArrayList<Cycle>(relevant.size() + basis.size());

        final int weight = candidate.length();
        for (final Cycle cycle : basis.members()) {
            if (cycle.length() < weight)
                alternate.add(cycle);
        }
        for (final Cycle cycle : relevant) {
            if (!cycle.equals(candidate))
                alternate.add(cycle);
        }

        // if the alternate basis is smaller, the candidate is essential
        return BitMatrix.from(alternate).eliminate() < basis.size();
    }


    /**
     * Simple class for helping find the essential cycles from the relevant
     * cycles.
     */
    private class MyCycle extends Cycle {

        private MyCycle(int[] path) {
            super(null, path);
        }

        /** @inheritDoc */
        @Override BitSet edges(int[] path) {
            return initial.toEdgeVector(path);
        }

        /** @inheritDoc */
        @Override int[][] family() {
            return new int[][]{path()};
        }

        /** @inheritDoc */
        @Override int sizeOfFamily() {
            return 1;
        }

        /** @inheritDoc */
        public String toString() {
            return Arrays.toString(path());
        }
    }
}
