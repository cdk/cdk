/*
 * Copyright (C) 2024 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.renderer.generators.standard;

import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.path.Type;
import org.openscience.cdk.tools.LoggingToolFactory;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.LogManager;

final class AwtArea {

    public static Area toArea(IRenderingElement e) {
        if (e instanceof LineElement)
            return ((LineElement)e).toArea();
        else if (e instanceof GeneralPath)
            return ((GeneralPath)e).toArea();
        else if (e instanceof ElementGroup) {
            Area total = null;
            ElementGroup grp = (ElementGroup)e;
            for (IRenderingElement child : grp) {
                Area childArea = toArea(child);
                if (childArea == null)
                    continue;
                if (total == null)
                    total = childArea;
                else
                    total.add(childArea);
            }
            return total;
        } else if (e == null) {
            // some bonds don't get rendered
            return null;
        }
        LoggingToolFactory
                .createLoggingTool(AwtArea.class)
                .warn("Unsupported awt.Area of rendering element: " +
                              e.getClass().getSimpleName());
        return null;
    }

    private static Vector2d newVector(double x, double y, double len) {
        Vector2d v = new Vector2d(x, y);
        v.normalize();
        v.scale(len);
        return v;
    }

    /**
     * Expand an area by a given amount (width) as though you have outlined a
     * shape with a stroke of the given width. This method is approximate on
     * curves.
     */
    static Area expand(Area area, double width) {
        Path2D path = new Path2D.Double();
        boolean  started = false;
        double[] first = new double[2];
        double[] p = new double[2];
        double[] data = new double[6];
        boolean flip = false;
        Type[] types = Type.values();
        PathIterator it = area.getPathIterator(new AffineTransform());
        for (;!it.isDone(); it.next()) {
            Type type = types[it.currentSegment(data)];
            switch (type) {
                case MoveTo:
                    p[0] = data[0];
                    p[1] = data[1];
                    first[0] = p[0];
                    first[1] = p[1];
                    break;
                case LineTo:
                    double dx = data[0] - p[0];
                    double dy = data[1] - p[1];

                    // try and work out what is the 'inside' as we want to
                    // expand outwards, we project the mid-point of the line
                    // a short amount along the two perpendicular vectors
                    if (!started) {
                        Vector2d p1 = newVector(dy, -dx, 0.01);
                        double[] mid = new double[]{ p[0] + dx/2, p[1] + dy/2 };
                        flip = !area.contains(mid[0] + p1.x, mid[1] + p1.y) &&
                                area.contains(mid[0] - p1.x, mid[1] - p1.y);
                    }

                    Vector2d v = flip ? newVector(dy, -dx, width) : newVector(-dy, dx, width);
                    if (started) {
                        path.lineTo(p[0] + v.x, p[1] + v.y);
                        path.lineTo(data[0] + v.x, data[1] + v.y);
                    } else {
                        path.moveTo(p[0] + v.x, p[1] + v.y);
                        path.lineTo(data[0] + v.x, data[1] + v.y);
                    }
                    p[0] = data[0];
                    p[1] = data[1];
                    started = true;
                    break;
                case CubicTo:
                    // JWM approximate with just a lineTo
                    // cp1x,cp1y, cp2x,cp2y x,y
                    dx = data[4] - p[0];
                    dy = data[5] - p[1];
                    v = flip ? newVector(dy, -dx, width) : newVector(-dy, dx, width);
                    if (started) {
                        path.lineTo(p[0] + v.x, p[1] + v.y);
                        path.lineTo(data[0] + v.x, data[1] + v.y);
                    } else {
                        path.moveTo(p[0] + v.x, p[1] + v.y);
                        path.lineTo(data[0] + v.x, data[1] + v.y);
                    }
                    p[0] = data[0];
                    p[1] = data[1];
                    started = true;
                    break;
                case QuadTo:
                    // JWM approximate with just a lineTo
                    // cx,cy,x,y
                    dx = data[2] - p[0];
                    dy = data[3] - p[1];
                    v = flip ? newVector(dy, -dx, width) : newVector(-dy, dx, width);
                    v.normalize();
                    v.scale(width);
                    if (started) {
                        path.lineTo(p[0] + v.x, p[1] + v.y);
                        path.lineTo(data[0] + v.x, data[1] + v.y);
                    } else {
                        path.moveTo(p[0] + v.x, p[1] + v.y);
                        path.lineTo(data[0] + v.x, data[1] + v.y);
                    }

                    p[0] = data[0];
                    p[1] = data[1];
                    started = true;
                    break;
                case Close:
                    dx = first[0] - p[0];
                    dy = first[1] - p[1];
                    if (Math.abs(dx) >= 0.01 || Math.abs(dy) >= 0.01) {
                        v = flip ? newVector(dy, -dx, width) : newVector(-dy, dx, width);
                        v.normalize();
                        v.scale(width);
                        path.lineTo(p[0] + v.x, p[1] + v.y);
                        path.lineTo(first[0] + v.x, first[1] + v.y);
                    }
                    path.closePath();
                    started = false;
                    break;
            }
        }
        return new Area(path);
    }
}
