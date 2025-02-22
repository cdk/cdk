package uk.ac.ebi.beam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Normalise directional labels such that the first label is always a '/'. Given a molecule with
 * directional bonds {@code F\C=C\F} the labels are normalised to be {@code F/C=C/F}.
 *
 * @author John May
 */
final class NormaliseDirectionalLabels
        extends AbstractFunction<Graph, Graph> {

    @Override public Graph apply(Graph g) {
        Traversal traversal = new Traversal(g);
        Graph h = new Graph(g.order());
        h.addFlags(g.getFlags(0xffffffff));

        // copy atom/topology information this is unchanged
        for (int u = 0; u < g.order(); u++) {
            h.addAtom(g.atom(u));
            h.addTopology(g.topologyOf(u));
        }

        // change edges (only changed added to replacement)
        for (int u = 0; u < g.order(); u++) {
            final int d = g.degree(u);
            for (int j = 0; j < d; ++j) {
                final Edge e = g.edgeAt(u, j);
                if (e.other(u) > u) {
                    if (traversal.acc.containsKey(e)) {
                        h.addEdge(traversal.acc.get(e));
                    }
                    else {
                        h.addEdge(e);
                    }
                }
            }
        }

        return h.sort(new Graph.CanOrderFirst());
    }

    private static final class Traversal {

        private final Graph     g;
        private final boolean[] visited;
        private final int[]     ordering;
        private       int       i;
        private Map<Edge, Edge> acc = new HashMap<Edge, Edge>();

        private List<Edge>   doubleBonds = new ArrayList<>();
        private Set<Integer> adj         = new HashSet<>();

        private Traversal(Graph g) {
            this.g = g;
            this.visited = new boolean[g.order()];
            this.ordering = new int[g.order()];

            BitSet dbAtoms = new BitSet();
            for (int u = 0; u < g.order(); u++) {
                if (!visited[u])
                    dbAtoms.or(visit(u, u));
            }

            Collections.sort(doubleBonds, new Comparator<Edge>() {
                @Override public int compare(Edge e, Edge f) {
                    int u1 = e.either();
                    int v1 = e.other(u1);

                    int u2 = f.either();
                    int v2 = f.other(u2);

                    int min1 = Math.min(ordering[u1], ordering[v1]);
                    int min2 = Math.min(ordering[u2], ordering[v2]);
                    int cmp = min1 - min2;
                    if (cmp != 0) return cmp;
                    int max1 = Math.max(ordering[u1], ordering[v1]);
                    int max2 = Math.max(ordering[u2], ordering[v2]);
                    return max1 - max2;
                }
            });

            for (Edge e : doubleBonds) {
                if (acc.containsKey(e))
                    continue;
                flip(g, e, dbAtoms);
            }
        }

        private BitSet visit(int p, int u) {
            visited[u] = true;
            ordering[u] = i++;
            BitSet dbAtoms = new BitSet();
            final int d = g.degree(u);
            for (int j = 0; j < d; ++j) {
                final Edge e = g.edgeAt(u, j);
                int v = e.other(u);
                if (v == p)
                    continue;
                if (e.bond().order() == 2 && hasAdjDirectionalLabels(g, e)) {

                    dbAtoms.set(u);
                    dbAtoms.set(v);

                    // only the first bond we encounter in an isolated system
                    // is marked - if we need to flip the other we propagate
                    // this down the chain
                    boolean newSystem = !adj.contains(u) && !adj.contains(v);

                    // to stop adding other we mark all vertices adjacent to the
                    // double bond
                    final int d2 = g.degree(u);
                    for (int j2 = 0; j2 < d2; ++j2) {
                        adj.add(g.edgeAt(u, j2).other(u));
                    }
                    final int d3 = g.degree(v);
                    for (int j2 = 0; j2 < d3; ++j2) {
                        adj.add(g.edgeAt(v, j2).other(v));
                    }
                    doubleBonds.add(e);
                }
                if (!visited[v])
                    dbAtoms.or(visit(u, v));
            }
            return dbAtoms;
        }

        private boolean hasAdjDirectionalLabels(Graph g, Edge e) {
            int u = e.either();
            int v = e.other(u);
            return hasAdjDirectionalLabels(g, u) && hasAdjDirectionalLabels(g, v);
        }

        private boolean hasAdjDirectionalLabels(Graph g, int u) {
            final int d = g.degree(u);
            for (int j = 0; j < d; ++j) {
                final Edge f = g.edgeAt(u, j);
                if (f.bond().directional())
                    return true;
            }
            return false;
        }

        private void flip(Graph g, Edge e, BitSet dbAtoms) {

            int u = e.either();
            int v = e.other(u);

            if (ordering[u] < ordering[v]) {
                Edge first = firstDirectionalLabel(g, u);
                if (first != null) {
                    flip(first, u, dbAtoms);
                }
                else {
                    first = firstDirectionalLabel(g, v);
                    flip(first, v, dbAtoms);
                }
            }
            else {
                Edge first = firstDirectionalLabel(g, v);
                if (first != null) {
                    flip(first, v, dbAtoms);
                }
                else {
                    first = firstDirectionalLabel(g, u);
                    flip(first, u, dbAtoms);
                }
            }
        }

        private void flip(Edge first, int u, BitSet dbAtoms) {
            if (first == null)
                return;
            if (ordering[first.other(u)] < ordering[u]) {
                if (first.bond(u) == Bond.UP || first.bond(u) == Bond.UP_AROMATIC)
                    invertExistingDirectionalLabels(g,
                                                    u,
                                                    new BitSet(),
                                                    acc,
                                                    dbAtoms,
                                                    u);
                else
                    markExistingDirectionalLabels(g,
                                                  u,
                                                  new BitSet(),
                                                  acc,
                                                  dbAtoms,
                                                  u);
            }
            else {
                if (first.bond(u) == Bond.DOWN || first.bond(u) == Bond.DOWN_AROMATIC)
                    invertExistingDirectionalLabels(g,
                                                    u,
                                                    new BitSet(),
                                                    acc,
                                                    dbAtoms,
                                                    u);
                else
                    markExistingDirectionalLabels(g,
                                                  u,
                                                  new BitSet(),
                                                  acc,
                                                  dbAtoms,
                                                  u);
            }
        }

        Edge firstDirectionalLabel(Graph g, int u) {
            Edge first = null;
            final int d = g.degree(u);
            for (int j = 0; j < d; ++j) {
                final Edge f = g.edgeAt(u, j);
                if (f.bond().directional()) {
                    if (first == null || ordering[f.other(u)] < ordering[first.other(u)])
                        first = f;
                }
            }
            return first;
        }

        private void invertExistingDirectionalLabels(Graph g,
                                                     int prev,
                                                     BitSet visited,
                                                     Map<Edge, Edge> replacement,
                                                     BitSet dbAtoms,
                                                     int u) {
            visited.set(u);
            final int d = g.degree(u);
            for (int j = 0; j < d; ++j) {
                final Edge e = g.edgeAt(u, j);
                int v = e.other(u);
                if (v == prev)
                    continue;
                Edge f = replacement.get(e);
                if (f == null) {
                    replacement.put(e, e.inverse());
                    if (!visited.get(v)) {
                        if (dbAtoms.get(v))
                            invertExistingDirectionalLabels(g, u, visited, replacement, dbAtoms, v);
                    }
                }
            }
        }

        private void markExistingDirectionalLabels(Graph g,
                                                   int prev,
                                                   BitSet visited,
                                                   Map<Edge, Edge> replacement,
                                                   BitSet dbAtoms,
                                                   int u) {
            visited.set(u);
            final int d = g.degree(u);
            for (int j = 0; j < d; ++j) {
                final Edge e = g.edgeAt(u, j);
                int v = e.other(u);
                if (v == prev)
                    continue;
                Edge f = replacement.get(e);
                if (f == null) {
                    replacement.put(e, e);
                    if (!visited.get(v)) {
                        if (dbAtoms.get(v))
                            markExistingDirectionalLabels(g, u, visited, replacement, dbAtoms, v);
                    }
                }
            }
        }
    }
}
