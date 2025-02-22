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
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Given a molecule with bond-based double bond configurations - add directional labels to edges
 * which do not have it assigned. For example the molecule {@code NC(/C)=C\C} has no directional
 * label between the nitrogen and the carbon. Applying this procedure will 'fill-in' missing
 * directional information on the edge - {@code N/C(/C)=C\C}. <br/>
 *
 * If required the directional labels in conjugated systems may be adjusted to allow for
 * full-specification. Attempting to assign a directional label to the central carbon of {@code
 * F/C=C(/F)C(/F)=C/F} creates a conflict. This conflict will be resolved by flipping the labels on
 * the second double-bond - {@code F/C=C(/F)\C(\F)=C\F}.
 *
 * @author John May
 */
final class AddDirectionalLabels
        extends AbstractFunction<Graph, Graph> {

    enum Status {
        COMPLETED,
        WAITING,
        INVALID
    }

    /**
     * Transform all implicit up/down to their explicit type. The original graph is unmodified
     *
     * @param g a chemical graph
     * @return new chemical graph but with all explicit bonds
     */
    public Graph apply(final Graph g)
            throws InvalidSmilesException {

        List<Edge> doublebonds = new ArrayList<>();

        // change edges (only changed added to replacement)
        for (int u = 0; u < g.order(); u++) {
            final int d = g.degree(u);
            for (int j = 0; j < d; ++j) {
                final Edge e = g.edgeAt(u, j);
                int v = e.other(u);
                if (v > u && e.bond() == Bond.DOUBLE) {
                    if (g.degree(u) < 2 || g.degree(v) < 2)
                        continue;
                    if (g.degree(u) + g.degree(v) > 4)
                        doublebonds.add(e);
                }
            }
        }
        
        if (doublebonds.isEmpty())
            return g;

        final Set<Edge>       remain       = new HashSet<>(doublebonds);
        final Map<Edge, Edge> replacements = new HashMap<>();

        boolean altered;
        final Set<Edge> completed = new HashSet<>();
        do {
            altered = false;
            for (final Edge e : remain) {
                Status status = replaceImplWithExpl(g, e, replacements);
                if (status != Status.WAITING) {
                    completed.add(e);
                    altered = true;
                }
            }
            remain.removeAll(completed);
            completed.clear();
        } while (altered && !remain.isEmpty());
        
        // completed bonds, we can't do anything
        if (remain.size() == doublebonds.size())
            return g;
        
        // cleanup any remaining edges that have 'dangling' directional labels
        for (Edge e : remain) {
            int u = e.either();
            int v = e.other(u);
            final int d = g.degree(u);
            for (int j = 0; j < d; ++j) {
                final Edge f = g.edgeAt(u, j);
                if (isDirectional(f, replacements) && safeToClean(g, f.other(u), replacements)) {
                    replacements.put(f,
                                     new Edge(u, f.other(u), Bond.IMPLICIT));
                }
            }
            final int d2 = g.degree(v);
            for (int j = 0; j < d2; ++j) {
                final Edge f = g.edgeAt(v, j);
                if (isDirectional(f, replacements) && safeToClean(g, f.other(v), replacements))
                    replacements.put(f,
                                     new Edge(v, f.other(v), Bond.IMPLICIT));
            }
        }

        Graph h = new Graph(g.order());
        h.addFlags(g.getFlags(0xffffffff));

        // copy atom/topology information this is unchanged
        for (int u = 0; u < g.order(); u++) {
            h.addAtom(g.atom(u));
            h.addTopology(g.topologyOf(u));
        }
        
        // append the edges, replacing any which need to be changed
        for (int u = 0; u < g.order(); u++) {
            final int d = g.degree(u);
            for (int j = 0; j < d; ++j) {
                Edge e = g.edgeAt(u, j);
                if (e.other(u) < u) {
                    Edge replacement = replacements.get(e);
                    if (replacement != null)
                        e = replacement;
                    h.addEdge(e);
                }
            }
        }

        return h.sort(new Graph.CanOrderFirst());
    }

    private boolean isDirectional(Edge f, Map<Edge,Edge> replacements) {
        if (f.bond().directional())
            return true;
        Edge g = replacements.get(f);
        return g != null && g.bond().directional();
    }

    boolean safeToClean(Graph g, int v, Map<Edge,Edge> replacements) {
        final int d = g.degree(v);
        for (int j = 0; j < d; ++j) {
            final Edge e = g.edgeAt(v, j);
            if (e.bond().order() == 2) {
                int w = e.other(v);
                final int d2 = g.degree(w);
                for (int j2 = 0; j2 < d2; ++j2) {
                    final Edge f = g.edgeAt(w, j2);
                    if (isDirectional(f, replacements))
                        return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Given a double bond edge traverse the neighbors of both endpoints and accumulate any explicit
     * replacements in the 'acc' accumulator.
     *
     * @param g   the chemical graph
     * @param e   a edge in the graph ('double bond type')
     * @param acc accumulator for new edges
     * @throws InvalidSmilesException thrown if the edge could not be converted
     */
    private Status replaceImplWithExpl(Graph g,
                                       Edge e,
                                       Map<Edge, Edge> acc)
            throws InvalidSmilesException {
        
        int u = e.either(), v = e.other(u);

        Status ustat = replaceImplWithExpl(g, e, u, acc);
        Status vstat = replaceImplWithExpl(g, e, v, acc);

        if (ustat == vstat)
            return ustat;
        else if (ustat == Status.INVALID && vstat != Status.WAITING)
            return Status.INVALID;
        else if (vstat == Status.INVALID && ustat != Status.WAITING)
            return Status.INVALID;
        else    
            return Status.WAITING;
    }

    /**
     * Given a double bond edge traverse the neighbors of one of the endpoints and accumulate any
     * explicit replacements in the 'acc' accumulator.
     *
     * @param g   the chemical graph
     * @param e   a edge in the graph ('double bond type')
     * @param u   a endpoint of the edge 'e'
     * @param acc accumulator for new edges
     * @return does the edge 'e' need to be reconsidered later
     * @throws InvalidSmilesException thrown if the edge could not be converted
     */
    private Status replaceImplWithExpl(Graph g,
                                       Edge e,
                                       int u,
                                       Map<Edge, Edge> acc)
            throws InvalidSmilesException {

        Edge implicit = null;
        Edge explicit = null;

        for (Edge f : g.edges(u)) {
            final Edge f2 = acc.containsKey(f) ? acc.get(f) : f;
            switch (f2.bond(u)) {
                case SINGLE:
                case IMPLICIT:
                case IMPLICIT_AROMATIC:
                    if (implicit != null)
                        return Status.WAITING;
                    implicit = f;
                    break;
                case DOUBLE:
                case DOUBLE_AROMATIC:
                    if (!f.equals(e))
                        return Status.COMPLETED;
                    break;
                case UP:
                case UP_AROMATIC:
                case DOWN:
                case DOWN_AROMATIC:
                    if (explicit != null) {

                        if (acc.containsKey(explicit))
                            explicit = acc.get(explicit);

                        // original bonds are invalid
                        if ((f.bond().directional()) && explicit.bond(u).inverse() != f.bond(u)) {
                            return Status.INVALID;
                        }
                        
                        if (explicit.bond(u).inverse() != f2.bond(u)) {
                            acc.put(f, f2.inverse());
                            BitSet visited = new BitSet();
                            visited.set(u);
                            invertExistingDirectionalLabels(g, visited, acc, f2
                                    .other(u));
                        }
                        return Status.COMPLETED;
                    }
                    explicit = f;
                    break;
            }
        }

        // no implicit don't do anything
        if (implicit == null)
            return Status.COMPLETED;
        // no explicit bond we might get one in future...?
        if (explicit == null)
            return Status.WAITING;
        
        if (acc.containsKey(explicit))
            explicit = acc.get(explicit);

        int v = implicit.other(u);

        Edge existing = acc.put(implicit, new Edge(u,
                                                   v,
                                                   explicit.bond(u)
                                                           .inverse()));

        if (existing != null && existing.bond(u) != explicit.bond(u).inverse()) {
            throw new InvalidSmilesException("unable to assign explict type for " + implicit);
        }

        return Status.COMPLETED;
    }

    private void invertExistingDirectionalLabels(Graph g,
                                                 BitSet visited,
                                                 Map<Edge, Edge> replacement,
                                                 int u) {
        visited.set(u);
        if (g.topologyOf(u) == null)
            return;
        for (Edge e : g.edges(u)) {
            int v = e.other(u);
            if (!visited.get(v)) {
                Edge f = replacement.get(e);
                if (f != null && f.bond().directional()) {
                    replacement.put(e,
                                    f.inverse());
                }
                else if (e.bond().directional()) {
                    replacement.put(e,
                                    e.inverse());
                }
                invertExistingDirectionalLabels(g, visited, replacement, v);
            }
        }
    }

}
