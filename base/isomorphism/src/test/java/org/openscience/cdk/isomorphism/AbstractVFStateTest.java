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

package org.openscience.cdk.isomorphism;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openscience.cdk.isomorphism.AbstractVFState.UNMAPPED;

/**
 * @author John May
 * @cdk.module test-isomorphism
 */
public class AbstractVFStateTest {

    // size = 0, always the first vertex
    @Test
    public void nextNAt0() {
        AbstractVFState state = create(5, 10);
        assertThat(state.nextN(-1), is(0));
    }

    // size > 0, select the first unmapped terminal vertex
    @Test
    public void nextNTerminal() {
        AbstractVFState state = create(5, 10);
        state.size = 2;
        state.m1[0] = 1;
        state.m1[1] = 0;
        state.t1[4] = 1; // <- first terminal
        assertThat(state.nextN(-1), is(4));
    }

    // no terminal mappings, select the first unmapped
    @Test
    public void nextNNonTerminal() {
        AbstractVFState state = create(5, 10);
        state.size = 2;
        state.m1[0] = 1;
        state.m1[1] = 0;
        assertThat(state.nextN(-1), is(2));
    }

    // size = 0, always the next vertex
    @Test
    public void nextMAt0() {
        AbstractVFState state = create(5, 10);
        assertThat(state.nextM(0, -1), is(0));
        assertThat(state.nextM(0, 0), is(1));
        assertThat(state.nextM(0, 1), is(2));
        assertThat(state.nextM(0, 2), is(3));
    }

    // size > 0, select the first unmapped terminal vertex
    @Test
    public void nextMTerminal() {
        AbstractVFState state = create(5, 10);
        state.size = 2;
        state.m2[0] = 1;
        state.m2[1] = 0;
        state.t1[1] = 1; // query vertex is in terminal set
        state.t2[4] = 1; // <- first terminal (not kept returned for now - allow disconnected)
        assertThat(state.nextM(1, -1), is(4));
    }

    // no terminal mappings, select the first unmapped
    @Test
    public void nextMNonTerminal() {
        AbstractVFState state = create(5, 10);
        state.size = 2;
        state.m2[0] = 1;
        state.m2[1] = 0;
        assertThat(state.nextM(0, -1), is(2));
    }

    @Test
    public void addNonFeasible() {
        AbstractVFState state = new AbstractVFState(new int[4][], new int[6][]) {

            @Override
            boolean feasible(int n, int m) {
                return false;
            }
        };
        assertFalse(state.add(0, 1));
        assertThat(state.size, is(0));
        assertThat(state.m1, is(new int[]{UNMAPPED, UNMAPPED, UNMAPPED, UNMAPPED}));
        assertThat(state.m2, is(new int[]{UNMAPPED, UNMAPPED, UNMAPPED, UNMAPPED, UNMAPPED, UNMAPPED}));
        assertThat(state.t1, is(new int[]{0, 0, 0, 0}));
        assertThat(state.t2, is(new int[]{0, 0, 0, 0, 0, 0}));
    }

    @Test
    public void add() {
        int[][] g1 = new int[][]{{1}, {0, 2}, {1, 3}, {2}};
        int[][] g2 = new int[][]{{1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4}};
        AbstractVFState state = create(g1, g2);
        assertTrue(state.add(0, 1));
        assertThat(state.size, is(1));
        assertThat(state.m1, is(new int[]{1, UNMAPPED, UNMAPPED, UNMAPPED}));
        assertThat(state.m2, is(new int[]{UNMAPPED, 0, UNMAPPED, UNMAPPED, UNMAPPED, UNMAPPED}));
        assertThat(state.t1, is(new int[]{0, 1, 0, 0}));
        assertThat(state.t2, is(new int[]{1, 0, 1, 0, 0, 0}));
        assertTrue(state.add(1, 2));
        assertThat(state.size, is(2));
        assertThat(state.m1, is(new int[]{1, 2, UNMAPPED, UNMAPPED}));
        assertThat(state.m2, is(new int[]{UNMAPPED, 0, 1, UNMAPPED, UNMAPPED, UNMAPPED}));
        assertThat(state.t1, is(new int[]{2, 1, 2, 0}));
        assertThat(state.t2, is(new int[]{1, 2, 1, 2, 0, 0}));
    }

    @Test
    public void remove() {
        int[][] g1 = new int[][]{{1}, {0, 2}, {1, 3}, {2}};
        int[][] g2 = new int[][]{{1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4}};
        AbstractVFState state = create(g1, g2);
        state.size = 2;
        // see add()
        state.m1[0] = 1;
        state.m1[1] = 2;
        state.m2[1] = 0;
        state.m2[2] = 1;
        state.t1[0] = 2;
        state.t1[1] = 1;
        state.t1[2] = 2;
        state.t2[0] = 1;
        state.t2[1] = 2;
        state.t2[2] = 1;
        state.t2[3] = 2;
        assertThat(state.size, is(2));
        assertThat(state.m1, is(new int[]{1, 2, UNMAPPED, UNMAPPED}));
        assertThat(state.m2, is(new int[]{UNMAPPED, 0, 1, UNMAPPED, UNMAPPED, UNMAPPED}));
        assertThat(state.t1, is(new int[]{2, 1, 2, 0}));
        assertThat(state.t2, is(new int[]{1, 2, 1, 2, 0, 0}));
        state.remove(1, 2);
        assertThat(state.size, is(1));
        assertThat(state.m1, is(new int[]{1, UNMAPPED, UNMAPPED, UNMAPPED}));
        assertThat(state.m2, is(new int[]{UNMAPPED, 0, UNMAPPED, UNMAPPED, UNMAPPED, UNMAPPED}));
        assertThat(state.t1, is(new int[]{0, 1, 0, 0}));
        assertThat(state.t2, is(new int[]{1, 0, 1, 0, 0, 0}));
        state.remove(0, 1);
        assertThat(state.size, is(0));
        assertThat(state.m1, is(new int[]{UNMAPPED, UNMAPPED, UNMAPPED, UNMAPPED}));
        assertThat(state.m2, is(new int[]{UNMAPPED, UNMAPPED, UNMAPPED, UNMAPPED, UNMAPPED, UNMAPPED}));
        assertThat(state.t1, is(new int[]{0, 0, 0, 0}));
        assertThat(state.t2, is(new int[]{0, 0, 0, 0, 0, 0}));
    }

    @Test
    public void copyMapping() {
        int[][] g1 = new int[][]{{1}, {0, 2}, {1, 3}, {2}};
        int[][] g2 = new int[][]{{1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4}};
        AbstractVFState state = create(g1, g2);
        state.m1[0] = 1;
        state.m1[1] = 2;
        state.m1[2] = 5;
        state.m1[3] = 6;
        assertThat(state.mapping(), is(state.m1));
        assertThat(state.mapping(), is(not(sameInstance(state.m1))));
    }

    @Test
    public void accessors() {
        int[][] g1 = new int[][]{{1}, {0, 2}, {1, 3}, {2}};
        int[][] g2 = new int[][]{{1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4}};
        AbstractVFState state = create(g1, g2);
        assertThat(state.nMax(), is(g1.length));
        assertThat(state.mMax(), is(g2.length));
        assertThat(state.size(), is(0));
        state.size = 2;
        assertThat(state.size(), is(2));
    }

    public AbstractVFState create(int g1Size, int g2Size) {
        return create(new int[g1Size][0], new int[g2Size][0]);
    }

    public AbstractVFState create(int[][] g1, int[][] g2) {
        return new AbstractVFState(g1, g2) {

            @Override
            boolean feasible(int n, int m) {
                return true;
            }
        };
    }

}
