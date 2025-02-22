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

/**
 * Convert a chemical graph with implicit edge labels to one with explicit
 * single or aromatic edge labels.
 *
 * @author John May
 */
final class ImplicitToExplicit extends AbstractFunction<Graph,Graph> {

    /**
     * Transform all implicit to explicit bonds. The original graph is
     * unmodified
     *
     * @param g a chemical graph
     * @return new chemical graph but with all explicit bonds
     */
    public Graph apply(final Graph g) {

        Graph h = new Graph(g.order());

        // copy atom/topology information
        for (int u = 0; u < g.order(); u++) {
            h.addAtom(g.atom(u));
            h.addTopology(g.topologyOf(u));
        }

        // apply edges
        for (int u = 0; u < g.order(); u++) {
            for (final Edge e : g.edges(u)) {
                if (e.other(u) > u)
                    h.addEdge(toExplicitEdge(g, e));
            }
        }

        return h;
    }

    /**
     * Given a chemical graph and an edge in that graph, return the explicit
     * form of that edge. Neither the graph or the edge is modified, if the edge
     * is already explicit then 'e' is returned.
     *
     * @param g chemical graph
     * @param e an edge of g
     * @return the edge with specified explicit bond type
     */
    static Edge toExplicitEdge(final Graph g, final Edge e) {
        final int u = e.either(), v = e.other(u);
        if (e.bond() == Bond.IMPLICIT) {
            return new Edge(u, v,
                            type(g.atom(u),
                                 g.atom(v)));
        }
        return e;
    }

    /**
     * Given two atoms which are implicitly connected determine the explicit
     * bond type. The type is 'aromatic' if both atoms are aromatic, if either
     * or both atoms are non-aromatic then the bond type is 'single'.
     *
     * @param u an atom
     * @param v another atom (connected to u)
     * @return the bond type
     */
    static Bond type(Atom u, Atom v) {
        return u.aromatic() && v.aromatic() ? Bond.AROMATIC : Bond.SINGLE;
    }
}
