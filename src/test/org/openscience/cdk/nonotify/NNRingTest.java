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
import org.openscience.cdk.interfaces.AbstractRingTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the {@link NNRing}.
 *
 * @cdk.module test-nonotify
 */
public class NNRingTest extends AbstractRingTest {

    @BeforeClass public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {
            public IChemObject newTestObject() {
                return new NNRing();
            }
        });
    }

    @Test public void testNNRing_int_String() {
        IRing r = new NNRing(5, "C");
        Assert.assertEquals(5, r.getAtomCount());
        Assert.assertEquals(5, r.getBondCount());
    }
    
    @Test public void testNNRing_int() {
        IRing r = new NNRing(5); // This does not create a ring!
        Assert.assertEquals(0, r.getAtomCount());
        Assert.assertEquals(0, r.getBondCount());
    }
    
    @Test public void testNNRing() {
        IRing ring = new NNRing();
        Assert.assertNotNull(ring);
        Assert.assertEquals(0, ring.getAtomCount());
        Assert.assertEquals(0, ring.getBondCount());
    }

    @Test public void testNNRing_IAtomContainer() {
        IAtomContainer container = newChemObject().getBuilder().newInstance(IAtomContainer.class);
        container.addAtom(container.getBuilder().newInstance(IAtom.class,"C"));
        container.addAtom(container.getBuilder().newInstance(IAtom.class,"C"));
        
        IRing ring = new NNRing(container);
        Assert.assertNotNull(ring);
        Assert.assertEquals(2, ring.getAtomCount());
        Assert.assertEquals(0, ring.getBondCount());
    }

    // Overwrite default methods: no notifications are expected!
    
    @Test public void testNotifyChanged() {
        NNChemObjectTestHelper.testNotifyChanged(newChemObject());
    }
    @Test public void testNotifyChanged_SetFlag() {
        NNChemObjectTestHelper.testNotifyChanged_SetFlag(newChemObject());
    }
    @Test public void testNotifyChanged_SetFlags() {
        NNChemObjectTestHelper.testNotifyChanged_SetFlags(newChemObject());
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
    @Test public void testNotifyChanged_SetProperty() {
        NNChemObjectTestHelper.testNotifyChanged_SetProperty(newChemObject());
    }
    @Test public void testNotifyChanged_RemoveProperty() {
        NNChemObjectTestHelper.testNotifyChanged_RemoveProperty(newChemObject());
    }
    @Test public void testSetAtoms_removeListener() {
        NNChemObjectTestHelper.testSetAtoms_removeListener(newChemObject());
    }
}