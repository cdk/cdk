/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools.diff;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * Compares two {@link IAtomType} classes.
 * 
 * @author     egonw
 * @cdk.module diff
 */
@TestClass("org.openscience.cdk.tools.diff.AtomTypeDiffTest")
public class AtomTypeDiff extends AbstractChemObjectDiff {
    
    @TestMethod("testMatchAgainstItself,testDiff")
    public static String diff( IChemObject first, IChemObject second ) {
        if (!(first instanceof IAtomType && second instanceof IAtomType)) {
            return null;
        }
        IAtomType firstElem = (IAtomType)first;
        IAtomType secondElem = (IAtomType)second;
        StringBuffer resultString = new StringBuffer(32);
        resultString.append(diff("N", firstElem.getAtomTypeName(), secondElem.getAtomTypeName()));
        resultString.append(diff("MBO", firstElem.getMaxBondOrder(), secondElem.getMaxBondOrder()));
        resultString.append(diff("BOS", firstElem.getBondOrderSum(), secondElem.getBondOrderSum()));
        resultString.append(diff("FC", firstElem.getFormalCharge(), secondElem.getFormalCharge()));
        resultString.append(diff("H", firstElem.getHybridization(), secondElem.getHybridization()));
        resultString.append(diff("NC", firstElem.getFormalNeighbourCount(), secondElem.getFormalNeighbourCount()));
        resultString.append(diff("CR", firstElem.getCovalentRadius(), secondElem.getCovalentRadius()));
        resultString.append(diff("VDWR", firstElem.getVanderwaalsRadius(), secondElem.getVanderwaalsRadius()));
        resultString.append(diff("V", firstElem.getValency(), secondElem.getValency()));
        resultString.append(IsotopeDiff.diff(first, second));
        if (resultString.length() > 0) {
            return "AtomTypeDiff(" + resultString.toString() + ")";
        } else {
            return "";
        }
    }

}
