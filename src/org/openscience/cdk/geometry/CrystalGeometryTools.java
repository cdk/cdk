/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
import java.awt.Dimension;
import java.util.Vector;

/**
 * A set of static methods for working with crystal coordinates.
 *
 * @keyword fractional coordinates, crystal
 */
public class CrystalGeometryTools {

    /**
     * Method that transforms fractional coordinates into cartesian coordinates.
     *
     * @param a     the a axis vector of the unit cell in cartesian coordinates
     * @param b     the b axis vector of the unit cell in cartesian coordinates
     * @param c     the c axis vector of the unit cell in cartesian coordinates
     * @param frac  a fractional coordinate to convert
     * @returns     an array of length 3 with the cartesian coordinates of the
     *              point defined by frac
     *
     * @keyword     cartesian coordinates
     * @keyword     fractional coordinates
     */
    public static double[] fractionalToCartesian(double[] a, double[] b, double[] c,
                                                 double[] frac) {
        double[] cart = new double[3];
        cart[0] = frac[0]*a[0] + frac[1]*a[1] + frac[2]*a[2];
        cart[1] = frac[0]*b[0] + frac[1]*b[1] + frac[2]*b[2];
        cart[2] = frac[0]*c[0] + frac[1]*c[1] + frac[2]*c[2];
        return cart;
    }
    
    /**
     * Calculate cartesian vectors for unit cell axes from axes lengths and angles
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
     * @param alpha     angle between b and c axes
     * @param beta      angle between a and c axes
     * @param gamma     angle between a and b axes
     * @returns         a 3x3 matrix with the three cartesian vectors representing
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

        /* some intermediate variables */
        double cosalpha = Math.cos(alpha);
        double cosbeta = Math.cos(beta);
        double cosgamma = Math.cos(gamma);
        double sinalpha = Math.sin(alpha);
        double sinbeta = Math.sin(beta);
        double singamma = Math.sin(gamma);

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
        axes[2][1] = clength*(cosalpha- // cy
             cosbeta*cosgamma)/singamma;
        axes[2][2] = V/(alength*blength
             *singamma);                // cz

        return axes;
    }
}



