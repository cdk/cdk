/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2002-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Tool with helper methods for IFingerprint.
 *
 * @author         steinbeck
 * @cdk.created    2002-02-24
 * @cdk.keyword    fingerprint
 * @cdk.module     standard
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.fingerprint.FingerprinterToolTest")
public class FingerprinterTool {
	
	private final static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(FingerprinterTool.class);
	
	/**
	 *  Checks whether all the positive bits in BitSet bs2 occur in BitSet bs1. If
	 *  so, the molecular structure from which bs2 was generated is a possible
	 *  substructure of bs1. <p>
	 *
	 *  Example: <pre>
	 *  Molecule mol = MoleculeFactory.makeIndole();
	 *  BitSet bs = Fingerprinter.getBitFingerprint(mol);
	 *  Molecule frag1 = MoleculeFactory.makePyrrole();
	 *  BitSet bs1 = Fingerprinter.getBitFingerprint(frag1);
	 *  if (Fingerprinter.isSubset(bs, bs1)) {
	 *      System.out.println("Pyrrole is subset of Indole.");
	 *  }
	 *  </pre>
	 *
	 *@param  bs1     The reference BitSet
	 *@param  bs2     The BitSet which is compared with bs1
	 *@return         True, if bs2 is a subset of bs1
	 *@cdk.keyword    substructure search
	 */
    @TestMethod("testIsSubset_BitSet_BitSet")
    public static boolean isSubset(BitSet bs1, BitSet bs2)
	{
		BitSet clone = (BitSet) bs1.clone();
		clone.and(bs2);
		if (clone.equals(bs2))
		{
			return true;
		}
		return false;
	}

	/**
	 * This lists all bits set in bs2 and not in bs2 (other way round not considered) in a list and to logger
	 * 
	 * @param bs1 First bitset
	 * @param bs2 Second bitset
	 * @return An arrayList of Integers
	 */
    @TestMethod("testListDifferences_BitSet_BitSet")
    public static List<Integer> listDifferences(BitSet bs1, BitSet bs2)
	{
		List<Integer> l=new ArrayList<Integer>();
		logger.debug("Listing bit positions set in bs2 but not in bs1");
		for (int f = 0; f < bs2.size(); f++) {
			if (bs2.get(f) && !bs1.get(f)) {
				l.add(f);
				logger.debug("Bit " + f + " not set in bs1");
			}
		}
		return l;
	}

}

