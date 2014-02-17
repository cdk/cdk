/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2002-2007  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.BitSet;
import java.util.Map;
import java.util.Random;

/**
 * Specialized version of the {@link Fingerprinter} which does not take bond orders
 * into account.
 *
 * @author         egonw
 * @cdk.created    2007-01-11
 * @cdk.keyword    fingerprint
 * @cdk.keyword    similarity
 * @cdk.module     standard
 * @cdk.githash
 * 
 * @see            org.openscience.cdk.fingerprint.Fingerprinter
 */
@TestClass("org.openscience.cdk.fingerprint.GraphOnlyFingerprinterTest")
public class GraphOnlyFingerprinter extends Fingerprinter {
	
	/**
	 * Creates a fingerprint generator of length <code>defaultSize</code>
	 * and with a search depth of <code>defaultSearchDepth</code>.
	 */
	public GraphOnlyFingerprinter() {
		super(DEFAULT_SIZE, DEFAULT_SEARCH_DEPTH);
	}
	
	public GraphOnlyFingerprinter(int size) {
		super(size, DEFAULT_SEARCH_DEPTH);
	}
	
	public GraphOnlyFingerprinter(int size, int searchDepth) {
		super(size, searchDepth);
	}

	/**
	 * Gets the bondSymbol attribute of the Fingerprinter class. Because we do
	 * not consider bond orders to be important, we just return "";
	 *
	 * @param  bond  Description of the Parameter
	 * @return       The bondSymbol value
	 */
	protected String getBondSymbol(IBond bond) {
		return "";
	}

    @TestMethod("testFingerPrint,testFingerprint")
    public BitSet getBitFingerprint(IAtomContainer container, int size) throws Exception {
		int[] hashes = findPathes(container, super.getSearchDepth());
		BitSet bitSet = new BitSet(size);
        for (int hash : hashes) {
            bitSet.set(new Random(hash).nextInt(size));
        }
		return bitSet;
	}
}

