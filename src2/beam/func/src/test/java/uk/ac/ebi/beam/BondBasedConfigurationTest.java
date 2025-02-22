package uk.ac.ebi.beam;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/** @author John May */
public class BondBasedConfigurationTest {

    @Test(expected = IllegalArgumentException.class)
    public void nonDoubleBond() throws Exception {
        Graph g = Graph.fromSmiles("CCCC");
        BondBasedConfiguration.configurationOf(g, 0, 1, 2, 3);
    }

    @Test
    public void opposite1() throws Exception {
        Graph g = Graph.fromSmiles("F/C=C/F");
        assertThat(BondBasedConfiguration.configurationOf(g, 0, 1, 2, 3),
                   is(Configuration.DoubleBond.OPPOSITE));
    }

    @Test
    public void opposite2() throws Exception {
        Graph g = Graph.fromSmiles("F\\C=C\\F");
        assertThat(BondBasedConfiguration.configurationOf(g, 0, 1, 2, 3),
                   is(Configuration.DoubleBond.OPPOSITE));
    }

    @Test
      public void together1() throws Exception {
        Graph g = Graph.fromSmiles("F/C=C\\F");
        assertThat(BondBasedConfiguration.configurationOf(g, 0, 1, 2, 3),
                   is(Configuration.DoubleBond.TOGETHER));
    }

    @Test
    public void together2() throws Exception {
        Graph g = Graph.fromSmiles("F\\C=C/F");
        assertThat(BondBasedConfiguration.configurationOf(g, 0, 1, 2, 3),
                   is(Configuration.DoubleBond.TOGETHER));
    }
}
