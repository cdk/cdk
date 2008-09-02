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

import javax.vecmath.Point2d;
import java.util.Iterator;

/**
 * Difference between two boolean[]'s.
 * 
 * @author     egonw
 * @cdk.module diff
 */
@TestClass("org.openscience.cdk.tools.diff.tree.Point2dDifferenceTest")
public class Point2dDifference extends AbstractDifferenceList implements IDifferenceList {

    private String name;
    
    private Point2dDifference(String name) {
        this.name = name;
    }
    
    @TestMethod("testDiff,testSame,testTwoNull,testOneNull")
    public static IDifference construct(String name, Point2d first, Point2d second) {
        if (first == null && second == null) return null;
        
        Point2dDifference totalDiff = new Point2dDifference(name);
        totalDiff.addChild(DoubleDifference.construct(
        	"x", 
        	first == null ? null : first.x,
        	second == null ? null : second.x
        ));
        totalDiff.addChild(DoubleDifference.construct(
        	"y", 
        	first == null ? null : first.y,
        	second == null ? null : second.y
        ));
        if (totalDiff.childCount() == 0) {
            return null;
        }
        return totalDiff;
    }

    @TestMethod("testToString")
    public String toString() {
        if (differences.size() == 0) return "";
        
        StringBuffer diffBuffer = new StringBuffer();
        diffBuffer.append(this.name).append('{');
        Iterator<IDifference> children = getChildren().iterator();
        while (children.hasNext()) {
            diffBuffer.append(children.next().toString());
            if (children.hasNext()) {
                diffBuffer.append(", ");
            }
        }
        diffBuffer.append('}');
        
        return diffBuffer.toString();
    }
}
