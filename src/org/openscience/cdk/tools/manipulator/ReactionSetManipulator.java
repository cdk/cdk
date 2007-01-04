/* $RCSfile$
 * $Author: egonw $ 
 * $Date: 2006-07-31 11:23:24 +0200 (Mon, 31 Jul 2006) $
 * $Revision: 6710 $
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
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * @cdk.module standard
 *
 * @see ChemModelManipulator
 */
public class ReactionSetManipulator {
    
    public static int getAtomCount(IReactionSet set) {
    	int count = 0;
        for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
        	count += ReactionManipulator.getAtomCount((IReaction)iter.next());
        }
        return count;
    }

    public static int getBondCount(IReactionSet set) {
    	int count = 0;
    	for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
        	count += ReactionManipulator.getBondCount((IReaction)iter.next());
        }
        return count;
    }

    public static void removeAtomAndConnectedElectronContainers(IReactionSet set, IAtom atom) {
    	for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            ReactionManipulator.removeAtomAndConnectedElectronContainers(reaction, atom);
        }
    }
    
    public static void removeElectronContainer(IReactionSet set, IElectronContainer electrons) {
    	for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            ReactionManipulator.removeElectronContainer(reaction, electrons);
        }
    }
    
    /** 
     * @deprecated This method has a serious performace impact. Try to use
     *   other methods.
     */
    public static IAtomContainer getAllInOneContainer(IReactionSet set) {
        IAtomContainer container = set.getBuilder().newAtomContainer();
        for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            container.add(ReactionManipulator.getAllInOneContainer(reaction));
        }
        return container;
    }
    
    public static IMoleculeSet getAllMolecules(IReactionSet set) {
        IMoleculeSet moleculeSet = set.getBuilder().newMoleculeSet();
        for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            moleculeSet.add(ReactionManipulator.getAllMolecules(reaction));
        }
        return moleculeSet;
    }
    
    public static List getAllIDs(IReactionSet set) {
        List IDlist = new ArrayList();
        for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            IDlist.addAll(ReactionManipulator.getAllIDs(reaction));
        }
        return IDlist;
    }
    
    /**
     * Returns all the AtomContainer's of a Reaction.
     */
    public static List getAllAtomContainers(IReactionSet set) {
    	
		return MoleculeSetManipulator.getAllAtomContainers(
            getAllMolecules(set)
        );
    }
    
    public static IReaction getRelevantReaction(IReactionSet set, IAtom atom) {
    	for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, atom);
            if (container != null) { // a match!
                return reaction;
            }
        }
        return null;
    }

    public static IReaction getRelevantReaction(IReactionSet set, IBond bond) {
    	for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, bond);
            if (container != null) { // a match!
                return reaction;
            }
        }
        return null;
    }

    public static IAtomContainer getRelevantAtomContainer(IReactionSet set, IAtom atom) {
    	for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, atom);
            if (container != null) { // a match!
                return container;
            }
        }
        return null;
    }

    public static IAtomContainer getRelevantAtomContainer(IReactionSet set, IBond bond) {
    	for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(reaction, bond);
            if (container != null) { // a match!
                return container;
            }
        }
        return null;
    }
    
    public static void setAtomProperties(IReactionSet set, Object propKey, Object propVal) {
    	for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            ReactionManipulator.setAtomProperties(reaction, propKey, propVal);
        }
    }
    
    public static List getAllChemObjects(IReactionSet set) {
        ArrayList list = new ArrayList();
        for (java.util.Iterator iter = set.reactions(); iter.hasNext();) {
            IReaction reaction = (IReaction)iter.next();
            list.addAll(ReactionManipulator.getAllChemObjects(reaction));
        }
        return list;
    }
    
}
