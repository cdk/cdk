/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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

import java.io.Serializable;
import java.util.Iterator;

import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;

/** 
 * A set of reactions, for example those taking part in a reaction.
 *
 * To retrieve the reactions from the set, there are two options:
 *
 * <pre>
 * Iterator reactions = reactionSet.reactions();
 * while (reactions.hasNext()) {
 *     IReaction reaction = (IReaction)reactions.next();
 * }
 * </pre>
 *
 * and
 *
 * <pre>
 * for (int i=0; i < reactionSet.getReactionCount(); i++) {
 *    IReaction reaction = reactionSet.getReaction(i);
 * }
 * </pre>
 *
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword reaction
 */
public class ReactionSet extends ChemObject implements Serializable, IReactionSet, IChemObjectListener, Cloneable
{

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 1555749911904585204L;

	/**
	 *  Array of Reactions.
	 */
	private IReaction[] reactions;
	
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
	 *  Constructs an empty ReactionSet.
	 */
	public ReactionSet() {
		reactionCount = 0;
		reactions = new Reaction[growArraySize];
	}


	
	/**
	 *  Adds an reaction to this container.
	 *
	 * @param  reaction  The reaction to be added to this container 
	 */
	public void addReaction(IReaction reaction) {
		if (reactionCount + 1 >= reactions.length) growReactionArray();
		reactions[reactionCount] = reaction;
		reactionCount++;
		notifyChanged();
	}

	/**
	 * Remove a reaction from this set.
	 *
	 * @param  pos  The position of the reaction to be removed.
	 */
	public void removeReaction(int pos) {
		reactions[pos].removeListener(this);
		for (int i = pos; i < reactionCount - 1; i++) {
			reactions[i] = reactions[i + 1];
		}
		reactions[reactionCount - 1] = null;
		reactionCount--;
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
    public IReaction getReaction(int number) {
        return (Reaction)reactions[number];
    }
    

    /**
     * Get an iterator for this reaction set.
     * 
     * @return A new Iterator for this ReactionSet.
     */
    public Iterable<IReaction> reactions() {
    	return new Iterable<IReaction>() {
        	public Iterator<IReaction> iterator() {
        		return new ReactionIterator();
        	}
        };
    }
    
    /**
     * The inner Iterator class.
     *
     */
    private class ReactionIterator implements Iterator<IReaction> {

        private int pointer = 0;
    	
        public boolean hasNext() {
            if (pointer < reactionCount) return true;
	    return false;
        }

        public IReaction next() {
            return reactions[pointer++];
        }

        public void remove() {
            removeReaction(--pointer);
        }
    	
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
        StringBuffer buffer = new StringBuffer(32);
        buffer.append("ReactionSet(");
        buffer.append(this.hashCode());
        buffer.append(", R=").append(getReactionCount()).append(", ");
        for (IReaction reaction : reactions()) {
            buffer.append(reaction.toString());
        }
        buffer.append(')');
        return buffer.toString();
    }

	/**
	 * Clones this <code>ReactionSet</code> and the contained <code>Reaction</code>s
     * too.
	 *
	 * @return  The cloned ReactionSet
	 */
	public Object clone() throws CloneNotSupportedException {
		ReactionSet clone = (ReactionSet)super.clone();
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
	
	public void stateChanged(IChemObjectChangeEvent event) {
		notifyChanged(event);
	}



	public void removeReaction(IReaction relevantReaction) {
		for (int i = reactionCount-1; i >= 0; i--) {
			if (reactions[i] == relevantReaction)
				removeReaction(i);
		}
	}
}
