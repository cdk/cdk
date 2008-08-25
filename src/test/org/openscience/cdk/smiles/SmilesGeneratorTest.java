/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.smiles;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.*;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.graph.AtomContainerBondPermutor;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point2d;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * @author         steinbeck
 * @cdk.created    2004-02-09
 * @cdk.module     test-smiles
 */
public class SmilesGeneratorTest extends CDKTestCase {
	
	/**
	 *  Constructor for the SmilesGeneratorTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public SmilesGeneratorTest(String name)
	{
		super(name);
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(SmilesGeneratorTest.class);
	}

	/**
	 *  A unit test for JUnit
	 */
	public void testSmilesGenerator()
	{
        Molecule mol2 = MoleculeFactory.makeAlphaPinene();
		SmilesGenerator sg = new SmilesGenerator();
		fixCarbonHCount(mol2);
		String smiles2 = sg.createSMILES(mol2);
		assertNotNull(smiles2);
		assertEquals("C1=C(C)C2CC(C1)C2(C)(C)", smiles2);
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testEthylPropylPhenantren()
	{
		Molecule mol1 = MoleculeFactory.makeEthylPropylPhenantren();
        SmilesGenerator sg = new SmilesGenerator();
		fixCarbonHCount(mol1);
		String smiles1 = sg.createSMILES(mol1);
		assertNotNull(smiles1);
		assertEquals("C=2C=C1C=3C=CC(=CC=3(C=CC1=C(C=2)CC))CCC", smiles1);
	}

	
	
	/**
	 *  A unit test for JUnit
	 */
	public void testPropylCycloPropane()
	{
		Molecule mol1 = MoleculeFactory.makePropylCycloPropane();
        SmilesGenerator sg = new SmilesGenerator();
		fixCarbonHCount(mol1);
		String smiles1 = sg.createSMILES(mol1);
		assertNotNull(smiles1);
		assertEquals("CCCC1CC1", smiles1);
	}
	
	

	/**
	 *  A unit test for JUnit
	 *
	 */
	public void testAlanin() throws Exception
	{
		HydrogenPlacer hydrogenPlacer = new HydrogenPlacer();
        Molecule mol1 = new Molecule();
		SmilesGenerator sg = new SmilesGenerator();
		mol1.addAtom(new Atom("N", new Point2d(1, 0)));
		// 1
		mol1.addAtom(new Atom("C", new Point2d(1, 2)));
		// 2
		mol1.addAtom(new Atom("F", new Point2d(1, 2)));
		// 3
		mol1.addAtom(new Atom("C", new Point2d(0, 0)));
		// 4
		mol1.addAtom(new Atom("C", new Point2d(1, 4)));
		// 5
		mol1.addAtom(new Atom("O", new Point2d(1, 5)));
		// 6
		mol1.addAtom(new Atom("O", new Point2d(1, 6)));
		// 7
		mol1.addBond(0, 1, IBond.Order.SINGLE);
		// 1
		mol1.addBond(1, 2, IBond.Order.SINGLE, CDKConstants.STEREO_BOND_UP);
		// 2
		mol1.addBond(1, 3, IBond.Order.SINGLE, CDKConstants.STEREO_BOND_DOWN);
		// 3
		mol1.addBond(1, 4, IBond.Order.SINGLE);
		// 4
		mol1.addBond(4, 5, IBond.Order.SINGLE);
		// 5
		mol1.addBond(4, 6, IBond.Order.DOUBLE);
		// 6
		addExplicitHydrogens(mol1);
		hydrogenPlacer.placeHydrogens2D(mol1, 1.0);
		IsotopeFactory ifac = IsotopeFactory.getInstance(mol1.getBuilder());
		ifac.configureAtoms(mol1);

		String smiles1 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		assertNotNull(smiles1);
		assertEquals("[H]OC(=O)[C@](F)(N([H])[H])C([H])([H])[H]", smiles1);
		
		//by setting additional stereo descriptors, we should get another smiles
		mol1.getBond(1).setStereo(CDKConstants.STEREO_BOND_DOWN);
		mol1.getBond(2).setStereo(CDKConstants.STEREO_BOND_UP);
		smiles1 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		assertNotNull(smiles1);
		assertEquals("[H]OC(=O)[C@](F)(C([H])([H])[H])N([H])[H]", smiles1);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testCisResorcinol() throws Exception
	{
		HydrogenPlacer hydrogenPlacer = new HydrogenPlacer();
        Molecule mol1 = new Molecule();
		SmilesGenerator sg = new SmilesGenerator();
		mol1.addAtom(new Atom("O", new Point2d(3, 1)));
		// 1
		mol1.addAtom(new Atom("H", new Point2d(2, 0)));
		// 2
		mol1.addAtom(new Atom("C", new Point2d(2, 1)));
		// 3
		mol1.addAtom(new Atom("C", new Point2d(1, 1)));
		// 4
		mol1.addAtom(new Atom("C", new Point2d(1, 4)));
		// 5
		mol1.addAtom(new Atom("C", new Point2d(1, 5)));
		// 6
		mol1.addAtom(new Atom("C", new Point2d(1, 2)));
		// 7
		mol1.addAtom(new Atom("C", new Point2d(2, 2)));
		// 1
		mol1.addAtom(new Atom("O", new Point2d(3, 2)));
		// 2
		mol1.addAtom(new Atom("H", new Point2d(2, 3)));
		// 3
		mol1.addBond(0, 2, IBond.Order.SINGLE, CDKConstants.STEREO_BOND_DOWN);
		// 1
		mol1.addBond(1, 2, IBond.Order.SINGLE, CDKConstants.STEREO_BOND_UP);
		// 2
		mol1.addBond(2, 3, IBond.Order.SINGLE);
		// 3
		mol1.addBond(3, 4, IBond.Order.SINGLE);
		// 4
		mol1.addBond(4, 5, IBond.Order.SINGLE);
		// 5
		mol1.addBond(5, 6, IBond.Order.SINGLE);
		// 6
		mol1.addBond(6, 7, IBond.Order.SINGLE);
		// 3
		mol1.addBond(7, 8, IBond.Order.SINGLE, CDKConstants.STEREO_BOND_UP);
		// 4
		mol1.addBond(7, 9, IBond.Order.SINGLE, CDKConstants.STEREO_BOND_DOWN);
		// 5
		mol1.addBond(7, 2, IBond.Order.SINGLE);
		// 6
		addExplicitHydrogens(mol1);
		hydrogenPlacer.placeHydrogens2D(mol1, 1.0);
		IsotopeFactory ifac = IsotopeFactory.getInstance(mol1.getBuilder());
		ifac.configureAtoms(mol1);
		String smiles1 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		assertNotNull(smiles1);
		assertEquals("[H]O[C@]1(C([H])([H])C([H])([H])C([H])([H])C([H])([H])[C@]1(O[H])([H]))([H])", smiles1);
		mol1 = (Molecule) AtomContainerManipulator.removeHydrogens(mol1);
		smiles1 = sg.createSMILES(mol1);
		assertNotNull(smiles1);
		assertEquals("OC1CCCCC1(O)", smiles1);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testCisTransDecalin() throws Exception
	{
		HydrogenPlacer hydrogenPlacer = new HydrogenPlacer();
        Molecule mol1 = new Molecule();
		SmilesGenerator sg = new SmilesGenerator();
		mol1.addAtom(new Atom("H", new Point2d(1, 0)));
		// 1
		mol1.addAtom(new Atom("C", new Point2d(1, 2)));
		// 2
		mol1.addAtom(new Atom("C", new Point2d(1, 2)));
		// 3
		mol1.addAtom(new Atom("C", new Point2d(0, 0)));
		// 4
		mol1.addAtom(new Atom("C", new Point2d(1, 4)));
		// 5
		mol1.addAtom(new Atom("C", new Point2d(1, 5)));
		// 6
		mol1.addAtom(new Atom("C", new Point2d(1, 6)));
		// 7
		mol1.addAtom(new Atom("H", new Point2d(1, 0)));
		// 1
		mol1.addAtom(new Atom("C", new Point2d(1, 2)));
		// 2
		mol1.addAtom(new Atom("C", new Point2d(1, 2)));
		// 3
		mol1.addAtom(new Atom("C", new Point2d(1, 2)));
		// 2
		mol1.addAtom(new Atom("C", new Point2d(1, 2)));
		// 3
		mol1.addBond(0, 1, IBond.Order.SINGLE, CDKConstants.STEREO_BOND_DOWN);
		// 1
		mol1.addBond(1, 2, IBond.Order.SINGLE);
		// 2
		mol1.addBond(2, 3, IBond.Order.SINGLE);
		// 3
		mol1.addBond(3, 4, IBond.Order.SINGLE);
		// 4
		mol1.addBond(4, 5, IBond.Order.SINGLE);
		// 5
		mol1.addBond(5, 6, IBond.Order.SINGLE);
		// 6
		mol1.addBond(6, 7, IBond.Order.SINGLE, CDKConstants.STEREO_BOND_DOWN);
		// 3
		mol1.addBond(6, 8, IBond.Order.SINGLE);
		// 4
		mol1.addBond(8, 9, IBond.Order.SINGLE);
		// 5
		mol1.addBond(9, 10, IBond.Order.SINGLE);
		// 6
		mol1.addBond(10, 11, IBond.Order.SINGLE);
		// 6
		mol1.addBond(11, 1, IBond.Order.SINGLE);
		// 6
		mol1.addBond(1, 6, IBond.Order.SINGLE);
		// 6

		addExplicitHydrogens(mol1);
		hydrogenPlacer.placeHydrogens2D(mol1, 1.0);
		IsotopeFactory ifac = IsotopeFactory.getInstance(mol1.getBuilder());
		ifac.configureAtoms(mol1);
		String smiles1 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		assertNotNull(smiles1);
		assertEquals("[H]C1([H])(C([H])([H])C([H])([H])C\\2([H])(C([H])([H])C([H])([H])C([H])([H])C([H])([H])C\\2([H])(C1([H])([H]))))", smiles1);
		mol1.getBond(6).setStereo(CDKConstants.STEREO_BOND_UP);
		String smiles3 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		assertNotSame(smiles1, smiles3);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	public void testDoubleBondConfiguration() throws Exception
	{
		HydrogenPlacer hydrogenPlacer = new HydrogenPlacer();
		Molecule mol1 = new Molecule();
        SmilesGenerator sg = new SmilesGenerator();
		mol1.addAtom(new Atom("S", new Point2d(0, 0)));
		// 1
		mol1.addAtom(new Atom("C", new Point2d(1, 1)));
		// 2
		mol1.addAtom(new Atom("F", new Point2d(2, 0)));
		// 3
		mol1.addAtom(new Atom("C", new Point2d(1, 2)));
		// 4
		mol1.addAtom(new Atom("F", new Point2d(2, 3)));
		// 5
		mol1.addAtom(new Atom("S", new Point2d(0, 3)));
		// 1

		mol1.addBond(0, 1, IBond.Order.SINGLE);
		// 1
		mol1.addBond(1, 2, IBond.Order.SINGLE);
		// 2
		mol1.addBond(1, 3, IBond.Order.DOUBLE);
		// 3
		mol1.addBond(3, 4, IBond.Order.SINGLE);
		// 4
		mol1.addBond(3, 5, IBond.Order.SINGLE);
		// 4
		IsotopeFactory ifac = IsotopeFactory.getInstance(mol1.getBuilder());
		ifac.configureAtoms(mol1);
		boolean[] bool = new boolean[mol1.getBondCount()];
		bool[2] = true;
		String smiles1 = sg.createSMILES(mol1, true, bool);
		assertNotNull(smiles1);
		assertEquals("F/C(=C/(F)S)S", smiles1);
		mol1.getAtom(4).setPoint2d(new Point2d(0, 3));
		mol1.getAtom(5).setPoint2d(new Point2d(2, 3));
		
		smiles1 = sg.createSMILES(mol1, true, bool);
		assertNotNull(smiles1);
		assertEquals("F/C(=C\\(F)S)S", smiles1);

		addExplicitHydrogens(mol1);
		hydrogenPlacer.placeHydrogens2D(mol1, 1.0);
		bool = new boolean[mol1.getBondCount()];
		bool[2] = true;
		smiles1 = sg.createSMILES(mol1, true, bool);
		assertNotNull(smiles1);
		assertEquals("[H]S/C(F)=C/(F)S[H]", smiles1);
		
		mol1.getAtom(5).setPoint2d(new Point2d(0, 3));
		mol1.getAtom(4).setPoint2d(new Point2d(2, 3));
		smiles1 = sg.createSMILES(mol1, true, bool);
		assertNotNull(smiles1);
		assertEquals("[H]S/C(F)=C\\(F)S[H]", smiles1);
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testPartitioning()
	{
		String smiles = "";
		Molecule molecule = new Molecule();
        SmilesGenerator sg = new SmilesGenerator();
		Atom sodium = new Atom("Na");
		sodium.setFormalCharge(+1);
		Atom hydroxyl = new Atom("O");
		hydroxyl.setHydrogenCount(1);
		hydroxyl.setFormalCharge(-1);
		molecule.addAtom(sodium);
		molecule.addAtom(hydroxyl);
		smiles = sg.createSMILES(molecule);
		assertTrue(smiles.indexOf(".") != -1);
	}


	/**
	 * @cdk.bug 791091
	 */
	public void testBug791091()
	{
		String smiles = "";
		Molecule molecule = new Molecule();
        SmilesGenerator sg = new SmilesGenerator();
		molecule.addAtom(new Atom("C"));
		molecule.addAtom(new Atom("C"));
		molecule.addAtom(new Atom("C"));
		molecule.addAtom(new Atom("C"));
		molecule.addAtom(new Atom("N"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addBond(2, 4, IBond.Order.SINGLE);
		molecule.addBond(4, 0, IBond.Order.SINGLE);
		molecule.addBond(4, 3, IBond.Order.SINGLE);
		fixCarbonHCount(molecule);
		smiles = sg.createSMILES(molecule);
		assertEquals("N1(C)CCC1", smiles);
	}

	/**
	 * @cdk.bug 590236
	 */
	public void testBug590236()
	{
		String smiles = "";
		Molecule molecule = new Molecule();
        SmilesGenerator sg = new SmilesGenerator();
		molecule.addAtom(new Atom("C"));
		Atom carbon2 = new Atom("C");
		carbon2.setMassNumber(13);
		molecule.addAtom(carbon2);
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		fixCarbonHCount(molecule);
		smiles = sg.createSMILES(molecule);
		assertEquals("C[13C]", smiles);
	}

	/**
	 * A bug reported for JChemPaint.
	 * 
	 * @cdk.bug 956923
	 */
	public void testSFBug956923() throws Exception 
	{
		String smiles = "";
		Molecule molecule = new Molecule();
        SmilesGenerator sg = new SmilesGenerator();
        sg.setUseAromaticityFlag(true);
		Atom sp2CarbonWithOneHydrogen = new Atom("C");
		sp2CarbonWithOneHydrogen.setHybridization(IAtomType.Hybridization.SP2);
		sp2CarbonWithOneHydrogen.setHydrogenCount(1);
		molecule.addAtom(sp2CarbonWithOneHydrogen);
		molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
		molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
		molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
		molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
		molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addBond(2, 3, IBond.Order.SINGLE);
		molecule.addBond(3, 4, IBond.Order.SINGLE);
		molecule.addBond(4, 5, IBond.Order.SINGLE);
		molecule.addBond(5, 0, IBond.Order.SINGLE);
		smiles = sg.createSMILES(molecule);
		assertEquals("c1ccccc1", smiles);
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testAtomPermutation()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("S"));
		mol.addAtom(new Atom("O"));
		mol.addAtom(new Atom("O"));
		mol.addAtom(new Atom("O"));
		mol.addAtom(new Atom("O"));
		mol.addBond(0, 1, IBond.Order.DOUBLE);
		mol.addBond(0, 2, IBond.Order.DOUBLE);
		mol.addBond(0, 3, IBond.Order.SINGLE);
		mol.addBond(0, 4, IBond.Order.SINGLE);
		mol.getAtom(3).setHydrogenCount(1);
		mol.getAtom(4).setHydrogenCount(1);
		AtomContainerAtomPermutor acap = new AtomContainerAtomPermutor(mol);
		SmilesGenerator sg = new SmilesGenerator();
		String smiles = "";
		String oldSmiles = sg.createSMILES(mol);
		while (acap.hasNext())
		{
			smiles = sg.createSMILES(new Molecule((AtomContainer) acap.next()));
			//logger.debug(smiles);
			assertEquals(oldSmiles, smiles);
		}

	}


	/**
	 *  A unit test for JUnit
	 */
	public void testBondPermutation()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("S"));
		mol.addAtom(new Atom("O"));
		mol.addAtom(new Atom("O"));
		mol.addAtom(new Atom("O"));
		mol.addAtom(new Atom("O"));
		mol.addBond(0, 1, IBond.Order.DOUBLE);
		mol.addBond(0, 2, IBond.Order.DOUBLE);
		mol.addBond(0, 3, IBond.Order.SINGLE);
		mol.addBond(0, 4, IBond.Order.SINGLE);
		mol.getAtom(3).setHydrogenCount(1);
		mol.getAtom(4).setHydrogenCount(1);
		AtomContainerBondPermutor acbp = new AtomContainerBondPermutor(mol);
		SmilesGenerator sg = new SmilesGenerator();
		String smiles = "";
		String oldSmiles = sg.createSMILES(mol);
		while (acbp.hasNext())
		{
			smiles = sg.createSMILES(new Molecule((AtomContainer) acbp.next()));
			//logger.debug(smiles);
			assertEquals(oldSmiles, smiles);
		}

	}

	private void fixCarbonHCount(Molecule mol) {
		/*
		 *  the following line are just a quick fix for this
		 *  particluar carbon-only molecule until we have a proper
		 *  hydrogen count configurator
		 */
		double bondCount = 0;
		org.openscience.cdk.interfaces.IAtom atom;
		for (int f = 0; f < mol.getAtomCount(); f++)
		{
			atom = mol.getAtom(f);
			bondCount = mol.getBondOrderSum(atom);
			int correction = (int)bondCount - (
				atom.getCharge()!=null ? atom.getCharge().intValue() : 0
			);
			if (atom.getSymbol().equals("C")) {
				atom.setHydrogenCount(4 - correction);
			} else if (atom.getSymbol().equals("N")) {
				atom.setHydrogenCount(3 - correction);
			}
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testPseudoAtom()
	{
		Atom atom = new PseudoAtom("Star");
		SmilesGenerator sg = new SmilesGenerator();
		String smiles = "";
		Molecule molecule = new Molecule();
		molecule.addAtom(atom);
		smiles = sg.createSMILES(molecule);
		assertEquals("[*]", smiles);
	}


	/**
	 *  Test generation of a reaction SMILES. I know, it's a stupid alchemic
	 *  reaction, but it serves its purpose.
	 */
	public void testReactionSMILES() throws Exception {
		Reaction reaction = new Reaction();
		Molecule methane = new Molecule();
		methane.addAtom(new Atom("C"));
		reaction.addReactant(methane);
		Molecule magic = new Molecule();
		magic.addAtom(new PseudoAtom("magic"));
		reaction.addAgent(magic);
		Molecule gold = new Molecule();
		gold.addAtom(new Atom("Au"));
		reaction.addProduct(gold);

		SmilesGenerator sg = new SmilesGenerator();
		String smiles = sg.createSMILES(reaction);
		//logger.debug("Generated SMILES: " + smiles);
		assertEquals("C>[*]>[Au]", smiles);
	}


	/**
	 *  Test generation of a D and L alanin.
	 */
	public void testAlaSMILES() throws Exception {
		String filename = "data/mdl/l-ala.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		Molecule mol1 = (Molecule) reader.read(new Molecule());
		addExplicitHydrogens(mol1);
		new HydrogenPlacer().placeHydrogens2D(mol1, 1.0);
		filename = "data/mdl/d-ala.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLV2000Reader(ins, Mode.STRICT);
		Molecule mol2 = (Molecule) reader.read(new Molecule());
		addExplicitHydrogens(mol2);
		new HydrogenPlacer().placeHydrogens2D(mol2, 1.0);
		SmilesGenerator sg = new SmilesGenerator();
		String smiles1 = sg.createChiralSMILES(mol1, new boolean[20]);
		String smiles2 = sg.createChiralSMILES(mol2, new boolean[20]);
		assertNotSame(smiles1, smiles2);
	}


	/**
	 *  Test some sugars
	 */
	public void testSugarSMILES() throws Exception {
		String filename = "data/mdl/D-mannose.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		Molecule mol1 = (Molecule) reader.read(new Molecule());
		new HydrogenPlacer().placeHydrogens2D(mol1, 1.0);
		filename = "data/mdl/D+-glucose.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLV2000Reader(ins, Mode.STRICT);
		Molecule mol2 = (Molecule) reader.read(new Molecule());
		new HydrogenPlacer().placeHydrogens2D(mol2, 1.0);
		SmilesGenerator sg = new SmilesGenerator();
		String smiles1 = sg.createChiralSMILES(mol1, new boolean[20]);
		String smiles2 = sg.createChiralSMILES(mol2, new boolean[20]);
		assertNotSame(smiles1, smiles2);
	}

	/**
	 *  Test for some rings where the double bond is broken
	 */
	public void testCycloOctan() throws Exception {
		String filename = "data/mdl/cyclooctan.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		Molecule mol1 = (Molecule) reader.read(new Molecule());
		SmilesGenerator sg = new SmilesGenerator();
		String moleculeSmile = sg.createSMILES(mol1);
		assertEquals(moleculeSmile, "C1=CCCCCCC1");
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testCycloOcten() throws Exception {
		String filename = "data/mdl/cycloocten.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		Molecule mol1 = (Molecule) reader.read(new Molecule());
		SmilesGenerator sg = new SmilesGenerator();
		String moleculeSmile = sg.createSMILES(mol1);
		assertEquals(moleculeSmile, "C1C=CCCCCC1");
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testCycloOctadien() throws Exception {
		String filename = "data/mdl/cyclooctadien.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		Molecule mol1 = (Molecule) reader.read(new Molecule());
		SmilesGenerator sg = new SmilesGenerator();
		String moleculeSmile = sg.createSMILES(mol1);
		assertEquals(moleculeSmile, "C=1CCC=CCCC=1");
	}


	/**
	 * @cdk.bug 1089770
	 */
	public void testSFBug1089770_1() throws Exception {
		String filename = "data/mdl/bug1089770-1.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		Molecule mol1 = (Molecule) reader.read(new Molecule());
		SmilesGenerator sg = new SmilesGenerator();
		String moleculeSmile = sg.createSMILES(mol1);
		//logger.debug(filename + " -> " + moleculeSmile);
		assertEquals(moleculeSmile, "C1CCC=2CCCC=2(C1)");
	}

	/**
	 * @cdk.bug 1089770
	 */
	public void testSFBug1089770_2() throws Exception {
		String filename = "data/mdl/bug1089770-2.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		Molecule mol1 = (Molecule) reader.read(new Molecule());
		SmilesGenerator sg = new SmilesGenerator();
		String moleculeSmile = sg.createSMILES(mol1);
		//logger.debug(filename + " -> " + moleculeSmile);
		assertEquals(moleculeSmile, "C=1CCC=CCCC=1");
	}
	
	/**
	 * @cdk.bug 1014344
	 */
	public void testSFBug1014344() throws Exception {
		String filename = "data/mdl/bug1014344-1.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLReader reader = new MDLReader(ins, Mode.STRICT);
		Molecule mol1 = (Molecule) reader.read(new Molecule());
		addImplicitHydrogens(mol1);
		SmilesGenerator sg = new SmilesGenerator();
		String molSmiles = sg.createSMILES(mol1);
		StringWriter output=new StringWriter();
		CMLWriter cmlWriter = new CMLWriter(output);
        cmlWriter.write(mol1);
        CMLReader cmlreader=new CMLReader(new ByteArrayInputStream(output.toString().getBytes()));
        IAtomContainer mol2=((IChemFile)cmlreader.read(new ChemFile())).getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        addImplicitHydrogens(mol2);
        String cmlSmiles = sg.createSMILES(new Molecule(mol2));
        assertEquals(molSmiles,cmlSmiles);        
	}

	/**
	 * @cdk.bug 1014344
	 */
	public void testTest() throws Exception {
		String filename_cml = "data/mdl/9554.mol";
		String filename_mol = "data/mdl/9553.mol";
		InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename_cml);
		InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(filename_mol);
		MDLV2000Reader reader1 = new MDLV2000Reader(ins1, Mode.STRICT);
        Molecule mol1 = (Molecule) reader1.read(new Molecule());
        addExplicitHydrogens(mol1);
        StructureDiagramGenerator sdg=new StructureDiagramGenerator(mol1);
        sdg.generateCoordinates();
		
        MDLV2000Reader reader2 = new MDLV2000Reader(ins2, Mode.STRICT);		
		Molecule mol2 = (Molecule) reader2.read(new Molecule());
		addExplicitHydrogens(mol2);
        sdg=new StructureDiagramGenerator(mol2);
        sdg.generateCoordinates();
		
		SmilesGenerator sg = new SmilesGenerator();
		
		String moleculeSmile1 = sg.createChiralSMILES(mol1, new boolean[mol1.getBondCount()]);
		String moleculeSmile2 = sg.createChiralSMILES(mol2, new boolean[mol2.getBondCount()]);
		assertFalse(moleculeSmile1.equals(moleculeSmile2));
	}
	
	
	/**
	 * @cdk.bug 1014344
	 */
	public void testSFBug1014344_1() throws Exception {
		String filename_cml = "data/cml/bug1014344-1.cml";
		String filename_mol = "data/mdl/bug1014344-1.mol";
		InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename_cml);
		InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(filename_mol);
		CMLReader reader1 = new CMLReader(ins1);
        IChemFile chemFile = (IChemFile)reader1.read(new ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IMolecule mol1 = model.getMoleculeSet().getMolecule(0);
		
		MDLReader reader2 = new MDLReader(ins2);		
		Molecule mol2 = (Molecule) reader2.read(new Molecule());
		
		SmilesGenerator sg = new SmilesGenerator();
		
		String moleculeSmile1 = sg.createSMILES(mol1);
//		logger.debug(filename_cml + " -> " + moleculeSmile1);
		String moleculeSmile2 = sg.createSMILES(mol2);
//		logger.debug(filename_mol + " -> " + moleculeSmile2);
		assertEquals(moleculeSmile1, moleculeSmile2);
	}

	/**
	 * @cdk.bug 1875946
	 */
	public void testPreservingFormalCharge() throws Exception {
		IMolecule mol = new Molecule();
		mol.addAtom(new Atom(Elements.OXYGEN));
		mol.getAtom(0).setFormalCharge(-1);
		mol.addAtom(new Atom(Elements.CARBON));
		mol.addBond(0,1,IBond.Order.SINGLE);
		SmilesGenerator generator = new SmilesGenerator();
		generator.createSMILES(new Molecule(mol));
		assertEquals(-1, mol.getAtom(0).getFormalCharge().intValue());
		// mmm, that does not reproduce the bug findings yet :(
	}

    public void testIndole() throws Exception {
        IMolecule mol = MoleculeFactory.makeIndole();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);

        SmilesGenerator smilesGenerator = new SmilesGenerator();
        smilesGenerator.setUseAromaticityFlag(true);
        String smiles = smilesGenerator.createSMILES(mol);
        assertTrue( smiles.indexOf("[nH]") >= 0);
    }

    public void testPyrrole() throws Exception {
        IMolecule mol = MoleculeFactory.makePyrrole();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);

        SmilesGenerator smilesGenerator = new SmilesGenerator();
        smilesGenerator.setUseAromaticityFlag(true);
        String smiles = smilesGenerator.createSMILES(mol);
        assertTrue(smiles.indexOf("[nH]") >= 0);
    }
    
    /**
     * @cdk.bug 2051597
     */
    public void testSFBug2051597() throws Exception {
        String smiles = "c1(c2ccc(c8ccccc8)cc2)" +
        "c(c3ccc(c9ccccc9)cc3)" +
        "c(c4ccc(c%10ccccc%10)cc4)" +
        "c(c5ccc(c%11ccccc%11)cc5)" +
        "c(c6ccc(c%12ccccc%12)cc6)" +
        "c1(c7ccc(c%13ccccc%13)cc7)";
        SmilesParser smilesParser = new SmilesParser(
                DefaultChemObjectBuilder.getInstance());
        IMolecule cdkMol = smilesParser.parseSmiles(smiles);
        SmilesGenerator smilesGenerator = new SmilesGenerator();
        smilesGenerator.setUseAromaticityFlag(true);    
        String genSmiles = smilesGenerator.createSMILES(cdkMol);

        // check that we have the appropriate ring closure symbols
        assertTrue( "There were'nt any % ring closures in the output", genSmiles.indexOf("%") >= 0);
        assertTrue(genSmiles.indexOf("%10") >= 0);
        assertTrue(genSmiles.indexOf("%11") >= 0);
        assertTrue(genSmiles.indexOf("%12") >= 0);
        assertTrue(genSmiles.indexOf("%13") >= 0);

        // check that we can read in the SMILES we got
        IMolecule cdkRoundTripMol 
            = smilesParser.parseSmiles(genSmiles);
        assertTrue(cdkRoundTripMol != null);
    }
}

