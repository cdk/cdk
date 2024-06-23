/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Tests CDK's valency checker capabilities in terms of example molecules.
 *
 * @cdk.module  test-valencycheck
 *
 * @author Egon Willighagen &lt;egonw@users.sf.net&gt;
 * @cdk.created 2007-07-28
 */
class CDKValencyCheckerTest {

    @Test
    void testInstance() {
        CDKValencyChecker checker = CDKValencyChecker.getInstance(SilentChemObjectBuilder.getInstance());
        Assertions.assertNotNull(checker);
    }

    @Test
    void testIsSaturated_IAtomContainer() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        IAtom c = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom h1 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom h2 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom h3 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom h4 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(c);
        mol.addAtom(h1);
        mol.addAtom(h2);
        mol.addAtom(h3);
        mol.addAtom(h4);
        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, h1));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, h2));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, h3));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, h4));
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assertions.assertTrue(checker.isSaturated(mol));

        // test methane with implicit hydrogen
        mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        c = mol.getBuilder().newInstance(IAtom.class, "C");
        c.setImplicitHydrogenCount(4);
        mol.addAtom(c);
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assertions.assertTrue(checker.isSaturated(mol));
    }

    @Test
    void testIsSaturatedPerAtom() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        IAtom c = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom h1 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom h2 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom h3 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom h4 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(c);
        mol.addAtom(h1);
        mol.addAtom(h2);
        mol.addAtom(h3);
        mol.addAtom(h4);
        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, h1));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, h2));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, h3));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, h4));
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assertions.assertTrue(checker.isSaturated(mol));

        // test methane with implicit hydrogen
        mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        c = mol.getBuilder().newInstance(IAtom.class, "C");
        c.setImplicitHydrogenCount(4);
        mol.addAtom(c);
        findAndConfigureAtomTypesForAllAtoms(mol);
        for (IAtom atom : mol.atoms()) {
            Assertions.assertTrue(checker.isSaturated(atom, mol));
        }
    }

    @Test
    void testIsSaturated_MissingHydrogens_Methane() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        IAtom c = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(c);
        c.setImplicitHydrogenCount(3);
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assertions.assertFalse(checker.isSaturated(mol));
    }

    /**
     * Tests if the saturation checker considers negative charges.
     */
    @Test
    void testIsSaturated_NegativelyChargedOxygen() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        IAtom c = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom h1 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom h2 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom h3 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom o = mol.getBuilder().newInstance(IAtom.class, "O");
        o.setFormalCharge(-1);
        mol.addAtom(c);
        mol.addAtom(h1);
        mol.addAtom(h2);
        mol.addAtom(h3);
        mol.addAtom(o);
        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, h1));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, h2));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, h3));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, o));
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assertions.assertTrue(checker.isSaturated(mol));
    }

    /**
     * Tests if the saturation checker considers positive
     * charges.
     */
    @Test
    void testIsSaturated_PositivelyChargedNitrogen() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        IAtom n = mol.getBuilder().newInstance(IAtom.class, "N");
        IAtom h1 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom h2 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom h3 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom h4 = mol.getBuilder().newInstance(IAtom.class, "H");
        n.setFormalCharge(+1);
        mol.addAtom(n);
        mol.addAtom(h1);
        mol.addAtom(h2);
        mol.addAtom(h3);
        mol.addAtom(h4);
        mol.addBond(mol.getBuilder().newInstance(IBond.class, n, h1));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, n, h2));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, n, h3));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, n, h4));
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assertions.assertTrue(checker.isSaturated(mol));
    }

    /**
     * Test sulfuric acid.
     */
    @Test
    void testBug772316() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        IAtom sulphur = mol.getBuilder().newInstance(IAtom.class, "S");
        IAtom o1 = mol.getBuilder().newInstance(IAtom.class, "O");
        IAtom o2 = mol.getBuilder().newInstance(IAtom.class, "O");
        IAtom o3 = mol.getBuilder().newInstance(IAtom.class, "O");
        IAtom o4 = mol.getBuilder().newInstance(IAtom.class, "O");
        IAtom h1 = mol.getBuilder().newInstance(IAtom.class, "H");
        IAtom h2 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(sulphur);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(o3);
        mol.addAtom(o4);
        mol.addAtom(h1);
        mol.addAtom(h2);
        mol.addBond(mol.getBuilder().newInstance(IBond.class, sulphur, o1, IBond.Order.DOUBLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, sulphur, o2, IBond.Order.DOUBLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, sulphur, o3, IBond.Order.SINGLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, sulphur, o4, IBond.Order.SINGLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, h1, o3, IBond.Order.SINGLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, h2, o4, IBond.Order.SINGLE));
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assertions.assertTrue(checker.isSaturated(mol));
    }

    /**
     * Tests if the saturation checker gets a proton right.
     */
    @Test
    void testIsSaturated_Proton() throws Exception {
        // test H+
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        IAtom hydrogen = mol.getBuilder().newInstance(IAtom.class, "H");
        hydrogen.setFormalCharge(+1);
        mol.addAtom(hydrogen);
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assertions.assertTrue(checker.isSaturated(mol));
    }

    /** TODO: check who added this test. I think Miguel; it seems to be a
     *  resonance structure.
     */
    @Test
    void test1() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom f1 = mol.getBuilder().newInstance(IAtom.class, "F");
        IAtom c2 = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom c3 = mol.getBuilder().newInstance(IAtom.class, "C");
        f1.setFormalCharge(1);
        mol.addAtom(f1);
        mol.addAtom(c2);
        mol.addAtom(c3);
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(1, 2, IBond.Order.DOUBLE);
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        findAndConfigureAtomTypesForAllAtoms(mol);
        mol.getAtom(2).setImplicitHydrogenCount(2); // third atom
        Assertions.assertTrue(checker.isSaturated(mol));
    }

    @Test
    void testIsSaturated_MissingBondOrders_Ethane() throws Exception {
        // test ethane with explicit hydrogen
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class, "C");
        c1.setImplicitHydrogenCount(2);
        c1.setHybridization(IAtomType.Hybridization.SP2);
        IAtom c2 = mol.getBuilder().newInstance(IAtom.class, "C");
        c2.setHybridization(IAtomType.Hybridization.SP2);
        c2.setImplicitHydrogenCount(2);
        mol.addAtom(c1);
        mol.addAtom(c2);
        IBond bond = mol.getBuilder().newInstance(IBond.class, c1, c2, Order.SINGLE);
        mol.addBond(bond);
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assertions.assertFalse(checker.isSaturated(mol));

        // sanity check
        bond.setOrder(Order.DOUBLE);
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assertions.assertTrue(checker.isSaturated(mol));
    }

    private void findAndConfigureAtomTypesForAllAtoms(IAtomContainer container) throws Exception {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
        for (IAtom atom : container.atoms()) {
            IAtomType type = matcher.findMatchingAtomType(container, atom);
            if (type != null) AtomTypeManipulator.configure(atom, type);
        }
    }

}
