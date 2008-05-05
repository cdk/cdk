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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @see ChemModelManipulator
 */
@TestClass("org.openscience.cdk.tools.manipulator.ReactionSetManipulatorTest")
public class ReactionSetManipulator {

    @TestMethod("testGetAtomCount_IReactionSet")
    public static int getAtomCount(IReactionSet set) {
    	int count = 0;
        for (Iterator<IReaction> iter = set.reactions(); iter.hasNext();) {
        	count += ReactionManipulator.getAtomCount(iter.next());
        }
        return count;
    }

    @TestMethod("testGetBondCount_IReactionSet")
    public static int getBondCount(IReactionSet set) {
    	int count = 0;
    	for (Iterator<IReaction> iter = set.reactions(); iter.hasNext();) {
        	count += ReactionManipulator.getBondCount(iter.next());
        }
        return count;
    }

    @TestMethod("testRemoveAtomAndConnectedElectronContainers_IReactionSet_IAtom")
    public static void removeAtomAndConnectedElectronContainers(IReactionSet set, IAtom atom) {
    	for (Iterator<IReaction> iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = iter.next();
            ReactionManipulator.removeAtomAndConnectedElectronContainers(reaction, atom);
        }
    }

    @TestMethod("testRemoveElectronContainer_IReactionSet_IElectronContainer")
    public static void removeElectronContainer(IReactionSet set, IElectronContainer electrons) {
    	for (Iterator<IReaction> iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = iter.next();
            ReactionManipulator.removeElectronContainer(reaction, electrons);
        }
    }
    
    /**
     * get all Molecules object from a set of Reactions. 
     * 
     * @param set The set of reaction to inspect
     * @return    The IMoleculeSet
     */
    @TestMethod("testGetAllMolecules_IReactionSet")
    public static IMoleculeSet getAllMolecules(IReactionSet set) {
    	IMoleculeSet moleculeSet = set.getBuilder().newMoleculeSet();
        for (Iterator<IReaction> iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = iter.next();
            IMoleculeSet molecules = ReactionManipulator.getAllMolecules(reaction);
            for(Iterator<IAtomContainer> it = molecules.molecules(); it.hasNext(); ){
	        	IAtomContainer ac = it.next();
	        	boolean contain = false;
	        	for(Iterator<IAtomContainer> it2 = moleculeSet.molecules();it2.hasNext();){
	         		if(it2.next().equals(ac)){
	             		contain = true;
	             		break;
	             	}
	         	}
	         	if(!contain)
	         		moleculeSet.addMolecule((IMolecule)(ac));
	         	
	        }
        }
        return moleculeSet;
    }

    @TestMethod("testGetAllIDs_IReactionSet")
	public static List<String> getAllIDs(IReactionSet set) {
        List<String> IDlist = new ArrayList<String>();
        if (set.getID() != null) IDlist.add(set.getID());
        for (Iterator<IReaction> iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = iter.next();
            IDlist.addAll(ReactionManipulator.getAllIDs(reaction));
        }
        return IDlist;
    }
    
    /**
     * Returns all the AtomContainer's of a Reaction.
     * @param set  the reaction set to get the molecules from
     * @return  a List containing the IAtomContainer objects in the IReactionSet
     */
    @TestMethod("testGetAllAtomContainers_IReactionSet")
    public static List<IAtomContainer> getAllAtomContainers(IReactionSet set) {
    	
		return MoleculeSetManipulator.getAllAtomContainers(
            getAllMolecules(set)
        );
    }

    @TestMethod("testGetRelevantReaction_IReactionSet_IAtom")
    public static IReaction getRelevantReaction(IReactionSet set, IAtom atom) {
    	for (Iterator<IReaction> iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = iter.next();
            IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, atom);
            if (container != null) { // a match!
                return reaction;
            }
        }
        return null;
    }

    @TestMethod("testGetRelevantReaction_IReactionSet_IBond")
    public static IReaction getRelevantReaction(IReactionSet set, IBond bond) {
    	for (Iterator<IReaction> iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = iter.next();
            IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, bond);
            if (container != null) { // a match!
                return reaction;
            }
        }
        return null;
    }

    @TestMethod("testGetRelevantAtomContainer_IReactionSet_IAtom")
    public static IAtomContainer getRelevantAtomContainer(IReactionSet set, IAtom atom) {
    	for (Iterator<IReaction> iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, atom);
            if (container != null) { // a match!
                return container;
            }
        }
        return null;
    }

    @TestMethod("testGetRelevantAtomContainer_IReactionSet_IBond")
    public static IAtomContainer getRelevantAtomContainer(IReactionSet set, IBond bond) {
    	for (Iterator<IReaction> iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, bond);
            if (container != null) { // a match!
                return container;
            }
        }
        return null;
    }

    @TestMethod("testSetAtomProperties_IReactionSet_Object_Object")
    public static void setAtomProperties(IReactionSet set, Object propKey, Object propVal) {
    	for (Iterator<IReaction> iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            ReactionManipulator.setAtomProperties(reaction, propKey, propVal);
        }
    }

    @TestMethod("testGetAllChemObjects_IReactionSet")
    public static List<IChemObject> getAllChemObjects(IReactionSet set) {
        ArrayList<IChemObject> list = new ArrayList<IChemObject>();
        list.add(set);
        for (Iterator<IReaction> iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = iter.next();
            list.addAll(ReactionManipulator.getAllChemObjects(reaction));
        }
        return list;
    }
    
}
