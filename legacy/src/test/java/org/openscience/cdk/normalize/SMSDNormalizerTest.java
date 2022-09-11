/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received rAtomCount copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.normalize;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 *
 * @cdk.module test-smsd
 */
class SMSDNormalizerTest {

    SMSDNormalizerTest() {}

    @BeforeAll
    static void setUpClass() throws Exception {}

    @AfterAll
    static void tearDownClass() throws Exception {}

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    /**
     * Test of makeDeepCopy method, of class SMSDNormalizer.
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

        IAtomContainer result = SMSDNormalizer.makeDeepCopy(container);
        for (int i = 0; i < result.getAtomCount(); i++) {
            Assertions.assertEquals(result.getAtom(i).getSymbol(), container.getAtom(i).getSymbol());
            Assertions.assertEquals(result.getAtom(i).getID(), container.getAtom(i).getID());
        }

    }

    /**
     * Test of aromatizeMolecule method, of class SMSDNormalizer.
     * @throws InvalidSmilesException
     */
    @Test
    void testAromatizeMolecule() throws InvalidSmilesException {
        String rawMolSmiles = "C1=CC2=C(C=C1)C=CC=C2";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(rawMolSmiles);
        SMSDNormalizer.aromatizeMolecule(mol);
        int count = 0;
        for (IBond b : mol.bonds()) {
            if (b.getFlag(CDKConstants.ISAROMATIC) && b.getOrder().equals(IBond.Order.DOUBLE)) {
                count++;
            }
        }
        Assertions.assertEquals(5, count);
    }

    /**
     * Test of getExplicitHydrogenCount method, of class SMSDNormalizer.
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
        int result = SMSDNormalizer.getExplicitHydrogenCount(atomContainer, atom);
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getImplicitHydrogenCount method, of class SMSDNormalizer.
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
        int result = SMSDNormalizer.getImplicitHydrogenCount(atomContainer, atom);
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getHydrogenCount method, of class SMSDNormalizer.
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
        int result = SMSDNormalizer.getHydrogenCount(atomContainer, atom);
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of removeHydrogensAndPreserveAtomID method, of class SMSDNormalizer.
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
        IAtomContainer result = SMSDNormalizer.removeHydrogensAndPreserveAtomID(atomContainer);

        for (IAtom a : result.atoms()) {
            if (a.getSymbol().equalsIgnoreCase("P")) {
                afterAtom = a;
                break;
            }
        }

        Assertions.assertEquals(afterAtom.getID(), beforeAtom.getID());
    }

    /**
     * Test of convertExplicitToImplicitHydrogens method, of class SMSDNormalizer.
     * @throws InvalidSmilesException
     */
    @Test
    void testConvertExplicitToImplicitHydrogens() throws InvalidSmilesException {
        String rawMolSmiles = "[H]POOSC(Br)C(Cl)C(F)I";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles(rawMolSmiles);
        int expResult = 11;
        IAtomContainer result = SMSDNormalizer.convertExplicitToImplicitHydrogens(atomContainer);
        Assertions.assertEquals(expResult, result.getAtomCount());
    }

    /**
     * Test of percieveAtomTypesAndConfigureAtoms method, of class SMSDNormalizer.
     * @throws Exception
     */
    @Test
    void testPercieveAtomTypesAndConfigureAtoms() throws Exception {
        String rawMolSmiles = "[H]POOSC(Br)C(Cl)C(F)I";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles(rawMolSmiles);
        SMSDNormalizer.percieveAtomTypesAndConfigureAtoms(atomContainer);
        Assertions.assertNotNull(atomContainer);
    }
}
