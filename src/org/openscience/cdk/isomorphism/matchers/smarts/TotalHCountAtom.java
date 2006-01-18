/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IAtom;

/**
 * This matcher checks the formal charge of the Atom.
 * This cannot be matched with a unpreprocessed Atom!
 *
 * @cdk.module extra
 */
public class TotalHCountAtom extends SMARTSAtom {
    
    private int hCount;
    
    public TotalHCountAtom(int hCount) {
        this.hCount = hCount;
    }
    
	public boolean matches(IAtom atom) {
        int hCount = ((Integer)atom.getProperty("org.openscience.cdk.Atom.totalHCount")).intValue();
        return (hCount == this.hCount);
    };

    public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("TotalHCountAtom(");
        s.append(this.hashCode() + ", ");
		s.append("HC:" + hCount);
		s.append(")");
		return s.toString();
    }
}

