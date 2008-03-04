/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
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
package org.openscience.cdk.isomorphism.matchers;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.CDKConstants;

/**
 * @cdk.module isomorphism
 * @cdk.svnrev $Revision$
 */
public class OrderQueryBond extends org.openscience.cdk.Bond implements IQueryBond {

    private static final long serialVersionUID = 2292654937621883661L;

    public OrderQueryBond() {
    }

    public OrderQueryBond(IQueryAtom atom1, IQueryAtom atom2, IBond.Order order) {
        super(atom1, atom2, order);
    }
    
	public boolean matches(IBond bond) {
        if (this.getOrder() == bond.getOrder()) {
            // bond orders match
            return true;
        } else if (this.getFlag(CDKConstants.ISAROMATIC) && 
        		   bond.getFlag(CDKConstants.ISAROMATIC)) {
            // or both are aromatic
        } // else
        return false;
    };

    public void setAtoms(IAtom[] atoms) {
        if (atoms.length > 0 && atoms[0] instanceof IQueryAtom) {
            super.setAtoms(atoms);
        } else {
            throw new IllegalArgumentException("Array is not of type QueryAtom[]");
        }
	}
    
	public void setAtomAt(IAtom atom, int position) {
        if (atom instanceof IQueryAtom) {
            super.setAtom(atom, position);
        } else {
            throw new IllegalArgumentException("Atom is not of type QueryAtom");
        }
    }
}

