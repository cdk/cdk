package org.openscience.cdk.graph;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 * @cdk.module test-core
 */
public class ConnectedComponentsTest {

    @Test
    public void connected() throws Exception {
        int[][] g = {{1}, {0, 2}, {1, 3}, {2}};
        assertThat(new ConnectedComponents(g).nComponents(), is(1));
        assertThat(new ConnectedComponents(g).components(), is(new int[]{1, 1, 1, 1}));
    }

    @Test
    public void disconnected() throws Exception {
        int[][] g = {{1}, {0, 2}, {1, 3}, {2}, {5, 6}, {4, 6}, {4, 5}, {}, {9}, {8}};
        assertThat(new ConnectedComponents(g).nComponents(), is(4));
        assertThat(new ConnectedComponents(g).components(), is(new int[]{1, 1, 1, 1, 2, 2, 2, 3, 4, 4}));
    }
}
