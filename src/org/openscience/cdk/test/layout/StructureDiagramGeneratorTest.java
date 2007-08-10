/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2003-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
import java.io.StringReader;
import java.util.List;

import javax.swing.JFrame;
import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.Mol2Reader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
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
	 */
	public StructureDiagramGeneratorTest(String name)
	{
		super(name);
	}


	/**
	 *  The JUnit setup method
	 */
	public void setUp() 
	{ 
			
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(StructureDiagramGeneratorTest.class);
	}

	public void runVisualTests() throws Exception
	{
		moleculeListViewer = new MoleculeListViewer();
		//MoleculeViewer2D.display(MoleculeFactory.loadMolecule("data/mdl/reserpine.mol"), true);
		/*showIt(MoleculeFactory.loadMolecule("data/mdl/reserpine.mol"), "Reserpine");
		showIt(MoleculeFactory.loadMolecule("data/mdl/four-ring-5x10.mol"), "5x10 condensed four membered rings");
		showIt(MoleculeFactory.loadMolecule("data/mdl/six-ring-4x4.mol"), "4x4 condensed six membered rings");
		showIt(MoleculeFactory.loadMolecule("data/mdl/polycarpol.mol")
		, "Polycarpol");*/
		showIt(makeTetraMethylCycloButane(), "TetraMethylCycloButane");
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
		showIt(makeJhao3(), "Bug jhao3");
		showIt(makeJhao4(), "Bug jhao4");
		showIt(makeBug1750968(), "Bug 1750968");
	}

	private boolean showIt(IMolecule molecule, String name) throws Exception
	{
		MoleculeViewer2D mv = new MoleculeViewer2D();
		mv.setAtomContainer(generateCoordinates(molecule));
		moleculeListViewer.addStructure(mv, name);
		return true;
	}

	public IAtomContainer generateCoordinates(IMolecule m) throws Exception
	{
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		sdg.setUseTemplates(true);
		sdg.setMolecule(m);
		sdg.generateCoordinates(new Vector2d(0, 1));
		return sdg.getMolecule();
	}

	/**
	 * @cdk.bug 736137
	 */
	public void testBug736137() throws Exception
	{
		String filename = "data/mdl/bug736137.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IChemSequence chemSequence = chemFile.getChemSequence(0);
		IChemModel chemModel = chemSequence.getChemModel(0);
		List atomContainers = ChemModelManipulator.getAllAtomContainers(chemModel);
		assertEquals(1, atomContainers.size());
	}

	public void visualBugPMR() throws Exception
	{
                String filename = "data/cml/SL0016a.cml";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		CMLReader reader = new CMLReader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IChemSequence seq = chemFile.getChemSequence(0);
		IChemModel model = seq.getChemModel(0);
		IMolecule mol = model.getMoleculeSet().getMolecule(0);
		//MoleculeViewer2D.display(mol, true, false, JFrame.DO_NOTHING_ON_CLOSE,"");

	}
	
	

	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  thrown if something goes wrong
	 *@cdk.bug 1670871
	 */
	public void testBugLecture2007() throws Exception
	{
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		//IMolecule mol = sp.parseSmiles("Oc1nc(nc2c(nn(c12)C)CCC)c3cc(ccc3(OCC))S(=O)(=O)N4CCN(C)CC4");
		IMolecule mol = sp.parseSmiles("O=C(N1CCN(CC1)CCCN(C)C)C3(C=2C=CC(=CC=2)C)(CCCCC3)");
		
		//IMolecule mol = sp.parseSmiles("C1CCC1CCCCCCCC1CC1");

		IAtomContainer ac = generateCoordinates(mol);
//		MoleculeViewer2D.display(new Molecule(ac), false);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}

	
	/**
	 *  A unit test for JUnit
	 */
	public void testAlphaPinene() throws Exception
	{
		Molecule m = MoleculeFactory.makeAlphaPinene();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testBiphenyl() throws Exception
	{
		Molecule m = MoleculeFactory.makeBiphenyl();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 */
	public void test4x3CondensedRings() throws Exception
	{
		Molecule m = MoleculeFactory.make4x3CondensedRings();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testPhenylEthylBenzene() throws Exception
	{
		Molecule m = MoleculeFactory.makePhenylEthylBenzene();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
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
	 */
	public void testMethylDecaline() throws Exception
	{
		Molecule m = MoleculeFactory.makeMethylDecaline();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testBranchedAliphatic() throws Exception
	{
		Molecule m = MoleculeFactory.makeBranchedAliphatic();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
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
	 *@exception  Exception  thrown if something goes wrong
	 *@cdk.bug 1670871
	 */
	public void xtestBug1670871() throws Exception
	{
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CC(=O)OC1C=CC(SC23CC4CC(CC(C4)C2)C3)N(C1SC56CC7CC(CC(C7)C5)C6)C(C)=O");
		IAtomContainer ac = generateCoordinates(mol);
		//MoleculeViewer2D.display(new Molecule(ac), false);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}



	/**
	 *  A unit test for JUnit
	 */
	public void testEthylCyclohexane() throws Exception
	{
		Molecule m = MoleculeFactory.makeEthylCyclohexane();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testBicycloRings() throws Exception
	{
		Molecule m = MoleculeFactory.makeBicycloRings();
		IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}

	
	/**
	 *  A unit test for JUnit
	 */
	public IMolecule makeJhao3() throws Exception
	{
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=C1C2=CC13(CC23)");
		return mol;
	}

	/**
	 *  A unit test for JUnit
	 */
	public IMolecule makeJhao4() throws Exception
	{
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CCC3C1CC23(CC12)");
		return mol;
	}
	
	/**
	 *  A unit test for JUnit
	 */
	public void testBenzene() throws Exception
	{
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("c1ccccc1");
		IAtomContainer ac = generateCoordinates(mol);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}


	/**
	 * @cdk.bug 780545
	 */
	public void testBug780545() throws Exception
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C"));
		IAtomContainer ac = generateCoordinates(mol);
        assertTrue(GeometryTools.has2DCoordinates(ac));
	}
	
	/**
	 * @cdk.bug 1598409
	 */
	public void testBug1598409() throws Exception
	{
		String smiles = "c1(:c(:c2-C(-c3:c(-C(=O)-c:2:c(:c:1-[H])-[H]):c(:c(:c(:c:3-[H])-[H])-N(-[H])-[H])-[H])=O)-[H])-[H]";
		SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		IMolecule cdkMol = parser.parseSmiles(smiles);
		HueckelAromaticityDetector.detectAromaticity(cdkMol, false);
		new StructureDiagramGenerator(cdkMol).generateCoordinates();
	}
	

	
	/**
	 * @cdk.bug 1572062
	 */
	public void testBug1572062() throws Exception {
		String filename = "data/mdl/sdg_test.mol";

//		set up molecule reader
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		IChemObjectReader molReader = new MDLReader(ins);

//		read molecule
		IMolecule molecule = (IMolecule) molReader.read(new
				Molecule());

//		rebuild 2D coordinates
		StructureDiagramGenerator structureDiagramGenerator = 
			new StructureDiagramGenerator();
		for (int i = 0; i < 10; i++) {
			structureDiagramGenerator.setMolecule(molecule);
			structureDiagramGenerator.generateCoordinates();
		}

	}
	
	/**
	 * @cdk.bug 884993
	 */
	public void testBug884993() throws Exception
	{
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("[N+](=O)([O-])C1=C(O)C(=CC(=C1)[N+](=O)[O-])[N+](=O)[O-].C23N(CCCC2)CCCC3");
		try{
			IAtomContainer ac = generateCoordinates(mol);
            assertTrue(GeometryTools.has2DCoordinates(ac));
            fail("This should have thrown a 'Molecule not connected' exception.");
		} catch(Exception exc) {
			// OK, an exception should have been thrown
			if (!(exc.toString().indexOf("Molecule not connected")>= 0)) fail();
		}
	}
	
	/**
	 * @cdk.bug 1714794
	 */
	public void xtestBug1714794() throws Exception {
        String problematicMol2AsSmiles = "N1c2c(c3c(c4c(c(c3O)C)OC(OC=CC(C(C(C(C(C(C(C(C=CC=C(C1=O)C)C)O)C)O)C)OC(=O)C)C)OC)(C4=O)C)c(c2C=NN(C12CC3CC(C1)CC(C2)C3)C)O)O";
        SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
        IMolecule cdkMol = parser.parseSmiles(problematicMol2AsSmiles);
        HueckelAromaticityDetector.detectAromaticity(cdkMol, false);
        new StructureDiagramGenerator(cdkMol).generateCoordinates();
        assertTrue(GeometryTools.has2DCoordinates(cdkMol));
        
        String problematicMol2 = "@<TRIPOS>MOLECULE\n"
            + "mol_197219.smi\n"
            + " 129 135 0 0 0\n"
            + "SMALL\n"
            + "GASTEIGER\n"
            + "Energy = 0\n"
            + "\n"
            + "@<TRIPOS>ATOM\n"
            + "      1 N1          0.0000    0.0000    0.0000 N.am    1  <1>        -0.2782\n"
            + "      2 H1          0.0000    0.0000    0.0000 H       1  <1>         0.1552\n"
            + "      3 C1          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0886\n"
            + "      4 C2          0.0000    0.0000    0.0000 C.ar    1  <1>         0.1500\n"
            + "      5 C3          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0714\n"
            + "      6 C4          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0456\n"
            + "      7 C5          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0788\n"
            + "      8 C6          0.0000    0.0000    0.0000 C.ar    1  <1>         0.1435\n"
            + "      9 C7          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0342\n"
            + "     10 C8          0.0000    0.0000    0.0000 C.ar    1  <1>         0.1346\n"
            + "     11 O1          0.0000    0.0000    0.0000 O.3     1  <1>        -0.5057\n"
            + "     12 H2          0.0000    0.0000    0.0000 H       1  <1>         0.2922\n"
            + "     13 C9          0.0000    0.0000    0.0000 C.3     1  <1>        -0.0327\n"
            + "     14 H3          0.0000    0.0000    0.0000 H       1  <1>         0.0280\n"
            + "     15 H4          0.0000    0.0000    0.0000 H       1  <1>         0.0280\n"
            + "     16 H5          0.0000    0.0000    0.0000 H       1  <1>         0.0280\n"
            + "     17 O2          0.0000    0.0000    0.0000 O.3     1  <1>        -0.4436\n"
            + "     18 C10         0.0000    0.0000    0.0000 C.3     1  <1>         0.3143\n"
            + "     19 O3          0.0000    0.0000    0.0000 O.2     1  <1>        -0.4528\n"
            + "     20 C11         0.0000    0.0000    0.0000 C.2     1  <1>         0.0882\n"
            + "     21 H6          0.0000    0.0000    0.0000 H       1  <1>         0.1022\n"
            + "     22 C12         0.0000    0.0000    0.0000 C.2     1  <1>        -0.0208\n"
            + "     23 H7          0.0000    0.0000    0.0000 H       1  <1>         0.0628\n"
            + "     24 C13         0.0000    0.0000    0.0000 C.3     1  <1>         0.0854\n"
            + "     25 H8          0.0000    0.0000    0.0000 H       1  <1>         0.0645\n"
            + "     26 C14         0.0000    0.0000    0.0000 C.3     1  <1>         0.0236\n"
            + "     27 H9          0.0000    0.0000    0.0000 H       1  <1>         0.0362\n"
            + "     28 C15         0.0000    0.0000    0.0000 C.3     1  <1>         0.1131\n"
            + "     29 H10         0.0000    0.0000    0.0000 H       1  <1>         0.0741\n"
            + "     30 C16         0.0000    0.0000    0.0000 C.3     1  <1>         0.0200\n"
            + "     31 H11         0.0000    0.0000    0.0000 H       1  <1>         0.0359\n"
            + "     32 C17         0.0000    0.0000    0.0000 C.3     1  <1>         0.0661\n"
            + "     33 H12         0.0000    0.0000    0.0000 H       1  <1>         0.0600\n"
            + "     34 C18         0.0000    0.0000    0.0000 C.3     1  <1>         0.0091\n"
            + "     35 H13         0.0000    0.0000    0.0000 H       1  <1>         0.0348\n"
            + "     36 C19         0.0000    0.0000    0.0000 C.3     1  <1>         0.0661\n"
            + "     37 H14         0.0000    0.0000    0.0000 H       1  <1>         0.0602\n"
            + "     38 C20         0.0000    0.0000    0.0000 C.3     1  <1>         0.0009\n"
            + "     39 H15         0.0000    0.0000    0.0000 H       1  <1>         0.0365\n"
            + "     40 C21         0.0000    0.0000    0.0000 C.2     1  <1>        -0.0787\n"
            + "     41 H16         0.0000    0.0000    0.0000 H       1  <1>         0.0576\n"
            + "     42 C22         0.0000    0.0000    0.0000 C.2     1  <1>        -0.0649\n"
            + "     43 H17         0.0000    0.0000    0.0000 H       1  <1>         0.0615\n"
            + "     44 C23         0.0000    0.0000    0.0000 C.2     1  <1>        -0.0542\n"
            + "     45 H18         0.0000    0.0000    0.0000 H       1  <1>         0.0622\n"
            + "     46 C24         0.0000    0.0000    0.0000 C.2     1  <1>         0.0115\n"
            + "     47 C25         0.0000    0.0000    0.0000 C.2     1  <1>         0.2441\n"
            + "     48 O4          0.0000    0.0000    0.0000 O.2     1  <1>        -0.2702\n"
            + "     49 C26         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0348\n"
            + "     50 H19         0.0000    0.0000    0.0000 H       1  <1>         0.0279\n"
            + "     51 H20         0.0000    0.0000    0.0000 H       1  <1>         0.0279\n"
            + "     52 H21         0.0000    0.0000    0.0000 H       1  <1>         0.0279\n"
            + "     53 C27         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0566\n"
            + "     54 H22         0.0000    0.0000    0.0000 H       1  <1>         0.0236\n"
            + "     55 H23         0.0000    0.0000    0.0000 H       1  <1>         0.0236\n"
            + "     56 H24         0.0000    0.0000    0.0000 H       1  <1>         0.0236\n"
            + "     57 O5          0.0000    0.0000    0.0000 O.3     1  <1>        -0.3909\n"
            + "     58 H25         0.0000    0.0000    0.0000 H       1  <1>         0.2098\n"
            + "     59 C28         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0577\n"
            + "     60 H26         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
            + "     61 H27         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
            + "     62 H28         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
            + "     63 O6          0.0000    0.0000    0.0000 O.3     1  <1>        -0.3910\n"
            + "     64 H29         0.0000    0.0000    0.0000 H       1  <1>         0.2098\n"
            + "     65 C29         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0567\n"
            + "     66 H30         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
            + "     67 H31         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
            + "     68 H32         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
            + "     69 O7          0.0000    0.0000    0.0000 O.3     1  <1>        -0.4608\n"
            + "     70 C30         0.0000    0.0000    0.0000 C.2     1  <1>         0.3042\n"
            + "     71 O8          0.0000    0.0000    0.0000 O.2     1  <1>        -0.2512\n"
            + "     72 C31         0.0000    0.0000    0.0000 C.3     1  <1>         0.0332\n"
            + "     73 H33         0.0000    0.0000    0.0000 H       1  <1>         0.0342\n"
            + "     74 H34         0.0000    0.0000    0.0000 H       1  <1>         0.0342\n"
            + "     75 H35         0.0000    0.0000    0.0000 H       1  <1>         0.0342\n"
            + "     76 C32         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0564\n"
            + "     77 H36         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
            + "     78 H37         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
            + "     79 H38         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
            + "     80 O9          0.0000    0.0000    0.0000 O.3     1  <1>        -0.3753\n"
            + "     81 C33         0.0000    0.0000    0.0000 C.3     1  <1>         0.0372\n"
            + "     82 H39         0.0000    0.0000    0.0000 H       1  <1>         0.0524\n"
            + "     83 H40         0.0000    0.0000    0.0000 H       1  <1>         0.0524\n"
            + "     84 H41         0.0000    0.0000    0.0000 H       1  <1>         0.0524\n"
            + "     85 C34         0.0000    0.0000    0.0000 C.2     1  <1>         0.2505\n"
            + "     86 O10         0.0000    0.0000    0.0000 O.2     1  <1>        -0.2836\n"
            + "     87 C35         0.0000    0.0000    0.0000 C.3     1  <1>         0.0210\n"
            + "     88 H42         0.0000    0.0000    0.0000 H       1  <1>         0.0309\n"
            + "     89 H43         0.0000    0.0000    0.0000 H       1  <1>         0.0309\n"
            + "     90 H44         0.0000    0.0000    0.0000 H       1  <1>         0.0309\n"
            + "     91 C36         0.0000    0.0000    0.0000 C.ar    1  <1>         0.1361\n"
            + "     92 C37         0.0000    0.0000    0.0000 C.ar    1  <1>         0.0613\n"
            + "     93 C38         0.0000    0.0000    0.0000 C.2     1  <1>         0.0580\n"
            + "     94 H45         0.0000    0.0000    0.0000 H       1  <1>         0.0853\n"
            + "     95 N2          0.0000    0.0000    0.0000 N.2     1  <1>        -0.1915\n"
            + "     96 N3          0.0000    0.0000    0.0000 N.pl3   1  <1>        -0.2525\n"
            + "     97 C39         0.0000    0.0000    0.0000 C.3     1  <1>         0.0525\n"
            + "     98 C40         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0271\n"
            + "     99 H46         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
            + "    100 H47         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
            + "    101 C41         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0385\n"
            + "    102 H48         0.0000    0.0000    0.0000 H       1  <1>         0.0302\n"
            + "    103 C42         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0472\n"
            + "    104 H49         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
            + "    105 H50         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
            + "    106 C43         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0385\n"
            + "    107 H51         0.0000    0.0000    0.0000 H       1  <1>         0.0302\n"
            + "    108 C44         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0271\n"
            + "    109 H52         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
            + "    110 H53         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
            + "    111 C45         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0472\n"
            + "    112 H54         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
            + "    113 H55         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
            + "    114 C46         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0385\n"
            + "    115 H56         0.0000    0.0000    0.0000 H       1  <1>         0.0302\n"
            + "    116 C47         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0271\n"
            + "    117 H57         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
            + "    118 H58         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
            + "    119 C48         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0472\n"
            + "    120 H59         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
            + "    121 H60         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
            + "    122 C49         0.0000    0.0000    0.0000 C.3     1  <1>         0.0189\n"
            + "    123 H61         0.0000    0.0000    0.0000 H       1  <1>         0.0444\n"
            + "    124 H62         0.0000    0.0000    0.0000 H       1  <1>         0.0444\n"
            + "    125 H63         0.0000    0.0000    0.0000 H       1  <1>         0.0444\n"
            + "    126 O11         0.0000    0.0000    0.0000 O.3     1  <1>        -0.5054\n"
            + "    127 H64         0.0000    0.0000    0.0000 H       1  <1>         0.2922\n"
            + "    128 O12         0.0000    0.0000    0.0000 O.3     1  <1>        -0.5042\n"
            + "    129 H65         0.0000    0.0000    0.0000 H       1  <1>         0.2923\n"
            + "@<TRIPOS>BOND\n"
            + "     1     1     2    1\n"
            + "     2     1     3    1\n"
            + "     3     3     4   ar\n"
            + "     4     4     5   ar\n"
            + "     5     5     6   ar\n"
            + "     6     6     7   ar\n"
            + "     7     7     8   ar\n"
            + "     8     8     9   ar\n"
            + "     9     9    10   ar\n"
            + "    10     5    10   ar\n"
            + "    11    10    11    1\n"
            + "    12    11    12    1\n"
            + "    13     9    13    1\n"
            + "    14    13    14    1\n"
            + "    15    13    15    1\n"
            + "    16    13    16    1\n"
            + "    17     8    17    1\n"
            + "    18    17    18    1\n"
            + "    19    18    19    1\n"
            + "    20    19    20    1\n"
            + "    21    20    21    1\n"
            + "    22    20    22    2\n"
            + "    23    22    23    1\n"
            + "    24    22    24    1\n"
            + "    25    24    25    1\n"
            + "    26    24    26    1\n"
            + "    27    26    27    1\n"
            + "    28    26    28    1\n"
            + "    29    28    29    1\n"
            + "    30    28    30    1\n"
            + "    31    30    31    1\n"
            + "    32    30    32    1\n"
            + "    33    32    33    1\n"
            + "    34    32    34    1\n"
            + "    35    34    35    1\n"
            + "    36    34    36    1\n"
            + "    37    36    37    1\n"
            + "    38    36    38    1\n"
            + "    39    38    39    1\n"
            + "    40    38    40    1\n"
            + "    41    40    41    1\n"
            + "    42    40    42    2\n"
            + "    43    42    43    1\n"
            + "    44    42    44    1\n"
            + "    45    44    45    1\n"
            + "    46    44    46    2\n"
            + "    47    46    47    1\n"
            + "    48     1    47   am\n"
            + "    49    47    48    2\n"
            + "    50    46    49    1\n"
            + "    51    49    50    1\n"
            + "    52    49    51    1\n"
            + "    53    49    52    1\n"
            + "    54    38    53    1\n"
            + "    55    53    54    1\n"
            + "    56    53    55    1\n"
            + "    57    53    56    1\n"
            + "    58    36    57    1\n"
            + "    59    57    58    1\n"
            + "    60    34    59    1\n"
            + "    61    59    60    1\n"
            + "    62    59    61    1\n"
            + "    63    59    62    1\n"
            + "    64    32    63    1\n"
            + "    65    63    64    1\n"
            + "    66    30    65    1\n"
            + "    67    65    66    1\n"
            + "    68    65    67    1\n"
            + "    69    65    68    1\n"
            + "    70    28    69    1\n"
            + "    71    69    70    1\n"
            + "    72    70    71    2\n"
            + "    73    70    72    1\n"
            + "    74    72    73    1\n"
            + "    75    72    74    1\n"
            + "    76    72    75    1\n"
            + "    77    26    76    1\n"
            + "    78    76    77    1\n"
            + "    79    76    78    1\n"
            + "    80    76    79    1\n"
            + "    81    24    80    1\n"
            + "    82    80    81    1\n"
            + "    83    81    82    1\n"
            + "    84    81    83    1\n"
            + "    85    81    84    1\n"
            + "    86    18    85    1\n"
            + "    87     7    85    1\n"
            + "    88    85    86    2\n"
            + "    89    18    87    1\n"
            + "    90    87    88    1\n"
            + "    91    87    89    1\n"
            + "    92    87    90    1\n"
            + "    93     6    91   ar\n"
            + "    94    91    92   ar\n"
            + "    95     3    92   ar\n"
            + "    96    92    93    1\n"
            + "    97    93    94    1\n"
            + "    98    93    95    2\n"
            + "    99    95    96    1\n"
            + "   100    96    97    1\n"
            + "   101    97    98    1\n"
            + "   102    98    99    1\n"
            + "   103    98   100    1\n"
            + "   104    98   101    1\n"
            + "   105   101   102    1\n"
            + "   106   101   103    1\n"
            + "   107   103   104    1\n"
            + "   108   103   105    1\n"
            + "   109   103   106    1\n"
            + "   110   106   107    1\n"
            + "   111   106   108    1\n"
            + "   112   108   109    1\n"
            + "   113   108   110    1\n"
            + "   114    97   108    1\n"
            + "   115   106   111    1\n"
            + "   116   111   112    1\n"
            + "   117   111   113    1\n"
            + "   118   111   114    1\n"
            + "   119   114   115    1\n"
            + "   120   114   116    1\n"
            + "   121   116   117    1\n"
            + "   122   116   118    1\n"
            + "   123    97   116    1\n"
            + "   124   114   119    1\n"
            + "   125   119   120    1\n"
            + "   126   119   121    1\n"
            + "   127   101   119    1\n"
            + "   128    96   122    1\n"
            + "   129   122   123    1\n"
            + "   130   122   124    1\n"
            + "   131   122   125    1\n"
            + "   132    91   126    1\n"
            + "   133   126   127    1\n"
            + "   134     4   128    1\n"
            + "   135   128   129    1\n";
        Mol2Reader r = new Mol2Reader(new StringReader(problematicMol2));
        IChemModel model = (IChemModel)r.read(NoNotificationChemObjectBuilder
                            .getInstance().newChemModel());
	    final IMolecule mol = model.getMoleculeSet().getMolecule(0);
        final IMolecule clone = (IMolecule)mol.clone();
        // modified from runner thread
        final boolean[] failsArray = new boolean[1];
        try{
        	new StructureDiagramGenerator(clone).generateCoordinates();
        	assertTrue(GeometryTools.has2DCoordinates(clone));
        } catch(Exception exc) {
        	failsArray[0] = true;
        }
        if (failsArray[1]) {
            fail("2D generation failed");
        }
	}

	Molecule makeTetraMethylCycloButane()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
		mol.addAtom(new Atom("C")); // 7
		mol.addAtom(new Atom("C")); // 8
		
		mol.addBond(0, 1, 1.0); // 1
		mol.addBond(1, 2, 1.0); // 2
		mol.addBond(2, 3, 1.0); // 3
		mol.addBond(3, 0, 1.0); // 4
		mol.addBond(0, 4, 1.0); // 5
		mol.addBond(1, 5, 1.0); // 6
		mol.addBond(2, 6, 1.0); // 7
		mol.addBond(3, 7, 1.0); // 8
		return mol;
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
	
	/**
	 * @cdk.bug 1750968
	 */
	public IMolecule makeBug1750968() throws Exception {
		String filename = "data/mdl/bug_1750968.mol";

//		set up molecule reader
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		IChemObjectReader molReader = new MDLReader(ins);

//		read molecule
		return ((IMolecule) molReader.read(new	Molecule()));
	}

}

