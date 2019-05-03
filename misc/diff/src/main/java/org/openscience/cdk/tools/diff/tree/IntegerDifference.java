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

/**
 * {@link IDifference} between two {@link Integer}.
 *
 * @author     egonw
 * @cdk.module diff
 * @cdk.githash
 */
public class IntegerDifference implements IDifference {

    private String  name;
    private Integer first;
    private Integer second;

    private IntegerDifference(String name, Integer first, Integer second) {
        this.name = name;
        this.first = first;
        this.second = second;
    }

    /**
     * Constructs a new {@link IDifference} object.
     *
     * @param name   a name reflecting the nature of the created {@link IDifference}
     * @param first  the first object to compare
     * @param second the second object to compare
     * @return       an {@link IDifference} reflecting the differences between the first and second object
     */
    public static IDifference construct(String name, Integer first, Integer second) {
        if (first == null && second == null) {
            return null; // no difference
        }
        if (first == null || second == null) {
            return new IntegerDifference(name, first, second);
        }
        if (first.intValue() == second.intValue()) {
            return null; // no difference
        }
        return new IntegerDifference(name, first, second);
    }

    /**
     * Returns a {@link String} representation for this {@link IDifference}.
     *
     * @return a {@link String}
     */
    @Override
    public String toString() {
        return name + ":" + (first == null ? "NA" : first) + "/" + (second == null ? "NA" : second);
    }
}
