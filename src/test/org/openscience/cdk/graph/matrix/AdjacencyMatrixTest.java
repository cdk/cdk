/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.NewCDKTestCase;

/**
 * @cdk.module test-atomtype
 */
public class AdjacencyMatrixTest extends NewCDKTestCase {
    
	private static SmilesParser sp;
	
    @BeforeClass
    public static void getSmilesParser() {
        sp = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
    }
	
    @Test
    public void testGetMatrix_IAtomContainer() throws Exception {
		IMolecule container = sp.parseSmiles("C1CC1");
		int[][] matrix = AdjacencyMatrix.getMatrix(container);
		Assert.assertEquals(3,matrix.length);
		Assert.assertEquals(3,matrix[0].length);
	}

}


