/* RingSet.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, geelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

import java.util.Vector;
import javax.vecmath.*;

/**
 *  Implementation of a set of Rings.
 *  Maintains a Vector "rings" to store "ring" objects
 */
public class RingSet extends Vector{
	

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
	 * stored in this setOfRings
	 *
	 * @param   newRing  The ring to be tested if it is already stored here
	 * @return     true if it is already stored
	 */
	public boolean ringAlreadyInSet(Ring newRing)
	{
		Ring ring;
		Bond[] bonds;
		Bond bond;
		int equalCount;
		boolean equals;
		for (int f = 0; f < this.size(); f++)
		{
			equals = false;
			equalCount = 0;
			ring = (Ring)this.elementAt(f);
			bonds = ring.getBonds();
			if (bonds.length == newRing.getBonds().length)
			{
				for (int i = 0; i < bonds.length; i++)
				{
					bond = newRing.getBondAt(i);
					for (int n = 0; n < bonds.length; n++)
					{
						if (bond == bonds[n])
						{
							equals = true;
							equalCount++;
							break;
						}
					}
					if (!equals) break;
				}
			}
			if (equalCount == bonds.length)
			{
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
	 * We define the heaviest ring as the one with the highest number of double bonds.
	 * Needed for example for the placement of in-ring double bonds.
	 *
	 * @param   bond  A bond which must be contained by the heaviest ring 
	 * @return  The ring with the higest number of double bonds connected to a given bond   
	 */
	public Ring getHeaviestRing(Bond bond)
	{
		Vector rings = getRings(bond);
		Ring ring = null;
		int maxOrderSum = 0;
		for (int i = 0; i < rings.size(); i++)
		{
			if (maxOrderSum < ((Ring)rings.elementAt(i)).getOrderSum()) 
			{
				ring = (Ring)rings.elementAt(i);
				maxOrderSum = ring.getOrderSum();
			} 
		}
		return ring;
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
	 * Adds all rings of another RingSet if they are not allready part of this ring set
	 *
	 * @param   rs  the ring set to be united with this one.
	 */
	public void add(RingSet rs)
	{
		for (int f = 0; f < rs.size(); f++)
		{
			if (!contains(rs.elementAt(f)))
			{
				addElement(rs.elementAt(f));
			}
		}
	}

	/**
	 * Returns the geometric center of all the rings in this ringset
	 *
	 * @return the geometric center of the rings in this ringset
	 */
	public Point2d get2DCenter()
	{
		double centerX = 0, centerY = 0;
		for (int i = 0; i < size(); i++)
		{
			centerX += ((Ring)elementAt(i)).get2DCenter().x;
			centerY += ((Ring)elementAt(i)).get2DCenter().y;
		}
		Point2d point = new Point2d(centerX / ((double)size()), centerY / ((double)size()));
		return point;
	}


	/**
	 * True, if at least one of the rings in the ringset cotains
	 * the given atom
	 *
	 * @return true, if the ringset contains the atom
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
	 * Returns a sequence of string representations for all rings in the 
	 * RingSet
	 *
	 * @param   molecule  Used to assign numbers to each atom in the rings
	 * @return  A sequence of string representations for all rings in the RingSet   
	 */
	public String reportRingList(Molecule molecule)
	{
		String ringList = "";
		for (int f = 0; f < size(); f++)
		{
			ringList += ((Ring)elementAt(f)).toString(molecule) + "\n";
		}
		return ringList;
		
	}
}