/* ConnectivityChecker.java
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

package org.openscience.cdk.tools;

import org.openscience.cdk.*;
import java.util.*;

/**
 * Tool class for checking whether the (sub)structure in an 
 * AtomContainer is connected
 */
 
public class ConnectivityChecker
{

	public boolean isConnected(AtomContainer ac)
	{
		boolean doneSomething;
		Bond bond = null;
		Atom atom = null;
		Vector visitedAtoms = new Vector();
		Vector bonds = new Vector();
		boolean foundConnection = false;
		System.out.println("Connectivity Checker");
		for (int f = 0; f < ac.getBondCount();f ++)
		{
			bonds.addElement(ac.getBondAt(f));
		}
		bond = (Bond)bonds.remove(0);
		visitedAtoms.add(bond.getAtomsVector());
		do
		{
			for (int f = 0; f < bonds.size(); f ++)
			{
				foundConnection = false;
				bond = (Bond)bonds.elementAt(f);
				for (int g = 0; g < bond.getAtomCount(); f++)
				{
					if (visitedAtoms.contains(bond.getAtomAt(g)))
					{
						foundConnection = true;
						break;
					}
				}
				if (foundConnection) 
				{
					visitedAtoms.add(bond.getAtomsVector());					
					bonds.remove(bond);
					break;
				}
			}
			System.out.println(bonds.size());
		}while(bonds.size() > 0 || foundConnection);
		if (!foundConnection && bonds.size() > 0)
		{
			return false;
		}

		return true;
	}
	

}