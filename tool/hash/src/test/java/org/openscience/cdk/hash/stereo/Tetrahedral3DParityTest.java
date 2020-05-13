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
import org.openscience.cdk.hash.stereo.Tetrahedral3DParity;

import javax.vecmath.Point3d;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class Tetrahedral3DParityTest {

    private static int CLOCKWISE     = -1;
    private static int ANTICLOCKWISE = +1;

    @Test(expected = IllegalArgumentException.class)
    public void testConstruction_Empty() throws Exception {
        new Tetrahedral3DParity(new Point3d[0]);
    }

    @Test
    public void testParity_Three_Clockwise() {
        Point3d[] coords = new Point3d[]{new Point3d(1.70, 0.98, -0.51), // -O
                new Point3d(2.65, -0.83, 0.62), // -N
                new Point3d(0.26, -0.33, 0.95), // -C
                new Point3d(1.44, -0.33, -0.03), // C (centre)
        };
        assertThat(new Tetrahedral3DParity(coords).parity(), is(CLOCKWISE));
    }

    @Test
    public void testParity_Three_Anticlockwise() {
        Point3d[] coords = new Point3d[]{new Point3d(1.70, 0.98, -0.51), // -O
                new Point3d(0.26, -0.33, 0.95), // -C
                new Point3d(2.65, -0.83, 0.62), // -N
                new Point3d(1.44, -0.33, -0.03), // C (centre)
        };
        assertThat(new Tetrahedral3DParity(coords).parity(), is(ANTICLOCKWISE));
    }

    @Test
    public void testParity_Four_Clockwise() {
        Point3d[] coords = new Point3d[]{new Point3d(1.70, 0.98, -0.51), // -O
                new Point3d(2.65, -0.83, 0.62), // -N
                new Point3d(0.26, -0.33, 0.95), // -C
                new Point3d(1.21, -0.97, -0.89), // -H
        };
        assertThat(new Tetrahedral3DParity(coords).parity(), is(CLOCKWISE));
    }

    @Test
    public void testParity_Four_Anticlockwise() {
        Point3d[] coords = new Point3d[]{new Point3d(1.70, 0.98, -0.51), // -O
                new Point3d(0.26, -0.33, 0.95), // -C
                new Point3d(2.65, -0.83, 0.62), // -N
                new Point3d(1.21, -0.97, -0.89), // -H
        };
        assertThat(new Tetrahedral3DParity(coords).parity(), is(ANTICLOCKWISE));
    }

}
