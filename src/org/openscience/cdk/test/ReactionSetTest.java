/* $RCSfile$
 * $Author: egonw $    
 * $Date: 2006-07-31 11:23:24 +0200 (Mon, 31 Jul 2006) $    
 * $Revision: 6710 $
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
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;

/**
 * Checks the funcitonality of the ReactionSet class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.ReactionSet
 */
public class ReactionSetTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public ReactionSetTest(String name) {
        super(name);
    }

    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(ReactionSetTest.class);
    }
    
    public void testReactionSet() {
        IReactionSet reactionSet = builder.newReactionSet();
        assertNotNull(reactionSet);
    }
    
	public void testClone() throws Exception {
        IReactionSet reactionSet = builder.newReactionSet();
        Object clone = reactionSet.clone();
        assertTrue(clone instanceof IReactionSet);
    }    
        
    public void testGetReactionCount() {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		reactionSet.addReaction(builder.newReaction()); // 3
		reactionSet.addReaction(builder.newReaction()); // 4
        assertEquals(4, reactionSet.getReactionCount());
    }
    
    public void testRemoveAllReactions(){
  		IReactionSet reactionSet = builder.newReactionSet();
   		reactionSet.addReaction(builder.newReaction());
   		reactionSet.removeAllReactions();
   		assertEquals(0,reactionSet.getReactionCount());
    }

    public void testReactions() {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		reactionSet.addReaction(builder.newReaction()); // 3
		reactionSet.addReaction(builder.newReaction()); // 4
        
		java.util.Iterator reactionIter = reactionSet.reactions();
        assertNotNull(reactionIter);
        int count = 0;
        
        while (reactionIter.hasNext()) {
            assertNotNull(reactionIter.next());
            ++count;
        }
        assertEquals(4, count);
    }
    
    public void testGetReaction_int() {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		reactionSet.addReaction(builder.newReaction()); // 3
		reactionSet.addReaction(builder.newReaction()); // 4
        
        for (int i=0; i<reactionSet.getReactionCount(); i++) {
            assertNotNull(reactionSet.getReaction(i));
        }
    }
    
    public void testAddReaction_IReaction() {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
        IReaction third = builder.newReaction();
		reactionSet.addReaction(third); // 3
		reactionSet.addReaction(builder.newReaction()); // 4
        
        assertEquals(4, reactionSet.getReactionCount());
        assertEquals(third, reactionSet.getReaction(2));
    }
   
    public void testRemoveReaction_int() {
	IReactionSet reactionSet = builder.newReactionSet();
	reactionSet.addReaction(builder.newReaction()); // 1
        reactionSet.addReaction(builder.newReaction()); // 2
	assertEquals(2, reactionSet.getReactionCount());
	reactionSet.removeReaction(1);
	assertEquals(1, reactionSet.getReactionCount());
    }
    

    
    public void testClone_Reaction() throws Exception {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		reactionSet.addReaction(builder.newReaction()); // 3
		reactionSet.addReaction(builder.newReaction()); // 4

		IReactionSet clone = (IReactionSet)reactionSet.clone();
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
        IReactionSet reactionSet = builder.newReactionSet();
        String description = reactionSet.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IReactionSet chemObject = new org.openscience.cdk.ReactionSet();
        chemObject.addListener(listener);

        chemObject.addReaction(builder.newReaction());
        assertTrue(listener.changed);

        listener.reset();
        assertFalse(listener.changed);
        
    }

    private class ChemObjectListenerImpl implements IChemObjectListener {
        private boolean changed;

        private ChemObjectListenerImpl() {
            changed = false;
        }

        public void stateChanged(IChemObjectChangeEvent e) {
            changed = true;
        }

        public void reset() {
            changed = false;
        }
    }

}
