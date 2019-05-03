/*
 *  Copyright (C) 2009  Mark Rijnbeek <markrynbeek@gmail.com>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.tools.manipulator;

import java.util.Comparator;

import javax.vecmath.Point2d;

import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Compares two IAtomContainers based on their 2D position.
 * <p>
 * Implemented specifically to be used in JChemPaint.
 *
 * @author Mark Rijnbeek
 * @cdk.created  2009-10-14
 * @cdk.module   standard
 * @cdk.githash
 */
public class AtomContainerComparatorBy2DCenter implements Comparator<IAtomContainer> {

    /**
     * Compare two AtomContainers based on their 2D position.
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(IAtomContainer a, IAtomContainer b) {

        Point2d p1 = center(a);
        Point2d p2 = center(b);

        if (p1.x > p2.x) return +1;
        if (p1.x < p2.x) return -1;
        if (p1.y > p2.y) return +1;
        if (p1.y < p2.y) return -1;

        return 0;

    }

    /*
     * maximum point to use when an null container is provided (sorts null to
     * end)
     */
    private static final Point2d MAXIMUM = new Point2d(Double.MAX_VALUE, Double.MAX_VALUE);

    private static Point2d center(IAtomContainer container) {
        return container != null ? GeometryUtil.get2DCenter(container) : MAXIMUM;
    }

}
