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
 * @author Arvid
 * @cdk.module renderbasic
 */
public class QuadTo extends PathElement {

    Point2d p1;
    Point2d p2;

    public QuadTo(Point2d p1, Point2d p2) {

        super( Type.QuadTo );
        this.p1 = p1;
        this.p2 = p2;
    }
    
    @Override
    public float[] points() {
     return new float[] { (float) p1.x,
                          (float) p1.y,
                          (float) p2.x,
                          (float) p2.y};
    }
}
