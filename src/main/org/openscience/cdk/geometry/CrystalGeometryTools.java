/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.geometry;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.ICrystal;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A set of static methods for working with crystal coordinates.
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 *
 * @cdk.keyword fractional coordinates, crystal
 */
@TestClass("org.openscience.cdk.geometry.CrystalGeometryToolsTest")
public class CrystalGeometryTools {

    /**
     * Inverts three cell axes.
     *
     * @return         a 3x3 matrix with the three cartesian vectors representing
     *                 the unit cell axes. The a axis is the first row.
     */
    @TestMethod("testCalcInvertedAxes_Vector3d_Vector3d_Vector3d")
    public static Vector3d[] calcInvertedAxes(Vector3d aAxis, Vector3d bAxis, Vector3d cAxis) {
         double det = aAxis.x*bAxis.y*cAxis.z - aAxis.x*bAxis.z*cAxis.y -
                      aAxis.y*bAxis.x*cAxis.z + aAxis.y*bAxis.z*cAxis.x +
                      aAxis.z*bAxis.x*cAxis.y - aAxis.z*bAxis.y*cAxis.x;
         Vector3d[] invaxes = new Vector3d[3];
         invaxes[0] = new Vector3d();
         invaxes[0].x = (bAxis.y*cAxis.z - bAxis.z*cAxis.y)/det;
         invaxes[0].y = (bAxis.z*cAxis.x - bAxis.x*cAxis.z)/det;
         invaxes[0].z = (bAxis.x*cAxis.y - bAxis.y*cAxis.x)/det;

         invaxes[1] = new Vector3d();
         invaxes[1].x = (aAxis.z*cAxis.y - aAxis.y*cAxis.z)/det;
         invaxes[1].y = (aAxis.x*cAxis.z - aAxis.z*cAxis.x)/det;
         invaxes[1].z = (aAxis.y*cAxis.x - aAxis.x*cAxis.y)/det;

         invaxes[2] = new Vector3d();
         invaxes[2].x = (aAxis.y*bAxis.z - aAxis.z*bAxis.y)/det;
         invaxes[2].y = (aAxis.z*bAxis.x - aAxis.x*bAxis.z)/det;
         invaxes[2].z = (aAxis.x*bAxis.y - aAxis.y*bAxis.x)/det;
         return invaxes;
    }

    /**
     * @cdk.dictref blue-obelisk:convertCartesianIntoFractionalCoordinates
     */
    @TestMethod("testCartesianToFractional_Vector3d_Vector3d_Vector3d_Point3d")
    public static Point3d cartesianToFractional(Vector3d aAxis, Vector3d bAxis, Vector3d cAxis,
                                                 Point3d cartPoint) {
        Vector3d[] invaxis = calcInvertedAxes(aAxis,bAxis,cAxis);
        Point3d frac = new Point3d();
        frac.x = invaxis[0].x*cartPoint.x + invaxis[0].y*cartPoint.y +
                 invaxis[0].z*cartPoint.z;
        frac.y = invaxis[1].x*cartPoint.x + invaxis[1].y*cartPoint.y +
                 invaxis[1].z*cartPoint.z;
        frac.z = invaxis[2].x*cartPoint.x + invaxis[2].y*cartPoint.y +
                 invaxis[2].z*cartPoint.z;
        return frac;
    }

    /**
     * @cdk.dictref blue-obelisk:convertFractionIntoCartesianCoordinates
     */
    @TestMethod("testFractionalToCartesian_Vector3d_Vector3d_Vector3d_Point3d")
    public static Point3d fractionalToCartesian(Vector3d aAxis, Vector3d bAxis, Vector3d cAxis,
                                                 Point3d frac) {
        Point3d cart = new Point3d();
        cart.x = frac.x*aAxis.x + frac.y*bAxis.x + frac.z*cAxis.x;
        cart.y = frac.x*aAxis.y + frac.y*bAxis.y + frac.z*cAxis.y;
        cart.z = frac.x*aAxis.z + frac.y*bAxis.z + frac.z*cAxis.z;
        return cart;
    }

    /**
     * Calculates cartesian vectors for unit cell axes from axes lengths and angles
     * between axes.
     *
     * <p>To calculate cartesian coordinates, it places the a axis on the x axes,
     * the b axis in the xy plane, making an angle gamma with the a axis, and places
     * the c axis to fullfil the remaining constraints. (See also
     * <a href="http://server.ccl.net/cca/documents/molecular-modeling/node4.html">the 
     * CCL archive</a>.)
     *
     * @param alength   length of the a axis
     * @param blength   length of the b axis
     * @param clength   length of the c axis
     * @param alpha     angle between b and c axes in degrees
     * @param beta      angle between a and c axes in degrees
     * @param gamma     angle between a and b axes in degrees
     * @return          an array of Vector3d objects with the three cartesian vectors representing
     *                  the unit cell axes.
     *
     * @cdk.keyword  notional coordinates
     * @cdk.dictref  blue-obelisk:convertNotionalIntoCartesianCoordinates
     */
    @TestMethod("testNotionalToCartesian_double_double_double_double_double_double")
    public static Vector3d[] notionalToCartesian(double alength, double blength,
                                                 double clength, double alpha,
                                                 double beta, double gamma) {
        Vector3d[] axes = new Vector3d[3];
        
        /* 1. align the a axis with x axis */
        axes[0] = new Vector3d();
        axes[0].x = alength;
        axes[0].y = 0.0;
        axes[0].z = 0.0;

        double toRadians = Math.PI/180.0;
        
        /* some intermediate variables */
        double cosalpha = Math.cos(toRadians*alpha);
        double cosbeta = Math.cos(toRadians*beta);
        double cosgamma = Math.cos(toRadians*gamma);
        double singamma = Math.sin(toRadians*gamma);

        /* 2. place the b is in xy plane making a angle gamma with a */
        axes[1] = new Vector3d();
        axes[1].x = blength*cosgamma;
        axes[1].y = blength*singamma;
        axes[1].z = 0.0;

        /* 3. now the c axis, with more complex maths */
        axes[2] = new Vector3d();
        double volume = alength * blength * clength *
                        Math.sqrt(1.0 - cosalpha*cosalpha -
                                  cosbeta*cosbeta -
                                  cosgamma*cosgamma +
                                  2.0*cosalpha*cosbeta*cosgamma);
        axes[2].x = clength*cosbeta;
        axes[2].y = clength*(cosalpha-cosbeta*cosgamma)/singamma;
        axes[2].z = volume/(alength*blength*singamma);
        
        return axes;
    }
    
    /**
     * @cdk.dictref  blue-obelisk:convertCartesianIntoNotionalCoordinates
     */
    @TestMethod("testCartesianToNotional_Vector3d_Vector3d_Vector3d")
    public static double[] cartesianToNotional(Vector3d aAxis, Vector3d bAxis, Vector3d cAxis) {
        double[] notionalCoords = new double[6];
        notionalCoords[0] = aAxis.length();
        notionalCoords[1] = bAxis.length();
        notionalCoords[2] = cAxis.length();
        notionalCoords[3] = bAxis.angle(cAxis)*180.0/Math.PI;
        notionalCoords[4] = aAxis.angle(cAxis)*180.0/Math.PI;
        notionalCoords[5] = aAxis.angle(bAxis)*180.0/Math.PI;
        return notionalCoords;
    }
                               
	/**
     * Determines if this model contains fractional (crystal) coordinates.
	 *
	 * @return  boolean indication that 3D coordinates are available 
	 */
    public static boolean hasCrystalCoordinates(IAtomContainer container) {
    	java.util.Iterator atoms = container.atoms().iterator();
        while (atoms.hasNext()) {
            if (((IAtom)atoms.next()).getFractionalPoint3d() == null) {
                return false;
            }
        }
        return true;
    }

	/**
     * Creates cartesian coordinates for all Atoms in the Crystal.
	 */
    public static void fractionalToCartesian(ICrystal crystal) {
    	java.util.Iterator atoms = crystal.atoms().iterator();
        Vector3d aAxis = crystal.getA();
        Vector3d bAxis = crystal.getB();
        Vector3d cAxis = crystal.getC();
        while (atoms.hasNext()) {
        	IAtom atom = (IAtom)atoms.next();
            Point3d fracPoint = atom.getFractionalPoint3d();
            if (fracPoint != null) {
                atom.setPoint3d(fractionalToCartesian(aAxis,bAxis,cAxis, fracPoint));
            }
        }
    }
}



