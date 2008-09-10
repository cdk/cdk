/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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


/**
 * Maintains a set of Ring objects.
 *
 * @cdk.module  interfaces
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword ring, set of
 */
public interface IRingSet extends IAtomContainerSet {
	
	/**
	 * Returns a vector of all rings that this bond is part of.
	 *
	 * @param   bond  The bond to be checked
	 * @return   A vector of all rings that this bond is part of  
	 */
	public IRingSet getRings(IBond bond);
	
	/**
	 * Returns a vector of all rings that this atom is part of.
	 *
	 * @param   atom  The atom to be checked
	 * @return   A vector of all rings that this bond is part of  
	 */
	public IRingSet getRings(IAtom atom);
	
	/**
	 * Returns all the rings in the RingSet that share
	 * one or more atoms with a given ring.
	 *
	 * @param   ring  A ring with which all return rings must share one or more atoms
	 * @return  All the rings that share one or more atoms with a given ring.   
	 */
	public IRingSet getConnectedRings(IRing ring);
	
	/**
	 * Adds all rings of another RingSet if they are not allready part of this ring set.
     *
     * If you want to add a single ring to the set use {@link #addAtomContainer(org.openscience.cdk.interfaces.IAtomContainer)} 
	 *
	 * @param   ringSet  the ring set to be united with this one.
	 */
	public void add(IRingSet ringSet);

   
    /**
	 * True, if at least one of the rings in the ringset contains
	 * the given atom.
	 *
     * @param  atom IAtom to check
	 * @return      true, if the ringset contains the atom
	 */
	public boolean contains(IAtom atom);
	
	/**
	 * True, if this set contains the IAtomContainer.
	 *
     * @param  container IAtomContainer to check
	 * @return           true, if the ringset contains the container
	 */
	public boolean contains(IAtomContainer container);
	
}
