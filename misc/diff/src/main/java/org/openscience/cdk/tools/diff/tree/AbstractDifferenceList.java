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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Diff between two IChemObjects.
 *
 * @author     egonw
 * @cdk.module diff
 * @cdk.githash
 */
public abstract class AbstractDifferenceList implements IDifferenceList {

    protected List<IDifference> differences;

    protected AbstractDifferenceList() {
        differences = new ArrayList<IDifference>();
    }

    /** {@inheritDoc} */
    @Override
    public void addChild(IDifference childDiff) {
        if (childDiff != null) {
            differences.add(childDiff);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addChildren(List<IDifference> children) {
        if (children != null) {
            differences.addAll(children);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IDifference> getChildren() {
        return new Iterable<IDifference>() {

            @Override
            public Iterator<IDifference> iterator() {
                return differences.iterator();
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public int childCount() {
        return differences.size();
    }

}
