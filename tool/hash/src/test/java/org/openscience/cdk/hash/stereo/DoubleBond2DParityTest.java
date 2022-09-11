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

package org.openscience.cdk.hash.stereo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.vecmath.Point2d;

/**
 * @author John May
 * @cdk.module test-hash
 */
class DoubleBond2DParityTest {

    private static final int UNSPECIFIED = 0;
    private static final int OPPOSITE    = +1;
    private static final int TOGETHER    = -1;

    @Test
    void unspecified() throws Exception {
        GeometricParity geometric = new DoubleBond2DParity(new Point2d(-2.6518, 0.1473), new Point2d(-1.8268, 0.1473),
                new Point2d(-3.0643, 0.8618), new Point2d(-1.4143, 0.1473));
        Assertions.assertEquals(UNSPECIFIED, geometric.parity());
    }

    @Test
    void opposite() throws Exception {
        GeometricParity geometric = new DoubleBond2DParity(new Point2d(-2.6518, 0.1473), new Point2d(-1.8268, 0.1473),
                new Point2d(-3.0643, 0.8618), new Point2d(-1.4143, -0.5671));
        Assertions.assertEquals(OPPOSITE, geometric.parity());
    }

    @Test
    void together() throws Exception {
        GeometricParity geometric = new DoubleBond2DParity(new Point2d(-2.6518, 0.1473), new Point2d(-1.8268, 0.1473),
                new Point2d(-3.0643, 0.8618), new Point2d(-1.4143, 0.8618));
        Assertions.assertEquals(TOGETHER, geometric.parity());
    }

    @Test
    void unspecified_both() throws Exception {
        GeometricParity geometric = new DoubleBond2DParity(new Point2d(-2.6518, 0.1473), new Point2d(-1.8268, 0.1473),
                new Point2d(-3.0643, 0.1473), new Point2d(-1.4143, 0.1473));
        Assertions.assertEquals(UNSPECIFIED, geometric.parity());
    }

    @Test
    void opposite_inverted() throws Exception {
        GeometricParity geometric = new DoubleBond2DParity(new Point2d(-2.6518, 0.1473), new Point2d(-1.8268, 0.1473),
                new Point2d(-3.0643, -0.5671), new Point2d(-1.4143, 0.8618));
        Assertions.assertEquals(OPPOSITE, geometric.parity());
    }

    @Test
    void together_inverted() throws Exception {
        GeometricParity geometric = new DoubleBond2DParity(new Point2d(-2.6518, 0.1473), new Point2d(-1.8268, 0.1473),
                new Point2d(-3.0643, -0.5671), new Point2d(-1.4143, -0.5671));
        Assertions.assertEquals(TOGETHER, geometric.parity());
    }

    // double bond rotated pi/6 radians (30 degrees)
    @Test
    void opposite30() throws Exception {
        GeometricParity geometric = new DoubleBond2DParity(new Point2d(-2.4455, 0.5046), new Point2d(-2.0330, -0.2099),
                new Point2d(-2.0330, 1.2191), new Point2d(-2.4455, -0.9244));
        Assertions.assertEquals(OPPOSITE, geometric.parity());
    }

    // double bond rotated pi/6 radians (30 degrees)
    @Test
    void together30() throws Exception {
        GeometricParity geometric = new DoubleBond2DParity(new Point2d(-2.4455, 0.5046), new Point2d(-2.0330, -0.2099),
                new Point2d(-3.2705, 0.5046), new Point2d(-2.4455, -0.9244));
        Assertions.assertEquals(TOGETHER, geometric.parity());
    }

    // double bond rotated pi/6 radians (30 degrees)
    @Test
    void unspecified30() throws Exception {
        GeometricParity geometric = new DoubleBond2DParity(new Point2d(-2.4455, 0.5046), new Point2d(-2.0330, -0.2099),
                new Point2d(-2.8580, 1.2191), new Point2d(-2.4455, -0.9244));
        Assertions.assertEquals(UNSPECIFIED, geometric.parity());
    }

}
