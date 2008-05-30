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
import org.openscience.cdk.interfaces.IElement;

/**
 * Compares two {@link IElement} classes.
 * 
 * @author     egonw
 * @cdk.module diff
 */
@TestClass("org.openscience.cdk.tools.diff.AbstractChemObjectDiffTest")
public abstract class AbstractChemObjectDiff {
    
    /**
     * Shows the differences between two String type fields identified by
     * by a field name.
     * 
     * @param field   Name of the field.
     * @param first   Field value for the first {@link IChemObject}.
     * @param second  Field value for the second {@link IChemObject}.
     * @return
     */
    protected static String diff(String field, String first, String second) {
        if ((first == null && second == null) || first.equals(second)) {
            return "";
        } else {
            return ", " + field + ":" + first + "/" + second;
        }
    }
    
    /**
     * Shows the differences between two Integer type fields identified by
     * by a field name.
     * 
     * @param field   Name of the field.
     * @param first   Field value for the first {@link IChemObject}.
     * @param second  Field value for the second {@link IChemObject}.
     * @return
     */
    @TestMethod("testDiffIntegerFields,testDiffIntegerFieldsNoDiff")
    protected static String diff(String field, Integer first, Integer second) {
        if ((first == null && second == null) || first.equals(second)) {
            return "";
        } else {
            return ", " + field + ":" + first + "/" + second;
        }
    }

    /**
     * Shows the differences between two Double type fields identified by
     * by a field name.
     * 
     * @param field   Name of the field.
     * @param first   Field value for the first {@link IChemObject}.
     * @param second  Field value for the second {@link IChemObject}.
     * @return
     */
    @TestMethod("testDiffIntegerFields,testDiffIntegerFieldsNoDiff")
    protected static String diff(String field, Double first, Double second) {
        if ((first == null && second == null) || Math.abs(first - second) < 0.000000001) {
            return "";
        } else {
            return ", " + field + ":" + first + "/" + second;
        }
    }

}
