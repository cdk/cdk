/* $RCSfile$
 * $Author: egonw $
 * $Date: 2008-07-25 02:46:55 -0400 (Fri, 25 Jul 2008) $
 * $Revision: 11746 $
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
 * This matcher any heavy atom that is not C or H.
 *
 * @cdk.module  smarts
 * @cdk.svnrev  $Revision: 11746 $
 * @cdk.keyword SMARTS
 */
public class NonCHHeavyAtom extends SMARTSAtom {

	private static final long serialVersionUID = 5145049891214205622L;

	/**
	 * Creates a new instance
	 *
	 */
	public NonCHHeavyAtom() {

	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org.openscience.cdk.interfaces.IAtom)
	 */
	public boolean matches(IAtom atom) {
        String symbol = atom.getSymbol();
        return !(symbol.equals("C") || symbol.equals("H"));

    }

	/* (non-Javadoc)
	 * @see org.openscience.cdk.PseudoAtom#toString()
	 */
	public String toString() {
		return "NonCHHeavyAtom()";
	}
}