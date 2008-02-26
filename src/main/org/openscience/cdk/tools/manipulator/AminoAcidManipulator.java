/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

/**
 * Class with convenience methods that provide methods to manipulate
 * AminoAcid's.
 *
 * @cdk.module  standard
 * @cdk.svnrev  $Revision$
 *
 * @author      Egon Willighagen
 * @cdk.created 2005-08-19
 */
@TestClass("org.openscience.cdk.tools.manipulator.AminoAcidManipulatorTest")
public class AminoAcidManipulator {

	/**
	 * Removes the singly bonded oxygen from the acid group of the AminoAcid.
	 * 
	 * @param acid AminoAcid from which to remove the oxygen
	 * @throws CDKException when the C-terminus is not defined for the given AminoAcid 
	 */
    @TestMethod("testRemoveAcidicOxygen_IAminoAcid")
    public static void removeAcidicOxygen(IAminoAcid acid) throws CDKException {
		if (acid.getCTerminus() == null) 
			throw new CDKException("Cannot remove oxygen: C-terminus is not defined!");
		
		java.util.List<IBond> bonds = acid.getConnectedBondsList(acid.getCTerminus());
		// ok, look for the oxygen which is singly bonded
        for (IBond bond : bonds) {
            if (bond.getOrder() == CDKConstants.BONDORDER_SINGLE) {
                for (int j = 0; j < bond.getAtomCount(); j++) {
                    if (bond.getAtom(j).getSymbol().equals("O")) {
                        // yes, we found a singly bonded oxygen!
                        acid.removeAtomAndConnectedElectronContainers(bond.getAtom(j));
                    }
                }
            }
        }
	}

	/**
	 * Adds the singly bonded oxygen from the acid group of the AminoAcid.
	 * 
	 * @param  acid         AminoAcid to which to add the oxygen
	 * @throws CDKException when the C-terminus is not defined for the given AminoAcid 
	 */
    @TestMethod("testAddAcidicOxygen_IAminoAcid")
    public static void addAcidicOxygen(IAminoAcid acid) throws CDKException {
		if (acid.getCTerminus() == null) 
			throw new CDKException("Cannot add oxygen: C-terminus is not defined!");

		IAtom acidicOxygen = acid.getBuilder().newAtom("O");
		acid.addAtom(acidicOxygen);
		acid.addBond(
			acid.getBuilder().newBond(acid.getCTerminus(), 
			acidicOxygen,
			CDKConstants.BONDORDER_SINGLE)
		);
	}
}

