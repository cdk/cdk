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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.AbstractPseudoAtomTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the {@link PseudoAtom}.
 *
 * @cdk.module test-silent
 */
public class PseudoAtomTest extends AbstractPseudoAtomTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new PseudoAtom();
            }
        });
    }

    @Test
    public void testPseudoAtom() {
        IPseudoAtom a = new PseudoAtom();
        Assert.assertEquals("R", a.getSymbol());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testPseudoAtom_IElement() {
        IElement element = newChemObject().getBuilder().newInstance(IElement.class);
        IPseudoAtom a = new PseudoAtom(element);
        Assert.assertEquals("R", a.getSymbol());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    @Override
    public void testPseudoAtom_IAtom() {
        IAtom element = newChemObject().getBuilder().newInstance(IAtom.class, "C");
        IPseudoAtom a = new PseudoAtom(element);
        Assert.assertEquals("R", a.getSymbol());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testPseudoAtom_String() {
        String label = "Arg255";
        IPseudoAtom a = new PseudoAtom(label);
        Assert.assertEquals("R", a.getSymbol());
        Assert.assertEquals(label, a.getLabel());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testPseudoAtom_String_Point2d() {
        Point2d point = new Point2d(1.0, 2.0);
        String label = "Arg255";
        IPseudoAtom a = new PseudoAtom(label, point);
        Assert.assertEquals("R", a.getSymbol());
        Assert.assertEquals(label, a.getLabel());
        Assert.assertEquals(point, a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testPseudoAtom_String_Point3d() {
        Point3d point = new Point3d(1.0, 2.0, 3.0);
        String label = "Arg255";
        IPseudoAtom a = new PseudoAtom(label, point);
        Assert.assertEquals("R", a.getSymbol());
        Assert.assertEquals(label, a.getLabel());
        Assert.assertEquals(point, a.getPoint3d());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getFractionalPoint3d());
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
}
