/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;

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
 * @see org.openscience.cdk.AtomContainer#removeAtomAndConnectedElectronContainers(IAtom)
 */
public class ChemModelManipulator {
    
    public static int getAtomCount(IChemModel chemModel) {
    	int count = 0;
        ICrystal crystal = chemModel.getCrystal();
        if (crystal != null) {
            count += crystal.getAtomCount();
        }
        ISetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
        if (moleculeSet != null) {
            count += SetOfMoleculesManipulator.getAtomCount(moleculeSet);
        }
        ISetOfReactions reactionSet = chemModel.getSetOfReactions();
        if (reactionSet != null) {
            count += SetOfReactionsManipulator.getAtomCount(reactionSet);
        }
        return count;
    }
    
    public static int getBondCount(IChemModel chemModel) {
    	int count = 0;
        ICrystal crystal = chemModel.getCrystal();
        if (crystal != null) {
            count += crystal.getBondCount();
        }
        ISetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
        if (moleculeSet != null) {
            count += SetOfMoleculesManipulator.getBondCount(moleculeSet);
        }
        ISetOfReactions reactionSet = chemModel.getSetOfReactions();
        if (reactionSet != null) {
            count += SetOfReactionsManipulator.getBondCount(reactionSet);
        }
        return count;
    }
    
    public static void removeAtomAndConnectedElectronContainers(IChemModel chemModel, IAtom atom) {
        ICrystal crystal = chemModel.getCrystal();
        if (crystal != null) {
            if (crystal.contains(atom)) {
                crystal.removeAtomAndConnectedElectronContainers(atom);
            }
            return;
        }
        ISetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
        if (moleculeSet != null) {
            SetOfMoleculesManipulator.removeAtomAndConnectedElectronContainers(moleculeSet, atom);
        }
        ISetOfReactions reactionSet = chemModel.getSetOfReactions();
        if (reactionSet != null) {
            SetOfReactionsManipulator.removeAtomAndConnectedElectronContainers(reactionSet, atom);
        }
    }
    
    public static void removeElectronContainer(IChemModel chemModel, IElectronContainer electrons) {
        ICrystal crystal = chemModel.getCrystal();
        if (crystal != null) {
            if (crystal.contains(electrons)) {
                crystal.removeElectronContainer(electrons);
            }
            return;
        }
        ISetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
        if (moleculeSet != null) {
            SetOfMoleculesManipulator.removeElectronContainer(moleculeSet, electrons);
        }
        ISetOfReactions reactionSet = chemModel.getSetOfReactions();
        if (reactionSet != null) {
            SetOfReactionsManipulator.removeElectronContainer(reactionSet, electrons);
        }
    }
    
    /**
     * Puts all the Molecules of this container together in one 
     * AtomContainer.
     *
     * @return  The AtomContainer with all the Molecules of this container
     * 
     * @deprecated This method has a serious performace impact. Try to use
     *   other methods.
     */
    public static IAtomContainer getAllInOneContainer(IChemModel chemModel) {
        IAtomContainer container = chemModel.getBuilder().newAtomContainer();
        ICrystal crystal = chemModel.getCrystal();
        if (crystal != null) {
            container.add(crystal);
        }
        ISetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
        if (moleculeSet != null) {
            container.add(SetOfMoleculesManipulator.getAllInOneContainer(moleculeSet));
        }
        ISetOfReactions reactionSet = chemModel.getSetOfReactions();
        if (reactionSet != null) {
            container.add(SetOfReactionsManipulator.getAllInOneContainer(reactionSet));
        }
        return container;
    }

    public static IAtomContainer createNewMolecule(IChemModel chemModel) {
        // Add a new molecule either the set of molecules
        IMolecule molecule = chemModel.getBuilder().newMolecule();
        if (chemModel.getSetOfMolecules() != null) {
            ISetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
            moleculeSet.addMolecule(molecule);
        } else {
            ISetOfMolecules moleculeSet = chemModel.getBuilder().newSetOfMolecules();
            moleculeSet.addMolecule(molecule);
            chemModel.setSetOfMolecules(moleculeSet);
        }
        return molecule;
    }

    public static IChemModel newChemModel(IAtomContainer molecule) {
        IChemModel model = molecule.getBuilder().newChemModel();
        ISetOfMolecules moleculeSet = model.getBuilder().newSetOfMolecules();
        moleculeSet.addAtomContainer(molecule);
        model.setSetOfMolecules(moleculeSet);
        return model;
    }

    /**
     * This badly named methods tries to determine which AtomContainer in the
     * ChemModel is best suited to contain added Atom's and Bond's.
     */
    public static IAtomContainer getRelevantAtomContainer(IChemModel chemModel, IAtom atom) {
        IAtomContainer result = null;
        if (chemModel.getSetOfMolecules() != null) {
            ISetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
            result = SetOfMoleculesManipulator.getRelevantAtomContainer(moleculeSet, atom);
            if (result != null) {
                return result;
            }
        }
        if (chemModel.getSetOfReactions() != null) {
            ISetOfReactions reactionSet = chemModel.getSetOfReactions();
            return SetOfReactionsManipulator.getRelevantAtomContainer(reactionSet, atom);
        }
        // This should never happen.
        return null;
    }

    public static IAtomContainer getRelevantAtomContainer(IChemModel chemModel, IBond bond) {
        IAtomContainer result = null;
        if (chemModel.getSetOfMolecules() != null) {
            ISetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
            result = SetOfMoleculesManipulator.getRelevantAtomContainer(moleculeSet, bond);
            if (result != null) {
                return result;
            }
        }
        if (chemModel.getSetOfReactions() != null) {
            ISetOfReactions reactionSet = chemModel.getSetOfReactions();
            return SetOfReactionsManipulator.getRelevantAtomContainer(reactionSet, bond);
        }
        // This should never happen.
        return null;
    }
    
    public static IReaction getRelevantReaction(IChemModel chemModel, IAtom atom) {
        IReaction reaction = null;
        if (chemModel.getSetOfReactions() != null) {
            ISetOfReactions reactionSet = chemModel.getSetOfReactions();
            reaction = SetOfReactionsManipulator.getRelevantReaction(reactionSet, atom);
        }
        return reaction;
    }

    /**
     * Returns all the AtomContainer's of a ChemModel.
     */
    public static IAtomContainer[] getAllAtomContainers(IChemModel chemModel) {
        ISetOfMolecules moleculeSet = chemModel.getBuilder().newSetOfMolecules();
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

    public static void setAtomProperties(IChemModel chemModel, Object propKey, Object propVal) {
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
    
	public static List getAllChemObjects(IChemModel chemModel) {
		ArrayList list = new ArrayList();
        list.add(chemModel);
        ICrystal crystal = chemModel.getCrystal();
        if (crystal != null) {
            list.add(crystal);
        }
        ISetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
        if (moleculeSet != null) {
            list.addAll(SetOfMoleculesManipulator.getAllChemObjects(moleculeSet));
        }
        ISetOfReactions reactionSet = chemModel.getSetOfReactions();
        if (reactionSet != null) {
            list.addAll(SetOfReactionsManipulator.getAllChemObjects(reactionSet));
        }
		return list;
	}

}

