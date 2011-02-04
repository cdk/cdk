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
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.tools.diff.tree.ChemObjectDifference;
import org.openscience.cdk.tools.diff.tree.IDifference;
import org.openscience.cdk.tools.diff.tree.IDifferenceList;
import org.openscience.cdk.tools.diff.tree.IntegerDifference;

/**
 * Compares two {@link IChemObject} classes.
 * 
 * @author     egonw
 * @cdk.module diff
 */
@TestClass("org.openscience.cdk.tools.diff.ElectronContainerDiffTest")
public class ElectronContainerDiff {

    /**
     * Overwrite the default public constructor because this class is not
     * supposed to be instantiated.
     */
    private ElectronContainerDiff() {}

    @TestMethod("testMatchAgainstItself,testDiff")
    public static String diff( IChemObject first, IChemObject second ) {
    	IDifference diff = difference(first, second);
    	if (diff == null) {
    		return "";
    	} else {
    		return diff.toString();
    	}
    }
    @TestMethod("testDifference")
    public static IDifference difference( IChemObject first, IChemObject second ) {
        if (!(first instanceof IElectronContainer && second instanceof IElectronContainer)) {
            return null;
        }
        IElectronContainer firstEC = (IElectronContainer)first;
        IElectronContainer secondEC = (IElectronContainer)second;
        IDifferenceList totalDiff = new ChemObjectDifference("ElectronContainerDiff");
        totalDiff.addChild(IntegerDifference.construct("eCount", firstEC.getElectronCount(), secondEC.getElectronCount()));
        totalDiff.addChild(ChemObjectDiff.difference(first, second));
        if (totalDiff.childCount() > 0) {
            return totalDiff;
        } else {
            return null;
        }
    }

}
