/*
 * $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.ringsearch;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class AllRingsFinderTest extends CDKTestCase
{
	boolean standAlone = false;
	
	public AllRingsFinderTest(String name)
	{
		super(name);
	}

	public static Test suite() {
		return new TestSuite(AllRingsFinderTest.class);
	}

	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	public void testAllRingsFinder()
	{
		AllRingsFinder arf = new AllRingsFinder();
		assertNotNull(arf);
	}
	
	public void testFindAllRings_IAtomContainer()
	{
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;
		Molecule molecule = MoleculeFactory.makeEthylPropylPhenantren();
		//display(molecule);
		try
		{
			ringSet = arf.findAllRings(molecule);
		}
		catch(Exception exc)
		{
			System.out.println(exc);	
		}

		assertTrue(ringSet.getAtomContainerCount() == 6); 
	}
	
	
	// Bug #746067
	public void testBondsWithinRing()
	{
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;
		Molecule molecule = MoleculeFactory.makeEthylPropylPhenantren();
		//display(molecule);
		try
		{
			ringSet = arf.findAllRings(molecule);
			for (int i = 0; i < ringSet.getAtomContainerCount(); i++) 
			{
				Ring ring = (Ring)ringSet.getAtomContainer(i);
				for (int j = 0; j < ring.getBondCount(); j++) 
				{
					org.openscience.cdk.interfaces.IBond ec = ring.getBond(j);

					org.openscience.cdk.interfaces.IAtom atom1 = ec.getAtom(0);
					org.openscience.cdk.interfaces.IAtom atom2 = ec.getAtom(1);
					assertTrue(ring.contains(atom1) && ring.contains(atom2));
				}
			}
		
		}
		catch(Exception exc)
		{
			System.out.println(exc);
		}
	}
	
	public void testFindAllRings_IAtomContainer_boolean()
	{
		fail("Test not implemented yet - it is depending on new implementation of AllRingsFinder.");
	}
	
	public void testSetTimeout_long()
	{
		AllRingsFinder arf = new AllRingsFinder();
		arf.setTimeout(0);
		Molecule molecule = MoleculeFactory.makeEthylPropylPhenantren();
		//display(molecule);
		try {
			arf.findAllRings(molecule);
		} catch (CDKException ex) {
			assertEquals("Timeout for AllringsFinder exceeded", ex.getMessage());
			return;
		}
		fail("Timeout did not throw CDKException.");
	}
	
	public void testCheckTimeout()
	{
		fail("Not implemented.");		
	}
	
	public void testGetTimeout()
	{
		AllRingsFinder arf = new AllRingsFinder();
		arf.setTimeout(3);
		assertEquals(3, arf.getTimeout(), 0.01);
	}
	
	public void testPorphyrine()
	{
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;
		try
		{
	        String filename = "data/mdl/porphyrin.mol";
	        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLReader reader = new MDLReader(ins);
			IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
		    org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
		    org.openscience.cdk.interfaces.IMolecule molecule = model.getMoleculeSet().getMolecule(0);
			
			ringSet = arf.findAllRings(molecule);
			assertEquals(20, ringSet.getAtomContainerCount());
		
		}
		catch(Exception exc)
		{
			fail(exc.getMessage());
		}
	}
	
	public void testCholoylCoA()
	{
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;
		try
		{
	        String filename = "data/mdl/choloylcoa.mol";
	        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLReader reader = new MDLReader(ins);
			IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
		    org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
		    org.openscience.cdk.interfaces.IMolecule molecule = model.getMoleculeSet().getMolecule(0);
			
			ringSet = arf.findAllRings(molecule);
			for (int i = 0; i < ringSet.getAtomContainerCount(); ++i) {
				System.out.println(ringSet.getAtomContainer(i).getAtomCount());
			}
			assertEquals(14, ringSet.getAtomContainerCount());
		
		}
		catch(Exception exc)
		{
			fail(exc.getMessage());
		}
	}
	
	public void showAzulene()
	{
		MoleculeListViewer listview = new MoleculeListViewer();
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;
		try
		{
	        String filename = "data/mdl/azulene.mol";
	        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLReader reader = new MDLReader(ins);
			IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
		    org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
		    org.openscience.cdk.interfaces.IMolecule molecule = model.getMoleculeSet().getMolecule(0);
		    listview.addStructure(molecule, "Azulene", false, false);
			
			ringSet = arf.findAllRings(molecule);
			for (int i = 0; i < ringSet.getAtomContainerCount(); i++) 
			{
				IAtomContainer ac = ringSet.getAtomContainer(i);
				Molecule newMol = new Molecule(ac);
				listview.addStructure(newMol, "ring no. " + (i+1), false, false);
			}
		
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
	
	public void showPorphyrin()
	{
		MoleculeListViewer listview = new MoleculeListViewer();
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		arf.setTimeout(10000);
		if (standAlone) arf.debug = true;
		try
		{
	        String filename = "data/mdl/porphyrin.mol";
	        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLReader reader = new MDLReader(ins);
			IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
		    org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
		    org.openscience.cdk.interfaces.IMolecule molecule = model.getMoleculeSet().getMolecule(0);
		    listview.addStructure(molecule, "Porphyrin", false, false);
			
			ringSet = arf.findAllRings(molecule);
			for (int i = 0; i < ringSet.getAtomContainerCount(); i++) 
			{
				IAtomContainer ac = ringSet.getAtomContainer(i);
				Molecule newMol = new Molecule(ac);
				String title = "ring no. " + (i + 1);
				if (HueckelAromaticityDetector.detectAromaticity(newMol)) title += " is aromatic";
				listview.addStructure(newMol, title , false, false);
			}
		
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}

	
	
	public void testBigMoleculeWithIsolatedRings()
    {
        IRingSet ringSet = null;
        AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;
        
        String filename = "data/cml/isolated_ringsystems.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		try {
		    CMLReader reader = new CMLReader(ins);
			IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
		    org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
		    org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
		    //logger.debug("Constructed Molecule");
		    //logger.debug("Starting AllRingsFinder");
		    ringSet = new AllRingsFinder().findAllRings(mol);
		    //logger.debug("Finished AllRingsFinder");
            assertEquals(24, ringSet.getAtomContainerCount());
		    //display(mol);
		
		} catch (Exception e) 
		{
		    if (standAlone) e.printStackTrace();
		    fail(e.toString());
		}
        
    }
    
	/* This test takes a very long time. It was to ensure that 
	   AllRingsFinder acually stops for the given examples. 
	   And it does, after a very long time. 
	   So, the test is commented our because of its long runtime */
	public void saTestBug777488()
	{
	        //String filename = "data/Bug646.cml";
                String filename = "data/cml/testBug777488-1-AllRingsFinder.cml";
		//String filename = "data/NCI_diversity_528.mol.cml";
		//String filename = "data/NCI_diversity_978.mol.cml";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		try {
		    CMLReader reader = new CMLReader(ins);
			IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
		    org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
		    org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
		    if (standAlone) System.out.println("Constructed Molecule");
		    if (standAlone) System.out.println("Starting AllRingsFinder");
		    IRingSet ringSet = new AllRingsFinder().findAllRings(mol);
		    if (standAlone) System.out.println("Finished AllRingsFinder");
		    if (standAlone) System.out.println("Found " + ringSet.getAtomContainerCount() + " rings.");
	
		   //display(mol);
		
		} catch (Exception e) 
		{
		    e.printStackTrace();
		    fail(e.toString());
		}
	}
	
	public static void main(String[] args)
	{
		AllRingsFinderTest arft = new AllRingsFinderTest("AllRingsFinderTest");
		arft.setStandAlone(true);
		//arft.testAllRingsFinder();
		//arft.saTestBug777488();
		//arft.showAzulene();
		arft.showPorphyrin();
		
		//arft.testBigMoleculeWithIsolatedRings();
	}	
}

