/* ChemObject.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk;

import org.openscience.cdk.event.*;
import java.util.*;

/**
 * The base class for all chemical objects in this cdk. It provides methods
 * for adding listeners and for their notification of events, as well a 
 * a hash table for administration of physical or chemical properties
 */

public class ChemObject implements CDKConstants
{
	/** Vector for listener administration */
	protected Vector chemObjects = new Vector();
	protected int size;
	protected Hashtable physicalProperties = new Hashtable();
	/* You will frequently have to use some flags on a ChemObject
	 * for example if you want to draw a molecule and see
	 * if you've already drawn an atom, or in a ring search to 
	 * check whether a vertex has been visited in a graph traversal.
	 * Use these flags while addressing particular positions in the
	 * flag array with self-defined constants (flags[VISITED] = true).
	 * 10 flags per object should be more than enough.
	 */
	protected boolean[] flags = new boolean[10];
	/** Array of multipurpose vectors. Handle like described for the
	  * flags above 
	  */
	public Vector[] pointers;

	/**
	 * Use this to add yourself to this ChemObject as a listener. 
	 * In order to do so, you must implement the ChemObjectListener Interface
	 *
	 * @param   col  You, the ChemObjectListener
	 */
	public void addListener(ChemObjectListener col)
	{
		if (!chemObjects.contains(col))
		{
			chemObjects.addElement(col);
		}
		// Should we through an exception if col is already in here or
		// just silently ignore it?
	}


	/**
	 * Use this to remove a ChemObjectListener from the 
	 * ListenerList of this ChemObject. It will then not be notified
	 * of change in this object anymore
	 *
	 * @param   col  The ChemObjectListener to be removed
	 */
	public void removeListener(ChemObjectListener col)
	{
		if (chemObjects.contains(col))
		{
			chemObjects.removeElement(col);
		}
	}

	/**
	 * This should be triggered by an method that changes the content
	 * of an object to that the registered listeners can react to it.
	 *
	 */
	protected void notifyChanged()
	{
		for (int f = 0; f < chemObjects.size(); f++)
		{
			((ChemObjectListener)chemObjects.elementAt(f)).stateChanged(new ChemObjectChangeEvent(this));
		}
	}



	/**
	 * Return the size of this ChemObject. This could be 
	 * the number of atoms for a Molecule or the number of Electron 
	 * in a bond
	 *
	 * @return     The size of the ChemObject
	 */
	public int getSize()
	{
		return this.size;
	}


	/**
	 * Set the Size of this ChemObject. 
	 *
	 * @param   size  The size of this ChemObject
	 */
	public void setSize(int size)
	{
		this.size = size;
	}
	
}



