/* $RCSfile$
 * $Author: egonw $
 * $Date: 2008-03-04 17:00:17 -0500 (Tue, 04 Mar 2008) $
 * $Revision: 10299 $
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

/**
 * This matches an aromatic or a single bond, used when no bond is specified between an atom
 *
 * @cdk.module  isomorphism
 * @cdk.svnrev  $Revision: 10299 $
 * @cdk.keyword SMARTS
 */
public class AromaticOrSingleQueryBond extends SMARTSBond {

    private static final long serialVersionUID = 6941220923564432716L;

    /**
     * Creates a new instance
     *
     */
    public AromaticOrSingleQueryBond() {
    	super();
    	setFlag(CDKConstants.ISAROMATIC, true);
    }

    /**
     * Creates a new instance
     *
     */
    public AromaticOrSingleQueryBond(IQueryAtom atom1, IQueryAtom atom2, Order order) {
    	super(atom1, atom2, order);
    	setFlag(CDKConstants.ISAROMATIC, true);
    }

	/* (non-Javadoc)
	 * @see org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond#matches(org.openscience.cdk.interfaces.IBond)
	 */
	public boolean matches(IBond bond) {
        return bond.getFlag(CDKConstants.ISAROMATIC) || bond.getOrder() == IBond.Order.SINGLE;        
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.Bond#toString()
     */
    public String toString() {
		return "AromaticOrSingleQueryBond()";
    }
}