/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk.tools;

import java.util.Hashtable;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.Ring;

public class DeAromatizationTool {

	/**
	 * Methods that takes a ring of which all bonds are aromatic, and assigns single
	 * and double bonds. It does this in a non-general way by looking at the ring
	 * size and take everything as a special case.
	 * 
	 * @param ring Ring to dearomatize
	 * @return  False if it could not convert the aromatic ring bond into single and double bonds
	 */
	public static boolean deAromatize(Ring ring) {
		boolean result = false;
		Hashtable elementCounts = new MFAnalyser(ring).getFormulaHashtable();
		if (ring.getRingSize() == 6) {
			if (((Integer)elementCounts.get("C")).intValue() == 6) {
				result = DeAromatizationTool.deAromatizeBenzene(ring);
			} else if (((Integer)elementCounts.get("C")).intValue() == 5 &&
			           ((Integer)elementCounts.get("N")).intValue() == 1) {
				result = DeAromatizationTool.deAromatizePyridine(ring);
			}
		}
		return result;
	}
	
	private static boolean deAromatizePyridine(Ring ring) {
		return deAromatizeBenzene(ring); // same task to do
	}
	
	private static boolean deAromatizeBenzene(Ring ring) {
		Bond[] bonds = ring.getBonds();
		if (bonds.length != 6) return false;
		for (int i = 0; i<bonds.length; i++) {
			if (i%2 == 0) {
				bonds[i].setOrder(CDKConstants.BONDORDER_SINGLE);
			} else {
				bonds[i].setOrder(CDKConstants.BONDORDER_DOUBLE);
			}
		}
		return true;
	}
	
}
