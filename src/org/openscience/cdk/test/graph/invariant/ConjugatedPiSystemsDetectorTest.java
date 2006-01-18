/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfAtomContainers;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetector;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Checks the funcitonality of the ConjugatedPiSystemsCalculator
 *
 * @cdk.module test
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

	public void testDetectButadiene()
	{
        logger.info("Entering testDetectButadiene.");
        org.openscience.cdk.interfaces.Molecule mol = null;
		try
		{
			String filename = "data/butadiene.cml";
			mol = readCMLMolecule(filename);
            
			HueckelAromaticityDetector.detectAromaticity(mol);
		} catch (Exception exc)
		{
			logger.debug(exc);
            fail("Error.");
		}
        
        SetOfAtomContainers acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        org.openscience.cdk.interfaces.IAtomContainer ac = acSet.getAtomContainer(0);
        assertEquals(4, ac.getAtomCount());
        assertEquals(3, ac.getBondCount());
        
        for (int i = 0; i < ac.getAtomCount(); i++) {
            assertTrue(mol.contains(ac.getAtomAt(i)));
        }
        
        for (int i = 0; i < ac.getBondCount(); i++) {
            assertTrue(mol.contains(ac.getBondAt(i)));
        }
	}
    
    public void testDetectNaphtalene()
	{
        logger.info("Entering testDetectNaphtalene.");
        org.openscience.cdk.interfaces.Molecule mol = null;
		try
		{
			String filename = "data/naphtalene.cml";
			mol = readCMLMolecule(filename);
            
			HueckelAromaticityDetector.detectAromaticity(mol);
		} catch (Exception exc)
		{
			logger.debug(exc);
            fail("Error.");
		}
        
        SetOfAtomContainers acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        org.openscience.cdk.interfaces.IAtomContainer ac = acSet.getAtomContainer(0);
        assertEquals(10, ac.getAtomCount());
        assertEquals(11, ac.getBondCount());
        
        for (int i = 0; i < ac.getAtomCount(); i++) {
            assertTrue(mol.contains(ac.getAtomAt(i)));
        }
        
        for (int i = 0; i < ac.getBondCount(); i++) {
            assertTrue(mol.contains(ac.getBondAt(i)));
        }
	}
    
    public void testDetectToluene()
	{
        logger.info("Entering testDetectToluene.");
        org.openscience.cdk.interfaces.Molecule mol = null;
		try
		{
			String filename = "data/toluene.cml";
			mol = readCMLMolecule(filename);
            
			HueckelAromaticityDetector.detectAromaticity(mol);
		} catch (Exception exc)
		{
			logger.debug(exc);
            fail("Error.");
		}
        
        SetOfAtomContainers acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        org.openscience.cdk.interfaces.IAtomContainer ac = acSet.getAtomContainer(0);
        assertEquals(6, ac.getAtomCount());
        assertEquals(6, ac.getBondCount());
        
        for (int i = 0; i < ac.getAtomCount(); i++) {
            assertTrue(mol.contains(ac.getAtomAt(i)));
        }
        
        for (int i = 0; i < ac.getBondCount(); i++) {
            assertTrue(mol.contains(ac.getBondAt(i)));
        }
	}
    
    public void testNonConnectedPiSystems()
	{
        logger.info("Entering testNonConnectedPiSystems.");
        Molecule mol = new Molecule();
		try
		{
			String filename = "data/mdl/nonConnectedPiSystems.mol";
			MDLReader reader = new MDLReader(new BufferedReader(new FileReader(filename)));
            reader.read(mol);
            
			HueckelAromaticityDetector.detectAromaticity(mol);
		} catch (Exception exc)
		{
			logger.debug(exc);
            fail("Error.");
		}
        
        SetOfAtomContainers acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(2, acSet.getAtomContainerCount());
        org.openscience.cdk.interfaces.IAtomContainer ac1 = acSet.getAtomContainer(0);
        assertEquals(4, ac1.getAtomCount());
        assertEquals(3, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            assertTrue(mol.contains(ac1.getAtomAt(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            assertTrue(mol.contains(ac1.getBondAt(i)));
        }
        
        org.openscience.cdk.interfaces.IAtomContainer ac2 = acSet.getAtomContainer(1);
        assertEquals(4, ac2.getAtomCount());
        assertEquals(3, ac2.getBondCount());
        
        for (int i = 0; i < ac2.getAtomCount(); i++) {
            assertTrue(mol.contains(ac2.getAtomAt(i)));
        }
        
        for (int i = 0; i < ac2.getBondCount(); i++) {
            assertTrue(mol.contains(ac2.getBondAt(i)));
        }
	}
    
    public void testPiSystemWithCarbokation()
	{
        logger.info("Entering testPiSystemWithCarbokation.");
        Molecule mol = new Molecule();
		try
		{
			String filename = "data/mdl/piSystemWithCarbokation.mol";
			MDLReader reader = new MDLReader(new BufferedReader(new FileReader(filename)));
            reader.read(mol);
            
			HueckelAromaticityDetector.detectAromaticity(mol);
		} catch (Exception exc)
		{
			logger.debug(exc);
            fail("Error.");
		}
        
        SetOfAtomContainers acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(1, acSet.getAtomContainerCount());
        org.openscience.cdk.interfaces.IAtomContainer ac1 = acSet.getAtomContainer(0);
        assertEquals(9, ac1.getAtomCount());
        assertEquals(8, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            assertTrue(mol.contains(ac1.getAtomAt(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            assertTrue(mol.contains(ac1.getBondAt(i)));
        }
        
	}
    
    public void testPiSystemWithCumulativeDB()
	{
        logger.info("Entering testPiSystemWithCumulativeDB.");
        Molecule mol = new Molecule();
		try
		{
			String filename = "data/mdl/piSystemCumulative.mol";
			MDLReader reader = new MDLReader(new BufferedReader(new FileReader(filename)));
            reader.read(mol);
            
			HueckelAromaticityDetector.detectAromaticity(mol);
		} catch (Exception exc)
		{
			logger.debug(exc);
            fail("Error.");
		}
        
        SetOfAtomContainers acSet = ConjugatedPiSystemsDetector.detect(mol);
        
        assertEquals(2, acSet.getAtomContainerCount());
        org.openscience.cdk.interfaces.IAtomContainer ac1 = acSet.getAtomContainer(0);
        assertEquals(4, ac1.getAtomCount());
        assertEquals(3, ac1.getBondCount());
        
        for (int i = 0; i < ac1.getAtomCount(); i++) {
            assertTrue(mol.contains(ac1.getAtomAt(i)));
        }
        
        for (int i = 0; i < ac1.getBondCount(); i++) {
            assertTrue(mol.contains(ac1.getBondAt(i)));
        }
        
        org.openscience.cdk.interfaces.IAtomContainer ac2 = acSet.getAtomContainer(0);
        assertEquals(4, ac2.getAtomCount());
        assertEquals(3, ac2.getBondCount());
        
        for (int i = 0; i < ac2.getAtomCount(); i++) {
            assertTrue(mol.contains(ac1.getAtomAt(i)));
        }
        
        for (int i = 0; i < ac2.getBondCount(); i++) {
            assertTrue(mol.contains(ac1.getBondAt(i)));
        }
        
	}
    
    private org.openscience.cdk.interfaces.Molecule readCMLMolecule(String filename) {
    	org.openscience.cdk.interfaces.Molecule mol = null;
        try {
            logger.debug("Filename: " + filename);
            InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
            CMLReader reader = new CMLReader(new InputStreamReader(ins));
            
            ChemFile file = (ChemFile)reader.read(new ChemFile());
            assertNotNull(file);
            assertEquals(1, file.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence sequence = file.getChemSequence(0);
            assertNotNull(sequence);
            assertEquals(1, sequence.getChemModelCount());
            org.openscience.cdk.interfaces.IChemModel chemModel = sequence.getChemModel(0);
            assertNotNull(chemModel);
            org.openscience.cdk.interfaces.SetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
            assertNotNull(moleculeSet);
            assertEquals(1, moleculeSet.getMoleculeCount());
            mol = moleculeSet.getMolecule(0);
            assertNotNull(mol);
        } catch (Exception exception) {
            String message = "Failed when reading CML";
            logger.error(message);
            logger.debug(exception);
            fail(message);
        }
        
        return mol;
    }
}
