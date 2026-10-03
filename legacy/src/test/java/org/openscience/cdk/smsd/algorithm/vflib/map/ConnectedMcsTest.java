/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your container code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;
import org.openscience.cdk.smsd.global.TimeOut;
import org.openscience.cdk.smsd.tools.TimeManager;

class ConnectedMcsTest {

    @Test
    void resultsStayExhaustiveInLargeSearchesAndSurviveReuse() {
        VFMCSMapper mapper = new VFMCSMapper(completeGraph(7), false);
        List<Map<INode, IAtom>> retained = mapper.getMaps(completeGraph(5));
        Assertions.assertEquals(2520, retained.size());
        Assertions.assertEquals(2520, new HashSet<>(retained).size());
        Assertions.assertTrue(retained.stream().allMatch(mapping -> mapping.size() == 5));
        Set<Map<INode, IAtom>> beforeReuse = new HashSet<>(retained);
        mapper.getMaps(completeGraph(4));
        Assertions.assertEquals(beforeReuse, new HashSet<>(retained));
    }

    private static IAtomContainer completeGraph(int size) {
        IAtomContainer graph = new AtomContainer();
        for (int i = 0; i < size; i++) graph.addAtom(new Atom("C"));
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) graph.addBond(i, j, IBond.Order.SINGLE);
        }
        return graph;
    }

    @Test
    void ringOpeningKeepsEveryAtomInBothDirections() throws Exception {
        assertOracle(molecule("C1CCCCC1"), molecule("CCCCCC"), false);
        Assertions.assertEquals(12, new VFMCSMapper(molecule("C1CCCCC1"), false)
                .countMaps(molecule("CCCCCC")));
    }

    @Test
    void maximumAtomsAreTiedByCommonBonds() throws Exception {
        // A triangle and an attached path both contain three atoms; the triangle retains three bonds.
        assertOracle(molecule("C1CC1"), molecule("C1CC1C"), false);
        Assertions.assertEquals(6, new VFMCSMapper(molecule("C1CC1"), false)
                .countMaps(molecule("C1CC1C")));
    }

    @Test
    void partialMappingsStayConnectedWhileWholeQueryMethodsAllowComponents() throws Exception {
        IAtomContainer query = molecule("C.C");
        IAtomContainer target = molecule("CC");
        VFMCSMapper mapper = new VFMCSMapper(query, false);
        Assertions.assertTrue(mapper.hasMap(target));
        Assertions.assertEquals(2, mapper.getFirstMap(target).size());
        Assertions.assertEquals(4, mapper.countMaps(target));
        Assertions.assertTrue(mapper.getMaps(target).stream().allMatch(mapping -> mapping.size() == 1));
        assertOracle(molecule("CN.CC"), molecule("CC.CC"), false);
    }

    @Test
    void strictFirstMethodsRequireEveryQueryAtom() throws Exception {
        VFMCSMapper mapper = new VFMCSMapper(molecule("CCCC"), false);
        IAtomContainer target = molecule("CCC");
        Assertions.assertFalse(mapper.hasMap(target));
        Assertions.assertTrue(mapper.getFirstMap(target).isEmpty());
        Assertions.assertTrue(mapper.getMaps(target).stream().allMatch(mapping -> mapping.size() == 3));
    }

    @Test
    void missingAndEmptyMatchesHaveOneEmptyMaximum() throws Exception {
        for (String[] pair : new String[][]{{"", "C"}, {"C", ""}, {"N", "C"}}) {
            List<Map<INode, IAtom>> mappings = new VFMCSMapper(molecule(pair[0]), false)
                    .getMaps(molecule(pair[1]));
            Assertions.assertEquals(1, mappings.size());
            Assertions.assertTrue(mappings.get(0).isEmpty());
        }
    }

    @Test
    void bondOrderChangesTheConnectedOverlap() throws Exception {
        assertOracle(molecule("CC"), molecule("C=C"), true);
        assertOracle(molecule("CC"), molecule("C=C"), false);
        Assertions.assertTrue(new VFMCSMapper(molecule("CC"), true).getMaps(molecule("C=C"))
                .stream().allMatch(mapping -> mapping.size() == 1));
    }

    @Test
    void constructorsAndNestedProbesPreserveAnExpiredClock() throws Exception {
        TimeOut timeout = TimeOut.getInstance();
        double oldLimit = timeout.getTimeOut();
        boolean oldFlag = timeout.isTimeOutFlag();
        try {
            IQuery query = new QueryCompiler(molecule("CC"), false).compile();
            TargetProperties target = new TargetProperties(molecule("CCC"));
            timeout.setTimeOut(0);
            timeout.setTimeOutFlag(true);
            VFMapper probe = new VFMapper(query);
            new VFMCSMapper(query);
            Assertions.assertTrue(timeout.isTimeOutFlag());
            Assertions.assertTrue(probe.getMaps(target, new TimeManager()).isEmpty());
            Assertions.assertTrue(timeout.isTimeOutFlag());
            timeout.setTimeOutFlag(false);
            TimeManager expired = new TimeManager();
            Assertions.assertTrue(probe.getMaps(target, expired).isEmpty());
            Assertions.assertTrue(timeout.isTimeOutFlag());
            timeout.setTimeOut(-1);
            Assertions.assertFalse(probe.getMaps(target).isEmpty());
            Assertions.assertFalse(timeout.isTimeOutFlag());
        } finally {
            timeout.setTimeOut(oldLimit);
            timeout.setTimeOutFlag(oldFlag);
        }
    }

    @Test
    void allFourVertexTopologiesAgreeWithTheIndependentOracle() {
        // All six possible edges: disconnected graphs are included, with mixed bond orders.
        IAtomContainer[] graphs = new IAtomContainer[64];
        for (int mask = 0; mask < graphs.length; mask++) {
            IAtomContainer graph = new AtomContainer();
            for (int atom = 0; atom < 4; atom++) graph.addAtom(new Atom("C"));
            int edge = 0;
            for (int first = 0; first < 4; first++) {
                for (int second = first + 1; second < 4; second++, edge++) {
                    if ((mask & (1 << edge)) != 0) {
                        graph.addBond(first, second,
                                (edge & 1) == 0 ? IBond.Order.SINGLE : IBond.Order.DOUBLE);
                    }
                }
            }
            graphs[mask] = graph;
        }
        for (int first = 0; first < graphs.length; first++) {
            for (int second = first; second < graphs.length; second++) {
                assertOracle(graphs[first], graphs[second], false);
                assertOracle(graphs[first], graphs[second], true);
            }
        }
    }

    @Test
    void connectedMaximaAgreeWithAnExhaustiveInjectionOracle() {
        Random random = new Random(75319L);
        for (int sample = 0; sample < 500; sample++) {
            assertOracle(randomGraph(random, 2 + random.nextInt(5), false, false),
                         randomGraph(random, 2 + random.nextInt(5), false, false), false);
        }
        random = new Random(75319L);
        for (int sample = 0; sample < 500; sample++) {
            assertOracle(randomGraph(random, 2 + random.nextInt(5), true, false),
                         randomGraph(random, 2 + random.nextInt(5), true, false), false);
        }
    }

    @Test
    void pairsThatBecomeReachableLaterRemainAvailable() throws Exception {
        IAtomContainer query = molecule("CCCC");
        IAtomContainer target = molecule("CCC");
        // The third atom of each path becomes a frontier candidate only after its neighbor is mapped.
        assertOracle(query, target, false);
        Assertions.assertEquals(4, new VFMCSMapper(query, false).countMaps(target));
    }

    @Test
    void siblingExclusionsAreRestoredAndRetainedResultsRemainStable() throws Exception {
        IAtomContainer query = molecule("C(C)(C)(C)C");
        IAtomContainer target = molecule("CCCC");
        assertOracle(query, target, false);
        VFMCSMapper mapper = new VFMCSMapper(query, false);
        List<Map<INode, IAtom>> previous = mapper.getMaps(target);
        Assertions.assertEquals(24, previous.size());
        Assertions.assertEquals(24, new HashSet<>(previous).size());
        Assertions.assertEquals(12, mapper.countMaps(molecule("CCC")));
        Assertions.assertTrue(previous.stream().allMatch(mapping -> mapping.size() == 3));
    }

    @Test
    void strictBondMaximaAgreeWithTheSameIndependentOracle() {
        Random random = new Random(942681L);
        for (int sample = 0; sample < 150; sample++) {
            assertOracle(randomGraph(random, 2 + random.nextInt(5), true, true),
                         randomGraph(random, 2 + random.nextInt(5), true, true), true);
        }
    }

    private static IAtomContainer molecule(String smiles) throws Exception {
        return new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smiles);
    }

    private static IAtomContainer randomGraph(Random random, int size, boolean cycles, boolean orders) {
        IAtomContainer graph = new AtomContainer();
        String[] elements = {"C", "C", "N", "O"};
        for (int i = 0; i < size; i++) {
            IAtom atom = new Atom(elements[random.nextInt(elements.length)]);
            atom.setImplicitHydrogenCount(0);
            graph.addAtom(atom);
            if (i > 0) graph.addBond(random.nextInt(i), i,
                    orders && random.nextBoolean() ? IBond.Order.DOUBLE : IBond.Order.SINGLE);
        }
        if (cycles) {
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    if (graph.getBond(graph.getAtom(i), graph.getAtom(j)) == null && random.nextDouble() < 0.25) {
                        graph.addBond(i, j, orders && random.nextBoolean()
                                ? IBond.Order.DOUBLE : IBond.Order.SINGLE);
                    }
                }
            }
        }
        return graph;
    }

    private static void assertOracle(IAtomContainer first, IAtomContainer second, boolean strictBonds) {
        compare(first, second, strictBonds);
        compare(second, first, strictBonds);
    }

    private static void compare(IAtomContainer query, IAtomContainer target, boolean strictBonds) {
        IQuery compiled = new QueryCompiler(query, strictBonds).compile();
        Set<String> actual = new HashSet<>();
        for (Map<INode, IAtom> mapping : new VFMCSMapper(compiled).getMaps(target)) {
            int[] indices = new int[query.getAtomCount()];
            Arrays.fill(indices, -1);
            for (Map.Entry<INode, IAtom> pair : mapping.entrySet()) {
                indices[query.indexOf(compiled.getAtom(pair.getKey()))] = target.indexOf(pair.getValue());
            }
            actual.add(Arrays.toString(indices));
        }
        Assertions.assertEquals(new InjectionOracle(query, target, strictBonds).mappings(), actual);
    }

    /** Enumerates atom injections and computes connectivity independently of the search frontier. */
    private static final class InjectionOracle {
        private final IAtomContainer query;
        private final IAtomContainer target;
        private final boolean strictBonds;
        private final int[] mapping;
        private final boolean[] used;
        private final Set<String> best = new HashSet<>();
        private int bestAtoms = -1;
        private int bestBonds = -1;

        InjectionOracle(IAtomContainer query, IAtomContainer target, boolean strictBonds) {
            this.query = query;
            this.target = target;
            this.strictBonds = strictBonds;
            mapping = new int[query.getAtomCount()];
            Arrays.fill(mapping, -1);
            used = new boolean[target.getAtomCount()];
        }

        Set<String> mappings() {
            enumerate(0, 0);
            return best;
        }

        private void enumerate(int index, int atoms) {
            if (atoms + mapping.length - index < bestAtoms) return;
            if (index == mapping.length) {
                save(atoms);
                return;
            }
            mapping[index] = -1;
            enumerate(index + 1, atoms);
            for (int i = 0; i < used.length; i++) {
                if (used[i] || !query.getAtom(index).getAtomicNumber().equals(target.getAtom(i).getAtomicNumber())) continue;
                mapping[index] = i;
                used[i] = true;
                enumerate(index + 1, atoms + 1);
                used[i] = false;
            }
            mapping[index] = -1;
        }

        private boolean commonBond(int first, int second) {
            IBond queryBond = query.getBond(query.getAtom(first), query.getAtom(second));
            IBond targetBond = target.getBond(target.getAtom(mapping[first]), target.getAtom(mapping[second]));
            return queryBond != null && targetBond != null && (!strictBonds
                    || (queryBond.isAromatic() && targetBond.isAromatic())
                    || (!queryBond.isAromatic() && !targetBond.isAromatic()
                    && queryBond.getOrder() == targetBond.getOrder()));
        }

        private void save(int atoms) {
            if (atoms < bestAtoms) return;
            boolean[] reached = new boolean[mapping.length];
            int first = -1;
            for (int i = 0; i < mapping.length; i++) if (mapping[i] >= 0) { first = i; break; }
            int connected = 0;
            if (first >= 0) {
                reached[first] = true;
                connected = 1;
                boolean changed = true;
                while (changed) {
                    changed = false;
                    for (int i = 0; i < mapping.length; i++) {
                        if (!reached[i]) continue;
                        for (int j = 0; j < mapping.length; j++) {
                            if (mapping[j] >= 0 && !reached[j] && commonBond(i, j)) {
                                reached[j] = true;
                                connected++;
                                changed = true;
                            }
                        }
                    }
                }
            }
            if (connected != atoms) return;
            int bonds = 0;
            for (int i = 0; i < mapping.length; i++) {
                if (mapping[i] < 0) continue;
                for (int j = i + 1; j < mapping.length; j++) {
                    if (mapping[j] >= 0 && commonBond(i, j)) bonds++;
                }
            }
            if (atoms > bestAtoms || bonds > bestBonds) {
                bestAtoms = atoms;
                bestBonds = bonds;
                best.clear();
            }
            if (atoms == bestAtoms && bonds == bestBonds) best.add(Arrays.toString(mapping));
        }
    }
}
