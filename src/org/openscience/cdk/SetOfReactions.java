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
 * @cdk.module data
 *
 * @cdk.keyword reaction
 * @cdk.keyword reaction
 */
public class SetOfReactions extends ChemObject implements java.io.Serializable, org.openscience.cdk.interfaces.ISetOfReactions
{

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 1528749911904585204L;

	/**
	 *  Array of Reactions.
	 */
	private org.openscience.cdk.interfaces.IReaction[] reactions;
	
	/**
	 *  Number of Reactions contained by this container.
	 */
	private int reactionCount;

	/**
	 *  Amount by which the Reactions array grows when elements are added and
	 *  the array is not large enough for that. 
	 */
	private int growArraySize = 5;


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
	public void addReaction(org.openscience.cdk.interfaces.IReaction reaction) {
		if (reactionCount + 1 >= reactions.length) growReactionArray();
		reactions[reactionCount] = reaction;
		reactionCount++;
		notifyChanged();
	}

    
    /**
	 *  
	 * Returns the Reaction at position <code>number</code> in the
	 * container.
	 *
	 * @param  number  The position of the Reaction to be returned
	 * @return         The Reaction at position <code>number</code>
	 */
    public org.openscience.cdk.interfaces.IReaction getReaction(int number) {
        return (Reaction)reactions[number];
    }
    

	/**
	 *  Returns the array of Reactions of this container.
	 *
	 * @return    The array of Reactions of this container 
	 */
	public org.openscience.cdk.interfaces.IReaction[] getReactions() {
        Reaction[] result = new Reaction[reactionCount];
        for (int i=0; i < reactionCount; i++) {
            result[i] = (Reaction)reactions[i];
        }
		return result;
	}
	
	/**
	 *  Grows the reaction array by a given size.
	 *
	 * @see    growArraySize
	 */
	private void growReactionArray() {
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
        buffer.append(this.hashCode()).append(", ");
        buffer.append("R=").append(getReactionCount()).append(", ");
        org.openscience.cdk.interfaces.IReaction[] reactions = getReactions();
        for (int i=0; i<reactions.length; i++) {
            buffer.append(reactions[i].toString());
        }
        buffer.append(")");
        return buffer.toString();
    }

	/**
	 * Clones this <code>SetOfReactions</code> and the contained <code>Reaction</code>s
     * too.
	 *
	 * @return  The cloned SetOfReactions
	 */
	public Object clone() throws CloneNotSupportedException {
		SetOfReactions clone = (SetOfReactions)super.clone();
        // clone the reactions
        clone.reactionCount = this.reactionCount;
		clone.reactions = new Reaction[clone.reactionCount];
		for (int f = 0; f < clone.reactionCount; f++) {
			clone.reactions[f] = (Reaction)((Reaction)reactions[f]).clone();
		}
		return clone;
	}

	/**
	 * Removes all Reactions from this container.
	 */
	public void removeAllReactions() {
		for (int pos = this.reactionCount - 1; pos >= 0; pos--)
		{
			this.reactions[pos] = null;
		}
		this.reactionCount = 0;
		notifyChanged();
	}
}
