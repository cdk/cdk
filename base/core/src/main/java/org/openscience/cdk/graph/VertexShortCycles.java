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
import java.util.List;

/**
 * Determine the set of cycles which are the shortest through each vertex.
 * Unlike the Smallest Set of Smallest Rings (SSSR), linear dependence of
 * each cycle does not need to be verified.
 *
 * @author John May
 * @cdk.module core
 */
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

        int[][] graph = initialCycles.graph();
        int[] sizeOf = new int[graph.length];

        this.paths = new ArrayList<int[]>(initialCycles.numberOfCycles());

        // cycles are returned ordered by length
        for (final InitialCycles.Cycle cycle : initialCycles.cycles()) {

            final int length = cycle.length();
            final int[] path = cycle.path();

            boolean found = false;

            // check if any vertex is the shortest through a vertex in the path
            for (final int v : path) {
                if (sizeOf[v] < 1 || length <= sizeOf[v]) {
                    found = true;
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
    int[][] paths() {
        int[][] paths = new int[this.paths.size()][0];
        for (int i = 0; i < this.paths.size(); i++)
            paths[i] = this.paths.get(i);
        return paths;
    }

    /**
     * The size of the shortest cycles set.
     *
     * @return number of cycles
     */
    int size() {
        return paths.size();
    }
}
