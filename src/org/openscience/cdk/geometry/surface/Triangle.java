/*
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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

package org.openscience.cdk.geometry.surface;

import javax.vecmath.Point3d;

/** 
 * Representation of a triangle in 3D.
 *
 * By default this class represent the triangle in clockwise 
 * fashion.
 *
 * @author Rajarshi Guha
 * @cdk.module extra
 * @cdk.svnrev  $Revision$
 */
public class Triangle {
    Point3d p1,p2,p3;
    public Triangle( Point3d p1, Point3d p2, Point3d p3 ) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }
}


