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
 * {@link org.openscience.cdk.hash.PerturbedAtomHashGenerator}. The method is
 * different from the original publication {@cdk.cite Ihlenfeldt93} where only
 * non-terminally removable vertices are considered. The method differs as it
 * allows us to make the code more modular. In reality, ring perception
 * provided by {@link RingSearch} is very computationally cheap. <p/><br/> A
 * alternative and (potentially) more robust way may be use the union of all
 * minimum equivalent cyclic sets.
 *
 * @author John May
 * @cdk.module hash
 * @see org.openscience.cdk.hash.PerturbedAtomHashGenerator
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

            Long         invariant = invariants[i];
            Set<Integer> set       = equivalent.get(invariant);

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
        int          minSize = Integer.MAX_VALUE;
        Set<Integer> min     = Collections.emptySet();
        for (Map.Entry<Long, Set<Integer>> e : equivalent.entrySet()) {
            Set<Integer> vertices = e.getValue();
            if (vertices.size() < minSize) {
                min  = vertices;
                minSize = vertices.size();
            }
        }

        return min;
    }

}
