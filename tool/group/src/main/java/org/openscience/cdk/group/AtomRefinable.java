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

import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Wraps an atom container to provide information on the connectivity.
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
     * Specialised option to allow generating automorphisms
     * that ignore the bond order.
     */
    private boolean ignoreBondOrders;
    
    private int maxBondOrder;
    
    public AtomRefinable(IAtomContainer atomContainer) {
        this(atomContainer, false);
    }
    
    public AtomRefinable(IAtomContainer atomContainer, boolean ignoreBondOrders) {
        this.atomContainer = atomContainer;
        this.ignoreBondOrders = ignoreBondOrders;
        setupConnectionTable(atomContainer);
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

    @Override
    public int[] getConnectedIndices(int vertexIndex) {
        return connectionTable[vertexIndex];
    }

    @Override
    public int getMaxConnectivity() {
        return maxBondOrder;
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
