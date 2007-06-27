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
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

import java.io.InputStream;

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

    public void testReserpine() throws Exception {
        String filename = "data/mdl/reserpine.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IMolecule mol = (IMolecule) reader.read(new NNMolecule());
        assertEquals(44, mol.getAtomCount());
        assertEquals(49, mol.getBondCount());
        assertNotNull(mol);

        String[] atomTypes = {
                "C4", "C4", "C4", "C4", "C4", "C4", "N3", "C4", "C4", "C4",
                "N3", "C4", "C4", "C4", "C4", "C4", "C4", "C4", "C4", "C4",
                "C4", "O2", "C4", "C4", "C4", "C4", "C4", "C4", "C4", "O2",
                "O2", "C4", "O2", "C4", "C4", "O2", "O2", "C4", "O2", "C4",
                "O2", "C4", "C4", "O2"
        };
        StructGenMatcher atm = new StructGenMatcher();
        for (int i = 0; i < atomTypes.length; i++) {
            IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
            assertNotNull(matched);
            assertEquals(atomTypes[i], matched.getAtomTypeName());
        }
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
            assertNotNull(matched);
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
            assertNotNull(matched);
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
            assertNotNull(matched);
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
            assertNotNull(matched);
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

    public void testArsenic() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("As");
        atom1.setHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 3; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newBond(floruineAtom, atom1, 1.0);
            mol.addBond(bond);
        }

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertNotNull(matched);
        assertEquals("As3", matched.getAtomTypeName());

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertNotNull(matched);
            assertEquals("Cl1", matched.getAtomTypeName());
        }

    }


}
