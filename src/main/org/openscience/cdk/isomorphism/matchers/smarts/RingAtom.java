/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import java.util.ArrayList;

/**
 * This matches an atom in a specific size ring.
 * 
 * @cdk.module  smarts
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS
 */
public class RingAtom extends SMARTSAtom {

	private static final long serialVersionUID = -5145049891214205622L;

	private int ringSize;

	/**
	 * Creates a new instance
	 *
	 * @param ringSize
	 */
	public RingAtom(int ringSize) {
		this.ringSize = ringSize;
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org.openscience.cdk.interfaces.IAtom)
	 */
	public boolean matches(IAtom atom) {
		if (atom.getFlag(CDKConstants.ISINRING)) {
			ArrayList ll = (ArrayList) atom
					.getProperty(CDKConstants.RING_SIZES);
			for (int i = 0; i < ll.size(); i++) {
				if (((Integer) ll.get(i)).intValue() == ringSize) {
					return true;
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.PseudoAtom#toString()
	 */
	public String toString() {
		return ("RingAtom(" + ringSize + ")");
	}
}
