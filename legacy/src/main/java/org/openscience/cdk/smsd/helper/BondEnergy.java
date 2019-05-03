/* Copyright (C) 2009-2010  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.smsd.helper;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

/**
 * Helper class defining the energy for a bond type. The bond
 * type is defined as to element symbols and a bond order.
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class BondEnergy {

    private String      symbol1   = "";
    private String      symbol2   = "";
    private IBond.Order bondOrder = null;
    private int         energy    = -1;

    /**
     * Creates a new bond energy for the given elements and
     * bond order.
     *
     * @param symbol1 element symbol for the first atom
     * @param symbol2 element symbol for the second atom
     * @param order   bond order
     * @param energy  energy for this bond type
     */
    public BondEnergy(String symbol1, String symbol2, IBond.Order order, int energy) {
        this.symbol1 = symbol1;
        this.symbol2 = symbol2;
        this.bondOrder = order;
        this.energy = energy;
    }

    /**
     * Returns the element symbol of the first atom.
     *
     * @return the element symbol as {@link String}
     */
    public String getSymbolFirstAtom() {
        return symbol1;
    }

    /**
     * Returns the element symbol of the second atom.
     *
     * @return the element symbol as {@link String}
     */
    public String getSymbolSecondAtom() {
        return symbol2;
    }

    /**
     * Returns the bond order for this bond type energy.
     *
     * @return the bond order of the bond type as {@link Order}
     */
    public IBond.Order getBondOrder() {
        return bondOrder;
    }

    /**
     * Returns the energy for this bond type.
     *
     * @return the bond energy as integer.
     */
    public int getEnergy() {
        return energy;
    }

    public boolean matches(IBond bond) {
        IAtom atom1 = bond.getBegin();
        IAtom atom2 = bond.getEnd();

        if ((atom1.getSymbol().equalsIgnoreCase(symbol1) && atom2.getSymbol().equalsIgnoreCase(symbol2))
                || (atom1.getSymbol().equalsIgnoreCase(symbol2) && atom2.getSymbol().equalsIgnoreCase(symbol1))) {
            if (bond.getOrder().compareTo(bondOrder) == 0) {
                return true;
            }
        }
        return false;
    }
}
