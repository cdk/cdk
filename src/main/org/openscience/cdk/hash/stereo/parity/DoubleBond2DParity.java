/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash.stereo.parity;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import javax.vecmath.Point2d;

/**
 * Calculate the geometric configuration of a double bond. The configuration is
 * provided as a parity (+1,0,-1) where +1 indicates the substituents are on
 * <i>opposite</i> sides (E or trans) and -1 indicates they are <i>together</i>
 * on the same side (Z or cis). If one of the substituents is parallel to the
 * double bond then the configuration is unspecified and 0 is returned.
 *
 * @author John May
 * @cdk.module hash
 */
@TestClass("org.openscience.cdk.hash.stereo.parity.DoubleBond2DParityTest")
public final class DoubleBond2DParity implements GeometricParity {

    // coordinates of the double bond atoms
    // x       w
    //  \     /
    //   u = v
    private Point2d u, v, x, w;

    /* the area below which we return unspecified parity */
    private static final double THRESHOLD = 0.1;

    /**
     * Create a new double bond parity for the 2D coordinates of the atoms.
     *
     * @param left             one atom of the double bond
     * @param right            the other atom of a double bond
     * @param leftSubstituent  the substituent atom connected to the left atom
     * @param rightSubstituent the substituent atom connected to the right atom
     */
    public DoubleBond2DParity(Point2d left, Point2d right,
                              Point2d leftSubstituent, Point2d rightSubstituent) {
        this.u = left;
        this.v = right;
        this.x = leftSubstituent;
        this.w = rightSubstituent;
    }

    /**
     * Calculate the configuration of the double bond as a parity.
     *
     * @return opposite (+1), together (-1) or unspecified (0)
     */
    @TestMethod("opposite,together,unspecified")
    @Override public int parity() {
        return parity(x, u, v) * parity(w, v, u);
    }

    /**
     * Determine the rotation parity of one side of the double bond. This parity
     * is the sign of the area of a triangle.
     *
     * <pre>
     * a
     *  \
     *   b = c
     * </pre>
     *
     * @param a coordinates of the substituent atom
     * @param b coordinates of the atom next to the substituent
     * @param c coordinates of the atom double bonds to <i>b</i>
     * @return clockwise (+1), anti-clockwise (-1) or unspecified (0)
     */
    private static int parity(Point2d a, Point2d b, Point2d c) {

        double det = (a.x - c.x) * (b.y - c.y) - (a.y - c.y) * (b.x - c.x);

        return Math.abs(det) < THRESHOLD ? 0 : (int) Math.signum(det);
    }


}
