package uk.ac.ebi.beam;

import java.util.*;

/**
 * Collection of utilities for transforming chemical graphs.
 *
 * @author John May
 */
public final class Functions {

    // convert to atom-based double-bond configurations
    private static final ToTrigonalTopology ttt = new ToTrigonalTopology();

    // convert to bond-based double-bond configuration
    private static final FromTrigonalTopology ftt = new FromTrigonalTopology();

    // bond label conversion -> to implicit
    private static final ExplicitToImplicit eti = new ExplicitToImplicit();

    // bond label conversion -> to explicit
    private static final ImplicitToExplicit ite = new ImplicitToExplicit();

    // use organic subset
    private static final ToSubsetAtoms tsa = new ToSubsetAtoms();

    // expand organic subset
    private static final FromSubsetAtoms fsa = new FromSubsetAtoms();

    // normalise directional labels
    private static final NormaliseDirectionalLabels ndl = new NormaliseDirectionalLabels();
    
    private static final AddDirectionalLabels adl = new AddDirectionalLabels();

    private static Random rand = new Random();

    /// non-instantiable
    private Functions() {
    }

    /**
     * Randomise the atom order of the provided chemical graph.
     *
     * @param g chemical graph
     * @return a copy of the original graph with the order of the atoms
     *         randomised
     */
    public static Graph randomise(Graph g) {
        return g.permute(random(g.order()));
    }

    /**
     * Reverse the atom order of the provided chemical graph.
     *
     * @param g chemical graph
     * @return a copy of the original graph with the order of the atoms
     *         reversed
     */
    public static Graph reverse(Graph g) {
        return g.permute(reverse(g.order()));
    }

    /**
     * Convert any directional bond based stereo configuration to atom-based
     * specification.
     *
     * @param g chemical graph graph
     * @return a copy of the original graph but with directional bonds removed
     *         and atom-based double-bond stereo configruation.
     */
    public static Graph atomBasedDBStereo(Graph g) {
        return eti.apply(ttt.apply(ite.apply(g)));
    }

    /**
     * Convert a graph with atom-based double-bond stereo configuration to
     * bond-based specification (direction UP and DOWN bonds).
     *
     * @param g chemical graph graph
     * @return a copy of the original graph but with bond-based
     *         stereo-chemistry
     */
    public static Graph bondBasedDBStereo(Graph g) {
        return eti.apply(ftt.apply(ite.apply(g)));
    }

    /**
     * Expand a graph with organic subsets to one with specified atom
     * properties.
     *
     * @param g a chemical graph
     * @return the chemical graph expanded
     */
    public static Graph expand(Graph g) {
        return eti.apply(fsa.apply(ite.apply(g)));
    }

    /**
     * Collapse a graph with specified atom properties to one with organic
     * subset atoms.
     *
     * @param g a chemical graph
     * @return the chemical graph expanded
     */
    public static Graph collapse(Graph g) {
        return eti.apply(tsa.apply(ite.apply(g)));
    }

    public static Graph normaliseDirectionalLabels(Graph g) throws InvalidSmilesException {
        if (g.getFlags(Graph.HAS_BND_STRO) == 0)
            return g;
        return ndl.apply(g);
    }

    private static int[] ident(int n) {
        int[] p = new int[n];
        for (int i = 0; i < n; i++)
            p[i] = i;
        return p;
    }

    /**
     * Apply the labeling {@code labels[]} to the graph {@code g}. The labels
     * are converted to a permutation which is then applied to the Graph and
     * rearrange it's vertex order.
     *
     * @param g      the graph to permute
     * @param labels the vertex labels - for example from a cannibalisation
     *               algorithm
     * @return a cpy of the original graph with it's vertices permuted by the
     *         labelling
     */
    public static Graph canonicalize(final Graph g,
                                     final long[] labels) {

        Integer[] is = new Integer[g.order()];

        for (int i = 0; i < is.length; i++)
            is[i] = i;

        // TODO: replace with radix sort (i.e. using a custom comparator)
        Arrays.sort(is, new Comparator<Integer>() {
            @Override public int compare(Integer i, Integer j) {
                if (labels[i] > labels[j])
                    return +1;
                else if (labels[i] < labels[j])
                    return -1;
                return 0;
            }
        });

        int[] p = new int[g.order()];
        for (int i = 0; i < is.length; i++)
            p[is[i]] = i;
        return g.permute(p);
    }

    /**
     * Renumbers atom-atom maps using a depth-first traversal. Note this function
     * modifies the input graph.
     * @param g the graph
     * @return the input graph
     */
    public static Graph renumberAtomMaps(final Graph g) {
        RenumberAtomMaps.renumber(g);
        return g;
    }

    /**
     * Generate a random permutation.
     * @param n size of the permutation
     * @param rnd random number generator
     * @return the permutation
     */
    private static int[] random(int n, Random rnd) {
        int[] p = ident(n);
        for (int i = n; i > 1; i--)
            swap(p, i - 1, rnd.nextInt(i));
        return p;
    }

    /**
     * Generate a random permutation using a shared RNG instance. The method is synchronized
     * @param n size of the permutation
     * @return the permutation
     */
    private synchronized static int[] random(int n) {
      return random(n, rand);
    }

    private static int[] reverse(int n) {
        int[] p = new int[n];
        for (int i = 0; i < n; i++)
            p[i] = n - i - 1;
        return p;
    }

    // inverse of permutation
    private static int[] inv(int[] p) {
        int[] q = p.clone();
        for (int i = 0; i < p.length; i++)
            q[p[i]] = i;
        return q;
    }

    private static void swap(int[] p, int i, int j) {
        int tmp = p[i];
        p[i] = p[j];
        p[j] = tmp;
    }
}
