/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.graph.matrix;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ElectronContainer;

/**
 * Calculator for a connection matrix representation of this AtomContainer. An
 * adjacency matrix is a matrix of quare NxN matrix, where N is the number of
 * atoms in the AtomContainer. If the i-th and the j-th atom in the
 * atomcontainer share a bond, the element i,j in the matrix is set to the
 * bond order value. Otherwise it is zero. See {@cdk.cite TRI1992}.
 *
 * @cdk.module  standard
 * @cdk.keyword connection matrix
 * @cdk.dictref blue-obelisk:calculateConnectivityMatrix
 *
 * @author      steinbeck
 * @cdk.created 2004-07-04
 */
public class ConnectionMatrix implements GraphMatrix {

	/**
	 * Returns the connection matrix representation of this AtomContainer.
	 *
     * @param  container The AtomContainer for which the matrix is calculated
	 * @return           A connection matrix representating this AtomContainer
	 */
	public static double[][] getMatrix(IAtomContainer container) {
		ElectronContainer electronContainer = null;
		int indexAtom1;
		int indexAtom2;
		double[][] conMat = new double[container.getAtomCount()][container.getAtomCount()];
		for (int f = 0; f < container.getElectronContainerCount(); f++)
		{
			electronContainer = container.getElectronContainerAt(f);
			if (electronContainer instanceof org.openscience.cdk.interfaces.Bond)
			{
				Bond bond = (Bond) electronContainer;
				indexAtom1 = container.getAtomNumber(bond.getAtomAt(0));
				indexAtom2 = container.getAtomNumber(bond.getAtomAt(1));
				conMat[indexAtom1][indexAtom2] = bond.getOrder();
				conMat[indexAtom2][indexAtom1] = bond.getOrder();
			}
		}
		return conMat;
	}

}

