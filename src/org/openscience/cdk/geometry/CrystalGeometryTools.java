/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.geometry;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.exception.CDKException;

/**
 * A set of static methods for working with crystal coordinates.
 *
 * @cdk.module standard
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 *
 * @cdk.keyword fractional coordinates, crystal
 */
public class CrystalGeometryTools {

    /**
     * Inverts three cell axes.
     *
     * @return         a 3x3 matrix with the three cartesian vectors representing
     *                 the unit cell axes. The a axis is the first row.
     */
    public static Vector3d[] calcInvertedAxes(Vector3d a, Vector3d b, Vector3d c) {
         double det = a.x*b.y*c.z - a.x*b.z*c.y -
                      a.y*b.x*c.z + a.y*b.z*c.x +
                      a.z*b.x*c.y - a.z*b.y*c.x;
         Vector3d[] invaxes = new Vector3d[3];
         invaxes[0] = new Vector3d();
         invaxes[0].x = (b.y*c.z - b.z*c.y)/det;
         invaxes[0].y = (b.z*c.x - b.x*c.z)/det;
         invaxes[0].z = (b.x*c.y - b.y*c.x)/det;

         invaxes[1] = new Vector3d();
         invaxes[1].x = (a.z*c.y - a.y*c.z)/det;
         invaxes[1].y = (a.x*c.z - a.z*c.x)/det;
         invaxes[1].z = (a.y*c.x - a.x*c.y)/det;

         invaxes[2] = new Vector3d();
         invaxes[2].x = (a.y*b.z - a.z*b.y)/det;
         invaxes[2].y = (a.z*b.x - a.x*b.z)/det;
         invaxes[2].z = (a.x*b.y - a.y*b.x)/det;
         return invaxes;
    }

    /**
     * Converts real coordinate (x,y,z) to a fractional coordinates
     * (xf, yf, zf).
     *
     * @deprecated
     */
    public static double[] cartesianToFractional(double[] a, double[] b, double[] c,
                                                 double[] cart) {
        double[] fractCoords = new double[3];
        Point3d fract = cartesianToFractional(new Vector3d(a[0], a[1], a[2]),
                                              new Vector3d(b[0], b[1], b[2]),
                                              new Vector3d(c[0], c[1], c[2]),
                                              new Point3d(cart[0], cart[1], cart[2]));
        fractCoords[0] = fract.x;
        fractCoords[1] = fract.y;
        fractCoords[2] = fract.z;
        return fractCoords;
    };

    public static Point3d cartesianToFractional(Vector3d a, Vector3d b, Vector3d c,
                                                 Point3d cartPoint) {
        Vector3d[] invaxis = calcInvertedAxes(a,b,c);
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
     * Method that transforms fractional coordinates into cartesian coordinates.
     *
     * @param a     the a axis vector of the unit cell in cartesian coordinates
     * @param b     the b axis vector of the unit cell in cartesian coordinates
     * @param c     the c axis vector of the unit cell in cartesian coordinates
     * @param frac  a fractional coordinate to convert
     * @return     an array of length 3 with the cartesian coordinates of the
     *              point defined by frac
     *
     * @cdk.keyword     cartesian coordinates
     * @cdk.keyword     fractional coordinates
     *
     * @see #cartesianToFractional(double[], double[], double[], double[])
     * @deprecated
     */
    public static double[] fractionalToCartesian(double[] a, double[] b, double[] c,
                                                 double[] frac) {
        double[] cart = new double[3];
        cart[0] = frac[0]*a[0] + frac[1]*b[0] + frac[2]*c[0];
        cart[1] = frac[0]*a[1] + frac[1]*b[1] + frac[2]*c[1];
        cart[2] = frac[0]*a[2] + frac[1]*b[2] + frac[2]*c[2];
        return cart;
    }
    
    public static Point3d fractionalToCartesian(Vector3d a, Vector3d b, Vector3d c,
                                                 Point3d frac) {
        Point3d cart = new Point3d();
        cart.x = frac.x*a.x + frac.y*b.x + frac.z*c.x;
        cart.y = frac.x*a.y + frac.y*b.y + frac.z*c.y;
        cart.z = frac.x*a.z + frac.y*b.z + frac.z*c.z;
        return cart;
    }

    /**
     * @deprecated
     */
    public static Point3d fractionalToCartesian(double[] a, double[] b, double[] c,
                                                 Point3d fracPoint) {
        double[] frac = new double[3];
        frac[0] = fracPoint.x;
        frac[1] = fracPoint.y;
        frac[2] = fracPoint.z;
        double[] cart = fractionalToCartesian(a,b,c, frac);
        return new Point3d(cart[0], cart[1], cart[2]);
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
     */
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
        double sinalpha = Math.sin(toRadians*alpha);
        double sinbeta = Math.sin(toRadians*beta);
        double singamma = Math.sin(toRadians*gamma);

        /* 2. place the b is in xy plane making a angle gamma with a */
        axes[1] = new Vector3d();
        axes[1].x = blength*cosgamma;
        axes[1].y = blength*singamma;
        axes[1].z = 0.0;

        /* 3. now the c axis, with more complex maths */
        axes[2] = new Vector3d();
        double V = alength * blength * clength *
                   Math.sqrt(1.0 - cosalpha*cosalpha -
                             cosbeta*cosbeta -
                             cosgamma*cosgamma +
                             2.0*cosalpha*cosbeta*cosgamma);
        axes[2].x = clength*cosbeta;
        axes[2].y = clength*(cosalpha-cosbeta*cosgamma)/singamma;
        axes[2].z = V/(alength*blength*singamma);
        
        return axes;
    }
    
    public static double[] cartesianToNotional(Vector3d a, Vector3d b, Vector3d c) {
        double[] notionalCoords = new double[6];
        notionalCoords[0] = a.length();
        notionalCoords[1] = b.length();
        notionalCoords[2] = c.length();
        notionalCoords[3] = b.angle(c)*180.0/Math.PI;
        notionalCoords[4] = a.angle(c)*180.0/Math.PI;
        notionalCoords[5] = a.angle(b)*180.0/Math.PI;
        return notionalCoords;
    }
                               
	/**
     * Determines if this model contains fractional (crystal) coordinates.
	 *
	 * @return  boolean indication that 3D coordinates are available 
	 */
    public static boolean hasCrystalCoordinates(AtomContainer m) {
        Atom[] atoms = m.getAtoms();
        for (int i=0; i < atoms.length; i++) {
            if (atoms[i].getFractionalPoint3d() == null) {
                return false;
            }
        }
        return true;
    }

	/**
     * Creates cartesian coordinates for all Atoms in the Crystal
	 *
	 * @return  boolean indication that 3D coordinates are available 
	 */
    public static void fractionalToCartesian(Crystal crystal) {
        Atom[] atoms = crystal.getAtoms();
        Vector3d a = crystal.getA();
        Vector3d b = crystal.getB();
        Vector3d c = crystal.getC();
        for (int i=0; i < atoms.length; i++) {
            Point3d fracPoint = atoms[i].getFractionalPoint3d();
            if (fracPoint != null) {
                atoms[i].setPoint3d(fractionalToCartesian(a,b,c, fracPoint));
            }
        }
    }
}



