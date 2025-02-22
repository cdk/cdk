package uk.ac.ebi.beam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * see. http://en.wikipedia.org/wiki/Biconnected_component
 *
 * @author John May
 */
final class BiconnectedComponents {

    private int[] depth;

    private final Graph  g;
    private final Edge[] stack;
    private int nstack = 0;

    private final List<List<Edge>> components = new ArrayList<List<Edge>>(2);

    private final BitSet cyclic = new BitSet();
    private final BitSet simple = new BitSet();

    int count = 0;
    int numfrags = 0;

    BiconnectedComponents(Graph g) {
        this(g, true);
    }

    BiconnectedComponents(Graph g, boolean storeComponents) {
        this.depth = new int[g.order()];
        this.g = g;
        this.stack  = new Edge[g.size()];
        
        if (storeComponents) {
            for (int u = 0; count < g.order(); u++) {
                if (depth[u] == 0) {
                    visitWithComp(u, null);
                    ++numfrags;
                }
            }
        } else {
            for (int u = 0; count < g.order(); u++) {
                if (depth[u] == 0) {
                    visit(u, null);
                    ++numfrags;
                }
            }
        }
    }

    private int visit(final int u, final Edge from) {
        depth[u] = ++count;
        int d    = g.degree(u);
        int lo   = count + 1;

        while (--d>=0) {
            final Edge e = g.edgeAt(u, d);
            if (e==from) continue;
            final int v = e.other(u);
            if (depth[v] == 0) {
                int res = visit(v, e);
                if (res < lo)
                    lo = res;
            }
            else if (depth[v] < lo) {
                lo = depth[v];
            }
        }
        if (lo <= depth[u])
            cyclic.set(u);
        return lo;
    }

    private int visitWithComp(final int u, final Edge from) {
        depth[u] = ++count;
        int j  = g.degree(u);
        int lo = count + 1; 
        while (--j>=0) {

            final Edge e = g.edgeAt(u, j);
            if (e==from) continue;

            final int v = e.other(u);
            if (depth[v] == 0) {
                stack[nstack] = e;
                ++nstack;
                int tmp = visitWithComp(v, e);
                if (tmp == depth[u])
                    storeWithComp(e);
                else if (tmp > depth[u])
                    --nstack;
                if (tmp < lo)
                    lo = tmp;
            }
            else if (depth[v] < depth[u]) {
                // back edge
                stack[nstack] = e;
                ++nstack;
                if (depth[v] < lo)
                    lo = depth[v];
            }
        }
        return lo;
    }
    
    private void storeWithComp(Edge e) {
        List<Edge> component = new ArrayList<Edge>(6);
        Edge f;

        final BitSet tmp = new BitSet();

        // count the number of unique vertices and edges
        int numEdges = 0;
        boolean spiro = false;

        do {
            f = stack[--nstack];
            int v = f.either();
            int w = f.other(v);

            if (cyclic.get(v) || cyclic.get(w))
                spiro = true;
                
            tmp.set(v);
            tmp.set(w);

            component.add(f);
            numEdges++;
        } while (f != e);

        cyclic.or(tmp);

        if (!spiro && tmp.cardinality() == numEdges)
            simple.or(tmp);

        components.add(Collections.unmodifiableList(component));
    }

    public List<List<Edge>> components() {
        return Collections.unmodifiableList(components);
    }

    BitSet cyclic() {
        return cyclic;
    }

    public boolean connected() {
        return numfrags < 2;
    }
}
