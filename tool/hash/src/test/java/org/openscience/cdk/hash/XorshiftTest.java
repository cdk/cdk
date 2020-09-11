/* Copyright (c) 2013. John May <jwmay@users.sf.net>
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
package org.openscience.cdk.hash;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class XorshiftTest {

    private final Xorshift generator = new Xorshift();

    @Test
    public void testNext() throws Exception {
        assertThat(generator.next(5L), is(178258005L));
        assertThat(generator.next(178258005L), is(5651489766934405L));
        assertThat(generator.next(5651489766934405L), is(-9127299601691290113L));
        assertThat(generator.next(-9127299601691290113L), is(146455018630021125L));
        assertThat(generator.next(146455018630021125L), is(2104002940825447L));
    }

    @Test
    public void testDistribution() throws Exception {

        int[] values = new int[10];

        long x = System.nanoTime();

        // fill the buckets (0..10)
        for (int i = 0; i < 1000000; i++) {
            // mask the sign bit and take the modulus, standard hash table
            values[(int) ((0x7FFFFFFFFFFFL & (x = generator.next(x))) % 10)]++;
        }

        for (int v : values) {
            assertTrue(v + " was not within 0.1 % of a uniform distribution", 99000 <= v && v <= 101000);
        }
    }

    /**
     * demonstrates a limitation of the xor-shift, 0 will always return 0
     */
    @Test
    public void demonstrateZeroLimitation() {
        assertThat(new Xorshift().next(0L), is(0L));
    }
}
