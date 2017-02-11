/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.renderer.generators.standard;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

/**
 * Immutable convex hull that is the smallest set of convex points that surround a shape.
 *
 * <pre>{@code
 * ConvexHull hull = ConvexHull.ofShape(shape);
 *
 * // the hull can be transformed
 * hull = hull.transform(new AffineTransform());
 *
 * // given a line, a point on the hull can be found that intersects the line
 * Point2D point == hull.intersect(new Line2D.Double(...), 0);
 * }</pre>
 *
 * @author John May
 */
final class ConvexHull {

    /** The convex hull. */
    private final Shape hull;

    /**
     * Internal constructor, the hull is an argument.
     *
     * @param hull the convex hull
     */
    private ConvexHull(final Shape hull) {
        this.hull = hull;
    }

    /**
     * Calculate the convex hull of a shape.
     *
     * @param shape a Java 2D shape
     * @return the convex hull
     */
    public static ConvexHull ofShape(final Shape shape) {
        return ofShapes(Collections.singletonList(shape));
    }

    /**
     * Calculate the convex hull of multiple shapes.
     *
     * @param shapes Java 2D shapes
     * @return the convex hull
     */
    public static ConvexHull ofShapes(final List<Shape> shapes) {
        final Path2D combined = new Path2D.Double();
        for (Shape shape : shapes)
            combined.append(shape, false);
        return new ConvexHull(shapeOf(grahamScan(pointsOf(combined))));
    }

    /**
     * The outline of the hull as a Java 2D shape.
     *
     * @return outline of the hull
     */
    Shape outline() {
        return hull;
    }

    /**
     * Apply the provided transformation to the convex hull.
     *
     * @param transform a transform
     * @return a new transformed hull
     */
    ConvexHull transform(final AffineTransform transform) {
        return new ConvexHull(transform.createTransformedShape(hull));
    }

    /**
     * Convert a list of points to a shape.
     *
     * @param points list of points
     * @return a shape
     */
    static Shape shapeOf(List<Point2D> points) {
        Path2D path = new Path2D.Double();
        if (!points.isEmpty()) {
            path.moveTo(points.get(0).getX(), points.get(0).getY());
            for (Point2D point : points)
                path.lineTo(point.getX(), point.getY());
            path.closePath();
        }
        return path;
    }

    /**
     * Convert a Java 2D shape to a list of points.
     *
     * @param shape a shape
     * @return list of point
     */
    static List<Point2D> pointsOf(final Shape shape) {
        final List<Point2D> points = new ArrayList<Point2D>();
        final double[] coordinates = new double[6];
        for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i.next()) {
            switch (i.currentSegment(coordinates)) {
                case PathIterator.SEG_CLOSE:
                    break;
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    points.add(new Point2D.Double(coordinates[0], coordinates[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    points.add(new Point2D.Double(coordinates[0], coordinates[1]));
                    points.add(new Point2D.Double(coordinates[2], coordinates[3]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    points.add(new Point2D.Double(coordinates[0], coordinates[1]));
                    points.add(new Point2D.Double(coordinates[2], coordinates[3]));
                    points.add(new Point2D.Double(coordinates[4], coordinates[5]));
                    break;
            }
        }

        if (!points.isEmpty() && points.get(points.size() - 1).equals(points.get(0))) {
            points.remove(points.size() - 1);
        }
        return points;
    }

    /**
     * The Graham Scan algorithm determines the points belonging to the convex hull in O(n lg n).
     *
     * @param points set of points
     * @return points in the convex hull
     * @see <a href="http://en.wikipedia.org/wiki/Graham_scan">Graham scan, Wikipedia</a>
     */
    static List<Point2D> grahamScan(final List<Point2D> points) {

        if (points.size() <= 3) return new ArrayList<Point2D>(points);

        Collections.sort(points, new CompareYThenX());
        Collections.sort(points, new PolarComparator(points.get(0)));

        Deque<Point2D> hull = new ArrayDeque<Point2D>();

        hull.push(points.get(0));
        hull.push(points.get(1));
        hull.push(points.get(2));

        for (int i = 3; i < points.size(); i++) {
            Point2D top = hull.pop();
            while (!hull.isEmpty() && !isLeftTurn(hull.peek(), top, points.get(i))) {
                top = hull.pop();
            }
            hull.push(top);
            hull.push(points.get(i));
        }

        return new ArrayList<Point2D>(hull);
    }

    /**
     * Determine the minimum intersection of a line and the outline of a shape (specified as a list
     * of points).
     *
     * @param outline the outline of a shape
     * @param line    the line
     * @return the intersection
     */
    private Point2D intersect(List<Point2D> outline, Line2D line) {

        Point2D previousPoint = outline.get(outline.size() - 1);
        for (Point2D point : outline) {

            Line2D currentLine = new Line2D.Double(point.getX(), point.getY(), previousPoint.getX(),
                    previousPoint.getY());
            if (line.intersectsLine(currentLine)) {
                return lineLineIntersect(currentLine, line);
            }
            previousPoint = point;
        }
        return new Point2D.Double(line.getX1(), line.getY1());
    }

    /**
     * Determine the intersect of a line (between a and b) with the hull.
     *
     * @param a first point of the line
     * @param b second points of the line
     * @return intersection, or null if not found
     */
    Point2D intersect(Point2D a, Point2D b) {
        return intersect(pointsOf(hull), new Line2D.Double(a.getX(), a.getY(), b.getX(), b.getY()));
    }

    /**
     * Calculate the intersection of two lines.
     *
     * @param lineA a line
     * @param lineB another line
     * @return the point where the two lines intersect (or null)
     */
    public static Point2D lineLineIntersect(final Line2D lineA, final Line2D lineB) {
        return lineLineIntersect(lineA.getX1(), lineA.getY1(), lineA.getX2(), lineA.getY2(), lineB.getX1(),
                lineB.getY1(), lineB.getX2(), lineB.getY2());

    }

    /**
     * Calculate the intersection of two lines described by the points (x1,y1 -> x2,y2) and (x3,y3
     * -> x4,y4).
     *
     * @param x1 first x coordinate of line 1
     * @param y1 first y coordinate of line 1
     * @param x2 second x coordinate of line 1
     * @param y2 second y coordinate of line 1
     * @param x3 first x coordinate of line 2
     * @param y3 first y coordinate of line 2
     * @param x4 first x coordinate of line 2
     * @param y4 first y coordinate of line 2
     * @return the point where the two lines intersect (or null)
     * @see <a href="http://en.wikipedia.org/wiki/Line–line_intersection">Line-line intersection,
     * Wikipedia</a>
     */
    static Point2D lineLineIntersect(final double x1, final double y1, final double x2, final double y2,
            final double x3, final double y3, final double x4, final double y4) {

        final double x = ((x2 - x1) * (x3 * y4 - x4 * y3) - (x4 - x3) * (x1 * y2 - x2 * y1))
                / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
        final double y = ((y3 - y4) * (x1 * y2 - x2 * y1) - (y1 - y2) * (x3 * y4 - x4 * y3))
                / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));

        return new Point2D.Double(x, y);
    }

    /**
     * Sort points counter clockwise (polar order) around a reference point.
     */
    static final class PolarComparator implements Comparator<Point2D> {

        private Point2D reference;

        PolarComparator(Point2D reference) {
            this.reference = reference;
        }

        /**{@inheritDoc} */
        @Override
        public int compare(Point2D a, Point2D b) {
            final double deltaX1 = a.getX() - reference.getX();
            final double deltaY1 = a.getY() - reference.getY();
            final double deltaX2 = b.getX() - reference.getX();
            final double deltaY2 = b.getY() - reference.getY();

            if (deltaY1 >= 0 && deltaY2 < 0)
                return -1;
            else if (deltaY2 >= 0 && deltaY1 < 0)
                return +1;
            else if (deltaY1 == 0 && deltaY2 == 0) { // corner case
                if (deltaX1 >= 0 && deltaX2 < 0)
                    return -1;
                else if (deltaX2 >= 0 && deltaX1 < 0)
                    return +1;
                else
                    return 0;
            } else
                return -winding(reference, a, b); // both above or below
        }
    }

    /**
     * Compares points by the y coordinate and then the x if the y's are equal.
     */
    static final class CompareYThenX implements Comparator<Point2D> {

        /**{@inheritDoc} */
        @Override
        public int compare(Point2D a, Point2D b) {
            if (a.getY() < b.getY()) return -1;
            if (a.getY() > b.getY()) return +1;
            if (a.getX() < b.getX()) return -1;
            if (a.getX() > b.getX()) return +1;
            return 0;
        }
    }

    /**
     * Determine if the three points make a left turn.
     *
     * @param a first point
     * @param b second point
     * @param c third point
     * @return whether the points make a left turn
     */
    private static boolean isLeftTurn(Point2D a, Point2D b, Point2D c) {
        return winding(a, b, c) > 0;
    }

    /**
     * Determine the winding of three points. The winding is the sign of the space - the parity.
     *
     * @param a first point
     * @param b second point
     * @param c third point
     * @return winding, -1=cw, 0=straight, +1=ccw
     */
    private static int winding(Point2D a, Point2D b, Point2D c) {
        return (int) Math.signum((b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY())
                * (c.getX() - a.getX()));
    }
}
