/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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

package org.openscience.cdk;

import org.openscience.cdk.interfaces.*;

import java.io.Serializable;

/**
 * Maintains a set of Ring objects.
 *
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword     ring, set of
 */
public class RingSet extends AtomContainerSet implements Serializable, IRingSet, Cloneable {

	private static final long serialVersionUID = 7168431521057961434L;
	
	/** Flag to denote that the set is order with the largest ring first? */
	public final static int LARGE_FIRST = 1;
    /** Flag to denote that the set is order with the smallest ring first? */
	public final static int SMALL_FIRST = 2;
	
	/**
	 * The constructor.
	 *
	 */
	public RingSet()
	{
		super();
	}
	
	
	/**
	 * Returns a vector of all rings that this bond is part of.
	 *
	 * @param   bond  The bond to be checked
	 * @return   A vector of all rings that this bond is part of  
	 */

	public IRingSet getRings(IBond bond)
	{
		IRingSet rings = bond.getBuilder().newRingSet();
		Ring ring;
		for (int i = 0; i < getAtomContainerCount(); i++)
		{
			ring = (Ring)getAtomContainer(i);
			if (ring.contains(bond))
			{
				rings.addAtomContainer(ring);
			}
		}
		return rings;
	}
	
	/**
	 * Returns a vector of all rings that this atom is part of.
	 *
	 * @param   atom  The atom to be checked
	 * @return   A vector of all rings that this bond is part of  
	 */

	public IRingSet getRings(IAtom atom)
	{
		IRingSet rings = new RingSet();
		IRing ring;
		for (int i = 0; i < getAtomContainerCount();i++)
		{
			ring = (Ring)getAtomContainer(i);
			if (ring.contains(atom))
			{
				rings.addAtomContainer(ring);
			}
		}
		return rings;
	}

	/**
	 * Returns all the rings in the RingSet that share
	 * one or more atoms with a given ring.
	 *
	 * @param   ring  A ring with which all return rings must share one or more atoms
	 * @return  All the rings that share one or more atoms with a given ring.   
	 */

	public IRingSet getConnectedRings(IRing ring)
	{
		IRingSet connectedRings = ring.getBuilder().newRingSet();
		IRing tempRing;
		IAtom atom;
		for (int i  = 0; i < ring.getAtomCount(); i++)
		{
			atom = ring.getAtom(i);
			for (int j = 0; j < getAtomContainerCount(); j++)
			{	
				tempRing = (IRing)getAtomContainer(j);
				if (tempRing != ring && !connectedRings.contains(tempRing) && tempRing.contains(atom))
				{
					connectedRings.addAtomContainer(tempRing);
				}
			}
		}
		return connectedRings;
	}

	/**
	 * Adds all rings of another RingSet if they are not already part of this ring set.
     *
     * If you want to add a single ring to the set use {@link #addAtomContainer(org.openscience.cdk.interfaces.IAtomContainer)} 
	 *
	 * @param   ringSet  the ring set to be united with this one.
	 */
	public void add(IRingSet ringSet)
	{
		for (int f = 0; f < ringSet.getAtomContainerCount(); f++)
		{
			if (!contains(ringSet.getAtomContainer(f)))
			{
				addAtomContainer(ringSet.getAtomContainer(f));
			}
		}
	}


    /**
	 * True, if at least one of the rings in the ringset contains
	 * the given atom.
	 *
     * @param  atom Atom to check
	 * @return      true, if the ringset contains the atom
	 */
	public boolean contains(IAtom atom) {
		for (int i = 0; i < getAtomContainerCount(); i++) {
			if (getAtomContainer(i).contains(atom)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks for presence of a ring in this RingSet.
	 * 
	 * @param  ring  The ring to check
	 * @return  true if ring is part of RingSet
	 * 
	 */
	public boolean contains(IAtomContainer ring) {
		for (int i = 0; i < getAtomContainerCount(); i++) {
			if (ring == getAtomContainer(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Clones this <code>RingSet</code> including the Rings.
	 *
	 * @return  The cloned object
	 */
	public Object clone() throws CloneNotSupportedException {
        return super.clone();
	}

    /**
     * Returns the String representation of this RingSet.
     *
     * @return The String representation of this RingSet
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(32);
        buffer.append("RingSet(");
        buffer.append(this.hashCode());
        if (getAtomContainerCount() > 0) {
        	buffer.append(", R=").append(getAtomContainerCount()).append(", ");
        	for (int i = 0; i < atomContainerCount; i++) {
        		IRing possibleRing = (IRing)atomContainers[i];
        		buffer.append(possibleRing.toString());
        		if (i+1 < atomContainerCount) {
        			buffer.append(", ");
        		}
        	}
        }
        buffer.append(')');
        return buffer.toString();
    }
    
 }
