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

/**
 * A path element.
 *
 * @author Arvid
 * @cdk.module renderbasic
 * @cdk.githash
 */
public abstract class PathElement {

    /** the type of the path element. */
    public final Type type;

    /**
     * Create a path element.
     *
     * @param type {@link Type} of this path element
     */
    public PathElement(Type type) {
        this.type = type;
    }

    /**
     * Get the type of the path element.
     *
     * @return the type of the path element
     */
    public Type type() {
        return type;
    }

    /**
     * Get the points in the path.
     *
     * @return a list of points
     */
    @Deprecated
    public abstract float[] points();

    /**
     * Load the provided array with the specified coordinates of this path
     * element.
     *
     * @param coords coordinates (length = 6)
     */
    public abstract void points(double[] coords);

    /**
     * Load the provided array with the specified coordinates of this path
     * element.
     *
     * @param coords coordinates (length = 6)
     */
    public void points(float[] coords) {
        double[] dfCoords = new double[coords.length];
        points(dfCoords);
        for (int i = 0; i < coords.length; i++)
            coords[i] = (float)dfCoords[i];
    }

    @Override
    public String toString() {
        double[] coords = new double[6];
        points(coords);
        switch (type) {
            case MoveTo:
                return String.format("MoveTo: [%.3f, %.3f]", coords[0], coords[1]);
            case LineTo:
                return String.format("LineTo: [%.3f, %.3f]", coords[0], coords[1]);
            case QuadTo:
                return String.format("QuadTo: [%.3f, %.3f, %.3f, %.3f, %.3f, %.3f]",
                                     coords[0], coords[1], coords[2], coords[3],
                                     coords[4], coords[5]);
            case CubicTo:
                return String.format("QuadTo: [%.3f, %.3f, %.3f, %.3f]",
                                     coords[0], coords[1], coords[2], coords[3]);
            case Close:
                return "Close";
        }
        return "???";
    }
}
