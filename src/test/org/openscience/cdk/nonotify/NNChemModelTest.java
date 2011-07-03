/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.nonotify;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.AbstractChemModelTest;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the {@link NNChemModel}.
 *
 * @cdk.module test-nonotify
 */
public class NNChemModelTest extends AbstractChemModelTest {

    @BeforeClass public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {
            public IChemObject newTestObject() {
                return new NNChemModel();
            }
        });
    }

    @Test public void testNNChemModel() {
	    IChemModel chemModel = new NNChemModel();
	    Assert.assertNotNull(chemModel);
    }

    // Overwrite default methods: no notifications are expected!
    
    @Test public void testNotifyChanged() {
        NNChemObjectTestHelper.testNotifyChanged(newChemObject());
    }
    @Test public void testNotifyChanged_IChemObjectChangeEvent() {
        NNChemObjectTestHelper.testNotifyChanged_IChemObjectChangeEvent(newChemObject());
    }
    @Test public void testStateChanged_IChemObjectChangeEvent() {
        NNChemObjectTestHelper.testStateChanged_IChemObjectChangeEvent(newChemObject());
    }
    @Test public void testClone_ChemObjectListeners() throws Exception {
        NNChemObjectTestHelper.testClone_ChemObjectListeners(newChemObject());
    }
    @Test public void testAddListener_IChemObjectListener() {
        NNChemObjectTestHelper.testAddListener_IChemObjectListener(newChemObject());
    }
    @Test public void testGetListenerCount() {
        NNChemObjectTestHelper.testGetListenerCount(newChemObject());
    }
    @Test public void testRemoveListener_IChemObjectListener() {
        NNChemObjectTestHelper.testRemoveListener_IChemObjectListener(newChemObject());
    }
    @Test public void testSetNotification_true() {
        NNChemObjectTestHelper.testSetNotification_true(newChemObject());
    }

    @Test public void testStateChanged_EventPropagation_Crystal() {
        NNChemObjectListener listener = new NNChemObjectListener();
        IChemModel chemObject = (IChemModel)newChemObject();
        chemObject.addListener(listener);

        ICrystal crystal = chemObject.getBuilder().newInstance(ICrystal.class);
        chemObject.setCrystal(crystal);
        Assert.assertFalse(listener.getChanged());
        // reset the listener
        listener.reset(); Assert.assertFalse(listener.getChanged());
        // changing the set should trigger a change event in the IChemModel
        crystal.add(chemObject.getBuilder().newInstance(IMolecule.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test public void testStateChanged_EventPropagation_MoleculeSet() {
        NNChemObjectListener listener = new NNChemObjectListener();
        IChemModel chemObject = (IChemModel)newChemObject();
        chemObject.addListener(listener);

        IMoleculeSet molSet = chemObject.getBuilder().newInstance(IMoleculeSet.class);
        chemObject.setMoleculeSet(molSet);
        Assert.assertFalse(listener.getChanged());
        // reset the listener
        listener.reset(); Assert.assertFalse(listener.getChanged());
        // changing the set should trigger a change event in the IChemModel
        molSet.addAtomContainer(chemObject.getBuilder().newInstance(IMolecule.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test public void testStateChanged_EventPropagation_ReactionSet() {
        NNChemObjectListener listener = new NNChemObjectListener();
        IChemModel chemObject = (IChemModel)newChemObject();
        chemObject.addListener(listener);

        IReactionSet reactionSet = chemObject.getBuilder().newInstance(IReactionSet.class);
        chemObject.setReactionSet(reactionSet);
        Assert.assertFalse(listener.getChanged());
        // reset the listener
        listener.reset(); Assert.assertFalse(listener.getChanged());
        // changing the set should trigger a change event in the IChemModel
        reactionSet.addReaction(chemObject.getBuilder().newInstance(IReaction.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test public void testStateChanged_EventPropagation_RingSet() {
        NNChemObjectListener listener = new NNChemObjectListener();
        IChemModel chemObject = (IChemModel)newChemObject();
        chemObject.addListener(listener);

        IRingSet ringSet = chemObject.getBuilder().newInstance(IRingSet.class);
        chemObject.setRingSet(ringSet);
        Assert.assertFalse(listener.getChanged());
        // reset the listener
        listener.reset(); Assert.assertFalse(listener.getChanged());
        // changing the set should trigger a change event in the IChemModel
        ringSet.addAtomContainer(chemObject.getBuilder().newInstance(IRing.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test public void testStateChanged_ButNotAfterRemoval_Crystal() {
        NNChemObjectListener listener = new NNChemObjectListener();
        IChemModel chemObject = (IChemModel)newChemObject();
        chemObject.addListener(listener);

        ICrystal crystal = chemObject.getBuilder().newInstance(ICrystal.class);
        chemObject.setCrystal(crystal);
        Assert.assertFalse(listener.getChanged());
        // remove the set from the IChemModel
        chemObject.setCrystal(null);
        // reset the listener
        listener.reset(); Assert.assertFalse(listener.getChanged());
        // changing the set must *not* trigger a change event in the IChemModel
        crystal.add(chemObject.getBuilder().newInstance(IMolecule.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test public void testStateChanged_ButNotAfterRemoval_MoleculeSet() {
        NNChemObjectListener listener = new NNChemObjectListener();
        IChemModel chemObject = (IChemModel)newChemObject();
        chemObject.addListener(listener);

        IMoleculeSet molSet = chemObject.getBuilder().newInstance(IMoleculeSet.class);
        chemObject.setMoleculeSet(molSet);
        Assert.assertFalse(listener.getChanged());
        // remove the set from the IChemModel
        chemObject.setMoleculeSet(null);
        // reset the listener
        listener.reset(); Assert.assertFalse(listener.getChanged());
        // changing the set must *not* trigger a change event in the IChemModel
        molSet.addAtomContainer(chemObject.getBuilder().newInstance(IMolecule.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test public void testStateChanged_ButNotAfterRemoval_ReactionSet() {
        NNChemObjectListener listener = new NNChemObjectListener();
        IChemModel chemObject = (IChemModel)newChemObject();
        chemObject.addListener(listener);

        IReactionSet reactionSet = chemObject.getBuilder().newInstance(IReactionSet.class);
        chemObject.setReactionSet(reactionSet);
        Assert.assertFalse(listener.getChanged());
        // remove the set from the IChemModel
        chemObject.setReactionSet(null);
        // reset the listener
        listener.reset(); Assert.assertFalse(listener.getChanged());
        // changing the set must *not* trigger a change event in the IChemModel
        reactionSet.addReaction(chemObject.getBuilder().newInstance(IReaction.class));
        Assert.assertFalse(listener.getChanged());
    }

    @Test public void testStateChanged_ButNotAfterRemoval_RingSet() {
        NNChemObjectListener listener = new NNChemObjectListener();
        IChemModel chemObject = (IChemModel)newChemObject();
        chemObject.addListener(listener);

        IRingSet ringSet = chemObject.getBuilder().newInstance(IRingSet.class);
        chemObject.setRingSet(ringSet);
        Assert.assertFalse(listener.getChanged());
        // remove the set from the IChemModel
        chemObject.setRingSet(null);
        // reset the listener
        listener.reset(); Assert.assertFalse(listener.getChanged());
        // changing the set must *not* trigger a change event in the IChemModel
        ringSet.addAtomContainer(chemObject.getBuilder().newInstance(IRing.class));
        Assert.assertFalse(listener.getChanged());
    }
    @Test public void testNotifyChanged_SetProperty() {
        NNChemObjectTestHelper.testNotifyChanged_SetProperty(newChemObject());
    }
    @Test public void testNotifyChanged_RemoveProperty() {
        NNChemObjectTestHelper.testNotifyChanged_RemoveProperty(newChemObject());
    }
    @Test public void testNotifyChanged_SetFlag() {
        NNChemObjectTestHelper.testNotifyChanged_SetFlag(newChemObject());
    }
    @Test public void testNotifyChanged_SetFlags() {
        NNChemObjectTestHelper.testNotifyChanged_SetFlags(newChemObject());
    }
}
