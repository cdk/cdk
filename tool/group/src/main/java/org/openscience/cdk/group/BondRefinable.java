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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Wraps an atom container to provide information on the bond connectivity.
 * 
 * @author maclean
 * @cdk.module group
 *
 */
public class BondRefinable implements Refinable {
    
    private final IAtomContainer atomContainer;

    /**
     * The connectivity between bonds; two bonds are connected
     * if they share an atom.
     */
    private int[][] connectionTable;

    /**
     * Specialised option to allow generating automorphisms that ignore the bond order.
     */
    private boolean ignoreBondOrders;
    
    public BondRefinable(IAtomContainer atomContainer) {
        this(atomContainer, false);
    }
    
    public BondRefinable(IAtomContainer atomContainer, boolean ignoreBondOrders) {
        this.atomContainer = atomContainer;
        this.ignoreBondOrders = ignoreBondOrders;
        setupConnectionTable(atomContainer);
    }

    @Override
    public int getVertexCount() {
        return atomContainer.getBondCount();
    }

    @Override
    public int getConnectivity(int vertexI, int vertexJ) {
        int indexInRow;
        int maxRowIndex = connectionTable[vertexI].length;
        for (indexInRow = 0; indexInRow < maxRowIndex; indexInRow++) {
            if (connectionTable[vertexI][indexInRow] == vertexJ) {
                return 1;
            }
        }
        return 0;
    }
    
    @Override
    public Invariant neighboursInBlock(Set<Integer> block, int vertexIndex) {
        int neighbours = 0;
        for (int connected : getConnectedIndices(vertexIndex)) {
            if (block.contains(connected)) {
                neighbours++;
            }
        }
        return new IntegerInvariant(neighbours);
    }

    private int[] getConnectedIndices(int vertexIndex) {
        return connectionTable[vertexIndex];
    }
    
    /**
     * Get the bond partition, based on the element types of the atoms at either end
     * of the bond, and the bond order.
     *
     * @return a partition of the bonds based on the element types and bond order
     */
    public Partition getInitialPartition() {
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
