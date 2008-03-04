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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * @cdk.module  isomorphism
 * @cdk.svnrev  $Revision$
 */
public class QueryAtomContainer extends org.openscience.cdk.AtomContainer implements IQueryAtomContainer {

    private static final long serialVersionUID = -1876912362585898476L;

    public QueryAtomContainer() {
    };
    
    public void add(IAtomContainer container) {
        if (container instanceof QueryAtomContainer) {
            super.add(container);
        } else {
            throw new IllegalArgumentException("AtomContainer is not of type QueryAtomContainer");
        }
    }
    
    public void addAtom(IAtom atom) {
        if (atom instanceof IQueryAtom) {
            super.addAtom(atom);
        } else {
            throw new IllegalArgumentException("Atom is not of type QueryAtom");
        }
    }

    public void addBond(IBond bond) {
        if (bond instanceof IQueryBond) {
            super.addBond(bond);
        } else {
            throw new IllegalArgumentException("Bond is not of type QueryBond");
        }
    }
    
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("QueryAtomContainer(");
		s.append(this.hashCode() + ", ");
		s.append("#A:" + getAtomCount() + ", ");
		s.append("#EC:" + getElectronContainerCount() + ", ");
		for (int i = 0; i < getAtomCount(); i++) {
			s.append(getAtom(i).toString() + ", ");
		}
		for (int i = 0; i < getBondCount(); i++) {
			s.append(getBond(i).toString() + ", ");
		}
		for (int i = 0; i < getLonePairCount(); i++) {
			s.append(getLonePair(i).toString() + ", ");
		}
		for (int i = 0; i < getSingleElectronCount(); i++) {
			s.append(getSingleElectron(i).toString() + ", ");
		}
		s.append(")");
		return s.toString();
	}
    
}

