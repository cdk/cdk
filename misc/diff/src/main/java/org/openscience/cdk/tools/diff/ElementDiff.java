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

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.tools.diff.tree.ChemObjectDifference;
import org.openscience.cdk.tools.diff.tree.IDifference;
import org.openscience.cdk.tools.diff.tree.IntegerDifference;
import org.openscience.cdk.tools.diff.tree.StringDifference;

/**
 * Compares two {@link IElement} classes.
 *
 * @author     egonw
 */
public class ElementDiff {

    /**
     * Overwrite the default public constructor because this class is not
     * supposed to be instantiated.
     */
    private ElementDiff() {}

    /**
     * Compare two {@link IChemObject} classes and return the difference as a {@link String}.
     *
     * @param first  the first of the two classes to compare
     * @param second the second of the two classes to compare
     * @return a {@link String} representation of the difference between the first and second {@link IChemObject}.
     */
    public static String diff(IChemObject first, IChemObject second) {
        IDifference difference = difference(first, second);
        if (difference == null) {
            return "";
        } else {
            return difference.toString();
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
        if (!(first instanceof IElement && second instanceof IElement)) {
            return null;
        }
        IElement firstElem = (IElement) first;
        IElement secondElem = (IElement) second;
        ChemObjectDifference coDiff = new ChemObjectDifference("ElementDiff");
        coDiff.addChild(StringDifference.construct("S", firstElem.getSymbol(), secondElem.getSymbol()));
        coDiff.addChild(StringDifference.construct("ID", firstElem.getID(), secondElem.getID()));
        coDiff.addChild(IntegerDifference.construct("AN", firstElem.getAtomicNumber(), secondElem.getAtomicNumber()));
        coDiff.addChild(ChemObjectDiff.difference(first, second));
        if (coDiff.childCount() > 0) {
            return coDiff;
        } else {
            return null;
        }
    }

}
