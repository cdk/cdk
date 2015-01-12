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
 *  */
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @cdk.module standard
 * @cdk.githash
 *
 * @see ChemModelManipulator
 */
public class ReactionManipulator {

    public static int getAtomCount(IReaction reaction) {
        int count = 0;
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            count += reactants.getAtomContainer(i).getAtomCount();
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            count += products.getAtomContainer(i).getAtomCount();
        }
        return count;
    }

    public static int getBondCount(IReaction reaction) {
        int count = 0;
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            count += reactants.getAtomContainer(i).getBondCount();
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            count += products.getAtomContainer(i).getBondCount();
        }
        return count;
    }

    public static void removeAtomAndConnectedElectronContainers(IReaction reaction, IAtom atom) {
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            IAtomContainer mol = reactants.getAtomContainer(i);
            if (mol.contains(atom)) {
                mol.removeAtomAndConnectedElectronContainers(atom);
            }
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            IAtomContainer mol = products.getAtomContainer(i);
            if (mol.contains(atom)) {
                mol.removeAtomAndConnectedElectronContainers(atom);
            }
        }
    }

    public static void removeElectronContainer(IReaction reaction, IElectronContainer electrons) {
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            IAtomContainer mol = reactants.getAtomContainer(i);
            if (mol.contains(electrons)) {
                mol.removeElectronContainer(electrons);
            }
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            IAtomContainer mol = products.getAtomContainer(i);
            if (mol.contains(electrons)) {
                mol.removeElectronContainer(electrons);
            }
        }
    }

    /**
     * Get all molecule of a {@link IReaction}: reactants + products.
     *
     * @param reaction  The IReaction
     * @return The IAtomContainerSet
     */
    public static IAtomContainerSet getAllMolecules(IReaction reaction) {
        IAtomContainerSet moleculeSet = reaction.getBuilder().newInstance(IAtomContainerSet.class);

        moleculeSet.add(getAllReactants(reaction));
        moleculeSet.add(getAllProducts(reaction));

        return moleculeSet;
    }

    /**
     * get all products of a IReaction
     *
     * @param reaction  The IReaction
     * @return The IAtomContainerSet
     */
    public static IAtomContainerSet getAllProducts(IReaction reaction) {
        IAtomContainerSet moleculeSet = reaction.getBuilder().newInstance(IAtomContainerSet.class);
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            moleculeSet.addAtomContainer(products.getAtomContainer(i));
        }
        return moleculeSet;
    }

    /**
     * get all reactants of a IReaction
     *
     * @param reaction  The IReaction
     * @return The IAtomContainerSet
     */
    public static IAtomContainerSet getAllReactants(IReaction reaction) {
        IAtomContainerSet moleculeSet = reaction.getBuilder().newInstance(IAtomContainerSet.class);
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            moleculeSet.addAtomContainer(reactants.getAtomContainer(i));
        }
        return moleculeSet;
    }

    /**
     * Returns a new Reaction object which is the reverse of the given
     * Reaction.
     * @param reaction the reaction being considered
     * @return the reverse reaction
     */
    public static IReaction reverse(IReaction reaction) {
        IReaction reversedReaction = reaction.getBuilder().newInstance(IReaction.class);
        if (reaction.getDirection() == IReaction.Direction.BIDIRECTIONAL) {
            reversedReaction.setDirection(IReaction.Direction.BIDIRECTIONAL);
        } else if (reaction.getDirection() == IReaction.Direction.FORWARD) {
            reversedReaction.setDirection(IReaction.Direction.BACKWARD);
        } else if (reaction.getDirection() == IReaction.Direction.BACKWARD) {
            reversedReaction.setDirection(IReaction.Direction.FORWARD);
        }
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            double coefficient = reaction.getReactantCoefficient(reactants.getAtomContainer(i));
            reversedReaction.addProduct(reactants.getAtomContainer(i), coefficient);
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            double coefficient = reaction.getProductCoefficient(products.getAtomContainer(i));
            reversedReaction.addReactant(products.getAtomContainer(i), coefficient);
        }
        return reversedReaction;
    }

    /**
     * Returns all the AtomContainer's of a Reaction.
     * @param reaction The reaction being considered
     * @return a list of the IAtomContainer objects comprising the reaction
     */
    public static List<IAtomContainer> getAllAtomContainers(IReaction reaction) {
        return MoleculeSetManipulator.getAllAtomContainers(getAllMolecules(reaction));
    }

    public static List<String> getAllIDs(IReaction reaction) {
        List<String> idList = new ArrayList<String>();
        if (reaction.getID() != null) idList.add(reaction.getID());
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            IAtomContainer mol = reactants.getAtomContainer(i);
            idList.addAll(AtomContainerManipulator.getAllIDs(mol));
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            IAtomContainer mol = products.getAtomContainer(i);
            idList.addAll(AtomContainerManipulator.getAllIDs(mol));
        }
        return idList;
    }

    public static IAtomContainer getRelevantAtomContainer(IReaction reaction, IAtom atom) {
        IAtomContainer result = MoleculeSetManipulator.getRelevantAtomContainer(reaction.getReactants(), atom);
        if (result != null) {
            return result;
        }
        return MoleculeSetManipulator.getRelevantAtomContainer(reaction.getProducts(), atom);
    }

    public static IAtomContainer getRelevantAtomContainer(IReaction reaction, IBond bond) {
        IAtomContainer result = MoleculeSetManipulator.getRelevantAtomContainer(reaction.getReactants(), bond);
        if (result != null) {
            return result;
        }
        return MoleculeSetManipulator.getRelevantAtomContainer(reaction.getProducts(), bond);
    }

    public static void setAtomProperties(IReaction reaction, Object propKey, Object propVal) {
        IAtomContainerSet reactants = reaction.getReactants();
        for (int j = 0; j < reactants.getAtomContainerCount(); j++) {
            AtomContainerManipulator.setAtomProperties(reactants.getAtomContainer(j), propKey, propVal);
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int j = 0; j < products.getAtomContainerCount(); j++) {
            AtomContainerManipulator.setAtomProperties(products.getAtomContainer(j), propKey, propVal);
        }
    }

    public static List<IChemObject> getAllChemObjects(IReaction reaction) {
        ArrayList<IChemObject> list = new ArrayList<IChemObject>();
        list.add(reaction);
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            list.add(reactants.getAtomContainer(i));
        }
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            list.add(products.getAtomContainer(i));
        }
        return list;
    }

    /**
     * get the IAtom which is mapped
     *
     * @param reaction   The IReaction which contains the mapping
     * @param chemObject The IChemObject which will be searched its mapped IChemObject
     * @return           The mapped IChemObject
     */
    public static IChemObject getMappedChemObject(IReaction reaction, IChemObject chemObject) {
        for (IMapping mapping : reaction.mappings()) {
            if (mapping.getChemObject(0).equals(chemObject)) {
                return mapping.getChemObject(1);
            } else if (mapping.getChemObject(1).equals(chemObject)) return mapping.getChemObject(0);
        }
        return null;
    }

}
