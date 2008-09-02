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
import org.openscience.cdk.interfaces.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class with convenience methods that provide methods from
 * methods from ChemObjects within the ChemSequence.
 *
 * @see org.openscience.cdk.AtomContainer#removeAtomAndConnectedElectronContainers(IAtom)
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.tools.manipulator.ChemSequenceManipulatorTest")
public class ChemSequenceManipulator {

	/**
	 * Get the total number of atoms inside an IChemSequence.
	 * 
	 * @param sequence   The IChemSequence object.
	 * @return           The number of Atom objects inside.
	 */
    @TestMethod("testGetAtomCount_IChemSequence")
    public static int getAtomCount(IChemSequence sequence) {
    	int count = 0;
        for (int i=0; i<sequence.getChemModelCount(); i++) {
        	count += ChemModelManipulator.getAtomCount(sequence.getChemModel(i));
        }
        return count;
    }

    /**
	 * Get the total number of bonds inside an IChemSequence.
	 * 
	 * @param sequence   The IChemSequence object.
	 * @return           The number of Bond objects inside.
	 */
    @TestMethod("testGetBondCount_IChemSequence")
    public static int getBondCount(IChemSequence sequence) {
    	int count = 0;
        for (int i=0; i<sequence.getChemModelCount(); i++) {
        	count += ChemModelManipulator.getBondCount(sequence.getChemModel(i));
        }
        return count;
    }

    /**
     * Returns all the AtomContainer's of a ChemSequence.
     */
    @TestMethod("testGetAllAtomContainers_IChemSequence")
    public static List<IAtomContainer> getAllAtomContainers(IChemSequence sequence) {
        List<IAtomContainer> acList = new ArrayList<IAtomContainer>();
        for (IChemModel model : sequence.chemModels()) {
            acList.addAll(ChemModelManipulator.getAllAtomContainers(model));
        }
        return acList;
    }

    /**
     * Returns a List of all IChemObject inside a ChemSequence.
     *
     * @return  A List of all ChemObjects.
     */
    @TestMethod("testGetAllChemObjects_IChemSequence")
    public static List<IChemObject> getAllChemObjects(IChemSequence sequence) {
		List<IChemObject> list = new ArrayList<IChemObject>();
        // list.add(sequence);
        for (int i=0; i<sequence.getChemModelCount(); i++) {
        	list.add(sequence.getChemModel(i));
        	List<IChemObject> current = ChemModelManipulator.getAllChemObjects(sequence.getChemModel(i));
            for (IChemObject chemObject : current) {
                if (!list.contains(chemObject)) list.add(chemObject);
            }
            
        }
		return list;
	}

    @TestMethod("testGetAllIDs_IChemSequence")
    public static List<String> getAllIDs(IChemSequence sequence) {
		ArrayList<String> list = new ArrayList<String>();
		if (sequence.getID() != null) list.add(sequence.getID());
        for (int i=0; i<sequence.getChemModelCount(); i++) {
        	list.addAll(ChemModelManipulator.getAllIDs(sequence.getChemModel(i)));
        }
		return list;
	}
}

