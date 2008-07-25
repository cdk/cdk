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
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

/**
 * Abstract smarts bond.
 *
 * @cdk.module  isomorphism
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS
 */
public class SMARTSBond extends org.openscience.cdk.Bond implements IQueryBond {
    
    private static final long serialVersionUID = 6164978147730140061L;

    public SMARTSBond() {}
    
    public SMARTSBond(IQueryAtom atom1, IQueryAtom atom2, IBond.Order order) {
        super(atom1, atom2, order);
    }

	public boolean matches(IBond bond) {
        return false;
        
    }
}

