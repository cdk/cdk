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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smarts.Smarts;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.openscience.cdk.isomorphism.MCSCasesTest.Condition.ANY;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Condition.ANY_ATOM;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Condition.BASIC;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Condition.BONDS;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Condition.ELEMENT;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Condition.ELEMENT_ORDER;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Condition.PSEUDO_ANY;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Condition.RINGS_CHAINS;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Condition.SMARTS;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Known.BRUTE_FORCE;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Known.HAND;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Known.PART;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Known.PINNED;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Known.WHOLE;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Prep.DAYLIGHT;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Prep.EXPLICIT_H;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Prep.LEGACY;
import static org.openscience.cdk.isomorphism.MCSCasesTest.Prep.NONE;
import static org.openscience.cdk.isomorphism.MCSTesting.keys;
import static org.openscience.cdk.isomorphism.MCSTesting.peptide;
import static org.openscience.cdk.isomorphism.MCSTesting.smi;

/**
 * Molecule pairs with their expected maximum common substructures: for each
 * search the numbers of mapped atoms, of common bonds and of maximum
 * mappings. The pairs come from the SMSD tests, from the overlap tests of
 * {@link UniversalIsomorphismTester}, from a panel of related drugs, and
 * from larger peptides and benzenoids.
 * {@link Known} says how each expectation is known: the scores of the
 * BRUTE_FORCE, WHOLE and HAND rows are known maxima, those of the PART and
 * PINNED rows are recorded.
 * <br><br>
 * Each row is one search, and each kind of check is a parameterised test
 * over the rows it applies to: the maximum mappings, the mappings of the
 * brute force search, other atom orders, and the overlaps of
 * {@link UniversalIsomorphismTester}. The small pairs of {@code PAIRS} are
 * searched in every build, those of {@code SLOW_PAIRS} in the slow tests.
 * <br><br>
 * The maximum mappings are checked the same way for every row: each one, and
 * the one of {@link MCS#match}, is valid and has the expected score, and
 * there are as many as expected. Below the 1000 mappings matchAll returns,
 * match is one of them, and for conditions that match the same both ways
 * round the search the other way round finds the same mappings turned round.
 * When every mapping maps the same atoms of one molecule and all bonds
 * between them, the mappings are the embeddings of that part. Without
 * explicit hydrogens, a connected query that does not map whole is not a
 * substructure of the target, and for conditions that match the same both
 * ways round a connected target that does not map whole is not a
 * substructure of the query. SMSD could keep only the mappings its chemical
 * filters ranked best, so some of its counts were lower; the rows say where.
 * <br><br>
 * Not ported: the chemical filter scores, a query molecule as the target,
 * and the tests of SMSD internals that ran no search. The rows search without
 * stereochemistry; {@link MCSTest} covers {@link MCS#withStereochemistry()}.
 *
 * @author Syed Asad Rahman
 */
final class MCSCasesTest {

    /** The most mappings matchAll returns. */
    private static final int CAP = 1000;

    /** How atoms and bonds match. */
    enum Condition {
        /** The default matching, as the bond sensitive search of SMSD. */
        BONDS,
        /** withMatching(ELEMENT), as the bond insensitive search of SMSD. */
        ELEMENT(Expr.Type.ELEMENT),
        /** As createSymbolAndBondOrderQueryContainer. */
        ELEMENT_ORDER(Expr.Type.ELEMENT, Expr.Type.ORDER),
        /** As createBasicQueryContainer. */
        BASIC(Expr.Type.ALIPHATIC_ELEMENT, Expr.Type.AROMATIC_ELEMENT, Expr.Type.IS_AROMATIC,
              Expr.Type.ALIPHATIC_ORDER, Expr.Type.STEREOCHEMISTRY),
        /** Any atom, as createAnyAtomContainer. */
        ANY_ATOM(Expr.Type.IS_AROMATIC, Expr.Type.ALIPHATIC_ORDER),
        /** Any atom and any bond, as createAnyAtomAnyBondContainer. */
        ANY,
        /** A pseudo atom matches any atom, as createAnyAtomForPseudoAtomQueryContainer. */
        PSEUDO_ANY(Expr.Type.ELEMENT, Expr.Type.IS_AROMATIC, Expr.Type.ALIPHATIC_ORDER),
        /** Ring atoms and bonds only on ring ones and chain on chain ones, as in the MCS documentation. */
        RINGS_CHAINS(Expr.Type.ELEMENT, Expr.Type.SINGLE_OR_AROMATIC, Expr.Type.IS_IN_RING, Expr.Type.IS_IN_CHAIN),
        /** The query is SMARTS. */
        SMARTS;

        private final Expr.Type[] types;

        Condition(Expr.Type... types) {
            this.types = types;
        }

        MCS search(IAtomContainer mol) {
            MCS mcs = this == BONDS || this == SMARTS ? MCS.find(mol) : MCS.find(mol).withMatching(types);
            // a search that has become far slower fails rather than runs on
            return mcs.withTimeout(5, TimeUnit.MINUTES);
        }

        /** Whether atoms match by symbol and bonds by the default rule or not at all, as in MCSTesting. */
        boolean plain() {
            return this == BONDS || this == ELEMENT;
        }

        /**
         * Whether atoms and bonds match the same both ways round: IS_AROMATIC
         * without ALIPHATIC_ELEMENT leaves aliphatic atoms free, and a pseudo
         * atom matches any atom (also with ELEMENT, whose only row with pseudo
         * atoms is * on *).
         */
        boolean symmetric() {
            return this != ANY_ATOM && this != PSEUDO_ANY && this != SMARTS;
        }

        /** The query with the expressions of withMatching, else as it is. */
        IAtomContainer rules(IAtomContainer mol) {
            return plain() || this == SMARTS ? mol : QueryAtomContainer.create(mol, types);
        }

        /** A substructure search with the same matching, ignoring stereochemistry as the rows do. */
        Pattern pattern(IAtomContainer mol) throws CloneNotSupportedException {
            IAtomContainer copy = mol.clone();
            copy.setStereoElements(new ArrayList<>());
            if (plain()) {
                BondMatcher bonds = this == BONDS ? BondMatcher.forStrictOrder() : BondMatcher.forAny();
                return VentoFoggia.findSubstructure(copy, AtomMatcher.forElement(), bonds);
            }
            return VentoFoggia.findSubstructure(rules(copy));
        }
    }

    /** How both molecules are prepared; the ring flags are always set. */
    enum Prep {
        /** As parsed: Kekule SMILES stay non-aromatic. */
        NONE,
        /** Atom types and the CDK legacy aromaticity, as the SMSD tests prepared them. */
        LEGACY,
        /** The Daylight aromaticity, as the MCS documentation advises. */
        DAYLIGHT,
        /** Explicit hydrogens. */
        EXPLICIT_H
    }

    /** How an expectation is known. */
    enum Known {
        /** The mappings are those of the brute force search, {@link MCSTesting#exact}. */
        BRUTE_FORCE,
        /** A molecule is mapped whole, so no mapping has more atoms and bonds; the mappings are its embeddings. */
        WHOLE,
        /**
         * Every mapping maps the same atoms of a molecule and all bonds between them, so the mappings are the
         * embeddings of that part; the score is recorded, as for PINNED.
         */
        PART,
        /** Worked out by hand, see the comment. */
        HAND,
        /**
         * Recorded with this MCS search as a regression pin, not an independently checked maximum, so a change is
         * a prompt to check by hand rather than proof of a regression.
         */
        PINNED
    }

    /** ADN.mol of the SMSD tests, with its hydrogens. */
    private static final String ADN = "N(C=1N=C(N=C2N(C(=NC12)[H])C3([H])OC([H])(C(O[H])([H])[H])C([H])(O[H])"
            + "C3([H])O[H])[H])([H])[H]";
    /** 5SD.mol of the SMSD tests, with its hydrogens. */
    private static final String SD5 = "C(C12C(C(C(=O)C(C1([H])C(C(C3([H])C2([H])C(C(C4(C([H])([H])[H])C3([H])C(C(C4=O)"
            + "([H])[H])([H])[H])([H])[H])([H])[H])([H])[H])([H])[H])([H])[H])([H])[H])([H])[H])"
            + "([H])([H])[H]";
    private static final String CHOLESTEROL  = "CC(C)CCCC(C)C1CCC2C1(CCC3C2CC=C4C3(CCC(C4)O)C)C";
    private static final String CHOLIC_ACID  = "CC(CCC(=O)O)C1CCC2C1(C(CC3C2C(CC4C3(CCC(C4)O)C)O)O)C";
    private static final String ALPHA_PINENE = "CC1=CCC2CC1C2(C)C";
    private static final String O_XYLENE     = "CC1=CC=CC=C1C";
    private static final String BUG_2944080  = "CCC(CC)(C(=O)NC(=O)NC(C)=O)Br";

    /** Small pairs, searched in every build. */
    private static final Pair[] PAIRS = {
            // the SMSD tests
            pair("fragment/heterocycle", "O=C1NC(=O)C2=C(N1)NC(=O)C=N2",
                 "OC[C@@H](O)[C@@H](O)[C@@H](O)CN1C(O)C(CCC(O)O)NC2C(O)NC(O)NC12",
                 // the target has single bonds only; those of the query form one chain N-C-C-N-C-N-C-N-C-C,
                 // which follows three paths through the rings of the target
                 row(BONDS, LEGACY, 10, 9, 3, HAND),
                 row(ELEMENT, LEGACY, 13, 14, 1, WHOLE)),
            pair("ethane/propane", "CC", "CCC",
                 // either bond, either way round
                 row(BONDS, NONE, 2, 1, 4, BRUTE_FORCE),
                 row(ELEMENT, NONE, 2, 1, 4, BRUTE_FORCE)),
            pair("methane/dimethylamine", "C", "CNC",
                 row(BONDS, NONE, 1, 0, 2, BRUTE_FORCE), row(ELEMENT, NONE, 1, 0, 2, BRUTE_FORCE),
                 row(ELEMENT_ORDER, NONE, 1, 0, 2, WHOLE)),
            pair("cyclohexanone/cyclohexanol", "O=C1CCCCC1", "OC1CCCCC1",
                 // C=O does not match C-O: the ring, 12 ways
                 row(BONDS, NONE, 6, 6, 12, BRUTE_FORCE),
                 row(ELEMENT, NONE, 7, 7, 2, BRUTE_FORCE)),
            pair("[N,O]CCC/ethanol", "[N,O]CCC", "OCC",
                 // O-C-C, the last query C has no partner
                 row(SMARTS, NONE, 3, 2, 1, HAND)),

            // the overlap tests of UniversalIsomorphismTester and QueryAtomContainerCreator; the pairs made
            // with overlap(...) are also checked against the overlaps of UniversalIsomorphismTester
            overlap("ethane/anhydride", "CC", "CC(=O)OC(=O)C",
                    // two C-C bonds, either way round
                    row(BONDS, NONE, 2, 1, 4, BRUTE_FORCE),
                    row(ELEMENT, NONE, 2, 1, 4, BRUTE_FORCE), row(BASIC, NONE, 2, 1, 4, WHOLE)),
            overlap("ethylcyclooctane", "C1CCCCCCC1CC", "C1CCCCCCC1CC",
                    // and its mirror image through the ethyl group
                    row(ANY_ATOM, NONE, 10, 10, 2, WHOLE)),
            overlap("cyclopentane/furan", "C1CCCC1", "O1C=CC=C1",
                    // five rotations, two directions
                    row(ANY, NONE, 5, 5, 10, WHOLE)),
            overlap("C*C/ethylamine", "C*C", "CCN",
                    // each query bond on C-C both ways round, or on C-N with * on N
                    row(SMARTS, NONE, 2, 1, 6, HAND)),

            // a panel of related drugs and other molecules; NONE keeps Kekule SMILES non-aromatic, so BONDS
            // compares the Kekule structures as written, and DAYLIGHT perceives aromaticity first
            pair("aspirin/salicylic acid", "CC(=O)OC1=CC=CC=C1C(=O)O", "OC(=O)C1=CC=CC=C1O",
                 // the phenol O on the ester O; the Kekule structures agree, so aromaticity changes nothing
                 row(BONDS, NONE, 10, 10, 1, WHOLE), row(BONDS, DAYLIGHT, 10, 10, 1, WHOLE),
                 // the C=O and C-OH oxygens of the acid can swap
                 row(ELEMENT, NONE, 10, 10, 2, WHOLE),
                 // the phenol H has no partner, the H of the acid keeps its O in place
                 row(BONDS, EXPLICIT_H, 15, 15, 1, PART),
                 row(ELEMENT, EXPLICIT_H, 15, 15, 1, PART)),
            pair("adenosine/guanosine", "NC1=NC=NC2=C1N=CN2C1OC(CO)C(O)C1O", "NC1=NC2=C(N=CN2C2OC(CO)C(O)C2O)C(=O)N1",
                 // the 6-amino N of adenine on N1 of guanine and N1 of adenine on the 2-amino N, leaving out the
                 // N1-C6 bond; ring and chain matching keeps to the purine and the ribose, where the C6=N1 bond of
                 // adenine is single in guanine
                 row(BONDS, NONE, 19, 20, 1, PINNED),
                 row(RINGS_CHAINS, NONE, 18, 19, 1, PINNED), row(ELEMENT, NONE, 19, 20, 1, PINNED),
                 row(BONDS, EXPLICIT_H, 30, 31, 4, PINNED), row(ELEMENT, EXPLICIT_H, 30, 31, 4, PINNED)),
    };

    /** The other pairs, searched in the slow tests. */
    private static final Pair[] SLOW_PAIRS = {
            // the SMSD tests
            pair("ethers", "CCCOCC(C)=C", "C\\C=C/OCC=C",
                 // C-O-C-C=C: next to the O the target has C=C where the query has C-C
                 row(BONDS, NONE, 5, 4, 1, BRUTE_FORCE),
                 // the chain either way round, either end C at the branch; SMSD found 1, kept by the bond-order
                 // score of its chemical filters
                 row(ELEMENT, NONE, 7, 6, 4, BRUTE_FORCE)),
            pair("aniline/diamine", "Nc1ccccc1", "C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C",
                 // either ring N, either way round the ring; SMSD found 2 with its chemical filters
                 row(BONDS, NONE, 7, 7, 4, WHOLE),
                 row(BONDS, LEGACY, 7, 7, 4, BRUTE_FORCE), row(ELEMENT, LEGACY, 7, 7, 4, BRUTE_FORCE)),
            pair("ethane/spiro-octane", "CC", "C1CCC12CCCC2",
                 // 9 bonds, both ways round
                 row(BONDS, NONE, 2, 1, 18, BRUTE_FORCE),
                 row(ELEMENT, NONE, 2, 1, 18, BRUTE_FORCE), row(ELEMENT_ORDER, NONE, 2, 1, 18, WHOLE)),
            pair("ethane/methane", "CC", "C",
                 row(BONDS, NONE, 1, 0, 2, BRUTE_FORCE), row(ELEMENT, NONE, 1, 0, 2, BRUTE_FORCE)),
            pair("benzene/naphthalene", "C1=CC=CC=C1", "C1=CC=C2C=CC=CC2=C1",
                 // either ring, 12 ways each; SMSD found 6 the other way round, with its chemical filters
                 row(BONDS, LEGACY, 6, 6, 24, BRUTE_FORCE),
                 row(ELEMENT, LEGACY, 6, 6, 24, BRUTE_FORCE)),
            pair("benzene/benzene", "C1=CC=CC=C1", "C1=CC=CC=C1",
                 row(BONDS, LEGACY, 6, 6, 12, BRUTE_FORCE), row(ELEMENT, LEGACY, 6, 6, 12, BRUTE_FORCE)),
            pair("cyclohexane/benzene", "C1CCCCC1", "C1=CC=CC=C1",
                 // no single bond matches an aromatic bond: any atom on any atom
                 row(BONDS, LEGACY, 1, 0, 36, BRUTE_FORCE),
                 row(ELEMENT, LEGACY, 6, 6, 12, BRUTE_FORCE)),
            pair("cyclopropane/isobutane", "C1CC1", "CC(C)C",
                 // any ring atom on the central C, its neighbours on 2 of the 3 methyls
                 row(BONDS, NONE, 3, 2, 18, BRUTE_FORCE),
                 row(ELEMENT, NONE, 3, 2, 18, BRUTE_FORCE)),
            pair("methane/toluene", "C", "CC1=CC=CC=C1",
                 row(BONDS, NONE, 1, 0, 7, BRUTE_FORCE), row(ELEMENT, NONE, 1, 0, 7, BRUTE_FORCE)),
            pair("bilirubin glucuronides",
                 "CC1=C(C=C)\\C(NC1=O)=C\\C1=C(C)C(CCC(=O)O[C@@H]2O[C@@H]([C@@H](O)[C@H](O)[C@H]2O)C(O)=O)"
                 + "=C(CC2=C(CCC(O)=O)C(C)=C(N2)\\C=C2NC(=O)C(C=C)=C/2C)N1",
                 "CC1=C(C=C)\\C(NC1=O)=C\\C1=C(C)C(CCC(=O)O[C@@H]2O[C@@H]([C@@H](O)[C@H](O)[C@H]2O)C(O)=O)"
                 + "=C(CC2=C(CCC(=O)O[C@@H]3O[C@@H]([C@@H](O)[C@H](O)[C@H]3O)C(O)=O)C(C)=C(N2)\\C=C2NC(=O)"
                 + "C(C=C)=C/2C)N1",
                 row(BONDS, LEGACY, 55, 59, 1, WHOLE),
                 // the two O of each carboxyl group can swap
                 row(ELEMENT, LEGACY, 55, 59, 4, WHOLE)),
            pair("hydrazine/triazane", "NN", "NNN",
                 row(BONDS, NONE, 2, 1, 4, BRUTE_FORCE), row(ELEMENT, NONE, 2, 1, 4, BRUTE_FORCE)),
            pair("isobutane/tert-butanol", "CC(C)C", "CC(C)(C)O",
                 // the methyls on the methyls in any order
                 row(BONDS, NONE, 4, 3, 6, BRUTE_FORCE),
                 row(ELEMENT, NONE, 4, 3, 6, BRUTE_FORCE)),
            pair("methane/methane", "C", "C",
                 row(BONDS, NONE, 1, 0, 1, BRUTE_FORCE), row(ELEMENT, NONE, 1, 0, 1, BRUTE_FORCE)),
            pair("hydrogen/hydrogen", "[H]", "[H]",
                 row(BONDS, NONE, 1, 0, 1, BRUTE_FORCE), row(ELEMENT, NONE, 1, 0, 1, BRUTE_FORCE),
                 row(ELEMENT_ORDER, NONE, 1, 0, 1, WHOLE)),
            pair("aminoethanol/ethanol", "OCCN", "CCO",
                 row(BONDS, NONE, 3, 2, 1, BRUTE_FORCE), row(ELEMENT, NONE, 3, 2, 1, BRUTE_FORCE)),
            pair("disconnected", "CC.NNN", "CC.NNN",
                 // the larger component, either way round
                 row(BONDS, NONE, 3, 2, 2, BRUTE_FORCE),
                 row(ELEMENT, NONE, 3, 2, 2, BRUTE_FORCE)),
            pair("disconnected, one N", "CC.NNN", "CC.NO",
                 row(BONDS, NONE, 2, 1, 2, BRUTE_FORCE), row(ELEMENT, NONE, 2, 1, 2, BRUTE_FORCE)),
            pair("R atoms", "*", "*",
                 row(BONDS, NONE, 1, 0, 1, BRUTE_FORCE), row(ELEMENT, NONE, 1, 0, 1, BRUTE_FORCE)),
            pair("furan/cyclopentane", "O1C=CC=C1", "C1CCCC1",
                 // the single C-C bond of furan on any of 5 bonds, either way round
                 row(BONDS, NONE, 2, 1, 10, BRUTE_FORCE),
                 row(ELEMENT, NONE, 4, 3, 10, BRUTE_FORCE)),
            pair("furan/furan", "O1C=CC=C1", "O1C=CC=C1",
                 row(BONDS, NONE, 5, 5, 2, BRUTE_FORCE), row(ELEMENT, NONE, 5, 5, 2, BRUTE_FORCE)),

            // the overlap tests of UniversalIsomorphismTester and QueryAtomContainerCreator; the pairs made
            // with overlap(...) are also checked against the overlaps of UniversalIsomorphismTester
            overlap("cyclohexene/alpha-pinene", "C1=CCCCC1", ALPHA_PINENE,
                    // both six rings have the double bond, each either way round
                    row(BONDS, NONE, 6, 6, 4, BRUTE_FORCE),
                    row(ELEMENT, NONE, 6, 6, 24, BRUTE_FORCE)),
            overlap("cyclohexane/alpha-pinene", "C1CCCCC1", ALPHA_PINENE,
                    // a path of six atoms over single bonds, any ring bond left out, either way round: 12 paths
                    // x 12, and 2 of the paths are the six-membered rings without their double bond
                    row(BONDS, NONE, 6, 5, 144, BRUTE_FORCE),
                    row(ELEMENT, NONE, 6, 6, 24, BRUTE_FORCE)),
            overlap("pyrrole/indole", "C1NC=CC=1", "C12=CC=CC=C1C=CN2",
                    // and its mirror image through the N
                    row(BONDS, LEGACY, 5, 5, 2, BRUTE_FORCE),
                    row(ELEMENT, LEGACY, 5, 5, 2, BRUTE_FORCE)),
            overlap("decalin/decalin", "C1CCCC2C1CCCC2", "C1CCCC2C1CCCC2",
                    // swap the rings, turn the molecule over
                    row(BONDS, NONE, 10, 11, 4, BRUTE_FORCE),
                    row(ELEMENT, NONE, 10, 11, 4, WHOLE), row(BASIC, NONE, 10, 11, 4, WHOLE)),
            overlap("cyclohexane/decalin", "C1CCCCC1", "C1CCCC2C1CCCC2",
                    // either ring, 12 ways each
                    row(BONDS, NONE, 6, 6, 24, BRUTE_FORCE),
                    row(BASIC, NONE, 6, 6, 24, WHOLE)),
            overlap("ADN/5SD", ADN, SD5,
                    // 5SD has only single C-C, C-H and C=O bonds, ADN no C=O: the ribose C and their H map, on
                    // chains of five 5SD carbons
                    row(BONDS, NONE, 11, 10, 88, PART),
                    // 32 more: a ribose O on the O of a C=O, in place of the H of the C it is bonded to
                    row(ELEMENT, NONE, 11, 10, 120, HAND)),
            overlap("bug 2944080", "CCC(=CC)C(=O)NC(N)=O", BUG_2944080,
                    // the query has =C-C, the target no C=C: the rest is the query of the next pair
                    row(BONDS, NONE, 9, 8, 2, BRUTE_FORCE)),
            overlap("bug 2944080, substructure", "CCCC(=O)NC(N)=O", BUG_2944080,
                    // the propyl group on either ethyl group and the C between them
                    row(BONDS, NONE, 9, 8, 2, BRUTE_FORCE)),
            overlap("butane/butane", "CCCC", "CCCC",
                    row(BONDS, NONE, 4, 3, 2, BRUTE_FORCE)),
            overlap("ethanolamine/pyrimidine", "NCCO", "NC1=NC=C(O)C(N)=N1.[H]Cl.[H]Cl",
                    // single bonds only from the 4-amino N over C4 and C5 to the OH
                    row(BONDS, NONE, 4, 3, 1, BRUTE_FORCE)),
            overlap("dimethylbutadiene/o-xylene", "C=C(C)C(C)=C", O_XYLENE,
                    // the Kekule structure as written: the two methylated ring atoms and their ring double bonds,
                    // both ways round; with aromaticity perceived only one C-C single bond, 3 x 2 x 2 ways
                    row(BONDS, NONE, 6, 5, 2, BRUTE_FORCE),
                    row(BONDS, DAYLIGHT, 2, 1, 12, BRUTE_FORCE),
                    row(ELEMENT_ORDER, NONE, 6, 5, 2, WHOLE)),
            overlap("dimethylbutene/o-xylene", "CC(C)=C(C)C", O_XYLENE,
                    // no ring double bond has two single bonds at both ends; two have them at one end, two ways
                    // round, two choices of the methyls at each end. ELEMENT_ORDER as BONDS: no aromatic bonds.
                    row(BONDS, NONE, 5, 4, 16, BRUTE_FORCE),
                    row(ELEMENT_ORDER, NONE, 5, 4, 16, HAND)),
            overlap("C**C/SCCS", "C**C", "SCCS",
                    // a query C on a C, the chain running over the other C to an S
                    row(SMARTS, NONE, 3, 2, 4, HAND)),
            pair("pinacol boronates", "*B1OC(C)(C)C(C)(C)O1", "CC1=CC(=CC=C1)B1OC(C)(C)C(C)(C)O1",
                 // a substructure: 8 symmetries of the pinacol group x 3! for the H of each of its 4 methyls
                 row(PSEUDO_ANY, EXPLICIT_H, 22, 22, 8 * 6 * 6 * 6 * 6, WHOLE)),

            // a panel of related drugs and other molecules; NONE keeps Kekule SMILES non-aromatic, so BONDS
            // compares the Kekule structures as written, and DAYLIGHT perceives aromaticity first
            pair("cyclohexane/hexane", "C1CCCCC1", "CCCCCC",
                 // a ring bond left out, 6 bonds and 2 directions
                 row(BONDS, NONE, 6, 5, 12, BRUTE_FORCE),
                 row(ELEMENT, NONE, 6, 5, 12, BRUTE_FORCE),
                 row(BONDS, EXPLICIT_H, 18, 17, 1000, PINNED), row(ELEMENT, EXPLICIT_H, 18, 17, 1000, PINNED)),
            pair("cyclopentane/cyclohexane", "C1CCCC1", "C1CCCCC1",
                 // a ring bond left out, a chain of 5 on the 6 ring at 6 places in 2 directions: 5 x 6 x 2
                 row(BONDS, NONE, 5, 4, 60, BRUTE_FORCE),
                 row(ELEMENT, NONE, 5, 4, 60, BRUTE_FORCE),
                 row(BONDS, EXPLICIT_H, 15, 14, 1000, PINNED), row(ELEMENT, EXPLICIT_H, 15, 14, 1000, PINNED)),
            pair("caffeine/theobromine", "CN1C=NC2=C1C(=O)N(C(=O)N2C)C", "CN1C=NC2=C1C(=O)NC(=O)N2C",
                 row(BONDS, NONE, 13, 14, 1, WHOLE), row(ELEMENT, NONE, 13, 14, 1, WHOLE),
                 // the N-H of theobromine has no partner, the H of the two shared methyls map in 3! x 3! ways
                 row(BONDS, EXPLICIT_H, 20, 21, 36, PART),
                 row(ELEMENT, EXPLICIT_H, 20, 21, 36, PART)),
            pair("caffeine/theophylline", "CN1C=NC2=C1C(=O)N(C(=O)N2C)C", "CN1C2=C(C(=O)N(C1=O)C)NC=N2",
                 row(BONDS, NONE, 13, 14, 1, WHOLE), row(BONDS, DAYLIGHT, 13, 14, 1, WHOLE),
                 row(ELEMENT, NONE, 13, 14, 1, WHOLE),
                 // as theobromine
                 row(BONDS, EXPLICIT_H, 20, 21, 36, PART),
                 row(ELEMENT, EXPLICIT_H, 20, 21, 36, PART)),
            pair("paracetamol/phenacetin", "CC(=O)NC1=CC=C(C=C1)O", "CCOC1=CC=C(C=C1)NC(C)=O",
                 row(BONDS, NONE, 11, 11, 1, WHOLE),
                 // only the Kekule structures keep the ring from being turned over
                 row(BONDS, DAYLIGHT, 11, 11, 2, WHOLE),
                 row(ELEMENT, NONE, 11, 11, 2, WHOLE),
                 row(BONDS, EXPLICIT_H, 19, 19, 6, PART), row(ELEMENT, EXPLICIT_H, 19, 19, 12, PART)),
            pair("ibuprofen/naproxen", "CC(C)CC1=CC=C(C=C1)C(C)C(=O)O", "COC1=CC2=C(C=C1)C=C(C=C2)C(C)C(=O)O",
                 row(BONDS, NONE, 12, 12, 1, PART),
                 // the CH2 of the isobutyl group is no longer on a ring C of naproxen, the ring turns over
                 row(BONDS, DAYLIGHT, 11, 11, 2, PART),
                 row(ELEMENT, NONE, 14, 14, 8, PART),
                 row(BONDS, EXPLICIT_H, 21, 21, 12, PART), row(ELEMENT, EXPLICIT_H, 25, 24, 72, PINNED)),
            pair("ibuprofen/ketoprofen", "CC(C)CC1=CC=C(C=C1)C(C)C(=O)O",
                 "CC(C(=O)O)C1=CC(=CC=C1)C(=O)C2=CC=CC=C2",
                 row(BONDS, NONE, 11, 11, 1, PART), row(ELEMENT, NONE, 14, 13, 16, PINNED),
                 row(BONDS, EXPLICIT_H, 19, 19, 6, PART), row(ELEMENT, EXPLICIT_H, 24, 23, 144, PINNED)),
            pair("dopamine/noradrenaline", "NCCC1=CC(O)=C(O)C=C1", "NCC(O)C1=CC(O)=C(O)C=C1",
                 row(BONDS, NONE, 11, 11, 1, WHOLE), row(ELEMENT, NONE, 11, 11, 1, WHOLE),
                 row(BONDS, EXPLICIT_H, 21, 21, 8, PART), row(ELEMENT, EXPLICIT_H, 21, 21, 8, PART)),
            pair("nicotine/cotinine", "CN1CCCC1C2=CN=CC=C2", "CN1C(CCC1=O)C2=CN=CC=C2",
                 row(BONDS, NONE, 12, 13, 1, WHOLE), row(ELEMENT, NONE, 12, 13, 1, WHOLE),
                 row(BONDS, EXPLICIT_H, 24, 25, 24, PART), row(ELEMENT, EXPLICIT_H, 24, 25, 24, PART)),
            pair("diazepam/oxazepam", "CN1C(=O)CN=C(C2=C1C=CC(=C2)Cl)C3=CC=CC=C3",
                 "OC1N=C(C2=CC=CC=C2)C2=C(NC1=O)C=CC(Cl)=C2",
                 row(BONDS, NONE, 19, 21, 1, PART), row(ELEMENT, NONE, 19, 21, 2, PART),
                 row(BONDS, EXPLICIT_H, 28, 30, 2, PART), row(ELEMENT, EXPLICIT_H, 28, 30, 4, PART)),
            pair("chlorpromazine/promazine", "CN(C)CCCN1C2=CC=CC=C2SC3=C1C=C(C=C3)Cl",
                 "CN(C)CCCN1C2=CC=CC=C2SC2=CC=CC=C21",
                 row(BONDS, NONE, 20, 21, 8, PINNED), row(ELEMENT, NONE, 20, 22, 4, WHOLE),
                 row(BONDS, EXPLICIT_H, 38, 39, 1000, PINNED), row(ELEMENT, EXPLICIT_H, 39, 41, 1000, PINNED)),
            pair("amitriptyline/nortriptyline", "CN(C)CCC=C1C2=CC=CC=C2CCC3=CC=CC=C31",
                 "CNCCC=C1C2=CC=CC=C2CCC2=CC=CC=C12",
                 row(BONDS, NONE, 20, 22, 4, WHOLE), row(ELEMENT, NONE, 20, 22, 4, WHOLE),
                 row(BONDS, EXPLICIT_H, 40, 42, 384, PART), row(ELEMENT, EXPLICIT_H, 40, 42, 384, PART)),
            pair("propranolol/atenolol", "CC(C)NCC(COC1=CC=CC2=CC=CC=C21)O", "CC(C)NCC(O)COC1=CC=C(CC(N)=O)C=C1",
                 row(BONDS, NONE, 16, 15, 2, PINNED), row(ELEMENT, NONE, 17, 16, 12, PINNED),
                 row(BONDS, EXPLICIT_H, 33, 32, 288, PINNED), row(ELEMENT, EXPLICIT_H, 35, 34, 1000, PINNED)),
            pair("sertraline/fluoxetine", "CNC1CCC(C2=CC=CC=C12)C3=CC(=C(C=C3)Cl)Cl",
                 "CNCCC(C1=CC=CC=C1)OC2=CC=C(C=C2)C(F)(F)F",
                 row(BONDS, NONE, 11, 10, 3, PINNED), row(ELEMENT, NONE, 11, 10, 48, PINNED),
                 row(BONDS, EXPLICIT_H, 23, 22, 48, PINNED), row(ELEMENT, EXPLICIT_H, 23, 22, 96, PINNED)),
            pair("morphine/codeine", "CN1CCC23C4C1CC5=C2C(=C(C=C5)O)OC3C(C=C4)O",
                 "CN1CCC23C4C1CC5=C2C(=C(C=C5)OC)OC3C(C=C4)O",
                 row(BONDS, NONE, 21, 25, 1, WHOLE), row(ELEMENT, NONE, 21, 25, 1, WHOLE),
                 // the phenol H has no partner; the H of the N-methyl (3!) and of the three CH2 (2 x 2 x 2)
                 row(BONDS, EXPLICIT_H, 39, 43, 48, PART),
                 row(ELEMENT, EXPLICIT_H, 39, 43, 48, PART)),
            pair("morphine/heroin", "CN1CCC23C4C1CC5=C2C(=C(C=C5)O)OC3C(C=C4)O",
                 "CC(=O)OC1C=CC2C3CC4=C5C2(C1OC5=C(C=C4)OC(C)=O)CCN3C",
                 row(BONDS, NONE, 21, 25, 1, WHOLE), row(ELEMENT, NONE, 21, 25, 1, WHOLE),
                 row(BONDS, EXPLICIT_H, 38, 42, 48, PART), row(ELEMENT, EXPLICIT_H, 38, 42, 48, PART)),
            pair("testosterone/estradiol", "CC12CCC3C(C1CCC2O)CCC4=CC(=O)CCC34C", "CC12CCC3C(C1CCC2O)CCC4=C3C=CC(=C4)O",
                 row(BONDS, NONE, 19, 19, 2, PINNED),
                 // rings C and D, C18, O17 and C5, C6, C7, C10, as ring A of estradiol is aromatic; the SMSD
                 // test had 15 atoms
                 row(BONDS, LEGACY, 15, 16, 1, PINNED),
                 row(BONDS, DAYLIGHT, 15, 16, 1, PINNED),
                 row(ELEMENT, NONE, 20, 23, 1, WHOLE),
                 row(BONDS, EXPLICIT_H, 41, 41, 1000, PINNED), row(ELEMENT, EXPLICIT_H, 43, 46, 1000, PINNED)),
            pair("progesterone/testosterone", "CC(=O)C1CCC2C1(CCC3C2CCC4=CC(=O)CCC34C)C",
                 "CC12CCC3C(C1CCC2O)CCC4=CC(=O)CCC34C",
                 row(BONDS, NONE, 20, 23, 1, PART), row(ELEMENT, NONE, 21, 22, 1, PINNED),
                 row(BONDS, EXPLICIT_H, 47, 50, 1000, PINNED), row(ELEMENT, EXPLICIT_H, 47, 50, 1000, PINNED)),
            pair("estrone/estradiol", "CC12CCC3C(C1CCC2=O)CCC4=C3C=CC(=C4)O", "CC12CCC3C(C1CCC2O)CCC4=C3C=CC(=C4)O",
                 // the keto O of estrone is double bonded, the hydroxy O of estradiol single bonded
                 row(BONDS, NONE, 19, 22, 1, PART),
                 row(ELEMENT, NONE, 20, 23, 1, WHOLE),
                 row(BONDS, EXPLICIT_H, 41, 44, 384, PART), row(ELEMENT, EXPLICIT_H, 42, 45, 384, WHOLE)),
            pair("cortisol/prednisolone", "OCC(=O)C1(O)CCC2C3CCC4=CC(=O)CCC4(C)C3C(O)CC21C",
                 "OCC(=O)C1(O)CCC2C3CCC4=CC(=O)C=CC4(C)C3C(O)CC21C",
                 row(BONDS, NONE, 26, 28, 2, PINNED), row(ELEMENT, NONE, 26, 29, 1, WHOLE),
                 row(BONDS, EXPLICIT_H, 54, 56, 1000, PINNED),
                 // all of prednisolone: 3! x 3! for the H of the methyls, 2^8 as the H of six CH2 can swap and
                 // those of C1 and C2 have two places each
                 row(ELEMENT, EXPLICIT_H, 54, 57, 9216, WHOLE)),
            pair("cholesterol/cholic acid", CHOLESTEROL, CHOLIC_ACID,
                 // C5=C6 is not common, so ring A can also be mapped turned over
                 row(BONDS, NONE, 25, 27, 2, PINNED),
                 // the 17 ring C, C18, C19, O3 and C20 to C24; the SMSD test had 25 atoms
                 row(ELEMENT, NONE, 25, 28, 1, PART),
                 row(BONDS, EXPLICIT_H, 60, 62, 1000, PINNED), row(ELEMENT, EXPLICIT_H, 60, 63, 1000, PINNED)),
            pair("penicillin G/amoxicillin", "CC1(C(N2C(S1)C(C2=O)NC(=O)CC3=CC=CC=C3)C(=O)O)C",
                 "CC1(C(N2C(S1)C(C2=O)NC(=O)C(C3=CC=C(C=C3)O)N)C(=O)O)C",
                 // the two methyls of the thiazolidine ring can swap
                 row(BONDS, NONE, 23, 25, 2, WHOLE),
                 row(ELEMENT, NONE, 23, 25, 8, WHOLE),
                 row(BONDS, EXPLICIT_H, 39, 41, 144, PART), row(ELEMENT, EXPLICIT_H, 39, 41, 288, PART)),
            pair("ampicillin/amoxicillin", "CC1(C(N2C(S1)C(C2=O)NC(=O)C(C3=CC=CC=C3)N)C(=O)O)C",
                 "CC1(C(N2C(S1)C(C2=O)NC(=O)C(C3=CC=C(C=C3)O)N)C(=O)O)C",
                 row(BONDS, NONE, 24, 26, 2, WHOLE), row(ELEMENT, NONE, 24, 26, 8, WHOLE),
                 row(BONDS, EXPLICIT_H, 42, 44, 144, PART), row(ELEMENT, EXPLICIT_H, 42, 44, 288, PART)),
            pair("cephalexin/cefadroxil", "CC1=C(N2C(C(C2=O)NC(=O)C(C3=CC=CC=C3)N)SC1)C(=O)O",
                 "CC1=C(N2C(C(C2=O)NC(=O)C(C3=CC=C(C=C3)O)N)SC1)C(=O)O",
                 row(BONDS, NONE, 24, 26, 1, WHOLE), row(ELEMENT, NONE, 24, 26, 4, WHOLE),
                 row(BONDS, EXPLICIT_H, 40, 42, 24, PART), row(ELEMENT, EXPLICIT_H, 40, 42, 48, PART)),
            pair("ciprofloxacin/levofloxacin", "C1CC1N2C=C(C(=O)C3=CC(=C(C=C32)N4CCNCC4)F)C(=O)O",
                 "CC1COC2=C3N1C=C(C(=O)C3=CC(=C2N4CCN(CC4)C)F)C(=O)O",
                 row(BONDS, NONE, 24, 26, 4, PART), row(ELEMENT, NONE, 24, 26, 8, PART),
                 row(BONDS, EXPLICIT_H, 40, 42, 768, PINNED), row(ELEMENT, EXPLICIT_H, 40, 42, 768, PINNED)),
            pair("omeprazole/lansoprazole", "CC1=CN=C(C(=C1OC)C)CS(=O)C2=NC3=C(N2)C=C(C=C3)OC",
                 "CC1=C(C=CN=C1CS(=O)C2=NC3=CC=CC=C3N2)OCC(F)(F)F",
                 row(BONDS, NONE, 21, 22, 2, PINNED), row(ELEMENT, NONE, 21, 23, 2, PART),
                 row(BONDS, EXPLICIT_H, 32, 33, 144, PINNED), row(ELEMENT, EXPLICIT_H, 33, 35, 72, PART)),
            pair("losartan/valsartan", "CCCCC1=NC(=C(N1CC2=CC=C(C=C2)C3=CC=CC=C3C4=NNN=N4)CO)Cl",
                 "CCCCC(=O)N(CC1=CC=C(C=C1)C2=CC=CC=C2C3=NNN=N3)C(C(C)C)C(=O)O",
                 row(BONDS, NONE, 27, 29, 1, PART), row(ELEMENT, NONE, 28, 30, 8, PART),
                 row(BONDS, EXPLICIT_H, 48, 50, 96, PART), row(ELEMENT, EXPLICIT_H, 49, 51, 192, PART)),
            pair("sildenafil/vardenafil", "CCCC1=NN(C2=C1N=C(NC2=O)C3=C(C=CC(=C3)S(=O)(=O)N4CCN(CC4)C)OCC)C",
                 "CCCC1=NC(=C2N1NC(=NC2=O)C3=C(C=CC(=C3)S(=O)(=O)N4CCN(CC4)CC)OCC)C",
                 row(BONDS, NONE, 24, 25, 4, PART), row(ELEMENT, NONE, 30, 31, 4, PINNED),
                 row(BONDS, EXPLICIT_H, 43, 44, 1000, PINNED), row(ELEMENT, EXPLICIT_H, 48, 49, 1000, PINNED)),
            pair("imatinib/nilotinib", "CC1=C(C=C(C=C1)NC(=O)C2=CC=C(C=C2)CN3CCN(CC3)C)NC4=NC=CC(=N4)C5=CN=CC=C5",
                 "CC1=C(C=C(C=C1)C(=O)NC2=CC(=CC(=C2)C(F)(F)F)N3C=C(N=C3)C)NC4=NC=CC(=N4)C5=CN=CC=C5",
                 row(BONDS, NONE, 21, 22, 1, PART), row(ELEMENT, NONE, 24, 24, 8, PINNED),
                 row(BONDS, EXPLICIT_H, 33, 35, 6, PART), row(ELEMENT, EXPLICIT_H, 33, 35, 6, PART)),
            pair("atorvastatin/rosuvastatin",
                 "CC(C)C1=C(C(=C(N1CCC(CC(CC(=O)O)O)O)C2=CC=C(C=C2)F)C3=CC=CC=C3)C(=O)NC4=CC=CC=C4",
                 "CC(C)C1=NC(=NC(=C1C=CC(CC(CC(=O)O)O)O)C2=CC=C(C=C2)F)N(C)S(=O)(=O)C",
                 row(BONDS, NONE, 16, 16, 2, PART), row(ELEMENT, NONE, 20, 20, 8, PINNED),
                 row(BONDS, EXPLICIT_H, 24, 24, 12, PART), row(ELEMENT, EXPLICIT_H, 30, 30, 96, PINNED)),
            pair("tamoxifen/raloxifene", "CCC(=C(C1=CC=CC=C1)C2=CC=C(C=C2)OCCN(C)C)C3=CC=CC=C3",
                 "OC1=CC=C(C=C1)C1=C(C(=O)C2=CC=C(OCCN3CCCCC3)C=C2)C2=CC=C(O)C=C2S1",
                 row(BONDS, NONE, 21, 22, 1, PART), row(ELEMENT, NONE, 22, 23, 8, PART),
                 row(BONDS, EXPLICIT_H, 34, 34, 288, PART), row(ELEMENT, EXPLICIT_H, 38, 38, 1000, PINNED)),
            pair("warfarin/coumarin", "CC(=O)CC(C1=CC=CC=C1)C2=C(C3=CC=CC=C3OC2=O)O", "O=C1C=CC2=CC=CC=C2O1",
                 row(BONDS, NONE, 11, 12, 1, WHOLE), row(ELEMENT, NONE, 11, 12, 1, WHOLE),
                 row(BONDS, EXPLICIT_H, 15, 16, 1, PART), row(ELEMENT, EXPLICIT_H, 16, 16, 13, PINNED)),
            pair("ATP/ADP", "C1=NC(=C2C(=N1)N(C=N2)C3C(C(C(O3)COP(=O)(O)OP(=O)(O)OP(=O)(O)O)O)O)N",
                 "C1=NC(=C2C(=N1)N(C=N2)C3C(C(C(O3)COP(=O)(O)OP(=O)(O)O)O)O)N",
                 // the two OH of the terminal phosphate of ADP on the OH and the bridging O of the middle
                 // phosphate of ATP
                 row(BONDS, NONE, 27, 29, 2, WHOLE),
                 // also the three free O of that phosphate in any order (3!), and the =O and OH of the first
                 row(ELEMENT, NONE, 27, 29, 12, WHOLE),
                 row(BONDS, EXPLICIT_H, 41, 43, 8, PART), row(ELEMENT, EXPLICIT_H, 41, 43, 16, PART)),
            pair("glucose/sucrose", "OCC1OC(O)C(O)C(O)C1O", "OCC1OC(OC2(CO)OC(CO)C(O)C2O)C(O)C(O)C1O",
                 row(BONDS, NONE, 12, 12, 1, WHOLE), row(ELEMENT, NONE, 12, 12, 1, WHOLE),
                 row(BONDS, EXPLICIT_H, 23, 23, 2, PART), row(ELEMENT, EXPLICIT_H, 23, 23, 2, PART)),
            pair("GAF/AGF", "NCC(=O)NC(C)C(=O)NC(CC1=CC=CC=C1)C(=O)O", "CC(N)C(=O)NCC(=O)NC(CC1=CC=CC=C1)C(=O)O",
                 row(BONDS, NONE, 20, 20, 1, PART), row(ELEMENT, NONE, 20, 20, 4, PART),
                 row(BONDS, EXPLICIT_H, 35, 35, 16, PINNED), row(ELEMENT, EXPLICIT_H, 35, 35, 32, PINNED)),
            pair("Leu-enkephalin/Met-enkephalin",
                 "NC(CC1=CC=C(O)C=C1)C(=O)NCC(=O)NCC(=O)NC(CC1=CC=CC=C1)C(=O)NC(CC(C)C)C(=O)O",
                 "NC(CC1=CC=C(O)C=C1)C(=O)NCC(=O)NCC(=O)NC(CC1=CC=CC=C1)C(=O)NC(CCSC)C(=O)O",
                 row(BONDS, NONE, 38, 39, 1, PART), row(ELEMENT, NONE, 38, 39, 8, PART),
                 row(BONDS, EXPLICIT_H, 69, 70, 128, PART), row(ELEMENT, EXPLICIT_H, 69, 70, 512, PART)),
            pair("palmitic/oleic acid", "CCCCCCCCCCCCCCCC(=O)O", "CCCCCCCCC=CCCCCCCCC(=O)O",
                 row(BONDS, NONE, 11, 10, 1, PART), row(ELEMENT, NONE, 18, 17, 2, WHOLE),
                 row(BONDS, EXPLICIT_H, 27, 26, 1000, PINNED), row(ELEMENT, EXPLICIT_H, 47, 46, 1000, PINNED)),
            pair("decane/cyclodecane", "CCCCCCCCCC", "C1CCCCCCCCC1",
                 // the chain on the ring at 10 places in 2 directions
                 row(BONDS, NONE, 10, 9, 20, WHOLE),
                 row(ELEMENT, NONE, 10, 9, 20, WHOLE),
                 row(BONDS, EXPLICIT_H, 30, 29, 1000, PINNED), row(ELEMENT, EXPLICIT_H, 30, 29, 1000, PINNED)),
            pair("naphthalene/azulene", "c1ccc2ccccc2c1", "c1ccc2cccc2cc1",
                 // 10 atoms and 10 bonds have one ring: the 10-membered perimeters, 10 rotations in 2 directions
                 row(BONDS, NONE, 10, 10, 20, BRUTE_FORCE),
                 row(ELEMENT, NONE, 10, 10, 20, BRUTE_FORCE),
                 row(BONDS, EXPLICIT_H, 18, 17, 16, PINNED), row(ELEMENT, EXPLICIT_H, 18, 17, 16, PINNED)),
            pair("anthracene/phenanthrene", "c1ccc2cc3ccccc3cc2c1", "c1ccc2c(c1)ccc1ccccc12",
                 row(BONDS, NONE, 14, 15, 16, PINNED), row(ELEMENT, NONE, 14, 15, 16, PINNED),
                 row(BONDS, EXPLICIT_H, 23, 24, 8, PINNED), row(ELEMENT, EXPLICIT_H, 23, 24, 8, PINNED)),
            pair("pyrene/perylene", "c1cc2ccc3cccc4ccc(c1)c2c34", "c1cc2cccc3c4cccc5cccc(c(c1)c23)c54",
                 row(BONDS, NONE, 16, 18, 16, PINNED), row(ELEMENT, NONE, 16, 18, 16, PINNED),
                 row(BONDS, EXPLICIT_H, 26, 26, 16, PINNED), row(ELEMENT, EXPLICIT_H, 26, 26, 16, PINNED)),
            pair("adamantane/cubane", "C1C2CC3CC1CC(C2)C3", "C12C3C4C1C5C2C3C45",
                 row(BONDS, NONE, 8, 8, 1000, PINNED), row(ELEMENT, NONE, 8, 8, 1000, PINNED),
                 row(BONDS, EXPLICIT_H, 16, 16, 1000, PINNED), row(ELEMENT, EXPLICIT_H, 16, 16, 1000, PINNED)),
            pair("strychnine/brucine", "O=C1CC2OCC=C3CN4CCC56C4CC3C2C6N1C1=CC=CC=C51",
                 "COC1=C(OC)C=C2C(=C1)C13CCN4CC5=CCOC6CC(=O)N2C3C6C5CC41",
                 row(BONDS, NONE, 25, 31, 1, WHOLE), row(ELEMENT, NONE, 25, 31, 1, WHOLE),
                 row(BONDS, EXPLICIT_H, 45, 51, 64, PART), row(ELEMENT, EXPLICIT_H, 45, 51, 64, PART)),
            pair("erythromycin/clarithromycin",
                 "CCC1C(C(C(C(=O)C(CC(C(C(C(C(C(=O)O1)C)OC2CC(C(C(O2)C)O)(C)OC)C)OC3C(C(CC(O3)C)N(C)C)O)(C)O)C)C)O)"
                 + "(C)O",
                 "CCC1C(C(C(C(=O)C(CC(C(C(C(C(C(=O)O1)C)OC2CC(C(C(O2)C)O)(C)OC)C)OC3C(C(CC(O3)C)N(C)C)O)(C)OC)C)C)"
                 + "O)(C)O",
                 row(BONDS, NONE, 51, 53, 2, WHOLE), row(ELEMENT, NONE, 51, 53, 2, WHOLE),
                 row(BONDS, EXPLICIT_H, 117, 119, 1000, PINNED), row(ELEMENT, EXPLICIT_H, 117, 119, 1000, PINNED)),
            pair("benzene/hexane", "c1ccccc1", "CCCCCC",
                 // aromatic bonds do not match single bonds: any atom on any atom
                 row(BONDS, NONE, 1, 0, 36, BRUTE_FORCE),
                 // as cyclohexane/hexane
                 row(ELEMENT, NONE, 6, 5, 12, BRUTE_FORCE),
                 // a C-H bond at most: any of the 6 of benzene on any of the 14 of hexane
                 row(BONDS, EXPLICIT_H, 2, 1, 84, HAND),
                 row(ELEMENT, EXPLICIT_H, 12, 11, 1000, PINNED)),

            // larger pairs: a made-up peptide of 40 residues and the same without its fifth, glucagon and GLP-1
            // (7-37), and two benzenoid flakes
            pair("pep40/del", peptide("WWHNEVDWCYHSVQMRWRNLIGIDWLTSMRLYDETQGMFS"),
                 peptide("WWHNVDWCYHSVQMRWRNLIGIDWLTSMRLYDETQGMFS"),
                 // 2^8 for the methyls of 2 Val and 3 Leu and the rings of Phe and 2 Tyr turned over
                 row(BONDS, NONE, 339, 350, 256, PINNED)),
            large("glucagon/GLP-1", peptide("HSQGTFTSDYSKYLDSRRAQDFVQWLMNT"),
                  peptide("HAEGTFTSDVSSYLEGQAAKEFIAWLVKGRG"),
                  row(BONDS, NONE, 205, 210, 256, PINNED)),
            large("flake2x5/flake3x4", "c1cc2ccc3cc4cc5cc6cccc7ccc8cc9cc%10cc(c1)c2c3c%10c4c9c5c8c76",
                  "c1cc2cc3cc4ccc5ccc6ccc7cc8cc9cccc%10c(c1)c2c1c3c2c4c5c6c7c2c8c1c9%10",
                  // all 34 atoms of the 2x5 flake, 2 of its 43 bonds without a partner
                  row(BONDS, NONE, 34, 41, 4, PINNED)),
    };

    @ParameterizedTest(name = "{0}")
    @MethodSource("rows")
    void testMappings(Case c) throws Exception {
        assertMappings(c);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("bruteForceRows")
    void testBruteForce(Case c) throws Exception {
        assertBruteForce(c);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("reorderedRows")
    void testAtomOrder(Case c) throws Exception {
        assertAtomOrder(c);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("overlapRows")
    void testOverlapBounds(Case c) throws Exception {
        assertOverlapBounds(c);
    }

    @Tag("SlowTest")
    @ParameterizedTest(name = "{0}")
    @MethodSource("slowRows")
    void testSlowMappings(Case c) throws Exception {
        assertMappings(c);
    }

    @Tag("SlowTest")
    @ParameterizedTest(name = "{0}")
    @MethodSource("slowBruteForceRows")
    void testSlowBruteForce(Case c) throws Exception {
        assertBruteForce(c);
    }

    @Tag("SlowTest")
    @ParameterizedTest(name = "{0}")
    @MethodSource("slowReorderedRows")
    void testSlowAtomOrder(Case c) throws Exception {
        assertAtomOrder(c);
    }

    @Tag("SlowTest")
    @ParameterizedTest(name = "{0}")
    @MethodSource("slowOverlapRows")
    void testSlowOverlapBounds(Case c) throws Exception {
        assertOverlapBounds(c);
    }

    static Stream<Case> rows() {
        return cases(PAIRS);
    }

    static Stream<Case> bruteForceRows() {
        return rows().filter(Case::bruteForce);
    }

    static Stream<Case> reorderedRows() {
        return rows().filter(Case::reordered);
    }

    static Stream<Case> overlapRows() {
        return rows().filter(Case::overlap);
    }

    static Stream<Case> slowRows() {
        return cases(SLOW_PAIRS);
    }

    static Stream<Case> slowBruteForceRows() {
        return slowRows().filter(Case::bruteForce);
    }

    static Stream<Case> slowReorderedRows() {
        return slowRows().filter(Case::reordered);
    }

    static Stream<Case> slowOverlapRows() {
        return slowRows().filter(Case::overlap);
    }

    /**
     * Checks the maximum mappings of a row as the class documentation says.
     */
    private static void assertMappings(Case c) throws Exception {
        IAtomContainer query = query(c);
        IAtomContainer target = target(c);
        IAtomContainer rules = c.condition.rules(query);
        MCS mcs = c.condition.search(query);
        List<int[]> mappings = mcs.matchAll(target);
        Set<String> keys = keys(mappings);
        Assertions.assertEquals(mappings.size(), keys.size(), "distinct mappings");
        Assertions.assertEquals(Math.min(c.mappings, CAP), mappings.size(), "maximum mappings");
        int[] match = mcs.match(target);
        List<int[]> checked = new ArrayList<>(mappings);
        checked.add(match);
        for (int[] mapping : checked) {
            Assertions.assertEquals(c.atoms, MCSTesting.atoms(mapping), "mapped atoms");
            MCSTesting.assertValid(rules, target, mapping, c.condition == BONDS);
            Assertions.assertEquals(c.bonds, MCSTesting.commonBonds(rules, target, mapping, c.condition == BONDS),
                                    "common bonds");
        }
        if (c.mappings < CAP) {
            Assertions.assertTrue(keys.contains(Arrays.toString(match)), "match is a maximum mapping");
            if (c.condition.symmetric()) {
                Assertions.assertEquals(keys, MCSTesting.inverses(c.condition.search(target).matchAll(query),
                                                                  query.getAtomCount()), "the other way round");
            }
        }
        Set<String> embeddings = embeddings(c, query, target, mappings, false);
        if (embeddings == null && c.condition.symmetric()) {
            embeddings = embeddings(c, query, target, mappings, true);
        }
        if (embeddings != null) {
            assertExact(c, embeddings, keys);
        } else {
            Assertions.assertFalse(c.known == WHOLE || c.known == PART, "the embeddings of a part");
        }
        if (c.known == WHOLE || c.known == PART) {
            Assertions.assertEquals(c.known == WHOLE, whole(c, query) || whole(c, target), "WHOLE or PART");
        }
        // an embedding of a connected molecule would map it whole; with explicit hydrogens a substructure search
        // that fails can try every order of the hydrogens
        if (c.prep != EXPLICIT_H && !whole(c, query) && ConnectivityChecker.isConnected(query)) {
            Assertions.assertFalse(c.condition.pattern(query).matches(target), "the query is a substructure");
        }
        if (c.prep != EXPLICIT_H && !whole(c, target) && c.condition.symmetric()
                && ConnectivityChecker.isConnected(target)) {
            Assertions.assertFalse(c.condition.pattern(target).matches(query), "the target is a substructure");
        }
    }

    /**
     * Checks that the mappings of a BRUTE_FORCE row are those of the brute
     * force search.
     */
    private static void assertBruteForce(Case c) throws Exception {
        IAtomContainer query = query(c);
        IAtomContainer target = target(c);
        Assertions.assertTrue(c.condition.plain(), "the brute force search matches as BONDS or ELEMENT");
        assertExact(c, MCSTesting.exact(query, target, c.condition == BONDS),
                    keys(c.condition.search(query).matchAll(target)));
    }

    /**
     * Checks that the atoms and bonds of both molecules in another order give
     * the same mappings (bug 999330).
     */
    private static void assertAtomOrder(Case c) throws Exception {
        Set<String> expected = keys(c.condition.search(query(c)).matchAll(target(c)));
        IAtomContainer query = query(c);
        IAtomContainer target = target(c);
        Random random = new Random(999330L);
        int[] queryOrder = MCSTesting.shuffle(query, random);
        int[] targetOrder = MCSTesting.shuffle(target, random);
        Set<String> found = new HashSet<>();
        for (int[] mapping : c.condition.search(query).matchAll(target)) {
            found.add(Arrays.toString(MCSTesting.unshuffle(mapping, queryOrder, targetOrder)));
        }
        Assertions.assertEquals(expected, found, "the same mappings");
    }

    /**
     * {@link UniversalIsomorphismTester} finds maximal, not maximum, common
     * substructures: none of its connected overlaps has more atoms than the
     * MCS, and a query it finds whole is mapped whole.
     */
    @SuppressWarnings("deprecation")
    private static void assertOverlapBounds(Case c) throws Exception {
        IAtomContainer query = c.condition.rules(query(c));
        IAtomContainer target = target(c);
        UniversalIsomorphismTester uit = new UniversalIsomorphismTester();
        Assertions.assertTrue(largestConnected(uit.getOverlaps(target, query)) <= c.atoms, "overlap");
        // a query molecule can only be the second argument
        if (c.condition.plain()) {
            Assertions.assertTrue(largestConnected(uit.getOverlaps(query, target)) <= c.atoms, "overlap");
        }
        if (uit.isSubgraph(target, query)) {
            Assertions.assertTrue(whole(c, query), "substructure");
        }
    }

    /** The mappings found are the exact ones or, at the cap, 1000 of them. */
    private static void assertExact(Case c, Set<String> exact, Set<String> found) {
        if (exact.size() < CAP) {
            Assertions.assertEquals(exact, found, "exact mappings");
        } else {
            Assertions.assertTrue(exact.containsAll(found), "exact mappings");
            if (c.mappings > CAP) {
                Assertions.assertEquals(c.mappings, exact.size(), "exact mappings");
            }
        }
    }

    private static boolean whole(Case c, IAtomContainer mol) {
        return c.atoms == mol.getAtomCount() && c.bonds == mol.getBondCount();
    }

    /**
     * If every mapping maps the same atoms of the query, or with
     * {@code turned} of the target, and all bonds between them, the maximum
     * mappings are the embeddings of that part in the other molecule; returns
     * them, else null. At the cap only for a whole molecule, as the mappings
     * left out may map other atoms.
     */
    private static Set<String> embeddings(Case c, IAtomContainer query, IAtomContainer target, List<int[]> mappings,
                                          boolean turned) throws CloneNotSupportedException {
        IAtomContainer mol = turned ? target : query;
        Set<Integer> mapped = new TreeSet<>();
        for (int[] mapping : mappings) {
            for (int i = 0; i < mapping.length; i++) {
                if (mapping[i] >= 0) {
                    mapped.add(turned ? mapping[i] : i);
                }
            }
        }
        boolean whole = mapped.size() == mol.getAtomCount();
        if (mapped.size() != c.atoms || !whole && (mappings.size() >= CAP || mol instanceof IQueryAtomContainer)) {
            return null;
        }
        List<IAtom> atoms = new ArrayList<>();
        for (int i : mapped) {
            atoms.add(mol.getAtom(i));
        }
        // the atoms keep their order
        IAtomContainer part = whole ? mol : AtomContainerManipulator.extractSubstructure(mol, atoms);
        if (part.getBondCount() != c.bonds) {
            return null;
        }
        Integer[] index = mapped.toArray(new Integer[0]);
        Set<String> keys = new HashSet<>();
        for (int[] embedding : c.condition.pattern(part).matchAll(turned ? query : target)) {
            int[] mapping = new int[query.getAtomCount()];
            Arrays.fill(mapping, -1);
            for (int k = 0; k < embedding.length; k++) {
                if (turned) {
                    mapping[embedding[k]] = index[k];
                } else {
                    mapping[index[k]] = embedding[k];
                }
            }
            keys.add(Arrays.toString(mapping));
        }
        return keys;
    }

    private static int largestConnected(List<IAtomContainer> overlaps) {
        int largest = 0;
        for (IAtomContainer overlap : overlaps) {
            if (ConnectivityChecker.isConnected(overlap)) {
                largest = Math.max(largest, overlap.getAtomCount());
            }
        }
        return largest;
    }

    private static IAtomContainer query(Case c) throws CDKException {
        if (c.condition != SMARTS) {
            return prepare(c.pair.query, c.prep);
        }
        // a query container, as VentoFoggia and UniversalIsomorphismTester take only that as a query
        IAtomContainer query = new QueryAtomContainer(SilentChemObjectBuilder.getInstance());
        Assertions.assertTrue(Smarts.parse(query, c.pair.query), c.pair.query);
        return query;
    }

    private static IAtomContainer target(Case c) throws CDKException {
        return prepare(c.pair.target, c.prep);
    }

    @SuppressWarnings("deprecation") // the CDK legacy aromaticity of the SMSD tests
    private static IAtomContainer prepare(String smiles, Prep prep) throws CDKException {
        IAtomContainer mol = smi(smiles);
        if (prep == LEGACY) {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            Aromaticity.cdkLegacy().apply(mol);
        } else if (prep == DAYLIGHT) {
            new Aromaticity(ElectronDonation.daylight(), Cycles.or(Cycles.all(), Cycles.all(6))).apply(mol);
        } else if (prep == EXPLICIT_H) {
            AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        }
        Cycles.markRingAtomsAndBonds(mol);
        return mol;
    }

    private static Stream<Case> cases(Pair[] pairs) {
        return Arrays.stream(pairs).flatMap(pair -> Arrays.stream(pair.cases));
    }

    private static Pair pair(String name, String query, String target, Case... cases) {
        return new Pair(name, query, target, false, false, cases);
    }

    /** A pair of the overlap tests, also checked against UniversalIsomorphismTester. */
    private static Pair overlap(String name, String query, String target, Case... cases) {
        return new Pair(name, query, target, true, false, cases);
    }

    /** A pair too large to search again in another atom order. */
    private static Pair large(String name, String query, String target, Case... cases) {
        return new Pair(name, query, target, false, true, cases);
    }

    private static Case row(Condition condition, Prep prep, int atoms, int bonds, int mappings, Known known) {
        return new Case(condition, prep, atoms, bonds, mappings, known);
    }

    /** Two molecules, as SMILES or for SMARTS a query, and the searches of them. */
    private static final class Pair {

        private final String  name;
        private final String  query;
        private final String  target;
        private final boolean overlap;
        private final boolean large;
        private final Case[]  cases;

        private Pair(String name, String query, String target, boolean overlap, boolean large, Case[] cases) {
            this.name = name;
            this.query = query;
            this.target = target;
            this.overlap = overlap;
            this.large = large;
            this.cases = cases;
            for (Case c : cases) {
                c.pair = this;
            }
        }
    }

    /** A search of a pair and its expected result; 1000 mappings means at least 1000, more is the exact count. */
    private static final class Case {

        private final Condition condition;
        private final Prep      prep;
        private final int       atoms;
        private final int       bonds;
        private final int       mappings;
        private final Known     known;
        private Pair            pair;

        private Case(Condition condition, Prep prep, int atoms, int bonds, int mappings, Known known) {
            this.condition = condition;
            this.prep = prep;
            this.atoms = atoms;
            this.bonds = bonds;
            this.mappings = mappings;
            this.known = known;
        }

        private boolean bruteForce() {
            return known == BRUTE_FORCE;
        }

        /**
         * Whether the row is searched again in another atom order: without
         * explicit hydrogens, below the cap, and not large.
         */
        private boolean reordered() {
            return prep != EXPLICIT_H && mappings < CAP && !pair.large;
        }

        private boolean overlap() {
            return pair.overlap;
        }

        @Override
        public String toString() {
            return pair.name + ", " + condition + ", " + prep;
        }
    }
}
