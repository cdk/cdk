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
import java.util.Iterator;
import java.util.List;

/**
 * Class with convenience methods that provide methods from
 * methods from ChemObjects within the ChemFile.
 *
 * @see org.openscience.cdk.AtomContainer#removeAtomAndConnectedElectronContainers(IAtom)
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.tools.manipulator.ChemFileManipulatorTest")
public class ChemFileManipulator {

	/**
	 * Get the total number of atoms inside an IChemFile.
	 * 
	 * @param file       The IChemFile object.
	 * @return           The number of Atom object inside.
	 */

    @TestMethod("testGetAtomCount_IChemFile")
    public static int getAtomCount(IChemFile file) {
    	int count = 0;
        for (int i=0; i<file.getChemSequenceCount(); i++) {
        	count += ChemSequenceManipulator.getAtomCount(file.getChemSequence(i));
        }
        return count;
    }

    /**
	 * Get the total number of bonds inside an IChemFile.
	 * 
	 * @param file       The IChemFile object.
	 * @return           The number of Bond object inside.
	 */
    @TestMethod("testGetBondCount_IChemFile")
    public static int getBondCount(IChemFile file) {
    	int count = 0;
        for (int i=0; i<file.getChemSequenceCount(); i++) {
        	count += ChemSequenceManipulator.getBondCount(file.getChemSequence(i));
        }
        return count;
    }

    /**
     * Returns a List of all IChemObject inside a ChemFile.
     *
     * @return  A list of all ChemObjects
     */
    @TestMethod("testGetAllChemObjects_IChemFile")
    public static List<IChemObject> getAllChemObjects(IChemFile file) {
    	List<IChemObject> list = new ArrayList<IChemObject>();
    	//list.add(file); // should not add the original file
        for (int i=0; i<file.getChemSequenceCount(); i++) {
        	list.add(file.getChemSequence(i));
            list.addAll(ChemSequenceManipulator.getAllChemObjects(
                file.getChemSequence(i)
            ));
        }
        return list;
    }

    @TestMethod("testGetAllIDs_IChemFile")
    public static List<String> getAllIDs(IChemFile file) {
    	List<String> list = new ArrayList<String>();
    	if (file.getID() != null) list.add(file.getID());
        for (int i=0; i<file.getChemSequenceCount(); i++) {
            list.addAll(ChemSequenceManipulator.getAllIDs(
                file.getChemSequence(i)
            ));
        }
        return list;
    }

    /**
     * Returns all the AtomContainer's of a ChemFile.
     */
    @TestMethod("testGetAllAtomContainers_IChemFile")
    public static List<IAtomContainer> getAllAtomContainers(IChemFile file) {
        List<IAtomContainer> acList = new ArrayList<IAtomContainer>();
        for (IChemSequence sequence : file.chemSequences()) {
            acList.addAll(ChemSequenceManipulator.getAllAtomContainers(sequence));
        }
        return acList;
    }
    
    /**
     * Get a list of all ChemModels inside an IChemFile.
     * 
     * @param file  The IChemFile object.
     * @return      The List of IChemModel objects inside.
     */
    @TestMethod("testGetAllChemModels_IChemFile")
    public static List<IChemModel> getAllChemModels(IChemFile file) {
        List<IChemModel> modelsList = new ArrayList<IChemModel>();

	    for (int f = 0; f < file.getChemSequenceCount(); f++){
		    for (IChemModel model : file.getChemSequence(f).chemModels()) {
			    modelsList.add(model);
		    }
	    }
	    return modelsList;
    }
    
    /**
     * Get a list of all IReaction inside an IChemFile.
     * 
     * @param file  The IChemFile object.
     * @return      The List of IReaction objects inside.
     */
    @TestMethod("testGetAllReactions_IChemFile")
    public static List<IReaction> getAllReactions(IChemFile file) {
        List<IReaction> reactonList = new ArrayList<IReaction>();
        List<IChemModel> chemModel = getAllChemModels(file);
	    for (int f = 0; f < chemModel.size(); f++){		    
		    for (IReaction reaction : chemModel.get(f).getReactionSet().reactions()) {
		    	reactonList.add(reaction);
		    }
	    }
	    return reactonList;
    }
}

