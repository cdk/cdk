/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.geometry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.geometry.CrystalGeometryTools;

/**
 * This class defines regression tests that should ensure that the source code
 * of the org.openscience.cdk.geometry.CrystalGeometryTools is not broken.
 * All methods that start with test are regression tests, e.g.
 * <code>testNotionalToCartesian()</code>.
 *
 * @cdk.module test
 *
 * @author     Egon Willighagen
 * @cdk.created    2003-08-19
 *
 * @see org.openscience.cdk.geometry.CrystalGeometryTools
 */
public class CrystalGeometryToolsTest extends TestCase {

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
    public void testNotionalToCartesian() {
        double[][] cardAxes = CrystalGeometryTools.notionalToCartesian(
            1.0, 2.0, 3.0, 90.0, 90.0, 90.0
        );
        // the a axis
        assertEquals(1.0, cardAxes[0][0], 0.001);
        assertEquals(0.0, cardAxes[0][1], 0.001);
        assertEquals(0.0, cardAxes[0][2], 0.001);
        // the b axis
        assertEquals(0.0, cardAxes[1][0], 0.001);
        assertEquals(2.0, cardAxes[1][1], 0.001);
        assertEquals(0.0, cardAxes[1][2], 0.001);
        // the c axis
        assertEquals(0.0, cardAxes[2][0], 0.001);
        assertEquals(0.0, cardAxes[2][1], 0.001);
        assertEquals(3.0, cardAxes[2][2], 0.001);
    }

    /**
     * This method tests the conversion of cartesian coordinates to 
     * notional coordinates.
     */
    public void testCartesianToNotional() {
        double[] a = {1.0, 0.0, 0.0};
        double[] b = {0.0, 2.0, 0.0};
        double[] c = {0.0, 0.0, 3.0};
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
    public void testFractionalToCartesian() {
        double[] a = {1.0, 0.0, 0.0};
        double[] b = {0.0, 2.0, 0.0};
        double[] c = {0.0, 0.0, 3.0};
        double[] fractCoords = {0.25, 0.50, 0.75};
        double[] cartCoords = CrystalGeometryTools.fractionalToCartesian(
            a, b, c, fractCoords
        );
        assertEquals(0.25, cartCoords[0], 0.001);
        assertEquals(1.0, cartCoords[1], 0.001);
        assertEquals(2.25, cartCoords[2], 0.001);
    }

    /**
     * This method tests the conversion of atomic fractional coordinates to
     * cartesian coordinates. The specific numbers are taken from 9603.res.
     */
    public void testFractionalToCartesian2() {
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
    }

    /**
     * This method tests the conversion of atomic cartesian coordinates to
     * fractional coordinates.
     */
    public void testCartesianToFractional() {
        double[] a = {1.0, 0.0, 0.0};
        double[] b = {0.0, 2.0, 0.0};
        double[] c = {0.0, 0.0, 3.0};
        double[] cartCoords = {0.25, 1.0, 2.25};
        double[] fractCoords = CrystalGeometryTools.cartesianToFractional(
            a, b, c, cartCoords
        );
        assertEquals(0.25, fractCoords[0], 0.001);
        assertEquals(0.50, fractCoords[1], 0.001);
        assertEquals(0.75, fractCoords[2], 0.001);
    }

    /**
     * This method tests the calculation of axis lengths.
     */
    public void testCalcAxisLength() {
        double[] a = {1.0, 1.0, 1.0};
        double length = CrystalGeometryTools.calcAxisLength(a);
        assertEquals(Math.sqrt(3.0), length, 0.001);
    }

    /**
     * This method tests the calculation of axis lengths too, like
     * <code>testCalcAxisLength()</code>.
     */
    public void testCalcAxisLength2() {
        double[] a = {1.0, 0.0, 0.0};
        double length = CrystalGeometryTools.calcAxisLength(a);
        assertEquals(1.0, length, 0.001);
        double[] b = {0.0, 1.0, 0.0};
        length = CrystalGeometryTools.calcAxisLength(b);
        assertEquals(1.0, length, 0.001);
        double[] c = {0.0, 0.0, 1.0};
        length = CrystalGeometryTools.calcAxisLength(c);
        assertEquals(1.0, length, 0.001);
    }

    /**
     * This method tests the calculation of the angle between two axes.
     */
    public void testCalcAngle() {
        double[] b = {0.0, 2.0, 0.0};
        double[] c = {0.0, 0.0, 3.0};
        double angle = CrystalGeometryTools.calcAxesAngle(b,c);
        assertEquals(90.0, angle, 0.001);
    }
    
    /**
     * This method tests the calculation of the angle between two axes too.
     */
    public void testCalcAngle2() {
        double[] b = {0.0, 1.0, 1.0};
        double[] c = {0.0, 0.0, 1.0};
        double angle = CrystalGeometryTools.calcAxesAngle(b,c);
        assertEquals(45.0, angle, 0.001);
    }
    
    /**
     * This method tests the calculation of the angle between one axis
     * and itself, which should be zero by definition.
     */
    public void testCalcAngle3() {
        double[] b = {4.5, 3.1, 1.7};
        double angle = CrystalGeometryTools.calcAxesAngle(b,b);
        assertEquals(0.0, angle, 0.001);
    }
    
    /**
     * This method tests the conversion of notional coordinates to
     * cartesian and back to notional.
     */
    public void testRoundTripUnitCellNotionalCoordinates() {
        double[][] cardAxes = CrystalGeometryTools.notionalToCartesian(
            7.6, 3.9, 10.3, 67.0, 91.2, 110.5
        );
        double[] a = {cardAxes[0][0], cardAxes[0][1], cardAxes[0][2]};
        double[] b = {cardAxes[1][0], cardAxes[1][1], cardAxes[1][2]};
        double[] c = {cardAxes[2][0], cardAxes[2][1], cardAxes[2][2]};
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
    public void testCalcInvertedAxes() {
        double[] a = {3.4, 7.6, 5.5};
        double[] b = {2.8, 4.0, 6.3};
        double[] c = {1.9, 3.9, 9.1};
        double[][] invertedAxes = CrystalGeometryTools.calcInvertedAxes(
            a,b,c
        );
        double[] a2 = {invertedAxes[0][0], invertedAxes[0][1], invertedAxes[0][2]};
        double[] b2 = {invertedAxes[1][0], invertedAxes[1][1], invertedAxes[1][2]};
        double[] c2 = {invertedAxes[2][0], invertedAxes[2][1], invertedAxes[2][2]};
        double[][] doubleAxes = CrystalGeometryTools.calcInvertedAxes(
            a2,b2,c2
        );
        double[] a3 = {doubleAxes[0][0], doubleAxes[0][1], doubleAxes[0][2]};
        double[] b3 = {doubleAxes[1][0], doubleAxes[1][1], doubleAxes[1][2]};
        double[] c3 = {doubleAxes[2][0], doubleAxes[2][1], doubleAxes[2][2]};
        assertEquals(a[0], a3[0], 0.001);
        assertEquals(a[1], a3[1], 0.001);
        assertEquals(a[2], a3[2], 0.001);
        assertEquals(b[0], b3[0], 0.001);
        assertEquals(b[1], b3[1], 0.001);
        assertEquals(b[2], b3[2], 0.001);
        assertEquals(c[0], c3[0], 0.001);
        assertEquals(c[1], c3[1], 0.001);
        assertEquals(c[2], c3[2], 0.001);
    }
}

