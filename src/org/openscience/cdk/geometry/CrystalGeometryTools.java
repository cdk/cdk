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

import javax.vecmath.*;
import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import java.awt.Dimension;
import java.util.Vector;

/**
 * A set of static methods for working with crystal coordinates.
 *
 * @cdkPackage standard
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 *
 * @keyword fractional coordinates, crystal
 */
public class CrystalGeometryTools {

    /**
     * Inverts three cell axes.
     *
     * @return         a 3x3 matrix with the three cartesian vectors representing
     *                 the unit cell axes. The a axis is the first row.
     */
    public static double[][] calcInvertedAxes(double[] a, double[] b, double[] c) {
         double det = a[0]*b[1]*c[2] -
                      a[0]*b[2]*c[1] -
                      a[1]*b[0]*c[2] +
                      a[1]*b[2]*c[0] +
                      a[2]*b[0]*c[1] -
                      a[2]*b[1]*c[0];
         double invaxis[][] = new double[3][3];
         invaxis[0][0] = (b[1]*c[2] - b[2]*c[1])/det;
         invaxis[0][1] = (b[2]*c[0] - b[0]*c[2])/det;
         invaxis[0][2] = (b[0]*c[1] - b[1]*c[0])/det;

         invaxis[1][0] = (a[2]*c[1] - a[1]*c[2])/det;
         invaxis[1][1] = (a[0]*c[2] - a[2]*c[0])/det;
         invaxis[1][2] = (a[1]*c[0] - a[0]*c[1])/det;

         invaxis[2][0] = (a[1]*b[2] - a[2]*b[1])/det;
         invaxis[2][1] = (a[2]*b[0] - a[0]*b[2])/det;
         invaxis[2][2] = (a[0]*b[1] - a[1]*b[0])/det;
         return invaxis;
    }

    /**
     * Converts real coordinate (x,y,z) to a fractional coordinates
     * (xf, yf, zf).
     */
    public static double[] cartesianToFractional(double[] a, double[] b, double[] c,
                                                 double[] cart) {
         double[] fractCoords = new double[3];
         double[][] invaxis = calcInvertedAxes(a,b,c);
         fractCoords[0] = invaxis[0][0]*cart[0] + invaxis[0][1]*cart[1] +
                          invaxis[0][2]*cart[2];
         fractCoords[1] = invaxis[1][0]*cart[0] + invaxis[1][1]*cart[1] +
                          invaxis[1][2]*cart[2];
         fractCoords[2] = invaxis[2][0]*cart[0] + invaxis[2][1]*cart[1] +
                          invaxis[2][2]*cart[2];
         return fractCoords;
    };

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
     * @keyword     cartesian coordinates
     * @keyword     fractional coordinates
     *
     * @see #cartesianToFractional(double[], double[], double[], double[])
     */
    public static double[] fractionalToCartesian(double[] a, double[] b, double[] c,
                                                 double[] frac) {
        double[] cart = new double[3];
        cart[0] = frac[0]*a[0] + frac[1]*b[0] + frac[2]*c[0];
        cart[1] = frac[0]*a[1] + frac[1]*b[1] + frac[2]*c[1];
        cart[2] = frac[0]*a[2] + frac[1]*b[2] + frac[2]*c[2];
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
     * @return         a 3x3 matrix with the three cartesian vectors representing
     *                  the unit cell axes. The a axis is the first row.
     *
     * @keyword  notional coordinates
     */
    public static double[][] notionalToCartesian(double alength, double blength,
                                                 double clength, double alpha,
                                                 double beta, double gamma) {
        double[][] axes = new double[3][3];
        
        /* 1. align the a axis with x axis */
        axes[0][0] = alength;           // ax
        axes[0][1] = 0.0;               // ay
        axes[0][2] = 0.0;               // az

        double toRadians = Math.PI/180;
        
        /* some intermediate variables */
        double cosalpha = Math.cos(toRadians*alpha);
        double cosbeta = Math.cos(toRadians*beta);
        double cosgamma = Math.cos(toRadians*gamma);
        double sinalpha = Math.sin(toRadians*alpha);
        double sinbeta = Math.sin(toRadians*beta);
        double singamma = Math.sin(toRadians*gamma);

        /* 2. place the b is in xy plane making a angle gamma with a */
        axes[1][0] = blength*cosgamma;  // bx
        axes[1][1] = blength*singamma;  // by
        axes[1][2] = 0.0;               // bz

        /* 3. now the c axis,
         * source: http://server.ccl.net/cca/documents/molecular-modeling/node4.html */
        double V = alength * blength * clength *
                   Math.sqrt(1.0 - cosalpha*cosalpha -
                             cosbeta*cosbeta -
                             cosgamma*cosgamma +
                             2.0*cosalpha*cosbeta*cosgamma);
        axes[2][0] = clength*cosbeta;   // cx
        axes[2][1] = clength*(cosalpha-cosbeta*cosgamma)/singamma;
        axes[2][2] = V/(alength*blength*singamma);                // cz

        return axes;
    }
    
    /**
     * Calculates notional coordinates for unit cell axes from cartesian
     * axes coordinates.
     *
     * <p>The array that is returned contains the lengths of a, b and c,
     * and the angles alpha, beta and gamma (in this order).
     *
     * @param a    length of the a axis
     * @param b    length of the b axis
     * @param c    length of the c axis
     * @return     an array of length 6 with a,b,c,alpha,beta and gamma
     *
     * @keyword  notional coordinates
     */
    public static double[] cartesianToNotional(double[] a, double[] b, double[] c) {
        double[] notionalCoords = new double[6];
        notionalCoords[0] = calcAxisLength(a);
        notionalCoords[1] = calcAxisLength(b);
        notionalCoords[2] = calcAxisLength(c);
        notionalCoords[3] = calcAxesAngle(b,c);
        notionalCoords[4] = calcAxesAngle(a,c);
        notionalCoords[5] = calcAxesAngle(a,b);
        return notionalCoords;
    }
                               
    /**
     * Converts the cell into a P1 cell.
     * The function assumes that unit cell axes are properly set.
     *
     * <p>Recognized space group strings:
     *   "P1","P 2_1 2_1 2_1"
     *
     * <p>This function assumes fractional coordinates, which the
     * Crystal does not provide by itself. It's broken!
     *
     * @return The Crystal clone with P1 space group.
     *
     * @deprecated
     */
    public static Crystal convertToP1Cell(Crystal crystal) throws CDKException {
        Crystal result = (Crystal)crystal.clone();
        if ("P 2_1 2_1 2_1".equals(crystal.getSpaceGroup())) {
            for (int i =0; i < crystal.getAtomCount(); i++) {
                Atom atom = crystal.getAtomAt(i);
                /* symmetry operations:

                identity (skipped) :   x      y      z
                                        -x+0.5 -y      z+0.5   I
                                        -x      y+0.5 -z+0.5   II
                                        x+0.5 -y+0.5 -z        III
                */

                // do not take into account moving into unit cell
                Point3d point = atom.getPoint3D();

                if (point != null) {
                    double[] a = crystal.getA();
                    double[] b = crystal.getB();
                    double[] c = crystal.getC();
                    // point I
                    Point3d newPoint = new Point3d();
                    newPoint.x = -1.0*point.x + 0.5*(a[0] + b[0] + c[0]);
                    newPoint.y = -1.0*point.y;
                    newPoint.z =      point.z + 0.5*(a[3] + b[3] + c[3]);
                    Atom syma = (Atom)atom.clone();
                    syma.setPoint3D(newPoint);
                    result.addAtom(syma);

                    // point II
                    newPoint.x = -1.0*point.x + 0.5*(a[0] + b[0] + c[0]);
                    newPoint.y = -1.0*point.y;
                    newPoint.z =      point.z + 0.5*(a[3] + b[3] + c[3]);
                    syma = (Atom)atom.clone();
                    syma.setPoint3D(newPoint);
                    result.addAtom(syma);

                    // point III
                    newPoint.x = -1.0*point.x + 0.5*(a[0] + b[0] + c[0]);
                    newPoint.y = -1.0*point.y;
                    newPoint.z =      point.z + 0.5*(a[3] + b[3] + c[3]);
                    syma = (Atom)atom.clone();
                    syma.setPoint3D(newPoint);
                    result.addAtom(syma);
                } else {
                    Atom syma = (Atom)atom.clone();
                    result.addAtom(syma);
                    syma = (Atom)atom.clone();
                    result.addAtom(syma);
                    syma = (Atom)atom.clone();
                    result.addAtom(syma);
                }
            }
        } else if ("P 2_1 2_1 2_1".equals(crystal.getSpaceGroup())) {
            // no transformation needed
        } else {
            throw new CDKException("This given spacegroup is not supported.");
        }
        return result;
    }
    
    /**
     * Calculates the length of a cell axis.
     */
    public static double calcAxisLength(double[] a) {
        return Math.sqrt(Math.pow(a[0],2.0) + Math.pow(a[1],2.0) + Math.pow(a[2],2.0));
    };

    /**
     * Calculates the angle between two axes.
     */
    public static double calcAxesAngle(double a[], double b[]) {
        return Math.toDegrees(Math.acos((a[0]*b[0]+a[1]*b[1]+a[2]*b[2])/
            (calcAxisLength(a)*calcAxisLength(b))));
    };

}



