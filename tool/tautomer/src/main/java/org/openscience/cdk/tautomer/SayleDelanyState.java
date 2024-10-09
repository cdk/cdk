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

import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.Matching;
import org.openscience.cdk.graph.invariant.Canon;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static org.openscience.cdk.tautomer.Tautomers.Role.Acceptor;
import static org.openscience.cdk.tautomer.Tautomers.Role.Conjugated;
import static org.openscience.cdk.tautomer.Tautomers.Role.Donor;
import static org.openscience.cdk.tautomer.Tautomers.Role.None;

/**
 * A state of the Sayle-Delany (SD) tautomer generation algorithm. 
 *
 * @author John Mayfield
 */
final class SayleDelanyState {

    /** Proton donor and acceptor candidates. */
    final Integer[] candidates;

    /** When each atom was visited. */
    private final int[] avisit;
    /** When each bond was visited. */
    private final int[] bvisit;

    /** Component label of each vertex. */
    private final int[] components;

    /** Number of donors and acceptors in each component. */
    private final int[] nDonors, nAcceptors;

    /** Type of each atom. */
    private final Tautomers.Role[] roles;

    /** Size of the state. */
    private int size = 2;

    /** Pi (double) bond assignments - stored as a matching. */
    private Matching matching;

    /** The container we are augmenting. */
    private final IAtomContainer mol;

    SayleDelanyState(IAtomContainer mol, Tautomers.Role[] roles) {
        this(mol, roles, mol.getAtomCount(), Tautomers.Order.SEQUENTIAL);
    }

    SayleDelanyState(IAtomContainer mol) {
        this(mol, AtomTypeMatcher.assignRoles(mol), mol.getAtomCount(), Tautomers.Order.SEQUENTIAL);
    }

    SayleDelanyState(IAtomContainer mol, Tautomers.Role[] roles, int limit, Tautomers.Order order) {

        this.mol = mol;
        this.roles = roles;

        this.matching = Matching.withCapacity(mol.getAtomCount());
        this.avisit = new int[mol.getAtomCount()];
        this.bvisit = new int[mol.getBondCount()];
        Arrays.fill(avisit, 0);
        Arrays.fill(bvisit, 0);

        // identify and label conjugated systems where protons can move freely
        this.components = new int[mol.getAtomCount()];
        int nComponents = labelComponents(components);

        // count the total number of proton donors/acceptors in each system
        this.nDonors = new int[nComponents];
        this.nAcceptors = new int[nComponents];
        int[] nDonorLast = new int[nComponents];
        int[] nAcceptorLast = new int[nComponents];

        for (int v = 0; v < mol.getAtomCount(); v++) {
            if (roles[v] == Donor) {
                nDonors[components[v]]++;
                nDonorLast[components[v]] = v;
            }
            if (roles[v] == Acceptor) {
                nAcceptors[components[v]]++;
                nAcceptorLast[components[v]] = v;
            }
        }
        // remove any components that have no donors or acceptor or that
        // have more movable hydrogens than the specified limit
        boolean[] skip = new boolean[nComponents + 1];
        skip[0] = true;
        for (int component = 1; component < nComponents; component++) {
            skip[component] = nDonors[component] == 0
                    || nAcceptors[component] == 0
                    || nDonors[component] + nAcceptors[component] > limit;
            
            if (nDonors[component] == 1 && nAcceptors[component] == 1 &&
                isSymmetricTerminal(nDonorLast[component], nAcceptorLast[component])) {
                skip[component] = true;
            }
        }
        for (int aidx = 0; aidx < mol.getAtomCount(); aidx++) {
            if (skip[components[aidx]])
                avisit[aidx] = 1;
        }

        // enqueue all donor/acceptor atoms, labelled as candidates
        Integer[] queue = new Integer[mol.getAtomCount()];
        int n = 0;
        for (int v = 0; v < mol.getAtomCount(); v++) {
            if (avisit[v] != 0)
                continue;
            if (roles[v] == Acceptor || roles[v] == Donor)
                queue[n++] = v;
        }
        this.candidates = Arrays.copyOf(queue, n);

        // no donors or acceptors
        if (n == 0) return;

        for (int bidx = 0; bidx < mol.getBondCount(); bidx++) {
            IBond bond = mol.getBond(bidx);
            IAtom beg = bond.getBegin();
            IAtom end = bond.getEnd();
            if (roles[beg.getIndex()] != None && roles[end.getIndex()] != None &&
                    avisit[beg.getIndex()] == 0 && avisit[end.getIndex()] == 0) {
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
            Arrays.sort(this.candidates, Comparator.<Integer>comparingInt(a -> components[a])
                                                   .thenComparingInt(a -> eneg(a, mol.getAtom(a))));
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
            if (roles[v] == Acceptor) impH++;
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

        assert roles[v] == Acceptor || roles[v] == Donor;
        assert type == Acceptor || type == Donor;

        if (feasible(v, type))
            size++;
        else
            remove(v);

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

        return avisit[bond.getIndex()] != 0 && size > bvisit[bond.getIndex()];
    }

    /**
     * Remove the assignment of vertex v. The state is reset to before the
     * assigned was made.
     *
     * @param v proton donor or acceptor
     */
    void remove(final int v) {
        assert avisit[v] != 0;
        assert roles[v] == Acceptor || roles[v] == Donor;
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
            if (roles[aidx] == Donor)
                nDonors[components[aidx]]++;
            else if (roles[aidx] == Acceptor)
                nAcceptors[components[aidx]]++;
            matching.unmatch(aidx);
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

        setRole(v, type);

        final int component = components[v];
        assert component != 0;

        // more hydrogen donors or acceptors assigned than were available
        if (nDonors[component] < 0 || nAcceptors[component] < 0)
            return false;

        // when a hydrogen donor or acceptor is set it may decide the
        // role of another vertex in the component. The donor and acceptor
        // counts may have changed and are retested  
        if (!assignDependantRoles(v) || nDonors[component] < 0 || nAcceptors[component] < 0)
            return false;

        // all hydrogen donors or acceptors of this component have been placed,
        // all unvisited candidates must be acceptors or donors and we must be
        // able to assign a Kekulé structure 
        if (nDonors[component] == 0 || nAcceptors[component] == 0) {
            final Tautomers.Role other = nDonors[component] == 0 ? Acceptor : Donor;
            for (int w = 0; w < avisit.length; w++)
                if (components[w] == component && avisit[w] == 0 && roles[w] != Conjugated) {
                    setRole(w, other);
                    if (!assignDependantRoles(w) || nDonors[component] < 0 || nAcceptors[component] < 0)
                        return false;
                }
            return kekulizeComponent(component);
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
        IAtom atom = mol.getAtom(v);
        final boolean matched = matching.matched(v);

        // a donor or matched acceptor/conjugated atom, then all unvisited bonds
        // must be single
        if (matched || is(v, Donor)) {
            // System.err.println(v + " must have all single bonds");
            if (avisit[v] == 0)
                avisit[v] = size;
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

        final IBond[] unset = new IBond[atom.getBondCount()];
        int n = 0;

        // identify neighbors in this component where bond order of v-w is not known
        for (IBond bond : atom.bonds()) {
            if (bvisit[bond.getIndex()] == 0)
                unset[n++] = bond;
        }

        // must be a donor: unmatched, no unknown neighbors, and not conjugated
        if (avisit[v] == 0 && roles[v] != Conjugated && n == 0) {
            setRole(v, Donor);
            if (!assignDependantRoles(v))
                return false;
        }

        // must need double bond, a conjugated atom or assigned acceptor without an assigned pi bond
        if (roles[v] == Conjugated || is(v, Acceptor)) {

            // v needs a pi bond but there are no unset bonds, infeasible
            if (n == 0 ) {
                // System.err.println(v + " wanted a pi bond but had none!");
                return false;
            }

            // v needs a pi bond and there is exactly one unset neighbor
            // we can assign that a double bond
            if (n == 1) {
                // assert matching.unmatched(unknown[0]);
                IBond bond = unset[0];
                int w = bond.getOther(atom).getIndex();

                // must be an acceptor
                if (roles[w] != Conjugated && avisit[w] == 0) {
                    // System.err.println(v + " must be an acceptor");
                    setRole(w, Acceptor);
                }

                // required for remove() to clear the matching
                if (avisit[v] == 0) avisit[v] = size;
                if (avisit[w] == 0) avisit[w] = size;

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
        if (roles[idx] == Conjugated)
            return true;
        return is(idx, Acceptor) || nAcceptors[components[idx]] > 0;
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
        return assignDependantRoles(beg.getIndex()) &&
                assignDependantRoles(end.getIndex());
    }

    private boolean assignBondOrders(IAtom atom) {
        final int aidx = atom.getIndex();
        assert roles[aidx] == Conjugated || roles[aidx] == Acceptor;
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

    private boolean kekulizeComponent(final int component) {
        if (nDonors[component] != 0)
            throw new IllegalArgumentException("Component has unplaced donors");
        if (nAcceptors[component] != 0)
            throw new IllegalArgumentException("Component has unplaced acceptors");
        for (int aidx = 0; aidx < avisit.length; aidx++) {
            if (components[aidx] != component)
                continue;
            if (avisit[aidx] != 0 && roles[aidx] != Acceptor)
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
    private void setRole(final int v, final Tautomers.Role type) {
        assert type == Acceptor || type == Donor;
        assert avisit[v] == 0;
        avisit[v] = size;
        if (type == Donor) {
            if (roles[v] == Acceptor)
                accept(v);
            nDonors[components[v]]--;
        }
        else { // Acceptor
            if (roles[v] == Donor)
                donate(v);
            nAcceptors[components[v]]--;
        }
    }

    /**
     * Accept a proton, vertex 'v' is now a proton donor.
     *
     * @param v vertex, the atom index
     */
    private void accept(final int v) {
        assert roles[v] == Acceptor;
        final IAtom atom = mol.getAtom(v);
        assert atom.getImplicitHydrogenCount() != null;
        atom.setImplicitHydrogenCount(atom.getImplicitHydrogenCount() + 1);
        roles[v] = Donor;
    }

    /**
     * Donate a proton, vertex 'v' is now a proton acceptor.
     *
     * @param v vertex, the atom index
     */
    private void donate(final int v) {
        assert roles[v] == Donor;
        final IAtom atom = mol.getAtom(v);
        assert atom.getImplicitHydrogenCount() != null;
        atom.setImplicitHydrogenCount(atom.getImplicitHydrogenCount() - 1);
        roles[v] = Acceptor;
    }

    IAtomContainer container() {
        return mol;
    }

    // refactor out

    private int labelComponents(int[] labels) {
        int label = 1;
        for (int v = 0; v < mol.getAtomCount(); v++) {
            if (labels[v] == 0 && roles[v] == Donor)
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
            if (labels[nidx] != 0 || types[nidx] == Tautomers.Role.None)
                continue;
            labelComponent(nbor, bond, label, labels, types);
        }
    }

    // clear the existing atom type and aromaticity information, we are 
    // going to be moving hydrogens around that will change the atom type
    // and break aromaticity
    static void clearTypeInfo(IAtomContainer container) {
        // note - bond order sum / valence will be unchanged
        for (IAtom a : container.atoms()) {
            a.setAtomTypeName(null);
            // a.setHybridization(null);
            a.setFormalNeighbourCount(null);
            a.setIsAromatic(false);
            if (a.getImplicitHydrogenCount() == null)
                throw new IllegalArgumentException("Molecule must have implicit hydrogens");
        }
        for (IBond b : container.bonds()) {
            b.setIsAromatic(false);
        }
    }

    private int eneg(int v, IAtom atom) {
        if (roles[v] == None || roles[v] == Conjugated)
            return 15;
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
                sb.append(roles[candidates[i]] == Donor ? "d" : "a");
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
}
