package uk.ac.ebi.beam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines a matching on a graph. A matching or independent edge set is a set of
 * edges without common vertices. A matching is perfect if every vertex in the
 * graph is matched. Another way of thinking about the matching is that each
 * vertex is incident to exactly one matched edge. <p/>
 *
 * This class provides storage and manipulation of a matching. A new match is
 * added with {@link #match(int, int)}, any existing match for the newly matched
 * vertices is non-longer available. For convenience {@link #matches()} provides
 * the current independent edge set.
 *
 * @author John May
 */
final class Matching {

    /** Indicates an unmatched vertex. */
    private static final int UNMATCHED = -1;

    /** Storage of which each vertex is matched with. */
    private final int[] match;

    /**
     * Create a matching of the given size.
     *
     * @param n number of items
     */
    private Matching(int n) {
        this.match = new int[n];
        Arrays.fill(match, UNMATCHED);
    }

    boolean matched(int v) {
        return !unmatched(v);
    }
    
    /**
     * Is the vertex v 'unmatched'.
     *
     * @param v a vertex
     * @return the vertex has no matching
     */
    boolean unmatched(int v) {
        int w = match[v];
        return w < 0 || match[w] != v;
    }

    /**
     * Access the vertex matched with 'v'.
     *
     * @param v a vertex
     * @return matched vertex
     * @throws IllegalArgumentException the vertex is currently unmatched
     */
    int other(int v) {
        if (unmatched(v))
            throw new IllegalArgumentException(v + " is not matched");
        return match[v];
    }

    /**
     * Add the edge '{u,v}' to the matched edge set. Any existing matches for
     * 'u' or 'v' are removed from the matched set.
     *
     * @param u a vertex
     * @param v another vertex
     */
    void match(int u, int v) {
        // set the new match, don't need to update existing - we only provide
        // access to bidirectional mappings
        match[u] = v;
        match[v] = u;
    }

    /**
     * Access the current non-redundant set of edges.
     *
     * @return matched pairs
     */
    Iterable<Tuple> matches() {

        List<Tuple> tuples = new ArrayList<Tuple>(match.length / 2);

        for (int v = 0; v < match.length; v++) {
            int w = match[v];
            if (w > v && match[w] == v) {
                tuples.add(Tuple.of(v, w));
            }
        }

        return tuples;
    }

    /**
     * Allocate a matching with enough capacity for the given graph.
     *
     * @param g a graph
     * @return matching
     */
    static Matching empty(Graph g) {
        return new Matching(g.order());
    }
}
