/*
 * Copyright (c) 2016 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.smiles.smarts;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ReactionRole;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A filter for substructure matches implementing the logic for Atom-Atom Mapping matching. The following
 * table from the Daylight theory manual summarises the expected functionality:
 *
 * <pre>
 * C>>C                 CC>>CC    4 hits                        No maps, normal match.
 * C>>C                 [CH3:7][CH3:8]>>[CH3:7][CH3:8] 4 hits   No maps in query, maps in target are ignored.
 * [C:1]>>C             [CH3:7][CH3:8]>>[CH3:7][CH3:8] 4 hits   Unpaired map in query ignored.
 * [C:1]>>[C:1]         CC>>CC  0 hits                          No maps in target, hence no matches.
 * [C:?1]>>[C:?1]       CC>>CC  4 hits                          Query says mapped as shown or not present.
 * [C:1]>>[C:1]         [CH3:7][CH3:8]>>[CH3:7][CH3:8] 2 hits   Matches for target 7,7 and 8,8 atom pairs.
 * [C:1]>>[C:2]         [CH3:7][CH3:8]>>[CH3:7][CH3:8] 4 hits   When a query class is not found on both sides of the
 *                                                              query, it is ignored; this query does NOT say that the
 *                                                              atoms are in different classes.
 * [C:1][C:1]>>[C:1]    [CH3:7][CH3:7]>>[CH3:7][CH3:7] 4 hits   Atom maps match with "or" logic. All atoms  get bound to
 *                                                              class 7.
 * [C:1][C:1]>>[C:1]    [CH3:7][CH3:8]>>[CH3:7][CH3:8] 4 hits   The reactant atoms are bound to classes 7 and 8. Note that
 *                                                              having the first query atom bound to class 7 does not
 *                                                              preclude binding the second atom. Next, the product
 *                                                              atom can bind to classes 7 or 8.
 * [C:1][C:1]>>[C:1]    [CH3:7][CH3:7]>>[CH3:7][CH3:8] 2 hits   The reactants are bound to class 7. The product atom can
 *                                                              bind to class 7 only.
 * </pre>
 *
 * @see <a href="http://www.daylight.com/dayhtml/doc/theory/theory.smarts.html">Daylight Theory Manual</a>
 */
final class SmartsAtomAtomMapFilter implements Predicate<int[]> {

    private final List<MappedPairs> mapped = new ArrayList<>();
    private final IAtomContainer target;

    SmartsAtomAtomMapFilter(IAtomContainer query, IAtomContainer target) {

        Multimap<Integer,Integer> reactInvMap = null;
        Multimap<Integer,Integer> prodInvMap  = null;

        this.target = target;

        // transform query maps in to matchable data-structure
        final int numAtoms = query.getAtomCount();
        for (int idx = 0; idx < numAtoms; idx++) {
            IAtom atom = query.getAtom(idx);
            final int mapidx = mapidx(atom);
            if (mapidx == 0) continue;
            switch (role(atom)) {
                case Reactant:
                    if (reactInvMap == null) reactInvMap = ArrayListMultimap.create();
                    reactInvMap.put(mapidx, idx);
                    break;
                case Product:
                    if (prodInvMap == null) prodInvMap = ArrayListMultimap.create();
                    prodInvMap.put(mapidx, idx);
                    break;
            }
        }

        if (reactInvMap != null && prodInvMap != null) {
            for (Map.Entry<Integer, Collection<Integer>> e : reactInvMap.asMap().entrySet()) {
                int[] reacMaps = Ints.toArray(e.getValue());
                int[] prodMaps = Ints.toArray(prodInvMap.get(e.getKey()));
                if (prodMaps.length == 0)
                    continue; // unpaired
                mapped.add(new MappedPairs(reacMaps, prodMaps));
            }
        }
    }

    /**
     * Safely access the mapidx of an atom, returns 0 if null.
     *
     * @param atom atom
     * @return mapidx, 0 if undefined
     */
    private int mapidx(IAtom atom) {
        Integer mapidx = atom.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
        if (mapidx != null)
            return mapidx;
        return 0;
    }

    /**
     * Safely access the reaction role of an atom, returns {@link ReactionRole#None} if null.
     *
     * @param atom atom
     * @return mapidx, None if undefined
     */
    private ReactionRole role(IAtom atom) {
        ReactionRole role = atom.getProperty(CDKConstants.REACTION_ROLE);
        if (role != null)
            return role;
        return ReactionRole.None;
    }

    /**
     * Filters a structure match (described as an index permutation query -> target) for
     * those where the atom-atom maps are acceptable.
     *
     * @param perm permuation
     * @return whether the match should be accepted
     */
    @Override
    public boolean apply(int[] perm) {
        for (MappedPairs mpair : mapped) {

            // possibly 'or' of query maps, need to use a set
            if (mpair.rIdxs.length > 1) {
                // bind target reactant maps
                final Set<Integer> bound = new HashSet<>();
                for (int rIdx : mpair.rIdxs) {
                    int refidx = mapidx(target.getAtom(perm[rIdx]));
                    if (refidx == 0) return false; // unmapped in target
                    bound.add(refidx);
                }

                // check product maps
                for (int pIdx : mpair.pIdxs) {
                    if (!bound.contains(mapidx(target.getAtom(perm[pIdx]))))
                        return false;
                }
            }
            // no 'or' of query atom map (more common case)
            else {
                final int refidx = mapidx(target.getAtom(perm[mpair.rIdxs[0]]));
                if (refidx == 0) return false; // unmapped in target
                // pairwise mismatch
                if (refidx != mapidx(target.getAtom(perm[mpair.pIdxs[0]])))
                    return false;
                for (int i = 1; i < mpair.pIdxs.length; i++) {
                    if (refidx != mapidx(target.getAtom(perm[mpair.pIdxs[i]])))
                        return false;
                }
            }


        }
        return true;
    }

    /**
     * Helper class list all reactant atom indices (rIdxs) and product
     * atom indices (pIdxs) that are in the same Atom-Atom-Mapping class.
     */
    private final class MappedPairs {
        final int[] rIdxs, pIdxs;

        private MappedPairs(int[] rIdxs, int[] pIdxs) {
            this.rIdxs = rIdxs;
            this.pIdxs = pIdxs;
        }

        @Override
        public String toString() {
            return "{" + Arrays.toString(rIdxs) + "=>" + Arrays.toString(pIdxs) + "}";
        }
    }
}
