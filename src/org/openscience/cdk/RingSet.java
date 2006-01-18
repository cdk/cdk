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

package org.openscience.cdk;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

/**
 * Maintains a set of Ring objects.
 *
 * @cdk.module data
 *
 * @cdk.keyword     ring, set of
 */
public class RingSet extends Vector implements java.io.Serializable, org.openscience.cdk.interfaces.RingSet
{

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -1859706720484903351L;

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
	 * Checks - and returns 'true' - if a certain ring is already
	 * stored in this setOfRings.
	 *
	 * @param   newRing  The ring to be tested if it is already stored here
	 * @return     true if it is already stored
	 */
	public boolean ringAlreadyInSet(org.openscience.cdk.interfaces.Ring newRing)
	{
		Ring ring;
		org.openscience.cdk.interfaces.Bond[] bonds;
		org.openscience.cdk.interfaces.Bond[] newBonds;
		org.openscience.cdk.interfaces.Bond bond;
		int equalCount;
		boolean equals;
		for (int f = 0; f < this.size(); f++)
		{
			equals = false;
			equalCount = 0;
			ring = (Ring)this.elementAt(f);
			bonds = ring.getBonds();
            newBonds = newRing.getBonds();
			if (bonds.length == newBonds.length) {
				for (int i = 0; i < bonds.length; i++) {
					bond = newBonds[i];
					for (int n = 0; n < bonds.length; n++) {
						if (bond == bonds[n]) {
							equals = true;
							equalCount++;
							break;
						}
					}
					if (!equals) break;
				}
			}
			if (equalCount == bonds.length) {
				return true;
			}
		}
		return false;	
	}
	
	
	/**
	 * Returns a vector of all rings that this bond is part of.
	 *
	 * @param   bond  The bond to be checked
	 * @return   A vector of all rings that this bond is part of  
	 */

	public Vector getRings(org.openscience.cdk.interfaces.Bond bond)
	{
		Vector rings = new Vector();
		Ring ring;
		for (int i = 0; i < this.size();i++)
		{
			ring = (Ring)elementAt(i);
			if (ring.contains(bond))
			{
				rings.addElement(ring);
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

	public org.openscience.cdk.interfaces.RingSet getRings(org.openscience.cdk.interfaces.IAtom atom)
	{
		RingSet rings = new RingSet();
		Ring ring;
		for (int i = 0; i < this.size();i++)
		{
			ring = (Ring)elementAt(i);
			if (ring.contains(atom))
			{
				rings.addElement(ring);
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

	public Vector getConnectedRings(org.openscience.cdk.interfaces.Ring ring)
	{
		Vector connectedRings = new Vector();
		Ring tempRing;
		org.openscience.cdk.interfaces.IAtom atom;
		for (int i  = 0; i < ring.getAtomCount(); i++)
		{
			atom = ring.getAtomAt(i);
			for (int j = 0; j < size(); j++)
			{	
				tempRing = (Ring)elementAt(j);
				if (tempRing != ring && tempRing.contains(atom))
				{
					connectedRings.addElement(tempRing);
				}
			}
		}
		return connectedRings;
	}

	/**
	 * Adds all rings of another RingSet if they are not allready part of this ring set.
	 *
	 * @param   ringSet  the ring set to be united with this one.
	 */
	public void add(org.openscience.cdk.interfaces.RingSet ringSet)
	{
		for (int f = 0; f < ringSet.size(); f++)
		{
			if (!contains(ringSet.get(f)))
			{
				addElement(ringSet.get(f));
			}
		}
	}

	/**
	 * True, if at least one of the rings in the ringset cotains
	 * the given atom.
	 *
     * @param  atom Atom to check
	 * @return      true, if the ringset contains the atom
	 */
	public boolean contains(org.openscience.cdk.interfaces.IAtom atom)
	{
		for (int i = 0; i < size(); i++)
		{
			if (((Ring)elementAt(i)).contains(atom))
			{
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
	public Object clone() {
		RingSet clone = (RingSet)super.clone();
        // clone the rings
        clone.removeAllElements();
        Enumeration rings = elements();
        while (rings.hasMoreElements()) {
            Object possibleRing = rings.nextElement();
            if (possibleRing instanceof ChemObject) {
                clone.addElement(((ChemObject)possibleRing).clone());
            } else {
                clone.addElement(possibleRing);
            }
        }
		return clone;
	}

    /**
     * Returns the String representation of this RingSet.
     *
     * @return The String representation of this RingSet
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("RingSet(");
        buffer.append(this.hashCode()).append(", ");
        buffer.append("R=").append(size()).append(", ");
        Enumeration rings = elements();
        while (rings.hasMoreElements()) {
            Ring possibleRing = (Ring)rings.nextElement();
            buffer.append(possibleRing.toString());
            if (rings.hasMoreElements()) {
                buffer.append(", ");
            }
        }
        buffer.append(")");
        return buffer.toString();
    }
    
 }
