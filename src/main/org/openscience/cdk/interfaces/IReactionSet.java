/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword reaction
 */
public interface IReactionSet extends IChemObject {

	/**
	 * Adds an reaction to this container.
	 *
	 * @param  reaction  The reaction to be added to this container 
	 */
	public void addReaction(IReaction reaction);

	/**
         * Remove a reaction from this set.
         *
         * @param  pos  The position of the reaction to be removed.
         */
        public void removeReaction(int pos);
	
        /**
	 * Returns the Reaction at position <code>number</code> in the
	 * container.
	 *
	 * @param  number  The position of the Reaction to be returned
	 * @return         The Reaction at position <code>number</code>
	 */
        public IReaction getReaction(int number);

	/**
	 * Returns the {@link Iterable} over Reactions of this container.
	 *
	 * @return    The {@link Iterable} over Reactions of this container 
	 */
	public Iterable<IReaction> reactions();
	
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

	/**
     * Removes all instances of a reaction from this IReactionSet.
     * 
     * @param relevantReaction
     */
    public void removeReaction(IReaction relevantReaction);
}
