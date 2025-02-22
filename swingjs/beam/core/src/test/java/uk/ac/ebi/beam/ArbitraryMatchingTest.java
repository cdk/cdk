package uk.ac.ebi.beam;

import org.hamcrest.collection.IsIterableWithSize;
import org.junit.Test;

import java.util.BitSet;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

/** @author John May */
public class ArbitraryMatchingTest {

    // simple example on furan (happens to be maximum)
    @Test public void furan() throws Exception {
        Graph g = Graph.fromSmiles("o1cccc1");
        Matching m = Matching.empty(g);
        ArbitraryMatching.initial(g, m,
                                  allOf(1, 2, 3, 4));
        // note this matching is maximum
        assertThat(m.matches(),
                   IsIterableWithSize.<Tuple>iterableWithSize(2));
        assertThat(m.matches(),
                   hasItems(Tuple.of(1, 2),
                            Tuple.of(3, 4)));
    }

    // furan different order - non maximum this time
    @Test public void furan_2() throws Exception {
        Graph g = Graph.fromSmiles("c1ccoc1");
        Matching m = Matching.empty(g);
        ArbitraryMatching.initial(g,
                                  m,
                                  allOf(0, 1, 2, 4));
        assertThat(m.matches(),
                   IsIterableWithSize.<Tuple>iterableWithSize(1));
        assertThat(m.matches(),
                   hasItem(Tuple.of(0, 1)));
    }

    @Test public void benzene() throws Exception {
        Graph g = Graph.fromSmiles("c1ccccc1");
        Matching m = Matching.empty(g);
        ArbitraryMatching.initial(g,
                                  m,
                                  allOf(0, 1, 2, 3, 4, 5));
        assertThat(m.matches(),
                   IsIterableWithSize.<Tuple>iterableWithSize(3));
        assertThat(m.matches(),
                   hasItems(Tuple.of(0, 1),
                            Tuple.of(2, 3),
                            Tuple.of(4, 5)));
    }

    static BitSet allOf(int... xs) {
        BitSet s = new BitSet();
        for (int x : xs)
            s.set(x);
        return s;
    }
}
