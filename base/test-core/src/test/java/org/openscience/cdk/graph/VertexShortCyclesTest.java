/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.graph;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.graph.InitialCyclesTest.anthracene;
import static org.openscience.cdk.graph.InitialCyclesTest.bicyclo;
import static org.openscience.cdk.graph.InitialCyclesTest.cyclophane_even;
import static org.openscience.cdk.graph.InitialCyclesTest.naphthalene;
import static org.openscience.cdk.graph.InitialCyclesTest.norbornane;

/**
 * @author John May
 * @cdk.module test-core
 */
public class VertexShortCyclesTest {

    @Test
    public void paths_norbornane() {
        int[][] norbornane = norbornane();
        VertexShortCycles vsc = new VertexShortCycles(norbornane);
        int[][] paths = vsc.paths();
        int[][] expected = new int[][]{{5, 6, 2, 1, 0, 5}, {5, 6, 2, 3, 4, 5}};
        assertThat(paths, is(expected));
    }

    @Test
    public void paths_bicyclo() {
        int[][] bicyclo = bicyclo();
        VertexShortCycles vsc = new VertexShortCycles(bicyclo);
        int[][] paths = vsc.paths();
        int[][] expected = new int[][]{{5, 0, 1, 2, 3, 4, 5}, {5, 0, 1, 2, 7, 6, 5}, {5, 4, 3, 2, 7, 6, 5}};
        assertThat(paths, is(expected));
    }

    @Test
    public void paths_napthalene() {
        int[][] napthalene = naphthalene();
        VertexShortCycles vsc = new VertexShortCycles(napthalene);
        int[][] paths = vsc.paths();
        int[][] expected = new int[][]{{5, 0, 1, 2, 3, 4, 5}, {5, 4, 7, 8, 9, 6, 5}};
        assertThat(paths, is(expected));
    }

    @Test
    public void paths_anthracene() {
        int[][] anthracene = anthracene();
        VertexShortCycles vsc = new VertexShortCycles(anthracene);
        int[][] paths = vsc.paths();
        int[][] expected = new int[][]{{5, 0, 1, 2, 3, 4, 5}, {9, 6, 5, 4, 7, 8, 9}, {9, 8, 10, 11, 12, 13, 9}};
        assertThat(paths, is(expected));
    }

    @Test
    public void paths_cyclophane_even() {
        int[][] cyclophane_even = cyclophane_even();
        VertexShortCycles vsc = new VertexShortCycles(cyclophane_even);
        int[][] paths = vsc.paths();
        int[][] expected = new int[][]{{3, 2, 1, 0, 5, 4, 3}, {3, 6, 7, 8, 9, 10, 11, 0, 1, 2, 3},
                {3, 6, 7, 8, 9, 10, 11, 0, 5, 4, 3}};
        assertThat(paths, is(expected));
    }

    @Test
    public void paths_cyclophane_odd() {
        int[][] cyclophane_even = cyclophane_even();
        VertexShortCycles vsc = new VertexShortCycles(cyclophane_even);
        int[][] paths = vsc.paths();
        int[][] expected = new int[][]{{3, 2, 1, 0, 5, 4, 3}, {3, 6, 7, 8, 9, 10, 11, 0, 1, 2, 3},
                {3, 6, 7, 8, 9, 10, 11, 0, 5, 4, 3}};
        assertThat(paths, is(expected));
    }

    @Test
    public void size_norbornane() {
        int[][] norbornane = norbornane();
        VertexShortCycles vsc = new VertexShortCycles(norbornane);
        int[][] paths = vsc.paths();
        assertThat(paths.length, is(2));
    }

    @Test
    public void size_bicyclo() {
        int[][] bicyclo = bicyclo();
        VertexShortCycles vsc = new VertexShortCycles(bicyclo);
        assertThat(vsc.size(), is(3));
    }

    @Test
    public void size_napthalene() {
        int[][] napthalene = naphthalene();
        VertexShortCycles vsc = new VertexShortCycles(napthalene);
        assertThat(vsc.size(), is(2));
    }

    @Test
    public void size_anthracene() {
        int[][] anthracene = anthracene();
        VertexShortCycles vsc = new VertexShortCycles(anthracene);
        assertThat(vsc.size(), is(3));
    }

    @Test
    public void size_cyclophane_even() {
        int[][] cyclophane_even = cyclophane_even();
        VertexShortCycles vsc = new VertexShortCycles(cyclophane_even);
        assertThat(vsc.size(), is(3));
    }

    @Test
    public void size_cyclophane_odd() {
        int[][] cyclophane_even = cyclophane_even();
        VertexShortCycles vsc = new VertexShortCycles(cyclophane_even);
        assertThat(vsc.size(), is(3));
    }

    @Test
    public void paths_cyclophanelike1() {
        int[][] g = cyclophanelike1();
        VertexShortCycles vsc = new VertexShortCycles(g);
        int[][] paths = vsc.paths();
        int[][] expected = new int[][]{{5, 0, 1, 2, 3, 4, 5}, {8, 7, 6, 5, 10, 9, 8}, {13, 12, 11, 8, 19, 18, 13},
                {13, 14, 15, 2, 17, 16, 13}};
        assertThat(paths, is(expected));
    }

    @Test
    public void paths_cyclophanelike2() {
        int[][] g = cyclophanelike2();
        VertexShortCycles vsc = new VertexShortCycles(g);
        int[][] paths = vsc.paths();
        int[][] expected = new int[][]{{5, 0, 1, 2, 3, 4, 5}, {9, 8, 7, 6, 11, 10, 9}, {15, 14, 13, 12, 17, 16, 15},
                {21, 20, 19, 18, 23, 22, 21}};
        assertThat(paths, is(expected));
    }

    /**
     * @cdk.inchi InChI=1/C20H32/c1-2-18-6-3-17(1)4-7-19(8-5-17)13-15-20(11-9-18,12-10-18)16-14-19/h1-16H2
     */
    static int[][] cyclophanelike1() {
        return new int[][]{{1, 5}, {0, 2}, {1, 3, 15, 17}, {2, 4}, {3, 5}, {4, 0, 6, 10}, {5, 7}, {6, 8},
                {7, 9, 11, 19}, {8, 10}, {9, 5}, {8, 12}, {11, 13}, {12, 14, 16, 18}, {13, 15}, {14, 2}, {13, 17},
                {16, 2}, {13, 19}, {18, 8}};
    }

    /**
     * @cdk.inchi InChI=1/C24H40/c1-2-18-4-3-17(1)19-5-7-21(8-6-19)23-13-15-24(16-14-23)22-11-9-20(18)10-12-22/h17-24H,1-16H2
     */
    static int[][] cyclophanelike2() {
        return new int[][]{{1, 5}, {0, 2}, {1, 3, 21}, {2, 4}, {3, 5}, {4, 0, 6}, {5, 7, 11}, {6, 8}, {7, 9},
                {8, 10, 12}, {9, 11}, {10, 6}, {9, 13, 17}, {12, 14}, {13, 15}, {14, 16, 18}, {15, 17}, {16, 12},
                {15, 19, 23}, {18, 20}, {19, 21}, {20, 2, 22}, {21, 23}, {22, 18}};
    }
}
