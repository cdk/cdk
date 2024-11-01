/* Copyright (C) 2008 Arvid Berg <goglepox@users.sf.net>
 * Contact: cdk-devel@list.sourceforge.net
 * This program
 * is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer.elements;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Path2D;


/**
 * A line between two points.
 *
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class LineElement implements IRenderingElement {

    /** The x-coordinate of the first point. **/
    public final double firstPointX;

    /** The y-coordinate of the first point. **/
    public final double firstPointY;

    /** The x-coordinate of the second point. **/
    public final double secondPointX;

    /** The y-coordinate of the second point. **/
    public final double secondPointY;

    /** The width of the line. **/
    public final double width;

    /** The color of the line. **/
    public final Color  color;

    /**
     * Make a line element.
     *
     * @param firstPointX x-coordinate of the first point
     * @param firstPointY y-coordinate of the first point
     * @param secondPointX x-coordinate of the second point
     * @param secondPointY y-coordinate of the second point
     * @param width the width of the line
     * @param color the color of the line
     */
    public LineElement(double firstPointX, double firstPointY, double secondPointX, double secondPointY, double width,
            Color color) {
        this.firstPointX = firstPointX;
        this.firstPointY = firstPointY;
        this.secondPointX = secondPointX;
        this.secondPointY = secondPointY;
        this.width = width;
        this.color = color;
    }

    /** {@inheritDoc} **/
    @Override
    public void accept(IRenderingVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Convert the Line to an awt.Area, by default the line will have an
     * approximation of rounded end caps.
     * @return the area
     */
    public Area toArea() {
        return toArea(true);
    }

    /**
     * Convert the Line to an awt.Area specifying whether to include the end
     * caps or not.
     *
     * @param endCaps include the end caps
     * @return the area
     */
    public Area toArea(boolean endCaps) {
        Point2d b = new Point2d(firstPointX, firstPointY);
        Point2d e = new Point2d(secondPointX, secondPointY);
        // v = unit vector of the line, o = orthogonal vector
        Vector2d v = new Vector2d(e.x - b.x, e.y - b.y);
        Vector2d o = new Vector2d(-v.y, v.x);
        v.normalize();
        v.scale(width/2);
        o.normalize();
        o.scale(width/2);
        Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        path.moveTo(b.x+o.x, b.y+o.y);
        path.lineTo(e.x+o.x, e.y+o.y);
        if (endCaps) {
            path.curveTo(e.x + o.x + v.x / 2, e.y + o.y + o.y / 2,
                         e.x + v.x + o.x / 2, e.y + v.y + o.y / 2,
                         e.x + v.x, e.y + v.y);
            path.curveTo(e.x + v.x - o.x / 2, e.y + v.y - o.y / 2,
                         e.x - o.x + v.x / 2, e.y - o.y + v.y / 2,
                         e.x - o.x, e.y - o.y);
        } else {
            path.lineTo(e.x + v.x, e.y + v.y);
            path.lineTo(e.x - o.x, e.y - o.y);
        }
        path.lineTo(b.x-o.x, b.y-o.y);
        if (endCaps) {
            path.curveTo(b.x - o.x - v.x / 2, b.y - o.y - v.y /2,
                         b.x - v.x - o.x / 2, b.y - v.y - o.y /2,
                         b.x - v.x, b.y - v.y);
            path.curveTo(b.x - v.x + o.x / 2, b.y - v.y + o.y / 2,
                         b.x + o.x - v.x / 2, b.y + o.y - v.y / 2,
                         b.x + o.x, b.y + o.y);
        } else {
            path.lineTo(b.x - v.x, b.y - v.y);
        }
        path.closePath();
        return new Area(path);
    }

    /**
     * The type of the line.
     */
    public enum LineType {
        SINGLE(1), DOUBLE(2), TRIPLE(3), QUADRUPLE(4);

        final int n;

        LineType(int n) {
            this.n = n;
        }

        /**
         * Returns the count for this line type.
         *
         * @return the count for this line type.
         */
        public int count() {
            return n;
        }
    }
}
