/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 * 			  John May <jwmay@users.sf.net>
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openscience.cdk.graph.JumboPathGraph.PathEdge;
import static org.openscience.cdk.graph.JumboPathGraph.ReducedEdge;
import static org.openscience.cdk.graph.JumboPathGraph.SimpleEdge;

/** @cdk.module test-core */
public class JumboPathGraphTest {

    @Test(expected = NullPointerException.class)
    public void nullMGraph() {
        new JumboPathGraph(null, new int[0], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void limitTooLow() {
        new JumboPathGraph(new int[4][], new int[0], -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void limitTooHigh() {
        new JumboPathGraph(new int[4][], new int[0], 5);
    }

    /* re-invoking the remove on the same vertex should not do anything */
    @Test
    public void repeatRemoval() {
        int ord = 3;
        int[][] k3 = completeGraphOfSize(ord);
        JumboPathGraph pg = new JumboPathGraph(k3, identity(3), ord);
        List<int[]> cycles = new ArrayList<int[]>();
        pg.remove(0, cycles);
        assertThat(cycles.size(), is(0));
        pg.remove(0, cycles);
        assertThat(cycles.size(), is(0));
        pg.remove(1, cycles);
        assertThat(cycles.size(), is(1));
        pg.remove(1, cycles);
        assertThat(cycles.size(), is(1));
        pg.remove(2, cycles);
        assertThat(cycles.size(), is(1));
        pg.remove(2, cycles);
        assertThat(cycles.size(), is(1));
    }

    @Test
    public void k3Degree() {
        int ord = 3;
        int[][] k3 = completeGraphOfSize(ord);
        JumboPathGraph pg = new JumboPathGraph(k3, identity(3), ord);
        // note - vertices are only added to either vertex (lowest rank)
        assertThat(pg.degree(0), is(2));
        assertThat(pg.degree(1), is(1));
        assertThat(pg.degree(2), is(0));
        pg.remove(0, new ArrayList<int[]>(0));
        assertThat(pg.degree(0), is(0));
        assertThat(pg.degree(1), is(2));
        assertThat(pg.degree(2), is(0));
        pg.remove(1, new ArrayList<int[]>(0));
        assertThat(pg.degree(0), is(0));
        assertThat(pg.degree(1), is(0));
        assertThat(pg.degree(2), is(0));
    }

    /* graph with 3 vertices where each vertex is connected to every vertex. */
    @Test(timeout = 200)
    public void k3() {
        int ord = 3;
        int[][] k3 = completeGraphOfSize(ord);
        JumboPathGraph pg = new JumboPathGraph(k3, identity(3), ord);
        List<int[]> cycles = new ArrayList<int[]>();
        for (int v = 0; v < ord; v++)
            pg.remove(v, cycles);
        assertThat(cycles.size(), is(1));
    }

    /* graph with 8 vertices where each vertex is connected to every vertex. */
    @Test(timeout = 200)
    public void k8() {
        int ord = 8;
        int[][] k8 = completeGraphOfSize(ord);
        JumboPathGraph pg = new JumboPathGraph(k8, identity(8), ord);
        List<int[]> cycles = new ArrayList<int[]>();
        for (int v = 0; v < ord; v++)
            pg.remove(v, cycles);
        assertThat(cycles.size(), is(8018));
    }

    public static int[] identity(int n) {
        int[] identity = new int[n];
        for (int i = 0; i < n; i++)
            identity[i] = i;
        return identity;
    }

    /* make a complete graph */
    static int[][] completeGraphOfSize(int n) {
        int[][] g = new int[n][];
        for (int v = 0; v < n; v++) {
            g[v] = new int[n - 1];
            int deg = 0;
            for (int w = 0; w < n; w++) {
                if (v != w) {
                    g[v][deg++] = w;
                }
            }
        }
        return g;
    }

    @Test
    public void loop() {
        assertFalse(new SimpleEdge(0, 1).loop());
        assertTrue(new SimpleEdge(0, 0).loop());
        assertFalse(new ReducedEdge(new SimpleEdge(0, 1), new SimpleEdge(2, 1), 1).loop());
        assertTrue(new ReducedEdge(new SimpleEdge(0, 1), new SimpleEdge(0, 1), 0).loop());
        assertTrue(new ReducedEdge(new SimpleEdge(0, 1), new SimpleEdge(0, 1), 1).loop());
    }

    @Test
    public void path() {
        assertThat(new SimpleEdge(0, 1).path(), is(new int[]{0, 1}));
        assertThat(new ReducedEdge(new SimpleEdge(0, 1), new SimpleEdge(2, 1), 1).path(), is(new int[]{0, 1, 2}));
        assertThat(new ReducedEdge(new ReducedEdge(new SimpleEdge(0, 1), new SimpleEdge(2, 1), 1), new ReducedEdge(
                new SimpleEdge(2, 3), new SimpleEdge(3, 4), 3), 2).path(), is(new int[]{0, 1, 2, 3, 4}));
    }

    @Test
    public void disjoint() {
        PathEdge e = new ReducedEdge(new SimpleEdge(0, 1), new SimpleEdge(2, 1), 1);
        PathEdge f = new ReducedEdge(new SimpleEdge(2, 3), new SimpleEdge(3, 4), 3);
        assertTrue(e.disjoint(f));
        assertTrue(f.disjoint(e));
        assertFalse(e.disjoint(e));
        assertFalse(f.disjoint(f));
    }

    @Test
    public void len() {
        assertThat(new SimpleEdge(0, 1).len(), is(2));
        PathEdge e = new ReducedEdge(new SimpleEdge(0, 1), new SimpleEdge(2, 1), 1);
        PathEdge f = new ReducedEdge(new SimpleEdge(2, 3), new SimpleEdge(3, 4), 3);
        assertThat(e.len(), is(3));
        assertThat(f.len(), is(3));
        assertThat(new ReducedEdge(e, f, 2).len(), is(5));
    }
}
