package uk.ac.ebi.beam;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Maximum matching in general graphs using Edmond's Blossom Algorithm. This
 * implementation was adapted D Eppstein's python code (<a
 * href="http://www.ics.uci.edu/~eppstein/PADS/CardinalityMatching.py">src</a>)
 * which provides efficient tree traversal and handling of blossoms. The
 * implementation may be quite daunting as a general introduction to the ideas.
 * Personally I found <a href="http://www.keithschwarz.com/interesting/">Keith
 * Schwarz</a> version very informative when starting to understand the
 * workings. <p/>
 *
 * An asymptotically better algorithm is described by Micali and Vazirani (1980)
 * and is similar to bipartite matching (<a href="http://en.wikipedia.org/wiki/Hopcroft%E2%80%93Karp_algorithm">Hopkroft-Karp</a>)
 * where by multiple augmenting paths are discovered at once. In general though
 * this version is very fast - particularly if given an existing matching to
 * start from. Even the very simple {@link ArbitraryMatching} eliminates many
 * loop iterations particularly at the start when all length 1 augmenting paths
 * are discovered.
 *
 * @author John May
 * @see <a href="http://en.wikipedia.org/wiki/Blossom_algorithm">Blossom
 *      algorithm, Wikipedia</a>
 * @see <a href="http://en.wikipedia.org/wiki/Hopcroft%E2%80%93Karp_algorithm">Hopkroft-Karp,
 *      Wikipedia</a>
 * @see <a href="http://research.microsoft.com/apps/video/dl.aspx?id=171055">Presentation
 *      from Vazirani on his and Micali O(|E| * sqrt(|V|)) algorithm</a>
 */
final class MaximumMatching {

    /** The graph we are matching on. */
    private final Graph graph;

    /** The current matching. */
    private final Matching matching;

    /** Subset of vertices to be matched. */
    private final IntSet subset;

    /* Algorithm data structures below. */
    
    /** Storage of the forest, even and odd levels */
    private final int[] even, odd;

    /** Special 'nil' vertex. */
    private static final int nil = -1;

    /** Queue of 'even' (free) vertices to start paths from. */
    private final FixedSizeQueue queue;

    /** Union-Find to store blossoms. */
    private final UnionFind uf;

    /**
     * Map stores the bridges of the blossom - indexed by with support
     * vertices.
     */
    private final Map<Integer, Tuple> bridges = new HashMap<Integer, Tuple>();

    /** Temporary array to fill with path information. */
    private final int[]  path;
    
    /**
     * Temporary bit sets when walking down 'trees' to check for
     * paths/blossoms.
     */
    private final BitSet vAncestors, wAncestors;
    
    /** Number of matched vertices. */
    private final int nMatched;


    private MaximumMatching(Graph graph, Matching matching, int nMatched, IntSet subset) {

        this.graph = graph;
        this.matching = matching;
        this.subset = subset;

        this.even = new int[graph.order()];
        this.odd = new int[graph.order()];

        this.queue = new FixedSizeQueue(graph.order());
        this.uf = new UnionFind(graph.order());

        // tmp storage of paths in the algorithm
        path = new int[graph.order()];
        vAncestors = new BitSet(graph.order());
        wAncestors = new BitSet(graph.order());

        // continuously augment while we find new paths, each
        // path increases the matching cardinality by 2
        while (augment()) {
            nMatched += 2;
        }
        
        this.nMatched = nMatched;
    }

    /**
     * Find an augmenting path an alternate it's matching. If an augmenting path
     * was found then the search must be restarted. If a blossom was detected
     * the blossom is contracted and the search continues.
     *
     * @return an augmenting path was found
     */
    private boolean augment() {

        // reset data structures
        Arrays.fill(even, nil);
        Arrays.fill(odd, nil);
        uf.clear();
        bridges.clear();
        queue.clear();

        // queue every unmatched vertex and place in the
        // even level (level = 0)        
        for (int v = 0; v < graph.order(); v++) {
            if (subset.contains(v) && matching.unmatched(v)) {
                even[v] = v;
                queue.enqueue(v);
            }
        }

        // for each 'free' vertex, start a bfs search
        while (!queue.empty()) {
            int v = queue.poll();

            final int d = graph.degree(v);
            for (int j=0; j<d; ++j) {
                final Edge e = graph.edgeAt(v, j);
                if (e.bond() == Bond.SINGLE)
                    continue;
                int w = e.other(v);

                if (!subset.contains(w))
                    continue;

                // the endpoints of the edge are both at even levels in the                
                // forest - this means it is either an augmenting path or
                // a blossom
                if (even[uf.find(w)] != nil) {
                    if (check(v, w))
                        return true;
                }

                // add the edge to the forest if is not already and extend
                // the tree with this matched edge
                else if (odd[w] == nil) {
                    odd[w] = v;
                    int u = matching.other(w);
                    // add the matched edge (potential though a blossom) if it
                    // isn't in the forest already
                    if (even[uf.find(u)] == nil) {
                        even[u] = w;
                        queue.enqueue(u);
                    }
                }
            }
        }

        // no augmenting paths, matching is maximum
        return false;
    }

    /**
     * An edge was found which connects two 'even' vertices in the forest. If
     * the vertices have the same root we have a blossom otherwise we have
     * identified an augmenting path. This method checks for these cases and
     * responds accordingly. <p/>
     *
     * If an augmenting path was found - then it's edges are alternated and the
     * method returns true. Otherwise if a blossom was found - it is contracted
     * and the search continues.
     *
     * @param v endpoint of an edge
     * @param w another endpoint of an edge
     * @return a path was augmented
     */
    private boolean check(int v, int w) {

        // self-loop (within blossom) ignored
        if (uf.connected(v, w))
            return false;

        vAncestors.clear();
        wAncestors.clear();
        int vCurr = v;
        int wCurr = w;

        // walk back along the trees filling up 'vAncestors' and 'wAncestors'
        // with the vertices in the tree -  vCurr and wCurr are the 'even' parents
        // from v/w along the tree
        while (true) {

            vCurr = parent(vAncestors, vCurr);
            wCurr = parent(wAncestors, wCurr);

            // v and w lead to the same root - we have found a blossom. We
            // travelled all the way down the tree thus vCurr (and wCurr) are
            // the base of the blossom
            if (vCurr == wCurr) {
                blossom(v, w, vCurr);
                return false;
            }

            // we are at the root of each tree and the roots are different, we
            // have found and augmenting path
            if (uf.find(even[vCurr]) == vCurr && uf.find(even[wCurr]) == wCurr) {
                augment(v);
                augment(w);
                matching.match(v, w);
                return true;
            }

            // the current vertex in 'v' can be found in w's ancestors they must
            // share a root - we have found a blossom whose base is 'vCurr'
            if (wAncestors.get(vCurr)) {
                blossom(v, w, vCurr);
                return false;
            }

            // the current vertex in 'w' can be found in v's ancestors they must
            // share a root, we have found a blossom whose base is 'wCurr'
            if (vAncestors.get(wCurr)) {
                blossom(v, w, wCurr);
                return false;
            }
        }
    }

    /**
     * Access the next ancestor in a tree of the forest. Note we go back two
     * places at once as we only need check 'even' vertices.
     *
     * @param ancestors temporary set which fills up the path we traversed
     * @param curr      the current even vertex in the tree
     * @return the next 'even' vertex
     */
    private int parent(BitSet ancestors, int curr) {
        curr = uf.find(curr);
        ancestors.set(curr);
        int parent = uf.find(even[curr]);
        if (parent == curr)
            return curr; // root of tree       
        ancestors.set(parent);
        return uf.find(odd[parent]);
    }

    /**
     * Create a new blossom for the specified 'bridge' edge.
     *
     * @param v    adjacent to w
     * @param w    adjacent to v
     * @param base connected to the stem (common ancestor of v and w)
     */
    private void blossom(int v, int w, int base) {
        base = uf.find(base);
        int[] supports1 = blossomSupports(v, w, base);
        int[] supports2 = blossomSupports(w, v, base);
        
        for (int i = 0; i < supports1.length; i++)
            uf.union(supports1[i], supports1[0]);
        for (int i = 0; i < supports2.length; i++)
            uf.union(supports2[i], supports2[0]);
        
        even[uf.find(base)] = even[base];         
    }

    /**
     * Creates the blossom 'supports' for the specified blossom 'bridge' edge
     * (v, w). We travel down each side to the base of the blossom ('base')
     * collapsing vertices and point any 'odd' vertices to the correct 'bridge'
     * edge. We do this by indexing the birdie to each vertex in the 'bridges'
     * map.
     *
     * @param v    an endpoint of the blossom bridge
     * @param w    another endpoint of the blossom bridge
     * @param base the base of the blossom
     */
    private int[] blossomSupports(int v, int w, int base) {

        int n = 0;
        path[n++] = uf.find(v);
        Tuple b = Tuple.of(v, w);
        while (path[n - 1] != base) {
            int u = even[path[n - 1]];
            path[n++] = u;
            this.bridges.put(u, b);
            // contracting the blossom allows us to continue searching from odd
            // vertices (any odd vertices are now even - part of the blossom set)
            queue.enqueue(u);
            path[n++] = uf.find(odd[u]);
        }

        return Arrays.copyOf(path, n);
    }

    /**
     * Augment all ancestors in the tree of vertex 'v'.
     *
     * @param v the leaf to augment from
     */
    private void augment(int v) {
        int n = buildPath(path, 0, v, nil);
        for (int i = 2; i < n; i += 2) {
            matching.match(path[i], path[i - 1]);
        }
    }

    /**
     * Builds the path backwards from the specified 'start' vertex until the
     * 'goal'. If the path reaches a blossom then the path through the blossom
     * is lifted to the original graph.
     *
     * @param path  path storage
     * @param i     offset (in path)
     * @param start start vertex
     * @param goal  end vertex
     * @return the number of items set to the path[].
     */
    private int buildPath(int[] path, int i, int start, int goal) {
        while (true) {

            // lift the path through the contracted blossom
            while (odd[start] != nil) {

                Tuple bridge = bridges.get(start);

                // add to the path from the bridge down to where 'start'
                // is - we need to reverse it as we travel 'up' the blossom
                // and then...
                int j = buildPath(path, i, bridge.first(), start);
                reverse(path, i, j - 1);
                i = j;

                // ... we travel down the other side of the bridge 
                start = bridge.second();
            }
            path[i++] = start;

            // root of the tree
            if (matching.unmatched(start))
                return i;

            path[i++] = matching.other(start);

            // end of recursive
            if (path[i - 1] == goal)
                return i;

            start = odd[path[i - 1]];
        }
    }

    /**
     * Utility to maximise an existing matching of the provided graph.
     *
     * @param g a graph
     * @param m matching on the graph, will me modified
     * @param n current matching cardinality         
     * @param s subset of vertices to match
     * @return the maximal matching on the graph
     */
    static int maximise(Graph g, Matching m, int n, IntSet s) {
        MaximumMatching mm = new MaximumMatching(g, m, n, s);
        return mm.nMatched;
    }

    /**
     * Utility to maximise an existing matching of the provided graph.
     *
     * @param g a graph
     * @param m matching on the graph
     * @return the maximal matching on the graph
     */
    static int maximise(Graph g, Matching m, int n) {
        return maximise(g, m, n, IntSet.universe());
    }

    /**
     * Utility to get the maximal matching of the specified graph.
     *
     * @param g a graph
     * @return the maximal matching on the graph
     */
    static Matching maximal(Graph g) {
        Matching m = Matching.empty(g);
        maximise(g, m, 0);
        return m;
    }

    /**
     * Utility class provides a fixed size queue. Enough space is allocated for
     * every vertex in the graph. Any new vertices are added at the 'end' index
     * and 'polling' a vertex advances the 'start'.
     */
    private static final class FixedSizeQueue {
        private final int[] vs;
        private int i = 0;
        private int n = 0;

        /**
         * Create a queue of size 'n'.
         *
         * @param n size of the queue
         */
        private FixedSizeQueue(int n) {
            vs = new int[n];
        }

        /**
         * Add an element to the queue.
         *
         * @param e
         */
        void enqueue(int e) {
            vs[n++] = e;
        }

        /**
         * Poll the first element from the queue.
         *
         * @return the first element.
         */
        int poll() {
            return vs[i++];
        }

        /**
         * Check if the queue has any items.
         *
         * @return the queue is empty
         */
        boolean empty() {
            return i == n;
        }

        /** Reset the queue. */
        void clear() {
            i = 0;
            n = 0;
        }
    }

    /** Utility to reverse a section of a fixed size array */
    static void reverse(int[] path, int i, int j) {
        while (i < j) {
            int tmp = path[i];
            path[i] = path[j];
            path[j] = tmp;
            i++;
            j--;
        }
    }
}
