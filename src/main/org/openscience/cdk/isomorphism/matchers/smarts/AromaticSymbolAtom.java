/* $Revision$ $Author$ $Date$ 
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * (or see http://www.gnu.org/copyleft/lesser.html)
 */
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;

/**
 * This class matches aromatic atom with element symbol specified.
 *
 * @cdk.module  smarts
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS
 */
public class AromaticSymbolAtom extends SMARTSAtom {
	private static final long serialVersionUID = -2887357588636946195L;

	public AromaticSymbolAtom(String symbol) {
		super();
		setFlag(CDKConstants.ISAROMATIC, true);
		setSymbol(symbol);
	}
	
	public boolean matches(IAtom atom) {
		if (atom.getSymbol().equals(this.getSymbol()) &&
				atom.getFlag(CDKConstants.ISAROMATIC)) {
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "AromaticSymbolAtom(" + getSymbol() + ")";
	}
}
