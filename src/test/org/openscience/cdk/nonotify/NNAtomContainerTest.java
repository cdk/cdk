/* $Revision$ $Author$ $Date$    
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
 */
package org.openscience.cdk.nonotify;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.AbstractAtomContainerTest;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;

/**
 * Checks the functionality of the {@link NNAtomContainer}.
 *
 * @cdk.module test-nonotify
 */
public class NNAtomContainerTest extends AbstractAtomContainerTest {

    @BeforeClass public static void setUp() {
    	  setChemObject(new NNAtomContainer());
    }

    @Test public void testNNAtomContainer_int_int_int_int() {
        // create an empty container with predefined
        // array lengths
        IAtomContainer ac = new NNAtomContainer(5,6,1,2);
        
        Assert.assertEquals(0, ac.getAtomCount());
        Assert.assertEquals(0, ac.getElectronContainerCount());

        // test whether the ElectronContainer is correctly initialized
        ac.addBond(ac.getBuilder().newBond(ac.getBuilder().newAtom("C"), ac.getBuilder().newAtom("C"), IBond.Order.DOUBLE));
        ac.addLonePair(ac.getBuilder().newLonePair(ac.getBuilder().newAtom("N")));
    }

    @Test public void testNNAtomContainer() {
        // create an empty container with in the constructor defined array lengths
        IAtomContainer container = new NNAtomContainer();
        
        Assert.assertEquals(0, container.getAtomCount());
        Assert.assertEquals(0, container.getBondCount());
        
        // test whether the ElectronContainer is correctly initialized
        container.addBond(container.getBuilder().newBond(container.getBuilder().newAtom("C"), container.getBuilder().newAtom("C"), IBond.Order.DOUBLE));
        container.addLonePair(container.getBuilder().newLonePair(container.getBuilder().newAtom("N")));
    }

    @Test public void testNNAtomContainer_IAtomContainer() {
        IMolecule acetone = newChemObject().getBuilder().newMolecule();
        IAtom c1 = acetone.getBuilder().newAtom("C");
        IAtom c2 = acetone.getBuilder().newAtom("C");
        IAtom o = acetone.getBuilder().newAtom("O");
        IAtom c3 = acetone.getBuilder().newAtom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newBond(c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newBond(c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newBond(c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        IAtomContainer container = new NNAtomContainer(acetone);
        Assert.assertEquals(4, container.getAtomCount());
        Assert.assertEquals(3, container.getBondCount());
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
}
