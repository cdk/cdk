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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * Compares two {@link IAtom} classes.
 * 
 * @author     egonw
 * @cdk.module diff
 */
@TestClass("org.openscience.cdk.tools.diff.AtomDiffTest")
public class AtomDiff extends AbstractChemObjectDiff {
    
    @TestMethod("testMatchAgainstItself,testDiff")
    public static String diff( IChemObject first, IChemObject second ) {
        if (!(first instanceof IAtom && second instanceof IAtom)) {
            return null;
        }
        IAtom firstElem = (IAtom)first;
        IAtom secondElem = (IAtom)second;
        StringBuffer resultString = new StringBuffer(32);
        resultString.append(diff("S", firstElem.getSymbol(), secondElem.getSymbol()));
        resultString.append(diff("H", firstElem.getHydrogenCount(), secondElem.getHydrogenCount()));
        resultString.append(diff("SP", firstElem.getStereoParity(), secondElem.getStereoParity()));
        resultString.append(diff("2D", firstElem.getPoint2d(), secondElem.getPoint2d()));
        resultString.append(diff("3D", firstElem.getPoint3d(), secondElem.getPoint3d()));
        resultString.append(diff("F3D", firstElem.getFractionalPoint3d(), secondElem.getFractionalPoint3d()));
        resultString.append(diff("C", firstElem.getCharge(), secondElem.getCharge()));
        resultString.append(AtomTypeDiff.diff(first, second));
        if (resultString.length() > 0) {
            return "AtomDiff(" + resultString.toString() + ")";
        } else {
            return "";
        }
    }

}
