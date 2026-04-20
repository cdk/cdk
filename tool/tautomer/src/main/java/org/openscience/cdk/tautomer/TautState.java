/*
 * Copyright (c) 2024 John Mayfield
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

package org.openscience.cdk.tautomer;

import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.Matching;
import org.openscience.cdk.graph.invariant.Canon;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static org.openscience.cdk.tautomer.Tautomers.Role.A;
import static org.openscience.cdk.tautomer.Tautomers.Role.C;
import static org.openscience.cdk.tautomer.Tautomers.Role.D;
import static org.openscience.cdk.tautomer.Tautomers.Role.X;

/**
 * Internal - The state of the Sayle-Delany (SD) tautomer generation algorithm.
 *
 * @author John Mayfield
 */
final class TautState {

    /** Proton donor and acceptor candidates. */
    final Integer[] candidates;

    /** When each atom was visited. */
    final int[] avisit;
    /** When each bond was visited. */
    final int[] bvisit;
    /** Zones for each atom. */
    final int[] zones;
    /** Number of donors in each zone. */
    final int[] nDonors;
    /** Number of acceptors in each zone. */
    final int[] nAcceptors;
    /** Current role of each atom. */
    final Tautomers.Role[] roles;
    /** Current size of the state. */
    private int size = 2;
    /** Pi (double) bond assignments - stored as a matching. */
    private Matching matching;
    /** The container we are augmenting. */
    private final IAtomContainer mol;

    TautStore inputState;

    TautState(IAtomContainer mol, Tautomers.Role[] roles) {
        this(mol, roles, mol.getAtomCount(), Tautomers.Order.SEQUENTIAL);
    }

    TautState(IAtomContainer mol) {
        this(mol, TautTypeMatcher.assignRoles(mol), mol.getAtomCount(), Tautomers.Order.SEQUENTIAL);
    }

    TautState(IAtomContainer mol, Tautomers.Role[] roles, int limit, Tautomers.Order order) {

        this.mol   = mol;
        this.roles = roles.clone();

        this.matching = Matching.withCapacity(mol.getAtomCount());
        this.avisit = new int[mol.getAtomCount()];
        this.bvisit = new int[mol.getBondCount()];
        Arrays.fill(avisit, 0);
        Arrays.fill(bvisit, 0);

        // identify and label conjugated systems (zones) where protons can move
        // freely
        this.zones = new int[mol.getAtomCount()];
        int numZones = labelZones(zones);

        // count the total number of proton donors/acceptors in each system
        this.nDonors = new int[numZones];
        this.nAcceptors = new int[numZones];
        int[] nDonorLast = new int[numZones];
        int[] nAcceptorLast = new int[numZones];

        for (int v = 0; v < mol.getAtomCount(); v++) {
            if (roles[v] == D) {
                nDonors[zones[v]]++;
                nDonorLast[zones[v]] = v;
            }
            if (roles[v] == A) {
                nAcceptors[zones[v]]++;
                nAcceptorLast[zones[v]] = v;
            }
        }


        // remove any components that have no donors or acceptor or that
        // have more movable hydrogens than the specified limit
        boolean[] skip = new boolean[numZones + 1];
        skip[0] = true;
        for (int component = 1; component < numZones; component++) {
            skip[component] = nDonors[component] == 0
                    || nAcceptors[component] == 0
                    || nDonors[component] + nAcceptors[component] > limit;
            
            if (nDonors[component] == 1 && nAcceptors[component] == 1 &&
                isSymmetricTerminal(nDonorLast[component], nAcceptorLast[component])) {
                skip[component] = true;
            }
        }
        for (int aidx = 0; aidx < mol.getAtomCount(); aidx++) {
            if (skip[zones[aidx]])
                avisit[aidx] = 1;
        }

        // enqueue all donor/acceptor atoms, labelled as candidates
        Integer[] queue = new Integer[mol.getAtomCount()];
        int n = 0;
        for (int v = 0; v < mol.getAtomCount(); v++) {
            if (avisit[v] != 0)
                continue;
            if (roles[v] == A || roles[v] == D)
                queue[n++] = v;
        }
        this.candidates = Arrays.copyOf(queue, n);
        this.inputState = new TautStore(mol);

        // no donors or acceptors
        if (n == 0)
            return;

        for (int bidx = 0; bidx < mol.getBondCount(); bidx++) {
            IBond bond = mol.getBond(bidx);
            IAtom beg = bond.getBegin();
            IAtom end = bond.getEnd();
            int bIdx = beg.getIndex();
            int eIdx = end.getIndex();
            if (roles[bIdx] != X && roles[eIdx] != X &&
                    avisit[bIdx] == 0 && avisit[eIdx] == 0) {
                bond.setOrder(null);
            } else {
                bvisit[bidx] = 1;
            }
        }


        if (order == Tautomers.Order.CANONICAL) {
            long[] labels = Canon.label(mol,
                                        GraphUtil.toAdjList(mol),
                                        invariants());
            Arrays.sort(this.candidates,
                        Comparator.comparingLong(a -> labels[a]));
        }

        if (order == Tautomers.Order.RANDOM) {
            // shuffles the queue, note we can end up placing some of the
            // hydrogens in one component/zone then another, optimally we would
            // place all of one component/zone before starting the next
            shuffle(candidates, n, new Random());
        } else {
            // SEQUENTIAL/SEQUENTIAL_NO_IDENTITY
            Arrays.sort(this.candidates, Comparator.<Integer>comparingInt(a -> zones[a])
                                                   .thenComparingInt(a -> enegPriority(a, mol.getAtom(a))));
        }
    }

    // augment canonical invariants,
    private long[] invariants() {
        int natoms = mol.getAtomCount();
        long[] labels = new long[natoms];

        for (int v = 0; v < natoms; v++) {
            IAtom atom = mol.getAtom(v);

            int deg  = mol.getAtom(v).getBondCount();
            int impH = atom.getImplicitHydrogenCount();
            int elem = atom.getAtomicNumber();
            int chg  = charge(atom);
            if (roles[v] == A) impH++;
            long label = 0;
            label |= deg;
            label <<= 4;
            label |= impH & 0xf;
            label <<= 7;   // atomic number <= 127 (7 bits)
            label |= elem & 0x7f;
            label <<= 1;   // charge sign == 1 (1 bit)
            label |= chg >> 31 & 0x1;
            label <<= 2;   // charge <= 3 (2 bits)
            label |= Math.abs(chg) & 0x3;
            labels[v] = label;
        }

        return labels;
    }

    private static int charge(IAtom atom) {
        return atom.getFormalCharge() != null ? atom.getFormalCharge() : 0;
    }


    // CO2H and CN2H3
    private boolean isSymmetricTerminal(int i, int j) {
        IAtom a = mol.getAtom(i);
        IAtom b = mol.getAtom(j);
        if (!a.getAtomicNumber().equals(b.getAtomicNumber()))
            return false;
        if (a.getBondCount() != 1 || b.getBondCount() != 1)
            return false;
        for (IBond bond : a.bonds()) {
            if (bond.getOther(a).getBond(b) == null)
                return false;
        }
        return true;
    }

    private static <T> void swap(T[] arr, int i, int j)
    {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    private static <T> void shuffle(T[] arr, int n, Random rnd)
    {
        for (int i = n; i > 1; i--)
            swap(arr, i - 1, rnd.nextInt(i));
    }

    /**
     * Index of the next proton donor or acceptor to assign.
     *
     * @return vertex, the atom index
     */
    int select() {
        for (final int v : candidates)
            if (avisit[v] == 0) return v;
        return -1;
    }

    /**
     * Add the vertex, v, to the state with the specified 'role'. The state
     * checks for feasibility and determines if the role is acceptable and 
     * updates other atoms that are dependant on this role.
     *
     * @param v    vertex, the atom index
     * @param type try adding v as proton donor or acceptor
     * @return v was successfully added as the specified role
     */
    boolean add(final int v, final Tautomers.Role type) {

        if (v < 0) return false;

        assert roles[v] == A || roles[v] == D;
        assert type == A || type == D;

        if (feasible(v, type)) {
            size++;
        } else {
            remove(v);
        }

        return avisit[v] != 0 && size > avisit[v];
    }

    boolean add(final IBond bond, final IBond.Order order) {

        if (bond == null || order == null) throw new IllegalArgumentException();

        if (visited(bond))
            return false; // Maybe OK - but should be checked not added for backtracking!

        if (feasible(bond, order))
            size++;
        else
            remove(bond);

        return bvisit[bond.getIndex()] != 0 && size > bvisit[bond.getIndex()];
    }

    /**
     * Remove the assignment of vertex v. The state is reset to before the
     * assigned was made.
     *
     * @param v proton donor or acceptor
     */
    void remove(final int v) {
        assert avisit[v] != 0;
        assert roles[v] == A || roles[v] == D;
        reset(avisit[v]);
    }

    void remove(final IBond b) {
        reset(bvisit[b.getIndex()]);
    }

    private void reset(int size) {
        this.size = size;
        resetAtoms(size);
        resetBonds(size);
    }

    private void resetBonds(int size) {
        for (int bidx = 0; bidx < bvisit.length; bidx++) {
            if (bvisit[bidx] < size)
                continue;
            bvisit[bidx] = 0;
            IBond bond = mol.getBond(bidx);
            int beg = bond.getBegin().getIndex();
            int end = bond.getEnd().getIndex();
            if (bond.getOrder() == IBond.Order.DOUBLE){
                matching.unmatch(beg);
                matching.unmatch(end);
            }
            bond.setOrder(null);
        }
    }

    private void resetAtoms(int size) {
        for (int aidx = 0; aidx < avisit.length; aidx++) {
            if (avisit[aidx] < size)
                continue;
            avisit[aidx] = 0;
            if (roles[aidx] == D)
                nDonors[zones[aidx]]++;
            else if (roles[aidx] == A)
                nAcceptors[zones[aidx]]++;
        }
    }

    /**
     * Access the current type of vertex, v.
     *
     * @param v vertex, the atom index
     * @return the type of the vertex
     */
    Tautomers.Role roleOf(final int v) {
        return roles[v];
    }

    /**
     * State is complete when there are no unvisited proton donors or
     * acceptors.
     *
     * @return status
     */
    boolean complete() {
        return select() == -1 && candidates.length != 0;
    }

    /**
     * Determine whether the type assignment of the vertex, v, is feasible.
     *
     * @param v    vertex, an atom index of a donor or acceptor
     * @param type v as a donor or acceptor
     * @return the assignment is feasible
     */
    private boolean feasible(final int v, final Tautomers.Role type) {

        if (!setRole(v, type))
            return false;

        final int zone = zones[v];
        assert zone != 0;

        // more hydrogen donors or acceptors assigned than were available
        if (nDonors[zone] < 0 || nAcceptors[zone] < 0)
            return false;

        // when a hydrogen donor or acceptor is set it may decide the
        // role of another vertex in the zone. The donor and acceptor
        // counts may have changed and are retested  
        if (!assignDependantRoles(v) || nDonors[zone] < 0 || nAcceptors[zone] < 0)
            return false;

        // all hydrogen donors or acceptors of this zone have been placed,
        // all unvisited candidates must be acceptors or donors and we must be
        // able to assign a Kekulé structure 
        if (nDonors[zone] == 0 || nAcceptors[zone] == 0) {
            final Tautomers.Role other = nDonors[zone] == 0 ? A : D;
            for (int aidx = 0; aidx < avisit.length; aidx++) {
                if (zones[aidx] == zone && avisit[aidx] == 0 && roles[aidx] != C) {
                    if (!setRole(aidx, other))
                        return false;
                    if (!assignDependantRoles(aidx))
                        return false;
                }
            }
            return kekulizeZone(zone);
        }

        return true;
    }

    /**
     * Check if an atom (@ aidx) is visited and has the given role.
     * @param aidx the atom index
     * @param role the role
     * @return the atom is that role or not
     */
    private boolean is(int aidx, Tautomers.Role role) {
        return avisit[aidx] > 0 && roles[aidx] == role;
    }

    /**
     * Recursively update the vertex, v and its neighbors. If possible double
     * bonds are assigned - this may decide proton donor or acceptors status of
     * unvisited vertices. The method will also check for unacceptable
     * assignment and returns false, if a contradiction was identified.
     *
     * @param v       vertex, the atom index
     * @return assignment was feasible
     */
    private boolean assignDependantRoles(final int v) {
        final IAtom   atom = mol.getAtom(v);
        final boolean hasDoubleBond = matching.matched(v);

        // a donor or hasDoubleBond acceptor/conjugated atom, then all unvisited
        // bonds must be single
        if (hasDoubleBond || is(v, D)) {
            // System.err.println(v + " must have all single bonds");
            if (avisit[v] == 0) {
                if (roles[v] != C && !setRole(v, A))
                    return false;
                avisit[v] = size;
            }
            for (IBond bond : atom.bonds()) {
                if (visited(bond))
                    continue;
                bvisit[bond.getIndex()] = size;
                bond.setOrder(IBond.Order.SINGLE);
                if (!assignDependantRoles(bond.getOther(atom).getIndex()))
                    return false;
            }
            return true;
        }

        final IBond[] unsetBonds = new IBond[atom.getBondCount()];
        int n = 0;

        // identify neighbors in this component where bond order of v-w is not known
        for (IBond bond : atom.bonds()) {
            if (bvisit[bond.getIndex()] == 0)
                unsetBonds[n++] = bond;
        }

        // must be a donor: no double bonds, no unknown neighbors, and not conjugated
        if (avisit[v] == 0 && roles[v] != C && n == 0) {
            return setRole(v, D); // assignDependantRoles(v);
        }

        // must need double bond, a conjugated atom or assigned acceptor without an assigned pi bond
        if (roles[v] == C || is(v, A)) {

            // v needs a pi bond but there are no unsetBonds bonds, infeasible
            if (n == 0 ) {
                // System.err.println(v + " wanted a pi bond but had none!");
                return false;
            }

            // v needs a pi bond and there is exactly one unsetBonds neighbor
            // we can assign that a double bond
            if (n == 1) {
                // assert matching.unmatched(unknown[0]);
                IBond bond = unsetBonds[0];
                int w = bond.getOther(atom).getIndex();

                // must be an acceptor
                if (roles[w] != C && avisit[w] == 0) {
                    // System.err.println(v + " must be an acceptor");
                    if (!setRole(w, A))
                        return false;
                }

                // required for remove() to clear the matching
                // if (avisit[w] == 0) avisit[w] = size;

                bvisit[bond.getIndex()] = size;
                matching.match(v, w);
                bond.setOrder(IBond.Order.DOUBLE);

                return assignDependantRoles(w);
            }
        }

        return true;
    }

    private boolean canHavePiBond(IAtom atom) {
        int idx = atom.getIndex();
        // already has a pi-bond (matched)?
        if (matching.matched(idx))
            return false;
        if (roles[idx] == C)
            return true;
        return is(idx, A) || nAcceptors[zones[idx]] > 0;
    }

    private boolean feasible(final IBond bond, final IBond.Order order) {

        IAtom beg = bond.getBegin();
        IAtom end = bond.getEnd();
        if (order == IBond.Order.DOUBLE) {
            // if either end atom can not have a pi-bond then fail
            if (!canHavePiBond(beg) || !canHavePiBond(end))
                return false;
            matching.match(beg.getIndex(), end.getIndex());
        }
        bvisit[bond.getIndex()] = size;
        bond.setOrder(order);

        if (!assignDependantRoles(beg.getIndex()))
            return false;
        if (!assignDependantRoles(end.getIndex()))
            return false;

        int component = zones[beg.getIndex()];
        if (component != zones[end.getIndex()]) {
            throw new IllegalStateException();
        }

        // all hydrogen donors or acceptors of this component have been placed,
        // all unvisited candidates must be acceptors or donors and we must be
        // able to assign a Kekulé structure
        if (component > 0 && (nDonors[component] == 0 || nAcceptors[component] == 0)) {
            final Tautomers.Role other = nDonors[component] == 0 ? A : D;
            for (int aidx = 0; aidx < avisit.length; aidx++) {
                if (zones[aidx] != component)
                    continue;
                if (roles[aidx] == C)
                    continue;
                if (avisit[aidx] == 0 && roles[aidx] != C) {
                    if (!setRole(aidx, other))
                        return false;
                    if (!assignDependantRoles(aidx))
                        return false;
                }
            }
            return kekulizeZone(component);
        }

        return true;
    }

    private boolean assignBondOrders(IAtom atom) {
        final int aidx = atom.getIndex();
        assert roles[aidx] == C || roles[aidx] == A;
        for (IBond bond : atom.bonds()) {
            if (visited(bond))
                continue;
            int mark = ++size;
            bvisit[bond.getIndex()] = mark;
            IAtom nbor = bond.getOther(atom);
            int nidx = nbor.getIndex();
            if (!matching.matched(aidx) && !matching.matched(nidx)) {
                matching.match(aidx, nidx);
                bond.setOrder(IBond.Order.DOUBLE);
                if (assignBondOrders(nbor))
                    continue;
                resetBonds(mark);
            }
            bvisit[bond.getIndex()] = mark;
            bond.setOrder(IBond.Order.SINGLE);
            if (!assignBondOrders(nbor)) {
                return false;
            }
        }
        return matching.matched(aidx);
    }

    private boolean kekulizeZone(final int component) {
        if (nDonors[component] != 0)
            throw new IllegalArgumentException("Component has unplaced donors: " + mol.getTitle());
        if (nAcceptors[component] != 0)
            throw new IllegalArgumentException("Component has unplaced acceptors: " + mol.getTitle());
        for (int aidx = 0; aidx < avisit.length; aidx++) {
            if (zones[aidx] != component)
                continue;
            if (avisit[aidx] != 0 && roles[aidx] != A)
                continue;
            if (!assignBondOrders(mol.getAtom(aidx)))
                return false;
        }
        return true;
    }

    private boolean visited(IBond bond) {
        return bvisit[bond.getIndex()] != 0;
    }

    /**
     * Set role (acceptor / donor) of an vertex.
     *
     * @param v    vertex, the atom index
     * @param type set to a proton donor or acceptor
     */
    private boolean setRole(final int v, final Tautomers.Role type) {
        assert type == A || type == D;
        assert avisit[v] == 0;
        avisit[v] = size;
        if (type == D) {
            if (roles[v] == A)
                accept(v);
            return --nDonors[zones[v]] >= 0;
        }
        else { // Acceptor
            if (roles[v] == D)
                donate(v);
            return --nAcceptors[zones[v]] >= 0;
        }
    }

    /**
     * Accept a proton, vertex 'v' is now a proton donor.
     *
     * @param v vertex, the atom index
     */
    private void accept(final int v) {
        assert roles[v] == A;
        final IAtom atom = mol.getAtom(v);
        assert atom.getImplicitHydrogenCount() != null;
        atom.setImplicitHydrogenCount(atom.getImplicitHydrogenCount() + 1);
        roles[v] = D;
    }

    /**
     * Donate a proton, vertex 'v' is now a proton acceptor.
     *
     * @param v vertex, the atom index
     */
    private void donate(final int v) {
        assert roles[v] == D;
        final IAtom atom = mol.getAtom(v);
        assert atom.getImplicitHydrogenCount() != null;
        atom.setImplicitHydrogenCount(atom.getImplicitHydrogenCount() - 1);
        roles[v] = A;
    }

    IAtomContainer container() {
        return mol;
    }

    // refactor out

    private int labelZones(int[] labels) {
        int label = 1;
        for (int v = 0; v < mol.getAtomCount(); v++) {
            if (labels[v] == 0 && roles[v] == D)
                labelComponent(mol.getAtom(v), null, label++, labels, roles);
        }
        return label;
    }

    private static void labelComponent(IAtom atom, IBond prev,
                                       int label, int[] labels, Tautomers.Role[] types) {
        labels[atom.getIndex()] = label;
        for (IBond bond : atom.bonds()) {
            if (bond == prev)
                continue;
            IAtom nbor = bond.getOther(atom);
            int nidx = nbor.getIndex();
            if (labels[nidx] != 0 || types[nidx] == Tautomers.Role.X)
                continue;
            labelComponent(nbor, bond, label, labels, types);
        }
    }

    static void check(IAtomContainer mol)
    {
        for (IAtom a : mol.atoms())
            if (a.getImplicitHydrogenCount() == null)
                throw new NullPointerException("Atom idx=" + a.getIndex() + " had unset implicit hydrogen count");
        for (IBond b : mol.bonds())
            if (b.getOrder() == null)
                throw new NullPointerException("Bond idx=" + b.getIndex() + " had unset bond order");
    }

    // clear the existing atom type and aromaticity information, we are 
    // going to be moving hydrogens around that will change the atom type
    // and break aromaticity
    static void clearTypeInfo(IAtomContainer mol) {

        // tautomers can break aromaticity, so clear it
        Aromaticity.clear(mol);

        for (IAtom a : mol.atoms()) {
            // valence, hybridisation does not change
            a.setAtomTypeName(null);
            a.setFormalNeighbourCount(null);
        }
    }

    private int enegPriority(int v, IAtom atom) {
        if (roles[v] == X || roles[v] == C)
            return 15;
        // N < Te < Se < S < C (maybe aromatic) < O < C
        switch (atom.getAtomicNumber()) {
            case IAtom.N:
                return 1;
            case IAtom.Te:
                return 2;
            case IAtom.Se:
                return 3;
            case IAtom.S:
                return 4;
            case IAtom.O:
                return 6;
            case IAtom.C:
                return atom.isInRing() ? 7 : 5;
        }
        return 0;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        //for (int i = 0; i < size; i++)
        //    sb.append('.');
        for (int i = 0; i < candidates.length; i++) {
            if (avisit[candidates[i]] != -1)
                sb.append(roles[candidates[i]] == D ? "d" : "a");
            else
                sb.append("-");
        }
        sb.append(" : ");
        for (int i = 1; i < nAcceptors.length; i++) {
            if (i > 1)
                sb.append(' ');
            sb.append(nDonors[i]).append("/").append(nAcceptors[i]);
        }
        return sb.toString();
    }

    static class TautStore {
        private final int AROM_MASK = 0xf0;
        private final int[] atype;
        private final int[] btype;

        TautStore(IAtomContainer mol) {
            this.atype = new int[mol.getAtomCount()];
            this.btype = new int[mol.getBondCount()];
            for (int i = 0; i < atype.length; i++) {
                atype[i] = mol.getAtom(i).getImplicitHydrogenCount();
                atype[i] |= mol.getAtom(i).isAromatic() ? AROM_MASK : 0;
            }
            for (int i = 0; i < btype.length; i++) {
                IBond bond = mol.getBond(i);
                btype[i] = (byte) (bond.getOrder().numeric() & 0xf);
                if (bond.isAromatic())
                    btype[i] |= AROM_MASK;
            }
        }

        void apply(IAtomContainer mol) {
            for (int i = 0; i < atype.length; i++) {
                IAtom atom = mol.getAtom(i);
                atom.setImplicitHydrogenCount(atype[i] & 0x0f);
                if ((atype[i] & AROM_MASK) != 0)
                    atom.set(IChemObject.AROMATIC);
                else
                    atom.clear(IChemObject.AROMATIC);
            }
            for (int i = 0; i < btype.length; i++) {
                IBond bond = mol.getBond(i);
                switch (btype[i]) {
                    case 0x01:
                        bond.setOrder(IBond.Order.SINGLE);
                        bond.clear(IChemObject.AROMATIC);
                        break;
                    case 0x02:
                        bond.setOrder(IBond.Order.DOUBLE);
                        bond.clear(IChemObject.AROMATIC);
                        break;
                    case 0x03:
                        bond.setOrder(IBond.Order.TRIPLE);
                        break;
                    case 0x04:
                        bond.setOrder(IBond.Order.QUADRUPLE);
                        break;
                    case 0xf1:
                        bond.setOrder(IBond.Order.SINGLE);
                        bond.set(IChemObject.AROMATIC);
                        break;
                    case 0xf2:
                        bond.setOrder(IBond.Order.DOUBLE);
                        bond.set(IChemObject.AROMATIC);
                        break;
                }
            }
        }
    }
}
