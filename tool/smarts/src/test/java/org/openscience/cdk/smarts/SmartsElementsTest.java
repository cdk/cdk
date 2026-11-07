/* Copyright (c) 2026 The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.smarts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * Tests for {@link SmartsElements}. The expected sets in
 * {@link #cases()} come from reading the SMARTS, not from the output of the
 * code, and {@link #neverRejectsAMatchingMolecule()} checks the claim that
 * matters (a necessary condition) against the real matcher.
 */
class SmartsElementsTest {

    private static Arguments of(String smarts, Integer... expected) {
        return Arguments.of(smarts, new TreeSet<>(Arrays.asList(expected)));
    }

    static Stream<Arguments> cases() {
        return Stream.of(
            // ---- plain atoms, organic subset and brackets
            of("C", 6),
            of("c", 6),
            of("[#6]", 6),
            of("[C]", 6),
            of("Cl", 17),              // chlorine, not carbon + l
            of("[Cl]", 17),
            of("Br", 35),
            of("[Si]", 14),            // silicon, not sulfur + i
            of("[Fe]", 26),
            of("[#26]", 26),
            of("[se]", 34),            // aromatic selenium
            of("[nH]", 7),
            of("[Na+]", 11),
            of("[O-]", 8),
            of("[#0]", 0),
            // ---- hydrogen: explicit hydrogen atoms only
            of("[H]", 1),
            of("[#1]", 1),
            of("[2H]", 1),
            of("[CH3]", 6),            // a hydrogen count, not a hydrogen atom
            of("[C;H1]", 6),
            // ---- isotopes
            of("[13C]", 6),
            // ---- whole structures
            of("CC(=O)O", 6, 8),
            of("c1ccccc1", 6),
            of("c1ccncc1", 6, 7),
            of("C1CC1", 6),
            of("C.N", 6, 7),           // disconnected components
            of("[#6][#7][#8]", 6, 7, 8),
            of("[NX3][CX3](=[OX1])[#6]", 6, 7, 8),
            // ---- primitives that name no element
            of("*"),
            of("[*]"),
            of("A"),
            of("a"),
            of("[R]"),
            of("[r6]"),
            of("[D3]"),
            of("[X4]"),
            of("[+]"),
            of("[-]"),
            of("[!#1]"),
            // ---- AND ('&', ';' and implicit) requires everything
            of("[C&N]", 6, 7),         // impossible, but vacuously fine
            of("[#6;a]", 6),
            of("[#6&a&R]", 6),
            of("[C;!N]", 6),           // only the positive side
            of("[#7;!#7]", 7),
            // ---- OR (',') only what both sides require
            of("[C,N]"),
            of("[Cl,Br,I]"),
            of("[C,c]", 6),            // aliphatic or aromatic carbon: carbon
            of("[#6,#7]"),
            of("[#6,*]"),              // anything matches
            of("[#6;R,#7;R]", 6),      // ; binds weaker than , so: #6 and (R or #7) and R
            of("[C,N;R]"),             // (C or N) and in a ring: nothing definite
            of("[#6X3,#6X4]", 6),
            // ---- NOT ('!') requires nothing
            of("[!C]"),
            of("[!#6&!#7]"),
            of("[!#6&!#7;R]"),
            of("[#6;!$(C=O)]", 6),     // the O is inside a negation
            of("[!$(C=O)]"),
            of("[#6]!@[#7]", 6, 7),
            // ---- recursive SMARTS
            of("[$(C=O)]", 6, 8),
            of("[C;$(C=O)]", 6, 8),
            of("[$(C=O),N]"),          // C and O, or N: nothing common
            of("[$(C=O),$(CN)]", 6),   // both alternatives need a carbon
            of("[$(*C)]", 6),
            of("[$([N,O]C)]", 6),      // the N,O is an OR
            of("[$(C[$(CN)])]", 6, 7),
            of("[$([#6]),$([#7])]"),
            of("[C;!$(C=O)]", 6),
            of("[#7;$(N-C=O),$(N-S=O)]", 7, 8),   // N, plus the O both alternatives need
            of("[$(C=O)]O", 6, 8),
            of("[N;!$(N=O)]C", 6, 7)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void requiredElements(String smarts, Set<Integer> expected) {
        Assertions.assertEquals(expected, SmartsElements.requiredElements(smarts),
                                "SMARTS: " + smarts);
    }

    @Test
    void resultIsSortedAndReadOnly() {
        Set<Integer> elements = SmartsElements.requiredElements("[#8][#7][#6]");
        Assertions.assertEquals("[6, 7, 8]", elements.toString());
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> elements.add(1));
    }

    @Test
    void nullSmarts() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> SmartsElements.requiredElements(null));
    }

    @Test
    void invalidSmarts() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> SmartsElements.requiredElements("C(("));
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> SmartsElements.requiredElements("[C"));
    }

    @Test
    void emptySmarts() {
        Assertions.assertTrue(SmartsElements.requiredElements("").isEmpty());
    }

    // ------------------------------------------------------------------
    // The property that matters: if the matcher says the molecule matches
    // then it must contain every required element. A false "required" would
    // make a pre-filter throw away real matches.

    private static final String[] MOLECULES = {
        "C", "CC", "CCO", "CC(=O)O", "CC(=O)N", "CC(=O)OC", "NC(=O)C",
        "c1ccccc1", "c1ccccc1O", "c1ccncc1", "c1ccc2ccccc2c1", "c1cc[nH]c1",
        "c1ccsc1", "c1ccoc1", "C1CC1", "C1CCCCC1", "C1=CCCCC1",
        "ClC(Cl)Cl", "BrCCBr", "FC(F)(F)c1ccccc1", "ICC", "CCl", "CBr",
        "CS(=O)(=O)N", "CP(=O)(O)O", "C[Si](C)(C)C", "C[Se]C", "[Na+].[Cl-]",
        "[Fe+2]", "[H]C([H])([H])O", "[H][H]", "[2H]C([2H])([2H])O", "[13CH4]",
        "C[N+](C)(C)C", "CC(=O)[O-]", "O=C=O", "N#N", "C#N", "C=C", "CC=O",
        "O", "N", "S", "[O-][N+](=O)c1ccccc1", "CN(C)C=O", "OC(=O)c1ccccc1N",
        "CC(C)(C)OC(=O)N", "C1CCC(CC1)N", "n1ccccc1", "[Cu]", "B(O)O", "FC1=CC=CC=C1",
        "CC(=O)Nc1ccc(O)cc1", "CN1CCC[C@H]1c1cccnc1"
    };

    private static final String[] QUERIES = Stream.concat(
        cases().map(a -> (String) a.get()[0]),
        Stream.of("[CH3]", "[CH2]", "[OH]", "[NH2]", "[#6]~[#7]", "[#6]~[#8]",
                  "C(=O)[OH]", "[cH]", "[nX2]", "[a;r6]~[a;r6]", "[$(C=O)][OH]",
                  "[!#1]", "[!#6;!#1]~[#6]~[!#6;!#1]", "[#8;!$(O=*)]",
                  "[$(C=O),$(S=O)]", "[$([OH]),$([O-])]", "[#6;!R]",
                  "[#7,#8;H1]", "[C,O;!R]=[C,N]")
    ).distinct().toArray(String[]::new);

    @Test
    void neverRejectsAMatchingMolecule() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        int matches = 0;
        for (String smi : MOLECULES) {
            IAtomContainer mol = smipar.parseSmiles(smi);
            Set<Integer> present = new TreeSet<>();
            for (IAtom atom : mol.atoms())
                if (atom.getAtomicNumber() != null)
                    present.add(atom.getAtomicNumber());
            for (String smarts : QUERIES) {
                Set<Integer> required = SmartsElements.requiredElements(smarts);
                // fresh pattern/molecule state each time, the match prepares
                // (ring marking, aromaticity) the molecule
                boolean matched = SmartsPattern.create(smarts, SilentChemObjectBuilder.getInstance())
                                               .matches(mol);
                if (matched) {
                    matches++;
                    Assertions.assertTrue(present.containsAll(required),
                        () -> smarts + " matched " + smi + " but requires " + required +
                              " and the molecule has " + present);
                }
            }
        }
        // the check is only meaningful if plenty of pairs actually match
        Assertions.assertTrue(matches > 300, "only " + matches + " matching pairs");
    }

    @Test
    void filterIsUsefulOnTheseQueries() throws InvalidSmilesException {
        // sanity check that the sets are not trivially empty: for some
        // molecule/query pairs the element test alone rules out the match
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("CCO");
        Set<Integer> present = new TreeSet<>();
        for (IAtom atom : mol.atoms())
            present.add(atom.getAtomicNumber());
        Assertions.assertFalse(present.containsAll(SmartsElements.requiredElements("CN")));
        Assertions.assertFalse(present.containsAll(SmartsElements.requiredElements("[Cl]")));
        Assertions.assertTrue(present.containsAll(SmartsElements.requiredElements("CO")));
    }
}
