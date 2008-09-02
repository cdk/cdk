/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * Checks the functionality of the ReactionSet class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.ReactionSet
 */
public class ReactionSetTest extends NewCDKTestCase {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testReactionSet() {
        IReactionSet reactionSet = builder.newReactionSet();
        Assert.assertNotNull(reactionSet);
    }
    
	@Test public void testClone() throws Exception {
        IReactionSet reactionSet = builder.newReactionSet();
        Object clone = reactionSet.clone();
        Assert.assertTrue(clone instanceof IReactionSet);
    }    
        
    @Test public void testGetReactionCount() {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		reactionSet.addReaction(builder.newReaction()); // 3
		reactionSet.addReaction(builder.newReaction()); // 4
		reactionSet.addReaction(builder.newReaction()); // 5
		reactionSet.addReaction(builder.newReaction()); // 6 (force growing)
        Assert.assertEquals(6, reactionSet.getReactionCount());
    }
    
    @Test public void testRemoveAllReactions(){
  		IReactionSet reactionSet = builder.newReactionSet();
   		reactionSet.addReaction(builder.newReaction());
   		reactionSet.removeAllReactions();
   		Assert.assertEquals(0,reactionSet.getReactionCount());
    }

    @Test public void testReactions() {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		reactionSet.addReaction(builder.newReaction()); // 3
		reactionSet.addReaction(builder.newReaction()); // 4
        
		Iterator<IReaction> reactionIter = reactionSet.reactions().iterator();
        Assert.assertNotNull(reactionIter);
        int count = 0;
        
        while (reactionIter.hasNext()) {
            Assert.assertNotNull(reactionIter.next());
            ++count;
        }
        Assert.assertEquals(4, count);
    }
    
    @Test public void testGetReaction_int() {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		reactionSet.addReaction(builder.newReaction()); // 3
		reactionSet.addReaction(builder.newReaction()); // 4
        
        for (int i=0; i<reactionSet.getReactionCount(); i++) {
            Assert.assertNotNull(reactionSet.getReaction(i));
        }
    }
    
    @Test public void testAddReaction_IReaction() {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
        IReaction third = builder.newReaction();
		reactionSet.addReaction(third); // 3
		reactionSet.addReaction(builder.newReaction()); // 4
        
        Assert.assertEquals(4, reactionSet.getReactionCount());
        Assert.assertEquals(third, reactionSet.getReaction(2));
    }
   
    @Test public void testRemoveReaction_int() {
    	IReactionSet reactionSet = builder.newReactionSet();
    	reactionSet.addReaction(builder.newReaction()); // 1
    	reactionSet.addReaction(builder.newReaction()); // 2
    	reactionSet.addReaction(builder.newReaction()); // 3
    	Assert.assertEquals(3, reactionSet.getReactionCount());
    	reactionSet.removeReaction(1);
    	Assert.assertEquals(2, reactionSet.getReactionCount());
    	Assert.assertNotNull(reactionSet.getReaction(0));
    	Assert.assertNotNull(reactionSet.getReaction(1));
    }
    
    @Test public void testClone_Reaction() throws Exception {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		reactionSet.addReaction(builder.newReaction()); // 3
		reactionSet.addReaction(builder.newReaction()); // 4

		IReactionSet clone = (IReactionSet)reactionSet.clone();
		Assert.assertEquals(reactionSet.getReactionCount(), clone.getReactionCount());
		for (int f = 0; f < reactionSet.getReactionCount(); f++) {
			for (int g = 0; g < clone.getReactionCount(); g++) {
				Assert.assertNotNull(reactionSet.getReaction(f));
				Assert.assertNotNull(clone.getReaction(g));
				Assert.assertNotSame(reactionSet.getReaction(f), clone.getReaction(g));
			}
		}
    }        

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test public void testToString() {
        IReactionSet reactionSet = builder.newReactionSet();
        String description = reactionSet.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
        
        IReaction reaction = builder.newReaction();
        reactionSet.addReaction(reaction);
        description = reactionSet.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IReactionSet chemObject = new org.openscience.cdk.ReactionSet();
        chemObject.addListener(listener);

        chemObject.addReaction(builder.newReaction());
        Assert.assertTrue(listener.changed);

        listener.reset();
        Assert.assertFalse(listener.changed);
        
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
