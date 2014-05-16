/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
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

package org.openscience.cdk.tautomer;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.Matching;
import org.openscience.cdk.graph.invariant.Canon;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;

import static org.openscience.cdk.tautomer.Role.Acceptor;
import static org.openscience.cdk.tautomer.Role.Conjugate;
import static org.openscience.cdk.tautomer.Role.Donor;
import static org.openscience.cdk.tautomer.Role.None;

/**
 * A state of the Sayle-Delany (SD) tautomer generation algorithm. 
 *
 * @author John May
 */
final class SDTautomerState {

    /** Proton donor and acceptor candidates. */
    final Integer[] candidates;

    /** When each vertex was visited. */
    private final int[] visited;

    /** Component label of each vertex. */
    private final int[] components;

    /** Number of donors and acceptors in each component. */
    private final int[] nDonors, nAcceptors;

    /** Type of each atom. */
    private final Role[] roles;

    /** Size of the state. */
    private int size = 0;

    /** Nil / unset value */
    private static final int Nil = -1;

    /** Pi (double) bond assignments - stored as a matching. */
    private final Matching matching;

    /** The container we are augmenting. */
    private final IAtomContainer container;

    /** Adjacency list representation of the container. */
    private final int[][] graph;

    /** Bond lookup from atom indices. */
    private final GraphUtil.EdgeToBondMap bonds;

    SDTautomerState(IAtomContainer container, int[][] graph, GraphUtil.EdgeToBondMap bonds, Role[] roles) {
        this(container, graph, bonds, roles, container.getAtomCount());
    }

    SDTautomerState(IAtomContainer container, int[][] graph, GraphUtil.EdgeToBondMap bonds, Role[] roles, int limit) {

        this.container = container;
        this.graph     = graph;
        this.bonds     = bonds;
        this.roles     = roles;

        this.matching = Matching.withCapacity(graph.length);
        this.visited  = new int[container.getAtomCount()];
        Arrays.fill(visited, Nil);

        clearTypeInfo(container);

        // identify and label conjugated systems where protons can move freely
        this.components = new int[graph.length];
        int nComponents = labelComponents(components);

        // count the total number of proton donors/acceptors in each system
        this.nDonors = new int[nComponents];
        this.nAcceptors = new int[nComponents];
        for (int v = 0; v < graph.length; v++) {
            if (roles[v] == Donor)
                nDonors[components[v]]++;
            if (roles[v] == Acceptor)
                nAcceptors[components[v]]++;
        }

        // remove any components that have no donors or acceptor or that
        // have more then limit
        for (int component = 1; component < nComponents; component++) {
            if (nDonors[component] == 0 || nAcceptors[component] == 0
                    || nDonors[component] + nAcceptors[component] > limit)
                for (int v = 0; v < graph.length; v++) {
                    if (components[v] == component)
                        components[v] = 0;
                }
        }

        // enqueue all donor/acceptor atoms, labelled as candidates 
        Integer[] queue = new Integer[graph.length];
        int n = 0;
        for (int v = 0; v < graph.length; v++) {
            if (components[v] < 1)
                continue;
            if (roles[v] == Acceptor || roles[v] == Donor)
                queue[n++] = v;
        }
        this.candidates = Arrays.copyOf(queue, n);

        // no donors or acceptors
        if (n == 0) return;

        final long[] labels = Canon.label(container, graph, initialInvariants());

        Arrays.sort(this.candidates, new Comparator<Integer>() {
            @Override public int compare(Integer u, Integer v) {
                if (components[u] < components[v])
                    return +1;
                if (components[u] > components[v])
                    return -1;
                if (labels[u] < labels[v])
                    return +1;
                if (labels[u] > labels[v])
                    return -1;
                return 0;
            }
        });
    }

    /**
     * Index of the next proton donor or acceptor to assign.
     *
     * @return vertex, the atom index
     */
    int select() {
        for (final int v : candidates)
            if (visited[v] == Nil) return v;
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
    boolean add(final int v, final Role type) {

        if (v < 0) return false;

        assert roles[v] == Acceptor || roles[v] == Donor;
        assert type == Acceptor || type == Donor;

        if (feasible(v, type)) {
            size++;
        }
        else {
            remove(v);
        }

        return visited[v] != Nil && size > visited[v];
    }

    /**
     * Remove the assignment of vertex v. The state is reset to before the
     * assigned was made.
     *
     * @param v proton donor or acceptor
     */
    void remove(final int v) {
        assert visited[v] != Nil;
        assert roles[v] == Acceptor || roles[v] == Donor;
        this.size = visited[v];
        for (int w = 0; w < visited.length; w++) {
            if (visited[w] >= size) {
                visited[w] = Nil;
                if (roles[w] == Donor)
                    nDonors[components[w]]++;
                else if (roles[w] == Acceptor)
                    nAcceptors[components[w]]++;
                matching.unmatch(w);
            }
        }
    }

    /**
     * Access the current type of vertex, v.
     *
     * @param v vertex, the atom index
     * @return the type of the vertex
     */
    Role roleOf(final int v) {
        return roles[v];
    }

    /**
     * State is complete when there are no unvisited proton donors or
     * acceptors.
     *
     * @return status
     */
    boolean complete() {
        return candidates.length > 0 && select() < 0;
    }

    /**
     * Determine whether the type assignment of the vertex, v, is feasible.
     *
     * @param v    vertex, an atom index of a donor or acceptor
     * @param type v as a donor or acceptor
     * @return the assignment is feasible
     */
    private boolean feasible(final int v, final Role type) {

        setRole(v, type);

        final int component = components[v];

        // more hydrogen donors or acceptors assigned than were available
        if (nDonors[component] < 0 || nAcceptors[component] < 0)
            return false;

        // when a hydrogen donor or acceptor is set it may decide the
        // role of another vertex in the component. The donor and acceptor
        // counts may have changed and are retested  
        if (!assignDependantRoles(v, new BitSet(graph.length)) || nDonors[component] < 0 || nAcceptors[component] < 0)
            return false;

        // all hydrogen donors or acceptors of this component have been placed,
        // all unvisited candidates must be acceptors or donors and we must be
        // able to assign a Kekulé structure 
        if (nDonors[component] == 0 || nAcceptors[component] == 0) {

            final Role other = nDonors[component] == 0 ? Acceptor : Donor;

            for (int w = 0; w < visited.length; w++)
                if (components[w] == component && visited[w] == Nil && roles[w] != Role.Conjugate)
                    setRole(w, other);

            if (!kekulise(component))
                return false;
        }

        return true;
    }

    /**
     * Recursively update the vertex, v and its neighbors. If possible double
     * bonds are assigned - this may decided proton donor or acceptors status of
     * unvisited vertices. The method will also check for unacceptable
     * assignment and returns false, if a condradiction was identified.
     *
     * @param v       vertex, the atom index
     * @param checked the vertices that were known and have been checked
     * @return assignment was feasible
     */
    private boolean assignDependantRoles(final int v, final BitSet checked) {

        if (roles[v] == Role.None || checked.get(v))
            return true;

        final boolean matched = matching.matched(v);

        // a donor or matched acceptor/conjugated atom, then all unknonwn bonds 
        // must be single
        if (visited[v] > Nil && (roles[v] == Donor || matched)) {
            checked.set(v);
            for (int w : graph[v])
                if (!assignDependantRoles(w, checked))
                    return false;
        }

        final int[] neighbors = graph[v];
        final int   component = components[v];

        final int[] unknown   = new int[neighbors.length];
        int n = 0;

        // identify neighbors in this component where bond order of v-w is not known
        for (final int w : neighbors) {
            if (components[w] != component)
                continue;
            if (visited[w] == Nil || matching.unmatched(w) && roles[w] != Donor)
                unknown[n++] = w;
        }

        // unmatched, no unknown neighbors, and not conjugated, then it must be a donor
        if (visited[v] == Nil && roles[v] != Role.Conjugate && !matched && n == 0) {
            setRole(v, Donor);
            assignDependantRoles(v, checked);
        }

        // a conjugated atom or assigned acceptor without an assigned pi bond
        if (!matched && (roles[v] == Role.Conjugate || (visited[v] > Nil && roles[v] == Acceptor))) {

            // v needs a pi bond but there are no unset bonds, infeasible
            if (n == 0) return false;

            // v needs a pi bond and there is exactly one unset neighbor
            // we can assign that a double bond
            if (n == 1) {
                assert matching.unmatched(unknown[0]);
                int w = unknown[0];

                // the other vertex must be an acceptor, set it
                if (roles[w] != Role.Conjugate && visited[w] == Nil)
                    setRole(w, Acceptor);

                // required for remove() to clear the matching
                if (visited[v] == Nil) visited[v] = size;
                if (visited[w] == Nil) visited[w] = size;

                matching.match(v, w);

                return assignDependantRoles(w, checked);
            }
        }

        return true;
    }

    /**
     * Assign a kekulé structure to vertices of the component.
     *
     * @param component the component to kekulise
     * @return kekulé structure could be assigned
     */
    private boolean kekulise(final int component) {
        BitSet subset = new BitSet();
        for (int v = 0; v < visited.length; v++) {
            if (components[v] == component && needsPiBond(v) && matching.unmatched(v)) {
                if (visited[v] == Nil) visited[v] = size;
                subset.set(v);
            }
        }
        return matching.perfect(graph, subset);
    }

    /**
     * Vertex v is an acceptor or conjugated and must be adjacent to a pi bond.
     */
    private boolean needsPiBond(final int v) {
        return roles[v] == Acceptor || roles[v] == Role.Conjugate;
    }

    /**
     * Set role (acceptor / donor) of an vertex.
     *
     * @param v    vertex, the atom index
     * @param type set to a proton donor or acceptor
     */
    private void setRole(final int v, final Role type) {
        assert type == Acceptor || type == Donor;
        assert visited[v] == Nil;
        visited[v] = size;
        if (type == Donor) {
            if (roles[v] == Acceptor)
                accept(v);
            nDonors[components[v]]--;
        }
        else {
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
        final IAtom atom = container.getAtom(v);
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
        final IAtom atom = container.getAtom(v);
        assert atom.getImplicitHydrogenCount() != null;
        atom.setImplicitHydrogenCount(atom.getImplicitHydrogenCount() - 1);
        roles[v] = Acceptor;
    }

    IAtomContainer container() {

        for (int v = 0; v < container.getAtomCount(); v++) {
            if (components[v] > 0 && roles[v] != Role.None) {
                for (int w : graph[v]) {
                    if (roles[w] != Role.None)
                        bonds.get(v, w).setOrder(IBond.Order.SINGLE);
                }
            }
        }

        for (int v = 0; v < container.getAtomCount(); v++) {
            if (matching.matched(v)) {
                int w = matching.other(v);
                if (w > v) bonds.get(v, w).setOrder(IBond.Order.DOUBLE);
            }
        }

        return container;
    }

    // refactor out

    private int labelComponents(int[] labels) {
        int label = 1;
        for (int v = 0; v < graph.length; v++) {
            if (labels[v] == 0 && roles[v] == Donor)
                labelComponent(graph, v, label++, labels, roles);
        }
        return label;
    }

    private static void labelComponent(int[][] g, int v, int label, int[] labels, Role[] types) {
        labels[v] = label;
        for (final int w : g[v]) {
            if (labels[w] == 0 && types[w] != Role.None) {
                labelComponent(g, w, label, labels, types);
            }
        }
    }

    // clear the existing atom type and aromaticity information, we are 
    // going to be moving hydrogens around that will change the atom type.
    private static void clearTypeInfo(IAtomContainer container) {
        // note - bond order sum / valence will be unchanged
        for (IAtom a : container.atoms()) {
            a.setAtomTypeName(null);
            a.setHybridization(null);
            a.setFormalNeighbourCount(null);
            a.setFlag(CDKConstants.ISAROMATIC, false);
        }
        for (IBond b : container.bonds()) {
            b.setFlag(CDKConstants.ISAROMATIC, false);
        }
    }

    long[] initialInvariants() {
        long[] labels = new long[graph.length];

        for (int v = 0; v < graph.length; v++) {
            IAtom atom = container.getAtom(v);

            int deg  = graph[v].length;
            int impH = implH(atom);
            int expH = 0;
            int elem = atomicNumber(atom);
            int chg  = charge(atom);

            // we need to adjust connectivity and h counts for donors/acceptors
            int adjust = 0;
            if (roles[v] == Donor)
                adjust = 2;
            else if (roles[v] == Acceptor)
                adjust = 3;

            // count non-suppressed (explicit) hydrogens
            for (int w : graph[v])
                if (atomicNumber(container.getAtom(w)) == 1)
                    expH++;

            long label = 0;
            label |= eneg(v, Elements.ofNumber(atom.getAtomicNumber()));
            label <<= 4;    // connectivity (first in) <= 15 (4 bits)
            label |= deg + impH + adjust & 0xf;
            label <<= 4;    // connectivity (heavy) <= 15 (4 bits)
            label |= deg - expH & 0xf;
            label <<= 7;   // atomic number <= 127 (7 bits)
            label |= elem & 0x7f;
            label <<= 1;   // charge sign == 1 (1 bit)
            label |= chg >> 31 & 0x1;
            label <<= 2;   // charge <= 3 (2 bits)
            label |= Math.abs(chg) & 0x3;
            label <<= 4;   // hydrogen count <= 15 (4 bits)
            label |= impH + expH + adjust & 0xf;

            labels[v] = label;
        }

        return labels;
    }

    private int eneg(int v, Elements elements) {
        if (roles[v] == None || roles[v] == Conjugate)
            return 15;
        switch (elements) {
            case Nitrogen:
                return 1;
            case Tellurium:
                return 2;
            case Selenium:
                return 3;
            case Sulfur:
                return 4;
            case Oxygen:
                return 5;
            case Carbon:
                return 6; // FIXME: distinguish in ring
        }
        return 0;
    }

    /**
     * Access atomic number of atom defaulting to 0 for pseudo atoms.
     *
     * @param atom an atom
     * @return the atomic number
     * @throws NullPointerException the atom was non-pseudo at did not have an
     *                              atomic number
     */
    private static int atomicNumber(IAtom atom) {
        Integer elem = atom.getAtomicNumber();
        if (elem != null)
            return elem;
        if (atom instanceof IPseudoAtom)
            return 0;
        throw new NullPointerException("a non-psuedo atom had unset atomic number");
    }

    /**
     * Access implicit hydrogen count of the atom defaulting to 0 for pseudo
     * atoms.
     *
     * @param atom an atom
     * @return the implicit hydrogen count
     * @throws NullPointerException the atom was non-pseudo at did not have an
     *                              implicit hydrogen count
     */
    private static int implH(IAtom atom) {
        Integer h = atom.getImplicitHydrogenCount();
        if (h != null)
            return h;
        if (atom instanceof IPseudoAtom)
            return 0;
        throw new NullPointerException("a non-psuedo atom had unset hydrogen count");
    }

    /**
     * Access formal charge of an atom defaulting to 0 if undefined.
     *
     * @param atom an atom
     * @return the formal charge
     */
    private static int charge(IAtom atom) {
        Integer charge = atom.getFormalCharge();
        if (charge != null)
            return charge;
        return 0;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        //for (int i = 0; i < size; i++)
        //    sb.append('.');
        for (int i = 0; i < candidates.length; i++) {
            if (visited[candidates[i]] != -1)
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
