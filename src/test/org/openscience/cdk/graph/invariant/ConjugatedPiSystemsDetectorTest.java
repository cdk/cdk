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
package org.openscience.cdk.graph.invariant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.InputStream;

/**
 * Checks the functionality of the ConjugatedPiSystemsCalculator.
 *
 * @cdk.module test-reaction
 */
public class ConjugatedPiSystemsDetectorTest extends NewCDKTestCase
{
	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
    LonePairElectronChecker lpcheck = new LonePairElectronChecker();
	
    private LoggingTool logger;

    @Before
    public void ConjugatedPiSystemsDetectorTest(String name)
	{
        logger = new LoggingTool(this);
	}

	@Test
    public void testDetectButadiene() throws Exception
	{
        logger.info("Entering testDetectButadiene.");
        IMolecule mol = null;
        String filename = "data/cml/butadiene.cml";
        mol = readCMLMolecule(filename);

        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac.getAtomCount());
        Assert.assertEquals(3, ac.getBondCount());
        
        for (int i = 0; i < ac.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac.getAtom(i)));
        }
        
        for (int i = 0; i < ac.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac.getBond(i)));
        }
	}
	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    @Test public void testDetectNaphtalene() throws Exception
	{
        logger.info("Entering testDetectNaphtalene.");
        IMolecule mol = null;
        String filename = "data/cml/naphtalene.cml";
        mol = readCMLMolecule(filename);

        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac = acSet.getAtomContainer(0);
        Assert.assertEquals(10, ac.getAtomCount());
        Assert.assertEquals(11, ac.getBondCount());
        
        for (int i = 0; i < ac.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac.getAtom(i)));
        }
        
        for (int i = 0; i < ac.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac.getBond(i)));
        }
	}
    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    @Test public void testDetectToluene() throws Exception
	{
        logger.info("Entering testDetectToluene.");
        IMolecule mol = null;
        String filename = "data/cml/toluene.cml";
        mol = readCMLMolecule(filename);

        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac = acSet.getAtomContainer(0);
        Assert.assertEquals(6, ac.getAtomCount());
        Assert.assertEquals(6, ac.getBondCount());
        
        for (int i = 0; i < ac.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac.getAtom(i)));
        }
        
        for (int i = 0; i < ac.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac.getBond(i)));
        }
	}
    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    @Test public void testNonConnectedPiSystems() throws Exception
	{
        logger.info("Entering testNonConnectedPiSystems.");
        IMolecule mol = null;
        String filename = "data/mdl/nonConnectedPiSystems.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
        IChemFile chemFile = (IChemFile)reader.read((ChemObject)new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(2, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac1.getAtomCount());
        Assert.assertEquals(3, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }
        
        IAtomContainer ac2 = acSet.getAtomContainer(1);
        Assert.assertEquals(4, ac2.getAtomCount());
        Assert.assertEquals(3, ac2.getBondCount());
        
        for (int i = 0; i < ac2.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac2.getAtom(i)));
        }
        
        for (int i = 0; i < ac2.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac2.getBond(i)));
        }
	}
    /**
	 *  A unit test for JUnit
	 *  
	 *@return    Description of the Return Value
	 */
    @Test public void testPiSystemWithCarbokation() throws Exception
	{
        logger.info("Entering testPiSystemWithCarbokation.");
        IMolecule mol = null;
        String filename = "data/mdl/piSystemWithCarbokation.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
        IChemFile chemFile = (IChemFile)reader.read((ChemObject)new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);    
        
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(2, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac1.getAtomCount());
        Assert.assertEquals(3, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++)
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        
        for (int i = 0; i < ac1.getBondCount(); i++)
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        
        IAtomContainer ac2 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac2.getAtomCount());
        Assert.assertEquals(3, ac2.getBondCount());
        
        for (int i = 0; i < ac2.getAtomCount(); i++)
            Assert.assertTrue(mol.contains(ac2.getAtom(i)));
        
        for (int i = 0; i < ac2.getBondCount(); i++)
            Assert.assertTrue(mol.contains(ac2.getBond(i)));
        
	}
    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    @Test public void testPiSystemWithCumulativeDB() throws Exception
	{
        logger.info("Entering testPiSystemWithCumulativeDB.");
        IMolecule mol = null;
        String filename = "data/mdl/piSystemCumulative.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
        IChemFile chemFile = (IChemFile)reader.read((ChemObject)new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);    

        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(2, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac1.getAtomCount());
        Assert.assertEquals(3, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }
        
        IAtomContainer ac2 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac2.getAtomCount());
        Assert.assertEquals(3, ac2.getBondCount());
        
        for (int i = 0; i < ac2.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac2.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }
        
	}

    /**
	 *  A unit test for JUnit
	 *
	 *  @cdk.inchi InChI=1/C2H4O2/c1-2(3)4/h1H3,(H,3,4)/f/h3
	 *  
	 *@return    Description of the Return Value
	 */
    @Test public void testAceticAcid() throws Exception
	{
    	IMolecule mol = null;
    	mol = (new SmilesParser(builder)).parseSmiles("CC(=O)O");
    	addImplicitHydrogens(mol);
    	lpcheck.saturate(mol);
    	AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
    	
    	IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(3, ac1.getAtomCount());
        Assert.assertEquals(2, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }
        
	}
    /**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
    @Test public void testNN_dimethylaniline_cation() throws Exception
	{
    	IMolecule mol = null;
    	String filename = "data/mdl/NN_dimethylaniline.mol";
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	MDLReader reader = new MDLReader(ins);
    	IChemFile chemFile = (IChemFile)reader.read((ChemObject)new ChemFile());
    	mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);  
    	
    	addImplicitHydrogens(mol);
    	lpcheck.saturate(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
    	IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(6, ac1.getAtomCount());
        Assert.assertEquals(5, ac1.getBondCount());
        
	}

    /**
	 *  A unit test for JUnit
	 *  
	 *@return    Description of the Return Value
	 */
    @Test public void test1_fluorobutadienene() throws Exception
	{
    	IMolecule mol = (new SmilesParser(builder)).parseSmiles("FC=CC=C");
    	addImplicitHydrogens(mol);
    	lpcheck.saturate(mol);
    	AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
    	
    	IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(5, ac1.getAtomCount());
        Assert.assertEquals(4, ac1.getBondCount());
        
	}
    /**
	 *  A unit test for JUnit
	 *
	 *  @cdk.inchi  InChI=1/C2F2/c3-1-2-4
	 *  
	 *@return    Description of the Return Value
	 */
    @Test public void testEthyne_difluoro() throws Exception
	{
    	IMolecule mol = null;
    	mol = (new SmilesParser(builder)).parseSmiles("FC#CF");
    	addImplicitHydrogens(mol);
    	lpcheck.saturate(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
    	
    	IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac1.getAtomCount());
        Assert.assertEquals(3, ac1.getBondCount());
        
	}
    /**
	 *  A unit test for JUnit
	 *
	 *  @cdk.inchi  InChI=1/C7H19N3/c1-8(2)7(9(3)4)10(5)6/h7H,1-6H3
	 *  
	 *@return    Description of the Return Value
	 */
    @Test public void test3Aminomethane_cation() throws Exception
	{
    	IMolecule mol = (new SmilesParser(builder)).parseSmiles("CN(C)C(N(C)C)N(C)C");
    	mol.getAtom(3).setFormalCharge(+1);
    	addImplicitHydrogens(mol);
    	lpcheck.saturate(mol);
    	AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
    	
    	IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac1.getAtomCount());
        Assert.assertEquals(3, ac1.getBondCount());
        
        
        
	}
    /**
	 *  A unit test for JUnit
	 *
	 *  @cdk.inchi  
	 *  
	 *@return    Description of the Return Value
	 */
    private IMolecule readCMLMolecule(String filename) throws Exception {
    	IMolecule mol = null;
    	logger.debug("Filename: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	CMLReader reader = new CMLReader(ins);

    	IChemFile file = (IChemFile)reader.read(new ChemFile());
    	Assert.assertNotNull(file);
    	Assert.assertEquals(1, file.getChemSequenceCount());
    	IChemSequence sequence = file.getChemSequence(0);
    	Assert.assertNotNull(sequence);
    	Assert.assertEquals(1, sequence.getChemModelCount());
    	IChemModel chemModel = sequence.getChemModel(0);
    	Assert.assertNotNull(chemModel);
    	IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
    	Assert.assertNotNull(moleculeSet);
    	Assert.assertEquals(1, moleculeSet.getMoleculeCount());
    	mol = moleculeSet.getMolecule(0);
    	Assert.assertNotNull(mol);
        
        return mol;
    	
    }
    
    /**
	 *  A unit test for JUnit: Cyanoallene
	 *
	 *  @cdk.inchi  InChI=1/C4H3N/c1-2-3-4-5/h3H,1H2
	 *  
	 *@return    Description of the Return Value
	 */
    @Test public void testCyanoallene() throws Exception
	{
    	IMolecule mol = null;
    	mol = (new SmilesParser(builder)).parseSmiles("C=C=CC#N");
    	addImplicitHydrogens(mol);
    	lpcheck.saturate(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
    	IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac1.getAtomCount());
        Assert.assertEquals(3, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }
        
	}
    /**
	 *  A unit test for JUnit with [H]C([H])=C([H])[C+]([H])[H]
	 */
	@Test public void testChargeWithProtonExplicit() throws ClassNotFoundException, CDKException, java.lang.Exception {
		SmilesParser sp = new SmilesParser(builder);
		IMolecule mol = sp.parseSmiles("[H]C([H])=C([H])[C+]([H])[H]");
		lpcheck.saturate(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
    	IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(3, ac1.getAtomCount());
        Assert.assertEquals(2, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }
	}
	/**
	 *  A unit test for JUnit with [H]C([H])=C([H])[C+]([H])[H]
	 */
	@Test public void testChargeWithProtonImplicit() throws ClassNotFoundException, CDKException, java.lang.Exception {
		SmilesParser sp = new SmilesParser(builder);
		IMolecule mol = sp.parseSmiles("C=C[C+]");
		lpcheck.saturate(mol);
    	CDKHueckelAromaticityDetector.detectAromaticity(mol);
        
    	IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(3, ac1.getAtomCount());
        Assert.assertEquals(2, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }
	}
}
