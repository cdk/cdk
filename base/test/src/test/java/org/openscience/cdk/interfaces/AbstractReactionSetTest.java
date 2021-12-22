/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.interfaces;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link IReactionSet} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractReactionSetTest extends AbstractChemObjectTest {

    @Test
    @Override
    public void testClone() throws Exception {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        Object clone = reactionSet.clone();
        Assert.assertTrue(clone instanceof IReactionSet);
    }

    @Test
    public void testGetReactionCount() {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 1
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 2
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 3
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 4
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 5
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 6 (force growing)
        Assert.assertEquals(6, reactionSet.getReactionCount());
    }

    @Test
    public void testRemoveAllReactions() {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class));
        reactionSet.removeAllReactions();
        Assert.assertEquals(0, reactionSet.getReactionCount());
    }

    @Test
    public void testReactions() {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 1
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 2
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 3
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 4

        Iterator<IReaction> reactionIter = reactionSet.reactions().iterator();
        Assert.assertNotNull(reactionIter);
        int count = 0;

        while (reactionIter.hasNext()) {
            Assert.assertNotNull(reactionIter.next());
            ++count;
        }
        Assert.assertEquals(4, count);
    }

    @Test
    public void testGetReaction_int() {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 1
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 2
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 3
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 4

        for (int i = 0; i < reactionSet.getReactionCount(); i++) {
            Assert.assertNotNull(reactionSet.getReaction(i));
        }
    }

    @Test
    public void testAddReaction_IReaction() {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 1
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 2
        IReaction third = reactionSet.getBuilder().newInstance(IReaction.class);
        reactionSet.addReaction(third); // 3
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 4

        Assert.assertEquals(4, reactionSet.getReactionCount());
        Assert.assertEquals(third, reactionSet.getReaction(2));
    }

    @Test
    public void testRemoveReaction_int() {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 1
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 2
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 3
        Assert.assertEquals(3, reactionSet.getReactionCount());
        reactionSet.removeReaction(1);
        Assert.assertEquals(2, reactionSet.getReactionCount());
        Assert.assertNotNull(reactionSet.getReaction(0));
        Assert.assertNotNull(reactionSet.getReaction(1));
    }

    @Test
    public void testClone_Reaction() throws Exception {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 1
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 2
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 3
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 4

        IReactionSet clone = (IReactionSet) reactionSet.clone();
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
    @Test
    public void testToString() {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        String description = reactionSet.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }

        IReaction reaction = reactionSet.getBuilder().newInstance(IReaction.class);
        reactionSet.addReaction(reaction);
        description = reactionSet.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    @Override
    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IReactionSet chemObject = (IReactionSet) newChemObject();
        chemObject.addListener(listener);

        chemObject.addReaction(chemObject.getBuilder().newInstance(IReaction.class));
        Assert.assertTrue(listener.changed);

        listener.reset();
        Assert.assertFalse(listener.changed);

    }

    @Test
    public void testRemoveReaction_IReaction() {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        IReaction reaction = reactionSet.getBuilder().newInstance(IReaction.class);
        reaction.setID("1");
        reactionSet.addReaction(reaction);
        IReaction relevantReaction = reactionSet.getBuilder().newInstance(IReaction.class);
        relevantReaction.setID("2");
        reactionSet.addReaction(relevantReaction);
        Assert.assertEquals(2, reactionSet.getReactionCount());
        reactionSet.removeReaction(relevantReaction);
        Assert.assertEquals(1, reactionSet.getReactionCount());
        Assert.assertEquals("1", reactionSet.getReaction(0).getID());
        reactionSet.addReaction(relevantReaction);
        reactionSet.addReaction(relevantReaction);
        Assert.assertEquals(3, reactionSet.getReactionCount());
        reactionSet.removeReaction(relevantReaction);
        Assert.assertEquals(1, reactionSet.getReactionCount());
        Assert.assertEquals("1", reactionSet.getReaction(0).getID());
    }

    private class ChemObjectListenerImpl implements IChemObjectListener {

        private boolean changed;

        private ChemObjectListenerImpl() {
            changed = false;
        }

        @Override
        public void stateChanged(IChemObjectChangeEvent e) {
            changed = true;
        }

        public void reset() {
            changed = false;
        }
    }

    @Test
    public void testIsEmpty() {

        IReactionSet set = (IReactionSet) newChemObject();

        Assert.assertTrue("new reaction set should be empty", set.isEmpty());

        set.addReaction(set.getBuilder().newInstance(IReaction.class));

        Assert.assertFalse("reaction set with a single reaction should not be empty", set.isEmpty());

        set.removeAllReactions();

        Assert.assertTrue("reaction set with all reactions removed should be empty", set.isEmpty());

    }

}
