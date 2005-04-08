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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.tools.manipulator;

import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.SetOfReactions;

/**
 * @cdk.module standard
 *
 * @see ChemModelManipulator
 */
public class SetOfReactionsManipulator {
    
    public static void removeAtomAndConnectedElectronContainers(SetOfReactions set, Atom atom) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            ReactionManipulator.removeAtomAndConnectedElectronContainers(reaction, atom);
            return;
        }
    }
    
    public static void removeElectronContainer(SetOfReactions set, ElectronContainer electrons) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            ReactionManipulator.removeElectronContainer(reaction, electrons);
            return;
        }
    }
    
    public static AtomContainer getAllInOneContainer(SetOfReactions set) {
        AtomContainer container = new AtomContainer();
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            container.add(ReactionManipulator.getAllInOneContainer(reaction));
        }
        return container;
    }
    
    public static SetOfMolecules getAllMolecules(SetOfReactions set) {
        SetOfMolecules moleculeSet = new SetOfMolecules();
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
    public static AtomContainer[] getAllAtomContainers(SetOfReactions set) {
		return SetOfMoleculesManipulator.getAllAtomContainers(
            getAllMolecules(set)
        );
    }
    
    public static Reaction getRelevantReaction(SetOfReactions set, Atom atom) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            AtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, atom);
            if (container != null) { // a match!
                return reaction;
            }
        }
        return null;
    }

    public static Reaction getRelevantReaction(SetOfReactions set, Bond bond) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            AtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, bond);
            if (container != null) { // a match!
                return reaction;
            }
        }
        return null;
    }

    public static AtomContainer getRelevantAtomContainer(SetOfReactions set, Atom atom) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            AtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, atom);
            if (container != null) { // a match!
                return container;
            }
        }
        return null;
    }

    public static AtomContainer getRelevantAtomContainer(SetOfReactions set, Bond bond) {
        Reaction[] reactions = set.getReactions();
        for (int i=0; i < reactions.length; i++) {
            Reaction reaction = reactions[i];
            AtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, bond);
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
}
