/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.ringsearch;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  This class tests the SSSRFinder class.
 *
 * @cdkPackage test
 *
 *@author     steinbeck
 *@created    October 17, 2003
 */
public class RingSearchTest extends TestCase
{

	static boolean standAlone = false;
	private LoggingTool logger = null;
	SSSRFinder sssrf;


	/**
	 *  Constructor for the RingSearchTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public RingSearchTest(String name)
	{
		super(name);
	}


	/**
	 *  The JUnit setup method
	 */
	public void setUp()
	{
		logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
		sssrf = new SSSRFinder();
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(RingSearchTest.class);
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testAlphaPinene()
	{
		Molecule molecule = MoleculeFactory.makeAlphaPinene();
		RingSet ringSet = sssrf.findSSSR(molecule);
		assertEquals(2, ringSet.size());
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBenzene() throws Exception
	{
		SmilesParser sp = new SmilesParser();
		Molecule molecule = sp.parseSmiles("c1ccccc1");
		RingSet ringSet = sssrf.findSSSR(molecule);
		assertEquals(1, ringSet.size());
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBicyclicCompound() throws Exception
	{
		SmilesParser sp = new SmilesParser();
		Molecule molecule = sp.parseSmiles("C1CCC(CCCCC2)C2C1");
		RingSet ringSet = sssrf.findSSSR(molecule);
		assertEquals(2, ringSet.size());
	}



	/**
	 *  A unit test for JUnit
	 */
	public void testProblem1()
	{
		Molecule molecule = null;
		Ring ring = null;
		try
		{
			String filename = "data/mdl/figueras-test-sep3D.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(new InputStreamReader(ins));
			molecule = (Molecule) reader.read((ChemObject) new Molecule());
			if (standAlone) System.out.println("Testing " + filename);
			
			RingSet ringSet = sssrf.findSSSR(molecule);
			if (standAlone) System.out.println("Found ring set of size: " + ringSet.size());
			assertTrue(ringSet.size() == 3);
			for (int f = 0; f < ringSet.size(); f++)
			{
				ring = (Ring) ringSet.elementAt(f);
				if (standAlone) System.out.println("ring: " + ring.toString(molecule));
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
			fail();
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testProblem2()
	{
		Molecule molecule = null;
		Ring ring = null;
		try
		{
			String filename = "data/mdl/figueras-test-buried.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(new InputStreamReader(ins));
			molecule = (Molecule) reader.read((ChemObject) new Molecule());
			if (standAlone) System.out.println("Testing " + filename);
			
			RingSet ringSet = sssrf.findSSSR(molecule);
			if (standAlone) System.out.println("Found ring set of size: " + ringSet.size());
			assertTrue(ringSet.size() == 10);
			for (int f = 0; f < ringSet.size(); f++)
			{
				ring = (Ring) ringSet.elementAt(f);
				if (standAlone) System.out.println("ring: " + ring.toString(molecule));
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
			fail();
		}
	}

	
	/**
	 *  A unit test for JUnit
	 */
	public void testProblem3()
	{
		Molecule molecule = null;
		Ring ring = null;
		try
		{
			String filename = "data/mdl/figueras-test-inring.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(new InputStreamReader(ins));
			molecule = (Molecule) reader.read((ChemObject) new Molecule());
			if (standAlone) System.out.println("Testing " + filename);
			
			RingSet ringSet = sssrf.findSSSR(molecule);
			if (standAlone) System.out.println("Found ring set of size: " + ringSet.size());
			assertTrue(ringSet.size() == 5);
			for (int f = 0; f < ringSet.size(); f++)
			{
				ring = (Ring) ringSet.elementAt(f);
				if (standAlone) System.out.println("ring: " + ring.toString(molecule));
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
			fail();
		}
	}
	
	
	/**
	 *  The main program for the RingSearchTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		RingSearchTest rst = new RingSearchTest("RingSearchTest");
		standAlone = true;
		rst.setUp();
		rst.testProblem1();
		rst.testProblem2();
		rst.testProblem3();

	}

}

