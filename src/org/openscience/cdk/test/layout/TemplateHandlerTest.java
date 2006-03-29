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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.layout.TemplateHandler;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  Description of the Class
 *
 * @cdk.module test-extra
 *
 *@author     steinbeck
 *@cdk.created    September 4, 2003
 * @cdk.require java1.4+
 */
public class TemplateHandlerTest extends CDKTestCase
{

	/**
	 *  Description of the Field
	 */
	public boolean standAlone = false;
	private LoggingTool logger = null;


	/**
	 *  Constructor for the TemplateHandlerTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public TemplateHandlerTest(String name)
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
		return new TestSuite(TemplateHandlerTest.class);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testInit() throws Exception
	{
		TemplateHandler th = new TemplateHandler();
		
		assertTrue(th.getTemplateCount() > 0);
	}

	public void testDetection() throws Exception
	{
		TemplateHandler th = new TemplateHandler();
		String smiles = "CC12C3(C6CC6)C4(C)C1C5(C(CC)C)C(C(CC)C)2C(C)3C45CC(C)C";
		Molecule mol = new SmilesParser().parseSmiles(smiles);
		assertTrue(th.mapTemplates(mol));
	}
	
	public void testAddMolecule() throws Exception
	{
		logger.debug("***TestAddMolecule***");
		boolean itIsInThere = false;
		TemplateHandler th = new TemplateHandler();
		Molecule mol = MoleculeFactory.makeAlphaPinene();
		String smiles = "C1=C(C)C2CC(C1)C2(C)(C)";
		Molecule smilesMol = new SmilesParser().parseSmiles(smiles);
		itIsInThere = th.mapTemplates(smilesMol);
		logger.debug("Alpha-Pinene found by templateMapper: " + itIsInThere);
		assertFalse(itIsInThere);
		th.addMolecule(mol);
		logger.debug("now adding template for alpha-Pinen and trying again.");
		itIsInThere = th.mapTemplates(smilesMol);
		logger.debug("Alpha-Pinene found by templateMapper: " + itIsInThere);
		assertTrue(itIsInThere);
	}

	public void testRemoveMolecule() throws Exception
	{
		logger.debug("***TestRemoveMolecule***");
		boolean itIsInThere = false;
		TemplateHandler th = new TemplateHandler();
		Molecule mol = MoleculeFactory.makeAlphaPinene();
		String smiles = "C1=C(C)C2CC(C1)C2(C)(C)";
		Molecule smilesMol = new SmilesParser().parseSmiles(smiles);
		itIsInThere = th.mapTemplates(smilesMol);
		logger.debug("Alpha-Pinene found by templateMapper: " + itIsInThere);
		assertFalse(itIsInThere);
		th.addMolecule(mol);
		logger.debug("now adding template for alpha-Pinen and trying again.");
		itIsInThere = th.mapTemplates(smilesMol);
		logger.debug("Alpha-Pinene found by templateMapper: " + itIsInThere);
		assertTrue(itIsInThere);
		logger.debug("now removing template for alpha-Pinen again and trying again.");
		th.removeMolecule(mol);
		itIsInThere = th.mapTemplates(smilesMol);
		logger.debug("Alpha-Pinene found by templateMapper: " + itIsInThere);
		assertFalse(itIsInThere);
		
	}

	
	public void visualLayout() throws Exception
	{
		String smiles = "CC12C3(C6CC6)C4(C)C1C5(C(CC)C)C(C(CC)C)2C(C)3C45CC(C)C";
		Molecule mol = new SmilesParser().parseSmiles(smiles);
		MoleculeViewer2D.display(mol, true);
	}


	/**
	 *  The main program for the TemplateHandlerTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		try
		{
			TemplateHandlerTest tht = new TemplateHandlerTest("TemplateHandlerTest");
			tht.setUp();
			tht.standAlone = true;
			tht.testInit();
			tht.testDetection();
			tht.visualLayout();
			tht.testAddMolecule();
			tht.testRemoveMolecule();
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
	}
}

