/*
 * Copyright (C) 2012 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version. All we ask is that proper credit is given for our
 * work, which includes - but is not limited to - adding the above copyright
 * notice to the beginning of your source code files, and to any copyright
 * notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.ringsearch;

import org.junit.Test;

import java.util.BitSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-core
 */
public class JumboCyclicVertexSearchTest {

    @Test
    public void testEmpty() {
        CyclicVertexSearch search = new JumboCyclicVertexSearch(new int[0][0]);
        assertNotNull(search);
    }

    @Test
    public void testCyclic() {
        // cyclohexane like
        int[][] g = new int[][]{{5, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 0}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        assertThat(search.cyclic(), is(new int[]{0, 1, 2, 3, 4, 5}));
    }

    @Test
    public void testCyclic_Int() {
        // cyclohexane like
        int[][] g = new int[][]{{5, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 0}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        for (int v = 0; v < g.length; v++)
            assertTrue(search.cyclic(v));
    }

    @Test
    public void testCyclic_IntInt() {
        int[][] g = new int[][]{{5, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 0, 6}, {5}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        assertTrue(search.cyclic(0, 1));
        assertTrue(search.cyclic(1, 2));
        assertTrue(search.cyclic(2, 3));
        assertTrue(search.cyclic(3, 4));
        assertTrue(search.cyclic(4, 5));
        assertTrue(search.cyclic(5, 0));
        assertFalse(search.cyclic(5, 6));
    }

    @Test
    public void vertexColor() {
        // medium size spiro cyclo hexane like
        int[][] g = new int[][]{{1, 5}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {0, 4, 7, 8}, {7, 10}, {5, 6}, {5, 9}, {8, 10},
                {6, 9, 12, 13}, {12, 15}, {10, 11}, {10, 14}, {13, 15}, {11, 14, 17, 18}, {17, 20}, {15, 16}, {15, 19},
                {18, 20}, {16, 19, 22, 23}, {22, 25}, {20, 21}, {20, 24}, {23, 25}, {21, 24, 27, 28}, {27, 30},
                {25, 26}, {25, 29}, {28, 30}, {26, 29, 32, 33}, {32, 35}, {30, 31}, {30, 34}, {33, 35},
                {31, 34, 37, 38}, {37, 40}, {35, 36}, {35, 39}, {38, 40}, {36, 39, 42, 43}, {42, 45}, {40, 41},
                {40, 44}, {43, 45}, {41, 44, 47, 48}, {47, 50}, {45, 46}, {45, 49}, {48, 50}, {46, 49, 52, 53},
                {52, 55}, {50, 51}, {50, 54}, {53, 55}, {51, 54, 57, 58}, {57, 60}, {55, 56}, {55, 59}, {58, 60},
                {56, 59}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        int[] colors = search.vertexColor();
    }

    @Test
    public void testIsolated() {
        // cyclohexane like
        int[][] g = new int[][]{{5, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 0}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        assertThat(search.isolated(), is(new int[][]{{0, 1, 2, 3, 4, 5}}));
    }

    @Test
    public void testIsolated_NonCyclic() {
        int[][] g = new int[][]{{1}, {0, 2}, {1, 3}, {2, 4}, {3}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        assertThat(search.cyclic(), is(new int[0]));
    }

    @Test
    public void testIsolated_Empty() {
        CyclicVertexSearch search = new JumboCyclicVertexSearch(new int[0][0]);
        assertThat(search.cyclic(), is(new int[0]));
        assertThat(search.isolated(), is(new int[0][0]));
        assertThat(search.fused(), is(new int[0][0]));
    }

    /**
     * C1CCC2(CC1)CCCCC2
     */
    @Test
    public void testIsolated_Spiro() {
        // spiro cyclo hexane like
        int[][] g = new int[][]{{1, 5}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {0, 4, 7, 8}, {7, 10}, {5, 6}, {5, 9}, {8, 10},
                {6, 9}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        int[][] isolated = search.isolated();
        assertThat(isolated.length, is(2));
        assertThat(isolated[0], is(new int[]{0, 1, 2, 3, 4, 5}));
        assertThat(isolated[1], is(new int[]{5, 6, 7, 8, 9, 10}));
    }

    /**
     * C1CCC2(CC1)CCC1(CC2)CCC2(CC1)CCC1(CC2)CCC2(CC1)CCC1(CC2)CCC2(CCC3(CCC4(CCC5(CCC6(CCCCC6)CC5)CC4)CC3)CC2)CC1
     */
    @Test
    public void testIsolated_SpiroMedium() {
        // medium size spiro cyclo hexane like
        int[][] g = new int[][]{{1, 5}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {0, 4, 7, 8}, {7, 10}, {5, 6}, {5, 9}, {8, 10},
                {6, 9, 12, 13}, {12, 15}, {10, 11}, {10, 14}, {13, 15}, {11, 14, 17, 18}, {17, 20}, {15, 16}, {15, 19},
                {18, 20}, {16, 19, 22, 23}, {22, 25}, {20, 21}, {20, 24}, {23, 25}, {21, 24, 27, 28}, {27, 30},
                {25, 26}, {25, 29}, {28, 30}, {26, 29, 32, 33}, {32, 35}, {30, 31}, {30, 34}, {33, 35},
                {31, 34, 37, 38}, {37, 40}, {35, 36}, {35, 39}, {38, 40}, {36, 39, 42, 43}, {42, 45}, {40, 41},
                {40, 44}, {43, 45}, {41, 44, 47, 48}, {47, 50}, {45, 46}, {45, 49}, {48, 50}, {46, 49, 52, 53},
                {52, 55}, {50, 51}, {50, 54}, {53, 55}, {51, 54, 57, 58}, {57, 60}, {55, 56}, {55, 59}, {58, 60},
                {56, 59}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        int[][] isolated = search.isolated();
        assertThat(isolated.length, is(12));
        assertThat(isolated[0], is(new int[]{0, 1, 2, 3, 4, 5}));
        assertThat(isolated[1], is(new int[]{5, 6, 7, 8, 9, 10}));
        assertThat(isolated[2], is(new int[]{10, 11, 12, 13, 14, 15}));
        assertThat(isolated[3], is(new int[]{15, 16, 17, 18, 19, 20}));
        assertThat(isolated[4], is(new int[]{20, 21, 22, 23, 24, 25}));
        assertThat(isolated[5], is(new int[]{25, 26, 27, 28, 29, 30}));
        assertThat(isolated[6], is(new int[]{30, 31, 32, 33, 34, 35}));
        assertThat(isolated[7], is(new int[]{35, 36, 37, 38, 39, 40}));
        assertThat(isolated[8], is(new int[]{40, 41, 42, 43, 44, 45}));
        assertThat(isolated[9], is(new int[]{45, 46, 47, 48, 49, 50}));
        assertThat(isolated[10], is(new int[]{50, 51, 52, 53, 54, 55}));
        assertThat(isolated[11], is(new int[]{55, 56, 57, 58, 59, 60}));
    }

    @Test
    public void testIsolated_Biphenyl() {
        // biphenyl like
        int[][] g = new int[][]{{5, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 0, 6}, {11, 7, 5}, {6, 8}, {7, 9}, {8, 10},
                {9, 11}, {10, 7}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        assertThat(search.cyclic(), is(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}));
        int[][] isolated = search.isolated();
        assertThat(isolated.length, is(2));
        assertThat(isolated[0], is(new int[]{0, 1, 2, 3, 4, 5}));
        assertThat(isolated[1], is(new int[]{6, 7, 8, 9, 10, 11}));
    }

    /**
     * C(C1CCCCC1)C1CCCCC1
     */
    @Test
    public void testIsolated_BenzylBenzene() {
        // benzylbenzene like
        int[][] g = new int[][]{{1, 5}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {0, 4, 12}, {7, 11}, {6, 8}, {7, 9, 12},
                {8, 10}, {9, 11}, {6, 10}, {8, 5}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        int[][] isolated = search.isolated();
        assertThat(isolated.length, is(2));
        assertThat(isolated[0], is(new int[]{0, 1, 2, 3, 4, 5}));
        assertThat(isolated[1], is(new int[]{6, 7, 8, 9, 10, 11}));
    }

    @Test
    public void testIsolatedFragments() {
        // two disconnected cyclohexanes
        int[][] g = new int[][]{{5, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 0}, {11, 7}, {6, 8}, {7, 9}, {8, 10},
                {9, 11}, {10, 7}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        assertThat(search.cyclic(), is(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}));
        int[][] isolated = search.isolated();
        assertThat(isolated.length, is(2));
        assertThat(isolated[0], is(new int[]{0, 1, 2, 3, 4, 5}));
        assertThat(isolated[1], is(new int[]{6, 7, 8, 9, 10, 11}));
    }

    /**
     * C1CC2CCC1CC2
     */
    @Test
    public void testFused() {
        int[][] g = new int[][]{{1, 5, 6}, {0, 2}, {1, 3}, {2, 4, 7}, {3, 5}, {0, 4}, {0, 7}, {6, 3}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        int[][] isolated = search.isolated();
        int[][] fused = search.fused();
        assertThat(isolated.length, is(0));
        assertThat(fused.length, is(1));
        assertThat(fused[0].length, is(g.length));
    }

    /**
     * two fused systems which are edge disjoint with respect to each other but
     * have a (non cyclic) edge which connects them C1CC2(CCC1CC2)C12CCC(CC1)CC2
     */
    @Test
    public void testFused_BiocycloEdgeLinked() {
        // biocyclooctanylbiocylooctane like
        int[][] g = new int[][]{{1, 5, 6}, {0, 2}, {1, 3}, {2, 4, 7, 8}, {3, 5}, {0, 4}, {0, 7}, {6, 3},
                {9, 13, 14, 3}, {8, 10}, {9, 11}, {10, 12, 15}, {11, 13}, {8, 12}, {8, 15}, {11, 14}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        int[][] isolated = search.isolated();
        int[][] fused = search.fused();
        assertThat(isolated.length, is(0));
        assertThat(fused.length, is(2));
        assertThat(fused[0], is(new int[]{0, 1, 2, 3, 4, 5, 6, 7}));
        assertThat(fused[1], is(new int[]{8, 9, 10, 11, 12, 13, 14, 15}));
    }

    /**
     * two fused systems which are edge disjoint with respect to each other
     * however in between the two fused cycle systems there is a single non
     * cyclic vertex which is adjacent to both C(C12CCC(CC1)CC2)C12CCC(CC1)CC2
     */
    @Test
    public void testFused_BiocycloVertexLinked() {
        // biocyclooctanylbiocylooctane like
        int[][] g = new int[][]{{1, 5}, {0, 2}, {1, 3, 6, 16}, {2, 4}, {3, 5}, {4, 0, 7}, {2, 7}, {6, 5}, {9, 13},
                {8, 10}, {9, 11, 14}, {10, 12}, {11, 13}, {12, 8, 15, 16}, {10, 15}, {14, 13}, {13, 2}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        int[][] isolated = search.isolated();
        int[][] fused = search.fused();
        assertThat(isolated.length, is(0));
        assertThat(fused.length, is(2));
        assertThat(fused[0], is(new int[]{0, 1, 2, 3, 4, 5, 6, 7}));
        assertThat(fused[1], is(new int[]{8, 9, 10, 11, 12, 13, 14, 15}));
    }

    /**
     * C1CCC2CCCCC2C1
     */
    @Test
    public void testFused_Orthofused() {
        // napthalene like
        int[][] g = new int[][]{{1, 5}, {0, 2}, {1, 3}, {2, 4}, {3, 5, 7}, {0, 6, 4}, {5, 9}, {4, 8}, {7, 9}, {6, 8}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        int[][] isolated = search.isolated();
        int[][] fused = search.fused();
        assertThat(isolated.length, is(0));
        assertThat(fused.length, is(1));
        assertThat(fused[0].length, is(g.length));
    }

    /**
     * C1CCC2CC3CCCCC3CC2C1
     */
    @Test
    public void testFused_Biorthofused() {
        // 3 fused rings
        int[][] g = new int[][]{{1, 5}, {0, 2, 10}, {3, 13, 1}, {2, 4}, {3, 5, 7}, {0, 6, 4}, {5, 9}, {4, 8}, {7, 9},
                {6, 8}, {1, 11}, {10, 12}, {11, 13}, {2, 12}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        int[][] isolated = search.isolated();
        int[][] fused = search.fused();
        assertThat(isolated.length, is(0));
        assertThat(fused.length, is(1));
        assertThat(fused[0].length, is(g.length));
    }

    /**
     * C1CC23CCC4(CC2)CCC2(CCCC5(CCCC6(CCC7(CCCC8(CCC9(CC8)CCC8(CCCC%10(CCCC%11(CCC(C1)(CC%11)C3)C%10)C8)CC9)C7)CC6)C5)C2)CC4
     */
    @Test
    public void testFused_Cylclophane() {
        // medium size spiro cyclophane
        int[][] g = new int[][]{{1, 5}, {0, 2}, {1, 3, 50, 46}, {2, 4}, {3, 5}, {0, 4, 7, 8}, {7, 10}, {5, 6}, {5, 9},
                {8, 10}, {6, 9, 12, 13}, {12, 15}, {10, 11}, {10, 14}, {13, 15, 16, 17}, {11, 14}, {14, 20}, {14, 18},
                {17, 19}, {18, 20, 21, 22}, {16, 19}, {19, 25}, {19, 23}, {22, 24, 26, 30}, {23, 25}, {21, 24},
                {23, 27}, {26, 28, 35, 31}, {27, 29}, {28, 30}, {23, 29}, {27, 32}, {31, 33}, {32, 34, 40, 36},
                {33, 35}, {27, 34}, {33, 37}, {36, 38}, {37, 39, 45, 41}, {38, 40}, {33, 39}, {38, 42},
                {41, 43, 58, 59}, {42, 44}, {43, 45}, {38, 44}, {2, 47}, {46, 48}, {47, 49}, {48, 50, 51, 55}, {2, 49},
                {49, 52}, {51, 53}, {52, 54}, {53, 55, 56, 57}, {49, 54}, {54, 59}, {54, 58}, {42, 57}, {42, 56}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        int[][] isolated = search.isolated();
        int[][] fused = search.fused();
        assertThat(isolated.length, is(0));
        assertThat(fused.length, is(1));
        assertThat(fused[0].length, is(g.length));
    }

    /**
     * CHEBI:33128
     */
    @Test
    public void testFused_Fullerene() {
        int[][] g = new int[][]{{1, 4, 8}, {0, 2, 11}, {1, 3, 14}, {2, 4, 17}, {3, 0, 5}, {4, 6, 19}, {5, 7, 21},
                {6, 8, 24}, {7, 0, 9}, {8, 10, 25}, {9, 11, 28}, {10, 1, 12}, {11, 13, 29}, {12, 14, 32}, {13, 2, 15},
                {14, 16, 33}, {15, 17, 36}, {16, 3, 18}, {17, 19, 37}, {18, 5, 20}, {19, 21, 39}, {20, 6, 22},
                {21, 23, 41}, {22, 24, 43}, {23, 7, 25}, {24, 9, 26}, {25, 27, 44}, {26, 28, 46}, {27, 10, 29},
                {28, 12, 30}, {29, 31, 47}, {30, 32, 49}, {31, 13, 33}, {32, 15, 34}, {33, 35, 50}, {34, 36, 52},
                {35, 16, 37}, {36, 18, 38}, {37, 39, 53}, {38, 20, 40}, {39, 41, 54}, {40, 22, 42}, {41, 43, 56},
                {42, 23, 44}, {43, 26, 45}, {44, 46, 57}, {45, 27, 47}, {46, 30, 48}, {47, 49, 58}, {48, 31, 50},
                {49, 34, 51}, {50, 52, 59}, {51, 35, 53}, {52, 38, 54}, {53, 40, 55}, {54, 56, 59}, {55, 42, 57},
                {56, 45, 58}, {57, 48, 59}, {58, 51, 55}};
        CyclicVertexSearch search = new JumboCyclicVertexSearch(g);
        int[][] isolated = search.isolated();
        int[][] fused = search.fused();
        assertThat(isolated.length, is(0));
        assertThat(fused.length, is(1));
        assertThat(fused[0].length, is(g.length));
    }

    @Test
    public void testToArray() throws Exception {
        BitSet s = new BitSet();
        s.set(0);
        s.set(6);
        assertThat(JumboCyclicVertexSearch.toArray(s), is(new int[]{0, 6}));
    }

    @Test
    public void testToArray_Empty() throws Exception {
        BitSet empty = new BitSet();
        assertThat(JumboCyclicVertexSearch.toArray(empty), is(new int[0]));
    }

    @Test
    public void testXor() throws Exception {
        BitSet s = new BitSet();
        BitSet t = new BitSet();

        s.set(0);
        s.set(1);
        t.set(1);
        t.set(2);

        BitSet u = JumboCyclicVertexSearch.xor(s, t);

        assertTrue(s.get(0));
        assertTrue(s.get(1));
        assertFalse(s.get(2));

        assertFalse(t.get(0));
        assertTrue(t.get(1));
        assertTrue(t.get(2));

        assertTrue(u.get(0));
        assertFalse(u.get(1));
        assertTrue(u.get(2));

    }

    @Test
    public void testAnd() throws Exception {

        BitSet s = new BitSet();
        BitSet t = new BitSet();

        s.set(0);
        s.set(1);
        t.set(1);
        t.set(2);

        BitSet u = JumboCyclicVertexSearch.and(s, t);

        assertTrue(s.get(0));
        assertTrue(s.get(1));
        assertFalse(s.get(2));

        assertFalse(t.get(0));
        assertTrue(t.get(1));
        assertTrue(t.get(2));

        assertFalse(u.get(0));
        assertTrue(u.get(1));
        assertFalse(u.get(2));

    }

    @Test
    public void testCopy() throws Exception {
        BitSet set = new BitSet();
        set.set(5);
        BitSet cpy = JumboCyclicVertexSearch.copy(set);
        assertTrue(cpy.get(5));
        set.set(6);
        assertFalse(cpy.get(6));
    }
}
