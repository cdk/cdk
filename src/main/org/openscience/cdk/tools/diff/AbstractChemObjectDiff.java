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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
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
     * Shows the differences between two boolean[] type fields identified by
     * by a field name.
     * 
     * @param field   Name of the field.
     * @param first   Field value for the first {@link boolean[]}.
     * @param second  Field value for the second {@link boolean[]}.
     * @return
     */
    @TestMethod("testDiffBooleanArrayFields,testDiffBooleanArrayFieldsNoDiff")
    protected static String diff(String field, boolean[] first, boolean[] second) {
        if (first == null && second == null) return "";
        String totalDiff = "";
        int firstLength = first.length;
        int secondLength = second.length;
        if (firstLength == secondLength) {
            for (int i=0; i<firstLength; i++) {
                if (first[i] != second[i]) {
                    totalDiff += ", " + field + i + ":" + (first[i] ? "T" : "F") + "/" + (second[i] ? "T" : "F");
                }
            }
        } else if (firstLength < secondLength) {
            for (int i=0; i<firstLength; i++) {
                if (first[i] != second[i]) {
                    totalDiff += ", " + field + i + ":" + (first[i] ? "T" : "F") + "/" + (second[i] ? "T" : "F");
                }
            }
            for (int i=firstLength; i<secondLength; i++) {
                totalDiff += ", " + field + i + ":NA/" + (second[i] ? "T" : "F");
            }
        } else { // secondLength < firstLength
            for (int i=0; i<secondLength; i++) {
                if (first[i] != second[i]) {
                    totalDiff += ", " + field + i + ":" + (first[i] ? "T" : "F") + "/" + (second[i] ? "T" : "F");
                }
            }
            for (int i=secondLength; i<firstLength; i++) {
                totalDiff += ", " + field + i + ":" + (first[i] ? "T" : "F") + "/NA";
            }
        }
        return totalDiff;
    }

    /**
     * Shows the differences between two Point2d type fields identified by
     * by a field name.
     * 
     * @param field   Name of the field.
     * @param first   Field value for the first {@link Point2d}.
     * @param second  Field value for the second {@link Point2d}.
     * @return
     */
    @TestMethod("testDiffPoint2dFields,testDiffPoint2dFieldsNoDiff")
    protected static String diff(String field, Point2d first, Point2d second) {
        if (first == null && second == null) return "";
        String totalDiff = "";
        if (first == null) {
            totalDiff += ", " + field + "({" + second.x + "," + second.y + "}/UNSET)";
        } else if (second == null) {
            totalDiff += ", " + field + "(UNSET/{" + first.x + "," + first.y + "})";
        } else {
            String xDiff = diff("x", first.x, second.x);
            if (xDiff.length() > 0) totalDiff += ", " + xDiff;
            String yDiff = diff("y", first.y, second.y);
            if (yDiff.length() > 0) totalDiff += ", " + yDiff;
            if (totalDiff.length() > 0) totalDiff = ", " + field + "(" + totalDiff + ")";
        }
        return totalDiff;
    }

    /**
     * Shows the differences between two Point3d type fields identified by
     * by a field name.
     * 
     * @param field   Name of the field.
     * @param first   Field value for the first {@link Point3d}.
     * @param second  Field value for the second {@link Point3d}.
     * @return
     */
    @TestMethod("testDiffPoint3dFields,testDiffPoint3dFieldsNoDiff")
    protected static String diff(String field, Point3d first, Point3d second) {
        if (first == null && second == null) return "";
        String totalDiff = "";
        if (first == null) {
            totalDiff += ", " + field + "({" + second.x + "," + second.y + "," + second.z + "}/UNSET)";
        } else if (second == null) {
            totalDiff += ", " + field + "(UNSET/{" + first.x + "," + first.y + "," + first.z + "})";
        } else {
            String xDiff = diff("x", first.x, second.x);
            if (xDiff.length() > 0) totalDiff += ", " + xDiff;
            String yDiff = diff("y", first.y, second.y);
            if (yDiff.length() > 0) totalDiff += ", " + yDiff;
            String zDiff = diff("z", first.z, second.z);
            if (zDiff.length() > 0) totalDiff += ", " + zDiff;
            if (totalDiff.length() > 0) totalDiff = ", " + field + "(" + totalDiff + ")";
        }
        return totalDiff;
    }

    /**
     * Shows the differences between two IBond.Order type fields identified by
     * by a field name.
     * 
     * @param field   Name of the field.
     * @param first   Field value for the first {@link IBond.Order}.
     * @param second  Field value for the second {@link IBond.Order}.
     * @return
     */
    @TestMethod("testDiffIBondOrderFields,testDiffIBondOrderFieldsNoDiff")
    protected static String diff(String field, IBond.Order first, IBond.Order second) {
        if ((first == null && second == null)) {
            return "";
        } else if (first == null) {
            return ", NULL/" + second;
        } else if (second == null) {
            return ", " + first + "/NULL";
        } else if (first.equals(second)) {
            return "";
        } else {
            return ", " + field + ":" + first + "/" + second;
        }
    }

    /**
     * Shows the differences between two IAtomType.Hybridization type fields identified by
     * by a field name.
     * 
     * @param field   Name of the field.
     * @param first   Field value for the first {@link IAtomType.Hybridization}.
     * @param second  Field value for the second {@link IAtomType.Hybridization}.
     * @return
     */
    @TestMethod("testDiffIAtomTypeTypeHybridizationFields,testDiffIAtomTypeTypeHybridizationFieldsNoDiff")
    protected static String diff(String field, IAtomType.Hybridization first, IAtomType.Hybridization second) {
        if ((first == null && second == null)) {
            return "";
        } else if (first == null) {
            return ", NULL/" + second;
        } else if (second == null) {
            return ", " + first + "/NULL";
        } else if (first.equals(second)) {
            return "";
        } else {
            return ", " + field + ":" + first + "/" + second;
        }
    }

    /**
     * Shows the differences between two String type fields identified by
     * by a field name.
     * 
     * @param field   Name of the field.
     * @param first   Field value for the first {@link IChemObject}.
     * @param second  Field value for the second {@link IChemObject}.
     * @return
     */
    @TestMethod("testDiffStringFields,testDiffStringFieldsNoDiff")
    protected static String diff(String field, String first, String second) {
        if (first == null && second == null) {
            return "";
        } else if (first == null) {
            return ", NULL/" + second;
        } else if (second == null) {
            return ", " + first + "/NULL";
        } else if (first.equals(second)) {
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
        if ((first == null && second == null)) {
            return "";
        } else if (first == null) {
            return ", NULL/" + second;
        } else if (second == null) {
            return ", " + first + "/NULL";
        } else if (first.equals(second)) {
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
        if (first == null && second == null) {
            return "";
        } else if (first == null) {
            return ", NULL/" + second;
        } else if (second == null) {
            return ", " + first + "/NULL";
        } else if (Math.abs(first - second) < 0.000000001) {
            return "";
        } else {
            return ", " + field + ":" + first + "/" + second;
        }
    }

}
