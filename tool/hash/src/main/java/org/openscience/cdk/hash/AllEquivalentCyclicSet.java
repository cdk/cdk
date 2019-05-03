/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.ringsearch.RingSearch;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Finds the set of equivalent values are members of a ring. This class is
 * intended to drive the systematic perturbation of the {@link
 * org.openscience.cdk.hash.PerturbedAtomHashGenerator}. This {@link
 * EquivalentSetFinder} provides the highest probability of avoid collisions due
 * to uniform atom environments but is much more demanding then the simpler
 * {@link MinimumEquivalentCyclicSet}.
 *
 * <br/> The easiest way to use this class is with the {@link
 * org.openscience.cdk.hash.HashGeneratorMaker}.
 * <blockquote><pre>
 * MoleculeHashGenerator generator =
 *   new HashGeneratorMaker().depth(6)
 *                           .elemental()
 *                           .perturbWith(new AllEquivalentCyclicSet())
 *                           .molecular();
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module hash
 * @see org.openscience.cdk.hash.PerturbedAtomHashGenerator
 * @see MinimumEquivalentCyclicSet
 * @see MinimumEquivalentCyclicSetUnion
 * @cdk.githash
 */
final class AllEquivalentCyclicSet extends EquivalentSetFinder {

    /**
     *{@inheritDoc}
     */
    @Override
    Set<Integer> find(long[] invariants, IAtomContainer container, int[][] graph) {

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
        Set<Integer> set = new TreeSet<Integer>();
        for (Map.Entry<Long, Set<Integer>> e : equivalent.entrySet()) {
            Set<Integer> vertices = e.getValue();
            if (vertices.size() > 1) {
                set.addAll(vertices);
            }
        }

        return set;
    }
}
