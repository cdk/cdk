/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.tools.manipulator;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.nonotify.NNAtom;
import org.openscience.cdk.nonotify.NNBond;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * @cdk.module test-atomtype
 */
public class BondManipulatorTest extends CDKTestCase {
    
    public BondManipulatorTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(BondManipulatorTest.class);
	}

	public void testGetAtomArray_IBond() {
		IAtom atom1 = new NNAtom(Elements.CARBON);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		IBond bond = new NNBond(atom1, atom2, CDKConstants.BONDORDER_TRIPLE);
		IAtom[] atoms = BondManipulator.getAtomArray(bond);
		assertEquals(2, atoms.length);
		assertEquals(atom1, atoms[0]);
		assertEquals(atom2, atoms[1]);
	}
	
	public void testIsHigherOrder_IBond_Order_IBond_Order() {
		assertFalse(BondManipulator.isHigherOrder(IBond.Order.SINGLE, IBond.Order.SINGLE));
		assertFalse(BondManipulator.isHigherOrder(IBond.Order.SINGLE, IBond.Order.DOUBLE));
		assertFalse(BondManipulator.isHigherOrder(IBond.Order.SINGLE, IBond.Order.TRIPLE));
		assertFalse(BondManipulator.isHigherOrder(IBond.Order.SINGLE, IBond.Order.QUADRUPLE));
		assertTrue(BondManipulator.isHigherOrder(IBond.Order.DOUBLE, IBond.Order.SINGLE));
		assertFalse(BondManipulator.isHigherOrder(IBond.Order.DOUBLE, IBond.Order.DOUBLE));
		assertFalse(BondManipulator.isHigherOrder(IBond.Order.DOUBLE, IBond.Order.TRIPLE));
		assertFalse(BondManipulator.isHigherOrder(IBond.Order.DOUBLE, IBond.Order.QUADRUPLE));
		assertTrue(BondManipulator.isHigherOrder(IBond.Order.TRIPLE, IBond.Order.SINGLE));
		assertTrue(BondManipulator.isHigherOrder(IBond.Order.TRIPLE, IBond.Order.DOUBLE));
		assertFalse(BondManipulator.isHigherOrder(IBond.Order.TRIPLE, IBond.Order.TRIPLE));
		assertFalse(BondManipulator.isHigherOrder(IBond.Order.TRIPLE, IBond.Order.QUADRUPLE));
		assertTrue(BondManipulator.isHigherOrder(IBond.Order.QUADRUPLE, IBond.Order.SINGLE));
		assertTrue(BondManipulator.isHigherOrder(IBond.Order.QUADRUPLE, IBond.Order.DOUBLE));
		assertTrue(BondManipulator.isHigherOrder(IBond.Order.QUADRUPLE, IBond.Order.TRIPLE));
		assertFalse(BondManipulator.isHigherOrder(IBond.Order.QUADRUPLE, IBond.Order.QUADRUPLE));
	}
	
	public void testIsLowerOrder_IBond_Order_IBond_Order() {
		assertFalse(BondManipulator.isLowerOrder(IBond.Order.SINGLE, IBond.Order.SINGLE));
		assertTrue(BondManipulator.isLowerOrder(IBond.Order.SINGLE, IBond.Order.DOUBLE));
		assertTrue(BondManipulator.isLowerOrder(IBond.Order.SINGLE, IBond.Order.TRIPLE));
		assertTrue(BondManipulator.isLowerOrder(IBond.Order.SINGLE, IBond.Order.QUADRUPLE));
		assertFalse(BondManipulator.isLowerOrder(IBond.Order.DOUBLE, IBond.Order.SINGLE));
		assertFalse(BondManipulator.isLowerOrder(IBond.Order.DOUBLE, IBond.Order.DOUBLE));
		assertTrue(BondManipulator.isLowerOrder(IBond.Order.DOUBLE, IBond.Order.TRIPLE));
		assertTrue(BondManipulator.isLowerOrder(IBond.Order.DOUBLE, IBond.Order.QUADRUPLE));
		assertFalse(BondManipulator.isLowerOrder(IBond.Order.TRIPLE, IBond.Order.SINGLE));
		assertFalse(BondManipulator.isLowerOrder(IBond.Order.TRIPLE, IBond.Order.DOUBLE));
		assertFalse(BondManipulator.isLowerOrder(IBond.Order.TRIPLE, IBond.Order.TRIPLE));
		assertTrue(BondManipulator.isLowerOrder(IBond.Order.TRIPLE, IBond.Order.QUADRUPLE));
		assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUADRUPLE, IBond.Order.SINGLE));
		assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUADRUPLE, IBond.Order.DOUBLE));
		assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUADRUPLE, IBond.Order.TRIPLE));
		assertFalse(BondManipulator.isLowerOrder(IBond.Order.QUADRUPLE, IBond.Order.QUADRUPLE));
	}
	public void testIncreaseBondOrder_IBond_Order() {
		assertEquals(IBond.Order.DOUBLE, BondManipulator.increaseBondOrder(IBond.Order.SINGLE));
		assertEquals(IBond.Order.TRIPLE, BondManipulator.increaseBondOrder(IBond.Order.DOUBLE));
		assertEquals(IBond.Order.QUADRUPLE, BondManipulator.increaseBondOrder(IBond.Order.TRIPLE));
		assertEquals(IBond.Order.QUADRUPLE, BondManipulator.increaseBondOrder(IBond.Order.QUADRUPLE));
	}
	
	public void testIncreaseBondOrder_IBond() {
		IBond bond = new Bond(); bond.setOrder(IBond.Order.SINGLE);
		BondManipulator.increaseBondOrder(bond);
		assertEquals(IBond.Order.DOUBLE, bond.getOrder());
		BondManipulator.increaseBondOrder(bond);
		assertEquals(IBond.Order.TRIPLE, bond.getOrder());
		BondManipulator.increaseBondOrder(bond);
		assertEquals(IBond.Order.QUADRUPLE, bond.getOrder());
		BondManipulator.increaseBondOrder(bond);
		assertEquals(IBond.Order.QUADRUPLE, bond.getOrder());
	}
	
	public void testDecreaseBondOrder_IBond_Order() {
		assertEquals(IBond.Order.SINGLE, BondManipulator.decreaseBondOrder(IBond.Order.SINGLE));
		assertEquals(IBond.Order.SINGLE, BondManipulator.decreaseBondOrder(IBond.Order.DOUBLE));
		assertEquals(IBond.Order.DOUBLE, BondManipulator.decreaseBondOrder(IBond.Order.TRIPLE));
		assertEquals(IBond.Order.TRIPLE, BondManipulator.decreaseBondOrder(IBond.Order.QUADRUPLE));
	}
	
	public void testDecreaseBondOrder_IBond() {
		IBond bond = new Bond(); bond.setOrder(IBond.Order.QUADRUPLE);
		BondManipulator.decreaseBondOrder(bond);
		assertEquals(IBond.Order.TRIPLE, bond.getOrder());
		BondManipulator.decreaseBondOrder(bond);
		assertEquals(IBond.Order.DOUBLE, bond.getOrder());
		BondManipulator.decreaseBondOrder(bond);
		assertEquals(IBond.Order.SINGLE, bond.getOrder());
		BondManipulator.decreaseBondOrder(bond);
		assertEquals(IBond.Order.SINGLE, bond.getOrder());
	}
	
	public void testDestroyBondOrder_IBond_Order() {
		assertEquals(1.0, BondManipulator.destroyBondOrder(IBond.Order.SINGLE), 0.00001);
		assertEquals(2.0, BondManipulator.destroyBondOrder(IBond.Order.DOUBLE), 0.00001);
		assertEquals(3.0, BondManipulator.destroyBondOrder(IBond.Order.TRIPLE), 0.00001);
		assertEquals(4.0, BondManipulator.destroyBondOrder(IBond.Order.QUADRUPLE), 0.00001);
	}
	
	public void testGetMaximumBondOrder_List() {
		List<IBond> bonds = new ArrayList<IBond>();
		IBond bond = new Bond(); bond.setOrder(IBond.Order.SINGLE);
		bonds.add(bond);
		bond = new Bond(); bond.setOrder(IBond.Order.QUADRUPLE);
		bonds.add(bond);
		bond = new Bond(); bond.setOrder(IBond.Order.QUADRUPLE);
		bonds.add(bond);
		assertEquals(IBond.Order.QUADRUPLE, BondManipulator.getMaximumBondOrder(bonds));
	}
	
	public void testGetMaximumBondOrder_Iterator() {
		List<IBond> bonds = new ArrayList<IBond>();
		IBond bond = new Bond(); bond.setOrder(IBond.Order.SINGLE);
		bonds.add(bond);
		bond = new Bond(); bond.setOrder(IBond.Order.QUADRUPLE);
		bonds.add(bond);
		bond = new Bond(); bond.setOrder(IBond.Order.QUADRUPLE);
		bonds.add(bond);
		assertEquals(IBond.Order.QUADRUPLE, BondManipulator.getMaximumBondOrder(bonds.iterator()));
	}
	
	public void testGetSingleBondEquivalentSum_List() {
		List<IBond> bonds = new ArrayList<IBond>();
		IBond bond = new Bond(); bond.setOrder(IBond.Order.SINGLE);
		bonds.add(bond);
		bond = new Bond(); bond.setOrder(IBond.Order.DOUBLE);
		bonds.add(bond);
		assertEquals(3, BondManipulator.getSingleBondEquivalentSum(bonds));
		bond = new Bond(); bond.setOrder(IBond.Order.QUADRUPLE);
		bonds.add(bond);
		assertEquals(7, BondManipulator.getSingleBondEquivalentSum(bonds));
	}
	
	public void testGetSingleBondEquivalentSum_Iterator() {
		List<IBond> bonds = new ArrayList<IBond>();
		IBond bond = new Bond(); bond.setOrder(IBond.Order.SINGLE);
		bonds.add(bond);
		bond = new Bond(); bond.setOrder(IBond.Order.DOUBLE);
		bonds.add(bond);
		assertEquals(3, BondManipulator.getSingleBondEquivalentSum(bonds.iterator()));
		bond = new Bond(); bond.setOrder(IBond.Order.QUADRUPLE);
		bonds.add(bond);
		assertEquals(7, BondManipulator.getSingleBondEquivalentSum(bonds.iterator()));
	}
	
	public void testCreateBondOrder_double() {
		assertEquals(IBond.Order.SINGLE, BondManipulator.createBondOrder(1.0));
		assertEquals(IBond.Order.DOUBLE, BondManipulator.createBondOrder(2.0));
		assertEquals(IBond.Order.TRIPLE, BondManipulator.createBondOrder(3.0));
		assertEquals(IBond.Order.QUADRUPLE, BondManipulator.createBondOrder(4.0));
	}

}


