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

import org.openscience.cdk.interfaces.IAtom;

/**
 * This matcher any Atom.
 *
 * @cdk.module  isomorphism
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS
 */
public class AnyAtom extends SMARTSAtom {
    
    private static final long serialVersionUID = -2061241755106011847L;

    /**
     * Creates a new instance
     */
    public AnyAtom() {
    }
    
    /* (non-Javadoc)
     * @see org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org.openscience.cdk.interfaces.IAtom)
     */
    public boolean matches(IAtom atom) {
    	return true;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.PseudoAtom#toString()
     */
    public String toString() {
		return "AnyAtom()";
    }
}

