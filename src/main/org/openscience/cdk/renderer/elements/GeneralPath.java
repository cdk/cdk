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
package org.openscience.cdk.renderer.elements;

import java.awt.Color;
import java.util.List;

import org.openscience.cdk.renderer.elements.path.PathElement;

/**
 * A path of rendering elements from the elements.path package.
 * 
 * @author Arvid
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class GeneralPath implements IRenderingElement{

    /** The color of the path. */
    public final Color color;

    /** The elements in the path. */
    public final List<PathElement> elements;

    /**
     * Make a path from a list of path elements.
     * 
     * @param elements the elements that make up the path
     * @param color the color of the path
     */
    public GeneralPath(List<PathElement> elements, Color color) {
        this.elements = elements;
        this.color = color;
    }

    /** {@inheritDoc} */
    public void accept( IRenderingVisitor v ) {
        v.visit( this );
    }

}