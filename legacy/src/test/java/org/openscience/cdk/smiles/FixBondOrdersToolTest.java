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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
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

/**
 *
 * @author         Rajarshi Guha
 * @cdk.created    2006-09-18
 * @cdk.module     test-smiles
 */
public class FixBondOrdersToolTest extends CDKTestCase {

    private static FixBondOrdersTool fbot;

    @BeforeClass
    public static void setup() {
        fbot = new FixBondOrdersTool();
    }

    @Test
    public void testConstructors() {
        // basically: just test that no exception is thrown
        Assert.assertNotNull(new FixBondOrdersTool());
    }

    @Test
    public void testInterruption() {
        fbot.setInterrupted(false);
        Assert.assertFalse(fbot.isInterrupted());
        fbot.setInterrupted(true);
        Assert.assertTrue(fbot.isInterrupted());
        fbot.setInterrupted(false);
    }

    @Test(timeout = 1000)
    public void testPyrrole() throws Exception {
        String smiles = "c2ccc3n([H])c1ccccc1c3(c2)";
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        smilesParser.kekulise(false);
        IAtomContainer molecule = smilesParser.parseSmiles(smiles);
        AtomContainerManipulator.setSingleOrDoubleFlags(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

        molecule = fbot.kekuliseAromaticRings(molecule);
        Assert.assertNotNull(molecule);

        molecule = (IAtomContainer) AtomContainerManipulator.removeHydrogens(molecule);
        int doubleBondCount = 0;
        for (int i = 0; i < molecule.getBondCount(); i++) {
            IBond bond = molecule.getBond(i);
            Assert.assertTrue(bond.getFlag(CDKConstants.ISAROMATIC));
            if (bond.getOrder() == Order.DOUBLE) doubleBondCount++;
        }
        Assert.assertEquals(6, doubleBondCount);
    }

    @Test(timeout = 1000)
    public void testPyrrole_Silent() throws Exception {
        String smiles = "c2ccc3n([H])c1ccccc1c3(c2)";
        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        smilesParser.kekulise(false);
        IAtomContainer molecule = smilesParser.parseSmiles(smiles);
        AtomContainerManipulator.setSingleOrDoubleFlags(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

        molecule = fbot.kekuliseAromaticRings(molecule);
        Assert.assertNotNull(molecule);
        molecule = (IAtomContainer) AtomContainerManipulator.removeHydrogens(molecule);
        int doubleBondCount = 0;
        for (int i = 0; i < molecule.getBondCount(); i++) {
            IBond bond = molecule.getBond(i);
            Assert.assertTrue(bond.getFlag(CDKConstants.ISAROMATIC));
            if (bond.getOrder() == Order.DOUBLE) doubleBondCount++;
        }
        Assert.assertEquals(6, doubleBondCount);
    }

    @Test
    public void testLargeRingSystem() throws Exception {
        String smiles = "O=C1Oc6ccccc6(C(O)C1C5c2ccccc2CC(c3ccc(cc3)c4ccccc4)C5)";
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = smilesParser.parseSmiles(smiles);

        molecule = fbot.kekuliseAromaticRings(molecule);
        Assert.assertNotNull(molecule);

        molecule = (IAtomContainer) AtomContainerManipulator.removeHydrogens(molecule);
        Assert.assertEquals(34, molecule.getAtomCount());

        // we should have 14 double bonds
        int doubleBondCount = 0;
        for (int i = 0; i < molecule.getBondCount(); i++) {
            IBond bond = molecule.getBond(i);
            if (bond.getOrder() == Order.DOUBLE) doubleBondCount++;
        }
        Assert.assertEquals(13, doubleBondCount);
    }

    /**
     * @cdk.bug 3506770
     */
    @Test
    public void testLargeBioclipseUseCase() throws Exception {
        String smiles = "COc1ccc2[C@@H]3[C@H](COc2c1)C(C)(C)OC4=C3C(=O)C(=O)C5=C4OC(C)(C)[C@@H]6COc7cc(OC)ccc7[C@H]56";
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = smilesParser.parseSmiles(smiles);

        molecule = fbot.kekuliseAromaticRings(molecule);
        Assert.assertNotNull(molecule);

        molecule = (IAtomContainer) AtomContainerManipulator.removeHydrogens(molecule);
        Assert.assertEquals(40, molecule.getAtomCount());

        // we should have 14 double bonds
        int doubleBondCount = 0;
        for (int i = 0; i < molecule.getBondCount(); i++) {
            IBond bond = molecule.getBond(i);
            if (bond.getOrder() == Order.DOUBLE) doubleBondCount++;
        }
        Assert.assertEquals(10, doubleBondCount);
    }

    /**
     * @cdk.inchi InChI=1/C4H5N/c1-2-4-5-3-1/h1-5H
     */
    @Test
    public void xtestPyrrole() throws Exception {
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
        Assert.assertNotNull(enol);
        //Assert.assertTrue(fbot.isOK(enol));

        // now check whether it did the right thing
        Assert.assertEquals(Order.DOUBLE, enol.getBond(0).getOrder());;
        Assert.assertEquals(Order.SINGLE, enol.getBond(1).getOrder());;
        Assert.assertEquals(Order.DOUBLE, enol.getBond(2).getOrder());;
        Assert.assertEquals(Order.SINGLE, enol.getBond(3).getOrder());;
        Assert.assertEquals(Order.SINGLE, enol.getBond(4).getOrder());;
    }

    @Test
    public void xtestPyridine() throws Exception {
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
        Assert.assertNotNull(enol);
        // Assert.assertTrue(dbst.isOK(enol));

        // now check whether it did the right thing
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(0).getOrder().numeric()
                + enol.getBond(5).getOrder().numeric()); // around atom1
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(0).getOrder().numeric()
                + enol.getBond(1).getOrder().numeric()); // around atom2
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(1).getOrder().numeric()
                + enol.getBond(2).getOrder().numeric()); // around atom3
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(2).getOrder().numeric()
                + enol.getBond(3).getOrder().numeric()); // around atom4
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(3).getOrder().numeric()
                + enol.getBond(4).getOrder().numeric()); // around atom5
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(4).getOrder().numeric()
                + enol.getBond(5).getOrder().numeric()); // around atom6
    }

    /**
     * @cdk.inchi InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H
     * @cdk.bug   1931262
     */
    @Test
    public void xtestBenzene() throws Exception {
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
        Assert.assertNotNull(enol);
        //Assert.assertTrue(dbst.isOK(enol));

        // now check whether it did the right thing
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(0).getOrder().numeric()
                + enol.getBond(5).getOrder().numeric()); // around atom1
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(0).getOrder().numeric()
                + enol.getBond(1).getOrder().numeric()); // around atom2
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(1).getOrder().numeric()
                + enol.getBond(2).getOrder().numeric()); // around atom3
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(2).getOrder().numeric()
                + enol.getBond(3).getOrder().numeric()); // around atom4
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(3).getOrder().numeric()
                + enol.getBond(4).getOrder().numeric()); // around atom5
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), enol
                .getBond(4).getOrder().numeric()
                + enol.getBond(5).getOrder().numeric()); // around atom6
    }

    /**
     * Just to ensure it doesn't throw exceptions
     * @throws Exception
     */
    @Test(timeout = 1000)
    public void testAcyclic() throws Exception {
        String smiles = "CCCCCCC";
        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer molecule = smilesParser.parseSmiles(smiles);

        molecule = fbot.kekuliseAromaticRings(molecule);
        Assert.assertNotNull(molecule);

    }
}
