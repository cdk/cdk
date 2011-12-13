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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.vecmath.Point2d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.graph.AtomContainerBondPermutor;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @author         steinbeck
 * @cdk.created    2004-02-09
 * @cdk.module     test-smiles
 */
public class SmilesGeneratorTest extends CDKTestCase {


	/**
	 *  A unit test for JUnit
	 */
	@Test
    public void testSmilesGenerator()
	{
	    IAtomContainer mol2 = MoleculeFactory.makeAlphaPinene();
		SmilesGenerator sg = new SmilesGenerator();
		fixCarbonHCount(mol2);
		String smiles2 = sg.createSMILES(mol2);
		Assert.assertNotNull(smiles2);
		Assert.assertEquals("C1=C(C)C2CC(C1)C2(C)(C)", smiles2);
	}


	/**
	 *  A unit test for JUnit
	 */
	@Test public void testEthylPropylPhenantren()
	{
	    IAtomContainer mol1 = MoleculeFactory.makeEthylPropylPhenantren();
        SmilesGenerator sg = new SmilesGenerator();
		fixCarbonHCount(mol1);
		String smiles1 = sg.createSMILES(mol1);
		Assert.assertNotNull(smiles1);
		Assert.assertEquals("C=2C=C1C=3C=CC(=CC=3(C=CC1=C(C=2)CC))CCC", smiles1);
	}

	
	
	/**
	 *  A unit test for JUnit
	 */
	@Test public void testPropylCycloPropane()
	{
	    IAtomContainer mol1 = MoleculeFactory.makePropylCycloPropane();
        SmilesGenerator sg = new SmilesGenerator();
		fixCarbonHCount(mol1);
		String smiles1 = sg.createSMILES(mol1);
		Assert.assertNotNull(smiles1);
		Assert.assertEquals("CCCC1CC1", smiles1);
	}
	
	

	/**
	 *  A unit test for JUnit
	 *
	 */
	@Test public void testAlanin() throws Exception
	{
		HydrogenPlacer hydrogenPlacer = new HydrogenPlacer();
        IAtomContainer mol1 = new AtomContainer();
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
		mol1.addBond(1, 2, IBond.Order.SINGLE, IBond.Stereo.UP);
		// 2
		mol1.addBond(1, 3, IBond.Order.SINGLE, IBond.Stereo.DOWN);
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
		Assert.assertNotNull(smiles1);
		Assert.assertEquals("[H]OC(=O)[C@](F)(N([H])[H])C([H])([H])[H]", smiles1);
		
		//by setting additional stereo descriptors, we should get another smiles
		mol1.getBond(1).setStereo(IBond.Stereo.DOWN);
		mol1.getBond(2).setStereo(IBond.Stereo.UP);
		smiles1 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		Assert.assertNotNull(smiles1);
		Assert.assertEquals("[H]OC(=O)[C@](F)(C([H])([H])[H])N([H])[H]", smiles1);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	@Test public void testCisResorcinol() throws Exception
	{
		HydrogenPlacer hydrogenPlacer = new HydrogenPlacer();
        IAtomContainer mol1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
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
		mol1.addBond(0, 2, IBond.Order.SINGLE, IBond.Stereo.DOWN);
		// 1
		mol1.addBond(1, 2, IBond.Order.SINGLE, IBond.Stereo.UP);
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
		mol1.addBond(7, 8, IBond.Order.SINGLE, IBond.Stereo.UP);
		// 4
		mol1.addBond(7, 9, IBond.Order.SINGLE, IBond.Stereo.DOWN);
		// 5
		mol1.addBond(7, 2, IBond.Order.SINGLE);
		// 6
		addExplicitHydrogens(mol1);
		hydrogenPlacer.placeHydrogens2D(mol1, 1.0);
		IsotopeFactory ifac = IsotopeFactory.getInstance(mol1.getBuilder());
		ifac.configureAtoms(mol1);
		String smiles1 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		Assert.assertNotNull(smiles1);
		Assert.assertEquals("[H]O[C@]1(C([H])([H])C([H])([H])C([H])([H])C([H])([H])[C@]1(O[H])([H]))([H])", smiles1);
		mol1 = AtomContainerManipulator.removeHydrogens(mol1);
		smiles1 = sg.createSMILES(mol1);
		Assert.assertNotNull(smiles1);
		Assert.assertEquals("OC1CCCCC1(O)", smiles1);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	@Test public void testCisTransDecalin() throws Exception
	{
		HydrogenPlacer hydrogenPlacer = new HydrogenPlacer();
        IAtomContainer mol1 = new AtomContainer();
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
		mol1.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.DOWN);
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
		mol1.addBond(6, 7, IBond.Order.SINGLE, IBond.Stereo.DOWN);
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
		Assert.assertNotNull(smiles1);
		Assert.assertEquals("[H]C1([H])(C([H])([H])C([H])([H])C\\2([H])(C([H])([H])C([H])([H])C([H])([H])C([H])([H])C\\2([H])(C1([H])([H]))))", smiles1);
		mol1.getBond(6).setStereo(IBond.Stereo.UP);
		String smiles3 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		Assert.assertNotSame(smiles1, smiles3);
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception  Description of the Exception
	 */
	@Test public void testDoubleBondConfiguration() throws Exception
	{
		HydrogenPlacer hydrogenPlacer = new HydrogenPlacer();
		IAtomContainer mol1 = new AtomContainer();
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
		Assert.assertNotNull(smiles1);
		Assert.assertEquals("F/C(=C/(F)S)S", smiles1);
		mol1.getAtom(4).setPoint2d(new Point2d(0, 3));
		mol1.getAtom(5).setPoint2d(new Point2d(2, 3));
		
		smiles1 = sg.createSMILES(mol1, true, bool);
		Assert.assertNotNull(smiles1);
		Assert.assertEquals("F/C(=C\\(F)S)S", smiles1);

		addExplicitHydrogens(mol1);
		hydrogenPlacer.placeHydrogens2D(mol1, 1.0);
		bool = new boolean[mol1.getBondCount()];
		bool[2] = true;
		smiles1 = sg.createSMILES(mol1, true, bool);
		Assert.assertNotNull(smiles1);
		Assert.assertEquals("[H]S/C(F)=C/(F)S[H]", smiles1);
		
		mol1.getAtom(5).setPoint2d(new Point2d(0, 3));
		mol1.getAtom(4).setPoint2d(new Point2d(2, 3));
		smiles1 = sg.createSMILES(mol1, true, bool);
		Assert.assertNotNull(smiles1);
		Assert.assertEquals("[H]S/C(F)=C\\(F)S[H]", smiles1);
	}


	/**
	 *  A unit test for JUnit
	 */
	@Test public void testPartitioning()
	{
		String smiles = "";
		IAtomContainer molecule = new AtomContainer();
        SmilesGenerator sg = new SmilesGenerator();
		Atom sodium = new Atom("Na");
		sodium.setFormalCharge(+1);
		Atom hydroxyl = new Atom("O");
		hydroxyl.setImplicitHydrogenCount(1);
		hydroxyl.setFormalCharge(-1);
		molecule.addAtom(sodium);
		molecule.addAtom(hydroxyl);
		smiles = sg.createSMILES(molecule);
		Assert.assertTrue(smiles.indexOf(".") != -1);
	}


	/**
	 * @cdk.bug 791091
	 */
	@Test public void testBug791091()
	{
		String smiles = "";
		IAtomContainer molecule = new AtomContainer();
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
		Assert.assertEquals("N1(C)CCC1", smiles);
	}

	/**
	 * @cdk.bug 590236
	 */
	@Test public void testBug590236()
	{
		String smiles = "";
		IAtomContainer molecule = new AtomContainer();
        SmilesGenerator sg = new SmilesGenerator();
		molecule.addAtom(new Atom("C"));
		Atom carbon2 = new Atom("C");
		carbon2.setMassNumber(13);
		molecule.addAtom(carbon2);
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		fixCarbonHCount(molecule);
		smiles = sg.createSMILES(molecule);
		Assert.assertEquals("C[13C]", smiles);
	}

	/**
	 * A bug reported for JChemPaint.
	 * 
	 * @cdk.bug 956923
	 */
	@Test public void testSFBug956923() throws Exception
	{
		String smiles = "";
		IAtomContainer molecule = new AtomContainer();
        SmilesGenerator sg = new SmilesGenerator();
        sg.setUseAromaticityFlag(true);
		Atom sp2CarbonWithOneHydrogen = new Atom("C");
		sp2CarbonWithOneHydrogen.setHybridization(IAtomType.Hybridization.SP2);
		sp2CarbonWithOneHydrogen.setImplicitHydrogenCount(1);
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
		Assert.assertEquals("c1ccccc1", smiles);
		sg.setUseAromaticityFlag(false);
		smiles = sg.createSMILES(molecule);
		Assert.assertEquals("C1CCCCC1", smiles);
	}


	/**
	 *  A unit test for JUnit
	 */
	@Test public void testAtomPermutation()
	{
		IAtomContainer mol = new AtomContainer();
		mol.addAtom(new Atom("S"));
		mol.addAtom(new Atom("O"));
		mol.addAtom(new Atom("O"));
		mol.addAtom(new Atom("O"));
		mol.addAtom(new Atom("O"));
		mol.addBond(0, 1, IBond.Order.DOUBLE);
		mol.addBond(0, 2, IBond.Order.DOUBLE);
		mol.addBond(0, 3, IBond.Order.SINGLE);
		mol.addBond(0, 4, IBond.Order.SINGLE);
		mol.getAtom(3).setImplicitHydrogenCount(1);
		mol.getAtom(4).setImplicitHydrogenCount(1);
		AtomContainerAtomPermutor acap = new AtomContainerAtomPermutor(mol);
		SmilesGenerator sg = new SmilesGenerator();
		String smiles = "";
		String oldSmiles = sg.createSMILES(mol);
		while (acap.hasNext())
		{
			smiles = sg.createSMILES(new AtomContainer((AtomContainer) acap.next()));
			//logger.debug(smiles);
			Assert.assertEquals(oldSmiles, smiles);
		}

	}


	/**
	 *  A unit test for JUnit
	 */
	@Test public void testBondPermutation()
	{
		IAtomContainer mol = new AtomContainer();
		mol.addAtom(new Atom("S"));
		mol.addAtom(new Atom("O"));
		mol.addAtom(new Atom("O"));
		mol.addAtom(new Atom("O"));
		mol.addAtom(new Atom("O"));
		mol.addBond(0, 1, IBond.Order.DOUBLE);
		mol.addBond(0, 2, IBond.Order.DOUBLE);
		mol.addBond(0, 3, IBond.Order.SINGLE);
		mol.addBond(0, 4, IBond.Order.SINGLE);
		mol.getAtom(3).setImplicitHydrogenCount(1);
		mol.getAtom(4).setImplicitHydrogenCount(1);
		AtomContainerBondPermutor acbp = new AtomContainerBondPermutor(mol);
		SmilesGenerator sg = new SmilesGenerator();
		String smiles = "";
		String oldSmiles = sg.createSMILES(mol);
		while (acbp.hasNext())
		{
			smiles = sg.createSMILES(new AtomContainer((AtomContainer) acbp.next()));
			//logger.debug(smiles);
			Assert.assertEquals(oldSmiles, smiles);
		}

	}

	private void fixCarbonHCount(IAtomContainer mol) {
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
				atom.setImplicitHydrogenCount(4 - correction);
			} else if (atom.getSymbol().equals("N")) {
				atom.setImplicitHydrogenCount(3 - correction);
			}
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	@Test public void testPseudoAtom()
	{
		IAtom atom = new PseudoAtom("Star");
		SmilesGenerator sg = new SmilesGenerator();
		String smiles = "";
		IAtomContainer molecule = new AtomContainer();
		molecule.addAtom(atom);
		smiles = sg.createSMILES(molecule);
		Assert.assertEquals("[*]", smiles);
	}


	/**
	 *  Test generation of a reaction SMILES. I know, it's a stupid alchemic
	 *  reaction, but it serves its purpose.
	 */
	@Test public void testReactionSMILES() throws Exception {
		Reaction reaction = new Reaction();
		AtomContainer methane = new AtomContainer();
		methane.addAtom(new Atom("C"));
		reaction.addReactant(methane);
		IAtomContainer magic = new AtomContainer();
		magic.addAtom(new PseudoAtom("magic"));
		reaction.addAgent(magic);
		IAtomContainer gold = new AtomContainer();
		gold.addAtom(new Atom("Au"));
		reaction.addProduct(gold);

		SmilesGenerator sg = new SmilesGenerator();
		String smiles = sg.createSMILES(reaction);
		//logger.debug("Generated SMILES: " + smiles);
		Assert.assertEquals("C>[*]>[Au]", smiles);
	}


	/**
	 *  Test generation of a D and L alanin.
	 */
	@Test public void testAlaSMILES() throws Exception {
		String filename = "data/mdl/l-ala.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		IAtomContainer mol1 = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
		addExplicitHydrogens(mol1);
		new HydrogenPlacer().placeHydrogens2D(mol1, 1.0);
		filename = "data/mdl/d-ala.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLV2000Reader(ins, Mode.STRICT);
		IAtomContainer mol2 = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
		addExplicitHydrogens(mol2);
		new HydrogenPlacer().placeHydrogens2D(mol2, 1.0);
		SmilesGenerator sg = new SmilesGenerator();
		String smiles1 = sg.createChiralSMILES(mol1, new boolean[20]);
		String smiles2 = sg.createChiralSMILES(mol2, new boolean[20]);
		Assert.assertNotSame(smiles1, smiles2);
	}


	/**
	 *  Test some sugars
	 */
	@Test public void testSugarSMILES() throws Exception {
		String filename = "data/mdl/D-mannose.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		IAtomContainer mol1 = reader.read(new AtomContainer());
		new HydrogenPlacer().placeHydrogens2D(mol1, 1.0);
		filename = "data/mdl/D+-glucose.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLV2000Reader(ins, Mode.STRICT);
		IAtomContainer mol2 = reader.read(new AtomContainer());
		new HydrogenPlacer().placeHydrogens2D(mol2, 1.0);
		SmilesGenerator sg = new SmilesGenerator();
		String smiles1 = sg.createChiralSMILES(mol1, new boolean[20]);
		String smiles2 = sg.createChiralSMILES(mol2, new boolean[20]);
		Assert.assertNotSame(smiles1, smiles2);
	}

	/**
	 *  Test for some rings where the double bond is broken
	 */
	@Test public void testCycloOctan() throws Exception {
		String filename = "data/mdl/cyclooctan.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		IAtomContainer mol1 = reader.read(new AtomContainer());
		SmilesGenerator sg = new SmilesGenerator();
		String moleculeSmile = sg.createSMILES(mol1);
		Assert.assertEquals(moleculeSmile, "C1=CCCCCCC1");
	}


	/**
	 *  A unit test for JUnit
	 */
	@Test public void testCycloOcten() throws Exception {
		String filename = "data/mdl/cycloocten.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		IAtomContainer mol1 = reader.read(new AtomContainer());
		SmilesGenerator sg = new SmilesGenerator();
		String moleculeSmile = sg.createSMILES(mol1);
		Assert.assertEquals(moleculeSmile, "C1C=CCCCCC1");
	}


	/**
	 *  A unit test for JUnit
	 */
	@Test public void testCycloOctadien() throws Exception {
		String filename = "data/mdl/cyclooctadien.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		IAtomContainer mol1 = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
		SmilesGenerator sg = new SmilesGenerator();
		String moleculeSmile = sg.createSMILES(mol1);
		Assert.assertEquals(moleculeSmile, "C=1CCC=CCCC=1");
	}


	/**
	 * @cdk.bug 1089770
	 */
	@Test public void testSFBug1089770_1() throws Exception {
		String filename = "data/mdl/bug1089770-1.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		IAtomContainer mol1 = reader.read(new AtomContainer());
		SmilesGenerator sg = new SmilesGenerator();
		String moleculeSmile = sg.createSMILES(mol1);
		//logger.debug(filename + " -> " + moleculeSmile);
		Assert.assertEquals(moleculeSmile, "C1CCC=2CCCC=2(C1)");
	}

	/**
	 * @cdk.bug 1089770
	 */
	@Test public void testSFBug1089770_2() throws Exception {
		String filename = "data/mdl/bug1089770-2.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		IAtomContainer mol1 = reader.read(new AtomContainer());
		SmilesGenerator sg = new SmilesGenerator();
		String moleculeSmile = sg.createSMILES(mol1);
		//logger.debug(filename + " -> " + moleculeSmile);
		Assert.assertEquals(moleculeSmile, "C=1CCC=CCCC=1");
	}
	
	/**
	 * @cdk.bug 1014344
	 */
	@Test public void testSFBug1014344() throws Exception {
		String filename = "data/mdl/bug1014344-1.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLReader reader = new MDLReader(ins, Mode.STRICT);
		IAtomContainer mol1 = reader.read(new AtomContainer());
		addImplicitHydrogens(mol1);
		SmilesGenerator sg = new SmilesGenerator();
		String molSmiles = sg.createSMILES(mol1);
		StringWriter output=new StringWriter();
		CMLWriter cmlWriter = new CMLWriter(output);
        cmlWriter.write(mol1);
        CMLReader cmlreader=new CMLReader(new ByteArrayInputStream(output.toString().getBytes()));
        IAtomContainer mol2=((IChemFile)cmlreader.read(new ChemFile())).getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        addImplicitHydrogens(mol2);
        String cmlSmiles = sg.createSMILES(new AtomContainer(mol2));
        Assert.assertEquals(molSmiles,cmlSmiles);
	}

	/**
	 * @cdk.bug 1014344
	 */
	@Test public void testTest() throws Exception {
		String filename_cml = "data/mdl/9554.mol";
		String filename_mol = "data/mdl/9553.mol";
		InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename_cml);
		InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(filename_mol);
		MDLV2000Reader reader1 = new MDLV2000Reader(ins1, Mode.STRICT);
        IAtomContainer mol1 = reader1.read(new AtomContainer());
        addExplicitHydrogens(mol1);
        StructureDiagramGenerator sdg=new StructureDiagramGenerator(mol1);
        sdg.generateCoordinates();
		
        MDLV2000Reader reader2 = new MDLV2000Reader(ins2, Mode.STRICT);		
		IAtomContainer mol2 = reader2.read(new AtomContainer());
		addExplicitHydrogens(mol2);
        sdg=new StructureDiagramGenerator(mol2);
        sdg.generateCoordinates();
		
		SmilesGenerator sg = new SmilesGenerator();
		
		String moleculeSmile1 = sg.createChiralSMILES(mol1, new boolean[mol1.getBondCount()]);
		String moleculeSmile2 = sg.createChiralSMILES(mol2, new boolean[mol2.getBondCount()]);
		Assert.assertNotSame(moleculeSmile2, moleculeSmile1);
	}
	
  /**
   * @cdk.bug 1535055
   */
	@Test public void testSFBug1535055() throws Exception {
	    String filename_cml = "data/cml/test1.cml";
	    InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename_cml);
	    CMLReader reader1 = new CMLReader(ins1);
	    IChemFile chemFile = (IChemFile)reader1.read(new ChemFile());
	    Assert.assertNotNull(chemFile);
	    IChemSequence seq = chemFile.getChemSequence(0);
	    Assert.assertNotNull(seq);
	    IChemModel model = seq.getChemModel(0);
	    Assert.assertNotNull(model);
	    IAtomContainer mol1 = model.getMoleculeSet().getAtomContainer(0);
	    Assert.assertNotNull(mol1);
	    
	    AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
	    Assert.assertTrue(CDKHueckelAromaticityDetector.detectAromaticity(mol1));
	    
	    SmilesGenerator sg = new SmilesGenerator();
      sg.setUseAromaticityFlag(true);
	    String mol1SMILES = sg.createSMILES(mol1);
	    Assert.assertTrue(mol1SMILES.contains("nH"));
	}
	
	/**
	 * @cdk.bug 1014344
	 */
	@Test public void testSFBug1014344_1() throws Exception {
		String filename_cml = "data/cml/bug1014344-1.cml";
		String filename_mol = "data/mdl/bug1014344-1.mol";
		InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename_cml);
		InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(filename_mol);
		CMLReader reader1 = new CMLReader(ins1);
        IChemFile chemFile = (IChemFile)reader1.read(new ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainer mol1 = model.getMoleculeSet().getAtomContainer(0);
		
		MDLReader reader2 = new MDLReader(ins2);		
		IAtomContainer mol2 = reader2.read(new AtomContainer());
		
		SmilesGenerator sg = new SmilesGenerator();
		
		String moleculeSmile1 = sg.createSMILES(mol1);
//		logger.debug(filename_cml + " -> " + moleculeSmile1);
		String moleculeSmile2 = sg.createSMILES(mol2);
//		logger.debug(filename_mol + " -> " + moleculeSmile2);
		Assert.assertEquals(moleculeSmile1, moleculeSmile2);
	}

	/**
	 * @cdk.bug 1875946
	 */
	@Test public void testPreservingFormalCharge() throws Exception {
		AtomContainer mol = new AtomContainer();
		mol.addAtom(new Atom(Elements.OXYGEN));
		mol.getAtom(0).setFormalCharge(-1);
		mol.addAtom(new Atom(Elements.CARBON));
		mol.addBond(0,1,IBond.Order.SINGLE);
		SmilesGenerator generator = new SmilesGenerator();
		generator.createSMILES(new AtomContainer(mol));
		Assert.assertEquals(-1, mol.getAtom(0).getFormalCharge().intValue());
		// mmm, that does not reproduce the bug findings yet :(
	}

    @Test public void testIndole() throws Exception {
        IAtomContainer mol = MoleculeFactory.makeIndole();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);

        SmilesGenerator smilesGenerator = new SmilesGenerator();
        smilesGenerator.setUseAromaticityFlag(true);
        String smiles = smilesGenerator.createSMILES(mol);
        Assert.assertTrue( smiles.indexOf("[nH]") >= 0);
    }

    @Test public void testPyrrole() throws Exception {
        IAtomContainer mol = MoleculeFactory.makePyrrole();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);

        SmilesGenerator smilesGenerator = new SmilesGenerator();
        smilesGenerator.setUseAromaticityFlag(true);
        String smiles = smilesGenerator.createSMILES(mol);
        Assert.assertTrue(smiles.indexOf("[nH]") >= 0);
    }
    
    /**
     * @cdk.bug 2051597
     */
    @Test public void testSFBug2051597() throws Exception {
        String smiles = "c1(c2ccc(c8ccccc8)cc2)" +
        "c(c3ccc(c9ccccc9)cc3)" +
        "c(c4ccc(c%10ccccc%10)cc4)" +
        "c(c5ccc(c%11ccccc%11)cc5)" +
        "c(c6ccc(c%12ccccc%12)cc6)" +
        "c1(c7ccc(c%13ccccc%13)cc7)";
        SmilesParser smilesParser = new SmilesParser(
                DefaultChemObjectBuilder.getInstance());
        IAtomContainer cdkMol = smilesParser.parseSmiles(smiles);
        SmilesGenerator smilesGenerator = new SmilesGenerator();
        smilesGenerator.setUseAromaticityFlag(true);    
        String genSmiles = smilesGenerator.createSMILES(cdkMol);

        // check that we have the appropriate ring closure symbols
        Assert.assertTrue( "There were'nt any % ring closures in the output", genSmiles.indexOf("%") >= 0);
        Assert.assertTrue(genSmiles.indexOf("%10") >= 0);
        Assert.assertTrue(genSmiles.indexOf("%11") >= 0);
        Assert.assertTrue(genSmiles.indexOf("%12") >= 0);
        Assert.assertTrue(genSmiles.indexOf("%13") >= 0);

        // check that we can read in the SMILES we got
        IAtomContainer cdkRoundTripMol 
            = smilesParser.parseSmiles(genSmiles);
        Assert.assertNotNull(cdkRoundTripMol);
    }

    /**
     * @cdk.bug 2596061
     */
    @Test
    public void testRoundTripPseudoAtom() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smiles = "[12*H2-]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        SmilesGenerator smilesGenerator = new SmilesGenerator();
        smilesGenerator.setUseAromaticityFlag(true);
        String genSmiles = smilesGenerator.createSMILES(mol);
        Assert.assertEquals(smiles, genSmiles);
    }

    /**
     * @cdk.bug 2781199
     */
    @Test
    public void testBug2781199() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smiles = "n1ncn(c1)CC";
        IAtomContainer mol = sp.parseSmiles(smiles);
        SmilesGenerator smilesGenerator = new SmilesGenerator();
        smilesGenerator.setUseAromaticityFlag(true);
        String genSmiles = smilesGenerator.createSMILES(mol);
        Assert.assertTrue("Generated SMILES should not have explicit H: "+genSmiles, genSmiles.indexOf("H") == -1);
    }

    /**
     * @cdk.bug 2898032
     */
    @Test
    public void testCanSmiWithoutConfiguredAtoms() throws CDKException, IOException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String s1 = "OC(=O)C(Br)(Cl)N";
        String s2 = "ClC(Br)(N)C(=O)O";

        IAtomContainer m1 = sp.parseSmiles(s1);
        IAtomContainer m2 = sp.parseSmiles(s2);

        SmilesGenerator sg = new SmilesGenerator();
        String o1 = sg.createSMILES(m1);
        String o2 = sg.createSMILES(m2);

        Assert.assertFalse("The two canonical SMILES should not match",o1.equals(o2));
    }

    /**
     * @cdk.bug 2898032
     */
    @Test
    public void testCanSmiWithConfiguredAtoms() throws CDKException, IOException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String s1 = "OC(=O)C(Br)(Cl)N";
        String s2 = "ClC(Br)(N)C(=O)O";

        IAtomContainer m1 = sp.parseSmiles(s1);
        IAtomContainer m2 = sp.parseSmiles(s2);

        IsotopeFactory fact = IsotopeFactory.getInstance(DefaultChemObjectBuilder.getInstance());
        fact.configureAtoms(m1);
        fact.configureAtoms(m2);

        SmilesGenerator sg = new SmilesGenerator();
        String o1 = sg.createSMILES(m1);
        String o2 = sg.createSMILES(m2);

        Assert.assertTrue("The two canonical SMILES should match",o1.equals(o2));
    }
    
    /**
     * @cdk.bug 3040273
     */
    @Test 
    public void testBug3040273() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String testSmiles = "C1(C(C(C(C(C1Br)Br)Br)Br)Br)Br";
        IAtomContainer mol = sp.parseSmiles(testSmiles);
        IsotopeFactory fact = IsotopeFactory.getInstance(DefaultChemObjectBuilder.getInstance());
        fact.configureAtoms(mol);
        SmilesGenerator sg = new SmilesGenerator();
        String smiles = sg.createSMILES((IAtomContainer) mol);
        IAtomContainer mol2 = sp.parseSmiles(smiles);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(mol, mol2));
    }

    
    @Test public void testCreateSMILESWithoutCheckForMultipleMolecules_withDetectAromaticity() throws CDKException{
        IAtomContainer benzene = MoleculeFactory.makeBenzene();
        SmilesGenerator sg = new SmilesGenerator(false);
        String smileswithoutaromaticity = sg.createSMILESWithoutCheckForMultipleMolecules(benzene, false, new boolean[benzene.getBondCount()]);
        Assert.assertEquals("C=1C=CC=CC=1", smileswithoutaromaticity);
    }

    @Test public void testCreateSMILESWithoutCheckForMultipleMolecules_withoutDetectAromaticity() throws CDKException{
        IAtomContainer benzene = MoleculeFactory.makeBenzene();
        SmilesGenerator sg = new SmilesGenerator(true);
        String smileswitharomaticity = sg.createSMILESWithoutCheckForMultipleMolecules(benzene, false, new boolean[benzene.getBondCount()]);
        Assert.assertEquals("c1ccccc1", smileswitharomaticity);
    }
}

