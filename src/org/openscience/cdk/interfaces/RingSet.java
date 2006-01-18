/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.interfaces;

import java.util.Vector;
import java.util.List;

/**
 * Maintains a set of Ring objects.
 *
 * @cdk.module  interfaces
 *
 * @cdk.keyword ring, set of
 */
public interface RingSet extends List {

	/**
	 * Checks - and returns 'true' - if a certain ring is already
	 * stored in this setOfRings.
	 *
	 * @param   newRing  The ring to be tested if it is already stored here
	 * @return     true if it is already stored
	 */
	public boolean ringAlreadyInSet(Ring newRing);	
	
	/**
	 * Returns a vector of all rings that this bond is part of.
	 *
	 * @param   bond  The bond to be checked
	 * @return   A vector of all rings that this bond is part of  
	 */
	public Vector getRings(Bond bond);
	
	/**
	 * Returns a vector of all rings that this atom is part of.
	 *
	 * @param   atom  The atom to be checked
	 * @return   A vector of all rings that this bond is part of  
	 */
	public RingSet getRings(IAtom atom);
	
	/**
	 * Returns all the rings in the RingSet that share
	 * one or more atoms with a given ring.
	 *
	 * @param   ring  A ring with which all return rings must share one or more atoms
	 * @return  All the rings that share one or more atoms with a given ring.   
	 */
	public Vector getConnectedRings(Ring ring);
	
	/**
	 * Adds all rings of another RingSet if they are not allready part of this ring set.
	 *
	 * @param   ringSet  the ring set to be united with this one.
	 */
	public void add(RingSet ringSet);

	/**
	 * True, if at least one of the rings in the ringset cotains
	 * the given atom.
	 *
     * @param  atom Atom to check
	 * @return      true, if the ringset contains the atom
	 */
	public boolean contains(IAtom atom);
	
}
