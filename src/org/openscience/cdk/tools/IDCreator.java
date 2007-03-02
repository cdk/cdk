/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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
 *
 */
package org.openscience.cdk.tools;

import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.ChemSequenceManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;

/**
 * Class that provides methods to give unique IDs to ChemObjects.
 * Methods are implemented for Atom, Bond, AtomContainer, AtomContainerSet
 * and Reaction. It will only create missing IDs. If you want to create new
 * IDs for all ChemObjects, you need to delete them first.
 *
 * @cdk.module standard
 *
 * @author   Egon Willighagen
 * @cdk.created  2003-04-01
 *
 * @cdk.keyword  id, creation
 * @cdk.bug      1455341
 */
public abstract class IDCreator {

	/**
	 * Labels the Atom's and Bond's in the AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML. Supports IAtomContainer, IAtomContainerSet,
     * IChemFile, IChemModel, IChemSequence, IReaction, IReactionSet,
     * and derived interfaces.
     * 
	 * @param  chemObject IChemObject to create IDs for.
	 */
	public static void createIDs(IChemObject chemObject) {
		if (chemObject == null) return;
		
		if (chemObject instanceof IAtomContainer) {
			createIDsForAtomContainer((IAtomContainer)chemObject, null);
		} else if (chemObject instanceof IAtomContainerSet) {
			createIDsForAtomContainerSet((IAtomContainerSet)chemObject, null);
		} else if (chemObject instanceof IReaction) {
			createIDsForReaction((IReaction)chemObject, null);
		} else if (chemObject instanceof IReactionSet) {
			createIDsForReactionSet((IReactionSet)chemObject, null);
		} else if (chemObject instanceof IChemFile) {
			createIDsForChemFile((IChemFile)chemObject, null);
		} else if (chemObject instanceof IChemSequence) {
			createIDsForChemSequence((IChemSequence)chemObject, null);
		} else if (chemObject instanceof IChemModel) {
			createIDsForChemModel((IChemModel)chemObject, null);
		}
	}

	/**
	 * Sets the ID on the object and adds it to the tabu list.
	 * 
	 * @param object   IChemObject to set the ID for
	 * @param tabuList Tabu list to add the ID to
	 */
    private static void setID(String identifier, IChemObject object, List tabuList) {
		object.setID(identifier);
		tabuList.add(identifier);
	}

    /**
     * Labels the Atom's and Bond's in the AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML.
     *
     * @see #createIDs(IAtomContainerSet)
     */
    private static void createIDsForAtomContainer(IAtomContainer container, List tabuList) {
    	if (tabuList == null) tabuList = AtomContainerManipulator.getAllIDs(container);
    	
    	int moleculeCount = 1;
    	int atomCount = 1;
    	int bondCount = 1;
    	
        if (container.getID() == null) {
            while (tabuList.contains("m" + moleculeCount)) moleculeCount++;
            setID("m" + moleculeCount, container, tabuList);
        }
        
        Iterator atoms = container.atoms();
        while(atoms.hasNext()) {
        	IAtom atom = (IAtom)atoms.next();
            if (atom.getID() == null) {
                while (tabuList.contains("a" + atomCount)) atomCount++;
                setID("a" + atomCount, atom, tabuList);
            }
        }

        Iterator bonds = container.bonds();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
            if (bond.getID() == null) {
                while (tabuList.contains("b" + bondCount)) bondCount++;
                setID("b" + bondCount, bond, tabuList);
            }
        }
    }

	/**
     * Labels the Atom's and Bond's in each AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML. It will also set id's for all AtomContainers, naming
     * them m1, m2, etc.
     * It will not the AtomContainerSet itself.
     */
    private static void createIDsForAtomContainerSet(IAtomContainerSet containerSet, List tabuList) {
        if (tabuList == null) tabuList = AtomContainerSetManipulator.getAllIDs(containerSet);

        int moleculeCount = 1;
        
        if (containerSet.getID() == null) {
            while (tabuList.contains("molSet" + moleculeCount)) moleculeCount++;
            setID("molSet" + moleculeCount, containerSet, tabuList);
        }

        Iterator acs = containerSet.atomContainers();
        while (acs.hasNext()) {
        	IAtomContainer container = (IAtomContainer)acs.next();
            if (container.getID() == null) {
                createIDsForAtomContainer(container, tabuList);
            }
        }
    }
    
    /**
     * Labels the reactants and products in the Reaction m1, m2, etc, and the atoms
     * accordingly, when no ID is given.
     */
    private static void createIDsForReaction(IReaction reaction, List tabuList) {
        if (tabuList == null) tabuList = ReactionManipulator.getAllIDs(reaction);
        
        int reactionCount = 1;
        
        if (reaction.getID() == null) {
            while (tabuList.contains("r" + reactionCount)) reactionCount++;
            setID("r" + reactionCount, reaction, tabuList);
        }

        Iterator reactants = reaction.getReactants().atomContainers();
        while (reactants.hasNext()) {
            createIDsForAtomContainer((IAtomContainer)reactants.next(), tabuList);
        }
        Iterator products = reaction.getProducts().atomContainers();
        while (products.hasNext()) {
            createIDsForAtomContainer((IAtomContainer)products.next(), tabuList);
        }
    }
    
    private static void createIDsForReactionSet(IReactionSet reactionSet, List tabuList) {
    	if (tabuList == null) tabuList = ReactionSetManipulator.getAllIDs(reactionSet);
        
        for (Iterator iter = reactionSet.reactions(); iter.hasNext();) {
            createIDsForReaction((IReaction)iter.next(), tabuList);
        }
    }
    
    private static void createIDsForChemFile(IChemFile file, List tabuList) {
    	if (tabuList == null) tabuList = ChemFileManipulator.getAllIDs(file);
        
    	Iterator sequences = file.chemSequences();
    	while (sequences.hasNext()) {
    		createIDsForChemSequence((IChemSequence)sequences.next(), tabuList);
    	}
    }
    
    private static void createIDsForChemSequence(IChemSequence sequence, List tabuList) {
    	if (tabuList == null) tabuList = ChemSequenceManipulator.getAllIDs(sequence);
        
    	Iterator models = sequence.chemModels();
    	while (models.hasNext()) {
    		createIDsForChemModel((IChemModel)models.next(), tabuList);
    	}
    }
    
    private static void createIDsForChemModel(IChemModel model, List tabuList) {
    	if (tabuList == null) tabuList = ChemModelManipulator.getAllIDs(model);
        
    	ICrystal crystal = model.getCrystal();
    	if (crystal != null) createIDsForAtomContainer(crystal, tabuList);
    	IMoleculeSet moleculeSet = model.getMoleculeSet();
    	if (moleculeSet != null) createIDsForAtomContainerSet(moleculeSet, tabuList);
    	IReactionSet reactionSet = model.getReactionSet();
    	if (reactionSet != null) createIDsForReactionSet(reactionSet, tabuList);
    }
    
}
