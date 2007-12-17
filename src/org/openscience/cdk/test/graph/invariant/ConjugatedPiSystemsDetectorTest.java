/* $RCSfile$    
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
 * 
 */
package org.openscience.cdk.test.graph.invariant;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Checks the functionality of the ConjugatedPiSystemsCalculator.
 *
 * @cdk.module test-extra
 */
public class ConjugatedPiSystemsDetectorTest extends CDKTestCase
{
	
    private LoggingTool logger;
    
	public ConjugatedPiSystemsDetectorTest(String name)
	{
		super(name);
        logger = new LoggingTool(this);
	}
	
	public static Test suite() {
		return new TestSuite(ConjugatedPiSystemsDetectorTest.class);
	}

	public void testDetectButadiene() throws Exception
	{
        logger.info("Entering testDetectButadiene.");
        IMolecule mol = null;
        String filename = "data/cml/butadiene.cml";
        mol = readCMLMolecule(filename);

        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        AtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac = acSet.getAtomContainer(0);
        assertEquals(4, ac.getAtomCount());
        assertEquals(3, ac.getBondCount());
        
        for (int i = 0; i < ac.getAtomCount(); i++) {
            assertTrue(mol.contains(ac.getAtom(i)));
        }
        
        for (int i = 0; i < ac.getBondCount(); i++) {
            assertTrue(mol.contains(ac.getBond(i)));
        }
	}
	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    public void testDetectNaphtalene() throws Exception
	{
        logger.info("Entering testDetectNaphtalene.");
        IMolecule mol = null;
        String filename = "data/cml/naphtalene.cml";
        mol = readCMLMolecule(filename);

        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        AtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac = acSet.getAtomContainer(0);
        assertEquals(10, ac.getAtomCount());
        assertEquals(11, ac.getBondCount());
        
        for (int i = 0; i < ac.getAtomCount(); i++) {
            assertTrue(mol.contains(ac.getAtom(i)));
        }
        
        for (int i = 0; i < ac.getBondCount(); i++) {
            assertTrue(mol.contains(ac.getBond(i)));
        }
	}
    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    public void testDetectToluene() throws Exception
	{
        logger.info("Entering testDetectToluene.");
        IMolecule mol = null;
        String filename = "data/cml/toluene.cml";
        mol = readCMLMolecule(filename);

        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        AtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac = acSet.getAtomContainer(0);
        assertEquals(6, ac.getAtomCount());
        assertEquals(6, ac.getBondCount());
        
        for (int i = 0; i < ac.getAtomCount(); i++) {
            assertTrue(mol.contains(ac.getAtom(i)));
        }
        
        for (int i = 0; i < ac.getBondCount(); i++) {
            assertTrue(mol.contains(ac.getBond(i)));
        }
	}
    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    public void testNonConnectedPiSystems() throws Exception
	{
        logger.info("Entering testNonConnectedPiSystems.");
        IMolecule mol = null;
        String filename = "data/mdl/nonConnectedPiSystems.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        IChemFile chemFile = (IChemFile)reader.read((ChemObject)new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        AtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(2, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        assertEquals(4, ac1.getAtomCount());
        assertEquals(3, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            assertTrue(mol.contains(ac1.getBond(i)));
        }
        
        IAtomContainer ac2 = acSet.getAtomContainer(1);
        assertEquals(4, ac2.getAtomCount());
        assertEquals(3, ac2.getBondCount());
        
        for (int i = 0; i < ac2.getAtomCount(); i++) {
            assertTrue(mol.contains(ac2.getAtom(i)));
        }
        
        for (int i = 0; i < ac2.getBondCount(); i++) {
            assertTrue(mol.contains(ac2.getBond(i)));
        }
	}
    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    public void testPiSystemWithCarbokation() throws Exception
	{
        logger.info("Entering testPiSystemWithCarbokation.");
        IMolecule mol = null;
        String filename = "data/mdl/piSystemWithCarbokation.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        IChemFile chemFile = (IChemFile)reader.read((ChemObject)new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);    

        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        AtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        assertEquals(9, ac1.getAtomCount());
        assertEquals(8, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            assertTrue(mol.contains(ac1.getBond(i)));
        }
        
	}
    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    public void testPiSystemWithCumulativeDB() throws Exception
	{
        logger.info("Entering testPiSystemWithCumulativeDB.");
        IMolecule mol = null;
        String filename = "data/mdl/piSystemCumulative.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        IChemFile chemFile = (IChemFile)reader.read((ChemObject)new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);    

        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        AtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(2, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        assertEquals(4, ac1.getAtomCount());
        assertEquals(3, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            assertTrue(mol.contains(ac1.getBond(i)));
        }
        
        IAtomContainer ac2 = acSet.getAtomContainer(0);
        assertEquals(4, ac2.getAtomCount());
        assertEquals(3, ac2.getBondCount());
        
        for (int i = 0; i < ac2.getAtomCount(); i++) {
            assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac2.getBondCount(); i++) {
            assertTrue(mol.contains(ac1.getBond(i)));
        }
        
	}

    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    public void testAceticAcid() throws Exception
	{
    	IMolecule mol = null;
    	mol = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("CC(=O)O");
    	addImplicitHydrogens(mol);
    	LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    	lpcheck.saturate(mol);
    	AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        AtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        assertEquals(3, ac1.getAtomCount());
        assertEquals(2, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            assertTrue(mol.contains(ac1.getBond(i)));
        }
        
	}
    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    public void testNN_dimethylaniline_cation() throws Exception
	{
    	IMolecule mol = null;
    	String filename = "data/mdl/NN_dimethylaniline.mol";
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	MDLReader reader = new MDLReader(ins, Mode.STRICT);
    	IChemFile chemFile = (IChemFile)reader.read((ChemObject)new ChemFile());
    	mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);  
//    	for(int i =0;i<mol.getAtomCount();i++)
//    		System.out.println(i+", "+mol.getAtom(i).getSymbol()+" "+mol.getAtom(i).getHydrogenCount());

//  	mol.getAtomAt(6).setFormalCharge(1);
    	addImplicitHydrogens(mol);
    	LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    	lpcheck.saturate(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        AtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        assertEquals(6, ac1.getAtomCount());
        assertEquals(5, ac1.getBondCount());
        
	}

    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    public void test1_fluorobutadienene() throws Exception
	{
    	IMolecule mol = null;
    	mol = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("FC=CC=C");
    	addImplicitHydrogens(mol);
    	LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    	lpcheck.saturate(mol);
    	AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        AtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        assertEquals(5, ac1.getAtomCount());
        assertEquals(4, ac1.getBondCount());
        
	}
    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    public void testEthyne_difluoro() throws Exception
	{
    	IMolecule mol = null;
    	mol = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("FC#CF");
    	addImplicitHydrogens(mol);
    	LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    	lpcheck.saturate(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        AtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        assertEquals(4, ac1.getAtomCount());
        assertEquals(3, ac1.getBondCount());
        
        
        
	}
    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    public void test3Aminomethane_cation() throws Exception
	{
    	IMolecule mol = null;
    	mol = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("CN(C)C(N(C)C)N(C)C");
    	mol.getAtom(3).setFormalCharge(+1);
    	addImplicitHydrogens(mol);
    	LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    	lpcheck.saturate(mol);
    	AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        AtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        assertEquals(4, ac1.getAtomCount());
        assertEquals(3, ac1.getBondCount());
        
        
        
	}
    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    private IMolecule readCMLMolecule(String filename) throws Exception {
    	IMolecule mol = null;
    	logger.debug("Filename: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	CMLReader reader = new CMLReader(ins);

    	IChemFile file = (IChemFile)reader.read(new ChemFile());
    	assertNotNull(file);
    	assertEquals(1, file.getChemSequenceCount());
    	IChemSequence sequence = file.getChemSequence(0);
    	assertNotNull(sequence);
    	assertEquals(1, sequence.getChemModelCount());
    	IChemModel chemModel = sequence.getChemModel(0);
    	assertNotNull(chemModel);
    	IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
    	assertNotNull(moleculeSet);
    	assertEquals(1, moleculeSet.getMoleculeCount());
    	mol = moleculeSet.getMolecule(0);
    	assertNotNull(mol);
        
        return mol;
    	
    }
    
    /**
	 *  A unit test for JUnit: Cyanoallene
	 *
	 *@return    Description of the Return Value
	 */
    public void testCyanoallene() throws Exception
	{
    	IMolecule mol = null;
    	mol = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C=C=CC#N");
    	addImplicitHydrogens(mol);
    	LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    	lpcheck.saturate(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        AtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        assertEquals(4, ac1.getAtomCount());
        assertEquals(3, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            assertTrue(mol.contains(ac1.getBond(i)));
        }
        
	}
    
}
