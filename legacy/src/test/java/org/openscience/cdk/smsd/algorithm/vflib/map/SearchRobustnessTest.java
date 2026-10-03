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

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smsd.algorithm.vflib.builder.TargetProperties;
import org.openscience.cdk.smsd.algorithm.vflib.builder.VFQueryBuilder;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IState;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;
import org.openscience.cdk.smsd.global.TimeOut;

class SearchRobustnessTest {

    @Test
    @Tag("SlowTest")
    void deepFirstAndAllSearchesUseHeapFrames() {
        withUnlimitedTimeout(() -> {
            IAtomContainer target = chain(16384);
            Assertions.assertTrue(new VFMapper(target, false).hasMap(target));
            // Identity predicates make this deep enumeration have one result and avoid output explosion.
            VFQueryBuilder query = indexedQuery(target, false);
            Assertions.assertEquals(1, new VFMapper(query).countMaps(target));
        });
    }

    @Test
    @Tag("SlowTest")
    void deepPartialSearchUsesHeapFrames() {
        withUnlimitedTimeout(() -> {
            IAtomContainer target = chain(10000);
            VFQueryBuilder query = indexedQuery(target, true);
            List<Map<INode, IAtom>> maps = new VFMCSMapper(query).getMaps(target);
            Assertions.assertEquals(1, maps.size());
            Assertions.assertEquals(target.getAtomCount(), maps.get(0).size());
        });
    }

    @Test
    void failedChildConstructionRestoresItsInsertedPair() {
        for (boolean partial : new boolean[]{false, true}) {
            AtomicBoolean fail = new AtomicBoolean(false);
            IAtomContainer targetMolecule = chain(4);
            VFQueryBuilder query = failingQuery(fail);
            TargetProperties target = new TargetProperties(targetMolecule);
            VFState root = new VFState(query, target, partial);
            Match first = new Match(query.getNode(1), target.getAtom(1));
            Assertions.assertTrue(root.isMatchFeasible(first));
            IState state = root.nextState(first);
            Match second = new Match(query.getNode(2), target.getAtom(2));
            Assertions.assertTrue(state.isMatchFeasible(second));
            fail.set(true);
            Assertions.assertThrows(IllegalStateException.class, () -> state.nextState(second));
            Assertions.assertEquals(1, state.getMap().size());
            Assertions.assertSame(target.getAtom(1), state.getMap().get(query.getNode(1)));
            state.backTrack();
            Assertions.assertTrue(root.getMap().isEmpty());
            fail.set(false);
            Assertions.assertEquals(2, new VFMapper(query).countMaps(target));
            Assertions.assertEquals(2, new VFMCSMapper(query).countMaps(target));
        }
    }

    @Test
    void mapperCanBeReusedAfterPredicateExceptions() {
        AtomicBoolean fail = new AtomicBoolean(true);
        VFQueryBuilder query = failingQuery(fail);
        IAtomContainer target = chain(4);
        VFMapper full = new VFMapper(query);
        VFMCSMapper mcs = new VFMCSMapper(query);
        Assertions.assertThrows(IllegalStateException.class, () -> full.getMaps(target));
        Assertions.assertThrows(IllegalStateException.class, () -> mcs.getMaps(target));
        fail.set(false);
        Assertions.assertEquals(2, full.countMaps(target));
        Assertions.assertEquals(2, mcs.countMaps(target));
    }

    @Test
    void reentrantPredicateCannotResetItsOuterDeadline() {
        TimeOut timeout = TimeOut.getInstance();
        double previous = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        IAtomContainer inner = chain(1);
        AtomicInteger callbacks = new AtomicInteger();
        try {
            timeout.setTimeOut(-1);
            new VFMCSMapper(inner, false).countMaps(inner);
            QueryAtom atom = new QueryAtom(SilentChemObjectBuilder.getInstance()) {
                @Override
                public boolean matches(IAtom target) {
                    callbacks.incrementAndGet();
                    // A callback is cooperative: detect its overrun after it returns.
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException(interrupted);
                    }
                    // This independent search resets the shared compatibility clock and flag.
                    new VFMCSMapper(inner, false).countMaps(inner);
                    return true;
                }
            };
            QueryAtomContainer query = new QueryAtomContainer(SilentChemObjectBuilder.getInstance());
            query.addAtom(atom);
            IQuery compiled = new QueryCompiler(query).compile();
            VFMapper full = new VFMapper(compiled);
            VFMCSMapper mcs = new VFMCSMapper(compiled);
            TargetProperties target = new TargetProperties(chain(1));
            // Sixty milliseconds allows entry into the callback but expires during its sleep.
            timeout.setTimeOut(0.001);
            Assertions.assertFalse(full.hasMap(target));
            Assertions.assertTrue(callbacks.get() > 0);
            Assertions.assertTrue(timeout.isTimeOutFlag());
            callbacks.set(0);
            Assertions.assertTrue(mcs.getMaps(target).isEmpty());
            Assertions.assertTrue(callbacks.get() > 0);
            Assertions.assertTrue(timeout.isTimeOutFlag());
        } finally {
            timeout.setTimeOut(previous);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

    @Test
    void compatibilityClockRetainsDisabledCutoffWhenPredicateEnablesTiming() {
        TimeOut timeout = TimeOut.getInstance();
        double previous = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        AtomicInteger callbacks = new AtomicInteger();
        IQuery query = cutoffChangingQuery(timeout, 0, 0, callbacks);
        TargetProperties target = new TargetProperties(chain(1));
        try {
            timeout.setTimeOut(-1);
            Assertions.assertTrue(new VFMapper(query).hasMap(target));
            Assertions.assertTrue(callbacks.get() > 0);
            Assertions.assertFalse(VFMapper.isTimeOut());
            timeout.setTimeOut(-1);
            callbacks.set(0);
            Assertions.assertEquals(1, new VFMCSMapper(query).countMaps(target));
            Assertions.assertTrue(callbacks.get() > 0);
            Assertions.assertFalse(VFMCSMapper.isTimeOut());
        } finally {
            timeout.setTimeOut(previous);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

    @Test
    void compatibilityClockRetainsEnabledCutoffWhenPredicateDisablesTiming() {
        TimeOut timeout = TimeOut.getInstance();
        double previous = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        AtomicInteger callbacks = new AtomicInteger();
        IQuery query = cutoffChangingQuery(timeout, -1, 80, callbacks);
        TargetProperties target = new TargetProperties(chain(1));
        try {
            timeout.setTimeOut(0.001);
            Assertions.assertFalse(new VFMapper(query).hasMap(target));
            Assertions.assertTrue(callbacks.get() > 0);
            Assertions.assertTrue(timeout.isTimeOutFlag());
            timeout.setTimeOutFlag(false);
            Assertions.assertTrue(VFMapper.isTimeOut());
            timeout.setTimeOut(0.001);
            callbacks.set(0);
            Assertions.assertTrue(new VFMCSMapper(query).getMaps(target).isEmpty());
            Assertions.assertTrue(callbacks.get() > 0);
            Assertions.assertTrue(timeout.isTimeOutFlag());
            timeout.setTimeOutFlag(false);
            Assertions.assertTrue(VFMCSMapper.isTimeOut());
        } finally {
            timeout.setTimeOut(previous);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

    @Test
    void independentNestedTimeoutDoesNotCancelAnOuterDisabledSearch() {
        TimeOut timeout = TimeOut.getInstance();
        double previous = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        AtomicInteger callbacks = new AtomicInteger();
        IAtomContainer target = chain(1);
        QueryAtomContainer query = new QueryAtomContainer(SilentChemObjectBuilder.getInstance());
        query.addAtom(new QueryAtom(SilentChemObjectBuilder.getInstance()) {
            @Override
            public boolean matches(IAtom atom) {
                callbacks.incrementAndGet();
                runExpiredNestedSearch(timeout, target);
                return true;
            }
        });
        IQuery compiled = new QueryCompiler(query).compile();
        VFMapper full = new VFMapper(compiled);
        VFMCSMapper mcs = new VFMCSMapper(compiled);
        try {
            for (int method = 0; method < 5; method++) {
                timeout.setTimeOut(-1);
                callbacks.set(0);
                switch (method) {
                    case 0: Assertions.assertTrue(full.hasMap(target)); break;
                    case 1: Assertions.assertEquals(1, full.getFirstMap(target).size()); break;
                    case 2: Assertions.assertEquals(1, full.getMaps(target).size()); break;
                    case 3: Assertions.assertEquals(1, mcs.getMaps(target).size()); break;
                    default: Assertions.assertEquals(1, mcs.getFirstMap(target).size()); break;
                }
                Assertions.assertTrue(callbacks.get() > 0);
                Assertions.assertFalse(timeout.isTimeOutFlag());
                Assertions.assertFalse(method < 3 ? VFMapper.isTimeOut() : VFMCSMapper.isTimeOut());
            }
        } finally {
            timeout.setTimeOut(previous);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

    @Test
    void failedRootConstructionRestoresItsOperationClockAndStatus() {
        TimeOut timeout = TimeOut.getInstance();
        double previous = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        AtomicBoolean fail = new AtomicBoolean(true);
        IAtomContainer target = chain(1);
        VFQueryBuilder query = new VFQueryBuilder() {
            @Override
            public INode getNode(int index) {
                if (fail.get()) {
                    runExpiredNestedSearch(timeout, target);
                    throw new IllegalStateException("root access failed");
                }
                return super.getNode(index);
            }
        };
        query.addNode((properties, atom) -> true, new Atom("C"));
        VFMapper full = new VFMapper(query);
        VFMCSMapper mcs = new VFMCSMapper(query);
        try {
            timeout.setTimeOut(-1);
            Assertions.assertThrows(IllegalStateException.class, () -> full.hasMap(target));
            Assertions.assertFalse(timeout.isTimeOutFlag());
            Assertions.assertFalse(VFMapper.isTimeOut());
            Assertions.assertThrows(IllegalStateException.class, () -> full.getFirstMap(target));
            Assertions.assertFalse(timeout.isTimeOutFlag());
            Assertions.assertFalse(VFMapper.isTimeOut());
            Assertions.assertThrows(IllegalStateException.class, () -> full.getMaps(target));
            Assertions.assertFalse(timeout.isTimeOutFlag());
            Assertions.assertFalse(VFMapper.isTimeOut());
            Assertions.assertThrows(IllegalStateException.class, () -> mcs.getMaps(target));
            Assertions.assertFalse(timeout.isTimeOutFlag());
            Assertions.assertFalse(VFMCSMapper.isTimeOut());
            Assertions.assertThrows(IllegalStateException.class, () -> mcs.getFirstMap(target));
            Assertions.assertFalse(timeout.isTimeOutFlag());
            Assertions.assertFalse(VFMCSMapper.isTimeOut());
            fail.set(false);
            Assertions.assertTrue(full.hasMap(target));
            Assertions.assertEquals(1, mcs.getMaps(target).size());
        } finally {
            timeout.setTimeOut(previous);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

    @Test
    void anyFiniteNegativeCutoffDisablesTiming() {
        TimeOut timeout = TimeOut.getInstance();
        double previous = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        try {
            timeout.setTimeOut(-0.5);
            IAtomContainer query = chain(3);
            IAtomContainer target = chain(4);
            Assertions.assertTrue(new VFMapper(query, false).hasMap(target));
            Assertions.assertEquals(3, new VFMCSMapper(query, false).getFirstMap(target).size());
            Assertions.assertEquals(4, new VFMCSMapper(query, false).countMaps(target));
            Assertions.assertFalse(timeout.isTimeOutFlag());
        } finally {
            timeout.setTimeOut(previous);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

    @Test
    void nullCompiledGraphsFailAtTheEntryPoint() {
        Assertions.assertThrows(NullPointerException.class, () -> new VFMapper((IQuery) null));
        Assertions.assertThrows(NullPointerException.class, () -> new VFMCSMapper((IQuery) null));
        IQuery query = new QueryCompiler(chain(1), false).compile();
        Assertions.assertThrows(NullPointerException.class,
                () -> new VFMapper(query).getMaps((TargetProperties) null));
        Assertions.assertThrows(NullPointerException.class,
                () -> new VFMCSMapper(query).getMaps((TargetProperties) null));
    }

    private static void withUnlimitedTimeout(Runnable check) {
        TimeOut timeout = TimeOut.getInstance();
        double previous = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        try {
            timeout.setTimeOut(-1);
            check.run();
        } finally {
            timeout.setTimeOut(previous);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

    private static void runExpiredNestedSearch(TimeOut timeout, IAtomContainer inner) {
        timeout.setTimeOut(0);
        Assertions.assertFalse(new VFMCSMapper(inner, false).hasMap(inner));
        Assertions.assertTrue(timeout.isTimeOutFlag());
        timeout.setTimeOut(-1);
    }

    private static IQuery cutoffChangingQuery(TimeOut timeout, double changedCutoff,
                                              long pause, AtomicInteger callbacks) {
        QueryAtomContainer query = new QueryAtomContainer(SilentChemObjectBuilder.getInstance());
        query.addAtom(new QueryAtom(SilentChemObjectBuilder.getInstance()) {
            @Override
            public boolean matches(IAtom target) {
                callbacks.incrementAndGet();
                timeout.setTimeOut(changedCutoff);
                if (pause > 0) {
                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException(interrupted);
                    }
                }
                return true;
            }
        });
        return new QueryCompiler(query).compile();
    }

    private static IAtomContainer chain(int size) {
        IAtomContainer graph = new AtomContainer();
        for (int i = 0; i < size; i++) {
            IAtom atom = new Atom("C");
            atom.setID(Integer.toString(i));
            graph.addAtom(atom);
            if (i > 0) graph.addBond(i - 1, i, IBond.Order.SINGLE);
        }
        return graph;
    }

    private static VFQueryBuilder indexedQuery(IAtomContainer target, boolean unmatchedRoot) {
        VFQueryBuilder query = new VFQueryBuilder();
        INode previous = null;
        for (int i = 0; i < target.getAtomCount(); i++) {
            String identifier = target.getAtom(i).getID();
            INode node = query.addNode((properties, atom) -> identifier.equals(atom.getID()), new Atom("C"));
            if (previous != null) query.connect(previous, node, (properties, bond) -> true);
            previous = node;
        }
        if (unmatchedRoot) {
            INode node = query.addNode((properties, atom) -> false, new Atom("N"));
            query.connect(previous, node, (properties, bond) -> true);
        }
        return query;
    }

    private static VFQueryBuilder failingQuery(AtomicBoolean fail) {
        VFQueryBuilder query = new VFQueryBuilder();
        INode previous = null;
        for (int i = 0; i < 4; i++) {
            final boolean throwsOnMatch = i == 3;
            INode node = query.addNode((properties, atom) -> {
                if (throwsOnMatch && fail.get()) throw new IllegalStateException("predicate failed");
                return true;
            }, new Atom("C"));
            if (previous != null) query.connect(previous, node, (properties, bond) -> true);
            previous = node;
        }
        return query;
    }
}
