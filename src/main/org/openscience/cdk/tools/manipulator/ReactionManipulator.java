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
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;

/**
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @see ChemModelManipulator
 */
@TestClass("org.openscience.cdk.tools.manipulator.ReactionManipulatorTest")
public class ReactionManipulator {

    @TestMethod("testGetAtomCount_IReaction")
    public static int getAtomCount(IReaction reaction) {
    	int count = 0;
        IMoleculeSet reactants = reaction.getReactants();
        for (int i=0; i<reactants.getAtomContainerCount(); i++) {
        	count += reactants.getMolecule(i).getAtomCount();
        }
        IMoleculeSet products = reaction.getProducts();
        for (int i=0; i<products.getAtomContainerCount(); i++) {
        	count += products.getMolecule(i).getAtomCount();
        }
        return count;
    }

    @TestMethod("testGetBondCount_IReaction")
    public static int getBondCount(IReaction reaction) {
    	int count = 0;
    	IMoleculeSet reactants = reaction.getReactants();
        for (int i=0; i<reactants.getAtomContainerCount(); i++) {
        	count += reactants.getMolecule(i).getBondCount();
        }
        IMoleculeSet products = reaction.getProducts();
        for (int i=0; i<products.getAtomContainerCount(); i++) {
        	count += products.getMolecule(i).getBondCount();
        }
        return count;
    }


    @TestMethod("testRemoveAtomAndConnectedElectronContainers_IReaction_IAtom")
    public static void removeAtomAndConnectedElectronContainers(IReaction reaction, IAtom atom) {
    	IMoleculeSet reactants = reaction.getReactants();
        for (int i=0; i<reactants.getAtomContainerCount(); i++) {
            IMolecule mol = reactants.getMolecule(i);
            if (mol.contains(atom)) {
                mol.removeAtomAndConnectedElectronContainers(atom);
            }
        }
        IMoleculeSet products = reaction.getProducts();
        for (int i=0; i<products.getAtomContainerCount(); i++) {
            IMolecule mol = products.getMolecule(i);
            if (mol.contains(atom)) {
                mol.removeAtomAndConnectedElectronContainers(atom);
            }
        }
    }

    @TestMethod("testRemoveElectronContainer_IReaction_IElectronContainer")
    public static void removeElectronContainer(IReaction reaction, IElectronContainer electrons) {
    	IMoleculeSet reactants = reaction.getReactants();
        for (int i=0; i<reactants.getAtomContainerCount(); i++) {
            IMolecule mol = reactants.getMolecule(i);
            if (mol.contains(electrons)) {
                mol.removeElectronContainer(electrons);
            }
        }
        IMoleculeSet products = reaction.getProducts();
        for (int i=0; i<products.getAtomContainerCount(); i++) {
            IMolecule mol = products.getMolecule(i);
            if (mol.contains(electrons)) {
                mol.removeElectronContainer(electrons);
            }
        }
    }
    
    /**
     * get all molecule of a IReaction. Reactants + Products
     * 
     * @param reaction  The IReaction
     * @return The IMoleculeSet
     */
    @TestMethod("testGetAllMolecules_IReaction")
    public static IMoleculeSet getAllMolecules(IReaction reaction) {
        IMoleculeSet moleculeSet = reaction.getBuilder().newMoleculeSet();

        moleculeSet.add(getAllReactants(reaction));
        moleculeSet.add(getAllProducts(reaction));
        
        return moleculeSet;
    }
    /**
     * get all products of a IReaction
     * 
     * @param reaction  The IReaction
     * @return The IMoleculeSet
     */
    @TestMethod("testGetAllProducts_IReaction")
    public static IMoleculeSet getAllProducts(IReaction reaction) {
        IMoleculeSet moleculeSet = reaction.getBuilder().newMoleculeSet();
        IMoleculeSet products = reaction.getProducts();
        for (int i=0; i<products.getAtomContainerCount(); i++) {
            moleculeSet.addMolecule(products.getMolecule(i));
        }
        return moleculeSet;
    }

    /**
     * get all reactants of a IReaction
     * 
     * @param reaction  The IReaction
     * @return The IMoleculeSet
     */
    @TestMethod("testGetAllReactants_IReaction")
    public static IMoleculeSet getAllReactants(IReaction reaction) {
        IMoleculeSet moleculeSet = reaction.getBuilder().newMoleculeSet();
        IMoleculeSet reactants = reaction.getReactants();
        for (int i=0; i<reactants.getAtomContainerCount(); i++) {
            moleculeSet.addMolecule(reactants.getMolecule(i));
        }
        return moleculeSet;
    }
    
    /**
     * Returns a new Reaction object which is the reverse of the given
     * Reaction.
     * @param reaction the reaction being considered
     * @return the reverse reaction
     */
    @TestMethod("testReverse_IReaction")
    public static IReaction reverse(IReaction reaction) {
        IReaction reversedReaction = reaction.getBuilder().newReaction();
        if (reaction.getDirection() == IReaction.Direction.BIDIRECTIONAL) {
            reversedReaction.setDirection(IReaction.Direction.BIDIRECTIONAL);
        } else if (reaction.getDirection() == IReaction.Direction.FORWARD) {
            reversedReaction.setDirection(IReaction.Direction.BACKWARD);
        } else if (reaction.getDirection() == IReaction.Direction.BACKWARD) {
            reversedReaction.setDirection(IReaction.Direction.FORWARD);
        }
        IMoleculeSet reactants = reaction.getReactants();
        for (int i=0; i<reactants.getAtomContainerCount(); i++) {
            double coefficient = reaction.getReactantCoefficient(reactants.getMolecule(i));
            reversedReaction.addProduct(reactants.getMolecule(i), coefficient);
        }
        IMoleculeSet products = reaction.getProducts();
        for (int i=0; i<products.getAtomContainerCount(); i++) {
            double coefficient = reaction.getProductCoefficient(products.getMolecule(i));
            reversedReaction.addReactant(products.getMolecule(i), coefficient);
        }
        return reversedReaction;
    }
    
    /**
     * Returns all the AtomContainer's of a Reaction.
     * @param reaction The reaction being considered
     * @return a list of the IAtomContainer objects comprising the reaction
     */
    @TestMethod("testGetAllAtomContainers_IReaction")
    public static List<IAtomContainer> getAllAtomContainers(IReaction reaction) {
		return MoleculeSetManipulator.getAllAtomContainers(
            getAllMolecules(reaction)
        );
    }

    @TestMethod("testGetAllIDs_IReaction")
    public static List<String> getAllIDs(IReaction reaction) {
        List<String> idList = new ArrayList<String>();
        if (reaction.getID() != null) idList.add(reaction.getID());
        IMoleculeSet reactants = reaction.getReactants();
        for (int i=0; i<reactants.getAtomContainerCount(); i++) {
            IMolecule mol = reactants.getMolecule(i);
            idList.addAll(AtomContainerManipulator.getAllIDs(mol));
        }
        IMoleculeSet products = reaction.getProducts();
        for (int i=0; i<products.getAtomContainerCount(); i++) {
            IMolecule mol = products.getMolecule(i);
            idList.addAll(AtomContainerManipulator.getAllIDs(mol));
        }
        return idList;
    }

    @TestMethod("testGetRelevantAtomContainer_IReaction_IAtom")
    public static IAtomContainer getRelevantAtomContainer(IReaction reaction, IAtom atom) {
        IAtomContainer result = MoleculeSetManipulator.getRelevantAtomContainer(reaction.getReactants(), atom);
        if (result != null) {
            return result;
        }
        return MoleculeSetManipulator.getRelevantAtomContainer(reaction.getProducts(), atom);
    }

    @TestMethod("testGetRelevantAtomContainer_IReaction_IBond")
    public static IAtomContainer getRelevantAtomContainer(IReaction reaction, IBond bond) {
        IAtomContainer result = MoleculeSetManipulator.getRelevantAtomContainer(reaction.getReactants(), bond);
        if (result != null) {
            return result;
        }
        return MoleculeSetManipulator.getRelevantAtomContainer(reaction.getProducts(), bond);
    }

    @TestMethod("testSetAtomProperties_IReactionSet_Object_Object")
    public static void setAtomProperties(IReaction reaction, Object propKey, Object propVal) {
    	IMoleculeSet reactants = reaction.getReactants();
        for (int j=0; j<reactants.getAtomContainerCount(); j++) {
            AtomContainerManipulator.setAtomProperties(
                reactants.getMolecule(j), propKey, propVal
            );
        }
        IMoleculeSet products = reaction.getProducts();
        for (int j=0; j<products.getAtomContainerCount(); j++) {
            AtomContainerManipulator.setAtomProperties(
                products.getMolecule(j), propKey, propVal
            );
        }
    }

    @TestMethod("testGetAllChemObjects_IReactionSet")
    public static List<IChemObject> getAllChemObjects(IReaction reaction) {
        ArrayList<IChemObject> list = new ArrayList<IChemObject>();
        list.add(reaction);
        IMoleculeSet reactants = reaction.getReactants();
        for (int i=0; i<reactants.getAtomContainerCount(); i++) {
            list.add(reactants.getMolecule(i));
        }
        IMoleculeSet products = reaction.getProducts();
        for (int i=0; i<products.getAtomContainerCount(); i++) {
            list.add(products.getMolecule(i));
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
    @TestMethod("testGetMappedChemObject_IReaction_IAtom,testGetMappedChemObject_IReaction_IBond")
    public static IChemObject getMappedChemObject(IReaction reaction, IChemObject chemObject){
        for (IMapping mapping : reaction.mappings()) {
            if (mapping.getChemObject(0).equals(chemObject)) {
                return mapping.getChemObject(1);
            } else if (mapping.getChemObject(1).equals(chemObject))
                return mapping.getChemObject(0);
        }
    	return null;
    }
    
}
