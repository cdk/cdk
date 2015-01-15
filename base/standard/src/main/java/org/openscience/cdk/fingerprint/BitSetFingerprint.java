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

import java.util.BitSet;


/**
 * @author jonalv
 * @cdk.module     standard
 * @cdk.githash
 */
public class BitSetFingerprint implements IBitFingerprint {

    /**
     *
     */
    private static final long serialVersionUID = 9034774011095165227L;
    private BitSet            bitset;

    public BitSetFingerprint(BitSet bitset) {
        this.bitset = bitset;
    }

    public BitSetFingerprint() {
        bitset = new BitSet();
    }

    public BitSetFingerprint(int size) {
        bitset = new BitSet(size);
    }

    public BitSetFingerprint(IBitFingerprint fingerprint) {
        if (fingerprint instanceof BitSetFingerprint) {
            bitset = (BitSet) ((BitSetFingerprint) fingerprint).bitset.clone();
        } else {
            BitSet bitSet = new BitSet();
            for (int i = 0; i < fingerprint.size(); i++) {
                bitSet.set(i, fingerprint.get(i));
            }
            this.bitset = bitSet;
        }
    }

    @Override
    public int cardinality() {
        return bitset.cardinality();
    }

    @Override
    public long size() {
        return bitset.size();
    }

    @Override
    public void and(IBitFingerprint fingerprint) {
        if (bitset.size() != fingerprint.size()) {
            throw new IllegalArgumentException("Fingerprints must have same size");
        }
        if (fingerprint instanceof BitSetFingerprint) {
            bitset.and(((BitSetFingerprint) fingerprint).bitset);
        } else {
            for (int i = 0; i < bitset.size(); i++) {
                bitset.set(i, bitset.get(i) && fingerprint.get(i));
            }
        }
    }

    @Override
    public void or(IBitFingerprint fingerprint) {
        if (bitset.size() != fingerprint.size()) {
            throw new IllegalArgumentException("Fingerprints must have same size");
        }
        if (fingerprint instanceof BitSetFingerprint) {
            bitset.or(((BitSetFingerprint) fingerprint).bitset);
        } else {
            for (int i = 0; i < bitset.size(); i++) {
                bitset.set(i, bitset.get(i) || fingerprint.get(i));
            }
        }
    }

    @Override
    public boolean get(int index) {
        return bitset.get(index);
    }

    @Override
    public void set(int index, boolean b) {
        bitset.set(index, b);
    }

    @Override
    public BitSet asBitSet() {
        return (BitSet) bitset.clone();
    }

    @Override
    public void set(int i) {
        bitset.set(i);
    }

    @Override
    public int hashCode() {
        return bitset.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BitSetFingerprint other = (BitSetFingerprint) obj;
        if (bitset == null) {
            if (other.bitset != null) return false;
        } else if (!bitset.equals(other.bitset)) return false;
        return true;
    }

    @Override
    public int[] getSetbits() {
        int[] result = new int[bitset.cardinality()];
        int index = 0;
        for (int i = 0; i < bitset.length(); i++) {
            if (bitset.get(i)) {
                result[index++] = i;
            }
        }
        return result;
    }
}
