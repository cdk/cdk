/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.ringsearch;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * This class tests the SSSRFinder class.
 *
 * @cdk.module test-extra
 *
 * @author     steinbeck
 * @cdk.created    2003-10-17
 */
public class RingSearchTest extends CDKTestCase
{

	static boolean standAlone = false;
	//private LoggingTool logger = null;
  


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
    public void setUp() throws Exception {
        super.setUp();
        //logger = new LoggingTool(this);
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
		IMolecule molecule = MoleculeFactory.makeAlphaPinene();
		IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
		assertEquals(2, ringSet.getAtomContainerCount());
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBenzene() throws Exception
	{
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule molecule = sp.parseSmiles("c1ccccc1");
		IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
		assertEquals(1, ringSet.getAtomContainerCount());
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBicyclicCompound() throws Exception
	{
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule molecule = sp.parseSmiles("C1CCC(CCCCC2)C2C1");
		IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
		assertEquals(2, ringSet.getAtomContainerCount());
	}

	public void testSFBug826942() throws Exception
	{
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule molecule = sp.parseSmiles("C1CCC2C(C1)C4CCC3(CCCCC23)(C4)");
		IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
		assertEquals(4, ringSet.getAtomContainerCount());
	}

	/**
	 *  A unit test for JUnit
	 */
	public void testProblem1()
	{
		IMolecule molecule = null;
		IRing ring = null;
		try
		{
			String filename = "data/mdl/figueras-test-sep3D.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			molecule = (IMolecule) reader.read((IChemObject) new org.openscience.cdk.Molecule());
			if (standAlone) System.out.println("Testing " + filename);
			
            IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
			if (standAlone) System.out.println("Found ring set of size: " + ringSet.getAtomContainerCount());
			assertEquals(3, ringSet.getAtomContainerCount());
			for (int f = 0; f < ringSet.getAtomContainerCount(); f++)
			{
				ring = (IRing) ringSet.getAtomContainer(f);
				if (standAlone) System.out.println("ring: " + toString(ring, molecule));
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
		IMolecule molecule = null;
		IRing ring = null;
		try
		{
			String filename = "data/mdl/figueras-test-buried.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			molecule = (IMolecule) reader.read((IChemObject) new org.openscience.cdk.Molecule());
			if (standAlone) System.out.println("Testing " + filename);
			
            IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
			if (standAlone) System.out.println("Found ring set of size: " + ringSet.getAtomContainerCount());
			assertEquals(10, ringSet.getAtomContainerCount());
			for (int f = 0; f < ringSet.getAtomContainerCount(); f++)
			{
				ring = (IRing) ringSet.getAtomContainer(f);
				if (standAlone) System.out.println("ring: " + toString(ring, molecule));
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
		IMolecule molecule = null;
		IRing ring = null;
		try
		{
			String filename = "data/mdl/figueras-test-inring.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			molecule = (IMolecule) reader.read((IChemObject) new org.openscience.cdk.Molecule());
			if (standAlone) System.out.println("Testing " + filename);
			
            IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
			if (standAlone) System.out.println("Found ring set of size: " + ringSet.getAtomContainerCount());
			assertEquals(5, ringSet.getAtomContainerCount());
			for (int f = 0; f < ringSet.getAtomContainerCount(); f++)
			{
				ring = (IRing) ringSet.getAtomContainer(f);
				if (standAlone) System.out.println("ring: " + toString(ring, molecule));
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
			fail();
		}
	}
	
	public void testBug891021() {
		IMolecule molecule = null;
		try {
			String filename = "data/mdl/too.many.rings.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			molecule = (IMolecule) reader.read((IChemObject) new org.openscience.cdk.Molecule());
			if (standAlone) System.out.println("Testing " + filename);
			
            IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
			if (standAlone) System.out.println("Found ring set of size: " + ringSet.getAtomContainerCount());
		} catch (Exception exc) {
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
        try {
            rst.setUp();
            rst.testProblem1();
            rst.testProblem2();
            rst.testProblem3();
        } catch(Exception exc) {
            System.err.println("Could setup the TestCase");
        }
	}

	 /**
	  * Convenience method for giving a string representation 
	  * of this ring based on the number of the atom in a given 
	  * molecule.
      *
	  * @param molecule  A molecule to determine an atom number for each ring atom
      * @return          string representation of this ring
	  */
	public String toString(IRing ring, IMolecule molecule)
	{
		String str = "";
		for (int f = 0; f < ring.getAtomCount(); f++)
		{
			try
			{
				str += molecule.getAtomNumber(ring.getAtom(f)) +  " - ";
			}
			catch(Exception exc)
			{
			    System.err.println("Could not create string representation of Ring: " + exc.getMessage());
			}
		}
		return str;
	}
	
}

