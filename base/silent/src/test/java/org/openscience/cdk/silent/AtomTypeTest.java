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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.test.interfaces.AbstractAtomTypeTest;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.test.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the {@link AtomType}.
 *
 */
class AtomTypeTest extends AbstractAtomTypeTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new AtomType("C");
            }
        });
    }

    @Test
    void testAtomType_String() {
        IAtomType at = new AtomType("C");
        Assertions.assertEquals("C", at.getSymbol());
    }

    @Test
    void testAtomType_IElement() {
        IElement element = new Element("C");
        IAtomType at = element.getBuilder().newInstance(IAtomType.class, element);
        Assertions.assertEquals("C", at.getSymbol());
    }

    @Test
    void testAtomType_String_String() {
        IAtomType at = new AtomType("C4", "C");
        Assertions.assertEquals("C", at.getSymbol());
        Assertions.assertEquals("C4", at.getAtomTypeName());
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
    void testCompare_AtomTypeName() {
        AtomType at1 = new AtomType("C");
        AtomType at2 = new AtomType("C");
        at1.setAtomTypeName("C4");
        at2.setAtomTypeName("C4");
        Assertions.assertTrue(at1.compare(at2));
    }

    @Test
    void testCompare_DiffAtomTypeName() {
        AtomType at1 = new AtomType("C");
        AtomType at2 = new AtomType("C");
        at1.setAtomTypeName("C4");
        at2.setAtomTypeName("C3");
        Assertions.assertFalse(at1.compare(at2));
    }

    @Test
    void testCompare_BondOrderSum() {
        AtomType at1 = new AtomType("C");
        AtomType at2 = new AtomType("C");
        at1.setBondOrderSum(1.5);
        at2.setBondOrderSum(1.5);
        Assertions.assertTrue(at1.compare(at2));
    }

    @Test
    void testCompare_DiffBondOrderSum() {
        AtomType at1 = new AtomType("C");
        AtomType at2 = new AtomType("C");
        at1.setBondOrderSum(1.5);
        at2.setBondOrderSum(2.0);
        Assertions.assertFalse(at1.compare(at2));
    }
}
