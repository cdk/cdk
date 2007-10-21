/* $RCSfile$
 * $Author: kaihartmann $ 
 * $Date: 2006-09-20 20:57:51 +0200 (Wed, 20 Sep 2006) $
 * $Revision: 6997 $
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

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMoleculeSet;

/**
 * @cdk.module standard
 * @cdk.svnrev  $Revision: 9162 $
 *
 * @see ChemModelManipulator
 */
public class MoleculeSetManipulator {
    
	public static int getAtomCount(IAtomContainerSet set) {
		return AtomContainerSetManipulator.getAtomCount(set);
	}
	
	public static int getBondCount(IAtomContainerSet set) {
		return AtomContainerSetManipulator.getBondCount(set);
	}
	
    public static void removeAtomAndConnectedElectronContainers(IMoleculeSet set, IAtom atom) {
        AtomContainerSetManipulator.removeAtomAndConnectedElectronContainers(set, atom);
    }
    
    public static void removeElectronContainer(IMoleculeSet set, IElectronContainer electrons) {
        AtomContainerSetManipulator.removeElectronContainer(set, electrons);
    }
    
    /**
     * Returns all the AtomContainer's of a MoleculeSet.
     */
    public static List getAllAtomContainers(IMoleculeSet set) {
		return AtomContainerSetManipulator.getAllAtomContainers(set);
    }
    
	/**
	 * @see AtomContainerSetManipulator
	 */
	public static double getTotalCharge(IMoleculeSet set) {
		return AtomContainerSetManipulator.getTotalCharge(set);
	}
	
	/**
	 * @see AtomContainerSetManipulator
	 */
	public static double getTotalFormalCharge(IMoleculeSet set) {
		return AtomContainerSetManipulator.getTotalFormalCharge(set);
	}
	
	/**
	 * @see AtomContainerSetManipulator
	 */
	public static int getTotalHydrogenCount(IMoleculeSet set) {
		return AtomContainerSetManipulator.getTotalHydrogenCount(set);
	}
	
    public static List getAllIDs(IMoleculeSet set) {
    	List list = new ArrayList();
    	// the ID is set in AtomContainerSetManipulator.getAllIDs()
    	list.addAll(AtomContainerSetManipulator.getAllIDs(set));
		return list;
	}

    public static void setAtomProperties(IMoleculeSet set, Object propKey, Object propVal) {
        AtomContainerSetManipulator.setAtomProperties(set, propKey, propVal);
    }

    public static IAtomContainer getRelevantAtomContainer(IMoleculeSet moleculeSet, IAtom atom) {
        return AtomContainerSetManipulator.getRelevantAtomContainer(moleculeSet, atom);
    }

    public static IAtomContainer getRelevantAtomContainer(IMoleculeSet moleculeSet, IBond bond) {
        return AtomContainerSetManipulator.getRelevantAtomContainer(moleculeSet, bond);
    }

    public static List getAllChemObjects(IMoleculeSet set) {
        return AtomContainerSetManipulator.getAllChemObjects(set);
    }

}

