/* $Revision: $ $Author: $ $Date: $ 
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

import java.util.ArrayList;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;

/**
 * This query atom matches any atom with a certain number of SSSR. 
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-04-24
 * @cdk.module smarts
 * @cdk.keyword SMARTS AST
 */
public class RingMembershipAtom extends SMARTSAtom {
	private static final long serialVersionUID = -7963168231557641862L;
	/**
	 * Number of SSSR
	 */
	private int numSSSR;

	public RingMembershipAtom(int num) {
		this.numSSSR = num;
	}

	public boolean matches(IAtom atom) {
		if (atom.getFlag(CDKConstants.ISINRING)) {
			// TODO: ESSENTIAL_RINGS not calculated in IAtom
			ArrayList rings = (ArrayList) atom
					.getProperty(CDKConstants.ESSENTIAL_RINGS);
			if (rings != null) {
				return rings.size() == numSSSR;
			}
		}
		return false;
	}
}
