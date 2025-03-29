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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 */
class DoubleArrayResultTest {

    DoubleArrayResultTest() {
        super();
    }

    @Test
    void testDoubleArrayResult_int() {
        DoubleArrayResult result = new DoubleArrayResult(5);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(5, result.length());
    }

    @Test
    void testDoubleArrayResult() {
        DoubleArrayResult result = new DoubleArrayResult();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.length());
    }

    @Test
    void testSize() {
        DoubleArrayResult result = new DoubleArrayResult();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.length());
        result.add(5);
        Assertions.assertEquals(1, result.length());
    }

    @Test
    void testAdd_double() {
        DoubleArrayResult result = new DoubleArrayResult();
        Assertions.assertNotNull(result);
        Assertions.assertEquals("", result.toString());
        result.add(5);
        result.add(2);
        result.add(-3);
        Assertions.assertEquals(3, result.length());
    }

    @Test
    void testToString() {
        DoubleArrayResult result = new DoubleArrayResult();
        Assertions.assertNotNull(result);
        Assertions.assertEquals("", result.toString());
        result.add(5);
        Assertions.assertEquals("5.0", result.toString());
        result.add(2);
        Assertions.assertEquals("5.0,2.0", result.toString());
        result.add(-3);
        Assertions.assertEquals("5.0,2.0,-3.0", result.toString());
    }

    @Test
    void testGet_int() {
        DoubleArrayResult result = new DoubleArrayResult();
        Assertions.assertNotNull(result);
        Assertions.assertEquals("", result.toString());
        result.add(5);
        Assertions.assertEquals(5, result.get(0), 0.000001);
        result.add(2);
        Assertions.assertEquals(5, result.get(0), 0.000001);
        Assertions.assertEquals(2, result.get(1), 0.000001);
        result.add(-1);
        Assertions.assertEquals(5, result.get(0), 0.000001);
        Assertions.assertEquals(2, result.get(1), 0.000001);
        Assertions.assertEquals(-1, result.get(2), 0.000001);
    }

}
