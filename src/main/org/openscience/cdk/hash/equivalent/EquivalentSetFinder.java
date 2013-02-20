package org.openscience.cdk.hash.equivalent;

import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.Set;

/**
 * Describes a function which identifies a set of equivalent atoms base on the
 * provided invariants. Given some other pre-conditions this set is filtered
 * down and an array of length 0 to n is returned. It is important to note that
 * the atoms may not actually be equivalent and are only equivalent by the
 * provided invariants. An example of a pre-condition could be that we only
 * return the vertices which are present in rings (cyclic). This condition
 * removes all terminal atoms which although equivalent are not relevant.
 *
 * @author John May
 * @cdk.module hash
 */
public interface EquivalentSetFinder {

    /**
     * Find a set of equivalent vertices (atoms) and return this set as an array
     * of indices.
     *
     * @param invariants the values for each vertex
     * @param container  the molecule which which the graph is based on
     * @param graph      adjacency list representation of the graph
     * @return set of equivalent vertices
     */
    public Set<Integer> find(long[] invariants, IAtomContainer container, int[][] graph);

}
