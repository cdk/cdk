/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.tools;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;

/**
 * @see org.openscience.cdk.tools.ChemModelManipulator
 */
public class ReactionManipulator {
    
    public static void removeAtomAndConnectedElectronContainers(Reaction reaction, Atom atom) {
        Molecule[] reactants = reaction.getReactants();
        for (int i=0; i<reactants.length; i++) {
            Molecule mol = reactants[i];
            if (mol.contains(atom)) {
                mol.removeAtomAndConnectedElectronContainers(atom);
            }
        }
        Molecule[] products = reaction.getProducts();
        for (int i=0; i<products.length; i++) {
            Molecule mol = products[i];
            if (mol.contains(atom)) {
                mol.removeAtomAndConnectedElectronContainers(atom);
            }
        }
    }
    
    public static void removeElectronContainer(Reaction reaction, ElectronContainer electrons) {
        Molecule[] reactants = reaction.getReactants();
        for (int i=0; i<reactants.length; i++) {
            Molecule mol = reactants[i];
            if (mol.contains(electrons)) {
                mol.removeElectronContainer(electrons);
            }
        }
        Molecule[] products = reaction.getProducts();
        for (int i=0; i<products.length; i++) {
            Molecule mol = products[i];
            if (mol.contains(electrons)) {
                mol.removeElectronContainer(electrons);
            }
        }
    }
    
    public static AtomContainer getAllInOneContainer(Reaction reaction) {
        AtomContainer container = new AtomContainer();
        Molecule[] reactants = reaction.getReactants();
        for (int i=0; i<reactants.length; i++) {
            Molecule molecule = reactants[i];
            container.add(molecule);
        }
        Molecule[] products = reaction.getProducts();
        for (int i=0; i<products.length; i++) {
            Molecule molecule = products[i];
            container.add(molecule);
        }
        return container;
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
        Molecule[] reactants = reaction.getReactants();
        for (int i=0; i<reactants.length; i++) {
            double coefficient = reaction.getReactantCoefficient(reactants[i]);
            reversedReaction.addProduct(reactants[i], coefficient);
        }
        Molecule[] products = reaction.getProducts();
        for (int i=0; i<products.length; i++) {
            double coefficient = reaction.getProductCoefficient(products[i]);
            reversedReaction.addReactant(products[i], coefficient);
        }
        return reversedReaction;
    }
}
