/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
 * 
 */
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Checks the funcitonality of the SetOfReactions class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.SetOfReactions
 */
public class SetOfReactionsTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public SetOfReactionsTest(String name) {
        super(name);
    }

    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(SetOfReactionsTest.class);
    }
    
    public void testSetOfReactions() {
        ISetOfReactions reactionSet = builder.newSetOfReactions();
        assertNotNull(reactionSet);
    }
    
	public void testClone() throws Exception {
        ISetOfReactions reactionSet = builder.newSetOfReactions();
        Object clone = reactionSet.clone();
        assertTrue(clone instanceof ISetOfReactions);
    }    
        
    public void testGetReactionCount() {
		ISetOfReactions reactionSet = builder.newSetOfReactions();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		reactionSet.addReaction(builder.newReaction()); // 3
		reactionSet.addReaction(builder.newReaction()); // 4
        assertEquals(4, reactionSet.getReactionCount());
    }
    
    public void testRemoveAllReactions(){
  		ISetOfReactions reactionSet = builder.newSetOfReactions();
   		reactionSet.addReaction(builder.newReaction());
   		reactionSet.removeAllReactions();
   		assertEquals(0,reactionSet.getReactions().length);
    }

    public void testGetReactions() {
		ISetOfReactions reactionSet = builder.newSetOfReactions();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		reactionSet.addReaction(builder.newReaction()); // 3
		reactionSet.addReaction(builder.newReaction()); // 4
        
		org.openscience.cdk.interfaces.IReaction[] reactions = reactionSet.getReactions();
        assertNotNull(reactions);
        assertEquals(4, reactions.length);
        for (int i=0; i<reactions.length; i++) {
            assertNotNull(reactions[i]);
        }
    }
    
    public void testGetReaction_int() {
		ISetOfReactions reactionSet = builder.newSetOfReactions();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		reactionSet.addReaction(builder.newReaction()); // 3
		reactionSet.addReaction(builder.newReaction()); // 4
        
        for (int i=0; i<reactionSet.getReactionCount(); i++) {
            assertNotNull(reactionSet.getReaction(i));
        }
    }
    
    public void testAddReaction_IReaction() {
		ISetOfReactions reactionSet = builder.newSetOfReactions();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
        IReaction third = builder.newReaction();
		reactionSet.addReaction(third); // 3
		reactionSet.addReaction(builder.newReaction()); // 4
        
        assertEquals(4, reactionSet.getReactionCount());
        assertEquals(third, reactionSet.getReaction(2));
    }
    
    public void testClone_Reaction() throws Exception {
		ISetOfReactions reactionSet = builder.newSetOfReactions();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		reactionSet.addReaction(builder.newReaction()); // 3
		reactionSet.addReaction(builder.newReaction()); // 4

		ISetOfReactions clone = (ISetOfReactions)reactionSet.clone();
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
        ISetOfReactions reactionSet = builder.newSetOfReactions();
        String description = reactionSet.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
