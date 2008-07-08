/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2008-02-25 14:11:58 +0100 (Mon, 25 Feb 2008) $
 *  
 *  Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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
package org.openscience.cdk.charges;


import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.Iterator;

/**
 *  Description of the Class
 *
 * @cdk.module test-charges
 *
 *@author     	  Miguel Rojas
 *@cdk.created    2008-18-05
 */
public class GasteigerPEPEPartialChargesTest extends NewCDKTestCase {

	private IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
    private LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    
	/**
	 *  A unit test for JUnit with methylenfluoride
	 *  
	 *  @cdk.inchi InChI=1/CH3F/c1-2/h1H3
	 */
    @Test
    public void testCalculateCharges_IAtomContainer() throws Exception {
    	double [] testResult={0.0,0.0,0.0,0.0,0.0};
		
		GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
		
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("F"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        
		addExplicitHydrogens(molecule);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		lpcheck.saturate(molecule);
		
		peoe.calculateCharges(molecule);
		for (int i=0;i<molecule.getAtomCount();i++){
			//logger.debug("Charge for atom:"+i+" S:"+mol.getAtomAt(i).getSymbol()+" Charge:"+mol.getAtomAt(i).getCharge());
			Assert.assertEquals(testResult[i],molecule.getAtom(i).getCharge(),0.01);
		}
	}

    @Test
    public void testAromaticAndNonAromatic() throws Exception {
        GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();

        String smiles1 = "c1ccccc1";
        String smiles2 = "C1=CC=CC=C1";

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = sp.parseSmiles(smiles1);
        IAtomContainer mol2 = sp.parseSmiles(smiles2);

        CDKHueckelAromaticityDetector.detectAromaticity(mol1);
        CDKHueckelAromaticityDetector.detectAromaticity(mol2);

        addExplicitHydrogens(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        lpcheck.saturate(mol1);

        addExplicitHydrogens(mol2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        lpcheck.saturate(mol2);

        peoe.calculateCharges(mol1);
        peoe.calculateCharges(mol2);
        for (int i = 0; i < mol1.getAtomCount(); i++) {
            Assert.assertEquals("charge on atom " + i + " does not match",
                    mol1.getAtom(i).getCharge(),
                    mol2.getAtom(i).getCharge(),
                    0.01);
        }

    }

    /**
     * 
	 */
    @Test
    public void testAssignGasteigerPiPartialCharges_IAtomContainer_Boolean() throws Exception {
    	double [] testResult={0.0,0.0,0.0,0.0,0.0};
		
		GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
		
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("F"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        
		addExplicitHydrogens(molecule);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		lpcheck.saturate(molecule);
		
		peoe.assignGasteigerPiPartialCharges(molecule, true);
		for (int i=0;i<molecule.getAtomCount();i++){
			//logger.debug("Charge for atom:"+i+" S:"+mol.getAtomAt(i).getSymbol()+" Charge:"+mol.getAtomAt(i).getCharge());
			Assert.assertEquals(testResult[i],molecule.getAtom(i).getCharge(),0.01);
		}
		
	}
    /**
     * 
	 */
    @Test
    public void testGetMaxGasteigerIters() throws Exception {
		
		GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
		
		Assert.assertEquals(8,peoe.getMaxGasteigerIters());
		
	}
    /**
     * 
	 */
    @Test
    public void testGetMaxResoStruc() throws Exception {
		
		GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
		
		Assert.assertEquals(50,peoe.getMaxResoStruc());
		
	}
    /**
     * 
	 */
    @Test
    public void testGetStepSize() throws Exception {
		
		GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
		Assert.assertEquals(5,peoe.getStepSize());
		
	}
    /**
     * 
	 */
    @Test
    public void testSetMaxGasteigerIters_Double() throws Exception {
		
		GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
		double MX_ITERATIONS = 10;
		peoe.setMaxGasteigerIters(MX_ITERATIONS);
		Assert.assertEquals(MX_ITERATIONS,peoe.getMaxGasteigerIters());
		
	}
    /**
     * 
	 */
    @Test
    public void testSetMaxResoStruc_Int() throws Exception {
		
		GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
		int MX_RESON = 1;
		peoe.setMaxResoStruc(MX_RESON);
		Assert.assertEquals(MX_RESON,peoe.getMaxResoStruc());
		
	}
    /**
     * 
	 */
    @Test
    public void testSetStepSize() throws Exception {
		
		GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
		int STEP_SIZE = 22;
		peoe.setStepSize(STEP_SIZE);
		Assert.assertEquals(STEP_SIZE,peoe.getStepSize());
		
	}
    /**
     * 
	 */
    @Test
    public void testAssignrPiMarsilliFactors_IAtomContainerSet() throws Exception {
    	GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
		
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("F"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        
        addExplicitHydrogens(molecule);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		lpcheck.saturate(molecule);
		for(Iterator<IAtom> it = molecule.atoms();it.hasNext();)
			it.next().setCharge(0.0);
		
        IMoleculeSet set = builder.newMoleculeSet();
        set.addAtomContainer(molecule);
        set.addAtomContainer(molecule);
		
        addExplicitHydrogens(molecule);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		lpcheck.saturate(molecule);
		
		Assert.assertNotNull(peoe.assignrPiMarsilliFactors(set));
		
		
	}
}
