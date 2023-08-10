/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IChemModel} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractChemModelTest extends AbstractChemObjectTest {

    @Test
    public void testSetMoleculeSet_IAtomContainerSet() {
        IChemModel chemModel = (IChemModel) newChemObject();
        IAtomContainerSet crystal = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
        chemModel.setMoleculeSet(crystal);
        Assertions.assertEquals(crystal, chemModel.getMoleculeSet());
    }

    @Test
    public void testGetMoleculeSet() {
        testSetMoleculeSet_IAtomContainerSet();
    }

    @Test
    public void testSetReactionSet_IReactionSet() {
        IChemModel chemModel = (IChemModel) newChemObject();
        IReactionSet crystal = chemModel.getBuilder().newInstance(IReactionSet.class);
        chemModel.setReactionSet(crystal);
        Assertions.assertEquals(crystal, chemModel.getReactionSet());
    }

    @Test
    public void testGetReactionSet() {
        testSetReactionSet_IReactionSet();
    }

    @Test
    public void testSetRingSet_IRingSet() {
        IChemModel chemModel = (IChemModel) newChemObject();
        IRingSet crystal = chemModel.getBuilder().newInstance(IRingSet.class);
        chemModel.setRingSet(crystal);
        Assertions.assertEquals(crystal, chemModel.getRingSet());
    }

    @Test
    public void testGetRingSet() {
        testSetRingSet_IRingSet();
    }

    @Test
    public void testSetCrystal_ICrystal() {
        IChemModel chemModel = (IChemModel) newChemObject();
        ICrystal crystal = chemModel.getBuilder().newInstance(ICrystal.class);
        chemModel.setCrystal(crystal);
        Assertions.assertEquals(crystal, chemModel.getCrystal());
    }

    @Test
    public void testGetCrystal() {
        testSetCrystal_ICrystal();
    }

    @Test
    public void testToString() {
        IChemModel model = (IChemModel) newChemObject();
        String description = model.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    @Override
    public void testClone() throws Exception {
        IChemModel model = (IChemModel) newChemObject();
        Object clone = model.clone();
        Assertions.assertNotNull(clone);
        Assertions.assertTrue(clone instanceof IChemModel);
    }

    @Test
    public void testClone_IAtomContainerSet() throws Exception {
        IChemModel model = (IChemModel) newChemObject();
        IChemModel clone = (IChemModel) model.clone();
        Assertions.assertNull(clone.getMoleculeSet());

        model.setMoleculeSet(model.getBuilder().newInstance(IAtomContainerSet.class));
        clone = (IChemModel) model.clone();
        Assertions.assertNotNull(clone.getMoleculeSet());
        Assertions.assertNotSame(model.getMoleculeSet(), clone.getMoleculeSet());
    }

    @Test
    public void testClone_IReactionSet() throws Exception {
        IChemModel model = (IChemModel) newChemObject();
        IChemModel clone = (IChemModel) model.clone();
        Assertions.assertNull(clone.getReactionSet());

        model.setReactionSet(model.getBuilder().newInstance(IReactionSet.class));
        clone = (IChemModel) model.clone();
        Assertions.assertNotNull(clone.getReactionSet());
        Assertions.assertNotSame(model.getReactionSet(), clone.getReactionSet());
    }

    @Test
    public void testClone_Crystal() throws Exception {
        IChemModel model = (IChemModel) newChemObject();
        IChemModel clone = (IChemModel) model.clone();
        Assertions.assertNull(clone.getCrystal());

        model.setCrystal(model.getBuilder().newInstance(ICrystal.class));
        clone = (IChemModel) model.clone();
        Assertions.assertNotNull(clone.getCrystal());
        Assertions.assertNotSame(model.getCrystal(), clone.getCrystal());
    }

    @Test
    public void testClone_RingSet() throws Exception {
        IChemModel model = (IChemModel) newChemObject();
        IChemModel clone = (IChemModel) model.clone();
        Assertions.assertNull(clone.getRingSet());

        model.setRingSet(model.getBuilder().newInstance(IRingSet.class));
        clone = (IChemModel) model.clone();
        Assertions.assertNotNull(clone.getRingSet());
        Assertions.assertNotSame(model.getRingSet(), clone.getRingSet());
    }

    @Test
    @Override
    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        chemObject.setMoleculeSet(chemObject.getBuilder().newInstance(IAtomContainerSet.class));
        Assertions.assertTrue(listener.changed);

        listener.reset();
        Assertions.assertFalse(listener.changed);
        chemObject.setReactionSet(chemObject.getBuilder().newInstance(IReactionSet.class));
        Assertions.assertTrue(listener.changed);

        listener.reset();
        Assertions.assertFalse(listener.changed);
        chemObject.setCrystal(chemObject.getBuilder().newInstance(ICrystal.class));
        Assertions.assertTrue(listener.changed);

        listener.reset();
        Assertions.assertFalse(listener.changed);
        chemObject.setRingSet(chemObject.getBuilder().newInstance(IRingSet.class));
        Assertions.assertTrue(listener.changed);
    }

    @Test
    public void testStateChanged_EventPropagation_Crystal() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        ICrystal crystal = chemObject.getBuilder().newInstance(ICrystal.class);
        chemObject.setCrystal(crystal);
        Assertions.assertTrue(listener.changed);
        // reset the listener
        listener.reset();
        Assertions.assertFalse(listener.changed);
        // changing the set should trigger a change event in the IChemModel
        crystal.add(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assertions.assertTrue(listener.changed);
    }

    @Test
    public void testStateChanged_EventPropagation_AtomContainerSet() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IAtomContainerSet molSet = chemObject.getBuilder().newInstance(IAtomContainerSet.class);
        chemObject.setMoleculeSet(molSet);
        Assertions.assertTrue(listener.changed);
        // reset the listener
        listener.reset();
        Assertions.assertFalse(listener.changed);
        // changing the set should trigger a change event in the IChemModel
        molSet.addAtomContainer(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assertions.assertTrue(listener.changed);
    }

    @Test
    public void testStateChanged_EventPropagation_ReactionSet() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IReactionSet reactionSet = chemObject.getBuilder().newInstance(IReactionSet.class);
        chemObject.setReactionSet(reactionSet);
        Assertions.assertTrue(listener.changed);
        // reset the listener
        listener.reset();
        Assertions.assertFalse(listener.changed);
        // changing the set should trigger a change event in the IChemModel
        reactionSet.addReaction(chemObject.getBuilder().newInstance(IReaction.class));
        Assertions.assertTrue(listener.changed);
    }

    @Test
    public void testStateChanged_EventPropagation_RingSet() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IRingSet ringSet = chemObject.getBuilder().newInstance(IRingSet.class);
        chemObject.setRingSet(ringSet);
        Assertions.assertTrue(listener.changed);
        // reset the listener
        listener.reset();
        Assertions.assertFalse(listener.changed);
        // changing the set should trigger a change event in the IChemModel
        ringSet.addAtomContainer(chemObject.getBuilder().newInstance(IRing.class));
        Assertions.assertTrue(listener.changed);
    }

    @Test
    public void testStateChanged_ButNotAfterRemoval_Crystal() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        ICrystal crystal = chemObject.getBuilder().newInstance(ICrystal.class);
        chemObject.setCrystal(crystal);
        Assertions.assertTrue(listener.changed);
        // remove the set from the IChemModel
        chemObject.setCrystal(null);
        // reset the listener
        listener.reset();
        Assertions.assertFalse(listener.changed);
        // changing the set must *not* trigger a change event in the IChemModel
        crystal.add(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assertions.assertFalse(listener.changed);
    }

    @Test
    public void testStateChanged_ButNotAfterRemoval_AtomContainerSet() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IAtomContainerSet molSet = chemObject.getBuilder().newInstance(IAtomContainerSet.class);
        chemObject.setMoleculeSet(molSet);
        Assertions.assertTrue(listener.changed);
        // remove the set from the IChemModel
        chemObject.setMoleculeSet(null);
        // reset the listener
        listener.reset();
        Assertions.assertFalse(listener.changed);
        // changing the set must *not* trigger a change event in the IChemModel
        molSet.addAtomContainer(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assertions.assertFalse(listener.changed);
    }

    @Test
    public void testStateChanged_ButNotAfterRemoval_ReactionSet() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IReactionSet reactionSet = chemObject.getBuilder().newInstance(IReactionSet.class);
        chemObject.setReactionSet(reactionSet);
        Assertions.assertTrue(listener.changed);
        // remove the set from the IChemModel
        chemObject.setReactionSet(null);
        // reset the listener
        listener.reset();
        Assertions.assertFalse(listener.changed);
        // changing the set must *not* trigger a change event in the IChemModel
        reactionSet.addReaction(chemObject.getBuilder().newInstance(IReaction.class));
        Assertions.assertFalse(listener.changed);
    }

    @Test
    public void testStateChanged_ButNotAfterRemoval_RingSet() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IRingSet ringSet = chemObject.getBuilder().newInstance(IRingSet.class);
        chemObject.setRingSet(ringSet);
        Assertions.assertTrue(listener.changed);
        // remove the set from the IChemModel
        chemObject.setRingSet(null);
        // reset the listener
        listener.reset();
        Assertions.assertFalse(listener.changed);
        // changing the set must *not* trigger a change event in the IChemModel
        ringSet.addAtomContainer(chemObject.getBuilder().newInstance(IRing.class));
        Assertions.assertFalse(listener.changed);
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
    public void testIsEmpty() {
        IChemModel chemModel = (IChemModel) newChemObject();
        Assertions.assertTrue(chemModel.isEmpty(), "new chem model is empty");
    }

    @Test
    public void testIsEmpty_MoleculeSet() {

        IChemModel chemModel = (IChemModel) newChemObject();
        IChemObjectBuilder builder = chemModel.getBuilder();

        Assertions.assertNotNull(chemModel);
        Assertions.assertTrue(chemModel.isEmpty());

        IAtom atom = builder.newInstance(IAtom.class);
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtomContainerSet mset = builder.newInstance(IAtomContainerSet.class);

        mol.addAtom(atom);
        mset.addAtomContainer(mol);
        chemModel.setMoleculeSet(mset);
        Assertions.assertFalse(chemModel.isEmpty(), "chem model with a molecule set should not be empty");
        mol.removeAtomOnly(atom);
        Assertions.assertFalse(chemModel.isEmpty(), "chem model with a (empty) molecule set should not be empty");
        chemModel.setMoleculeSet(null);
        Assertions.assertTrue(chemModel.isEmpty(), "chemo model with no molecule set should be empty");
    }

    @Test
    public void testIsEmpty_ReactionSet() {

        IChemModel model = (IChemModel) newChemObject();
        IChemObjectBuilder builder = model.getBuilder();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        IReaction reaction = builder.newInstance(IReaction.class);

        reaction.addReactant(molecule);

        IReactionSet set = builder.newInstance(IReactionSet.class);
        model.setReactionSet(set);
        Assertions.assertTrue(model.isEmpty(), "model has an empty reaction set and should be empty");
        set.addReaction(reaction);
        Assertions.assertFalse(model.isEmpty(), "model has a reaction set and should not be empty");
        model.setReactionSet(null);
        Assertions.assertTrue(model.isEmpty(), "model has no reaction set");

    }

    @Test
    public void testIsEmpty_RingSet() {

        IChemModel model = (IChemModel) newChemObject();
        IChemObjectBuilder builder = model.getBuilder();

        IAtomContainer container = builder.newInstance(IAtomContainer.class);
        IRingSet ringset = builder.newInstance(IRingSet.class);

        Assertions.assertTrue(model.isEmpty());
        model.setRingSet(ringset);
        Assertions.assertTrue(model.isEmpty());
        ringset.addAtomContainer(container);
        Assertions.assertFalse(model.isEmpty());
        model.setRingSet(null);
        Assertions.assertTrue(model.isEmpty());

    }

    @Test
    public void testIsEmpty_Crystal() {

        IChemModel model = (IChemModel) newChemObject();
        IChemObjectBuilder builder = model.getBuilder();

        ICrystal crystal = builder.newInstance(ICrystal.class);
        model.setCrystal(crystal);
        Assertions.assertTrue(model.isEmpty());
        crystal.addAtom(builder.newInstance(IAtom.class, "C"));
        Assertions.assertFalse(model.isEmpty());
        model.setCrystal(null);
        Assertions.assertTrue(model.isEmpty());

    }

}
