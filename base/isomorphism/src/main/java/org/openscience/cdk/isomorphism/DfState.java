/*
 * Copyright (C) 2018 NextMove Software
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk.isomorphism;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Internals of the DF ("Depth-First" {@cdk.cite Jeliazkova18}) substructure
 * matching algorithm. The algorithm is a simple but elegant backtracking search
 * iterating over the bonds of a query. Like the popular VF2 the algorithm, it
 * uses linear memory but unlike VF2 bonded atoms are selected from the
 * neighbor lists of already mapped atoms.
 * <br><br>
 * In practice VF2 take O(N<sup>2</sup>) to match a linear chain against it's self
 * whilst this algorithm is O(N).
 * <br><br>
 * Usage:
 * <pre>{@code
 * DfState state = new DfState(query);
 * state.setMol(mol);
 * int count = 0;
 * while (state.matchNext()) {
 *   state.amap; // permutation of query to molecule
 *   ++count;
 * }
 * }</pre>
 * <b>References</b>
 * <ul>
 *     <li>{@cdk.cite Ray57}</li>
 *     <li>{@cdk.cite Ullmann76}</li>
 *     <li>{@cdk.cite Cordella04}</li>
 *     <li>{@cdk.cite Jeliazkova18}</li>
 * </ul>
 *
 * @author John Mayfield
 * @see DfPattern
 */
final class DfState implements Iterable<int[]> {

    private static final int UNMAPPED = -1;

    private final IAtomContainer query;
    private final IQueryBond[]   qbonds;
    private final int            numAtoms;
    private       int            numBonds;
    private int                  numMapped;
    private final int[]          amap;

    private IAtomContainer mol;
    private boolean[]      avisit;

    // To make the algorithm re-entrant we need to
    // manage our own stack
    private static final class StackFrame {
        private int      bidx;
        private IAtom    atom;
        private Iterator iter;

        private StackFrame(StackFrame frame) {
            this.bidx = frame.bidx;
            this.atom = frame.atom;
            this.iter = frame.iter;
        }

        private StackFrame() {}
    }

    private int          sptr;
    private StackFrame[] stack;

    DfState(IQueryAtomContainer query) {

        IChemObjectBuilder builder = query.getBuilder();
        if (builder == null)
            throw new IllegalArgumentException();

        IAtomContainer tmp = builder.newAtomContainer();
        tmp.add(query);
        this.qbonds = new IQueryBond[tmp.getBondCount()];
        this.amap = new int[query.getAtomCount()];

        int stackSize = 0;
        for (IAtom atom : tmp.atoms()) {
            if (atom instanceof IQueryAtom) {
                if (amap[atom.getIndex()] == 0) {
                    stackSize += prepare(atom, null) + 1;
                }
            } else
                throw new IllegalArgumentException();
        }

        this.stack = new StackFrame[stackSize + 2];
        for (int i = 0; i < stack.length; i++)
            this.stack[i] = new StackFrame();

        this.numAtoms = amap.length;
        this.query = tmp;
    }

    /**
     * Copy constructor, if a state has already been prepared the internals
     * can be copied and the separate instance used in a thread-safe manner.
     *
     * @param state the state
     */
    DfState(DfState state) {
        // only need shallow copy of the query bonds
        this.qbonds = Arrays.copyOf(state.qbonds, state.qbonds.length);
        this.query = state.query;
        this.numBonds = state.numBonds;
        this.numAtoms = state.numAtoms;
        this.numMapped = state.numMapped;
        this.amap = state.amap.clone();
        this.avisit = state.avisit != null ? state.avisit.clone() : null;
        this.mol = state.mol;
        // deep copy of the stack-frame
        this.stack = new StackFrame[state.stack.length];
        for (int i = 0; i < stack.length; i++)
            this.stack[i] = new StackFrame(state.stack[i]);
        this.sptr = state.sptr;
    }

    // prepare the query, the required stack size is returned
    private int prepare(IAtom atom, IBond prev) {
        int count = 0;
        amap[atom.getIndex()] = 1;
        for (IBond bond : atom.bonds()) {
            if (bond == prev)
                continue;
            IAtom nbr = bond.getOther(atom);
            if (amap[nbr.getIndex()] == 0) {
                qbonds[numBonds++] = (IQueryBond) bond;
                count += prepare(nbr, bond) + 1;
            } else if (nbr.getIndex() < atom.getIndex()) {
                ++count; // ring closure
                qbonds[numBonds++] = (IQueryBond) bond;
            }
        }
        return count;
    }

    /**
     * Set the molecule to be matched.
     * @param mol the molecule
     */
    void setMol(IAtomContainer mol) {
        this.mol = mol;
        Arrays.fill(amap, -1);
        numMapped = 0;
        this.avisit = new boolean[mol.getAtomCount()];
        sptr = 0;
        store(0, null);
    }

    /**
     * Set the molecule to be matched and the 'root' atom at which the match
     * must start (e.g. query atom 0). It is presumed the root atom has already
     * been tested against the query atom and matched.
     *
     * @param atom the root atom.
     */
    void setRoot(IAtom atom) {
        setMol(atom.getContainer());
        numMapped = 1;
        int aidx = atom.getIndex();
        avisit[aidx] = true;
        amap[0] = aidx;
    }

    private int currBondIdx() {
        return stack[sptr].bidx;
    }

    @SuppressWarnings("unchecked")
    private Iterator<IAtom> atoms() {
        if (stack[sptr].iter == null)
            stack[sptr].iter = mol.atoms().iterator();
        return stack[sptr].iter;
    }

    @SuppressWarnings("unchecked")
    private Iterator<IBond> bonds(IAtom atom) {
        if (stack[sptr].iter == null)
            stack[sptr].iter = atom.bonds().iterator();
        return stack[sptr].iter;
    }

    /**
     * Store the specified bond index and mapped query atom (optional)
     * on the stack.
     * @param bidx bond index
     * @param queryatom query atom - can be null
     */
    private void store(int bidx, IQueryAtom queryatom) {
        ++sptr;
        stack[sptr].bidx = bidx;
        stack[sptr].iter = null;
        if (queryatom != null)
            stack[sptr].atom = queryatom;
        else
            stack[sptr].atom = null;
    }

    /**
     * Pops a stack frame until a the query/mol atom pairing is unmapped
     * or we reach the bottom of the stack
     */
    private void backtrack() {
        IAtom qatom = stack[sptr].atom;
        --sptr;
        if (qatom != null) {
            --numMapped;
            avisit[amap[qatom.getIndex()]] = false;
            amap[qatom.getIndex()] = UNMAPPED;
        } else if (sptr != 0) {
            backtrack();
        }
    }

    /**
     * Determine if a atom from the molecule is unvisited and if it is matched
     * by the query atom. If the match is feasible the provided query bond index
     * stored on the stack.
     *
     * @param qatom atom from the query
     * @param atom atom from the molecule
     * @return the match was feasible and the state was stored
     */
    private boolean feasible(int bidx, IQueryAtom qatom, IAtom atom) {
        int aidx = atom.getIndex();
        if (avisit[aidx] || !qatom.matches(atom))
            return false;
        ++numMapped;
        amap[qatom.getIndex()] = aidx;
        avisit[aidx] = true;
        store(bidx, qatom);
        return true;
    }

    /**
     * Determine if a bond from the molecule exists and if it is matched
     * by the query bond. If the match is feasible the current query bond index
     * is increment and stored on the stack.
     *
     * @param qbond bond from the query
     * @param bond bond from the molecule
     * @return the match was feasible and the state was stored
     */
    private boolean feasible(IQueryBond qbond, IBond bond) {
        if (bond == null || !qbond.matches(bond))
            return false;
        store(currBondIdx() + 1, null);
        return true;
    }

    /**
     * Primary match function, if this function returns true the algorithm
     * has found a match. Calling it again will backtrack and find the next
     * match.
     *
     * @return a mapping was found
     */
    boolean matchNext() {
        if (numAtoms == 0)
            return false;
        if (sptr > 1)
            backtrack();
        main:
        while (sptr != 0) {
            final int bidx = currBondIdx();

            if (bidx == numBonds) {

                // done
                if (numMapped == numAtoms)
                    return true;

                // handle disconnected atoms
                for (IAtom qatom : query.atoms()) {
                    if (amap[qatom.getIndex()] == UNMAPPED) {
                        Iterator<IAtom> iter = atoms();
                        while (iter.hasNext()) {
                            IAtom atom = iter.next();
                            if (feasible(bidx, (IQueryAtom) qatom, atom))
                                continue main;
                        }
                        break;
                    }
                }
                backtrack();
                continue;
            }

            IQueryBond qbond = qbonds[bidx];
            IQueryAtom qbeg  = (IQueryAtom) qbond.getBegin();
            IQueryAtom qend  = (IQueryAtom) qbond.getEnd();

            int begIdx = amap[qbeg.getIndex()];
            int endIdx = amap[qend.getIndex()];

            // both atoms matched, there must be a bond between them
            if (begIdx != UNMAPPED && endIdx != UNMAPPED) {
                IBond bond = mol.getAtom(begIdx).getBond(mol.getAtom(endIdx));
                if (feasible(qbond, bond))
                    continue;
            }
            // 'beg' is mapped, find a feasible 'end' from it's neighbor list
            else if (begIdx != UNMAPPED) {
                IAtom           beg   = mol.getAtom(begIdx);
                Iterator<IBond> biter = bonds(beg);
                while (biter.hasNext()) {
                    IBond bond = biter.next();
                    IAtom end  = bond.getOther(beg);
                    if (qbond.matches(bond) && feasible(bidx + 1, qend, end))
                        continue main;
                }
            }
            // 'end' is mapped, find a feasible 'beg' from it's neighbor list
            else if (endIdx != UNMAPPED) {
                IAtom           end   = mol.getAtom(endIdx);
                Iterator<IBond> biter = bonds(end);
                while (biter.hasNext()) {
                    IBond bond = biter.next();
                    IAtom beg  = bond.getOther(end);
                    if (qbond.matches(bond) && feasible(bidx + 1, qbeg, beg))
                        continue main;
                }
            }
            // 'beg' nor 'end' matched, find a feasible mapping from
            // any atom in the molecule
            else {
                Iterator<IAtom> aiter = atoms();
                while (aiter.hasNext()) {
                    if (feasible(bidx, qbeg, aiter.next()))
                        continue main;
                }
            }
            backtrack();
        }
        return false;
    }

    /**
     * Adapter to current CDK {@link Pattern} that takes and iterator of an
     * int[] permutation from the query to the molecule.
     * @return the iterator
     */
    @Override
    public Iterator<int[]> iterator() {
        final DfState lstate = new DfState(this);
        return new Iterator<int[]>() {
            boolean hasNext;

            @Override
            public boolean hasNext() {
                return hasNext || (hasNext = lstate.matchNext());
            }

            @Override
            public int[] next() {
                if (!hasNext())
                    return new int[0];
                hasNext = false;
                return lstate.amap;
            }

            @Override
            public void remove() {
                throw new IllegalArgumentException();
            }
        };
    }
}
