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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.tools.diff.tree.ChemObjectDifference;
import org.openscience.cdk.tools.diff.tree.IDifference;
import org.openscience.cdk.tools.diff.tree.IntegerDifference;

/**
 * Compares two {@link IAtomContainer} classes.
 * 
 * @author     egonw
 * @cdk.module diff
 */
@TestClass("org.openscience.cdk.tools.diff.AtomContainerDiffTest")
public class AtomContainerDiff {
    
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
        if (!(first instanceof IAtomContainer && second instanceof IAtomContainer)) {
            return null;
        }
        IAtomContainer firstAC = (IAtomContainer)first;
        IAtomContainer secondAC = (IAtomContainer)second;
        ChemObjectDifference totalDiff = new ChemObjectDifference("AtomContainerDiff");
        totalDiff.addChild(IntegerDifference.construct("atomCount", firstAC.getAtomCount(), secondAC.getAtomCount()));
        if (firstAC.getAtomCount() == secondAC.getAtomCount()) {
            for (int i=0; i<firstAC.getAtomCount(); i++) {
                totalDiff.addChild(AtomDiff.difference(firstAC.getAtom(i), secondAC.getAtom(i)));
            }
        }
        totalDiff.addChild(IntegerDifference.construct("electronContainerCount", firstAC.getElectronContainerCount(), secondAC.getElectronContainerCount()));
        if (firstAC.getElectronContainerCount() == secondAC.getElectronContainerCount()) {
            for (int i=0; i<firstAC.getElectronContainerCount(); i++) {
                if (firstAC.getElectronContainer(i) instanceof IBond &&
                    secondAC.getElectronContainer(i) instanceof IBond) {
                    totalDiff.addChild(BondDiff.difference(firstAC.getElectronContainer(i), secondAC.getElectronContainer(i)));
                } else if (firstAC.getElectronContainer(i) instanceof ILonePair &&
                        secondAC.getElectronContainer(i) instanceof ILonePair) {
                    totalDiff.addChild(LonePairDiff.difference(firstAC.getElectronContainer(i), secondAC.getElectronContainer(i)));
                } else if (firstAC.getElectronContainer(i) instanceof ISingleElectron &&
                        secondAC.getElectronContainer(i) instanceof ISingleElectron) {
                    totalDiff.addChild(SingleElectronDiff.difference(firstAC.getElectronContainer(i), secondAC.getElectronContainer(i)));
                } else {
                    totalDiff.addChild(ElectronContainerDiff.difference(firstAC.getElectronContainer(i), secondAC.getElectronContainer(i)));
                }
            }
        }
        totalDiff.addChild(ChemObjectDiff.difference(first, second));
        if (totalDiff.childCount() > 0) {
            return totalDiff;
        } else {
            return null;
        }
    }

}
