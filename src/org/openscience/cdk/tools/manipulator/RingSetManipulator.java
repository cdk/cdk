/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
 */
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @cdk.module standard
 */
public class RingSetManipulator {
    
	/**
	 * Returns all the atoms and bonds from all the rings in the RingSet 
	 * in one AtomContainer.
	 *
	 * @return an AtomContainer with all atoms and bonds from the RingSet
	 */
	public static AtomContainer getAllInOneContainer(RingSet ringSet) {
		AtomContainer container = new AtomContainer();
		for (int i = 0; i < ringSet.size(); i++) {
			container.add((Ring)ringSet.elementAt(i));
		}
		return container;
	}

    /**
     * Sorts the rings in the set by size. The largest ring comes
     * first.
     */
	public static void sort(RingSet ringSet) {
		Collections.sort(ringSet, new RingSizeComparator(RingSizeComparator.LARGE_FIRST));	
	}

	/**
	 * We define the heaviest ring as the one with the highest number of double bonds.
	 * Needed for example for the placement of in-ring double bonds.
	 *
	 * @param   bond  A bond which must be contained by the heaviest ring 
	 * @return  The ring with the higest number of double bonds connected to a given bond
	 */
	public static Ring getHeaviestRing(RingSet ringSet, Bond bond)
	{
		Vector rings = ringSet.getRings(bond);
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
	public static Ring getMostComplexRing(RingSet ringSet)
	{
		int[] neighbors = new int[ringSet.size()];
		Ring ring1, ring2;
		Atom atom1, atom2;
		int mostComplex = 0, mostComplexPosition = 0;
		/* for all rings in this RingSet */
		for (int i = 0; i < ringSet.size(); i++)
		{
			/* Take each ring */
			ring1 = (Ring)ringSet.elementAt(i);
			/* look at each Atom in this ring whether it is part of any other ring */
			for (int j = 0; j < ring1.getAtomCount(); j++)
			{
				atom1 = ring1.getAtomAt(j);
				/* Look at each of the other rings in the ringset */
				for (int k = i + 1; k < ringSet.size(); k++)
				{
					ring2 = (Ring)ringSet.elementAt(k);
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
		return (Ring) ringSet.elementAt(mostComplexPosition);
	}
}
