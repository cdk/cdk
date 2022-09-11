/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.smsd.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smiles.SmilesParser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * @cdk.module test-smsd
 * @author Asad
 */
class ExtAtomContainerManipulatorTest {

    public ExtAtomContainerManipulatorTest() {}

    @BeforeAll
    static void setUpClass() throws Exception {}

    @AfterAll
    static void tearDownClass() throws Exception {}

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    /**
     * Test of makeDeepCopy method, of class ExtAtomContainerManipulator.
     * @throws InvalidSmilesException
     */
    @Test
    void testMakeDeepCopy() throws InvalidSmilesException {
        String rawMolSmiles = "[H]POOSC(Br)C(Cl)C(F)I";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer container = sp.parseSmiles(rawMolSmiles);

        int counter = 0;
        for (IAtom a : container.atoms()) {
            a.setID(String.valueOf(counter++));
        }

        IAtomContainer result = ExtAtomContainerManipulator.makeDeepCopy(container);
        for (int i = 0; i < result.getAtomCount(); i++) {
            Assertions.assertEquals(result.getAtom(i).getSymbol(), container.getAtom(i).getSymbol());
            Assertions.assertEquals(result.getAtom(i).getID(), container.getAtom(i).getID());
        }

    }

    /**
     * Test of aromatizeMolecule method, of class ExtAtomContainerManipulator.
     * @throws InvalidSmilesException
     */
    @Test
    void testAromatizeMolecule() throws InvalidSmilesException {
        String rawMolSmiles = "C1=CC2=C(C=C1)C=CC=C2";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(rawMolSmiles);
        ExtAtomContainerManipulator.aromatizeMolecule(mol);
        int count = 0;
        for (IBond b : mol.bonds()) {
            if (b.getFlag(CDKConstants.ISAROMATIC) && b.getOrder().equals(IBond.Order.DOUBLE)) {
                count++;
            }
        }
        Assertions.assertEquals(5, count);
    }

    /**
     * Test of getExplicitHydrogenCount method, of class ExtAtomContainerManipulator.
     * @throws InvalidSmilesException
     */
    @Test
    void testGetExplicitHydrogenCount() throws InvalidSmilesException {

        String rawMolSmiles = "[H]POOSC(Br)C(Cl)C(F)I";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles(rawMolSmiles);
        IAtom atom = null;
        for (IAtom a : atomContainer.atoms()) {
            if (a.getSymbol().equalsIgnoreCase("P")) {
                atom = a;
                break;
            }
        }

        int expResult = 1;
        int result = ExtAtomContainerManipulator.getExplicitHydrogenCount(atomContainer, atom);
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getImplicitHydrogenCount method, of class ExtAtomContainerManipulator.
     * @throws InvalidSmilesException
     */
    @Test
    void testGetImplicitHydrogenCount() throws InvalidSmilesException {

        String rawMolSmiles = "[H]POOSC(Br)C(Cl)C(F)I";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles(rawMolSmiles);
        IAtom atom = null;
        for (IAtom a : atomContainer.atoms()) {
            if (a.getSymbol().equalsIgnoreCase("P")) {
                atom = a;
                break;
            }
        }

        int expResult = 1;
        int result = ExtAtomContainerManipulator.getImplicitHydrogenCount(atom);
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getHydrogenCount method, of class ExtAtomContainerManipulator.
     * @throws InvalidSmilesException
     */
    @Test
    void testGetHydrogenCount() throws InvalidSmilesException {
        String rawMolSmiles = "[H]POOSC(Br)C(Cl)C(F)I";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles(rawMolSmiles);
        IAtom atom = null;
        for (IAtom a : atomContainer.atoms()) {
            if (a.getSymbol().equalsIgnoreCase("P")) {
                atom = a;
                break;
            }
        }
        int expResult = 2;
        int result = ExtAtomContainerManipulator.getHydrogenCount(atomContainer, atom);
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of removeHydrogensAndPreserveAtomID method, of class ExtAtomContainerManipulator.
     * @throws InvalidSmilesException
     */
    @Test
    void testRemoveHydrogensAndPreserveAtomID() throws InvalidSmilesException {
        String rawMolSmiles = "[H]POOSC(Br)C(Cl)C(F)I";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles(rawMolSmiles);
        IAtom beforeAtom = null;
        IAtom afterAtom = null;
        for (IAtom a : atomContainer.atoms()) {
            if (a.getSymbol().equalsIgnoreCase("P")) {
                beforeAtom = a;
                a.setID("TEST");
                break;
            }
        }
        IAtomContainer result = ExtAtomContainerManipulator.removeHydrogensExceptSingleAndPreserveAtomID(atomContainer);

        for (IAtom a : result.atoms()) {
            if (a.getSymbol().equalsIgnoreCase("P")) {
                afterAtom = a;
                break;
            }
        }

        Assertions.assertEquals(afterAtom.getID(), beforeAtom.getID());
    }

    /**
     * Test of convertExplicitToImplicitHydrogens method, of class ExtAtomContainerManipulator.
     * @throws InvalidSmilesException
     */
    @Test
    void testConvertExplicitToImplicitHydrogens() throws InvalidSmilesException {
        String rawMolSmiles = "[H]POOSC(Br)C(Cl)C(F)I";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles(rawMolSmiles);
        int expResult = 11;
        IAtomContainer result = ExtAtomContainerManipulator.convertExplicitToImplicitHydrogens(atomContainer);
        Assertions.assertEquals(expResult, result.getAtomCount());
    }

    /**
     * Test of percieveAtomTypesAndConfigureAtoms method, of class ExtAtomContainerManipulator.
     * @throws Exception
     */
    @Test
    void testPercieveAtomTypesAndConfigureAtoms() throws Exception {
        String rawMolSmiles = "[H]POOSC(Br)C(Cl)C(F)I";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles(rawMolSmiles);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
        Assertions.assertNotNull(atomContainer);
    }
}
