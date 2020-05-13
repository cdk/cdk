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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VecmathUtilTest {

    @Test
    public void testToAwtPoint() throws Exception {
        Point2D p = VecmathUtil.toAwtPoint(new Point2d(4, 2));
        assertThat(p.getX(), closeTo(4d, 0.01));
        assertThat(p.getY(), closeTo(2d, 0.01));
    }

    @Test
    public void testToVecmathPoint() throws Exception {
        Point2d p = VecmathUtil.toVecmathPoint(new Point2D.Double(4, 2));
        assertThat(p.x, closeTo(4d, 0.01));
        assertThat(p.y, closeTo(2d, 0.01));
    }

    @Test
    public void testNewUnitVector() throws Exception {
        Vector2d unit = VecmathUtil.newUnitVector(new Point2d(4, 2), new Point2d(6, 7));
        assertThat(unit.x, closeTo(0.371d, 0.01));
        assertThat(unit.y, closeTo(0.928d, 0.01));
        assertThat(unit.length(), closeTo(1d, 0.01));
    }

    @Test
    public void testNewUnitVectorFromBond() throws Exception {
        IAtom a1 = mock(IAtom.class);
        IAtom a2 = mock(IAtom.class);
        when(a1.getPoint2d()).thenReturn(new Point2d(0, 1));
        when(a2.getPoint2d()).thenReturn(new Point2d(1, 0));
        IBond bond = mock(IBond.class);
        when(bond.getOther(a1)).thenReturn(a2);
        when(bond.getOther(a2)).thenReturn(a1);
        Vector2d unit = VecmathUtil.newUnitVector(a1, bond);
        assertThat(unit.x, closeTo(0.707d, 0.01));
        assertThat(unit.y, closeTo(-0.707d, 0.01));
        assertThat(unit.length(), closeTo(1d, 0.01));
    }

    @Test
    public void testNewUnitVectors() throws Exception {
        IAtom fromAtom = mock(IAtom.class);
        IAtom toAtom1 = mock(IAtom.class);
        IAtom toAtom2 = mock(IAtom.class);
        IAtom toAtom3 = mock(IAtom.class);
        when(fromAtom.getPoint2d()).thenReturn(new Point2d(4, 2));
        when(toAtom1.getPoint2d()).thenReturn(new Point2d(-5, 3));
        when(toAtom2.getPoint2d()).thenReturn(new Point2d(6, -4));
        when(toAtom3.getPoint2d()).thenReturn(new Point2d(7, 5));
        List<Vector2d> vectors = VecmathUtil.newUnitVectors(fromAtom, Arrays.asList(toAtom1, toAtom2, toAtom3));

        assertThat(vectors.size(), is(3));
        assertThat(vectors.get(0).x, closeTo(-0.993, 0.01));
        assertThat(vectors.get(0).y, closeTo(0.110, 0.01));
        assertThat(vectors.get(1).x, closeTo(0.316, 0.01));
        assertThat(vectors.get(1).y, closeTo(-0.948, 0.01));
        assertThat(vectors.get(2).x, closeTo(0.707, 0.01));
        assertThat(vectors.get(2).y, closeTo(0.707, 0.01));
    }

    @Test
    public void testNewPerpendicularVector() throws Exception {
        Vector2d perpendicular = VecmathUtil.newPerpendicularVector(new Vector2d(5, 2));
        assertThat(perpendicular.x, closeTo(-2d, 0.01));
        assertThat(perpendicular.y, closeTo(5d, 0.01));
    }

    @Test
    public void testScale() throws Exception {
        Vector2d vector = VecmathUtil.scale(new Vector2d(4, 2), 2.5);
        assertThat(vector.x, closeTo(10d, 0.01));
        assertThat(vector.y, closeTo(5d, 0.01));
    }

    @Test
    public void testSum() throws Exception {
        Vector2d vector = VecmathUtil.sum(new Vector2d(4, 2), new Vector2d(2, 5));
        assertThat(vector.x, closeTo(6d, 0.01));
        assertThat(vector.y, closeTo(7d, 0.01));
    }

    @Test
    public void testNegate() throws Exception {
        Vector2d vector = VecmathUtil.negate(new Vector2d(4, 2));
        assertThat(vector.x, closeTo(-4d, 0.01));
        assertThat(vector.y, closeTo(-2d, 0.01));
    }

    @Test
    public void testAdjacentLength() throws Exception {
        double length = VecmathUtil.adjacentLength(new Vector2d(2, 4), new Vector2d(9, 4), 6d);
        assertThat(length, closeTo(4.94, 0.01));
    }

    @Test
    public void testAverage() throws Exception {
        Vector2d mean = VecmathUtil.average(Arrays.asList(new Vector2d(0.5, 0.5), new Vector2d(0.5, -0.5)));
        assertThat(mean.x, closeTo(0.5d, 0.01));
        assertThat(mean.y, closeTo(0d, 0.01));
    }

    @Test
    public void testGetNearestVector1() throws Exception {
        // not unit vectors, but okay for test
        Vector2d nearest = VecmathUtil.getNearestVector(new Vector2d(0, 1),
                Arrays.asList(new Vector2d(0.5, 0.5), new Vector2d(0.5, -0.5)));
        assertThat(nearest.x, closeTo(0.5d, 0.01));
        assertThat(nearest.y, closeTo(0.5d, 0.01));
    }

    @Test
    public void testGetNearestVector2() throws Exception {
        // not unit vectors, but okay for test
        Vector2d nearest = VecmathUtil.getNearestVector(new Vector2d(0, -1),
                Arrays.asList(new Vector2d(0.5, 0.5), new Vector2d(0.5, -0.5)));
        assertThat(nearest.x, closeTo(0.5d, 0.01));
        assertThat(nearest.y, closeTo(-0.5d, 0.01));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNearestVectorComplainsWhenNoVectorsProvided() throws Exception {
        VecmathUtil.getNearestVector(new Vector2d(1, 0), Collections.<Vector2d> emptyList());
    }

    @Test
    public void testGetNearestVectorFromBonds() throws Exception {

        IAtom a1 = mock(IAtom.class);
        IAtom a2 = mock(IAtom.class);
        IAtom a3 = mock(IAtom.class);
        IAtom a4 = mock(IAtom.class);
        IBond b1 = mock(IBond.class);
        IBond b2 = mock(IBond.class);
        IBond b3 = mock(IBond.class);
        when(b1.getOther(a1)).thenReturn(a2);
        when(b2.getOther(a1)).thenReturn(a3);
        when(b3.getOther(a1)).thenReturn(a4);
        when(a1.getPoint2d()).thenReturn(new Point2d(0, 0));
        when(a2.getPoint2d()).thenReturn(new Point2d(0, 1));
        when(a3.getPoint2d()).thenReturn(new Point2d(1, 0));
        when(a4.getPoint2d()).thenReturn(new Point2d(1, 1)); // this one is
                                                             // found

        Vector2d nearest = VecmathUtil.getNearestVector(new Vector2d(0.5, 0.5), a1, Arrays.asList(b1, b2, b3));
        assertThat(nearest.x, closeTo(0.707d, 0.01));
        assertThat(nearest.y, closeTo(0.707d, 0.01));
    }

    @Test
    public void intersection1() {
        Tuple2d intersect = VecmathUtil.intersection(new Point2d(1, 1), new Vector2d(0, 1), new Point2d(1, 0),
                new Vector2d(1, 0));
        assertThat(intersect.x, closeTo(1.0, 0.01));
        assertThat(intersect.y, closeTo(0.0, 0.01));
    }

    @Test
    public void intersection2() {
        Tuple2d intersect = VecmathUtil.intersection(new Point2d(6, 1), new Vector2d(-4, -2), new Point2d(1, 6),
                new Vector2d(2, 4));
        assertThat(intersect.x, closeTo(-4, 0.01));
        assertThat(intersect.y, closeTo(-4, 0.01));
    }

    @Test
    public void parallelLines() {
        Tuple2d intersect = VecmathUtil.intersection(new Point2d(0, 1), new Vector2d(0, 1), new Point2d(0, -1),
                new Vector2d(0, 1));
        assertTrue(Double.isNaN(intersect.x));
        assertTrue(Double.isNaN(intersect.y));
    }

    @Test
    public void sweepEast() {
        assertThat(VecmathUtil.extent(new Vector2d(1, 0)), is(closeTo(Math.toRadians(0), 0.01)));
    }

    @Test
    public void sweepNorth() {
        assertThat(VecmathUtil.extent(new Vector2d(0, 1)), is(closeTo(Math.toRadians(90), 0.01)));
    }

    @Test
    public void sweepWest() {
        assertThat(VecmathUtil.extent(new Vector2d(-1, 0)), is(closeTo(Math.toRadians(180), 0.01)));
    }

    @Test
    public void sweepSouth() {
        assertThat(VecmathUtil.extent(new Vector2d(0, -1)), is(closeTo(Math.toRadians(270), 0.01)));
    }

    @Test
    public void largestGapSouthWest() {
        Vector2d vector = VecmathUtil.newVectorInLargestGap(Arrays.asList(new Vector2d(0, 1), new Vector2d(1, 0)));
        assertThat(vector.x, closeTo(-0.707d, 0.01));
        assertThat(vector.y, closeTo(-0.707d, 0.01));
        assertThat(vector.length(), closeTo(1d, 0.01));
    }

    @Test
    public void largestGapEast() {
        Vector2d vector = VecmathUtil.newVectorInLargestGap(Arrays
                .asList(new Vector2d(1, 1), new Vector2d(1, -1), new Vector2d(-1, -1), new Vector2d(-1, 1),
                        new Vector2d(-1, 0), new Vector2d(0, 1), new Vector2d(0, -1)));
        assertThat(vector.x, closeTo(1, 0.01));
        assertThat(vector.y, closeTo(0, 0.01));
        assertThat(vector.length(), closeTo(1d, 0.01));
    }
}
