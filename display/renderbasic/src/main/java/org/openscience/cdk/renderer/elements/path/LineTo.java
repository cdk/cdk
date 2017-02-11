/*
 * Copyright (C) 2009  Arvid Berg <goglepox@users.sourceforge.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.renderer.elements.path;

import javax.vecmath.Point2d;


/**
 * A line element in the path.
 *
 * @author Arvid
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class LineTo extends PathElement {

    /** The point to make a line to. */
    public final double[] coords;

    /**
     * Make a line to this point.
     *
     * @param point the endpoint of the line
     */
    public LineTo(Point2d point) {
        this(point.x, point.y);
    }

    /**
     * Make a line path element.
     *
     * @param coords the x,y coordinates in index 0,1
     */
    public LineTo(double[] coords) {
        super(Type.LineTo);
        this.coords = new double[2];
        this.coords[0] = coords[0];
        this.coords[1] = coords[1];
    }

    /**
     * Make a line path element.
     *
     * @param x x coord
     * @param y y coord
     */
    public LineTo(double x, double y) {
        this(new double[]{x, y});
    }

    /** {@inheritDoc} **/
    @Override
    public float[] points() {
        return new float[]{(float) coords[0], (float) coords[1]};
    }

    /**{@inheritDoc} */
    @Override
    public void points(double[] coords) {
        coords[0] = this.coords[0];
        coords[1] = this.coords[1];
    }
}
