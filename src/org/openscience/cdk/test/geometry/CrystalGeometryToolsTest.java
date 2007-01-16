/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.test.geometry;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.test.CDKTestCase;

/**
 * This class defines regression tests that should ensure that the source code
 * of the org.openscience.cdk.geometry.CrystalGeometryTools is not broken.
 * All methods that start with test are regression tests, e.g.
 * <code>testNotionalToCartesian()</code>.
 *
 * @cdk.module test-standard
 *
 * @author     Egon Willighagen
 * @cdk.created    2003-08-19
 *
 * @see org.openscience.cdk.geometry.CrystalGeometryTools
 */
public class CrystalGeometryToolsTest extends CDKTestCase {

    public CrystalGeometryToolsTest(String name) {
        super(name);
    }
    
    public void setUp() {}
    
    /**
     * Defines a set of tests that can be used in automatic regression testing
     * with JUnit.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(CrystalGeometryToolsTest.class);
        return suite;
    }
    
    /**
     * This method tests the conversion of notional coordinates to 
     * cartesian coordinates. The test assumes that the 
     * <code>CrystalGeometryTools.notionalToCartesian()</code> methods
     * places the a axis on the x axis and the b axis in the xy plane.
     */
    public void testNotionalToCartesian_double_double_double_double_double_double() {
        Vector3d[] cardAxes = CrystalGeometryTools.notionalToCartesian(
            1.0, 2.0, 3.0, 90.0, 90.0, 90.0
        );
        // the a axis
        assertEquals(1.0, cardAxes[0].x, 0.001);
        assertEquals(0.0, cardAxes[0].y, 0.001);
        assertEquals(0.0, cardAxes[0].z, 0.001);
        // the b axis
        assertEquals(0.0, cardAxes[1].x, 0.001);
        assertEquals(2.0, cardAxes[1].y, 0.001);
        assertEquals(0.0, cardAxes[1].z, 0.001);
        // the c axis
        assertEquals(0.0, cardAxes[2].x, 0.001);
        assertEquals(0.0, cardAxes[2].y, 0.001);
        assertEquals(3.0, cardAxes[2].z, 0.001);
    }

    /**
     * This method tests the conversion of cartesian coordinates to 
     * notional coordinates.
     */
    public void testCartesianToNotional_Vector3d_Vector3d_Vector3d() {
        Vector3d a = new Vector3d(1.0, 0.0, 0.0);
        Vector3d b = new Vector3d(0.0, 2.0, 0.0);
        Vector3d c = new Vector3d(0.0, 0.0, 3.0);
        double[] notionalCoords = CrystalGeometryTools.cartesianToNotional(
            a,b,c
        );
        assertEquals(1.0, notionalCoords[0], 0.001);
        assertEquals(2.0, notionalCoords[1], 0.001);
        assertEquals(3.0, notionalCoords[2], 0.001);
        assertEquals(90.0, notionalCoords[3], 0.001);
        assertEquals(90.0, notionalCoords[4], 0.001);
        assertEquals(90.0, notionalCoords[5], 0.001);
    }
    
    /**
     * This method tests the conversion of atomic fractional coordinates to
     * cartesian coordinates.
     */
    public void testFractionalToCartesian_Vector3d_Vector3d_Vector3d_Point3d() {
        Vector3d a = new Vector3d(1.0, 0.0, 0.0);
        Vector3d b = new Vector3d(0.0, 2.0, 0.0);
        Vector3d c = new Vector3d(0.0, 0.0, 3.0);
        Point3d fractCoord = new Point3d(0.25, 0.50, 0.75);
        Point3d cartCoord = CrystalGeometryTools.fractionalToCartesian(
            a, b, c, fractCoord
        );
        assertEquals(0.25, cartCoord.x, 0.001);
        assertEquals(1.0,  cartCoord.y, 0.001);
        assertEquals(2.25, cartCoord.z, 0.001);
    }

    /**
     * This method tests the conversion of atomic fractional coordinates to
     * cartesian coordinates. The specific numbers are taken from 9603.res.
     *
     * <p>Uncommented because it fails, which might be correct and needs
     * to be checked by manually calculating the values.
     */
    /** public void testFractionalToCartesian2() {
        double[][] cardAxes = CrystalGeometryTools.notionalToCartesian(
            9.3323, 10.1989, 11.2477, 69.043, 74.441, 77.821
        );
        double[] a = {cardAxes[0][0], cardAxes[0][1], cardAxes[0][2]};
        double[] b = {cardAxes[1][0], cardAxes[1][1], cardAxes[1][2]};
        double[] c = {cardAxes[2][0], cardAxes[2][1], cardAxes[2][2]};
        
        double[] fractCoords = {0.517879, 0.258121, 0.698477};
        double[] cartCoords = CrystalGeometryTools.fractionalToCartesian(
            a, b, c, fractCoords
        );
        assertEquals(7.005, cartCoords[0], 0.001);
        assertEquals(8.441, cartCoords[1], 0.001);
        assertEquals(3.096, cartCoords[2], 0.001);
    } **/

    /**
     * This method tests the conversion of atomic cartesian coordinates to
     * fractional coordinates.
     */
    public void testCartesianToFractional_Vector3d_Vector3d_Vector3d_Point3d() {
        Vector3d a = new Vector3d(1.0, 0.0, 0.0);
        Vector3d b = new Vector3d(0.0, 2.0, 0.0);
        Vector3d c = new Vector3d(0.0, 0.0, 3.0);
        Point3d cartCoord = new Point3d(0.25, 1.0, 2.25);
        Point3d fractCoord = CrystalGeometryTools.cartesianToFractional(
            a, b, c, cartCoord
        );
        assertEquals(0.25, fractCoord.x, 0.001);
        assertEquals(0.50, fractCoord.y, 0.001);
        assertEquals(0.75, fractCoord.z, 0.001);
    }

    /**
     * This method tests the calculation of axis lengths.
     */
    public void testCalcAxisLength() {
        Vector3d a = new Vector3d(1.0, 1.0, 1.0);
        double length = a.length();
        assertEquals(Math.sqrt(3.0), length, 0.001);
    }

    /**
     * This method tests the calculation of axis lengths too, like
     * <code>testCalcAxisLength()</code>.
     */
    public void testCalcAxisLength2() {
        Vector3d a = new Vector3d(1.0, 0.0, 0.0);
        double length = a.length();
        assertEquals(1.0, length, 0.001);
        Vector3d b = new Vector3d(0.0, 1.0, 0.0);
        length = b.length();
        assertEquals(1.0, length, 0.001);
        Vector3d c = new Vector3d(0.0, 0.0, 1.0);
        length = c.length();
        assertEquals(1.0, length, 0.001);
    }

    /**
     * This method tests the calculation of the angle between two axes.
     */
    public void testCalcAngle() {
        Vector3d b = new Vector3d(0.0, 2.0, 0.0);
        Vector3d c = new Vector3d(0.0, 0.0, 3.0);
        double angle = b.angle(c)*180.0/Math.PI;
        assertEquals(90.0, angle, 0.001);
    }
    
    /**
     * This method tests the calculation of the angle between two axes too.
     */
    public void testCalcAngle2() {
        Vector3d b = new Vector3d(0.0, 1.0, 1.0);
        Vector3d c = new Vector3d(0.0, 0.0, 1.0);
        double angle = b.angle(c)*180.0/Math.PI;
        assertEquals(45.0, angle, 0.001);
    }
    
    /**
     * This method tests the calculation of the angle between one axis
     * and itself, which should be zero by definition.
     */
    public void testCalcAngle3() {
        Vector3d b = new Vector3d(4.5, 3.1, 1.7);
        double angle = b.angle(b)*180.0/Math.PI;
        assertEquals(0.0, angle, 0.001);
    }
    
    /**
     * This method tests the conversion of notional coordinates to
     * cartesian and back to notional.
     */
    public void testRoundTripUnitCellNotionalCoordinates() {
        Vector3d[] cardAxes = CrystalGeometryTools.notionalToCartesian(
            7.6, 3.9, 10.3, 67.0, 91.2, 110.5
        );
        Vector3d a = cardAxes[0];
        Vector3d b = cardAxes[1];
        Vector3d c = cardAxes[2];
        double[] notionalCoords = CrystalGeometryTools.cartesianToNotional(
            a,b,c
        );
        assertEquals(7.6, notionalCoords[0], 0.001);
        assertEquals(3.9, notionalCoords[1], 0.001);
        assertEquals(10.3, notionalCoords[2], 0.001);
        assertEquals(67.0, notionalCoords[3], 0.001);
        assertEquals(91.2, notionalCoords[4], 0.001);
        assertEquals(110.5, notionalCoords[5], 0.001);
    }
    
    /**
     * This method tests wether two times inversion of the axes
     * gives back the original axes.
     */
    public void testCalcInvertedAxes_Vector3d_Vector3d_Vector3d() {
        Vector3d a = new Vector3d(3.4, 7.6, 5.5);
        Vector3d b = new Vector3d(2.8, 4.0, 6.3);
        Vector3d c = new Vector3d(1.9, 3.9, 9.1);
        Vector3d[] invertedAxes = CrystalGeometryTools.calcInvertedAxes(
            a,b,c
        );
        Vector3d a2 = invertedAxes[0];
        Vector3d b2 = invertedAxes[1];
        Vector3d c2 = invertedAxes[2];
        Vector3d[] doubleAxes = CrystalGeometryTools.calcInvertedAxes(
            a2,b2,c2
        );
        Vector3d a3 = doubleAxes[0];
        Vector3d b3 = doubleAxes[1];
        Vector3d c3 = doubleAxes[2];
        assertEquals(a.x, a3.x, 0.001);
        assertEquals(a.y, a3.y, 0.001);
        assertEquals(a.z, a3.z, 0.001);
        assertEquals(b.x, b3.x, 0.001);
        assertEquals(b.y, b3.y, 0.001);
        assertEquals(b.z, b3.z, 0.001);
        assertEquals(c.x, c3.x, 0.001);
        assertEquals(c.y, c3.y, 0.001);
        assertEquals(c.z, c3.z, 0.001);
    }
}

