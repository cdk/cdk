/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.diff.tree.ChemObjectDifference;
import org.openscience.cdk.tools.diff.tree.DoubleDifference;
import org.openscience.cdk.tools.diff.tree.IDifference;
import org.openscience.cdk.tools.diff.tree.IntegerDifference;
import org.openscience.cdk.tools.diff.tree.Point2dDifference;
import org.openscience.cdk.tools.diff.tree.Point3dDifference;

/**
 * Compares two {@link IAtom} classes.
 *
 * @author     egonw
 * @cdk.module diff
 * @cdk.githash
 */
public class AtomDiff {

    /**
     * Overwrite the default public constructor because this class is not
     * supposed to be instantiated.
     */
    private AtomDiff() {}

    /**
     * Compare two {@link IChemObject} classes and return the difference as a {@link String}.
     *
     * @param first  the first of the two classes to compare
     * @param second the second of the two classes to compare
     * @return a {@link String} representation of the difference between the first and second {@link IChemObject}.
     */
    public static String diff(IChemObject first, IChemObject second) {
        IDifference diff = difference(first, second);
        if (diff == null) {
            return "";
        } else {
            return diff.toString();
        }
    }

    /**
     * Compare two {@link IChemObject} classes and return the difference as an {@link IDifference}.
     *
     * @param first  the first of the two classes to compare
     * @param second the second of the two classes to compare
     * @return an {@link IDifference} representation of the difference between the first and second {@link IChemObject}.
     */
    public static IDifference difference(IChemObject first, IChemObject second) {
        if (!(first instanceof IAtom && second instanceof IAtom)) {
            return null;
        }
        IAtom firstElem = (IAtom) first;
        IAtom secondElem = (IAtom) second;
        ChemObjectDifference totalDiff = new ChemObjectDifference("AtomDiff");
        totalDiff.addChild(IntegerDifference.construct("H", firstElem.getImplicitHydrogenCount(),
                secondElem.getImplicitHydrogenCount()));
        totalDiff
                .addChild(IntegerDifference.construct("SP", firstElem.getStereoParity(), secondElem.getStereoParity()));
        totalDiff.addChild(Point2dDifference.construct("2D", firstElem.getPoint2d(), secondElem.getPoint2d()));
        totalDiff.addChild(Point3dDifference.construct("3D", firstElem.getPoint3d(), secondElem.getPoint3d()));
        totalDiff.addChild(Point3dDifference.construct("F3D", firstElem.getFractionalPoint3d(),
                secondElem.getFractionalPoint3d()));
        totalDiff.addChild(DoubleDifference.construct("C", firstElem.getCharge(), secondElem.getCharge()));
        totalDiff.addChild(AtomTypeDiff.difference(first, second));
        if (totalDiff.childCount() > 0) {
            return totalDiff;
        } else {
            return null;
        }
    }

}
