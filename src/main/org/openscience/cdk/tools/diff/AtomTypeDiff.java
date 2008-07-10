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
import org.openscience.cdk.tools.diff.tree.AtomTypeHybridizationDifference;
import org.openscience.cdk.tools.diff.tree.BondOrderDifference;
import org.openscience.cdk.tools.diff.tree.ChemObjectDifference;
import org.openscience.cdk.tools.diff.tree.DoubleDifference;
import org.openscience.cdk.tools.diff.tree.IDifference;
import org.openscience.cdk.tools.diff.tree.IntegerDifference;
import org.openscience.cdk.tools.diff.tree.StringDifference;

/**
 * Compares two {@link IAtomType} classes.
 * 
 * @author     egonw
 * @cdk.module diff
 */
@TestClass("org.openscience.cdk.tools.diff.AtomTypeDiffTest")
public class AtomTypeDiff {
    
    @TestMethod("testMatchAgainstItself,testDiff")
    public static String diff( IChemObject first, IChemObject second ) {
        IDifference difference = difference(first, second);
        if (difference == null) {
            return "";
        } else {
            return difference.toString();
        }
    }
    @TestMethod("testDifference")
    public static IDifference difference( IChemObject first, IChemObject second ) {
        if (!(first instanceof IAtomType && second instanceof IAtomType)) {
            return null;
        }
        IAtomType firstElem = (IAtomType)first;
        IAtomType secondElem = (IAtomType)second;
        ChemObjectDifference totalDiff = new ChemObjectDifference("AtomTypeDiff");
        totalDiff.addChild(StringDifference.construct("N", firstElem.getAtomTypeName(), secondElem.getAtomTypeName()));
        totalDiff.addChild(BondOrderDifference.construct("MBO", firstElem.getMaxBondOrder(), secondElem.getMaxBondOrder()));
        totalDiff.addChild(DoubleDifference.construct("BOS", firstElem.getBondOrderSum(), secondElem.getBondOrderSum()));
        totalDiff.addChild(IntegerDifference.construct("FC", firstElem.getFormalCharge(), secondElem.getFormalCharge()));
        totalDiff.addChild(AtomTypeHybridizationDifference.construct("H", firstElem.getHybridization(), secondElem.getHybridization()));
        totalDiff.addChild(IntegerDifference.construct("NC", firstElem.getFormalNeighbourCount(), secondElem.getFormalNeighbourCount()));
        totalDiff.addChild(DoubleDifference.construct("CR", firstElem.getCovalentRadius(), secondElem.getCovalentRadius()));
        totalDiff.addChild(IntegerDifference.construct("V", firstElem.getValency(), secondElem.getValency()));
        totalDiff.addChild(IsotopeDiff.difference(first, second));
        if (totalDiff.childCount() > 0) {
            return totalDiff;
        } else {
            return null;
        }
    }

}
