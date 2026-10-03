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
package org.openscience.cdk.smsd;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.smsd.algorithm.mcsplus.MCSPlus;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.algorithm.mcsplus.MCSPlusHandler;
import org.openscience.cdk.smsd.algorithm.vflib.VFlibMCSHandler;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMCSMapper;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.global.TimeOut;
import org.openscience.cdk.smsd.helper.FinalMappings;
import org.openscience.cdk.smsd.tools.MolHandler;

class SearchLifecycleTest {
    private IAtomContainer molecule(String smiles) throws Exception {
        return new SmilesParser(DefaultChemObjectBuilder.getInstance()).parseSmiles(smiles);
    }

    @Test
    void mcsPlusPredicateCannotRetroactivelyChangeItsTimeoutBudget() throws Exception {
        TimeOut timeout = TimeOut.getInstance();
        double previous = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        try {
            timeout.setTimeOut(-1);
            QueryAtomContainer query = new QueryAtomContainer(DefaultChemObjectBuilder.getInstance());
            query.addAtom(new QueryAtom(DefaultChemObjectBuilder.getInstance()) {
                @Override
                public boolean matches(IAtom atom) {
                    TimeOut.getInstance().setTimeOut(0);
                    return true;
                }
            });
            MCSPlusHandler handler = new MCSPlusHandler();
            handler.set(query, molecule("C"));
            handler.searchMCS(true);
            Assertions.assertEquals(Collections.singletonMap(0, 0), handler.getFirstMapping());
            Assertions.assertEquals(0, timeout.getTimeOut());
            Assertions.assertFalse(timeout.isTimeOutFlag());
            Assertions.assertFalse(MCSPlus.isTimeOut());
        } finally {
            timeout.setTimeOut(previous);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

    @Test
    void independentPredicateSearchTimeoutDoesNotCancelMcsPlus() throws Exception {
        TimeOut timeout = TimeOut.getInstance();
        double previous = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        IAtomContainer inner = molecule("C");
        try {
            timeout.setTimeOut(-1);
            QueryAtomContainer query = new QueryAtomContainer(DefaultChemObjectBuilder.getInstance());
            query.addAtom(new QueryAtom(DefaultChemObjectBuilder.getInstance()) {
                @Override
                public boolean matches(IAtom atom) {
                    TimeOut.getInstance().setTimeOut(0);
                    Assertions.assertFalse(new VFMCSMapper(inner, true).hasMap(inner));
                    Assertions.assertTrue(TimeOut.getInstance().isTimeOutFlag());
                    TimeOut.getInstance().setTimeOut(-1);
                    return true;
                }
            });
            MCSPlusHandler handler = new MCSPlusHandler();
            handler.set(query, inner);
            handler.searchMCS(true);
            Assertions.assertEquals(Collections.singletonMap(0, 0), handler.getFirstMapping());
            Assertions.assertFalse(timeout.isTimeOutFlag());
            Assertions.assertFalse(MCSPlus.isTimeOut());
        } finally {
            timeout.setTimeOut(previous);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

    @Test
    void mcsPlusPreparationFailureRestoresItsOwnClockAndStatus() throws Exception {
        TimeOut timeout = TimeOut.getInstance();
        double previous = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        IAtomContainer inner = molecule("C");
        AtomicBoolean armed = new AtomicBoolean();
        try {
            QueryAtomContainer query = new QueryAtomContainer(DefaultChemObjectBuilder.getInstance()) {
                @Override
                public int getAtomCount() {
                    if (armed.getAndSet(false)) {
                        TimeOut.getInstance().setTimeOut(0);
                        MCSPlusHandler nested = new MCSPlusHandler();
                        nested.set(new MolHandler(inner, false, false), new MolHandler(inner, false, false));
                        nested.searchMCS(true);
                        Assertions.assertTrue(TimeOut.getInstance().isTimeOutFlag());
                        TimeOut.getInstance().setTimeOut(-1);
                        throw new IllegalStateException("Preparation callback failed");
                    }
                    return super.getAtomCount();
                }
            };
            query.addAtom(new QueryAtom(DefaultChemObjectBuilder.getInstance()) {
                @Override
                public boolean matches(IAtom atom) { return true; }
            });
            MCSPlusHandler handler = new MCSPlusHandler();
            handler.set(query, inner);
            timeout.setTimeOut(-1);
            armed.set(true);
            Assertions.assertThrows(IllegalStateException.class, () -> handler.searchMCS(true));
            Assertions.assertFalse(timeout.isTimeOutFlag());
            Assertions.assertFalse(MCSPlus.isTimeOut());
            handler.searchMCS(true);
            Assertions.assertEquals(Collections.singletonMap(0, 0), handler.getFirstMapping());
        } finally {
            timeout.setTimeOut(previous);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

    @Test
    void mcsPlusHandlerCanBeReusedInBothDirections() throws Exception {
        MCSPlusHandler handler = new MCSPlusHandler();
        for (String[] pair : Arrays.asList(new String[]{"CC", "CCC"},
                new String[]{"NNN", "NN"}, new String[]{"CC", "CCC"})) {
            handler.set(new MolHandler(molecule(pair[0]), false, false),
                        new MolHandler(molecule(pair[1]), false, false));
            handler.searchMCS(true);
            Assertions.assertEquals(2, handler.getFirstMapping().size());
            Assertions.assertTrue(handler.getAllMapping().stream().allMatch(map -> map.size() == 2));
            Assertions.assertTrue(handler.getFirstAtomMapping().keySet().stream()
                    .allMatch(atom -> atom.getSymbol().equals(pair[0].substring(0, 1))));
        }
        handler.set(new MolHandler(molecule("N"), false, false), new MolHandler(molecule("C"), false, false));
        handler.searchMCS(true);
        Assertions.assertTrue(handler.getFirstMapping().isEmpty());
    }

    @Test
    void vfHandlerDoesNotRetainThePreviousMaximum() throws Exception {
        VFlibMCSHandler handler = new VFlibMCSHandler();
        for (String[] pair : Arrays.asList(new String[]{"CCCC", "CCCCC"}, new String[]{"NN", "NNN"})) {
            handler.set(new MolHandler(molecule(pair[0]), false, false),
                        new MolHandler(molecule(pair[1]), false, false));
            handler.searchMCS(true);
            Assertions.assertEquals(pair[0].length(), handler.getFirstMapping().size());
            Assertions.assertTrue(handler.getAllMapping().stream().allMatch(map -> map.size() == pair[0].length()));
            Assertions.assertTrue(handler.getFirstAtomMapping().keySet().stream()
                    .allMatch(atom -> atom.getSymbol().equals(pair[0].substring(0, 1))));
        }
    }

    @Test
    void vfMapperFindsPartialMcsWhenQueryIsLargerThanTarget() throws Exception {
        VFMCSMapper mapper = new VFMCSMapper(molecule("CCCC"), true);
        List<Map<INode, IAtom>> mappings = mapper.getMaps(molecule("CCC"));
        Assertions.assertFalse(mappings.isEmpty());
        Assertions.assertTrue(mappings.stream().allMatch(map -> map.size() == 3));
        mappings = mapper.getMaps(molecule("CC"));
        Assertions.assertFalse(mappings.isEmpty());
        Assertions.assertTrue(mappings.stream().allMatch(map -> map.size() == 2));
    }

    @Test
    void vfMapperReturnsOnlyMaximumMappings() throws Exception {
        VFMCSMapper mapper = new VFMCSMapper(molecule("CCC"), true);
        List<Map<INode, IAtom>> mappings = mapper.getMaps(molecule("CCCC"));
        Assertions.assertFalse(mappings.isEmpty());
        Assertions.assertTrue(mappings.stream().allMatch(map -> map.size() == 3));
    }

    @Test
    void compatibilityStateIsIsolatedAcrossThreads() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2);
        try {
            java.util.ArrayList<Future<Boolean>> results = new java.util.ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                final int value = i;
                results.add(executor.submit(() -> {
                    TimeOut.getInstance().setTimeOut(value);
                    FinalMappings.getInstance().set(Collections.singletonList(Collections.singletonMap(0, value)));
                    barrier.await(5, TimeUnit.SECONDS);
                    return TimeOut.getInstance().getTimeOut() == value
                            && FinalMappings.getInstance().getFinalMapping().get(0).get(0) == value;
                }));
            }
            for (Future<Boolean> result : results) {
                Assertions.assertTrue(result.get(10, TimeUnit.SECONDS));
            }
        } finally {
            executor.shutdownNow();
        }
    }
    @Test
    void vfSearchMarksAnExpiredDeadline() throws Exception {
        TimeOut timeout = TimeOut.getInstance();
        double previous = timeout.getTimeOut();
        boolean previousFlag = timeout.isTimeOutFlag();
        try {
            timeout.setTimeOut(0);
            new VFMCSMapper(molecule("CCCC"), true).getMaps(molecule("CCCN"));
            Assertions.assertTrue(timeout.isTimeOutFlag());
            Assertions.assertFalse(new VFMCSMapper(molecule("CCCC"), true).hasMap(molecule("CCCCC")));
            Assertions.assertTrue(timeout.isTimeOutFlag());
            Assertions.assertTrue(new VFMCSMapper(molecule("CCCC"), true)
                    .getFirstMap(molecule("CCCCC")).isEmpty());
            Assertions.assertTrue(timeout.isTimeOutFlag());
        } finally {
            timeout.setTimeOut(previous);
            timeout.setTimeOutFlag(previousFlag);
        }
    }

}
