/* RingPartitioner.java
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
package org.openscience.cdk.ringsearch;


import java.util.*;
import org.openscience.cdk.*;


public class RingPartitioner
{

	public static boolean debug = false; // minimum details
	
	public static Vector partitionRings(RingSet ringSet)
	{
		Vector ringSets = new Vector();
		Ring ring;
		RingSet rs = (RingSet)ringSet.clone();
		do
		{
			ring = (Ring)rs.elementAt(0);
			RingSet newRs = new RingSet();
			newRs.addElement(ring);
			ringSets.addElement(walkRingSystem(rs, ring, newRs));
		
		}while(rs.size() > 0);
		
		
		return ringSets;
	}
	
	private static RingSet walkRingSystem(RingSet rs, Ring ring, RingSet newRs)
	{
		Ring tempRing;
		Vector tempRings = rs.getConnectedRings(ring);
		if (debug) System.out.println("walkRingSystem -> tempRings.size(): " + tempRings.size());
		rs.removeElement(ring);		
		for (int f = 0; f < tempRings.size(); f++)
		{
			tempRing = (Ring)tempRings.elementAt(f);
			if (!newRs.contains(tempRing))
			{
				newRs.addElement(tempRing);
				newRs.add(walkRingSystem(rs, tempRing, newRs));
			}
		}
		return newRs;
	}

}








