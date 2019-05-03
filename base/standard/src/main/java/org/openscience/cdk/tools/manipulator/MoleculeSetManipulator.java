/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElectronContainer;

/**
 * @cdk.module standard
 * @cdk.githash
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

    public static void removeAtomAndConnectedElectronContainers(IAtomContainerSet set, IAtom atom) {
        AtomContainerSetManipulator.removeAtomAndConnectedElectronContainers(set, atom);
    }

    public static void removeElectronContainer(IAtomContainerSet set, IElectronContainer electrons) {
        AtomContainerSetManipulator.removeElectronContainer(set, electrons);
    }

    /**
     * Returns all the AtomContainer's of a MoleculeSet.
     * @param set The collection of IAtomContainer objects
     * @return a list containing individual IAtomContainer's
     */
    public static List<IAtomContainer> getAllAtomContainers(IAtomContainerSet set) {
        return AtomContainerSetManipulator.getAllAtomContainers(set);
    }

    /**
     * @param set The collection of IAtomContainer objects
     * @see AtomContainerSetManipulator
     * @return The total charge on the collection of molecules
     */
    public static double getTotalCharge(IAtomContainerSet set) {
        return AtomContainerSetManipulator.getTotalCharge(set);
    }

    /**
     * @param set The collection of IAtomContainer objects
     * @see AtomContainerSetManipulator
     * @return The total formal charge on the collection of molecules
     */
    public static double getTotalFormalCharge(IAtomContainerSet set) {
        return AtomContainerSetManipulator.getTotalFormalCharge(set);
    }

    /**
     * @param set The collection of IAtomContainer objects
     * @see AtomContainerSetManipulator
     * @return the total implicit hydrogen count on the collection of molecules
     */
    public static int getTotalHydrogenCount(IAtomContainerSet set) {
        return AtomContainerSetManipulator.getTotalHydrogenCount(set);
    }

    public static List<String> getAllIDs(IAtomContainerSet set) {
        List<String> list = new ArrayList<String>();
        // the ID is set in AtomContainerSetManipulator.getAllIDs()
        list.addAll(AtomContainerSetManipulator.getAllIDs(set));
        return list;
    }

    public static void setAtomProperties(IAtomContainerSet set, Object propKey, Object propVal) {
        AtomContainerSetManipulator.setAtomProperties(set, propKey, propVal);
    }

    public static IAtomContainer getRelevantAtomContainer(IAtomContainerSet moleculeSet, IAtom atom) {
        return AtomContainerSetManipulator.getRelevantAtomContainer(moleculeSet, atom);
    }

    public static IAtomContainer getRelevantAtomContainer(IAtomContainerSet moleculeSet, IBond bond) {
        return AtomContainerSetManipulator.getRelevantAtomContainer(moleculeSet, bond);
    }

    public static List<IChemObject> getAllChemObjects(IAtomContainerSet set) {
        return AtomContainerSetManipulator.getAllChemObjects(set);
    }

}
