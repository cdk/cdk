/* $Revision: 5889 $ $Author: egonw $ $Date: 2006-04-06 15:24:58 +0200 (Thu, 06 Apr 2006) $
 * 
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.atomtype;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.StructGenMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * This class tests the matching of atom types defined in the
 * structgen atom type list.
 *
 * @cdk.module test-core
 */
public class StructGenMatcherTest extends CDKTestCase {

    private LoggingTool logger = new LoggingTool(StructGenMatcherTest.class);

    public StructGenMatcherTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    public static Test suite() {
        return new TestSuite(StructGenMatcherTest.class);
    }

    public void testStructGenMatcher() throws ClassNotFoundException, CDKException {
        StructGenMatcher matcher = new StructGenMatcher();
        assertNotNull(matcher);
    }

    public void testFindMatchingAtomType_IAtomContainer_IAtom() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom atom = DefaultChemObjectBuilder.getInstance().newAtom("C");
        atom.setHydrogenCount(4);
        mol.addAtom(atom);

        StructGenMatcher atm = new StructGenMatcher();
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        assertNotNull(matched);

        assertEquals("C", matched.getSymbol());
    }

    public void testN3() throws CDKException {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        atom.setHydrogenCount(3);
        mol.addAtom(atom);

        StructGenMatcher atm = new StructGenMatcher();
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        assertNotNull(matched);

        assertEquals("N", matched.getSymbol());
    }


    public void testFlourine() throws Exception {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        atom1.setHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 4; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newAtom("F");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newBond(floruineAtom, atom1);
            mol.addBond(bond);
        }

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertNotNull(matched);
        assertEquals("C4", matched.getAtomTypeName());

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertNotNull("atom "+i+" failed to match", matched);
            assertEquals("F1", matched.getAtomTypeName());
        }
    }

    public void testChlorine() throws Exception {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        atom1.setHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 4; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newBond(floruineAtom, atom1);
            mol.addBond(bond);
        }

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertNotNull(matched);
        assertEquals("C4", matched.getAtomTypeName());

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertNotNull("atom "+i+" failed to match", matched);
            assertEquals("Cl1", matched.getAtomTypeName());
        }
    }

    public void testBromine() throws Exception {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        atom1.setHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 4; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newAtom("Br");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newBond(floruineAtom, atom1);
            mol.addBond(bond);
        }

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertNotNull(matched);
        assertEquals("C4", matched.getAtomTypeName());

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertNotNull("atom "+i+" failed to match", matched);
            assertEquals("Br1", matched.getAtomTypeName());
        }
    }

    public void testIodine() throws Exception {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        atom1.setHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 4; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newAtom("I");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newBond(floruineAtom, atom1);
            mol.addBond(bond);
        }

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertNotNull(matched);
        assertEquals("C4", matched.getAtomTypeName());

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertNotNull("atom "+i+" failed to match", matched);
            assertEquals("I1", matched.getAtomTypeName());
        }
    }

    public void testLithium() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("Li");
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newAtom("F");
        IBond bond = DefaultChemObjectBuilder.getInstance().newBond(atom1, atom2);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addBond(bond);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertNotNull(matched);
        assertEquals("Li1", matched.getAtomTypeName());

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertNotNull(matched);
        assertEquals("F1", matched.getAtomTypeName());
    }

    /*
    Tests As3, Cl1
     */
    public void testArsenic() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("As");
        atom1.setHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 3; i++) {
            IAtom atom = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
            mol.addAtom(atom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newBond(atom, atom1, 1.0);
            mol.addBond(bond);
        }

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertNotNull(matched);
        assertEquals("As3", matched.getAtomTypeName());

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertNotNull("atom "+i+" failed to match", matched);
            assertEquals("Cl1", matched.getAtomTypeName());
        }
    }

    /*
    Tests C4, O2
     */
    public void testOxygen1() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom carbon = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newAtom("O");

        carbon.setHydrogenCount(1);
        o1.setHydrogenCount(1);
        o2.setHydrogenCount(0);

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newBond(carbon, o1, 1.0);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newBond(carbon, o2, 2.0);

        mol.addAtom(carbon);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addBond(bond1);
        mol.addBond(bond2);

        StructGenMatcher matcher = new StructGenMatcher();

        // look at the sp2 O first
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertNotNull(matched);
        assertEquals("O2", matched.getAtomTypeName());


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertNotNull(matched);
        assertEquals("C4", matched.getAtomTypeName());

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertNotNull(matched);
        assertEquals("O2", matched.getAtomTypeName());
    }

    /*
    Tests O2, H1
     */
    public void testOxygen2() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newAtom("H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newAtom("H");

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newBond(h1, o1, 1.0);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newBond(o1, o2, 1.0);
        IBond bond3 = DefaultChemObjectBuilder.getInstance().newBond(o2, h2, 1.0);

        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertNotNull(matched);
        assertEquals("O2", matched.getAtomTypeName());

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertNotNull(matched);
        assertEquals("O2", matched.getAtomTypeName());

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertNotNull(matched);
        assertEquals("H1", matched.getAtomTypeName());

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(3));
        assertNotNull(matched);
        assertEquals("H1", matched.getAtomTypeName());
    }

    /*
    Tests P5, S2, Cl1
     */
    public void testP5() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom p = DefaultChemObjectBuilder.getInstance().newAtom("P");
        IAtom cl1 = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
        IAtom cl2 = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
        IAtom cl3 = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newBond(p, cl1, 1.0);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newBond(p, cl2, 1.0);
        IBond bond3 = DefaultChemObjectBuilder.getInstance().newBond(p, cl3, 1.0);
        IBond bond4 = DefaultChemObjectBuilder.getInstance().newBond(p, s, 2.0);

        mol.addAtom(p);
        mol.addAtom(cl1);
        mol.addAtom(cl2);
        mol.addAtom(cl3);
        mol.addAtom(s);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);
        mol.addBond(bond4);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertNotNull(matched);
        assertEquals("P4", matched.getAtomTypeName());


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(4));
        assertNotNull(matched);
        assertEquals("S2", matched.getAtomTypeName());

        for (int i = 1; i < 4; i++) {
            matched = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            assertNotNull("atom "+i+" failed to match", matched);
            assertEquals("Cl1", matched.getAtomTypeName());
        }
    }

    /*
    Tests P3, O2, C4
     */
    public void testP3() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom p = DefaultChemObjectBuilder.getInstance().newAtom("P");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o3 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newAtom("C");

        c1.setHydrogenCount(3);
        c2.setHydrogenCount(3);
        c3.setHydrogenCount(3);

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newBond(p, o1, 1.0);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newBond(p, o2, 1.0);
        IBond bond3 = DefaultChemObjectBuilder.getInstance().newBond(p, o3, 1.0);
        IBond bond4 = DefaultChemObjectBuilder.getInstance().newBond(c1, o1, 1.0);
        IBond bond5 = DefaultChemObjectBuilder.getInstance().newBond(c2, o2, 1.0);
        IBond bond6 = DefaultChemObjectBuilder.getInstance().newBond(c3, o3, 1.0);

        mol.addAtom(p);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(o3);
        mol.addAtom(c1);
        mol.addAtom(c2);
        mol.addAtom(c3);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);
        mol.addBond(bond4);
        mol.addBond(bond5);
        mol.addBond(bond6);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        String[] atomTypes = {"P3", "O2", "O2", "O2" , "C4", "C4", "C4" };
        for (int i = 0; i < mol.getAtomCount(); i++) {
            matched = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            assertNotNull("atom "+i+" failed to match", matched);
            assertEquals(atomTypes[i], matched.getAtomTypeName());
        }       
    }


}
