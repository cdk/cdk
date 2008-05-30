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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;

/**
 * @cdk.module test-diff
 */
public class AbstractChemObjectDiffTest {

    @Test public void testDiffIBondOrderFields() {
        String result = LocalChemObjectDiffer.diff("SomeInteger", IBond.Order.DOUBLE, IBond.Order.SINGLE);
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        Assert.assertTrue(result.contains("DOUBLE"));
        Assert.assertTrue(result.contains("SINGLE"));
    }

    @Test public void testDiffIOrderFieldsNoDiff() {
        String result = LocalChemObjectDiffer.diff("SomeInteger", IBond.Order.SINGLE, IBond.Order.SINGLE);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length());
    }

    @Test public void testDiffIAtomTypeHybridizationFields() {
        String result = LocalChemObjectDiffer.diff("SomeInteger", IAtomType.Hybridization.PLANAR3, IAtomType.Hybridization.SP3);
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
