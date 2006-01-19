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
import org.openscience.cdk.interfaces.Reaction;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.SetOfReactions;

/**
 * @cdk.module standard
 *
 * @see ChemModelManipulator
 */
public class SetOfReactionsManipulator {
    
    public static void removeAtomAndConnectedElectronContainers(SetOfReactions set, IAtom atom) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            ReactionManipulator.removeAtomAndConnectedElectronContainers(reaction, atom);
            return;
        }
    }
    
    public static void removeElectronContainer(SetOfReactions set, IElectronContainer electrons) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            ReactionManipulator.removeElectronContainer(reaction, electrons);
            return;
        }
    }
    
    public static IAtomContainer getAllInOneContainer(SetOfReactions set) {
        IAtomContainer container = set.getBuilder().newAtomContainer();
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            container.add(ReactionManipulator.getAllInOneContainer(reaction));
        }
        return container;
    }
    
    public static ISetOfMolecules getAllMolecules(SetOfReactions set) {
        ISetOfMolecules moleculeSet = set.getBuilder().newSetOfMolecules();
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            moleculeSet.add(ReactionManipulator.getAllMolecules(reaction));
        }
        return moleculeSet;
    }
    
    public static Vector getAllIDs(SetOfReactions set) {
        Vector IDlist = new Vector();
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            IDlist.addAll(ReactionManipulator.getAllIDs(reaction));
        }
        return IDlist;
    }
    
    /**
     * Returns all the AtomContainer's of a Reaction.
     */
    public static IAtomContainer[] getAllAtomContainers(SetOfReactions set) {
		return SetOfMoleculesManipulator.getAllAtomContainers(
            getAllMolecules(set)
        );
    }
    
    public static Reaction getRelevantReaction(SetOfReactions set, IAtom atom) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, atom);
            if (container != null) { // a match!
                return reaction;
            }
        }
        return null;
    }

    public static Reaction getRelevantReaction(SetOfReactions set, IBond bond) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, bond);
            if (container != null) { // a match!
                return reaction;
            }
        }
        return null;
    }

    public static IAtomContainer getRelevantAtomContainer(SetOfReactions set, IAtom atom) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, atom);
            if (container != null) { // a match!
                return container;
            }
        }
        return null;
    }

    public static IAtomContainer getRelevantAtomContainer(SetOfReactions set, IBond bond) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, bond);
            if (container != null) { // a match!
                return container;
            }
        }
        return null;
    }
    
    public static void setAtomProperties(SetOfReactions set, Object propKey, Object propVal) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            ReactionManipulator.setAtomProperties(reaction, propKey, propVal);
        }
    }
    
    public static List getAllChemObjects(SetOfReactions set) {
        ArrayList list = new ArrayList();
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            list.addAll(ReactionManipulator.getAllChemObjects(reaction));
        }
        return list;
    }
    
}
