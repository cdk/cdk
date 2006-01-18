/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.tools.manipulator;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.Ring;
import org.openscience.cdk.interfaces.RingSet;

/**
 * @cdk.module standard
 * @cdk.bug    1117775
 */
public class RingSetManipulator {
    
	/**
	 * Returns all the atoms and bonds from all the rings in the RingSet 
	 * in one AtomContainer.
	 *
	 * @return an AtomContainer with all atoms and bonds from the RingSet
	 */
	public static IAtomContainer getAllInOneContainer(RingSet ringSet) {
		// FIXME: make RingSet a subclass of IChemObject (see bug #) and clean up
		// the code in the next line
		IAtomContainer container = ((Ring)ringSet.get(0)).getBuilder().newAtomContainer();
		for (int i = 0; i < ringSet.size(); i++) {
			container.add((Ring)ringSet.get(i));
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
	public static Ring getHeaviestRing(RingSet ringSet, IBond bond)
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
		IAtom atom1, atom2;
		int mostComplex = 0, mostComplexPosition = 0;
		/* for all rings in this RingSet */
		for (int i = 0; i < ringSet.size(); i++)
		{
			/* Take each ring */
			ring1 = (Ring)ringSet.get(i);
			/* look at each Atom in this ring whether it is part of any other ring */
			for (int j = 0; j < ring1.getAtomCount(); j++)
			{
				atom1 = ring1.getAtomAt(j);
				/* Look at each of the other rings in the ringset */
				for (int k = i + 1; k < ringSet.size(); k++)
				{
					ring2 = (Ring)ringSet.get(k);
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
		return (Ring) ringSet.get(mostComplexPosition);
	}

	  /**
	   * Checks if <code>atom1</code> and <code>atom2</code> share membership in the same ring or ring system.
	   * Membership in the same ring is checked if the RingSet contains the SSSR of a molecule; membership in
	   * the same ring or same ring system is checked if the RingSet contains all rings of a molecule.<BR><BR>
	   * <B>Important:</B> This method only returns meaningful results if <code>atom1</code> and
	   * <code>atom2</code> are members of the same molecule for which the RingSet was calculated!
	   *
	   * @param atom1 The first atom
	   * @param atom2 The second atom
	   * @return ???boolean true if <code>atom1</code> and <code>atom2</code> share membership of at least one ring or ring system, false otherwise
	   */
	  public static boolean isSameRing(RingSet ringSet, IAtom atom1, IAtom atom2)
	  {
	    Iterator iterator = ringSet.iterator();
	    while(iterator.hasNext())
	    {
	      Ring ring = (Ring) iterator.next();
	      if(ring.contains(atom1))
	        if(ring.contains(atom2))
	          return true;
	    }
	    return false;
	  }
}
