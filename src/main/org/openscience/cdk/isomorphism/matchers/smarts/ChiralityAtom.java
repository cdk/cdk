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
 * This matches an atom with chirality property. It is not implemented yet. 
 * It'll match any atom right now.  
 *
 * @cdk.module  smarts
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS
 */
public class ChiralityAtom extends SMARTSAtom {
	/**
	 * The degree of the chirality
	 */
	private int degree;
	
	/**
	 * Whether unspecified chirality should be taken into consideration 
	 */
	private boolean unspecified;
	
	/**
	 * Whether the chirality is clockwise 
	 */
	private boolean clockwise;
	
    public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public boolean isUnspecified() {
		return unspecified;
	}

	public void setUnspecified(boolean unspecified) {
		this.unspecified = unspecified;
	}

	public boolean isClockwise() {
		return clockwise;
	}

	public void setClockwise(boolean clockwise) {
		this.clockwise = clockwise;
	}

	/**
     * Creates a new instance
     *
     */
    public ChiralityAtom() {
    	super();
    }

    public boolean matches(IAtom atom) {
    	// TODO: Chirality matching logic
        return true;
    }
}
