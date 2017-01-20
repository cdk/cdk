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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * The union of all the smallest set of equivalent values are members of a ring.
 * This class is intended to drive the systematic perturbation of the {@link
 * org.openscience.cdk.hash.PerturbedAtomHashGenerator}. The method is more
 * comprehensive then a single {@link MinimumEquivalentCyclicSet} and not as
 * computationally demanding as {@link AllEquivalentCyclicSet}. In reality one
 * should choose either use the fast (but good) heuristic {@link
 * MinimumEquivalentCyclicSet} or the exact {@link AllEquivalentCyclicSet}. This
 * method is provided for demonstration only.
 *
 * As with the {@link MinimumEquivalentCyclicSet} perturbation, this method does
 * not guarantee that all molecules will be distinguished. At the time of
 * writing (Feb 2013) there are only 8 structure in PubChem-Compound which need
 * the more comprehensive perturbation method ({@link AllEquivalentCyclicSet}),
 * these are listed below.
 *
 * <table><tr><td>CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=144432">144432</a>
 * and CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=15584856">15584856</a></td></tr>
 * <tr><td>CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=138898">138898</a>
 * and CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=241107">241107</a></td></tr>
 * <tr><td>CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=9990759">9990759</a>
 * and CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=10899923">10899923</a></td></tr>
 * <tr><td>CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=5460768">5460768</a>
 * and CID <a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=20673269">20673269</a></td></tr>
 * </table>
 *
 * <br/> The easiest way to use this class is with the {@link
 * org.openscience.cdk.hash.HashGeneratorMaker}.
 * <blockquote><pre>
 * MoleculeHashGenerator generator =
 *   new HashGeneratorMaker().depth(6)
 *                           .elemental()
 *                           .perturbWith(new MinimumEquivalentCyclicSetUnion())
 *                           .molecular();
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module hash
 * @see org.openscience.cdk.hash.PerturbedAtomHashGenerator
 * @see MinimumEquivalentCyclicSet
 * @see AllEquivalentCyclicSet
 * @deprecated provided for to demonstrate a relatively robust but ultimately
 *             incomplete approach
 * @cdk.githash
 */
@Deprecated
final class MinimumEquivalentCyclicSetUnion extends EquivalentSetFinder {

    /**
     *{@inheritDoc}
     */
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
            } else if (vertices.size() == minSize) {
                min.addAll(vertices);
            }
        }

        return min;
    }
}
