/* Copyright (C) 2011  Jonathan Alvarsson <jonalv@users.sf.net>
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
package org.openscience.cdk.fingerprint;

import java.io.Serializable;
import java.util.BitSet;

/**
 * Interface for bit fingerprint representations.
 *
 * @author jonalv
 * @cdk.module core
 * @cdk.githash
 */
public interface IBitFingerprint extends Serializable {

    /**
     * Returns the number of bits set to true in the fingerprint.
     *
     * @return the number of true bits.
     */
    public int cardinality();

    /**
     * Returns the size of the fingerprint, i.e., the number of hash bins.
     *
     * @return the size of the fingerprint.
     */
    public long size();

    /**
     * Performs a logical <b>AND</b> of the bits in this target bit set with the bits in the
     * argument fingerprint. This fingerprint is modified so that each bit in it has the value
     * <code>true</code> if and only if it both initially had the value <code>true</code> and the
     * corresponding bit in the fingerprint argument also had the value <code>true</code>.
     *
     * @param fingerprint the fingerprint with which to perform the AND operation
     * @throws IllegalArgumentException if the two fingerprints are not of same size
     */
    public void and(IBitFingerprint fingerprint);

    /**
     * Performs a logical <b>OR</b> of the bits in this target bit set with the bits in the argument
     * fingerprint. This operation can also be seen as merging two fingerprints. This fingerprint is
     * modified so that each bit in it has the value <code>true</code> if and only if it either
     * already had the value <code>true</code> or the corresponding bit in the bit set argument has
     * the value <code>true</code>.
     *
     * @param fingerprint the fingerprint with which to perform the OR operation
     * @throws IllegalArgumentException if the two fingerprints are not of same size
     */
    public void or(IBitFingerprint fingerprint);

    /**
     * Returns the value of the bit with the specified index. The value is <code>true</code> if the
     * bit with the index <code>index</code> is currently set in this fingerprint; otherwise, the
     * result is <code>false</code>.
     *
     * @param index the index of the bit to return the value for
     * @return the value of the bit at <code>index</code>
     */
    public boolean get(int index);

    /**
     * Sets the bit at the specified index to the specified value.
     *
     * @param index the index of the bit to change
     * @param value the new value for the bit at position <code>index</code>
     */
    public void set(int index, boolean value);

    /**
     * Returns a <code>BitSet</code> representation of the fingerprint. This might take
     * significantly more memory!
     *
     * @return the fingerprint as a <code>BitSet</code>
     */
    public BitSet asBitSet();

    /**
     * Sets the bit at the specified index to true.
     *
     * @param i index
     */
    public void set(int i);

    /**
     * Returns a listing of the bits in the fingerprint that are set to true.
     *
     * @return listing of all bits that are set
     */
    public int[] getSetbits();
}
