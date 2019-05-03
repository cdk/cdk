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
 * Calculate the permutation parity on a given array of current values.
 *
 * @author John May
 * @cdk.module hash
 * @see <a href="http://en.wikipedia.org/wiki/Parity_of_a_permutation">Parity of
 *      a Permutation, Wikipedia</a>
 * @cdk.githash
 */
abstract class PermutationParity {

    /**
     * Identity parity which always returns 1 (even). This is useful for
     * configurations which do not require ordering, such as, double bonds with
     * implicit hydrogens.
     */
    public static final PermutationParity IDENTITY = new PermutationParity() {

                                                       @Override
                                                       public int parity(long[] current) {
                                                           return 1;
                                                       }
                                                   };

    /**
     * Calculate the permutation parity of a permutation on the current values.
     * The inversion parity counts whether we need to do an odd or even number
     * of swaps to put the values in sorted order. If the values contain
     * duplicates then the parity is returned as 0.
     *
     * @param current current values of invariants
     * @return -1, odd number of swaps, +1, even number of swaps, 0, contains
     *         duplicates
     */
    abstract int parity(long[] current);

}
