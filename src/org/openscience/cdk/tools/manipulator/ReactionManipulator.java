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

import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfMolecules;

/**
 * @cdk.module standard
 *
 * @see ChemModelManipulator
 */
public class ReactionManipulator {
    
    public static void removeAtomAndConnectedElectronContainers(Reaction reaction, Atom atom) {
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
    
    public static void removeElectronContainer(Reaction reaction, ElectronContainer electrons) {
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
    
    public static AtomContainer getAllInOneContainer(Reaction reaction) {
        AtomContainer container = new AtomContainer();
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
        SetOfMolecules moleculeSet = new SetOfMolecules();
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
        Reaction reversedReaction = new Reaction();
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
    public static AtomContainer[] getAllAtomContainers(Reaction reaction) {
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

    /**
     * This badly named methods tries to determine which AtomContainer in the
     * ChemModel is best suited to contain added Atom's and Bond's.
     */
    public static AtomContainer getRelevantAtomContainer(Reaction reaction, Atom atom) {
        Molecule[] reactants = reaction.getReactants().getMolecules();
        for (int j=0; j<reactants.length;j++) {
            if (reactants[j].contains(atom)) {
                return reactants[j];
            }
        }
        Molecule[] products = reaction.getProducts().getMolecules();
        for (int j=0; j<products.length; j++) {
            if (products[j].contains(atom)) {
                return products[j];
            }
        }
        return null;
    }
}
