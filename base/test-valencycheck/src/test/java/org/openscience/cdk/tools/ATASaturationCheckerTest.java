/*  Copyright (C) 2012  Klas Jönsson <klas.joensson@gmail.com>
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
package org.openscience.cdk.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * A test class for the AtomTypeAwereSaturationChecker-class
 *
 * @author Klas J&ouml;nsson
 * @cdk.created 2012-04-18
 * @cdk.module  test-valencycheck
 */
class ATASaturationCheckerTest extends CDKTestCase {

    private SaturationChecker                      satcheck   = null;
    boolean                                standAlone = false;
    private static final SmilesParser            sp         = new SmilesParser(SilentChemObjectBuilder.getInstance());
    private final AtomTypeAwareSaturationChecker atasc      = new AtomTypeAwareSaturationChecker();

    @BeforeEach
    void setUp() throws Exception {
        satcheck = new SaturationChecker();
    }

    /**
     * A test of decideBondOrder(IAtomContainer) with a molecule we created
     * from scratch.
     * @throws Exception
     */
    @Test
    void testASimpleCarbonRing() throws Exception {
        // First we create a simple carbon ring to play with...
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtomType carbon = mol.getBuilder().newInstance(IAtomType.class, Elements.CARBON);

        IAtom a0 = mol.newAtom(6, 1);
        a0.setHybridization(IAtomType.Hybridization.SP2);
        AtomTypeManipulator.configureUnsetProperties(a0, carbon);
        IAtom a1 = mol.newAtom(6, 1);
        a1.setHybridization(IAtomType.Hybridization.SP2);
        AtomTypeManipulator.configureUnsetProperties(a1, carbon);
        IAtom a2 = mol.newAtom(6, 1);
        a2.setHybridization(IAtomType.Hybridization.SP2);
        AtomTypeManipulator.configureUnsetProperties(a2, carbon);
        IAtom a3 = mol.newAtom(6, 1);
        a3.setHybridization(IAtomType.Hybridization.SP2);
        AtomTypeManipulator.configureUnsetProperties(a3, carbon);
        IAtom a4 = mol.newAtom(6, 1);
        a4.setHybridization(IAtomType.Hybridization.SP2);
        AtomTypeManipulator.configureUnsetProperties(a4, carbon);
        IAtom a5 = mol.newAtom(6, 1);
        a5.setHybridization(IAtomType.Hybridization.SP2);
        AtomTypeManipulator.configureUnsetProperties(a5, carbon);

        mol.addAtom(a0);
        mol.addAtom(a1);
        mol.addAtom(a2);
        mol.addAtom(a3);
        mol.addAtom(a4);
        mol.addAtom(a5);

        IBond b0 = mol.newBond(a0, a1);
        b0.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
        IBond b1 = mol.newBond(a1, a2);
        b1.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
        IBond b2 = mol.newBond(a2, a3);
        b2.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
        IBond b3 = mol.newBond(a3, a4);
        b3.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
        IBond b4 = mol.newBond(a4, a5);
        b4.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
        IBond b5 = mol.newBond(a5, a0);
        b5.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        AtomTypeTools att = new AtomTypeTools();
        att.assignAtomTypePropertiesToAtom(mol, false);

        // ...then we send it to the method we want to test...
        atasc.decideBondOrder(mol, false);

        Assertions.assertEquals(Order.DOUBLE, b0.getOrder());
        Assertions.assertEquals(Order.SINGLE, b1.getOrder());
        Assertions.assertEquals(Order.DOUBLE, b2.getOrder());
        Assertions.assertEquals(Order.SINGLE, b3.getOrder());
        Assertions.assertEquals(Order.DOUBLE, b4.getOrder());
        Assertions.assertEquals(Order.SINGLE, b5.getOrder());

        Assertions.assertTrue(satcheck.isSaturated(a0, mol));

    }

    /**
     * A test of decideBondOrder(IAtomContainer) with a molecule we created
     * from a SMILES.
     * @throws Exception
     */
    @Test
    void testQuinone() throws Exception {
        IAtomContainer mol = sp.parseSmiles("O=c1ccc(=O)cc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

        atasc.decideBondOrder(mol, true);

        Assertions.assertSame(IAtomType.Hybridization.SP2, mol.getAtom(1).getHybridization());

        Assertions.assertEquals(IElement.C, (int) mol.getBond(0).getEnd().getAtomicNumber());
        Assertions.assertEquals(IElement.O, (int) mol.getBond(0).getBegin().getAtomicNumber());
        Assertions.assertEquals(Order.DOUBLE, mol.getBond(0).getOrder());

        Assertions.assertEquals(IElement.C, (int) mol.getBond(1).getBegin().getAtomicNumber());
        Assertions.assertEquals(IElement.C, (int) mol.getBond(1).getEnd().getAtomicNumber());
        Assertions.assertEquals(Order.SINGLE, mol.getBond(1).getOrder());

        Assertions.assertEquals(IElement.C, (int) mol.getBond(2).getBegin().getAtomicNumber());
        Assertions.assertEquals(IElement.C, (int) mol.getBond(2).getEnd().getAtomicNumber());
        Assertions.assertEquals(Order.DOUBLE, mol.getBond(2).getOrder());

        Assertions.assertEquals(IElement.C, (int) mol.getBond(3).getBegin().getAtomicNumber());
        Assertions.assertEquals(IElement.C, (int) mol.getBond(3).getEnd().getAtomicNumber());
        Assertions.assertEquals(Order.SINGLE, mol.getBond(3).getOrder());

        Assertions.assertEquals(IElement.O, (int) mol.getBond(4).getEnd().getAtomicNumber());
        Assertions.assertEquals(IElement.C, (int) mol.getBond(4).getBegin().getAtomicNumber());
        Assertions.assertEquals(Order.DOUBLE, mol.getBond(4).getOrder());

        Assertions.assertEquals(IElement.C, (int) mol.getBond(5).getBegin().getAtomicNumber());
        Assertions.assertEquals(IElement.C, (int) mol.getBond(5).getEnd().getAtomicNumber());
        Assertions.assertSame(Order.SINGLE, mol.getBond(5).getOrder());

        Assertions.assertEquals(IElement.C, (int) mol.getBond(6).getBegin().getAtomicNumber());
        Assertions.assertEquals(IElement.C, (int) mol.getBond(6).getEnd().getAtomicNumber());
        Assertions.assertEquals(Order.DOUBLE, mol.getBond(6).getOrder());

        Assertions.assertEquals(IElement.C, (int) mol.getBond(7).getBegin().getAtomicNumber());
        Assertions.assertEquals(IElement.C, (int) mol.getBond(7).getEnd().getAtomicNumber());
        Assertions.assertEquals(Order.SINGLE, mol.getBond(7).getOrder());

        Assertions.assertEquals(mol.getBond(0).getEnd(), mol.getBond(7).getBegin());
    }

    /**
     * A test of decideBondOrder(IAtomContainer) with a simple carbon ring we
     * created from a SMILES.
     * @throws CDKException
     */
    @Test
    void testASimpleCarbonRing2() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("c1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

        atasc.decideBondOrder(mol, true);

        Assertions.assertEquals(mol.getAtom(1).getHybridization(), IAtomType.Hybridization.SP2);
        Assertions.assertEquals(mol.getBond(0).getOrder(), Order.DOUBLE);
        Assertions.assertEquals(mol.getBond(1).getOrder(), Order.SINGLE);
        Assertions.assertEquals(mol.getBond(2).getOrder(), Order.DOUBLE);
        Assertions.assertEquals(mol.getBond(3).getOrder(), Order.SINGLE);
        Assertions.assertEquals(mol.getBond(4).getOrder(), Order.DOUBLE);
        Assertions.assertEquals(mol.getBond(5).getOrder(), Order.SINGLE);
    }

    /**
     * This method tests the AtomTypeAwareSaturnationChecker with a large ring
     * system.
     * @throws Exception
     */
    @Test
    void testALargeRingSystem() throws Exception {
        // Should have 13 double bonds.
        String smiles = "O=C1Oc6ccccc6(C(O)C1C5c2ccccc2CC(c3ccc(cc3)c4ccccc4)C5)";
        IAtomContainer mol = sp.parseSmiles(smiles);

        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder().equals(Order.DOUBLE)) doubleBondCount++;
        }
        Assertions.assertEquals(13, doubleBondCount);
    }

    /**
     * This do the same as the method above, but with five other large ring
     * systems.
     * @throws Exception
     */
    @Test
    void testLargeRingSystem1() throws Exception {
        // Should have 6 double bonds
        String smiles = "c1ccc2c(c1)CC4NCCc3cccc2c34";
        IAtomContainer mol = sp.parseSmiles(smiles);
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder().equals(Order.DOUBLE)) doubleBondCount++;
        }
        Assertions.assertEquals(6, doubleBondCount);
    }

    @Test
    void testLargeRingSystem2() throws Exception {
        // Should have 8 double bonds
        String smiles = "Oc1ccc(cc1)c1coc2c(c1=O)c(O)cc(c2)O";
        IAtomContainer mol = sp.parseSmiles(smiles);
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder().equals(Order.DOUBLE)) doubleBondCount++;
        }
        Assertions.assertEquals(8, doubleBondCount);
    }

    @Test
    void testADoubleRingWithANitrogenAtom() throws Exception {
        /*
         * Should have 4 double bonds and all three bonds to/from the nitrogen
         * should be single
         */
        String smiles = "c1ccn2cccc2c1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        AtomContainerManipulator.percieveAtomTypesAndConfigureUnsetProperties(mol);

        IAtom nitrogen = mol.getAtom(3);

        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0, singleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder().equals(Order.DOUBLE)) doubleBondCount++;
            if (bond.contains(nitrogen)) if (bond.getOrder().equals(Order.SINGLE)) singleBondCount++;
        }
        Assertions.assertEquals(4, doubleBondCount);
        Assertions.assertEquals(3, singleBondCount);
    }

    @Test
    void testLargeRingSystem3() throws Exception {
        // Should have 17 double bonds
        String smiles = "O=C5C=C(O)C(N=Nc1ccc(cc1)Nc2ccccc2)=CC5(=NNc3ccc(cc3)Nc4ccccc4)";
        IAtomContainer mol = sp.parseSmiles(smiles);
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder().equals(Order.DOUBLE)) doubleBondCount++;
        }
        Assertions.assertEquals(17, doubleBondCount);
    }

    @Test
    void testLargeRingSystem4() throws Exception {
        // Should have 18 double bonds
        String smiles = "c1ccc(cc1)[Sn](c2ccccc2)(c3ccccc3)S[Sn](c4ccccc4)(c5ccccc5)c6ccccc6";
        IAtomContainer mol = sp.parseSmiles(smiles);
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder().equals(Order.DOUBLE)) doubleBondCount++;
        }
        Assertions.assertEquals(18, doubleBondCount);
    }

    @Test
    void testLargeRingSystem5() throws Exception {
        // Should have 24 double bonds
        String smiles = "O=C1c2ccccc2C(=O)c3c1ccc4c3[nH]c5c6C(=O)c7ccccc7C(=O)c6c8[nH]c9c%10C(=O)c%11ccccc%11C(=O)c%10ccc9c8c45";

        IAtomContainer mol = sp.parseSmiles(smiles);
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder().equals(Order.DOUBLE)) doubleBondCount++;
        }
        Assertions.assertEquals(24, doubleBondCount);
    }

    /**
     * From DeduceBondSystemToolTest
     *
     * @throws Exception
     */
    @Test
    void testLargeBioclipseUseCase() throws Exception {
        // Should have 14 double bonds
        String smiles = "COc1ccc2[C@@H]3[C@H](COc2c1)C(C)(C)OC4=C3C(=O)C(=O)C5=C4OC(C)(C)[C@@H]6COc7cc(OC)ccc7[C@H]56";
        IAtomContainer molecule = sp.parseSmiles(smiles);

        atasc.decideBondOrder(molecule, true);

        int doubleBondCount = 0;
        for (int i = 0; i < molecule.getBondCount(); i++) {
            IBond bond = molecule.getBond(i);
            if (bond.getOrder() == Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(10, doubleBondCount);
    }

    @Test
    void testCyclobutadiene() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("c1ccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

        atasc.decideBondOrder(mol, true);
        Assertions.assertEquals(mol.getAtom(1).getHybridization(), IAtomType.Hybridization.SP2);
        Assertions.assertEquals(Order.DOUBLE, mol.getBond(0).getOrder());
        Assertions.assertEquals(Order.SINGLE, mol.getBond(1).getOrder());
        Assertions.assertEquals(Order.DOUBLE, mol.getBond(2).getOrder());
        Assertions.assertEquals(Order.SINGLE, mol.getBond(3).getOrder());
    }

    @Test
    void testPyrrole() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("c1c[nH]cc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

        atasc.decideBondOrder(mol, true);
        Assertions.assertEquals(mol.getAtom(1).getHybridization(), IAtomType.Hybridization.SP2);
        Assertions.assertEquals(Order.DOUBLE, mol.getBond(0).getOrder());
        Assertions.assertEquals(Order.SINGLE, mol.getBond(1).getOrder());
        Assertions.assertEquals(Order.SINGLE, mol.getBond(2).getOrder());
        Assertions.assertEquals(Order.DOUBLE, mol.getBond(3).getOrder());
        Assertions.assertEquals(Order.SINGLE, mol.getBond(4).getOrder());
    }

    @Test
    void testFurane() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("c1cocc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

        atasc.decideBondOrder(mol, true);
        Assertions.assertEquals(mol.getAtom(1).getHybridization(), IAtomType.Hybridization.SP2);
        Assertions.assertEquals(Order.DOUBLE, mol.getBond(0).getOrder());
        Assertions.assertEquals(Order.SINGLE, mol.getBond(1).getOrder());
        //bond to oxygen
        Assertions.assertEquals(Order.SINGLE, mol.getBond(2).getOrder());
        //bond to oxygen
        Assertions.assertEquals(Order.DOUBLE, mol.getBond(3).getOrder());
        Assertions.assertEquals(Order.SINGLE, mol.getBond(4).getOrder());
    }

    @Test
    void testAnOtherDoubleRing() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("c1cccc2cccc2c1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

        atasc.decideBondOrder(mol, true);
        Assertions.assertEquals(mol.getAtom(1).getHybridization(), IAtomType.Hybridization.SP2);
        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(5, doubleBondCount);
    }

    @Test
    void testAnOtherRingSystem() throws CDKException {
        // Should have 7 double bonds
        //    	IAtomContainer mol = sp.parseSmiles("O=c2c1ccccc1c3ccccc23");
        // Should have 6 double bonds
        IAtomContainer mol = sp.parseSmiles("o2c1ccccc1c3c2cccc3");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        atasc.decideBondOrder(mol, true);
        Assertions.assertEquals(mol.getAtom(1).getHybridization(), IAtomType.Hybridization.SP2);
        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(6, doubleBondCount);
    }

    @Test
    void testAnOtherRingSystem2() throws CDKException {
        // Should have 7 double bonds
        IAtomContainer mol = sp.parseSmiles("O=c2c1ccccc1c3ccccc23");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        atasc.decideBondOrder(mol, true);
        Assertions.assertEquals(mol.getAtom(1).getHybridization(), IAtomType.Hybridization.SP2);
        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(7, doubleBondCount);
    }

    @Test
    void testAzulene() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("c12c(ccccc2)ccc1");
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(5, doubleBondCount);

        Assertions.assertEquals(Order.SINGLE, mol.getBond(0).getOrder());
    }

    @Test
    void mailCase1a() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("o1cccc1");
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(2, doubleBondCount);
    }

    @Test
    void mailCase1b() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("O1cccc1");
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(2, doubleBondCount);
    }

    @Test
    void mailCase3b() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("c1ccccc1Oc1cOcc1");
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(5, doubleBondCount);
    }

    @Test
    void mailCase4() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("o2c1ccccc1c3c2cccc3");
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(6, doubleBondCount);
    }

    @Test
    void mailCase5a() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("c5cc2ccc1ccccc1c2c6c4c3ccccc3ccc4ccc56");
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(13, doubleBondCount);
    }

    @Test
    void mailCase5b() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("c1cc2ccc3ccc4ccc5ccc6ccc1c7c2c3c4c5c67");

        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }

        Assertions.assertEquals(12, doubleBondCount);
    }

    @Test
    void mailCase6() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("c1ccc2c(c1)cc-3c4c2cccc4-c5c3cccc5");
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(10, doubleBondCount);
    }

    @Test
    void testNPolycyclicCompounds() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("n12cncc1cccc2");
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(4, doubleBondCount);
    }

    @Test
    void testIndoles2() throws CDKException {
        IAtomContainer mol = sp
                .parseSmiles("Cl.Cl.Oc1ccc2CC3[C@](Cc4c(-c5ccccc5)c(C)[nH0](Cc5ccccc5)c4[C@@H]([C@](CCN3CC3CC3)(c2c1O1)2)1)2(O)");
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(11, doubleBondCount);
    }

    @Test
    void someOtherWieredDoubleRing() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("CCc2c3ccccc3[nH]c2");
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds())
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;

        Assertions.assertEquals(4, doubleBondCount);
    }

    @Test
    void testButadieneSmile() throws Exception {
        IAtomContainer mol = sp.parseSmiles("cccc");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        /*
         * The SMILES-parser does not seams raise the SINGLE_OR_DOUBLE-flag if a
         * molecule don't have any rings
         */
        for (IBond bond : mol.bonds())
            bond.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);

        atasc.decideBondOrder(mol, false);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(2, doubleBondCount);
    }

    @Test
    void testButadiene() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtomType carbon = mol.getBuilder().newInstance(IAtomType.class, Elements.CARBON);

        IAtom a0 = mol.newAtom(6, 2);
        a0.setHybridization(IAtomType.Hybridization.SP2);
        AtomTypeManipulator.configureUnsetProperties(a0, carbon);
        IAtom a1 = mol.newAtom(6, 1);
        a1.setHybridization(IAtomType.Hybridization.SP2);
        AtomTypeManipulator.configureUnsetProperties(a1, carbon);
        IAtom a2 = mol.newAtom(6, 1);
        a2.setHybridization(IAtomType.Hybridization.SP2);
        AtomTypeManipulator.configureUnsetProperties(a2, carbon);
        IAtom a3 = mol.newAtom(6, 2);
        a3.setHybridization(IAtomType.Hybridization.SP2);
        AtomTypeManipulator.configureUnsetProperties(a3, carbon);

        mol.addAtom(a0);
        mol.addAtom(a1);
        mol.addAtom(a2);
        mol.addAtom(a3);

        IBond b0 = mol.newBond(a0, a1);
        b0.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
        IBond b1 = mol.newBond(a1, a2);
        b1.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
        IBond b2 = mol.newBond(a2, a3);
        b2.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        AtomTypeTools att = new AtomTypeTools();
        att.assignAtomTypePropertiesToAtom(mol, true);

        atasc.decideBondOrder(mol, true);

        Assertions.assertEquals(Order.DOUBLE, mol.getBond(0).getOrder());
        Assertions.assertEquals(Order.SINGLE, mol.getBond(1).getOrder());
        Assertions.assertEquals(Order.DOUBLE, mol.getBond(2).getOrder());
    }

    @Test
    void testMolFromSdf() throws CDKException {
        IAtomContainer mol = sp.parseSmiles("OC(COc1ccccc1CC=C)CNC(C)C");
        atasc.decideBondOrder(mol, true);

        int doubleBondCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE) doubleBondCount++;
        }
        Assertions.assertEquals(4, doubleBondCount);
    }

    @Test
    void testOnlyOneAtom() throws CDKException {
        /*
         * If all bonds in the molecule are implicit, then it was noticed that
         * the SatChecker failed
         */
        IAtomContainer mol = sp.parseSmiles("C");

        int preBondCount = mol.getBondCount();
        atasc.decideBondOrder(mol);

        Assertions.assertEquals(preBondCount, mol.getBondCount());
    }

    @Test
    void testBug3394() {
        IAtomContainer mol;
        try {
            mol = sp.parseSmiles("OCC1OC(O)C(O)C(Op2(OC3C(O)C(O)OC(CO)C3O)np(OC4C(O)C(O)OC(CO)C4O)(OC5C(O)C(O)OC(CO)C5O)np(OC6C(O)C(O)OC(CO)C6O)(OC7C(O)C(O)OC(CO)C7O)n2)C1O");
            atasc.decideBondOrder(mol);
        } catch (InvalidSmilesException e) {
            Assertions.fail("SMILES failed");
        } catch (CDKException e) {
            Assertions.fail("ATASatChecer failed");
        }

    }
}
