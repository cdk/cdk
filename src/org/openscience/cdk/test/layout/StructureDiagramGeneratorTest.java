/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 *  A set of test cases for the StructureDiagramGenerator
 *
 * @cdk.module test-extra
 *
 *@author     steinbeck
 *@cdk.created    August 29, 2003
 */
public class StructureDiagramGeneratorTest extends CDKTestCase
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
		/*showIt(MoleculeFactory.loadMolecule("data/mdl/reserpine.mol"), "Reserpine");
		showIt(MoleculeFactory.loadMolecule("data/mdl/four-ring-5x10.mol"), "5x10 condensed four membered rings");
		showIt(MoleculeFactory.loadMolecule("data/mdl/six-ring-4x4.mol"), "4x4 condensed six membered rings");
		showIt(MoleculeFactory.loadMolecule("data/mdl/polycarpol.mol")
		, "Polycarpol");*/
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
		//showIt(makeBug736137(), "Bug 736137");
		//showIt(makeBug891021(), "Bug 891021");
		showIt(makeJhao1(), "Bug jhao1");
		showIt(makeJhao2(), "Bug jhao2");
		try
		{
			showIt(makeJhao3(), "Bug jhao3");
			showIt(makeJhao4(), "Bug jhao4");
		}catch(Exception exc)
		{
			System.out.println("test jhao3 and 4 failed");	
			exc.printStackTrace();
		}
		
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
	public IAtomContainer generateCoordinates(Molecule m) throws Exception
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
			CMLReader reader = new CMLReader(ins);
			ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
			IChemSequence[] chemSequence = chemFile.getChemSequences();
		    IChemModel[] chemModels = chemSequence[0].getChemModels();
		    IAtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
	
			molecule = new Molecule(atomContainer);
		}
		catch(Exception exc)
		{
			fail(exc.toString());	
		}
		return molecule;

	}

	/**
	 *  Description of the Method
	 *
	 *@exception  java.lang.Exception  Description of the Exception
	 */
	public Molecule makeBug891021()
	{
		String filename = "data/mdl/bug891021.mol";
		Molecule molecule = null;
		try
		{
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
			IChemSequence[] chemSequence = chemFile.getChemSequences();
			IChemModel[] chemModels = chemSequence[0].getChemModels();
			IAtomContainer atomContainer = ChemModelManipulator.getAllInOneContainer(chemModels[0]);
	
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
		    CMLReader reader = new CMLReader(ins);
		    ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		    IChemSequence seq = chemFile.getChemSequence(0);
		    IChemModel model = seq.getChemModel(0);
		    IMolecule mol = model.getMoleculeSet().getMolecule(0);
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
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBiphenyl() throws Exception
	{
		Molecule m = MoleculeFactory.makeBiphenyl();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void test4x3CondensedRings() throws Exception
	{
		Molecule m = MoleculeFactory.make4x3CondensedRings();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testPhenylEthylBenzene() throws Exception
	{
		Molecule m = MoleculeFactory.makePhenylEthylBenzene();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testSpiroRings() throws Exception
	{
		Molecule m = MoleculeFactory.makeSpiroRings();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
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
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBranchedAliphatic() throws Exception
	{
		Molecule m = MoleculeFactory.makeBranchedAliphatic();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testDiamantane() throws Exception
	{
		Molecule m = MoleculeFactory.makeDiamantane();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testEthylCyclohexane() throws Exception
	{
		Molecule m = MoleculeFactory.makeEthylCyclohexane();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testBicycloRings() throws Exception
	{
		Molecule m = MoleculeFactory.makeBicycloRings();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public Molecule makeJhao3() throws Exception
	{
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C=C1C2=CC13(CC23)");
		return mol;
	}

	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public Molecule makeJhao4() throws Exception
	{
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CCC3C1CC23(CC12)");
		return mol;
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
		IAtomContainer ac = generateCoordinates(mol);
        assertTrue(GeometryTools.has2DCoordinates(ac));
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
		IAtomContainer ac = generateCoordinates(mol);
        assertTrue(GeometryTools.has2DCoordinates(ac));
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
			IAtomContainer ac = generateCoordinates(mol);
            assertTrue(GeometryTools.has2DCoordinates(ac));
		}
		catch(Exception exc)
		{
			if (!(exc.toString().indexOf("Molecule not connected")>= 0)) fail();
		}
	}
	
	
	Molecule makeJhao1()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("O")); // 8
		mol.addAtom(new Atom("C")); // 9
		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(0, 3, 1.0); // 2
		mol.addBond(0, 4, 1.0); // 3
		mol.addBond(0, 7, 1.0); // 4
		mol.addBond(1, 4, 1.0); // 5
		mol.addBond(1, 5, 1.0); // 6
		mol.addBond(1, 6, 1.0); // 7
		mol.addBond(2, 3, 1.0); // 8
		mol.addBond(2, 5, 1.0); // 9
		mol.addBond(2, 6, 1.0); // 10
		mol.addBond(2, 7, 1.0); // 11
		mol.addBond(3, 8, 1.0); // 12
		return mol;
	}

	Molecule makeJhao2()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("O")); // 8
		mol.addAtom(new Atom("C")); // 9
		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(0, 3, 1.0); // 2
		mol.addBond(0, 4, 1.0); // 3
		mol.addBond(0, 7, 1.0); // 4
		mol.addBond(1, 5, 1.0); // 5
		mol.addBond(1, 6, 1.0); // 6
		mol.addBond(1, 7, 1.0); // 7
		mol.addBond(2, 3, 1.0); // 8
		mol.addBond(2, 4, 1.0); // 9
		mol.addBond(2, 5, 1.0); // 10
		mol.addBond(2, 6, 1.0); // 11
		mol.addBond(3, 8, 1.0); // 12
		return mol;
	}
}

