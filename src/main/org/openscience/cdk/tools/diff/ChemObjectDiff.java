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
import org.openscience.cdk.tools.diff.tree.BooleanArrayDifference;
import org.openscience.cdk.tools.diff.tree.ChemObjectDifference;
import org.openscience.cdk.tools.diff.tree.IDifference;

/**
 * Compares two {@link IChemObject} classes.
 * 
 * @author     egonw
 * @cdk.module diff
 */
@TestClass("org.openscience.cdk.tools.diff.ChemObjectDiffTest")
public class ChemObjectDiff {

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
        if (!(first instanceof IChemObject && second instanceof IChemObject)) {
            return null;
        }
        IChemObject firstElem = (IChemObject)first;
        IChemObject secondElem = (IChemObject)second;
        ChemObjectDifference coDiff = new ChemObjectDifference("ChemObjectDiff");
        // Compare flags
        boolean[] firstFlags = firstElem.getFlags();
        boolean[] secondFlags = secondElem.getFlags();
        coDiff.addChild(BooleanArrayDifference.construct("flag", firstFlags, secondFlags));
        if (coDiff.childCount() > 0) {
            return coDiff;
        } else {
            return null;
        }
    }

}
