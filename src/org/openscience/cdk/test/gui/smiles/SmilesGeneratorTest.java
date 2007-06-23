/* $Revision: 6067 $ $Author: egonw $ $Date: 2006-04-21 10:59:31 +0200 (Fri, 21 Apr 2006) $
 *
 *  Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
package org.openscience.cdk.test.gui.smiles;

import java.io.IOException;
import java.io.InputStream;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.graph.AtomContainerBondPermutor;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *@author         steinbeck
 *@cdk.created    February 9, 2004
 *@cdk.module     test-gui-smiles
 */
public class SmilesGeneratorTest extends CDKTestCase
{
	boolean standAlone = false;

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
	 *  Sets the standAlone attribute of the SmilesGeneratorTest object
	 *
	 *@param  standAlone  The new standAlone value
	 */
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testSmilesGenerator()
	{
        Molecule mol2 = MoleculeFactory.makeAlphaPinene();
		SmilesGenerator sg = new SmilesGenerator();
		fixCarbonHCount(mol2);
		String smiles2 = null;
		if (standAlone)
		{
			display(mol2);
		}
		try
		{
			smiles2 = sg.createSMILES(mol2);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES 2: " + smiles2);
		}
		assertNotNull(smiles2);
		assertTrue(smiles2.equals("C1=C(C)C2CC(C1)C2(C)(C)"));
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testEthylPropylPhenantren()
	{
		Molecule mol1 = MoleculeFactory.makeEthylPropylPhenantren();
        SmilesGenerator sg = new SmilesGenerator();
		fixCarbonHCount(mol1);
		String smiles1 = null;
		if (standAlone)
		{
			display(mol1);
		}
		try
		{
			smiles1 = sg.createSMILES(mol1);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES 1: " + smiles1);
		}
		assertNotNull(smiles1);
		assertTrue(smiles1.equals("c2cc1c3ccc(cc3(ccc1c(c2)CC))CCC"));
	}

	
	
	/**
	 *  A unit test for JUnit
	 */
	public void testPropylCycloPropane()
	{
		Molecule mol1 = MoleculeFactory.makePropylCycloPropane();
        SmilesGenerator sg = new SmilesGenerator();
		fixCarbonHCount(mol1);
		String smiles1 = null;
		if (standAlone)
		{
			display(mol1);
		}
		try
		{
			smiles1 = sg.createSMILES(mol1);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES 1: " + smiles1);
		}
		assertNotNull(smiles1);
		assertTrue(smiles1.equals("CCCC1CC1"));
	}
	
	

	/**
	 *  A unit test for JUnit
	 *
	 */
	public void testAlanin()
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
		mol1.addBond(0, 1, 1);
		// 1
		mol1.addBond(1, 2, 1, CDKConstants.STEREO_BOND_UP);
		// 2
		mol1.addBond(1, 3, 1, CDKConstants.STEREO_BOND_DOWN);
		// 3
		mol1.addBond(1, 4, 1);
		// 4
		mol1.addBond(4, 5, 1);
		// 5
		mol1.addBond(4, 6, 2);
		// 6
		try
		{
			new HydrogenAdder().addHydrogensToSatisfyValency(mol1);
			hydrogenPlacer.placeHydrogens2D(mol1, 1.0);
			IsotopeFactory ifac = IsotopeFactory.getInstance(mol1.getBuilder());
			ifac.configureAtoms(mol1);
		} catch (Exception ex)
		{
			fail();
		}

		String smiles1 = null;
		if (standAlone)
		{
			display(mol1);
		}
		try
		{
			smiles1 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES 1: " + smiles1);
		}
		assertNotNull(smiles1);
		assertTrue(smiles1.equals("[H]OC(=O)[C@](F)(N([H])[H])C([H])([H])[H]"));
		mol1.getBond(1).setStereo(CDKConstants.STEREO_BOND_DOWN);
		mol1.getBond(2).setStereo(CDKConstants.STEREO_BOND_UP);
		try
		{
			smiles1 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES 1: " + smiles1);
		}
		assertNotNull(smiles1);
		assertTrue(smiles1.equals("[H]OC(=O)[C@](F)(C([H])([H])[H])N([H])[H]"));
	}


	/**
	 *  A unit test for JUnit
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
		mol1.addBond(0, 2, 1, CDKConstants.STEREO_BOND_DOWN);
		// 1
		mol1.addBond(1, 2, 1, CDKConstants.STEREO_BOND_UP);
		// 2
		mol1.addBond(2, 3, 1);
		// 3
		mol1.addBond(3, 4, 1);
		// 4
		mol1.addBond(4, 5, 1);
		// 5
		mol1.addBond(5, 6, 1);
		// 6
		mol1.addBond(6, 7, 1);
		// 3
		mol1.addBond(7, 8, 1, CDKConstants.STEREO_BOND_UP);
		// 4
		mol1.addBond(7, 9, 1, CDKConstants.STEREO_BOND_DOWN);
		// 5
		mol1.addBond(7, 2, 1);
		// 6
		try
		{
			new HydrogenAdder().addHydrogensToSatisfyValency(mol1);
			hydrogenPlacer.placeHydrogens2D(mol1, 1.0);
			IsotopeFactory ifac = IsotopeFactory.getInstance(mol1.getBuilder());
			ifac.configureAtoms(mol1);
		} catch (IOException ex)
		{
		} catch (ClassNotFoundException ex)
		{
		}
		String smiles1 = null;
		if (standAlone)
		{
			display(mol1);
		}
		try
		{
			smiles1 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES 1: " + smiles1);
		}
		assertNotNull(smiles1);
		assertTrue(smiles1.equals("[H]O[C@]1(C([H])([H])C([H])([H])C([H])([H])C([H])([H])[C@]1(O[H])([H]))([H])"));
		mol1 = (Molecule) AtomContainerManipulator.removeHydrogens(mol1);
		try
		{
			smiles1 = sg.createSMILES(mol1);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES 1: " + smiles1);
		}
		assertNotNull(smiles1);
		assertTrue(smiles1.equals("OC1CCCCC1(O)"));
	}


	/**
	 *  A unit test for JUnit
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
		mol1.addBond(0, 1, 1, CDKConstants.STEREO_BOND_DOWN);
		// 1
		mol1.addBond(1, 2, 1);
		// 2
		mol1.addBond(2, 3, 1);
		// 3
		mol1.addBond(3, 4, 1);
		// 4
		mol1.addBond(4, 5, 1);
		// 5
		mol1.addBond(5, 6, 1);
		// 6
		mol1.addBond(6, 7, 1, CDKConstants.STEREO_BOND_DOWN);
		// 3
		mol1.addBond(6, 8, 1);
		// 4
		mol1.addBond(8, 9, 1);
		// 5
		mol1.addBond(9, 10, 1);
		// 6
		mol1.addBond(10, 11, 1);
		// 6
		mol1.addBond(11, 1, 1);
		// 6
		mol1.addBond(1, 6, 1);
		// 6
		try
		{
			new HydrogenAdder().addHydrogensToSatisfyValency(mol1);
			hydrogenPlacer.placeHydrogens2D(mol1, 1.0);
			IsotopeFactory ifac = IsotopeFactory.getInstance(mol1.getBuilder());
			ifac.configureAtoms(mol1);
		} catch (IOException ex)
		{
		} catch (ClassNotFoundException ex)
		{
		}
		String smiles1 = null;
		if (standAlone)
		{
			display(mol1);
		}
		try
		{
			smiles1 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES 1: " + smiles1);
		}
		assertNotNull(smiles1);
		assertTrue(smiles1.equals("[H]C1([H])(C([H])([H])C([H])([H])C\\2([H])(C([H])([H])C([H])([H])C([H])([H])C([H])([H])C\\2([H])(C1([H])([H]))))"));
		mol1.getBond(6).setStereo(CDKConstants.STEREO_BOND_UP);
		String smiles3 = null;
		try
		{
			smiles3 = sg.createSMILES(mol1, true, new boolean[mol1.getBondCount()]);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		assertFalse(smiles1.equals(smiles3));
	}


	/**
	 *  A unit test for JUnit
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

		mol1.addBond(0, 1, 1);
		// 1
		mol1.addBond(1, 2, 1);
		// 2
		mol1.addBond(1, 3, 2);
		// 3
		mol1.addBond(3, 4, 1);
		// 4
		mol1.addBond(3, 5, 1);
		// 4
		try
		{
			IsotopeFactory ifac = IsotopeFactory.getInstance(mol1.getBuilder());
			ifac.configureAtoms(mol1);
		} catch (IOException ex)
		{
		}
		String smiles1 = null;
		if (standAlone)
		{
			display(mol1);
		}
		boolean[] bool = new boolean[mol1.getBondCount()];
		bool[2] = true;
		try
		{
			smiles1 = sg.createSMILES(mol1, true, bool);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES 1: " + smiles1);
		}
		assertNotNull(smiles1);
		assertTrue(smiles1.equals("F/C(=C/(F)S)S"));
		mol1.getAtom(4).setPoint2d(new Point2d(0, 3));
		mol1.getAtom(5).setPoint2d(new Point2d(2, 3));
		try
		{
			smiles1 = sg.createSMILES(mol1, true, bool);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES 1: " + smiles1);
		}
		assertNotNull(smiles1);
		assertTrue(smiles1.equals("F/C(=C\\(F)S)S"));
		try
		{
			new HydrogenAdder().addHydrogensToSatisfyValency(mol1);
			hydrogenPlacer.placeHydrogens2D(mol1, 1.0);
		} catch (IOException ex)
		{
		} catch (ClassNotFoundException ex)
		{
		}
		bool = new boolean[mol1.getBondCount()];
		bool[2] = true;
		try
		{
			smiles1 = sg.createSMILES(mol1, true, bool);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		assertTrue(smiles1.equals("[H]S/C(F)=C/(F)S[H]"));
		mol1.getAtom(5).setPoint2d(new Point2d(0, 3));
		mol1.getAtom(4).setPoint2d(new Point2d(2, 3));
		try
		{
			smiles1 = sg.createSMILES(mol1, true, bool);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		assertTrue(smiles1.equals("[H]S/C(F)=C\\(F)S[H]"));
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
		try
		{
			smiles = sg.createSMILES(molecule);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES: " + smiles);
		}
		assertTrue(smiles.indexOf(".") != -1);
	}


	/**
	 *  A unit test for JUnit
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
		molecule.addBond(0, 1, 1.0);
		molecule.addBond(1, 2, 1.0);
		molecule.addBond(2, 4, 1.0);
		molecule.addBond(4, 0, 1.0);
		molecule.addBond(4, 3, 1.0);
		fixCarbonHCount(molecule);
		try
		{
			smiles = sg.createSMILES(molecule);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES: " + smiles);
		}
		assertEquals("N1(C)CCC1", smiles);
	}


	/**
	 *  A bug reported for JChemPaint.
	 */
	public void testSFBug956923() throws Exception 
	{
		String smiles = "";
		Molecule molecule = new Molecule();
        SmilesGenerator sg = new SmilesGenerator();
		Atom sp2CarbonWithOneHydrogen = new Atom("C");
		sp2CarbonWithOneHydrogen.setHybridization(CDKConstants.HYBRIDIZATION_SP2);
		sp2CarbonWithOneHydrogen.setHydrogenCount(1);
		molecule.addAtom(sp2CarbonWithOneHydrogen);
		molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
		molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
		molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
		molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
		molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
		molecule.addBond(0, 1, 1.0);
		molecule.addBond(1, 2, 1.0);
		molecule.addBond(2, 3, 1.0);
		molecule.addBond(3, 4, 1.0);
		molecule.addBond(4, 5, 1.0);
		molecule.addBond(5, 0, 1.0);
		try
		{
			smiles = sg.createSMILES(molecule);
		} catch (Exception exc)
		{
			fail(exc.getMessage());
		}
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
		mol.addBond(0, 1, 2.0);
		mol.addBond(0, 2, 2.0);
		mol.addBond(0, 3, 1.0);
		mol.addBond(0, 4, 1.0);
		mol.getAtom(3).setHydrogenCount(1);
		mol.getAtom(4).setHydrogenCount(1);
		AtomContainerAtomPermutor acap = new
				AtomContainerAtomPermutor(mol);
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
		mol.addBond(0, 1, 2.0);
		mol.addBond(0, 2, 2.0);
		mol.addBond(0, 3, 1.0);
		mol.addBond(0, 4, 1.0);
		mol.getAtom(3).setHydrogenCount(1);
		mol.getAtom(4).setHydrogenCount(1);
		AtomContainerBondPermutor acbp = new
				AtomContainerBondPermutor(mol);
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

	private void fixCarbonHCount(Molecule mol)
	{
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
			if (standAlone)
			{
				System.out.println("Hydrogen count for atom " + f + ": " + atom.getHydrogenCount());
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
		try
		{
			smiles = sg.createSMILES(molecule);
		} catch (Exception exc)
		{
			System.out.println(exc);
			if (!standAlone)
			{
				fail();
			}
		}
		if (standAlone)
		{
			System.err.println("SMILES: " + smiles);
		}
		assertEquals("[*]", smiles);

	}


	/**
	 *  Test generation of a reaction SMILES. I know, it's a stupid alchemic
	 *  reaction, but it serves its purpose.
	 */
	public void testReactionSMILES()
	{
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
		try
		{
			String smiles = sg.createSMILES(reaction);
			//logger.debug("Generated SMILES: " + smiles);
			assertEquals("C>[*]>[Au]", smiles);
		} catch (Exception exc)
		{
			System.out.println(exc);
		}
	}


	/**
	 *  Test generation of a d and l alanin
	 */
	public void testAlaSMILES()
	{
		try
		{
			String filename = "data/mdl/l-ala.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			Molecule mol1 = (Molecule) reader.read(new Molecule());
			new HydrogenAdder().addExplicitHydrogensToSatisfyValency(mol1);
			new HydrogenPlacer().placeHydrogens2D(mol1, 1.0);
			filename = "data/mdl/d-ala.mol";
			ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			reader = new MDLReader(ins);
			Molecule mol2 = (Molecule) reader.read(new Molecule());
			new HydrogenAdder().addExplicitHydrogensToSatisfyValency(mol2);
			new HydrogenPlacer().placeHydrogens2D(mol2, 1.0);
			SmilesGenerator sg = new SmilesGenerator();
			String smiles1 = sg.createChiralSMILES(mol1, new boolean[20]);
			String smiles2 = sg.createChiralSMILES(mol2, new boolean[20]);
			assertFalse(smiles1.equals(smiles2));
		} catch (Exception exc)
		{
			System.out.println(exc);
		}
	}


	/**
	 *  Test some sugars
	 */
	public void testSugarSMILES()
	{
		try
		{
			String filename = "data/mdl/D-mannose.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			Molecule mol1 = (Molecule) reader.read(new Molecule());
			new HydrogenAdder().addExplicitHydrogensToSatisfyValency(mol1);
			new HydrogenPlacer().placeHydrogens2D(mol1, 1.0);
			filename = "data/mdl/D+-glucose.mol";
			ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			reader = new MDLReader(ins);
			Molecule mol2 = (Molecule) reader.read(new Molecule());
			new HydrogenAdder().addExplicitHydrogensToSatisfyValency(mol2);
			new HydrogenPlacer().placeHydrogens2D(mol2, 1.0);
			SmilesGenerator sg = new SmilesGenerator();
			String smiles1 = sg.createChiralSMILES(mol1, new boolean[20]);
			String smiles2 = sg.createChiralSMILES(mol2, new boolean[20]);
			assertFalse(smiles1.equals(smiles2));
		} catch (Exception exc)
		{
			System.out.println(exc);
		}
	}

	private void display(Molecule molecule)
	{
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		MoleculeViewer2D mv = new MoleculeViewer2D();
		Renderer2DModel r2dm = mv.getRenderer2DModel();
		r2dm.setDrawNumbers(true);

		try
		{
			sdg.setMolecule((Molecule) molecule.clone());
			sdg.generateCoordinates(new Vector2d(0, 1));
			mv.setAtomContainer(sdg.getMolecule());
			mv.display();
		} catch (Exception exc)
		{
			System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
			exc.printStackTrace();
		}
	}


	/**
	 *  Test for some rings where the double bond is broken
	 */
	public void testCycloOctan()
	{
		try
		{
			String filename = "data/mdl/cyclooctan.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			Molecule mol1 = (Molecule) reader.read(new Molecule());
            SmilesGenerator sg = new SmilesGenerator();
			String moleculeSmile = sg.createSMILES(mol1);
			assertEquals(moleculeSmile, "C1=CCCCCCC1");
		} catch (Exception exc)
		{
			exc.printStackTrace();
			System.out.println(exc);
			fail(exc.getMessage());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testCycloOcten()
	{
		try
		{
			String filename = "data/mdl/cycloocten.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			Molecule mol1 = (Molecule) reader.read(new Molecule());
            SmilesGenerator sg = new SmilesGenerator();
			String moleculeSmile = sg.createSMILES(mol1);
			assertEquals(moleculeSmile, "C1C=CCCCCC1");
		} catch (Exception exc)
		{
			exc.printStackTrace();
			System.out.println(exc);
			fail(exc.getMessage());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testCycloOctadien()
	{
		try
		{
			String filename = "data/mdl/cyclooctadien.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			Molecule mol1 = (Molecule) reader.read(new Molecule());
            SmilesGenerator sg = new SmilesGenerator();
			String moleculeSmile = sg.createSMILES(mol1);
			assertEquals(moleculeSmile, "C=1CCC=CCCC=1");
		} catch (Exception exc)
		{
			exc.printStackTrace();
			System.out.println(exc);
			fail(exc.getMessage());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testSFBug1089770_1()
	{
		try
		{
			String filename = "data/mdl/bug1089770-1.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			Molecule mol1 = (Molecule) reader.read(new Molecule());
            SmilesGenerator sg = new SmilesGenerator();
			String moleculeSmile = sg.createSMILES(mol1);
			//logger.debug(filename + " -> " + moleculeSmile);
			assertEquals(moleculeSmile, "C1CCC=2CCCC=2(C1)");
		} catch (Exception exc)
		{
			exc.printStackTrace();
			System.out.println(exc);
			fail(exc.getMessage());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testSFBug1089770_2()
	{
		try
		{
			String filename = "data/mdl/bug1089770-2.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(ins);
			Molecule mol1 = (Molecule) reader.read(new Molecule());
            SmilesGenerator sg = new SmilesGenerator();
			String moleculeSmile = sg.createSMILES(mol1);
			//logger.debug(filename + " -> " + moleculeSmile);
			assertEquals(moleculeSmile, "C=1CCC=CCCC=1");
		} catch (Exception exc)
		{
			exc.printStackTrace();
			System.out.println(exc);
			fail(exc.getMessage());
		}
	}

}

