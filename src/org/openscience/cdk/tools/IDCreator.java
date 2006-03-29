/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) Project
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

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISetOfAtomContainers;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import org.openscience.cdk.tools.manipulator.SetOfAtomContainersManipulator;

/**
 * Class that provides methods to give unique IDs to ChemObjects.
 * Methods are implemented for Atom, Bond, AtomContainer, SetOfAtomContainers
 * and Reaction. It will only create missing IDs. If you want to create new
 * IDs for all ChemObjects, you need to delete them first.
 *
 * @cdk.module standard
 *
 * @author   Egon Willighagen
 * @cdk.created  2003-04-01
 *
 * @cdk.keyword  id, creation
 */
public class IDCreator {

    /**
     * A list of taken IDs.
     */
    private Vector tabuList;
    
    /**
     * Keep track of numbers.
     */
    int atomCount;
    int bondCount;
    int moleculeCount;
    int reactionCount;
    
    public IDCreator() {
        reset();
    }
    
    public void reset() {
        tabuList = null;
        atomCount = 0;
        bondCount = 0;
        moleculeCount = 0;
        reactionCount = 0;
    }
    
    /**
     * Labels the Atom's and Bond's in the AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML.
     *
     * @see #createIDs(ISetOfAtomContainers)
     */
    public void createIDs(IAtomContainer container) {
        if (tabuList == null) tabuList = AtomContainerManipulator.getAllIDs(container);
        
        if (container.getID() == null) {
            moleculeCount++;
            while (tabuList.contains("m" + moleculeCount)) moleculeCount++;
            container.setID("m" + moleculeCount);
        }
        
        IAtom[] atoms = container.getAtoms();
        for (int i=0; i<atoms.length; i++) {
        	IAtom atom = atoms[i];
            if (atom.getID() == null) {
                atomCount++;
                while (tabuList.contains("a" + atomCount)) atomCount++;
                atoms[i].setID("a" + atomCount);
            }
        }
        IBond[] bonds = container.getBonds();
        for (int i=0; i<bonds.length; i++) {
        	IBond bond = bonds[i];
            if (bond.getID() == null) {
                bondCount++;
                while (tabuList.contains("b" + bondCount)) bondCount++;
                bonds[i].setID("b" + bondCount);
            }
        }
    }

    public void createIDs(ISetOfMolecules containerSet) {
    	createIDs((ISetOfAtomContainers)containerSet);
    }    
    
    /**
     * Labels the Atom's and Bond's in each AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML. It will also set id's for all AtomContainers, naming
     * them m1, m2, etc.
     * It will not the SetOfAtomContainers itself.
     */
    public void createIDs(ISetOfAtomContainers containerSet) {
        if (tabuList == null) tabuList = SetOfAtomContainersManipulator.getAllIDs(containerSet);

        if (containerSet.getID() == null) {
            moleculeCount++;
            while (tabuList.contains("molSet" + moleculeCount)) moleculeCount++;
            containerSet.setID("molSet" + moleculeCount);
        }

        IAtomContainer[] containers = containerSet.getAtomContainers();
        for (int i=0; i<containers.length; i++) {
        	IAtomContainer container = containers[i];
            if (container.getID() == null) {
                createIDs(container);
            }
        }
    }
    
    /**
     * Labels the reactants and products in the Reaction m1, m2, etc, and the atoms
     * accordingly, when no ID is given.
     */
    public void createIDs(IReaction reaction) {
        if (tabuList == null) tabuList = ReactionManipulator.getAllIDs(reaction);
        
        if (reaction.getID() == null) {
            reactionCount++;
            while (tabuList.contains("r" + reactionCount)) reactionCount++;
            reaction.setID("r" + reactionCount);
        }

        IAtomContainer[] reactants = reaction.getReactants().getAtomContainers();
        for (int i=0; i<reactants.length; i++) {
            createIDs(reactants[i]);
        }
        IAtomContainer[] products = reaction.getProducts().getAtomContainers();
        for (int i=0; i<products.length; i++) {
            createIDs(products[i]);
        }
    }
    
    public void createIDs(ISetOfReactions reactionSet) {
    	IReaction[] reactions = reactionSet.getReactions();
        for (int i=0; i<reactions.length; i++) {
            createIDs(reactions[i]);
        }
    }
    
    public void createIDs(IChemFile file) {
    	IChemSequence[] sequences = file.getChemSequences();
    	for (int i=0; i<sequences.length; i++) {
    		createIDs(sequences[i]);
    	}
    }
    
    public void createIDs(IChemSequence sequence) {
    	IChemModel[] models = sequence.getChemModels();
    	for (int i=0; i<models.length; i++) {
    		createIDs(models[i]);
    	}
    }
    
    public void createIDs(IChemModel model) {
    	ICrystal crystal = model.getCrystal();
    	if (crystal != null) createIDs(crystal);
    	ISetOfMolecules moleculeSet = model.getSetOfMolecules();
    	if (moleculeSet != null) createIDs(moleculeSet);
    	ISetOfReactions reactionSet = model.getSetOfReactions();
    	if (reactionSet != null) createIDs(reactionSet);
    }
    
    public void createIDs(ICrystal crystal) {
    	createIDs((IAtomContainer)crystal);
    }
}
