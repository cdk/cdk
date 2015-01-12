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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.renderer.elements.path.Close;
import org.openscience.cdk.renderer.elements.path.CubicTo;
import org.openscience.cdk.renderer.elements.path.LineTo;
import org.openscience.cdk.renderer.elements.path.MoveTo;
import org.openscience.cdk.renderer.elements.path.PathElement;
import org.openscience.cdk.renderer.elements.path.QuadTo;

/**
 * A path of rendering elements from the elements.path package.
 *
 * @author Arvid
 * @cdk.module renderbasic
 * @cdk.githash
 */
public class GeneralPath implements IRenderingElement {

    /** The color of the path. */
    public final Color             color;

    /** The width of the stroke. */
    public final double            stroke;

    /** Fill the shape instead of drawing outline. */
    public final boolean           fill;

    /** The elements in the path. */
    public final List<PathElement> elements;

    /** Winding rule for determining path interior. */
    public final int               winding;

    /**
     * @see PathIterator#WIND_EVEN_ODD
     */
    public static final int        WIND_EVEN_ODD = 0;

    /**
     * @see PathIterator#WIND_NON_ZERO
     */
    public static final int        WIND_NON_ZERO = 1;

    /**
     * Make a path from a list of path elements.
     *
     * @param elements the elements that make up the path
     * @param color the color of the path
     */
    public GeneralPath(List<PathElement> elements, Color color) {
        this(elements, color, WIND_EVEN_ODD, 1, true);

    }

    /**
     * Make a path from a list of path elements.
     *
     * @param elements the elements that make up the path
     * @param color the color of the path
     */
    private GeneralPath(List<PathElement> elements, Color color, int winding, double stroke, boolean fill) {
        this.elements = elements;
        this.color = color;
        this.winding = winding;
        this.fill = fill;
        this.stroke = stroke;
    }

    /**
     * Recolor the path with the specified color.
     *
     * @param newColor new path color
     * @return the recolored path
     */
    public GeneralPath recolor(Color newColor) {
        return new GeneralPath(elements, newColor, winding, stroke, fill);
    }

    /**
     * Outline the general path with the specified stroke size.
     *
     * @param newStroke new stroke size
     * @return the outlined path
     */
    public GeneralPath outline(double newStroke) {
        return new GeneralPath(elements, color, winding, newStroke, false);
    }

    /** {@inheritDoc} */
    @Override
    public void accept(IRenderingVisitor v) {
        v.visit(this);
    }

    /**
     * Create a filled path of the specified Java 2D Shape and color.
     *
     * @param shape Java 2D shape
     * @param color the color to fill the shape with
     * @return a new general path
     */
    public static GeneralPath shapeOf(Shape shape, Color color) {
        List<PathElement> elements = new ArrayList<PathElement>();
        PathIterator pathIt = shape.getPathIterator(new AffineTransform());
        double[] data = new double[6];
        while (!pathIt.isDone()) {
            switch (pathIt.currentSegment(data)) {
                case PathIterator.SEG_MOVETO:
                    elements.add(new MoveTo(data));
                    break;
                case PathIterator.SEG_LINETO:
                    elements.add(new LineTo(data));
                    break;
                case PathIterator.SEG_CLOSE:
                    elements.add(new Close());
                    break;
                case PathIterator.SEG_QUADTO:
                    elements.add(new QuadTo(data));
                    break;
                case PathIterator.SEG_CUBICTO:
                    elements.add(new CubicTo(data));
                    break;
            }
            pathIt.next();
        }
        return new GeneralPath(elements, color, pathIt.getWindingRule(), 0d, true);
    }

    /**
     * Create an outline path of the specified Java 2D Shape and color.
     *
     * @param shape Java 2D shape
     * @param color the color to draw the outline with
     * @return a new general path
     */
    public static GeneralPath outlineOf(Shape shape, double stroke, Color color) {
        List<PathElement> elements = new ArrayList<PathElement>();
        PathIterator pathIt = shape.getPathIterator(new AffineTransform());
        double[] data = new double[6];
        while (!pathIt.isDone()) {
            switch (pathIt.currentSegment(data)) {
                case PathIterator.SEG_MOVETO:
                    elements.add(new MoveTo(data));
                    break;
                case PathIterator.SEG_LINETO:
                    elements.add(new LineTo(data));
                    break;
                case PathIterator.SEG_CLOSE:
                    elements.add(new Close());
                    break;
                case PathIterator.SEG_QUADTO:
                    elements.add(new QuadTo(data));
                    break;
                case PathIterator.SEG_CUBICTO:
                    elements.add(new CubicTo(data));
                    break;
            }
            pathIt.next();
        }
        return new GeneralPath(elements, color, pathIt.getWindingRule(), stroke, false);
    }

}
