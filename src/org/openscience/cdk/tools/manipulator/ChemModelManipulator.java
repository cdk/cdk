/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.SetOfReactions;

/**
 * Class with convenience methods that provide methods from
 * methods from ChemObjects within the ChemModel. For example:
 * <pre>
 * ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, atom);
 * </pre>
 * will find the Atom in the model by traversing the ChemModel's
 * SetOfMolecules, Crystal and SetOfReactions fields and remove
 * it with the removeAtomAndConnectedElectronContainers(Atom) method.
 *
 * @cdk.module standard
 *
 * @see org.openscience.cdk.AtomContainer#removeAtomAndConnectedElectronContainers(Atom)
 */
public class ChemModelManipulator {
    
    public static void removeAtomAndConnectedElectronContainers(ChemModel chemModel, Atom atom) {
        Crystal crystal = chemModel.getCrystal();
        if (crystal != null) {
            if (crystal.contains(atom)) {
                crystal.removeAtomAndConnectedElectronContainers(atom);
            }
            return;
        }
        SetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
        if (moleculeSet != null) {
            SetOfMoleculesManipulator.removeAtomAndConnectedElectronContainers(moleculeSet, atom);
        }
        SetOfReactions reactionSet = chemModel.getSetOfReactions();
        if (reactionSet != null) {
            SetOfReactionsManipulator.removeAtomAndConnectedElectronContainers(reactionSet, atom);
        }
    }
    
    public static void removeElectronContainer(ChemModel chemModel, ElectronContainer electrons) {
        Crystal crystal = chemModel.getCrystal();
        if (crystal != null) {
            if (crystal.contains(electrons)) {
                crystal.removeElectronContainer(electrons);
            }
            return;
        }
        SetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
        if (moleculeSet != null) {
            SetOfMoleculesManipulator.removeElectronContainer(moleculeSet, electrons);
        }
        SetOfReactions reactionSet = chemModel.getSetOfReactions();
        if (reactionSet != null) {
            SetOfReactionsManipulator.removeElectronContainer(reactionSet, electrons);
        }
    }
    
    /**
     * Puts all the Molecules of this container together in one 
     * AtomCcntainer.
     *
     * @return  The AtomContainer with all the Molecules of this container
     */
    public static AtomContainer getAllInOneContainer(ChemModel chemModel) {
        AtomContainer container = new AtomContainer();
        Crystal crystal = chemModel.getCrystal();
        if (crystal != null) {
            container.add(crystal);
        }
        SetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
        if (moleculeSet != null) {
            container.add(SetOfMoleculesManipulator.getAllInOneContainer(moleculeSet));
        }
        SetOfReactions reactionSet = chemModel.getSetOfReactions();
        if (reactionSet != null) {
            container.add(SetOfReactionsManipulator.getAllInOneContainer(reactionSet));
        }
        return container;
    }
    
    /**
     * This badly named methods tries to determine which AtomContainer in the
     * ChemModel is best suited to contain added Atom's and Bond's.
     */
    public static AtomContainer createNewMolecule(ChemModel chemModel) {
        // Add a new molecule either the set of molecules
        Molecule molecule = new Molecule();
        if (chemModel.getSetOfMolecules() != null) {
            SetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
            moleculeSet.addMolecule(molecule);
        } else {
            SetOfMolecules moleculeSet = new SetOfMolecules();
            moleculeSet.addMolecule(molecule);
            chemModel.setSetOfMolecules(moleculeSet);
        }
        return molecule;
    }

    /**
     * This badly named methods tries to determine which AtomContainer in the
     * ChemModel is best suited to contain added Atom's and Bond's.
     */
    public static AtomContainer getRelevantAtomContainer(ChemModel chemModel, Atom atom) {
        if (chemModel.getSetOfMolecules() != null) {
            SetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
            Molecule[] molecules = moleculeSet.getMolecules();
            for (int i=0; i<molecules.length; i++) {
                if (molecules[i].contains(atom)) {
                    return molecules[i];
                }
            }
        }
        if (chemModel.getSetOfReactions() != null) {
            SetOfReactions reactionSet = chemModel.getSetOfReactions();
            Reaction[] reactions = reactionSet.getReactions();
            for (int i=0; i<reactions.length; i++) {
                Reaction reaction = reactions[i];
                AtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, atom);
                if (container != null) {
                    return container;
                }
            }
        }
        // This should never happen.
        return null;
    }
    
    public static Reaction getRelevantReaction(ChemModel chemModel, Atom atom) {
        Reaction reaction = null;
        if (chemModel.getSetOfReactions() != null) {
            SetOfReactions reactionSet = chemModel.getSetOfReactions();
            reaction = SetOfReactionsManipulator.getRelevantReaction(reactionSet, atom);
        }
        return reaction;
    }

    /**
     * Returns all the AtomContainer's of a ChemModel.
     */
    public static AtomContainer[] getAllAtomContainers(ChemModel chemModel) {
        SetOfMolecules moleculeSet = new SetOfMolecules();
        if (chemModel.getSetOfMolecules() != null) {
            moleculeSet.add(chemModel.getSetOfMolecules());
        }
        if (chemModel.getSetOfReactions() != null) {
            moleculeSet.add(
                SetOfReactionsManipulator.getAllMolecules(
                    chemModel.getSetOfReactions()
                )
            );
        }
        return SetOfMoleculesManipulator.getAllAtomContainers(moleculeSet);
    }

    public static void setAtomProperties(ChemModel chemModel, Object propKey, Object propVal) {
        if (chemModel.getSetOfMolecules() != null) {
            SetOfMoleculesManipulator.setAtomProperties(
                chemModel.getSetOfMolecules(), propKey, propVal
            );
        }
        if (chemModel.getSetOfReactions() != null) {
            SetOfReactionsManipulator.setAtomProperties(
                chemModel.getSetOfReactions(), propKey, propVal
            );
        }
        if (chemModel.getCrystal() != null) {
            AtomContainerManipulator.setAtomProperties(
                chemModel.getCrystal(), propKey, propVal
            );
        }
    }
}

