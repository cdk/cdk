package org.openscience.cdk.fingerprint;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.TestMoleculeFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 * @cdk.module test-fingerprint
 */
public class ShortestPathWalkerTest {

    @Test
    public void testPaths() throws Exception {
        IAtomContainer triazole = TestMoleculeFactory.make123Triazole();
        ShortestPathWalker walker = new ShortestPathWalker(triazole);
        Set<String> expected = new TreeSet<String>(Arrays.asList("C", "N2N1N", "N", "N1N1C", "N1C2C", "C1N", "N2N1C",
                "C1N2N", "C1N1N", "N1C", "C2C1N", "C2C", "N2N", "N1N2N", "N1N"));
        Set<String> actual = walker.paths();
        assertThat(actual, is(expected));
    }

    @Test
    public void testToString() throws Exception {
        IAtomContainer triazole = TestMoleculeFactory.make123Triazole();
        ShortestPathWalker walker = new ShortestPathWalker(triazole);
        assertThat(walker.toString(),
                is("C->C1N->C1N1N->C1N2N->C2C->C2C1N->N->N1C->N1C2C->N1N->N1N1C->N1N2N->N2N->N2N1C->N2N1N"));
    }
}
