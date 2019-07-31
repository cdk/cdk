/* Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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


/**
 * An oval element (should) have both a width and a height.
 *
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class OvalElement implements IRenderingElement {

    /** The x-coordinate of the center of the oval. **/
    public final double  xCoord;

    /** The y-coordinate of the center of the oval. **/
    public final double  yCoord;

    /** The radius of the oval. **/
    public final double  radius;

    /** The stroke width. */
    public final double stroke;

    /** If true, draw the oval as filled. **/
    public final boolean fill;

    /** The color to draw the oval. **/
    public final Color   color;

    /**
     * Make an oval with a default radius of 10.
     *
     * @param xCoord the x-coordinate of the center of the oval
     * @param yCoord the y-coordinate of the center of the oval
     * @param color the color of the oval
     */
    public OvalElement(double xCoord, double yCoord, Color color) {
        this(xCoord, yCoord, 10, color);
    }

    /**
     * Make an oval with the supplied radius.
     *
     * @param xCoord the x-coordinate of the center of the oval
     * @param yCoord the y-coordinate of the center of the oval
     * @param radius the radius of the oval
     * @param color the color of the oval
     */
    public OvalElement(double xCoord, double yCoord, double radius, Color color) {
        this(xCoord, yCoord, radius, true, color);
    }

    /**
     * Make an oval with a particular fill and color.
     *
     * @param xCoord the x-coordinate of the center of the oval
     * @param yCoord the y-coordinate of the center of the oval
     * @param radius the radius of the oval
     * @param fill if true, fill the oval when drawing
     * @param color the color of the oval
     */
    public OvalElement(double xCoord, double yCoord, double radius, double stroke,
                       boolean fill, Color color) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.radius = radius;
        this.stroke = stroke;
        this.fill = fill;
        this.color = color;
    }

    public OvalElement(double xCoord, double yCoord, double radius,
                       boolean fill, Color color) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.radius = radius;
        this.stroke = 1;
        this.fill = fill;
        this.color = color;
    }

    /** {@inheritDoc} **/
    @Override
    public void accept(IRenderingVisitor visitor) {
        visitor.visit(this);
    }
}
