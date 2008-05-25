/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Calculator for a connection matrix representation of this AtomContainer. An
 * adjacency matrix is a matrix of quare NxN matrix, where N is the number of
 * atoms in the AtomContainer. If the i-th and the j-th atom in the
 * atomcontainer share a bond, the element i,j in the matrix is set to the
 * bond order value. Otherwise it is zero. See {@cdk.cite TRI92}.
 *
 * @cdk.module  standard
 * @cdk.svnrev  $Revision$
 * @cdk.keyword connection matrix
 * @cdk.dictref blue-obelisk:calculateConnectivityMatrix
 *
 * @author      steinbeck
 * @cdk.created 2004-07-04
 */
@TestClass("org.openscience.cdk.graph.matrix.ConnectionMatrixTest")
public class ConnectionMatrix implements IGraphMatrix {

	/**
	 * Returns the connection matrix representation of this AtomContainer.
	 *
     * @param  container The AtomContainer for which the matrix is calculated
	 * @return           A connection matrix representating this AtomContainer
	 */
    @TestMethod("testGetMatrix_IAtomContainer")
    public static double[][] getMatrix(IAtomContainer container) {
		IBond bond = null;
		int indexAtom1;
		int indexAtom2;
		double[][] conMat = new double[container.getAtomCount()][container.getAtomCount()];
		for (int f = 0; f < container.getBondCount(); f++)
		{
			bond = container.getBond(f);
			indexAtom1 = container.getAtomNumber(bond.getAtom(0));
			indexAtom2 = container.getAtomNumber(bond.getAtom(1));
			conMat[indexAtom1][indexAtom2] = BondManipulator.destroyBondOrder(bond.getOrder());
			conMat[indexAtom2][indexAtom1] = BondManipulator.destroyBondOrder(bond.getOrder());
		}
		return conMat;
	}

}

