/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 * 
 */
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfReactions;

/**
 * Checks the funcitonality of the SetOfReactions class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.SetOfReactions
 */
public class SetOfReactionsTest extends TestCase {

    public SetOfReactionsTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(SetOfReactionsTest.class);
    }
    
    public void testSetOfReactions() {
        SetOfReactions reactionSet = new SetOfReactions();
        assertNotNull(reactionSet);
    }
    
	public void testClone() {
        SetOfReactions reactionSet = new SetOfReactions();
        Object clone = reactionSet.clone();
        assertTrue(clone instanceof SetOfReactions);
    }    
        
    public void testGetReactionCount() {
		SetOfReactions reactionSet = new SetOfReactions();
		reactionSet.addReaction(new Reaction()); // 1
		reactionSet.addReaction(new Reaction()); // 2
		reactionSet.addReaction(new Reaction()); // 3
		reactionSet.addReaction(new Reaction()); // 4
        assertEquals(4, reactionSet.getReactionCount());
    }

    public void testGetReactions() {
		SetOfReactions reactionSet = new SetOfReactions();
		reactionSet.addReaction(new Reaction()); // 1
		reactionSet.addReaction(new Reaction()); // 2
		reactionSet.addReaction(new Reaction()); // 3
		reactionSet.addReaction(new Reaction()); // 4
        
        Reaction[] reactions = reactionSet.getReactions();
        assertNotNull(reactions);
        assertEquals(4, reactions.length);
        for (int i=0; i<reactions.length; i++) {
            assertNotNull(reactions[i]);
        }
    }
    
    public void testGetReaction_int() {
		SetOfReactions reactionSet = new SetOfReactions();
		reactionSet.addReaction(new Reaction()); // 1
		reactionSet.addReaction(new Reaction()); // 2
		reactionSet.addReaction(new Reaction()); // 3
		reactionSet.addReaction(new Reaction()); // 4
        
        for (int i=0; i<reactionSet.getReactionCount(); i++) {
            assertNotNull(reactionSet.getReaction(i));
        }
    }
    
    public void testAddReaction_Reaction() {
		SetOfReactions reactionSet = new SetOfReactions();
		reactionSet.addReaction(new Reaction()); // 1
		reactionSet.addReaction(new Reaction()); // 2
        Reaction third = new Reaction();
		reactionSet.addReaction(third); // 3
		reactionSet.addReaction(new Reaction()); // 4
        
        assertEquals(4, reactionSet.getReactionCount());
        assertEquals(third, reactionSet.getReaction(2));
    }
    
    public void testClone_Reaction() {
		SetOfReactions reactionSet = new SetOfReactions();
		reactionSet.addReaction(new Reaction()); // 1
		reactionSet.addReaction(new Reaction()); // 2
		reactionSet.addReaction(new Reaction()); // 3
		reactionSet.addReaction(new Reaction()); // 4

		SetOfReactions clone = (SetOfReactions)reactionSet.clone();
		assertEquals(reactionSet.getReactionCount(), clone.getReactionCount());
		for (int f = 0; f < reactionSet.getReactionCount(); f++) {
			for (int g = 0; g < clone.getReactionCount(); g++) {
				assertNotNull(reactionSet.getReaction(f));
				assertNotNull(clone.getReaction(g));
				assertNotSame(reactionSet.getReaction(f), clone.getReaction(g));
			}
		}
    }        

    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        SetOfReactions reactionSet = new SetOfReactions();
        String description = reactionSet.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
