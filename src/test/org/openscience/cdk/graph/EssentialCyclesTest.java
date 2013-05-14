/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
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
import static org.junit.Assert.*;
import static org.openscience.cdk.graph.InitialCyclesTest.anthracene;
import static org.openscience.cdk.graph.InitialCyclesTest.bicyclo;
import static org.openscience.cdk.graph.InitialCyclesTest.cyclophane_even;
import static org.openscience.cdk.graph.InitialCyclesTest.napthalene;

/**
 * @author John May
 * @cdk.module test-core
 */
public class EssentialCyclesTest {

    @Test public void paths_bicyclo() {
        int[][] bicyclo = bicyclo();
        EssentialCycles essential = new EssentialCycles(bicyclo);
        int[][] paths = essential.paths();
        assertThat(paths, is(new int[0][0]));
    }

    @Test public void paths_napthalene() {
        int[][] napthalene = napthalene();
        EssentialCycles essential = new EssentialCycles(napthalene);
        int[][] paths = essential.paths();
        int[][] expected = new int[][]{{5, 0, 1, 2, 3, 4},
                                       {5, 4, 7, 8, 9, 6}};
        assertThat(paths, is(expected));
    }

    @Test public void paths_anthracene() {
        int[][] anthracene = anthracene();
        EssentialCycles essential = new EssentialCycles(anthracene);
        int[][] paths = essential.paths();
        int[][] expected = new int[][]{{5, 0, 1, 2, 3, 4},
                                       {9, 8, 10, 11, 12, 13},
                                       {9, 6, 5, 4, 7, 8}};
        assertThat(paths, is(expected));
    }

    @Test public void paths_cyclophane_even() {
        int[][] cyclophane_even = cyclophane_even();
        EssentialCycles essential = new EssentialCycles(cyclophane_even);
        int[][] paths = essential.paths();
        int[][] expected = new int[][]{{3, 2, 1, 0, 5, 4}};
        assertThat(paths, is(expected));
    }

    @Test public void paths_cyclophane_odd() {
        int[][] cyclophane_even = cyclophane_even();
        EssentialCycles essential = new EssentialCycles(cyclophane_even);
        int[][] paths = essential.paths();
        int[][] expected = new int[][]{{3, 2, 1, 0, 5, 4}};
        assertThat(paths, is(expected));
    }

    @Test public void size_bicyclo() {
        int[][] bicyclo = bicyclo();
        EssentialCycles essential = new EssentialCycles(bicyclo);
        assertThat(essential.size(), is(0));
    }

    @Test public void size_napthalene() {
        int[][] napthalene = napthalene();
        EssentialCycles essential = new EssentialCycles(napthalene);
        assertThat(essential.size(), is(2));
    }

    @Test public void size_anthracene() {
        int[][] anthracene = anthracene();
        EssentialCycles essential = new EssentialCycles(anthracene);
        assertThat(essential.size(), is(3));
    }

    @Test public void size_cyclophane_even() {
        int[][] cyclophane_even = cyclophane_even();
        EssentialCycles relevant = new EssentialCycles(cyclophane_even);
        assertThat(relevant.size(), is(1));
    }

    @Test public void size_cyclophane_odd() {
        int[][] cyclophane_even = cyclophane_even();
        EssentialCycles essential = new EssentialCycles(cyclophane_even);
        assertThat(essential.size(), is(1));
    }
}
