/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.isomorphism.matchers;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;

/**
 * @cdk.module extra
 */
public abstract class QueryAtomContainer extends AtomContainer {

    public QueryAtomContainer() {
        throw new InstantiationError("An abstract QueryAtomContainer should not be used directly");
    };
    
	public boolean matches(AtomContainer container) {
        throw new AbstractMethodError("The QueryBond class did not implement this method");
    };

    public void add(AtomContainer container) {
        if (container instanceof QueryAtomContainer) {
            super.add(container);
        } else {
            throw new IllegalArgumentException("AtomContainer is not of type QueryAtomContainer");
        }
    }
    
    public void addAtom(Atom atom) {
        if (atom instanceof QueryAtom) {
            super.addAtom(atom);
        } else {
            throw new IllegalArgumentException("Atom is not of type QueryAtom");
        }
    }

    public void addBond(Bond bond) {
        if (bond instanceof QueryBond) {
            super.addBond(bond);
        } else {
            throw new IllegalArgumentException("Bond is not of type QueryBond");
        }
    }
}

