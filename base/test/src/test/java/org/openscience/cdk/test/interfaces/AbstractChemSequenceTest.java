/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IChemSequence;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IChemSequence} implementations.
 *
 */
public abstract class AbstractChemSequenceTest extends AbstractChemObjectTest {

    @Test
    public void testAddChemModel_IChemModel() {
        IChemSequence cs = (IChemSequence) newChemObject();
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        Assertions.assertEquals(3, cs.getChemModelCount());
    }

    @Test
    public void testRemoveChemModel_int() {
        IChemSequence cs = (IChemSequence) newChemObject();
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        Assertions.assertEquals(3, cs.getChemModelCount());
        cs.removeChemModel(1);
        Assertions.assertEquals(2, cs.getChemModelCount());
    }

    @Test
    public void testGrowChemModelArray() {
        IChemSequence cs = (IChemSequence) newChemObject();
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        Assertions.assertEquals(3, cs.getChemModelCount());
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class)); // this one should enfore array grow
        Assertions.assertEquals(6, cs.getChemModelCount());
    }

    @Test
    public void testGetChemModelCount() {
        IChemSequence cs = (IChemSequence) newChemObject();
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        Assertions.assertEquals(3, cs.getChemModelCount());
    }

    @Test
    public void testGetChemModel_int() {
        IChemSequence cs = (IChemSequence) newChemObject();
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        IChemModel second = cs.getBuilder().newInstance(IChemModel.class);
        cs.addChemModel(second);
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));

        Assertions.assertEquals(second, cs.getChemModel(1));
    }

    @Test
    public void testChemModels() {
        IChemSequence cs = (IChemSequence) newChemObject();
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));
        cs.addChemModel(cs.getBuilder().newInstance(IChemModel.class));

        Assertions.assertEquals(3, cs.getChemModelCount());
        Iterator<IChemModel> models = cs.chemModels().iterator();
        int count = 0;
        while (models.hasNext()) {
            Assertions.assertNotNull(models.next());
            ++count;
        }
        Assertions.assertEquals(3, count);
    }

    /** Test for RFC #9 */
    @Test
    public void testToString() {
        IChemSequence cs = (IChemSequence) newChemObject();
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
        IChemSequence chemObject = (IChemSequence) newChemObject();
        chemObject.addListener(listener);

        chemObject.addChemModel(chemObject.getBuilder().newInstance(IChemModel.class));
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
        IChemSequence sequence = (IChemSequence) newChemObject();
        Object clone = sequence.clone();
        Assertions.assertTrue(clone instanceof IChemSequence);
    }

    @Test
    public void testClone_IChemModel() throws Exception {
        IChemSequence sequence = (IChemSequence) newChemObject();
        sequence.addChemModel(sequence.getBuilder().newInstance(IChemModel.class)); // 1
        sequence.addChemModel(sequence.getBuilder().newInstance(IChemModel.class)); // 2
        sequence.addChemModel(sequence.getBuilder().newInstance(IChemModel.class)); // 3
        sequence.addChemModel(sequence.getBuilder().newInstance(IChemModel.class)); // 4

        IChemSequence clone = (IChemSequence) sequence.clone();
        Assertions.assertEquals(sequence.getChemModelCount(), clone.getChemModelCount());
        for (int f = 0; f < sequence.getChemModelCount(); f++) {
            for (int g = 0; g < clone.getChemModelCount(); g++) {
                Assertions.assertNotNull(sequence.getChemModel(f));
                Assertions.assertNotNull(clone.getChemModel(g));
                Assertions.assertNotSame(sequence.getChemModel(f), clone.getChemModel(g));
            }
        }
    }
}
