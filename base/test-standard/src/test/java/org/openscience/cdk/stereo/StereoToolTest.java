/* Copyright (C) 2010 Gilleain Torrance <gilleain.torrance@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
package org.openscience.cdk.stereo;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.stereo.StereoTool.SquarePlanarShape;
import org.openscience.cdk.stereo.StereoTool.TetrahedralSign;

/**
 * @author maclean
 * @cdk.module test-standard
 */
public class StereoToolTest extends CDKTestCase {

    private static final Point3d  ORIGIN = new Point3d(0, 0, 0);
    private static final Vector3d XAXIS  = new Vector3d(1, 0, 0);
    private static final Vector3d YAXIS  = new Vector3d(0, 1, 0);
    private static final Vector3d ZAXIS  = new Vector3d(0, 0, 1);

    @Test
    public void positivePointPlaneDistanceTest() {
        // the normal for the Y-Z plane is X
        Vector3d planeNormal = new Vector3d(XAXIS);
        planeNormal.normalize();

        // an arbitrary point in the Y-Z plane
        Point3d pointInPlane = new Point3d(0, 1, 1);

        // make a positive point on the X axis = same direction as the normal
        Point3d pointToMeasurePos = new Point3d(2, 0, 0);

        double distancePos = StereoTool.signedDistanceToPlane(planeNormal, pointInPlane, pointToMeasurePos);
        Assert.assertEquals(2.0, distancePos, 0.1);
    }

    @Test
    public void negativePointPlaneDistanceTest() {
        // the normal for the Y-Z plane is X
        Vector3d planeNormal = new Vector3d(XAXIS);
        planeNormal.normalize();

        // an arbitrary point in the Y-Z plane
        Point3d pointInPlane = new Point3d(0, 1, 1);

        // make a negative point on the X axis = opposite direction to normal
        Point3d pointToMeasureNeg = new Point3d(-2, 0, 0);

        double distance = StereoTool.signedDistanceToPlane(planeNormal, pointInPlane, pointToMeasureNeg);
        Assert.assertEquals(-2.0, distance, 0.1);
    }

    @Test
    public void getNormalFromThreePoints() {
        // these are, of course, points on these axes, not the axis vectors
        Point3d axisXPoint = new Point3d(XAXIS);
        Point3d axisYPoint = new Point3d(YAXIS);

        // the normal of X and Y should be Z
        Vector3d normal = StereoTool.getNormal(ORIGIN, axisXPoint, axisYPoint);
        assertEquals(ZAXIS, normal, 0.0001);
    }

    @Test
    public void tetrahedralPlusAtomsAboveXYClockwiseTest() {
        // above the XY plane
        IAtom baseA = new Atom("C", new Point3d(0, 0, 1));
        IAtom baseB = new Atom("C", new Point3d(1, 0, 1));
        IAtom baseC = new Atom("C", new Point3d(1, 1, 1));

        IAtom positiveApex = new Atom("C", new Point3d(0.5, 0.5, 2));
        TetrahedralSign tetSign = StereoTool.getHandedness(baseC, baseB, baseA, positiveApex);
        Assert.assertEquals(TetrahedralSign.MINUS, tetSign);
    }

    @Test
    public void tetrahedralPlusAtomsAboveXYTest() {
        // above the XY plane
        IAtom baseA = new Atom("C", new Point3d(0, 0, 1));
        IAtom baseB = new Atom("C", new Point3d(1, 0, 1));
        IAtom baseC = new Atom("C", new Point3d(1, 1, 1));

        IAtom positiveApex = new Atom("C", new Point3d(0.5, 0.5, 2));
        TetrahedralSign tetSign = StereoTool.getHandedness(baseA, baseB, baseC, positiveApex);
        Assert.assertEquals(TetrahedralSign.PLUS, tetSign);
    }

    @Test
    public void tetrahedralMinusAtomsAboveXYTest() {
        // above the XY plane
        IAtom baseA = new Atom("C", new Point3d(0, 0, 1));
        IAtom baseB = new Atom("C", new Point3d(1, 0, 1));
        IAtom baseC = new Atom("C", new Point3d(1, 1, 1));

        IAtom negativeApex = new Atom("C", new Point3d(0.5, 0.5, -1));
        TetrahedralSign tetSign = StereoTool.getHandedness(baseA, baseB, baseC, negativeApex);
        Assert.assertEquals(TetrahedralSign.MINUS, tetSign);
    }

    @Test
    public void tetrahedralPlusAtomsBelowXYTest() {
        // below the XY plane
        IAtom baseA = new Atom("C", new Point3d(0, 0, -1));
        IAtom baseB = new Atom("C", new Point3d(1, 0, -1));
        IAtom baseC = new Atom("C", new Point3d(1, 1, -1));

        IAtom positiveApex = new Atom("C", new Point3d(0.5, 0.5, 0));
        TetrahedralSign tetSign = StereoTool.getHandedness(baseA, baseB, baseC, positiveApex);
        Assert.assertEquals(TetrahedralSign.PLUS, tetSign);
    }

    @Test
    public void tetrahedralMinusAtomsBelowXYTest() {
        // below the XY plane
        IAtom baseA = new Atom("C", new Point3d(0, 0, -1));
        IAtom baseB = new Atom("C", new Point3d(1, 0, -1));
        IAtom baseC = new Atom("C", new Point3d(1, 1, -1));

        IAtom negativeApex = new Atom("C", new Point3d(0.5, 0.5, -2));
        TetrahedralSign tetSign = StereoTool.getHandedness(baseA, baseB, baseC, negativeApex);
        Assert.assertEquals(TetrahedralSign.MINUS, tetSign);
    }

    @Test
    public void colinearTestWithColinearPoints() {
        Point3d pointA = new Point3d(1, 1, 1);
        Point3d pointB = new Point3d(2, 2, 2);
        Point3d pointC = new Point3d(3, 3, 3);

        Assert.assertTrue(StereoTool.isColinear(pointA, pointB, pointC));
    }

    @Test
    public void colinearTestWithNearlyColinearPoints() {
        Point3d pointA = new Point3d(1, 1, 1);
        Point3d pointB = new Point3d(2, 2.001, 2);
        Point3d pointC = new Point3d(3, 3, 3);

        Assert.assertTrue(StereoTool.isColinear(pointA, pointB, pointC));
    }

    @Test
    public void colinearTestWithNonColinearPoints() {
        Point3d pointA = new Point3d(1, 1, 1);
        Point3d pointB = new Point3d(2, 3, 2);
        Point3d pointC = new Point3d(3, 3, 3);

        Assert.assertFalse(StereoTool.isColinear(pointA, pointB, pointC));
    }

    @Test
    public void squarePlanarUShapeTest() {
        // all points are in the XY plane
        IAtom atomA = new Atom("C", new Point3d(1, 2, 0));
        IAtom atomB = new Atom("C", new Point3d(1, 1, 0));
        IAtom atomC = new Atom("C", new Point3d(2, 1, 0));
        IAtom atomD = new Atom("C", new Point3d(2, 2, 0));

        SquarePlanarShape shape = StereoTool.getSquarePlanarShape(atomA, atomB, atomC, atomD);
        Assert.assertEquals(SquarePlanarShape.U_SHAPE, shape);
    }

    @Test
    public void squarePlanar4ShapeTest() {
        // all points are in the XY plane
        IAtom atomA = new Atom("C", new Point3d(1, 2, 0));
        IAtom atomB = new Atom("C", new Point3d(2, 1, 0));
        IAtom atomC = new Atom("C", new Point3d(2, 2, 0));
        IAtom atomD = new Atom("C", new Point3d(1, 1, 0));

        SquarePlanarShape shape = StereoTool.getSquarePlanarShape(atomA, atomB, atomC, atomD);
        Assert.assertEquals(SquarePlanarShape.FOUR_SHAPE, shape);
    }

    @Test
    public void squarePlanarZShapeTest() {
        // all points are in the XY plane
        IAtom atomA = new Atom("C", new Point3d(1, 2, 0));
        IAtom atomB = new Atom("C", new Point3d(1, 1, 0));
        IAtom atomC = new Atom("C", new Point3d(2, 2, 0));
        IAtom atomD = new Atom("C", new Point3d(2, 1, 0));

        SquarePlanarShape shape = StereoTool.getSquarePlanarShape(atomA, atomB, atomC, atomD);
        Assert.assertEquals(SquarePlanarShape.Z_SHAPE, shape);
    }

    @Test
    public void trigonalBipyramidalTest() {
        IAtom atomA = new Atom("C", new Point3d(1, 1, 2)); // axis point 1
        IAtom atomB = new Atom("C", new Point3d(1, 1, 1)); // center of plane
        IAtom atomC = new Atom("C", new Point3d(0, 1, 1));
        IAtom atomD = new Atom("C", new Point3d(1, 0, 1));
        IAtom atomE = new Atom("C", new Point3d(2, 2, 1));
        IAtom atomF = new Atom("C", new Point3d(1, 1, 0)); // axis point 2
        Assert.assertTrue(StereoTool.isTrigonalBipyramidal(atomA, atomB, atomC, atomD, atomE, atomF));
    }

    @Test
    public void octahedralTest() {
        IAtom atomA = new Atom("C", new Point3d(2, 2, 2)); // axis point 1
        IAtom atomB = new Atom("C", new Point3d(2, 2, 1)); // center of plane
        IAtom atomC = new Atom("C", new Point3d(1, 3, 1));
        IAtom atomD = new Atom("C", new Point3d(3, 3, 1));
        IAtom atomE = new Atom("C", new Point3d(3, 1, 1));
        IAtom atomF = new Atom("C", new Point3d(1, 3, 1));
        IAtom atomG = new Atom("C", new Point3d(2, 2, 0)); // axis point 2

        Assert.assertTrue(StereoTool.isOctahedral(atomA, atomB, atomC, atomD, atomE, atomF, atomG));
    }

    @Test
    public void squarePlanarTest() {
        IAtom atomA = new Atom("C", new Point3d(1, 2, 0));
        IAtom atomB = new Atom("C", new Point3d(1, 1, 0));
        IAtom atomC = new Atom("C", new Point3d(2, 2, 0));
        IAtom atomD = new Atom("C", new Point3d(2, 1, 0));
        Assert.assertTrue(StereoTool.isSquarePlanar(atomA, atomB, atomC, atomD));
    }

    @Test
    public void allCoplanarTest() {
        Point3d pointA = new Point3d(1, 1, 0);
        Point3d pointB = new Point3d(2, 1, 0);
        Point3d pointC = new Point3d(1, 2, 0);
        Point3d pointD = new Point3d(2, 2, 0);
        Point3d pointE = new Point3d(3, 2, 0);
        Point3d pointF = new Point3d(3, 3, 0);

        Vector3d normal = StereoTool.getNormal(pointA, pointB, pointC);
        Assert.assertTrue(StereoTool.allCoplanar(normal, pointA, pointB, pointC, pointD, pointE, pointF));
    }

    @Test
    public void getStereoACWTest() {
        IAtom closestAtomToViewer = new Atom("F", new Point3d(1, 1, 1));
        IAtom highestCIPPriority = new Atom("I", new Point3d(0, 1, 2));
        IAtom middleCIPPriority = new Atom("Br", new Point3d(0, 0, 0));
        IAtom nearlylowestCIPPriority = new Atom("Cl", new Point3d(0, 2, 0));
        Assert.assertEquals(Stereo.ANTI_CLOCKWISE, StereoTool.getStereo(closestAtomToViewer, highestCIPPriority,
                middleCIPPriority, nearlylowestCIPPriority));
    }

    @Test
    public void getStereoCWTest() {
        IAtom closestAtomToViewer = new Atom("F", new Point3d(1, 1, 1));
        IAtom highestCIPPriority = new Atom("I", new Point3d(0, 1, 2));
        IAtom middleCIPPriority = new Atom("Br", new Point3d(0, 2, 0));
        IAtom nearlylowestCIPPriority = new Atom("Cl", new Point3d(0, 0, 0));
        Assert.assertEquals(Stereo.CLOCKWISE, StereoTool.getStereo(closestAtomToViewer, highestCIPPriority,
                middleCIPPriority, nearlylowestCIPPriority));
    }
}
