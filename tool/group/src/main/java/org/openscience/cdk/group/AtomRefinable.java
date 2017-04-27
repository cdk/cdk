/* Copyright (C) 2017  Gilleain Torrance <gilleain.torrance@gmail.com>
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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Wraps an atom container to provide information on the atom connectivity.
 * 
 * @author maclean
 * @cdk.module group
 *
 */
public class AtomRefinable implements Refinable {
    
    private final IAtomContainer atomContainer;
    
    /**
     * A convenience lookup table for atom-atom connections.
     */
    private int[][] connectionTable;

    /**
     * A convenience lookup table for bond orders.
     */
    private int[][] bondOrders;
    
    /**
     * Ignore the elements when creating the initial partition.
     */
    private boolean ignoreElements;
    
    /**
     * Specialised option to allow generating automorphisms
     * that ignore the bond order.
     */
    private boolean ignoreBondOrders;
    
    private int maxBondOrder;
    
    /**
     * Create a refinable from an atom container with flags set to false.
     * 
     * @param atomContainer the atom and bond data
     */
    public AtomRefinable(IAtomContainer atomContainer) {
        this(atomContainer, false, false);
    }
    
    /**
     * Create a refinable from an atom container with supplied flags.
     * 
     * @param atomContainer the atom and bond data
     * @param ignoreElements 
     * @param ignoreBondOrders
     */
    public AtomRefinable(IAtomContainer atomContainer, boolean ignoreElements, boolean ignoreBondOrders) {
        this.atomContainer = atomContainer;
        this.ignoreElements = ignoreElements;
        this.ignoreBondOrders = ignoreBondOrders;
        setupConnectionTable(atomContainer);
    }
    
    @Override
    public Invariant neighboursInBlock(Set<Integer> block, int vertexIndex) {
        // choose the invariant to use 
        if (ignoreBondOrders || maxBondOrder == 1) {
            return getSimpleInvariant(block, vertexIndex);
        } else {
            return getMultipleInvariant(block, vertexIndex);
        }
    }
    
    /**
     * @return a simple count of the neighbours of vertexIndex that are in block
     */
    private Invariant getSimpleInvariant(Set<Integer> block, int vertexIndex) {
        int neighbours = 0;
        for (int connected : getConnectedIndices(vertexIndex)) {
            if (block.contains(connected)) {
                neighbours++;
            }
        }
        return new IntegerInvariant(neighbours);
    }
    
    /**
     * @return a list of bond orders of connections to neighbours of vertexIndex that are in block
     */
    private Invariant getMultipleInvariant(Set<Integer> block, int vertexIndex) {
        int[] bondOrderCounts = new int[maxBondOrder];
        for (int connected : getConnectedIndices(vertexIndex)) {
            if (block.contains(connected)) {
                int bondOrder = getConnectivity(vertexIndex, connected);
                bondOrderCounts[bondOrder - 1]++;
            }
        }
        return new IntegerListInvariant(bondOrderCounts);
    }

    @Override
    public int getVertexCount() {
        return atomContainer.getAtomCount();
    }

    @Override
    public int getConnectivity(int vertexI, int vertexJ) {
        int indexInRow;
        int maxRowIndex = connectionTable[vertexI].length;
        for (indexInRow = 0; indexInRow < maxRowIndex; indexInRow++) {
            if (connectionTable[vertexI][indexInRow] == vertexJ) {
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
                return bondOrders[vertexI][indexInRow];
            } else {
                return 0;
            }
        }
    }

    private int[] getConnectedIndices(int vertexIndex) {
        return connectionTable[vertexIndex];
    }
    
    /**
     * Get the element partition from an atom container, which is simply a list
     * of sets of atom indices where all atoms in one set have the same element
     * symbol.
     *
     * So for atoms [C0, N1, C2, P3, C4, N5] the partition would be
     * [{0, 2, 4}, {1, 5}, {3}] with cells for elements C, N, and P.
     *
     * @return a partition of the atom indices based on the element symbols
     */
    @Override
    public Partition getInitialPartition() {
        if (ignoreElements) {
            int n = atomContainer.getAtomCount();
            return Partition.unit(n);
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
                int index = atomContainer.getAtomNumber(connected);
                connectionTable[atomIndex][i] = index;
                if (!ignoreBondOrders) {
                    IBond bond = atomContainer.getBond(atom, connected);
                    boolean isArom = bond.getFlag(CDKConstants.ISAROMATIC);
                    int orderNumber = (isArom) ? 5 : bond.getOrder().numeric();
                    bondOrders[atomIndex][i] = orderNumber;
                    
                    // TODO
                    if (orderNumber > maxBondOrder) {
                        maxBondOrder = orderNumber;
                    }
                }
                i++;
            }
        }
    }
}
