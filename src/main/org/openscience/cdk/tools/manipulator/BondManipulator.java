/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import java.util.Iterator;
import java.util.List;

/**
 * Class with convenience methods that provide methods to manipulate
 * AtomContainer's. For example:
 * <pre>
 * AtomContainerManipulator.replaceAtomByAtom(container, atom1, atom2);
 * </pre>
 * will replace the Atom in the AtomContainer, but in all the ElectronContainer's
 * it participates too.
 *
 * @cdk.module  core
 * @cdk.svnrev  $Revision$
 *
 * @author  Egon Willighagen
 * @cdk.created 2003-08-07
 */
@TestClass("org.openscience.cdk.tools.manipulator.BondManipulatorTest")
public class BondManipulator {
	
	/**
	 * Constructs an array of Atom objects from Bond.
	 * @param  container The Bond object.
	 * @return The array of Atom objects.
	 */
    @TestMethod("testGetAtomArray_IBond")
    public static IAtom[] getAtomArray(IBond container) {
		IAtom[] ret = new IAtom[container.getAtomCount()];
		for (int i = 0; i < ret.length; ++i) ret[i] = container.getAtom(i);
		return ret;
	}
	
	/**
	 * Returns true if the first bond has a lower bond order than the second bond.
	 * It returns false if the bond order is equal, and if the order of the first
	 * bond is larger than that of the second. Also returns false if either bond
	 * order is unset.
	 * 
	 * @param first  The first bond order object
	 * @param second The second bond order object
	 * @return true if the first bond order is lower than the second one, false othrwise
     * @see #isHigherOrder(org.openscience.cdk.interfaces.IBond.Order, org.openscience.cdk.interfaces.IBond.Order)
	 */
    @TestMethod("testIsLowerOrder_IBond_Order_IBond_Order")
    public static boolean isLowerOrder(IBond.Order first, IBond.Order second) {
		if (first == null || second == null) return false;
		
		if (second == IBond.Order.QUADRUPLE) {
			if (first !=  IBond.Order.QUADRUPLE) return true;
		}
		if (second == IBond.Order.TRIPLE) {
			if (first ==  IBond.Order.SINGLE ||
				first ==  IBond.Order.DOUBLE) return true;
		} else if (second == IBond.Order.DOUBLE) {
			if (first ==  IBond.Order.SINGLE) return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the first bond has a higher bond order than the second bond.
	 * It returns false if the bond order is equal, and if the order of the first
	 * bond is lower than that of the second. Also returns false if either bond
	 * order is unset.
	 * 
	 * @param first  The first bond order object
	 * @param second  The second bond order object
	 * @return true if the first bond order is higher than the second one, false othrwise
     * @see #isLowerOrder(org.openscience.cdk.interfaces.IBond.Order, org.openscience.cdk.interfaces.IBond.Order)
	 */
    @TestMethod("testIsHigherOrder_IBond_Order_IBond_Order")
    public static boolean isHigherOrder(IBond.Order first, IBond.Order second) {
		if (first == null || second == null) return false;
		
		if (second == IBond.Order.QUADRUPLE) {
			return false;
		}
		if (second == IBond.Order.TRIPLE) {
			if (first ==  IBond.Order.QUADRUPLE) return true;
		} else if (second == IBond.Order.DOUBLE) {
			if (first ==  IBond.Order.TRIPLE ||
				first ==  IBond.Order.QUADRUPLE) return true;
		} else if (second == IBond.Order.SINGLE) {
			if (first !=  IBond.Order.SINGLE) return true;
		}
		return false;
	}
	
	/**
	 * Returns the IBond.Order one higher. Does not increase the bond order
	 * beyond the QUADRUPLE bond order.
     * @param oldOrder the old order
     * @return The incremented bond order
     * @see #increaseBondOrder(org.openscience.cdk.interfaces.IBond)
     * @see #decreaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     * @see #decreaseBondOrder(org.openscience.cdk.interfaces.IBond)
     */
    @TestMethod("testIncreaseBondOrder_IBond_Order")
    public static IBond.Order increaseBondOrder(IBond.Order oldOrder) {
		if (oldOrder == IBond.Order.TRIPLE) {
			return IBond.Order.QUADRUPLE;
		} else if (oldOrder == IBond.Order.DOUBLE) {
			return IBond.Order.TRIPLE;
		} else if (oldOrder == IBond.Order.SINGLE) {
			return IBond.Order.DOUBLE;
		}
		return oldOrder;
	}

    /**
     * Increment the bond order of this bond.
     *
     * @param bond  The bond whose order is to be incremented
     * @see #increaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     * @see #decreaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     * @see #decreaseBondOrder(org.openscience.cdk.interfaces.IBond)
     */
    @TestMethod("testIncreaseBondOrder_IBond")
    public static void increaseBondOrder(IBond bond) {
		bond.setOrder(increaseBondOrder(bond.getOrder()));
	}

	/**
	 * Returns the IBond.Order one lower. Does not decrease the bond order
	 * lower the QUADRUPLE bond order.
     * @param oldOrder the old order
     * @return the decremented order
     * @see #decreaseBondOrder(org.openscience.cdk.interfaces.IBond)
     * @see #increaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     * @see #increaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     */
    @TestMethod("testDecreaseBondOrder_IBond_Order")
    public static IBond.Order decreaseBondOrder(IBond.Order oldOrder) {
		if (oldOrder == IBond.Order.TRIPLE) {
			return IBond.Order.DOUBLE;
		} else if (oldOrder == IBond.Order.DOUBLE) {
			return IBond.Order.SINGLE;
		} else if (oldOrder == IBond.Order.QUADRUPLE) {
			return IBond.Order.TRIPLE;
		}
		return oldOrder;
	}

    /**
     * Decrease the order of a bond.
     *
     * @param bond  The bond in question
     * @see #decreaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     * @see #increaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     * @see #increaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     */
    @TestMethod("testDecreaseBondOrder_IBond")
    public static void decreaseBondOrder(IBond bond) {
		bond.setOrder(decreaseBondOrder(bond.getOrder()));
	}

	/**
	 * Convenience method to convert a double into an IBond.Order.
	 * Returns NULL if the bond order is not 1.0, 2.0, 3.0 and 4.0.
     * @param bondOrder The numerical bond order
     * @return An instance of {@link org.openscience.cdk.interfaces.IBond.Order}
     * @see #destroyBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     */
    @TestMethod("testCreateBondOrder_double")
    public static IBond.Order createBondOrder(double bondOrder) {
		if (bondOrder == 1.0) {
			return IBond.Order.SINGLE;
		} else if (bondOrder == 2.0) {
			return IBond.Order.DOUBLE;
		} else if (bondOrder == 3.0) {
			return IBond.Order.TRIPLE;
		} else if (bondOrder == 4.0) {
			return IBond.Order.QUADRUPLE;
		}
		return null;
	}

    /**
     * Convert a {@link org.openscience.cdk.interfaces.IBond.Order} to a numeric value.
     *
     * Single, double, triple and quadruple bonds are converted to 1.0, 2.0, 3.0
     * and 4.0 respectively.
     *
     * @param bondOrder The bond order object
     * @return  The numeric value
     * @see #createBondOrder(double)
     */
    @TestMethod("testDestroyBondOrder_IBond_Order")
    public static double destroyBondOrder(IBond.Order bondOrder) {
		if (bondOrder == IBond.Order.SINGLE) {
			return 1.0;
		} else if (bondOrder == IBond.Order.DOUBLE) {
			return 2.0;
		} else if (bondOrder == IBond.Order.TRIPLE) {
			return 3.0;
		}
		return 4.0;
	}
	
	/**
	 * Returns the maximum bond order for a List of bonds.
	 * 
	 * @param bonds The list of bonds to search through
	 * @return  The maximum bond order found
     * @see #getMaximumBondOrder(java.util.Iterator)
	 */
    @TestMethod("testGetMaximumBondOrder_List")
    public static IBond.Order getMaximumBondOrder(List<IBond> bonds) {
		return getMaximumBondOrder(bonds.iterator());
	}

    /**
     * Returns the maximum bond order for a List of bonds, given an iterator to the list.
     * @param bonds An iterator for the list of bonds
     * @return The maximum bond order found
     * @see #getMaximumBondOrder(java.util.List)
     */
    @TestMethod("testGetMaximumBondOrder_Iterator")
    public static IBond.Order getMaximumBondOrder(Iterator<IBond> bonds) {
		IBond.Order maxOrder = IBond.Order.SINGLE;
		while (bonds.hasNext()) {
			IBond bond = bonds.next();
			if (isHigherOrder(bond.getOrder(), maxOrder)) maxOrder = bond.getOrder();
		}
		return maxOrder;
	}

    /**
     * Get the single bond equivalent (SBE) of a list of bonds.
     *
     * This sums the bond orders of all the bonds.
     *
     * @param bonds The list of bonds
     * @return  The SBE sum
     * @see #getSingleBondEquivalentSum(java.util.Iterator)
     */
    @TestMethod("testGetSingleBondEquivalentSum_List")
    public static int getSingleBondEquivalentSum(List<IBond> bonds) {
		return getSingleBondEquivalentSum(bonds.iterator());
	}

    /**
     * Get the single bond equivalent (SBE) of a list of bonds, given an iterator to the list.
     *
     * @param bonds An iterator to the list of bonds
     * @return The SBE sum
     */
    @TestMethod("testGetSingleBondEquivalentSum_Iterator")
    public static int getSingleBondEquivalentSum(Iterator<IBond> bonds) {
		int sum = 0;
		while (bonds.hasNext()) {
			IBond nextBond = bonds.next();
			if (nextBond.getOrder() == IBond.Order.SINGLE) {
				sum += 1;
			} else if (nextBond.getOrder() == IBond.Order.DOUBLE) {
				sum += 2;
			} else if (nextBond.getOrder() == IBond.Order.TRIPLE) {
				sum += 3;
			} else if (nextBond.getOrder() == IBond.Order.QUADRUPLE) {
				sum += 4;
			}
		}
		return sum;
	}

	
}

