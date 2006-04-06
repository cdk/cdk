/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.isomorphism;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.isomorphism.IsomorphismTester;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the funcitonality of the IsomorphismTester
 *
 * @cdk.module test-standard
 */
public class IsomorphismTesterTest extends CDKTestCase
{
	Molecule pinene_1 = null, pinene_2 = null, pinene_non = null;
	public IsomorphismTesterTest(String name)
	{
		super(name);
	}
	
	public void setUp()
	{
		pinene_1 = new Molecule();
		pinene_1.addAtom(new Atom("C")); // 1
		pinene_1.addAtom(new Atom("C")); // 2
		pinene_1.addAtom(new Atom("C")); // 3
		pinene_1.addAtom(new Atom("C")); // 4
		pinene_1.addAtom(new Atom("C")); // 5
		pinene_1.addAtom(new Atom("C")); // 6
		pinene_1.addAtom(new Atom("C")); // 7
		pinene_1.addAtom(new Atom("C")); // 8
		pinene_1.addAtom(new Atom("C")); // 9 
		pinene_1.addAtom(new Atom("C")); // 10
		
		pinene_1.addBond(0, 1, 2); // 1
		pinene_1.addBond(1, 2, 1); // 2
		pinene_1.addBond(2, 3, 1); // 3
		pinene_1.addBond(3, 4, 1); // 4
		pinene_1.addBond(4, 5, 1); // 5
		pinene_1.addBond(5, 0, 1); // 6
		pinene_1.addBond(0, 6, 1); // 7
		pinene_1.addBond(3, 7, 1); // 8
		pinene_1.addBond(5, 7, 1); // 9
		pinene_1.addBond(7, 8, 1); // 10
		pinene_1.addBond(7, 9, 1); // 11

		pinene_2 = new Molecule();
		pinene_2.addAtom(new Atom("C")); // 1
		pinene_2.addAtom(new Atom("C")); // 2
		pinene_2.addAtom(new Atom("C")); // 3
		pinene_2.addAtom(new Atom("C")); // 4
		pinene_2.addAtom(new Atom("C")); // 5
		pinene_2.addAtom(new Atom("C")); // 6
		pinene_2.addAtom(new Atom("C")); // 7
		pinene_2.addAtom(new Atom("C")); // 8
		pinene_2.addAtom(new Atom("C")); // 9 
		pinene_2.addAtom(new Atom("C")); // 10
		
		pinene_2.addBond(0, 4, 1); // 1
		pinene_2.addBond(0, 5, 1); // 2
		pinene_2.addBond(0, 8, 1); // 3
		pinene_2.addBond(1, 2, 1); // 4
		pinene_2.addBond(1, 9, 1); // 5
		pinene_2.addBond(2, 3, 1); // 6
		pinene_2.addBond(2, 0, 1); // 7
		pinene_2.addBond(3, 8, 1); // 8
		pinene_2.addBond(8, 7, 1); // 9
		pinene_2.addBond(7, 9, 2); // 10
		pinene_2.addBond(7, 6, 1); // 11
		
		pinene_non = new Molecule();
		pinene_non.addAtom(new Atom("C")); // 1
		pinene_non.addAtom(new Atom("C")); // 2
		pinene_non.addAtom(new Atom("C")); // 3
		pinene_non.addAtom(new Atom("C")); // 4
		pinene_non.addAtom(new Atom("C")); // 5
		pinene_non.addAtom(new Atom("C")); // 6
		pinene_non.addAtom(new Atom("C")); // 7
		pinene_non.addAtom(new Atom("C")); // 8
		pinene_non.addAtom(new Atom("C")); // 9 
		pinene_non.addAtom(new Atom("C")); // 10
		
		pinene_non.addBond(0, 5, 1); // 1
		pinene_non.addBond(0, 7, 1); // 2
		pinene_non.addBond(0, 8, 1); // 3
		pinene_non.addBond(1, 9, 1); // 4
		pinene_non.addBond(1, 4, 1); // 5
		pinene_non.addBond(2, 3, 1); // 6
		pinene_non.addBond(2, 4, 1); // 7
		pinene_non.addBond(2, 6, 1); // 8
		pinene_non.addBond(2, 7, 1); // 9
		pinene_non.addBond(4, 5, 2); // 10
		pinene_non.addBond(7, 9, 1); // 11
		
		
	}
	
	public static Test suite() {
		return new TestSuite(IsomorphismTesterTest.class);
	}

	public void testIsomorphism()
	{
		try
		{
			IsomorphismTester it = new IsomorphismTester(pinene_1);
			assertTrue(it.isIsomorphic(pinene_2));
			assertTrue(!it.isIsomorphic(pinene_non));
		}
		catch(Exception exc)
		{
			System.err.println("An Exception");
			fail();
		}
	}
}
