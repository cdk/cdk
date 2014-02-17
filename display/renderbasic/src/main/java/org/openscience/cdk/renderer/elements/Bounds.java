/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *  
 * Contact: cdk-devel@lists.sourceforge.net
 *  
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above 
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.renderer.elements;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * Defines a bound box element which the renderer can use to determine the true
 * drawing limits. Using only atom coordinates adjuncts (e.g. hydrogen labels)
 * may be truncated. If a generator provide a bounding box element, then the
 * min/max bounds of all bounding boxes are utilised.
 *
 * @author John May
 * @cdk.module renderbasic
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.renderer.elements.BoundingBoxTest")
public final class Bounds implements IRenderingElement {

    /** Minimum x/y coordinates. */
    public final double minX, minY;

    /** Maximum x/y coordinates. */
    public final double maxX, maxY;

    /**
     * Specify the min/max coordinates of the bounding box.
     * 
     * @param x1 min x coordinate
     * @param y1 min y coordinate
     * @param x2 max x coordinate
     * @param y2 max y coordinate
     */
    @TestMethod("testConstructor")
    public Bounds(double x1, double y1, double x2, double y2) {
        this.minX = x1;
        this.minY = y1;
        this.maxX = x2;
        this.maxY = y2;
    }

    /**
     * Specifies the width of the bounding box.
     * 
     * @return the width of the bounding box
     */
    @TestMethod("width")
    public final double width() {
        return maxX - minX;
    }

    /**
     * Specifies the height of the bounding box.
     *
     * @return the height of the bounding box
     */
    @TestMethod("width")
    public final double height() {
        return maxY - minY;
    }

    /** @inheritDoc */
    @TestMethod("testAccept")
    @Override public void accept(IRenderingVisitor visitor) {
        visitor.visit(this);
    }

    /** @inheritDoc */
    @Override public String toString() {
        return "{{" + minX + ", " + minY + "} - {" + maxX + ", " + maxY + "}}";
    }
}
