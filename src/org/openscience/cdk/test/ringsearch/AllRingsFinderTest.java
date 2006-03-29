/*
 * $RCSfile$
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
 */
package org.openscience.cdk.test.ringsearch;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test
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

		assertTrue(ringSet.size() == 6); 
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
			for (int i = 0; i < ringSet.size(); i++) 
			{
				Ring ring = (Ring)ringSet.get(i);
				for (int j = 0; j < ring.getElectronContainerCount(); j++) 
				{
					org.openscience.cdk.interfaces.IElectronContainer ec = ring.getElectronContainerAt(j);
					if (ec instanceof org.openscience.cdk.interfaces.IBond)
					{
						org.openscience.cdk.interfaces.IAtom atom1 = ((Bond)ec).getAtomAt(0);
						org.openscience.cdk.interfaces.IAtom atom2 = ((Bond)ec).getAtomAt(1);
						assertTrue(ring.contains(atom1) && ring.contains(atom2));
					}
				}
			}
		
		}
		catch(Exception exc)
		{
			System.out.println(exc);
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
		    CMLReader reader = new CMLReader(new InputStreamReader(ins));
			IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
		    org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
		    org.openscience.cdk.interfaces.IMolecule mol = model.getSetOfMolecules().getMolecule(0);
		    //System.out.println("Constructed Molecule");
		    //System.out.println("Starting AllRingsFinder");
		    ringSet = new AllRingsFinder().findAllRings(mol);
		    //System.out.println("Finished AllRingsFinder");
            assertEquals(24, ringSet.size());
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
		    CMLReader reader = new CMLReader(new InputStreamReader(ins));
			IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		    org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
		    org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
		    org.openscience.cdk.interfaces.IMolecule mol = model.getSetOfMolecules().getMolecule(0);
		    if (standAlone) System.out.println("Constructed Molecule");
		    if (standAlone) System.out.println("Starting AllRingsFinder");
		    IRingSet ringSet = new AllRingsFinder().findAllRings(mol);
		    if (standAlone) System.out.println("Finished AllRingsFinder");
		    if (standAlone) System.out.println("Found " + ringSet.size() + " rings.");
	
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
		arft.saTestBug777488();
		//arft.testBigMoleculeWithIsolatedRings();
	}	
}

