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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * @author maclean
 * @cdk.module test-group
 *
 */
public class PermutationTest extends CDKTestCase {

    @Test
    public void sizeNConstructor() {
        int size = 4;
        Permutation p = new Permutation(size);
        for (int index = 0; index < size; index++) {
            Assert.assertEquals(index, p.get(index));
        }
    }

    @Test
    public void valuesConstructor() {
        int[] values = new int[]{1, 0, 3, 2};
        Permutation p = new Permutation(values);
        for (int index = 0; index < p.size(); index++) {
            Assert.assertEquals(values[index], p.get(index));
        }
    }

    @Test
    public void cloneConstructor() {
        int[] values = new int[]{1, 0, 3, 2};
        Permutation a = new Permutation(values);
        Permutation b = new Permutation(a);
        Assert.assertEquals(a, b);
    }

    @Test
    public void equalsTest() {
        Permutation a = new Permutation(1, 2, 0, 3);
        Permutation b = new Permutation(1, 2, 0, 3);
        Assert.assertEquals(a, b);
    }

    @Test
    public void equalsTest_null() {
        Permutation a = new Permutation(1, 2, 0, 3);
        Assert.assertNotSame(a, null);
    }

    @Test
    public void equalsTest_difference() {
        Permutation a = new Permutation(1, 2, 0, 3);
        Permutation b = new Permutation(1, 0, 2, 3);
        Assert.assertNotSame(a, b);
    }

    @Test
    public void isIdentityTest() {
        int size = 4;
        Permutation p = new Permutation(size);
        Assert.assertTrue(p.isIdentity());
    }

    @Test
    public void sizeTest() {
        int size = 4;
        Permutation p = new Permutation(size);
        Assert.assertEquals(size, p.size());
    }

    @Test
    public void getTest() {
        Permutation p = new Permutation(1, 0);
        Assert.assertEquals(1, p.get(0));
    }

    @Test
    public void getValuesTest() {
        int[] values = new int[]{1, 0, 3, 2};
        Permutation p = new Permutation(values);
        Assert.assertArrayEquals(values, p.getValues());
    }

    @Test
    public void firstIndexDiffTest() {
        int[] valuesA = new int[]{1, 0, 3, 2};
        int[] valuesB = new int[]{1, 0, 2, 3};
        Permutation a = new Permutation(valuesA);
        Permutation b = new Permutation(valuesB);
        Assert.assertEquals(2, a.firstIndexOfDifference(b));
    }

    @Test
    public void getOrbitTest() {
        Permutation p = new Permutation(4, 6, 1, 3, 2, 5, 0);
        List<Integer> orbit = p.getOrbit(1);
        Assert.assertEquals(5, orbit.size());
        Assert.assertTrue(orbit.contains(1));
    }

    @Test
    public void setTest() {
        Permutation p = new Permutation(1, 0);
        p.set(0, 0);
        p.set(1, 1);
        Assert.assertEquals(0, p.get(0));
        Assert.assertEquals(1, p.get(1));
    }

    @Test
    public void setToTest() {
        int[] values = new int[]{1, 0, 3, 2};
        Permutation a = new Permutation(values);
        Permutation b = new Permutation(values.length);
        a.setTo(b);
        Assert.assertTrue(a.isIdentity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setToTest_differentLength() {
        Permutation a = new Permutation(1, 0, 2);
        Permutation b = new Permutation(0, 1);
        a.setTo(b);
    }

    @Test
    public void multiplyTest() {
        int[] valuesA = new int[]{1, 0, 2, 3};
        int[] valuesB = new int[]{0, 1, 3, 2};
        int[] expectC = new int[]{1, 0, 3, 2};
        Permutation a = new Permutation(valuesA);
        Permutation b = new Permutation(valuesB);
        Permutation c = new Permutation(expectC);
        Assert.assertEquals(c, a.multiply(b));
    }

    @Test
    public void invertTest() {
        int[] values = new int[]{3, 1, 0, 2};
        int[] invert = new int[]{2, 1, 3, 0};
        Permutation p = new Permutation(values);
        Permutation invP = new Permutation(invert);
        Assert.assertEquals(invP, p.invert());
    }

    @Test
    public void toCycleStringTest() {
        int[] values = new int[]{0, 2, 1, 4, 5, 3, 7, 8, 9, 6};
        String expected = "(0)(1, 2)(3, 4, 5)(6, 7, 8, 9)";
        Permutation p = new Permutation(values);
        Assert.assertEquals(expected, p.toCycleString());
    }

}
