package org.openscience.cdk.smsd.algorithm.mcgregor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.global.TimeOut;

class McGregorSearchTest {
    private final SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());

    @Test
    void extendsSeedAndSharesBothSeedEntryPoints() throws Exception {
        IAtomContainer source = parser.parseSmiles("CCC");
        IAtomContainer target = parser.parseSmiles("CCCC");
        double oldTimeout = TimeOut.getInstance().getTimeOut();
        try {
            TimeOut.getInstance().setTimeOut(-0.5);
            List<List<Integer>> fromMap = new ArrayList<>();
            List<List<Integer>> fromClique = new ArrayList<>();
            McGregor first = new McGregor(source, target, fromMap, true);
            McGregor second = new McGregor(source, target, fromClique, true);
            first.startMcGregorIteration(0, Collections.singletonMap(0, 0));
            second.startMcGregorIteration(0, Arrays.asList(1), Arrays.asList(0, 0, 1));
            Assertions.assertEquals(3, first.getMCSSize());
            Assertions.assertEquals(fromMap, fromClique);
            for (List<Integer> map : fromMap) {
                Assertions.assertEquals(6, map.size());
                Assertions.assertEquals(0, map.get(0));
                Assertions.assertEquals(0, map.get(1));
            }
        } finally {
            TimeOut.getInstance().setTimeOut(oldTimeout);
            TimeOut.getInstance().setTimeOutFlag(false);
        }
    }

    @Test
    void observesDeadlineAndResetsItOnNextSearch() throws Exception {
        List<List<Integer>> maps = new ArrayList<>();
        McGregor search = new McGregor(parser.parseSmiles("CCC"), parser.parseSmiles("CCCC"), maps, true);
        double oldTimeout = TimeOut.getInstance().getTimeOut();
        try {
            TimeOut.getInstance().setTimeOut(0);
            search.startMcGregorIteration(0, Collections.singletonMap(0, 0));
            Assertions.assertTrue(TimeOut.getInstance().isTimeOutFlag());
            Assertions.assertTrue(maps.isEmpty());
            TimeOut.getInstance().setTimeOut(-0.5);
            search.startMcGregorIteration(0, Collections.singletonMap(0, 0));
            Assertions.assertFalse(TimeOut.getInstance().isTimeOutFlag());
            Assertions.assertEquals(3, search.getMCSSize());
            Assertions.assertFalse(maps.isEmpty());
        } finally {
            TimeOut.getInstance().setTimeOut(oldTimeout);
            TimeOut.getInstance().setTimeOutFlag(false);
        }
    }
    @Test
    void queryPredicatesOverrideDisplaySymbolsWhenExtendingSeed() throws Exception {
        IAtomContainer query = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        query.addAtom(parser.parseSmiles("C").getAtom(0));
        org.openscience.cdk.isomorphism.matchers.QueryAtom oxygen =
                new org.openscience.cdk.isomorphism.matchers.QueryAtom(
                        new org.openscience.cdk.isomorphism.matchers.Expr(
                                org.openscience.cdk.isomorphism.matchers.Expr.Type.ELEMENT, 8));
        oxygen.setSymbol("N");
        query.addAtom(oxygen);
        query.addBond(0, 1, org.openscience.cdk.interfaces.IBond.Order.SINGLE);
        List<List<Integer>> maps = new ArrayList<>();
        new McGregor(query, parser.parseSmiles("CO"), maps, true)
                .startMcGregorIteration(0, Collections.singletonMap(0, 0));
        Assertions.assertEquals(Arrays.asList(Arrays.asList(0, 0, 1, 1)), maps);
    }

    @Test
    void extensionCannotFlipAQueryPredicateAwayFromItsSeed() throws Exception {
        IAtomContainer query = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        org.openscience.cdk.isomorphism.matchers.QueryAtom any =
                new org.openscience.cdk.isomorphism.matchers.QueryAtom(
                        new org.openscience.cdk.isomorphism.matchers.Expr(
                                org.openscience.cdk.isomorphism.matchers.Expr.Type.TRUE));
        org.openscience.cdk.isomorphism.matchers.QueryAtom oxygen =
                new org.openscience.cdk.isomorphism.matchers.QueryAtom(
                        new org.openscience.cdk.isomorphism.matchers.Expr(
                                org.openscience.cdk.isomorphism.matchers.Expr.Type.ELEMENT, 8));
        any.setSymbol("O");
        oxygen.setSymbol("C");
        query.addAtom(any);
        query.addAtom(oxygen);
        query.addBond(new org.openscience.cdk.isomorphism.matchers.QueryBond(any, oxygen,
                new org.openscience.cdk.isomorphism.matchers.Expr(
                        org.openscience.cdk.isomorphism.matchers.Expr.Type.TRUE)));
        List<List<Integer>> maps = new ArrayList<>();
        new McGregor(query, parser.parseSmiles("OC"), maps, false)
                .startMcGregorIteration(0, Collections.singletonMap(0, 0));
        Assertions.assertFalse(maps.isEmpty());
        for (List<Integer> map : maps) Assertions.assertEquals(Arrays.asList(0, 0), map);
    }

    @Test
    void reuseCannotDowngradeAnExistingMaximum() throws Exception {
        List<List<Integer>> maps = new ArrayList<>();
        McGregor search = new McGregor(parser.parseSmiles("CCC.C"), parser.parseSmiles("CCC.C"), maps, true);
        search.startMcGregorIteration(0, Collections.singletonMap(0, 0));
        Assertions.assertEquals(3, search.getMCSSize());
        search.startMcGregorIteration(search.getMCSSize(), Collections.singletonMap(3, 3));
        Assertions.assertEquals(3, search.getMCSSize());
        for (List<Integer> map : maps) Assertions.assertEquals(6, map.size());
    }

    @Test
    void labelsAreNotLimitedToFiftyFourMappedAtoms() throws Exception {
        char[] carbons = new char[60];
        Arrays.fill(carbons, 'C');
        IAtomContainer source = parser.parseSmiles(new String(carbons));
        IAtomContainer target = parser.parseSmiles(new String(carbons));
        java.util.Map<Integer, Integer> seed = new java.util.TreeMap<>();
        for (int i = 0; i < 59; i++) seed.put(i, i);
        List<List<Integer>> maps = new ArrayList<>();
        McGregor search = new McGregor(source, target, maps, true);
        search.startMcGregorIteration(0, seed);
        Assertions.assertEquals(60, search.getMCSSize());
        Assertions.assertEquals(1, maps.size());
        Assertions.assertEquals(120, maps.get(0).size());
    }

    @Test
    void invalidSeedIndicesAndRepeatedTargetsLeaveSearchStateUnchanged() throws Exception {
        List<List<Integer>> maps = new ArrayList<>();
        maps.add(Arrays.asList(0, 0));
        McGregor search = new McGregor(parser.parseSmiles("CCC"), parser.parseSmiles("CCC"), maps, true);
        for (Map<Integer, Integer> seed : Arrays.asList(Collections.singletonMap(-1, 0),
                Collections.singletonMap(3, 0), Collections.singletonMap(0, -1),
                Collections.singletonMap(0, 3))) {
            assertRejectedWithoutChangingState(search, maps, IllegalArgumentException.class,
                    () -> search.startMcGregorIteration(0, seed));
        }
        Map<Integer, Integer> repeatedTarget = new TreeMap<>();
        repeatedTarget.put(0, 0);
        repeatedTarget.put(1, 0);
        assertRejectedWithoutChangingState(search, maps, IllegalArgumentException.class,
                () -> search.startMcGregorIteration(0, repeatedTarget));
        assertRejectedWithoutChangingState(search, maps, IllegalArgumentException.class,
                () -> search.startMcGregorIteration(-1, Collections.singletonMap(0, 0)));
    }

    @Test
    void seedAtomPredicatesAreValidatedBeforeSearchStarts() throws Exception {
        IAtomContainer query = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        org.openscience.cdk.isomorphism.matchers.QueryAtom oxygen =
                new org.openscience.cdk.isomorphism.matchers.QueryAtom(
                        new org.openscience.cdk.isomorphism.matchers.Expr(
                                org.openscience.cdk.isomorphism.matchers.Expr.Type.ELEMENT, 8));
        oxygen.setSymbol("C");
        query.addAtom(oxygen);
        List<List<Integer>> maps = new ArrayList<>();
        McGregor search = new McGregor(query, parser.parseSmiles("C"), maps, false);
        assertRejectedWithoutChangingState(search, maps, IllegalArgumentException.class,
                () -> search.startMcGregorIteration(0, Collections.singletonMap(0, 0)));
        assertRejectedWithoutChangingState(search, maps, IllegalArgumentException.class,
                () -> search.startMcGregorIteration(0, Arrays.asList(1), Arrays.asList(0, 0, 1)));
        McGregor ordinary = new McGregor(parser.parseSmiles("O"), parser.parseSmiles("C"), maps, true);
        assertRejectedWithoutChangingState(ordinary, maps, IllegalArgumentException.class,
                () -> ordinary.startMcGregorIteration(0, Collections.singletonMap(0, 0)));
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void nullAndWrongTypedSeedsHaveExplicitFailures() throws Exception {
        IAtomContainer molecule = parser.parseSmiles("C");
        List<List<Integer>> maps = new ArrayList<>();
        McGregor search = new McGregor(molecule, molecule, maps, true);
        assertRejectedWithoutChangingState(search, maps, NullPointerException.class,
                () -> search.startMcGregorIteration(0, (Map<Integer, Integer>) null));
        for (Map seed : Arrays.asList(Collections.singletonMap(null, 0), Collections.singletonMap(0, null),
                Collections.singletonMap("0", 0), Collections.singletonMap(0, "0"))) {
            assertRejectedWithoutChangingState(search, maps, IllegalArgumentException.class,
                    () -> search.startMcGregorIteration(0, seed));
        }
        Assertions.assertThrows(NullPointerException.class, () -> new McGregor(null, molecule, maps, true));
        Assertions.assertThrows(NullPointerException.class, () -> new McGregor(molecule, null, maps, true));
        Assertions.assertThrows(NullPointerException.class, () -> new McGregor(molecule, molecule, null, true));
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void invalidCompatibilityTriplesAndCliqueVerticesLeaveSearchStateUnchanged() throws Exception {
        List<List<Integer>> maps = new ArrayList<>();
        maps.add(Arrays.asList(0, 0));
        McGregor search = new McGregor(parser.parseSmiles("CCC"), parser.parseSmiles("CCC"), maps, true);
        assertRejectedWithoutChangingState(search, maps, NullPointerException.class,
                () -> search.startMcGregorIteration(0, null, Arrays.asList(0, 0, 1)));
        assertRejectedWithoutChangingState(search, maps, NullPointerException.class,
                () -> search.startMcGregorIteration(0, Arrays.asList(1), null));
        for (List triples : Arrays.asList(Arrays.asList(0, 0), Arrays.asList(0, 0, null),
                Arrays.asList("0", 0, 1), Arrays.asList(0, "0", 1), Arrays.asList(0, 0, "1"),
                Arrays.asList(0, 0, 1, 1, 1, 1), Arrays.asList(-1, 0, 1), Arrays.asList(0, 3, 1))) {
            assertRejectedWithoutChangingState(search, maps, IllegalArgumentException.class,
                    () -> search.startMcGregorIteration(0, Arrays.asList(1), triples));
        }
        for (List vertices : Arrays.asList(Arrays.asList((Object) null), Arrays.asList("1"),
                Arrays.asList(2), Arrays.asList(1, 1))) {
            assertRejectedWithoutChangingState(search, maps, IllegalArgumentException.class,
                    () -> search.startMcGregorIteration(0, vertices, Arrays.asList(0, 0, 1)));
        }
        assertRejectedWithoutChangingState(search, maps, IllegalArgumentException.class,
                () -> search.startMcGregorIteration(0, Arrays.asList(1, 2), Arrays.asList(0, 0, 1, 0, 1, 2)));
        assertRejectedWithoutChangingState(search, maps, IllegalArgumentException.class,
                () -> search.startMcGregorIteration(0, Arrays.asList(1, 2), Arrays.asList(0, 0, 1, 1, 0, 2)));
    }

    @Test
    void validSeedDoesNotRequireConnectedOrBondIdenticalInputs() throws Exception {
        Map<Integer, Integer> seed = new TreeMap<>();
        seed.put(0, 0);
        seed.put(1, 1);
        List<List<Integer>> maps = new ArrayList<>();
        McGregor search = new McGregor(parser.parseSmiles("CC"), parser.parseSmiles("C.C"), maps, true);
        search.startMcGregorIteration(0, seed);
        Assertions.assertEquals(2, search.getMCSSize());
        Assertions.assertEquals(Arrays.asList(Arrays.asList(0, 0, 1, 1)), maps);
    }

    @Test
    void malformedBorrowedTopologyIsRejectedBeforeChangingState() throws Exception {
        for (int type = 0; type < 4; type++) {
            for (boolean invalidSource : new boolean[]{true, false}) {
                IAtomContainer source = carbonGraph(3);
                IAtomContainer target = carbonGraph(3);
                List<List<Integer>> maps = new ArrayList<>();
                maps.add(Arrays.asList(0, 0));
                McGregor search = new McGregor(source, target, maps, true);
                IAtomContainer invalid = invalidSource ? source : target;
                switch (type) {
                    case 0:
                        invalid.addBond(new org.openscience.cdk.silent.Bond(
                                invalid.getAtom(0), invalid.getAtom(0),
                                org.openscience.cdk.interfaces.IBond.Order.SINGLE));
                        break;
                    case 1:
                        invalid.addBond(new org.openscience.cdk.silent.Bond(
                                new org.openscience.cdk.interfaces.IAtom[]{invalid.getAtom(0),
                                        invalid.getAtom(1), invalid.getAtom(2)},
                                org.openscience.cdk.interfaces.IBond.Order.SINGLE));
                        break;
                    case 2:
                        invalid.addBond(new org.openscience.cdk.silent.Bond(
                                invalid.getAtom(0), new org.openscience.cdk.silent.Atom("C"),
                                org.openscience.cdk.interfaces.IBond.Order.SINGLE));
                        break;
                    default:
                        invalid.addBond(0, 1, org.openscience.cdk.interfaces.IBond.Order.SINGLE);
                        invalid.addBond(1, 0, org.openscience.cdk.interfaces.IBond.Order.DOUBLE);
                }
                assertRejectedWithoutChangingState(search, maps, IllegalArgumentException.class,
                        () -> search.startMcGregorIteration(0, Collections.singletonMap(0, 0)));
                assertRejectedWithoutChangingState(search, maps, IllegalArgumentException.class,
                        () -> search.startMcGregorIteration(0, Arrays.asList(1), Arrays.asList(0, 0, 1)));
            }
        }
    }

    @Test
    void nullableChemicalMetadataRemainsSupported() throws Exception {
        IAtomContainer source = carbonGraph(2);
        IAtomContainer target = carbonGraph(2);
        source.addBond(new org.openscience.cdk.silent.Bond(source.getAtom(0), source.getAtom(1), null));
        target.addBond(new org.openscience.cdk.silent.Bond(target.getAtom(0), target.getAtom(1), null));
        List<List<Integer>> maps = new ArrayList<>();
        new McGregor(source, target, maps, true).startMcGregorIteration(0, Collections.singletonMap(0, 0));
        Assertions.assertEquals(Arrays.asList(Arrays.asList(0, 0, 1, 1)), maps);
    }

    @Test
    void borrowedGraphsCanGrowBeforeStartingSearch() throws Exception {
        IAtomContainer source = carbonGraph(2);
        IAtomContainer target = carbonGraph(2);
        List<List<Integer>> maps = new ArrayList<>();
        McGregor search = new McGregor(source, target, maps, true);
        for (int i = 2; i < 60; i++) {
            source.addAtom(new org.openscience.cdk.silent.Atom("C"));
            target.addAtom(new org.openscience.cdk.silent.Atom("C"));
        }
        for (int i = 1; i < 60; i++) {
            source.addBond(i - 1, i, org.openscience.cdk.interfaces.IBond.Order.SINGLE);
            target.addBond(i - 1, i, org.openscience.cdk.interfaces.IBond.Order.SINGLE);
        }
        Map<Integer, Integer> seed = new TreeMap<>();
        for (int i = 0; i < 59; i++) seed.put(i, i);
        search.startMcGregorIteration(0, seed);
        Assertions.assertEquals(60, search.getMCSSize());
        Assertions.assertEquals(1, maps.size());
        Assertions.assertEquals(120, maps.get(0).size());
    }

    @Test
    void seedPredicateCannotDisableTheRequestedDeadline() throws Exception {
        IAtomContainer source = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        source.addAtom(new org.openscience.cdk.isomorphism.matchers.QueryAtom(
                new org.openscience.cdk.isomorphism.matchers.Expr(
                        org.openscience.cdk.isomorphism.matchers.Expr.Type.TRUE)) {
            @Override
            public boolean matches(org.openscience.cdk.interfaces.IAtom atom) {
                TimeOut.getInstance().setTimeOut(-1);
                return true;
            }
        });
        List<List<Integer>> maps = new ArrayList<>();
        double previousTimeout = TimeOut.getInstance().getTimeOut();
        boolean previousFlag = TimeOut.getInstance().isTimeOutFlag();
        try {
            TimeOut.getInstance().setTimeOut(0);
            new McGregor(source, parser.parseSmiles("C"), maps, false)
                    .startMcGregorIteration(0, Collections.singletonMap(0, 0));
            Assertions.assertTrue(TimeOut.getInstance().isTimeOutFlag());
            Assertions.assertTrue(maps.isEmpty());
        } finally {
            TimeOut.getInstance().setTimeOut(previousTimeout);
            TimeOut.getInstance().setTimeOutFlag(previousFlag);
        }
    }

    @Test
    void extensionPropagatesUnmappedAtomPredicateFailure() throws Exception {
        IllegalStateException failure = new IllegalStateException("Atom predicate evaluation failed");
        IAtomContainer source = carbonGraph(1);
        org.openscience.cdk.isomorphism.matchers.QueryAtom predicate =
                new org.openscience.cdk.isomorphism.matchers.QueryAtom(
                        new org.openscience.cdk.isomorphism.matchers.Expr(
                                org.openscience.cdk.isomorphism.matchers.Expr.Type.TRUE)) {
            @Override
            public boolean matches(org.openscience.cdk.interfaces.IAtom atom) {
                throw failure;
            }
        };
        predicate.setSymbol("C");
        source.addAtom(predicate);
        source.addBond(0, 1, org.openscience.cdk.interfaces.IBond.Order.SINGLE);
        Assertions.assertSame(failure, extensionFailure(source, IllegalStateException.class));
    }

    @Test
    void extensionPropagatesBondPredicateFailure() throws Exception {
        IllegalStateException failure = new IllegalStateException("Bond predicate evaluation failed");
        IAtomContainer source = carbonGraph(2);
        source.addBond(new org.openscience.cdk.isomorphism.matchers.QueryBond(
                source.getAtom(0), source.getAtom(1), new org.openscience.cdk.isomorphism.matchers.Expr(
                        org.openscience.cdk.isomorphism.matchers.Expr.Type.TRUE)) {
            @Override
            public boolean matches(org.openscience.cdk.interfaces.IBond bond) {
                throw failure;
            }
        });
        Assertions.assertSame(failure, extensionFailure(source, IllegalStateException.class));
    }

    @Test
    void extensionPropagatesUnsetOrdinaryAtomicNumberFailure() throws Exception {
        IAtomContainer source = carbonGraph(2);
        source.getAtom(1).setAtomicNumber(null);
        source.addBond(0, 1, org.openscience.cdk.interfaces.IBond.Order.SINGLE);
        NullPointerException failure = extensionFailure(source, NullPointerException.class);
        Assertions.assertTrue(failure.getMessage().contains("unset atomic number"));
    }

    @Test
    void nestedSearchDeadlineDoesNotCancelOrMarkTheOuterExtension() throws Exception {
        IAtomContainer source = carbonGraph(1);
        int[] calls = {0};
        org.openscience.cdk.isomorphism.matchers.QueryAtom predicate =
                new org.openscience.cdk.isomorphism.matchers.QueryAtom(
                        new org.openscience.cdk.isomorphism.matchers.Expr(
                                org.openscience.cdk.isomorphism.matchers.Expr.Type.TRUE)) {
            @Override
            public boolean matches(org.openscience.cdk.interfaces.IAtom atom) {
                calls[0]++;
                double outerTimeout = TimeOut.getInstance().getTimeOut();
                try {
                    TimeOut.getInstance().setTimeOut(0);
                    new org.openscience.cdk.smsd.algorithm.vflib.map.VFMapper(carbonGraph(1), true)
                            .hasMap(carbonGraph(1));
                    Assertions.assertTrue(TimeOut.getInstance().isTimeOutFlag());
                } finally {
                    TimeOut.getInstance().setTimeOut(outerTimeout);
                }
                return true;
            }
        };
        predicate.setSymbol("C");
        source.addAtom(predicate);
        source.addBond(0, 1, org.openscience.cdk.interfaces.IBond.Order.SINGLE);
        List<List<Integer>> maps = new ArrayList<>();
        McGregor search = new McGregor(source, parser.parseSmiles("CC"), maps, true);
        double previousTimeout = TimeOut.getInstance().getTimeOut();
        boolean previousFlag = TimeOut.getInstance().isTimeOutFlag();
        try {
            TimeOut.getInstance().setTimeOut(-1);
            search.startMcGregorIteration(0, Collections.singletonMap(0, 0));
            Assertions.assertTrue(calls[0] > 0);
            Assertions.assertEquals(2, search.getMCSSize());
            Assertions.assertEquals(Arrays.asList(Arrays.asList(0, 0, 1, 1)), maps);
            Assertions.assertFalse(TimeOut.getInstance().isTimeOutFlag());
        } finally {
            TimeOut.getInstance().setTimeOut(previousTimeout);
            TimeOut.getInstance().setTimeOutFlag(previousFlag);
        }
    }

    private <T extends Throwable> T extensionFailure(IAtomContainer source, Class<T> failure) throws Exception {
        List<List<Integer>> maps = new ArrayList<>();
        McGregor search = new McGregor(source, parser.parseSmiles("CC"), maps, true);
        double previousTimeout = TimeOut.getInstance().getTimeOut();
        boolean previousFlag = TimeOut.getInstance().isTimeOutFlag();
        try {
            TimeOut.getInstance().setTimeOut(-1);
            T thrown = Assertions.assertThrows(failure,
                    () -> search.startMcGregorIteration(0, Collections.singletonMap(0, 0)));
            Assertions.assertTrue(maps.isEmpty());
            return thrown;
        } finally {
            TimeOut.getInstance().setTimeOut(previousTimeout);
            TimeOut.getInstance().setTimeOutFlag(previousFlag);
        }
    }

    private static IAtomContainer carbonGraph(int count) {
        IAtomContainer graph = new org.openscience.cdk.AtomContainerLegacy() {};
        for (int i = 0; i < count; i++) graph.addAtom(new org.openscience.cdk.silent.Atom("C"));
        return graph;
    }

    private static <T extends Throwable> void assertRejectedWithoutChangingState(McGregor search,
            List<List<Integer>> maps, Class<T> failure, Executable action) {
        List<List<Integer>> previousMappings = new ArrayList<>(maps);
        int previousMaximum = search.getMCSSize();
        boolean previousTimeoutFlag = TimeOut.getInstance().isTimeOutFlag();
        try {
            TimeOut.getInstance().setTimeOutFlag(true);
            Assertions.assertThrows(failure, action);
            Assertions.assertTrue(TimeOut.getInstance().isTimeOutFlag());
            Assertions.assertEquals(previousMaximum, search.getMCSSize());
            Assertions.assertEquals(previousMappings, maps);
        } finally {
            TimeOut.getInstance().setTimeOutFlag(previousTimeoutFlag);
        }
    }

}
