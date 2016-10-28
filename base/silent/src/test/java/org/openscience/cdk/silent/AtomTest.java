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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.AbstractAtomTest;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the {@link Atom}.
 *
 * @cdk.module test-silent
 */
public class AtomTest extends AbstractAtomTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new Atom();
            }
        });
    }

    @Test
    public void testAtom() {
        IAtom a = new Atom();
        Assert.assertNotNull(a);
    }

    @Test
    public void testAtom_IElement() {
        IElement element = newChemObject().getBuilder().newInstance(IElement.class);
        IAtom a = new Atom(element);
        Assert.assertNotNull(a);
    }

    @Test
    public void testAtom_String() {
        IAtom a = new Atom("C");
        Assert.assertEquals("C", a.getSymbol());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testAtom_NH4plus_direct() {
        IAtom a = new Atom(7, 4, +1);
        Assert.assertEquals("N", a.getSymbol());
        Assert.assertEquals((Integer) 7, a.getAtomicNumber());
        Assert.assertEquals((Integer) 4, a.getImplicitHydrogenCount());
        Assert.assertEquals((Integer) 1, a.getFormalCharge());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testAtom_CH3_direct() {
        IAtom a = new Atom(6, 3);
        Assert.assertEquals("C", a.getSymbol());
        Assert.assertEquals((Integer) 6, a.getAtomicNumber());
        Assert.assertEquals((Integer) 3, a.getImplicitHydrogenCount());
        Assert.assertEquals((Integer) 0, a.getFormalCharge());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testAtom_Cl_direct() {
        IAtom a = new Atom(17);
        Assert.assertEquals("Cl", a.getSymbol());
        Assert.assertEquals((Integer) 17, a.getAtomicNumber());
        Assert.assertEquals((Integer) 0, a.getImplicitHydrogenCount());
        Assert.assertEquals((Integer) 0, a.getFormalCharge());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testAtom_NH4plus() {
        IAtom a = new Atom("NH4+");
        Assert.assertEquals("N", a.getSymbol());
        Assert.assertEquals((Integer) 7, a.getAtomicNumber());
        Assert.assertEquals((Integer) 4, a.getImplicitHydrogenCount());
        Assert.assertEquals((Integer) 1, a.getFormalCharge());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testAtom_Ominus() {
        IAtom a = new Atom("O-");
        Assert.assertEquals("O", a.getSymbol());
        Assert.assertEquals((Integer) 8, a.getAtomicNumber());
        Assert.assertEquals((Integer) 0, a.getImplicitHydrogenCount());
        Assert.assertEquals(Integer.valueOf(-1), a.getFormalCharge());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testAtom_Ca2plus() {
        IAtom a = new Atom("Ca+2");
        Assert.assertEquals("Ca", a.getSymbol());
        Assert.assertEquals((Integer) 20, a.getAtomicNumber());
        Assert.assertEquals((Integer) 0, a.getImplicitHydrogenCount());
        Assert.assertEquals(Integer.valueOf(+2), a.getFormalCharge());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testAtom_13CH3() {
        IAtom a = new Atom("13CH3");
        Assert.assertEquals("C", a.getSymbol());
        Assert.assertEquals((Integer) 13, a.getMassNumber());
        Assert.assertEquals((Integer) 6, a.getAtomicNumber());
        Assert.assertEquals((Integer) 3, a.getImplicitHydrogenCount());
        Assert.assertEquals((Integer) 0, a.getFormalCharge());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testAtom_String_Point3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        IAtom a = new Atom("C", point3d);
        Assert.assertEquals("C", a.getSymbol());
        Assert.assertEquals(point3d, a.getPoint3d());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testAtom_String_Point2d() {
        Point2d point2d = new Point2d(1.0, 2.0);

        IAtom a = new Atom("C", point2d);
        Assert.assertEquals("C", a.getSymbol());
        Assert.assertEquals(point2d, a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
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
