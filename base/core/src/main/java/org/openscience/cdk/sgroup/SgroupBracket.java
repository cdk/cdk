/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.sgroup;

import javax.vecmath.Point2d;

/**
 * Representation of an Sgroup bracket.
 */
public class SgroupBracket {

    private final Point2d p1, p2;

    /**
     * Create an Sgroup bracket.
     *
     * @param x1 first x coord
     * @param y1 first y coord
     * @param x2 second x coord
     * @param y2 second y coord
     */
    public SgroupBracket(double x1, double y1, double x2, double y2) {
        this.p1 = new Point2d(x1, y1);
        this.p2 = new Point2d(x2, y2);
    }

    /**
     * Copy constructor.
     * @param org original sgroup bracket
     */
    public SgroupBracket(SgroupBracket org) {
        this(org.p1.x, org.p1.y,
             org.p2.x, org.p2.y);
    }

    /**
     * First point of the bracket (x1,y1).
     *
     * @return first point
     */
    public Point2d getFirstPoint() {
        return p1;
    }

    /**
     * Second point of the bracket (x2,y2).
     *
     * @return second point
     */
    public Point2d getSecondPoint() {
        return p2;
    }

    @Override
    public String toString() {
        return "SgroupBracket{" +
               "x1=" + p1.x +
               ", y1=" + p1.y +
               ", x2=" + p2.x +
               ", y2=" + p2.y +
               '}';
    }
}
