/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.ChemModelManipulator;

/**
 *  A set of test cases for the StructureDiagramGenerator
 *
 * @cdkPackage test
 *
 *@author     steinbeck
 *@created    August 29, 2003
 */
public class StructureDiagramGeneratorTest extends TestCase
{

	MoleculeListViewer moleculeListViewer = null;
	boolean standAlone = false;


	/**
	 *  Constructor for the StructureDiagramGeneratorTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public StructureDiagramGeneratorTest(String name)
	{
		super(name);
	}


	/**
	 *  The JUnit setup method
	 */
	public void setUp() { }


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(StructureDiagramGeneratorTest.class);
	}


	/**
	 *  Description of the Method
	 */
	public void runVisualTests()
	{
		moleculeListViewer = new MoleculeListViewer();
		//MoleculeViewer2D.display(MoleculeFactory.loadMolecule("data/mdl/reserpine.mol"), true);
		showIt(MoleculeFactory.loadMolecule("data/mdl/reserpine.mol"), "Reserpine");
		showIt(MoleculeFactory.loadMolecule("data/mdl/four-ring-5x10.mol"), "5x10 condensed four membered rings");
		showIt(MoleculeFactory.loadMolecule("data/mdl/six-ring-4x4.mol"), "4x4 condensed six membered rings");
		showIt(MoleculeFactory.loadMolecule("data/mdl/polycarpol.mol"), "Polycarpol");
		showIt(MoleculeFactory.makeAlphaPinene(), "alpha-Pinene");
		showIt(MoleculeFactory.makeBiphenyl(), "Biphenyl");
		showIt(MoleculeFactory.make4x3CondensedRings(), "4x3CondensedRings");
		showIt(MoleculeFactory.makePhenylEthylBenzene(), "PhenylEthylBenzene");
		showIt(MoleculeFactory.makeSpiroRings(), "Spiro");
		showIt(MoleculeFactory.makeMethylDecaline(), "Methyldecaline");
		showIt(MoleculeFactory.makeBranchedAliphatic(), "Branched aliphatic");
		showIt(MoleculeFactory.makeDiamantane(), "Diamantane - Was A Problem! - Solved :-)");
		showIt(MoleculeFactory.makeEthylCyclohexane(), "Ethylcyclohexane");
		showIt(MoleculeFactory.makeBicycloRings(), "Bicyclo-[2.2.2]-octane");		
		showIt(makeBug736137(), "Bug 736137");
	}


	/**
	 *  Description of the Method
	 *
	 *@param  molecule  Description of the Parameter
	 *@param  name      Description of the Parameter
	 *@return           Description of the Return Value
	 */
	private boolean showIt(Molecule molecule, String name)
	{
		MoleculeViewer2D mv = new MoleculeViewer2D();
		try
		{
			mv.setAtomContainer(generateCoordinates(molecule));
			moleculeListViewer.addStructure(mv, name);
		} catch (Exception exc)
		{
			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  m              Description of the Parameter
	 *@return                Description of the Return Value
	 *@exception  Exception  Description of the Exception
	 */
	public AtomContainer generateCoordinates(Molecule m) throws Exception
	{
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		sdg.setMolecule(m);
		sdg.generateCoordinates(new Vector2d(0, 1));
		return sdg.getMolecule();
	}


	/**
	 *  The main program for the StructureDiagramGeneratorTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		try
		{
			StructureDiagramGeneratorTest sdg = new StructureDiagramGeneratorTest("StructureDiagramGeneratorTest");
			sdg.runVisualTests();
			//sdg.bug736137();
			//sdg.testSpiroRings();
			//sdg.visualBugPMR();
			//sdg.testBranchedAliphatic();
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@exception  java.lang.Exception  Description of the Exception
	 */
	public Molecule makeBug736137()
	{
		//String filename = "data/mdl/bug736137.mol";
		Molecule molecule = null;
		try
		{
			String filename = "data/r.cml";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			//MDLReader reader = new MDLReader(new InputStreamReader(ins));
			 CMLReader reader = new CMLReader(new InputStreamReader(ins));
		 ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		      ChemSequence[] chemSequence = chemFile.getChemSequences();
		      ChemModel[] chemModels = chemSequence[0].getChemModels();
		      AtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
	
			molecule = new Molecule(atomContainer);
		}
		catch(Exception exc)
		{
			fail(exc.toString());	
		}
		return molecule;

	}

	public void visualBugPMR()
	{
                String filename = "data/SL0016a.cml";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		try {
		    CMLReader reader = new CMLReader(new InputStreamReader(ins));
		    ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		    ChemSequence seq = chemFile.getChemSequence(0);
		    ChemModel model = seq.getChemModel(0);
		    Molecule mol = model.getSetOfMolecules().getMolecule(0);
		    MoleculeViewer2D.display(mol, true);
		    //System.out.println(new SmilesGenerator().createSMILES(mol));
		} catch (Exception e) 
		{
		    e.printStackTrace();
		    fail(e.toString());
		}
	}
	
	
	
	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testAlphaPinene() throws Exception
	{
		Molecule m = MoleculeFactory.makeAlphaPinene();
		AtomContainer ac = generateCoordinates(m);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBiphenyl() throws Exception
	{
		Molecule m = MoleculeFactory.makeBiphenyl();
		AtomContainer ac = generateCoordinates(m);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void test4x3CondensedRings() throws Exception
	{
		Molecule m = MoleculeFactory.make4x3CondensedRings();
		AtomContainer ac = generateCoordinates(m);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testPhenylEthylBenzene() throws Exception
	{
		Molecule m = MoleculeFactory.makePhenylEthylBenzene();
		AtomContainer ac = generateCoordinates(m);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testSpiroRings() throws Exception
	{
		Molecule m = MoleculeFactory.makeSpiroRings();
		AtomContainer ac = generateCoordinates(m);
		if (standAlone)MoleculeViewer2D.display(new Molecule(ac), false);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testMethylDecaline() throws Exception
	{
		Molecule m = MoleculeFactory.makeMethylDecaline();
		AtomContainer ac = generateCoordinates(m);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBranchedAliphatic() throws Exception
	{
		Molecule m = MoleculeFactory.makeBranchedAliphatic();
		AtomContainer ac = generateCoordinates(m);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testDiamantane() throws Exception
	{
		Molecule m = MoleculeFactory.makeDiamantane();
		AtomContainer ac = generateCoordinates(m);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testEthylCyclohexane() throws Exception
	{
		Molecule m = MoleculeFactory.makeEthylCyclohexane();
		AtomContainer ac = generateCoordinates(m);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBicycloRings() throws Exception
	{
		Molecule m = MoleculeFactory.makeBicycloRings();
		AtomContainer ac = generateCoordinates(m);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBenzene() throws Exception
	{
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("c1ccccc1");
		AtomContainer ac = generateCoordinates(mol);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBug780545() throws Exception
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C"));
		AtomContainer ac = generateCoordinates(mol);
	}
	
	
	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBug884993() throws Exception
	{
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("[N+](=O)([O-])C1=C(O)C(=CC(=C1)[N+](=O)[O-])[N+](=O)[O-].C23N(CCCC2)CCCC3");
		try{
			AtomContainer ac = generateCoordinates(mol);
		}
		catch(Exception exc)
		{
			if (!(exc.toString().indexOf("Molecule not connected")>= 0)) fail();
		}
	}
}

