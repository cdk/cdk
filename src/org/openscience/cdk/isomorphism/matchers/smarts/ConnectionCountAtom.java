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
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.Atom;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;

/**
 * This matcher checks the total valency of the Atom.
 * This cannot be matched with a unpreprocessed Atom!
 *
 * @cdk.module experimental
 */
public class ConnectionCountAtom extends SMARTSAtom {
    
    private int count;
    
    public ConnectionCountAtom(int count) {
        this.count = count;
    }
    
	public boolean matches(Atom atom) {
        int count = ((Integer)atom.getProperty("org.openscience.cdk.Atom.connectionCount")).intValue();
        return (count == this.count);
    };
}

