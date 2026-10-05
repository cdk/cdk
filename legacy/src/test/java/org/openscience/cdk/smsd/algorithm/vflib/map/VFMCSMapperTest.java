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
package org.openscience.cdk.smsd.algorithm.vflib.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.BruteForceMCS;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;
import org.openscience.cdk.smsd.global.TimeOut;

import static org.openscience.cdk.smsd.BruteForceMCS.completeGraph;
import static org.openscience.cdk.smsd.BruteForceMCS.randomGraph;

/**
 * Checks the connected MCS search of {@link VFMCSMapper}; many tests compare
 * its mappings with a brute force search over all atom mappings.
 *
 * @author Syed Asad Rahman
 */
class VFMCSMapperTest {

    @Test
    void testCyclohexaneHexane() throws Exception {
        // a ring bond is left out, 6 bonds to break and 2 directions
        assertSameAsBruteForce(smi("C1CCCCC1"), smi("CCCCCC"), true);
        Assertions.assertEquals(12, new VFMCSMapper(smi("C1CCCCC1"), true).countMaps(smi("CCCCCC")));
        Assertions.assertEquals(12, new VFMCSMapper(smi("CCCCCC"), true).countMaps(smi("C1CCCCC1")));
    }

    @Test
    void testCyclopentaneCyclohexane() throws Exception {
        // 5 bonds to break in the query, 6 paths in the target and 2 directions
        assertSameAsBruteForce(smi("C1CCCC1"), smi("C1CCCCC1"), true);
        Assertions.assertEquals(60, new VFMCSMapper(smi("C1CCCC1"), true).countMaps(smi("C1CCCCC1")));
    }

    @Test
    void testTiesBrokenByCommonBonds() throws Exception {
        // the triangle and the path both have 3 atoms, the triangle has one more bond
        assertSameAsBruteForce(smi("C1CC1"), smi("C1CC1C"), false);
        Assertions.assertEquals(6, new VFMCSMapper(smi("C1CC1"), false).countMaps(smi("C1CC1C")));
    }

    @Test
    void testBondOrder() throws Exception {
        assertSameAsBruteForce(smi("CC"), smi("C=C"), true);
        assertSameAsBruteForce(smi("CC"), smi("C=C"), false);
        for (Map<INode, IAtom> map : new VFMCSMapper(smi("CC"), true).getMaps(smi("C=C"))) {
            Assertions.assertEquals(1, map.size());
        }
    }

    @Test
    void testDisconnectedQuery() throws Exception {
        // the MCS is connected, the substructure methods map the whole query
        VFMCSMapper mapper = new VFMCSMapper(smi("C.C"), false);
        Assertions.assertTrue(mapper.hasMap(smi("CC")));
        Assertions.assertEquals(2, mapper.getFirstMap(smi("CC")).size());
        Assertions.assertEquals(4, mapper.countMaps(smi("CC")));
        for (Map<INode, IAtom> map : mapper.getMaps(smi("CC"))) {
            Assertions.assertEquals(1, map.size());
        }
        assertSameAsBruteForce(smi("CN.CC"), smi("CC.CC"), false);
        assertSameAsBruteForce(smi("CC.O"), smi("OCC"), true);
    }

    @Test
    void testNoCommonAtoms() throws Exception {
        for (String[] pair : new String[][]{{"", "C"}, {"C", ""}, {"N", "C"}}) {
            List<Map<INode, IAtom>> maps = new VFMCSMapper(smi(pair[0]), false).getMaps(smi(pair[1]));
            Assertions.assertEquals(1, maps.size());
            Assertions.assertTrue(maps.get(0).isEmpty());
        }
    }

    @Test
    void testAllMapsOfCompleteGraphs() {
        List<Map<INode, IAtom>> maps = new VFMCSMapper(completeGraph(6), false).getMaps(completeGraph(4));
        Assertions.assertEquals(360, maps.size());
        Assertions.assertEquals(360, new HashSet<>(maps).size());
    }

    @Test
    void testTooManyMaps() {
        // 2520 equally good maps, only the first MAX_MAPPINGS are kept
        List<Map<INode, IAtom>> maps = new VFMCSMapper(completeGraph(7), false).getMaps(completeGraph(5));
        Assertions.assertEquals(VFMCSMapper.MAX_MAPPINGS, maps.size());
        Assertions.assertEquals(VFMCSMapper.MAX_MAPPINGS, new HashSet<>(maps).size());
        for (Map<INode, IAtom> map : maps) {
            Assertions.assertEquals(5, map.size());
        }
    }

    @Test
    void testMapperReuse() throws Exception {
        VFMCSMapper mapper = new VFMCSMapper(smi("C(C)(C)(C)C"), false);
        List<Map<INode, IAtom>> first = mapper.getMaps(smi("CCCC"));
        Assertions.assertEquals(24, first.size());
        Assertions.assertEquals(12, mapper.countMaps(smi("CCC")));
        Assertions.assertEquals(24, first.size());
    }

    @Test
    void testTimeout() {
        TimeOut timeOut = TimeOut.getInstance();
        double limit = timeOut.getTimeOut();
        try {
            timeOut.setTimeOut(0.00001);
            // far too many maps to list in under a millisecond
            Assertions.assertNotNull(new VFMCSMapper(completeGraph(10), false).getMaps(completeGraph(9)));
            Assertions.assertTrue(timeOut.isTimeOutFlag());
        } finally {
            timeOut.setTimeOut(limit);
            timeOut.setTimeOutFlag(false);
        }
    }

    @Test
    void testTimeoutKeepsAMapping() {
        // an odd ring cannot fit in a grid, so the whole query search runs out of time
        IAtomContainer ring = new AtomContainer();
        for (int i = 0; i < 21; i++) {
            ring.addAtom(new Atom("C"));
            if (i > 0) {
                ring.addBond(i - 1, i, IBond.Order.SINGLE);
            }
        }
        ring.addBond(20, 0, IBond.Order.SINGLE);
        IAtomContainer grid = new AtomContainer();
        for (int i = 0; i < 64; i++) {
            grid.addAtom(new Atom("C"));
            if (i % 8 > 0) {
                grid.addBond(i - 1, i, IBond.Order.SINGLE);
            }
            if (i >= 8) {
                grid.addBond(i - 8, i, IBond.Order.SINGLE);
            }
        }
        TimeOut timeOut = TimeOut.getInstance();
        double limit = timeOut.getTimeOut();
        try {
            timeOut.setTimeOut(0.001);
            List<Map<INode, IAtom>> maps = new VFMCSMapper(ring, false).getMaps(grid);
            Assertions.assertTrue(timeOut.isTimeOutFlag());
            Assertions.assertTrue(maps.get(0).size() > 1);
        } finally {
            timeOut.setTimeOut(limit);
            timeOut.setTimeOutFlag(false);
        }
    }

    @Test
    void testCanonicalRanks() {
        // 3-methylhexane has no symmetry, so each atom keeps its rank when the atoms are renumbered
        int[][] graph = {{1}, {0, 2}, {1, 3, 6}, {2, 4}, {3, 5}, {4}, {2}};
        int[] renumber = {4, 6, 0, 3, 5, 1, 2};
        int[] ranks = VFState.canonicalRanks(graph, sameInvariants(7));
        int[] renumbered = VFState.canonicalRanks(renumber(graph, renumber), sameInvariants(7));
        for (int i = 0; i < 7; i++) {
            Assertions.assertEquals(ranks[i], renumbered[renumber[i]]);
        }
        // the six atoms of a ring look alike, but still get different ranks
        int[][] ring = {{1, 5}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 0}};
        int[] ringRanks = VFState.canonicalRanks(ring, sameInvariants(6));
        Arrays.sort(ringRanks);
        Assertions.assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5}, ringRanks);
    }

    @Test
    void testSearchOrder() throws Exception {
        // testosterone has no symmetry, so its atoms are ranked the same way whatever their order
        IAtomContainer query = smi("CC12CCC3C(CCC4=CC(=O)CCC34C)C1CCC2O");
        IAtomContainer target = smi("CC12CCC3C(CCC4=C3C=CC(=C4)O)C1CCC2O");
        List<IAtom> expected = searchOrder(query, target);
        Random random = new Random(1004L);
        for (int i = 0; i < 4; i++) {
            shuffle(query, random);
            shuffle(target, random);
            Assertions.assertEquals(expected, searchOrder(query, target));
        }
    }

    @Test
    @Timeout(value = 20, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void testAtomOrder() throws Exception {
        // C16F34 against C20F42: the search order is taken from the structure,
        // so a different atom order gives an equally good answer just as quickly
        IAtomContainer query = smi(perfluoroalkane(16));
        IAtomContainer target = smi(perfluoroalkane(20));
        Random random = new Random(20261004L);
        for (int i = 0; i < 4; i++) {
            shuffle(query, random);
            shuffle(target, random);
            List<Map<INode, IAtom>> maps = new VFMCSMapper(query, true).getMaps(target);
            Assertions.assertEquals(VFMCSMapper.MAX_MAPPINGS, maps.size());
            // only one CF3 end of the query can lie on a CF3 end of the target, so one fluorine is left out
            Assertions.assertEquals(49, maps.get(0).size());
        }
    }

    @Test
    void testAllFourAtomGraphs() {
        // every graph on 4 atoms, including disconnected ones, with mixed bond orders
        IAtomContainer[] graphs = new IAtomContainer[64];
        for (int mask = 0; mask < graphs.length; mask++) {
            IAtomContainer graph = new AtomContainer();
            for (int i = 0; i < 4; i++) {
                graph.addAtom(new Atom("C"));
            }
            int edge = 0;
            for (int i = 0; i < 4; i++) {
                for (int j = i + 1; j < 4; j++, edge++) {
                    if ((mask & (1 << edge)) != 0) {
                        graph.addBond(i, j, (edge & 1) == 0 ? IBond.Order.SINGLE : IBond.Order.DOUBLE);
                    }
                }
            }
            graphs[mask] = graph;
        }
        for (int i = 0; i < graphs.length; i++) {
            for (int j = i; j < graphs.length; j++) {
                assertSameAsBruteForce(graphs[i], graphs[j], false);
                assertSameAsBruteForce(graphs[i], graphs[j], true);
            }
        }
    }

    @Test
    void testRandomGraphs() {
        Random random = new Random(75319L);
        for (int i = 0; i < 400; i++) {
            boolean bonds = i % 2 == 0;
            assertSameAsBruteForce(randomGraph(random, 2 + random.nextInt(5), bonds),
                    randomGraph(random, 2 + random.nextInt(5), bonds), bonds);
        }
    }

    private static IAtomContainer smi(String smiles) throws Exception {
        return new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smiles);
    }

    private static String perfluoroalkane(int carbons) {
        StringBuilder smiles = new StringBuilder("F");
        for (int i = 0; i < carbons; i++) {
            smiles.append("C(F)(F)");
        }
        return smiles.append('F').toString();
    }

    private static void shuffle(IAtomContainer container, Random random) {
        List<IAtom> atoms = new ArrayList<>();
        for (IAtom atom : container.atoms()) {
            atoms.add(AtomRef.deref(atom));
        }
        List<IBond> bonds = new ArrayList<>();
        for (IBond bond : container.bonds()) {
            bonds.add(BondRef.deref(bond));
        }
        Collections.shuffle(atoms, random);
        Collections.shuffle(bonds, random);
        container.setAtoms(atoms.toArray(new IAtom[0]));
        container.setBonds(bonds.toArray(new IBond[0]));
    }

    private static int[][] sameInvariants(int size) {
        int[][] invariants = new int[size][];
        for (int i = 0; i < size; i++) {
            invariants[i] = new int[]{6};
        }
        return invariants;
    }

    // atom i becomes atom renumber[i]
    private static int[][] renumber(int[][] graph, int[] renumber) {
        int[][] result = new int[graph.length][];
        for (int i = 0; i < graph.length; i++) {
            result[renumber[i]] = new int[graph[i].length];
            for (int k = 0; k < graph[i].length; k++) {
                result[renumber[i]][k] = renumber[graph[i][k]];
            }
        }
        return result;
    }

    // query atoms from lowest to highest rank in the partial search
    private static List<IAtom> searchOrder(IAtomContainer query, IAtomContainer target) {
        IQuery compiled = new QueryCompiler(query, true).compile();
        List<IAtom> order = new ArrayList<>();
        for (INode node : new VFState(compiled, new TargetProperties(target), true).rankedNodes()) {
            order.add(AtomRef.deref(compiled.getAtom(node)));
        }
        return order;
    }

    private static void assertSameAsBruteForce(IAtomContainer a, IAtomContainer b, boolean bonds) {
        Assertions.assertEquals(BruteForceMCS.mappings(a, b, bonds), mappings(a, b, bonds));
        Assertions.assertEquals(BruteForceMCS.mappings(b, a, bonds), mappings(b, a, bonds));
    }

    private static Set<String> mappings(IAtomContainer query, IAtomContainer target, boolean bonds) {
        IQuery compiled = new QueryCompiler(query, bonds).compile();
        List<Map<INode, IAtom>> maps = new VFMCSMapper(compiled).getMaps(target);
        Assertions.assertEquals(maps.size(), new HashSet<>(maps).size(), "duplicate mappings");
        Set<String> result = new HashSet<>();
        for (Map<INode, IAtom> map : maps) {
            int[] indices = new int[query.getAtomCount()];
            Arrays.fill(indices, -1);
            for (Map.Entry<INode, IAtom> e : map.entrySet()) {
                indices[query.indexOf(compiled.getAtom(e.getKey()))] = target.indexOf(e.getValue());
            }
            result.add(Arrays.toString(indices));
        }
        return result;
    }
}
