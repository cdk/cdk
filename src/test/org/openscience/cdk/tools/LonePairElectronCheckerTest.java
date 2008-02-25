/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *                     2007  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.LonePair;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Tests CDK's Lone Pair Electron checking capabilities in terms of
 * example molecules.
 * 
 * @cdk.module test-extra
 *
 * @author         Miguel Rojas
 * @cdk.created    2006-04-01
 */
public class LonePairElectronCheckerTest extends CDKTestCase
{

	LonePairElectronChecker lpcheck = null;
	boolean standAlone = false;


	/**
	 *  Constructor for the LonePairCheckerTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public LonePairElectronCheckerTest(String name)
	{
		super(name);
	}

    /**
    *  The JUnit setup method
    */
    public void setUp() throws Exception {
    	lpcheck = new LonePairElectronChecker();
    }

	/**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public static Test suite() {
        TestSuite suite = new TestSuite(LonePairElectronCheckerTest.class);
        return suite;
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testAllSaturated_Formaldehyde() throws CDKException
	{
		// test Formaldehyde, CH2=O with explicit hydrogen
		Molecule m = new Molecule();
		Atom c = new Atom("C");
		Atom h1 = new Atom("H");
		Atom h2 = new Atom("H");
		Atom O = new Atom("O");
		m.addAtom(c);
		m.addAtom(h1);
		m.addAtom(h2);
		m.addAtom(O);
		for(int i = 0; i < 2 ; i++){
			LonePair lp = new LonePair(O);
			m.addLonePair(lp);
		}
		m.addBond(new Bond(c, h1));
		m.addBond(new Bond(c, h2));
		m.addBond(new Bond(c, O, IBond.Order.DOUBLE));
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);
		
		assertTrue(lpcheck.allSaturated(m));
	}

	/**
	 *  A unit test for JUnit
	 */
	public void testAllSaturated_Methanethiol() throws CDKException {
		// test Methanethiol, CH4S
		Atom c = new Atom("C");
		c.setHydrogenCount(3);
		Atom s = new Atom("S");
		s.setHydrogenCount(1);
		
		Bond b1 = new Bond(c, s, IBond.Order.SINGLE);
		
		Molecule m = new Molecule();
		m.addAtom(c);
		m.addAtom(s);
		m.addBond(b1);
		for(int i = 0; i < 1 ; i++){
			LonePair lp = new LonePair(s);
			m.addLonePair(lp);
		}
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);
		
		assertFalse(lpcheck.allSaturated(m));
	}
	/**
	 *  A unit test for JUnit
	 */
	public void testNewSaturate_Methyl_chloride() throws CDKException {
		// test Methyl chloride, CH3Cl
		Atom c1 = new Atom("C");
		c1.setHydrogenCount(3);
		Atom cl = new Atom("Cl");
		Bond b1 = new Bond(c1, cl, IBond.Order.SINGLE);
		
		Molecule m = new Molecule();
		m.addAtom(c1);
		m.addAtom(cl);
		m.addBond(b1);
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);
		lpcheck.saturate(m);
		assertEquals(3, m.getConnectedLonePairsCount(cl));
		assertEquals(0, m.getConnectedLonePairsCount(c1));
	}
	/**
	 *  A unit test for JUnit
	 */
	public void testNewSaturate_Methyl_alcohol() throws CDKException {
		// test Methyl chloride, CH3OH
		Atom c1 = new Atom("C");
		c1.setHydrogenCount(3);
		Atom o = new Atom("O");
		o.setHydrogenCount(1);
		Bond b1 = new Bond(c1, o, IBond.Order.SINGLE);
		
		Molecule m = new Molecule();
		m.addAtom(c1);
		m.addAtom(o);
		m.addBond(b1);
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);
		lpcheck.saturate(m);
		assertEquals(2, m.getConnectedLonePairsCount(o));
		assertEquals(0, m.getConnectedLonePairsCount(c1));
	}
	/**
	 *  A unit test for JUnit
	 */
	public void testNewSaturate_Methyl_alcohol_AddH() throws CDKException {
		// test Methyl alcohol, CH3OH
		Molecule m = new Molecule();
		m.addAtom(new Atom("C"));
		m.addAtom(new Atom("O"));
		for(int i = 0 ; i < 4 ; i++)
			m.addAtom(new Atom("H"));
		
		m.addBond(0, 1, IBond.Order.SINGLE);
		m.addBond(0, 2, IBond.Order.SINGLE);
		m.addBond(0, 3, IBond.Order.SINGLE);
		m.addBond(0, 4, IBond.Order.SINGLE);
		m.addBond(1, 5, IBond.Order.SINGLE);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);
		lpcheck.saturate(m);
		
		assertEquals(2, m.getConnectedLonePairsCount(m.getAtom(1)));
		assertEquals(0, m.getConnectedLonePairsCount(m.getAtom(0)));
	}
	/**
	 *  A unit test for JUnit
	 */
	public void testNewSaturate_Methyl_alcohol_protonated() throws CDKException {
		// test Methyl alcohol protonated, CH3OH2+
		Atom c1 = new Atom("C");
		c1.setHydrogenCount(3);
		Atom o = new Atom("O");
        o.setFormalCharge(+1);
		o.setHydrogenCount(2);
		Bond b1 = new Bond(c1, o, IBond.Order.SINGLE);
		
		Molecule m = new Molecule();
		m.addAtom(c1);
		m.addAtom(o);
		m.addBond(b1);
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);
		lpcheck.saturate(m);
		
		assertEquals(1, m.getConnectedLonePairsCount(o));
	}
	/**
	 *  A unit test for JUnit
	 */
	public void testNewSaturate_methoxide_anion() throws CDKException {
		// test methoxide anion, CH3O-
		Atom c1 = new Atom("C");
		c1.setHydrogenCount(3);
		Atom o = new Atom("O");
        o.setFormalCharge(-1);
		Bond b1 = new Bond(c1, o, IBond.Order.SINGLE);
		
		Molecule m = new Molecule();
		m.addAtom(c1);
		m.addAtom(o);
		m.addBond(b1);
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);
		lpcheck.saturate(m);
		
		assertEquals(3, m.getConnectedLonePairsCount(o));
	}
	/**
	 *  A unit test for JUnit
	 */
	public void testNewSaturate_Ammonia() throws CDKException {
		// test Ammonia, H3N
		Atom n = new Atom("N");
		n.setHydrogenCount(3);
		
		Molecule m = new Molecule();
		m.addAtom(n);
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);
		lpcheck.saturate(m);
		
		assertEquals(1, m.getConnectedLonePairsCount(n));
	}
	/**
	 *  A unit test for JUnit
	 */
	public void testNewSaturate_methylamine_radical_cation() throws CDKException {
		// test Ammonia, CH3NH3+
		Atom c = new Atom("C");
		c.setHydrogenCount(3);
		Atom n = new Atom("N");
		n.setHydrogenCount(3);
        n.setFormalCharge(+1);
		Bond b1 = new Bond(c, n, IBond.Order.SINGLE);
		
		Molecule m = new Molecule();
		m.addAtom(c);
		m.addAtom(n);
		m.addBond(b1);
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);
		lpcheck.saturate(m);
		
		assertEquals(0, m.getConnectedLonePairsCount(n));
	}
	/**
	 *  A unit test for JUnit O=C([H])[C+]([H])[C-]([H])[H]
	 */
	public void testNewSaturate_withHAdded() throws CDKException {
		// O=C([H])[C+]([H])[C-]([H])[H]
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("O=C([H])[C+]([H])[C-]([H])[H]");
		lpcheck.saturate(mol);
		
		assertEquals(2, mol.getConnectedLonePairsCount(mol.getAtom(0)));
		assertEquals(0, mol.getConnectedLonePairsCount(mol.getAtom(3)));
		assertEquals(1, mol.getConnectedLonePairsCount(mol.getAtom(5)));
	}
}
