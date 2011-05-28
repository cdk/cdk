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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * A cubic curve in the path.
 * 
 * @author Arvid
 * @cdk.module renderbasic
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.renderer.elements.path.CubicToTest")
public class CubicTo extends PathElement {

    /** first control point in the cubic. */
    public final Point2d cp1;
    
    /** second control point in the cubic. */
    public final Point2d cp2;
    
    /** end point of the cubic. */
    public final Point2d ep;

    /**
     * Make a cubic curve path element.
     * 
     * @param cp1 first control point in the cubic
     * @param cp2 second control point in the cubic
     * @param ep end point of the cubic 
     */
    @TestMethod("testConstructor")
    public CubicTo(Point2d cp1, Point2d cp2, Point2d ep) {
        super( Type.CubicTo );
        this.cp1 = cp1;
        this.cp2 = cp2;
        this.ep = ep;
    }

    /** {@inheritDoc} **/
    @Override
    @TestMethod("testPoints")
    public float[] points() {
     return new float[] { (float) cp1.x,
                          (float) cp1.y,
                          (float) cp2.x,
                          (float) cp2.y,
                          (float) ep.x,
                          (float) ep.y};
    }
}
