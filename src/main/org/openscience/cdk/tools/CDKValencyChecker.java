/* $Revision$ $Author$ $Date$
 *  
 * Copyright (C) 2007  Egon Willighagen
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
package org.openscience.cdk.tools;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;

import java.util.Hashtable;
import java.util.Map;

/**
 * Assumes CDK atom types to be detected and adds missing hydrogens based on the
 * atom typing.
 * 
 * @author     egonw
 * @cdk.module valencycheck
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.tools.CDKValencyCheckerTest")
public class CDKValencyChecker implements IValencyChecker {

    private AtomTypeFactory atomTypeList;
    private final static String ATOM_TYPE_LIST = "org/openscience/cdk/dict/data/cdk-atom-types.owl";

    private static Map<String,CDKValencyChecker> tables = new Hashtable<String,CDKValencyChecker>(3);

    private CDKValencyChecker(IChemObjectBuilder builder) {
        if (atomTypeList == null)
            atomTypeList = AtomTypeFactory.getInstance(ATOM_TYPE_LIST, builder);
    }

    @TestMethod("testInstance")
    public static CDKValencyChecker getInstance(IChemObjectBuilder builder) {
        if (!tables.containsKey(builder.getClass().getName()))
            tables.put(builder.getClass().getName(), new CDKValencyChecker(builder));
        return tables.get(builder.getClass().getName());
    }

    @TestMethod("testIsSaturated_IAtomContainer,testIsSaturated_MissingHydrogens_Methane")
    public boolean isSaturated(IAtomContainer atomContainer) throws CDKException {
        for (IAtom atom : atomContainer.atoms()) {
            if (!isSaturated(atom, atomContainer)) return false;
        }        
		return true;
	}

    @TestMethod("testIsSaturatedPerAtom")
    public boolean isSaturated(IAtom atom, IAtomContainer container) throws CDKException {
		System.out.println(atom.getAtomTypeName());
		IAtomType type =  atomTypeList.getAtomType(atom.getAtomTypeName());
		if (type == null)
			throw new CDKException("Atom type is not a recognized CDK atom type: " + atom.getAtomTypeName());
		
		if (type.getFormalNeighbourCount() == CDKConstants.UNSET)
			throw new CDKException("Atom tfindAndConfigureAtomTypesForAllAtomsype is too general; cannot decide the number of implicit hydrogen to add for: " + atom.getAtomTypeName());

		if (type.getProperty(CDKConstants.PI_BOND_COUNT) == CDKConstants.UNSET)
			throw new CDKException("Atom type is too general; cannot determine the number of pi bonds for: " + atom.getAtomTypeName());

        double bondOrderSum = container.getBondOrderSum(atom);
        IBond.Order maxBondOrder = container.getMaximumBondOrder(atom);
        Integer hcount = atom.getHydrogenCount() == CDKConstants.UNSET ?  0 : atom.getHydrogenCount();
        
        int piBondCount = ((Integer)type.getProperty(CDKConstants.PI_BOND_COUNT)).intValue();
        int formalNeighborCount = type.getFormalNeighbourCount().intValue();
        
        int typeMaxBondOrder = piBondCount + 1;   
        int typeBondOrderSum = formalNeighborCount + piBondCount;
        
        if (bondOrderSum + hcount == typeBondOrderSum && 
        	maxBondOrder.ordinal() <= typeMaxBondOrder) {
        	return true;
        }
		return false;
	}

}
