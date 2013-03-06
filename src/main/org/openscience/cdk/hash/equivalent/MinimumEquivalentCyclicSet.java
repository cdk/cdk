package org.openscience.cdk.hash.equivalent;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.ringsearch.RingSearch;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Finds the smallest set of equivalent values are members of a ring. If there
 * are multiple smallest sets then the set with the lowest invariant value is
 * returned. This class is intended to drive the systematic perturbation of the
 * {@link org.openscience.cdk.hash.PerturbedAtomHashGenerator}.
 *
 * This method will not distinguish all possible molecules but represents a good
 * enough approximation to quickly narrow down an identity search. At the time
 * of writing (Feb, 2013) there are only 128 molecules (64 false positives) in
 * PubChem-Compound (46E6 molecules) which are not separated. In many data sets
 * this method will suffice however the exact {@link AllEquivalentCyclicSet} is
 * provided. <p/><br/>
 *
 * This method is currently the default used by the {@link
 * org.openscience.cdk.hash.HashGeneratorMaker} but can also be explicitly
 * specified. <blockquote>
 * <pre>
 * MoleculeHashGenerator generator =
 *   new HashGeneratorMaker().depth(6)
 *                           .elemental()
 *                           .perturbed() // use this class by default
 *                           .molecular();
 *
 * // explicitly specify the method
 * MoleculeHashGenerator generator =
 *   new HashGeneratorMaker().depth(6)
 *                           .elemental()
 *                           .perturbWith(new MinimumEquivalentCyclicSet())
 *                           .molecular();
 * </pre>
 * </blockquote>
 *
 * @author John May
 * @cdk.module hash
 * @see org.openscience.cdk.hash.PerturbedAtomHashGenerator
 * @see MinimumEquivalentCyclicSetUnion
 * @see AllEquivalentCyclicSet
 * @see org.openscience.cdk.hash.HashGeneratorMaker
 */
@TestClass("org.openscience.cdk.hash.equivalent.MinimumEquivalentCyclicSetTest")
public final class MinimumEquivalentCyclicSet implements EquivalentSetFinder {

    /**
     * @inheritDoc
     */
    @TestMethod("testFind_OneChoice,testFind_TwoChoices,testFind_NoChoice")
    @Override
    public Set<Integer> find(long[] invariants, IAtomContainer container, int[][] graph) {

        int n = invariants.length;

        // find cyclic vertices using DFS
        RingSearch ringSearch = new RingSearch(container, graph);

        // ordered map of the set of vertices for each value
        Map<Long, Set<Integer>> equivalent = new TreeMap<Long, Set<Integer>>();

        // divide the invariants into equivalent indexed and ordered sets
        for (int i = 0; i < invariants.length; i++) {

            Long invariant = invariants[i];
            Set<Integer> set = equivalent.get(invariant);

            if (set == null) {
                if (ringSearch.cyclic(i)) {
                    set = new HashSet<Integer>(n / 2);
                    set.add(i);
                    equivalent.put(invariant, set);
                }
            } else {
                set.add(i);
            }
        }

        // find the smallest set of equivalent cyclic vertices
        int minSize = Integer.MAX_VALUE;
        Set<Integer> min = Collections.emptySet();
        for (Map.Entry<Long, Set<Integer>> e : equivalent.entrySet()) {
            Set<Integer> vertices = e.getValue();
            if (vertices.size() < minSize && vertices.size() > 1) {
                min = vertices;
                minSize = vertices.size();
            }
        }

        return min;
    }

}
