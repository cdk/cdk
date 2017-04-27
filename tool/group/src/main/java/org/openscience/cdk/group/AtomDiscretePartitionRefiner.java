/* Copyright (C) 2012  Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.cdk.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * A tool for determining the automorphism group of the atoms in a molecule, or
 * for checking for a canonical form of a molecule.
 *
 * If two atoms are equivalent under an automorphism in the group, then
 * roughly speaking they are in symmetric positions in the molecule. For
 * example, the C atoms in two methyl groups attached to a benzene ring
 * are 'equivalent' in this sense.
 *
 * <p>There are a couple of ways to use it - firstly, get the automorphisms.</p>
 *
 * <pre>
 *     IAtomContainer ac = ... // get an atom container somehow
 *     AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
 *     PermutationGroup autG = refiner.getAutomorphismGroup(ac);
 *     for (Permutation automorphism : autG.all()) {
 *         ... // do something with the permutation
 *     }
 * </pre>
 *
 * <p>Another is to check an atom container to see if it is canonical:</p>
 *
 * <pre>
 *     IAtomContainer ac = ... // get an atom container somehow
 *     AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
 *     if (refiner.isCanonical(ac)) {
 *         ... // do something with the atom container
 *     }
 * </pre>
 *
 * Note that it is not necessary to call {@link #refine(IAtomContainer)} before
 * either of these methods. However if both the group and the canonical check
 * are required, then the code should be:
 *
 * <pre>
 *     AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
 *     refiner.refine(ac);
 *     boolean isCanon = refiner.isCanonical();
 *     PermutationGroup autG = refiner.getAutomorphismGroup();
 * </pre>
 *
 * This way, the refinement is not carried out multiple times. Finally, remember
 * to call {@link #reset} if the refiner is re-used on multiple structures.
 *
 * @author maclean
 * @cdk.module group
 */
public class AtomDiscretePartitionRefiner extends AbstractDiscretePartitionRefiner {

    /**
     * A convenience lookup table for atom-atom connections.
     */
    private int[][] connectionTable;

    /**
     * A convenience lookup table for bond orders.
     */
    private int[][] bondOrders;

    /**
     * Specialised option to allow generating automorphisms
     * that ignore the element symbols.
     */
    private boolean ignoreElements;

    /**
     * Specialised option to allow generating automorphisms
     * that ignore the bond order.
     */
    private boolean ignoreBondOrders;

    /**
     * Default constructor - does not ignore elements or bond orders
     * or bond orders.
     */
    public AtomDiscretePartitionRefiner() {
        this(false, false);
    }

    /**
     * Make a refiner with various advanced options.
     *
     * @param ignoreElements ignore element symbols when making automorphisms
     * @param ignoreBondOrders ignore bond order when making automorphisms
     */
    public AtomDiscretePartitionRefiner(boolean ignoreElements, boolean ignoreBondOrders) {
        this.ignoreElements = ignoreElements;
        this.ignoreBondOrders = ignoreBondOrders;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int getVertexCount() {
        return connectionTable.length;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int getConnectivity(int i, int j) {
        int indexInRow;
        int maxRowIndex = connectionTable[i].length;
        for (indexInRow = 0; indexInRow < maxRowIndex; indexInRow++) {
            if (connectionTable[i][indexInRow] == j) {
                break;
            }
        }
        if (ignoreBondOrders) {
            if (indexInRow < maxRowIndex) {
                return 1;
            } else {
                return 0;
            }
        } else {
            if (indexInRow < maxRowIndex) {
                return bondOrders[i][indexInRow];
            } else {
                return 0;
            }
        }
    }

    /**
     * Used by the equitable refiner to get the indices of atoms connected to
     * the atom at <code>atomIndex</code>.
     *
     * @param atomIndex the index of the incident atom
     * @return an array of atom indices
     */
    public int[] getConnectedIndices(int atomIndex) {
        return connectionTable[atomIndex];
    }

    /**
     * Get the element partition from an atom container, which is simply a list
     * of sets of atom indices where all atoms in one set have the same element
     * symbol.
     *
     * So for atoms [C0, N1, C2, P3, C4, N5] the partition would be
     * [{0, 2, 4}, {1, 5}, {3}] with cells for elements C, N, and P.
     *
     * @param atomContainer the atom container to get element symbols from
     * @return a partition of the atom indices based on the element symbols
     */
    public Partition getElementPartition(IAtomContainer atomContainer) {
        if (ignoreElements) {
            int n = atomContainer.getAtomCount();
            return Partition.unit(n);
        }

        if (connectionTable == null) {
            setupConnectionTable(atomContainer);
        }

        Map<String, SortedSet<Integer>> cellMap = new HashMap<String, SortedSet<Integer>>();
        int numberOfAtoms = atomContainer.getAtomCount();
        for (int atomIndex = 0; atomIndex < numberOfAtoms; atomIndex++) {
            String symbol = atomContainer.getAtom(atomIndex).getSymbol();
            SortedSet<Integer> cell;
            if (cellMap.containsKey(symbol)) {
                cell = cellMap.get(symbol);
            } else {
                cell = new TreeSet<Integer>();
                cellMap.put(symbol, cell);
            }
            cell.add(atomIndex);
        }

        List<String> atomSymbols = new ArrayList<String>(cellMap.keySet());
        Collections.sort(atomSymbols);

        Partition elementPartition = new Partition();
        for (String key : atomSymbols) {
            SortedSet<Integer> cell = cellMap.get(key);
            elementPartition.addCell(cell);
        }

        return elementPartition;
    }

    /**
     * Reset the connection table.
     */
    public void reset() {
        connectionTable = null;
    }

    /**
     * Refine an atom container, which has the side effect of calculating
     * the automorphism group.
     *
     * If the group is needed afterwards, call {@link #getAutomorphismGroup()}
     * instead of {@link #getAutomorphismGroup(IAtomContainer)} otherwise the
     * refine method will be called twice.
     *
     * @param atomContainer the atomContainer to refine
     */
    public void refine(IAtomContainer atomContainer) {
        refine(atomContainer, getElementPartition(atomContainer));
    }

    /**
     * Refine an atom partition based on the connectivity in the atom container.
     *
     * @param atomContainer the atom container to use
     * @param partition the initial partition of the atoms
     */
    public void refine(IAtomContainer atomContainer, Partition partition) {
        setup(atomContainer);
        super.refine(partition);
    }

    /**
     * Checks if the atom container is canonical. Note that this calls
     * {@link #refine} first.
     *
     * @param atomContainer the atom container to check
     * @return true if the atom container is canonical
     */
    public boolean isCanonical(IAtomContainer atomContainer) {
        setup(atomContainer);
        super.refine(getElementPartition(atomContainer));
        return isCanonical();
    }

    /**
     * Gets the automorphism group of the atom container. By default it uses an
     * initial partition based on the element symbols (so all the carbons are in
     * one cell, all the nitrogens in another, etc). If this behaviour is not
     * desired, then use the {@link #ignoreElements} flag in the constructor.
     *
     * @param atomContainer the atom container to use
     * @return the automorphism group of the atom container
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer) {
        setup(atomContainer);
        super.refine(getElementPartition(atomContainer));
        return super.getAutomorphismGroup();
    }

    /**
     * Speed up the search for the automorphism group using the automorphisms in
     * the supplied group. Note that the behaviour of this method is unknown if
     * the group does not contain automorphisms...
     *
     * @param atomContainer the atom container to use
     * @param group the group of known automorphisms
     * @return the full automorphism group
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer, PermutationGroup group) {
        setup(atomContainer, group);
        super.refine(getElementPartition(atomContainer));
        return super.getAutomorphismGroup();
    }

    /**
     * Get the automorphism group of the molecule given an initial partition.
     *
     * @param atomContainer the atom container to use
     * @param initialPartition an initial partition of the atoms
     * @return the automorphism group starting with this partition
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer, Partition initialPartition) {
        setup(atomContainer);
        super.refine(initialPartition);
        return super.getAutomorphismGroup();
    }

    /**
     * Get the automorphism partition (equivalence classes) of the atoms.
     *
     * @param atomContainer the molecule to calculate equivalence classes for
     * @return a partition of the atoms into equivalence classes
     */
    public Partition getAutomorphismPartition(IAtomContainer atomContainer) {
        setup(atomContainer);
        super.refine(getElementPartition(atomContainer));
        return super.getAutomorphismPartition();
    }

    /**
     * Makes a lookup table for the connection between atoms, to avoid looking
     * through the bonds each time.
     *
     * @param atomContainer the atom
     */
    private void setupConnectionTable(IAtomContainer atomContainer) {
        int atomCount = atomContainer.getAtomCount();
        connectionTable = new int[atomCount][];
        if (!ignoreBondOrders) {
            bondOrders = new int[atomCount][];
        }
        for (int atomIndex = 0; atomIndex < atomCount; atomIndex++) {
            IAtom atom = atomContainer.getAtom(atomIndex);
            List<IAtom> connectedAtoms = atomContainer.getConnectedAtomsList(atom);
            int numConnAtoms = connectedAtoms.size();
            connectionTable[atomIndex] = new int[numConnAtoms];
            if (!ignoreBondOrders) {
                bondOrders[atomIndex] = new int[numConnAtoms];
            }
            int i = 0;
            for (IAtom connected : connectedAtoms) {
                int index = atomContainer.indexOf(connected);
                connectionTable[atomIndex][i] = index;
                if (!ignoreBondOrders) {
                    IBond bond = atomContainer.getBond(atom, connected);
                    boolean isArom = bond.getFlag(CDKConstants.ISAROMATIC);
                    int orderNumber = (isArom) ? 5 : bond.getOrder().numeric();
                    bondOrders[atomIndex][i] = orderNumber;
                }
                i++;
            }
        }
    }

    private void setup(IAtomContainer atomContainer) {
        // have to setup the connection table before making the group
        // otherwise the size may be wrong, but only setup if it doesn't exist
        if (connectionTable == null) {
            setupConnectionTable(atomContainer);
        }
        int size = getVertexCount();
        PermutationGroup group = new PermutationGroup(new Permutation(size));
        super.setup(group, new AtomEquitablePartitionRefiner(this));
    }

    private void setup(IAtomContainer atomContainer, PermutationGroup group) {
        setupConnectionTable(atomContainer);
        super.setup(group, new AtomEquitablePartitionRefiner(this));
    }
}
