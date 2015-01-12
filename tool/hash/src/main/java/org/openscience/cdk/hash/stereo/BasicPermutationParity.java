/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
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


/**
 * A basic implementation suitable for determining the parity of the indicates a
 * provided sub-array.
 *
 * @author John May
 * @cdk.module hash
 * @cdk.githash
 */
final class BasicPermutationParity extends PermutationParity {

    private final int[] indices;

    /**
     * Create a permutation parity for the provided indices.
     *
     * @param indices sub-array of indices
     * @throws NullPointerException     the provided indices were null
     * @throws IllegalArgumentException less then two indices provided
     */
    public BasicPermutationParity(int[] indices) {
        if (indices == null) throw new NullPointerException("no indices[] provided");
        if (indices.length < 2)
            throw new IllegalArgumentException("at least 2 incides required,"
                    + "use PermutationParity.IDENTITY for single neighbors");
        this.indices = indices;
    }

    /**
     * The number values to check is typically small ({@literal < 5}) and thus
     * we use brute-force to count the number of inversions.
     *
     * @inheritDoc
     */
    @Override
    public int parity(long[] current) {

        int count = 0;

        for (int i = 0, n = indices.length; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int cmp = compare(current[indices[i]], current[indices[j]]);
                if (cmp == 0)
                    return 0;
                else if (cmp > 0) count++;
            }
        }

        // value is odd, -1 or value is even +1
        return Integer.lowestOneBit(count) == 1 ? -1 : +1;
    }

    // TODO is CDK on JDK 7? this can be replaced with Long.compare(long, long)
    private static int compare(long a, long b) {
        return a > b ? +1 : a < b ? -1 : 0;
    }

}
