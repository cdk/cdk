/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.BitSet;

/**
 * @author John May
 * @cdk.module test-hash
 */
class SuppressedTest {

    @Test
    void none() throws Exception {
        Suppressed suppressed = Suppressed.none();
        for (int i = 0; i < 1000; i++) {
            Assertions.assertFalse(suppressed.contains(i));
        }
    }

    @Test
    void bitset() throws Exception {
        BitSet set = new BitSet();
        set.set(2);
        set.set(3);
        set.set(5);
        set.set(7);
        set.set(11);
        set.set(42);
        Suppressed suppressed = Suppressed.fromBitSet(set);

        Assertions.assertTrue(suppressed.contains(2));
        Assertions.assertTrue(suppressed.contains(3));
        Assertions.assertTrue(suppressed.contains(5));
        Assertions.assertTrue(suppressed.contains(7));
        Assertions.assertTrue(suppressed.contains(11));
        Assertions.assertTrue(suppressed.contains(42));

        Assertions.assertFalse(suppressed.contains(0));
        Assertions.assertFalse(suppressed.contains(1));
        Assertions.assertFalse(suppressed.contains(4));
        Assertions.assertFalse(suppressed.contains(6));
        Assertions.assertFalse(suppressed.contains(8));
        Assertions.assertFalse(suppressed.contains(9));
        Assertions.assertFalse(suppressed.contains(10));
        Assertions.assertFalse(suppressed.contains(12));
        Assertions.assertFalse(suppressed.contains(13));
        Assertions.assertFalse(suppressed.contains(14));

    }
}
