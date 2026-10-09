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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.Intractable;
import org.openscience.cdk.graph.ConnectedComponents;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.openscience.cdk.interfaces.IStereoElement.GRP_NUM_SHIFT;
import static org.openscience.cdk.interfaces.IStereoElement.GRP_RAC;
import static org.openscience.cdk.isomorphism.MCSTesting.atoms;
import static org.openscience.cdk.isomorphism.MCSTesting.bruteForceEdges;
import static org.openscience.cdk.isomorphism.MCSTesting.commonBonds;
import static org.openscience.cdk.isomorphism.MCSTesting.completeGraph;
import static org.openscience.cdk.isomorphism.MCSTesting.exact;
import static org.openscience.cdk.isomorphism.MCSTesting.inverse;
import static org.openscience.cdk.isomorphism.MCSTesting.inverses;
import static org.openscience.cdk.isomorphism.MCSTesting.keys;
import static org.openscience.cdk.isomorphism.MCSTesting.randomGraph;
import static org.openscience.cdk.isomorphism.MCSTesting.setAromatic;
import static org.openscience.cdk.isomorphism.MCSTesting.smi;
import static org.openscience.cdk.isomorphism.MCSTesting.snapshot;
import static org.openscience.cdk.isomorphism.MCSTesting.withConfigurations;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ELEMENT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.SINGLE_OR_AROMATIC;

/**
 * Properties of {@link MCS} that hold for any input.
 * <br><br>
 * Exactness: the default matching, {@code withMatching(ELEMENT)} and query
 * molecules made with {@code QueryAtomContainer.create(query, ELEMENT)} give
 * the maximum mappings of {@link MCSTesting#exact}, compared as sets, on
 * every ordered pair of connected graphs with up to four, five and six
 * atoms, plain and with N, O and aromatic ring bonds, and of the
 * graphs on four atoms with single and double bonds, on random pairs drawn
 * from strata of size, density and elements, and on small and random
 * molecules with hydrogens, pseudo atoms and several parts, there also
 * without any matching types.
 * <br><br>
 * Metamorphic relations, each checked only where it holds, on random graphs
 * and on drug-like and ring-rich molecules: self (the mappings are the
 * automorphisms, and a new search with a time limit finds the same on a
 * copy), symmetry (swapping query and target inverts the mappings),
 * substructure (a connected query is mapped whole exactly when it is a
 * substructure, by its embeddings), permutation of atoms and bonds, and
 * monotonicity under deleting a target atom.
 * <br><br>
 * With stereochemistry, see {@code assertStereo}, on small molecules and
 * graphs with configurations, and with complete rings as well; without any,
 * the same list as without it.
 * <br><br>
 * Maximum common edge subgraphs, {@code withDisconnected}: the mappings of
 * {@link MCSTesting#bruteForceEdges} on random pairs, some in parts, and the
 * same turned round when query and target swap; on random molecules the
 * relations that need no brute force, see {@code assertEdgeRelations}; and
 * every subset of the options together on random graphs with configurations,
 * see {@code assertOptions}.
 * <br><br>
 * Every search is also checked as any search, see {@code search}. With 1000
 * or more maximum mappings, the 1000 returned must each be one. Limits are
 * checked by {@link MCSTesting#assertLimits}. Only the pairs of graphs with
 * up to four atoms are searched in every build; the rest is tagged SlowTest.
 *
 * @author Syed Asad Rahman
 */
final class MCSPropertyTest {

    /** The most mappings matchAll returns. */
    private static final int                         CAP       = 1000;
    private static final IChemObjectBuilder          BUILDER   = SilentChemObjectBuilder.getInstance();
    private static final String[]                    CARBON    = {"C"};
    private static final String[]                    CNO       = {"C", "N", "O"};
    /** Probability of each bond besides a random spanning tree; 0 makes a tree. */
    private static final double[]                    DENSITIES = {0, 0.1, 0.25, 0.5};
    /** The connected graphs by number of atoms, see {@link #connectedGraphs(int)}. */
    private static final Map<Integer, List<Integer>> GRAPHS    = new HashMap<>();

    private static final String[] MOLECULES = {
            "CC(=O)Oc1ccccc1C(=O)O", "CC(=O)Nc1ccc(O)cc1", "CC(C)Cc1ccc(cc1)C(C)C(=O)O", "Cn1cnc2c1c(=O)n(C)c(=O)n2C",
            "CN1CCCC1c1cccnc1", "CN1CCC23c4c5ccc(O)c4OC2C(O)C=CC3C1C5", "CN1C(=O)CN=C(c2ccccc2)c2cc(Cl)ccc12",
            "Cc1ccc(NC(=O)c2ccc(CN3CCN(C)CC3)cc2)cc1Nc1nccc(-c2cccnc2)n1",
            "CCCc1nn(C)c2c(=O)[nH]c(-c3cc(S(=O)(=O)N4CCN(C)CC4)ccc3OCC)nc12",
            "Cc1ccc(-c2cc(C(F)(F)F)nn2-c2ccc(S(N)(=O)=O)cc2)cc1", "CCOC(=O)N1CCC(=C2c3ccc(Cl)cc3CCc3cccnc32)CC1",
            "COc1ccc2nccc(C(O)C3CC4CCN3CC4C=C)c2c1", "O=C1CC2OCC=C3CN4CCC56C4CC3C2C6N1c1ccccc15",
            "CC(C)CCCC(C)C1CCC2C1(C)CCC1C2CC=C2CC(O)CCC21C", "CC12CCC3C(CCC4=CC(=O)CCC34C)C1CCC2O",
            "CC12CCC3c4ccc(O)cc4CCC3C1CCC2O", "c1ccc2cc3ccccc3cc2c1", "c1ccc2c(c1)ccc1ccccc12", "C1C2CC3CC1CC(C2)C3",
            "C12C3C4C1C5C2C3C45", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O", "CC(=O)CC(c1ccccc1)c1c(O)c2ccccc2oc1=O",
            "OC(=O)c1cn(C2CC2)c2cc(N3CCNCC3)c(F)cc2c1=O", "CCC(=C(c1ccccc1)c1ccc(OCCN(C)C)cc1)c1ccccc1",
            "OC1(CCN(CCCC(=O)c2ccc(F)cc2)CC1)c1ccc(Cl)cc1", "CCN(CC)CC(=O)Nc1c(C)cccc1C", "CC(C)NCC(O)COc1cccc2ccccc12",
            "OC(c1ccccc1)(c1ccccc1)c1ccccc1", "[O-][N+](=O)c1ccc(cc1)C(=O)O", "C1CCC2(CC1)CCCCC2"
    };

    /** Alanine, cysteine, threonine, butanediol, dimethylcyclohexane, alkenes and a tetrose, for configurations. */
    private static final String[] CHIRAL = {"NC(C)C(=O)O", "NC(CS)C(=O)O", "CC(C(C(=O)O)N)O", "CC(O)C(C)O",
                                            "CC1CCCCC1C", "FC(Cl)=CF", "ClC=CC=CF", "CC=CC(C)O", "O=CC(O)C(O)CO",
                                            "CC(N)(O)CC"};

    /** Ring systems and functional groups, as queries for the substructure relation. */
    private static final String[] FRAGMENTS = {
            "c1ccccc1", "C1CCCCC1", "C1CCNCC1", "C1CNCCN1", "c1ccncc1", "c1ccc2ccccc2c1", "C1CC2CCC1C2",
            "NC=O", "OC=O", "CC(C)C", "C=C", "NS(=O)=O", "FC(F)F", "Clc1ccccc1", "CC12CCC3C(CCC4CCCCC34)C1CCC2"
    };

    // exactness against the brute force search

    @Test
    void testAllPairsUpToFourAtoms() throws Exception {
        // 10 graphs, 100 ordered pairs, plain and labelled
        assertAllPairs(() -> graphsUpTo(4, false));
        assertAllPairs(() -> graphsUpTo(4, true));
    }

    @Test
    @Tag("SlowTest")
    void testAllPairsUpToFiveAtoms() throws Exception {
        int[] expected = {1, 1, 2, 6, 21};
        for (int n = 1; n <= 5; n++) {
            Assertions.assertEquals(expected[n - 1], connectedGraphs(n).size(), n + " atoms");
        }
        // 31 graphs, 961 ordered pairs
        assertAllPairs(() -> graphsUpTo(5, false));
    }

    @Test
    @Tag("SlowTest")
    void testAllLabelledPairsUpToFiveAtoms() throws Exception {
        assertAllPairs(() -> graphsUpTo(5, true));
    }

    @Test
    @Tag("SlowTest")
    void testAllGraphsOnFourAtoms() throws Exception {
        // 4096 ordered pairs, disconnected graphs too
        assertAllPairs(MCSTesting::graphsOnFourAtoms);
    }

    @Test
    @Tag("SlowTest")
    void testRandomPairs() throws Exception {
        // three pairs per stratum, from two seeds
        assertRandomPairs(new Random(20261004L), 2);
        assertRandomPairs(new Random(4102026L), 1);
    }

    @Test
    @Tag("SlowTest")
    void testRandomMolecules() throws Exception {
        assertRandomMolecules(new Random(4102026L), 500, 6);
    }

    @Test
    @Tag("SlowTest")
    void testSmallMolecules() throws Exception {
        // ring heteroatoms, explicit hydrogens, pseudo atoms, molecules in parts, and queries larger than their targets
        String[][] pairs = {{"CC(=O)O", "OC(=O)CC"}, {"c1ccncc1", "c1ccccc1"}, {"C1CCOC1", "C1CCNCC1"},
                {"[H]OC=O", "OC([H])=O"}, {"C=CC=C", "CC=CC"}, {"NCC(=O)O", "CC=O"},
                {"CCO", "CC.CCO"}, {"OCCN", "OCC.CCN"}, {"CCO.CN", "NCCO"}, {"CC.CC", "CCCC"},
                {"OCCc1ccccc1", "c1ccccc1"}, {"*CC(=O)N", "NC(=O)C*"}};
        for (String[] pair : pairs) {
            assertMolecules(smi(pair[0]), smi(pair[1]));
            assertMolecules(smi(pair[1]), smi(pair[0]));
        }
    }

    @Test
    @Tag("SlowTest")
    void testMoreMappingsThanReturned() throws Exception {
        // K6 and K6 have 720 maximum mappings, the others 5040, of which 1000 are returned
        for (int n = 6; n <= 7; n++) {
            for (int m = 6; m <= 7; m++) {
                IAtomContainer a = completeGraph(n);
                IAtomContainer b = completeGraph(m);
                Assertions.assertEquals(n == 6 && m == 6 ? 720 : 5040, exact(a, b, true).size());
                assertPairs(a, Collections.singletonList(b));
            }
        }
    }

    @Test
    @Tag("SlowTest")
    void testLimits() throws Exception {
        List<IAtomContainer> graphs = graphsUpTo(4, false);
        graphs.addAll(graphsUpTo(4, true));
        for (IAtomContainer query : graphs) {
            for (IAtomContainer target : graphs) {
                assertLimits(query, target);
            }
        }
        Random random = new Random(20261006L);
        for (int i = 0; i < 100; i++) {
            double density = DENSITIES[i % DENSITIES.length];
            String[] elements = i / DENSITIES.length % 2 == 0 ? CNO : CARBON;
            IAtomContainer query = randomGraph(random, 2 + random.nextInt(6), density, elements);
            assertLimits(query, randomGraph(random, 2 + random.nextInt(6), density, elements));
        }
    }

    @Test
    @Tag("SlowTest")
    void testAllFourAtomGraphsCompleteRings() throws Exception {
        // the triangle, the square, and K4, whose four triangles are all relevant rings, among them
        List<IAtomContainer> targets = MCSTesting.graphsOnFourAtoms();
        for (IAtomContainer query : MCSTesting.graphsOnFourAtoms()) {
            for (IAtomContainer target : targets) {
                assertCompleteRings(query, target, true);
                assertCompleteRings(query, target, false);
            }
        }
    }

    @Test
    @Tag("SlowTest")
    void testCompleteRingsRandomPairs() throws Exception {
        Random random = new Random(1062026L);
        for (int i = 0; i < 400; i++) {
            double density = DENSITIES[i % DENSITIES.length];
            String[] elements = i / DENSITIES.length % 2 == 0 ? CNO : CARBON;
            IAtomContainer a = randomGraph(random, 2 + random.nextInt(6), density, elements);
            IAtomContainer b = randomGraph(random, 2 + random.nextInt(6), density, elements);
            for (boolean orders : new boolean[]{true, false}) {
                assertCompleteRings(a, b, orders);
                assertCompleteRings(b, a, orders);
            }
        }
    }

    @Test
    @Tag("SlowTest")
    void testDisconnectedRandomPairs() throws Exception {
        assertEdgePairs(new Random(6102026L), 300, 6, 2);
    }

    @Test
    @Tag("SlowTest")
    void testOptionCombinations() throws Exception {
        assertOptionCombinations(new Random(20261007L), 100, 5);
    }

    @Test
    @Tag("SlowTest")
    void testDisconnectedSweep() throws Exception {
        // the pairs of seven carbon atoms catch a frame that offers roots only if it had no candidates
        assertEdgePairs(new Random(7102026L), 3000, 7, 3);
        Random random = new Random(8102026L);
        for (int i = 0; i < 200; i++) {
            IAtomContainer a = randomMolecule(random, 2 + random.nextInt(8));
            assertEdgeRelations(a, randomMolecule(random, 2 + random.nextInt(8)), random);
        }
    }

    @Test
    @Tag("SlowTest")
    void testOptionCombinationsSweep() throws Exception {
        assertOptionCombinations(new Random(20261008L), 300, 6);
    }

    @Test
    @Tag("SlowTest")
    void testAllPairsUpToSixAtoms() throws Exception {
        Assertions.assertEquals(112, connectedGraphs(6).size());
        // 143 graphs, 20,449 ordered pairs
        assertAllPairs(() -> graphsUpTo(6, false));
    }

    @Test
    @Tag("SlowTest")
    void testAllLabelledPairsUpToSixAtoms() throws Exception {
        assertAllPairs(() -> graphsUpTo(6, true));
    }

    @Test
    @Tag("SlowTest")
    void testRandomPairsOfEightAndNineAtoms() throws Exception {
        // the densities in turn, and blocks of four pairs of C, N and O or of carbon only, 24 of each;
        // a brute force search of two carbon graphs with nine atoms takes about a second
        Random random = new Random(98L);
        for (int k = 0; k < 48; k++) {
            double density = DENSITIES[k % DENSITIES.length];
            String[] elements = k / DENSITIES.length % 2 == 0 ? CNO : CARBON;
            IAtomContainer query = randomGraph(random, 8 + random.nextInt(2), density, elements);
            assertPairs(query, Collections.singletonList(randomGraph(random, 8 + random.nextInt(2), density,
                                                                     elements)));
        }
    }

    @Test
    @Tag("SlowTest")
    void testManyRandomMolecules() throws Exception {
        assertRandomMolecules(new Random(20261004L), 3000, 9);
    }

    // metamorphic relations

    @Test
    @Tag("SlowTest")
    void testSelf() throws Exception {
        for (String smiles : MOLECULES) {
            assertSelf(smi(smiles), true);
            assertSelf(smi(smiles), false);
        }
        Random random = new Random(46125L);
        for (int i = 0; i < 60; i++) {
            IAtomContainer graph = randomGraph(random, 1 + random.nextInt(9), 0.25, CNO);
            assertSelf(graph, true);
            assertSelf(graph, false);
        }
        // the common substructure is connected, so a molecule in parts maps onto itself with a largest part
        random = new Random(5113L);
        for (int i = 0; i < 60; i++) {
            IAtomContainer graph = deleteBonds(randomGraph(random, 2 + random.nextInt(8), 0.25, CNO), random);
            for (boolean orders : new boolean[]{true, false}) {
                Assertions.assertEquals(largestPart(graph), search(mcs(graph, orders), graph, graph, orders).atoms);
            }
        }
        // of the parts with six atoms, the ring has more bonds than the chain
        IAtomContainer salt = smi("CCCC(=O)[O-].[Na+].c1ccccc1");
        Result self = search(MCS.find(salt), salt, salt, true);
        Assertions.assertEquals(6, self.atoms);
        Assertions.assertEquals(6, self.bonds);
    }

    @Test
    @Tag("SlowTest")
    void testSymmetry() throws Exception {
        // equal sizes as well, where neither search runs the other way round, and graphs in parts
        Random random = new Random(77321L);
        for (int i = 0; i < 150; i++) {
            int size = 1 + random.nextInt(8);
            IAtomContainer a = randomGraph(random, size, 0.25, CNO);
            IAtomContainer b = randomGraph(random, i % 3 == 0 ? size : 1 + random.nextInt(8), 0.25, CNO);
            if (i % 2 == 0) {
                deleteBonds(b, random);
            }
            if (i % 4 == 0) {
                deleteBonds(a, random);
            }
            assertSymmetric(a, b);
        }
        String[][] pairs = {{"OCCO.NCCN", "OCCN"}, {"CCCC.c1ccccc1", "CCC=C"}, {"FC(F)(F)Cl", "OCCN"},
                {"[Na+].[Cl-]", "Cl"}, {"C1CC1.C1CCC1", "C1CCCCC1"}};
        for (String[] pair : pairs) {
            assertSymmetric(smi(pair[0]), smi(pair[1]));
        }
        // 2520 maximum mappings each way, more than are returned
        assertSymmetric(completeGraph(5), completeGraph(7));
    }

    @Test
    @Tag("SlowTest")
    void testSubstructure() throws Exception {
        // both sides of the relation are checked
        int[] fragments = new int[2];
        for (String fragment : FRAGMENTS) {
            for (String smiles : MOLECULES) {
                fragments[assertSubstructure(smi(fragment), smi(smiles)) ? 0 : 1]++;
            }
        }
        Assertions.assertTrue(fragments[0] > 50 && fragments[1] > 50, Arrays.toString(fragments));
        // a part of a molecule is always a substructure of it; changed, or on another molecule, it may not be
        Random random = new Random(990017L);
        int[] parts = new int[2];
        for (String smiles : MOLECULES) {
            IAtomContainer mol = smi(smiles);
            for (int k = 0; k < 3; k++) {
                IAtomContainer part = fragment(mol, random, 1 + random.nextInt(Math.min(14, mol.getAtomCount())));
                Assertions.assertTrue(assertSubstructure(part, mol));
                parts[assertSubstructure(part, smi(MOLECULES[random.nextInt(MOLECULES.length)])) ? 0 : 1]++;
                mutate(part, random);
                parts[assertSubstructure(part, mol) ? 0 : 1]++;
            }
        }
        Assertions.assertTrue(parts[0] > 20 && parts[1] > 20, Arrays.toString(parts));
        random = new Random(31337L);
        for (int i = 0; i < 100; i++) {
            IAtomContainer target = randomGraph(random, 1 + random.nextInt(9), 0.25, CNO);
            IAtomContainer query = fragment(target, random, 1 + random.nextInt(target.getAtomCount()));
            Assertions.assertTrue(assertSubstructure(query, target));
            mutate(query, random);
            assertSubstructure(query, target);
            assertSubstructure(query, randomGraph(random, 1 + random.nextInt(9), 0.25, CNO));
        }
    }

    @Test
    @Tag("SlowTest")
    void testPermutation() throws Exception {
        Random random = new Random(8086L);
        for (int i = 0; i < 80; i++) {
            IAtomContainer a = randomGraph(random, 1 + random.nextInt(8), 0.25, CNO);
            IAtomContainer b = randomGraph(random, 1 + random.nextInt(8), 0.25, CNO);
            if (i % 4 == 0) {
                deleteBonds(a, random);
            }
            assertPermutationInvariant(a, b, true, random);
            assertPermutationInvariant(a, b, false, random);
        }
        // more maximum mappings than are returned
        assertPermutationInvariant(completeGraph(7), completeGraph(5), true, random);
        assertPermutationInvariant(completeGraph(5), completeGraph(7), false, random);
    }

    @Test
    @Tag("SlowTest")
    void testDeleteTargetAtom() throws Exception {
        int[][] pairs = {{0, 1}, {2, 25}, {4, 11}, {14, 15}, {6, 21}, {28, 0}, {18, 19}};
        for (int[] pair : pairs) {
            assertDeletionMonotone(smi(MOLECULES[pair[0]]), smi(MOLECULES[pair[1]]));
        }
        Random random = new Random(161803L);
        for (int i = 0; i < 40; i++) {
            assertDeletionMonotone(randomGraph(random, 1 + random.nextInt(7), 0.25, CNO),
                                   randomGraph(random, 1 + random.nextInt(7), 0.25, CNO));
        }
    }

    @Test
    @Tag("SlowTest")
    void testAllPairsOfMolecules() throws Exception {
        // symmetry, permutation, deletion and stereochemistry checked for each pair once, the query alternately the
        // first and the second
        Random random = new Random(1234567L);
        for (int i = 0; i < MOLECULES.length; i++) {
            for (int j = i; j < MOLECULES.length; j++) {
                IAtomContainer a = smi(MOLECULES[(i + j) % 2 == 0 ? i : j]);
                IAtomContainer b = smi(MOLECULES[(i + j) % 2 == 0 ? j : i]);
                if (i != j) {
                    assertSymmetric(a, b);
                }
                assertPermutationInvariant(a, b, true, random);
                assertPermutationInvariant(a, b, false, random);
                assertDeletionMonotone(a, b);
                assertNoStereo(a, b);
            }
        }
    }

    @Test
    @Tag("SlowTest")
    void testCompleteRingsRelations() throws Exception {
        Random random = new Random(2610L);
        for (int i = 0; i < MOLECULES.length; i++) {
            for (int j = i + 1; j < MOLECULES.length; j++) {
                assertCompleteRingsRelations(smi(MOLECULES[i]), smi(MOLECULES[j]), random);
            }
        }
        // on itself, every atom and as many mappings as without the option (M4)
        for (String smiles : new String[]{"C12C3C4C1C5C2C3C45", "C1C2CC3CC1CC(C2)C3", "C1CC2CCC1CC2", MCSTest.C60}) {
            IAtomContainer mol = smi(smiles);
            Assertions.assertEquals(keys(MCS.find(mol).matchAll(mol)),
                                    keys(MCS.find(mol).withCompleteRings().matchAll(mol)));
            assertCompleteRingsRelations(mol, smi(smiles), random);
        }
    }

    @Test
    @Tag("SlowTest")
    void testSubstructureAllPairs() throws Exception {
        Random random = new Random(55555L);
        for (String smiles : MOLECULES) {
            IAtomContainer mol = smi(smiles);
            for (int k = 0; k < 6; k++) {
                IAtomContainer fragment = fragment(mol, random, 1 + random.nextInt(Math.min(18, mol.getAtomCount())));
                Assertions.assertTrue(assertSubstructure(fragment, mol));
                IAtomContainer changed = fragment.clone();
                mutate(changed, random);
                for (String other : MOLECULES) {
                    assertSubstructure(fragment, smi(other));
                    assertSubstructure(changed, smi(other));
                }
            }
        }
    }

    @Test
    @Tag("SlowTest")
    void testRandomGraphSweep() throws Exception {
        Random random = new Random(20261004L);
        for (int i = 0; i < 500; i++) {
            IAtomContainer a = randomGraph(random, 1 + random.nextInt(12), 0.25, CNO);
            IAtomContainer b = randomGraph(random, 1 + random.nextInt(12), 0.25, CNO);
            assertSelf(a, true);
            assertSelf(a, false);
            if (i % 3 == 0) {
                deleteBonds(b, random);
            }
            assertSymmetric(a, b);
            assertPermutationInvariant(a, b, true, random);
            assertPermutationInvariant(a, b, false, random);
            assertDeletionMonotone(a, b);
            IAtomContainer query = fragment(b, random, 1 + random.nextInt(b.getAtomCount()));
            Assertions.assertTrue(assertSubstructure(query, b));
            assertSubstructure(query, a);
        }
    }

    // stereochemistry

    @Test
    @Tag("SlowTest")
    void testStereochemistryRandom() throws Exception {
        Random random = new Random(20261006L);
        int changed = 0;
        for (String a : CHIRAL) {
            for (String b : CHIRAL) {
                for (int k = 0; k < 3; k++) {
                    changed += assertStereo(withConfigurations(smi(a), random), withConfigurations(smi(b), random));
                }
            }
        }
        for (int i = 0; i < 300; i++) {
            IAtomContainer a = withConfigurations(randomGraph(random, 2 + random.nextInt(5), 0.25, CNO), random);
            changed += assertStereo(a, withConfigurations(randomGraph(random, 2 + random.nextInt(5), 0.25, CNO),
                                                          random));
        }
        // the configurations leave out atoms often enough to be tested
        Assertions.assertTrue(changed > 50, changed + " searches");
        // without configurations nothing changes
        for (int i = 0; i + 1 < MOLECULES.length; i += 3) {
            assertNoStereo(smi(MOLECULES[i]), smi(MOLECULES[i + 1]));
        }
    }

    @Test
    @Tag("SlowTest")
    void testStereochemistryWithCompleteRings() throws Exception {
        Random random = new Random(61006L);
        for (int i = 0; i < 200; i++) {
            double density = DENSITIES[1 + i % 3];
            IAtomContainer a = withConfigurations(randomGraph(random, 2 + random.nextInt(6), density, CNO), random);
            IAtomContainer b = withConfigurations(randomGraph(random, 2 + random.nextInt(6), density, CNO), random);
            for (boolean orders : new boolean[]{true, false}) {
                assertRingsAndStereo(a, b, orders);
                assertRingsAndStereo(b, a, orders);
            }
        }
    }

    // checks of any search

    /** The mappings of one search and the score they share. */
    private static final class Result {

        final List<int[]> mappings;
        final Set<String> keys;
        final int         atoms;
        final int         bonds;

        Result(List<int[]> mappings, int atoms, int bonds) {
            this.mappings = mappings;
            this.keys = keys(mappings);
            this.atoms = atoms;
            this.bonds = bonds;
        }

        /** Whether there may be more maximum mappings than were returned. */
        boolean capped() {
            return mappings.size() >= CAP;
        }

        /** Compares the scores: atoms, then common bonds. */
        int compareScore(Result other) {
            return atoms != other.atoms ? Integer.compare(atoms, other.atoms) : Integer.compare(bonds, other.bonds);
        }
    }

    // the default matching (orders), or ELEMENT
    private static MCS mcs(IAtomContainer query, boolean orders) {
        return orders ? MCS.find(query) : MCS.find(query).withMatching(ELEMENT);
    }

    /**
     * Every maximum mapping, checked for what holds for any search: the
     * mappings are distinct and valid, have the same score, keep to the
     * bounds, and match returns one of them. The query is the plain molecule
     * in the atom order of the search's query.
     */
    private static Result search(MCS mcs, IAtomContainer query, IAtomContainer target, boolean orders)
            throws Intractable {
        List<int[]> mappings = mcs.matchAll(target);
        Assertions.assertTrue(mappings.size() <= CAP, "at most 1000 mappings");
        Assertions.assertEquals(mappings.size(), keys(mappings).size(), "distinct mappings");
        int atoms = mappings.isEmpty() ? 0 : atoms(mappings.get(0));
        int bonds = mappings.isEmpty() ? 0 : commonBonds(query, target, mappings.get(0), orders);
        for (int[] mapping : mappings) {
            assertMaximum(query, target, mapping, atoms, bonds, orders);
        }
        Assertions.assertTrue(atoms <= Math.min(query.getAtomCount(), target.getAtomCount()), "atoms in both");
        Assertions.assertTrue(bonds <= Math.min(query.getBondCount(), target.getBondCount()), "bonds in both");
        // the mapped atoms are connected, so they lie in one part of each input
        Assertions.assertTrue(atoms <= Math.min(largestPart(query), largestPart(target)), "atoms in one part");
        if (atoms > 0) {
            Assertions.assertTrue(bonds >= atoms - 1, "common bonds connect the mapped atoms");
        }
        // one atom, or one bond, in common is a common substructure
        Assertions.assertEquals(sharedAtom(query, target), atoms > 0, "an atom in common");
        if (sharedBond(query, target, orders)) {
            Assertions.assertTrue(atoms >= 2, "a bond in common");
        }
        Result result = new Result(mappings, atoms, bonds);
        int[] one = mcs.match(target);
        if (mappings.isEmpty()) {
            Assertions.assertEquals(0, one.length, "match finds nothing either");
        } else if (!result.capped()) {
            Assertions.assertTrue(result.keys.contains(Arrays.toString(one)), "match returns one of the mappings");
        } else {
            assertMaximum(query, target, one, atoms, bonds, orders);
        }
        return result;
    }

    // a valid mapping with this score; with the score of the maximum it is a maximum mapping
    private static void assertMaximum(IAtomContainer query, IAtomContainer target, int[] mapping, int atoms,
                                      int bonds, boolean orders) {
        MCSTesting.assertValid(query, target, mapping, orders);
        Assertions.assertEquals(atoms, atoms(mapping), "mapped atoms");
        Assertions.assertEquals(bonds, commonBonds(query, target, mapping, orders), "common bonds");
    }

    // exactness

    /** Every ordered pair of the graphs, two copies so that a query is never the same object as its target. */
    private static void assertAllPairs(Supplier<List<IAtomContainer>> graphs) throws Intractable {
        List<IAtomContainer> targets = graphs.get();
        for (IAtomContainer query : graphs.get()) {
            assertPairs(query, targets);
        }
    }

    // the limits of the default matching and of ELEMENT
    private static void assertLimits(IAtomContainer query, IAtomContainer target) throws Intractable {
        MCSTesting.assertLimits(mcs(query, true), target, exact(query, target, true));
        MCSTesting.assertLimits(mcs(query, false), target, exact(query, target, false));
    }

    /** {@code perStratum} random pairs for each pair of sizes from 2 to 7, each density and each set of elements. */
    private static void assertRandomPairs(Random random, int perStratum) throws Intractable {
        for (int n = 2; n <= 7; n++) {
            for (int m = 2; m <= 7; m++) {
                for (double density : DENSITIES) {
                    for (String[] elements : new String[][]{CARBON, CNO}) {
                        for (int k = 0; k < perStratum; k++) {
                            IAtomContainer query = randomGraph(random, n, density, elements);
                            assertPairs(query, Collections.singletonList(randomGraph(random, m, density, elements)));
                        }
                    }
                }
            }
        }
    }

    /** The default matching, withMatching(ELEMENT) and a query molecule, each reused for every target. */
    private static void assertPairs(IAtomContainer query, List<IAtomContainer> targets) throws Intractable {
        MCS byOrder = mcs(query, true);
        MCS byElement = mcs(query, false);
        // its atoms are in the order of the plain query, so the mappings compare
        MCS queryMolecule = MCS.find(QueryAtomContainer.create(query, ELEMENT));
        for (IAtomContainer target : targets) {
            Set<String> exact = exact(query, target, true);
            assertExact("default matching", query, target, true, byOrder, exact);
            // with single bonds only, bond orders make no difference
            if (!singleBonds(query) || !singleBonds(target)) {
                exact = exact(query, target, false);
            }
            assertExact("withMatching(ELEMENT)", query, target, false, byElement, exact);
            assertExact("QueryAtomContainer.create(query, ELEMENT)", query, target, false, queryMolecule, exact);
        }
    }

    /**
     * Random molecules with hydrogens, pseudo atoms, single, double, triple
     * and aromatic bonds, and some bonds left out so that some are in parts,
     * see {@link #assertMolecules}.
     */
    private static void assertRandomMolecules(Random random, int pairs, int maxAtoms) throws Exception {
        for (int i = 0; i < pairs; i++) {
            IAtomContainer a = randomMolecule(random, 1 + random.nextInt(maxAtoms));
            IAtomContainer b = randomMolecule(random, 1 + random.nextInt(maxAtoms));
            assertMolecules(a, b);
        }
    }

    /**
     * The default matching both ways round, ELEMENT, a query molecule, and no
     * matching types, against the brute force search; the inputs are left as
     * they were.
     */
    private static void assertMolecules(IAtomContainer a, IAtomContainer b) throws Exception {
        String before = snapshot(a) + " / " + snapshot(b);
        assertExact("default matching", a, b, true, MCS.find(a), exact(a, b, true));
        assertExact("default matching", b, a, true, MCS.find(b), exact(b, a, true));
        // a pseudo atom matches any atom with withMatching, see QueryAtomContainer.create
        if (!hasPseudoAtom(a)) {
            Set<String> exact = exact(a, b, false);
            assertExact("withMatching(ELEMENT)", a, b, false, mcs(a, false), exact);
            assertExact("query molecule", a, b, false, MCS.find(QueryAtomContainer.create(a, ELEMENT)), exact);
        }
        // without types every atom and bond matches: the brute force search of all-carbon copies, kept
        // to small pairs as most atom pairs then match
        if (a.getAtomCount() <= 6 && b.getAtomCount() <= 6) {
            Set<String> exact = exact(carbons(a), carbons(b), false);
            MCS any = MCS.find(a).withMatching();
            List<int[]> mappings = any.matchAll(b);
            Assertions.assertEquals(exact, keys(mappings), before);
            Assertions.assertEquals(exact.size(), mappings.size(), before);
            int[] one = any.match(b);
            Assertions.assertTrue(exact.isEmpty() ? one.length == 0 : exact.contains(Arrays.toString(one)),
                                  before);
        }
        Assertions.assertEquals(before, snapshot(a) + " / " + snapshot(b), "inputs unchanged");
    }

    /** Checks a search against the {@code exact} set of maximum mappings, and as any search. */
    private static void assertExact(String matching, IAtomContainer query, IAtomContainer target, boolean orders,
                                    MCS mcs, Set<String> exact) throws Intractable {
        try {
            Result result = search(mcs, query, target, orders);
            if (exact.size() < CAP) {
                Assertions.assertEquals(exact, result.keys);
            } else {
                // which 1000 are returned is not fixed; each is a maximum mapping, so has the maximum score
                Assertions.assertEquals(CAP, result.mappings.size());
                Assertions.assertTrue(exact.containsAll(result.keys));
                Assertions.assertTrue(exact.contains(Arrays.toString(mcs.match(target))), "match");
            }
        } catch (AssertionError e) {
            throw new AssertionError(matching + "\nquery  " + snapshot(query) + "\ntarget " + snapshot(target), e);
        }
    }

    /**
     * Random pairs of C, N and O, or of carbon only, with up to
     * {@code maxAtoms} atoms, some in parts, see {@link #assertEdges}, for
     * fragments of 1 to {@code maxBonds} bonds.
     */
    private static void assertEdgePairs(Random random, int pairs, int maxAtoms, int maxBonds) throws Intractable {
        for (int i = 0; i < pairs; i++) {
            double density = DENSITIES[i % DENSITIES.length];
            String[] elements = i / DENSITIES.length % 2 == 0 ? CNO : CARBON;
            IAtomContainer a = randomGraph(random, 1 + random.nextInt(maxAtoms), density, elements);
            IAtomContainer b = randomGraph(random, 1 + random.nextInt(maxAtoms), density, elements);
            if (i % 3 == 0) {
                deleteBonds(a, random);
            }
            if (i % 4 == 1) {
                deleteBonds(b, random);
            }
            for (int minBonds = 1; minBonds <= maxBonds; minBonds++) {
                assertEdges(a, b, minBonds);
            }
        }
    }

    /**
     * MCES by the default matching and by ELEMENT: the brute force set, as
     * many as asked for, each once, see {@link MCSTesting#assertLimits}, and
     * the same set turned round when the query and target swap.
     */
    private static void assertEdges(IAtomContainer a, IAtomContainer b, int minBonds) throws Intractable {
        for (boolean orders : new boolean[]{true, false}) {
            Set<String> exact = bruteForceEdges(a, b, orders, minBonds);
            MCSTesting.assertLimits(mcs(a, orders).withDisconnected(minBonds), b, exact);
            List<int[]> swapped = mcs(b, orders).withDisconnected(minBonds).matchAll(a, exact.size() + 1);
            Assertions.assertEquals(exact, inverses(swapped, a.getAtomCount()), "swapped");
        }
    }

    /**
     * The relations of MCES that need no brute force, by the default
     * matching, for fragments of 1 to 3 bonds: the score falls as the size
     * grows, is at most the bonds of either molecule, has at least the bonds
     * of the connected MCS if that is a fragment, and does not rise when a
     * target atom is deleted; a connected query that fits whole has its
     * embeddings, and a query whose parts have enough bonds maps whole onto
     * itself; renumbering permutes the mappings, and a time limit leaves them
     * as they are. Disjoint unions add their common bonds.
     */
    private static void assertEdgeRelations(IAtomContainer a, IAtomContainer b, Random random) throws Exception {
        List<int[]> connected = MCS.find(a).matchAll(b);
        int atoms = connected.isEmpty() ? 0 : atoms(connected.get(0));
        int bonds = connected.isEmpty() ? 0 : commonBonds(a, b, connected.get(0), true);
        long last = Long.MAX_VALUE;
        for (int k = 1; k <= 3; k++) {
            MCS mcs = MCS.find(a).withDisconnected(k);
            List<int[]> mappings = mcs.matchAll(b);
            long score = edgeScore(a, b, k, mappings);
            Assertions.assertTrue(score <= last, "a larger size, no better score");
            last = score;
            Assertions.assertTrue(score >> 32 <= Math.min(a.getBondCount(), b.getBondCount()), "bonds of both");
            if (bonds >= k) {
                Assertions.assertTrue(score >> 32 >= bonds, "the connected MCS is a fragment");
            }
            if (atoms == a.getAtomCount() && bonds == a.getBondCount() && bonds >= k
                && largestPart(a) == atoms && connected.size() < CAP) {
                Assertions.assertEquals(keys(connected), keys(mappings), "the embeddings");
            }
            long whole = MCSTesting.score(a, a, identity(a.getAtomCount()), true, k);
            if (whole >= 0) {
                IAtomContainer copy = a.clone();
                Assertions.assertEquals(whole, edgeScore(a, copy, k, mcs.matchAll(copy)), "itself");
            }
            IAtomContainer smaller = b.clone();
            smaller.removeAtom(smaller.getAtom(random.nextInt(b.getAtomCount())));
            Assertions.assertTrue(edgeScore(a, smaller, k, mcs.matchAll(smaller)) <= score, "a target atom deleted");
            if (mappings.size() < CAP) {
                Assertions.assertEquals(keys(mappings), keys(mcs.withTimeout(1, TimeUnit.MINUTES).matchAll(b)));
                IAtomContainer q = a.clone();
                IAtomContainer t = b.clone();
                int[] queryOrder = MCSTesting.shuffle(q, random);
                int[] targetOrder = MCSTesting.shuffle(t, random);
                Set<String> back = new HashSet<>();
                for (int[] mapping : MCS.find(q).withDisconnected(k).matchAll(t)) {
                    back.add(Arrays.toString(MCSTesting.unshuffle(mapping, queryOrder, targetOrder)));
                }
                Assertions.assertEquals(keys(mappings), back, "renumbered");
            }
        }
        IAtomContainer ab = a.clone();
        ab.add(b.clone());
        IAtomContainer ba = b.clone();
        ba.add(a.clone());
        long both = edgeScore(ab, ba, 1, MCS.find(ab).withDisconnected(1).matchAll(ba));
        Assertions.assertTrue(both >> 32 >= 2 * (edgeScore(a, b, 1, MCS.find(a).withDisconnected(1).matchAll(b)) >> 32),
                              "disjoint unions add");
    }

    // the score of the MCES mappings, each valid and distinct, see MCSTesting#score; -1 if there are none
    private static long edgeScore(IAtomContainer query, IAtomContainer target, int minBonds, List<int[]> mappings) {
        long score = mappings.isEmpty() ? -1 : MCSTesting.score(query, target, mappings.get(0), true, minBonds);
        Assertions.assertTrue(mappings.isEmpty() || score >= 0, "valid");
        for (int[] mapping : mappings) {
            Assertions.assertEquals(score, MCSTesting.score(query, target, mapping, true, minBonds), "the same score");
        }
        Assertions.assertEquals(mappings.size(), keys(mappings).size(), "distinct");
        return score;
    }

    /**
     * Random pairs of C, N and O with configurations, some in parts, with
     * every subset of the options, see {@link #assertOptions}.
     */
    private static void assertOptionCombinations(Random random, int pairs, int maxAtoms) throws Exception {
        for (int i = 0; i < pairs; i++) {
            double density = DENSITIES[1 + i % 3];
            IAtomContainer a = randomGraph(random, 2 + random.nextInt(maxAtoms - 1), density, CNO);
            IAtomContainer b = randomGraph(random, 2 + random.nextInt(maxAtoms - 1), density, CNO);
            if (i % 3 == 0) {
                deleteBonds(a, random);
            }
            if (i % 4 == 1) {
                deleteBonds(b, random);
            }
            withConfigurations(a, random);
            withConfigurations(b, random);
            for (boolean orders : new boolean[]{true, false}) {
                assertOptions(a, b, orders, random);
                assertOptions(b, a, orders, random);
            }
        }
    }

    /**
     * Complete rings, stereochemistry and fragments of one or two bonds, in
     * every combination, added in a random order: the brute force set with the
     * same options and its limits, the same set turned round when query and
     * target swap, and no better score than with an option left out or with
     * smaller fragments; the inputs are left as they were.
     */
    private static void assertOptions(IAtomContainer query, IAtomContainer target, boolean orders, Random random)
            throws Exception {
        String before = snapshot(query) + " / " + snapshot(target);
        long[] scores = new long[12];
        for (int minBonds = 0; minBonds <= 2; minBonds++) {
            for (int options = 0; options < 4; options++) {
                boolean rings = (options & 1) != 0;
                boolean stereo = (options & 2) != 0;
                MCS mcs = withOptions(mcs(query, orders), rings, stereo, minBonds, random);
                Set<String> exact = MCSTesting.bruteForce(query, target, orders, rings, stereo, minBonds);
                MCSTesting.assertLimits(mcs, target, exact);
                MCS back = withOptions(mcs(target, orders), rings, stereo, minBonds, random);
                Assertions.assertEquals(exact, inverses(back.matchAll(query, exact.size() + 1), query.getAtomCount()),
                                        "swapped");
                long score = exact.isEmpty() ? -1
                                             : MCSTesting.score(query, target, mcs.match(target), orders, minBonds);
                scores[4 * minBonds + options] = score;
                for (int fewer : new int[]{options & ~1, options & ~2}) {
                    if (fewer != options) {
                        Assertions.assertTrue(score <= scores[4 * minBonds + fewer], "no better score with an option");
                    }
                }
                if (minBonds == 2) {
                    Assertions.assertTrue(score <= scores[4 + options], "no better score with larger fragments");
                }
            }
        }
        Assertions.assertEquals(before, snapshot(query) + " / " + snapshot(target), "inputs unchanged");
    }

    // the options in a random order, each a new search that keeps the others
    private static MCS withOptions(MCS mcs, boolean rings, boolean stereo, int minBonds, Random random) {
        int first = random.nextInt(3), step = 1 + random.nextInt(2);
        for (int i = 0; i < 3; i++) {
            int option = (first + i * step) % 3;
            if (option == 0 && rings) {
                mcs = mcs.withCompleteRings();
            } else if (option == 1 && stereo) {
                mcs = mcs.withStereochemistry();
            } else if (option == 2 && minBonds > 0) {
                mcs = mcs.withDisconnected(minBonds);
            }
        }
        return mcs;
    }

    // metamorphic relations

    // complete rings: the brute force mappings and the limits, and no better score than without the option, the
    // same one only if some of those mappings are complete, which are then the mappings (M1); the inputs are left
    // as they were
    private static void assertCompleteRings(IAtomContainer query, IAtomContainer target, boolean orders)
            throws Exception {
        String before = snapshot(query) + " / " + snapshot(target);
        MCS mcs = mcs(query, orders).withCompleteRings();
        MCSTesting.assertLimits(mcs, target, MCSTesting.bruteForce(query, target, orders, true));
        List<int[]> mappings = mcs.matchAll(target);
        Result without = search(mcs(query, orders), query, target, orders);
        Set<String> complete = new HashSet<>();
        for (int[] mapping : without.mappings) {
            if (MCSTesting.complete(query, target, mapping, orders)) {
                complete.add(Arrays.toString(mapping));
            }
        }
        int score = mappings.isEmpty() ? -1 : new Result(mappings, atoms(mappings.get(0)),
                commonBonds(query, target, mappings.get(0), orders)).compareScore(without);
        Assertions.assertTrue(score <= 0, "no better score");
        if (!without.capped()) {
            Assertions.assertEquals(score == 0 ? keys(mappings) : Collections.emptySet(), complete, "M1");
        }
        Assertions.assertEquals(before, snapshot(query) + " / " + snapshot(target), "inputs unchanged");
    }

    // complete rings: the mappings turned round when the molecules are swapped (M2), or renumbered (M5)
    private static void assertCompleteRingsRelations(IAtomContainer a, IAtomContainer b, Random random)
            throws Exception {
        Set<String> expected = keys(MCS.find(a).withCompleteRings().matchAll(b));
        Assertions.assertTrue(expected.size() < CAP);
        Assertions.assertEquals(expected, MCSTesting.inverses(MCS.find(b).withCompleteRings().matchAll(a),
                                                              a.getAtomCount()), "M2");
        for (int k = 0; k < 5; k++) {
            IAtomContainer query = a.clone();
            IAtomContainer target = b.clone();
            int[] queryOrder = MCSTesting.shuffle(query, random);
            int[] targetOrder = MCSTesting.shuffle(target, random);
            Set<String> found = new HashSet<>();
            for (int[] mapping : MCS.find(query).withCompleteRings().matchAll(target)) {
                found.add(Arrays.toString(MCSTesting.unshuffle(mapping, queryOrder, targetOrder)));
            }
            Assertions.assertEquals(expected, found, "M5");
        }
    }

    private static void assertSelf(IAtomContainer mol, boolean orders) throws Exception {
        Result self = search(mcs(mol, orders), mol, mol, orders);
        Assertions.assertEquals(mol.getAtomCount(), self.atoms, "every atom");
        Assertions.assertEquals(mol.getBondCount(), self.bonds, "every bond");
        // a new search, with a time limit, of a copy
        Result copy = search(mcs(mol, orders).withTimeout(1, TimeUnit.MINUTES), mol, mol.clone(), orders);
        Assertions.assertEquals(self.mappings.size(), copy.mappings.size());
        if (self.capped()) {
            return;
        }
        Assertions.assertEquals(self.keys, copy.keys);
        // the mappings are the automorphisms: they include the identity and are closed under inverse and composition
        int n = mol.getAtomCount();
        Assertions.assertTrue(self.keys.contains(Arrays.toString(identity(n))), "identity");
        for (int[] p : self.mappings) {
            Assertions.assertTrue(self.keys.contains(Arrays.toString(inverse(p, n))), "inverse");
            for (int[] q : self.mappings) {
                int[] pq = new int[n];
                for (int i = 0; i < n; i++) {
                    pq[i] = p[q[i]];
                }
                Assertions.assertTrue(self.keys.contains(Arrays.toString(pq)), "composition");
            }
        }
    }

    // the symmetry relation for the default matching and ELEMENT; the default is stricter, so ELEMENT scores no lower
    private static void assertSymmetric(IAtomContainer a, IAtomContainer b) throws Exception {
        Result byDefault = assertSymmetric(a, b, true);
        Result byElement = assertSymmetric(a, b, false);
        Assertions.assertTrue(byElement.compareScore(byDefault) >= 0, "ELEMENT matches at least as much");
    }

    private static Result assertSymmetric(IAtomContainer a, IAtomContainer b, boolean orders) throws Exception {
        Result ab = search(mcs(a, orders), a, b, orders);
        Result ba = search(mcs(b, orders), b, a, orders);
        Assertions.assertEquals(ab.atoms, ba.atoms, "atoms both ways");
        Assertions.assertEquals(ab.bonds, ba.bonds, "common bonds both ways");
        // there are as many maximum mappings each way, so as many are returned
        Assertions.assertEquals(ab.mappings.size(), ba.mappings.size(), "mappings both ways");
        List<int[]> inverted = new ArrayList<>();
        for (int[] mapping : ba.mappings) {
            inverted.add(inverse(mapping, a.getAtomCount()));
        }
        if (ab.capped()) {
            for (int[] mapping : inverted) {
                assertMaximum(a, b, mapping, ab.atoms, ab.bonds, orders);
            }
        } else {
            Assertions.assertEquals(ab.keys, keys(inverted), "inverse mappings");
        }
        return ab;
    }

    /**
     * The substructure relation, for the default matching; returns whether the
     * query is a substructure of the target. The query must have no pseudo
     * atoms, which ELEMENT would match to any atom.
     */
    private static boolean assertSubstructure(IAtomContainer query, IAtomContainer target) throws Exception {
        // atoms by element, aromatic bonds to aromatic bonds and other bonds
        // to non-aromatic bonds of the same order: the default matching
        Pattern pattern = Pattern.findSubstructure(QueryAtomContainer.create(query, ELEMENT, SINGLE_OR_AROMATIC));
        boolean matches = pattern.matches(target);
        Result result = search(MCS.find(query), query, target, true);
        boolean whole = result.atoms == query.getAtomCount() && result.bonds == query.getBondCount();
        if (matches && largestPart(query) == query.getAtomCount()) {
            Assertions.assertTrue(whole, "a connected substructure is mapped whole");
        }
        if (whole) {
            Assertions.assertTrue(matches, "a query mapped whole is a substructure");
            // every embedding has the most atoms and bonds there can be, so the maximum mappings are the embeddings
            int[][] embeddings = pattern.matchAll(target).limit(CAP).toArray();
            if (embeddings.length < CAP) {
                Assertions.assertEquals(keys(Arrays.asList(embeddings)), result.keys, "embeddings");
            } else {
                Assertions.assertEquals(CAP, result.mappings.size());
            }
        }
        return matches;
    }

    private static void assertPermutationInvariant(IAtomContainer query, IAtomContainer target, boolean orders,
                                                   Random random) throws Exception {
        Result expected = search(mcs(query, orders), query, target, orders);
        // shuffle the query, the target, and both
        for (int k = 0; k < 3; k++) {
            IAtomContainer q = query.clone();
            IAtomContainer t = target.clone();
            int[] queryOrder = k == 1 ? identity(q.getAtomCount()) : MCSTesting.shuffle(q, random);
            int[] targetOrder = k == 0 ? identity(t.getAtomCount()) : MCSTesting.shuffle(t, random);
            Result actual = search(mcs(q, orders), q, t, orders);
            Assertions.assertEquals(0, expected.compareScore(actual), "score after shuffling");
            Assertions.assertEquals(expected.mappings.size(), actual.mappings.size(), "mappings after shuffling");
            List<int[]> back = new ArrayList<>();
            for (int[] mapping : actual.mappings) {
                back.add(MCSTesting.unshuffle(mapping, queryOrder, targetOrder));
            }
            if (expected.capped()) {
                for (int[] mapping : back) {
                    assertMaximum(query, target, mapping, expected.atoms, expected.bonds, orders);
                }
            } else {
                Assertions.assertEquals(expected.keys, keys(back), "mappings after shuffling");
            }
        }
    }

    /** The monotonicity relation, for the default matching: delete each target atom in turn. */
    private static void assertDeletionMonotone(IAtomContainer query, IAtomContainer target) throws Exception {
        Result before = search(MCS.find(query), query, target, true);
        for (int v = 0; v < target.getAtomCount(); v++) {
            IAtomContainer smaller = target.clone();
            smaller.removeAtom(smaller.getAtom(v));
            Result after = search(MCS.find(query), query, smaller, true);
            Assertions.assertTrue(after.compareScore(before) <= 0, "no better score");
            if (target.getConnectedBondsCount(target.getAtom(v)) == 1) {
                // its image is a leaf of the common substructure, which can be left out
                Assertions.assertTrue(after.atoms >= before.atoms - 1, "at most one atom fewer");
            }
            // the maximum mappings that leave the atom out are still mappings, and the
            // maximum mappings into the smaller target are mappings into the target
            Set<String> avoiding = new HashSet<>();
            for (int[] mapping : before.mappings) {
                int[] shifted = shift(mapping, v);
                if (shifted != null) {
                    avoiding.add(Arrays.toString(shifted));
                }
            }
            if (!avoiding.isEmpty()) {
                Assertions.assertEquals(0, after.compareScore(before), "the same score");
                if (!before.capped()) {
                    Assertions.assertEquals(avoiding, after.keys, "the mappings that leave the atom out");
                }
            } else if (!before.capped() && !before.mappings.isEmpty()) {
                Assertions.assertTrue(after.compareScore(before) < 0, "every maximum mapping used the atom");
            }
            if (after.compareScore(before) == 0) {
                for (int[] mapping : after.mappings) {
                    int[] unshifted = new int[mapping.length];
                    for (int i = 0; i < mapping.length; i++) {
                        unshifted[i] = mapping[i] >= v ? mapping[i] + 1 : mapping[i];
                    }
                    assertMaximum(query, target, unshifted, before.atoms, before.bonds, true);
                }
            }
        }
    }

    // the mapping with target atom v deleted, null if it maps to v
    private static int[] shift(int[] mapping, int v) {
        int[] shifted = new int[mapping.length];
        for (int i = 0; i < mapping.length; i++) {
            if (mapping[i] == v) {
                return null;
            }
            shifted[i] = mapping[i] > v ? mapping[i] - 1 : mapping[i];
        }
        return shifted;
    }

    private static int[] identity(int size) {
        int[] identity = new int[size];
        for (int i = 0; i < size; i++) {
            identity[i] = i;
        }
        return identity;
    }

    /**
     * With stereochemistry checked, with bond orders and without: the brute
     * force set and its limits, and the relations of MCS#withStereochemistry;
     * returns the number of searches whose mappings it changes.
     */
    private static int assertStereo(IAtomContainer query, IAtomContainer target) throws Exception {
        Random random = new Random(query.getAtomCount() * 31L + target.getAtomCount());
        int changed = 0;
        for (boolean orders : new boolean[]{true, false}) {
            MCS on = mcs(query, orders).withStereochemistry();
            Set<String> exact = MCSTesting.bruteForce(query, target, orders, false, true);
            MCSTesting.assertLimits(on, target, exact);
            Result result = search(on, query, target, orders);
            Result off = search(mcs(query, orders), query, target, orders);
            Assertions.assertTrue(result.compareScore(off) < 0 || off.keys.containsAll(result.keys) || off.capped());
            changed += result.keys.equals(off.keys) ? 0 : 1;
            Assertions.assertEquals(exact, inverses(mcs(target, orders).withStereochemistry().matchAll(query),
                                                    query.getAtomCount()));
            Assertions.assertEquals(exact, keys(mcs(mirror(query.clone()), orders).withStereochemistry()
                                                       .matchAll(mirror(target.clone()))));
            IAtomContainer q = query.clone(), t = target.clone();
            int[] queryOrder = MCSTesting.shuffle(q, random), targetOrder = MCSTesting.shuffle(t, random);
            Set<String> back = new HashSet<>();
            for (int[] mapping : mcs(q, orders).withStereochemistry().matchAll(t)) {
                back.add(Arrays.toString(MCSTesting.unshuffle(mapping, queryOrder, targetOrder)));
            }
            Assertions.assertEquals(exact, back, "renumbered");
            t = target.clone();
            AtomContainerManipulator.convertImplicitToExplicitHydrogens(t);
            Assertions.assertEquals(exact, keys(on.matchAll(t)), "explicit hydrogens on the target");
            t = target.clone();
            int[] group = {0};
            t.stereoElements().forEach(se -> se.setGroupInfo(GRP_RAC | ++group[0] << GRP_NUM_SHIFT));
            Assertions.assertEquals(off.keys, keys(on.matchAll(t)), "every target configuration racemic");
            Result self = search(mcs(query, orders).withStereochemistry(), query, query.clone(), orders);
            Assertions.assertEquals(query.getAtomCount(), self.atoms);
            Assertions.assertTrue(self.mappings.size() <= search(mcs(query, orders), query, query.clone(), orders)
                                                               .mappings.size());
        }
        return changed;
    }

    // with stereochemistry checked or not, the same list in the same order, for molecules without configurations
    private static void assertNoStereo(IAtomContainer query, IAtomContainer target) throws Exception {
        for (boolean orders : new boolean[]{true, false}) {
            Assertions.assertArrayEquals(mcs(query, orders).matchAll(target).toArray(),
                                         mcs(query, orders).withStereochemistry().matchAll(target).toArray());
        }
    }

    // complete rings and stereochemistry together: the brute force set with both and its limits, the options in
    // either order, and no better score than either option alone
    private static void assertRingsAndStereo(IAtomContainer query, IAtomContainer target, boolean orders)
            throws Exception {
        MCS both = mcs(query, orders).withCompleteRings().withStereochemistry();
        Set<String> exact = MCSTesting.bruteForce(query, target, orders, true, true);
        MCSTesting.assertLimits(both, target, exact);
        List<int[]> mappings = mcs(query, orders).withStereochemistry().withCompleteRings().matchAll(target);
        Assertions.assertEquals(exact, keys(mappings), "either order");
        for (MCS alone : new MCS[]{mcs(query, orders).withCompleteRings(), mcs(query, orders).withStereochemistry()}) {
            Assertions.assertTrue(result(query, target, mappings, orders)
                                          .compareScore(result(query, target, alone.matchAll(target), orders)) <= 0);
        }
    }

    // the result of a search that may find nothing
    private static Result result(IAtomContainer query, IAtomContainer target, List<int[]> mappings, boolean orders) {
        return mappings.isEmpty() ? new Result(mappings, 0, 0)
                                  : new Result(mappings, atoms(mappings.get(0)),
                                               commonBonds(query, target, mappings.get(0), orders));
    }

    // the mirror image: each centre inverted, in its group, which setConfigOrder clears
    private static IAtomContainer mirror(IAtomContainer mol) {
        for (IStereoElement<?, ?> se : mol.stereoElements()) {
            int group = se.getGroupInfo();
            if (se.getConfigClass() == IStereoElement.TH) {
                se.setConfigOrder(3 - se.getConfigOrder());
                se.setGroupInfo(group);
            }
        }
        return mol;
    }

    // inputs

    /** The connected graphs with up to {@code maxAtoms} atoms as molecules, new objects on each call. */
    private static List<IAtomContainer> graphsUpTo(int maxAtoms, boolean labelled) {
        List<IAtomContainer> graphs = new ArrayList<>();
        for (int n = 1; n <= maxAtoms; n++) {
            for (int mask : connectedGraphs(n)) {
                graphs.add(labelled ? labelled(n, mask) : graph(n, mask, CARBON));
            }
        }
        return graphs;
    }

    /**
     * A graph of {@link #connectedGraphs(int)} with nitrogen as atom 0,
     * oxygen as atom 3 and its ring bonds aromatic, alternately with single
     * and double order, which the default matching ignores.
     */
    private static IAtomContainer labelled(int n, int mask) {
        String[] symbols = new String[n];
        Arrays.fill(symbols, "C");
        symbols[0] = "N";
        if (n > 3) {
            symbols[3] = "O";
        }
        IAtomContainer mol = graph(n, mask, symbols);
        Cycles.markRingAtomsAndBonds(mol);
        boolean single = true;
        for (IBond bond : mol.bonds()) {
            if (bond.isInRing()) {
                setAromatic(bond);
                bond.setOrder(single ? IBond.Order.SINGLE : IBond.Order.DOUBLE);
                single = !single;
            }
        }
        return mol;
    }

    // a graph with single bonds for the atom pairs in mask; atom i is symbols[i], or symbols[0] if there is one
    private static IAtomContainer graph(int n, int mask, String[] symbols) {
        IAtomContainer mol = BUILDER.newAtomContainer();
        for (int i = 0; i < n; i++) {
            mol.addAtom(BUILDER.newInstance(IAtom.class, symbols[symbols.length == 1 ? 0 : i]));
        }
        int[][] pairs = pairs(n);
        for (int k = 0; k < pairs.length; k++) {
            if ((mask & 1 << k) != 0) {
                mol.addBond(pairs[k][0], pairs[k][1], IBond.Order.SINGLE);
            }
        }
        return mol;
    }

    /** A random molecule: a tree with about a quarter of the other atom pairs, less some bonds. */
    private static IAtomContainer randomMolecule(Random random, int size) {
        String[] symbols = {"C", "C", "C", "N", "O", "H", "R"};
        IBond.Order[] orders = {IBond.Order.SINGLE, IBond.Order.SINGLE, IBond.Order.DOUBLE, IBond.Order.TRIPLE};
        IAtomContainer mol = BUILDER.newAtomContainer();
        for (int i = 0; i < size; i++) {
            String symbol = symbols[random.nextInt(symbols.length)];
            mol.addAtom("R".equals(symbol) ? BUILDER.newInstance(IPseudoAtom.class, "R")
                                           : BUILDER.newInstance(IAtom.class, symbol));
        }
        for (int i = 1; i < size; i++) {
            int parent = random.nextInt(i);
            for (int j = 0; j < i; j++) {
                if ((j == parent || random.nextDouble() < 0.25) && random.nextDouble() > 0.15) {
                    IBond bond = mol.newBond(mol.getAtom(j), mol.getAtom(i), orders[random.nextInt(orders.length)]);
                    if (random.nextDouble() < 0.2) {
                        bond.setOrder(random.nextBoolean() ? IBond.Order.SINGLE : IBond.Order.DOUBLE);
                        bond.setIsAromatic(true);
                    }
                }
            }
        }
        return mol;
    }

    // a copy with the same atom indices and bonds, but all carbon atoms and single bonds
    private static IAtomContainer carbons(IAtomContainer mol) {
        IAtomContainer copy = BUILDER.newAtomContainer();
        for (int i = 0; i < mol.getAtomCount(); i++) {
            copy.addAtom(BUILDER.newInstance(IAtom.class, "C"));
        }
        for (IBond bond : mol.bonds()) {
            copy.addBond(mol.indexOf(bond.getBegin()), mol.indexOf(bond.getEnd()), IBond.Order.SINGLE);
        }
        return copy;
    }

    /**
     * The connected simple graphs with {@code n} atoms, one for each
     * isomorphism class: the subsets of the bonds of the complete graph K_n,
     * written as bit masks over {@link #pairs(int)}, that are connected and
     * are the smallest mask over all orders of the atoms.
     */
    private static synchronized List<Integer> connectedGraphs(int n) {
        List<Integer> graphs = GRAPHS.get(n);
        if (graphs != null) {
            return graphs;
        }
        int[][] pairs = pairs(n);
        int[][] index = new int[n][n];
        for (int k = 0; k < pairs.length; k++) {
            index[pairs[k][0]][pairs[k][1]] = k;
            index[pairs[k][1]][pairs[k][0]] = k;
        }
        List<int[]> orders = new ArrayList<>();
        permutations(new int[n], new boolean[n], 0, orders);
        graphs = new ArrayList<>();
        for (int mask = 0; mask < 1 << pairs.length; mask++) {
            if (connected(n, pairs, mask) && smallest(mask, pairs, index, orders)) {
                graphs.add(mask);
            }
        }
        GRAPHS.put(n, graphs);
        return graphs;
    }

    /** Whether no order of the atoms gives a smaller mask; every class has one such mask. */
    private static boolean smallest(int mask, int[][] pairs, int[][] index, List<int[]> orders) {
        for (int[] order : orders) {
            int image = 0;
            for (int k = 0; k < pairs.length; k++) {
                if ((mask & 1 << k) != 0) {
                    image |= 1 << index[order[pairs[k][0]]][order[pairs[k][1]]];
                }
            }
            if (image < mask) {
                return false;
            }
        }
        return true;
    }

    private static boolean connected(int n, int[][] pairs, int mask) {
        int seen = 1;
        for (boolean grown = true; grown;) {
            grown = false;
            for (int k = 0; k < pairs.length; k++) {
                int u = 1 << pairs[k][0];
                int v = 1 << pairs[k][1];
                if ((mask & 1 << k) != 0 && ((seen & u) != 0) != ((seen & v) != 0)) {
                    seen |= u | v;
                    grown = true;
                }
            }
        }
        return seen == (1 << n) - 1;
    }

    /** The atom pairs (i, j), i &lt; j, in lexicographic order. */
    private static int[][] pairs(int n) {
        int[][] pairs = new int[n * (n - 1) / 2][];
        int k = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                pairs[k++] = new int[]{i, j};
            }
        }
        return pairs;
    }

    private static void permutations(int[] order, boolean[] used, int i, List<int[]> orders) {
        if (i == order.length) {
            orders.add(order.clone());
            return;
        }
        for (int j = 0; j < order.length; j++) {
            if (!used[j]) {
                used[j] = true;
                order[i] = j;
                permutations(order, used, i + 1, orders);
                used[j] = false;
            }
        }
    }

    // delete about a fifth of the bonds, which may leave the graph in parts
    private static IAtomContainer deleteBonds(IAtomContainer graph, Random random) {
        List<IBond> deleted = new ArrayList<>();
        for (IBond bond : graph.bonds()) {
            if (random.nextInt(5) == 0) {
                deleted.add(bond);
            }
        }
        for (IBond bond : deleted) {
            graph.removeBond(bond);
        }
        return graph;
    }

    /**
     * A connected part of the molecule: atoms grown from a random atom along
     * random bonds, with the bonds they were reached by and about half of the
     * other bonds between them.
     */
    private static IAtomContainer fragment(IAtomContainer mol, Random random, int size) throws Exception {
        boolean[] atoms = new boolean[mol.getAtomCount()];
        boolean[] bonds = new boolean[mol.getBondCount()];
        atoms[random.nextInt(atoms.length)] = true;
        for (int count = 1; count < size; count++) {
            List<IBond> frontier = new ArrayList<>();
            for (IBond bond : mol.bonds()) {
                if (atoms[mol.indexOf(bond.getBegin())] != atoms[mol.indexOf(bond.getEnd())]) {
                    frontier.add(bond);
                }
            }
            if (frontier.isEmpty()) {
                break;
            }
            IBond bond = frontier.get(random.nextInt(frontier.size()));
            bonds[mol.indexOf(bond)] = true;
            atoms[mol.indexOf(bond.getBegin())] = true;
            atoms[mol.indexOf(bond.getEnd())] = true;
        }
        IAtomContainer fragment = mol.clone();
        List<IBond> otherBonds = new ArrayList<>();
        for (int i = 0; i < bonds.length; i++) {
            IBond bond = mol.getBond(i);
            boolean inside = atoms[mol.indexOf(bond.getBegin())] && atoms[mol.indexOf(bond.getEnd())];
            if (!bonds[i] && (!inside || random.nextBoolean())) {
                otherBonds.add(fragment.getBond(i));
            }
        }
        List<IAtom> otherAtoms = new ArrayList<>();
        for (int i = 0; i < atoms.length; i++) {
            if (!atoms[i]) {
                otherAtoms.add(fragment.getAtom(i));
            }
        }
        for (IBond bond : otherBonds) {
            fragment.removeBond(bond);
        }
        for (IAtom atom : otherAtoms) {
            fragment.removeAtom(atom);
        }
        return fragment;
    }

    // change the element of an atom, or the order of a bond
    private static void mutate(IAtomContainer mol, Random random) {
        if (mol.getBondCount() > 0 && random.nextBoolean()) {
            IBond bond = mol.getBond(random.nextInt(mol.getBondCount()));
            if (bond.isAromatic()) {
                bond.setIsAromatic(false);
                bond.setOrder(IBond.Order.SINGLE);
            } else {
                bond.setOrder(bond.getOrder() == IBond.Order.SINGLE ? IBond.Order.DOUBLE : IBond.Order.SINGLE);
            }
        } else {
            IAtom atom = mol.getAtom(random.nextInt(mol.getAtomCount()));
            String next = "C".equals(atom.getSymbol()) ? "N" : "N".equals(atom.getSymbol()) ? "O" : "C";
            atom.setSymbol(next);
            atom.setAtomicNumber("C".equals(next) ? 6 : "N".equals(next) ? 7 : 8);
        }
    }

    // properties of the inputs

    // the number of atoms in the largest connected part
    private static int largestPart(IAtomContainer mol) {
        int[] sizes = new int[mol.getAtomCount() + 1];
        int largest = 0;
        for (int part : new ConnectedComponents(GraphUtil.toAdjList(mol)).components()) {
            largest = Math.max(largest, ++sizes[part]);
        }
        return largest;
    }

    private static boolean singleBonds(IAtomContainer mol) {
        for (IBond bond : mol.bonds()) {
            if (bond.isAromatic() || bond.getOrder() != IBond.Order.SINGLE) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasPseudoAtom(IAtomContainer mol) {
        for (IAtom atom : mol.atoms()) {
            if (atom instanceof IPseudoAtom) {
                return true;
            }
        }
        return false;
    }

    private static boolean sharedAtom(IAtomContainer a, IAtomContainer b) {
        Set<String> symbols = new HashSet<>();
        for (IAtom atom : a.atoms()) {
            symbols.add(atom.getSymbol());
        }
        for (IAtom atom : b.atoms()) {
            if (symbols.contains(atom.getSymbol())) {
                return true;
            }
        }
        return false;
    }

    // a bond of each with the same symbols at its ends, which with orders match by the default rule
    private static boolean sharedBond(IAtomContainer a, IAtomContainer b, boolean orders) {
        for (IBond x : a.bonds()) {
            for (IBond y : b.bonds()) {
                if (orders && !MCSTesting.bondsMatch(x, y)) {
                    continue;
                }
                String x1 = x.getBegin().getSymbol();
                String x2 = x.getEnd().getSymbol();
                String y1 = y.getBegin().getSymbol();
                String y2 = y.getEnd().getSymbol();
                if ((x1.equals(y1) && x2.equals(y2)) || (x1.equals(y2) && x2.equals(y1))) {
                    return true;
                }
            }
        }
        return false;
    }
}
