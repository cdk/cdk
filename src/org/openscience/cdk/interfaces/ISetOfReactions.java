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
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.interfaces;


/** 
 * A set of reactions, for example those taking part in a reaction.
 *
 * @cdk.module  interfaces
 *
 * @cdk.keyword reaction
 */
public interface ISetOfReactions extends IChemObject {

	/**
	 * Adds an reaction to this container.
	 *
	 * @param  reaction  The reaction to be added to this container 
	 */
	public void addReaction(IReaction reaction);
    
    /**
	 * Returns the Reaction at position <code>number</code> in the
	 * container.
	 *
	 * @param  number  The position of the Reaction to be returned
	 * @return         The Reaction at position <code>number</code>
	 */
    public IReaction getReaction(int number);

	/**
	 * Returns the array of Reactions of this container.
	 *
	 * @return    The array of Reactions of this container 
	 */
	public IReaction[] getReactions();
	
	/**
	 * Returns the number of Reactions in this Container.
	 *
	 * @return     The number of Reactions in this Container
	 */
	public int getReactionCount();
	
	/**
	 * Removes all reactions from this set.
	 */
	public void removeAllReactions();

}
