/*  Copyright (C) 2008  Gilleain Torrance <gilleain.torrance@gmail.com>
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
import java.util.ArrayList;

import javax.vecmath.Point2d;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * @cdk.module renderbasic
 */
@TestClass("org.openscience.cdk.renderer.elements.PathElementTest")
public class PathElement implements IRenderingElement {

    public final ArrayList<Point2d> points;
    public final Color color;
    
    @TestMethod("testConstructor")
    public PathElement(ArrayList<Point2d> points, Color color) {
        this.points = points;
        this.color = color;
    }
    
    @TestMethod("testAccept")
    public void accept(IRenderingVisitor v) {
        v.visit(this);
    }

}
