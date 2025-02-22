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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Given a molecule with explict double bond configurations remove redundant
 * UP/DOWN bond. For example the removing redundant up/down labels from of
 * {@code N/C(/C)=C\C} produces {@code N/C(C)=C\C}.
 *
 * @author John May
 */
final class RemoveUpDownBonds extends AbstractFunction<Graph,Graph> {

    public Graph apply(final Graph g)
            throws InvalidSmilesException {

        Graph h = new Graph(g.order());

        // copy atom/topology information this is unchanged
        for (int u = 0; u < g.order(); u++) {
            h.addAtom(g.atom(u));
            h.addTopology(g.topologyOf(u));
        }

        int[] ordering = new DepthFirstOrder(g).visited;

        Map<Edge, Edge> replacements = new HashMap<Edge, Edge>();
        Set<Integer> dbCentres = new TreeSet<Integer>();

        // change edges (only changed added to replacement)
        for (int u = 0; u < g.order(); u++) {
            for (final Edge e : g.edges(u)) {
                if (e.other(u) > u && e.bond() == Bond.DOUBLE) {
                    removeRedundant(g, e, ordering, replacements);
                    dbCentres.add(u);
                    dbCentres.add(e.other(u));
                }
            }
        }

        // ensure we haven't accidentally removed one between two
        for (Edge e : new HashSet<Edge>(replacements.keySet())) {
            if (dbCentres.contains(e.either())
                    && dbCentres.contains(e.other(e.either()))) {
                replacements.remove(e);
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

    /**
     * Given a double bond edge traverse the neighbors of both endpoints and
     * accumulate any explicit replacements in the 'acc' accumulator.
     *
     * @param g   the chemical graph
     * @param e   a edge in the graph ('double bond type')
     * @param acc accumulator for new edges
     * @throws uk.ac.ebi.beam.InvalidSmilesException
     *          thrown if the edge could not be converted
     */
    private void removeRedundant(Graph g,
                                 Edge e,
                                 int[] ordering,
                                 Map<Edge, Edge> acc)
            throws InvalidSmilesException {

        int u = e.either(), v = e.other(u);

        replaceImplWithExpl(g, e, u, ordering, acc);
        replaceImplWithExpl(g, e, v, ordering, acc);
    }

    /**
     * Given a double bond edge traverse the neighbors of one of the endpoints
     * and accumulate any explicit replacements in the 'acc' accumulator.
     *
     * @param g   the chemical graph
     * @param e   a edge in the graph ('double bond type')
     * @param u   a endpoint of the edge 'e'
     * @param acc accumulator for new edges
     * @throws uk.ac.ebi.beam.InvalidSmilesException
     *          thrown if the edge could not be converted
     */
    private void replaceImplWithExpl(final Graph g,
                                     final Edge e,
                                     final int u,
                                     final int[] ordering,
                                     final Map<Edge, Edge> acc)
            throws InvalidSmilesException {

        Set<Edge> edges = new TreeSet<Edge>(Collections
                                                    .reverseOrder(new Comparator<Edge>() {
                                                        @Override
                                                        public int compare(Edge e, Edge f) {
                                                            int v = ordering[e
                                                                    .other(u)];
                                                            int w = ordering[f
                                                                    .other(u)];
                                                            if (v > w)
                                                                return +1;
                                                            if (v < w)
                                                                return -1;
                                                            return 0;
                                                        }
                                                    }));

        for (Edge f : g.edges(u)) {
            switch (f.bond()) {
                case DOUBLE:
                    if (!f.equals(e))
                        return;
                    break;
                case UP:
                case DOWN:
                case UP_AROMATIC:
                case DOWN_AROMATIC:
                    edges.add(f);
                    break;
            }
        }

        if (edges.size() == 2) {
            Iterator<Edge> it = edges.iterator();
            Edge explicit = it.next();
            int v = explicit.either();
            int w = explicit.other(v);
            acc.put(explicit, new Edge(v,
                                       w,
                                       Bond.IMPLICIT));
        } else if (edges.size() > 2) {
            throw new InvalidSmilesException("Too many up/down bonds on double bonded atom");
        }
    }

    private static final class DepthFirstOrder {
        private final Graph g;
        private final int[] visited;
        private       int   i;

        private DepthFirstOrder(Graph g) {
            this.g = g;
            this.visited = new int[g.order()];
            Arrays.fill(visited, -1);
            for (int u = 0; u < g.order(); u++) {
                if (visited[u] < 0) {
                    visit(u);
                }
            }
        }

        private void visit(int u) {
            visited[u] = i++;
            for (Edge e : g.edges(u)) {
                int v = e.other(u);
                if (visited[v] < 0) {
                    visit(v);
                }
            }
        }
    }
}
