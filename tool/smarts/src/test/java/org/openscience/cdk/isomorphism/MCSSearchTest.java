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
package org.openscience.cdk.isomorphism;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.PseudoAtom;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.openscience.cdk.isomorphism.MCSTesting.assertValid;
import static org.openscience.cdk.isomorphism.MCSTesting.atoms;
import static org.openscience.cdk.isomorphism.MCSTesting.bruteForceEdges;
import static org.openscience.cdk.isomorphism.MCSTesting.carbons;
import static org.openscience.cdk.isomorphism.MCSTesting.commonBonds;
import static org.openscience.cdk.isomorphism.MCSTesting.completeGraph;
import static org.openscience.cdk.isomorphism.MCSTesting.exact;
import static org.openscience.cdk.isomorphism.MCSTesting.graphsOnFourAtoms;
import static org.openscience.cdk.isomorphism.MCSTesting.keys;
import static org.openscience.cdk.isomorphism.MCSTesting.randomGraph;
import static org.openscience.cdk.isomorphism.MCSTesting.shuffle;
import static org.openscience.cdk.isomorphism.MCSTesting.smarts;
import static org.openscience.cdk.isomorphism.MCSTesting.smi;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ELEMENT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ISOTOPE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.SINGLE_OR_AROMATIC;

/**
 * Checks the search behind {@link MCS} through its package-private parts:
 * the mappings of wide targets against a brute force search, how the work
 * grows, atoms without a symbol, the stop signal, the search order, the
 * whole-query search and the bounds, and the disconnected search on every
 * pair of the graphs on four atoms. Without bonds a search matches as
 * {@code withMatching(ELEMENT)}.
 *
 * @author Syed Asad Rahman
 */
final class MCSSearchTest {

    // no upper bound on a score
    private static final long NONE = (long) Integer.MAX_VALUE << 32 | Integer.MAX_VALUE;

    // mappings

    @Test
    void testWideTargets() throws Exception {
        // the targets need more than one word of bits per query atom, or more than two
        assertRings(new int[][]{{4, 64}, {3, 129}});
    }

    @Test
    @Tag("SlowTest")
    void testWideTargetsSlow() throws Exception {
        assertRings(new int[][]{{5, 65}, {6, 70}});
        // the mappings of random pairs with 61, 64 or 130 atoms that match nothing in front of the target
        Random random = new Random(6465L);
        for (int i = 0; i < 60; i++) {
            boolean bonds = i % 2 == 0;
            IAtomContainer query = randomGraph(random, 2 + random.nextInt(5), 0.25, "C", "N", "O");
            IAtomContainer target = randomGraph(random, 2 + random.nextInt(5), 0.25, "C", "N", "O");
            Set<String> exact = exact(query, target, bonds);
            for (int pad : new int[]{61, 64, 130}) {
                List<int[]> mappings = run(query, padded(target, pad), bonds);
                Assertions.assertEquals(mappings.size(), keys(mappings).size(), "each mapping once");
                Assertions.assertEquals(shifted(exact, pad), keys(mappings));
            }
        }
    }

    @Test
    @Tag("SlowTest")
    @Timeout(value = 20, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void testPerfluoroalkanes() throws Exception {
        // C16F34 on C20F42: only one CF3 end of the query can lie on a CF3 end of the target, so one fluorine
        // is left out; the order of the atoms does not matter
        IAtomContainer query = smi(perfluoroalkane(16));
        IAtomContainer target = smi(perfluoroalkane(20));
        Random random = new Random(20261004L);
        for (int i = 0; i < 4; i++) {
            shuffle(query, random);
            shuffle(target, random);
            List<int[]> mappings = run(query, target, true);
            Assertions.assertEquals(1000, keys(mappings).size());
            int bonds = commonBonds(query, target, mappings.get(0), true);
            for (int[] mapping : mappings) {
                Assertions.assertEquals(49, atoms(mapping));
                Assertions.assertEquals(bonds, commonBonds(query, target, mapping, true));
                assertValid(query, target, mapping, true);
            }
        }
    }

    @Test
    @Tag("SlowTest")
    @Timeout(value = 30, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void testStars() throws Exception {
        // a carbon with 300 carbons and a nitrogen around it, and one with 301 carbons: a state has up to
        // 90,000 candidate pairs, but keeps only their query atoms, so a path of 300 states stays small
        IAtomContainer query = star(300, "N"), target = star(301, null);
        int[] mapping = search(query, target, true, never()).run(1).get(0);
        Assertions.assertEquals(301, atoms(mapping));
        Assertions.assertEquals(300, commonBonds(query, target, mapping, true));
        List<int[]> mappings = run(query, target, true);
        Assertions.assertEquals(1000, keys(mappings).size());
        for (int[] each : mappings)
            Assertions.assertEquals(300, commonBonds(query, target, each, true));
    }

    @Test
    @Tag("SlowTest")
    void testWorkGrowth() throws Exception {
        // twice the atoms: about four times the work for a chain on itself, as each first placement but the two
        // that fit fails only at the far end, and about twice the work for a star on itself and for a SMARTS chain
        // on propane, though ranking a chain takes a round for every two atoms
        assertGrowth(work(smi(carbons(500)), smi(carbons(500))),
                     work(smi(carbons(1000)), smi(carbons(1000))), 6);
        assertGrowth(work(star(500, null), star(500, null)), work(star(1000, null), star(1000, null)), 3);
        assertGrowth(work(smarts(carbons(2000)), smi("CCC")), work(smarts(carbons(4000)), smi("CCC")), 3);
    }

    @Test
    void testSameInputsSameList() throws Exception {
        IAtomContainer query = smi("C1CCCCC1"), target = smi("CCCCCC");
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(query);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(target);
        for (boolean bonds : new boolean[]{true, false}) {
            List<int[]> first = run(query, target, bonds), second = run(query, target, bonds);
            Assertions.assertEquals(1000, first.size());
            for (int i = 0; i < first.size(); i++)
                Assertions.assertArrayEquals(first.get(i), second.get(i));
        }
    }

    // matching

    @Test
    void testDefaultAtoms() throws Exception {
        // a pseudo atom matches an element by its symbol
        IAtom carbonLabel = new PseudoAtom("R1");
        carbonLabel.setSymbol("C");
        Assertions.assertEquals(1, atoms(run(mol(carbonLabel), smi("C"), true).get(0)));
        // an atom without a symbol matches nothing, not even another one
        Assertions.assertTrue(run(mol(noSymbol()), smi("C"), true).isEmpty());
        Assertions.assertTrue(run(mol(noSymbol()), mol(noSymbol()), true).isEmpty());
        Assertions.assertTrue(run(smi("C"), mol(noSymbol()), true).isEmpty());
        // so C?C on itself is one carbon on either, and C?CC on CC the bond of the last two
        IAtomContainer cxc = chain(true, false, true), cxcc = chain(true, false, true, true);
        List<int[]> mappings = run(cxc, chain(true, false, true), true);
        Assertions.assertEquals(4, mappings.size());
        Assertions.assertEquals(1, atoms(mappings.get(0)));
        mappings = run(cxcc, smi("CC"), true);
        Assertions.assertEquals(2, mappings.size());
        for (int[] mapping : mappings) {
            Assertions.assertEquals(2, atoms(mapping));
            Assertions.assertEquals(1, commonBonds(cxcc, smi("CC"), mapping, true));
        }
        // the label bound leaves such atoms out, and still covers the maximum
        MCSGraph graph = graph(cxc, chain(true, false, true), true);
        Assertions.assertEquals(2L << 32, graph.labelBound(false));
        Assertions.assertEquals(1L << 32, graph.labelBound(true));
        graph = graph(cxcc, chain(true, false, true, true), true);
        Assertions.assertEquals(3L << 32 | 1, graph.labelBound(false));
        Assertions.assertEquals(2L << 32 | 1, graph.labelBound(true));
        Assertions.assertEquals(0, graph(mol(noSymbol()), mol(noSymbol()), true).labelBound(true));
    }

    @Test
    void testCompleteRingsIncludeEveryCompatibleBond() throws Exception {
        IAtomContainer query = new AtomContainer(), target = new AtomContainer();
        for (String symbol : new String[]{"C", "N", "O", "S", "P", "F", "Cl"}) {
            query.addAtom(new Atom(symbol));
            target.addAtom(new Atom(symbol));
        }
        query.addAtom(new Atom("Br"));
        for (int[] edge : new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}, {0, 4},
                                     {1, 4}, {1, 5}, {2, 5}, {2, 6}, {3, 6}}) {
            query.addBond(edge[0], edge[1], IBond.Order.SINGLE);
            target.addBond(edge[0], edge[1], IBond.Order.SINGLE);
        }
        for (int a = 0; a < 4; a++)
            query.addBond(a, 7, IBond.Order.SINGLE);
        // the three outer triangles have 7 atoms and 9 bonds, but mapping them also includes bond 0-3;
        // its relevant query rings need the unmatched bromine, so that mapping does not have complete rings
        int[] mapping = MCS.find(query).withCompleteRings().match(target);
        Assertions.assertEquals(5, atoms(mapping));
        Assertions.assertEquals(6, commonBonds(query, target, mapping, true));
        mapping = MCS.find(query).match(target);
        Assertions.assertEquals(7, atoms(mapping));
        Assertions.assertEquals(10, commonBonds(query, target, mapping, true));
    }

    // stopping

    @Test
    void testSetUpStops() throws Exception {
        // ranking the atoms of a long chain asks whether to stop as it goes, and stops at the first yes
        AtomicInteger asked = new AtomicInteger();
        MCSGraph.Clock clock = new MCSGraph.Clock(() -> asked.incrementAndGet() > 10);
        MCSSearch search = search(smi("CN"), smi(carbons(2000)), true, clock);
        Assertions.assertThrows(MCSGraph.Stop.class, () -> search.run(1));
        Assertions.assertEquals(11, asked.get());
        // left to finish, the same search asks more often than that
        asked.set(0);
        MCSGraph.Clock never = new MCSGraph.Clock(() -> asked.incrementAndGet() < 0);
        Assertions.assertEquals(1, search(smi("CN"), smi(carbons(2000)), true, never).run(1).size());
        Assertions.assertTrue(asked.get() > 11, () -> asked.get() + " times");
    }

    @Test
    void testStopInWholeQuerySearch() throws Exception {
        // an odd ring never fits in a grid, but each placement must be tried to know, so the search asks
        AtomicBoolean stop = new AtomicBoolean();
        MCSSearch search = search(ring(21), grid(8), true, new MCSGraph.Clock(stop::get));
        MCSSearch.Embedding embedding = search.embedding();
        Assertions.assertNotNull(embedding);
        stop.set(true);
        Assertions.assertThrows(MCSGraph.Stop.class, () -> embedding.all(1000));
    }

    @Test
    @Tag("SlowTest")
    @Timeout(value = 10, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void testNoEmbedding() throws Exception {
        // a ring that does not fit leaves out a bond at least, so a path round it is a maximum
        IAtomContainer ring = ring(13), grid = grid(8);
        int[] mapping = search(ring, grid, true, never()).run(1).get(0);
        Assertions.assertEquals(13, atoms(mapping));
        Assertions.assertEquals(12, commonBonds(ring, grid, mapping, true));
    }

    @Test
    void testStopInSearch() throws Exception {
        // listing 1000 maximum mappings of K10 on K9 takes many ticks, so the search asks
        AtomicBoolean stop = new AtomicBoolean();
        MCSSearch search = search(completeGraph(10), completeGraph(9), true, new MCSGraph.Clock(stop::get));
        Assertions.assertNull(search.embedding());
        search.rank();
        int[] seed = search.seed(NONE);
        long score = search.score(seed);
        stop.set(true);
        Assertions.assertThrows(MCSGraph.Stop.class, () -> search.collect(seed, score, score, 1000));
    }

    @Test
    void testStopWhileBoundingHubs() throws Exception {
        // bounding the two hubs compares 128 x 128 bonds
        IAtomContainer query = star(128, null), target = star(128, null);
        for (IBond bond : target.bonds())
            bond.setOrder(IBond.Order.DOUBLE);
        MCSSearch search = search(query, target, true, stopIn("link"));
        search.rank();
        Assertions.assertThrows(MCSGraph.Stop.class, () -> search.collect(null, 0, NONE, 1));
    }

    @Test
    @Tag("SlowTest")
    void testStopWhileListingHubTargets() throws Exception {
        // the unmatched nitrogen prevents a whole-query embedding; collecting ties lists the free terminal targets
        MCSSearch search = search(star(128, "N"), star(129, null), true, stopIn("targets"));
        Assertions.assertThrows(MCSGraph.Stop.class, () -> search.run(1000));
    }

    @Test
    void testStopWhileCountingCommonBonds() throws Exception {
        MCSSearch search = search(star(128, null), star(129, null), true, stopIn("common"));
        search.rank();
        Assertions.assertThrows(MCSGraph.Stop.class, () -> search.seed(NONE));
    }

    // the search order

    @Test
    void testCanonicalRanks() throws Exception {
        // refinement alone tells the atoms of 3-methylhexane apart, so each keeps its rank when the atoms are
        // renumbered
        int[][] graph = {{1}, {0, 2}, {1, 3, 6}, {2, 4}, {3, 5}, {4}, {2}};
        int[] renumber = {4, 6, 0, 3, 5, 1, 2};
        int[][] renumbered = new int[7][];
        for (int i = 0; i < 7; i++) {
            renumbered[renumber[i]] = new int[graph[i].length];
            for (int k = 0; k < graph[i].length; k++)
                renumbered[renumber[i]][k] = renumber[graph[i][k]];
        }
        int[] ranks = ranks(graph), others = ranks(renumbered);
        for (int i = 0; i < 7; i++)
            Assertions.assertEquals(ranks[i], others[renumber[i]]);
        // the six atoms of a ring look alike, but still get different ranks
        int[] ring = ranks(new int[][]{{1, 5}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 0}});
        Arrays.sort(ring);
        Assertions.assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5}, ring);
    }

    // the whole query

    @Test
    void testEmbeddings() throws Exception {
        assertEmbeddings(smi("c1ccccc1"), smi("c1ccc2ccccc2c1"), 24);
        // the three methyls of isobutane on the four of neopentane: 4 x 3 x 2
        assertEmbeddings(smi("CC(C)C"), smi("CC(C)(C)C"), 24);
        assertEmbeddings(smi("CCCCCC"), smi("CCCCCC"), 2);
        assertEmbeddings(smi("Cc1ccccc1"), smi("Cc1ccccc1"), 2);
        // the same toluene with its atoms in another order
        assertEmbeddings(smi("Cc1ccccc1"), smi("c1cc(C)ccc1"), 2);
    }

    @Test
    @Timeout(value = 30, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void testDisconnectedQueries() throws Exception {
        // a hydrate with two sodium ions against a salt with one: a disconnected query is not mapped whole,
        // the phosphate is the largest part in common
        IAtomContainer hydrate = smi("O.O.O.O.O.O.O.OP(=O)([O-])[O-].[Na+].[Na+]");
        IAtomContainer nadp = smi("NC(=O)c1ccc[n+](c1)C1OC(COP(=O)([O-])OP(=O)([O-])OCC2OC(n3cnc4c(N)ncnc43)"
                                  + "C(OP(=O)(O)O)C2O)C(O)C1O.[Na+]");
        Assertions.assertNull(search(hydrate, nadp, true, never()).embedding());
        List<int[]> mappings = run(hydrate, nadp, true);
        Assertions.assertFalse(mappings.isEmpty());
        for (int[] mapping : mappings) {
            Assertions.assertEquals(5, atoms(mapping));
            Assertions.assertEquals(4, commonBonds(hydrate, nadp, mapping, true));
        }
        // the heptane of the mixture does not fit in the sugar, a chain of six carbons does
        IAtomContainer mixture = smi("OC(O)(C)C.O.O.O.O.O.O.CCCCCCC");
        IAtomContainer raffinose = smi("OCC1OC(OCC2OC(OC3(CO)OC(CO)C(O)C3O)C(O)C(O)C2O)C(O)C(O)C1O");
        Assertions.assertNull(search(mixture, raffinose, false, never()).embedding());
        mappings = run(mixture, raffinose, false);
        Assertions.assertFalse(mappings.isEmpty());
        for (int[] mapping : mappings)
            Assertions.assertEquals(6, atoms(mapping));
    }

    // the bounds

    @Test
    void testBondBound() throws Exception {
        // against the bonds of every induced subgraph, connected or not
        for (IAtomContainer mol : graphsOnFourAtoms()) {
            MCSGraph.Side side = graph(mol, mol, true).query;
            for (int kept = 0; kept <= 4; kept++) {
                int most = 0;
                for (int mask = 0; mask < 16; mask++) {
                    if (Integer.bitCount(mask) > kept)
                        continue;
                    int bonds = 0;
                    for (IBond bond : mol.bonds()) {
                        int ends = 1 << mol.indexOf(bond.getBegin()) | 1 << mol.indexOf(bond.getEnd());
                        if ((mask & ends) == ends)
                            bonds++;
                    }
                    most = Math.max(most, bonds);
                }
                Assertions.assertEquals(most, side.bondBound(kept, never()));
            }
        }
        IAtomContainer adamantane = smi("C1C2CC3CC1CC(C2)C3");
        Assertions.assertEquals(8, graph(adamantane, adamantane, true).query.bondBound(8, never()));
    }

    @Test
    void testBondBoundMappings() throws Exception {
        // size or elements leave one or two atoms out, yet every maximum is found
        for (String[] pair : new String[][]{{"C1CCC1", "C1CCCC1"}, {"C1CCC1", "C1CCCCC1"},
                                          {"C1CCNC1", "C1COCC1"}, {"C1CNNCC1", "C1COOCC1"}}) {
            for (String[] order : new String[][]{pair, {pair[1], pair[0]}}) {
                IAtomContainer query = smi(order[0]), target = smi(order[1]);
                for (boolean bonds : new boolean[]{false, true}) {
                    Set<String> expected = exact(query, target, bonds);
                    String what = order[0] + " on " + order[1] + (bonds ? "" : " by element");
                    for (int cap : new int[]{1, 2, 1000})
                        assertMaximumMappings(expected, search(query, target, bonds, never()).run(cap), cap, what);
                }
            }
        }
    }

    @Test
    void testLabelBound() throws Exception {
        // on itself every atom and bond
        for (String smiles : new String[]{"c1ccccc1O", "CC(=O)Oc1ccccc1C(=O)O", "C1CC2CCC1C2", "[Na+].[Cl-]", "*CC"}) {
            IAtomContainer mol = smi(smiles);
            Assertions.assertEquals((long) mol.getAtomCount() << 32 | mol.getBondCount(),
                                    graph(mol, smi(smiles), true).labelBound(false));
        }
        // cyclohexane on hexane: the seed has the 6 atoms and 5 bonds of the bound
        MCSGraph graph = graph(smi("C1CCCCC1"), smi("CCCCCC"), true);
        MCSSearch search = new MCSSearch(graph, never(), 0);
        search.rank();
        Assertions.assertEquals(6L << 32 | 5, graph.labelBound(false));
        Assertions.assertEquals(graph.labelBound(false), search.score(search.seed(NONE)));
        // there is none for expressions
        Assertions.assertEquals(-1, graph(smi("CC"), smi("CC"), false).labelBound(false));
        Assertions.assertEquals(-1, graph(smi("CC"), smi("CC"), false).labelBound(true));
        Assertions.assertEquals(-1, graph(smarts("[C,N]~O"), smi("CO"), true).labelBound(true));
    }

    @Test
    @Tag("SlowTest")
    void testLabelBoundRandom() throws Exception {
        Random random = new Random(4102026L);
        for (int i = 0; i < 300; i++) {
            IAtomContainer a = randomGraph(random, 2 + random.nextInt(5), 0.25, "C", "N", "O");
            IAtomContainer b = randomGraph(random, 2 + random.nextInt(5), 0.25, "C", "N", "O");
            MCSGraph graph = graph(a, b, true);
            MCSSearch search = new MCSSearch(graph, never(), 0);
            long bound = graph.labelBound(false);
            long connected = graph.labelBound(true);
            Assertions.assertTrue(connected >>> 32 <= bound >>> 32 && (int) connected <= (int) bound);
            Set<String> exact = exact(a, b, true);
            if (exact.isEmpty())
                continue;
            // no maximum mapping has more atoms or more common bonds
            int[] mapping = parse(exact.iterator().next());
            long score = (long) atoms(mapping) << 32 | commonBonds(a, b, mapping, true);
            Assertions.assertTrue(score >>> 32 <= bound >>> 32 && (int) score <= (int) bound);
            Assertions.assertTrue(score >>> 32 <= connected >>> 32 && (int) score <= (int) connected);
            // and a seed that reaches it has the maximum score
            search.rank();
            if (search.score(search.seed(NONE)) == bound)
                Assertions.assertEquals(score, bound);
        }
    }

    @Test
    void testConnectedLabelBound() throws Exception {
        assertConnectedBound("NC=O", "NCO", 3L << 32 | 1, 2L << 32 | 1);
        assertConnectedBound("CC=CC=CC", "CCCCCC", 6L << 32 | 3, 2L << 32 | 1);
        assertConnectedBound("CC.CC", "CCCC", 4L << 32 | 2, 2L << 32 | 1);
    }

    @Test
    @Tag("SlowTest")
    void testCollect() throws Exception {
        Random random = new Random(2026L);
        for (int i = 0; i < 300; i++) {
            boolean bonds = i % 2 == 0;
            IAtomContainer a = randomGraph(random, 2 + random.nextInt(5), 0.25, "C", "N", "O");
            IAtomContainer b = randomGraph(random, 2 + random.nextInt(5), 0.25, "C", "N", "O");
            Set<String> exact = exact(a, b, bonds);
            if (exact.isEmpty())
                continue;
            int[] mapping = parse(exact.iterator().next());
            long score = (long) atoms(mapping) << 32 | commonBonds(a, b, mapping, bonds);
            // given the maximum score, exactly the maximum mappings
            assertSameSet(exact, ranked(a, b, bonds).collect(null, score, score, Integer.MAX_VALUE));
            // from nothing, through every better score on the way
            assertSameSet(exact, ranked(a, b, bonds).collect(null, 0, NONE, Integer.MAX_VALUE));
            // and one of them when one is asked for
            List<int[]> one = ranked(a, b, bonds).collect(null, 0, NONE, 1);
            Assertions.assertEquals(1, one.size());
            Assertions.assertTrue(exact.contains(Arrays.toString(one.get(0))));
        }
    }

    @Test
    @Tag("SlowTest")
    void testSmallRingSystems() throws Exception {
        // fused, bridged and spiro rings, where a state has several query atoms to try and is bounded again
        String[] smiles = {"C1CC2CCC12", "C1C2CC1C2", "C1CC11CC1", "C1CCCCC1", "c1ccccc1", "C1CCC1", "CC1CC1",
                           "[H]C1CCC1"};
        for (String a : smiles) {
            for (String b : smiles) {
                for (boolean bonds : new boolean[]{true, false})
                    assertSameSet(exact(smi(a), smi(b), bonds), search(smi(a), smi(b), bonds, never()).run(1000));
            }
        }
    }

    @Test
    @Tag("SlowTest")
    void testRandomRingCores() throws Exception {
        Random random = new Random(20261006L);
        for (int i = 0; i < 60; i++) {
            IAtomContainer a = ringCore(random), b = ringCore(random);
            for (boolean bonds : new boolean[]{true, false})
                assertSameSet(exact(a, b, bonds), search(a, b, bonds, never()).run(Integer.MAX_VALUE));
        }
    }

    @Test
    @Tag("SlowTest")
    void testAllFourAtomGraphsDisconnected() throws Exception {
        // fragments of one or two bonds, in graphs in parts too, where a frame offers the roots of new fragments
        List<IAtomContainer> targets = graphsOnFourAtoms();
        for (IAtomContainer a : graphsOnFourAtoms()) {
            for (IAtomContainer b : targets) {
                for (int minBonds = 1; minBonds <= 2; minBonds++) {
                    for (boolean bonds : new boolean[]{true, false})
                        assertSameSet(bruteForceEdges(a, b, bonds, minBonds),
                                      search(a, b, bonds, never(), minBonds).run(Integer.MAX_VALUE));
                }
            }
        }
    }

    @Test
    void testDisconnectedSeedAfterStereoCut() throws Exception {
        // isotopes fix the atom pairs; opposite centres cut the greedy seed, exposing links to its fragment
        IAtomContainer query = stereoSeedGraph(false), target = stereoSeedGraph(true);
        MCS mcs = MCS.find(query).withMatching(ELEMENT, ISOTOPE, SINGLE_OR_AROMATIC)
                     .withStereochemistry().withDisconnected(2);
        // keeping a later seed fragment used to retain the one-bond component 8-11, despite the minimum of two
        int[] expected = {-1, -1, 2, 3, 4, 5, 6, 7, -1, 9, 10, -1};
        Assertions.assertArrayEquals(expected, mcs.match(target));
        List<int[]> mappings = mcs.matchAll(target, 10);
        Assertions.assertEquals(1, mappings.size());
        Assertions.assertArrayEquals(expected, mappings.get(0));
    }

    private static IAtomContainer stereoSeedGraph(boolean target) {
        IAtomContainer mol = new AtomContainer();
        for (int i = 0; i < 12; i++) {
            IAtom atom = new Atom("C");
            atom.setMassNumber(12 + i);
            mol.addAtom(atom);
        }
        int[][] edges = {{10, 1}, {0, 1}, {3, 4}, {2, 5}, {5, 4}, {10, 3}, {9, 3}, {0, 4},
                         {10, 6}, {11, 9}, {0, 3}, {8, 11}, {6, 4}, {7, 2}, {0, 2}, {8, 6}};
        for (int i = 0; i < edges.length; i++)
            if (!target || i != 0 && i != 4 && i != 8 && i != 9 && i != 15)
                mol.addBond(edges[i][0], edges[i][1], IBond.Order.SINGLE);
        mol.addStereoElement(new TetrahedralChirality(mol.getAtom(0),
                            new IAtom[]{mol.getAtom(1), mol.getAtom(2), mol.getAtom(3), mol.getAtom(4)},
                            target ? ITetrahedralChirality.Stereo.ANTI_CLOCKWISE
                                   : ITetrahedralChirality.Stereo.CLOCKWISE));
        return mol;
    }

    private static MCSGraph.Clock never() {
        return new MCSGraph.Clock(() -> false);
    }

    // stop only inside the named loop of the search or its parts, so setup or state polls cannot mask work it does
    // not tick
    private static MCSGraph.Clock stopIn(String method) {
        return new MCSGraph.Clock(() -> {
            for (StackTraceElement frame : Thread.currentThread().getStackTrace())
                if (frame.getClassName().startsWith(MCSSearch.class.getName()) && frame.getMethodName().equals(method))
                    return true;
            return false;
        });
    }

    // the work of a search for every mapping, as the number of times it asks whether to stop
    private static int work(IAtomContainer query, IAtomContainer target) throws MCSGraph.Stop {
        AtomicInteger asked = new AtomicInteger();
        search(query, target, true, new MCSGraph.Clock(() -> asked.incrementAndGet() < 0)).run(1000);
        return asked.get();
    }

    private static void assertGrowth(int work, int more, int most) {
        Assertions.assertTrue(more < most * work, () -> work + " then " + more);
    }

    private static MCSSearch search(IAtomContainer query, IAtomContainer target, boolean bonds,
                                    MCSGraph.Clock clock) throws MCSGraph.Stop {
        return search(query, target, bonds, clock, 0);
    }

    private static MCSSearch search(IAtomContainer query, IAtomContainer target, boolean bonds,
                                    MCSGraph.Clock clock, int minBonds) throws MCSGraph.Stop {
        return new MCSSearch(graph(query, target, bonds), clock, minBonds);
    }

    private static MCSGraph graph(IAtomContainer query, IAtomContainer target, boolean bonds) {
        IAtomContainer mol = bonds ? query : QueryAtomContainer.create(query, ELEMENT);
        return new MCSGraph(mol, query, target, false, false);
    }

    private static MCSSearch ranked(IAtomContainer query, IAtomContainer target, boolean bonds)
            throws MCSGraph.Stop {
        MCSSearch search = search(query, target, bonds, never());
        search.rank();
        return search;
    }

    private static List<int[]> run(IAtomContainer query, IAtomContainer target, boolean bonds)
            throws MCSGraph.Stop {
        return search(query, target, bonds, never()).run(1000);
    }

    // a ring of a atoms on a ring of b: a path of a atoms, any of the a ring bonds left out, laid along the larger
    // ring from any of its b atoms either way, 2ab mappings
    private static void assertRings(int[][] sizes) throws MCSGraph.Stop {
        for (int[] size : sizes) {
            int a = size[0], b = size[1];
            IAtomContainer small = ring(a), large = ring(b);
            for (IAtomContainer[] pair : new IAtomContainer[][]{{small, large}, {large, small}}) {
                List<int[]> mappings = run(pair[0], pair[1], true);
                Assertions.assertEquals(2 * a * b, mappings.size());
                Assertions.assertEquals(2 * a * b, keys(mappings).size());
                for (int[] mapping : mappings) {
                    Assertions.assertEquals(a, atoms(mapping));
                    Assertions.assertEquals(a - 1, commonBonds(pair[0], pair[1], mapping, true));
                }
            }
        }
    }

    // at most cap of the expected maximum mappings, each once, and all of them when there are no more than cap
    private static void assertMaximumMappings(Set<String> expected, List<int[]> mappings, int cap, String what) {
        String message = what + ", at most " + cap;
        Assertions.assertEquals(Math.min(cap, expected.size()), mappings.size(), message);
        Assertions.assertEquals(mappings.size(), keys(mappings).size(), message);
        Assertions.assertTrue(expected.containsAll(keys(mappings)), message);
    }

    private static void assertSameSet(Set<String> expected, List<int[]> mappings) {
        Assertions.assertEquals(mappings.size(), keys(mappings).size(), "each mapping once");
        Assertions.assertEquals(expected, keys(mappings));
    }

    private static void assertConnectedBound(String a, String b, long global, long connected) throws Exception {
        IAtomContainer query = smi(a), target = smi(b);
        for (IAtomContainer[] pair : new IAtomContainer[][]{{query, target}, {target, query}}) {
            MCSGraph graph = graph(pair[0], pair[1], true);
            Assertions.assertEquals(global, graph.labelBound(false), a + " / " + b);
            Assertions.assertEquals(connected, graph.labelBound(true), a + " / " + b);
            assertSameSet(exact(pair[0], pair[1], true), new MCSSearch(graph, never(), 0).run(Integer.MAX_VALUE));
        }
    }

    private static void assertEmbeddings(IAtomContainer query, IAtomContainer target, int count) throws Exception {
        MCSSearch.Embedding embedding = search(query, target, true, never()).embedding();
        Assertions.assertNotNull(embedding);
        List<int[]> embeddings = embedding.all(1000);
        Assertions.assertEquals(count, keys(embeddings).size());
        Assertions.assertEquals(count, embeddings.size());
        for (int[] each : embeddings)
            Assertions.assertEquals(query.getBondCount(), commonBonds(query, target, each, true));
    }

    private static int[] ranks(int[][] graph) throws Exception {
        int[] start = new int[graph.length + 1];
        for (int i = 0; i < graph.length; i++)
            start[i + 1] = start[i] + graph[i].length;
        int[] nbr = new int[start[graph.length]];
        for (int i = 0; i < graph.length; i++)
            System.arraycopy(graph[i], 0, nbr, start[i], graph[i].length);
        return MCSGraph.refine(start, nbr, new int[graph.length], true, never());
    }

    private static int[] parse(String mapping) {
        String[] parts = mapping.substring(1, mapping.length() - 1).split(", ");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++)
            result[i] = Integer.parseInt(parts[i]);
        return result;
    }

    // the mappings with the target atoms moved up by pad
    private static Set<String> shifted(Set<String> mappings, int pad) {
        Set<String> shifted = new HashSet<>();
        for (String mapping : mappings) {
            int[] map = parse(mapping);
            for (int i = 0; i < map.length; i++)
                if (map[i] >= 0) map[i] += pad;
            shifted.add(Arrays.toString(map));
        }
        return shifted;
    }

    // the molecule after pad xenon atoms
    private static IAtomContainer padded(IAtomContainer mol, int pad) {
        IAtomContainer padded = new AtomContainer();
        for (int i = 0; i < pad; i++)
            padded.addAtom(new Atom("Xe"));
        for (IAtom atom : mol.atoms())
            padded.addAtom(new Atom(atom.getSymbol()));
        for (IBond bond : mol.bonds()) {
            padded.addBond(pad + bond.getBegin().getIndex(), pad + bond.getEnd().getIndex(), bond.getOrder());
            padded.getBond(padded.getBondCount() - 1).setIsAromatic(bond.isAromatic());
        }
        return padded;
    }

    // a chain of carbons, or of atoms without a symbol where false, joined by single bonds
    private static IAtomContainer chain(boolean... carbons) {
        IAtomContainer mol = new AtomContainer();
        for (boolean carbon : carbons) {
            mol.addAtom(carbon ? new Atom("C") : noSymbol());
            if (mol.getAtomCount() > 1)
                mol.addBond(mol.getAtomCount() - 2, mol.getAtomCount() - 1, IBond.Order.SINGLE);
        }
        return mol;
    }

    private static IAtom noSymbol() {
        IAtom atom = new Atom();
        atom.setSymbol(null);
        return atom;
    }

    private static IAtomContainer mol(IAtom atom) {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(atom);
        return mol;
    }

    // 6 or 7 carbons, a ring of 4 to 6 of them, and each other two bonded with probability 1/2
    private static IAtomContainer ringCore(Random random) {
        int size = 6 + random.nextInt(2), ring = 4 + random.nextInt(3);
        IAtomContainer mol = new AtomContainer();
        for (int i = 0; i < size; i++)
            mol.addAtom(new Atom("C"));
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (j < ring && (j == i + 1 || i == 0 && j == ring - 1) || random.nextBoolean())
                    mol.addBond(i, j, IBond.Order.SINGLE);
            }
        }
        return mol;
    }

    private static IAtomContainer ring(int size) {
        IAtomContainer ring = new AtomContainer();
        for (int i = 0; i < size; i++)
            ring.addAtom(new Atom("C"));
        for (int i = 0; i < size; i++)
            ring.addBond(i, (i + 1) % size, IBond.Order.SINGLE);
        return ring;
    }

    private static IAtomContainer grid(int side) {
        IAtomContainer grid = new AtomContainer();
        for (int i = 0; i < side * side; i++) {
            grid.addAtom(new Atom("C"));
            if (i % side > 0)
                grid.addBond(i - 1, i, IBond.Order.SINGLE);
            if (i >= side)
                grid.addBond(i - side, i, IBond.Order.SINGLE);
        }
        return grid;
    }

    // a carbon with leaves carbons around it, and one more leaf of the given symbol unless it is null
    private static IAtomContainer star(int leaves, String other) {
        IAtomContainer star = new AtomContainer();
        star.addAtom(new Atom("C"));
        for (int i = 0; i < leaves; i++) {
            star.addAtom(new Atom("C"));
            star.addBond(0, i + 1, IBond.Order.SINGLE);
        }
        if (other != null) {
            star.addAtom(new Atom(other));
            star.addBond(0, leaves + 1, IBond.Order.SINGLE);
        }
        return star;
    }

    private static String perfluoroalkane(int carbons) {
        StringBuilder smiles = new StringBuilder("F");
        for (int i = 0; i < carbons; i++)
            smiles.append("C(F)(F)");
        return smiles.append('F').toString();
    }
}
