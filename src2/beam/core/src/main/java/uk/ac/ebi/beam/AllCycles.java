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
import java.util.List;

/**
 * Aromaticity perception using AllCycles.
 *
 * @author John May
 */
final class AllCycles {

    /** Number of pi electrons for Sp2 atoms. */
    private final int[] ps;

    private final List<PathEdge> pathGraph[];

    boolean[] aromatic;

    private final Graph org;
    
    private static final int MAX_VERTEX_DEGREE = 684;

    @SuppressWarnings("unchecked") AllCycles(Graph g, ElectronDonation model, int lim) {

        this.org = g;
        this.ps = new int[g.order()];
        this.pathGraph = new List[g.order()];
        this.aromatic = new boolean[g.order()];

        ElectronDonation.Cycle cycle = new ElectronDonation.Cycle() {
            @Override public boolean contains(int u) {
                throw new UnsupportedOperationException();
            }
        };

        BitSet cyclic = new BiconnectedComponents(g).cyclic();

        for (int u = 0; u < g.order(); u++)
            ps[u] = model.contribution(u, g, cycle, cyclic);

        for (int u = 0; u < g.order(); u++)
            this.pathGraph[u] = new ArrayList<PathEdge>();

        // build the path graph
        for (Edge e : g.edges()) {
            int u = e.either();
            int v = e.other(u);
            if (cyclic.get(u) && cyclic.get(v) && ps[u] >= 0 && ps[v] >= 0) {
                PathEdge f = new PathEdge(u, v, EMPTY_SET, 0);
                add(u, v, f);
            }
        }

        for (int u = 0; u < g.order(); u++) {
            if (this.pathGraph[u].size() > MAX_VERTEX_DEGREE)
                throw new IllegalArgumentException("too many cycles generated: " + pathGraph[u].size());
            reduce(u, lim);
        }
    }

    public Graph aromaticForm() {
        Graph cpy = new Graph(org.order());
        cpy.addFlags(org.getFlags(0xffffffff));
        
        for (int i = 0; i < org.order(); i++) {
            if (aromatic[i]) {
                cpy.addAtom(org.atom(i).toAromatic());
                cpy.addFlags(Graph.HAS_AROM);
            } else {
                cpy.addAtom(org.atom(i));
            }
            cpy.addTopology(org.topologyOf(i));
        }

        for (Edge e : org.edges()) {
            int u = e.either();
            int v = e.other(u);
            if (aromatic[u] && aromatic[v]) {
                // check implHCount for subset
                cpy.addEdge(new Edge(u, v, Bond.IMPLICIT));
            }
            else {
                cpy.addEdge(e);
            }

        }

        // check for required hydrogens
        for (int i = 0; i < org.order(); i++) {
            int hCount = org.implHCount(i);
            if (hCount != cpy.implHCount(i)) {
                cpy.setAtom(i,
                            new AtomImpl.BracketAtom(-1,
                                                     cpy.atom(i).element(),
                                                     hCount,
                                                     0,
                                                     0,
                                                     true));
            }
        }
        return cpy.sort(new Graph.CanOrderFirst());
    }

    private void add(PathEdge e) {
        int u = e.either();
        int v = e.other(u);
        add(u, v, e);
    }

    private void add(int u, int v, PathEdge e) {
        this.pathGraph[Math.min(u, v)].add(e);
    }

    private void reduce(int x, int lim) {
        List<PathEdge> es = pathGraph[x];
        int deg = es.size();
        for (int i = 0; i < deg; i++) {
            PathEdge e1 = es.get(i);
            for (int j = i + 1; j < deg; j++) {
                PathEdge e2 = es.get(j);
                if (!e1.intersects(e2)) {
                    PathEdge reduced = reduce(e1, e2, x);
                    if (reduced.xs.cardinality() >= lim)
                        continue;
                    if (reduced.loop()) {
                        if (reduced.checkPiElectrons(ps)) {
                            reduced.flag(aromatic);
                        }
                    }
                    else {
                        add(reduced);
                    }
                }
            }
        }
        pathGraph[x].clear();
    }

    /** An empty bit-set. */
    private static final BitSet EMPTY_SET = new BitSet(0);

    static BitSet union(BitSet s, BitSet t, int x) {
        BitSet u = (BitSet) s.clone();
        u.or(t);
        u.set(x);
        return u;
    }

    private PathEdge reduce(PathEdge e, PathEdge f, int x) {
        return new PathEdge(e.other(x),
                            f.other(x),
                            union(e.xs, f.xs, x),
                            ps[x] + e.ps + f.ps);
    }

    private static final class PathEdge {
        /* Reduced vertices. */
        BitSet xs;
        /** End points of the edge. */
        int    u, v;
        /** Number of pi electrons in the path. */
        int ps;

        protected PathEdge(int u, int v, BitSet xs, int ps) {
            this.xs = xs;
            this.ps = ps;
            this.u = u;
            this.v = v;
        }

        final int either() {
            return u;
        }

        final int other(int x) {
            return (x == u) ? v : u;
        }

        final boolean loop() {
            return u == v;
        }

        // 4n+2
        final boolean checkPiElectrons(int[] ps) {
            return (this.ps + ps[u] - 2) % 4 == 0;
        }

        final void flag(boolean[] mark) {
            mark[u] = true;
            for (int i = xs.nextSetBit(0); i >= 0; i = xs.nextSetBit(i + 1)) {
                mark[i] = true;
            }
        }

        final boolean intersects(PathEdge e) {
            return e.xs.intersects(xs);
        }
    }

    static AllCycles daylightModel(Graph g) {
        return new AllCycles(g, ElectronDonation.daylight(), g.order());
    }

    static AllCycles daylightModel(Graph g, int lim) {
        return new AllCycles(g, ElectronDonation.daylight(), lim);
    }
}
