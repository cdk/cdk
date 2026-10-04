/* Copyright (C) 2026  Syed Asad Rahman <s9asad@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.Intractable;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.global.TimeOut;

import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ELEMENT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.IS_IN_RING;
import static org.openscience.cdk.smsd.BruteForceMCS.completeGraph;
import static org.openscience.cdk.smsd.BruteForceMCS.randomGraph;

/**
 * Checks {@link MCS}; some tests compare its mappings with a brute force
 * search over all atom mappings.
 *
 * @author Syed Asad Rahman
 */
class MCSTest {

    private static final String C60      = "c12c3c4c5c1c6c7c8c2c9c%10c3c%11c%12c4c%13c%14c5c%15c6c%16c7c%17c%18c8c9"
            + "c%19c%20c%10c%11c%21c%22c%12c%13c%23c%24c%14c%15c%25c%16c%26c%17c%27c%18c%19c%28c%20c%21c%29c%22"
            + "c%23c%30c%24c%25c%26c%31c%27c%28c%29c%30%31";
    private static final String CORONENE = "c1cc2ccc3ccc4ccc5ccc6ccc1c7c2c3c4c5c67";

    @Test
    void testMatch() throws Exception {
        IAtomContainer query = smi("C1CCCCC1");
        IAtomContainer target = smi("CCCCCC");
        int[] mapping = MCS.find(query).match(target);
        Assertions.assertEquals(6, mapping.length);
        Assertions.assertEquals(6, countMapped(mapping));
        // a ring bond is left out, 6 bonds to break and 2 directions
        Assertions.assertEquals(12, MCS.find(query).matchAll(target).size());
        assertSameAsBruteForce(query, target, true);
    }

    @Test
    void testUnmappedQueryAtoms() throws Exception {
        // the query is the larger molecule
        IAtomContainer query = smi("OCCc1ccccc1");
        IAtomContainer target = smi("c1ccccc1");
        int[] mapping = MCS.find(query).match(target);
        Assertions.assertEquals(9, mapping.length);
        Assertions.assertArrayEquals(new int[]{-1, -1, -1}, Arrays.copyOf(mapping, 3));
        Assertions.assertEquals(6, countMapped(mapping));
        assertSameAsBruteForce(query, target, true);
    }

    @Test
    void testValidMappings() throws Exception {
        IAtomContainer query = smi("CC(=O)Oc1ccccc1C(=O)O");
        IAtomContainer target = smi("OC(=O)c1ccccc1O");
        for (int[] mapping : MCS.find(query).matchAll(target)) {
            Assertions.assertEquals(10, countMapped(mapping));
            Assertions.assertEquals(10, countCommonBonds(query, target, mapping));
            for (int i = 0; i < mapping.length; i++) {
                if (mapping[i] >= 0) {
                    Assertions.assertEquals(query.getAtom(i).getSymbol(), target.getAtom(mapping[i]).getSymbol());
                }
            }
        }
    }

    @Test
    void testNoCommonAtoms() throws Exception {
        for (String[] pair : new String[][]{{"", "C"}, {"C", ""}, {"N", "C"}}) {
            MCS mcs = MCS.find(smi(pair[0]));
            Assertions.assertEquals(0, mcs.match(smi(pair[1])).length);
            Assertions.assertTrue(mcs.matchAll(smi(pair[1])).isEmpty());
        }
    }

    @Test
    void testBondOrder() throws Exception {
        Assertions.assertEquals(1, countMapped(MCS.find(smi("CC")).match(smi("C=C"))));
        Assertions.assertEquals(2, countMapped(MCS.find(smi("CC")).withMatching(ELEMENT).match(smi("C=C"))));
    }

    @Test
    void testRingMatching() throws Exception {
        IAtomContainer query = smi("CC1CCCCC1");
        IAtomContainer target = smi("CCCCCCC");
        Cycles.markRingAtomsAndBonds(query);
        Cycles.markRingAtomsAndBonds(target);
        Assertions.assertEquals(7, countMapped(MCS.find(query).match(target)));
        // ring atoms of the query may only be mapped to ring atoms
        Assertions.assertEquals(1, countMapped(MCS.find(query).withMatching(ELEMENT, IS_IN_RING).match(target)));
    }

    @Test
    void testQueryMolecule() throws Exception {
        IAtomContainer query = smi("CC=CC");
        IAtomContainer target = smi("CCCC");
        Assertions.assertEquals(2, countMapped(MCS.find(query).match(target)));
        Assertions.assertEquals(4, countMapped(MCS.find(QueryAtomContainer.create(query, ELEMENT)).match(target)));
    }

    @Test
    void testRandomGraphs() throws Exception {
        // connected and disconnected graphs, some with aromatic bonds
        Random random = new Random(20261004L);
        for (int i = 0; i < 300; i++) {
            boolean bonds = i % 2 == 0;
            IAtomContainer a = vary(randomGraph(random, 2 + random.nextInt(5), bonds), random);
            IAtomContainer b = vary(randomGraph(random, 2 + random.nextInt(5), bonds), random);
            assertSameAsBruteForce(a, b, bonds);
        }
    }

    @Test
    void testMatchIsAMaximumMapping() throws Exception {
        Random random = new Random(1004L);
        for (int i = 0; i < 100; i++) {
            IAtomContainer a = randomGraph(random, 2 + random.nextInt(6), true);
            IAtomContainer b = randomGraph(random, 2 + random.nextInt(6), true);
            int[] mapping = MCS.find(a).match(b);
            List<int[]> all = MCS.find(a).matchAll(b);
            if (mapping.length == 0) {
                Assertions.assertTrue(all.isEmpty());
            } else {
                Assertions.assertTrue(toSet(all).contains(Arrays.toString(mapping)),
                        () -> Arrays.toString(mapping) + " not in " + toSet(all));
            }
        }
    }

    @Test
    void testTooManyMappings() throws Exception {
        // 2520 equally good mappings in each direction, 1000 are returned
        for (IAtomContainer[] pair : new IAtomContainer[][]{{completeGraph(5), completeGraph(7)},
                {completeGraph(7), completeGraph(5)}}) {
            List<int[]> mappings = MCS.find(pair[0]).matchAll(pair[1]);
            Assertions.assertEquals(1000, mappings.size());
            Assertions.assertEquals(1000, toSet(mappings).size());
            for (int[] mapping : mappings) {
                Assertions.assertEquals(5, countMapped(mapping));
                Assertions.assertEquals(10, countCommonBonds(pair[0], pair[1], mapping));
            }
        }
    }

    @Test
    void testTimeout() throws Exception {
        TimeOut timeOut = TimeOut.getInstance();
        double limit = timeOut.getTimeOut();
        try {
            timeOut.setTimeOut(-1);
            timeOut.setTimeOutFlag(false);
            // the exact search of coronene against C60 takes far longer than this
            MCS mcs = MCS.find(smi(CORONENE)).withTimeout(200, TimeUnit.MILLISECONDS);
            Assertions.assertThrows(Intractable.class, () -> mcs.match(smi(C60)));
            // the global TimeOut is left alone
            Assertions.assertEquals(-1, timeOut.getTimeOut());
            Assertions.assertFalse(timeOut.isTimeOutFlag());
        } finally {
            timeOut.setTimeOut(limit);
            timeOut.setTimeOutFlag(false);
        }
    }

    @Test
    void testGlobalTimeOutIgnored() throws Exception {
        TimeOut timeOut = TimeOut.getInstance();
        double limit = timeOut.getTimeOut();
        try {
            // well under the time the search needs; withMatching stops the swap,
            // so K10 stays the query and cannot be mapped whole
            timeOut.setTimeOut(0.00001);
            timeOut.setTimeOutFlag(false);
            Assertions.assertEquals(1000, MCS.find(completeGraph(10)).withMatching(ELEMENT)
                    .matchAll(completeGraph(9)).size());
            Assertions.assertFalse(timeOut.isTimeOutFlag());
        } finally {
            timeOut.setTimeOut(limit);
            timeOut.setTimeOutFlag(false);
        }
    }

    @Test
    void testInterrupted() throws Exception {
        MCS mcs = MCS.find(smi("C1CCCCC1"));
        IAtomContainer target = smi("CCCCCC");
        Thread.currentThread().interrupt();
        try {
            Assertions.assertThrows(Intractable.class, () -> mcs.match(target));
            // the interrupt is kept for the caller
            Assertions.assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
        Assertions.assertEquals(6, countMapped(mcs.match(target)));
    }

    @Test
    void testInterruptedWhileSearching() throws Exception {
        MCS mcs = MCS.find(smi(CORONENE));
        IAtomContainer target = smi(C60);
        Throwable[] thrown = new Throwable[1];
        Thread thread = new Thread(() -> {
            try {
                mcs.match(target);
            } catch (Throwable e) {
                thrown[0] = e;
            }
        });
        thread.start();
        Thread.sleep(100);
        thread.interrupt();
        thread.join(TimeUnit.SECONDS.toMillis(10));
        Assertions.assertFalse(thread.isAlive());
        Assertions.assertTrue(thrown[0] instanceof Intractable);
    }

    @Test
    void testOptionsMakeNewSearches() throws Exception {
        MCS mcs = MCS.find(smi("CC"));
        MCS byElement = mcs.withMatching(ELEMENT);
        MCS limited = mcs.withTimeout(10, TimeUnit.SECONDS);
        Assertions.assertNotSame(mcs, byElement);
        Assertions.assertNotSame(mcs, limited);
        Assertions.assertEquals(1, countMapped(mcs.match(smi("C=C"))));
        Assertions.assertEquals(1, countMapped(limited.match(smi("C=C"))));
        Assertions.assertEquals(2, countMapped(byElement.match(smi("C=C"))));
    }

    @Test
    void testSharedBetweenThreads() throws Exception {
        IAtomContainer query = smi("CC(=O)Oc1ccccc1C(=O)O");
        Cycles.markRingAtomsAndBonds(query);
        MCS[] searches = {MCS.find(query), MCS.find(query).withMatching(ELEMENT, IS_IN_RING)};
        String[] targets = {"OC(=O)c1ccccc1O", "CC(=O)Nc1ccc(O)cc1", "c1ccc2ccccc2c1", "CCOC(=O)c1ccccc1"};
        List<Set<String>> expected = new ArrayList<>();
        for (int i = 0; i < 2 * targets.length; i++) {
            expected.add(toSet(searches[i % 2].matchAll(ring(targets[i / 2]))));
        }
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Future<Set<String>>> results = new ArrayList<>();
            for (int i = 0; i < 80; i++) {
                int k = i % expected.size();
                results.add(executor.submit(() -> toSet(searches[k % 2].matchAll(ring(targets[k / 2])))));
            }
            for (int i = 0; i < results.size(); i++) {
                Assertions.assertEquals(expected.get(i % expected.size()), results.get(i).get());
            }
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void testArguments() throws Exception {
        Assertions.assertThrows(NullPointerException.class, () -> MCS.find(null));
        Assertions.assertThrows(NullPointerException.class, () -> MCS.find(smi("C")).match(null));
        Assertions.assertThrows(NullPointerException.class, () -> MCS.find(smi("C")).withMatching((Expr.Type) null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MCS.find(smi("C")).withTimeout(0, TimeUnit.SECONDS));
        // a query molecule is matched by its own expressions
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MCS.find(QueryAtomContainer.create(smi("CC"), ELEMENT)).withMatching(ELEMENT));
    }

    private static IAtomContainer smi(String smiles) throws Exception {
        return new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smiles);
    }

    private static IAtomContainer ring(String smiles) throws Exception {
        IAtomContainer mol = smi(smiles);
        Cycles.markRingAtomsAndBonds(mol);
        return mol;
    }

    // leave out about a fifth of the bonds and make about a fifth aromatic
    private static IAtomContainer vary(IAtomContainer graph, Random random) {
        List<IBond> bonds = new ArrayList<>();
        for (IBond bond : graph.bonds()) {
            bonds.add(bond);
        }
        for (IBond bond : bonds) {
            double r = random.nextDouble();
            if (r < 0.2) {
                graph.removeBond(bond);
            } else if (r < 0.4) {
                bond.setIsAromatic(true);
            }
        }
        return graph;
    }

    private static int countMapped(int[] mapping) {
        int count = 0;
        for (int j : mapping) {
            if (j >= 0) {
                count++;
            }
        }
        return count;
    }

    private static int countCommonBonds(IAtomContainer query, IAtomContainer target, int[] mapping) {
        int count = 0;
        for (IBond bond : query.bonds()) {
            int a = mapping[query.indexOf(bond.getBegin())];
            int b = mapping[query.indexOf(bond.getEnd())];
            if (a >= 0 && b >= 0 && target.getBond(target.getAtom(a), target.getAtom(b)) != null) {
                count++;
            }
        }
        return count;
    }

    private static Set<String> toSet(List<int[]> mappings) {
        Set<String> set = new HashSet<>();
        for (int[] mapping : mappings) {
            set.add(Arrays.toString(mapping));
        }
        return set;
    }

    // with bonds: the default matching; without: withMatching(ELEMENT), and a query molecule made with ELEMENT
    private static void assertSameAsBruteForce(IAtomContainer a, IAtomContainer b, boolean bonds) throws Exception {
        assertSameAsBruteForce(a, b, bonds, bonds ? MCS.find(a) : MCS.find(a).withMatching(ELEMENT));
        assertSameAsBruteForce(b, a, bonds, bonds ? MCS.find(b) : MCS.find(b).withMatching(ELEMENT));
        if (!bonds) {
            assertSameAsBruteForce(a, b, false, MCS.find(QueryAtomContainer.create(a, ELEMENT)));
        }
    }

    private static void assertSameAsBruteForce(IAtomContainer query, IAtomContainer target, boolean bonds, MCS mcs)
            throws Exception {
        Set<String> expected = BruteForceMCS.mappings(query, target, bonds);
        // MCS returns no mapping if no atom matches
        int[] none = new int[query.getAtomCount()];
        Arrays.fill(none, -1);
        expected.remove(Arrays.toString(none));
        Assertions.assertEquals(expected, toSet(mcs.matchAll(target)));
    }
}
