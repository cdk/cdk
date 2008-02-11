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
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.test.NewCDKTestCase;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Helper class that all atom type matcher test classes must implement.
 * It keeps track of the atom types which have been tested, to ensure
 * that all atom types are tested.
 *
 * @cdk.module test
 */
abstract public class AbstractAtomTypeTest extends NewCDKTestCase {

	public void assertAtomTypes(Map<String, Integer> testedAtomTypes, String[] expectedTypes, IAtomContainer mol) throws CDKException {
		Assert.assertEquals(
			"The number of expected atom types is unequal to the number of atoms",
			expectedTypes.length, mol.getAtomCount()
		);
		CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        for (int i=0; i<expectedTypes.length; i++) {
        	IAtom testedAtom = mol.getAtom(i);
        	IAtomType foundType = atm.findMatchingAtomType(mol, testedAtom); 
        	assertAtomType(testedAtomTypes, expectedTypes[i], foundType);
        	// test for bug #1890702: configure, and then make sure the same atom type is perceived
        	AtomTypeManipulator.configure(testedAtom, foundType);
        	IAtomType secondType = atm.findMatchingAtomType(mol, testedAtom);
        	assertAtomType(testedAtomTypes, 
        		"Incorrect perception *after* assigning atom type properties.",
        		expectedTypes[i], secondType
        	);
        }
	}

	public void assertAtomType(Map<String, Integer> testedAtomTypes, String expectedID, IAtomType foundAtomType) {
		this.assertAtomType(
			testedAtomTypes, 
			"No atom type percieved! Expected atom type: " + expectedID, 
			expectedID, foundAtomType
		);
	}

	public void assertAtomType(Map<String, Integer> testedAtomTypes, String error, String expectedID, IAtomType foundAtomType) {
		addTestedAtomType(testedAtomTypes, expectedID);

		Assert.assertNotNull(error, foundAtomType);
		Assert.assertEquals(error, expectedID, foundAtomType.getAtomTypeName());
	}

	private void addTestedAtomType(Map<String, Integer> testedAtomTypes, String expectedID) {
		if (testedAtomTypes == null) {
			testedAtomTypes = new HashMap<String, Integer>();
		}
	
		if (testedAtomTypes.containsKey(expectedID)) {
			// increase the count, so that redundancy can be calculated
			testedAtomTypes.put(expectedID,
                    1 + testedAtomTypes.get(expectedID)
            );
		} else {
			testedAtomTypes.put(expectedID, 1);
		}
	}
	
}
