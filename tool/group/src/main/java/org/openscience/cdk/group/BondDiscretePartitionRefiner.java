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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * A tool for determining the automorphism group of the atoms in a molecule, or
 * for checking for a canonical form of a molecule.
 *
 * If two bonds are equivalent under an automorphism in the group, then
 * roughly speaking they are in symmetric positions in the molecule. For
 * example, the C-C bonds attaching two methyl groups to a benzene ring
 * are 'equivalent' in this sense.
 *
 * <p>There are a couple of ways to use it - firstly, get the automorphisms.</p>
 *
 * <pre>
 *     IAtomContainer ac = ... // get an atom container somehow
 *     BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
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
 *     BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
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
 *     BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
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
public class BondDiscretePartitionRefiner extends AbstractDiscretePartitionRefiner {

    /**
     * The connectivity between bonds; two bonds are connected
     * if they share an atom.
     */
    private int[][] connectionTable;

    /**
     * Specialised option to allow generating automorphisms that ignore the bond order.
     */
    private boolean ignoreBondOrders;

    /**
     * Make a bond partition refiner that takes bond-orders into account.
     */
    public BondDiscretePartitionRefiner() {
        this(false);
    }

    /**
     * Make a bond partition refiner and specify whether bonds-orders should be
     * considered when calculating the automorphisms.
     *
     * @param ignoreBondOrders if true, ignore the bond orders
     */
    public BondDiscretePartitionRefiner(boolean ignoreBondOrders) {
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
                return 1;
            }
        }
        return 0;
    }

    /**
     * Used by the equitable refiner to get the indices of bonds connected to
     * the bond at <code>bondIndex</code>.
     *
     * @param bondIndex the index of the incident bond
     * @return an array of bond indices
     */
    public int[] getConnectedIndices(int bondIndex) {
        return connectionTable[bondIndex];
    }

    /**
     * Get the bond partition, based on the element types of the atoms at either end
     * of the bond, and the bond order.
     *
     * @param atomContainer the container with the bonds to partition
     * @return a partition of the bonds based on the element types and bond order
     */
    public Partition getBondPartition(IAtomContainer atomContainer) {
        int bondCount = atomContainer.getBondCount();
        Map<String, SortedSet<Integer>> cellMap = new HashMap<String, SortedSet<Integer>>();

        // make mini-'descriptors' for bonds like "C=O" or "C#N" etc
        for (int bondIndex = 0; bondIndex < bondCount; bondIndex++) {
            IBond bond = atomContainer.getBond(bondIndex);
            String el0 = bond.getAtom(0).getSymbol();
            String el1 = bond.getAtom(1).getSymbol();
            String boS;
            if (ignoreBondOrders) {
                // doesn't matter what it is, so long as it's constant
                boS = "1";
            } else {
                boolean isArom = bond.getFlag(CDKConstants.ISAROMATIC);
                int orderNumber = (isArom) ? 5 : bond.getOrder().numeric();
                boS = String.valueOf(orderNumber);
            }
            String bondString;
            if (el0.compareTo(el1) < 0) {
                bondString = el0 + boS + el1;
            } else {
                bondString = el1 + boS + el0;
            }
            SortedSet<Integer> cell;
            if (cellMap.containsKey(bondString)) {
                cell = cellMap.get(bondString);
            } else {
                cell = new TreeSet<Integer>();
                cellMap.put(bondString, cell);
            }
            cell.add(bondIndex);
        }

        // sorting is necessary to get cells in order
        List<String> bondStrings = new ArrayList<String>(cellMap.keySet());
        Collections.sort(bondStrings);

        // the partition of the bonds by these 'descriptors'
        Partition bondPartition = new Partition();
        for (String key : bondStrings) {
            SortedSet<Integer> cell = cellMap.get(key);
            bondPartition.addCell(cell);
        }
        bondPartition.order();
        return bondPartition;
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
        refine(atomContainer, getBondPartition(atomContainer));
    }

    /**
     * Refine a bond partition based on the connectivity in the atom container.
     *
     * @param partition the initial partition of the bonds
     * @param atomContainer the atom container to use
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
        super.refine(getBondPartition(atomContainer));
        return isCanonical();
    }

    /**
     * Gets the automorphism group of the atom container. By default it uses an
     * initial partition based on the bond 'types' (so all the C-C bonds are in
     * one cell, all the C=N in another, etc). If this behaviour is not
     * desired, then use the {@link #ignoreBondOrders} flag in the constructor.
     *
     * @param atomContainer the atom container to use
     * @return the automorphism group of the atom container
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer) {
        setup(atomContainer);
        super.refine(getBondPartition(atomContainer));
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
        super.refine(getBondPartition(atomContainer));
        return getAutomorphismGroup();
    }

    /**
     * Get the automorphism group of the molecule given an initial partition.
     *
     * @param atomContainer the atom container to use
     * @param initialPartition an initial partition of the bonds
     * @return the automorphism group starting with this partition
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer, Partition initialPartition) {
        setup(atomContainer);
        super.refine(initialPartition);
        return super.getAutomorphismGroup();
    }

    /**
     * Get the automorphism partition (equivalence classes) of the bonds.
     *
     * @param atomContainer the molecule to calculate equivalence classes for
     * @return a partition of the bonds into equivalence classes
     */
    public Partition getAutomorphismPartition(IAtomContainer atomContainer) {
        setup(atomContainer);
        super.refine(getBondPartition(atomContainer));
        return super.getAutomorphismPartition();
    }

    private void setup(IAtomContainer atomContainer) {
        // have to setup the connection table before making the group
        // otherwise the size may be wrong
        if (connectionTable == null) {
            setupConnectionTable(atomContainer);
        }

        int size = getVertexCount();
        PermutationGroup group = new PermutationGroup(new Permutation(size));
        super.setup(group, new BondEquitablePartitionRefiner(this));
    }

    private void setup(IAtomContainer atomContainer, PermutationGroup group) {
        setupConnectionTable(atomContainer);
        super.setup(group, new BondEquitablePartitionRefiner(this));
    }

    private void setupConnectionTable(IAtomContainer atomContainer) {
        int bondCount = atomContainer.getBondCount();
        // unfortunately, we have to sort the bonds
        List<IBond> bonds = new ArrayList<IBond>();
        Map<String, IBond> bondMap = new HashMap<String, IBond>();
        for (int bondIndexI = 0; bondIndexI < bondCount; bondIndexI++) {
            IBond bond = atomContainer.getBond(bondIndexI);
            bonds.add(bond);
            int a0 = atomContainer.getAtomNumber(bond.getAtom(0));
            int a1 = atomContainer.getAtomNumber(bond.getAtom(1));
            String boS;
            if (ignoreBondOrders) {
                // doesn't matter what it is, so long as it's constant
                boS = "1";
            } else {
                boolean isArom = bond.getFlag(CDKConstants.ISAROMATIC);
                int orderNumber = (isArom) ? 5 : bond.getOrder().numeric();
                boS = String.valueOf(orderNumber);
            }
            String bondString;
            if (a0 < a1) {
                bondString = a0 + "," + boS + "," + a1;
            } else {
                bondString = a1 + "," + boS + "," + a0;
            }
            bondMap.put(bondString, bond);
        }

        List<String> keys = new ArrayList<String>(bondMap.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            bonds.add(bondMap.get(key));
        }

        connectionTable = new int[bondCount][];
        for (int bondIndexI = 0; bondIndexI < bondCount; bondIndexI++) {
            IBond bondI = bonds.get(bondIndexI);
            List<Integer> connectedBondIndices = new ArrayList<Integer>();
            for (int bondIndexJ = 0; bondIndexJ < bondCount; bondIndexJ++) {
                if (bondIndexI == bondIndexJ) continue;
                IBond bondJ = bonds.get(bondIndexJ);
                if (bondI.isConnectedTo(bondJ)) {
                    connectedBondIndices.add(bondIndexJ);
                }
            }
            int connBondCount = connectedBondIndices.size();
            connectionTable[bondIndexI] = new int[connBondCount];
            for (int index = 0; index < connBondCount; index++) {
                connectionTable[bondIndexI][index] = connectedBondIndices.get(index);
            }
        }
    }
}
