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
}