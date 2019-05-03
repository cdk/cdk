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

import org.junit.Test;
import org.openscience.cdk.hash.stereo.DoubleBond3DParity;
import org.openscience.cdk.hash.stereo.GeometricParity;

import javax.vecmath.Point3d;

import static org.junit.Assert.assertEquals;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class DoubleBond3DParityTest {

    private static final int OPPOSITE = +1;
    private static final int TOGETHER = -1;

    @Test
    public void opposite() throws Exception {
        GeometricParity geometric = new DoubleBond3DParity(new Point3d(-4.6440, 0.4411, 0.5917), new Point3d(-3.7159,
                0.1090, -0.5916), new Point3d(-5.3207, -0.6945, 1.3818), new Point3d(-3.0392, 1.2445, -1.3818));
        assertEquals(OPPOSITE, geometric.parity());
    }

    @Test
    public void together() throws Exception {
        GeometricParity geometric = new DoubleBond3DParity(new Point3d(-4.6440, 0.4411, 0.5917), new Point3d(-3.7159,
                0.1090, -0.5916), new Point3d(-4.8954, 1.9087, 0.9848), new Point3d(-3.0392, 1.2445, -1.3818));
        assertEquals(TOGETHER, geometric.parity());
    }

    @Test
    public void opposite_endOn() throws Exception {
        GeometricParity geometric = new DoubleBond3DParity(new Point3d(-4.3262, 0.3192, 0.6495), new Point3d(-4.3206,
                0.3724, -0.8896), new Point3d(-4.3367, -1.0402, 1.3729), new Point3d(-4.3101, 1.7319, -1.6131));
        assertEquals(OPPOSITE, geometric.parity());
    }

    @Test
    public void together_endOn() throws Exception {
        GeometricParity geometric = new DoubleBond3DParity(new Point3d(-4.3262, 0.3192, 0.6495), new Point3d(-4.3206,
                0.3724, -0.8896), new Point3d(-4.3214, 1.6255, 1.4651), new Point3d(-4.3101, 1.7319, -1.6131));
        assertEquals(TOGETHER, geometric.parity());
    }

    /* slight torsion around double bond */
    @Test
    public void opposite_torsion() throws Exception {
        GeometricParity geometric = new DoubleBond3DParity(new Point3d(-4.6152, 0.3287, 0.5476), new Point3d(-3.6042,
                0.3501, -0.6139), new Point3d(-4.5881, -0.9142, 1.5627), new Point3d(-3.1478, 1.6935, -1.2129));
        assertEquals(OPPOSITE, geometric.parity());
    }

    /* slight torsion around double bond */
    @Test
    public void together_torsion() throws Exception {
        GeometricParity geometric = new DoubleBond3DParity(new Point3d(-4.6152, 0.3287, 0.5476), new Point3d(-3.6042,
                0.3501, -0.6139), new Point3d(-5.6414, 1.6608, 0.7013), new Point3d(-3.1478, 1.6935, -1.2129));
        assertEquals(TOGETHER, geometric.parity());
    }
}
