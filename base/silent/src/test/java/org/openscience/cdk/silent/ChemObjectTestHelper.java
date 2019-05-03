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
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * Helper class to test the functionality of the NNChemObject.
 *
 * @cdk.module test-silent
 */
public class ChemObjectTestHelper {

    public static void testNotifyChanged(IChemObject chemObject) {
        ChemObjectListener listener = new ChemObjectListener();
        chemObject.addListener(listener);

        chemObject.setID("Changed");
        Assert.assertFalse(listener.getChanged());
    }

    public static void testNotifyChanged_IChemObjectChangeEvent(IChemObject chemObject) {
        ChemObjectListener listener = new ChemObjectListener();
        chemObject.addListener(listener);

        chemObject.setID("Changed");
        Assert.assertNull(listener.getEvent());
    }

    public static void testStateChanged_IChemObjectChangeEvent(IChemObject chemObject) {
        ChemObjectListener listener = new ChemObjectListener();
        chemObject.addListener(listener);

        chemObject.setID("Changed");
        Assert.assertFalse(listener.getChanged());

        listener.reset();
        Assert.assertFalse(listener.getChanged());
        chemObject.setProperty("Changed", "Again");
        Assert.assertFalse(listener.getChanged());

        listener.reset();
        Assert.assertFalse(listener.getChanged());
        chemObject.setFlag(CDKConstants.ISALIPHATIC, true);
        Assert.assertFalse(listener.getChanged());
    }

    public static void testNotifyChanged_SetFlag(IChemObject chemObject) {
        ChemObjectListener listener = new ChemObjectListener();
        chemObject.addListener(listener);

        Assert.assertFalse(listener.getChanged());
        chemObject.setFlag(CDKConstants.DUMMY_POINTER, true);
        Assert.assertFalse(listener.getChanged());
    }

    public static void testNotifyChanged_SetFlags(IChemObject chemObject) {
        ChemObjectListener listener = new ChemObjectListener();
        chemObject.addListener(listener);

        Assert.assertFalse(listener.getChanged());
        chemObject.setFlags(new boolean[chemObject.getFlags().length]);
        Assert.assertFalse(listener.getChanged());
    }

    public static void testClone_ChemObjectListeners(IChemObject chemObject) throws Exception {
        ChemObjectListener listener = new ChemObjectListener();
        chemObject.addListener(listener);
        IChemObject chemObject2 = (IChemObject) chemObject.clone();

        // test lack of cloning of listeners
        Assert.assertEquals(0, chemObject.getListenerCount());
        Assert.assertEquals(0, chemObject2.getListenerCount());
    }

    public static void testAddListener_IChemObjectListener(IChemObject chemObject) {
        Assert.assertEquals(0, chemObject.getListenerCount());
        ChemObjectListener listener = new ChemObjectListener();
        chemObject.addListener(listener);
        Assert.assertEquals(0, chemObject.getListenerCount());
    }

    public static void testGetListenerCount(IChemObject chemObject) {
        ChemObjectListener listener = new ChemObjectListener();
        chemObject.addListener(listener);
        Assert.assertEquals(0, chemObject.getListenerCount());
    }

    public static void testRemoveListener_IChemObjectListener(IChemObject chemObject) {
        ChemObjectListener listener = new ChemObjectListener();
        chemObject.addListener(listener);
        Assert.assertEquals(0, chemObject.getListenerCount());
        chemObject.removeListener(listener);
        Assert.assertEquals(0, chemObject.getListenerCount());
    }

    public static void testSetNotification_true(IChemObject chemObject) {
        ChemObjectListener listener = new ChemObjectListener();
        chemObject.addListener(listener);
        chemObject.setNotification(true);

        chemObject.setID("Changed");
        Assert.assertFalse(listener.getChanged());
    }

    public static void testNotifyChanged_SetProperty(IChemObject chemObject) {
        ChemObjectListener listener = new ChemObjectListener();
        chemObject.addListener(listener);

        chemObject.setProperty("Changed", "Yes");
        Assert.assertFalse(listener.getChanged());
    }

    public static void testNotifyChanged_RemoveProperty(IChemObject chemObject) {
        chemObject.setProperty("Changed", "Yes");

        ChemObjectListener listener = new ChemObjectListener();
        chemObject.addListener(listener);

        chemObject.removeProperty("Changed");
        Assert.assertFalse(listener.getChanged());
    }

    public static void testSetAtoms_removeListener(IChemObject newChemObject) {
        IAtomContainer container = (IAtomContainer) newChemObject;

        IAtom[] atoms = new IAtom[4];
        atoms[0] = container.getBuilder().newInstance(IAtom.class, "C");
        atoms[1] = container.getBuilder().newInstance(IAtom.class, "C");
        atoms[2] = container.getBuilder().newInstance(IAtom.class, "C");
        atoms[3] = container.getBuilder().newInstance(IAtom.class, "O");
        container.setAtoms(atoms);

        // if an atom changes, the atomcontainer will throw a change event too
        ChemObjectListener listener = new ChemObjectListener();
        container.addListener(listener);
        Assert.assertFalse(listener.getChanged());

        // ok, change the atom, and make sure we do get an event
        atoms[0].setAtomTypeName("C.sp2");
        Assert.assertFalse(listener.getChanged());

        // reset the listener, overwrite the atoms, and change an old atom.
        // if all is well, we should not get a change event this time
        listener.reset();
        Assert.assertFalse(listener.getChanged()); // make sure the reset worked
        container.setAtoms(new IAtom[0]);
        atoms[1].setAtomTypeName("C.sp2"); // make a change to an old atom
        Assert.assertFalse(listener.getChanged()); // but no change event should happen
    }
}
