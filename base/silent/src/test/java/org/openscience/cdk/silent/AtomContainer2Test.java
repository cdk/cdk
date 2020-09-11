/*
 * Copyright (c) 2017 John Mayfield <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.openscience.cdk.silent;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.interfaces.AbstractAtomContainerTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Checks the functionality of the {@link AtomContainer}.
 *
 * @cdk.module test-silent
 */
public class AtomContainer2Test extends AbstractAtomContainerTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new AtomContainer2();
            }
        });
    }


    @Test
    public void testAtomContainer_int_int_int_int() {
        // create an empty container with predefined
        // array lengths
        IAtomContainer container = new AtomContainer2(5, 6, 1, 2);

        Assert.assertEquals(0, container.getAtomCount());
        Assert.assertEquals(0, container.getElectronContainerCount());

        // test whether the ElectronContainer is correctly initialized
        IAtom c1 = container.getBuilder().newInstance(IAtom.class, "C");
        IAtom c2  = container.getBuilder().newInstance(IAtom.class, "C");
        IAtom n = container.getBuilder().newInstance(IAtom.class, "N");
        container.addAtom(c1);
        container.addAtom(c2);
        container.addAtom(n);
        container.addBond(container.getBuilder().newInstance(IBond.class, c1, c2, IBond.Order.DOUBLE));
        container.addLonePair(container.getBuilder().newInstance(ILonePair.class, n));
    }

    @Test
    public void testAtomContainer() {
        // create an empty container with in the constructor defined array lengths
        IAtomContainer container = new AtomContainer2();

        Assert.assertEquals(0, container.getAtomCount());
        Assert.assertEquals(0, container.getBondCount());

        // test whether the ElectronContainer is correctly initialized
        IAtom c1 = container.getBuilder().newInstance(IAtom.class, "C");
        IAtom c2  = container.getBuilder().newInstance(IAtom.class, "C");
        IAtom n = container.getBuilder().newInstance(IAtom.class, "N");
        container.addAtom(c1);
        container.addAtom(c2);
        container.addAtom(n);
        container.addBond(container.getBuilder().newInstance(IBond.class, c1, c2, IBond.Order.DOUBLE));
        container.addLonePair(container.getBuilder().newInstance(ILonePair.class, n));
    }

    @Test
    public void testAtomContainer_IAtomContainer() {
        IAtomContainer acetone = newChemObject().getBuilder().newInstance(IAtomContainer.class);
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class, "C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class, "O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class, "C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class, c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class, c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class, c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);

        IAtomContainer container = new AtomContainer2(acetone);
        Assert.assertEquals(4, container.getAtomCount());
        Assert.assertEquals(3, container.getBondCount());
    }

    // Overwrite default methods: no notifications are expected!

    @Test
    @Override
    public void testNotifyChanged() {
        ChemObjectTestHelper.testNotifyChanged(newChemObject());
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
    public void testSetAtoms_removeListener() {
        ChemObjectTestHelper.testSetAtoms_removeListener(newChemObject());
    }

    @Test
    public void testAtomAdjacencyOnClone() throws CloneNotSupportedException {
        IAtomContainer org = (IAtomContainer) newChemObject();
        IAtom          a1 = org.getBuilder().newAtom();
        IAtom          a2 = org.getBuilder().newAtom();
        a1.setSymbol("C");
        a2.setSymbol("C");
        org.addAtom(a1);
        org.addAtom(a2);
        org.addBond(0, 1, IBond.Order.SINGLE);
        assertThat(org.getAtom(0).getBondCount(), is(1));
        assertThat(org.getAtom(1).getBondCount(), is(1));
        org.clone();
        assertThat(org.getAtom(0).getBondCount(), is(1));
        assertThat(org.getAtom(1).getBondCount(), is(1));
    }

    @Test
    public void testAtomGetBond() {
        IAtomContainer mol = (IAtomContainer) newChemObject();
        IAtom          a1 = mol.getBuilder().newAtom();
        IAtom          a2 = mol.getBuilder().newAtom();
        IAtom          a3 = mol.getBuilder().newAtom();
        a1.setSymbol("CH3");
        a2.setSymbol("CH2");
        a3.setSymbol("OH");
        mol.addAtom(a1);
        mol.addAtom(a2);
        mol.addAtom(a3);
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        assertThat(mol.getBond(0),
                   is(mol.getAtom(0).getBond(mol.getAtom(1))));
        assertThat(mol.getBond(1),
                   is(mol.getAtom(1).getBond(mol.getAtom(2))));
        assertNull(mol.getAtom(0).getBond(mol.getAtom(2)));
    }
}
