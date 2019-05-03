/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * This matches a bond with a certain bond order.
 *
 * Daylight spec indicates that if match a single bond
 * using '-', it should be an aliphatic single bond
 *
 * @cdk.module  smarts
 * @cdk.githash
 * @cdk.keyword SMARTS
 */
@Deprecated
public class OrderQueryBond extends SMARTSBond {

    private static final long serialVersionUID = -5139538872961160661L;

    /**
     * Creates a new instance
     *
     * @param order the order of bond
     */
    public OrderQueryBond(IBond.Order order, IChemObjectBuilder builder) {
        super(builder);
        this.setOrder(order);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond#matches(org
     * .openscience.cdk.interfaces.IBond)
     */
    @Override
    public boolean matches(IBond bond) {
        if (bond.isAromatic() ^ isAromatic()) return false;

        // we check for both bonds being aromatic - but the query will
        // never come in as aromatic (since there is a separate aromatic
        // query bond). But no harm in checking
        return bond.isAromatic() || bond.getOrder() == getOrder();
    }

    /*
     * (non-Javadoc)
     * @see org.openscience.cdk.Bond#toString()
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("OrderQueryBond(");
        s.append(this.hashCode() + ", ");
        s.append("#O:" + getOrder());
        s.append(')');
        return s.toString();
    }
}
