/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) Project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.tools;

import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfAtomContainers;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

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
     * @see #createAtomContainerAndAtomAndBondIDs(SetOfAtomContainers)
     */
    public void createIDs(AtomContainer container) {
        if (tabuList == null) tabuList = AtomContainerManipulator.getAllIDs(container);
        
        if (container.getID() == null) {
            moleculeCount++;
            while (tabuList.contains("m" + moleculeCount)) moleculeCount++;
            container.setID("m" + moleculeCount);
        }
        
        Atom[] atoms = container.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            Atom atom = atoms[i];
            if (atom.getID() == null) {
                atomCount++;
                while (tabuList.contains("a" + atomCount)) atomCount++;
                atoms[i].setID("a" + atomCount);
            }
        }
        Bond[] bonds = container.getBonds();
        for (int i=0; i<bonds.length; i++) {
            Bond bond = bonds[i];
            if (bond.getID() == null) {
                bondCount++;
                while (tabuList.contains("b" + bondCount)) bondCount++;
                bonds[i].setID("b" + bondCount);
            }
        }
    }

    /**
     * Labels the Atom's and Bond's in each AtomContainer using the a1, a2, b1, b2
     * scheme often used in CML. It will also set id's for all AtomContainers, naming
     * them m1, m2, etc.
     * It will not the SetOfAtomContainers itself.
     */
    public void createIDs(SetOfAtomContainers containerSet) {
        if (containerSet.getID() == null) {
            moleculeCount++;
            while (tabuList.contains("molSet" + moleculeCount)) moleculeCount++;
            containerSet.setID("molSet" + moleculeCount);
        }

        AtomContainer[] containers = containerSet.getAtomContainers();
        for (int i=0; i<containers.length; i++) {
            AtomContainer container = containers[i];
            if (container.getID() == null) {
                createIDs(container);
            }
        }
    }
    
    /**
     * Labels the reactants and products in the Reaction m1, m2, etc, and the atoms
     * accordingly, when no ID is given.
     */
    public void createIDs(Reaction reaction) {
        if (tabuList == null) tabuList = ReactionManipulator.getAllIDs(reaction);
        
        if (reaction.getID() == null) {
            reactionCount++;
            while (tabuList.contains("r" + reactionCount)) reactionCount++;
            reaction.setID("r" + reactionCount);
        }

        AtomContainer[] reactants = reaction.getReactants().getAtomContainers();
        for (int i=0; i<reactants.length; i++) {
            createIDs(reactants[i]);
        }
        AtomContainer[] products = reaction.getProducts().getAtomContainers();
        for (int i=0; i<products.length; i++) {
            createIDs(products[i]);
        }
    }
    
    public void createIDs(SetOfReactions reactionSet) {
        Reaction[] reactions = reactionSet.getReactions();
        for (int i=0; i<reactions.length; i++) {
            createIDs(reactions[i]);
        }
    }
    
}
