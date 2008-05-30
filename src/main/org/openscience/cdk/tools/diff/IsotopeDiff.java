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
import org.openscience.cdk.interfaces.IIsotope;

/**
 * Compares two {@link IIsotope} classes.
 * 
 * @author     egonw
 * @cdk.module diff
 */
@TestClass("org.openscience.cdk.tools.diff.IsotopeDiffTest")
public class IsotopeDiff extends AbstractChemObjectDiff {
    
    @TestMethod("testMatchAgainstItself,testDiff")
    public static String diff( IChemObject first, IChemObject second ) {
        if (!(first instanceof IIsotope && second instanceof IIsotope)) {
            return null;
        }
        IIsotope firstElem = (IIsotope)first;
        IIsotope secondElem = (IIsotope)second;
        StringBuffer resultString = new StringBuffer(32);
        resultString.append(diff("MN", firstElem.getMassNumber(), secondElem.getMassNumber()));
        resultString.append(diff("EM", firstElem.getExactMass(), secondElem.getExactMass()));
        resultString.append(diff("AB", firstElem.getNaturalAbundance(), secondElem.getNaturalAbundance()));
        resultString.append(ElementDiff.diff(first, second));
        if (resultString.length() > 0) {
            return "IsotopeDiff(" + resultString.toString() + ")";
        } else {
            return "";
        }
    }

}
