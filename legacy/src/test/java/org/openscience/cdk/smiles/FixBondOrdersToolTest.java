/*
 * Copyright (C) 2006-2007  Rajarshi Guha <rajarshi@users.sf.net>
 * Copyright (C) 2012 Kevin Lawson <kevin.lawson@syngenta.com>
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
package org.openscience.cdk.smiles;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.Bond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.time.Duration;

/**
 *
 * @author         Rajarshi Guha
 * @cdk.created    2006-09-18
 * @cdk.module     test-smiles
 */
class FixBondOrdersToolTest extends CDKTestCase {

    private static FixBondOrdersTool fbot;

    @BeforeAll
    static void setup() {
        fbot = new FixBondOrdersTool();
    }

    @Test
    void testConstructors() {
        // basically: just test that no exception is thrown
        Assertions.assertNotNull(new FixBondOrdersTool());
    }

    @Test
    void testInterruption() {
        fbot.setInterrupted(false);
        Assertions.assertFalse(fbot.isInterrupted());
        fbot.setInterrupted(true);
        Assertions.assertTrue(fbot.isInterrupted());
        fbot.setInterrupted(false);
    }

    @Test
    void testPyrrole() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(500), () -> {
            String smiles = "c2ccc3n([H])c1ccccc1c3(c2)";
            SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            smilesParser.kekulise(false);
            IAtomContainer molecule = smilesParser.parseSmiles(smiles);
            AtomContainerManipulator.setSingleOrDoubleFlags(molecule);
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

            molecule = fbot.kekuliseAromaticRings(molecule);
            Assertions.assertNotNull(molecule);

            molecule = AtomContainerManipulator.removeHydrogens(molecule);
            int doubleBondCount = 0;
            for (int i = 0; i < molecule.getBondCount(); i++) {
                IBond bond = molecule.getBond(i);
                Assertions.assertTrue(bond.getFlag(CDKConstants.ISAROMATIC));
                if (bond.getOrder() == Order.DOUBLE) doubleBondCount++;
            }
            Assertions.assertEquals(6, doubleBondCount);
        });
    }

    @Test
    void testPyrrole_Silent() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(500), () -> {
            String smiles = "c2ccc3n([H])c1ccccc1c3(c2)";
            SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
            smilesParser.kekulise(false);
            IAtomContainer molecule = smilesParser.parseSmiles(smiles);
            AtomContainerManipulator.setSingleOrDoubleFlags(molecule);
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

            molecule = fbot.kekuliseAromaticRings(molecule);
            Assertions.assertNotNull(molecule);
            molecule = AtomContainerManipulator.removeHydrogens(molecule);
            int doubleBondCount = 0;
            for (int i = 0; i < molecule.getBondCount(); i++) {
                IBond bond = molecule.getBond(i);
                Assertions.assertTrue(bond.getFlag(CDKConstants.ISAROMATIC));
                if (bond.getOrder() == Order.DOUBLE) doubleBondCount++;
            }
            Assertions.assertEquals(6, doubleBondCount);
        });
    }

    @Test
    void testLargeRingSystem() throws Exception {
        String smiles = "O=C1Oc6ccccc6(C(O)C1C5c2ccccc2CC(c3ccc(cc3)c4ccccc4)C5)";
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = smilesParser.parseSmiles(smiles);

        molecule = fbot.kekuliseAromaticRings(molecule);
        Assertions.assertNotNull(molecule);

        molecule = AtomContainerManipulator.removeHydrogens(molecule);
        Assertions.assertEquals(34, molecule.getAtomCount());

        // we should have 14 double bonds
        int doubleBondCount = 0;
        for (int i = 0; i < molecule.getBondCount(); i++) {
            IBond bond = molecule.getBond(i);
            if (bond.getOrder() == Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(13, doubleBondCount);
    }

    /**
     * @cdk.bug 3506770
     */
    @Test
    void testLargeBioclipseUseCase() throws Exception {
        String smiles = "COc1ccc2[C@@H]3[C@H](COc2c1)C(C)(C)OC4=C3C(=O)C(=O)C5=C4OC(C)(C)[C@@H]6COc7cc(OC)ccc7[C@H]56";
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = smilesParser.parseSmiles(smiles);

        molecule = fbot.kekuliseAromaticRings(molecule);
        Assertions.assertNotNull(molecule);

        molecule = AtomContainerManipulator.removeHydrogens(molecule);
        Assertions.assertEquals(40, molecule.getAtomCount());

        // we should have 14 double bonds
        int doubleBondCount = 0;
        for (int i = 0; i < molecule.getBondCount(); i++) {
            IBond bond = molecule.getBond(i);
            if (bond.getOrder() == Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(10, doubleBondCount);
    }

    /**
     * @cdk.inchi InChI=1/C4H5N/c1-2-4-5-3-1/h1-5H
     */
    @Test
    void xtestPyrrole() throws Exception {
        IAtomContainer enol = new AtomContainer();

        // atom block
        IAtom atom1 = new Atom(Elements.CARBON);
        atom1.setHybridization(Hybridization.SP2);
        IAtom atom2 = new Atom(Elements.CARBON);
        atom2.setHybridization(Hybridization.SP2);
        IAtom atom3 = new Atom(Elements.CARBON);
        atom3.setHybridization(Hybridization.SP2);
        IAtom atom4 = new Atom(Elements.CARBON);
        atom4.setHybridization(Hybridization.SP2);
        IAtom atom5 = new Atom(Elements.NITROGEN);
        atom5.setHybridization(Hybridization.SP2);
        atom5.setImplicitHydrogenCount(1);

        // bond block
        IBond bond1 = new Bond(atom1, atom2);
        IBond bond2 = new Bond(atom2, atom3);
        IBond bond3 = new Bond(atom3, atom4);
        IBond bond4 = new Bond(atom4, atom5);
        IBond bond5 = new Bond(atom5, atom1);

        enol.addAtom(atom1);
        enol.addAtom(atom2);
        enol.addAtom(atom3);
        enol.addAtom(atom4);
        enol.addAtom(atom5);
        enol.addBond(bond1);
        enol.addBond(bond2);
        enol.addBond(bond3);
        enol.addBond(bond4);
        enol.addBond(bond5);

        // perceive atom types
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(enol);

        // now have the algorithm have a go at it
        enol = fbot.kekuliseAromaticRings(enol);
        Assertions.assertNotNull(enol);
        //Assert.assertTrue(fbot.isOK(enol));

        // now check whether it did the right thing
        Assertions.assertEquals(Order.DOUBLE, enol.getBond(0).getOrder());
        Assertions.assertEquals(Order.SINGLE, enol.getBond(1).getOrder());
        Assertions.assertEquals(Order.DOUBLE, enol.getBond(2).getOrder());
        Assertions.assertEquals(Order.SINGLE, enol.getBond(3).getOrder());
        Assertions.assertEquals(Order.SINGLE, enol.getBond(4).getOrder());
    }

    @Test
    void xtestPyridine() throws Exception {
        IAtomContainer enol = new AtomContainer();

        // atom block
        IAtom atom1 = new Atom(Elements.CARBON);
        atom1.setHybridization(Hybridization.SP2);
        IAtom atom2 = new Atom(Elements.CARBON);
        atom2.setHybridization(Hybridization.SP2);
        IAtom atom3 = new Atom(Elements.CARBON);
        atom3.setHybridization(Hybridization.SP2);
        IAtom atom4 = new Atom(Elements.CARBON);
        atom4.setHybridization(Hybridization.SP2);
        IAtom atom5 = new Atom(Elements.CARBON);
        atom5.setHybridization(Hybridization.SP2);
        IAtom atom6 = new Atom(Elements.NITROGEN);
        atom6.setHybridization(Hybridization.SP2);

        // bond block
        IBond bond1 = new Bond(atom1, atom2);
        IBond bond2 = new Bond(atom2, atom3);
        IBond bond3 = new Bond(atom3, atom4);
        IBond bond4 = new Bond(atom4, atom5);
        IBond bond5 = new Bond(atom5, atom6);
        IBond bond6 = new Bond(atom6, atom1);

        enol.addAtom(atom1);
        enol.addAtom(atom2);
        enol.addAtom(atom3);
        enol.addAtom(atom4);
        enol.addAtom(atom5);
        enol.addAtom(atom6);
        enol.addBond(bond1);
        enol.addBond(bond2);
        enol.addBond(bond3);
        enol.addBond(bond4);
        enol.addBond(bond5);
        enol.addBond(bond6);

        // perceive atom types
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(enol);

        // now have the algorithm have a go at it
        enol = fbot.kekuliseAromaticRings(enol);
        Assertions.assertNotNull(enol);
        // Assert.assertTrue(dbst.isOK(enol));

        // now check whether it did the right thing
        Assertions.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(0).getOrder().numeric()
                + enol.getBond(5).getOrder().numeric()); // around atom1
        Assertions.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(0).getOrder().numeric()
                + enol.getBond(1).getOrder().numeric()); // around atom2
        Assertions.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(1).getOrder().numeric()
                + enol.getBond(2).getOrder().numeric()); // around atom3
        Assertions.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(2).getOrder().numeric()
                + enol.getBond(3).getOrder().numeric()); // around atom4
        Assertions.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(3).getOrder().numeric()
                + enol.getBond(4).getOrder().numeric()); // around atom5
        Assertions.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(4).getOrder().numeric()
                + enol.getBond(5).getOrder().numeric()); // around atom6
    }

    /**
     * @cdk.inchi InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H
     * @cdk.bug   1931262
     */
    @Test
    void xtestBenzene() throws Exception {
        IAtomContainer enol = new AtomContainer();

        // atom block
        IAtom atom1 = new Atom(Elements.CARBON);
        atom1.setHybridization(Hybridization.SP2);
        IAtom atom2 = new Atom(Elements.CARBON);
        atom2.setHybridization(Hybridization.SP2);
        IAtom atom3 = new Atom(Elements.CARBON);
        atom3.setHybridization(Hybridization.SP2);
        IAtom atom4 = new Atom(Elements.CARBON);
        atom4.setHybridization(Hybridization.SP2);
        IAtom atom5 = new Atom(Elements.CARBON);
        atom5.setHybridization(Hybridization.SP2);
        IAtom atom6 = new Atom(Elements.CARBON);
        atom6.setHybridization(Hybridization.SP2);

        // bond block
        IBond bond1 = new Bond(atom1, atom2);
        IBond bond2 = new Bond(atom2, atom3);
        IBond bond3 = new Bond(atom3, atom4);
        IBond bond4 = new Bond(atom4, atom5);
        IBond bond5 = new Bond(atom5, atom6);
        IBond bond6 = new Bond(atom6, atom1);

        enol.addAtom(atom1);
        enol.addAtom(atom2);
        enol.addAtom(atom3);
        enol.addAtom(atom4);
        enol.addAtom(atom5);
        enol.addAtom(atom6);
        enol.addBond(bond1);
        enol.addBond(bond2);
        enol.addBond(bond3);
        enol.addBond(bond4);
        enol.addBond(bond5);
        enol.addBond(bond6);

        // perceive atom types
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(enol);

        // now have the algorithm have a go at it
        enol = fbot.kekuliseAromaticRings(enol);
        Assertions.assertNotNull(enol);
        //Assert.assertTrue(dbst.isOK(enol));

        // now check whether it did the right thing
        Assertions.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(0).getOrder().numeric()
                + enol.getBond(5).getOrder().numeric()); // around atom1
        Assertions.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(0).getOrder().numeric()
                + enol.getBond(1).getOrder().numeric()); // around atom2
        Assertions.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(1).getOrder().numeric()
                + enol.getBond(2).getOrder().numeric()); // around atom3
        Assertions.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(2).getOrder().numeric()
                + enol.getBond(3).getOrder().numeric()); // around atom4
        Assertions.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(3).getOrder().numeric()
                + enol.getBond(4).getOrder().numeric()); // around atom5
        Assertions.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(4).getOrder().numeric()
                + enol.getBond(5).getOrder().numeric()); // around atom6
    }

    /**
     * Just to ensure it doesn't throw exceptions
     * @throws Exception
     */
    @Test
    void testAcyclic() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(500), () -> {
            String smiles = "CCCCCCC";
            SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
            IAtomContainer molecule = smilesParser.parseSmiles(smiles);

            molecule = fbot.kekuliseAromaticRings(molecule);
            Assertions.assertNotNull(molecule);
        });
    }
}
