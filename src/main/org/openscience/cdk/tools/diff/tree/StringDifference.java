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
package org.openscience.cdk.tools.diff.tree;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * {@link IDifference} between two {@link String}.
 * 
 * @author     egonw
 * @cdk.module diff
 */
@TestClass("org.openscience.cdk.tools.diff.tree.StringDifferenceTest")
public class StringDifference implements IDifference {

    private String name;
    private String first;
    private String second;
    
    private StringDifference(String name, String first, String second) {
        this.name = name;
        this.first = first;
        this.second = second;
    }
    
    @TestMethod("testDiff,testSame,testTwoNull,testOneNull")
    public static IDifference construct(String name, String first, String second) {
        if (first == null && second == null) {
            return null; // no difference
        }
        if (first == null || second == null) {
            return new StringDifference(name, first, second);
        }
        if (first.equals(second)) {
            return null; // no difference
        }
        return new StringDifference(name, first, second);
    }

    @TestMethod("testToString")
    public String toString() {
        return name + ":" + 
            (first == null ? "NA" : first) +
            "/" +
            (second == null ? "NA" : second);        
    }
}
