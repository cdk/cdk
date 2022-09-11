/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash.stereo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author John May
 * @cdk.module test-hash
 */
class BasicPermutationParityTest {

    private final BasicPermutationParity permutationParity = new BasicPermutationParity(new int[]{0, 1, 2, 3});

    @Test
    void testConstruction_Null() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> {new BasicPermutationParity(null);});
    }

    @Test
    void testConstruction_Empty() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    new BasicPermutationParity(new int[0]);
                                });
    }

    @Test
    void testParity_Even() throws Exception {
        Assertions.assertEquals(1, permutationParity.parity(new long[]{4, 3, 2, 1}));
    }

    @Test
    void testParity_Odd() throws Exception {
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{4, 2, 3, 1}));
    }

    @Test
    void testParity_Even_Negative() throws Exception {
        Assertions.assertEquals(1, permutationParity.parity(new long[]{4, 3, -1, -2}));
    }

    @Test
    void testParity_Odd_Negative() throws Exception {
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{4, -1, 3, -2}));
    }

    @Test
    void testParity_Duplicate() throws Exception {
        Assertions.assertEquals(0, permutationParity.parity(new long[]{4, 3, -1, -1}));
    }

    @Test
    void testParity_All() throws Exception {
        Assertions.assertEquals(1, permutationParity.parity(new long[]{1, 2, 3, 4}));
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{2, 1, 3, 4}));
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{1, 3, 2, 4}));
        Assertions.assertEquals(1, permutationParity.parity(new long[]{3, 1, 2, 4}));
        Assertions.assertEquals(1, permutationParity.parity(new long[]{2, 3, 1, 4}));
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{3, 2, 1, 4}));
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{1, 2, 4, 3}));
        Assertions.assertEquals(1, permutationParity.parity(new long[]{2, 1, 4, 3}));
        Assertions.assertEquals(1, permutationParity.parity(new long[]{1, 4, 2, 3}));
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{4, 1, 2, 3}));
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{2, 4, 1, 3}));
        Assertions.assertEquals(1, permutationParity.parity(new long[]{4, 2, 1, 3}));
        Assertions.assertEquals(1, permutationParity.parity(new long[]{1, 3, 4, 2}));
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{3, 1, 4, 2}));
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{1, 4, 3, 2}));
        Assertions.assertEquals(1, permutationParity.parity(new long[]{4, 1, 3, 2}));
        Assertions.assertEquals(1, permutationParity.parity(new long[]{3, 4, 1, 2}));
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{4, 3, 1, 2}));
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{2, 3, 4, 1}));
        Assertions.assertEquals(1, permutationParity.parity(new long[]{3, 2, 4, 1}));
        Assertions.assertEquals(1, permutationParity.parity(new long[]{2, 4, 3, 1}));
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{4, 2, 3, 1}));
        Assertions.assertEquals(-1, permutationParity.parity(new long[]{3, 4, 2, 1}));
        Assertions.assertEquals(1, permutationParity.parity(new long[]{4, 3, 2, 1}));

    }

}
