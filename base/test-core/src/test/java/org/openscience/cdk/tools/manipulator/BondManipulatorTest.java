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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Bond;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.silent.Atom;

/**
 */
class BondManipulatorTest extends CDKTestCase {

    @Test
    void testGetAtomArray_IBond() {
        IAtom atom1 = new Atom(Elements.CARBON);
        IAtom atom2 = new Atom(Elements.CARBON);
        IBond bond = new Bond(atom1, atom2, Order.TRIPLE);
        IAtom[] atoms = BondManipulator.getAtomArray(bond);
        Assertions.assertEquals(2, atoms.length);
        Assertions.assertEquals(atom1, atoms[0]);
        Assertions.assertEquals(atom2, atoms[1]);
    }

    @Test
    void testIsHigherOrder_IBond_Order_IBond_Order() {
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.SINGLE, Order.SINGLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.SINGLE, Order.DOUBLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.SINGLE, Order.TRIPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.SINGLE, Order.QUADRUPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.SINGLE, Order.QUINTUPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.SINGLE, Order.SEXTUPLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.DOUBLE, Order.SINGLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.DOUBLE, Order.DOUBLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.DOUBLE, Order.TRIPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.DOUBLE, Order.QUADRUPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.DOUBLE, Order.QUINTUPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.DOUBLE, Order.SEXTUPLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.TRIPLE, Order.SINGLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.TRIPLE, Order.DOUBLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.TRIPLE, Order.TRIPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.TRIPLE, Order.QUADRUPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.TRIPLE, Order.QUINTUPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.TRIPLE, Order.SEXTUPLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.QUADRUPLE, Order.SINGLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.QUADRUPLE, Order.DOUBLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.QUADRUPLE, Order.TRIPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.QUADRUPLE, Order.QUADRUPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.QUADRUPLE, Order.QUINTUPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.QUADRUPLE, Order.SEXTUPLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.QUINTUPLE, Order.SINGLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.QUINTUPLE, Order.DOUBLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.QUINTUPLE, Order.TRIPLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.QUINTUPLE, Order.QUADRUPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.QUINTUPLE, Order.QUINTUPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.QUINTUPLE, Order.SEXTUPLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.SEXTUPLE, Order.SINGLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.SEXTUPLE, Order.DOUBLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.SEXTUPLE, Order.TRIPLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.SEXTUPLE, Order.QUADRUPLE));
        Assertions.assertTrue(BondManipulator.isHigherOrder(Order.SEXTUPLE, Order.QUINTUPLE));
        Assertions.assertFalse(BondManipulator.isHigherOrder(Order.SEXTUPLE, Order.SEXTUPLE));
    }

    @Test
    void testIsLowerOrder_IBond_Order_IBond_Order() {
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.SINGLE, Order.SINGLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.SINGLE, Order.DOUBLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.SINGLE, Order.TRIPLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.SINGLE, Order.QUADRUPLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.SINGLE, Order.QUINTUPLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.SINGLE, Order.SEXTUPLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.DOUBLE, Order.SINGLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.DOUBLE, Order.DOUBLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.DOUBLE, Order.TRIPLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.DOUBLE, Order.QUADRUPLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.DOUBLE, Order.QUINTUPLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.DOUBLE, Order.SEXTUPLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.TRIPLE, Order.SINGLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.TRIPLE, Order.DOUBLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.TRIPLE, Order.TRIPLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.TRIPLE, Order.QUADRUPLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.TRIPLE, Order.QUINTUPLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.TRIPLE, Order.SEXTUPLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.QUADRUPLE, Order.SINGLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.QUADRUPLE, Order.DOUBLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.QUADRUPLE, Order.TRIPLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.QUADRUPLE, Order.QUADRUPLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.QUADRUPLE, Order.QUINTUPLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.QUADRUPLE, Order.SEXTUPLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.QUINTUPLE, Order.SINGLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.QUINTUPLE, Order.DOUBLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.QUINTUPLE, Order.TRIPLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.QUINTUPLE, Order.QUADRUPLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.QUINTUPLE, Order.QUINTUPLE));
        Assertions.assertTrue(BondManipulator.isLowerOrder(Order.QUINTUPLE, Order.SEXTUPLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.SEXTUPLE, Order.SINGLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.SEXTUPLE, Order.DOUBLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.SEXTUPLE, Order.TRIPLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.SEXTUPLE, Order.QUADRUPLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.SEXTUPLE, Order.QUINTUPLE));
        Assertions.assertFalse(BondManipulator.isLowerOrder(Order.SEXTUPLE, Order.SEXTUPLE));
    }

    @Test
    void testIncreaseBondOrder_IBond_Order() {
        Assertions.assertEquals(Order.DOUBLE, BondManipulator.increaseBondOrder(Order.SINGLE));
        Assertions.assertEquals(Order.TRIPLE, BondManipulator.increaseBondOrder(Order.DOUBLE));
        Assertions.assertEquals(Order.QUADRUPLE, BondManipulator.increaseBondOrder(Order.TRIPLE));
        Assertions.assertEquals(Order.QUINTUPLE, BondManipulator.increaseBondOrder(Order.QUADRUPLE));
        Assertions.assertEquals(Order.SEXTUPLE, BondManipulator.increaseBondOrder(Order.QUINTUPLE));
        Assertions.assertEquals(Order.SEXTUPLE, BondManipulator.increaseBondOrder(Order.SEXTUPLE));
    }

    @Test
    void testIncreaseBondOrder_IBond() {
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.SINGLE);
        BondManipulator.increaseBondOrder(bond);
        Assertions.assertEquals(Order.DOUBLE, bond.getOrder());
        BondManipulator.increaseBondOrder(bond);
        Assertions.assertEquals(Order.TRIPLE, bond.getOrder());
        BondManipulator.increaseBondOrder(bond);
        Assertions.assertEquals(Order.QUADRUPLE, bond.getOrder());
        BondManipulator.increaseBondOrder(bond);
        Assertions.assertEquals(Order.QUINTUPLE, bond.getOrder());
        BondManipulator.increaseBondOrder(bond);
        Assertions.assertEquals(Order.SEXTUPLE, bond.getOrder());
        BondManipulator.increaseBondOrder(bond);
        Assertions.assertEquals(Order.SEXTUPLE, bond.getOrder());
    }

    @Test
    void testDecreaseBondOrder_IBond_Order() {
        Assertions.assertEquals(Order.SINGLE, BondManipulator.decreaseBondOrder(Order.SINGLE));
        Assertions.assertEquals(Order.SINGLE, BondManipulator.decreaseBondOrder(Order.DOUBLE));
        Assertions.assertEquals(Order.DOUBLE, BondManipulator.decreaseBondOrder(Order.TRIPLE));
        Assertions.assertEquals(Order.TRIPLE, BondManipulator.decreaseBondOrder(Order.QUADRUPLE));
        Assertions.assertEquals(Order.QUADRUPLE, BondManipulator.decreaseBondOrder(Order.QUINTUPLE));
        Assertions.assertEquals(Order.QUINTUPLE, BondManipulator.decreaseBondOrder(Order.SEXTUPLE));
    }

    @Test
    void testDecreaseBondOrder_IBond() {
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.SEXTUPLE);
        BondManipulator.decreaseBondOrder(bond);
        Assertions.assertEquals(Order.QUINTUPLE, bond.getOrder());
        BondManipulator.decreaseBondOrder(bond);
        Assertions.assertEquals(Order.QUADRUPLE, bond.getOrder());
        BondManipulator.decreaseBondOrder(bond);
        Assertions.assertEquals(Order.TRIPLE, bond.getOrder());
        BondManipulator.decreaseBondOrder(bond);
        Assertions.assertEquals(Order.DOUBLE, bond.getOrder());
        BondManipulator.decreaseBondOrder(bond);
        Assertions.assertEquals(Order.SINGLE, bond.getOrder());
        BondManipulator.decreaseBondOrder(bond);
        Assertions.assertEquals(Order.SINGLE, bond.getOrder());
    }

    @Test
    void testDestroyBondOrder_IBond_Order() {
        Assertions.assertEquals(1.0, BondManipulator.destroyBondOrder(Order.SINGLE), 0.00001);
        Assertions.assertEquals(2.0, BondManipulator.destroyBondOrder(Order.DOUBLE), 0.00001);
        Assertions.assertEquals(3.0, BondManipulator.destroyBondOrder(Order.TRIPLE), 0.00001);
        Assertions.assertEquals(4.0, BondManipulator.destroyBondOrder(Order.QUADRUPLE), 0.00001);
        Assertions.assertEquals(5.0, BondManipulator.destroyBondOrder(Order.QUINTUPLE), 0.00001);
        Assertions.assertEquals(6.0, BondManipulator.destroyBondOrder(Order.SEXTUPLE), 0.00001);
    }

    @Test
    void testGetMaximumBondOrder_List() {
        List<IBond> bonds = new ArrayList<>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.SINGLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        Assertions.assertEquals(Order.QUADRUPLE, BondManipulator.getMaximumBondOrder(bonds));
    }

    @Test
    void testGetMaximumBondOrder_Iterator() {
        List<IBond> bonds = new ArrayList<>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.SINGLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        Assertions.assertEquals(Order.QUADRUPLE, BondManipulator.getMaximumBondOrder(bonds.iterator()));
    }

    @Test
    void testGetMaximumBondOrder_IBond_IBond() {
        IBond bond1 = new Bond();
        bond1.setOrder(IBond.Order.SINGLE);
        IBond bond2 = new Bond();
        bond2.setOrder(IBond.Order.QUADRUPLE);
        Assertions.assertEquals(Order.QUADRUPLE, BondManipulator.getMaximumBondOrder(bond1, bond2));
    }

    @Test
    void testGetMaximumBondOrder_IBond_IBond_Unset() {
        IBond bond1 = new Bond();
        bond1.setOrder(IBond.Order.UNSET);
        IBond bond2 = new Bond();
        bond2.setOrder(IBond.Order.DOUBLE);
        Assertions.assertEquals(Order.DOUBLE, BondManipulator.getMaximumBondOrder(bond1, bond2));
    }

    @Test
    void testGetMaximumBondOrder_IBond_IBond_null() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    IBond bond1 = new Bond();
                                    bond1.setOrder(IBond.Order.UNSET);
                                    IBond bond2 = new Bond();
                                    bond2.setOrder(IBond.Order.DOUBLE);
                                    BondManipulator.getMaximumBondOrder(null, bond2);
                                });
    }

    @Test
    void testGetMaximumBondOrder_Unset_Unset() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    BondManipulator.getMaximumBondOrder(IBond.Order.UNSET, IBond.Order.UNSET);
                                });
    }

    @Test
    void testGetMaximumBondOrder_Order_Order() {
        Assertions.assertEquals(Order.QUADRUPLE, BondManipulator.getMaximumBondOrder(Order.SINGLE, Order.QUADRUPLE));
    }

    @Test
    void testGetMaximumBondOrder_Order_Order_Single() {
        Assertions.assertEquals(Order.SINGLE, BondManipulator.getMaximumBondOrder(Order.SINGLE, Order.SINGLE));
    }

    @Test
    void testGetMaximumBondOrder_Order_Order_Unset() {
        Assertions.assertEquals(Order.SINGLE, BondManipulator.getMaximumBondOrder(Order.SINGLE, Order.UNSET));
        Assertions.assertEquals(Order.SINGLE, BondManipulator.getMaximumBondOrder(Order.UNSET, Order.SINGLE));
    }

    @Test
    void testGetMinimumBondOrder_List() {
        List<IBond> bonds = new ArrayList<>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.DOUBLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.TRIPLE);
        bonds.add(bond);
        Assertions.assertEquals(Order.DOUBLE, BondManipulator.getMinimumBondOrder(bonds));
    }

    @Test
    void testGetMinimumBondOrder_Iterator() {
        List<IBond> bonds = new ArrayList<>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.DOUBLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.TRIPLE);
        bonds.add(bond);
        Assertions.assertEquals(Order.DOUBLE, BondManipulator.getMinimumBondOrder(bonds.iterator()));
    }

    @Test
    void testGetMinimumBondOrder_HigherOrders() {
        List<IBond> bonds = new ArrayList<>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.QUINTUPLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.SEXTUPLE);
        bonds.add(bond);
        Assertions.assertEquals(Order.QUINTUPLE, BondManipulator.getMinimumBondOrder(bonds.iterator()));
    }

    @Test
    void testGetSingleBondEquivalentSum_List() {
        List<IBond> bonds = new ArrayList<>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.SINGLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.DOUBLE);
        bonds.add(bond);
        Assertions.assertEquals(3, BondManipulator.getSingleBondEquivalentSum(bonds));
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        Assertions.assertEquals(7, BondManipulator.getSingleBondEquivalentSum(bonds));
    }

    @Test
    void testGetSingleBondEquivalentSum_Iterator() {
        List<IBond> bonds = new ArrayList<>();
        IBond bond = new Bond();
        bond.setOrder(IBond.Order.SINGLE);
        bonds.add(bond);
        bond = new Bond();
        bond.setOrder(IBond.Order.DOUBLE);
        bonds.add(bond);
        Assertions.assertEquals(3, BondManipulator.getSingleBondEquivalentSum(bonds.iterator()));
        bond = new Bond();
        bond.setOrder(IBond.Order.QUADRUPLE);
        bonds.add(bond);
        Assertions.assertEquals(7, BondManipulator.getSingleBondEquivalentSum(bonds.iterator()));
    }

    @Test
    void testCreateBondOrder_double() {
        Assertions.assertEquals(Order.SINGLE, BondManipulator.createBondOrder(1.0));
        Assertions.assertEquals(Order.DOUBLE, BondManipulator.createBondOrder(2.0));
        Assertions.assertEquals(Order.TRIPLE, BondManipulator.createBondOrder(3.0));
        Assertions.assertEquals(Order.QUADRUPLE, BondManipulator.createBondOrder(4.0));
        Assertions.assertEquals(Order.QUINTUPLE, BondManipulator.createBondOrder(5.0));
        Assertions.assertEquals(Order.SEXTUPLE, BondManipulator.createBondOrder(6.0));
    }

}
