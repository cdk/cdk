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
					bond = newRing.getBond(i);
					for (int n = 0; n < bonds.length; n++)
					{
						if (bond.equals(bonds[n]))
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


//	/**
//	 * Returns a vector that contains all bonds participating
//	 * in one of the rings in this ringset.
//	 *
//	 * @return   The Vector that contains all the bonds
//	 */
//	public Vector getBonds()
//	{
//		Vector bonds = new Vector();
//		Ring ring;
//		for (int i = 0; i < this.size(); i++)
//		{
//			ring = (Ring)elementAt(i);
//			for (int f = 0; f < ring.getRingSize(); f++)
//			{
//				if (!bonds.contains(ring.getBond(f)))
//				{
//					bonds.addElement(ring.getBond(f));
//				}
//			} 
//		}	
//		return bonds;
//	}
}