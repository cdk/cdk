package org.openscience.cdk.tools;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * @author maclean
 * @cdk.module test-stereo
 */
public class StereoToolTest extends CDKTestCase {
    
    @Test
    public void positivePointPlaneDistanceTest() {
        // the normal for the Y-Z plane is X
        Vector3d planeNormal = new Vector3d(1, 0, 0);
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
        Vector3d planeNormal = new Vector3d(1, 0, 0);
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
        Point3d axisX = new Point3d(1, 0, 0);
        Point3d axisY = new Point3d(0, 1, 0);
        Point3d axisZ = new Point3d(0, 0, 1);
        Point3d origin = new Point3d(0, 0, 0);
        
        // the normal of X and Y should be Z
        Vector3d normal = StereoTool.getNormal(origin, axisX, axisY);
        Assert.assertEquals(axisZ, normal);
    }

}
