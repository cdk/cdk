/*  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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
package org.openscience.cdk.renderer.elements;

import java.awt.Color;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * A 'wedge' is a triangle aligned along a bond that indicates stereochemistry.
 * It can be dashed or not to indicate up and down.
 * 
 * @cdk.module renderbasic
 */
@TestClass("org.openscience.cdk.renderer.elements.WedgeLineElementTest")
public class WedgeLineElement extends LineElement {

    /**
     * If true, the wedge should be rendered as a dashed triangle.
     */
    public final boolean isDashed;

    /**
     * The direction indicates which way the wedge gets thicker.
     */
    public final Direction direction;

    /**
     * 'toFirst' means that the wedge gets thicker in the direction of the first
     * point in the line.
     */
    public enum Direction {
        toFirst, toSecond;
    }

    /**
     * Make a wedge between the points (x1, y1) and (x2, y2) with a certain
     * width, direction, dash, and color.
     * 
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param width the width of the wedge
     * @param dashed if true, the wedge should be dashed
     * @param direction the direction of the thickness
     * @param color the color of the wedge
     */
    @TestMethod("testConstructor")
    public WedgeLineElement(double x1, double y1, double x2, double y2,
            double width, boolean dashed, Direction direction, Color color) {
        super(x1, y1, x2, y2, width, color);
        this.isDashed = dashed;
        this.direction = direction;
    }

    /**
     * Make a wedge along the given line element.
     * 
     * @param element the line element to use as the basic geometry
     * @param dashed if true, the wedge should be dashed
     * @param direction the direction of the thickness
     * @param color the color of the wedge
     */
    public WedgeLineElement(LineElement element, boolean dashed,
            Direction direction, Color color) {
        this(direction == Direction.toFirst ? element.x2 : element.x1,
             direction == Direction.toFirst ? element.y2 : element.y1,
             direction == Direction.toFirst ? element.x1 : element.x2,
             direction == Direction.toFirst ? element.y1 : element.y2,
             element.width, dashed, direction, color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TestMethod("testAccept")
    public void accept(IRenderingVisitor v) {
        v.visit(this);
    }
}
