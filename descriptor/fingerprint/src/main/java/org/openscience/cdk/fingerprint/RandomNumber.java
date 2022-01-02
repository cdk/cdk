/*
 * Copyright (C) 2012   Syed Asad Rahman <asad@ebi.ac.uk>
 *
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
 *
 * Original algorithm from commons-math3.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openscience.cdk.fingerprint;

import java.io.Serializable;

/**
 * Generates pseudorandom numbers using the MersenneTwister adapted from commons-math3.
 * Previously we included commons-math3 as a dependency but since this was the only usage
 * we have reimplemented the algorithm here.<br/>
 *
 * Note - since chemical fingerprints don't need to be secure a very simple PRNG
 * like XorShift would be simpler and faster:
 *
 * <pre>{@code
 *     long xorshift64(long seed) {
 *         seed = seed ^ seed << 21;
 *         seed = seed ^ seed >>> 35;
 *         return seed ^ seed << 4;
 *     }
 * }</pre>
 *
 * @author Syed Asad Rahman (2012)
 * @author John Mayfield
 * @cdk.keyword fingerprint
 * @cdk.keyword similarity
 */
final class RandomNumber implements Serializable {

    private static final int N = 624;
    private static final int M = 397;
    private static final int[] MAG01 = new int[]{0, -1727483681};
    private int[] mt = new int[624];
    private int mti;

    public RandomNumber() {
    }

    private void setSeed(int seed) {
        int[] seeds = new int[]{0,seed};
        long longMT = 19650218;
        this.mt[0] = (int) longMT;

        for (this.mti = 1; this.mti < 624; ++this.mti) {
            longMT = 1812433253L * (longMT ^ longMT >> 30) + (long) this.mti & 4294967295L;
            this.mt[this.mti] = (int) longMT;
        }

        int i = 1;
        int j = 0;

        int k;
        long l0;
        long l1;
        long l;
        for (k = 624; k != 0; --k) {
            l0 = (long) this.mt[i] & 2147483647L | (this.mt[i] < 0 ? 2147483648L : 0L);
            l1 = (long) this.mt[i - 1] & 2147483647L | (this.mt[i - 1] < 0 ? 2147483648L : 0L);
            l = (l0 ^ (l1 ^ l1 >> 30) * 1664525L) + (long) seeds[j] + (long) j;
            this.mt[i] = (int) (l & 4294967295L);
            ++i;
            ++j;
            if (i >= 624) {
                this.mt[0] = this.mt[623];
                i = 1;
            }

            if (j >= 2) {
                j = 0;
            }
        }

        for (k = 623; k != 0; --k) {
            l0 = (long) this.mt[i] & 2147483647L | (this.mt[i] < 0 ? 2147483648L : 0L);
            l1 = (long) this.mt[i - 1] & 2147483647L | (this.mt[i - 1] < 0 ? 2147483648L : 0L);
            l = (l0 ^ (l1 ^ l1 >> 30) * 1566083941L) - (long) i;
            this.mt[i] = (int) (l & 4294967295L);
            ++i;
            if (i >= 624) {
                this.mt[0] = this.mt[623];
                i = 1;
            }
        }

        this.mt[0] = -2147483648;
    }

    private int next(int bits) {
        int y;
        if (this.mti >= 624) {
            int mtNext = this.mt[0];

            int k;
            int mtCurr;
            for (k = 0; k < 227; ++k) {
                mtCurr = mtNext;
                mtNext = this.mt[k + 1];
                y = mtCurr & -2147483648 | mtNext & 2147483647;
                this.mt[k] = this.mt[k + 397] ^ y >>> 1 ^ MAG01[y & 1];
            }

            for (k = 227; k < 623; ++k) {
                mtCurr = mtNext;
                mtNext = this.mt[k + 1];
                y = mtCurr & -2147483648 | mtNext & 2147483647;
                this.mt[k] = this.mt[k + -227] ^ y >>> 1 ^ MAG01[y & 1];
            }

            y = mtNext & -2147483648 | this.mt[0] & 2147483647;
            this.mt[623] = this.mt[396] ^ y >>> 1 ^ MAG01[y & 1];
            this.mti = 0;
        }

        y = this.mt[this.mti++];
        y ^= y >>> 11;
        y ^= y << 7 & -1658038656;
        y ^= y << 15 & -272236544;
        y ^= y >>> 18;
        return y >>> 32 - bits;
    }

    public int generateMersenneTwisterRandomNumber(int n, int seed) {
        setSeed(seed);

        if ((n & -n) == n) {
            return (int) ((long) n * (long) this.next(31) >> 31);
        } else {
            int bits;
            int val;
            do {
                bits = this.next(31);
                val = bits % n;
            } while (bits - val + (n - 1) < 0);

            return val;
        }
    }
}
