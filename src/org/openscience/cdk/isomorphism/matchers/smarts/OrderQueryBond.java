/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

/**
 * @cdk.module extra
 */
public class OrderQueryBond extends SMARTSBond {

    private static final long serialVersionUID = -5139538872961160661L;

    public OrderQueryBond() {
    }

    public OrderQueryBond(IQueryAtom atom1, IQueryAtom atom2, double order) {
        super(atom1, atom2, order);
    }
    
	public boolean matches(IBond bond) {
        if (this.getOrder() == bond.getOrder()) {
            // bond orders match
            return true;
        } // else
        return false;
    };

    public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("OrderQueryBond(");
        s.append(this.hashCode() + ", ");
		s.append("#O:" + getOrder());
		s.append(", #A:" + atoms.length);
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i] == null) {
				s.append(", null");
			} else {
				s.append(", " + atoms[i].toString());
			}
		}
		s.append(")");
		return s.toString();
    }
}

