package uk.ac.ebi.beam;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/** @author John May */
public class BiconnectedComponentsTest {
    
    @Test public void benzene() throws Exception {
        Graph g = Graph.fromSmiles("c1ccccc1");
        BiconnectedComponents bc = new BiconnectedComponents(g);
        assertThat(bc.components().size(), is(1));
        assertThat(bc.components().get(0).size(), is(6));
    }

    @Test public void benzylbenzene() throws Exception {
        Graph g = Graph.fromSmiles("c1ccccc1Cc1ccccc1");
        BiconnectedComponents bc = new BiconnectedComponents(g, false);
        assertThat(bc.cyclic().cardinality(), is(12));
    }

    @Test public void spiro() throws Exception {
        Graph g = Graph.fromSmiles("C1CCCCC11CCCCC1");
        BiconnectedComponents bc = new BiconnectedComponents(g);
        assertThat(bc.components().size(), is(2));
        assertThat(bc.components().get(0).size(), is(6));
        assertThat(bc.components().get(0).size(), is(6));
    }
    
    @Test public void fused() throws Exception {
        Graph g = Graph.fromSmiles("C1=CC2=CC=CC=C2C=C1");
        BiconnectedComponents bc = new BiconnectedComponents(g);
        assertThat(bc.components().size(), is(1));
        assertThat(bc.components().get(0).size(), is(11));            
    }

    @Test public void bridged() throws Exception {
        Graph g = Graph.fromSmiles("C1CC2CCC1C2");
        BiconnectedComponents bc = new BiconnectedComponents(g);
        assertThat(bc.components().size(), is(1));
        assertThat(bc.components().get(0).size(), is(8));
    }
    
    @Test public void exocyclic() throws Exception {
        Graph g = Graph.fromSmiles("[AsH]=C1C=CC=CC=C1");
        BiconnectedComponents bc = new BiconnectedComponents(g);
        assertThat(bc.components().size(), is(1));
        assertThat(bc.components().get(0).size(), is(7));
    }
}
