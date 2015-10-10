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
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

/**
 * This matches an aromatic or a single bond, used when no bond is specified between an atom.
 *
 * @cdk.module  smarts
 * @cdk.githash
 * @cdk.keyword SMARTS
 */
public class AromaticOrSingleQueryBond extends SMARTSBond {

    private static final long serialVersionUID = 6941220923564432716L;

    /**
     * Creates a new instance.
     *
     */
    public AromaticOrSingleQueryBond(IChemObjectBuilder builder) {
        super(builder);
        setIsAromatic(true);
    }

    /**
     * Creates a new instance
     *
     */
    public AromaticOrSingleQueryBond(IQueryAtom atom1, IQueryAtom atom2, Order order, IChemObjectBuilder builder) {
        super(atom1, atom2, order, builder);
        setIsAromatic(true);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond#matches(org
     * .openscience.cdk.interfaces.IBond)
     */
    @Override
    public boolean matches(IBond bond) {
        return bond.isAromatic() || bond.getOrder() == IBond.Order.SINGLE;
    }

    /*
     * (non-Javadoc)
     * @see org.openscience.cdk.Bond#toString()
     */
    @Override
    public String toString() {
        return "AromaticOrSingleQueryBond()";
    }
}
