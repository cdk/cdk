/* Ring.java
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

import javax.vecmath.*;
import java.util.Vector;

public class Ring extends AtomContainer
{

	/**
	 * constructs an empty ring.
	 *
	 */

	public Ring()
	{
		super();
		
	}
	

	/**
	 * constructs a ring that will have a certain size
	 *
	 * @param   ringSize  The size (number of atoms) the ring will have
	 */

	public Ring(int ringSize)
	{
		super(ringSize, ringSize);
	}
	

	/**
	 * Returns the number of atoms\edges in this ring.
	 *
	 * @return   The number of atoms\edges in this ring   
	 */

	public int getRingSize()
	{
		return this.atomCount;
	}
	

	/**
	 * Returns the next bond in order, relative to a given bond and atom.
	 * Example: Let the ring be composed of 0-1, 1-2, 2-3 and 3-0. A request getNextAtom(1-2, 2)
	 * will return Atom 3 from Bond 2-3.
	 *
	 * @param   bond  A bond for which an atom from a consecutive bond is sought
	 * @param   atom  A atom from the bond above to assign a search direction
	 * @return  A bond from the next bond in the order given by the above assignment   
	 */
	public Bond getNextBond(Bond bond, Atom atom)
	{
		Bond tempBond;
		for (int f = 0; f < getBondCount(); f++)
		{
			tempBond = getBondAt(f);
			if (tempBond.contains(atom) && bond != tempBond)
			{
				return tempBond;
			}
		}
		return null;
	}
	
	/**
	 * Returns the sum of all bond orders in the ring
	 *
	 * @return the sum of all bond orders in the ring    
	 */
	public int getOrderSum()
	{
		int orderSum = 0;
		for (int i = 0; i < bondCount; i++)
		{
			orderSum += bonds[i].getOrder();
		}
		return orderSum;
	}
	
	
	public String toString(Molecule molecule)
	{
		String str = "";
		for (int f = 0; f < getAtomCount(); f++)
		{
			try
			{
				str += molecule.getAtomNumber(getAtomAt(f)) +  " - ";
			}
			catch(Exception exc)
			{
			
			}
		}
		return str;
	
	}
}