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
 * This way, the refinement is not carried out multiple times.
 *
 * @author maclean
 * @cdk.module group
 */
public class BondDiscretePartitionRefiner extends AtomContainerDiscretePartitionRefiner {

    private BondRefinable refinable;

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
        return refinable.getVertexCount();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int getConnectivity(int i, int j) {
        return refinable.getConnectivity(i, j);
    }

    /**
     * Get the bond partition, based on the element types of the atoms at either end
     * of the bond, and the bond order.
     *
     * @param atomContainer the container with the bonds to partition
     * @return a partition of the bonds based on the element types and bond order
     */
    public Partition getInitialPartition(IAtomContainer atomContainer) {
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
    
    protected Refinable getRefinable(IAtomContainer atomContainer) {
        refinable = new BondRefinable(atomContainer, ignoreBondOrders);
        return refinable;
    }
}
