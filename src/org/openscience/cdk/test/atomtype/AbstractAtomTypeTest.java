/* $Revision: 5889 $ $Author: egonw $ $Date: 2006-04-06 15:24:58 +0200 (Thu, 06 Apr 2006) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.test.atomtype;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.test.NewCDKTestCase;

/**
 * Helper class that all atom type matcher test classes must implement.
 * It keeps track of the atom types which have been tested, to ensure
 * that all atom types are tested.
 *
 * @cdk.module test-core
 */
abstract public class AbstractAtomTypeTest extends NewCDKTestCase {

	protected Map<String, Integer> testedAtomTypes;
	
	public void assertAtomType(String expectedID, IAtomType foundAtomType) {
		addTestedAtomType(expectedID);

		Assert.assertNotNull(foundAtomType);
		Assert.assertEquals(expectedID, foundAtomType.getAtomTypeName());
	}

	public void assertAtomType(String error, String expectedID, IAtomType foundAtomType) {
		addTestedAtomType(expectedID);

		Assert.assertNotNull(error, foundAtomType);
		Assert.assertEquals(error, expectedID, foundAtomType.getAtomTypeName());
	}

	private void addTestedAtomType(String expectedID) {
		if (testedAtomTypes == null) {
			testedAtomTypes = new HashMap<String, Integer>();
		}
	
		if (testedAtomTypes.containsKey(expectedID)) {
			// increase the count, so that redundancy can be calculated
			testedAtomTypes.put(expectedID,
				Integer.valueOf(1 + testedAtomTypes.get(expectedID).intValue())
			);
		} else {
			testedAtomTypes.put(expectedID, Integer.valueOf(1));
		}
	}
	
}
