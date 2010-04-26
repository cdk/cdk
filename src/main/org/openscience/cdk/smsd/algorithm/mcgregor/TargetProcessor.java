
/* Copyright (C) 2005-2006  Markus Leber
 *               2006-2009  Syed Asad Rahman {asad@ebi.ac.uk}
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
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

import java.util.List;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Class to handle mappings of target molecule based on the query.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.algorithm.mcgregor.TargetProcessorTest")
public class TargetProcessor {

    private List<String> cTab1Copy;
    private List<String> cTab2Copy;
    private String[] signArray;
    //number of remaining molecule A bonds after the clique search, which are
    //neighbors of the MCS
    private int neighborBondNumB = 0;
    //number of remaining molecule A bonds after the clique search, which aren't
    //neighbors
    private int setBondNumB = 0;
    private List<Integer> iBondNeighborsB;
    private List<String> cBondNeighborsB;
    private int newNeighborNumA;
    private List<Integer> newINeighborsA;
    private List<String> newCNeighborsA;

    /**
     *
     * @param cTab1Copy
     * @param cTab2Copy
     * @param signArray
     * @param neighbor_bondnum_B
     * @param set_bondnum_B
     * @param iBondNeighborsB
     * @param cBondNeighborsB
     * @param newNeighborNumA
     * @param newINeighborsA
     * @param newCNeighborsA
     */
    protected TargetProcessor(
            List<String> cTab1Copy,
            List<String> cTab2Copy,
            String[] signArray,
            int neighbor_bondnum_B,
            int set_bondnum_B,
            List<Integer> iBondNeighborsB,
            List<String> cBondNeighborsB,
            int newNeighborNumA,
            List<Integer> newINeighborsA,
            List<String> newCNeighborsA) {

        this.cTab1Copy = cTab1Copy;
        this.cTab2Copy = cTab2Copy;
        this.signArray = signArray;
        this.neighborBondNumB = neighbor_bondnum_B;
        this.setBondNumB = set_bondnum_B;
        this.iBondNeighborsB = iBondNeighborsB;
        this.cBondNeighborsB = cBondNeighborsB;
        this.newNeighborNumA = newNeighborNumA;
        this.newCNeighborsA = newCNeighborsA;
        this.newINeighborsA = newINeighborsA;
    }

    protected void process(
            IAtomContainer target,
            List<Integer> unmapped_atoms_molB,
            int mappingSize,
            List<Integer> i_bond_setB,
            List<String> c_bond_setB,
            List<Integer> mapped_atoms,
            int counter) {


        int unmapped_numB = unmapped_atoms_molB.size();
        boolean bond_considered = false;
        boolean normal_bond = true;


        for (int atomIndex = 0; atomIndex < target.getBondCount(); atomIndex++) {

            Integer indexI = target.getAtomNumber(target.getBond(atomIndex).getAtom(0));
            Integer indexJ = target.getAtomNumber(target.getBond(atomIndex).getAtom(1));
            Integer order = target.getBond(atomIndex).getOrder().ordinal() + 1;

            for (int b = 0; b < unmapped_numB; b++) {
                if (unmapped_atoms_molB.get(b).equals(indexI)) {
                    normal_bond = unMappedAtomsEqualsIndexI(target, mappingSize, atomIndex, counter, mapped_atoms, indexI, indexJ, order);
                    bond_considered = true;
                } else if (unmapped_atoms_molB.get(b) == indexJ) {
                    normal_bond = unMappedAtomsEqualsIndexJ(target, mappingSize, atomIndex, counter, mapped_atoms, indexI, indexJ, order);
                    bond_considered = true;
                }

                if (normal_bond && bond_considered) {
                    markNormalBonds(atomIndex, i_bond_setB, c_bond_setB, indexI, indexJ, order);
                    normal_bond = true;
                    break;
                }

            }
            bond_considered = false;
        }

    }

    /**
     *
     * @param setNumB
     * @param unmapped_atoms_molB
     * @param newMapingSize
     * @param i_bond_setB
     * @param c_bond_setB
     * @param new_Mapping
     * @param counter
     * @param new_i_bond_setB
     * @param new_c_bond_setB
     */
    protected void process(
            int setNumB,
            List<Integer> unmapped_atoms_molB,
            int newMapingSize,
            List<Integer> i_bond_setB,
            List<String> c_bond_setB,
            List<Integer> new_Mapping,
            int counter,
            List<Integer> new_i_bond_setB,
            List<String> new_c_bond_setB) {

        //The special signs must be transfered to the corresponding atoms of molecule A

        boolean bond_considered = false;
        boolean normal_bond = true;
        for (int atomIndex = 0; atomIndex < setNumB; atomIndex++) {

            Integer indexI = i_bond_setB.get(atomIndex * 3 + 0);
            Integer indexJ = i_bond_setB.get(atomIndex * 3 + 1);
            Integer order = i_bond_setB.get(atomIndex * 3 + 2);

            for (Integer unMappedAtomIndex : unmapped_atoms_molB) {
                if (unMappedAtomIndex.equals(indexI)) {
                    normal_bond = unMappedAtomsEqualsIndexI(setNumB, i_bond_setB, newMapingSize,
                            atomIndex, counter, new_Mapping, indexI, indexJ, order);
                    bond_considered = true;
                } else if (unMappedAtomIndex.equals(indexJ)) {
                    normal_bond = unMappedAtomsEqualsIndexJ(setNumB, i_bond_setB, newMapingSize,
                            atomIndex, counter, new_Mapping, indexI, indexJ, order);
                    bond_considered = true;
                }
                if (normal_bond && bond_considered) {
                    markNormalBonds(atomIndex, new_i_bond_setB, new_c_bond_setB, indexI, indexJ, order);
                    normal_bond = true;
                    break;
                }

            }
            bond_considered = false;
        }
    }

    private boolean unMappedAtomsEqualsIndexI(
            IAtomContainer target,
            int mappingSize,
            int atomIndex,
            int counter,
            List<Integer> mapped_atoms,
            Integer indexI,
            Integer indexJ,
            Integer order) {
        boolean normal_bond = true;
        for (int c = 0; c < mappingSize; c++) {
            if (mapped_atoms.get(c * 2 + 1).equals(indexJ)) {
                setBondNeighbors(indexI, indexJ, order);
                if (cTab2Copy.get(atomIndex * 4 + 3).compareToIgnoreCase("X") == 0) {
                    step1(atomIndex, counter);
                    McGregorChecks.changeCharBonds(indexJ, signArray[counter], target.getBondCount(),
                            target, cTab2Copy);
                    int cor_atom = McGregorChecks.searchCorrespondingAtom(mappingSize, indexJ, 2, mapped_atoms);
                    //Commented by Asad
                    McGregorChecks.changeCharBonds(cor_atom, signArray[counter], newNeighborNumA,
                            newINeighborsA, newCNeighborsA);
//                                changeCharBonds(cor_atom, signArray[counter], query.getBondCount(), query, cTab1Copy);
                    counter++;
                } else {
                    step2(atomIndex);
                }
                normal_bond = false;
                neighborBondNumB++;
            }
        }
        return normal_bond;
    }

    private boolean unMappedAtomsEqualsIndexJ(
            IAtomContainer target,
            int mappingSize,
            int atomIndex,
            int counter, List<Integer> mapped_atoms,
            Integer indexI,
            Integer indexJ,
            Integer order) {
        boolean normal_bond = true;
        for (int c = 0; c < mappingSize; c++) {
            if (mapped_atoms.get(c * 2 + 1).equals(indexI)) {
                setBondNeighbors(indexI, indexJ, order);
                if (cTab2Copy.get(atomIndex * 4 + 2).compareToIgnoreCase("X") == 0) {
                    step3(atomIndex, counter);
                    McGregorChecks.changeCharBonds(indexI, signArray[counter], target.getBondCount(),
                            target, cTab2Copy);
                    int cor_atom = McGregorChecks.searchCorrespondingAtom(mappingSize, indexI, 2, mapped_atoms);
                    McGregorChecks.changeCharBonds(cor_atom, signArray[counter], newNeighborNumA,
                            newINeighborsA, newCNeighborsA);
//                                changeCharBonds(cor_atom, signArray[counter], query.getBondCount(), query, cTab1Copy);
                    counter++;
                } else {
                    step4(atomIndex);
                }
                normal_bond = false;
                neighborBondNumB++;
            }
        }

        return normal_bond;
    }

    private boolean unMappedAtomsEqualsIndexI(
            int setNumB,
            List<Integer> i_bond_setB,
            int newMappingSize,
            int atomIndex,
            int counter,
            List<Integer> new_Mapping,
            Integer indexI,
            Integer indexJ,
            Integer order) {
        boolean normal_bond = true;
        for (int c = 0; c < newMappingSize; c++) {
            if (new_Mapping.get(c * 2 + 1).equals(indexJ)) {
                setBondNeighbors(indexI, indexJ, order);
                if (cTab2Copy.get(atomIndex * 4 + 3).compareToIgnoreCase("X") == 0) {
                    step1(atomIndex, counter);
                    McGregorChecks.changeCharBonds(indexJ, signArray[counter], setNumB, i_bond_setB, cTab2Copy);
                    int cor_atom = McGregorChecks.searchCorrespondingAtom(newMappingSize, indexJ, 2, new_Mapping);
                    McGregorChecks.changeCharBonds(cor_atom, signArray[counter], newNeighborNumA,
                            newINeighborsA, newCNeighborsA);
                    counter++;

                } else {
                    step2(atomIndex);
                }

                normal_bond = false;
                neighborBondNumB++;

            }
        }
        return normal_bond;
    }

    private boolean unMappedAtomsEqualsIndexJ(
            int setNumB,
            List<Integer> i_bond_setB,
            int newMappingSize,
            int atomIndex,
            int counter, List<Integer> new_Mapping,
            Integer indexI,
            Integer indexJ,
            Integer order) {
        boolean normal_bond = true;
        for (int c = 0; c < newMappingSize; c++) {
            if (new_Mapping.get(c * 2 + 1).equals(indexI)) {
                setBondNeighbors(indexI, indexJ, order);

                if (cTab2Copy.get(atomIndex * 4 + 2).compareToIgnoreCase("X") == 0) {

                    step3(atomIndex, counter);
                    McGregorChecks.changeCharBonds(indexI, signArray[counter], setNumB, i_bond_setB, cTab2Copy);
                    int cor_atom = McGregorChecks.searchCorrespondingAtom(newMappingSize, indexI, 2, new_Mapping);
                    McGregorChecks.changeCharBonds(cor_atom, signArray[counter], newNeighborNumA,
                            newINeighborsA, newCNeighborsA);
                    counter++;
                } else {
                    step4(atomIndex);
                }

                normal_bond = false;
                neighborBondNumB++;

            }
        }

        return normal_bond;
    }

    private void markNormalBonds(
            int atomIndex,
            List<Integer> i_bond_setB,
            List<String> c_bond_setB,
            Integer indexI,
            Integer indexJ,
            Integer order) {
        i_bond_setB.add(indexI);
        i_bond_setB.add(indexJ);
        i_bond_setB.add(order);
        c_bond_setB.add(cTab2Copy.get(atomIndex * 4 + 0));
        c_bond_setB.add(cTab2Copy.get(atomIndex * 4 + 1));
        c_bond_setB.add("X");
        c_bond_setB.add("X");
        setBondNumB++;
    }

    private void setBondNeighbors(Integer indexI,
            Integer indexJ,
            Integer order) {
        iBondNeighborsB.add(indexI);
        iBondNeighborsB.add(indexJ);
        iBondNeighborsB.add(order);
    }

    private void step1(int atomIndex, int counter) {
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 0));
        cBondNeighborsB.add(signArray[counter]);
        cBondNeighborsB.add("X");
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 1));
    }

    private void step2(int atomIndex) {
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 0));
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 1));
        cBondNeighborsB.add("X");
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 3));
    }

    private void step3(int atomIndex, int counter) {
        cBondNeighborsB.add(signArray[counter]);
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 1));
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 0));
        cBondNeighborsB.add("X");
    }

    private void step4(int atomIndex) {
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 0));
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 1));
        cBondNeighborsB.add(cTab2Copy.get(atomIndex * 4 + 2));
        cBondNeighborsB.add("X");
    }

    /**
     *
     * @return
     */
    protected List<String> getCTab1() {
        return this.cTab1Copy;
    }

    /**
     *
     * @return
     */
    protected List<String> getCTab2() {
        return this.cTab2Copy;
    }

    /**
     *
     * @return number of remaining molecule A bonds after the clique search,
     * which are neighbors of the MCS
     *
     */
    protected int getNeighborBondNumB() {
        return this.neighborBondNumB;
    }

    /**
     *
     * @return number of remaining molecule A bonds after the clique search,
     * which aren't neighbors
     */
    protected int getBondNumB() {
        return this.setBondNumB;
    }

    List<Integer> getIBondNeighboursB() {
        return this.iBondNeighborsB;
    }

    List<String> getCBondNeighborsB() {
        return this.cBondNeighborsB;
    }
}
