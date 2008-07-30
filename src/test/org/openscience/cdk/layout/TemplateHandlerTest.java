/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2003-2007  Christoph Steinbeck
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
package org.openscience.cdk.layout;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module  test-sdg
 * @author      steinbeck
 * @cdk.created September 4, 2003
 * @cdk.require java1.4+
 */
public class TemplateHandlerTest extends CDKTestCase
{

	/**
	 *  Description of the Field
	 */
	public boolean standAlone = false;
	private LoggingTool logger = null;

	private SmilesParser sp = null;
	private StructureDiagramGenerator sdg = null;

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
		sdg = new StructureDiagramGenerator();
		sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
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
		TemplateHandler th = new TemplateHandler(DefaultChemObjectBuilder.getInstance());
		
		assertTrue(th.getTemplateCount() > 0);
	}

	public void testDetection() throws Exception
	{
		TemplateHandler th = new TemplateHandler(DefaultChemObjectBuilder.getInstance());
		String smiles = "CC12C3(C6CC6)C4(C)C1C5(C(CC)C)C(C(CC)C)2C(C)3C45CC(C)C";
		IMolecule mol = sp.parseSmiles(smiles);
		assertTrue(th.mapTemplates(mol));
	}
	
	public void testAddMolecule() throws Exception
	{
		logger.debug("***TestAddMolecule***");
		boolean itIsInThere = false;
		TemplateHandler th = new TemplateHandler(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = MoleculeFactory.makeAlphaPinene();
		sdg.setMolecule(mol); sdg.generateCoordinates(); mol = sdg.getMolecule();

		String smiles = "C1=C(C)C2CC(C1)C2(C)(C)";
		IMolecule smilesMol = sp.parseSmiles(smiles);
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
		TemplateHandler th = new TemplateHandler(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = MoleculeFactory.makeAlphaPinene();
		sdg.setMolecule(mol); sdg.generateCoordinates(); mol = sdg.getMolecule();
		
		String smiles = "C1=C(C)C2CC(C1)C2(C)(C)";
		IMolecule smilesMol = sp.parseSmiles(smiles);
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

	
	/**
	 * Loads a molecule with two adamantanes and one cubane
	 * substructure and tests whether all are found.
	 */
	public void getMappedSubstructures_IAtomContainer() throws Exception {
		// Set up molecule reader
		String filename = "data/mdl/diadamantane-cubane.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		ISimpleChemObjectReader molReader = new MDLReader(ins, Mode.STRICT);
		
		// Read molecule
		IMolecule molecule = (IMolecule) molReader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
		
		// Map templates
		TemplateHandler th = new TemplateHandler(DefaultChemObjectBuilder.getInstance());
		IAtomContainerSet mappedStructures = th.getMappedSubstructures(molecule);
		
		// Do the assertion
		assertEquals("3 mapped templates", 3, mappedStructures.getAtomContainerCount());
	}

}

