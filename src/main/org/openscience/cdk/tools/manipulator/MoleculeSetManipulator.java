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
 */
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @see ChemModelManipulator
 */
@TestClass("org.openscience.cdk.tools.manipulator.MoleculeSetManipulatorTest")
public class MoleculeSetManipulator {

    @TestMethod("testGetAtomCount_IAtomContainerSet")
    public static int getAtomCount(IAtomContainerSet set) {
		return AtomContainerSetManipulator.getAtomCount(set);
	}

    @TestMethod("testGetBondCount_IAtomContainerSet")
    public static int getBondCount(IAtomContainerSet set) {
		return AtomContainerSetManipulator.getBondCount(set);
	}

    @TestMethod("testRemoveAtomAndConnectedElectronContainers_IMoleculeSet_IAtom")
    public static void removeAtomAndConnectedElectronContainers(IMoleculeSet set, IAtom atom) {
        AtomContainerSetManipulator.removeAtomAndConnectedElectronContainers(set, atom);
    }

    @TestMethod("testRemoveElectronContainer_IMoleculeSet_IElectronContainer")
    public static void removeElectronContainer(IMoleculeSet set, IElectronContainer electrons) {
        AtomContainerSetManipulator.removeElectronContainer(set, electrons);
    }
    
    /**
     * Returns all the AtomContainer's of a MoleculeSet.
     * @param set The collection of IMolecule objects
     * @return a list containing individual IAtomContainer's
     */
    @TestMethod("testGetAllAtomContainers_IMoleculeSet")
    public static List<IAtomContainer> getAllAtomContainers(IMoleculeSet set) {
		return AtomContainerSetManipulator.getAllAtomContainers(set);
    }
    
	/**
	 * @param set The collection of IMolecule objects
     * @see AtomContainerSetManipulator
     * @return The total charge on the collection of molecules
	 */
    @TestMethod("testGetTotalCharge_IMoleculeSet")
    public static double getTotalCharge(IMoleculeSet set) {
		return AtomContainerSetManipulator.getTotalCharge(set);
	}
	
	/**
	 * @param set The collection of IMolecule objects
     * @see AtomContainerSetManipulator
     * @return The total formal charge on the collection of molecules
	 */
    @TestMethod("testGetTotalFormalCharge_IMoleculeSet")
    public static double getTotalFormalCharge(IMoleculeSet set) {
		return AtomContainerSetManipulator.getTotalFormalCharge(set);
	}
	
	/**
	 * @param set The collection of IMolecule objects
     * @see AtomContainerSetManipulator
     * @return the total implicit hydrogen count on the collection of molecules
	 */
    @TestMethod("testGetTotalHydrogenCount_IMoleculeSet")
    public static int getTotalHydrogenCount(IMoleculeSet set) {
		return AtomContainerSetManipulator.getTotalHydrogenCount(set);
	}

    @TestMethod("testGetAllIDs_IMoleculeSet")
    public static List<String> getAllIDs(IMoleculeSet set) {
    	List<String> list = new ArrayList<String>();
    	// the ID is set in AtomContainerSetManipulator.getAllIDs()
    	list.addAll(AtomContainerSetManipulator.getAllIDs(set));
		return list;
	}

    @TestMethod("testSetAtomProperties_IMoleculeSet_Object_Object")
    public static void setAtomProperties(IMoleculeSet set, Object propKey, Object propVal) {
        AtomContainerSetManipulator.setAtomProperties(set, propKey, propVal);
    }

    @TestMethod("testGetRelevantAtomContainer_IMoleculeSet_IAtom")
    public static IAtomContainer getRelevantAtomContainer(IMoleculeSet moleculeSet, IAtom atom) {
        return AtomContainerSetManipulator.getRelevantAtomContainer(moleculeSet, atom);
    }

    @TestMethod("testGetRelevantAtomContainer_IMoleculeSet_IBond")
    public static IAtomContainer getRelevantAtomContainer(IMoleculeSet moleculeSet, IBond bond) {
        return AtomContainerSetManipulator.getRelevantAtomContainer(moleculeSet, bond);
    }

    @TestMethod("testGetAllChemObjects_IMoleculeSet")
    public static List<IChemObject> getAllChemObjects(IMoleculeSet set) {
        return AtomContainerSetManipulator.getAllChemObjects(set);
    }

}

