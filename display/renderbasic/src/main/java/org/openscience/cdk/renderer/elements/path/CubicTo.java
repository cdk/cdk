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
 */package org.openscience.cdk.renderer.elements.path;

import javax.vecmath.Point2d;


/**
 * A cubic curve in the path.
 *
 * @author Arvid
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class CubicTo extends PathElement {

    /** Coordinates of control point 1, control point 2 and end point. */
    public final double[] coords;

    /**
     * Make a cubic curve path element.
     *
     * @param cp1 first control point in the cubic
     * @param cp2 second control point in the cubic
     * @param ep end point of the cubic
     */
    public CubicTo(Point2d cp1, Point2d cp2, Point2d ep) {
        this(cp1.x, cp1.y, cp2.x, cp2.y, ep.x, ep.y);
    }

    /**
     * Make a cubic curve path element.
     *
     * @param coords [0,1] : control point 1, [2,3] : control point 2, [4,5] end
     *               point
     */
    public CubicTo(double[] coords) {
        super(Type.CubicTo);
        this.coords = new double[6];
        this.coords[0] = coords[0];
        this.coords[1] = coords[1];
        this.coords[2] = coords[2];
        this.coords[3] = coords[3];
        this.coords[4] = coords[4];
        this.coords[5] = coords[5];
    }

    /**
     * Make a cubic curve path element.
     *
     * @param cp1x first control point in the cubic x coord
     * @param cp1y first control point in the cubic y coord
     * @param cp2x second control point in the cubic x coord
     * @param cp2y second control point in the cubic y coord
     * @param epx end point of the cubic x coord
     * @param epy end point of the cubic y coord
     */
    public CubicTo(double cp1x, double cp1y, double cp2x, double cp2y, double epx, double epy) {
        this(new double[]{cp1x, cp1y, cp2x, cp2y, epx, epy});
    }

    /** {@inheritDoc} **/
    @Override
    public float[] points() {
        return new float[]{(float) coords[0], (float) coords[1], (float) coords[2], (float) coords[3],
                (float) coords[4], (float) coords[5]};
    }

    /**{@inheritDoc} */
    @Override
    public void points(double[] coords) {
        coords[0] = this.coords[0];
        coords[1] = this.coords[1];
        coords[2] = this.coords[2];
        coords[3] = this.coords[3];
        coords[4] = this.coords[4];
        coords[5] = this.coords[5];
    }
}
