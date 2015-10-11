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


import org.openscience.cdk.tools.LoggingToolFactory;

import javax.vecmath.Vector2d;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Defines a bounding box element which the renderer can use to determine the true
 * drawing limits. Using only atom coordinates adjuncts (e.g. hydrogen labels)
 * may be truncated. If a generator provide a bounding box element, then the
 * min/max bounds of all bounding boxes are utilised.
 *
 * @author John May
 * @cdk.module renderbasic
 * @cdk.githash
 */
public final class Bounds implements IRenderingElement {

    /**
     * Minimum x/y coordinates.
     */
    public double minX, minY;

    /**
     * Maximum x/y coordinates.
     */
    public double maxX, maxY;

    /**
     * Know which elements are within this bound box.
     */
    private final ElementGroup elements = new ElementGroup();

    /**
     * Specify the min/max coordinates of the bounding box.
     *
     * @param x1 min x coordinate
     * @param y1 min y coordinate
     * @param x2 max x coordinate
     * @param y2 max y coordinate
     */
    public Bounds(double x1, double y1, double x2, double y2) {
        this.minX = x1;
        this.minY = y1;
        this.maxX = x2;
        this.maxY = y2;
    }

    /**
     * An empty bounding box.
     */
    public Bounds() {
        this(+Double.MAX_VALUE, +Double.MAX_VALUE,
             -Double.MAX_VALUE, -Double.MAX_VALUE);
    }

    /**
     * An bounding box around the specified element.
     */
    public Bounds(IRenderingElement element) {
        this();
        add(element);
    }

    /**
     * Add the specified element bounds.
     */
    public void add(IRenderingElement element) {
        elements.add(element);
        traverse(element);
    }

    /**
     * Ensure the point x,y is included in the bounding box.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void add(double x, double y) {
        if (x < minX) minX = x;
        if (y < minY) minY = y;
        if (x > maxX) maxX = x;
        if (y > maxY) maxY = y;
    }

    /**
     * Add one bounds to another.
     *
     * @param bounds other bounds
     */
    public void add(Bounds bounds) {
        if (bounds.minX < minX) minX = bounds.minX;
        if (bounds.minY < minY) minY = bounds.minY;
        if (bounds.maxX > maxX) maxX = bounds.maxX;
        if (bounds.maxY > maxY) maxY = bounds.maxY;
    }

    /**
     * Add the provided general path to the bounding box.
     *
     * @param path general path
     */
    private void add(GeneralPath path) {
        double[] points = new double[6];
        for (org.openscience.cdk.renderer.elements.path.PathElement element : path.elements) {
            element.points(points);
            switch (element.type()) {
                case MoveTo:
                case LineTo:
                    add(points[0], points[1]);
                    break;
                case QuadTo:
                    add(points[2], points[3]);
                    break;
                case CubicTo:
                    add(points[4], points[5]);
                    break;
            }
        }
    }

    private void traverse(IRenderingElement newElement) {
        Deque<IRenderingElement> stack = new ArrayDeque<>();
        stack.push(newElement);
        while (!stack.isEmpty()) {
            final IRenderingElement element = stack.poll();
            if (element instanceof Bounds) {
                add((Bounds) element);
            } else if (element instanceof GeneralPath) {
                add((GeneralPath) element);
            } else if (element instanceof LineElement) {
                LineElement lineElem = (LineElement) element;
                Vector2d vec = new Vector2d(lineElem.secondPointX-lineElem.firstPointX,
                                            lineElem.secondPointY-lineElem.firstPointY);
                Vector2d ortho = new Vector2d(-vec.y, vec.x);
                ortho.normalize();
                vec.normalize();
                ortho.scale(lineElem.width / 2);  // stroke width
                vec.scale(lineElem.width / 2);    // stroke rounded also makes line longer
                add(lineElem.firstPointX - vec.x + ortho.x, lineElem.firstPointY - vec.y + ortho.y);
                add(lineElem.secondPointX + vec.x + ortho.x, lineElem.secondPointY + vec.y + ortho.y);
                add(lineElem.firstPointX - vec.x - ortho.x, lineElem.firstPointY - vec.y - ortho.y);
                add(lineElem.secondPointX + vec.x - ortho.x, lineElem.secondPointY + vec.y - ortho.y);
            } else if (element instanceof ElementGroup) {
                for (IRenderingElement child : (ElementGroup) element)
                    stack.add(child);
            } else if (element instanceof MarkedElement) {
                stack.add(((MarkedElement)element).element());
            } else {
                // ignored from bounds calculation, we don't really
                // care but log we skipped it
                LoggingToolFactory.createLoggingTool(Bounds.class)
                                  .warn(element.getClass() + " not included in bounds calculation");
            }
        }
    }

    /**
     * Access the root rendering element, it contains all
     * elements added to the bounds so far.
     *
     * @return root rendering element
     */
    public IRenderingElement root() {
        return elements;
    }

    /**
     * Specifies the width of the bounding box.
     *
     * @return the width of the bounding box
     */
    public final double width() {
        return maxX - minX;
    }

    /**
     * Specifies the height of the bounding box.
     *
     * @return the height of the bounding box
     */
    public final double height() {
        return maxY - minY;
    }

    /**
     * The bounds are empty and contain no elements.
     *
     * @return bounds are empty (true) or not (false)
     */
    public final boolean isEmpty() {
        return minX > maxX || minY > maxY;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void accept(IRenderingVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return "{{" + minX + ", " + minY + "} - {" + maxX + ", " + maxY + "}}";
    }
}
