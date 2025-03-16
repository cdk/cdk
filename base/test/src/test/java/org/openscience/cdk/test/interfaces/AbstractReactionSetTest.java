/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IReactionSet} implementations.
 *
 */
public abstract class AbstractReactionSetTest extends AbstractChemObjectTest {

    @Test
    @Override
    public void testClone() throws Exception {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        Object clone = reactionSet.clone();
        Assertions.assertTrue(clone instanceof IReactionSet);
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
        Assertions.assertEquals(6, reactionSet.getReactionCount());
    }

    @Test
    public void testRemoveAllReactions() {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class));
        reactionSet.removeAllReactions();
        Assertions.assertEquals(0, reactionSet.getReactionCount());
    }

    @Test
    public void testReactions() {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 1
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 2
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 3
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 4

        Iterator<IReaction> reactionIter = reactionSet.reactions().iterator();
        Assertions.assertNotNull(reactionIter);
        int count = 0;

        while (reactionIter.hasNext()) {
            Assertions.assertNotNull(reactionIter.next());
            ++count;
        }
        Assertions.assertEquals(4, count);
    }

    @Test
    public void testGetReaction_int() {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 1
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 2
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 3
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 4

        for (int i = 0; i < reactionSet.getReactionCount(); i++) {
            Assertions.assertNotNull(reactionSet.getReaction(i));
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

        Assertions.assertEquals(4, reactionSet.getReactionCount());
        Assertions.assertEquals(third, reactionSet.getReaction(2));
    }

    @Test
    public void testRemoveReaction_int() {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 1
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 2
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 3
        Assertions.assertEquals(3, reactionSet.getReactionCount());
        reactionSet.removeReaction(1);
        Assertions.assertEquals(2, reactionSet.getReactionCount());
        Assertions.assertNotNull(reactionSet.getReaction(0));
        Assertions.assertNotNull(reactionSet.getReaction(1));
    }

    @Test
    public void testClone_Reaction() throws Exception {
        IReactionSet reactionSet = (IReactionSet) newChemObject();
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 1
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 2
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 3
        reactionSet.addReaction(reactionSet.getBuilder().newInstance(IReaction.class)); // 4

        IReactionSet clone = (IReactionSet) reactionSet.clone();
        Assertions.assertEquals(reactionSet.getReactionCount(), clone.getReactionCount());
        for (int f = 0; f < reactionSet.getReactionCount(); f++) {
            for (int g = 0; g < clone.getReactionCount(); g++) {
                Assertions.assertNotNull(reactionSet.getReaction(f));
                Assertions.assertNotNull(clone.getReaction(g));
                Assertions.assertNotSame(reactionSet.getReaction(f), clone.getReaction(g));
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
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }

        IReaction reaction = reactionSet.getBuilder().newInstance(IReaction.class);
        reactionSet.addReaction(reaction);
        description = reactionSet.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    @Override
    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IReactionSet chemObject = (IReactionSet) newChemObject();
        chemObject.addListener(listener);

        chemObject.addReaction(chemObject.getBuilder().newInstance(IReaction.class));
        Assertions.assertTrue(listener.changed);

        listener.reset();
        Assertions.assertFalse(listener.changed);

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
        Assertions.assertEquals(2, reactionSet.getReactionCount());
        reactionSet.removeReaction(relevantReaction);
        Assertions.assertEquals(1, reactionSet.getReactionCount());
        Assertions.assertEquals("1", reactionSet.getReaction(0).getID());
        reactionSet.addReaction(relevantReaction);
        reactionSet.addReaction(relevantReaction);
        Assertions.assertEquals(3, reactionSet.getReactionCount());
        reactionSet.removeReaction(relevantReaction);
        Assertions.assertEquals(1, reactionSet.getReactionCount());
        Assertions.assertEquals("1", reactionSet.getReaction(0).getID());
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

        Assertions.assertTrue(set.isEmpty(), "new reaction set should be empty");

        set.addReaction(set.getBuilder().newInstance(IReaction.class));

        Assertions.assertFalse(set.isEmpty(), "reaction set with a single reaction should not be empty");

        set.removeAllReactions();

        Assertions.assertTrue(set.isEmpty(), "reaction set with all reactions removed should be empty");

    }

}
