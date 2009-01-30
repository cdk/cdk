/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2008  Miguel Rojas <miguelrojasch@users.sf.net>
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionScheme;

/**
 * @cdk.module standard
 *
 * @see ChemModelManipulator
 */
@TestClass("org.openscience.cdk.tools.manipulator.ReactionSetManipulatorTest")
public class ReactionSchemeManipulator {

    /**
     * Get all Molecules object from a set of Reactions given a IMoleculeSet to add. 
     * 
     * @param  scheme The set of reaction to inspect
     * @param  molSet The set of molecules to be added
     * @return        The IMoleculeSet
     */
    @TestMethod("testGetAllMolecules_IReactionScheme_IMoleculeSet")
    public static IMoleculeSet getAllMolecules(IReactionScheme scheme, IMoleculeSet molSet) {
    	// A ReactionScheme can contain other IRreactionSet objects
		if(scheme.getReactionSchemeCount() != 0)
    		for(IReactionScheme rm : scheme.reactionSchemes()){
                for (IAtomContainer ac : getAllMolecules(rm, molSet).atomContainers()) {
                    boolean contain = false;
                    for (IAtomContainer atomContainer : molSet.molecules()) {
                        if (atomContainer.equals(ac)) {
                            contain = true;
                            break;
                        }
                    }
                    if (!contain)
                        molSet.addMolecule((IMolecule) (ac));
                }
    		}
        for (IReaction reaction : scheme.reactions()) {
            IMoleculeSet newMoleculeSet = ReactionManipulator.getAllMolecules(reaction);
            for (IAtomContainer ac : newMoleculeSet.molecules()) {
                boolean contain = false;
                for (IAtomContainer atomContainer : molSet.molecules()) {
                    if (atomContainer.equals(ac)) {
                        contain = true;
                        break;
                    }
                }
                if (!contain)
                    molSet.addMolecule((IMolecule) (ac));

            }
        }
	    
	    return molSet;
    }
    /**
     * get all Molecules object from a set of Reactions. 
     * 
     * @param scheme The scheme of reaction to inspect
     * @return       The IMoleculeSet
     */
    @TestMethod("testGetAllMolecules_IReactionScheme")
    public static IMoleculeSet getAllMolecules(IReactionScheme scheme) {
    	return getAllMolecules(scheme, scheme.getBuilder().newMoleculeSet());
    }

    /**
     * Get all ID of this IReactionSet.
     * 
     * @param scheme  The IReactionScheme to analyze
     * @return        A List with all ID
     */
    @TestMethod("testGetAllIDs_IReactionScheme")
	public static List<String> getAllIDs(IReactionScheme scheme) {
        List<String> IDlist = new ArrayList<String>();
        if (scheme.getID() != null) IDlist.add(scheme.getID());
        for (IReaction reaction : scheme.reactions()) {
            IDlist.addAll(ReactionManipulator.getAllIDs(reaction));
        }
        return IDlist;
    }
    
    
}
