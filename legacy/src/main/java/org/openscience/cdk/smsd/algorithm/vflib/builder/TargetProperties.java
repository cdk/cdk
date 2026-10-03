/* Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.vflib.builder;

import java.util.ArrayList;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;
import org.openscience.cdk.AtomRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Read-only topology snapshot of a simple undirected molecular graph. Degree
 * and adjacency are captured at construction; atom and bond payloads remain
 * borrowed. Safely published topology can be shared for reading, but callers
 * must coordinate access to mutable chemical payloads during matching.
 *
 * <p>Construction does not prepare missing chemical properties. Predicate
 * callers must supply any required aromaticity, ring or hydrogen metadata.
 * Nullable properties do not guarantee that an atom satisfies a matcher.</p>
 *
 * <p>Serialization requires serializable atom and bond payloads. The sparse
 * serialized snapshot differs from the original dense representation;
 * migration of snapshots from that representation is unsupported.</p>
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class TargetProperties implements java.io.Serializable {

    private static final long serialVersionUID = 4686494070058200325L;

    /**
     * Atom lookup index.
     * @serial Underlying atom payloads mapped to their container indices.
     */
    private final Map<IAtom, Integer> atoms = new HashMap<>();
    /**
     * Captured atom sequence.
     * @serial Borrowed atom payloads in captured container order.
     */
    private final IAtom[] atomsIndex;
    /**
     * Captured atom degrees.
     * @serial Captured degree of each atom.
     */
    private final int[] degrees;
    /**
     * Captured neighbor views.
     * @serial Read-only views over the captured adjacency rows.
     */
    private final List<List<IAtom>> neighbors;
    /**
     * Captured adjacency indices.
     * @serial Neighbor atom indices for each captured atom.
     */
    private final int[][] bondNeighbors;
    /**
     * Captured adjacency bonds.
     * @serial Borrowed bond payloads corresponding to each adjacency row.
     */
    private final IBond[][] bonds;

    /**
     * Return the captured degree of an atom or its reference wrapper.
     * @param atom borrowed atom, reference wrapper or null
     * @return number of connected bonds, or zero for an unknown atom
     */
    public Integer countNeighbors(IAtom atom) {
        Integer index = atoms.get(AtomRef.deref(atom));
        return index == null ? 0 : degrees[index];
    }

    /**
     * Return the captured adjacency of an atom or its reference wrapper.
     * @param atom borrowed atom, reference wrapper or null
     * @return read-only list of borrowed neighboring atoms, or null if absent
     */
    public List<IAtom> getNeighbors(IAtom atom) {
        Integer index = atoms.get(AtomRef.deref(atom));
        return index == null ? null : neighbors.get(index);
    }

    /**
     * Return the captured bond between two atoms, in either endpoint order.
     * @param atom1 first atom or its reference wrapper
     * @param atom2 second atom or its reference wrapper
     * @return borrowed bond, or null if an endpoint or the bond is absent
     */
    public IBond getBond(IAtom atom1, IAtom atom2) {
        Integer first = atoms.get(AtomRef.deref(atom1));
        Integer second = atoms.get(AtomRef.deref(atom2));
        if (first == null || second == null) {
            return null;
        }
        // Molecular graphs are sparse; scan the shorter adjacency list.
        if (bondNeighbors[first].length > bondNeighbors[second].length) {
            int swap = first;
            first = second;
            second = swap;
        }
        for (int i = bondNeighbors[first].length - 1; i >= 0; i--) {
            if (bondNeighbors[first][i] == second) {
                return bonds[first][i];
            }
        }
        return null;
    }

    /**
     * Count the atoms captured in the snapshot.
     * @return number of atoms
     */
    public int getAtomCount() {
        return atomsIndex.length;
    }

    /**
     * Build a simple molecular graph snapshot in O(V + E) time and space.
     * Atom and bond objects remain borrowed; do not mutate them during a search.
     * Nullable chemical properties are allowed, but graph topology must be valid.
     *
     * @param container container whose topology is captured
     * @throws NullPointerException if the container is null
     * @throws IllegalArgumentException for null/duplicate atoms, foreign endpoints,
     *         self-loops, parallel bonds or bonds with other than two endpoints
     */
    public TargetProperties(IAtomContainer container) {
        Objects.requireNonNull(container, "target container");
        int size = container.getAtomCount();
        atomsIndex = new IAtom[size];
        degrees = new int[size];
        neighbors = new ArrayList<>(size);
        bondNeighbors = new int[size][];
        bonds = new IBond[size][];
        for (int i = 0; i < size; i++) {
            IAtom atom = container.getAtom(i);
            if (atom == null) throw new IllegalArgumentException("Null atom at index " + i);
            if (atoms.put(AtomRef.deref(atom), i) != null) {
                throw new IllegalArgumentException("Duplicate atom at index " + i);
            }
            atomsIndex[i] = atom;
        }
        for (IBond bond : container.bonds()) {
            if (bond == null || bond.getAtomCount() != 2) {
                throw new IllegalArgumentException("SMSD requires non-null two-centre bonds");
            }
            Integer first = atoms.get(AtomRef.deref(bond.getBegin()));
            Integer second = atoms.get(AtomRef.deref(bond.getEnd()));
            if (first == null || second == null) {
                throw new IllegalArgumentException("Bond endpoint is not in the target container");
            }
            if (first.equals(second)) throw new IllegalArgumentException("Self-loop bond is unsupported");
            degrees[first]++;
            degrees[second]++;
        }
        int[] counts = new int[size];
        for (int i = 0; i < size; i++) {
            bondNeighbors[i] = new int[degrees[i]];
            bonds[i] = new IBond[degrees[i]];
        }
        for (IBond bond : container.bonds()) {
            int first = atoms.get(AtomRef.deref(bond.getBegin()));
            int second = atoms.get(AtomRef.deref(bond.getEnd()));
            int offset = counts[first]++;
            bondNeighbors[first][offset] = second;
            bonds[first][offset] = bond;
            offset = counts[second]++;
            bondNeighbors[second][offset] = first;
            bonds[second][offset] = bond;
        }
        // Row stamps detect parallel pairs in linear time without boxed edge keys.
        int[] seen = new int[size];
        for (int i = 0; i < size; i++) {
            for (int index : bondNeighbors[i]) {
                if (seen[index] == i + 1) {
                    throw new IllegalArgumentException("Parallel bonds are unsupported between atoms "
                            + i + " and " + index);
                }
                seen[index] = i + 1;
            }
            neighbors.add(new NeighborList(i));
        }
    }

    /** Read-only array view: no duplicate atom array or wrapper per adjacency row. */
    private final class NeighborList extends AbstractList<IAtom>
            implements RandomAccess, java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final int row;

        NeighborList(int row) {
            this.row = row;
        }

        @Override
        public IAtom get(int index) {
            return atomsIndex[bondNeighbors[row][index]];
        }

        @Override
        public int size() {
            return bondNeighbors[row].length;
        }
    }

    /**
     * Return an atom captured in container order.
     * @param index zero-based atom index
     * @return borrowed atom, or null if the index is out of range
     */
    public IAtom getAtom(int index) {
        return index >= 0 && index < atomsIndex.length ? atomsIndex[index] : null;
    }
}
