/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link IChemModel} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractChemModelTest extends AbstractChemObjectTest {

    @Test
    public void testSetMoleculeSet_IAtomContainerSet() {
        IChemModel chemModel = (IChemModel) newChemObject();
        IAtomContainerSet crystal = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
        chemModel.setMoleculeSet(crystal);
        Assert.assertEquals(crystal, chemModel.getMoleculeSet());
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
        Assert.assertEquals(crystal, chemModel.getReactionSet());
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
        Assert.assertEquals(crystal, chemModel.getRingSet());
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
        Assert.assertEquals(crystal, chemModel.getCrystal());
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
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    @Override
    public void testClone() throws Exception {
        IChemModel model = (IChemModel) newChemObject();
        Object clone = model.clone();
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone instanceof IChemModel);
    }

    @Test
    public void testClone_IAtomContainerSet() throws Exception {
        IChemModel model = (IChemModel) newChemObject();
        IChemModel clone = (IChemModel) model.clone();
        Assert.assertNull(clone.getMoleculeSet());

        model.setMoleculeSet(model.getBuilder().newInstance(IAtomContainerSet.class));
        clone = (IChemModel) model.clone();
        Assert.assertNotNull(clone.getMoleculeSet());
        Assert.assertNotSame(model.getMoleculeSet(), clone.getMoleculeSet());
    }

    @Test
    public void testClone_IReactionSet() throws Exception {
        IChemModel model = (IChemModel) newChemObject();
        IChemModel clone = (IChemModel) model.clone();
        Assert.assertNull(clone.getReactionSet());

        model.setReactionSet(model.getBuilder().newInstance(IReactionSet.class));
        clone = (IChemModel) model.clone();
        Assert.assertNotNull(clone.getReactionSet());
        Assert.assertNotSame(model.getReactionSet(), clone.getReactionSet());
    }

    @Test
    public void testClone_Crystal() throws Exception {
        IChemModel model = (IChemModel) newChemObject();
        IChemModel clone = (IChemModel) model.clone();
        Assert.assertNull(clone.getCrystal());

        model.setCrystal(model.getBuilder().newInstance(ICrystal.class));
        clone = (IChemModel) model.clone();
        Assert.assertNotNull(clone.getCrystal());
        Assert.assertNotSame(model.getCrystal(), clone.getCrystal());
    }

    @Test
    public void testClone_RingSet() throws Exception {
        IChemModel model = (IChemModel) newChemObject();
        IChemModel clone = (IChemModel) model.clone();
        Assert.assertNull(clone.getRingSet());

        model.setRingSet(model.getBuilder().newInstance(IRingSet.class));
        clone = (IChemModel) model.clone();
        Assert.assertNotNull(clone.getRingSet());
        Assert.assertNotSame(model.getRingSet(), clone.getRingSet());
    }

    @Test
    @Override
    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        chemObject.setMoleculeSet(chemObject.getBuilder().newInstance(IAtomContainerSet.class));
        Assert.assertTrue(listener.changed);

        listener.reset();
        Assert.assertFalse(listener.changed);
        chemObject.setReactionSet(chemObject.getBuilder().newInstance(IReactionSet.class));
        Assert.assertTrue(listener.changed);

        listener.reset();
        Assert.assertFalse(listener.changed);
        chemObject.setCrystal(chemObject.getBuilder().newInstance(ICrystal.class));
        Assert.assertTrue(listener.changed);

        listener.reset();
        Assert.assertFalse(listener.changed);
        chemObject.setRingSet(chemObject.getBuilder().newInstance(IRingSet.class));
        Assert.assertTrue(listener.changed);
    }

    @Test
    public void testStateChanged_EventPropagation_Crystal() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        ICrystal crystal = chemObject.getBuilder().newInstance(ICrystal.class);
        chemObject.setCrystal(crystal);
        Assert.assertTrue(listener.changed);
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.changed);
        // changing the set should trigger a change event in the IChemModel
        crystal.add(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assert.assertTrue(listener.changed);
    }

    @Test
    public void testStateChanged_EventPropagation_AtomContainerSet() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IAtomContainerSet molSet = chemObject.getBuilder().newInstance(IAtomContainerSet.class);
        chemObject.setMoleculeSet(molSet);
        Assert.assertTrue(listener.changed);
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.changed);
        // changing the set should trigger a change event in the IChemModel
        molSet.addAtomContainer(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assert.assertTrue(listener.changed);
    }

    @Test
    public void testStateChanged_EventPropagation_ReactionSet() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IReactionSet reactionSet = chemObject.getBuilder().newInstance(IReactionSet.class);
        chemObject.setReactionSet(reactionSet);
        Assert.assertTrue(listener.changed);
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.changed);
        // changing the set should trigger a change event in the IChemModel
        reactionSet.addReaction(chemObject.getBuilder().newInstance(IReaction.class));
        Assert.assertTrue(listener.changed);
    }

    @Test
    public void testStateChanged_EventPropagation_RingSet() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IRingSet ringSet = chemObject.getBuilder().newInstance(IRingSet.class);
        chemObject.setRingSet(ringSet);
        Assert.assertTrue(listener.changed);
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.changed);
        // changing the set should trigger a change event in the IChemModel
        ringSet.addAtomContainer(chemObject.getBuilder().newInstance(IRing.class));
        Assert.assertTrue(listener.changed);
    }

    @Test
    public void testStateChanged_ButNotAfterRemoval_Crystal() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        ICrystal crystal = chemObject.getBuilder().newInstance(ICrystal.class);
        chemObject.setCrystal(crystal);
        Assert.assertTrue(listener.changed);
        // remove the set from the IChemModel
        chemObject.setCrystal(null);
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.changed);
        // changing the set must *not* trigger a change event in the IChemModel
        crystal.add(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assert.assertFalse(listener.changed);
    }

    @Test
    public void testStateChanged_ButNotAfterRemoval_AtomContainerSet() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IAtomContainerSet molSet = chemObject.getBuilder().newInstance(IAtomContainerSet.class);
        chemObject.setMoleculeSet(molSet);
        Assert.assertTrue(listener.changed);
        // remove the set from the IChemModel
        chemObject.setMoleculeSet(null);
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.changed);
        // changing the set must *not* trigger a change event in the IChemModel
        molSet.addAtomContainer(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assert.assertFalse(listener.changed);
    }

    @Test
    public void testStateChanged_ButNotAfterRemoval_ReactionSet() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IReactionSet reactionSet = chemObject.getBuilder().newInstance(IReactionSet.class);
        chemObject.setReactionSet(reactionSet);
        Assert.assertTrue(listener.changed);
        // remove the set from the IChemModel
        chemObject.setReactionSet(null);
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.changed);
        // changing the set must *not* trigger a change event in the IChemModel
        reactionSet.addReaction(chemObject.getBuilder().newInstance(IReaction.class));
        Assert.assertFalse(listener.changed);
    }

    @Test
    public void testStateChanged_ButNotAfterRemoval_RingSet() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IRingSet ringSet = chemObject.getBuilder().newInstance(IRingSet.class);
        chemObject.setRingSet(ringSet);
        Assert.assertTrue(listener.changed);
        // remove the set from the IChemModel
        chemObject.setRingSet(null);
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.changed);
        // changing the set must *not* trigger a change event in the IChemModel
        ringSet.addAtomContainer(chemObject.getBuilder().newInstance(IRing.class));
        Assert.assertFalse(listener.changed);
    }

    private class ChemObjectListenerImpl implements IChemObjectListener {

        private boolean changed;

        private ChemObjectListenerImpl() {
            changed = false;
        }

        @Test
        @Override
        public void stateChanged(IChemObjectChangeEvent e) {
            changed = true;
        }

        @Test
        public void reset() {
            changed = false;
        }
    }

    @Test
    public void testIsEmpty() {
        IChemModel chemModel = (IChemModel) newChemObject();
        Assert.assertTrue("new chem model is empty", chemModel.isEmpty());
    }

    @Test
    public void testIsEmpty_MoleculeSet() {

        IChemModel chemModel = (IChemModel) newChemObject();
        IChemObjectBuilder builder = chemModel.getBuilder();

        Assert.assertNotNull(chemModel);
        Assert.assertTrue(chemModel.isEmpty());

        IAtom atom = builder.newInstance(IAtom.class);
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtomContainerSet mset = builder.newInstance(IAtomContainerSet.class);

        mol.addAtom(atom);
        mset.addAtomContainer(mol);
        chemModel.setMoleculeSet(mset);
        Assert.assertFalse("chem model with a molecule set should not be empty", chemModel.isEmpty());
        mol.removeAtom(atom);
        Assert.assertFalse("chem model with a (empty) molecule set should not be empty", chemModel.isEmpty());
        chemModel.setMoleculeSet(null);
        Assert.assertTrue("chemo model with no molecule set should be empty", chemModel.isEmpty());
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
        Assert.assertTrue("model has an empty reaction set and should be empty", model.isEmpty());
        set.addReaction(reaction);
        Assert.assertFalse("model has a reaction set and should not be empty", model.isEmpty());
        model.setReactionSet(null);
        Assert.assertTrue("model has no reaction set", model.isEmpty());

    }

    @Test
    public void testIsEmpty_RingSet() {

        IChemModel model = (IChemModel) newChemObject();
        IChemObjectBuilder builder = model.getBuilder();

        IAtomContainer container = builder.newInstance(IAtomContainer.class);
        IRingSet ringset = builder.newInstance(IRingSet.class);

        Assert.assertTrue(model.isEmpty());
        model.setRingSet(ringset);
        Assert.assertTrue(model.isEmpty());
        ringset.addAtomContainer(container);
        Assert.assertFalse(model.isEmpty());
        model.setRingSet(null);
        Assert.assertTrue(model.isEmpty());

    }

    @Test
    public void testIsEmpty_Crystal() {

        IChemModel model = (IChemModel) newChemObject();
        IChemObjectBuilder builder = model.getBuilder();

        ICrystal crystal = builder.newInstance(ICrystal.class);
        model.setCrystal(crystal);
        Assert.assertTrue(model.isEmpty());
        crystal.addAtom(builder.newInstance(IAtom.class, "C"));
        Assert.assertFalse(model.isEmpty());
        model.setCrystal(null);
        Assert.assertTrue(model.isEmpty());

    }

}
