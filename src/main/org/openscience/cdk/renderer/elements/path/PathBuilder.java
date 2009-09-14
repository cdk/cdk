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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.renderer.elements.GeneralPath;

/**
 * @author Arvid
 * @cdk.module renderbasic
 */
public class PathBuilder {

    List<PathElement> elements = new ArrayList<PathElement>();
    Color             color    = Color.BLACK;

    private <T extends PathElement> void add( T element ) {

        elements.add( element );
    }

    public PathBuilder moveTo( Point2d point ) {
        add( new MoveTo( point ) );
        return this;
    }

    public PathBuilder lineTo( Point2d point ) {
        add( new LineTo( point ) );
        return this;
    }

    public PathBuilder quadTo( Point2d p1, Point2d p2 ) {
        add( new QuadTo( p1, p2 ) );
        return this;
    }

    public PathBuilder cubicTo( Point2d p1, Point2d p2, Point2d p3 ) {
        add( new CubicTo( p1, p2, p3 ) );
        return this;
    }

    public void close() {
        add( new Close() );
    }

    public PathBuilder color( Color color ) {
        this.color = color;
        return this;
    }

    public GeneralPath createPath() {
        return new GeneralPath( elements, color );
    }
}