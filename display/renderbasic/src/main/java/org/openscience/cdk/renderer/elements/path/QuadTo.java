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
 * Make a quadratic curve in the path.
 *
 * @author Arvid
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class QuadTo extends PathElement {

    /** Coordinates of control point and end point. */
    public final double[] coords;

    /**
     * Make a quad curve.
     *
     * @param cp control point of the curve
     * @param ep end point of the curve
     */
    public QuadTo(Point2d cp, Point2d ep) {
        this(cp.x, cp.y, ep.x, ep.y);
    }

    /**
     * Make a quad curve path element.
     *
     * @param coords [0,1] : control point 1, [2,3] : control point 2, [4,5] end
     *               point
     */
    public QuadTo(double[] coords) {
        super(Type.QuadTo);
        this.coords = new double[4];
        this.coords[0] = coords[0];
        this.coords[1] = coords[1];
        this.coords[2] = coords[2];
        this.coords[3] = coords[3];
    }

    /**
     * Make a quad curve path element.
     *
     * @param cpx control point in the cubic x coord
     * @param cpy control point in the cubic y coord
     * @param epx end point of the cubic x coord
     * @param epy end point of the cubic y coord
     */
    public QuadTo(double cpx, double cpy, double epx, double epy) {
        this(new double[]{cpx, cpy, epx, epy});
    }

    /** {@inheritDoc} **/
    @Override
    public float[] points() {
        return new float[]{(float) coords[0], (float) coords[1], (float) coords[2], (float) coords[3]};
    }

    /**{@inheritDoc} */
    @Override
    public void points(double[] coords) {
        coords[0] = this.coords[0];
        coords[1] = this.coords[1];
        coords[2] = this.coords[2];
        coords[3] = this.coords[3];
    }
}
