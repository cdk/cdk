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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.tools.manipulator.RingSetManipulatorTest")
public class RingSetManipulator {
    /**
     * Return the total number of atoms over all the rings in the colllection.
     *
     * @param set The collection of rings
     * @return  The total number of atoms
     */
    @TestMethod("testGetAtomCount")
    public static int getAtomCount(IRingSet set) {
		int count = 0;
        for (IAtomContainer atomContainer : set.atomContainers()) {
            count += atomContainer.getAtomCount();
        }
        return count;
	}

    /**
     * Return the total number of bonds over all the rings in the colllection.
     *
     * @param set The collection of rings
     * @return The total number of  bonds
     */
    @TestMethod("testGetBondCount")
    public static int getBondCount(IRingSet set) {
        int count = 0;
        for (IAtomContainer atomContainer : set.atomContainers()) {
            count += atomContainer.getBondCount();
        }
        return count;
	}
	
	/**
     * Returns all the AtomContainer's in a RingSet.
     * @param set The collection of rings
     * @return A list of IAtomContainer objects corresponding to individual rings
     */
    @TestMethod("testGetAllAtomContainers_IRingSet")
    public static List<IAtomContainer> getAllAtomContainers(IRingSet set) {
    	List<IAtomContainer> atomContainerList = new ArrayList<IAtomContainer>();
        for (IAtomContainer atomContainer : set.atomContainers()) {
            atomContainerList.add(atomContainer);
        }
    	return atomContainerList;
    }

    /**
     * Sorts the rings in the set by size. The smallest ring comes
     * first.
     * @param ringSet The collection of rings
     */
    @TestMethod("testSort_IRingSet")
    public static void sort(IRingSet ringSet) {
		List<IRing> ringList = new ArrayList<IRing>();
        for (IAtomContainer atomContainer : ringSet.atomContainers()) {
            ringList.add((IRing) atomContainer);
        }
		Collections.sort(ringList, new RingSizeComparator(RingSizeComparator.SMALL_FIRST));
		ringSet.removeAllAtomContainers();
        for (IAtomContainer aRingList : ringList) ringSet.addAtomContainer(aRingList);
		
	}

	/**
	 * We define the heaviest ring as the one with the highest number of double bonds.
	 * Needed for example for the placement of in-ring double bonds.
	 *
	 * @param ringSet The collection of rings
     * @param   bond  A bond which must be contained by the heaviest ring
	 * @return  The ring with the higest number of double bonds connected to a given bond
	 */
    @TestMethod("testGetHeaviestRing_IRingSet_IBond")
    public static IRing getHeaviestRing(IRingSet ringSet, IBond bond)
	{
		IRingSet rings = ringSet.getRings(bond);
		IRing ring = null;
		int maxOrderSum = 0;
        for (Object ring1 : rings.atomContainers()) {
            if (maxOrderSum < ((IRing) ring1).getBondOrderSum()) {
                ring = (IRing) ring1;
                maxOrderSum = ring.getBondOrderSum();
            }
        }
		return ring;
	}

	/**
	 * Returns the ring with the highest numbers of other rings attached to it.
	 *
	 * @param ringSet The collection of rings
     * @return the ring with the highest numbers of other rings attached to it.
	 */
    @TestMethod("testGetMostComplexRing_IRingSet")
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
	   * @param ringSet The collection of rings
       * @param  atom1   The first atom
	   * @param  atom2   The second atom
	   * @return boolean true if <code>atom1</code> and <code>atom2</code> share membership of at least one ring or ring system, false otherwise
	   */
      @TestMethod("testIsSameRing_IRingSet_IAtom_IAtom")
      public static boolean isSameRing(IRingSet ringSet, IAtom atom1, IAtom atom2)
	  {
          for (IAtomContainer atomContainer : ringSet.atomContainers()) {
              IRing ring = (IRing) atomContainer;
              if (ring.contains(atom1) && ring.contains(atom2)) return true;
          }
	    return false;
	  }
	  
	  /**
	   * Checks - and returns 'true' - if a certain ring is already
	   * stored in the ringset. This is not a test for equality of Ring
	   * objects, but compares all Bond objects of the ring. 
	   *
	   * @param   newRing  The ring to be tested if it is already stored
	   * @param ringSet The collection of rings
       * @return     true if it is already stored
	   */
      @TestMethod("testRingAlreadyInSet_IRing_IRingSet")
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
                  for (IBond newBond : newRing.bonds()) {
                      for (IBond bond : ring.bonds()) {
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
     * This method assumes that aromaticity perception has been done before hand.
     * 
     * @param ringset The collection of rings
     */
    @TestMethod("markAromatic")
    public static void markAromaticRings(IRingSet ringset) {
        for (IAtomContainer atomContainer : ringset.atomContainers()) {
            RingManipulator.markAromaticRings((IRing) atomContainer);
        }
	}
}
