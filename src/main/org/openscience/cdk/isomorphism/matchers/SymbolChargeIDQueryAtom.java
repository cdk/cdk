/* $RCSfile$
 * $Author: kaihartmann $
 * $Date: 2006-06-07 11:41:42 +0200 (Wed, 07 Jun 2006) $
 * $Revision: 6349 $
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

/**
 * @cdk.module  isomorphism
 * @cdk.svnrev  $Revision: 9162 $
 */
public class SymbolChargeIDQueryAtom extends org.openscience.cdk.Atom implements IQueryAtom {
    
    private static final long serialVersionUID = 3328313175614956150L;

    public SymbolChargeIDQueryAtom() {}
    
    public SymbolChargeIDQueryAtom(IAtom atom) {
        super(atom.getSymbol());
        setFormalCharge(atom.getFormalCharge());
        setID(atom.getID());
    }
    public void setOperator(String str){}
    public boolean matches(IAtom atom) {
        return this.getSymbol().equals(atom.getSymbol())&&
               this.getFormalCharge()==atom.getFormalCharge() &&
               this.getID().equals(atom.getID());
    };

    public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("SymbolAndChargeQueryAtom(");
		s.append(this.hashCode() + ", ");
		s.append(getID() + ", ");
		s.append(getSymbol() + ", ");
		s.append(getFormalCharge());
		s.append(")");
		return s.toString();
    }
}
