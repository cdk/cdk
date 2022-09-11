/* Copyright (C) 2012  Gilleain Torrance <gilleain.torrance@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.group;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @author maclean
 * @cdk.module test-group
 *
 */
class PermutationTest extends CDKTestCase {

    @Test
    void sizeNConstructor() {
        int size = 4;
        Permutation p = new Permutation(size);
        for (int index = 0; index < size; index++) {
            Assertions.assertEquals(index, p.get(index));
        }
    }

    @Test
    void valuesConstructor() {
        int[] values = new int[]{1, 0, 3, 2};
        Permutation p = new Permutation(values);
        for (int index = 0; index < p.size(); index++) {
            Assertions.assertEquals(values[index], p.get(index));
        }
    }

    @Test
    void cloneConstructor() {
        int[] values = new int[]{1, 0, 3, 2};
        Permutation a = new Permutation(values);
        Permutation b = new Permutation(a);
        Assertions.assertEquals(a, b);
    }

    @Test
    void equalsTest() {
        Permutation a = new Permutation(1, 2, 0, 3);
        Permutation b = new Permutation(1, 2, 0, 3);
        Assertions.assertEquals(a, b);
    }

    @Test
    void equalsTest_null() {
        Permutation a = new Permutation(1, 2, 0, 3);
        Assertions.assertNotSame(a, null);
    }

    @Test
    void equalsTest_difference() {
        Permutation a = new Permutation(1, 2, 0, 3);
        Permutation b = new Permutation(1, 0, 2, 3);
        Assertions.assertNotSame(a, b);
    }

    @Test
    void isIdentityTest() {
        int size = 4;
        Permutation p = new Permutation(size);
        Assertions.assertTrue(p.isIdentity());
    }

    @Test
    void sizeTest() {
        int size = 4;
        Permutation p = new Permutation(size);
        Assertions.assertEquals(size, p.size());
    }

    @Test
    void getTest() {
        Permutation p = new Permutation(1, 0);
        Assertions.assertEquals(1, p.get(0));
    }

    @Test
    void getValuesTest() {
        int[] values = new int[]{1, 0, 3, 2};
        Permutation p = new Permutation(values);
        Assertions.assertArrayEquals(values, p.getValues());
    }

    @Test
    void firstIndexDiffTest() {
        int[] valuesA = new int[]{1, 0, 3, 2};
        int[] valuesB = new int[]{1, 0, 2, 3};
        Permutation a = new Permutation(valuesA);
        Permutation b = new Permutation(valuesB);
        Assertions.assertEquals(2, a.firstIndexOfDifference(b));
    }

    @Test
    void getOrbitTest() {
        Permutation p = new Permutation(4, 6, 1, 3, 2, 5, 0);
        List<Integer> orbit = p.getOrbit(1);
        Assertions.assertEquals(5, orbit.size());
        Assertions.assertTrue(orbit.contains(1));
    }

    @Test
    void setTest() {
        Permutation p = new Permutation(1, 0);
        p.set(0, 0);
        p.set(1, 1);
        Assertions.assertEquals(0, p.get(0));
        Assertions.assertEquals(1, p.get(1));
    }

    @Test
    void setToTest() {
        int[] values = new int[]{1, 0, 3, 2};
        Permutation a = new Permutation(values);
        Permutation b = new Permutation(values.length);
        a.setTo(b);
        Assertions.assertTrue(a.isIdentity());
    }

    @Test
    void setToTest_differentLength() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Permutation a = new Permutation(1, 0, 2);
            Permutation b = new Permutation(0, 1);
            a.setTo(b);
        });
    }

    @Test
    void multiplyTest() {
        int[] valuesA = new int[]{1, 0, 2, 3};
        int[] valuesB = new int[]{0, 1, 3, 2};
        int[] expectC = new int[]{1, 0, 3, 2};
        Permutation a = new Permutation(valuesA);
        Permutation b = new Permutation(valuesB);
        Permutation c = new Permutation(expectC);
        Assertions.assertEquals(c, a.multiply(b));
    }

    @Test
    void invertTest() {
        int[] values = new int[]{3, 1, 0, 2};
        int[] invert = new int[]{2, 1, 3, 0};
        Permutation p = new Permutation(values);
        Permutation invP = new Permutation(invert);
        Assertions.assertEquals(invP, p.invert());
    }

    @Test
    void toCycleStringTest() {
        int[] values = new int[]{0, 2, 1, 4, 5, 3, 7, 8, 9, 6};
        String expected = "(0)(1, 2)(3, 4, 5)(6, 7, 8, 9)";
        Permutation p = new Permutation(values);
        Assertions.assertEquals(expected, p.toCycleString());
    }

}
