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
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * @cdk.module standard
 *
 * @see ChemModelManipulator
 */
@TestClass("org.openscience.cdk.tools.manipulator.ReactionSchemeManipulatorTest")
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
        if(scheme.getReactionSchemeCount() != 0)
    		for(IReactionScheme rs : scheme.reactionSchemes()){
    			IDlist.addAll(getAllIDs(rs));
    		}
        return IDlist;
    }

    /**
     * Get all IReaction's object from a given IReactionScheme. 
     * 
     * @param  scheme The IReactionScheme to extract
     * @return        The IReactionSet
     */
    @TestMethod("testGetAllReactions_IReactionScheme")
    public static IReactionSet getAllReactions(IReactionScheme scheme) {
    	IReactionSet reactionSet = scheme.getBuilder().newReactionSet();
    	
    	// A ReactionScheme can contain other IRreactionSet objects
		if(scheme.getReactionSchemeCount() != 0)
    		for(IReactionScheme schemeInt : scheme.reactionSchemes()){
                for (IReaction reaction : getAllReactions(schemeInt).reactions())
                    reactionSet.addReaction(reaction);
    		}
        for (IReaction reaction : scheme.reactions())
        	reactionSet.addReaction(reaction);
        
            
	    
	    return reactionSet;
    }

    /**
     * Create a IReactionScheme give a IReactionSet object.
     * 
     * @param  reactionSet The IReactionSet
     * @return             The IReactionScheme
     */
    @TestMethod("testCreateReactionScheme_IReactionSet")
	public static IReactionScheme createReactionScheme(IReactionSet reactionSet) {
        IReactionScheme reactionScheme = reactionSet.getBuilder().newReactionScheme();
        
        // Looking for those reactants which doesn't have any precursor. They are the top.
        ArrayList<IReaction> listTopR = new ArrayList<IReaction>();
        for (IReaction reaction : reactionSet.reactions()) {
        	if(extractPrecursorReaction(reaction,reactionSet).getReactionCount() == 0)
    			listTopR.add(reaction);
        }
        
        for(IReaction reaction: listTopR){
        	reactionScheme.addReaction(reaction);
        	IReactionScheme newReactionScheme = setScheme(reaction, reactionSet);
        	if(newReactionScheme.getReactionCount() != 0 || newReactionScheme.getReactionSchemeCount() != 0)
        		reactionScheme.add(newReactionScheme);
        }
        return reactionScheme;
    }
    /**
     * Extract a set of Reactions which are in top of a IReactionScheme. The top reactions are those
     * which any of their reactants are participating in other reactions as a products.
     * 
     * @param reactionScheme  The IReactionScheme
     * @return                The set of top reactions
     */
    @TestMethod("testExtractTopReactions_IReactionScheme")
	public static IReactionSet extractTopReactions(IReactionScheme reactionScheme) {
    	IReactionSet reactionSet = reactionScheme.getBuilder().newReactionSet();
    	
    	IReactionSet allSet = getAllReactions(reactionScheme);
    	for (IReaction reaction : allSet.reactions()) {
			IReactionSet precuSet = extractPrecursorReaction(reaction,allSet);
    		if(precuSet.getReactionCount() == 0){
    			boolean found = false;
    			for(IReaction reactIn : reactionSet.reactions()){
    				if(reactIn.equals(reaction))
    					found = true;
    			}
    			if(!found)
    				reactionSet.addReaction(reaction);
    		}
    			
    	}
        return reactionSet;
    }
    /**
     * Create a IReactionScheme given as a top a IReaction. If it doesn't exist any subsequent reaction
     * return null;
     * 
     * @param reaction       The IReaction as a top
     * @param reactionSet    The IReactionSet to extract a IReactionScheme
     * @return               The IReactionScheme
     */
    private static IReactionScheme setScheme(IReaction reaction, IReactionSet reactionSet){
    	IReactionScheme reactionScheme = reaction.getBuilder().newReactionScheme();
    	
    	IReactionSet reactConSet = extractSubsequentReaction(reaction, reactionSet);
    	if(reactConSet.getReactionCount() != 0){
    		for (IReaction reactionInt : reactConSet.reactions()) {
        		reactionScheme.addReaction(reactionInt);
        		IReactionScheme newRScheme = setScheme(reactionInt, reactionSet);
        		if(newRScheme.getReactionCount() != 0 || newRScheme.getReactionSchemeCount() != 0){
        			reactionScheme.add(newRScheme);
        		}
    		}  	
    	}
    	return reactionScheme;
    }
    /**
     * Extract reactions from a IReactionSet which at least one product is existing 
     * as reactant given a IReaction
     * 
     * @param reaction    The IReaction to analyze
     * @param reactionSet The IReactionSet to inspect
     * @return            A IReactionSet containing the reactions
     */
    private static IReactionSet extractPrecursorReaction(IReaction reaction, IReactionSet reactionSet){
    	IReactionSet reactConSet = reaction.getBuilder().newReactionSet();
		for (IAtomContainer reactant : reaction.getReactants().molecules()) {
        	for (IReaction reactionInt : reactionSet.reactions()) {
    			for (IAtomContainer precursor : reactionInt.getProducts().molecules()) {
    	        	if(reactant.equals(precursor)){
    	        		reactConSet.addReaction(reactionInt);
    	        	}
    			}
    		}
    	}
		return reactConSet;
    }

    /**
     * Extract reactions from a IReactionSet which at least one reactant is existing 
     * as precursor given a IReaction
     * 
     * @param reaction    The IReaction to analyze
     * @param reactionSet The IReactionSet to inspect
     * @return            A IReactionSet containing the reactions
     */
    private static IReactionSet extractSubsequentReaction(IReaction reaction, IReactionSet reactionSet){
    	IReactionSet reactConSet = reaction.getBuilder().newReactionSet();
		for (IAtomContainer reactant : reaction.getProducts().molecules()) {
        	for (IReaction reactionInt : reactionSet.reactions()) {
    			for (IAtomContainer precursor : reactionInt.getReactants().molecules()) {
    	        	if(reactant.equals(precursor)){
    	        		reactConSet.addReaction(reactionInt);
    	        	}
    			}
    		}
    	}
		return reactConSet;
    }

    /**
     * Extract the list of molecules taking part in the IReactionScheme to originate a 
     * product given a reactant. 
     * 
     * @param origenMol           The start IMolecule
     * @param finalMol            The end IMolecule
     * @param reactionScheme      The IReactionScheme containing the molecules
     * @return                    A List of IMoleculeSet given the path
     */
    @TestMethod("testGetMoleculeSet_IMolecule_IMolecule_IReactionScheme")
	public static ArrayList<IMoleculeSet> getMoleculeSet(IMolecule origenMol, IMolecule finalMol, IReactionScheme reactionScheme) {
    	ArrayList<IMoleculeSet> listPath = new ArrayList<IMoleculeSet>();
    	IReactionSet reactionSet = getAllReactions(reactionScheme);
    	
    	// down search
    	// Looking for those reactants which are the origenMol
    	boolean found = false;
        for (IReaction reaction : reactionSet.reactions()) {
        	if(found)
        		break;
        	for (IAtomContainer reactant : reaction.getReactants().molecules()) {
        		if(found)
            		break;
            	if(reactant.equals(origenMol)){
            		IMoleculeSet allSet = reactionSet.getBuilder().newMoleculeSet();
    	        	// START
    	        	for (IAtomContainer product : reaction.getProducts().molecules()) {
    	        		if(found)
    	            		break;
    	            	if(!product.equals(finalMol)){
    	            		 IMoleculeSet allSet2 = getReactionPath(product,finalMol,reactionSet);
    	    	        	if(allSet2.getAtomContainerCount() != 0){
        	    	        	allSet.addAtomContainer(origenMol);
    	    	        		allSet.addAtomContainer(product);
    	        				allSet.add(allSet2);
    	        			}
    	    	        }else{
    	    	        	allSet.addAtomContainer(origenMol);
    	    	        	allSet.addAtomContainer(product);
    	    	        }
        	            if(allSet.getAtomContainerCount() != 0){
        	            	listPath.add(allSet);
	        	            found = true;
        	            }
    	        	}
    	            
    	            break;
    	        }
        	}
        }
    	// TODO Looking for those products which are the origenMol
        
        // TODO: up search
    	
        return listPath;
    }
	private static IMoleculeSet getReactionPath(IAtomContainer reactant, IMolecule finalMol,IReactionSet reactionSet) {
		IMoleculeSet allSet = reactionSet.getBuilder().newMoleculeSet();
    	for (IReaction reaction : reactionSet.reactions()) {
        	for (IAtomContainer reactant2 : reaction.getReactants().molecules()) {
    	        if(reactant2.equals(reactant)){
	        		for (IAtomContainer product : reaction.getProducts().molecules()) {
	        			if(!product.equals(finalMol)){
	        				IMoleculeSet allSet2 = getReactionPath(product,finalMol,reactionSet);
	        				if(allSet2.getAtomContainerCount() != 0){
	        					allSet.addAtomContainer(reactant);
	        					allSet.add(allSet2);
	        				}
		    	        }else{
		    	        	allSet.addAtomContainer(product);
    	    	        	return allSet;
    	    	        }
    	        	}
    	        	
    	        }
        	}
        }
    	return allSet;
	}
}
