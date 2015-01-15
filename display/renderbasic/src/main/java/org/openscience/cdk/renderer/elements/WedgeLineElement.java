/*  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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
 * A 'wedge' is a triangle aligned along a bond that indicates stereochemistry.
 * It can be dashed or not to indicate up and down.
 *
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class WedgeLineElement extends LineElement {

    /**
     * If the bond is dashed ,wedged, or "up_or_down", i.e., not defined.
     */
    public enum TYPE {
        DASHED, WEDGED, INDIFF
    }

    /**
     * The type of the bond (dashed, wedged, not defined).
     */
    public final TYPE      type;

    /**
     * The direction indicates which way the wedge gets thicker.
     */
    public final Direction direction;

    /**
     * 'toFirst' means that the wedge gets thicker in the direction of the first
     * point in the line.
     */
    public enum Direction {
        toFirst, toSecond;
    }

    /**
     * Make a wedge between the points (x1, y1) and (x2, y2) with a certain
     * width, direction, dash, and color.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param width the width of the wedge
     * @param type the bond is dashed ,wedged, or "up_or_down", i.e., not defined.
     * @param direction the direction of the thickness
     * @param color the color of the wedge
     */
    public WedgeLineElement(double x1, double y1, double x2, double y2, double width, TYPE type, Direction direction,
            Color color) {
        super(x1, y1, x2, y2, width, color);
        this.type = type;
        this.direction = direction;
    }

    /**
     * Make a wedge along the given line element.
     *
     * @param element the line element to use as the basic geometry
     * @param type if the bond is dashed ,wedged, or "up_or_down", i.e., not defined
     * @param direction the direction of the thickness
     * @param color the color of the wedge
     */
    public WedgeLineElement(LineElement element, TYPE type, Direction direction, Color color) {
        this(direction == Direction.toFirst ? element.secondPointX : element.firstPointX,
                direction == Direction.toFirst ? element.secondPointY : element.firstPointY,
                direction == Direction.toFirst ? element.firstPointX : element.secondPointX,
                direction == Direction.toFirst ? element.firstPointY : element.secondPointY, element.width, type,
                direction, color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(IRenderingVisitor v) {
        v.visit(this);
    }
}
