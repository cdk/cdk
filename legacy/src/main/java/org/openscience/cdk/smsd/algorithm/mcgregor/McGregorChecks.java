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
package org.openscience.cdk.smsd.algorithm.mcgregor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smsd.algorithm.matchers.AtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.BondMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultBondMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultMCSPlusAtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultMatcher;
import org.openscience.cdk.smsd.helper.BinaryTree;

/**
 * Provides legacy bond-table and arc-matrix helpers for McGregor extension.
 * Index tables use endpoint/order triples, label tables use four entries per
 * bond, and atom mappings use flattened source/target pairs. Callers supply
 * aligned, non-null tables with valid sizes and indices; these low-level
 * helpers do not perform the public search entry-point validation.
 *
 * <p>Mapped identity labels constrain boundary correspondence before actual
 * atom/bond predicates are evaluated. String-label comparison alone is not a
 * chemical or stereochemical compatibility test. Methods that update tables
 * mutate the supplied lists and must not share those lists across searches.
 * Chemistry evaluation failures propagate to the caller; they are not treated
 * as incompatible atom or bond pairs.</p>
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class McGregorChecks {

    /**
     * Constructs the legacy McGregor helper facade.
     */
    public McGregorChecks() {
    }

    /**
     * Checks whether any boundary-bond pair has compatible labels and chemistry.
     * @param source source graph containing any directional predicates
     * @param target target graph
     * @param neighborBondNumA number of source boundary-bond records
     * @param neighborBondNumB number of target boundary-bond records
     * @param iBondNeighborAtomsA source endpoint/order triples
     * @param iBondNeighborAtomsB target endpoint/order triples
     * @param cBondNeighborsA source four-entry endpoint-label/backup records
     * @param cBondNeighborsB target four-entry endpoint-label/backup records
     * @param shouldMatchBonds whether strict ordinary bond chemistry is checked; query predicates always apply
     * @return whether a feasible boundary-bond pair exists
     * @throws RuntimeException if atom or bond predicate evaluation fails
     */
    protected static boolean isFurtherMappingPossible(IAtomContainer source, IAtomContainer target,
            int neighborBondNumA, int neighborBondNumB, List<Integer> iBondNeighborAtomsA,
            List<Integer> iBondNeighborAtomsB, List<String> cBondNeighborsA, List<String> cBondNeighborsB,
            boolean shouldMatchBonds) {

        for (int row = 0; row < neighborBondNumA; row++) {
            //            System.out.println("i " + row);
            String g1A = cBondNeighborsA.get(row * 4 + 0);
            String g2a = cBondNeighborsA.get(row * 4 + 1);

            for (int column = 0; column < neighborBondNumB; column++) {

                String g1B = cBondNeighborsB.get(column * 4 + 0);
                String g2B = cBondNeighborsB.get(column * 4 + 1);

                if (isLabelMatch(g1A, g2a, g1B, g2B)) {

                    int indexI = iBondNeighborAtomsA.get(row * 3 + 0);
                    int indexIPlus1 = iBondNeighborAtomsA.get(row * 3 + 1);

                    int indexJ = iBondNeighborAtomsB.get(column * 3 + 0);
                    int indexJPlus1 = iBondNeighborAtomsB.get(column * 3 + 1);

                    IAtom r1A = source.getAtom(indexI);
                    IAtom r2A = source.getAtom(indexIPlus1);
                    IBond reactantBond = source.getBond(r1A, r2A);

                    IAtom p1B = target.getAtom(indexJ);
                    IAtom p2B = target.getAtom(indexJPlus1);
                    IBond productBond = target.getBond(p1B, p2B);

                    if (isMatchFeasible(source, reactantBond, target, productBond, shouldMatchBonds)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks source/target bond compatibility and either endpoint orientation.
     *
     * A seeded extension must also verify the selected endpoint orientation against its existing atom pairs.
     * @param ac1 source graph
     * @param bondA1 source bond or query predicate
     * @param ac2 target graph
     * @param bondA2 target bond
     * @param shouldMatchBonds whether strict ordinary bond chemistry is checked; query predicates always apply
     * @return whether the bond and endpoint matchers accept either orientation; false for null bonds
     * @throws RuntimeException if atom or bond predicate evaluation fails
     */
    protected static boolean isMatchFeasible(IAtomContainer ac1, IBond bondA1, IAtomContainer ac2, IBond bondA2,
            boolean shouldMatchBonds) {

        if (bondA1 == null || bondA2 == null) return false;
        BondMatcher bondMatcher = new DefaultBondMatcher(ac1, bondA1, shouldMatchBonds);
        AtomMatcher first = new DefaultMCSPlusAtomMatcher(ac1, bondA1.getBegin(), shouldMatchBonds);
        AtomMatcher second = new DefaultMCSPlusAtomMatcher(ac1, bondA1.getEnd(), shouldMatchBonds);
        return DefaultMatcher.isBondMatch(bondMatcher, ac2, bondA2, shouldMatchBonds)
                && DefaultMatcher.isAtomMatch(first, second, ac2, bondA2, shouldMatchBonds);
    }

    // Labels preserve mapped endpoint identity. Unmapped chemistry must be
    // checked by the actual atom predicates, not their optional display symbols.
    static boolean isLabelMatch(String a, String b, String c, String d) {
        return sameEndpoint(a, c) && sameEndpoint(b, d)
                || sameEndpoint(a, d) && sameEndpoint(b, c);
    }

    private static boolean sameEndpoint(String query, String target) {
        boolean queryMapped = query != null && query.startsWith("$");
        boolean targetMapped = target != null && target.startsWith("$");
        return queryMapped || targetMapped ? queryMapped && query.equals(target) : true;
    }

    /**
     * Finds the mapped counterpart of an atom index.
     * @param mappedAtomsSize number of source/target atom pairs
     * @param atomFromOtherMolecule index whose counterpart is requested
     * @param molecule 1 to search source indices, or 2 to search target indices
     * @param mappedAtomsOrg flattened source/target index pairs
     * @return the last matching counterpart, or legacy value zero when no pair matches
     */
    protected static int searchCorrespondingAtom(int mappedAtomsSize, int atomFromOtherMolecule, int molecule,
            List<Integer> mappedAtomsOrg) {

        List<Integer> mappedAtoms = mappedAtomsOrg;

        int correspondingAtom = 0;
        for (int a = 0; a < mappedAtomsSize; a++) {
            if ((molecule == 1) && (mappedAtoms.get(a * 2 + 0) == atomFromOtherMolecule)) {
                correspondingAtom = mappedAtoms.get(a * 2 + 1);
            }
            if ((molecule == 2) && (mappedAtoms.get(a * 2 + 1) == atomFromOtherMolecule)) {
                correspondingAtom = mappedAtoms.get(a * 2 + 0);
            }
        }
        return correspondingAtom;
    }

    /**
     * Compares two endpoint-label pairs case-insensitively in either orientation.
     *
     * This legacy string comparison does not replace atom query predicates or element matching.
     * @param g1A first source endpoint label
     * @param g2A second source endpoint label
     * @param g1B first target endpoint label
     * @param g2B second target endpoint label
     * @return whether the supplied string pairs agree in either orientation
     */
    protected static boolean isAtomMatch(String g1A, String g2A, String g1B, String g2B) {
        if ((g1A.compareToIgnoreCase(g1B) == 0 && g2A.compareToIgnoreCase(g2B) == 0)
                || (g1A.compareToIgnoreCase(g2B) == 0 && g2A.compareToIgnoreCase(g1B) == 0)) {
            return true;
        }
        return false;
    }

    /*
     * Modified function call by ASAD in Java have to check
     */
    /**
     * Recursively visits both branches of an acyclic legacy search tree.
     *
     * This method does not detach caller-owned tree links; recursion requires an acyclic tree and can exhaust the stack on deep inputs.
     * @param curStruc root of the acyclic branch tree to visit
     * @return legacy status value zero
     */
    protected static int removeTreeStructure(BinaryTree curStruc) {

        BinaryTree equalStruc = curStruc.getEqual();
        BinaryTree notEqualStruc = curStruc.getNotEqual();
        curStruc = null;

        if (equalStruc != null) {
            removeTreeStructure(equalStruc);
        }

        if (notEqualStruc != null) {
            removeTreeStructure(notEqualStruc);
        }

        return 0;
    }

    //Function compaires a structure array with itself. Sometimes a mapping occurs several times within the array.
    //The function eliminates these recurring mappings. Function is called in function best_solution.
    //The function is called by itself as long as the last list element is processed.
    /**
     * Copies atom pairs while retaining only the last pair for each source index.
     * @param atomMapping flattened source/target atom pairs
     * @return a new flattened list with repeated source indices removed
     */
    protected static List<Integer> removeRecurringMappings(List<Integer> atomMapping) {

        boolean exist = true;
        List<Integer> tempMap = new ArrayList<>();
        int tempCounter = 0;
        int atomMappingSize = atomMapping.size();
        for (int x = 0; x < atomMappingSize; x += 2) {
            int atom = atomMapping.get(x);
            for (int y = x + 2; y < atomMappingSize; y += 2) {
                if (atom == atomMapping.get(y)) {
                    exist = false;
                }
            }
            if (exist == true) {
                tempMap.add(atomMapping.get(x + 0));
                tempMap.add(atomMapping.get(x + 1));
                tempCounter += 2;
            }

            exist = true;
        }

        return tempMap;
    }

    /**
     * Zeros conflicting matrix entries and retains the selected arc.
     * @param row selected source boundary-bond row
     * @param column selected target boundary-bond column
     * @param marcs mutable row-major arc matrix
     * @param mcGregorHelper aligned source/target boundary tables and dimensions
     */
    protected static void removeRedundantArcs(int row, int column, List<Integer> marcs, McgregorHelper mcGregorHelper) {
        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();
        List<Integer> iBondNeighborAtomsA = mcGregorHelper.getiBondNeighborAtomsA();
        List<Integer> iBondNeighborAtomsB = mcGregorHelper.getiBondNeighborAtomsB();
        int g1Atom = iBondNeighborAtomsA.get(row * 3 + 0);
        int g2Atom = iBondNeighborAtomsA.get(row * 3 + 1);
        int g3Atom = iBondNeighborAtomsB.get(column * 3 + 0);
        int g4Atom = iBondNeighborAtomsB.get(column * 3 + 1);

        for (int x = 0; x < neighborBondNumA; x++) {
            int rowAtom1 = iBondNeighborAtomsA.get(x * 3 + 0);
            int rowAtom2 = iBondNeighborAtomsA.get(x * 3 + 1);

            for (int y = 0; y < neighborBondNumB; y++) {
                int columnAtom3 = iBondNeighborAtomsB.get(y * 3 + 0);
                int columnAtom4 = iBondNeighborAtomsB.get(y * 3 + 1);

                if (McGregorChecks.cases(g1Atom, g2Atom, g3Atom, g4Atom, rowAtom1, rowAtom2, columnAtom3,
                        columnAtom4)) {
                    marcs.set(x * neighborBondNumB + y, 0);
                }

            }
        }

        for (int v = 0; v < neighborBondNumA; v++) {
            marcs.set(v * neighborBondNumB + column, 0);
        }

        for (int w = 0; w < neighborBondNumB; w++) {
            marcs.set(row * neighborBondNumB + w, 0);
        }

        marcs.set(row * neighborBondNumB + column, 1);
    }

    /**
     * Copies endpoint labels and resets both backup labels to X.
     * @param bondNumber number of bond records to copy
     * @param cSet four-entry endpoint-label/backup records
     * @return a new four-entry label record for each requested bond
     */
    protected static List<String> generateCSetCopy(int bondNumber, List<String> cSet) {
        List<String> cTabCopy = new ArrayList<>();
        for (int a = 0; a < bondNumber; a++) {
            cTabCopy.add(cSet.get(a * 4 + 0));
            cTabCopy.add(cSet.get(a * 4 + 1));
            cTabCopy.add("X");
            cTabCopy.add("X");
        }
        return cTabCopy;
    }

    /**
     * Creates bond label records from the graph endpoint symbols.
     * @param atomContainer simple molecular graph whose endpoints supply labels
     * @return four-entry endpoint-label/backup records, with backups initially X
     * @throws IOException retained for compatibility with legacy bond-table preparation
     */
    protected static List<String> generateCTabCopy(IAtomContainer atomContainer) throws IOException {
        List<String> cTabCopy = new ArrayList<>();
        for (int a = 0; a < atomContainer.getBondCount(); a++) {
            String atomI = atomContainer.getBond(a).getBegin().getSymbol();
            String atomJ = atomContainer.getBond(a).getEnd().getSymbol();
            cTabCopy.add(atomI);
            cTabCopy.add(atomJ);
            cTabCopy.add("X");
            cTabCopy.add("X");
        }
        return cTabCopy;
    }

    /**
     * Checks whether a shared first source endpoint lacks a shared target endpoint.
     * @param g1Atom first selected source endpoint
     * @param g3Atom first selected target endpoint
     * @param g4Atom second selected target endpoint
     * @param rowAtom1 first candidate source endpoint
     * @param rowAtom2 second candidate source endpoint
     * @param columnAtom3 first candidate target endpoint
     * @param columnAtom4 second candidate target endpoint
     * @return whether the candidate arc conflicts with the selected first source endpoint
     */
    protected static boolean case1(int g1Atom, int g3Atom, int g4Atom, int rowAtom1, int rowAtom2,
            int columnAtom3, int columnAtom4) {
        if (((g1Atom == rowAtom1) || (g1Atom == rowAtom2))
                && (!(((columnAtom3 == g3Atom) || (columnAtom4 == g3Atom)) || ((columnAtom3 == g4Atom) || (columnAtom4 == g4Atom))))) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether a shared second source endpoint lacks a shared target endpoint.
     * @param g2Atom second selected source endpoint
     * @param g3Atom first selected target endpoint
     * @param g4Atom second selected target endpoint
     * @param rowAtom1 first candidate source endpoint
     * @param rowAtom2 second candidate source endpoint
     * @param columnAtom3 first candidate target endpoint
     * @param columnAtom4 second candidate target endpoint
     * @return whether the candidate arc conflicts with the selected second source endpoint
     */
    protected static boolean case2(int g2Atom, int g3Atom, int g4Atom, int rowAtom1, int rowAtom2,
            int columnAtom3, int columnAtom4) {
        if (((g2Atom == rowAtom1) || (g2Atom == rowAtom2))
                && (!(((columnAtom3 == g3Atom) || (columnAtom4 == g3Atom)) || ((columnAtom3 == g4Atom) || (columnAtom4 == g4Atom))))) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether a shared first target endpoint lacks a shared source endpoint.
     * @param g1Atom first selected source endpoint
     * @param g3Atom first selected target endpoint
     * @param g2Atom second selected source endpoint
     * @param rowAtom1 first candidate source endpoint
     * @param rowAtom2 second candidate source endpoint
     * @param columnAtom3 first candidate target endpoint
     * @param columnAtom4 second candidate target endpoint
     * @return whether the candidate arc conflicts with the selected first target endpoint
     */
    protected static boolean case3(int g1Atom, int g3Atom, int g2Atom, int rowAtom1, int rowAtom2,
            int columnAtom3, int columnAtom4) {
        if (((g3Atom == columnAtom3) || (g3Atom == columnAtom4))
                && (!(((rowAtom1 == g1Atom) || (rowAtom2 == g1Atom)) || ((rowAtom1 == g2Atom) || (rowAtom2 == g2Atom))))) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether a shared second target endpoint lacks a shared source endpoint.
     * @param g1Atom first selected source endpoint
     * @param g2Atom second selected source endpoint
     * @param g4Atom second selected target endpoint
     * @param rowAtom1 first candidate source endpoint
     * @param rowAtom2 second candidate source endpoint
     * @param columnAtom3 first candidate target endpoint
     * @param columnAtom4 second candidate target endpoint
     * @return whether the candidate arc conflicts with the selected second target endpoint
     */
    protected static boolean case4(int g1Atom, int g2Atom, int g4Atom, int rowAtom1, int rowAtom2,
            int columnAtom3, int columnAtom4) {
        if (((g4Atom == columnAtom3) || (g4Atom == columnAtom4))
                && (!(((rowAtom1 == g1Atom) || (rowAtom2 == g1Atom)) || ((rowAtom1 == g2Atom) || (rowAtom2 == g2Atom))))) {
            return true;
        }
        return false;
    }

    /**
     * Checks all four endpoint-consistency conflicts for two candidate arcs.
     * @param g1Atom first selected source endpoint
     * @param g2Atom second selected source endpoint
     * @param g3Atom first selected target endpoint
     * @param g4Atom second selected target endpoint
     * @param rowAtom1 first candidate source endpoint
     * @param rowAtom2 second candidate source endpoint
     * @param columnAtom3 first candidate target endpoint
     * @param columnAtom4 second candidate target endpoint
     * @return whether either source or target endpoint correspondence conflicts
     */
    protected static boolean cases(int g1Atom, int g2Atom, int g3Atom, int g4Atom, int rowAtom1, int rowAtom2,
            int columnAtom3, int columnAtom4) {
        if (case1(g1Atom, g3Atom, g4Atom, rowAtom1, rowAtom2, columnAtom3, columnAtom4)
                || case2(g2Atom, g3Atom, g4Atom, rowAtom1, rowAtom2, columnAtom3, columnAtom4)
                || case3(g1Atom, g3Atom, g2Atom, rowAtom1, rowAtom2, columnAtom3, columnAtom4)
                || case4(g1Atom, g2Atom, g4Atom, rowAtom1, rowAtom2, columnAtom3, columnAtom4)) {
            return true;
        }
        return false;
    }

    /**
     * Marks compatible boundary-bond pairs in the supplied arc matrix.
     * @param source source graph containing any directional predicates
     * @param target target graph
     * @param neighborBondNumA number of source boundary-bond records
     * @param neighborBondNumB number of target boundary-bond records
     * @param iBondNeighborAtomsA source endpoint/order triples
     * @param iBondNeighborAtomsB target endpoint/order triples
     * @param cBondNeighborsA source four-entry endpoint-label/backup records
     * @param cBondNeighborsB target four-entry endpoint-label/backup records
     * @param modifiedARCS mutable row-major arc matrix updated in place
     * @param shouldMatchBonds whether strict ordinary bond chemistry is checked; query predicates always apply
     * @return the supplied matrix after feasible entries have been set to one
     */
    protected static List<Integer> setArcs(IAtomContainer source, IAtomContainer target, int neighborBondNumA,
            int neighborBondNumB, List<Integer> iBondNeighborAtomsA, List<Integer> iBondNeighborAtomsB,
            List<String> cBondNeighborsA, List<String> cBondNeighborsB, List<Integer> modifiedARCS,
            boolean shouldMatchBonds) {

        for (int row = 0; row < neighborBondNumA; row++) {
            for (int column = 0; column < neighborBondNumB; column++) {

                String g1A = cBondNeighborsA.get(row * 4 + 0);
                String g2A = cBondNeighborsA.get(row * 4 + 1);
                String g1B = cBondNeighborsB.get(column * 4 + 0);
                String g2B = cBondNeighborsB.get(column * 4 + 1);

                if (isLabelMatch(g1A, g2A, g1B, g2B)) {

                    int indexI = iBondNeighborAtomsA.get(row * 3 + 0);
                    int indexIPlus1 = iBondNeighborAtomsA.get(row * 3 + 1);

                    IAtom r1A = source.getAtom(indexI);
                    IAtom r2A = source.getAtom(indexIPlus1);
                    IBond reactantBond = source.getBond(r1A, r2A);

                    int indexJ = iBondNeighborAtomsB.get(column * 3 + 0);
                    int indexJPlus1 = iBondNeighborAtomsB.get(column * 3 + 1);

                    IAtom p1B = target.getAtom(indexJ);
                    IAtom p2B = target.getAtom(indexJPlus1);
                    IBond productBond = target.getBond(p1B, p2B);
                    if (isMatchFeasible(source, reactantBond, target, productBond, shouldMatchBonds)) {
                        modifiedARCS.set(row * neighborBondNumB + column, 1);
                    }
                }
            }
        }
        return modifiedARCS;
    }

    /**
     * Counts entries equal to one in the requested matrix dimensions.
     * @param tempmarcs row-major arc matrix
     * @param neighborBondNumA number of source boundary-bond records
     * @param neighborBondNumB number of target boundary-bond records
     * @return number of remaining feasible arcs
     */
    protected static int countArcsLeft(List<Integer> tempmarcs, int neighborBondNumA, int neighborBondNumB) {
        int arcsleft = 0;

        for (int a = 0; a < neighborBondNumA; a++) {
            for (int b = 0; b < neighborBondNumB; b++) {

                if (tempmarcs.get(a * neighborBondNumB + b) == (1)) {
                    arcsleft++;
                }
            }
        }
        return arcsleft;
    }

    /**
     * Relabels an atom in bond records and preserves its original endpoint labels.
     * @param correspondingAtom atom index to relabel
     * @param newSymbol unique mapped-atom identity label, normally beginning with $
     * @param neighborBondNum number of bond records to inspect
     * @param atomContainer simple molecular graph whose endpoints supply labels
     * @param cBondNeighbors mutable four-entry endpoint-label/backup records
     * @return legacy status value zero
     */
    protected static int changeCharBonds(int correspondingAtom, String newSymbol, int neighborBondNum,
            IAtomContainer atomContainer, List<String> cBondNeighbors) {
        for (int atomIndex = 0; atomIndex < neighborBondNum; atomIndex++) {
            IBond bond = atomContainer.getBond(atomIndex);
            if ((atomContainer.indexOf(bond.getBegin()) == correspondingAtom)
                    && (cBondNeighbors.get(atomIndex * 4 + 2).compareToIgnoreCase("X") == 0)) {
                cBondNeighbors.set(atomIndex * 4 + 2, cBondNeighbors.get(atomIndex * 4 + 0));
                cBondNeighbors.set(atomIndex * 4 + 0, newSymbol);
            }

            if ((atomContainer.indexOf(bond.getEnd()) == correspondingAtom)
                    && (cBondNeighbors.get(atomIndex * 4 + 3).compareToIgnoreCase("X") == 0)) {
                cBondNeighbors.set(atomIndex * 4 + 3, cBondNeighbors.get(atomIndex * 4 + 1));
                cBondNeighbors.set(atomIndex * 4 + 1, newSymbol);
            }

        }

        return 0;
    }

    /**
     * Relabels an atom in bond records and preserves its original endpoint labels.
     * @param correspondingAtom atom index to relabel
     * @param newSymbol unique mapped-atom identity label, normally beginning with $
     * @param neighborBondNum number of bond records to inspect
     * @param iBondNeighbors endpoint/order triples aligned with the label records
     * @param cBondNeighbors mutable four-entry endpoint-label/backup records
     * @return legacy status value zero
     */
    protected static int changeCharBonds(int correspondingAtom, String newSymbol, int neighborBondNum,
            List<Integer> iBondNeighbors, List<String> cBondNeighbors) {

        for (int atomIndex = 0; atomIndex < neighborBondNum; atomIndex++) {
            if ((iBondNeighbors.get(atomIndex * 3 + 0) == (correspondingAtom))
                    && (cBondNeighbors.get(atomIndex * 4 + 2).compareToIgnoreCase("X") == 0)) {
                cBondNeighbors.set(atomIndex * 4 + 2, cBondNeighbors.get(atomIndex * 4 + 0));
                cBondNeighbors.set(atomIndex * 4 + 0, newSymbol);
            }

            if ((iBondNeighbors.get(atomIndex * 3 + 1) == (correspondingAtom))
                    && (cBondNeighbors.get(atomIndex * 4 + 3).compareToIgnoreCase("X") == 0)) {
                cBondNeighbors.set(atomIndex * 4 + 3, cBondNeighbors.get(atomIndex * 4 + 1));
                cBondNeighbors.set(atomIndex * 4 + 1, newSymbol);
            }

        }

        return 0;
    }

    static boolean isFurtherMappingPossible(IAtomContainer source, IAtomContainer target,
            McgregorHelper mcGregorHelper, boolean shouldMatchBonds) {

        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();
        List<Integer> iBondNeighborAtomsA = mcGregorHelper.getiBondNeighborAtomsA();
        List<Integer> iBondNeighborAtomsB = mcGregorHelper.getiBondNeighborAtomsB();
        List<String> cBondNeighborsA = mcGregorHelper.getcBondNeighborsA();
        List<String> cBondNeighborsB = mcGregorHelper.getcBondNeighborsB();

        for (int row = 0; row < neighborBondNumA; row++) {
            //            System.out.println("i " + row);
            String g1A = cBondNeighborsA.get(row * 4 + 0);
            String g2A = cBondNeighborsA.get(row * 4 + 1);

            for (int column = 0; column < neighborBondNumB; column++) {

                String g1B = cBondNeighborsB.get(column * 4 + 0);
                String g2B = cBondNeighborsB.get(column * 4 + 1);

                if (isLabelMatch(g1A, g2A, g1B, g2B)) {

                    int indexI = iBondNeighborAtomsA.get(row * 3 + 0);
                    int indexIPlus1 = iBondNeighborAtomsA.get(row * 3 + 1);

                    int indexJ = iBondNeighborAtomsB.get(column * 3 + 0);
                    int indexJPlus1 = iBondNeighborAtomsB.get(column * 3 + 1);

                    IAtom r1A = source.getAtom(indexI);
                    IAtom r2A = source.getAtom(indexIPlus1);
                    IBond reactantBond = source.getBond(r1A, r2A);

                    IAtom p1B = target.getAtom(indexJ);
                    IAtom p2B = target.getAtom(indexJPlus1);
                    IBond productBond = target.getBond(p1B, p2B);

                    if (isMatchFeasible(source, reactantBond, target, productBond, shouldMatchBonds)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    static List<Integer> markUnMappedAtoms(boolean flag, IAtomContainer container, Map<Integer, Integer> presentMapping) {
        boolean[] mapped = new boolean[container.getAtomCount()];
        for (Integer atom : flag ? presentMapping.keySet() : presentMapping.values()) {
            if (atom >= 0 && atom < mapped.length) mapped[atom] = true;
        }
        return unmappedAtoms(mapped);
    }

    static List<Integer> markUnMappedAtoms(boolean flag, IAtomContainer container, List<Integer> mappedAtoms,
            int cliqueSize) {
        boolean[] mapped = new boolean[container.getAtomCount()];
        int offset = flag ? 0 : 1;
        for (int i = 0; i < cliqueSize; i++) {
            int atom = mappedAtoms.get(i * 2 + offset);
            if (atom >= 0 && atom < mapped.length) mapped[atom] = true;
        }
        return unmappedAtoms(mapped);
    }

    private static List<Integer> unmappedAtoms(boolean[] mapped) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < mapped.length; i++) {
            if (!mapped[i]) result.add(i);
        }
        return result;
    }
}
