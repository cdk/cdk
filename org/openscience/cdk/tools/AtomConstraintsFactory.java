/*
 * AtomConstraintsFactory.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 *
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.tools;

import org.openscience.cdk.*;
import java.util.*;
import java.io.*;

/**
 *  StandardsIsotopes.java Used to store data of a particular isotope
 *
 *@author     steinbeck
 *@created    5. Juni 2001
 */

public class AtomConstraintsFactory {
	/** 
	  * This needs extensive checking
	  */
	private static AtomConstraints[] atomConstraints =
			{
			new AtomConstraints("H", 1, 1),
			new AtomConstraints("C", 3, 4),
			new AtomConstraints("Si", 3, 4),
			new AtomConstraints("N", 3, 3),
			new AtomConstraints("N", 3, 5),
			new AtomConstraints("P", 3, 3),
			new AtomConstraints("P", 3, 5),
			new AtomConstraints("O", 2, 2),
			new AtomConstraints("S", 2, 2),
			new AtomConstraints("F", 1, 1),
			new AtomConstraints("Cl", 1, 1),
			new AtomConstraints("Br", 1, 1),
			new AtomConstraints("I", 1, 1),
			new AtomConstraints("X", 3, 10)
			};

	/**
	 *  Gets the AtomConstraints attribute of the AtomConstraintsFactory class
	 *
	 *@param  elementSymbol  Description of Parameter
	 *@return                The AtomConstraints value
	 */
	public static Vector getAtomConstraints(String elementSymbol)
	{
		Vector contraints = new Vector();
		for (int f = 0; f < atomConstraints.length; f++) 
		{
			if (atomConstraints[f].elementSymbol.equals(elementSymbol)) 
			{
				contraints.addElement(atomConstraints[f]);
			}
		}
		return contraints;
	}
}

