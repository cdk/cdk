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

import org.openscience.cdk.annotations.TestClass;

/**
 * @author jonalv
 * @cdk.module     standard 
 */
@TestClass("org.openscience.cdk.fingerprint.BitSetFingerprintTest")
public class BitSetFingerprint implements IBitFingerprint {

	private BitSet bitset;
	
	public BitSetFingerprint(BitSet bitset) {
		this.bitset = bitset;
	}
	
	public BitSetFingerprint() {
		bitset = new BitSet();
	}

	public BitSetFingerprint(int size) {
		bitset = new BitSet(size);
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
		if ( bitset.size() != fingerprint.size() ) {
			throw new IllegalArgumentException(
			              "Fingerprints must have same size" );
		}
		if ( fingerprint instanceof BitSetFingerprint ) {
			bitset.and( ((BitSetFingerprint)fingerprint).bitset );
		}
		else {
			for ( int i = 0 ; i<bitset.size() ; i++ ) {
				bitset.set( i, bitset.get(i) && fingerprint.get(i) );
			}
		}
	}

	@Override
	public void or(IBitFingerprint fingerprint) {
		if ( bitset.size() != fingerprint.size() ) {
			throw new IllegalArgumentException(
			              "Fingerprints must have same size" );
		}
		if ( fingerprint instanceof BitSetFingerprint ) {
			bitset.or( ((BitSetFingerprint)fingerprint).bitset );
		}
		else {
			for ( int i = 0 ; i<bitset.size() ; i++ ) {
				bitset.set( i, bitset.get(i) || fingerprint.get(i) );
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
}
