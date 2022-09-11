/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.structgen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
class MathToolsTest extends CDKTestCase {

    MathToolsTest() {
        super();
    }

    void testMax_arraydouble() {
        double[] doubles = {2.0, 1.0, 3.0, 5.0, 4.0};
        Assertions.assertEquals(5.0, MathTools.max(doubles), 0.001);
    }

    @Test
    void testMin_arraydouble() {
        double[] doubles = {2.0, 1.0, 3.0, 5.0, 4.0};
        Assertions.assertEquals(1.0, MathTools.min(doubles), 0.001);
    }

    @Test
    void testMax_arrayint() {
        int[] ints = {1, 2, 3, 4, 5};
        Assertions.assertEquals(5, MathTools.max(ints));
    }

    @Test
    void testMin_arrayint() {
        int[] ints = {1, 2, 3, 4, 5};
        Assertions.assertEquals(1, MathTools.min(ints));
    }

    @Test
    void testIsEven_int() {
        Assertions.assertTrue(MathTools.isEven(2));
        Assertions.assertTrue(MathTools.isEven(208));
    }

    @Test
    void testIsOdd_int() {
        Assertions.assertTrue(MathTools.isOdd(1));
        Assertions.assertTrue(MathTools.isOdd(3));
        Assertions.assertTrue(MathTools.isOdd(209));
    }

}
