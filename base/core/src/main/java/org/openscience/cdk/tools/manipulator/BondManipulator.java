/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

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
 * @cdk.githash
 *
 * @author  Egon Willighagen
 * @cdk.created 2003-08-07
 */
public class BondManipulator {

    /**
     * Constructs an array of Atom objects from Bond.
     * @param  container The Bond object.
     * @return The array of Atom objects.
     */
    public static IAtom[] getAtomArray(IBond container) {
        IAtom[] ret = new IAtom[container.getAtomCount()];
        for (int i = 0; i < ret.length; ++i)
            ret[i] = container.getAtom(i);
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
     * @return true if the first bond order is lower than the second one, false otherwise
     * @see #isHigherOrder(org.openscience.cdk.interfaces.IBond.Order, org.openscience.cdk.interfaces.IBond.Order)
     */
    public static boolean isLowerOrder(IBond.Order first, IBond.Order second) {
        if (first == null || second == null ||
            first == IBond.Order.UNSET || second == IBond.Order.UNSET) return false;
        return first.compareTo(second) < 0;
    }

    /**
     * Returns true if the first bond has a higher bond order than the second bond.
     * It returns false if the bond order is equal, and if the order of the first
     * bond is lower than that of the second. Also returns false if either bond
     * order is unset.
     *
     * @param first  The first bond order object
     * @param second  The second bond order object
     * @return true if the first bond order is higher than the second one, false otherwise
     * @see #isLowerOrder(org.openscience.cdk.interfaces.IBond.Order, org.openscience.cdk.interfaces.IBond.Order)
     */
    public static boolean isHigherOrder(IBond.Order first, IBond.Order second) {
        if (first == null || second == null ||
            first == IBond.Order.UNSET || second == IBond.Order.UNSET) return false;
        return first.compareTo(second) > 0;
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
    public static IBond.Order increaseBondOrder(IBond.Order oldOrder) {
    	switch (oldOrder) {
        case SINGLE:
            return Order.DOUBLE;
        case DOUBLE:
            return Order.TRIPLE;
        case TRIPLE:
            return Order.QUADRUPLE;
        case QUADRUPLE:
            return Order.QUINTUPLE;
        case QUINTUPLE:
            return Order.SEXTUPLE;
        default:
            return oldOrder;
    	}
    }

    /**
     * Increment the bond order of this bond.
     *
     * @param bond  The bond whose order is to be incremented
     * @see #increaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     * @see #decreaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     * @see #decreaseBondOrder(org.openscience.cdk.interfaces.IBond)
     */
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
    public static IBond.Order decreaseBondOrder(IBond.Order oldOrder) {
    	switch (oldOrder) {
        case DOUBLE:
            return Order.SINGLE;
        case TRIPLE:
            return Order.DOUBLE;
        case QUADRUPLE:
            return Order.TRIPLE;
        case QUINTUPLE:
            return Order.QUADRUPLE;
        case SEXTUPLE:
            return Order.QUINTUPLE;
        default:
            return oldOrder;
    	}
    }

    /**
     * Decrease the order of a bond.
     *
     * @param bond  The bond in question
     * @see #decreaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     * @see #increaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     * @see #increaseBondOrder(org.openscience.cdk.interfaces.IBond.Order)
     */
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
    public static IBond.Order createBondOrder(double bondOrder) {
    	for (IBond.Order order : IBond.Order.values()) {
    		if (order.numeric().doubleValue() == bondOrder) return order;
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
     * 
     * @deprecated use <code>IBond.Order.numeric().doubleValue()</code> instead
     */
    public static double destroyBondOrder(IBond.Order bondOrder) {
        return bondOrder.numeric().doubleValue();
    }

    /**
     * Returns the maximum bond order for a List of bonds.
     *
     * @param bonds The list of bonds to search through
     * @return  The maximum bond order found
     * @see #getMaximumBondOrder(java.util.Iterator)
     */
    public static IBond.Order getMaximumBondOrder(List<IBond> bonds) {
        return getMaximumBondOrder(bonds.iterator());
    }

    /**
     * Returns the maximum bond order for a List of bonds, given an iterator to the list.
     * @param bonds An iterator for the list of bonds
     * @return The maximum bond order found
     * @see #getMaximumBondOrder(java.util.List)
     */
    public static IBond.Order getMaximumBondOrder(Iterator<IBond> bonds) {
        IBond.Order maxOrder = IBond.Order.SINGLE;
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            if (isHigherOrder(bond.getOrder(), maxOrder)) maxOrder = bond.getOrder();
        }
        return maxOrder;
    }

    /**
     * Returns the maximum bond order for the two bonds.
     *
     * @param  firstBond  first bond to compare
     * @param  secondBond second bond to compare
     * @return            The maximum bond order found
     */
    public static IBond.Order getMaximumBondOrder(IBond firstBond, IBond secondBond) {
        if (firstBond == null || secondBond == null)
            throw new IllegalArgumentException("null instance of IBond provided");
        return getMaximumBondOrder(firstBond.getOrder(), secondBond.getOrder());
    }

    /**
     * Returns the maximum bond order for the two bond orders.
     *
     * @param  firstOrder  first bond order to compare
     * @param  secondOrder second bond order to compare
     * @return             The maximum bond order found
     */
    public static IBond.Order getMaximumBondOrder(IBond.Order firstOrder, IBond.Order secondOrder) {
        if (firstOrder == Order.UNSET) {
            if (secondOrder == Order.UNSET) throw new IllegalArgumentException("Both bond orders are unset");
            return secondOrder;
        }
        if (secondOrder == Order.UNSET) {
            if (firstOrder == Order.UNSET) throw new IllegalArgumentException("Both bond orders are unset");
            return firstOrder;
        }

        if (isHigherOrder(firstOrder, secondOrder))
            return firstOrder;
        else
            return secondOrder;
    }

    /**
     * Returns the minimum bond order for a List of bonds.
     *
     * @param bonds The list of bonds to search through
     * @return  The maximum bond order found
     * @see #getMinimumBondOrder(java.util.Iterator)
     */
    public static IBond.Order getMinimumBondOrder(List<IBond> bonds) {
        return getMinimumBondOrder(bonds.iterator());
    }

    /**
     * Returns the minimum bond order for a List of bonds, given an iterator
     * to the list.
     *
     * @param bonds An iterator for the list of bonds
     * @return The minimum bond order found
     * @see #getMinimumBondOrder(java.util.List)
     */
    public static IBond.Order getMinimumBondOrder(Iterator<IBond> bonds) {
        IBond.Order minOrder = IBond.Order.SEXTUPLE;
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            if (isLowerOrder(bond.getOrder(), minOrder)) minOrder = bond.getOrder();
        }
        return minOrder;
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
    public static int getSingleBondEquivalentSum(List<IBond> bonds) {
        return getSingleBondEquivalentSum(bonds.iterator());
    }

    /**
     * Get the single bond equivalent (SBE) of a list of bonds, given an iterator to the list.
     *
     * @param bonds An iterator to the list of bonds
     * @return The SBE sum
     */
    public static int getSingleBondEquivalentSum(Iterator<IBond> bonds) {
        int sum = 0;
        while (bonds.hasNext()) {
            IBond.Order order = bonds.next().getOrder();
            if (order != null) {
                sum += order.numeric();
            }
        }
        return sum;
    }

}
