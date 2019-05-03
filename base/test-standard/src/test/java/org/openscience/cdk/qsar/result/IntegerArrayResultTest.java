/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.qsar.result;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class IntegerArrayResultTest extends CDKTestCase {

    public IntegerArrayResultTest() {
        super();
    }

    @Test
    public void IntegerArrayResult_int() {
        IntegerArrayResult result = new IntegerArrayResult(5);
        Assert.assertNotNull(result);
        Assert.assertEquals(5, result.length());
    }

    @Test
    public void testIntegerArrayResult() {
        IntegerArrayResult result = new IntegerArrayResult();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length());
    }

    @Test
    public void testAdd_int() {
        IntegerArrayResult result = new IntegerArrayResult();
        Assert.assertNotNull(result);
        result.add(5);
        result.add(5);
        result.add(5);
        result.add(5);
        result.add(5);
        Assert.assertEquals(5, result.length());
    }

    @Test
    public void testSize() {
        IntegerArrayResult result = new IntegerArrayResult();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length());
        result.add(5);
        Assert.assertEquals(1, result.length());
    }

    @Test
    public void testToString() {
        IntegerArrayResult result = new IntegerArrayResult();
        Assert.assertNotNull(result);
        Assert.assertEquals("", result.toString());
        result.add(5);
        Assert.assertEquals("5", result.toString());
        result.add(2);
        Assert.assertEquals("5,2", result.toString());
        result.add(-3);
        Assert.assertEquals("5,2,-3", result.toString());
    }

    @Test
    public void testGet_int() {
        IntegerArrayResult result = new IntegerArrayResult();
        Assert.assertNotNull(result);
        Assert.assertEquals("", result.toString());
        result.add(5);
        Assert.assertEquals(5, result.get(0));
        result.add(2);
        Assert.assertEquals(5, result.get(0));
        Assert.assertEquals(2, result.get(1));
        result.add(-1);
        Assert.assertEquals(5, result.get(0));
        Assert.assertEquals(2, result.get(1));
        Assert.assertEquals(-1, result.get(2));
    }

}
