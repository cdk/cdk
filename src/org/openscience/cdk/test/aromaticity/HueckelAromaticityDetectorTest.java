/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.test.aromaticity;

import java.io.InputStream;

import javax.vecmath.Point2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  Description of the Class
 *
 * @cdk.module test-standard
 *
 *@author     steinbeck
 *@cdk.created    2002-10-06
 */
public class HueckelAromaticityDetectorTest extends CDKTestCase
{
	boolean standAlone = false;
	private static LoggingTool logger = new LoggingTool(HueckelAromaticityDetectorTest.class);

	/**
	 *  Constructor for the HueckelAromaticityDetectorTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public HueckelAromaticityDetectorTest(String name)
	{
		super(name);
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(HueckelAromaticityDetectorTest.class);
	}


	/**
	 *  Sets the standAlone attribute of the HueckelAromaticityDetectorTest object
	 *
	 *@param  standAlone  The new standAlone value
	 */
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}

	public void testDetectAromaticity_IAtomContainer()
	{
		IMolecule mol = makeAromaticMolecule();
		try
		{

			HueckelAromaticityDetector.detectAromaticity(mol);
			
			int numberOfAromaticAtoms = 0;
			for (int i = 0; i < mol.getAtomCount(); i++) {
				if (((IAtom)mol.getAtom(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticAtoms++;
			}
			assertEquals(6, numberOfAromaticAtoms);
			
			int numberOfAromaticBonds= 0;
			for (int i = 0; i < mol.getBondCount(); i++) {
				if (((IBond)mol.getBond(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticBonds++;
			}
			assertEquals(6, numberOfAromaticBonds);
			
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
	
	public void testDetectAromaticity_IAtomContainer_IRingSet()
	{
		
		IMolecule mol = makeAromaticMolecule();
		try
		{
			IRingSet rs = (new AllRingsFinder()).findAllRings(mol);
			HueckelAromaticityDetector.detectAromaticity(mol, rs);
			
			int numberOfAromaticAtoms = 0;
			for (int i = 0; i < mol.getAtomCount(); i++) {
				if (((IAtom)mol.getAtom(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticAtoms++;
			}
			assertEquals(6, numberOfAromaticAtoms);
			
			int numberOfAromaticBonds= 0;
			for (int i = 0; i < mol.getBondCount(); i++) {
				if (((IBond)mol.getBond(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticBonds++;
			}
			assertEquals(6, numberOfAromaticBonds);
			
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
	
	public void testDetectAromaticity_IAtomContainer_boolean()
	{
		
		try
		{
			IMolecule mol = makeAromaticMolecule();
			for (int i = 0; i < mol.getAtomCount(); ++i) mol.getAtom(i).setFlag(CDKConstants.ISAROMATIC, true);
			HueckelAromaticityDetector.detectAromaticity(mol, false);
			
			int numberOfAromaticAtoms = 0;
			for (int i = 0; i < mol.getAtomCount(); i++) {
				if (((IAtom)mol.getAtom(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticAtoms++;
			}
			assertEquals(10, numberOfAromaticAtoms);
			
			int numberOfAromaticBonds= 0;
			for (int i = 0; i < mol.getBondCount(); i++) {
				if (((IBond)mol.getBond(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticBonds++;
			}
			assertEquals(11, numberOfAromaticBonds);
			
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
		
		try
		{
			IMolecule mol = makeAromaticMolecule();
			for (int i = 0; i < mol.getAtomCount(); ++i) mol.getAtom(i).setFlag(CDKConstants.ISAROMATIC, true);
			HueckelAromaticityDetector.detectAromaticity(mol, true);
			
			int numberOfAromaticAtoms = 0;
			for (int i = 0; i < mol.getAtomCount(); i++) {
				if (((IAtom)mol.getAtom(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticAtoms++;
			}
			assertEquals(6, numberOfAromaticAtoms);
			
			int numberOfAromaticBonds= 0;
			for (int i = 0; i < mol.getBondCount(); i++) {
				if (((IBond)mol.getBond(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticBonds++;
			}
			assertEquals(6, numberOfAromaticBonds);
			
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
	
	public void testDetectAromaticity_IAtomContainer_boolean_AllRingsFinder()
	{
		try
		{
			IMolecule mol = makeAromaticMolecule();
			for (int i = 0; i < mol.getAtomCount(); ++i) mol.getAtom(i).setFlag(CDKConstants.ISAROMATIC, true);
			HueckelAromaticityDetector.detectAromaticity(mol, false, new AllRingsFinder());
			
			int numberOfAromaticAtoms = 0;
			for (int i = 0; i < mol.getAtomCount(); i++) {
				if (((IAtom)mol.getAtom(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticAtoms++;
			}
			assertEquals(10, numberOfAromaticAtoms);
			
			int numberOfAromaticBonds= 0;
			for (int i = 0; i < mol.getBondCount(); i++) {
				if (((IBond)mol.getBond(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticBonds++;
			}
			assertEquals(11, numberOfAromaticBonds);
			
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
		
		try
		{
			IMolecule mol = makeAromaticMolecule();
			for (int i = 0; i < mol.getAtomCount(); ++i) mol.getAtom(i).setFlag(CDKConstants.ISAROMATIC, true);
			HueckelAromaticityDetector.detectAromaticity(mol, true, new AllRingsFinder());
			
			int numberOfAromaticAtoms = 0;
			for (int i = 0; i < mol.getAtomCount(); i++) {
				if (((IAtom)mol.getAtom(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticAtoms++;
			}
			assertEquals(6, numberOfAromaticAtoms);
			
			int numberOfAromaticBonds= 0;
			for (int i = 0; i < mol.getBondCount(); i++) {
				if (((IBond)mol.getBond(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticBonds++;
			}
			assertEquals(6, numberOfAromaticBonds);
			
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
	
	public void testDetectAromaticity_IAtomContainer_IRingSet_boolean()
	{
		try
		{
			IMolecule mol = makeAromaticMolecule();
			for (int i = 0; i < mol.getAtomCount(); ++i) mol.getAtom(i).setFlag(CDKConstants.ISAROMATIC, true);
			IRingSet rs = (new AllRingsFinder()).findAllRings(mol);
			HueckelAromaticityDetector.detectAromaticity(mol, rs, false);
			
			int numberOfAromaticAtoms = 0;
			for (int i = 0; i < mol.getAtomCount(); i++) {
				if (((IAtom)mol.getAtom(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticAtoms++;
			}
			assertEquals(10, numberOfAromaticAtoms);
			
			int numberOfAromaticBonds= 0;
			for (int i = 0; i < mol.getBondCount(); i++) {
				if (((IBond)mol.getBond(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticBonds++;
			}
			assertEquals(11, numberOfAromaticBonds);
			
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
		
		try
		{
			IMolecule mol = makeAromaticMolecule();
			for (int i = 0; i < mol.getAtomCount(); ++i) mol.getAtom(i).setFlag(CDKConstants.ISAROMATIC, true);
			IRingSet rs = (new AllRingsFinder()).findAllRings(mol);
			HueckelAromaticityDetector.detectAromaticity(mol, rs, true);
			
			int numberOfAromaticAtoms = 0;
			for (int i = 0; i < mol.getAtomCount(); i++) {
				if (((IAtom)mol.getAtom(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticAtoms++;
			}
			assertEquals(6, numberOfAromaticAtoms);
			
			int numberOfAromaticBonds= 0;
			for (int i = 0; i < mol.getBondCount(); i++) {
				if (((IBond)mol.getBond(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticBonds++;
			}
			assertEquals(6, numberOfAromaticBonds);
			
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
	
	/**
	 *  A unit test for JUnit
	 */
	public void testSetRingFlags_IRingSet()
	{
		//boolean isAromatic = false;
		try
		{

			IMolecule mol = makeAromaticMolecule();
			IRingSet rs = (new AllRingsFinder()).findAllRings(mol);
			HueckelAromaticityDetector.detectAromaticity(mol, rs, true);
			
			IRingSet ringset = (new SSSRFinder(mol)).findSSSR();
			HueckelAromaticityDetector.setRingFlags(ringset);
			
			int numberOfAromaticRings = 0;
			for (int i = 0; i < ringset.getAtomContainerCount(); i++) {
				if (((Ring)ringset.getAtomContainer(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticRings++;
			}
			assertEquals(numberOfAromaticRings, 1);
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
	
	public void testGetRingSet()
	{
		IMolecule mol = makeAromaticMolecule();
		try
		{

			HueckelAromaticityDetector.detectAromaticity(mol);
			IRingSet rs = HueckelAromaticityDetector.getRingSet();
			assertEquals(3, rs.getAtomContainerCount());
			
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
	
	public void testSetTimeout_long()
	{
		HueckelAromaticityDetector.setTimeout(0);
		IMolecule mol = makeAromaticMolecule();
		try {
			HueckelAromaticityDetector.detectAromaticity(mol);
			int numberOfAromaticAtoms = 0;
			for (int i = 0; i < mol.getAtomCount(); i++) {
				if (((IAtom)mol.getAtom(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticAtoms++;
			}
			assertEquals(0, numberOfAromaticAtoms);
			
		} catch (Exception exc)
		{
			if (exc.getMessage().equals("Timeout for AllringsFinder exceeded")) {
				HueckelAromaticityDetector.setTimeout(5000); // reset timeout to default
				return;
			}
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
	
	public void testSetRingFinder_AllRingsFinder()
	{
		// Will fail because it never uses the instance of HAD (static method)
		AllRingsFinder finder = new AllRingsFinder();
		finder.setTimeout(0);
		HueckelAromaticityDetector detector = new HueckelAromaticityDetector(); 
		detector.setRingFinder(finder);
		IMolecule mol = makeAromaticMolecule();
		try {
			detector.detectAromaticity(mol);
			int numberOfAromaticAtoms = 0;
			for (int i = 0; i < mol.getAtomCount(); i++) {
				if (((IAtom)mol.getAtom(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticAtoms++;
			}
			assertEquals(0, numberOfAromaticAtoms);
			
		} catch (Exception exc)
		{
			if (exc.getMessage().equals("Timeout for AllringsFinder exceeded")) {
				return;
			}
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
	
	public void testHueckelAromaticityDetector()
	{
		// For autogenerated constructor
		HueckelAromaticityDetector detector = new HueckelAromaticityDetector(); 
		assertNotNull(detector);
	}
	
	public void testPyridine()
	{
		//boolean isAromatic = false;
		try
		{
			SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

			IMolecule mol = sp.parseSmiles("c1ccncc1");
			IRingSet rs = (new AllRingsFinder()).findAllRings(mol);
			HueckelAromaticityDetector.detectAromaticity(mol, rs, true);
			
			IRingSet ringset = (new SSSRFinder(mol)).findSSSR();
			HueckelAromaticityDetector.setRingFlags(ringset);
			
			int numberOfAromaticRings = 0;
			for (int i = 0; i < ringset.getAtomContainerCount(); i++) {
				if (((Ring)ringset.getAtomContainer(i)).getFlag(CDKConstants.ISAROMATIC))
					numberOfAromaticRings++;
			}
			assertEquals(numberOfAromaticRings, 1);
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
	
	
	/**
	 *  A unit test for JUnit The special difficulty with Azulene is that only the
	 *  outermost larger 10-ring is aromatic according to Hueckel rule.
	 */
	public void testAzulene()
	{
		boolean[] testResult =
				{true,
				true,
				true,
				true,
				true,
				true,
				true,
				true,
				true,
				true
				};
		Molecule molecule = MoleculeFactory.makeAzulene();
		boolean isAromatic = false;
		boolean result = false;
		try
		{
			isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			result = (molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC) == testResult[f]);
			assertTrue(result);
			logger.debug("Result for atom " + f + " is correct?: " + result);
		}
		if (standAlone && isAromatic)
		{
			System.out.println("Azulene is aromatic");
		}
	}


	/**
	 *  A unit test for JUnit. The N has to be counted correctly
	 */
	public void testIndole()
	{
		Molecule molecule = MoleculeFactory.makeIndole();
		boolean testResults[] = {
				true,
				true,
				true,
				true,
				true,
				true,
				true,
				true,
				true
				};
		//boolean isAromatic = false;
		try
		{
			HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testThiazole()
	{
		Molecule molecule = MoleculeFactory.makeThiazole();
		boolean[] testResults = {true, true, true, true, true};
		try
		{
			HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}

		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}

	}


	/**
	 *  A unit test for JUnit
	 */
	public void testTetraDehydroDecaline()
	{
		boolean isAromatic = false;
		//boolean testResults[] = {true, false, false};
		try
		{
			SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

			IMolecule mol = sp.parseSmiles("C1CCCc2c1cccc2");
			IRingSet rs = (new AllRingsFinder()).findAllRings(mol);
			//logger.debug("rs.size(): " + rs.size());
			HueckelAromaticityDetector.detectAromaticity(mol, rs, true);
			IRing r = null;
			int i = 0, aromacount = 0;
			java.util.Iterator rings = rs.atomContainers();
			while (rings.hasNext()) {
				r = (IRing)rings.next();
				isAromatic = r.getFlag(CDKConstants.ISAROMATIC);
				
				if (standAlone && isAromatic)
				{
					System.out.println("Ring " + i + " in test molecule is aromatic.");
				} else if (standAlone && !isAromatic)
				{
					System.out.println("Ring " + i + " in test molecule is not aromatic.");
				}
				if (isAromatic) aromacount++;
				i++;
			}
			assertEquals(aromacount, 1);
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}

    /**
     * This is a bug reported for JCP.
     */
    public void testSFBug956924() {
		try {
			SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

			IMolecule mol = sp.parseSmiles("[cH+]1cccccc1"); // tropylium cation
			assertTrue(HueckelAromaticityDetector.detectAromaticity(mol));
            assertEquals(7, mol.getAtomCount());
			for (int f = 0; f < mol.getAtomCount(); f++) {
				assertTrue(mol.getAtom(f).getFlag(CDKConstants.ISAROMATIC));
			}
		} catch (Exception exc) {
			fail(exc.toString());
		}
	}

    /**
     * This is a bug reported for JCP.
     */
    public void testSFBug956923() {
		boolean testResults[] = {false, false, false, false, false, false, false, false};
		try {
			SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

			IMolecule mol = sp.parseSmiles("O=c1cccccc1"); // tropone
			assertFalse(HueckelAromaticityDetector.detectAromaticity(mol));
            assertEquals(testResults.length, mol.getAtomCount());
			for (int f = 0; f < mol.getAtomCount(); f++) {
				assertTrue(mol.getAtom(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}
		} catch (Exception exc) {
			fail(exc.toString());
		}
	}

	/**
	 *  A unit test for JUnit
	 */
	public void testPorphyrine()
	{
		IMolecule molecule = null;
		boolean isAromatic = false;
		boolean testResults[] = {
				false,
				false,
				false,
				false,
				false,
				true,
				true,
				true,
				true,
				true,
				false,
				true,
				true,
				true,
				false,
				true,
				true,
				false,
				false,
				true,
				true,
				false,
				false,
				false,
				true,
				true,
				false,
				false,
				false,
				true,
				true,
				false,
				false,
				false,
				false,
				true,
				true,
				true,
				true,
				false,
				false,
				false
				};
		try
		{
			String filename = "data/mdl/porphyrin.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			molecule = (Molecule) reader.read((ChemObject) new Molecule());

			isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
			fail();
		}
		assertTrue(isAromatic);
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testBug698152()
	{
		Molecule molecule = null;
		//boolean isAromatic = false;
		boolean[] testResults = {true,
				true,
				true,
				true,
				true,
				true,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false};
		try
		{
			String filename = "data/mdl/bug698152.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			molecule = (Molecule) reader.read((ChemObject) new Molecule());

			HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
			fail();
		}
	}

	/**
	 *  A test for the fix of bug #716259, where a quinone ring 
	 *  was falsely detected as aromatic
	 */
	public void testBug716259()
	{
		Molecule molecule = null;
		//boolean isAromatic = false;
		boolean[] testResults = {
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false
		};
		try
		{
			String filename = "data/mdl/bug716259.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			molecule = (Molecule) reader.read((ChemObject) new Molecule());
			
			HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
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
	public void testQuinone()
	{
		Molecule molecule = MoleculeFactory.makeQuinone();
		//boolean isAromatic = false;
		boolean[] testResults = {false, false, false, false, false, false, false, false};
		try
		{
			HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}

		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}

	}
  
  public void testBug1328739() throws Exception {
      String filename = "data/mdl/bug1328739.mol";
      InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
      MDLReader reader = new MDLReader(ins);
      Molecule molecule = (Molecule) reader.read(new Molecule());
      HueckelAromaticityDetector.detectAromaticity(molecule);
      assertEquals(15, molecule.getBondCount());
      assertTrue(molecule.getBond(0).getFlag(CDKConstants.ISAROMATIC));
      assertTrue(molecule.getBond(1).getFlag(CDKConstants.ISAROMATIC));
      assertTrue(molecule.getBond(2).getFlag(CDKConstants.ISAROMATIC));
      assertTrue(molecule.getBond(3).getFlag(CDKConstants.ISAROMATIC));
      assertTrue(molecule.getBond(4).getFlag(CDKConstants.ISAROMATIC));
      assertTrue(molecule.getBond(6).getFlag(CDKConstants.ISAROMATIC));
  }

	/**
	 *  A unit test for JUnit
	 */
	public void testBenzene()
	{
		Molecule molecule = MoleculeFactory.makeBenzene();
		//boolean isAromatic = false;
		boolean[] testResults = {true,true,true,true,true,true};
		try
		{
			HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtom(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}

		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
		
	}

	private IMolecule makeAromaticMolecule()
	{
		  IMolecule mol = new Molecule();
		  IAtom a1 = mol.getBuilder().newAtom("C");
		  a1.setPoint2d(new Point2d(329.99999999999994, 971.0));  mol.addAtom(a1);
		  IAtom a2 = mol.getBuilder().newAtom("C");
		  a2.setPoint2d(new Point2d(298.8230854637602, 989.0));  mol.addAtom(a2);
		  IAtom a3 = mol.getBuilder().newAtom("C");
		  a3.setPoint2d(new Point2d(298.8230854637602, 1025.0));  mol.addAtom(a3);
		  IAtom a4 = mol.getBuilder().newAtom("C");
		  a4.setPoint2d(new Point2d(330.0, 1043.0));  mol.addAtom(a4);
		  IAtom a5 = mol.getBuilder().newAtom("C");
		  a5.setPoint2d(new Point2d(361.1769145362398, 1025.0));  mol.addAtom(a5);
		  IAtom a6 = mol.getBuilder().newAtom("C");
		  a6.setPoint2d(new Point2d(361.1769145362398, 989.0));  mol.addAtom(a6);
		  IAtom a7 = mol.getBuilder().newAtom("C");
		  a7.setPoint2d(new Point2d(392.3538290724796, 971.0));  mol.addAtom(a7);
		  IAtom a8 = mol.getBuilder().newAtom("C");
		  a8.setPoint2d(new Point2d(423.5307436087194, 989.0));  mol.addAtom(a8);
		  IAtom a9 = mol.getBuilder().newAtom("C");
		  a9.setPoint2d(new Point2d(423.5307436087194, 1025.0));  mol.addAtom(a9);
		  IAtom a10 = mol.getBuilder().newAtom("C");
		  a10.setPoint2d(new Point2d(392.3538290724796, 1043.0));  mol.addAtom(a10);
		  IBond b1 = mol.getBuilder().newBond(a1, a2, 2.0);
		  mol.addBond(b1);
		  IBond b2 = mol.getBuilder().newBond(a2, a3, 1.0);
		  mol.addBond(b2);
		  IBond b3 = mol.getBuilder().newBond(a3, a4, 2.0);
		  mol.addBond(b3);
		  IBond b4 = mol.getBuilder().newBond(a4, a5, 1.0);
		  mol.addBond(b4);
		  IBond b5 = mol.getBuilder().newBond(a5, a6, 2.0);
		  mol.addBond(b5);
		  IBond b6 = mol.getBuilder().newBond(a6, a1, 1.0);
		  mol.addBond(b6);
		  IBond b7 = mol.getBuilder().newBond(a6, a7, 1.0);
		  mol.addBond(b7);
		  IBond b8 = mol.getBuilder().newBond(a7, a8, 1.0);
		  mol.addBond(b8);
		  IBond b9 = mol.getBuilder().newBond(a8, a9, 1.0);
		  mol.addBond(b9);
		  IBond b10 = mol.getBuilder().newBond(a9, a10, 1.0);
		  mol.addBond(b10);
		  IBond b11 = mol.getBuilder().newBond(a10, a5, 1.0);
		  mol.addBond(b11);
		  return mol;
	}
	
	/**
	 *  The main program for the HueckelAromaticityDetectorTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		HueckelAromaticityDetectorTest hadt = new HueckelAromaticityDetectorTest("HueckelAromaticityDetectorTest");
		hadt.setStandAlone(true);
		//hadt.testAzulene();
		hadt.testTetraDehydroDecaline();
		//hadt.testIndole();
		//hadt.testThiazole();
		//hadt.testBug698152();
		//hadt.testPorphyrine();
		//hadt.testQuinone();
		//hadt.testBenzene();
		//hadt.testBug716259();
	}
}

