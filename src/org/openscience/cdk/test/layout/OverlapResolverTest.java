/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.layout;

import org.openscience.cdk.controller.*;
import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.applications.swing.*;
import org.openscience.cdk.tools.*;
import java.util.*;
import java.io.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *  Description of the Class
 *
 * @cdkPackage test
 *
 *@author     steinbeck
 *@created    September 4, 2003
 */
public class OverlapResolverTest extends TestCase
{

	/**
	 *  Description of the Field
	 */
	public boolean standAlone = false;
	private LoggingTool logger = null;


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
	public void setUp()
	{
		logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(TemplateHandlerTest.class);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testResolveOverlap1() throws Exception
	{
		Molecule molecule = null;
		logger.debug("Test case with atom clash");
		try
		{
			String filename = "data/overlaptest.cml";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			CMLReader reader = new CMLReader(new InputStreamReader(ins));
			ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
			ChemSequence[] chemSequence = chemFile.getChemSequences();
			ChemModel[] chemModels = chemSequence[0].getChemModels();
			AtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
			//MoleculeViewer2D.display(new Molecule(atomContainer), false);
			//OverlapResolver.resolveOverlap(atomContainer, null);
			double score = new OverlapResolver().getAtomOverlapScore(atomContainer, new Vector());
			assertTrue(score > 0);
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
		Molecule molecule = null;
		logger.debug("Test case with neither bond nor atom overlap");
		try
		{
			String filename = "data/overlaptest2.cml";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			CMLReader reader = new CMLReader(new InputStreamReader(ins));
			ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
			ChemSequence[] chemSequence = chemFile.getChemSequences();
			ChemModel[] chemModels = chemSequence[0].getChemModels();
			AtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
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
		Molecule molecule = null;
		logger.debug("Test case with bond overlap");
		try
		{
			String filename = "data/overlaptest3.cml";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			CMLReader reader = new CMLReader(new InputStreamReader(ins));
			ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
			ChemSequence[] chemSequence = chemFile.getChemSequences();
			ChemModel[] chemModels = chemSequence[0].getChemModels();
			AtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
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
		Molecule molecule = null;
		double overlapScore = 0;
		logger.debug("Test case with atom clash");
		try
		{
			String filename = "data/overlaptest.cml";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			CMLReader reader = new CMLReader(new InputStreamReader(ins));
			ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
			ChemSequence[] chemSequence = chemFile.getChemSequences();
			ChemModel[] chemModels = chemSequence[0].getChemModels();
			AtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
			//MoleculeViewer2D.display(new Molecule(atomContainer), false);
			//OverlapResolver.resolveOverlap(atomContainer, null);
			OverlapResolver or = new OverlapResolver(); 
			overlapScore = or.resolveOverlap(atomContainer, null);
			MoleculeViewer2D.display(new Molecule(atomContainer), false);
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
			ort.testResolveOverlap4();
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
	}
}

