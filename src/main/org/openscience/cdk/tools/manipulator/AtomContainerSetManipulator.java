/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @see ChemModelManipulator
 */
@TestClass("org.openscience.cdk.tools.manipulator.AtomContainerSetManipulatorTest")
public class AtomContainerSetManipulator {

    @TestMethod("testGetAtomCount_IAtomContainerSet")
    public static int getAtomCount(IAtomContainerSet set) {
		int count = 0;
        for (IAtomContainer atomContainer : set.atomContainers()) {
            count += (atomContainer).getAtomCount();
        }
        return count;
	}

    @TestMethod("testGetBondCount_IAtomContainerSet")
    public static int getBondCount(IAtomContainerSet set) {
		int count = 0;
        for (IAtomContainer atomContainer : set.atomContainers()) {
            count += (atomContainer).getBondCount();
        }
        return count;
	}

    @TestMethod("testRemoveAtomAndConnectedElectronContainers_IAtomContainerSet_IAtom")
    public static void removeAtomAndConnectedElectronContainers(IAtomContainerSet set, IAtom atom) {
        for (IAtomContainer atomContainer : set.atomContainers()) {
            if (atomContainer.contains(atom)) {
                atomContainer.removeAtomAndConnectedElectronContainers(atom);
                IMoleculeSet molecules = ConnectivityChecker.partitionIntoMolecules(atomContainer);
                if (molecules.getAtomContainerCount() > 1) {
                    set.removeAtomContainer(atomContainer);
                    for (int k = 0; k < molecules.getAtomContainerCount(); k++) {
                        set.addAtomContainer(molecules.getAtomContainer(k));
                    }
                }
                return;
            }
        }
    }

    @TestMethod("testRemoveElectronContainer_IAtomContainerSet_IElectronContainer")
    public static void removeElectronContainer(IAtomContainerSet set, IElectronContainer electrons) {
        for (IAtomContainer atomContainer : set.atomContainers()) {
            if (atomContainer.contains(electrons)) {
                atomContainer.removeElectronContainer(electrons);
                IMoleculeSet molecules = ConnectivityChecker.partitionIntoMolecules(atomContainer);
                if (molecules.getAtomContainerCount() > 1) {
                    set.removeAtomContainer(atomContainer);
                    for (int k = 0; k < molecules.getAtomContainerCount(); k++) {
                        set.addAtomContainer(molecules.getMolecule(k));
                    }
                }
                return;
            }
        }
    }
    
	/**
     * Returns all the AtomContainer's of a MoleculeSet.
     *
     * @param set The collection of IAtomContainer objects
     * @return A list of individual IAtomContainer's
     */
    @TestMethod("testGetAllAtomContainers_IAtomContainerSet")
    public static List<IAtomContainer> getAllAtomContainers(IAtomContainerSet set) {
    	List<IAtomContainer> atomContainerList = new ArrayList<IAtomContainer>();
        for (IAtomContainer atomContainer : set.atomContainers()) {
            atomContainerList.add(atomContainer);
        }
    	return atomContainerList;
    }
	
	/**
	 * @param set The collection of IAtomContainer objects
     * @return The summed charges of all atoms in this set.
	 */
    @TestMethod("testGetTotalCharge_IAtomContainerSet")
    public static double getTotalCharge(IAtomContainerSet set) {
		double charge = 0;
		for (int i = 0; i < set.getAtomContainerCount(); i++) {
			int thisCharge = AtomContainerManipulator.getTotalFormalCharge(set.getAtomContainer(i));
			double stoich = set.getMultiplier(i);
			charge += stoich * thisCharge;
		}
		return charge;
	}
	
	/**
	 * @param set The collection of IAtomContainer objects
     * @return The summed formal charges of all atoms in this set.
	 */
    @TestMethod("testGetTotalFormalCharge_IAtomContainerSet")
    public static double getTotalFormalCharge(IAtomContainerSet set) {
		int charge = 0;
		for (int i = 0; i < set.getAtomContainerCount(); i++) {
			int thisCharge = AtomContainerManipulator.getTotalFormalCharge(set.getAtomContainer(i));
			double stoich = set.getMultiplier(i);
			charge += stoich * thisCharge;
		}
		return charge;
	}
	
	/**
	 * @param set  The collection of IAtomContainer objects
     * @return The summed implicit hydrogens of all atoms in this set.
	 */
    @TestMethod("testGetTotalHydrogenCount_IAtomContainerSet")
    public static int getTotalHydrogenCount(IAtomContainerSet set) {
		int hCount = 0;
		for (int i = 0; i < set.getAtomContainerCount(); i++) {
			hCount += AtomContainerManipulator.getTotalHydrogenCount(set.getAtomContainer(i));
		}
		return hCount;
	}

    @TestMethod("testGetAllIDs_IAtomContainerSet")
    public static List<String> getAllIDs(IAtomContainerSet set) {
        List<String> idList = new ArrayList<String>();
        if (set != null) {
            if (set.getID() != null) idList.add(set.getID());
            for (int i = 0; i < set.getAtomContainerCount(); i++) {
                idList.addAll(AtomContainerManipulator.getAllIDs(set.getAtomContainer(i)));
            }
        }
        return idList;
    }

    @TestMethod("testSetAtomProperties_IAtomContainerSet_Object_Object")
    public static void setAtomProperties(IAtomContainerSet set, Object propKey, Object propVal) {
        if (set != null) {
            for (int i = 0; i < set.getAtomContainerCount(); i++) {
                AtomContainerManipulator.setAtomProperties(set.getAtomContainer(i), propKey, propVal);
            }
        }
    }

    @TestMethod("testGetRelevantAtomContainer_IAtomContainerSet_IAtom")
    public static IAtomContainer getRelevantAtomContainer(IAtomContainerSet containerSet, IAtom atom) {
        for (IAtomContainer atomContainer : containerSet.atomContainers()) {
            if (atomContainer.contains(atom)) {
                return atomContainer;
            }
        }
        return null;
    }

    @TestMethod("testGetRelevantAtomContainer_IAtomContainerSet_IBond")
    public static IAtomContainer getRelevantAtomContainer(IAtomContainerSet containerSet, IBond bond) {
        for (IAtomContainer atomContainer : containerSet.atomContainers()) {
            if (atomContainer.contains(bond)) {
                return atomContainer;
            }
        }
        return null;
    }
    
    /**
     * Does not recursively return the contents of the AtomContainer.
     * 
     * @param set The collection of IAtomContainer objects
     * @return a list of individual ChemObject's
     */
    @TestMethod("testGetAllChemObjects_IAtomContainerSet")
    public static List<IChemObject> getAllChemObjects(IAtomContainerSet set) {
        ArrayList<IChemObject> list = new ArrayList<IChemObject>();
        list.add(set);
        for (IAtomContainer atomContainer : set.atomContainers()) {
            list.add(atomContainer);
        }
        return list;
    }
        
    /**
     * <p>Sorts the IAtomContainers in the given IAtomContainerSet by the following
     * criteria with decreasing priority:</p>
     * <ul>
     *   <li>Compare atom count
     *   <li>Compare molecular weight (heavy atoms only)
     *   <li>Compare bond count
     *   <li>Compare sum of bond orders (heavy atoms only)
     * </ul>
     * <p>If no difference can be found with the above criteria, the IAtomContainers are
     * considered equal.</p>
     * @param atomContainerSet The collection of IAtomContainer objects
     */
    @TestMethod("testSort_IAtomContainerSet")
    public static void sort(IAtomContainerSet atomContainerSet) {
        List<IAtomContainer> atomContainerList = AtomContainerSetManipulator.getAllAtomContainers(atomContainerSet);
        Collections.sort(atomContainerList, new AtomContainerComparator());
        atomContainerSet.removeAllAtomContainers();
        for (Object anAtomContainerList : atomContainerList)
            atomContainerSet.addAtomContainer((IAtomContainer) anAtomContainerList);
    }
    
    /**
     * Tells if an AtomContainerSet contains at least one AtomContainer with the
     * same ID as atomContainer. Note this checks getID() for equality, not pointers.
     * 
     * @param relevantContainer The IAtomContainer to look for
     * @param atomContainerSet The collection of IAtomContainer objects
     */
    @TestMethod("testContainsByID_IAtomContainerSet_IAtomContainer")
	public static boolean containsByID(IAtomContainerSet atomContainerSet,
			String id) {
		for(IAtomContainer ac : atomContainerSet.atomContainers()){
			if(ac.getID()!=null && ac.getID().equals(id))
				return true;
		}
		return false;
	}
    
}

