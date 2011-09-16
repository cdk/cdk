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
package org.openscience.cdk.silent;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.AbstractMoleculeTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the {@link Molecule}.
 *
 * @cdk.module test-silent
 */
public class MoleculeTest extends AbstractMoleculeTest {

    @BeforeClass public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {
            public IChemObject newTestObject() {
                return new Molecule();
            }
        });
    }

    @Test public void testMolecule() {
        IMolecule m = new Molecule();
        Assert.assertNotNull(m);
    }

    @Test public void testMolecule_int_int_int_int() {
        IMolecule m = new Molecule(5,5,1,1);
        Assert.assertNotNull(m);
        Assert.assertEquals(0, m.getAtomCount());
        Assert.assertEquals(0, m.getBondCount());
        Assert.assertEquals(0, m.getLonePairCount());
        Assert.assertEquals(0, m.getSingleElectronCount());
    }

    @Test public void testMolecule_IAtomContainer() {
        IAtomContainer acetone = new AtomContainer();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        IMolecule m = new Molecule(acetone);
        Assert.assertNotNull(m);
        Assert.assertEquals(4, m.getAtomCount());
        Assert.assertEquals(3, m.getBondCount());
    }

    // Overwrite default methods: no notifications are expected!
    
    @Test public void testNotifyChanged() {
        ChemObjectTestHelper.testNotifyChanged(newChemObject());
    }
    @Test public void testNotifyChanged_SetFlag() {
        ChemObjectTestHelper.testNotifyChanged_SetFlag(newChemObject());
    }
    @Test public void testNotifyChanged_SetFlags() {
        ChemObjectTestHelper.testNotifyChanged_SetFlags(newChemObject());
    }
    @Test public void testNotifyChanged_IChemObjectChangeEvent() {
        ChemObjectTestHelper.testNotifyChanged_IChemObjectChangeEvent(newChemObject());
    }
    @Test public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectTestHelper.testStateChanged_IChemObjectChangeEvent(newChemObject());
    }
    @Test public void testClone_ChemObjectListeners() throws Exception {
        ChemObjectTestHelper.testClone_ChemObjectListeners(newChemObject());
    }
    @Test public void testAddListener_IChemObjectListener() {
        ChemObjectTestHelper.testAddListener_IChemObjectListener(newChemObject());
    }
    @Test public void testGetListenerCount() {
        ChemObjectTestHelper.testGetListenerCount(newChemObject());
    }
    @Test public void testRemoveListener_IChemObjectListener() {
        ChemObjectTestHelper.testRemoveListener_IChemObjectListener(newChemObject());
    }
    @Test public void testSetNotification_true() {
        ChemObjectTestHelper.testSetNotification_true(newChemObject());
    }
    @Test public void testNotifyChanged_SetProperty() {
        ChemObjectTestHelper.testNotifyChanged_SetProperty(newChemObject());
    }
    @Test public void testNotifyChanged_RemoveProperty() {
        ChemObjectTestHelper.testNotifyChanged_RemoveProperty(newChemObject());
    }
    @Test public void testSetAtoms_removeListener() {
        ChemObjectTestHelper.testSetAtoms_removeListener(newChemObject());
    }
}
