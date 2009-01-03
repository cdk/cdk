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
 * This smarts atom matches any atom with a certain number of explicit
 * connections.
 *
 * @cdk.module  smarts
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS 
 */
public class ExplicitConnectionAtom extends SMARTSAtom {
	private static final long serialVersionUID = 7453671653627040279L;

	/**
	 * Number of explicit connections.
	 */
	private int numOfConnection;

	/**
	 * Creates a new instance.
	 */
	public ExplicitConnectionAtom() {
		super();
	}

	/**
	 * Creates a new instance.
	 */
	public ExplicitConnectionAtom(int connection) {
		super();
		this.numOfConnection = connection;
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org.openscience.cdk.interfaces.IAtom)
	 */
    public boolean matches(IAtom atom) {
        int ih = atom.getHydrogenCount() == CDKConstants.UNSET ? 0 : atom.getHydrogenCount();
        int conn = ((Integer) atom.getProperty(CDKConstants.TOTAL_CONNECTIONS)).intValue() - ih;
        return numOfConnection == conn;
    }

    /**
	 * Returns number of explicit connections.
	 */
	public int getNumOfConnection() {
		return numOfConnection;
	}

	/**
	 * Sets number of explicit connections.
	 * 
	 * @param numOfConnection
	 */
	public void setNumOfConnection(int numOfConnection) {
		this.numOfConnection = numOfConnection;
	}
}
