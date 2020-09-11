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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.graph.InitialCyclesTest.anthracene;
import static org.openscience.cdk.graph.InitialCyclesTest.bicyclo;
import static org.openscience.cdk.graph.InitialCyclesTest.k4;
import static org.openscience.cdk.graph.InitialCyclesTest.naphthalene;
import static org.openscience.cdk.graph.InitialCyclesTest.norbornane;

/**
 * @author John May
 * @cdk.module test-core
 */
public class TripletShortCyclesTest {

    @Test
    public void lexicographic() {

        int[] exp = new int[]{0, 1, 2, 3, 4};

        assertThat(TripletShortCycles.lexicographic(new int[]{0, 1, 2, 3, 4}), is(exp));
        assertThat(TripletShortCycles.lexicographic(new int[]{4, 0, 1, 2, 3}), is(exp));
        assertThat(TripletShortCycles.lexicographic(new int[]{3, 4, 0, 1, 2}), is(exp));
        assertThat(TripletShortCycles.lexicographic(new int[]{2, 3, 4, 0, 1}), is(exp));
        assertThat(TripletShortCycles.lexicographic(new int[]{1, 2, 3, 4, 0}), is(exp));

        assertThat(TripletShortCycles.lexicographic(new int[]{4, 3, 2, 1, 0}), is(exp));
        assertThat(TripletShortCycles.lexicographic(new int[]{3, 2, 1, 0, 4}), is(exp));
        assertThat(TripletShortCycles.lexicographic(new int[]{2, 1, 0, 4, 3}), is(exp));
        assertThat(TripletShortCycles.lexicographic(new int[]{1, 0, 4, 3, 2}), is(exp));
        assertThat(TripletShortCycles.lexicographic(new int[]{0, 4, 3, 2, 1}), is(exp));
    }

    @Test
    public void empty() throws Exception {
        TripletShortCycles esssr = new TripletShortCycles(new MinimumCycleBasis(new int[0][]), false);
        int[][] paths = esssr.paths();
        assertThat(paths, is(new int[0][]));
    }

    @Test
    public void unmodifiable() throws Exception {
        TripletShortCycles esssr = new TripletShortCycles(new MinimumCycleBasis(k4()), false);
        int[][] paths = esssr.paths();
        // modify paths
        for (int[] path : paths) {
            for (int i = 0; i < path.length; i++) {
                path[i] = path[i] + 1;
            }
        }
        // the internal paths should not be changed
        assertThat(paths, is(not(esssr.paths())));
    }

    @Test
    public void naphthalenePaths() {
        TripletShortCycles esssr = new TripletShortCycles(new MinimumCycleBasis(naphthalene()), false);
        int[][] paths = esssr.paths();
        assertThat(paths[0].length - 1, is(6));
        assertThat(paths[1].length - 1, is(6));
        assertThat(paths[0], is(not(paths[1])));
        assertThat(paths[2].length - 1, is(10));
    }

    @Test
    public void naphthaleneSize() {
        TripletShortCycles esssr = new TripletShortCycles(new MinimumCycleBasis(naphthalene()), false);
        assertThat(esssr.size(), is(3));
    }

    @Test
    public void anthracenePaths() {
        TripletShortCycles esssr = new TripletShortCycles(new MinimumCycleBasis(anthracene()), false);
        int[][] paths = esssr.paths();
        assertThat(paths[0].length - 1, is(6));
        assertThat(paths[1].length - 1, is(6));
        assertThat(paths[2].length - 1, is(6));

        assertThat(paths[0], is(not(paths[1])));
        assertThat(paths[0], is(not(paths[2])));
        assertThat(paths[1], is(not(paths[2])));

        assertThat(paths[3].length - 1, is(10));
        assertThat(paths[4].length - 1, is(10));

        assertThat(paths[3], is(not(paths[4])));
    }

    @Test
    public void anthraceneSize() {
        TripletShortCycles esssr = new TripletShortCycles(new MinimumCycleBasis(anthracene()), false);
        assertThat(esssr.size(), is(5));
    }

    @Test
    public void bicycloPaths() {
        TripletShortCycles esssr = new TripletShortCycles(new MinimumCycleBasis(bicyclo()), false);
        int[][] paths = esssr.paths();
        assertThat(paths[0].length - 1, is(6));
        assertThat(paths[1].length - 1, is(6));
        assertThat(paths[2].length - 1, is(6));

        assertThat(paths[0], is(not(paths[1])));
        assertThat(paths[0], is(not(paths[2])));
        assertThat(paths[1], is(not(paths[2])));
    }

    @Test
    public void bicycloSize() {
        TripletShortCycles esssr = new TripletShortCycles(new MinimumCycleBasis(bicyclo()), false);
        assertThat(esssr.size(), is(3));
    }

    @Test
    public void norbornanePaths() {
        TripletShortCycles esssr = new TripletShortCycles(new MinimumCycleBasis(norbornane()), false);
        int[][] paths = esssr.paths();
        assertThat(paths[0].length - 1, is(5));
        assertThat(paths[1].length - 1, is(5));
        assertThat(paths[2].length - 1, is(6));

        assertThat(paths[0], is(not(paths[1])));
    }

    @Test
    public void norbornaneSize() {
        TripletShortCycles esssr = new TripletShortCycles(new MinimumCycleBasis(norbornane()), false);
        assertThat(esssr.size(), is(3));
    }

    /**
     * Ensures non-canonic really does give you all cycles. If we didn't use
     * multiple shortest paths here we would miss one of the larger cycles
     */
    @Test
    public void cyclophanePaths() {
        TripletShortCycles esssr = new TripletShortCycles(new MinimumCycleBasis(cyclophane()), false);
        assertThat(esssr.paths(), is(new int[][]{{0, 1, 2, 3, 4, 5, 0}, {6, 7, 8, 9, 10, 11, 6},
                {0, 1, 2, 3, 17, 18, 19, 16, 9, 8, 7, 6, 15, 14, 13, 12, 0},
                {0, 1, 2, 3, 17, 18, 19, 16, 9, 10, 11, 6, 15, 14, 13, 12, 0},
                {0, 5, 4, 3, 17, 18, 19, 16, 9, 8, 7, 6, 15, 14, 13, 12, 0},
                {0, 5, 4, 3, 17, 18, 19, 16, 9, 10, 11, 6, 15, 14, 13, 12, 0}}));
    }

    @Test
    public void cyclophaneSize() {
        TripletShortCycles esssr = new TripletShortCycles(new MinimumCycleBasis(cyclophane()), false);
        assertThat(esssr.size(), is(6));
    }

    /** @cdk.inchi InChI=1S/C20H36/c1-2-6-18-13-15-20(16-14-18)8-4-3-7-19-11-9-17(5-1)10-12-19/h17-20H,1-16H2 */
    static int[][] cyclophane() {
        return new int[][]{{1, 5, 12}, {0, 2}, {1, 3}, {2, 4, 17}, {3, 5}, {4, 0}, {7, 11, 15}, {6, 8}, {7, 9},
                {8, 10, 16}, {9, 11}, {10, 6}, {0, 13}, {12, 14}, {13, 15}, {14, 6}, {9, 19}, {3, 18}, {17, 19},
                {18, 16}};
    }
}
