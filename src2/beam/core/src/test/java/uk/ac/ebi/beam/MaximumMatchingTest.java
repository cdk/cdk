package uk.ac.ebi.beam;

import org.hamcrest.collection.IsIterableWithSize;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

/** @author John May */
public class MaximumMatchingTest {

    /** Contrived example to test blossoming. */
    @Test public void blossom() throws Exception {

        Graph g = Graph.fromSmiles("CCCCCC1CCCC1CC");
        Matching m = Matching.empty(g);

        // initial matching from double-bonds (size = 5) 
        m.match(1, 2);
        m.match(3, 4);
        m.match(5, 6);
        m.match(7, 8);
        m.match(9, 10);

        MaximumMatching.maximise(g, m, 10);

        // once maximised the matching has been augmented such that there
        // are now six disjoint edges (only possibly by contracting blossom)
        assertThat(m.matches(),
                   IsIterableWithSize.<Tuple>iterableWithSize(6));
        assertThat(m.matches(),
                   hasItems(Tuple.of(0, 1),
                            Tuple.of(2, 3),
                            Tuple.of(4, 5),
                            Tuple.of(6, 7),
                            Tuple.of(8, 9),
                            Tuple.of(10, 11)));
    }

    @Test public void simple_maximal() throws Exception {
        Graph g = Graph.fromSmiles("cccc");
        Matching m = MaximumMatching.maximal(g);
        assertThat(m.matches(),
                   hasItems(Tuple.of(0, 1),
                            Tuple.of(2, 3)));
    }

    @Test public void simple_augment() throws Exception {
        Graph g = Graph.fromSmiles("cccc");
        Matching m = Matching.empty(g);
        m.match(1, 2);
        MaximumMatching.maximise(g, m, 2);
        assertThat(m.matches(),
                   hasItems(Tuple.of(0, 1),
                            Tuple.of(2, 3)));
    }

    @Test public void simple_augment_subset() throws Exception {
        Graph g = Graph.fromSmiles("cccc");
        Matching m = Matching.empty(g);
        m.match(1, 2);
        // no vertex '3' matching can not be improved
        MaximumMatching.maximise(g, m, 2, IntSet.allOf(0, 1, 2));
        assertThat(m.matches(),
                   hasItems(Tuple.of(1, 2)));
    }

    @Test public void furan() throws Exception {
        Graph g = Graph.fromSmiles("o1cccc1");
        IntSet s = IntSet.allOf(1, 2, 3, 4); // exclude the oxygen
        Matching m = Matching.empty(g);
        MaximumMatching.maximise(g, m, 0, s);
        assertThat(m.matches(), hasItems(Tuple.of(1, 2),
                                         Tuple.of(3, 4)));
    }

    @Test public void furan_augment() throws Exception {
        Graph g = Graph.fromSmiles("o1cccc1");
        IntSet s = IntSet.allOf(1, 2, 3, 4); // exclude the oxygen
        Matching m = Matching.empty(g);
        m.match(2, 3);
        MaximumMatching.maximise(g, m, 2, s);
        assertThat(m.matches(), hasItems(Tuple.of(1, 2),
                                         Tuple.of(3, 4)));
    }

    @Test public void quinone() throws Exception {
        Graph g = Graph.fromSmiles("oc1ccc(o)cc1");
        Matching m = MaximumMatching.maximal(g);
        assertThat(m.matches(), hasItems(Tuple.of(0, 1),
                                         Tuple.of(2, 3),
                                         Tuple.of(4, 5),
                                         Tuple.of(6, 7)));
    }

    @Test public void quinone_subset() throws Exception {
        Graph g = Graph.fromSmiles("oc1ccc(o)cc1");
        // mocks the case where the oxygen atoms are already double bonded - we
        // therefore don't include those of the adjacent carbons in the vertex
        // subset to be matched
        Matching m = Matching.empty(g);
        MaximumMatching.maximise(g, m, 0, IntSet.allOf(2, 3, 6, 7));
        assertThat(m.matches(), hasItems(Tuple.of(2, 3),
                                         Tuple.of(6, 7)));
    }

    @Test public void napthalene_augment() throws Exception {
        Graph g = Graph.fromSmiles("C1C=CC2=CCC=CC2=C1");
        Matching m = Matching.empty(g);
        m.match(1, 2);
        m.match(3, 4);
        m.match(6, 7);
        m.match(8, 9);
        MaximumMatching.maximise(g, m, 8);
        assertThat(m.matches(), hasItems(Tuple.of(0, 1),
                                         Tuple.of(2, 3),
                                         Tuple.of(4, 5),
                                         Tuple.of(6, 7),
                                         Tuple.of(8, 9)));
    }

    @Test public void azulene() throws Exception {
        Graph g = Graph.fromSmiles("C1CC2CCCCCC2C1");
        Matching m = MaximumMatching.maximal(g);
        assertThat(m.matches(), hasItems(Tuple.of(0, 1),
                                         Tuple.of(2, 3),
                                         Tuple.of(4, 5),
                                         Tuple.of(6, 7),
                                         Tuple.of(8, 9)));
    }

    @Test public void imidazole() throws Exception {
        Graph g = Graph.fromSmiles("[nH]1ccnc1");
        Matching m = Matching.empty(g);
        MaximumMatching.maximise(g,
                                 m,
                                 0,
                                 IntSet.allOf(1, 2, 3, 4)); // not the 'nH'
        assertThat(m.matches(), hasItems(Tuple.of(1, 2),
                                         Tuple.of(3, 4)));
    }

    @Test public void benzimidazole() throws Exception {
        Graph g = Graph.fromSmiles("c1nc2ccccc2[nH]1");
        Matching m = Matching.empty(g);
        MaximumMatching.maximise(g,
                                 m,
                                 0,
                                 IntSet.noneOf(8)); // not the 'nH'
        assertThat(m.matches(), hasItems(Tuple.of(0, 1),
                                         Tuple.of(2, 3),
                                         Tuple.of(4, 5),
                                         Tuple.of(6, 7)));
    }
}
