/* $Revision$ 
 * $Author$ 
 * $Date$ 
 * Copyright (C) 2008 Arvid Berg <goglepox@users.sf.net> 
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

import java.awt.Color;

/**
 * A line between two points.
 * 
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class LineElement implements IRenderingElement {

    /** The x-coordinate of the first point. **/
    public final double x1;
    
    /** The y-coordinate of the first point. **/
    public final double y1;
    
    /** The x-coordinate of the second point. **/
    public final double x2;
    
    /** The y-coordinate of the second point. **/
    public final double y2;
    
    /** The width of the line. **/
    public final double width;
    
    /** The color of the line. **/
    public final Color color;

    /**
     * Make a line element. 
     * 
     * @param x1 x-coordinate of the first point
     * @param y2 y-coordinate of the first point
     * @param x2 x-coordinate of the second point
     * @param y2 y-coordinate of the second point
     * @param width the width of the line
     * @param color the color of the line
     */
    public LineElement(
          double x1, double y1, double x2, double y2, double width, Color color) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.width = width;
        this.color = color;
    }

    /** {@inheritDoc} **/
    public void accept(IRenderingVisitor v) {
        v.visit(this);
    }

    /**
     * The type of the line.
     */
    public enum LineType {
        SINGLE(1), DOUBLE(2), TRIPLE(3), QUADRUPLE(4);

        int n;

        private LineType(int n) {
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
