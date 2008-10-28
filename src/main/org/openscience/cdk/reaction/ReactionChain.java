/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *  $Revision: 5855 $
 *
 *  Copyright (C) 2006-2007  Miguel Rojas <miguelrojasch@yahoo.es>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.reaction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IReaction;


/**
 * Classes that extends the definition of reaction to a chain reaction. 
 * This is designed to contains a set of reactions which are lineal linked as
 * chain. That would mean no exist branches or cycles and in this concept
 * you have a start reaction and final reaction. Each reaction is included
 * in a step of the chain. 
 *
 * @author      miguelrojasch <miguelrojasch@yahoo.es>
 * @cdk.module  reaction
 */
@TestClass(value="org.openscience.cdk.reaction.ReactionChainTest")
public class ReactionChain extends ReactionSet{

	HashMap<IReaction, Integer> hashMapChain = new HashMap<IReaction,Integer>();
	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 5006986269373043994L;
	
	/**
	 * Added a IReaction for this chain in position.
	 *  
	 * @param reaction  The IReaction
	 * @param position  The position in this chain where the reaction is to be inserted
	 */
    @TestMethod(value="testAddReaction_IReaction_int")
	public void addReaction(IReaction reaction, int position){
		hashMapChain.put(reaction, position);
		this.addReaction(reaction);
	}
	/**
	 * Get the position of the reaction into this chain reaction object.
	 * @param reaction The IReaction to look at
	 * @return         The position of the IReaction in this chain
	 */
    @TestMethod(value="testGetReactionStep_IReaction")
	public int getReactionStep(IReaction reaction){
		
		if(hashMapChain.containsKey(reaction))
			return hashMapChain.get(reaction);
		else
			return -1;
	}
	

	/**
	 * Get the reaction of this chain reaction object at the position.
	 * 
	 * @param  position The position of the IReaction in this chain to look for
	 * @return          Reaction The IReaction to look at
	 * 
	 */
    @TestMethod(value="testGetReaction_int")
	public IReaction getReaction(int position){

		if(hashMapChain.containsKey(position))
			return null;
		
		Set<Entry<IReaction, Integer>> entries = hashMapChain.entrySet();
		for(Iterator<Entry<IReaction, Integer>> it = entries.iterator(); it.hasNext();){
			Entry<IReaction, Integer> entry = it.next();
			if(entry.getValue().equals(position))
				return entry.getKey();
		}
		
		return null;
	}
	
}
