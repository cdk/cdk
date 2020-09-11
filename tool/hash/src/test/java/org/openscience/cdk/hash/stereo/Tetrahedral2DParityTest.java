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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.hash.stereo.GeometricParity;
import org.openscience.cdk.hash.stereo.Tetrahedral2DParity;

import javax.vecmath.Point2d;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class Tetrahedral2DParityTest {

    private static final int CLOCKWISE     = -1;
    private static final int ANTICLOCKWISE = +1;
    private static final int NONE          = 0;

    @Test(expected = IllegalArgumentException.class)
    public void testConstruction_InvalidCoords() {
        new Tetrahedral2DParity(new Point2d[0], new int[4]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstruction_InvalidElev() {
        new Tetrahedral2DParity(new Point2d[4], new int[0]);
    }

    /**
     * aminoethanol (explicit H) hatch bond on hydrogen (none,none,none,down)
     *
     * @cdk.inchi InChI=1S/C2H7NO/c1-2(3)4/h2,4H,3H2,1H3/t2-/m1/s1
     */
    @Test
    public void testParity_Four_NNND() {
        Point2d[] coords = new Point2d[]{new Point2d(-7.75, 5.79), // -O
                new Point2d(-6.42, 3.48), // -N
                new Point2d(-9.09, 3.48), // -C
                new Point2d(-9.09, 5.02), // -H (down)
        };
        int[] elev = new int[]{0, 0, 0, -1};
        GeometricParity parity = new Tetrahedral2DParity(coords, elev);
        org.hamcrest.MatcherAssert.assertThat(parity.parity(), is(CLOCKWISE));
    }

    /**
     * aminoethanol (explicit H) wedge on hydrogen (none,none,none,up)
     *
     * @cdk.inchi InChI=1S/C2H7NO/c1-2(3)4/h2,4H,3H2,1H3/t2-/m1/s1
     */
    @Test
    public void testParity_Four_NNNU() {
        Point2d[] coords = new Point2d[]{new Point2d(-7.75, 5.79), // -O
                new Point2d(-6.42, 3.48), // -N
                new Point2d(-9.09, 3.48), // -C
                new Point2d(-9.09, 5.02), // -H (up)
        };
        int[] elev = new int[]{0, 0, 0, 1};
        GeometricParity parity = new Tetrahedral2DParity(coords, elev);
        org.hamcrest.MatcherAssert.assertThat(parity.parity(), is(ANTICLOCKWISE));
    }

    /**
     * aminoethanol (explicit H) with no wedge/hatch bonds
     * (none,none,none,none)
     *
     * @cdk.inchi InChI=1S/C2H7NO/c1-2(3)4/h2,4H,3H2,1H3/t2-/m1/s1
     */
    @Test
    public void testParity_Four_NNNN() {
        Point2d[] coords = new Point2d[]{new Point2d(-7.75, 5.79), // -O
                new Point2d(-6.42, 3.48), // -N
                new Point2d(-9.09, 3.48), // -C
                new Point2d(-9.09, 5.02), // -H
        };
        int[] elev = new int[]{0, 0, 0, 0}; // no wedge/hatch bonds
        GeometricParity parity = new Tetrahedral2DParity(coords, elev);
        org.hamcrest.MatcherAssert.assertThat(parity.parity(), is(NONE));
    }

    /**
     * aminoethanol (explicit H) with a wedge bond on non hydrogens
     * (up,up,up,none)
     *
     * @cdk.inchi InChI=1S/C2H7NO/c1-2(3)4/h2,4H,3H2,1H3/t2-/m1/s1
     */
    @Test
    public void testParity_Four_UUUN() {
        Point2d[] coords = new Point2d[]{new Point2d(-7.75, 5.79), // -O
                new Point2d(-6.42, 3.48), // -N
                new Point2d(-9.09, 3.48), // -C
                new Point2d(-9.09, 5.02), // -H
        };
        int[] elev = new int[]{1, 1, 1, 0}; // no wedge/hatch bonds
        GeometricParity parity = new Tetrahedral2DParity(coords, elev);
        org.hamcrest.MatcherAssert.assertThat(parity.parity(), is(CLOCKWISE));
    }

    /**
     * aminoethanol (explicit H) with a wedge bond on non hydrogens
     * (down,down,down,none)
     *
     * @cdk.inchi InChI=1S/C2H7NO/c1-2(3)4/h2,4H,3H2,1H3/t2-/m1/s1
     */
    @Test
    public void testParity_Four_DDDN() {
        Point2d[] coords = new Point2d[]{new Point2d(-7.75, 5.79), // -O
                new Point2d(-6.42, 3.48), // -N
                new Point2d(-9.09, 3.48), // -C
                new Point2d(-9.09, 5.02), // -H
        };
        int[] elev = new int[]{-1, -1, -1, 0}; // no wedge/hatch bonds
        GeometricParity parity = new Tetrahedral2DParity(coords, elev);
        org.hamcrest.MatcherAssert.assertThat(parity.parity(), is(ANTICLOCKWISE));
    }

    /**
     * aminoethanol (explicit H) with a wedge bond on all atoms (up,up,up,up) -
     * makes no sense
     *
     * @cdk.inchi InChI=1S/C2H7NO/c1-2(3)4/h2,4H,3H2,1H3/t2-/m1/s1
     */
    @Test
    public void testParity_Four_UUUU() {
        Point2d[] coords = new Point2d[]{new Point2d(-7.75, 5.79), // -O
                new Point2d(-6.42, 3.48), // -N
                new Point2d(-9.09, 3.48), // -C
                new Point2d(-9.09, 5.02), // -H
        };
        int[] elev = new int[]{1, 1, 1, 1}; // no wedge/hatch bonds
        GeometricParity parity = new Tetrahedral2DParity(coords, elev);
        org.hamcrest.MatcherAssert.assertThat(parity.parity(), is(NONE));
    }

    /**
     * aminoethanol (explicit H) with a hatch bond on all atoms
     * (down,down,down,down) - makes no sense
     *
     * @cdk.inchi InChI=1S/C2H7NO/c1-2(3)4/h2,4H,3H2,1H3/t2-/m1/s1
     */
    @Test
    public void testParity_Four_DDDD() {
        Point2d[] coords = new Point2d[]{new Point2d(-7.75, 5.79), // -O
                new Point2d(-6.42, 3.48), // -N
                new Point2d(-9.09, 3.48), // -C
                new Point2d(-9.09, 5.02), // -H
        };
        int[] elev = new int[]{-1, -1, -1, -1}; // no wedge/hatch bonds
        GeometricParity parity = new Tetrahedral2DParity(coords, elev);
        org.hamcrest.MatcherAssert.assertThat(parity.parity(), is(NONE));
    }

    /**
     * aminoethanol (implicit H) (up,none,none)
     *
     * @cdk.inchi InChI=1S/C2H7NO/c1-2(3)4/h2,4H,3H2,1H3/t2-/m1/s1
     */
    @Test
    public void testParity_Three_UNN() {
        Point2d[] coords = new Point2d[]{new Point2d(-7.75, 5.79), // -O (up)
                new Point2d(-6.42, 3.48), // -N
                new Point2d(-9.09, 3.48), // -C
                new Point2d(-7.75, 4.25), //  C (centre)
        };
        int[] elev = new int[]{1, 0, 0, 0};
        GeometricParity parity = new Tetrahedral2DParity(coords, elev);
        org.hamcrest.MatcherAssert.assertThat(parity.parity(), is(CLOCKWISE));
    }

    /**
     * aminoethanol (implicit H) (up,up,up)
     *
     * @cdk.inchi InChI=1S/C2H7NO/c1-2(3)4/h2,4H,3H2,1H3/t2-/m1/s1
     */
    @Test
    public void testParity_Three_UUU() {
        Point2d[] coords = new Point2d[]{new Point2d(-7.75, 5.79), // -O (up)
                new Point2d(-6.42, 3.48), // -N
                new Point2d(-9.09, 3.48), // -C
                new Point2d(-7.75, 4.25), //  C (centre)
        };
        int[] elev = new int[]{1, 1, 1, 0};
        GeometricParity parity = new Tetrahedral2DParity(coords, elev);
        org.hamcrest.MatcherAssert.assertThat(parity.parity(), is(CLOCKWISE));
    }

    /**
     * aminoethanol (implicit H) (down, none, none)
     *
     * @cdk.inchi InChI=1S/C2H7NO/c1-2(3)4/h2,4H,3H2,1H3/t2-/m1/s1
     */
    @Test
    public void testParity_Three_DNN() {
        Point2d[] coords = new Point2d[]{new Point2d(-7.75, 5.79), // -O (down)
                new Point2d(-6.42, 3.48), // -N
                new Point2d(-9.09, 3.48), // -C
                new Point2d(-7.75, 4.25), //  C (centre)
        };
        int[] elev = new int[]{-1, 0, 0, 0};
        GeometricParity parity = new Tetrahedral2DParity(coords, elev);
        org.hamcrest.MatcherAssert.assertThat(parity.parity(), is(ANTICLOCKWISE));
    }

    /**
     * aminoethanol (implicit H) (down, none, none)
     *
     * @cdk.inchi InChI=1S/C2H7NO/c1-2(3)4/h2,4H,3H2,1H3/t2-/m1/s1
     */
    @Test
    public void testParity_Three_DDD() {
        Point2d[] coords = new Point2d[]{new Point2d(-7.75, 5.79), // -O (down)
                new Point2d(-6.42, 3.48), // -N
                new Point2d(-9.09, 3.48), // -C
                new Point2d(-7.75, 4.25), //  C (centre)
        };
        int[] elev = new int[]{-1, -1, -1, 0};
        GeometricParity parity = new Tetrahedral2DParity(coords, elev);
        org.hamcrest.MatcherAssert.assertThat(parity.parity(), is(ANTICLOCKWISE));
    }

}
