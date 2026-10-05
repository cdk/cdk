/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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

package org.openscience.cdk.isomorphism;


import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.Arrays;

/**
 * A state for the Vento-Foggia (VF) algorithm. The state allows adding and
 * removing of mappings as well as generating the new candidate mappings {@link
 * #nextN(int)} and {@link #nextM(int, int)}. The feasibility check is left for
 * subclasses to implement.
 *
 * @author John May
 */
abstract class AbstractVFState extends State {

    /** Value indicates a vertex is unmapped. */
    protected static final int UNMAPPED = -1;

    /** The molecules being matched (ideally backed by an adjacency list implementation) */
    protected final IAtomContainer mol1, mol2;

    /** Mapping - m1 is the the mapping from g1 to g1, m2 is from g2 to g1. */
    protected final int[]      m1, m2;

    /** The (terminal) vertices which are adjacent to each mapped pair. */
    protected final int[]      t1, t2;

    /** Size of current solution - the number of vertices matched. */
    protected int              size;

    /**
     * Create a state which will be used to match g1 in g2.
     *
     * @param mol1 find this graph
     * @param g2 search this graph
     */
    public AbstractVFState(final IAtomContainer mol1, final IAtomContainer g2) {
        this.mol1 = mol1;
        this.mol2 = g2;
        this.m1 = new int[mol1.getAtomCount()];
        this.m2 = new int[g2.getAtomCount()];
        this.t1 = new int[mol1.getAtomCount()];
        this.t2 = new int[g2.getAtomCount()];
        size = 0;
        Arrays.fill(m1, UNMAPPED);
        Arrays.fill(m2, UNMAPPED);
    }

    /**
     * Given the current query candidate (n), find the next candidate. The next
     * candidate is the next vertex > n (in some ordering) that is unmapped and
     * is adjacent to a mapped vertex (terminal). If there is no such vertex
     * (disconnected) the next unmapped vertex is returned. If there are no more
     * candidates m == |V| of G1.
     *
     * @param n previous candidate n
     * @return the next value of n
     */
    @Override
    final int nextN(int n) {
        if (size == 0) return 0;
        for (int i = n + 1; i < mol1.getAtomCount(); i++)
            if (m1[i] == UNMAPPED && t1[i] > 0) return i;
        for (int i = n + 1; i < mol1.getAtomCount(); i++)
            if (m1[i] == UNMAPPED) return i;
        return nMax();
    }

    /**
     * Given the current target candidate (m), find the next candidate. The next
     * candidate is the next vertex > m (in some ordering) that is unmapped and
     * is adjacent to a mapped vertex (terminal). If there is no such vertex
     * (disconnected) the next unmapped vertex is returned. If there are no more
     * candidates m == |V| of G2.
     *
     * @param m previous candidate m
     * @return the next value of m
     */
    @Override
    final int nextM(int n, int m) {
        if (size == 0) return m + 1;
        // if the query vertex 'n' is in the terminal set (t1) then the
        // target vertex must be in the terminal set (t2)
        for (int i = m + 1; i < mol2.getAtomCount(); i++)
            if (m2[i] == UNMAPPED && (t1[n] == 0 || t2[i] > 0)) return i;
        return mMax();
    }

    /**{@inheritDoc} */
    @Override
    final int nMax() {
        return mol1.getAtomCount();
    }

    /**{@inheritDoc} */
    @Override
    final int mMax() {
        return mol2.getAtomCount();
    }

    /**{@inheritDoc} */
    @Override
    final boolean add(int n, int m) {
        if (!feasible(n, m)) return false;
        m1[n] = m;
        m2[m] = n;
        size = size + 1;
        IAtom nAtom = mol1.getAtom(n);
        for (IBond bond : mol1.getConnectedBondsList(nAtom)) {
            int nidx = mol1.indexOf(bond.getOther(nAtom));
            if (t1[nidx] == 0) t1[nidx] = size;
        }
        IAtom mAtom = mol2.getAtom(m);
        for (IBond bond : mol2.getConnectedBondsList(mAtom)) {
            int nidx = mol2.indexOf(bond.getOther(mAtom));
            if (t2[nidx] == 0) t2[nidx] = size;
        }
        return true;
    }

    /**{@inheritDoc} */
    @Override
    final void remove(int n, int m) {
        m1[n] = m2[m] = UNMAPPED;
        size = size - 1;
        IAtom nAtom = mol1.getAtom(n);
        for (IBond bond : mol1.getConnectedBondsList(nAtom)) {
            int nidx = mol1.indexOf(bond.getOther(nAtom));
            if (t1[nidx] > size) t1[nidx] = 0;
        }
        IAtom mAtom = mol2.getAtom(m);
        for (IBond bond : mol2.getConnectedBondsList(mAtom)) {
            int nidx = mol2.indexOf(bond.getOther(mAtom));
            if (t2[nidx] > size) t2[nidx] = 0;
        }
    }

    /**
     * Is the candidate pair {n, m} feasible. Verifies if the adding candidate
     * pair {n, m} to the state would lead to an invalid mapping.
     *
     * @param n query vertex
     * @param m target vertex
     * @return the mapping is feasible
     */
    abstract boolean feasible(int n, int m);

    /**{@inheritDoc} */
    @Override
    int[] mapping() {
        return Arrays.copyOf(m1, m1.length);
    }

    /**{@inheritDoc} */
    @Override
    int size() {
        return size;
    }
}
