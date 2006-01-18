/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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

import java.util.List;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ElectronContainer;
import org.openscience.cdk.interfaces.SetOfMolecules;

/**
 * @cdk.module standard
 *
 * @see ChemModelManipulator
 */
public class SetOfMoleculesManipulator {
    
    public static void removeAtomAndConnectedElectronContainers(SetOfMolecules set, IAtom atom) {
        SetOfAtomContainersManipulator.removeAtomAndConnectedElectronContainers(set, atom);
    }
    
    public static void removeElectronContainer(SetOfMolecules set, ElectronContainer electrons) {
        SetOfAtomContainersManipulator.removeElectronContainer(set, electrons);
    }
    
    /**
     * Puts all the Molecules of this container together in one 
     * AtomCcntainer.
     *
     * @return  The AtomContainer with all the Molecules of this container
     */
    public static IAtomContainer getAllInOneContainer(SetOfMolecules set) {
        return SetOfAtomContainersManipulator.getAllInOneContainer(set);
    }
    
    /**
     * Returns all the AtomContainer's of a SetOfMolecules.
     */
    public static IAtomContainer[] getAllAtomContainers(SetOfMolecules set) {
		return SetOfAtomContainersManipulator.getAllAtomContainers(set);
    }
    
	/**
	 * @see SetOfAtomContainersManipulator
	 */
	public static double getTotalCharge(SetOfMolecules set) {
		return SetOfAtomContainersManipulator.getTotalCharge(set);
	}
	
	/**
	 * @see SetOfAtomContainersManipulator
	 */
	public static double getTotalFormalCharge(SetOfMolecules set) {
		return SetOfAtomContainersManipulator.getTotalFormalCharge(set);
	}
	
	/**
	 * @see SetOfAtomContainersManipulator
	 */
	public static int getTotalHydrogenCount(SetOfMolecules set) {
		return SetOfAtomContainersManipulator.getTotalHydrogenCount(set);
	}
	
    public static Vector getAllIDs(SetOfMolecules set) {
		return SetOfAtomContainersManipulator.getAllIDs(set);
	}

    public static void setAtomProperties(SetOfMolecules set, Object propKey, Object propVal) {
        SetOfAtomContainersManipulator.setAtomProperties(set, propKey, propVal);
    }

    public static IAtomContainer getRelevantAtomContainer(SetOfMolecules moleculeSet, IAtom atom) {
        return SetOfAtomContainersManipulator.getRelevantAtomContainer(moleculeSet, atom);
    }

    public static IAtomContainer getRelevantAtomContainer(SetOfMolecules moleculeSet, Bond bond) {
        return SetOfAtomContainersManipulator.getRelevantAtomContainer(moleculeSet, bond);
    }

    public static List getAllChemObjects(SetOfMolecules set) {
        return SetOfAtomContainersManipulator.getAllChemObjects(set);
    }

}

