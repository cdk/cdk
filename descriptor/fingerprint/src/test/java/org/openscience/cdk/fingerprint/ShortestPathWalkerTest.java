package org.openscience.cdk.fingerprint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.graph.AllPairsShortestPaths;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.TestMoleculeFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 */
class ShortestPathWalkerTest {

    @Test
    void testOverflowDoesNotBypassPathLimit() {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        for (int v = 0; v <= 96; v++)
            mol.addAtom(new Atom(v == 0 || v == 96 ? "N" : "C"));
        for (int i = 0; i < 32; i++) {
            int v = 3 * i;
            mol.addBond(v, v + 1, IBond.Order.SINGLE);
            mol.addBond(v, v + 2, IBond.Order.SINGLE);
            mol.addBond(v + 1, v + 3, IBond.Order.SINGLE);
            mol.addBond(v + 2, v + 3, IBond.Order.SINGLE);
        }
        int previous = 0;
        for (int step = 1; step < 64; step++) {
            int next = mol.getAtomCount();
            mol.addAtom(new Atom("C"));
            mol.addBond(previous, next, IBond.Order.SINGLE);
            previous = next;
        }
        mol.addBond(previous, 96, IBond.Order.SINGLE);

        // There are 2^32 + 1 shortest routes between the only two nitrogen atoms.
        // Fail before invoking the walker on an unfixed counter, which would try to enumerate them.
        Assertions.assertEquals(Integer.MAX_VALUE, new AllPairsShortestPaths(mol).from(0).nPathsTo(96));
        Set<String> paths = new ShortestPathWalker(mol).paths();
        Assertions.assertTrue(paths.contains("N1C"));
        for (String path : paths)
            Assertions.assertFalse(path.length() > 1 && path.startsWith("N") && path.endsWith("N"));
    }

    @Test
    void testPaths() throws Exception {
        IAtomContainer triazole = TestMoleculeFactory.make123Triazole();
        ShortestPathWalker walker = new ShortestPathWalker(triazole);
        Set<String> expected = new TreeSet<>(Arrays.asList("C", "N2N1N", "N", "N1N1C", "N1C2C", "C1N", "N2N1C",
                "C1N2N", "C1N1N", "N1C", "C2C1N", "C2C", "N2N", "N1N2N", "N1N"));
        Set<String> actual = walker.paths();
        assertThat(actual, is(expected));
    }

    @Test
    void testToString() throws Exception {
        IAtomContainer triazole = TestMoleculeFactory.make123Triazole();
        ShortestPathWalker walker = new ShortestPathWalker(triazole);
        assertThat(walker.toString(),
                is("C->C1N->C1N1N->C1N2N->C2C->C2C1N->N->N1C->N1C2C->N1N->N1N1C->N1N2N->N2N->N2N1C->N2N1N"));
    }
}
