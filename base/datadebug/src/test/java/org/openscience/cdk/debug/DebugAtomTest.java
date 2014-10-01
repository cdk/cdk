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
package org.openscience.cdk.debug;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.AbstractAtomTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the {@link DebugAtom}.
 *
 * @cdk.module test-datadebug
 */
public class DebugAtomTest extends AbstractAtomTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new DebugAtom();
            }
        });
    }

    @Test
    public void testDebugAtom() {
        IAtom a = new DebugAtom();
        Assert.assertNotNull(a);
    }

    @Test
    public void testDebugAtom_IElement() {
        IChemObject object = newChemObject();
        IElement element = object.getBuilder().newInstance(IElement.class);
        IAtom a = new DebugAtom(element);
        Assert.assertNotNull(a);
    }

    @Test
    public void testDebugAtom_String() {
        IAtom a = new DebugAtom("C");
        Assert.assertEquals("C", a.getSymbol());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testDebugAtom_String_Point3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        IAtom a = new DebugAtom("C", point3d);
        Assert.assertEquals("C", a.getSymbol());
        Assert.assertEquals(point3d, a.getPoint3d());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testDebugAtom_String_Point2d() {
        Point2d point2d = new Point2d(1.0, 2.0);

        IAtom a = new DebugAtom("C", point2d);
        Assert.assertEquals("C", a.getSymbol());
        Assert.assertEquals(point2d, a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getFractionalPoint3d());
    }
}
