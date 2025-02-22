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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generate a SMILES line notation for a given chemical graph.
 *
 * @author John May
 */
final class Generator {

    private final Graph         g;
    private final StringBuilder sb;

    private final int[]                           visitedAt;
    private final int[]                           tmp;
    private       int                             nVisit;
    private final AtomToken[]                     tokens;
    private final Map<Integer, List<RingClosure>> rings;
    private final RingNumbering                   rnums;

    /**
     * Create a new generator the given chemical graph.
     *
     * @param g chemical graph
     */
    Generator(Graph g, RingNumbering rnums) throws InvalidSmilesException {
        this(g, new int[g.order()], rnums);
    }

    /**
     * Create a new generator the given chemical graph.
     *
     * @param g chemical graph
     * @param visitedAt the index of the atom in the output         
     */
    Generator(Graph g, int[] visitedAt, RingNumbering rnums) throws InvalidSmilesException {
        this.g = g;
        this.rnums = rnums;
        this.sb = new StringBuilder(g.order() * 2);
        this.visitedAt = visitedAt;
        this.tmp = new int[4];
        this.tokens = new AtomToken[g.order()];
        this.rings = new HashMap<Integer, List<RingClosure>>();

        // prepare ring closures and topologies
        Arrays.fill(visitedAt, -1);
        for (int u = 0; u < g.order() && nVisit < g.order(); u++) {
            if (visitedAt[u] < 0)
                prepare(u, u);
        }

        if (g.getFlags(Graph.HAS_EXT_STRO) != 0) {
            for (int u = 0; u < g.order(); u++) {
                if (g.topologyOf(u).configuration().type() == Configuration.Type.ExtendedTetrahedral) {
                    setAllenalStereo(g, visitedAt, u);
                }
            }
        }

        // write notation
        nVisit = 0;
        Arrays.fill(visitedAt, -1);
        for (int u = 0; u < g.order() && nVisit < g.order(); u++) {
            if (visitedAt[u] < 0) {
                if (u > 0) {
                    rnums.reset();
                    write(u, u, Bond.DOT);
                }
                else {
                    write(u, u, Bond.IMPLICIT);
                }
            }
        }
    }

    private void setAllenalStereo(Graph g, int[] visitedAt, int u)
    {
        assert g.degree(u) == 2;
        Edge a = g.edgeAt(u, 0);
        Edge b = g.edgeAt(u, 1);
        assert a.bond() == Bond.DOUBLE &&
               b.bond() == Bond.DOUBLE;

        int aAtom = a.other(u);
        int bAtom = b.other(u);

        if (rings.get(aAtom) == null && rings.get(bAtom) == null) {
            // no rings on either end, this is simply the order we visited the
            // atoms in
            tokens[u].configure(g.topologyOf(u).configurationOf(visitedAt));
        } else {
            // hokay this case is harder... this makes me wince but BEAM v2
            // has a much better way of handling this

            // we can be clever here rollback any changes we make (see the
            // tetrahedral handling) however since this is a very rare
            // operation it much simpler to copy the array
            int[] tmp = Arrays.copyOf(visitedAt, visitedAt.length);

            if (visitedAt[aAtom] > visitedAt[bAtom]) {
                int swap = aAtom;
                aAtom = bAtom;
                bAtom = swap;
            }

            assert rings.get(aAtom) == null || rings.get(aAtom).size() == 1;
            assert rings.get(bAtom) == null || rings.get(bAtom).size() == 1;

            if (rings.get(aAtom) != null) {
                tmp[rings.get(aAtom).get(0).other(aAtom)] = visitedAt[aAtom];
            }
            if (rings.get(bAtom) != null) {
                tmp[rings.get(bAtom).get(0).other(bAtom)] = visitedAt[bAtom];
            }

            tokens[u].configure(g.topologyOf(u).configurationOf(tmp));
        }
    }

    /**
     * First traversal of the molecule assigns ring bonds (numbered later) and
     * configures topologies.
     *
     * @param u the vertex to visit
     * @param p the atom we came from
     */
    void prepare(int u, int p) {
        visitedAt[u] = nVisit++;
        tokens[u] = g.atom(u).token();
        tokens[u].setGraph(g);
        tokens[u].setIdx(u);

        final int d = g.degree(u);
        for (int j=0; j<d; ++j) {
            final Edge e = g.edgeAt(u,j);
            int v = e.other(u);
            if (visitedAt[v] < 0) {
                prepare(v, u);
            } else if (v != p && visitedAt[v] < visitedAt[u]) {
                cyclicEdge(v, u, e.bond(v));
            }
        }

        prepareStereochemistry(u, p);
    }

    private void prepareStereochemistry(int u, int prev) {
        final Topology topology = g.topologyOf(u);
        if (topology != Topology.unknown()) {
            List<RingClosure> closures = rings.get(u);
            if (closures != null) {
                
                // most of time we only have a single closure, we can
                // handle this easily by moving the ranks of the prev
                // and curr atom back and using the curr rank for the
                // ring
                if (closures.size() == 1) {
                    int ring = closures.get(0).other(u);
                    int uAt = visitedAt[u]; 
                    int rAt = visitedAt[ring]; 
                    visitedAt[prev]--;
                    visitedAt[u]--;
                    visitedAt[ring] = uAt;
                    tokens[u].configure(topology.configurationOf(visitedAt));
                    // restore
                    visitedAt[prev]++;
                    visitedAt[u]++;
                    visitedAt[ring] = rAt;
                } else {
                    // more complicated, we first move the other two atoms out
                    // the way then store and change the current ranks of the 
                    // ring atoms. We restore all visitedAt once we exit
                    assert closures.size() <= 4; 
                    
                    visitedAt[prev] -= 4;
                    visitedAt[u] -= 4;
                    int rank = visitedAt[u]; 
                    for (int i = 0; i < closures.size(); ++i) {
                        final int v = closures.get(i).other(u);
                        tmp[i] = visitedAt[v];
                        visitedAt[v] = ++rank;
                    }
                    
                    tokens[u].configure(topology.configurationOf(visitedAt));
                    // restore
                    for (int i = 0; i < closures.size(); ++i)
                        visitedAt[closures.get(i).other(u)] = tmp[i];
                    visitedAt[prev] += 4;
                    visitedAt[u] += 4;
                }
            }
            else {
                tokens[u].configure(topology.configurationOf(visitedAt));
            }
        }
    }

    /**
     * Second traversal writes the bonds and atoms to the SMILES string.
     *
     * @param u a vertex
     * @param p previous vertex
     * @param b the bond from the previous vertex to this vertex
     */
    void write(int u, int p, Bond b) throws InvalidSmilesException {
        visitedAt[u] = nVisit++;

        int remaining = g.degree(u);

        if (u != p)
            remaining--;

        // assign ring numbers
        final List<RingClosure> closures = rings.get(u);
        if (closures != null) {
            for (RingClosure rc : closures) {
                // as we are composing tokens, make sure apply in reverse
                int rnum = rnums.next();
                if (rc.register(rnum)) {
                    int v = rc.other(u);
                    tokens[u] = new RingNumberToken(new RingBondToken(tokens[u],
                                                                      rc.bond(u)),
                                                    rnum);
                    rnums.use(rnum);
                } else {
                    tokens[u] = new RingNumberToken(tokens[u],
                                                    rc.rnum);
                    rnums.free(rc.rnum);
                }
                remaining--;
            }
        }

        sb.append(b.token());
        tokens[u].append(sb);

        final int d = g.degree(u);
        for (int j=0; j<d; ++j) {
            final Edge e = g.edgeAt(u,j);
            int v = e.other(u);
            if (visitedAt[v] < 0) {
                if (--remaining > 0) {
                    sb.append('(');
                    write(v, u, e.bond(u));
                    sb.append(')');
                } else {
                    write(v, u, e.bond(u));
                }
            }
        }
    }

   

    /**
     * Indicate that the edge connecting the vertices u and v forms a ring.
     *
     * @param u a vertex
     * @param v a vertex connected to u
     * @param b bond type connecting u to v
     */
    private void cyclicEdge(int u, int v, Bond b) {
        RingClosure r = new RingClosure(u, v, b);
        addRing(r.u, r);
        addRing(r.v, r);
    }

    /**
     * Add a ring closure to the the vertex 'u'.
     *
     * @param u  a vertex
     * @param rc ring closure
     */
    private void addRing(int u, RingClosure rc) {
        List<RingClosure> closures = rings.get(u);
        if (closures == null) {
            closures = new ArrayList<RingClosure>(2);
            rings.put(u, closures);
        }
        closures.add(rc);
    }

    /**
     * Access the generated SMILES string.
     *
     * @return smiles string
     */
    String string() {
        return sb.toString();
    }

    /**
     * Convenience method for generating a SMILES string for the specified
     * chemical graph.
     *
     * @param g the graph to generate the SMILE for
     * @return SMILES gor the provided chemical graph
     */
    static String generate(final Graph g) throws InvalidSmilesException {
        return new Generator(g, new IterativeRingNumbering(1)).string();
    }

    /**
     * Convenience method for generating a SMILES string for the specified
     * chemical graph.
     *
     * @param g the graph to generate the SMILE for
     * @param visitedAt store when each atom was visited
     * @return SMILES gor the provided chemical graph
     */
    static String generate(final Graph g, int[] visitedAt) throws InvalidSmilesException {
        return new Generator(g, visitedAt, new IterativeRingNumbering(1)).string();
    }

    static final class RingClosure {
        final int u, v;
        final Bond b;
        int rnum = -1;

        RingClosure(int u, int v, Bond b) {
            this.u = u;
            this.v = v;
            this.b = b;
        }

        int other(int x) {
            if (x == u) return v;
            if (x == v) return u;
            throw new IllegalArgumentException("non edge endpoint");
        }

        Bond bond(int x) {
            if (x == u) return b;
            else if (x == v) return b.inverse();
            throw new IllegalArgumentException("invalid endpoint");
        }

        boolean register(int rnum) {
            if (this.rnum < 0) {
                this.rnum = rnum;
                return true;
            }
            return false;
        }
    }

    static abstract class AtomToken {

        Graph g;
        int idx;

        void setGraph(Graph g) {
            this.g = g;
        }

        void setIdx(int idx) {
            this.idx = idx;
        }

        abstract void configure(Configuration c);

        abstract void append(StringBuilder sb);
    }

    static final class SubsetToken extends AtomToken {
        private final String str;

        SubsetToken(String str) {
            this.str = str;
        }

        @Override public void configure(Configuration c) {
            // do nothing
        }

        @Override public void append(StringBuilder sb) {
            sb.append(str);
        }
    }

    static final class BracketToken extends AtomToken {

        private Atom atom;
        private Configuration c = Configuration.UNKNOWN;

        BracketToken(Atom a) {
            this.atom = a;
        }

        @Override public void configure(Configuration c) {
            this.c = c;
        }

        @Override public void append(StringBuilder sb) {
            boolean hExpand = atom.element() == Element.Hydrogen &&
                              g.degree(idx) == 0;
            sb.append('[');
            if (atom.isotope() >= 0)
                sb.append(atom.isotope());
            sb.append(atom.aromatic() ? atom.element()
                                            .symbol()
                                            .toLowerCase(Locale.ENGLISH)
                                      : atom.element()
                                            .symbol());
            if (c != Configuration.UNKNOWN) {
                switch (c.type()) {
                    case SquarePlanar:
                        sb.append(g.degree(idx) == 4 ? c.shorthand().symbol() : c.symbol());
                        break;
                    case TrigonalBipyramidal:
                        sb.append(g.degree(idx) == 5 ? c.shorthand().symbol() : c.symbol());
                        break;
                    case Octahedral:
                        sb.append(g.degree(idx) == 6 ? c.shorthand().symbol() : c.symbol());
                        break;
                    default:
                        sb.append(c.shorthand().symbol());
                        break;
                }
            }
            if (atom.hydrogens() > 0 && !hExpand)
                sb.append(Element.Hydrogen.symbol());
            if (atom.hydrogens() > 1 && !hExpand)
                sb.append(atom.hydrogens());
            if (atom.charge() != 0) {
                sb.append(atom.charge() > 0 ? '+' : '-');
                int absCharge = Math.abs(atom.charge());
                if (absCharge > 1)
                    sb.append(absCharge);
            }
            if (atom.atomClass() != 0)
                sb.append(':').append(atom.atomClass());
            sb.append(']');
            if (hExpand) {
                int h = atom.hydrogens();
                while (h > 1) {
                    sb.append("([H])");
                    h--;
                }
                if (h > 0)
                    sb.append("[H]");
            }
        }
    }

    static abstract class TokenAdapter extends AtomToken {

        private AtomToken parent;

        TokenAdapter(AtomToken parent) {
            this.parent = parent;
        }

        @Override public final void configure(Configuration c) {
            this.parent.configure(c);
        }

        @Override public void append(StringBuilder sb) {
            parent.append(sb);
        }
    }

    static final class RingNumberToken extends TokenAdapter {
        int rnum;

        RingNumberToken(AtomToken p, int rnum) {
            super(p);
            this.rnum = rnum;
        }

        @Override public void append(StringBuilder sb) {
            super.append(sb);
            if (rnum > 9)
                sb.append('%');
            sb.append(rnum);
        }
    }

    static final class RingBondToken extends TokenAdapter {
        Bond bond;

        RingBondToken(AtomToken p, Bond bond) {
            super(p);
            this.bond = bond;
        }

        @Override public void append(StringBuilder sb) {
            super.append(sb);
            sb.append(bond);
        }
    }

    /** Defines how ring numbering proceeds. */
    static interface RingNumbering {
        /**
         * The next ring number in the sequence.
         *
         * @return ring number
         */
        int next() throws InvalidSmilesException;

        /**
         * Mark the specified ring number as used.
         *
         * @param rnum ring number
         */
        void use(int rnum);

        /**
         * Mark the specified ring number as no longer used.
         *
         * @param rnum ring number
         */
        void free(int rnum);
        
        /** Reset ring number usage */
        void reset();
    }

    /** Labelling of ring opening/closures always using the lowest ring number. */
    static final class ReuseRingNumbering implements RingNumbering {

        private boolean[] used = new boolean[100];
        private final int offset;

        ReuseRingNumbering(int first) {
            this.offset = first;
        }

        @Override public int next() throws InvalidSmilesException {
            for (int i = offset; i < used.length; i++) {
                if (!used[i]) {
                    return i;
                }
            }
            throw new InvalidSmilesException("no available ring numbers");
        }

        @Override public void use(int rnum) {
            used[rnum] = true;
        }

        @Override public void free(int rnum) {
            used[rnum] = false;
        }

        @Override public void reset() {
            // do nothing 
        }
    }

    /**
     * Iterative labelling of ring opening/closures. Once the number 99 has been
     * used the number restarts using any free numbers.
     */
    static final class IterativeRingNumbering implements RingNumbering {

        private boolean[] used = new boolean[100];
        private final int offset;
        private       int pos;

        IterativeRingNumbering(int first) {
            this.offset = first;
            this.pos = offset;
        }

        @Override public int next() throws InvalidSmilesException {
            while (pos < 100 && used[pos])
                pos++;
            if (pos < 100)
                return pos;
            pos = offset;
            while (pos < 100 && used[pos])
                pos++;
            if (pos < 100)
                return pos;
            else
                throw new InvalidSmilesException("no more ring numbers can be assigned");
        }

        @Override public void use(int rnum) {
            used[rnum] = true;
        }

        @Override public void free(int rnum) {
            used[rnum] = false;
        }

        @Override public void reset() {
            pos = 1;
        }
    }
}
