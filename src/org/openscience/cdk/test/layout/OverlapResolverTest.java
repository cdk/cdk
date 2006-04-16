/*
 *  $RCSfile$
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
package org.openscience.cdk.test.layout;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.layout.OverlapResolver;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 *  Description of the Class
 *
 * @cdk.module test-extra
 *
 *@author     steinbeck
 *@cdk.created    September 4, 2003
 * @cdk.require java1.4+
 */
public class OverlapResolverTest extends CDKTestCase
{

	/**
	 *  Description of the Field
	 */
	public boolean standAlone = false;
	private LoggingTool logger = null;
	StructureDiagramGenerator sdg = null;

	/**
	 *  Constructor for the OverlapResolverTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public OverlapResolverTest(String name)
	{
		super(name);
	}


	/**
	 *  The JUnit setup method
	 */
	public void setUp() throws Exception
	{
        super.setUp();
		logger = new LoggingTool(this);
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(OverlapResolverTest.class);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testResolveOverlap1() throws Exception
	{
		logger.debug("Test case with atom clash");
		try
		{
			String filename = "data/cml/overlaptest.cml";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			CMLReader reader = new CMLReader(ins);
			IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
			org.openscience.cdk.interfaces.IChemSequence[] chemSequence = chemFile.getChemSequences();
			org.openscience.cdk.interfaces.IChemModel[] chemModels = chemSequence[0].getChemModels();
			IAtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
			
			OverlapResolver or = new OverlapResolver();
			double score = new OverlapResolver().getAtomOverlapScore(atomContainer, new Vector());
			logger.debug("Overlap Score before treatment: " +  score);
			assertTrue(score > 0);
			or.resolveOverlap(atomContainer, null);
			//MoleculeViewer2D.display(new Molecule(atomContainer), false);
			score = new OverlapResolver().getAtomOverlapScore(atomContainer, new Vector());
			logger.debug("Overlap Score after treatment: " +  score);
			assertTrue(score == 0);
		} catch (Exception exc)
		{
			fail(exc.toString());
		}
		logger.debug("End of test case with atom clash");

	}

	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testResolveOverlap2() throws Exception
	{
		logger.debug("Test case with neither bond nor atom overlap");
		try
		{
			String filename = "data/cml/overlaptest2.cml";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			CMLReader reader = new CMLReader(ins);
			IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
			org.openscience.cdk.interfaces.IChemSequence[] chemSequence = chemFile.getChemSequences();
			org.openscience.cdk.interfaces.IChemModel[] chemModels = chemSequence[0].getChemModels();
			IAtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
			//MoleculeViewer2D.display(new Molecule(atomContainer), false);
			double score = new OverlapResolver().getOverlapScore(atomContainer, new Vector(), new Vector());
			assertTrue(score == 0.0);
		} catch (Exception exc)
		{
			fail(exc.toString());
		}
		logger.debug("End of test case with neither bond nor atom overlap");

	}

	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testResolveOverlap3() throws Exception
	{
		logger.debug("Test case with bond overlap");
		try
		{
			String filename = "data/cml/overlaptest3.cml";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			CMLReader reader = new CMLReader(ins);
			IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
			org.openscience.cdk.interfaces.IChemSequence[] chemSequence = chemFile.getChemSequences();
			org.openscience.cdk.interfaces.IChemModel[] chemModels = chemSequence[0].getChemModels();
			IAtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
			//MoleculeViewer2D.display(new Molecule(atomContainer), false);
			double score = new OverlapResolver().getBondOverlapScore(atomContainer, new Vector());
			assertTrue(score > 0);
		} catch (Exception exc)
		{
			fail(exc.toString());
		}
		logger.debug("End of test case with bond overlap");

	}

	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testResolveOverlap4() throws Exception
	{
		double overlapScore = 0;
		logger.debug("Test case with atom clash");
		try
		{
			String filename = "data/cml/overlaptest.cml";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			CMLReader reader = new CMLReader(ins);
			IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
			org.openscience.cdk.interfaces.IChemSequence[] chemSequence = chemFile.getChemSequences();
			org.openscience.cdk.interfaces.IChemModel[] chemModels = chemSequence[0].getChemModels();
			IAtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
			//MoleculeViewer2D.display(new Molecule(atomContainer), false);
			OverlapResolver or = new OverlapResolver(); 
			overlapScore = or.resolveOverlap(atomContainer, null);
			//MoleculeViewer2D.display(new Molecule(atomContainer), false);
			assertTrue(overlapScore == 0);
		} catch (Exception exc)
		{
			exc.printStackTrace();
			fail(exc.toString());
		}
		logger.debug("End of test case with atom clash");

	}

	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testResolveOverlap5() throws Exception
	{
		double overlapScore = 0;
		logger.debug("Test case with atom clash");
		try
		{
			IAtomContainer atomContainer = new SmilesParser().parseSmiles("OC4C(N2C1=C(C(=NC(=N1)SC)SC)C3=C2N=CN=C3N)OC(C4O)CO");
			StructureDiagramGenerator sdg = new StructureDiagramGenerator();
			sdg.setMolecule(new Molecule(atomContainer));
			sdg.generateCoordinates();
			atomContainer = sdg.getMolecule();
			OverlapResolver or = new OverlapResolver(); 
			overlapScore = or.resolveOverlap(atomContainer, null);
			//MoleculeViewer2D.display(new Molecule(atomContainer), true);
			assertTrue(overlapScore == 0);
		} catch (Exception exc)
		{
			exc.printStackTrace();
			fail(exc.toString());
		}
		logger.debug("End of test case with atom clash");

	}

	
	
	/**
	 *  The main program for the OverlapResolverTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		try
		{
			OverlapResolverTest ort = new OverlapResolverTest("OverlapResolverTest");
			ort.setUp();
			ort.standAlone = true;
			//ort.testResolveOverlap1();
			//ort.testResolveOverlap2();
			//ort.testResolveOverlap3();
			//ort.testResolveOverlap4();
			ort.testResolveOverlap5();
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
	}
}

