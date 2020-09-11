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

import org.junit.Test;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConvexHullTest {

    @Test
    public void ofOvalShape() throws Exception {
        RoundRectangle2D oval = new RoundRectangle2D.Double(-5, -5, 10, 10, 5, 5);
        ConvexHull hull = ConvexHull.ofShape(oval);
        Rectangle2D bounds = hull.outline().getBounds2D();
        assertThat(bounds.getMinX(), closeTo(-5, 0.01));
        assertThat(bounds.getMinY(), closeTo(-5, 0.01));
        assertThat(bounds.getMaxX(), closeTo(5, 0.01));
        assertThat(bounds.getMaxY(), closeTo(5, 0.01));
    }

    @Test
    public void ofTriangle() throws Exception {
        Path2D path = new Path2D.Double();
        path.moveTo(-5, 0);
        path.lineTo(0, 10);
        path.lineTo(5, 0);
        path.closePath();
        ConvexHull hull = ConvexHull.ofShape(path);
        Rectangle2D bounds = hull.outline().getBounds2D();
        assertThat(bounds.getMinX(), closeTo(-5, 0.01));
        assertThat(bounds.getMinY(), closeTo(0, 0.01));
        assertThat(bounds.getMaxX(), closeTo(5, 0.01));
        assertThat(bounds.getMaxY(), closeTo(10, 0.01));
    }

    @Test
    public void ofRectangles() throws Exception {
        Rectangle2D rect1 = new Rectangle2D.Double(-10, -10, 5, 5);
        Rectangle2D rect2 = new Rectangle2D.Double(15, 16, 20, 25);
        Rectangle2D rect3 = new Rectangle2D.Double(-15, 6, 2, 5);
        ConvexHull hull = ConvexHull.ofShapes(Arrays.<Shape> asList(rect1, rect2, rect3));
        Rectangle2D bounds = hull.outline().getBounds2D();
        assertThat(bounds.getMinX(), closeTo(-15, 0.01));
        assertThat(bounds.getMinY(), closeTo(-10, 0.01));
        assertThat(bounds.getMaxX(), closeTo(35, 0.01));
        assertThat(bounds.getMaxY(), closeTo(41, 0.01));
    }

    @Test
    public void transformDoesNotModifyOriginal() throws Exception {
        Rectangle2D rect1 = new Rectangle2D.Double(-10, -10, 5, 5);
        Rectangle2D rect2 = new Rectangle2D.Double(15, 16, 20, 25);
        Rectangle2D rect3 = new Rectangle2D.Double(-15, 6, 2, 5);
        ConvexHull hull = ConvexHull.ofShapes(Arrays.<Shape> asList(rect1, rect2, rect3));

        ConvexHull transformedHull = hull.transform(AffineTransform.getTranslateInstance(10, 15));

        Rectangle2D bounds = hull.outline().getBounds2D();
        Rectangle2D transformedBounds = transformedHull.outline().getBounds2D();

        assertThat(bounds.getMinX(), not(closeTo(transformedBounds.getMinX(), 0.01)));
        assertThat(bounds.getMinY(), not(closeTo(transformedBounds.getMinY(), 0.01)));
        assertThat(bounds.getMaxX(), not(closeTo(transformedBounds.getMaxX(), 0.01)));
        assertThat(bounds.getMaxY(), not(closeTo(transformedBounds.getMaxY(), 0.01)));
    }

    @Test
    public void testShapeOf() throws Exception {
        List<Point2D> points = Arrays.<Point2D> asList(new Point2D.Double(-5d, -5d), new Point2D.Double(-5d, 5d),
                new Point2D.Double(5d, 5d), new Point2D.Double(5d, -5d));
        Rectangle2D bounds = ConvexHull.shapeOf(points).getBounds2D();
        assertThat(bounds.getMinX(), closeTo(-5, 0.01));
        assertThat(bounds.getMinY(), closeTo(-5, 0.01));
        assertThat(bounds.getMaxX(), closeTo(5, 0.01));
        assertThat(bounds.getMaxY(), closeTo(5, 0.01));
    }

    @Test
    public void emptyShapeDoesBreak() throws Exception {
        Shape shape = ConvexHull.shapeOf(Collections.<Point2D> emptyList());
    }

    @Test
    public void testPointsOf() throws Exception {
        Rectangle2D rect = new Rectangle2D.Double(-5, -5, 10, 10);
        List<Point2D> points = ConvexHull.pointsOf(rect);
        assertThat(points.size(), is(4));
        assertThat(points.get(0).getX(), closeTo(-5, 0.01));
        assertThat(points.get(0).getY(), closeTo(-5, 0.01));
        assertThat(points.get(1).getX(), closeTo(5, 0.01));
        assertThat(points.get(1).getY(), closeTo(-5, 0.01));
        assertThat(points.get(2).getX(), closeTo(5, 0.01));
        assertThat(points.get(2).getY(), closeTo(5, 0.01));
        assertThat(points.get(3).getX(), closeTo(-5, 0.01));
        assertThat(points.get(3).getY(), closeTo(5, 0.01));
    }

    @Test
    public void intersectionOfRect() throws Exception {
        Rectangle2D rect = new Rectangle2D.Double(-5, -5, 10, 10);
        ConvexHull hull = ConvexHull.ofShape(rect);
        Point2D intersect = hull.intersect(new Point2D.Double(0, 0), new Point2D.Double(10, 0));
        assertThat(intersect.getX(), closeTo(5, 0.01));
        assertThat(intersect.getY(), closeTo(0, 0.01));
    }

}
