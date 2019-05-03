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

import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class SuppressedTest {

    @Test
    public void none() throws Exception {
        Suppressed suppressed = Suppressed.none();
        for (int i = 0; i < 1000; i++) {
            assertFalse(suppressed.contains(i));
        }
    }

    @Test
    public void bitset() throws Exception {
        BitSet set = new BitSet();
        set.set(2);
        set.set(3);
        set.set(5);
        set.set(7);
        set.set(11);
        set.set(42);
        Suppressed suppressed = Suppressed.fromBitSet(set);

        assertTrue(suppressed.contains(2));
        assertTrue(suppressed.contains(3));
        assertTrue(suppressed.contains(5));
        assertTrue(suppressed.contains(7));
        assertTrue(suppressed.contains(11));
        assertTrue(suppressed.contains(42));

        assertFalse(suppressed.contains(0));
        assertFalse(suppressed.contains(1));
        assertFalse(suppressed.contains(4));
        assertFalse(suppressed.contains(6));
        assertFalse(suppressed.contains(8));
        assertFalse(suppressed.contains(9));
        assertFalse(suppressed.contains(10));
        assertFalse(suppressed.contains(12));
        assertFalse(suppressed.contains(13));
        assertFalse(suppressed.contains(14));

    }
}
