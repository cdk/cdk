/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-03-29 23:42:34 +0100 (Wed, 29 Mar 2006) $
 * $Revision: 5865 $
 * 
 * Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
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
 * 
 */

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * Checks the functionality of the Crystal.
 *
 * @cdk.module test-extra
 */
public class VariousTests extends CDKTestCase {

    public VariousTests(String name) {
        super(name);
    }

	public void test1456139() throws Exception{
		SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = p.parseSmiles("Cc1nn(C)cc1[C@H]2[C@H](C(=O)N)C(=O)C[C@@](C)(O)[C@@H]2C(=O)N");
		IMolecule mol2=DefaultChemObjectBuilder.getInstance().newMolecule(mol);		
		assertNotNull(mol2);
		assertEquals(22, mol2.getAtomCount());
	}

	public static Test suite() {
		return new TestSuite(VariousTests.class);
	}

}
