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
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.smsd.algorithm.matchers.AtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.BondMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultBondMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultMCSPlusAtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultMatcher;
import org.openscience.cdk.smsd.helper.BinaryTree;

/**
 * Class to perform check/methods for McGregor class.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class McGregorChecks {

    /**
     *
     * @param source
     * @param target
     * @param neighborBondNumA
     * @param neighborBondNumB
     * @param iBondNeighborAtomsA
     * @param iBondNeighborAtomsB
     * @param cBondNeighborsA
     * @param cBondNeighborsB
     * @param shouldMatchBonds
     * @return
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

                if (isAtomMatch(g1A, g2a, g1B, g2B)) {
                    try {

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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return false;
    }

    protected static boolean isMatchFeasible(IAtomContainer ac1, IBond bondA1, IAtomContainer ac2, IBond bondA2,
            boolean shouldMatchBonds) {

        if (ac1 instanceof IQueryAtomContainer) {
            if (((IQueryBond) bondA1).matches(bondA2)) {
                IQueryAtom atom1 = (IQueryAtom) (bondA1.getBegin());
                IQueryAtom atom2 = (IQueryAtom) (bondA1.getEnd());
                // ok, bonds match
                if (atom1.matches(bondA2.getBegin()) && atom2.matches(bondA2.getEnd())
                        || atom1.matches(bondA2.getEnd()) && atom2.matches(bondA2.getBegin())) {
                    // ok, atoms match in either order
                    return true;
                }
                return false;
            }
            return false;
        } else {

            //Bond Matcher
            BondMatcher bondMatcher = new DefaultBondMatcher(ac1, bondA1, shouldMatchBonds);
            //Atom Matcher
            AtomMatcher atomMatcher1 = new DefaultMCSPlusAtomMatcher(ac1, bondA1.getBegin(), shouldMatchBonds);
            //Atom Matcher
            AtomMatcher atomMatcher2 = new DefaultMCSPlusAtomMatcher(ac1, bondA1.getEnd(), shouldMatchBonds);

            if (DefaultMatcher.isBondMatch(bondMatcher, ac2, bondA2, shouldMatchBonds)
                    && DefaultMatcher.isAtomMatch(atomMatcher1, atomMatcher2, ac2, bondA2, shouldMatchBonds)) {
                return true;
            }
            return false;
        }
    }

    /**
     *
     * @param mappedAtomsSize
     * @param atomFromOtherMolecule
     * @param molecule
     * @param mappedAtomsOrg
     * @return
     */
    protected static int searchCorrespondingAtom(int mappedAtomsSize, int atomFromOtherMolecule, int molecule,
            List<Integer> mappedAtomsOrg) {

        List<Integer> mappedAtoms = new ArrayList<Integer>(mappedAtomsOrg);

        int correspondingAtom = 0;
        for (int a = 0; a < mappedAtomsSize; a++) {
            if ((molecule == 1) && (mappedAtoms.get(a * 2 + 0).intValue() == atomFromOtherMolecule)) {
                correspondingAtom = mappedAtoms.get(a * 2 + 1);
            }
            if ((molecule == 2) && (mappedAtoms.get(a * 2 + 1).intValue() == atomFromOtherMolecule)) {
                correspondingAtom = mappedAtoms.get(a * 2 + 0);
            }
        }
        return correspondingAtom;
    }

    /**
     *
     * @param g1A
     * @param g2A
     * @param g1B
     * @param g2B
     * @return
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
     *
     * @param atomMapping
     * @return
     */
    protected static List<Integer> removeRecurringMappings(List<Integer> atomMapping) {

        boolean exist = true;
        List<Integer> tempMap = new ArrayList<Integer>();
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
     * The function is called in function partsearch. The function is given a temporary matrix and a position (row/column)
     * within this matrix. First the function sets all entries to zero, which can be exlcuded in respect to the current
     * atom by atom matching. After this the function replaces all entries in the same row and column of the current
     * position by zeros. Only the entry of the current position is set to one.
     * Return value "count_arcsleft" counts the number of arcs, which are still in the matrix.
     * @param row
     * @param column
     * @param marcs
     * @param mcGregorHelper
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
     *
     * @param bondNumber
     * @param cSet
     * @return
     */
    protected static List<String> generateCSetCopy(int bondNumber, List<String> cSet) {
        List<String> cTabCopy = new ArrayList<String>();
        for (int a = 0; a < bondNumber; a++) {
            cTabCopy.add(cSet.get(a * 4 + 0));
            cTabCopy.add(cSet.get(a * 4 + 1));
            cTabCopy.add("X");
            cTabCopy.add("X");
        }
        return cTabCopy;
    }

    /**
     *
     * @param atomContainer
     * @return
     * @throws IOException
     */
    protected static List<String> generateCTabCopy(IAtomContainer atomContainer) throws IOException {
        List<String> cTabCopy = new ArrayList<String>();
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
     *
     * @param g1Atom
     * @param g3Atom
     * @param g4Atom
     * @param rowAtom1
     * @param rowAtom2
     * @param columnAtom3
     * @param columnAtom4
     * @return
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
     *
     * @param g2Atom
     * @param g3Atom
     * @param g4Atom
     * @param rowAtom1
     * @param rowAtom2
     * @param columnAtom3
     * @param columnAtom4
     * @return
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
     *
     * @param g1Atom
     * @param g3Atom
     * @param g2Atom
     * @param rowAtom1
     * @param rowAtom2
     * @param columnAtom3
     * @param columnAtom4
     * @return
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
     *
     * @param g1Atom
     * @param g2Atom
     * @param g4Atom
     * @param rowAtom1
     * @param rowAtom2
     * @param columnAtom3
     * @param columnAtom4
     * @return
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
     *
     * @param g1Atom
     * @param g2Atom
     * @param g3Atom
     * @param g4Atom
     * @param rowAtom1
     * @param rowAtom2
     * @param columnAtom3
     * @param columnAtom4
     * @return
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
     *
     * @param source
     * @param target
     * @param neighborBondNumA
     * @param neighborBondNumB
     * @param iBondNeighborAtomsA
     * @param iBondNeighborAtomsB
     * @param cBondNeighborsA
     * @param cBondNeighborsB
     * @param modifiedARCS
     * @param shouldMatchBonds
     * @return
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

                if (McGregorChecks.isAtomMatch(g1A, g2A, g1B, g2B)) {

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
     *
     * @param tempmarcs
     * @param neighborBondNumA
     * @param neighborBondNumB
     * @return
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
     *
     * @param correspondingAtom
     * @param newSymbol
     * @param neighborBondNum
     * @param atomContainer
     * @param cBondNeighbors
     * @return
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
     *
     * @param correspondingAtom
     * @param newSymbol
     * @param neighborBondNum
     * @param iBondNeighbors
     * @param cBondNeighbors
     * @return
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

                if (isAtomMatch(g1A, g2A, g1B, g2B)) {
                    try {

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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return false;
    }

    static List<Integer> markUnMappedAtoms(boolean flag, IAtomContainer container, Map<Integer, Integer> presentMapping) {
        List<Integer> unmappedMolAtoms = new ArrayList<Integer>();

        int unmappedNum = 0;
        boolean atomIsUnmapped = true;

        for (int a = 0; a < container.getAtomCount(); a++) {
            //Atomic list are only numbers from 1 to atom_number1
            if (flag && presentMapping.containsKey(a)) {
                atomIsUnmapped = false;
            } else if (!flag && presentMapping.containsValue(a)) {
                atomIsUnmapped = false;
            }
            if (atomIsUnmapped) {
                unmappedMolAtoms.add(unmappedNum++, a);
            }
            atomIsUnmapped = true;
        }
        return unmappedMolAtoms;
    }

    static List<Integer> markUnMappedAtoms(boolean flag, IAtomContainer container, List<Integer> mappedAtoms,
            int cliqueSize) {
        List<Integer> unmappedMolAtoms = new ArrayList<Integer>();
        int unmappedNum = 0;
        boolean atomIsUnmapped = true;
        for (int a = 0; a < container.getAtomCount(); a++) {
            //Atomic list are only numbers from 1 to atom_number1
            for (int b = 0; b < cliqueSize; b += 2) {
                if (flag && mappedAtoms.get(b) == a) {
                    atomIsUnmapped = false;
                } else if (!flag && mappedAtoms.get(b + 1) == a) {
                    atomIsUnmapped = false;
                }
            }
            if (atomIsUnmapped) {
                unmappedMolAtoms.add(unmappedNum++, a);
            }
            atomIsUnmapped = true;
        }
        return unmappedMolAtoms;
    }
}
