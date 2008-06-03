/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools.diff;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;

/**
 * @cdk.module test-diff
 */
public class AbstractChemObjectDiffTest {

    @Test public void testDiffPoint2dFields() {
        Point2d p2d1 = new Point2d();
        p2d1.x = 0.0;
        p2d1.y = 1.0;
        Point2d p2d2 = new Point2d();
        p2d2.x = 2.0;
        p2d2.y = 3.0;
        String result = LocalChemObjectDiffer.diff("SomePoint2d", p2d1, p2d2);
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        Assert.assertTrue(result.contains("0.0"));
        Assert.assertTrue(result.contains("1.0"));
        Assert.assertTrue(result.contains("2.0"));
        Assert.assertTrue(result.contains("3.0"));
    }

    @Test public void testDiffPoint2dFieldsNoDiff() {
        Point2d p2d1 = new Point2d();
        p2d1.x = 0.0;
        p2d1.y = 1.0;
        Point2d p2d2 = new Point2d();
        p2d2.x = 0.0;
        p2d2.y = 1.0;
        String result = LocalChemObjectDiffer.diff("SomePoint2d", p2d1, p2d2);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length());
    }

    @Test public void testDiffPoint3dFields() {
        Point3d p3d1 = new Point3d();
        p3d1.x = 0.0;
        p3d1.y = 1.0;
        Point3d p3d2 = new Point3d();
        p3d2.x = 2.0;
        p3d2.y = 3.0;
        String result = LocalChemObjectDiffer.diff("SomePoint3d", p3d1, p3d2);
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        Assert.assertTrue(result.contains("0.0"));
        Assert.assertTrue(result.contains("1.0"));
        Assert.assertTrue(result.contains("2.0"));
        Assert.assertTrue(result.contains("3.0"));
    }

    @Test public void testDiffPoint3dFieldsNoDiff() {
        Point3d p3d1 = new Point3d();
        p3d1.x = 0.0;
        p3d1.y = 1.0;
        Point3d p3d2 = new Point3d();
        p3d2.x = 0.0;
        p3d2.y = 1.0;
        String result = LocalChemObjectDiffer.diff("SomePoint3d", p3d1, p3d2);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length());
    }

    @Test public void testDiffIBondOrderFields() {
        String result = LocalChemObjectDiffer.diff("SomeIBond.Order", IBond.Order.DOUBLE, IBond.Order.SINGLE);
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        Assert.assertTrue(result.contains("DOUBLE"));
        Assert.assertTrue(result.contains("SINGLE"));
    }

    @Test public void testDiffIOrderFieldsNoDiff() {
        String result = LocalChemObjectDiffer.diff("SomeIBond.Order", IBond.Order.SINGLE, IBond.Order.SINGLE);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length());
    }

    @Test public void testDiffIAtomTypeHybridizationFields() {
        String result = LocalChemObjectDiffer.diff("SomeIAtomType.Hybridization", IAtomType.Hybridization.PLANAR3, IAtomType.Hybridization.SP3);
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        Assert.assertTrue(result.contains("PLANAR3"));
        Assert.assertTrue(result.contains("SP3"));
    }

    @Test public void testDiffIAtomTypeTypeHybridizationFieldsNoDiff() {
        String result = LocalChemObjectDiffer.diff("SomeInteger", IAtomType.Hybridization.PLANAR3, IAtomType.Hybridization.PLANAR3);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length());
    }

    @Test public void testDiffDoubleFields() {
        String result = LocalChemObjectDiffer.diff("SomeInteger", new Double(5), new Double(5.1));
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        Assert.assertTrue(result.contains("SomeInteger"));
        Assert.assertTrue(result.contains("5.1"));
    }

    @Test public void testDiffDoubleFieldsNoDiff() {
        String result = LocalChemObjectDiffer.diff("SomeInteger", new Double(5.00000000000001), new Double(5.00000000000002));
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length());
    }

    @Test public void testDiffIntegerFields() {
        String result = LocalChemObjectDiffer.diff("SomeInteger", new Integer(5), new Integer(4));
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        Assert.assertTrue(result.contains("SomeInteger"));
        Assert.assertTrue(result.contains("5"));
        Assert.assertTrue(result.contains("4"));
    }

    @Test public void testDiffIntegerFieldsNoDiff() {
        String result = LocalChemObjectDiffer.diff("SomeInteger", new Integer(5), new Integer(5));
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length());
    }

    @Test public void testDiffStringFields() {
        String result = LocalChemObjectDiffer.diff("SomeInteger", "Foo", "Bar");
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        Assert.assertTrue(result.contains("SomeInteger"));
        Assert.assertTrue(result.contains("Foo"));
        Assert.assertTrue(result.contains("Bar"));
    }
    
    @Test public void testDiffStringFieldsNoDiff() {
        String result = LocalChemObjectDiffer.diff("SomeInteger", "Foo", "Foo");
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length());
    }
    
    /**
     * Local extension of the abstract {@link AbstractChemObjectDiff} to allow
     * testing of its methods.
     */
    class LocalChemObjectDiffer extends AbstractChemObjectDiff {}
    
}
