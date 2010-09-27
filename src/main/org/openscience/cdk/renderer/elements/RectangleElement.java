/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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
 * A rectangle, with width and height.
 * 
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class RectangleElement implements IRenderingElement {
    
    /** The x-coordinate of the center of the rectangle. **/
    public final double x;
    
    /** The y-coordinate of the center of the rectangle. **/
    public final double y;
    
    /** The width of the rectangle. **/
    public final double width;
    
    /** The height of the rectangle. **/
    public final double height;
    
    /** If true, the rectangle is drawn as filled. **/
    public final boolean filled;
    
    /** The color of the rectangle. **/
    public final Color color;

    
    /**
     * Make a rectangle from two opposite corners (x1, y1) and (x2, y2).
     * 
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param color the color of the rectangle
     */
    public RectangleElement(
            double x1, double y1, double x2, double y2, Color color) {
        
        this(x1, y1, x2 - x1, y2 - y1, false, color);
    }
    
    /**
     * Make a rectangle centered on (x, y).
     * 
     * @param x x-coordinate of the center of the rectangle
     * @param y y-coordinate of the center of the rectangle
     * @param width width of the rectangle
     * @param height height of the rectangle
     * @param filled if true, the rectangle is drawn as filled
     * @param color the color of the rectangle
     */
    public RectangleElement(double x,
                            double y, 
                            double width,
                            double height,
                            boolean filled,
                            Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.filled = filled;
        this.color = color;
    }
    
    /** {@inheritDoc }**/
    public void accept(IRenderingVisitor v) {
        v.visit(this);
    }

}
