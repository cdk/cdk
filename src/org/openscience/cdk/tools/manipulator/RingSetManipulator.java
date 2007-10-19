/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;

/**
 * @cdk.module standard
 */
public class RingSetManipulator {
    
	public static int getAtomCount(IRingSet set) {
		int count = 0;
		Iterator acs = set.atomContainers();
        while (acs.hasNext()) {
        	count += ((IAtomContainer)acs.next()).getAtomCount();
        }
        return count;
	}
	
	public static int getBondCount(IRingSet set) {
		int count = 0;
		Iterator acs = set.atomContainers();
        while (acs.hasNext()) {
        	count += ((IAtomContainer)acs.next()).getBondCount();
        }
        return count;
	}
	
	/**
     * Returns all the AtomContainer's in a RingSet.
     */
    public static List getAllAtomContainers(IRingSet set) {
    	List atomContainerList = new ArrayList();
    	Iterator acs = set.atomContainers();
    	while(acs.hasNext()){
    		atomContainerList.add((IAtomContainer)acs.next());
    	}
    	return atomContainerList;
    }
    /**
     * Sorts the rings in the set by size. The smallest ring comes
     * first.
     */
	public static void sort(IRingSet ringSet) {
		List ringList = new ArrayList();
		java.util.Iterator rings = ringSet.atomContainers();
		while (rings.hasNext()) {
			ringList.add(rings.next());
		}
		Collections.sort(ringList, new RingSizeComparator(RingSizeComparator.SMALL_FIRST));
		ringSet.removeAllAtomContainers();
		Iterator iter = ringList.iterator();
		while (iter.hasNext()) ringSet.addAtomContainer((IRing)iter.next());
		
	}

	/**
	 * We define the heaviest ring as the one with the highest number of double bonds.
	 * Needed for example for the placement of in-ring double bonds.
	 *
	 * @param   bond  A bond which must be contained by the heaviest ring 
	 * @return  The ring with the higest number of double bonds connected to a given bond
	 */
	public static IRing getHeaviestRing(IRingSet ringSet, IBond bond)
	{
		List rings = ringSet.getRings(bond);
		IRing ring = null;
		int maxOrderSum = 0;
		for (int i = 0; i < rings.size(); i++)
		{
			if (maxOrderSum < ((IRing)rings.get(i)).getBondOrderSum()) 
			{
				ring = (IRing)rings.get(i);
				maxOrderSum = ring.getBondOrderSum();
			} 
		}
		return ring;
	}

	/**
	 * Returns the ring with the highest numbers of other rings attached to it.
	 *
	 * @return the ring with the highest numbers of other rings attached to it.    
	 */
	public static IRing getMostComplexRing(IRingSet ringSet)
	{
		int[] neighbors = new int[ringSet.getAtomContainerCount()];
		IRing ring1, ring2;
		IAtom atom1, atom2;
		int mostComplex = 0, mostComplexPosition = 0;
		/* for all rings in this RingSet */
		for (int i = 0; i < ringSet.getAtomContainerCount(); i++)
		{
			/* Take each ring */
			ring1 = (IRing)ringSet.getAtomContainer(i);
			/* look at each Atom in this ring whether it is part of any other ring */
			for (int j = 0; j < ring1.getAtomCount(); j++)
			{
				atom1 = ring1.getAtom(j);
				/* Look at each of the other rings in the ringset */
				for (int k = i + 1; k < ringSet.getAtomContainerCount(); k++)
				{
					ring2 = (IRing)ringSet.getAtomContainer(k);
					if (ring1 != ring2)
					{
						for (int l = 0; l < ring2.getAtomCount(); l++)
						{
							atom2 = ring2.getAtom(l);
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
		return (IRing) ringSet.getAtomContainer(mostComplexPosition);
	}

	  /**
	   * Checks if <code>atom1</code> and <code>atom2</code> share membership in the same ring or ring system.
	   * Membership in the same ring is checked if the RingSet contains the SSSR of a molecule; membership in
	   * the same ring or same ring system is checked if the RingSet contains all rings of a molecule.<BR><BR>
	   * 
	   * <p><B>Important:</B> This method only returns meaningful results if <code>atom1</code> and
	   * <code>atom2</code> are members of the same molecule for which the RingSet was calculated!
	   *
	   * @param  atom1   The first atom
	   * @param  atom2   The second atom
	   * @return boolean true if <code>atom1</code> and <code>atom2</code> share membership of at least one ring or ring system, false otherwise
	   */
	  public static boolean isSameRing(IRingSet ringSet, IAtom atom1, IAtom atom2)
	  {
	    java.util.Iterator rings = ringSet.atomContainers();
	    while (rings.hasNext()) {
	      IRing ring = (IRing)rings.next();
	      if(ring.contains(atom1))
	        if(ring.contains(atom2))
	          return true;
	    }
	    return false;
	  }
	  
	  /**
	   * Checks - and returns 'true' - if a certain ring is already
	   * stored in the ringset. This is not a test for equality of Ring
	   * objects, but compares all Bond objects of the ring. 
	   *
	   * @param   newRing  The ring to be tested if it is already stored
	   * @return     true if it is already stored
	   */
      public static boolean ringAlreadyInSet(IRing newRing, IRingSet ringSet) {
          IRing ring;
//		  IBond[] bonds;
//		  IBond[] newBonds;
//		  IBond bond;
          int equalCount;
          boolean equals;
          for (int f = 0; f < ringSet.getAtomContainerCount(); f++) {
              equals = false;
              equalCount = 0;
              ring = (IRing) ringSet.getAtomContainer(f);

//              bonds = ring.getBonds();
//			  newBonds = newRing.getBonds();


              if (ring.getBondCount() == newRing.getBondCount()) {
                  Iterator bonds = ring.bonds();
                  Iterator newBonds = newRing.bonds();
                  while (newBonds.hasNext()) {
                      IBond newBond = (IBond) newBonds.next();
                      while (bonds.hasNext()) {
                          IBond bond = (IBond) bonds.next();
                          if (newBond == bond) {
                              equals = true;
                              equalCount++;
                              break;
                          }
                      }
                      if (!equals) break;
                  }
              }

              if (equalCount == ring.getBondCount()) {
                  return true;
              }
          }
          return false;
      }

    /**
     * Iterates over the rings in the ring set, and marks the ring
     * aromatic if all atoms and all bonds are aromatic.
     * 
     * @param ringset
     */
	public static void markAromaticRings(IRingSet ringset) {
		Iterator<IAtomContainer> rings = ringset.atomContainers();
		while (rings.hasNext()) {
			RingManipulator.markAromaticRings((IRing)rings.next());
		}
	}
}
