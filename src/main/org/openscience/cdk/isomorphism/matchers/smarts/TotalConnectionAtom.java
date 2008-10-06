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

/**
 * This matches an atom using total number of connections.
 * 
 * @cdk.module  smarts
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS
 */
public class TotalConnectionAtom extends SMARTSAtom {
	private static final long serialVersionUID = 2714616726873309671L;

	/**
	 * Creates a new instance.
	 */
	public TotalConnectionAtom(int count) {
		this.setProperty(CDKConstants.TOTAL_CONNECTIONS, count);
	}
	
	/**
	 * This returns the total connection of an atom.
	 */
	public int getTC(IAtom atom) {
		if (atom.getProperty(CDKConstants.TOTAL_CONNECTIONS) != null)
			return (Integer) atom.getProperty(CDKConstants.TOTAL_CONNECTIONS);
		else
			return 0;
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org.openscience.cdk.interfaces.IAtom)
	 */
	public boolean matches(IAtom atom) {
		return (getTC(atom) != 0 && getTC(atom) == getTC(this));
	}
}
