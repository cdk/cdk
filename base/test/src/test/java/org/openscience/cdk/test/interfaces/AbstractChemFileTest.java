/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IChemSequence;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IChemFile} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractChemFileTest extends AbstractChemObjectTest {

    @Test
    public void testAddChemSequence_IChemSequence() {
        IChemFile cs = (IChemFile) newChemObject();
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        Assertions.assertEquals(3, cs.getChemSequenceCount());
    }

    @Test
    public void testRemoveChemSequence_int() {
        IChemFile cs = (IChemFile) newChemObject();
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        Assertions.assertEquals(3, cs.getChemSequenceCount());
        cs.removeChemSequence(1);
        Assertions.assertEquals(2, cs.getChemSequenceCount());
    }

    @Test
    public void testGetChemSequence_int() {
        IChemFile cs = (IChemFile) newChemObject();
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        IChemSequence second = cs.getBuilder().newInstance(IChemSequence.class);
        cs.addChemSequence(second);
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        Assertions.assertEquals(second, cs.getChemSequence(1));
    }

    @Test
    public void testGrowChemSequenceArray() {
        IChemFile cs = (IChemFile) newChemObject();
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        Assertions.assertEquals(3, cs.getChemSequenceCount());
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class)); // this one should enfore array grow
        Assertions.assertEquals(6, cs.getChemSequenceCount());
    }

    @Test
    public void testChemSequences() {
        IChemFile cs = (IChemFile) newChemObject();
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));

        Assertions.assertNotNull(cs.chemSequences());
        Assertions.assertEquals(3, cs.getChemSequenceCount());
    }

    @Test
    public void testGetChemSequenceCount() {
        IChemFile cs = (IChemFile) newChemObject();
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));
        cs.addChemSequence(cs.getBuilder().newInstance(IChemSequence.class));

        Assertions.assertEquals(3, cs.getChemSequenceCount());
    }

    /** Test for RFC #9 */
    @Test
    public void testToString() {
        IChemFile cs = (IChemFile) newChemObject();
        String description = cs.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    @Override
    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemFile chemObject = (IChemFile) newChemObject();
        chemObject.addListener(listener);

        chemObject.addChemSequence(chemObject.getBuilder().newInstance(IChemSequence.class));
        Assertions.assertTrue(listener.changed);
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

        void reset() {
            changed = false;
        }
    }

    @Test
    @Override
    public void testClone() throws Exception {
        IChemFile file = (IChemFile) newChemObject();
        Object clone = file.clone();
        Assertions.assertTrue(clone instanceof IChemFile);
    }

    @Test
    public void testClone_ChemSequence() throws Exception {
        IChemFile file = (IChemFile) newChemObject();
        file.addChemSequence(file.getBuilder().newInstance(IChemSequence.class)); // 1
        file.addChemSequence(file.getBuilder().newInstance(IChemSequence.class)); // 2
        file.addChemSequence(file.getBuilder().newInstance(IChemSequence.class)); // 3
        file.addChemSequence(file.getBuilder().newInstance(IChemSequence.class)); // 4

        IChemFile clone = (IChemFile) file.clone();
        Assertions.assertEquals(file.getChemSequenceCount(), clone.getChemSequenceCount());
        for (int f = 0; f < file.getChemSequenceCount(); f++) {
            for (int g = 0; g < clone.getChemSequenceCount(); g++) {
                Assertions.assertNotNull(file.getChemSequence(f));
                Assertions.assertNotNull(clone.getChemSequence(g));
                Assertions.assertNotSame(file.getChemSequence(f), clone.getChemSequence(g));
            }
        }
    }
}
