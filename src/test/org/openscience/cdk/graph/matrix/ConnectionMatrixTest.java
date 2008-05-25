/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.graph.matrix;


import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-standard
 */
public class ConnectionMatrixTest extends NewCDKTestCase {

	private final static SmilesParser sp = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());

    public ConnectionMatrixTest() {
        super();
    }

    @Test
	public void testGetMatrix_IAtomContainer() throws Exception {
		IMolecule container = sp.parseSmiles("C1CC1");
		double[][] matrix = ConnectionMatrix.getMatrix(container);
		Assert.assertEquals(3,matrix.length);
		Assert.assertEquals(3,matrix[0].length);
	}

    @Test
	public void testLonePairs() throws Exception {
		IMolecule container = new NNMolecule();
		container.addAtom(container.getBuilder().newAtom("I"));
		container.addLonePair(container.getBuilder().newLonePair(container.getAtom(0)));
		container.addAtom(container.getBuilder().newAtom("H"));
		container.getBuilder().newBond(container.getAtom(0), container.getAtom(1), IBond.Order.SINGLE);
		
		double[][] matrix = ConnectionMatrix.getMatrix(container);
		Assert.assertEquals(2,matrix.length);
		Assert.assertEquals(2,matrix[0].length);
	}

}


