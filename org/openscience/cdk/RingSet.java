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
	 * We define the heaviest ring as the one with the highest number of double bonds.
	 * Needed for example for the placement of in-ring double bonds.
	 *
	 * @param   bond   
	 * @return     
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
	 * Returns the ring with the highest numbers of other rings attached to it.
	 *
	 * @return the ring with the highest numbers of other rings attached to it.    
	 */
	public Ring getMostComplexRing()
	{
		int[] neighbors = new int[size()];
		Ring ring;
		Bond bond1, bond2;
		int mostComplex = 0, mostComplexPosition = 0;
		for (int i = 0; i < size(); i++)
		{
			ring = (Ring)elementAt(i);
			for (int j = 0; j < ring.getBondCount(); j++)
			{
				bond1 = ring.getBondAt(j);
				for (int k = i + 1; k < size(); k++)
				{
					ring = (Ring)elementAt(k);
					for (int l = 0; l < ring.getBondCount(); l++)
					{
						bond2 = ring.getBondAt(l);
						if (bond1 == bond2)
						{
							neighbors[i]++;								
							neighbors[k]++;
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
}