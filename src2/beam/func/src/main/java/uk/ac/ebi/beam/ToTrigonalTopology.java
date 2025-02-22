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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Convert direction (up/down) bonds to trigonal topology (double bond atom
 * centric stereo specification).
 *
 * <blockquote><pre>
 *    F/C=C/F -> F/[C@H]=[C@H]F
 *    F/C=C\F -> F/[C@H]=[C@@H]F
 *    F\C=C/F -> F/[C@@H]=[C@H]F
 *    F\C=C\F -> F/[C@@H]=[C@@H]F
 * </pre></blockquote>
 *
 * @author John May
 */
final class ToTrigonalTopology extends AbstractFunction<Graph,Graph> {

    public Graph apply(Graph g) {

        Graph h = new Graph(g.order());

        // original topology information this is unchanged
        for (int u = 0; u < g.order(); u++) {
            h.addTopology(g.topologyOf(u));
        }

        Map<Edge, Edge> replacements = new HashMap<Edge, Edge>();

        // change edges (only changed added to replacement)
        for (int u = 0; u < g.order(); u++) {
            for (final Edge e : g.edges(u)) {
                if (e.other(u) > u && e.bond().directional()) {
                    replacements.put(e,
                                     new Edge(u, e.other(u), Bond.IMPLICIT));
                }
            }
        }


        List<Edge> es = doubleBondLabelledEdges(g);


        for (Edge e : es) {
            int u = e.either();
            int v = e.other(u);

            // add to topologies
            h.addTopology(toTrigonal(g, e, u));
            h.addTopology(toTrigonal(g, e, v));
        }

        for (int u = 0; u < g.order(); u++) {
            Atom a = g.atom(u);
            if (a.subset() && h.topologyOf(u) != Topology.unknown()) {
                h.addAtom(asBracketAtom(u, g));
            } else {
                h.addAtom(a);
            }
        }

        // append the edges, replacing any which need to be changed
        for (int u = 0; u < g.order(); u++) {
            for (Edge e : g.edges(u)) {
                if (e.other(u) > u) {
                    Edge replacement = replacements.get(e);
                    if (replacement != null)
                        e = replacement;
                    h.addEdge(e);
                }
            }
        }

        return h;
    }

    private Atom asBracketAtom(int u, Graph g) {
        Atom a = g.atom(u);
        int sum = a.aromatic() ? 1 : 0;
        for (Edge e : g.edges(u)) {
            sum += e.bond().order();
        }
        return new AtomImpl.BracketAtom(-1,
                                        a.element(),
                                        a.aromatic() ? a.element().aromaticImplicitHydrogens(sum) 
                                                     : a.element().implicitHydrogens(sum),
                                        0,
                                        0,
                                        a.aromatic());
    }

    private Topology toTrigonal(Graph g, Edge e, int u) {

        List<Edge> es = g.edges(u);
        int offset = es.indexOf(e);

        int parity = 0;

        // vertex information for topology
        int j = 0;
        int[] vs = new int[]{
                e.other(u), // double bond
                u,          // for implicit H
                u,          // for implicit H
        };

        if (es.size() == 2) {
            Edge e1 = es.get((offset + 1) % 2);
            Bond b = e1.bond(u);
            if (isUp(b)) {
                vs[1] = e1.other(u);
            } else if (isDown(b)) {
                vs[2] = e1.other(u);
            }
        } else if (es.size() == 3) {
            Edge e1 = es.get((offset + 1) % 3);
            Edge e2 = es.get((offset + 2) % 3);
            Bond b1 = e1.bond(u);
            Bond b2 = e2.bond(u);
            if (b1 == Bond.SINGLE || b1 == Bond.IMPLICIT) {
                if (isUp(b2)) {
                    vs[1] = e2.other(u);
                    vs[2] = e1.other(u);
                } else if (isDown(b2)) {
                    vs[1] = e1.other(u);
                    vs[2] = e2.other(u);
                }
            } else {
                if (isUp(b1)) {
                    vs[1] = e1.other(u);
                    vs[2] = e2.other(u);
                } else if (isDown(b1)) {
                    vs[1] = e2.other(u);
                    vs[2] = e1.other(u);
                }
            }
        }

        if (vs[1] == vs[2])
            return Topology.unknown();

        Configuration c = es.get(offset).other(u) < u ? Configuration.DB1
                                                      : Configuration.DB2;


        return Topology.trigonal(u, vs, c);
    }

    static boolean isUp(Bond b) {
        return b == Bond.UP || b == Bond.UP_AROMATIC;
    }

    static boolean isDown(Bond b) {
        return b == Bond.DOWN || b == Bond.DOWN_AROMATIC;
    }

    private List<Edge> doubleBondLabelledEdges(Graph g) {
        List<Edge> es = new ArrayList<Edge>();
        for (int u = 0; u < g.order(); u++) {
            for (Edge e : g.edges(u)) {
                if (e.other(u) > u && e.bond() == Bond.DOUBLE) {
                    es.add(e);
                }
            }
        }
        return es;
    }

}
