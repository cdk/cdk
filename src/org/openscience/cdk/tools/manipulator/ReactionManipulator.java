/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.Reaction;
import org.openscience.cdk.interfaces.SetOfMolecules;

/**
 * @cdk.module standard
 *
 * @see ChemModelManipulator
 */
public class ReactionManipulator {
    
    public static void removeAtomAndConnectedElectronContainers(Reaction reaction, IAtom atom) {
        Molecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            Molecule mol = reactants[i];
            if (mol.contains(atom)) {
                mol.removeAtomAndConnectedElectronContainers(atom);
            }
        }
        Molecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            Molecule mol = products[i];
            if (mol.contains(atom)) {
                mol.removeAtomAndConnectedElectronContainers(atom);
            }
        }
    }
    
    public static void removeElectronContainer(Reaction reaction, IElectronContainer electrons) {
        Molecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            Molecule mol = reactants[i];
            if (mol.contains(electrons)) {
                mol.removeElectronContainer(electrons);
            }
        }
        Molecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            Molecule mol = products[i];
            if (mol.contains(electrons)) {
                mol.removeElectronContainer(electrons);
            }
        }
    }
    
    public static IAtomContainer getAllInOneContainer(Reaction reaction) {
        IAtomContainer container = reaction.getBuilder().newAtomContainer();
        if (reaction == null) {
            return container;
        }
        Molecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            Molecule molecule = reactants[i];
            container.add(molecule);
        }
        Molecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            Molecule molecule = products[i];
            container.add(molecule);
        }
        return container;
    }
    
    public static SetOfMolecules getAllMolecules(Reaction reaction) {
        SetOfMolecules moleculeSet = reaction.getBuilder().newSetOfMolecules();
        Molecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            moleculeSet.addMolecule(reactants[i]);
        }
        Molecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            moleculeSet.addMolecule(products[i]);
        }
        return moleculeSet;
    }
    
    /**
     * Returns a new Reaction object which is the reverse of the given
     * Reaction.
     */
    public static Reaction reverse(Reaction reaction) {
        Reaction reversedReaction = reaction.getBuilder().newReaction();
        if (reaction.getDirection() == Reaction.BIDIRECTIONAL) {
            reversedReaction.setDirection(Reaction.BIDIRECTIONAL);
        } else if (reaction.getDirection() == Reaction.FORWARD) {
            reversedReaction.setDirection(Reaction.BACKWARD);
        } else if (reaction.getDirection() == Reaction.BACKWARD) {
            reversedReaction.setDirection(Reaction.FORWARD);
        }
        Molecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            double coefficient = reaction.getReactantCoefficient(reactants[i]);
            reversedReaction.addProduct(reactants[i], coefficient);
        }
        Molecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            double coefficient = reaction.getProductCoefficient(products[i]);
            reversedReaction.addReactant(products[i], coefficient);
        }
        return reversedReaction;
    }
    
    /**
     * Returns all the AtomContainer's of a Reaction.
     */
    public static IAtomContainer[] getAllAtomContainers(Reaction reaction) {
		return SetOfMoleculesManipulator.getAllAtomContainers(
            getAllMolecules(reaction)
        );
    }
    
    public static Vector getAllIDs(Reaction reaction) {
        Vector idList = new Vector();
        if (reaction.getID() != null) idList.addElement(reaction.getID());
        Molecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            Molecule mol = reactants[i];
            idList.addAll(AtomContainerManipulator.getAllIDs(mol));
        }
        Molecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            Molecule mol = products[i];
            idList.addAll(AtomContainerManipulator.getAllIDs(mol));
        }
        return idList;
    }

    public static IAtomContainer getRelevantAtomContainer(Reaction reaction, IAtom atom) {
        IAtomContainer result = SetOfMoleculesManipulator.getRelevantAtomContainer(reaction.getReactants(), atom);
        if (result != null) {
            return result;
        }
        return SetOfMoleculesManipulator.getRelevantAtomContainer(reaction.getProducts(), atom);
    }

    public static IAtomContainer getRelevantAtomContainer(Reaction reaction, IBond bond) {
        IAtomContainer result = SetOfMoleculesManipulator.getRelevantAtomContainer(reaction.getReactants(), bond);
        if (result != null) {
            return result;
        }
        return SetOfMoleculesManipulator.getRelevantAtomContainer(reaction.getProducts(), bond);
    }
    
    public static void setAtomProperties(Reaction reaction, Object propKey, Object propVal) {
        Molecule[] reactants = reaction.getReactants().getMolecules();
        for (int j=0; j<reactants.length; j++) {
            AtomContainerManipulator.setAtomProperties(
                reactants[j], propKey, propVal
            );
        }
        Molecule[] products = reaction.getProducts().getMolecules();
        for (int j=0; j<products.length; j++) {
            AtomContainerManipulator.setAtomProperties(
                products[j], propKey, propVal
            );
        }
    }
    
    public static List getAllChemObjects(Reaction reaction) {
        ArrayList list = new ArrayList();
        Molecule[] reactants = reaction.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            list.add(reactants[i]);
        }
        Molecule[] products = reaction.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            list.add(products[i]);
        }
        return list;
    }
    
}
