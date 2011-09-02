package org.openscience.cdk.fingerprint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
/**
 * @author jonalv
 * @cdk.module     standard
 *
 */
public class IntArrayFingerprint implements IBitFingerprint {

	private int[] trueBits;
	
	public IntArrayFingerprint(Map<String, Integer> rawFingerPrint) {
		trueBits = new int[rawFingerPrint.size()];
		int i = 0;
		for ( String key : rawFingerPrint.keySet() ) {
			trueBits[i++] = key.hashCode();
		}
		Arrays.sort(trueBits);
	}
	
	public IntArrayFingerprint() {
		trueBits = new int[0];
	}
	
	public IntArrayFingerprint(IBitFingerprint fingerprint) {
		// if it is an IntArrayFingerprint we can do faster (System.arraycopy)
		if ( fingerprint instanceof IntArrayFingerprint ) {
			IntArrayFingerprint iaFP = (IntArrayFingerprint) fingerprint;
			trueBits = new int[iaFP.trueBits.length];
			System.arraycopy(iaFP.trueBits, 0, trueBits, 0, trueBits.length);
		}
		else {
			trueBits = new int[fingerprint.cardinality()];
			int index = 0;
			for ( int i = 0 ; i<fingerprint.size() ; i++ ) {
				if (fingerprint.get(i)) {
					trueBits[index++] = i;
				}
			}
		}
	}

	@Override
	public int cardinality() {
		return trueBits.length;
	}

	@Override
	public long size() {
		return 4294967296l;
	}

	@Override
	public void and(IBitFingerprint fingerprint) {
		if ( fingerprint instanceof IntArrayFingerprint) {
			and( (IntArrayFingerprint)fingerprint );
		}
		else {
			//TODO add support for this?
			throw new UnsupportedOperationException(
				"AND on IntArrayFingerPrint only supported for other " +
				"IntArrayFingerPrints for the moment" );
		}
	}
	
	public void and(IntArrayFingerprint fingerprint) {
		List<Integer> tmp = new ArrayList<Integer>();
		int i=0;
		int j=0;
		while ( i<trueBits.length && 
			    j<fingerprint.trueBits.length ) {
			int local = trueBits[i];
			int remote = fingerprint.trueBits[j];
			if (local == remote) {
				tmp.add(local);
				i++; j++;
			}
			else if (local<remote) {
				i++;
			}
			else {
				j++;
			}
		}
		trueBits = new int[tmp.size()];
		i=0;
		for ( Integer t : tmp ) {
			trueBits[i] = t;
		}
		Arrays.sort(trueBits);
	}

	@Override
	public void or(IBitFingerprint fingerprint) {
		if ( fingerprint instanceof IntArrayFingerprint) {
			or( (IntArrayFingerprint)fingerprint );
		}
		else {
			//TODO add support for this?
			throw new UnsupportedOperationException(
				"OR on IntArrayFingerPrint only supported for other " +
				"IntArrayFingerPrints for the moment" );
		}
	}
	
	public void or(IntArrayFingerprint fingerprint) {
		Set<Integer> tmp = new HashSet<Integer>();
		for (int i = 0; i < trueBits.length; i++) {
			tmp.add(trueBits[i]);
		}
		for (int i = 0 ; i < fingerprint.trueBits.length; i++) {
			tmp.add(fingerprint.trueBits[i]);
		}
		trueBits = new int[tmp.size()];
		int i=0;
		for ( Integer t : tmp ) {
			trueBits[i++] = t;
		}
		Arrays.sort(trueBits);
	}

	@Override
	public boolean get(int index) {
		return ( Arrays.binarySearch(trueBits, index) >= 0 );
	}

	/* 
	 * This method is VERY INNEFICIENT when caleld multiple times. It is the 
	 * cost of keeping down the memory footprint. Avoid using it for building 
	 * up IntArrayFingerprints -- instead use the constructor taking a so 
	 * called raw fingerprint.
	 */
	@Override
	public void set(int index, boolean value) {
		int i = Arrays.binarySearch(trueBits, index);
		// bit at index is set to true and shall be set to false
		if ( i >= 0 && !value ) {
			int[] tmp = new int[trueBits.length - 1];
			System.arraycopy(trueBits, 0, tmp, 0, i);
			System.arraycopy(trueBits, i+1, tmp, i, trueBits.length-i-1);
		}
		// bit at index is set to false and shall be set to true
		else if ( i < 0 && value ){
			int[] tmp = new int[trueBits.length + 1];
			System.arraycopy(trueBits, 0, tmp, 0, trueBits.length);
			tmp[tmp.length-1] = index;
			trueBits = tmp;
		}
		//rest of possible ops are no-ops
		Arrays.sort(trueBits);
	}

	@Override
	public BitSet asBitSet() {
		//TODO support this?
		throw new UnsupportedOperationException();		
	}

	@Override
	public void set(int i) {
		set(i, true);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(trueBits);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntArrayFingerprint other = (IntArrayFingerprint) obj;
		if (!Arrays.equals(trueBits, other.trueBits))
			return false;
		return true;
	}

    @Override
    public int[] getSetbits() {
        int[] copy = new int[trueBits.length];
        System.arraycopy( trueBits, 0, copy, 0, trueBits.length );
        return copy;
    }
	
}
