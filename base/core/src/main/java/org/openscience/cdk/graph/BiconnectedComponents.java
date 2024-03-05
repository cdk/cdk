/*
 * Copyright (C) 2024 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.graph;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Internal API - use {@link Cycles#markRingAtomsAndBonds}.
 * <br/>
 * Find and mark the atom/bonds in a molecule use the standard Hopcroft/Tarjan
 * algorithm to find biconnected components using articulation points.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Biconnected_component">
 *      Biconnected Component (Wiki)</a>
 * @author John Mayfield (né May)
 */
final class BiconnectedComponents {

    private int   remaining;
    private int   numBackEdges;
    private final int[] visit;

    private BiconnectedComponents(final IAtomContainer mol)
    {
        visit = new int[mol.getAtomCount()];
        remaining = mol.getAtomCount();
    }

    /**
     * Visit the next atom.
     *
     * @param atom  current atom
     * @param prev  bond we came from (nullable)
     * @param depth current depth
     * @return the lowest point reached
     */
    private int visit(final IAtom atom, final IBond prev, final int depth) {
        visit[atom.getIndex()] = depth;
        remaining--;
        int lo = depth + 1;
        for (IBond bond : atom.bonds()) {
            if (bond == prev)
                continue;
            final IAtom nbr   = bond.getOther(atom);
            final int   visit = this.visit[nbr.getIndex()];
            if (visit == 0) {
                final int res = visit(nbr, bond, depth + 1);
                bond.setIsInRing(res <= depth);
                lo = Math.min(res, lo);
            } else if (visit < depth) {
                numBackEdges++;
                bond.setIsInRing(true);
                lo = Math.min(visit, lo);
            }
        }
        atom.setIsInRing(lo <= depth);
        return lo;
    }

    static int mark(IAtomContainer mol)
    {
        BiconnectedComponents state = new BiconnectedComponents(mol);
        for (IAtom atom : mol.atoms()) {
            if (state.visit[atom.getIndex()] == 0)
                state.visit(atom, null, 1);
            // commonly we have a single connected component and visit all atoms
            // in the first traversal
            if (state.remaining == 0)
                break;
        }
        return state.numBackEdges;
    }
}
