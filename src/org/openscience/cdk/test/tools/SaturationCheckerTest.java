/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 *
 *  Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.tools;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.renderer.*;
import junit.framework.*;

/**
 *  Description of the Class
 *
 *@author     steinbeck
 *@created    February 20, 2003
 */
public class SaturationCheckerTest extends TestCase
{

	SaturationChecker satcheck = null;
	boolean standAlone = false;


	/**
	 *  Constructor for the SaturationCheckerTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public SaturationCheckerTest(String name)
	{
		super(name);
	}


	/**
	 *  The JUnit setup method
	 */
	public void setUp()
	{
		try
		{
			satcheck = new SaturationChecker();
		} catch (Exception e)
		{
			fail();
		}
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(SaturationCheckerTest.class);
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testAllSaturated()
	{
		// test methane with explicit hydrogen
		Molecule m = new Molecule();
		Atom c = new Atom("C");
		Atom h1 = new Atom("H");
		Atom h2 = new Atom("H");
		Atom h3 = new Atom("H");
		Atom h4 = new Atom("H");
		m.addAtom(c);
		m.addAtom(h1);
		m.addAtom(h2);
		m.addAtom(h3);
		m.addAtom(h4);
		m.addBond(new Bond(c, h1));
		m.addBond(new Bond(c, h2));
		m.addBond(new Bond(c, h3));
		m.addBond(new Bond(c, h4));
		assertTrue(satcheck.allSaturated(m));

		// test methane with implicit hydrogen
		m = new Molecule();
		c = new Atom("C");
		c.setHydrogenCount(4);
		m.addAtom(c);
		assertTrue(satcheck.allSaturated(m));
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testIsSaturated()
	{
		// test methane with explicit hydrogen
		Molecule m = new Molecule();
		Atom c = new Atom("C");
		Atom h1 = new Atom("H");
		Atom h2 = new Atom("H");
		Atom h3 = new Atom("H");
		Atom h4 = new Atom("H");
		m.addAtom(c);
		m.addAtom(h1);
		m.addAtom(h2);
		m.addAtom(h3);
		m.addAtom(h4);
		m.addBond(new Bond(c, h1));
		m.addBond(new Bond(c, h2));
		m.addBond(new Bond(c, h3));
		m.addBond(new Bond(c, h4));
		assertTrue(satcheck.isSaturated(c, m));
		assertTrue(satcheck.isSaturated(h1, m));
		assertTrue(satcheck.isSaturated(h2, m));
		assertTrue(satcheck.isSaturated(h3, m));
		assertTrue(satcheck.isSaturated(h4, m));
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testSaturate()
	{
		// test ethene
		Atom c1 = new Atom("C");
		c1.setHydrogenCount(2);
		Atom c2 = new Atom("C");
		c2.setHydrogenCount(2);
		Bond b = new Bond(c1, c2, 1);
		// force single bond, saturate() must fix that
		Molecule m = new Molecule();
		m.addAtom(c1);
		m.addAtom(c2);
		m.addBond(b);
		satcheck.saturate(m);
		assertTrue(2.0 == b.getOrder());
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testAromaticSaturation()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7


		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 4, 1.0); // 4
		mol.addBond(4, 5, 1.0); // 5
		mol.addBond(5, 0, 1.0); // 6
		mol.addBond(0, 6, 1.0); // 7
		mol.addBond(6, 7, 3.0); // 8
		
		for (int f = 0; f < 6; f++)
		{
			mol.getAtomAt(f).flags[CDKConstants.ISAROMATIC] = true;
			mol.getBondAt(f).flags[CDKConstants.ISAROMATIC] = true;
		}
		
		satcheck.addHydrogensToSatisfyValency(mol);
		satcheck.saturate(mol);
		MFAnalyser mfa = new MFAnalyser(mol);
		if (standAlone)
		{
			MoleculeViewer2D.display(mol, true);
		}
		assertEquals(mfa.getAtomCount("H"),6);
	}
	
	/**
	 *  The main program for the SaturationCheckerTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		SaturationCheckerTest sct = new SaturationCheckerTest("SaturationCheckerTest");
		sct.standAlone = true;
		sct.setUp();
		sct.testAromaticSaturation();
	}
}

