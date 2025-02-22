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
 * Convert a chemical graph with explicit  single or aromatic edge labels to one
 * with implicit edge labels.
 *
 * @author John May
 */
final class ExplicitToImplicit
        extends AbstractFunction<Graph, Graph> {

    /**
     * Transform all explicit to implicit bonds. The original graph is
     * unmodified.
     *
     * @param g a chemical graph
     * @return new chemical graph but with all explicit bonds
     */
    public Graph apply(final Graph g) {

        Graph h = new Graph(g.order());

        // atom/topology information doesn't change
        for (int u = 0; u < g.order(); u++) {
            h.addAtom(g.atom(u));
            h.addTopology(g.topologyOf(u));
        }

        // transform edges
        for (int u = 0; u < g.order(); u++) {
            for (final Edge e : g.edges(u)) {
                if (e.other(u) > u)
                    h.addEdge(toImplicitEdge(g, e));
            }
        }

        return h;
    }

    /**
     * Given a chemical graph and an edge in that graph, return the implicit
     * form of that edge. Neither the graph or the edge is modified, if the edge
     * is already explicit then 'e' is returned.
     *
     * @param g chemical graph
     * @param e an edge of g
     * @return the edge with specified explicit bond type
     */
    static Edge toImplicitEdge(final Graph g, final Edge e) {
        final int u = e.either(), v = e.other(u);
        if (e.bond() == Bond.SINGLE || e.bond() == Bond.AROMATIC) {
            return new Edge(u, v,
                            type(g.atom(u),
                                 g.atom(v),
                                 e.bond()));
        }
        return e;
    }

    /**
     * Given two atoms which are explicit connected determine the implicit bond
     * type. If both atoms are aromatic but connected by a single bond the bond
     * type is {@link Bond#SINGLE} otherwise it is implicit.
     *
     * @param u an atom
     * @param v another atom (connected to u)
     * @param b explicit bond type
     * @return the bond type
     */
    static Bond type(Atom u, Atom v, Bond b) {
        if (u.aromatic() && v.aromatic())
            return b == Bond.AROMATIC ? Bond.IMPLICIT : b;
        else
            return b == Bond.AROMATIC ? Bond.AROMATIC : Bond.IMPLICIT;
    }
}
