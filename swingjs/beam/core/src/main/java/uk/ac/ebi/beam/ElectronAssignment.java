/*
 * Copyright (c) 2013, European Bioinformatics Institute (EMBL-EBI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */

package uk.ac.ebi.beam;

import java.util.BitSet;

/**
 * Verifies delocalised electrons can be assigned to a structure without
 * changing bond orders. To check and assign the electrons please use {@see
 * Localise} or {@see Graph#kekule}. Although faster than assigning a Kekulé
 * structure the method is the same and returning a structure with specified
 * bond orders is usually preferred.
 *
 * @author John May
 * @see Localise
 * @see uk.ac.ebi.beam.Graph#kekule()
 */
final class ElectronAssignment {

    private ElectronAssignment() {
    }

    /**
     * Check if it is possible to assign electrons to the subgraph (specified by
     * the set bits in of {@code bs}). Each connected subset is counted up and
     * checked for odd cardinality.
     *
     * @param g  graph
     * @param bs binary set indicated vertices for the subgraph
     * @return there is an odd cardinality subgraph
     */
    private static boolean containsOddCardinalitySubgraph(Graph g, BitSet bs) {

        // mark visited those which are not in any subgraph 
        boolean[] visited = new boolean[g.order()];
        for (int i = bs.nextClearBit(0); i < g.order(); i = bs.nextClearBit(i + 1))
            visited[i] = true;

        // from each unvisited vertices visit the connected vertices and count
        // how many there are in this component. if there is an odd number there
        // is no assignment of double bonds possible
        for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
            if (!visited[i] && isOdd(visit(g, i, 0, visited)))
                return true;
        }

        return false;
    }


    /**
     * Determine the size the connected component using a depth-first-search.
     *
     * @param g       graph
     * @param v       vertex
     * @param c       count
     * @param visited which vertices have been visited
     * @return size of the component from {@code v}
     */
    private static int visit(Graph g, int v, int c, boolean[] visited) {
        visited[v] = true;
        for (final Edge e : g.edges(v)) {
            int w = e.other(v);
            if (!visited[w] && e.bond().order() == 1)
                c = visit(g, w, c, visited);
        }
        return 1 + c;
    }

    /**
     * Test if an a number, {@code x} is odd.
     *
     * @param x a number
     * @return the number is odd
     */
    private static boolean isOdd(int x) {
        return (x & 0x1) == 1;
    }

    /**
     * Utility method to verify electrons can be assigned.
     *
     * @param g graph to check
     * @return electrons could be assigned to delocalised structure
     */
    static boolean verify(Graph g) {
        return g.getFlags(Graph.HAS_AROM) == 0 || !containsOddCardinalitySubgraph(g, Localise.buildSet(g, new BitSet()));
    }
}
