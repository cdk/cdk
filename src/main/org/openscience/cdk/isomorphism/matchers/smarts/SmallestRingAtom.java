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

import java.util.Collections;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;

/**
 * This smarts atom matches any atom with the smallest SSSR size being a 
 * certain value.
 *
 * @cdk.module  smarts
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS 
 */
public class SmallestRingAtom extends SMARTSAtom {
	private static final long serialVersionUID = 8201040824866400163L;
	/**
	 * The size of the smallest SSSR
	 */
	private int smallestRingSize;

	public SmallestRingAtom(int size) {
		this.smallestRingSize = size;
	}

	public boolean matches(IAtom atom) {
		if (atom.getFlag(CDKConstants.ISINRING)) {
			List<Integer> rings = (List<Integer>) atom
					.getProperty(CDKConstants.RING_SIZES);
			if (rings == null || rings.size() == 0) {
				return false;
			}
			Collections.sort(rings);
			if ((rings.get(0)).intValue() == smallestRingSize) {
				return true;
			}
		}
		return false;
	}
}
