package org.openscience.cdk.graph;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openscience.cdk.graph.RegularPathGraphTest.completeGraphOfSize;

/**
 * @author John May
 * @cdk.module test-core
 */
public class AllCyclesTest {

    @Test
    public void rank() throws Exception {
        // given vertices based on degree
        int[][] g = new int[][]{{0, 0, 0, 0}, // 5th
                {0, 0, 0, 0}, // 6th
                {0, 0, 0}, // 4th
                {0}, // 2nd
                {0, 0}, // 3rd
                {} // 1st
        };
        assertThat(AllCycles.rank(g), is(new int[]{4, 5, 3, 1, 2, 0}));
    }

    @Test
    public void verticesInOrder() {
        int[] vertices = AllCycles.verticesInOrder(new int[]{4, 3, 1, 2, 0});
        assertThat(vertices, is(new int[]{4, 2, 3, 1, 0}));
    }

    @Test
    public void completed() {
        AllCycles ac = new AllCycles(completeGraphOfSize(4), 4, 100);
        assertTrue(ac.completed());
        assertThat(ac.size(), is(7));
    }

    @Test(timeout = 50)
    public void impractical() {
        // k12 - ouch
        AllCycles ac = new AllCycles(completeGraphOfSize(12), 12, 100);
        assertFalse(ac.completed());
    }

    @Test
    public void k4Paths() {
        AllCycles ac = new AllCycles(completeGraphOfSize(4), 4, 1000);
        assertThat(ac.paths(), is(new int[][]{{2, 1, 0, 2}, {3, 1, 0, 3}, {3, 2, 0, 3}, {3, 2, 1, 3}, {3, 2, 1, 0, 3},
                {3, 2, 0, 1, 3}, {3, 0, 2, 1, 3}}));

    }

    @Test
    public void k5Paths() {
        AllCycles ac = new AllCycles(completeGraphOfSize(5), 5, 1000);
        assertThat(ac.paths(), is(new int[][]{{2, 1, 0, 2}, {3, 1, 0, 3}, {4, 1, 0, 4}, {3, 2, 0, 3}, {3, 2, 1, 3},
                {3, 2, 1, 0, 3}, {3, 2, 0, 1, 3}, {4, 2, 0, 4}, {4, 2, 1, 4}, {4, 2, 1, 0, 4}, {4, 2, 0, 1, 4},
                {3, 0, 2, 1, 3}, {4, 0, 2, 1, 4}, {4, 3, 0, 4}, {4, 3, 1, 4}, {4, 3, 1, 0, 4}, {4, 3, 0, 1, 4},
                {4, 3, 2, 4}, {4, 3, 2, 0, 4}, {4, 3, 2, 1, 4}, {4, 3, 2, 1, 0, 4}, {4, 3, 2, 0, 1, 4},
                {4, 3, 0, 2, 4}, {4, 3, 1, 2, 4}, {4, 3, 0, 1, 2, 4}, {4, 3, 1, 0, 2, 4}, {4, 3, 0, 2, 1, 4},
                {4, 3, 1, 2, 0, 4}, {4, 0, 3, 1, 4}, {4, 0, 3, 2, 4}, {4, 0, 3, 2, 1, 4}, {4, 0, 3, 1, 2, 4},
                {4, 1, 3, 2, 4}, {4, 1, 3, 2, 0, 4}, {4, 1, 3, 0, 2, 4}, {4, 0, 1, 3, 2, 4}, {4, 1, 0, 3, 2, 4}}));
    }

    @Test
    public void k4Size() {
        AllCycles ac = new AllCycles(completeGraphOfSize(4), 4, 1000);
        assertThat(ac.size(), is(7));
    }

    @Test
    public void k5Size() {
        AllCycles ac = new AllCycles(completeGraphOfSize(5), 5, 1000);
        assertThat(ac.size(), is(37));
    }

    @Test
    public void k6Size() {
        AllCycles ac = new AllCycles(completeGraphOfSize(6), 6, 1000);
        assertThat(ac.size(), is(197));
    }

    @Test
    public void k7Size() {
        AllCycles ac = new AllCycles(completeGraphOfSize(7), 7, 1000);
        assertThat(ac.size(), is(1172));
    }
}
