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
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk;


/** 
 * A set of reactions, for example those taking part in a reaction.
 *
 * To retrieve the reactions from the set, there are two options:
 *
 * <pre>
 * Reaction[] reactions = setOfReactions.getReactions();
 * for (int i=0; i < reactions.length; i++) {
 *     Reaction reaction = reactions[i];
 * }
 * </pre>
 *
 * and
 *
 * <pre>
 * for (int i=0; i < setOfReactions.getReactionCount(); i++) {
 *    Reaction reaction = setOfReactions.getReaction(i);
 * }
 * </pre>
 *
 * @cdkPackage core
 *
 * @cdk.keyword reaction
 * @cdk.keyword reaction
 */
public class SetOfReactions extends ChemObject implements java.io.Serializable, Cloneable
{

	/**
	 *  Array of Reactions.
	 */
	protected Reaction[] reactions;
	
	/**
	 *  Number of Reactions contained by this container.
	 */
	protected int reactionCount;

	/**
	 *  Amount by which the Reactions array grows when elements are added and
	 *  the array is not large enough for that. 
	 */
	protected int growArraySize = 5;


	/**
	 *  Constructs an empty SetOfReactions.
	 */
	public SetOfReactions() {
		reactionCount = 0;
		reactions = new Reaction[growArraySize];
	}


	
	/**
	 *  Adds an reaction to this container.
	 *
	 * @param  reaction  The reaction to be added to this container 
	 */
	public void addReaction(Reaction reaction) {
		if (reactionCount + 1 >= reactions.length) growReactionArray();
		reactions[reactionCount] = reaction;
		reactionCount++;
	}


	/**
	 *  Returns the array of Reactions of this container.
	 *
	 * @return    The array of Reactions of this container 
	 */
	public Reaction[] getReactions() {
        Reaction[] result = new Reaction[reactionCount];
        for (int i=0; i < reactionCount; i++) {
            result[i] = reactions[i];
        }
		return result;
	}
	
	/**
	 *  Grows the reaction array by a given size.
	 *
	 * @see    growArraySize
	 */
	protected void growReactionArray() {
		growArraySize = reactions.length;
		Reaction[] newreactions = new Reaction[reactions.length + growArraySize];
		System.arraycopy(reactions, 0, newreactions, 0, reactions.length);
		reactions = newreactions;
	}
	

	/**
	 * Returns the number of Reactions in this Container.
	 *
	 * @return     The number of Reactions in this Container
	 */
	public int getReactionCount() {
		return this.reactionCount;
	}

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SetOfReactions(");
        buffer.append(this.hashCode() + ", ");
        buffer.append("R=" + getReactionCount() + ", ");
        Reaction[] reactions = getReactions();
        for (int i=0; i<reactions.length; i++) {
            buffer.append(reactions[i].toString());
        }
        buffer.append(")");
        return buffer.toString();
    }
}
