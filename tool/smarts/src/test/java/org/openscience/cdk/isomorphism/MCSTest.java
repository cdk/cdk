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

import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomRef;
import org.openscience.cdk.exception.Intractable;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.AtomContainerLegacy;
import org.openscience.cdk.silent.Bond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smarts.Smarts;
import org.openscience.cdk.smarts.SmartsPattern;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.openscience.cdk.isomorphism.MCSTest.Option.BY_ELEMENT;
import static org.openscience.cdk.isomorphism.MCSTest.Option.DAYLIGHT;
import static org.openscience.cdk.isomorphism.MCSTest.Option.EXPLICIT_H;
import static org.openscience.cdk.isomorphism.MCSTest.Option.SMARTS;
import static org.openscience.cdk.isomorphism.MCSTest.Unchanged.SAME;
import static org.openscience.cdk.isomorphism.MCSTest.Unchanged.SAME_ORDER;
import static org.openscience.cdk.isomorphism.MCSTesting.assertLimits;
import static org.openscience.cdk.isomorphism.MCSTesting.assertValid;
import static org.openscience.cdk.isomorphism.MCSTesting.atoms;
import static org.openscience.cdk.isomorphism.MCSTesting.bruteForce;
import static org.openscience.cdk.isomorphism.MCSTesting.bruteForceEdges;
import static org.openscience.cdk.isomorphism.MCSTesting.commonBonds;
import static org.openscience.cdk.isomorphism.MCSTesting.complete;
import static org.openscience.cdk.isomorphism.MCSTesting.completeGraph;
import static org.openscience.cdk.isomorphism.MCSTesting.exact;
import static org.openscience.cdk.isomorphism.MCSTesting.inverses;
import static org.openscience.cdk.isomorphism.MCSTesting.keys;
import static org.openscience.cdk.isomorphism.MCSTesting.shuffle;
import static org.openscience.cdk.isomorphism.MCSTesting.smarts;
import static org.openscience.cdk.isomorphism.MCSTesting.smi;
import static org.openscience.cdk.isomorphism.MCSTesting.snapshot;
import static org.openscience.cdk.isomorphism.MCSTesting.stereoOk;
import static org.openscience.cdk.isomorphism.MCSTesting.unshuffle;
import static org.openscience.cdk.isomorphism.MCSTesting.withConfigurations;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ELEMENT;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.FORMAL_CHARGE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ISOTOPE;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.IS_IN_CHAIN;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.IS_IN_RING;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.ORDER;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.SINGLE_OR_AROMATIC;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.STEREOCHEMISTRY;

/**
 * Checks the public API and the documented behaviour of {@link MCS}: the
 * arguments and options, how atoms and bonds match, query molecules, the
 * mappings returned, the limits, and use from several threads. Expected
 * mappings are worked out by hand or by {@link MCSTesting#exact}, and
 * compared as sets, never by their order.
 *
 * @author Syed Asad Rahman
 */
final class MCSTest {

    static final String         C60      = "c12c3c4c5c1c6c7c8c2c9c%10c3c%11c%12c4c%13c%14c5c%15c6c%16c7c%17c%18c8c9"
            + "c%19c%20c%10c%11c%21c%22c%12c%13c%23c%24c%14c%15c%25c%16c%26c%17c%27c%18c%19c%28c%20c%21c%29c%22"
            + "c%23c%30c%24c%25c%26c%31c%27c%28c%29c%30%31";
    private static final String CORONENE = "c1cc2ccc3ccc4ccc5ccc6ccc1c7c2c3c4c5c67";
    private static final String ASPIRIN  = "CC(=O)Oc1ccccc1C(=O)O";
    private static final String PALMITIC = "CCCCCCCCCCCCCCCC(=O)O";
    private static final String OLEIC    = "CCCCCCCCC=CCCCCCCCC(=O)O";
    private static final String CUBANE   = "C12C3C4C1C5C2C3C45";
    private static final String ADAMANTANE = "C1C2CC3CC1CC(C2)C3";
    private static final String L_ALA    = "N[C@@H](C)C(=O)O";
    private static final String D_ALA    = "N[C@H](C)C(=O)O";
    private static final String L_THR    = "C[C@H]([C@@H](C(=O)O)N)O";
    private static final String L_ALLO   = "C[C@@H]([C@@H](C(=O)O)N)O";
    private static final String D_THR    = "C[C@@H]([C@H](C(=O)O)N)O";

    /**
     * The bond kinds of the default rule: single, double, each also aromatic,
     * no order, no order aromatic, UNSET, UNSET aromatic.
     */
    private static final IBond.Order[] ORDERS   = {IBond.Order.SINGLE, IBond.Order.DOUBLE, IBond.Order.SINGLE,
                                                   IBond.Order.DOUBLE, null, null, IBond.Order.UNSET,
                                                   IBond.Order.UNSET};
    private static final boolean[]     AROMATIC = {false, false, true, true, false, true, false, true};
    /** Which query bond kind (row) matches which target bond kind (column). */
    private static final String[]      BONDS    = {"10000000", "01000000", "00110101", "00110101", "00001000",
                                                   "00110101", "00000010", "00110101"};

    /** No atom in common, so no mapping. */
    private static final Score NONE = new Score(0, 0, 0);

    /** How the molecules of a row are matched and prepared, when not by default and as parsed. */
    enum Option {
        /** Atoms by element and bonds whatever their order, as withMatching(ELEMENT). */
        BY_ELEMENT,
        /** Explicit hydrogens on both molecules. */
        EXPLICIT_H,
        /** Both molecules prepared for SMARTS, which perceives aromaticity as Daylight does. */
        DAYLIGHT,
        /** The query is SMARTS, and the target is prepared for it. */
        SMARTS
    }

    /** Complete rings leave the mappings of the default search as they are. */
    enum Unchanged {
        /** The same mappings. */
        SAME,
        /** The same list, in the same order. */
        SAME_ORDER
    }

    // arguments and options

    @Test
    void testArguments() throws Exception {
        Assertions.assertThrows(NullPointerException.class, () -> MCS.find(null));
        Assertions.assertThrows(NullPointerException.class, () -> MCS.find(smi("C")).match(null));
        Assertions.assertThrows(NullPointerException.class, () -> MCS.find(smi("C")).withMatching((Expr.Type) null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MCS.find(smi("C")).withTimeout(0, TimeUnit.SECONDS));
        MCS mcs = MCS.find(smi("CC"));
        Assertions.assertThrows(NullPointerException.class, () -> mcs.matchAll(null));
        Assertions.assertThrows(NullPointerException.class, () -> mcs.withMatching(ELEMENT).match(null));
        Assertions.assertThrows(NullPointerException.class, () -> mcs.withMatching(ELEMENT, null));
        Assertions.assertThrows(NullPointerException.class, () -> mcs.withMatching((Expr.Type[]) null));
        Assertions.assertThrows(NullPointerException.class, () -> mcs.withTimeout(1, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> mcs.withTimeout(-1, TimeUnit.MILLISECONDS));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> mcs.withTimeout(Long.MIN_VALUE, TimeUnit.NANOSECONDS));
        // the longest time limit is as good as none
        assertMappings(mcs.withTimeout(Long.MAX_VALUE, TimeUnit.DAYS), smi("CCC"),
                set(m(0, 1), m(1, 0), m(1, 2), m(2, 1)));
    }

    @Test
    void testOptionsMakeNewSearches() throws Exception {
        MCS mcs = MCS.find(smi("CC"));
        MCS byElement = mcs.withMatching(ELEMENT);
        MCS limited = mcs.withTimeout(10, TimeUnit.SECONDS);
        Assertions.assertNotSame(mcs, byElement);
        Assertions.assertNotSame(mcs, limited);
        Assertions.assertEquals(1, atoms(mcs.match(smi("C=C"))));
        Assertions.assertEquals(1, atoms(limited.match(smi("C=C"))));
        Assertions.assertEquals(2, atoms(byElement.match(smi("C=C"))));

        // each new search keeps the other option: C-O on C=C maps both atoms when every atom matches
        MCS co = MCS.find(smi("CO"));
        Set<String> both = set(m(0, 1), m(1, 0));
        assertMappings(co.withMatching(), smi("C=C"), both);
        assertMappings(co.withTimeout(10, TimeUnit.SECONDS).withMatching(), smi("C=C"), both);
        assertMappings(co.withMatching().withTimeout(10, TimeUnit.SECONDS), smi("C=C"), both);
        // a new matching replaces the old one, which still maps a carbon only
        MCS element = co.withMatching(ELEMENT);
        assertMappings(element.withMatching(), smi("C=C"), both);
        assertMappings(element, smi("C=C"), set(m(0, -1), m(1, -1)));
        // the exact search of coronene against C60 takes far longer than the limit kept by withMatching
        MCS hard = MCS.find(smi(CORONENE)).withTimeout(10, TimeUnit.MILLISECONDS).withMatching(ELEMENT);
        Assertions.assertThrows(Intractable.class, () -> hard.match(smi(C60)));
        // the types are copied: changing the array afterwards has no effect
        Expr.Type[] types = {ELEMENT};
        MCS copied = mcs.withMatching(types);
        types[0] = ORDER;
        Assertions.assertEquals(2, atoms(copied.match(smi("C=C"))));
        Assertions.assertEquals(1, atoms(mcs.withMatching(types).match(smi("C=C"))));
    }

    // matching

    @Test
    void testBondOrder() throws Exception {
        String[] orders = {"-", "=", "#"};
        for (String a : orders) {
            for (String b : orders) {
                IAtomContainer query = smi("C" + a + "C");
                IAtomContainer target = smi("C" + b + "C");
                // the same order maps the bond, either way round; otherwise one atom on its own
                Set<String> expected = a.equals(b) ? set(m(0, 1), m(1, 0))
                                                   : set(m(0, -1), m(1, -1), m(-1, 0), m(-1, 1));
                Assertions.assertEquals(expected, exact(query, target, true));
                assertMappings(MCS.find(query), target, expected);
                assertMappings(MCS.find(query).withMatching(ELEMENT), target, set(m(0, 1), m(1, 0)));
            }
        }
        // C=C-C on C-C=C: only one way round has both bonds in common
        IAtomContainer query = smi("C=CC");
        IAtomContainer target = smi("CC=C");
        assertScore(query, target, assertMappings(MCS.find(query), target, set(m(2, 1, 0))), 3, 2, true);
        assertMappings(MCS.find(query).withMatching(ELEMENT), target, set(m(0, 1, 2), m(2, 1, 0)));
        // the carbonyl and hydroxy oxygens are told apart by the bond order
        query = smi("CC(=O)O");
        target = smi("CC(O)=O");
        assertMappings(MCS.find(query), target, set(m(0, 1, 3, 2)));
        assertMappings(MCS.find(query).withMatching(ELEMENT), target, set(m(0, 1, 2, 3), m(0, 1, 3, 2)));
    }

    @Test
    void testAmides() throws Exception {
        // N-methylacetamide on propanamide: the acetamide group; the N-methyl has no partner, as the N of
        // propanamide is terminal
        IAtomContainer query = smi("CC(=O)NC");
        IAtomContainer target = smi("CCC(=O)N");
        Set<String> expected = set(m(1, 2, 3, 4, -1));
        Assertions.assertEquals(expected, exact(query, target, true));
        assertScore(query, target, assertMappings(MCS.find(query), target, expected), 4, 3, true);
        assertMappings(MCS.find(query).withMatching(ELEMENT), target, expected);
        // the other way round
        assertMappings(MCS.find(target), query, set(m(-1, 0, 1, 2, 3)));
    }

    @Test
    void testAromaticBonds() throws Exception {
        IAtomContainer aromatic = smi("c1ccccc1");
        IAtomContainer kekule = smi("C1=CC=CC=C1");
        Assertions.assertTrue(aromatic.getBond(0).isAromatic());
        Assertions.assertFalse(kekule.getBond(0).isAromatic());
        // aromatic bonds match whatever their Kekule order: all 12 symmetries of the ring
        Set<String> ring = ringMappings(6, 0, 1, 2, 3, 4, 5);
        Assertions.assertEquals(ring, exact(aromatic, aromatic, true));
        assertScore(aromatic, aromatic, assertMappings(MCS.find(aromatic), aromatic, ring), 6, 6, true);
        // an aromatic bond does not match a single or double bond: one atom, on any of the six
        Set<String> oneAtom = exact(kekule, aromatic, true);
        Assertions.assertEquals(36, oneAtom.size());
        assertScore(kekule, aromatic, assertMappings(MCS.find(kekule), aromatic, oneAtom), 1, 0, true);
        assertScore(aromatic, kekule, assertMappings(MCS.find(aromatic), kekule, oneAtom), 1, 0, true);
        // with ELEMENT the bond orders and aromaticity are ignored
        assertMappings(MCS.find(kekule).withMatching(ELEMENT), aromatic, ring);
        assertMappings(MCS.find(aromatic).withMatching(ELEMENT), kekule, ring);
        // two Kekule structures: only the 6 symmetries that keep the double bonds in place
        Set<String> kekuleRing = exact(kekule, kekule, true);
        Assertions.assertEquals(6, kekuleRing.size());
        assertMappings(MCS.find(kekule), kekule, kekuleRing);
    }

    @Test
    void testDefaultBonds() throws Exception {
        for (int i = 0; i < BONDS.length; i++) {
            for (int j = 0; j < BONDS.length; j++) {
                int expected = BONDS[i].charAt(j) == '1' ? 2 : 1;
                String cell = i + " on " + j;
                // the whole query, a query that cannot map whole (O on N) both ways round, and a plain bond in a
                // query molecule
                Assertions.assertEquals(expected, atoms(MCS.find(bond(i)).match(bond(j))), cell);
                Assertions.assertEquals(expected, atoms(MCS.find(bond(i, "O")).match(bond(j, "N"))), cell);
                Assertions.assertEquals(expected, atoms(MCS.find(bond(j, "N")).match(bond(i, "O"))), cell);
                Assertions.assertEquals(expected, atoms(MCS.find(inQuery(bond(i))).match(bond(j))), cell);
            }
        }
    }

    @Test
    void testAromaticFlags() throws Exception {
        // only the bond flags count for the default matching: aromatic bonds between atoms not flagged aromatic
        IAtomContainer query = smi("c1ccccc1");
        for (IAtom atom : query.atoms()) {
            atom.setIsAromatic(false);
        }
        assertMappings(MCS.find(query), smi("c1ccccc1"), ringMappings(6, 0, 1, 2, 3, 4, 5));
    }

    @Test
    void testBondsMayBeMissingOnEitherSide() throws Exception {
        // a bond of the target can be left out as well as one of the query: the chain on the triangle, either way
        // round from any of its atoms
        IAtomContainer query = smi("CCC");
        IAtomContainer target = smi("C1CC1");
        Set<String> expected = exact(query, target, true);
        Assertions.assertEquals(6, expected.size());
        assertScore(query, target, assertMappings(MCS.find(query), target, expected), 3, 2, true);
        // a four-membered ring on methylcyclopropane: a bond left out on each side, three in common
        // (two paths of four atoms in the target, 8 ways to lay the ring on each)
        query = smi("C1CCC1");
        target = smi("CC1CC1");
        expected = exact(query, target, true);
        Assertions.assertEquals(16, expected.size());
        assertScore(query, target, assertMappings(MCS.find(query), target, expected), 4, 3, true);
    }

    @Test
    void testWithoutMatchingTypes() throws Exception {
        // every atom and bond matches
        assertMappings(MCS.find(smi("N")).withMatching(), smi("C"), set(m(0)));
        assertMappings(MCS.find(smi("CCO")).withMatching(), smi("N#N"),
                set(m(0, 1, -1), m(1, 0, -1), m(-1, 0, 1), m(-1, 1, 0)));
        assertMappings(MCS.find(smi("c1ccccc1")).withMatching(), smi("C1CCNCC1"), ringMappings(6, 0, 1, 2, 3, 4, 5));
        assertMappings(MCS.find(smi("[Na+].[Cl-]")).withMatching(), smi("CCO"),
                set(m(0, -1), m(1, -1), m(2, -1), m(-1, 0), m(-1, 1), m(-1, 2)));
    }

    @Test
    void testCharges() throws Exception {
        // glycine as the zwitterion and as the neutral molecule
        IAtomContainer query = smi("[NH3+]CC(=O)[O-]");
        IAtomContainer target = smi("NCC(=O)O");
        // charges are ignored by default
        Set<String> expected = set(m(0, 1, 2, 3, 4));
        Assertions.assertEquals(expected, exact(query, target, true));
        assertScore(query, target, assertMappings(MCS.find(query), target, expected), 5, 4, true);
        // and by ELEMENT, which also lets the two oxygens swap
        assertMappings(MCS.find(query).withMatching(ELEMENT), target, set(m(0, 1, 2, 3, 4), m(0, 1, 2, 4, 3)));
        // with FORMAL_CHARGE the charged N and O are left out, the neutral O goes on either target O
        List<int[]> mappings = assertMappings(MCS.find(query).withMatching(ELEMENT, FORMAL_CHARGE), target,
                set(m(-1, 1, 2, 3, -1), m(-1, 1, 2, 4, -1)));
        assertScore(query, target, mappings, 3, 2, false);
        // a nitro group drawn with charges matches one drawn without only as written: C-N and one N=O
        assertMappings(MCS.find(smi("C[N+](=O)[O-]")), smi("CN(=O)=O"), set(m(0, 1, 2, -1), m(0, 1, 3, -1)));
        assertMappings(MCS.find(smi("C[N+](=O)[O-]")).withMatching(ELEMENT), smi("CN(=O)=O"),
                       set(m(0, 1, 2, 3), m(0, 1, 3, 2)));
    }

    @Test
    void testStereochemistryNotChecked() throws Exception {
        // an enantiomer maps whole, with or without the STEREOCHEMISTRY type
        IAtomContainer query = smi("C[C@H](N)C(=O)O");
        IAtomContainer target = smi("C[C@@H](N)C(=O)O");
        assertMappings(MCS.find(query), target, set(m(0, 1, 2, 3, 4, 5)));
        assertMappings(MCS.find(query).withMatching(ELEMENT, SINGLE_OR_AROMATIC, STEREOCHEMISTRY), target,
                       set(m(0, 1, 2, 3, 4, 5)));
    }

    @Test
    void testIsotopes() throws Exception {
        IAtomContainer labelled = smi("[13CH3]C(=O)O");
        IAtomContainer acid = smi("CC(=O)O");
        // isotopes are ignored by default
        assertMappings(MCS.find(labelled), acid, set(m(0, 1, 2, 3)));
        assertMappings(MCS.find(acid), labelled, set(m(0, 1, 2, 3)));
        // with ISOTOPE the 13C matches no atom of the target
        List<int[]> mappings = assertMappings(MCS.find(labelled).withMatching(ELEMENT, ISOTOPE), acid,
                set(m(-1, 1, 2, 3), m(-1, 1, 3, 2)));
        assertScore(labelled, acid, mappings, 3, 2, false);
        // a query atom without a mass number matches any isotope, see QueryAtomContainer.create
        assertMappings(MCS.find(acid).withMatching(ELEMENT, ISOTOPE), labelled, set(m(0, 1, 2, 3), m(0, 1, 3, 2)));
        // deuterium and hydrogen
        assertMappings(MCS.find(smi("[2H]OC")), smi("[H]OC"), set(m(0, 1, 2)));
        assertMappings(MCS.find(smi("[2H]OC")).withMatching(ELEMENT, ISOTOPE), smi("[H]OC"), set(m(-1, 1, 2)));
    }

    @Test
    void testPseudoAtoms() throws Exception {
        // a pseudo atom from SMILES has the symbol R, and setting its label does not change it
        Assertions.assertEquals("R", smi("*").getAtom(0).getSymbol());
        Assertions.assertEquals("R", pseudo("*", "R1").getAtom(0).getSymbol());
        // R matches R
        IAtomContainer query = pseudo("*CCO", "R");
        IAtomContainer target = pseudo("*CCO", "R");
        Assertions.assertEquals(set(m(0, 1, 2, 3)), exact(query, target, true));
        assertScore(query, target, assertMappings(MCS.find(query), target, set(m(0, 1, 2, 3))), 4, 3, true);
        // R does not match C
        target = smi("CCCO");
        Assertions.assertEquals(set(m(-1, 1, 2, 3)), exact(query, target, true));
        assertMappings(MCS.find(query), target, set(m(-1, 1, 2, 3)));
        assertMappings(MCS.find(target), query, set(m(-1, 1, 2, 3)));
        // '*' from SMILES and a pseudo atom labelled R1 match by their symbol
        query = smi("*CCO");
        target = pseudo("OCC*", "R1");
        Assertions.assertEquals(set(m(3, 2, 1, 0)), exact(query, target, true));
        assertMappings(MCS.find(query), target, set(m(3, 2, 1, 0)));
        // only pseudo atoms in common
        assertMappings(MCS.find(smi("*")), pseudo("N*", "R"), set(m(1)));
        assertNothingInCommon(MCS.find(smi("*")), smi("CCO"));
        // with ELEMENT a pseudo atom of the query matches any atom, and an element no pseudo atom of the target
        assertMappings(MCS.find(smi("*CC")).withMatching(ELEMENT), smi("NCC"), set(m(0, 1, 2)));
        assertMappings(MCS.find(smi("NCC")).withMatching(ELEMENT), smi("*CC"), set(m(-1, 1, 2), m(-1, 2, 1)));
    }

    @Test
    void testExplicitHydrogens() throws Exception {
        // hydrogens on both sides: the carbon on the carbon, the 24 orders of the four hydrogens
        IAtomContainer query = smi("[H]C([H])([H])[H]");
        IAtomContainer target = smi("C([H])([H])([H])[H]");
        Set<String> expected = exact(query, target, true);
        Assertions.assertEquals(24, expected.size());
        List<int[]> mappings = assertMappings(MCS.find(query), target, expected);
        assertScore(query, target, mappings, 5, 4, true);
        for (int[] mapping : mappings) {
            Assertions.assertEquals(0, mapping[1]);
        }
        // hydrogens on one side only are left out: implicit hydrogens are not atoms
        assertMappings(MCS.find(query), smi("C"), set(m(-1, 0, -1, -1, -1)));
        assertMappings(MCS.find(smi("C")), query, set(m(1)));
        assertMappings(MCS.find(smi("[H]OCC")), smi("CCO"), set(m(-1, 2, 1, 0)));
        assertMappings(MCS.find(smi("CCO")), smi("[H]OCC"), set(m(3, 2, 1)));
        assertMappings(MCS.find(smi("[H]OCC")).withMatching(ELEMENT), smi("CCO"), set(m(-1, 2, 1, 0)));
        // methanol with the hydroxy hydrogen on both sides
        assertMappings(MCS.find(smi("[H]OC")), smi("CO[H]"), set(m(2, 1, 0)));
    }

    @Test
    void testRingMatching() throws Exception {
        IAtomContainer query = smi("CC1CCCCC1");
        IAtomContainer target = smi("CCCCCCC");
        Cycles.markRingAtomsAndBonds(query);
        Cycles.markRingAtomsAndBonds(target);
        Assertions.assertEquals(7, atoms(MCS.find(query).match(target)));
        // ring atoms of the query may only be mapped to ring atoms
        Assertions.assertEquals(1, atoms(MCS.find(query).withMatching(ELEMENT, IS_IN_RING).match(target)));
        // the chain atoms of heptane may be mapped to ring atoms, unless IS_IN_CHAIN keeps them to chain atoms
        Assertions.assertEquals(7, atoms(MCS.find(target).withMatching(ELEMENT, IS_IN_RING).match(query)));
        Assertions.assertEquals(1, atoms(MCS.find(target).withMatching(ELEMENT, IS_IN_RING, IS_IN_CHAIN).match(query)));
        Assertions.assertEquals(1, atoms(MCS.find(query).withMatching(ELEMENT, IS_IN_RING, IS_IN_CHAIN).match(target)));

        // the ring and chain matching of the documentation still maps part of a ring:
        // cyclopentane on cyclohexane, a ring bond left out, 5 x 6 x 2 ways
        Expr.Type[] ringsAndChains = {ELEMENT, SINGLE_OR_AROMATIC, IS_IN_RING, IS_IN_CHAIN};
        IAtomContainer cyclopentane = ring("C1CCCC1");
        IAtomContainer cyclohexane = ring("C1CCCCC1");
        List<int[]> mappings = MCS.find(cyclopentane).withMatching(ringsAndChains).matchAll(cyclohexane);
        Assertions.assertEquals(60, mappings.size());
        assertScore(cyclopentane, cyclohexane, mappings, 5, 4, true);
    }

    @Test
    void testBondWithoutOrder() throws Exception {
        // a target bond with no order, or UNSET, matches no single or double query bond when orders are compared;
        // ELEMENT ignores the order on either side
        Set<String> single = set(m(0, 1, -1), m(1, 0, -1));
        for (IBond.Order order : new IBond.Order[]{null, IBond.Order.UNSET}) {
            IAtomContainer query = smi("CC=C");
            IAtomContainer target = smi("CC=C");
            target.getBond(1).setOrder(order);
            assertMappings(MCS.find(query).withMatching(ELEMENT, SINGLE_OR_AROMATIC), target, single);
            assertMappings(MCS.find(query).withMatching(ELEMENT, ORDER), target, single);
            Assertions.assertEquals(3, atoms(MCS.find(query).withMatching(ELEMENT).match(target)));
            Assertions.assertEquals(3, atoms(MCS.find(target).withMatching(ELEMENT).match(query)));
        }
        // a query bond with an UNSET order is searched, and matches no other order
        IAtomContainer query = smi("CC=C");
        query.getBond(1).setOrder(IBond.Order.UNSET);
        assertMappings(MCS.find(query).withMatching(ELEMENT, ORDER), smi("CC=C"), single);
    }

    // query molecules and SMARTS

    @Test
    void testWithMatchingOnQueryMolecule() throws Exception {
        // a query molecule is matched by its own expressions
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MCS.find(QueryAtomContainer.create(smi("CC"), ELEMENT)).withMatching(ELEMENT));
        MCS query = MCS.find(smarts("[N,O]~C"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> query.withMatching(ELEMENT));
        Assertions.assertThrows(IllegalArgumentException.class, () -> query.withMatching());
        // as is a molecule with one query atom
        IAtomContainer mol = smi("CC");
        mol.addAtom(new QueryAtom(ELEMENT, 8));
        mol.addBond(1, 2, IBond.Order.SINGLE);
        Assertions.assertThrows(IllegalArgumentException.class, () -> MCS.find(mol).withMatching(ELEMENT));
        // or one query bond
        IAtomContainer mol2 = smi("CC.O");
        mol2.addBond(new QueryBond(mol2.getAtom(1), mol2.getAtom(2), Expr.Type.TRUE));
        Assertions.assertThrows(IllegalArgumentException.class, () -> MCS.find(mol2).withMatching(ELEMENT));
        // both are searched: the query atom matches O and the query bond any bond
        assertMappings(MCS.find(mol), smi("CCO"), set(m(0, 1, 2)));
        assertMappings(MCS.find(mol2), smi("CC=O"), set(m(0, 1, 2)));
    }

    @Test
    void testSmartsQuery() throws Exception {
        IAtomContainer query = smarts("[N,O]~C");
        assertMappings(MCS.find(query), smi("CCO"), set(m(2, 1)));
        // the ~ bond matches any bond, so also the double bond
        assertMappings(MCS.find(query), smi("C=O"), set(m(1, 0)));
        assertMappings(MCS.find(query), smi("NCCO"), set(m(0, 1), m(3, 2)));
        assertNothingInCommon(MCS.find(query), smi("[Na+].[Cl-]"));
    }

    @Test
    void testSmartsStereochemistryNotChecked() throws Exception {
        // the local predicates defer configuration checks; MCS does not apply the mapping filter of SmartsPattern
        String[][] pairs = {{L_ALA, D_ALA}, {"C/C=C/C", "C/C=C\\C"}};
        for (String[] pair : pairs) {
            IAtomContainer query = smarts(pair[0]), target = smi(pair[1]);
            SmartsPattern.prepare(target);
            Assertions.assertFalse(SmartsPattern.create(pair[0]).matches(target));
            MCS mcs = MCS.find(query);
            Assertions.assertEquals(query.getAtomCount(), atoms(mcs.match(target)));
            Assertions.assertThrows(IllegalArgumentException.class, mcs::withStereochemistry);
        }
        // a recursive predicate still uses the complete pattern's own stereochemistry check
        MCS recursive = MCS.find(smarts("[$(" + L_ALA + ")]"));
        Assertions.assertArrayEquals(new int[]{0}, recursive.match(smi(L_ALA)));
        assertNothingInCommon(recursive, smi(D_ALA));
    }

    @Test
    void testRecursiveSmartsQuery() throws Exception {
        // only the carbon next to the oxygen is a C bonded to O
        assertMappings(MCS.find(smarts("[$(CO)]C")), smi("CCCO"), set(m(2, 1)));
        // without the recursive part, both C-C bonds either way round
        assertMappings(MCS.find(smarts("CC")), smi("CCCO"), set(m(0, 1), m(1, 0), m(1, 2), m(2, 1)));
        // two recursive atoms that compare equal as expressions are still told apart by what they match
        IAtomContainer target = smi("OCCN");
        SmartsPattern.prepare(target);
        assertMappings(MCS.find(smarts("[$(C~O)]-[$(C~N)]")), target, set(m(1, 2)));
    }

    @Test
    void testRingSmartsQuery() throws Exception {
        IAtomContainer query = smarts("[C;R]-[C;!R]");
        IAtomContainer target = smi("CC1CCCCC1");
        // ring flags not set: no atom is in a ring, so only the chain atom of the query is mapped
        for (IAtom atom : target.atoms()) {
            atom.setIsInRing(false);
        }
        for (IBond bond : target.bonds()) {
            bond.setIsInRing(false);
        }
        Set<String> chain = new HashSet<>();
        for (int i = 0; i < target.getAtomCount(); i++) {
            chain.add(Arrays.toString(m(-1, i)));
        }
        assertMappings(MCS.find(query), target, chain);
        // prepared: the ring atom bonded to the methyl, and the methyl
        SmartsPattern.prepare(target);
        assertMappings(MCS.find(query), target, set(m(1, 0)));
        // an aromatic SMARTS ring on phenol written as a Kekule structure
        query = smarts("c1ccccc1");
        target = smi("OC1=CC=CC=C1");
        assertNothingInCommon(MCS.find(query), target);
        SmartsPattern.prepare(target);
        assertMappings(MCS.find(query), target, ringMappings(6, 1, 2, 3, 4, 5, 6));
        // ring sizes: the eleven bonds of the decalin, either way round; not the linker, not the cyclopropane
        target = smi("C1CCC2CCCCC2C1CC1CC1");
        SmartsPattern.prepare(target);
        List<int[]> mappings = MCS.find(smarts("[C;r6]~[C;r6]")).matchAll(target);
        Assertions.assertEquals(22, mappings.size());
        Assertions.assertEquals(22, keys(mappings).size());
        for (int[] mapping : mappings) {
            Assertions.assertEquals(2, atoms(mapping));
        }
    }

    @Test
    void testSmartsInQueryAtomContainer() throws Exception {
        // a SMARTS query in a QueryAtomContainer gives the same mappings as in a normal molecule
        String[] patterns = {"[N,O]~C(=O)c1ccccc1", "[$(CO)]C", "[C;$(C=O)]~[O,N]", "[#6;R]-[#6;!R]", "[r6]~[r5]",
                             "[c;x2]:[c;x3]", "a:a-!@a", "[#6]@[#6]", "[#6]!@[#6]", "C=,#C", "[NX3;H2,H1;!$(NC=O)]"};
        String[] targets = {ASPIRIN, "CN1CCC[C@H]1c1cccnc1", "c1ccc2[nH]ccc2c1", "CC(=O)NCO", "C1CCC2(CC1)CC2"};
        int found = 0;
        for (String pattern : patterns) {
            IAtomContainer query = new QueryAtomContainer(SilentChemObjectBuilder.getInstance());
            Assertions.assertTrue(Smarts.parse(query, pattern), pattern);
            for (String smiles : targets) {
                IAtomContainer target = smi(smiles);
                SmartsPattern.prepare(target);
                Set<String> expected = keys(MCS.find(smarts(pattern)).matchAll(target));
                Assertions.assertEquals(expected, keys(MCS.find(query).matchAll(target)), pattern + " / " + smiles);
                found += expected.size();
            }
        }
        Assertions.assertTrue(found > 100, found + " mappings");
    }

    // the mappings returned

    @Test
    void testUnmappedQueryAtoms() throws Exception {
        // the query is the larger molecule
        IAtomContainer query = smi("OCCc1ccccc1");
        IAtomContainer target = smi("c1ccccc1");
        int[] mapping = MCS.find(query).match(target);
        Assertions.assertEquals(9, mapping.length);
        Assertions.assertArrayEquals(new int[]{-1, -1, -1}, Arrays.copyOf(mapping, 3));
        Assertions.assertEquals(6, atoms(mapping));
    }

    @Test
    void testNoCommonAtoms() throws Exception {
        for (String[] pair : new String[][]{{"", "C"}, {"C", ""}, {"N", "C"}}) {
            MCS mcs = MCS.find(smi(pair[0]));
            Assertions.assertEquals(0, mcs.match(smi(pair[1])).length);
            Assertions.assertTrue(mcs.matchAll(smi(pair[1])).isEmpty());
        }
        // an empty molecule has no atom in common with any molecule, whatever the matching
        for (String[] pair : new String[][]{{"", ""}, {"", "CCO"}, {"c1ccccc1", ""}}) {
            MCS mcs = MCS.find(smi(pair[0]));
            for (MCS search : new MCS[]{mcs, mcs.withMatching(ELEMENT), mcs.withMatching()}) {
                assertNothingInCommon(search, smi(pair[1]));
            }
        }
    }

    @Test
    void testMatchAllIsUnmodifiable() throws Exception {
        List<int[]> mappings = MCS.find(smi("CC")).matchAll(smi("CCC"));
        Assertions.assertEquals(4, mappings.size());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> mappings.add(new int[]{0, 1}));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> mappings.remove(0));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> mappings.set(0, new int[]{0, 1}));
        Assertions.assertThrows(UnsupportedOperationException.class, mappings::clear);
        List<int[]> none = MCS.find(smi("N")).matchAll(smi("C"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> none.add(new int[]{0}));
    }

    @Test
    void testMatchAllWithLimit() throws Exception {
        // cyclopentane on cyclohexane: 10 of the 60 when 10 are asked for; cyclohexane on hexane: all 12 of 13
        MCS cyclopentane = MCS.find(smi("C1CCCC1"));
        Set<String> sixty = keys(cyclopentane.matchAll(smi("C1CCCCC1"), 100));
        Set<String> ten = keys(cyclopentane.matchAll(smi("C1CCCCC1"), 10));
        Assertions.assertTrue(sixty.size() == 60 && ten.size() == 10 && sixty.containsAll(ten));
        Assertions.assertEquals(12, keys(MCS.find(smi("C1CCCCC1")).matchAll(smi("CCCCCC"), 12)).size());
        Assertions.assertEquals(12, MCS.find(smi("C1CCCCC1")).matchAll(smi("CCCCCC"), 13).size());
    }

    @Test
    @Tag("SlowTest")
    void testMatchAllWithLimitCompleteGraphs() throws Exception {
        // K5 on K7: 7 x 6 x 5 x 4 x 3 embeddings, each with all 10 bonds; K7 on K5 the same turned round, and pair
        // by pair with ELEMENT
        IAtomContainer k5 = completeGraph(5);
        IAtomContainer k7 = completeGraph(7);
        Set<String> embeddings = exact(k5, k7, true);
        Assertions.assertEquals(2520, embeddings.size());
        assertScore(k5, k7, MCS.find(k5).matchAll(k7, 3000), 5, 10, true);
        assertLimits(MCS.find(k5), k7, embeddings);
        assertLimits(MCS.find(k7), k5, exact(k7, k5, true));
        assertLimits(MCS.find(k7).withMatching(ELEMENT), k5, exact(k7, k5, false));
    }

    @Test
    @Tag("SlowTest")
    void testMatchAllWithLimitExplicitHydrogens() throws Exception {
        // cyclohexane on hexane: 12 ways for the C, 2 for the H of each inner CH2, 3 x 2 at each end, 12 x 2^4 x 6^2
        IAtomContainer query = withHydrogens("C1CCCCC1");
        IAtomContainer target = withHydrogens("CCCCCC");
        List<int[]> mappings = MCS.find(query).matchAll(target, 10000);
        Assertions.assertEquals(6912, keys(mappings).size());
        assertScore(query, target, mappings, 18, 17, true);
        // ibuprofen on itself: (3!)^3 for the methyl H, 2 for the CH2 H, 2 for the isobutyl methyls, 2 for the
        // ring turned over, which a Kekule structure cannot be
        query = withHydrogens("CC(C)Cc1ccc(cc1)C(C)C(=O)O");
        target = withHydrogens("CC(C)Cc1ccc(cc1)C(C)C(=O)O");
        mappings = MCS.find(query).matchAll(target, 2000);
        Assertions.assertEquals(1728, keys(mappings).size());
        assertScore(query, target, mappings, 33, 33, true);
    }

    @Test
    void testMatchAllWithLimitArguments() throws Exception {
        MCS mcs = MCS.find(smi("CC"));
        IAtomContainer target = smi("CCC");
        for (int limit : new int[]{0, -1}) {
            IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
                    () -> mcs.matchAll(target, limit));
            Assertions.assertEquals("The limit must be greater than zero: " + limit, e.getMessage());
        }
        // the limit is checked first
        NullPointerException e = Assertions.assertThrows(NullPointerException.class, () -> mcs.matchAll(null, 5));
        Assertions.assertEquals("target", e.getMessage());
        Assertions.assertThrows(IllegalArgumentException.class, () -> mcs.matchAll(null, 0));
    }

    @Test
    void testCompleteRings() throws Exception {
        // ring atoms on ring atoms only, a ring on a ring of its size
        assertCompleteRings("C1CCCCC1", "CCCCCC", score(6, 5, 12), NONE);
        assertCompleteRings("CCCCCC", "C1CCCCC1", score(6, 5, 12), NONE);
        assertCompleteRings("c1ccccc1", "CCCCCC", score(1, 0, 36), NONE);
        assertCompleteRings("C1CCCCC1", "CC1CCCCC1", score(6, 6, 12), SAME);
        assertCompleteRings("C1CCCC1", "C1CCCCC1", score(5, 4, 60), NONE);
        assertCompleteRings("OCC1CCCCC1", "C1CCCCC1", score(6, 6, 12), SAME);
        // naphthalene: the rings are the relevant rings
        assertCompleteRings("c1ccc2ccccc2c1", "c1ccc2ccccc2c1", score(10, 11, 4), SAME);
        // a ring atom with a substituent
        assertCompleteRings("Cc1ccccc1", "CC1CCCCC1", score(2, 1, 14), score(1, 0, 1));
        assertCompleteRings("Cc1ccccc1", "CC1CCCCC1", score(7, 7, 2), SAME, BY_ELEMENT);
        // a SMARTS atom on no ring of the pattern is a chain atom
        assertCompleteRings("[C;R]C(=O)[N,O]", "NC(=O)C1CCCCC1", score(4, 3, 1), score(3, 2, 1), SMARTS);
        assertCompleteRings("[#6]1[#6][#6][#6][#6]1", "OC(=O)c1ccccc1", score(5, 4, 80), NONE, SMARTS);
        assertCompleteRings("cC(=O)O", "OC(=O)c1ccccc1", score(4, 3, 1), score(3, 2, 1), SMARTS);
    }

    @Test
    @Tag("SlowTest")
    void testCompleteRingsSlow() throws Exception {
        // naphthalene, decalin, hydrindane, spiro[4.5]decane, norbornane, bicyclo[2.2.0]hexane,
        // bicyclo[2.2.2]octane, adamantane and cubane: the rings are the relevant rings, a perimeter is not one
        assertCompleteRings("c1ccc2ccccc2c1", "c1ccc2cccc2cc1", score(10, 10, 20), NONE);
        assertCompleteRings(CUBANE, "C1CCC1", score(4, 4, 48), SAME);
        assertCompleteRings("c1ccc2ccccc2c1", "c1ccccc1", score(6, 6, 24), SAME);
        assertCompleteRings("C1CCC2CCCCC2C1", "C1CCCCC1", score(6, 6, 24), SAME);
        assertCompleteRings("C1CCC2CCCCC2C1", "C1CCCCCCCCC1", score(10, 10, 20), NONE);
        assertCompleteRings("C1CCC2CCCC2C1", "C1CCCCC1", score(6, 6, 12), SAME);
        assertCompleteRings("C1CCC2CCCC2C1", "C1CCC2(CC1)CCCC2", score(9, 9, 16), score(6, 6, 12));
        assertCompleteRings("C1CCC2(CC1)CCCC2", "C1CCCCC1", score(6, 6, 12), SAME);
        assertCompleteRings("C1CCC2(CC1)CCCC2", "C1CCC(CC1)C1CCCC1", score(10, 10, 16), score(6, 6, 12));
        assertCompleteRings("C1CC2CCC1C2", "C1CCCCC1", score(6, 6, 12), NONE);
        assertCompleteRings("C1CC2CCC1C2", "C1CCCC1", score(5, 5, 20), SAME);
        assertCompleteRings("C1CC2CCC12", "C1CCCCC1", score(6, 6, 12), NONE);
        assertCompleteRings("C1CC2CCC1CC2", "C1CCCCC1", score(6, 6, 36), SAME);
        assertCompleteRings(ADAMANTANE, "C1CCCCC1", score(6, 6, 48), SAME);
        // adamantane on cubane, also with explicit hydrogens
        assertCompleteRings(ADAMANTANE, CUBANE, score(8, 8, 1000), NONE);
        assertCompleteRings(ADAMANTANE, CUBANE, score(16, 16, 1000), score(1, 0, 128), BY_ELEMENT, EXPLICIT_H);
        // a ring atom with a substituent, linked rings, and a bond whose ring is not whole
        assertCompleteRings("c1ccccc1C1CCCCC1", "c1ccccc1-c1ccccc1", score(7, 7, 4), score(6, 6, 24));
        assertCompleteRings("c1ccccc1C1CCCCC1", "c1ccccc1-c1ccccc1", score(12, 13, 8), SAME, BY_ELEMENT);
        assertCompleteRings("OC(=O)c1ccccc1", "OC(=O)C1CCCCC1", score(4, 3, 1), score(3, 2, 1));
        assertCompleteRings("OC(=O)c1ccccc1", "OC(=O)C1CCCCC1", score(9, 9, 4), SAME, BY_ELEMENT);
        assertCompleteRings("c1ccccc1Cc1ccccc1", "c1ccc2c(c1)Cc1ccccc1-2", score(13, 14, 8), score(6, 6, 48));
        assertCompleteRings("c1ccccc1-c1ccccc1", "c1ccc2c(c1)-c1ccccc1-2", score(12, 13, 16), score(6, 6, 48));
        assertCompleteRings("c1ccc2c(c1)-c1ccccc1-2", "c1ccc2c(c1)Cc1ccccc1-2", score(12, 13, 16), score(6, 6, 48));
        assertCompleteRings("C1CC1c1ccccc1", "C1CC(C1)c1ccccc1", score(9, 9, 12), score(6, 6, 12));
        assertCompleteRings("CCc1ccccc1", "C1Cc2ccccc2C1", score(8, 8, 4), score(6, 6, 12));
        assertCompleteRings("C1CCc2ccccc2C1", "C1Cc2ccccc2C1", score(9, 9, 12), score(6, 6, 12));
        // chlorpromazine and promazine as Kekule structures, then aromatic
        String chlorpromazine = "CN(C)CCCN1C2=CC=CC=C2SC3=C1C=C(C=C3)Cl";
        String promazine = "CN(C)CCCN1C2=CC=CC=C2SC2=CC=CC=C21";
        assertCompleteRings(chlorpromazine, promazine, score(20, 21, 8), score(6, 6, 24));
        assertCompleteRings(chlorpromazine, promazine, score(20, 22, 4), SAME, DAYLIGHT);
        // chains, where the list is the same in the same order; testosterone and estradiol
        assertCompleteRings("CCCCCC", "CCCC(C)CCC", score(6, 5, 4), SAME_ORDER);
        assertCompleteRings("CC12CCC3C(C1CCC2O)CCC4=CC(=O)CCC34C", "CC12CCC3C(C1CCC2O)CCC4=C3C=CC(=C4)O",
                            score(19, 19, 2), score(11, 12, 1));
    }

    @Test
    void testCompleteRingsTooManyRings() throws Exception {
        MCS cyclohexane = MCS.find(smi("C1CCCCC1")).withCompleteRings();
        // k six-membered rings joined para in a macrocycle have 2^k + k relevant rings: 521 are searched
        Assertions.assertEquals(score(6, 6, 108), scoreOf(smi("C1CCCCC1"), cpp(9), cyclohexane.matchAll(cpp(9)), true));
        Intractable e = Assertions.assertThrows(Intractable.class, () -> cyclohexane.match(cpp(10)));
        Assertions.assertEquals("MCS search too large: a ring system of 60 atoms has too many rings", e.getMessage());
        // RelevantCycles would count 32 rings of [32]CPP, as its count overflows
        Assertions.assertThrows(Intractable.class, () -> cyclohexane.match(cpp(32)));
    }

    @Test
    @Tag("SlowTest")
    void testCompleteRingsLargeRingSystems() throws Exception {
        MCS cyclohexane = MCS.find(smi("C1CCCCC1")).withCompleteRings();
        // [1025]acene has at least as many rings as its 1025 independent cycles, so it is refused before they are found
        Assertions.assertTimeout(Duration.ofSeconds(1), () -> {
            Intractable acene = Assertions.assertThrows(Intractable.class, () -> cyclohexane.match(acene(1025)));
            Assertions.assertEquals("MCS search too large: a ring system of 4102 atoms has too many rings",
                                    acene.getMessage());
        });
        // a benzenoid of 10 x 10 rings is searched, one of 12 x 12 is not
        Assertions.assertEquals(6, atoms(cyclohexane.match(sheet(10))));
        Assertions.assertThrows(Intractable.class, () -> cyclohexane.match(sheet(12)));
        // C60 on itself: the 120 symmetries
        IAtomContainer c60 = smi(C60);
        Assertions.assertEquals(score(60, 90, 120), scoreOf(c60, c60, MCS.find(c60).withCompleteRings().matchAll(c60),
                                                            true));
    }

    @Test
    void testCompleteRingsOptions() throws Exception {
        IAtomContainer hexane = smi("CCCCCC");
        MCS mcs = MCS.find(smi("C1CCCCC1"));
        MCS rings = mcs.withCompleteRings();
        Assertions.assertNotSame(mcs, rings);
        Assertions.assertEquals(12, mcs.matchAll(hexane).size());
        // the other options keep it, before or after it
        for (MCS search : new MCS[]{rings, rings.withMatching(ELEMENT), rings.withTimeout(1, TimeUnit.MINUTES),
                                    rings.withStereochemistry(), rings.withDisconnected(1),
                                    mcs.withMatching().withCompleteRings(),
                                    mcs.withStereochemistry().withCompleteRings(),
                                    mcs.withDisconnected(1).withCompleteRings()}) {
            assertNothingInCommon(search, hexane);
        }
        // ring flags are not read, so hexane flagged as a ring is still a chain, and they are not set
        for (IAtom atom : hexane.atoms()) {
            atom.setIsInRing(true);
        }
        assertNothingInCommon(rings, hexane);
        IAtomContainer unflagged = chain(6);
        unflagged.addBond(5, 0, IBond.Order.SINGLE);
        Assertions.assertEquals(6, atoms(rings.match(unflagged)));
        for (IAtom atom : unflagged.atoms()) {
            Assertions.assertFalse(atom.isInRing());
        }
        // the checks of rings stop with the search, see also testInterruptedWhileSearching and
        // testSharedBetweenThreads
        MCS hard = MCS.find(smi(CORONENE)).withCompleteRings().withTimeout(1, TimeUnit.MILLISECONDS);
        Assertions.assertThrows(Intractable.class, () -> hard.match(smi(C60)));
    }

    // stereochemistry

    @Test
    void testStereochemistry() throws Exception {
        // L-alanine on L- and D-alanine and -cysteine: the same arrangement, whatever the CIP labels
        assertStereochemistry(L_ALA, L_ALA, score(6, 5, 1), score(6, 5, 1));
        assertStereochemistry(L_ALA, D_ALA, score(5, 4, 2), score(6, 5, 1));
        assertStereochemistry(L_ALA, "N[C@@H](CS)C(=O)O", score(6, 5, 1), score(6, 5, 1));
        assertStereochemistry(L_ALA, "N[C@H](CS)C(=O)O", score(5, 4, 2), score(6, 5, 1));
        // a configuration of one side only, or in a racemic group, matches either way
        assertStereochemistry(L_ALA, "NC(C)C(=O)O", score(6, 5, 1), score(6, 5, 1));
        assertStereochemistry("NC(C)C(=O)O", L_ALA, score(6, 5, 1), score(6, 5, 1));
        assertStereochemistry(L_ALA, L_ALA + " |&1:1|", score(6, 5, 1), score(6, 5, 1));
        assertStereochemistry("C[C@@H](O)CC", "CC(O)C", score(4, 3, 2), score(4, 3, 2));
        // an explicit hydrogen stands for the implicit one, also searched the other way round
        assertStereochemistry(L_ALA, "N[C@@]([H])(C)C(=O)O", score(6, 5, 1), score(6, 5, 1));
        assertStereochemistry(L_ALA, "N[C@]([H])(C)C(=O)O", score(5, 4, 2), score(6, 5, 1));
        assertStereochemistry("N[C@]([H])(C)C(=O)O", L_ALA, score(5, 4, 2), score(6, 5, 1));
        // the only embedding breaks the configuration
        assertStereochemistry(L_ALA, "N[C@H](C)C(=O)OC", score(5, 4, 2), score(6, 5, 1));
        // E on Z: C=C-C has no configuration, and without orders a double bond lies on a single one
        assertStereochemistry("C/C=C/C", "C/C=C\\C", score(3, 2, 4), score(4, 3, 2));
        assertStereochemistry("C/C=C/C", "C(\\C)=C/C", score(4, 3, 2), score(4, 3, 2));
        assertStereochemistry("C/C=C/C", "C/C=C\\C", score(3, 2, 8), score(4, 3, 2), BY_ELEMENT);
        assertStereochemistry("F/C(Cl)=C/F", "Cl\\C(F)=C/F", score(5, 4, 1), score(5, 4, 1));
        assertStereochemistry("F/C(Cl)=C/F", "F/C(Cl)=C\\F", score(4, 3, 1), score(5, 4, 1));
        // a rotation of chiral butane-2,3-diol keeps the configurations, a mirror of meso does not
        assertStereochemistry("C[C@@H](O)[C@@H](C)O", "C[C@@H](O)[C@@H](C)O", score(6, 5, 2), score(6, 5, 2));
        assertStereochemistry("C[C@@H](O)[C@H](C)O", "C[C@@H](O)[C@H](C)O", score(6, 5, 1), score(6, 5, 2));
        assertStereochemistry("C[C@@H](O)[C@@H](C)O", "C[C@@H](O)[C@H](C)O", score(5, 4, 4), score(6, 5, 2));
        // a quaternary centre is fixed by three neighbours; allenes are not checked
        assertStereochemistry("C[C@](N)(O)CC", "C[C@@](N)(O)Cl", score(4, 3, 1), score(4, 3, 2));
        assertStereochemistry("CC=[C@]=CC", "CC=[C@@]=CC", score(5, 4, 2), score(5, 4, 2));
        // without orders the oxygens trade places; with explicit hydrogens NH2 goes, never the methyl
        assertStereochemistry(L_ALA, D_ALA, score(5, 4, 4), score(6, 5, 2), BY_ELEMENT);
        assertStereochemistry(L_ALA, D_ALA, score(9, 8, 6), score(13, 12, 12), EXPLICIT_H);
        assertMappings(MCS.find(smi(L_ALA)).withStereochemistry(), smi(D_ALA),
                       set(m(-1, 1, 2, 3, 4, 5), m(0, 1, -1, 3, 4, 5)));
        assertMappings(MCS.find(smi("F/C(Cl)=C/F")).withStereochemistry(), smi("F/C(Cl)=C\\F"), set(m(0, 1, 2, 3, -1)));
        // from molfiles without the chiral flag each alanine is in a racemic group, so L and D match whole
        Assertions.assertEquals(score(6, 5, 1), scoreOf(alanine(6, 0), alanine(1, 0), true, true));
        Assertions.assertEquals(score(5, 4, 2), scoreOf(alanine(6, 1), alanine(1, 1), true, true));
    }

    @Test
    @Tag("SlowTest")
    void testStereochemistrySlow() throws Exception {
        // threonines: a racemic or relative group is inverted as a whole, a group of one centre alone
        assertStereochemistry(L_THR, L_ALLO, score(7, 6, 2), score(8, 7, 1));
        assertStereochemistry(L_THR, D_THR, score(6, 5, 3), score(8, 7, 1));
        assertStereochemistry(L_THR, D_THR + " |&1:1,2|", score(8, 7, 1), score(8, 7, 1));
        assertStereochemistry(L_THR, L_ALLO + " |&1:1,2|", score(7, 6, 3), score(8, 7, 1));
        assertStereochemistry(L_THR, L_ALLO + " |o1:1,2|", score(7, 6, 3), score(8, 7, 1));
        assertStereochemistry(L_THR, L_ALLO + " |&1:1,&2:2|", score(8, 7, 1), score(8, 7, 1));
        assertStereochemistry(L_THR, L_ALLO, score(7, 6, 4), score(8, 7, 2), BY_ELEMENT);
        // a rotation of trans-1,2-dimethylcyclohexane keeps the configurations, a mirror of cis does not; on
        // cis, trans gives up a ring bond at one centre
        assertStereochemistry("C[C@@H]1CCCC[C@H]1C", "C[C@@H]1CCCC[C@H]1C", score(8, 8, 2), score(8, 8, 2));
        assertStereochemistry("C[C@@H]1CCCC[C@@H]1C", "C[C@@H]1CCCC[C@@H]1C", score(8, 8, 1), score(8, 8, 2));
        assertStereochemistry("C[C@@H]1CCCC[C@H]1C", "C[C@@H]1CCCC[C@@H]1C", score(8, 7, 2), score(8, 8, 2));
    }

    @Test
    void testStereochemistryOptions() throws Exception {
        IAtomContainer query = smi(L_ALA), target = smi(D_ALA);
        String before = snapshot(query) + " / " + snapshot(target);
        MCS mcs = MCS.find(query), stereo = mcs.withStereochemistry();
        Assertions.assertNotSame(mcs, stereo);
        Assertions.assertEquals(6, atoms(mcs.match(target)));
        // kept by the other options, before or after them
        for (MCS search : new MCS[]{stereo, stereo.withTimeout(1, TimeUnit.MINUTES), stereo.withMatching(ELEMENT),
                                    stereo.withCompleteRings(), stereo.withDisconnected(1),
                                    mcs.withDisconnected(1).withStereochemistry(),
                                    mcs.withMatching(ELEMENT).withCompleteRings()
                                            .withTimeout(1, TimeUnit.MINUTES).withStereochemistry()}) {
            Assertions.assertEquals(5, atoms(search.match(target)));
        }
        Assertions.assertEquals(before, snapshot(query) + " / " + snapshot(target));
        // the option applies to molecule queries; top-level SMARTS configurations are not checked
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> MCS.find(smarts(L_ALA)).withStereochemistry());
        Assertions.assertEquals("A query molecule is matched by its own expressions", e.getMessage());
        // STEREOCHEMISTRY in the matching changes nothing, with the option or without
        for (MCS search : new MCS[]{mcs, stereo}) {
            Assertions.assertArrayEquals(search.withMatching(ELEMENT).matchAll(target).toArray(),
                                         search.withMatching(ELEMENT, STEREOCHEMISTRY).matchAll(target).toArray());
        }
        assertSharedBetweenThreads(new MCS[]{MCS.find(smi(L_THR)).withStereochemistry()},
                                   L_THR, L_ALLO, D_THR, D_THR + " |&1:1,2|", L_ALLO + " |o1:1,2|");
        // saturated coronene on saturated C60, with configurations at random: a time limit stops the search as it
        // checks them
        Random random = new Random(20261006L);
        MCS hard = MCS.find(withConfigurations(smi(CORONENE.toUpperCase()), random)).withStereochemistry();
        IAtomContainer c60 = withConfigurations(smi(C60.toUpperCase()), random);
        Assertions.assertThrows(Intractable.class, () -> hard.withTimeout(1, TimeUnit.MILLISECONDS).match(c60));
    }

    @Test
    @Tag("SlowTest")
    void testStereochemistryInterrupted() throws Exception {
        // as in testStereochemistryOptions, an interrupt stops the search as it checks the configurations
        Random random = new Random(20261006L);
        MCS hard = MCS.find(withConfigurations(smi(CORONENE.toUpperCase()), random)).withStereochemistry();
        assertInterrupted(hard, withConfigurations(smi(C60.toUpperCase()), random));
    }

    @Test
    void testStereochemistryEmbeddings() throws Exception {
        // with explicit hydrogens on its enantiomer, about a million embeddings each break the configuration; they
        // are ruled out as they are built, so the search ends well within its time limit
        IAtomContainer query = withHydrogens("CC[C@@H](C(C)C)C(C)(C)C");
        IAtomContainer target = withHydrogens("CC[C@H](C(C)C)C(C)(C)C");
        MCS mcs = MCS.find(query).withStereochemistry().withTimeout(10, TimeUnit.SECONDS);
        int atoms = atoms(mcs.match(target));
        for (int[] mapping : mcs.matchAll(target)) {
            Assertions.assertTrue(atoms(mapping) == atoms && atoms < query.getAtomCount()
                                  && stereoOk(query, target, mapping));
        }
    }

    @Test
    void testStereochemistryWithCompleteRings() throws Exception {
        // the cyclopropanes: the ring stays whole, so a methyl goes where stereochemistry alone gives up a ring
        // bond
        assertStereochemistryWithCompleteRings("C[C@@H]1C[C@H]1C", "C[C@@H]1C[C@@H]1C",
                                               score(4, 4, 4), score(5, 5, 2), score(5, 4, 2));
        // a ring on a chain
        assertStereochemistryWithCompleteRings("C[C@@H]1CCCC[C@H]1C", "C[C@@H](CCCC)[C@@H](C)CC",
                                               score(1, 0, 20), score(1, 0, 20), score(8, 7, 4));
        // one of the options has nothing to do
        assertStereochemistryWithCompleteRings(L_ALA, D_ALA,
                                               score(5, 4, 2), score(6, 5, 1), score(5, 4, 2));
        assertStereochemistryWithCompleteRings("C1CCCCC1", "CCCCCC",
                                               NONE, NONE, score(6, 5, 12));
    }

    @Test
    @Tag("SlowTest")
    void testStereochemistryWithCompleteRingsSlow() throws Exception {
        // trans-1,2-dimethylcyclohexane on cis, and the other way round: the ring stays whole, so a methyl goes
        // where stereochemistry alone gives up a ring bond; cis- on trans-1,4
        assertStereochemistryWithCompleteRings("C[C@@H]1CCCC[C@H]1C", "C[C@@H]1CCCC[C@@H]1C",
                                               score(7, 7, 4), score(8, 8, 2), score(8, 7, 2));
        assertStereochemistryWithCompleteRings("C[C@@H]1CCCC[C@@H]1C", "C[C@@H]1CCCC[C@H]1C",
                                               score(7, 7, 4), score(8, 8, 2), score(8, 7, 2));
        assertStereochemistryWithCompleteRings("C[C@H]1CC[C@@H](C)CC1", "C[C@H]1CC[C@H](C)CC1",
                                               score(7, 7, 4), score(8, 8, 4), score(8, 7, 8));
        // the query embeds, and every embedding breaks a configuration
        assertStereochemistryWithCompleteRings("C[C@@H]1CCCC[C@H]1C", "C[C@@H]1CCCC[C@@H]1CC",
                                               score(7, 7, 4), score(8, 8, 2), score(8, 7, 10));
        // E- on Z-cyclooctene: a whole ring fixes the double bond, so no ring atom maps
        assertStereochemistryWithCompleteRings("C1CCC/C=C/CC1", "C1CCC/C=C\\CC1",
                                               NONE, score(8, 8, 2), score(7, 6, 12));
        // a centre outside the ring
        assertStereochemistryWithCompleteRings("C[C@H](O)C1CCCCC1", "C[C@@H](O)C1CCCCC1",
                                               score(8, 8, 4), score(9, 9, 2), score(8, 8, 4));
    }

    // disconnected MCS

    @Test
    void testDisconnected() throws Exception {
        // the two ethyls; no fragment has two bonds
        assertDisconnected("CCOCC", "CCSCC", 1, score(4, 2, 8), score(2, 1, 8));
        assertDisconnected("CCOCC", "CCSCC", 2, NONE);
        // butane frees an end for ethane
        assertDisconnected("CCCC.CC", "CCCCC", 1, score(5, 3, 16), score(4, 3, 4));
        assertDisconnected("CCCC.CC", "CCCCC", 2, score(4, 3, 4));
        assertDisconnected("CCCC.CC", "CCCCC", 4, NONE);
        // bonds first keeps the ring
        assertDisconnected("C1CC1.CC", "CC1CC1", 1, score(3, 3, 6));
        // either ester C-O breaks; with fragments of two bonds, the acyl-O, or an ethyl is left
        assertDisconnected("CCOC(C)=O", "CC(=O)O.OCC", 1, score(6, 4, 3), score(4, 3, 1));
        assertDisconnected("CCOC(C)=O", "CC(=O)O.OCC", 2, score(6, 4, 1));
        // no bonds; the O has no bond in common; methane has no bond, ethane too few for fragments of two
        assertDisconnected("[Na+].[Cl-]", "[Na+].[Cl-]", 1, NONE, score(1, 0, 2));
        assertDisconnected("CC.O", "OCC", 1, score(2, 1, 2), score(2, 1, 2));
        assertDisconnected("C", "C", 1, NONE, score(1, 0, 1));
        assertDisconnected("CC", "CC", 2, NONE, score(2, 1, 2));
        // the carbonyl O is left out, unless bonds match whatever their order
        assertDisconnected("CC(C)=O", "CC(C)O", 1, score(3, 2, 2), score(3, 2, 2));
        assertDisconnected("CC(C)=O", "CC(C)O", 1, score(4, 3, 2), BY_ELEMENT);
        // a SMARTS query in parts, matched by its own expressions: the C-C on either end of the chain's C-C,
        // and the C=O
        IAtomContainer target = smi("CCC=O");
        SmartsPattern.prepare(target);
        assertScores(MCS.find(smarts("CC.C=O")).withDisconnected(1), smarts("CC.C=O"), target, true, 1,
                     score(4, 2, 2), "CC.C=O");
        assertScores(MCS.find(smarts("CC.C=O")), smarts("CC.C=O"), target, true, 0, score(2, 1, 5), "CC.C=O");
    }

    @Test
    @Tag("SlowTest")
    void testDisconnectedSlow() throws Exception {
        // two rings around a changed linker
        assertDisconnected("C1CC1CCC1CC1", "C1CC1OC1CC1", 1, score(6, 6, 72), score(3, 3, 24));
        assertDisconnected("c1ccc(cc1)CCc1ccccc1", "c1ccc(cc1)Oc1ccccc1", 1, score(12, 12, 288), score(6, 6, 48));
        // benzene and formic acid on benzoic acid; cyclohexane on hexane
        assertDisconnected("c1ccccc1.OC=O", "OC(=O)c1ccccc1", 1, score(9, 8, 12), score(6, 6, 12));
        assertDisconnected("C1CCCCC1", "CCCCCC", 1, score(6, 5, 12), score(6, 5, 12));
        // methane on methanol and the other way round, 4 x 3 x 2 ways for the H; ethane on ethanol
        assertDisconnected("[H]C([H])([H])[H]", "[H]OC([H])([H])[H]", 1, score(4, 3, 24), score(4, 3, 24));
        assertDisconnected("[H]OC([H])([H])[H]", "[H]C([H])([H])[H]", 1, score(4, 3, 24), score(4, 3, 24));
        assertDisconnected("[H]C([H])([H])C([H])([H])[H]", "[H]OC([H])([H])C([H])([H])[H]", 1,
                           score(7, 6, 72), score(7, 6, 72));
        // split at the C=C, unless bonds match whatever their order
        assertDisconnected(PALMITIC, OLEIC, 1, score(18, 16, 12), score(11, 10, 1));
        assertDisconnected(PALMITIC, OLEIC, 1, score(18, 17, 2), score(18, 17, 2), BY_ELEMENT);
        // no bond in common, unless bonds match whatever their order
        assertDisconnected("c1ccccc1", "CCCCCC", 1, NONE, score(1, 0, 36));
        assertDisconnected("c1ccccc1", "CCCCCC", 1, score(6, 5, 12), score(6, 5, 12), BY_ELEMENT);
        // the Diels-Alder reaction: the bond orders change; by element, 2 of the 24 are the reaction's
        assertDisconnected("C=CC=C.C=C", "C1=CCCCC1", 1, score(4, 2, 12));
        assertDisconnected("C=CC=C.C=C", "C1=CCCCC1", 1, score(6, 4, 24), BY_ELEMENT);
    }

    @Test
    void testDisconnectedAtomOrder() throws Exception {
        // butane and ethane on pentane, whatever the order of the atoms, and either way round
        IAtomContainer query = smi("CCCC.CC");
        IAtomContainer target = smi("CCCCC");
        Set<String> expected = bruteForceEdges(query, target, true, 1);
        Assertions.assertEquals(16, expected.size());
        Random random = new Random(20261006L);
        for (int i = 0; i < 10; i++) {
            IAtomContainer q = query.clone();
            IAtomContainer t = target.clone();
            int[] queryOrder = shuffle(q, random);
            int[] targetOrder = shuffle(t, random);
            Set<String> back = new HashSet<>();
            for (int[] mapping : MCS.find(q).withDisconnected(1).matchAll(t)) {
                back.add(Arrays.toString(unshuffle(mapping, queryOrder, targetOrder)));
            }
            Assertions.assertEquals(expected, back);
        }
        Assertions.assertEquals(expected, inverses(MCS.find(target).withDisconnected(1).matchAll(query), 6));
    }

    @Test
    void testDisconnectedOptions() throws Exception {
        MCS mcs = MCS.find(smi("CCOCC"));
        for (int minBonds : new int[]{0, -1}) {
            IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
                    () -> mcs.withDisconnected(minBonds));
            Assertions.assertEquals("A fragment must have at least one bond: " + minBonds, e.getMessage());
        }
        // a new search, both ethyls; the old one is still connected, one ethyl
        IAtomContainer target = smi("CCSCC");
        MCS edges = mcs.withDisconnected(1);
        Assertions.assertNotSame(mcs, edges);
        Set<String> ethyls = keys(edges.matchAll(target));
        Assertions.assertEquals(8, ethyls.size());
        Assertions.assertEquals(2, atoms(mcs.match(target)));
        // kept by the other options, before or after them, and a new size replaces the old one
        for (MCS search : new MCS[]{edges.withTimeout(1, TimeUnit.MINUTES), edges.withCompleteRings(),
                                    edges.withStereochemistry(), edges.withDisconnected(2).withDisconnected(1),
                                    mcs.withTimeout(1, TimeUnit.MINUTES).withDisconnected(1),
                                    mcs.withCompleteRings().withDisconnected(1),
                                    mcs.withStereochemistry().withDisconnected(1)}) {
            Assertions.assertEquals(ethyls, keys(search.matchAll(target)));
        }
        assertNothingInCommon(edges.withDisconnected(2), target);
        // and the matching: with ELEMENT the C=O of acetone maps on the C-O of isopropanol
        MCS acetone = MCS.find(smi("CC(C)=O"));
        IAtomContainer isopropanol = smi("CC(C)O");
        Set<String> element = keys(acetone.withMatching(ELEMENT).withDisconnected(1).matchAll(isopropanol));
        Assertions.assertEquals(element, keys(acetone.withDisconnected(1).withMatching(ELEMENT).matchAll(isopropanol)));
        Assertions.assertEquals(4, atoms(acetone.withDisconnected(1).withMatching(ELEMENT).match(isopropanol)));
        Assertions.assertEquals(3, atoms(acetone.withDisconnected(1).match(isopropanol)));
        // the inputs are left as they were
        IAtomContainer query = smi(ASPIRIN), other = smi("OC(=O)c1ccccc1O");
        String before = snapshot(query) + " / " + snapshot(other);
        Assertions.assertFalse(MCS.find(query).withDisconnected(1).matchAll(other).isEmpty());
        Assertions.assertFalse(MCS.find(other).withMatching(ELEMENT).withDisconnected(2).matchAll(query).isEmpty());
        Assertions.assertEquals(before, snapshot(query) + " / " + snapshot(other));
        // one search can be shared between threads
        assertSharedBetweenThreads(new MCS[]{MCS.find(query).withDisconnected(1),
                                             MCS.find(query).withMatching(ELEMENT).withDisconnected(2)},
                                   "OC(=O)c1ccccc1O", "CC(=O)Nc1ccc(O)cc1", "CCOC(=O)c1ccccc1");
        // the time limit stops the search of coronene in C60
        MCS hard = MCS.find(smi(CORONENE)).withDisconnected(1);
        Assertions.assertThrows(Intractable.class, () -> hard.withTimeout(1, TimeUnit.MILLISECONDS).match(smi(C60)));
    }

    @Test
    @Tag("SlowTest")
    void testDisconnectedInterrupted() throws Exception {
        // an interrupt stops the search of coronene in C60
        assertInterrupted(MCS.find(smi(CORONENE)).withDisconnected(1), smi(C60));
    }

    @Test
    void testDisconnectedWithOtherOptions() throws Exception {
        // L-alanine on D-alanine as with stereochemistry alone; E on Z: the two single bonds as fragments leave
        // the double bond unfixed
        assertDisconnectedWithOtherOptions(L_ALA, D_ALA, 1,
                                           score(6, 5, 1), score(5, 4, 2), score(5, 4, 2));
        assertDisconnectedWithOtherOptions("C/C=C/C", "C/C=C\\C", 1,
                                           score(4, 3, 2), score(4, 2, 6), score(4, 2, 6));
        // one of the options has nothing to do
        assertDisconnectedWithOtherOptions("CC.CC", "C[C@H](F)Cl", 1,
                                           score(2, 1, 4), score(2, 1, 4), score(2, 1, 4));
    }

    @Test
    @Tag("SlowTest")
    void testDisconnectedWithOtherOptionsSlow() throws Exception {
        // two rings around a changed linker; the Diels-Alder reaction: no chain bond lies on a ring bond
        assertDisconnectedWithOtherOptions("C1CC1CCC1CC1", "C1CC1OC1CC1", 1,
                                           score(6, 6, 72), score(6, 6, 72), score(6, 6, 72));
        assertDisconnectedWithOtherOptions("C=CC=C.C=C", "C1=CCCCC1", 1,
                                           NONE, score(6, 4, 24), NONE, BY_ELEMENT);
        assertDisconnectedWithOtherOptions("C=CC=C.C=C", "C1=CCCCC1", 1,
                                           NONE, score(4, 2, 12), NONE);
        // a ring opened on a chain is not whole; a linker bond lost, and without rings a ring atom on the linker,
        // in more ways
        assertDisconnectedWithOtherOptions("CC1CCCCC1", "CCCCCCC", 1,
                                           NONE, score(7, 6, 4), NONE);
        assertDisconnectedWithOtherOptions("C1CC1CC1CC1", "C1CC1CCC1CC1", 1,
                                           score(7, 7, 48), score(7, 7, 80), score(7, 7, 48));
        assertDisconnectedWithOtherOptions("C1CCCCC1", "CCCCCC", 1,
                                           NONE, score(6, 5, 12), NONE);
        // trans- on cis-1,2-dimethylcyclohexane: stereochemistry gives up a ring bond, so with complete rings a
        // methyl goes instead
        assertDisconnectedWithOtherOptions("C[C@@H]1CCCC[C@H]1C", "C[C@@H]1CCCC[C@@H]1C", 1,
                                           score(8, 8, 2), score(8, 7, 2), score(7, 7, 4));
        assertDisconnectedWithOtherOptions("C[C@@H]1CCCC[C@H]1C", "C[C@@H]1CCCC[C@@H]1C", 2,
                                           score(8, 8, 2), score(8, 7, 2), score(7, 7, 4));
        // enantiomers: two of the four ligands; the three alike hydrogens, the halogens not alike
        assertDisconnectedWithOtherOptions("F[C@](Cl)(Br)C", "F[C@@](Cl)(Br)C", 1,
                                           score(5, 4, 1), score(3, 2, 6), score(3, 2, 6));
        assertDisconnectedWithOtherOptions("[H]C([H])([H])[C@H](F)Cl", "[H]C([H])([H])[C@@H](F)Cl", 1,
                                           score(7, 6, 6), score(6, 5, 12), score(6, 5, 12));
        assertDisconnectedWithOtherOptions("C[C@H](O)C1CC1", "C[C@@H](O)C1CC1", 1,
                                           score(6, 6, 2), score(6, 5, 4), score(6, 5, 4));
        // two fragments, one with a ring and one with a double bond
        assertDisconnectedWithOtherOptions("OC1CC1.C/C=C/C", "C/C=C\\CC1CC1O", 1,
                                           score(8, 7, 4), score(8, 6, 12), score(8, 6, 12));
    }

    @Test
    void testInputsUnchanged() throws Exception {
        // a SMARTS query and a prepared target: the acid group and the ring
        IAtomContainer query = smarts("[N,O]~C(=O)c1ccccc1");
        IAtomContainer target = smi(ASPIRIN);
        SmartsPattern.prepare(target);
        String before = snapshot(query) + " / " + snapshot(target);
        MCS mcs = MCS.find(query);
        Assertions.assertEquals(9, atoms(mcs.match(target)));
        Assertions.assertEquals(2, mcs.matchAll(target).size());
        Assertions.assertEquals(before, snapshot(query) + " / " + snapshot(target));
    }

    // limits and threads

    @Test
    void testTimeoutDuringSetUp() throws Exception {
        // ordering the atoms of a long chain takes milliseconds, the limit must stop it early
        MCS mcs = MCS.find(smi("CN")).withTimeout(1, TimeUnit.MILLISECONDS);
        IAtomContainer target = chain(32000);
        long start = System.nanoTime();
        Assertions.assertThrows(Intractable.class, () -> mcs.match(target));
        Assertions.assertTrue(System.nanoTime() - start < TimeUnit.SECONDS.toNanos(3), "stopped promptly");
    }

    @Test
    @Tag("SlowTest")
    void testTimeoutMessage() throws Exception {
        // the exact search of coronene against C60 takes far longer than this
        MCS mcs = MCS.find(smi(CORONENE)).withTimeout(200, TimeUnit.MILLISECONDS);
        Intractable e = Assertions.assertThrows(Intractable.class, () -> mcs.match(smi(C60)));
        Assertions.assertEquals("MCS search did not finish after 200 ms.", e.getMessage());
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
        Assertions.assertEquals(6, atoms(mcs.match(target)));
    }

    @Test
    @Tag("SlowTest")
    void testInterruptedWhileSearching() throws Exception {
        assertInterrupted(MCS.find(smi(CORONENE)), smi(C60));
        assertInterrupted(MCS.find(smi(CORONENE)).withCompleteRings(), smi(C60));
    }

    /** A search interrupted after 100 ms stops and throws Intractable. */
    private static void assertInterrupted(MCS mcs, IAtomContainer target) throws Exception {
        Throwable[] thrown = new Throwable[1];
        Thread thread = new Thread(() -> {
            try {
                mcs.match(target);
            } catch (Throwable e) {
                thrown[0] = e;
            }
        });
        // a daemon, so a search that fails to stop cannot keep the build running
        thread.setDaemon(true);
        thread.start();
        Thread.sleep(100);
        thread.interrupt();
        thread.join(TimeUnit.SECONDS.toMillis(10));
        Assertions.assertFalse(thread.isAlive());
        Assertions.assertTrue(thrown[0] instanceof Intractable);
    }

    @Test
    void testTooLarge() throws Exception {
        // more than 2^30 atom pairs
        IAtomContainer mol = chain(32769);
        Intractable e = Assertions.assertThrows(Intractable.class, () -> MCS.find(mol).match(mol));
        Assertions.assertTrue(e.getMessage().contains("too large"), e.getMessage());
    }

    @Test
    void testTooManyBondPairs() {
        // K1000 has only one million atom pairs, but needs 3,898,597,500 words for its bond pairs. Counts alone
        // suffice: the search must refuse it before reading the graph or allocating the overflowing array.
        IAtomContainer complete = new AtomContainer() {
            @Override
            public int getAtomCount() {
                return 1000;
            }

            @Override
            public int getBondCount() {
                return 499500;
            }
        };
        Intractable e = Assertions.assertThrows(Intractable.class, () -> MCS.find(complete).match(complete));
        Assertions.assertEquals("MCS search too large: 499500 x 499500 bonds", e.getMessage());
        Assertions.assertThrows(Intractable.class,
                                () -> MCS.find(complete).withMatching(ELEMENT).matchAll(complete));
    }

    @Test
    void testLegacyAtomReferences() throws Exception {
        // its constructor is package-private; keep the builder's global choice unchanged
        Constructor<AtomContainerLegacy> legacy = AtomContainerLegacy.class.getDeclaredConstructor();
        legacy.setAccessible(true);
        IAtom carbon = new Atom("C"), oxygen = new Atom("O");
        IAtomContainer wrapped = legacy.newInstance();
        wrapped.addAtom(carbon);
        wrapped.addAtom(oxygen);
        wrapped.addBond(new Bond(new AtomRef(carbon), new AtomRef(oxygen), IBond.Order.SINGLE));
        IAtomContainer wrappedAtoms = legacy.newInstance();
        wrappedAtoms.addAtom(new AtomRef(carbon));
        wrappedAtoms.addAtom(new AtomRef(oxygen));
        wrappedAtoms.addBond(new Bond(carbon, oxygen, IBond.Order.SINGLE));
        IAtomContainer plain = smi("CO");
        for (IAtomContainer query : new IAtomContainer[]{plain, wrapped, wrappedAtoms}) {
            for (IAtomContainer target : new IAtomContainer[]{plain, wrapped, wrappedAtoms}) {
                for (MCS mcs : new MCS[]{MCS.find(query), MCS.find(query).withMatching(ELEMENT)}) {
                    Assertions.assertArrayEquals(new int[]{0, 1}, mcs.match(target));
                    Assertions.assertEquals(1, mcs.matchAll(target).size());
                }
            }
        }
    }

    @Test
    @Tag("SlowTest")
    void testLargeSearch() throws Exception {
        IAtomContainer mol = chain(8000);
        Assertions.assertEquals(8000, atoms(MCS.find(mol).withTimeout(10, TimeUnit.SECONDS).match(chain(8000))));
        // a hub of 50,000 bonds: the rows of target atoms by bond count stop at the most bonds a query atom has
        Assertions.assertEquals(2, atoms(MCS.find(smi("CC")).match(star(50000))));
    }

    @Test
    void testBondToItselfOrParallelBonds() throws Exception {
        // not a molecule, on either side
        IAtomContainer loop = smi("CCC");
        loop.addBond(0, 0, IBond.Order.SINGLE);
        IAtomContainer twice = smi("CCC");
        twice.addBond(2, 1, IBond.Order.SINGLE);
        for (IAtomContainer mol : new IAtomContainer[]{loop, twice}) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> MCS.find(smi("CCC")).match(mol));
            Assertions.assertThrows(IllegalArgumentException.class, () -> MCS.find(mol).matchAll(smi("CCC")));
        }
    }

    @Test
    void testSharedBetweenThreads() throws Exception {
        IAtomContainer query = smi(ASPIRIN);
        Cycles.markRingAtomsAndBonds(query);
        assertSharedBetweenThreads(new MCS[]{MCS.find(query), MCS.find(query).withMatching(ELEMENT, IS_IN_RING),
                                             MCS.find(query).withCompleteRings(), MCS.find(query).withDisconnected(1)},
                                   "OC(=O)c1ccccc1O", "CC(=O)Nc1ccc(O)cc1", "c1ccc2ccccc2c1", "CCOC(=O)c1ccccc1");
    }

    /** Each search on each target, ten times over in four threads, gives the mappings it gives in one. */
    private static void assertSharedBetweenThreads(MCS[] searches, String... targets) throws Exception {
        int pairs = searches.length * targets.length;
        List<Set<String>> expected = new ArrayList<>();
        for (int i = 0; i < pairs; i++) {
            expected.add(keys(searches[i % searches.length].matchAll(ring(targets[i / searches.length]))));
        }
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<Future<Set<String>>> results = new ArrayList<>();
            for (int i = 0; i < 10 * pairs; i++) {
                MCS search = searches[i % pairs % searches.length];
                String target = targets[i % pairs / searches.length];
                results.add(executor.submit(() -> keys(search.matchAll(ring(target)))));
            }
            for (int i = 0; i < results.size(); i++) {
                Assertions.assertEquals(expected.get(i % pairs), results.get(i).get(2, TimeUnit.MINUTES));
            }
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Checks a query on a target by default and with complete rings: the
     * score of each search, see {@link Score}. Each mapping with complete
     * rings must have them, and small pairs give the mappings of the brute
     * force search.
     */
    private static void assertCompleteRings(String query, String target, Score byDefault, Score whole,
                                            Option... options) throws Exception {
        checkCompleteRings(query, target, byDefault, whole, null, options);
    }

    /**
     * Checks a query on a target as
     * {@link #assertCompleteRings(String, String, Score, Score, Option...)}
     * does, where complete rings leave the mappings as they are.
     */
    private static void assertCompleteRings(String query, String target, Score byDefault, Unchanged unchanged,
                                            Option... options) throws Exception {
        checkCompleteRings(query, target, byDefault, byDefault, unchanged, options);
    }

    /**
     * Checks a query on a target by default and with complete rings; with
     * {@code unchanged} not null, the mappings must be the same.
     */
    private static void checkCompleteRings(String query, String target, Score byDefault, Score whole,
                                           Unchanged unchanged, Option... options) throws Exception {
        String row = describe(query, target, options);
        boolean smarts = has(options, SMARTS), orders = !has(options, BY_ELEMENT);
        IAtomContainer q = smarts ? smarts(query) : prepared(query, options);
        IAtomContainer t = prepared(target, options);
        MCS mcs = orders ? MCS.find(q) : MCS.find(q).withMatching(ELEMENT);
        List<int[]> plain = mcs.matchAll(t);
        List<int[]> rings = mcs.withCompleteRings().matchAll(t);
        Assertions.assertEquals(byDefault, scoreOf(q, t, plain, orders), row);
        Assertions.assertEquals(whole, scoreOf(q, t, rings, orders), row);
        if (unchanged != null) {
            Assertions.assertEquals(keys(plain), keys(rings), row);
        }
        for (int i = 0; unchanged == SAME_ORDER && i < rings.size(); i++) {
            Assertions.assertArrayEquals(plain.get(i), rings.get(i), row);
        }
        for (int[] mapping : rings) {
            Assertions.assertTrue(smarts || complete(q, t, mapping, orders), row);
        }
        // small enough for the brute force search
        if (!smarts && Math.min(q.getAtomCount(), t.getAtomCount()) <= 6
            && Math.max(q.getAtomCount(), t.getAtomCount()) <= 10) {
            Assertions.assertEquals(bruteForce(q, t, orders, true), keys(rings), row);
        }
    }

    /**
     * Checks a query on a target with stereochemistry and without: the score
     * of each search, see {@link Score}.
     */
    private static void assertStereochemistry(String query, String target, Score stereo, Score plain,
                                              Option... options) throws Exception {
        String row = describe(query, target, options);
        boolean orders = !has(options, BY_ELEMENT);
        IAtomContainer q = prepared(query, options), t = prepared(target, options);
        Assertions.assertEquals(stereo, scoreOf(q, t, orders, true), row);
        Assertions.assertEquals(plain, scoreOf(q, t, orders, false), row);
    }

    /**
     * Checks a query on a target with complete rings and stereochemistry,
     * with complete rings alone and with stereochemistry alone: the score of
     * each search, see {@link Score}; with both, the mappings of the brute
     * force search, whichever option comes first.
     */
    private static void assertStereochemistryWithCompleteRings(String query, String target, Score both, Score rings,
                                                               Score stereo) throws Exception {
        String row = describe(query, target);
        IAtomContainer q = smi(query), t = smi(target);
        MCS mcs = MCS.find(q);
        List<int[]> mappings = mcs.withCompleteRings().withStereochemistry().matchAll(t);
        Assertions.assertEquals(both, scoreOf(q, t, mappings, true), row);
        Assertions.assertEquals(rings, scoreOf(q, t, mcs.withCompleteRings().matchAll(t), true), row);
        Assertions.assertEquals(stereo, scoreOf(q, t, mcs.withStereochemistry().matchAll(t), true), row);
        Assertions.assertEquals(bruteForce(q, t, true, true, true), keys(mappings), row);
        // the options in either order
        Assertions.assertEquals(keys(mappings), keys(mcs.withStereochemistry().withCompleteRings().matchAll(t)), row);
    }

    /**
     * Checks a query on a target with fragments of at least {@code minBonds}
     * bonds and with complete rings, with stereochemistry and with both: the
     * score of each search, see {@link Score}, and the mappings of the brute
     * force search, whichever option comes first.
     */
    private static void assertDisconnectedWithOtherOptions(String query, String target, int minBonds, Score rings,
                                                           Score stereo, Score both, Option... options)
            throws Exception {
        String row = describe(query, target, options) + ", fragments of " + minBonds + " or more bonds";
        boolean orders = !has(options, BY_ELEMENT);
        IAtomContainer q = prepared(query, options), t = prepared(target, options);
        MCS mcs = (orders ? MCS.find(q) : MCS.find(q).withMatching(ELEMENT)).withDisconnected(minBonds);
        Set<String> byRings = assertScores(mcs.withCompleteRings(), q, t, orders, minBonds, rings, row);
        Set<String> byStereo = assertScores(mcs.withStereochemistry(), q, t, orders, minBonds, stereo, row);
        Set<String> byBoth = assertScores(mcs.withCompleteRings().withStereochemistry(), q, t, orders, minBonds, both,
                                          row);
        Assertions.assertEquals(bruteForce(q, t, orders, true, false, minBonds), byRings, row);
        Assertions.assertEquals(bruteForce(q, t, orders, false, true, minBonds), byStereo, row);
        Assertions.assertEquals(bruteForce(q, t, orders, true, true, minBonds), byBoth, row);
        // the options in any order
        Assertions.assertEquals(byBoth, keys(mcs.withStereochemistry().withCompleteRings().matchAll(t)), row);
        MCS last = orders ? MCS.find(q) : MCS.find(q).withMatching(ELEMENT);
        Assertions.assertEquals(byBoth, keys(last.withStereochemistry().withCompleteRings().withDisconnected(minBonds)
                                                 .matchAll(t)), row);
    }

    /**
     * Checks a query on a target with fragments of at least {@code minBonds}
     * bonds: the score of the maximum common edge subgraphs, see
     * {@link Score}. Small pairs give the mappings of the brute force
     * search.
     */
    private static void assertDisconnected(String query, String target, int minBonds, Score edges,
                                           Option... options) throws Exception {
        assertDisconnected(query, target, minBonds, edges, (Score) null, options);
    }

    /**
     * Checks a query on a target as
     * {@link #assertDisconnected(String, String, int, Score, Option...)} does,
     * and unless {@code connected} is null, the score of the connected MCS.
     */
    private static void assertDisconnected(String query, String target, int minBonds, Score edges, Score connected,
                                           Option... options) throws Exception {
        String row = describe(query, target, options) + ", fragments of " + minBonds + " or more bonds";
        boolean orders = !has(options, BY_ELEMENT);
        IAtomContainer q = prepared(query, options), t = prepared(target, options);
        MCS mcs = orders ? MCS.find(q) : MCS.find(q).withMatching(ELEMENT);
        Set<String> mappings = assertScores(mcs.withDisconnected(minBonds), q, t, orders, minBonds, edges, row);
        if (q.getAtomCount() <= 8 && t.getAtomCount() <= 8) {
            Assertions.assertEquals(bruteForceEdges(q, t, orders, minBonds), mappings, row);
        }
        if (connected != null) {
            assertScores(mcs, q, t, orders, 0, connected, row);
        }
    }

    /**
     * The atoms mapped, the common bonds and the number of the maximum
     * mappings of a search, for a row.
     */
    private static Score score(int atoms, int bonds, int mappings) {
        return new Score(atoms, bonds, mappings);
    }

    /** Whether the options of a row include one. */
    private static boolean has(Option[] options, Option option) {
        return Arrays.asList(options).contains(option);
    }

    /** A row's query, target and options, for the messages. */
    private static String describe(String query, String target, Option... options) {
        return query + " on " + target + (options.length == 0 ? "" : " " + Arrays.toString(options));
    }

    /**
     * A molecule of a row: with EXPLICIT_H, with explicit hydrogens; with
     * DAYLIGHT or SMARTS, prepared for SMARTS, which perceives aromaticity as
     * Daylight does.
     */
    private static IAtomContainer prepared(String smiles, Option... options) throws Exception {
        IAtomContainer mol = has(options, EXPLICIT_H) ? withHydrogens(smiles) : smi(smiles);
        if (has(options, DAYLIGHT) || has(options, SMARTS)) {
            SmartsPattern.prepare(mol);
        }
        return mol;
    }

    /** [k]CPP without aromaticity: k cyclohexane rings, each bonded at its 4-position to the 1-position of the next. */
    private static IAtomContainer cpp(int k) throws Exception {
        StringBuilder smiles = new StringBuilder("C19CCC(CC1)");
        for (int i = 2; i < k; i++) {
            smiles.append("C1CCC(CC1)");
        }
        return smi(smiles.append("C1CCC9CC1").toString());
    }

    /** [k]acene without aromaticity: two chains of 2k + 1 carbons, bonded to each other at every other atom. */
    private static IAtomContainer acene(int k) {
        int width = 2 * k + 1;
        IAtomContainer mol = new AtomContainer();
        for (int i = 0; i < 2 * width; i++) {
            mol.addAtom(new Atom("C"));
            if (i % width > 0) {
                mol.addBond(i - 1, i, IBond.Order.SINGLE);
            }
            if (i >= width && i % 2 == 1) {
                mol.addBond(i - width, i, IBond.Order.SINGLE);
            }
        }
        return mol;
    }

    /**
     * A k x k parallelogram of six-membered carbon rings: k + 1 rows of 2k + 2
     * atoms, bonded along each row and to the row before at every other atom,
     * as bricks in a wall.
     */
    private static IAtomContainer sheet(int k) {
        int width = 2 * k + 2;
        IAtomContainer mol = new AtomContainer();
        for (int i = 0; i < width * (k + 1); i++) {
            mol.addAtom(new Atom("C"));
            if (i % width > 0) {
                mol.addBond(i - 1, i, IBond.Order.SINGLE);
            }
            if (i >= width && (i % width + i / width) % 2 == 1) {
                mol.addBond(i - width, i, IBond.Order.SINGLE);
            }
        }
        return mol;
    }

    /**
     * The score of matchAll, by default or with ELEMENT, with stereochemistry
     * or without: each mapping is checked by assertScore and found once,
     * match returns one of them, and below ten atoms they are the mappings of
     * the brute force search.
     */
    private static Score scoreOf(IAtomContainer query, IAtomContainer target, boolean orders, boolean stereo)
            throws Exception {
        MCS mcs = orders ? MCS.find(query) : MCS.find(query).withMatching(ELEMENT);
        mcs = stereo ? mcs.withStereochemistry() : mcs;
        List<int[]> mappings = mcs.matchAll(target);
        int atoms = atoms(mappings.get(0));
        int bonds = commonBonds(query, target, mappings.get(0), orders);
        assertScore(query, target, mappings, atoms, bonds, orders);
        Assertions.assertEquals(mappings.size(), keys(mappings).size());
        Assertions.assertTrue(keys(mappings).contains(Arrays.toString(mcs.match(target))));
        Assertions.assertTrue(query.getAtomCount() > 9
                              || bruteForce(query, target, orders, false, stereo).equals(keys(mappings)));
        return score(atoms, bonds, mappings.size());
    }

    /**
     * Alanine from a V2000 molfile: L with a hashed bond to the methyl (6), D
     * with a wedged one (1); chiral flag 0 or 1.
     */
    private static IAtomContainer alanine(int stereo, int chiral) throws Exception {
        StringBuilder molfile = new StringBuilder("\n  test\n\n  6  5  0  0  " + chiral + "  0  0  0  0  0999 V2000\n");
        double[] xy = {-1.299, 0.75, 0, 0, 0, -1.5, 1.299, 0.75, 2.598, 0, 1.299, 2.25};
        for (int i = 0; i < 6; i++) {
            molfile.append(String.format(Locale.ROOT, "%10.4f%10.4f%10.4f %-3s 0  0  0  0  0  0  0  0  0  0  0  0\n",
                                         xy[2 * i], xy[2 * i + 1], 0.0, "NCCCOO".charAt(i)));
        }
        molfile.append("  1  2  1  0\n  2  3  1  ").append(stereo)
               .append("\n  2  4  1  0\n  4  5  2  0\n  4  6  1  0\nM  END\n");
        try (MDLV2000Reader reader = new MDLV2000Reader(new StringReader(molfile.toString()))) {
            return reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        }
    }

    private static IAtomContainer withHydrogens(String smiles) throws Exception {
        IAtomContainer mol = smi(smiles);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        return mol;
    }

    private static IAtomContainer ring(String smiles) throws Exception {
        IAtomContainer mol = smi(smiles);
        Cycles.markRingAtomsAndBonds(mol);
        return mol;
    }

    /** A chain of carbon atoms, built without perceiving rings. */
    private static IAtomContainer chain(int size) {
        IAtom[] atoms = new IAtom[size];
        for (int i = 0; i < size; i++) {
            atoms[i] = new Atom("C");
        }
        IAtomContainer chain = SilentChemObjectBuilder.getInstance().newAtomContainer();
        chain.setAtoms(atoms);
        for (int i = 1; i < size; i++) {
            chain.addBond(i - 1, i, IBond.Order.SINGLE);
        }
        return chain;
    }

    /** A carbon joined to the given number of others. */
    private static IAtomContainer star(int leaves) {
        IAtomContainer star = new AtomContainer();
        for (int i = 0; i <= leaves; i++) {
            star.addAtom(new Atom("C"));
        }
        for (int i = 1; i <= leaves; i++) {
            star.addBond(0, i, IBond.Order.SINGLE);
        }
        return star;
    }

    /** Two carbons joined by a bond of the given kind, then a chain of single bonds through the other atoms. */
    private static IAtomContainer bond(int kind, String... more) {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.getBond(0).setOrder(ORDERS[kind]);
        mol.getBond(0).setIsAromatic(AROMATIC[kind]);
        for (String symbol : more) {
            mol.addAtom(new Atom(symbol));
            mol.addBond(mol.getAtomCount() - 2, mol.getAtomCount() - 1, IBond.Order.SINGLE);
        }
        return mol;
    }

    /** The same atoms and bonds, plain ones, in a query molecule. */
    private static IAtomContainer inQuery(IAtomContainer mol) {
        QueryAtomContainer query = new QueryAtomContainer(SilentChemObjectBuilder.getInstance());
        for (IAtom atom : mol.atoms()) {
            query.addAtom(new Atom(atom.getSymbol()));
        }
        for (IBond bond : mol.bonds()) {
            IBond copy = new Bond(query.getAtom(bond.getBegin().getIndex()), query.getAtom(bond.getEnd().getIndex()));
            copy.setOrder(bond.getOrder());
            copy.setIsAromatic(bond.isAromatic());
            query.addBond(copy);
        }
        return query;
    }

    /** A SMILES with the pseudo atoms (*) given a label. */
    private static IAtomContainer pseudo(String smiles, String label) throws Exception {
        IAtomContainer mol = smi(smiles);
        for (IAtom atom : mol.atoms()) {
            if (atom instanceof IPseudoAtom) {
                ((IPseudoAtom) atom).setLabel(label);
            }
        }
        return mol;
    }

    private static int[] m(int... mapping) {
        return mapping;
    }

    private static Set<String> set(int[]... mappings) {
        return keys(Arrays.asList(mappings));
    }

    /** The 12 ways to lay query atoms 0 to 5, a six-membered ring in ring order, on a ring of target atoms. */
    private static Set<String> ringMappings(int queryAtoms, int... ring) {
        Set<String> mappings = new HashSet<>();
        for (int start = 0; start < 6; start++) {
            for (int step : new int[]{1, 5}) {
                int[] mapping = new int[queryAtoms];
                Arrays.fill(mapping, -1);
                for (int i = 0; i < 6; i++) {
                    mapping[i] = ring[(start + step * i) % 6];
                }
                mappings.add(Arrays.toString(mapping));
            }
        }
        return mappings;
    }

    /**
     * Checks the number of mappings and the score of each, see
     * {@link MCSTesting#score}, and that match returns one of them; returns
     * them.
     */
    private static Set<String> assertScores(MCS mcs, IAtomContainer query, IAtomContainer target, boolean orders,
                                            int minBonds, Score expected, String row) throws Exception {
        String message = row + ": " + expected;
        List<int[]> mappings = assertMappings(mcs, target, keys(mcs.matchAll(target)));
        Assertions.assertEquals(expected.mappings, mappings.size(), message);
        for (int[] mapping : mappings) {
            Assertions.assertEquals(expected.rank(minBonds), MCSTesting.score(query, target, mapping, orders, minBonds),
                                    message);
        }
        return keys(mappings);
    }

    private static void assertNothingInCommon(MCS mcs, IAtomContainer target) throws Exception {
        Assertions.assertArrayEquals(new int[0], mcs.match(target));
        Assertions.assertTrue(mcs.matchAll(target).isEmpty());
    }

    /** Checks that matchAll returns the expected mappings, each once, and that match returns one of them. */
    private static List<int[]> assertMappings(MCS mcs, IAtomContainer target, Set<String> expected)
            throws Exception {
        List<int[]> mappings = mcs.matchAll(target);
        Assertions.assertEquals(expected, keys(mappings));
        Assertions.assertEquals(expected.size(), mappings.size());
        int[] one = mcs.match(target);
        if (expected.isEmpty()) {
            Assertions.assertEquals(0, one.length);
        } else {
            Assertions.assertTrue(expected.contains(Arrays.toString(one)), () -> Arrays.toString(one));
        }
        return mappings;
    }

    /** The score of maximum mappings, each valid and of the same score, or NONE if there are none. */
    private static Score scoreOf(IAtomContainer query, IAtomContainer target, List<int[]> mappings, boolean orders) {
        if (mappings.isEmpty()) {
            return NONE;
        }
        int atoms = atoms(mappings.get(0)), bonds = commonBonds(query, target, mappings.get(0), orders);
        assertScore(query, target, mappings, atoms, bonds, orders);
        return score(atoms, bonds, mappings.size());
    }

    /** Checks the atoms and common bonds of each mapping, and that it is valid. */
    private static void assertScore(IAtomContainer query, IAtomContainer target, List<int[]> mappings, int atoms,
                                    int bonds, boolean orders) {
        for (int[] mapping : mappings) {
            Assertions.assertEquals(atoms, atoms(mapping), Arrays.toString(mapping));
            Assertions.assertEquals(bonds, commonBonds(query, target, mapping, orders), Arrays.toString(mapping));
            assertValid(query, target, mapping, orders);
        }
    }

    /**
     * The atoms mapped, the common bonds and the number of the maximum
     * mappings of a search, written atoms/bonds/mappings, or none.
     */
    private static final class Score {

        private final int atoms;
        private final int bonds;
        private final int mappings;

        private Score(int atoms, int bonds, int mappings) {
            this.atoms = atoms;
            this.bonds = bonds;
            this.mappings = mappings;
        }

        /**
         * The score of each mapping as {@link MCSTesting#score} gives it: with
         * fragments of at least one bond, the common bonds come first.
         */
        private long rank(int minBonds) {
            return minBonds > 0 ? (long) bonds << 32 | atoms : (long) atoms << 32 | bonds;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Score)) {
                return false;
            }
            Score that = (Score) obj;
            return atoms == that.atoms && bonds == that.bonds && mappings == that.mappings;
        }

        @Override
        public int hashCode() {
            return (atoms * 31 + bonds) * 31 + mappings;
        }

        @Override
        public String toString() {
            return mappings == 0 ? "none" : atoms + "/" + bonds + "/" + mappings;
        }
    }
}
