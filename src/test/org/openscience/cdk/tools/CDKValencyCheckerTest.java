/* $Revision: 8537 $ $Author: egonw $ $Date: 2007-07-14 15:46:21 +0200 (Sat, 14 Jul 2007) $ 
 *
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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

import junit.framework.TestSuite;
import org.openscience.cdk.*;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.CDKValencyChecker;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import java.util.Iterator;

/**
 * Tests CDK's valency checker capabilities in terms of example molecules.
 *
 * @cdk.module  test-valencycheck
 *
 * @author      Egon Willighagen <egonw@users.sf.net>
 * @cdk.created 2007-07-28
 */
public class CDKValencyCheckerTest extends CDKTestCase {

	public CDKValencyCheckerTest(String name) {
		super(name);
	}
	
    public static TestSuite suite() {
    	return new TestSuite(CDKValencyCheckerTest.class);
    }
	
	public void testIsSaturated_IAtomContainer() throws CDKException {
		// test methane with explicit hydrogen
		Molecule mol = new Molecule();
		CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
		Atom c = new Atom("C");
		Atom h1 = new Atom("H");
		Atom h2 = new Atom("H");
		Atom h3 = new Atom("H");
		Atom h4 = new Atom("H");
		mol.addAtom(c);
		mol.addAtom(h1);
		mol.addAtom(h2);
		mol.addAtom(h3);
		mol.addAtom(h4);
		mol.addBond(new Bond(c, h1));
		mol.addBond(new Bond(c, h2));
		mol.addBond(new Bond(c, h3));
		mol.addBond(new Bond(c, h4));
		findAndConfigureAtomTypesForAllAtoms(mol);
		assertTrue(checker.isSaturated(mol));

		// test methane with implicit hydrogen
		mol = new Molecule();
		c = new Atom("C");
		c.setHydrogenCount(4);
		mol.addAtom(c);
		findAndConfigureAtomTypesForAllAtoms(mol);
		assertTrue(checker.isSaturated(mol));
	}

	public void testIsSaturated_MissingHydrogens_Methane() throws CDKException {
		// test methane with explicit hydrogen
		Molecule mol = new Molecule();
		CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
		Atom c = new Atom("C");
		mol.addAtom(c);
		c.setHydrogenCount(3);
		findAndConfigureAtomTypesForAllAtoms(mol);
		assertFalse(checker.isSaturated(mol));
	}

	/**
     * Tests if the saturation checker considers negative charges.
     */
	public void testIsSaturated_NegativelyChargedOxygen() throws CDKException {
		// test methane with explicit hydrogen
		Molecule mol = new Molecule();
		CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
		Atom c = new Atom("C");
		Atom h1 = new Atom("H");
		Atom h2 = new Atom("H");
		Atom h3 = new Atom("H");
		Atom o = new Atom("O");
        o.setFormalCharge(-1);
		mol.addAtom(c);
		mol.addAtom(h1);
		mol.addAtom(h2);
		mol.addAtom(h3);
		mol.addAtom(o);
		mol.addBond(new Bond(c, h1));
		mol.addBond(new Bond(c, h2));
		mol.addBond(new Bond(c, h3));
		mol.addBond(new Bond(c, o));
		findAndConfigureAtomTypesForAllAtoms(mol);
		assertTrue(checker.isSaturated(mol));
	}
	
    /**
     * Tests if the saturation checker considers positive
     * charges.
     */
	public void testIsSaturated_PositivelyChargedNitrogen() throws CDKException {
		// test methane with explicit hydrogen
		Molecule mol = new Molecule();
		CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
		Atom n = new Atom("N");
		Atom h1 = new Atom("H");
		Atom h2 = new Atom("H");
		Atom h3 = new Atom("H");
		Atom h4 = new Atom("H");
        n.setFormalCharge(+1);
		mol.addAtom(n);
		mol.addAtom(h1);
		mol.addAtom(h2);
		mol.addAtom(h3);
		mol.addAtom(h4);
		mol.addBond(new Bond(n, h1));
		mol.addBond(new Bond(n, h2));
		mol.addBond(new Bond(n, h3));
		mol.addBond(new Bond(n, h4));
		findAndConfigureAtomTypesForAllAtoms(mol);
		assertTrue(checker.isSaturated(mol));
	}

    /**
     * Test sulfuric acid.
     */
    public void testBug772316() throws CDKException {
		// test methane with explicit hydrogen
		Molecule mol = new Molecule();
		CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
		Atom sulphur = new Atom("S");
		Atom o1 = new Atom("O");
		Atom o2 = new Atom("O");
		Atom o3 = new Atom("O");
		Atom o4 = new Atom("O");
		Atom h1 = new Atom("H");
		Atom h2 = new Atom("H");
		mol.addAtom(sulphur);
		mol.addAtom(o1);
		mol.addAtom(o2);
		mol.addAtom(o3);
		mol.addAtom(o4);
		mol.addAtom(h1);
		mol.addAtom(h2);
		mol.addBond(new Bond(sulphur, o1, IBond.Order.DOUBLE));
		mol.addBond(new Bond(sulphur, o2, IBond.Order.DOUBLE));
		mol.addBond(new Bond(sulphur, o3, IBond.Order.SINGLE));
		mol.addBond(new Bond(sulphur, o4, IBond.Order.SINGLE));
		mol.addBond(new Bond(h1, o3, IBond.Order.SINGLE));
		mol.addBond(new Bond(h2, o4, IBond.Order.SINGLE));
		findAndConfigureAtomTypesForAllAtoms(mol);
		assertTrue(checker.isSaturated(mol));
    }

    /**
     * Tests if the saturation checker gets a proton right.
     */
	public void testIsSaturated_Proton() throws CDKException {
		// test H+
		Molecule mol = new Molecule();
		CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
		Atom hydrogen = new Atom("H");
        hydrogen.setFormalCharge(+1);
		mol.addAtom(hydrogen);
		findAndConfigureAtomTypesForAllAtoms(mol);
		assertTrue(checker.isSaturated(mol));
	}
	
	/** TODO: check who added this test. I think Miguel; it seems to be a
	 *  resonance structure.
	 */
    public void test1() throws CDKException {
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("[F+]=C=C");
		CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
		findAndConfigureAtomTypesForAllAtoms(mol);
		mol.getAtom(2).setHydrogenCount(2); // third atom
		assertTrue(checker.isSaturated(mol));
    }
        
	public void testIsSaturated_MissingBondOrders_Ethane() throws CDKException {
		// test ethane with explicit hydrogen
		Molecule mol = new Molecule();
		CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
		Atom c1 = new Atom("C");
		c1.setHydrogenCount(2);
        c1.setHybridization(IAtomType.Hybridization.SP2);
		Atom c2 = new Atom("C");
        c2.setHybridization(IAtomType.Hybridization.SP2);
		c2.setHydrogenCount(2);
		mol.addAtom(c1);
		mol.addAtom(c2);
		IBond bond = new Bond(c1, c2, CDKConstants.BONDORDER_SINGLE);
		mol.addBond(bond);
		findAndConfigureAtomTypesForAllAtoms(mol);
		assertFalse(checker.isSaturated(mol));
		
		// sanity check
		bond.setOrder(CDKConstants.BONDORDER_DOUBLE);
		mol.addBond(bond);
		findAndConfigureAtomTypesForAllAtoms(mol);
		assertFalse(checker.isSaturated(mol));
	}

	private void findAndConfigureAtomTypesForAllAtoms(IAtomContainer container) throws CDKException {
    	CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
    	Iterator<IAtom> atoms = container.atoms();
    	while (atoms.hasNext()) {
    		IAtom atom = atoms.next();
    		IAtomType type = matcher.findMatchingAtomType(container, atom);
        	assertNotNull(type);
        	AtomTypeManipulator.configure(atom, type);
    	}
    }
    
}
