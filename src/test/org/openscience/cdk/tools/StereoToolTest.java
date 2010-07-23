package org.openscience.cdk.tools;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.tools.StereoTool.TetrahedralSign;

/**
 * @author maclean
 * @cdk.module test-stereo
 */
public class StereoToolTest extends CDKTestCase {

    private static final Point3d ORIGIN = new Point3d(0, 0, 0);
    private static final Vector3d XAXIS = new Vector3d(1, 0, 0);
    private static final Vector3d YAXIS = new Vector3d(0, 1, 0);
    private static final Vector3d ZAXIS = new Vector3d(0, 0, 1);
    
    @Test
    public void positivePointPlaneDistanceTest() {
        // the normal for the Y-Z plane is X
        Vector3d planeNormal = new Vector3d(XAXIS);
        planeNormal.normalize();
        
        // an arbitrary point in the Y-Z plane
        Point3d pointInPlane = new Point3d(0, 1, 1);
        
        // make a positive point on the X axis = same direction as the normal
        Point3d pointToMeasurePos = new Point3d(2, 0, 0);
        
        double distancePos = StereoTool.signedDistanceToPlane(
                planeNormal, pointInPlane, pointToMeasurePos);
        Assert.assertEquals(2.0, distancePos);
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
        
        double distance = StereoTool.signedDistanceToPlane(
                planeNormal, pointInPlane, pointToMeasureNeg);
        Assert.assertEquals(-2.0, distance);
    }
    
    @Test
    public void getNormalFromThreePoints() {
        // these are, of course, points on these axes, not the axis vectors
        Point3d axisXPoint = new Point3d(XAXIS);
        Point3d axisYPoint = new Point3d(YAXIS);
        
        // the normal of X and Y should be Z
        Vector3d normal = StereoTool.getNormal(ORIGIN, axisXPoint, axisYPoint);
        Assert.assertEquals(ZAXIS, normal);
    }
    
    @Test
    public void tetrahedralPlusAtomsTest() {
        IAtom baseA = new Atom("C", new Point3d(ORIGIN));
        IAtom baseB = new Atom("C", new Point3d(XAXIS));
        IAtom baseC = new Atom("C", new Point3d(YAXIS));
        
        IAtom positiveApex = new Atom("C", new Point3d(0.5, 0.5, 1));
        TetrahedralSign tetSign =
            StereoTool.getHandedness(baseA, baseB, baseC, positiveApex);
        Assert.assertEquals(TetrahedralSign.PLUS, tetSign);
    }
    
    @Test
    public void tetrahedralMinusAtomsTest() {
        IAtom baseA = new Atom("C", new Point3d(ORIGIN));
        IAtom baseB = new Atom("C", new Point3d(XAXIS));
        IAtom baseC = new Atom("C", new Point3d(YAXIS));
        
        IAtom negativeApex = new Atom("C", new Point3d(0.5, 0.5, -1));
        TetrahedralSign tetSign =
            StereoTool.getHandedness(baseA, baseB, baseC, negativeApex);
        Assert.assertEquals(TetrahedralSign.MINUS, tetSign);
    }
    
    @Test
    public void colinearTestWithColinearPoints() {
        Point3d pointA = new Point3d(1, 1, 1);
        Point3d pointB = new Point3d(2, 2, 2);
        Point3d pointC = new Point3d(3, 3, 3);
        
        Assert.assertTrue(StereoTool.colinear(pointA, pointB, pointC));
    }
    
    @Test
    public void colinearTestWithNearlyColinearPoints() {
        Point3d pointA = new Point3d(1, 1, 1);
        Point3d pointB = new Point3d(2, 2.001, 2);
        Point3d pointC = new Point3d(3, 3, 3);
        
        Assert.assertTrue(StereoTool.colinear(pointA, pointB, pointC));
    }

    @Test
    public void colinearTestWithNonColinearPoints() {
        Point3d pointA = new Point3d(1, 1, 1);
        Point3d pointB = new Point3d(2, 3, 2);
        Point3d pointC = new Point3d(3, 3, 3);
        
        Assert.assertFalse(StereoTool.colinear(pointA, pointB, pointC));
    }
}
