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

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Tests CDK's valency checker capabilities in terms of example molecules.
 *
 * @cdk.module  test-valencycheck
 *
 * @author Egon Willighagen &lt;egonw@users.sf.net&gt;
 * @cdk.created 2007-07-28
 */
public class CDKValencyCheckerTest extends CDKTestCase {

    @Test
    public void testInstance() {
        CDKValencyChecker checker = CDKValencyChecker.getInstance(DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(checker);
    }

    @Test
    public void testIsSaturated_IAtomContainer() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer mol = new AtomContainer();
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
        Assert.assertTrue(checker.isSaturated(mol));

        // test methane with implicit hydrogen
        mol = new AtomContainer();
        c = new Atom("C");
        c.setImplicitHydrogenCount(4);
        mol.addAtom(c);
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assert.assertTrue(checker.isSaturated(mol));
    }

    @Test
    public void testIsSaturatedPerAtom() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer mol = new AtomContainer();
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
        Assert.assertTrue(checker.isSaturated(mol));

        // test methane with implicit hydrogen
        mol = new AtomContainer();
        c = new Atom("C");
        c.setImplicitHydrogenCount(4);
        mol.addAtom(c);
        findAndConfigureAtomTypesForAllAtoms(mol);
        for (IAtom atom : mol.atoms()) {
            Assert.assertTrue(checker.isSaturated(atom, mol));
        }
    }

    @Test
    public void testIsSaturated_MissingHydrogens_Methane() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer mol = new AtomContainer();
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        Atom c = new Atom("C");
        mol.addAtom(c);
        c.setImplicitHydrogenCount(3);
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assert.assertFalse(checker.isSaturated(mol));
    }

    /**
     * Tests if the saturation checker considers negative charges.
     */
    @Test
    public void testIsSaturated_NegativelyChargedOxygen() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer mol = new AtomContainer();
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
        Assert.assertTrue(checker.isSaturated(mol));
    }

    /**
     * Tests if the saturation checker considers positive
     * charges.
     */
    @Test
    public void testIsSaturated_PositivelyChargedNitrogen() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer mol = new AtomContainer();
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
        Assert.assertTrue(checker.isSaturated(mol));
    }

    /**
     * Test sulfuric acid.
     */
    @Test
    public void testBug772316() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer mol = new AtomContainer();
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
        Assert.assertTrue(checker.isSaturated(mol));
    }

    /**
     * Tests if the saturation checker gets a proton right.
     */
    @Test
    public void testIsSaturated_Proton() throws Exception {
        // test H+
        IAtomContainer mol = new AtomContainer();
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        Atom hydrogen = new Atom("H");
        hydrogen.setFormalCharge(+1);
        mol.addAtom(hydrogen);
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assert.assertTrue(checker.isSaturated(mol));
    }

    /** TODO: check who added this test. I think Miguel; it seems to be a
     *  resonance structure.
     */
    @Test
    public void test1() throws Exception {
        IAtomContainer mol = new AtomContainer();
        Atom f1 = new Atom("F");
        Atom c2 = new Atom("C");
        Atom c3 = new Atom("C");
        f1.setFormalCharge(1);
        mol.addAtom(f1);
        mol.addAtom(c2);
        mol.addAtom(c3);
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(1, 2, IBond.Order.DOUBLE);
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        findAndConfigureAtomTypesForAllAtoms(mol);
        mol.getAtom(2).setImplicitHydrogenCount(2); // third atom
        Assert.assertTrue(checker.isSaturated(mol));
    }

    @Test
    public void testIsSaturated_MissingBondOrders_Ethane() throws Exception {
        // test ethane with explicit hydrogen
        IAtomContainer mol = new AtomContainer();
        CDKValencyChecker checker = CDKValencyChecker.getInstance(mol.getBuilder());
        Atom c1 = new Atom("C");
        c1.setImplicitHydrogenCount(2);
        c1.setHybridization(IAtomType.Hybridization.SP2);
        Atom c2 = new Atom("C");
        c2.setHybridization(IAtomType.Hybridization.SP2);
        c2.setImplicitHydrogenCount(2);
        mol.addAtom(c1);
        mol.addAtom(c2);
        IBond bond = new Bond(c1, c2, Order.SINGLE);
        mol.addBond(bond);
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assert.assertFalse(checker.isSaturated(mol));

        // sanity check
        bond.setOrder(Order.DOUBLE);
        mol.addBond(bond);
        findAndConfigureAtomTypesForAllAtoms(mol);
        Assert.assertFalse(checker.isSaturated(mol));
    }

    private void findAndConfigureAtomTypesForAllAtoms(IAtomContainer container) throws Exception {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
        Iterator<IAtom> atoms = container.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            IAtomType type = matcher.findMatchingAtomType(container, atom);
            if (type != null) AtomTypeManipulator.configure(atom, type);
        }
    }

}
