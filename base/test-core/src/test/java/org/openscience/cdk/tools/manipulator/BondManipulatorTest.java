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
package org.openscience.cdk.tools.manipulator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.silent.Atom;

/**
 * @cdk.module test-core
 */
public class BondManipulatorTest extends CDKTestCase {

    @Test
    public void testGetAtomArray_IBond() {
        IAtom atom1 = new Atom(Elements.CARBON);
        IAtom atom2 = new Atom(Elements.CARBON);
        IBond bond = new Bond(atom1, atom2, Order.TRIPLE);
        IAtom[] atoms = BondManipulator.getAtomArray(bond);
        Assert.assertEquals(2, atoms.length);
        Assert.assertEquals(atom1, atoms[0]);
        Assert.assertEquals(atom2, atoms[1]);
    }

    @Test
    public void testIsHigherOrder_IBond_Order_IBond_Order() {
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.SINGLE, IBond.Order.SINGLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.SINGLE, IBond.Order.DOUBLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.SINGLE, IBond.Order.TRIPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.SINGLE, IBond.Order.QUADRUPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.SINGLE, IBond.Order.QUINTUPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.SINGLE, IBond.Order.SEXTUPLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.DOUBLE, IBond.Order.SINGLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.DOUBLE, IBond.Order.DOUBLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.DOUBLE, IBond.Order.TRIPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.DOUBLE, IBond.Order.QUADRUPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.DOUBLE, IBond.Order.QUINTUPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.DOUBLE, IBond.Order.SEXTUPLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.TRIPLE, IBond.Order.SINGLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.TRIPLE, IBond.Order.DOUBLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.TRIPLE, IBond.Order.TRIPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.TRIPLE, IBond.Order.QUADRUPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.TRIPLE, IBond.Order.QUINTUPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.TRIPLE, IBond.Order.SEXTUPLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.QUADRUPLE, IBond.Order.SINGLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.QUADRUPLE, IBond.Order.DOUBLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.QUADRUPLE, IBond.Order.TRIPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.QUADRUPLE, IBond.Order.QUADRUPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.QUADRUPLE, IBond.Order.QUINTUPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.QUADRUPLE, IBond.Order.SEXTUPLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.QUINTUPLE, IBond.Order.SINGLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.QUINTUPLE, IBond.Order.DOUBLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.QUINTUPLE, IBond.Order.TRIPLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.QUINTUPLE, IBond.Order.QUADRUPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.QUINTUPLE, IBond.Order.QUINTUPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.QUINTUPLE, IBond.Order.SEXTUPLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.SEXTUPLE, IBond.Order.SINGLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.SEXTUPLE, IBond.Order.DOUBLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.SEXTUPLE, IBond.Order.TRIPLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.SEXTUPLE, IBond.Order.QUADRUPLE));
        Assert.assertTrue(BondManipulator.isHigherOrder(IBond.Order.SEXTUPLE, IBond.Order.QUINTUPLE));
        Assert.assertFalse(BondManipulator.isHigherOrder(IBond.Order.SEXTUPLE, IBond.Order.SEXTUPLE));
    }

    @Test
    public void testIsLowerOrder_IBond_Order_IBond_Order() {
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.SINGLE, IBond.Order.SINGLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.SINGLE, IBond.Order.DOUBLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.SINGLE, IBond.Order.TRIPLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.SINGLE, IBond.Order.QUADRUPLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.SINGLE, IBond.Order.QUINTUPLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.SINGLE, IBond.Order.SEXTUPLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.DOUBLE, IBond.Order.SINGLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.DOUBLE, IBond.Order.DOUBLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.DOUBLE, IBond.Order.TRIPLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.DOUBLE, IBond.Order.QUADRUPLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.DOUBLE, IBond.Order.QUINTUPLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.DOUBLE, IBond.Order.SEXTUPLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.TRIPLE, IBond.Order.SINGLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.TRIPLE, IBond.Order.DOUBLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.TRIPLE, IBond.Order.TRIPLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.TRIPLE, IBond.Order.QUADRUPLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.TRIPLE, IBond.Order.QUINTUPLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.TRIPLE, IBond.Order.SEXTUPLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUADRUPLE, IBond.Order.SINGLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUADRUPLE, IBond.Order.DOUBLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUADRUPLE, IBond.Order.TRIPLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUADRUPLE, IBond.Order.QUADRUPLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.QUADRUPLE, IBond.Order.QUINTUPLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.QUADRUPLE, IBond.Order.SEXTUPLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUINTUPLE, IBond.Order.SINGLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUINTUPLE, IBond.Order.DOUBLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUINTUPLE, IBond.Order.TRIPLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUINTUPLE, IBond.Order.QUADRUPLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUINTUPLE, IBond.Order.QUINTUPLE));
        Assert.assertTrue(BondManipulator.isLowerOrder(IBond.Order.QUINTUPLE, IBond.Order.SEXTUPLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.SEXTUPLE, IBond.Order.SINGLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.SEXTUPLE, IBond.Order.DOUBLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.SEXTUPLE, IBond.Order.TRIPLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.SEXTUPLE, IBond.Order.QUADRUPLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.SEXTUPLE, IBond.Order.QUINTUPLE));
        Assert.assertFalse(BondManipulator.isLowerOrder(IBond.Order.SEXTUPLE, IBond.Order.SEXTUPLE));
    }

    @Test
    public void testIncreaseBondOrder_IBond_Order() {
        Assert.assertEquals(IBond.Order.DOUBLE, BondManipulator.increaseBondOrder(IBond.Order.SINGLE));
        Assert.assertEquals(IBond.Order.TRIPLE, BondManipulator.increaseBondOrder(IBond.Order.DOUBLE));
        Assert.assertEquals(IBond.Order.QUADRUPLE, BondManipulator.increaseBondOrder(IBond.Order.TRIPLE));
        Assert.assertEquals(IBond.Order.QUINTUPLE, BondManipulator.increaseBondOrder(IBond.Order.QUADRUPLE));
        Assert.assertEquals(IBond.Order.SEXTUPLE, BondManipulator.increaseBondOrder(IBond.Order.QUINTUPLE));
        Assert.assertEquals(IBond.Order.SEXTUPLE, BondManipulator.increaseBondOrder(IBond.Order.SEXTUPLE));
    }

    @Test
    public void testIncreaseBondOrder_IBond() {
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.SINGLE);
        BondManipulator.increaseBondOrder(bond);
        Assert.assertEquals(IBond.Order.DOUBLE, bond.getOrder());
        BondManipulator.increaseBondOrder(bond);
        Assert.assertEquals(IBond.Order.TRIPLE, bond.getOrder());
        BondManipulator.increaseBondOrder(bond);
        Assert.assertEquals(IBond.Order.QUADRUPLE, bond.getOrder());
        BondManipulator.increaseBondOrder(bond);
        Assert.assertEquals(IBond.Order.QUINTUPLE, bond.getOrder());
        BondManipulator.increaseBondOrder(bond);
        Assert.assertEquals(IBond.Order.SEXTUPLE, bond.getOrder());
        BondManipulator.increaseBondOrder(bond);
        Assert.assertEquals(IBond.Order.SEXTUPLE, bond.getOrder());
    }

    @Test
    public void testDecreaseBondOrder_IBond_Order() {
        Assert.assertEquals(IBond.Order.SINGLE, BondManipulator.decreaseBondOrder(IBond.Order.SINGLE));
        Assert.assertEquals(IBond.Order.SINGLE, BondManipulator.decreaseBondOrder(IBond.Order.DOUBLE));
        Assert.assertEquals(IBond.Order.DOUBLE, BondManipulator.decreaseBondOrder(IBond.Order.TRIPLE));
        Assert.assertEquals(IBond.Order.TRIPLE, BondManipulator.decreaseBondOrder(IBond.Order.QUADRUPLE));
        Assert.assertEquals(IBond.Order.QUADRUPLE, BondManipulator.decreaseBondOrder(IBond.Order.QUINTUPLE));
        Assert.assertEquals(IBond.Order.QUINTUPLE, BondManipulator.decreaseBondOrder(IBond.Order.SEXTUPLE));
    }

    @Test
    public void testDecreaseBondOrder_IBond() {
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.SEXTUPLE);
        BondManipulator.decreaseBondOrder(bond);
        Assert.assertEquals(IBond.Order.QUINTUPLE, bond.getOrder());
        BondManipulator.decreaseBondOrder(bond);
        Assert.assertEquals(IBond.Order.QUADRUPLE, bond.getOrder());
        BondManipulator.decreaseBondOrder(bond);
        Assert.assertEquals(IBond.Order.TRIPLE, bond.getOrder());
        BondManipulator.decreaseBondOrder(bond);
        Assert.assertEquals(IBond.Order.DOUBLE, bond.getOrder());
        BondManipulator.decreaseBondOrder(bond);
        Assert.assertEquals(IBond.Order.SINGLE, bond.getOrder());
        BondManipulator.decreaseBondOrder(bond);
        Assert.assertEquals(IBond.Order.SINGLE, bond.getOrder());
    }

    @Test
    public void testDestroyBondOrder_IBond_Order() {
        Assert.assertEquals(1.0, BondManipulator.destroyBondOrder(IBond.Order.SINGLE), 0.00001);
        Assert.assertEquals(2.0, BondManipulator.destroyBondOrder(IBond.Order.DOUBLE), 0.00001);
        Assert.assertEquals(3.0, BondManipulator.destroyBondOrder(IBond.Order.TRIPLE), 0.00001);
        Assert.assertEquals(4.0, BondManipulator.destroyBondOrder(IBond.Order.QUADRUPLE), 0.00001);
        Assert.assertEquals(5.0, BondManipulator.destroyBondOrder(IBond.Order.QUINTUPLE), 0.00001);
        Assert.assertEquals(6.0, BondManipulator.destroyBondOrder(IBond.Order.SEXTUPLE), 0.00001);
    }

    @Test
    public void testGetMaximumBondOrder_List() {
        List<IBond> bonds = new ArrayList<IBond>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.SINGLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        Assert.assertEquals(IBond.Order.QUADRUPLE, BondManipulator.getMaximumBondOrder(bonds));
    }

    @Test
    public void testGetMaximumBondOrder_Iterator() {
        List<IBond> bonds = new ArrayList<IBond>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.SINGLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        Assert.assertEquals(IBond.Order.QUADRUPLE, BondManipulator.getMaximumBondOrder(bonds.iterator()));
    }

    @Test
    public void testGetMaximumBondOrder_IBond_IBond() {
        IBond bond1 = new Bond();
        bond1.setOrder(IBond.Order.SINGLE);
        IBond bond2 = new Bond();
        bond2.setOrder(IBond.Order.QUADRUPLE);
        Assert.assertEquals(IBond.Order.QUADRUPLE, BondManipulator.getMaximumBondOrder(bond1, bond2));
    }

    @Test
    public void testGetMaximumBondOrder_IBond_IBond_Unset() {
        IBond bond1 = new Bond();
        bond1.setOrder(IBond.Order.UNSET);
        IBond bond2 = new Bond();
        bond2.setOrder(IBond.Order.DOUBLE);
        Assert.assertEquals(IBond.Order.DOUBLE, BondManipulator.getMaximumBondOrder(bond1, bond2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMaximumBondOrder_IBond_IBond_null() {
        IBond bond1 = new Bond();
        bond1.setOrder(IBond.Order.UNSET);
        IBond bond2 = new Bond();
        bond2.setOrder(IBond.Order.DOUBLE);
        BondManipulator.getMaximumBondOrder(null, bond2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMaximumBondOrder_Unset_Unset() {
        BondManipulator.getMaximumBondOrder(IBond.Order.UNSET, IBond.Order.UNSET);
    }

    @Test
    public void testGetMaximumBondOrder_Order_Order() {
        Assert.assertEquals(IBond.Order.QUADRUPLE,
                BondManipulator.getMaximumBondOrder(IBond.Order.SINGLE, IBond.Order.QUADRUPLE));
    }

    @Test
    public void testGetMaximumBondOrder_Order_Order_Single() {
        Assert.assertEquals(IBond.Order.SINGLE,
                BondManipulator.getMaximumBondOrder(IBond.Order.SINGLE, IBond.Order.SINGLE));
    }

    @Test
    public void testGetMaximumBondOrder_Order_Order_Unset() {
        Assert.assertEquals(IBond.Order.SINGLE,
                BondManipulator.getMaximumBondOrder(IBond.Order.SINGLE, IBond.Order.UNSET));
        Assert.assertEquals(IBond.Order.SINGLE,
                BondManipulator.getMaximumBondOrder(IBond.Order.UNSET, IBond.Order.SINGLE));
    }

    @Test
    public void testGetMinimumBondOrder_List() {
        List<IBond> bonds = new ArrayList<IBond>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.DOUBLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.TRIPLE);
        bonds.add(bond);
        Assert.assertEquals(IBond.Order.DOUBLE, BondManipulator.getMinimumBondOrder(bonds));
    }

    @Test
    public void testGetMinimumBondOrder_Iterator() {
        List<IBond> bonds = new ArrayList<IBond>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.DOUBLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.TRIPLE);
        bonds.add(bond);
        Assert.assertEquals(IBond.Order.DOUBLE, BondManipulator.getMinimumBondOrder(bonds.iterator()));
    }

    @Test
    public void testGetMinimumBondOrder_HigherOrders() {
        List<IBond> bonds = new ArrayList<IBond>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.QUINTUPLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.SEXTUPLE);
        bonds.add(bond);
        Assert.assertEquals(IBond.Order.QUINTUPLE, BondManipulator.getMinimumBondOrder(bonds.iterator()));
    }

    @Test
    public void testGetSingleBondEquivalentSum_List() {
        List<IBond> bonds = new ArrayList<IBond>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.SINGLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.DOUBLE);
        bonds.add(bond);
        Assert.assertEquals(3, BondManipulator.getSingleBondEquivalentSum(bonds));
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        Assert.assertEquals(7, BondManipulator.getSingleBondEquivalentSum(bonds));
    }

    @Test
    public void testGetSingleBondEquivalentSum_Iterator() {
        List<IBond> bonds = new ArrayList<IBond>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.SINGLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.DOUBLE);
        bonds.add(bond);
        Assert.assertEquals(3, BondManipulator.getSingleBondEquivalentSum(bonds.iterator()));
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        Assert.assertEquals(7, BondManipulator.getSingleBondEquivalentSum(bonds.iterator()));
    }

    @Test
    public void testCreateBondOrder_double() {
        Assert.assertEquals(IBond.Order.SINGLE, BondManipulator.createBondOrder(1.0));
        Assert.assertEquals(IBond.Order.DOUBLE, BondManipulator.createBondOrder(2.0));
        Assert.assertEquals(IBond.Order.TRIPLE, BondManipulator.createBondOrder(3.0));
        Assert.assertEquals(IBond.Order.QUADRUPLE, BondManipulator.createBondOrder(4.0));
        Assert.assertEquals(IBond.Order.QUINTUPLE, BondManipulator.createBondOrder(5.0));
        Assert.assertEquals(IBond.Order.SEXTUPLE, BondManipulator.createBondOrder(6.0));
    }

}
