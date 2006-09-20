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
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IMoleculeSet;

/**
 * @cdk.module standard
 *
 * @see ChemModelManipulator
 */
public class ReactionManipulator {
    
    public static int getAtomCount(IReaction reaction) {
    	int count = 0;
        IMolecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
        	count += reactants[i].getAtomCount();
        }
        IMolecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
        	count += products[i].getAtomCount();
        }
        return count;
    }
    
    public static int getBondCount(IReaction reaction) {
    	int count = 0;
        IMolecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
        	count += reactants[i].getBondCount();
        }
        IMolecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
        	count += products[i].getBondCount();
        }
        return count;
    }
    
    public static void removeAtomAndConnectedElectronContainers(IReaction reaction, IAtom atom) {
        IMolecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            IMolecule mol = reactants[i];
            if (mol.contains(atom)) {
                mol.removeAtomAndConnectedElectronContainers(atom);
            }
        }
        IMolecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            IMolecule mol = products[i];
            if (mol.contains(atom)) {
                mol.removeAtomAndConnectedElectronContainers(atom);
            }
        }
    }
    
    public static void removeElectronContainer(IReaction reaction, IElectronContainer electrons) {
        IMolecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            IMolecule mol = reactants[i];
            if (mol.contains(electrons)) {
                mol.removeElectronContainer(electrons);
            }
        }
        IMolecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            IMolecule mol = products[i];
            if (mol.contains(electrons)) {
                mol.removeElectronContainer(electrons);
            }
        }
    }
    
    /** 
     * @deprecated This method has a serious performace impact. Try to use
     *   other methods.
     */
    public static IAtomContainer getAllInOneContainer(IReaction reaction) {
        IAtomContainer container = reaction.getBuilder().newAtomContainer();
        if (reaction == null) {
            return container;
        }
        IMolecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            IMolecule molecule = reactants[i];
            container.add(molecule);
        }
        IMolecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            IMolecule molecule = products[i];
            container.add(molecule);
        }
        return container;
    }
    
    public static IMoleculeSet getAllMolecules(IReaction reaction) {
        IMoleculeSet moleculeSet = reaction.getBuilder().newMoleculeSet();
        IMolecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            moleculeSet.addMolecule(reactants[i]);
        }
        IMolecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            moleculeSet.addMolecule(products[i]);
        }
        return moleculeSet;
    }
    
    /**
     * Returns a new Reaction object which is the reverse of the given
     * Reaction.
     */
    public static IReaction reverse(IReaction reaction) {
        IReaction reversedReaction = reaction.getBuilder().newReaction();
        if (reaction.getDirection() == IReaction.BIDIRECTIONAL) {
            reversedReaction.setDirection(IReaction.BIDIRECTIONAL);
        } else if (reaction.getDirection() == IReaction.FORWARD) {
            reversedReaction.setDirection(IReaction.BACKWARD);
        } else if (reaction.getDirection() == IReaction.BACKWARD) {
            reversedReaction.setDirection(IReaction.FORWARD);
        }
        IMolecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            double coefficient = reaction.getReactantCoefficient(reactants[i]);
            reversedReaction.addProduct(reactants[i], coefficient);
        }
        IMolecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            double coefficient = reaction.getProductCoefficient(products[i]);
            reversedReaction.addReactant(products[i], coefficient);
        }
        return reversedReaction;
    }
    
    /**
     * Returns all the AtomContainer's of a Reaction.
     */
    public static IAtomContainer[] getAllAtomContainers(IReaction reaction) {
		return MoleculeSetManipulator.getAllAtomContainers(
            getAllMolecules(reaction)
        );
    }
    
    public static Vector getAllIDs(IReaction reaction) {
        Vector idList = new Vector();
        if (reaction.getID() != null) idList.addElement(reaction.getID());
        IMolecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            IMolecule mol = reactants[i];
            idList.addAll(AtomContainerManipulator.getAllIDs(mol));
        }
        IMolecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            IMolecule mol = products[i];
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
        IMolecule[] reactants = reaction.getReactants().getMolecules();
        for (int j=0; j<reactants.length; j++) {
            AtomContainerManipulator.setAtomProperties(
                reactants[j], propKey, propVal
            );
        }
        IMolecule[] products = reaction.getProducts().getMolecules();
        for (int j=0; j<products.length; j++) {
            AtomContainerManipulator.setAtomProperties(
                products[j], propKey, propVal
            );
        }
    }
    
    public static List getAllChemObjects(IReaction reaction) {
        ArrayList list = new ArrayList();
        IMolecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            list.add(reactants[i]);
        }
        IMolecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            list.add(products[i]);
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
    public static IChemObject getMappedChemObject(IReaction reaction, IChemObject chemObject){
    	IMapping[] mappings = reaction.getMappings();
    	for(int i = 0 ; i < mappings.length ; i++){
    		IMapping mapping = mappings[i];
    		IChemObject[] map = mapping.getRelatedChemObjects();
			if(map[0].equals(chemObject)){
				return map[1];
			}else if(map[1].equals(chemObject))
				return map[0];
    	}
    	return null;
    }
    
}
