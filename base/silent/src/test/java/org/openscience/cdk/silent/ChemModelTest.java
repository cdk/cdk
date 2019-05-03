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
package org.openscience.cdk.silent;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.AbstractChemModelTest;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the {@link ChemModel}.
 *
 * @cdk.module test-silent
 */
public class ChemModelTest extends AbstractChemModelTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new ChemModel();
            }
        });
    }

    @Test
    public void testChemModel() {
        IChemModel chemModel = new ChemModel();
        Assert.assertNotNull(chemModel);
    }

    // Overwrite default methods: no notifications are expected!

    @Test
    @Override
    public void testNotifyChanged() {
        ChemObjectTestHelper.testNotifyChanged(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_IChemObjectChangeEvent() {
        ChemObjectTestHelper.testNotifyChanged_IChemObjectChangeEvent(newChemObject());
    }

    @Test
    @Override
    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectTestHelper.testStateChanged_IChemObjectChangeEvent(newChemObject());
    }

    @Test
    @Override
    public void testClone_ChemObjectListeners() throws Exception {
        ChemObjectTestHelper.testClone_ChemObjectListeners(newChemObject());
    }

    @Test
    @Override
    public void testAddListener_IChemObjectListener() {
        ChemObjectTestHelper.testAddListener_IChemObjectListener(newChemObject());
    }

    @Test
    @Override
    public void testGetListenerCount() {
        ChemObjectTestHelper.testGetListenerCount(newChemObject());
    }

    @Test
    @Override
    public void testRemoveListener_IChemObjectListener() {
        ChemObjectTestHelper.testRemoveListener_IChemObjectListener(newChemObject());
    }

    @Test
    @Override
    public void testSetNotification_true() {
        ChemObjectTestHelper.testSetNotification_true(newChemObject());
    }

    @Test
    @Override
    public void testStateChanged_EventPropagation_Crystal() {
        ChemObjectListener listener = new ChemObjectListener();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        ICrystal crystal = chemObject.getBuilder().newInstance(ICrystal.class);
        chemObject.setCrystal(crystal);
        Assert.assertFalse(listener.getChanged());
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.getChanged());
        // changing the set should trigger a change event in the IChemModel
        crystal.add(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Override
    @Test
    public void testStateChanged_EventPropagation_AtomContainerSet() {
        ChemObjectListener listener = new ChemObjectListener();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IAtomContainerSet molSet = chemObject.getBuilder().newInstance(IAtomContainerSet.class);
        chemObject.setMoleculeSet(molSet);
        Assert.assertFalse(listener.getChanged());
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.getChanged());
        // changing the set should trigger a change event in the IChemModel
        molSet.addAtomContainer(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test
    @Override
    public void testStateChanged_EventPropagation_ReactionSet() {
        ChemObjectListener listener = new ChemObjectListener();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IReactionSet reactionSet = chemObject.getBuilder().newInstance(IReactionSet.class);
        chemObject.setReactionSet(reactionSet);
        Assert.assertFalse(listener.getChanged());
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.getChanged());
        // changing the set should trigger a change event in the IChemModel
        reactionSet.addReaction(chemObject.getBuilder().newInstance(IReaction.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test
    @Override
    public void testStateChanged_EventPropagation_RingSet() {
        ChemObjectListener listener = new ChemObjectListener();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IRingSet ringSet = chemObject.getBuilder().newInstance(IRingSet.class);
        chemObject.setRingSet(ringSet);
        Assert.assertFalse(listener.getChanged());
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.getChanged());
        // changing the set should trigger a change event in the IChemModel
        ringSet.addAtomContainer(chemObject.getBuilder().newInstance(IRing.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test
    @Override
    public void testStateChanged_ButNotAfterRemoval_Crystal() {
        ChemObjectListener listener = new ChemObjectListener();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        ICrystal crystal = chemObject.getBuilder().newInstance(ICrystal.class);
        chemObject.setCrystal(crystal);
        Assert.assertFalse(listener.getChanged());
        // remove the set from the IChemModel
        chemObject.setCrystal(null);
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.getChanged());
        // changing the set must *not* trigger a change event in the IChemModel
        crystal.add(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Override
    @Test
    public void testStateChanged_ButNotAfterRemoval_AtomContainerSet() {
        ChemObjectListener listener = new ChemObjectListener();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IAtomContainerSet molSet = chemObject.getBuilder().newInstance(IAtomContainerSet.class);
        chemObject.setMoleculeSet(molSet);
        Assert.assertFalse(listener.getChanged());
        // remove the set from the IChemModel
        chemObject.setMoleculeSet(null);
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.getChanged());
        // changing the set must *not* trigger a change event in the IChemModel
        molSet.addAtomContainer(chemObject.getBuilder().newInstance(IAtomContainer.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test
    @Override
    public void testStateChanged_ButNotAfterRemoval_ReactionSet() {
        ChemObjectListener listener = new ChemObjectListener();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IReactionSet reactionSet = chemObject.getBuilder().newInstance(IReactionSet.class);
        chemObject.setReactionSet(reactionSet);
        Assert.assertFalse(listener.getChanged());
        // remove the set from the IChemModel
        chemObject.setReactionSet(null);
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.getChanged());
        // changing the set must *not* trigger a change event in the IChemModel
        reactionSet.addReaction(chemObject.getBuilder().newInstance(IReaction.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test
    @Override
    public void testStateChanged_ButNotAfterRemoval_RingSet() {
        ChemObjectListener listener = new ChemObjectListener();
        IChemModel chemObject = (IChemModel) newChemObject();
        chemObject.addListener(listener);

        IRingSet ringSet = chemObject.getBuilder().newInstance(IRingSet.class);
        chemObject.setRingSet(ringSet);
        Assert.assertFalse(listener.getChanged());
        // remove the set from the IChemModel
        chemObject.setRingSet(null);
        // reset the listener
        listener.reset();
        Assert.assertFalse(listener.getChanged());
        // changing the set must *not* trigger a change event in the IChemModel
        ringSet.addAtomContainer(chemObject.getBuilder().newInstance(IRing.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test
    @Override
    public void testNotifyChanged_SetProperty() {
        ChemObjectTestHelper.testNotifyChanged_SetProperty(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_RemoveProperty() {
        ChemObjectTestHelper.testNotifyChanged_RemoveProperty(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_SetFlag() {
        ChemObjectTestHelper.testNotifyChanged_SetFlag(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_SetFlags() {
        ChemObjectTestHelper.testNotifyChanged_SetFlags(newChemObject());
    }
}
