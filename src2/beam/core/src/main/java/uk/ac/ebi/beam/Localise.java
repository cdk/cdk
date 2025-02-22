package uk.ac.ebi.beam;

import java.util.BitSet;

/**
 * Utility to localise aromatic bonds.
 *
 * @author John May
 */
final class Localise {

    private static Graph generateKekuleForm(Graph g, BitSet subset, BitSet aromatic, boolean inplace) throws InvalidSmilesException {

        // make initial (empty) matching - then improve it, first
        // by matching the first edges we find, most of time this
        // gives us a perfect matching if not we maximise it
        // with Edmonds' algorithm

        final Matching m = Matching.empty(g);
        final int n = subset.cardinality();
        int nMatched = ArbitraryMatching.initial(g, m, subset);
        if (nMatched < n) {
            if (n - nMatched == 2)
                nMatched = ArbitraryMatching.augmentOnce(g, m, nMatched, subset);
            if (nMatched < n)
                nMatched = MaximumMatching.maximise(g, m, nMatched, IntSet.fromBitSet(subset));
            if (nMatched < n)
                throw new InvalidSmilesException("Could not Kekulise");
        }

        return inplace ? assign(g, subset, aromatic, m)
                       : copyAndAssign(g, subset, aromatic, m);
    }

    // invariant, m is a perfect matching
    private static Graph copyAndAssign(Graph delocalised, BitSet subset, BitSet aromatic, Matching m) throws InvalidSmilesException {
        Graph localised = new Graph(delocalised.order());
        localised.setFlags(delocalised.getFlags() & ~Graph.HAS_AROM);
        for (int u = 0; u < delocalised.order(); u++) {
            localised.addAtom(delocalised.atom(u).toAliphatic());
            localised.addTopology(delocalised.topologyOf(u));
            final int d = delocalised.degree(u);
            for (int j = 0; j < d; ++j) {
                final Edge e = delocalised.edgeAt(u, j);
                int v = e.other(u);
                if (v < u) {
                    switch (e.bond()) {
                        case SINGLE:
                            if (aromatic.get(u) && aromatic.get(v)) {
                                localised.addEdge(Bond.SINGLE.edge(u, v));
                            } else {
                                localised.addEdge(Bond.IMPLICIT.edge(u, v));
                            }
                            break;
                        case AROMATIC:
                            if (subset.get(u) && m.other(u) == v) {
                                localised.addEdge(Bond.DOUBLE_AROMATIC.edge(u, v));
                            }
                            else if (aromatic.get(u) && aromatic.get(v)) {
                                localised.addEdge(Bond.IMPLICIT_AROMATIC.edge(u, v));
                            }
                            else {
                                localised.addEdge(Bond.IMPLICIT.edge(u, v));
                            }
                            break;
                        case IMPLICIT:
                            if (subset.get(u) && m.other(u) == v) {
                                localised.addEdge(Bond.DOUBLE_AROMATIC.edge(u, v));
                            }
                            else if (aromatic.get(u) && aromatic.get(v)) {
                                localised.addEdge(Bond.IMPLICIT_AROMATIC.edge(u, v));
                            }
                            else {
                                localised.addEdge(e);
                            }
                            break;
                        default:
                            localised.addEdge(e);
                            break;
                    }
                }
            }
        }
        return localised;
    }

    // invariant, m is a perfect matching
    private static Graph assign(Graph g, BitSet subset, BitSet aromatic, Matching m) throws InvalidSmilesException {
        g.setFlags(g.getFlags() & ~Graph.HAS_AROM);
        for (int u = aromatic.nextSetBit(0); u >= 0; u = aromatic.nextSetBit(u + 1)) {
            g.setAtom(u, g.atom(u).toAliphatic());
            int deg = g.degree(u);
            for (int j = 0; j < deg; ++j) {
                Edge e = g.edgeAt(u, j);
                int v = e.other(u);
                if (v < u) {
                    switch (e.bond()) {
                        case UP:
                        case UP_AROMATIC:
                            if (aromatic.get(u) && aromatic.get(v))
                                e.bond(Bond.UP_AROMATIC);
                            else
                                e.bond(Bond.UP);
                            break;
                        case DOWN:
                        case DOWN_AROMATIC:
                            if (aromatic.get(u) && aromatic.get(v))
                                e.bond(Bond.DOWN_AROMATIC);
                            else
                                e.bond(Bond.DOWN);
                            break;
                        case SINGLE:
                            if (aromatic.get(u) && aromatic.get(v)) {
                                e.bond(Bond.SINGLE);
                            } else {
                                e.bond(Bond.IMPLICIT);
                            }
                            break;
                        case AROMATIC:
                            if (subset.get(u) && m.other(u) == v) {
                                e.bond(Bond.DOUBLE_AROMATIC);
                                g.updateBondedValence(u, +1);
                                g.updateBondedValence(v, +1);
                            }
                            else if (aromatic.get(v)) {
                                e.bond(Bond.IMPLICIT_AROMATIC);
                            }
                            else {
                                e.bond(Bond.IMPLICIT);
                            }
                            break;
                        case IMPLICIT:
                            if (subset.get(u) && m.other(u) == v) {
                                e.bond(Bond.DOUBLE_AROMATIC);
                                g.updateBondedValence(u, +1);
                                g.updateBondedValence(v, +1);
                            }
                            else if (aromatic.get(u) && aromatic.get(v)) {
                                e.bond(Bond.IMPLICIT_AROMATIC);
                            }
                            break;
                    }
                }
            }
        }
        return g;
    }

    static BitSet buildSet(Graph g, BitSet aromatic) {

        BitSet undecided = new BitSet(g.order());

        for (int v = 0; v < g.order(); v++) {
            if (g.atom(v).aromatic()) {
                aromatic.set(v);
                if (!predetermined(g, v))
                    undecided.set(v);
            }
        }

        return undecided;
    }

    static boolean predetermined(Graph g, int v) {

        Atom a = g.atom(v);

        int q = a.charge();
        int deg = g.degree(v) + g.implHCount(v);

        if (g.bondedValence(v) > g.degree(v)) {
            final int d = g.degree(v);
            for (int j = 0; j < d; ++j) {
                Edge e = g.edgeAt(v, j);
                if (e.bond() == Bond.DOUBLE) {
                    if (q == 0 && (a.element() == Element.Nitrogen ||
                                   a.element() == Element.Phosphorus ||
                                  (a.element() == Element.Sulfur && deg > 3)))
                        return false;
                    return true;
                }
                // triple or quadruple bond - we don't need to assign anymore p electrons
                else if (e.bond().order() > 2) {
                    return true;
                }
            }
        }

        // no pi bonds does the degree and charge indicate that
        // there can be no other pi bonds
        switch (a.element()) {
            case Boron:
                return (q == 0) && deg == 3;
            case Carbon:
                return ((q == 1 || q == -1) && deg == 3) || (q == 0 && deg == 4);
            case Silicon:
            case Germanium:
                return q < 0;
            case Nitrogen:
            case Phosphorus:
            case Arsenic:
            case Antimony:
                if (q == 0)
                    return deg == 3 || deg > 4;
                else if (q == 1)
                    return deg > 3;
                else
                    return true;
            case Oxygen:
            case Sulfur:
            case Selenium:
            case Tellurium:
                if (q == 0)
                    return deg == 2 || deg == 4 || deg > 5;
                else if (q == -1 || q == +1)
                    return deg == 3 || deg == 5 || deg > 6;
                else
                    return false;
        }

        return false;
    }

    static boolean inSmallRing(Graph g, Edge e) {
        BitSet visit = new BitSet();
        return inSmallRing(g, e.either(), e.other(e.either()), e.other(e.either()), 1, new BitSet());
    }

    static boolean inSmallRing(Graph g, int v, int prev, int t, int d, BitSet visit) {
        if (d > 7)
            return false;
        if (v == t)
            return true;
        if (visit.get(v))
            return false;
        visit.set(v);
        final int deg = g.degree(v);
        for (int j = 0; j < deg; ++j) {
            final Edge e = g.edgeAt(v, j);
            int w = e.other(v);
            if (w == prev) continue;
            if (inSmallRing(g, w, v, t, d + 1, visit)) {
                return true;
            }
        }
        return false;
    }

    static Graph resonate(Graph g, BitSet cyclic, boolean ordered) {

        BitSet subset = new BitSet();

        for (int u = cyclic.nextSetBit(0); u >= 0; u = cyclic.nextSetBit(u + 1)) {
            // candidates must have a bonded
            // valence of one more than their degree
            // and in a ring
            int uExtra = g.bondedValence(u) - g.degree(u);
            if (uExtra > 0) {

                int  other  = -1;
                Edge target = null;

                final int d = g.degree(u);
                for (int j = 0; j < d; ++j) {

                    final Edge e = g.edgeAt(u, j);
                    final int v = e.other(u);
                    // check for bond validity
                    if (e.bond().order() == 2) {

                        int vExtra = g.bondedValence(v) - g.degree(v);
                        if (cyclic.get(v) && vExtra > 0) {

                            if (hasAdjDirectionalLabels(g, e, cyclic) && !inSmallRing(g, e)) {
                                other = -1;
                                target = null;
                                break;
                            }
                            if (vExtra > 1 && hasAdditionalCyclicDoubleBond(g, cyclic, u, v)) {
                                other = -1;
                                target = null;
                                break;
                            }
                            if (other == -1) {
                                other  = v;  // first one
                                target = e;
                            } else {
                                other = -2; // found more than one
                                target = null;
                            }
                        }
                        // only one double bond don't check any more
                        if (uExtra == 1)
                            break;
                    }
                }

                if (target != null) {
                    subset.set(u);
                    subset.set(other);
                    target.bond(Bond.IMPLICIT);
                }
            }
        }

        if (!ordered)
            g = g.sort(new Graph.CanOrderFirst());

        final Matching m = Matching.empty(g);
        int n = subset.cardinality();
        int nMatched = ArbitraryMatching.dfs(g, m, subset);
        if (nMatched < n) {
            if (n - nMatched == 2)
                nMatched = ArbitraryMatching.augmentOnce(g, m, nMatched, subset);
            if (nMatched < n)
                nMatched = MaximumMatching.maximise(g, m, nMatched, IntSet.fromBitSet(subset));
            if (nMatched < n)
                throw new InternalError("Could not Kekulise");
        }

        // assign new double bonds
        for (int v = subset.nextSetBit(0); v >= 0; v = subset.nextSetBit(v + 1)) {
            int w = m.other(v);
            subset.clear(w);
            g.edge(v,w).bond(Bond.DOUBLE);
        }

        return g;    
    }

    /**
     * Resonate double bonds in a cyclic system such that given a molecule with the same ordering
     * produces the same resonance assignment. This procedure provides a canonical kekulué
     * representation for conjugated rings.
     *
     * @param g graph
     * @return the input graph (same reference)
     */
    static Graph resonate(Graph g) {
        return resonate(g, new BiconnectedComponents(g).cyclic(), false);
    }

    private static boolean hasAdditionalCyclicDoubleBond(Graph g, BitSet cyclic, int u, int v) {
        for (Edge f : g.edges(v)) {
            if (f.bond() == Bond.DOUBLE && f.other(v) != u && cyclic.get(u)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasAdjDirectionalLabels(Graph g, Edge e, BitSet cyclic) {
        int u = e.either();
        int v = e.other(u);
        return hasAdjDirectionalLabels(g, u, cyclic) && hasAdjDirectionalLabels(g, v, cyclic);
    }

    private static boolean hasAdjDirectionalLabels(Graph g, int u, BitSet cyclic) {
        final int d = g.degree(u);
        for (int j = 0; j < d; ++j) {
            final Edge f = g.edgeAt(u, j);
            final int v = f.other(u);
            if (f.bond().directional() && cyclic.get(v)) {
                return true;
            }
        }
        return false;
    }

    static Graph localise(Graph delocalised) throws InvalidSmilesException {

        // nothing to do, return fast
        if (delocalised.getFlags(Graph.HAS_AROM) == 0)
            return delocalised;

        BitSet aromatic = new BitSet();
        BitSet subset = buildSet(delocalised, aromatic);
        if (hasOddCardinality(subset))
            throw new InvalidSmilesException("a valid kekulé structure could not be assigned");
        return Localise.generateKekuleForm(delocalised, subset, aromatic, false);
    }

    static Graph localiseInPlace(Graph delocalised) throws InvalidSmilesException {

        // nothing to do, return fast
        if (delocalised.getFlags(Graph.HAS_AROM) == 0)
            return delocalised;

        BitSet aromatic = new BitSet();
        BitSet subset = buildSet(delocalised, aromatic);
        if (hasOddCardinality(subset))
            throw new InvalidSmilesException("a valid kekulé structure could not be assigned");
        return Localise.generateKekuleForm(delocalised, subset, aromatic, true);
    }

    private static boolean hasOddCardinality(BitSet s) {
        return (s.cardinality() & 0x1) == 1;
    }
}
