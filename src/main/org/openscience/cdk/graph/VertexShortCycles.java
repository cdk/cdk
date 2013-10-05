package org.openscience.cdk.graph;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Determine the set of cycles which are the shortest through each vertex. 
 * Unlike the Smallest Set of Smallest Rings (SSSR), linear dependence of 
 * each cycle does not need to be verified. 
 *
 * @author John May
 * @cdk.module core
 */
@TestClass("graph.VertexShortCyclesTest")
final class VertexShortCycles {

    /** Shortest cycles stored as closed walks. */
    private final List<int[]> paths;

    /** Construct the vertex short cycles for the
     *  given graph. */
    VertexShortCycles(int[][] graph) {
        this(new InitialCycles(graph));
    }

    /** Construct the vertex short cycles for the
     *  given initial cycles. */
    VertexShortCycles(InitialCycles initialCycles) {

        int[][] graph  = initialCycles.graph();
        int[]   sizeOf = new int[graph.length];

        this.paths = new ArrayList<int[]>(initialCycles.numberOfCycles());

        // cycles are returned ordered by length
        for (final InitialCycles.Cycle cycle : initialCycles.cycles()) {

            final int length = cycle.length();
            final int[] path = cycle.path();

            boolean found = false;

            // check if any vertex is the shortest through a vertex in the path
            for (final int v : path) {
                if (sizeOf[v] < 1 || length <= sizeOf[v]) {
                    found     = true;
                    sizeOf[v] = length;
                }
            }

            if (found) {
                for (int[] p : cycle.family()) {
                    paths.add(p);
                }
            }
        }
    }

    /**
     * The paths of the shortest cycles, that paths are closed walks such that
     * the last and first vertex is the same.
     * 
     * @return the paths
     */
    @TestMethod("paths_bicyclo,paths_napthalene,paths_anthracene," +
                        "paths_cyclophane_even")
    int[][] paths() {
        int[][] cpy = new int[paths.size()][0];
        for (int i = 0; i < paths.size(); i++) {
            int[] path = paths.get(i);
            cpy[i] = Arrays.copyOf(path, path.length);
        }
        return cpy;
    }

    /**
     * The size of the shortest cycles set.
     *
     * @return number of cycles
     */
    @TestMethod("size_bicyclo,size_napthalene,size_anthracene," +
                        "size_cyclophane_even")
    int size() {
        return paths.size();
    }
}
