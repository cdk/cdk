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

import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;

/**
 * {@link IDifference} between two {@link Object}s which contains one or more child
 * {@link IDifference} objects.
 *
 * @author     egonw
 * @cdk.module diff
 * @cdk.githash
 */
public interface IDifferenceList extends IDifference {

    /**
     * Adds a new {@link IDifference} as child to this tree. For example, an {@link IAtom} difference
     * would have a child difference for {@link Point2d}.
     *
     * @param childDiff child {@link IDifference} to add to this {@link IDifference}
     */
    public void addChild(IDifference childDiff);

    /**
     * Adds multiple {@link IDifference}s as child to this tree.
     *
     * @param children a {@link List} of {@link IDifference}s to add to this {@link IDifference}
     */
    public void addChildren(List<IDifference> children);

    /**
     * Returns an {@link Iterable} of {@link IDifference} for all childs of this {@link IDifference}.
     *
     * @return an {@link Iterable} implementation with all children
     */
    public Iterable<IDifference> getChildren();

    /**
     * Returns the number of children of this {@link IDifference}.
     *
     * @return an int reflecting the number of children
     */
    public int childCount();

}
