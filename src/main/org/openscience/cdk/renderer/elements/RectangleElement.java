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
 * @cdk.module renderbasic
 */
public class RectangleElement implements IRenderingElement {
    
    public final double x;
    public final double y;
    public final double width;
    public final double height;
    public final boolean filled;
    public final Color color;

    
    public RectangleElement(
            double x1, double y1, double x2, double y2, Color color) {
        
        this(x1, y1, x2 - x1, y2 - y1, false, color);
    }
    
    public RectangleElement(double x,
                            double y, 
                            double width,
                            double height,
                            boolean filled,
                            Color color) {
        this.x = x;
        this.y =y;
        this.width = width;
        this.height = height;
        this.filled = filled;
        this.color = color;
    }
    
    public void accept(IRenderingVisitor v) {
        v.visit(this);
    }

}
