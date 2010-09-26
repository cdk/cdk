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
 * Builder class for paths. All methods for constructing path elements return
 * a reference to the builder, so that it can be used like:
 * <pre>
 *  PathBuilder builder = new PathBuilder();
 *  builder.moveTo(p1).lineTo(p1).close();
 *  GeneralPath path = builder.createPath();
 * </pre>
 * 
 * @author Arvid
 * @cdk.module renderbasic
 */
public class PathBuilder {

    /** The path that is being built */
    private List<PathElement> elements;
    
    /** The color of the path */
    private Color color;
    
    /**
     * Make a new path builder with a default color of black.
     */
    public PathBuilder() {
        this(Color.BLACK);
    }
    
    /**
     * Make a path builder that will make a path with a particular color.  
     * 
     * @param color the color of the path
     */
    public PathBuilder(Color color) {
        elements = new ArrayList<PathElement>();
        this.color = color;
    }

    /**
     * Internal method that adds the element to the path.
     * 
     * @param element the element to add to the path
     */
    private <T extends PathElement> void add( T element ) {
        elements.add( element );
    }

    /**
     * Make a move in the path, without drawing anything. This is usually used
     * to start a path.
     * 
     * @param point the point to move to
     * @return a reference to this builder
     */
    public PathBuilder moveTo( Point2d point ) {
        add( new MoveTo( point ) );
        return this;
    }

    /**
     * Make a line in the path, from the last point to the given point.
     * 
     * @param point the point to make a line to
     * @return a reference to this builder
     */
    public PathBuilder lineTo( Point2d point ) {
        add( new LineTo( point ) );
        return this;
    }

    /**
     * Make a quadratic curve in the path, with one control point.
     *  
     * @param cp the control point of the curve
     * @param ep the end point of the curve
     * @return a reference to this builder
     */
    public PathBuilder quadTo( Point2d cp, Point2d ep ) {
        add( new QuadTo( cp, ep ) );
        return this;
    }

    /**
     * Make a cubic curve in the path, with two control points.
     * 
     * @param cp1 the first control point
     * @param cp2 the second control point
     * @param ep the end point of the curve
     * @return  a reference to this builder
     */
    public PathBuilder cubicTo( Point2d cp1, Point2d cp2, Point2d ep ) {
        add( new CubicTo( cp1, cp2, ep ) );
        return this;
    }

    /**
     * Close the path.
     */
    public void close() {
        add( new Close() );
    }

    public PathBuilder color( Color color ) {
    	this.color = color;
    	return this;
    }

    /**
     * Create and return the final path.
     * 
     * @return the newly created path
     */
    public GeneralPath createPath() {
        return new GeneralPath( elements, color );
    }
}