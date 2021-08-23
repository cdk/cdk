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
package org.openscience.cdk.tools.diff.tree;

import java.util.Iterator;
import javax.vecmath.Point2d;

/**
 * Difference between two boolean[]'s.
 *
 * @author egonw
 * @cdk.module diff
 * @cdk.githash
 */
public class Point2dDifference extends AbstractDifferenceList implements IDifferenceList {

    private String name;

    private Point2dDifference(String name) {
        this.name = name;
    }

    /**
     * Constructs a new {@link IDifference} object.
     *
     * @param name a name reflecting the nature of the created {@link IDifference}
     * @param first the first object to compare
     * @param second the second object to compare
     * @return an {@link IDifference} reflecting the differences between the first and second object
     */
    public static IDifference construct(String name, Point2d first, Point2d second) {
        if (first == null && second == null) return null;

        Point2dDifference totalDiff = new Point2dDifference(name);
        totalDiff.addChild(
                DoubleDifference.construct(
                        "x", first == null ? null : first.x, second == null ? null : second.x));
        totalDiff.addChild(
                DoubleDifference.construct(
                        "y", first == null ? null : first.y, second == null ? null : second.y));
        if (totalDiff.childCount() == 0) {
            return null;
        }
        return totalDiff;
    }

    /**
     * Returns a {@link String} representation for this {@link IDifference}.
     *
     * @return a {@link String}
     */
    @Override
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
