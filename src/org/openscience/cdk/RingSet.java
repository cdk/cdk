/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import javax.vecmath.Point2d;


/**
 * Maintains a set of Ring objects.
 *
 * @cdk.module core
 *
 * @cdk.keyword     ring, set of
 */
public class RingSet extends Vector implements java.io.Serializable, Cloneable
{

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
	public boolean ringAlreadyInSet(Ring newRing)
	{
		Ring ring;
		Bond[] bonds;
		Bond[] newBonds;
		Bond bond;
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

	public Vector getRings(Bond bond)
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

	public RingSet getRings(Atom atom)
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

	public Vector getConnectedRings(Ring ring)
	{
		Vector connectedRings = new Vector();
		Ring tempRing;
		Atom atom;
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
	 * Returns the ring with the highest numbers of other rings attached to it.
	 *
	 * @return the ring with the highest numbers of other rings attached to it.    
	 */
	public Ring getMostComplexRing()
	{
		int[] neighbors = new int[size()];
		Ring ring1, ring2;
		Atom atom1, atom2;
		int mostComplex = 0, mostComplexPosition = 0;
		/* for all rings in this RingSet */
		for (int i = 0; i < size(); i++)
		{
			/* Take each ring */
			ring1 = (Ring)elementAt(i);
			/* look at each Atom in this ring whether it is part of any other ring */
			for (int j = 0; j < ring1.getAtomCount(); j++)
			{
				atom1 = ring1.getAtomAt(j);
				/* Look at each of the other rings in the ringset */
				for (int k = i + 1; k < size(); k++)
				{
					ring2 = (Ring)elementAt(k);
					if (ring1 != ring2)
					{
						for (int l = 0; l < ring2.getAtomCount(); l++)
						{
							atom2 = ring2.getAtomAt(l);
							if (atom1 == atom2)
							{
								neighbors[i]++;								
								neighbors[k]++;
								break;
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < neighbors.length; i++)
		{
			if (neighbors[i] > mostComplex)
			{
				mostComplex = neighbors[i];
				mostComplexPosition = i;
			}
		}
		return (Ring) elementAt(mostComplexPosition);
	}
	

	/**
	 * Adds all rings of another RingSet if they are not allready part of this ring set.
	 *
	 * @param   ringSet  the ring set to be united with this one.
	 */
	public void add(RingSet ringSet)
	{
		for (int f = 0; f < ringSet.size(); f++)
		{
			if (!contains(ringSet.elementAt(f)))
			{
				addElement(ringSet.elementAt(f));
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
	public boolean contains(Atom atom)
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
        Enumeration rings = clone.elements();
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
}
