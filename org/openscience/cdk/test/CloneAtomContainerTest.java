/* CloneAtomContainerTest.java
 * 
 * $ author: 	Christoph Steinbeck $ 
 * $ contact: 	steinbeck@ice.mpg.de 	$
 * $ date: 		2001-08-09 				$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test;

import junit.framework.*;
import org.openscience.cdk.*;

/**
 *
 * TestCase for the ChemObject class.
 *
 */
public class CloneAtomContainerTest extends TestCase {

	public CloneAtomContainerTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(CloneAtomContainerTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(CloneAtomContainerTest.class));
	}

	public void testClone() 
	{
		Molecule molecule = MoleculeFactory.makeAlphaPinene();
		Molecule clonedMol = (Molecule)molecule.clone();
		System.out.println("CloneAtomContainerTest->molecule: " + molecule);
		System.out.println("CloneAtomContainerTest->clonedMolecule: " + clonedMol);
		System.out.println("CloneAtomContainerTest->molecule.getAtomAt(3): " + molecule.getAtomAt(3));
		System.out.println("CloneAtomContainerTest->clonedMol.getAtomAt(3): " + clonedMol.getAtomAt(3));
		assert(molecule.getAtomCount() == clonedMol.getAtomCount());
		
	}

}
