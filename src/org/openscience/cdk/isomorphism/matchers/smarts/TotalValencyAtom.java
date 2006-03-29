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

import org.openscience.cdk.interfaces.IAtom;

/**
 * This matcher checks the total valency of the Atom.
 * This cannot be matched with a unpreprocessed Atom!
 *
 * @cdk.module extra
 */
public class TotalValencyAtom extends SMARTSAtom {
    
    private int valency;
    
    public TotalValencyAtom(int valency) {
        this.valency = valency;
    }
    
	public boolean matches(IAtom atom) {
        int valency = ((Integer)atom.getProperty("org.openscience.cdk.Atom.totalValency")).intValue();
        return (valency == this.valency);
    };

    public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("TotalValency(");
        s.append(this.hashCode() + ", ");
		s.append("V:" + valency);
        s.append(")");
		return s.toString();
    }
}

