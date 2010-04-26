
/* Copyright (C) 2005-2006 Markus Leber
 *               2006-2009 Syed Asad Rahman {asad@ebi.ac.uk}
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

import java.util.ArrayList;
import java.util.List;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Class to handle mappings of query molecule.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */


@TestClass("org.openscience.cdk.smsd.algorithm.mcgregor.QueryProcessorTest")
public class QueryProcessor {

    private List<String> cTab1Copy;
    private List<String> cTab2Copy;
    private String[] signs;
    private int neighborBondNumA = 0; //number of remaining molecule A bonds after the clique search, which are neighbors of the MCS_1
    private int setBondNumA = 0; //number of remaining molecule A bonds after the clique search, which aren't neighbors
    private List<Integer> iBondNeighborsA;
    private List<String> cBondNeighborsA;
    private int newNeighborNumA;
    private List<Integer> newINeighborsA;
    private List<String> newCNeighborsA;

    /**
     * Query molecule
     * @param cTab1Copy
     * @param cTab2Copy
     * @param signArray
     * @param neighbor_bondnum_A
     * @param set_bondnum_A
     * @param i_bond_neighborsA
     * @param c_bond_neighborsA
     * @param mappingSize
     * @param i_bond_setA
     * @param c_bond_setA 
     */
    protected QueryProcessor(
            List<String> cTab1Copy,
            List<String> cTab2Copy,
            String[] signArray,
            int neighbor_bondnum_A,
            int set_bondnum_A,
            List<Integer> i_bond_neighborsA,
            List<String> c_bond_neighborsA,
            int mappingSize,
            List<Integer> i_bond_setA,
            List<String> c_bond_setA) {
 
        this.cTab1Copy = cTab1Copy;
        this.cTab2Copy = cTab2Copy;
        this.signs = signArray;
        this.neighborBondNumA = neighbor_bondnum_A;
        this.setBondNumA = set_bondnum_A;
        this.iBondNeighborsA = i_bond_neighborsA;
        this.cBondNeighborsA = c_bond_neighborsA;
        this.newNeighborNumA = mappingSize;
        this.newINeighborsA = i_bond_setA;
        this.newCNeighborsA = c_bond_setA;
    }

    /**
     *
     * @param query
     * @param target 
     * @param unmapped_atoms_molA
     * @param mapped_atoms
     * @param counter
     */
    protected void process(
            IAtomContainer query,
            IAtomContainer target,
            List<Integer> unmapped_atoms_molA,
            List<Integer> mapped_atoms,
            int counter) {

        int unmapped_numA = unmapped_atoms_molA.size();
        boolean bond_considered = false;
        boolean normal_bond = true;

//        System.out.println("\n" + cTab1Copy + "\n");


        for (int atomIndex = 0; atomIndex < query.getBondCount(); atomIndex++) {


            Integer indexI = query.getAtomNumber(query.getBond(atomIndex).getAtom(0));
            Integer indexJ = query.getAtomNumber(query.getBond(atomIndex).getAtom(1));
            Integer order = query.getBond(atomIndex).getOrder().ordinal() + 1;

//            System.out.println(AtomI + "= , =" + AtomJ );
            for (Integer unMappedAtomIndex = 0; unMappedAtomIndex < unmapped_numA; unMappedAtomIndex++) {

                if (unmapped_atoms_molA.get(unMappedAtomIndex).equals(indexI)) {
                    normal_bond = unMappedAtomsEqualsIndexJ(query, target, atomIndex, counter, mapped_atoms, indexI, indexJ, order);
                    bond_considered = true;
                } else //Does a ungemaptes atom at second position in the connection occur?
                if (unmapped_atoms_molA.get(unMappedAtomIndex).equals(indexJ)) {
                    normal_bond = unMappedAtomsEqualsIndexI(query, target, atomIndex, counter, mapped_atoms, indexI, indexJ, order);
                    bond_considered = true;
                }
                if (normal_bond && bond_considered) {
                    markNormalBonds(atomIndex, indexI, indexJ, order);
                    normal_bond = true;
                    break;
                }
            }
            bond_considered = false;
        }
    }

    /**
     * 
     * @param setNumA
     * @param setNumB
     * @param i_bond_setA
     * @param i_bond_setB
     * @param unmapped_atoms_molA
     * @param new_Mapping
     * @param counter
     */
    protected void process(
            int setNumA,
            int setNumB,
            List<Integer> i_bond_setA,
            List<Integer> i_bond_setB,
            List<Integer> unmapped_atoms_molA,
            List<Integer> new_Mapping,
            int counter) {

//
//            int newMapingSize,
//            List<Integer> new_i_bond_setA,
//            List<String> new_c_bond_setA,
        boolean bond_considered = false;
        boolean normal_bond = true;


        for (int atomIndex = 0; atomIndex < setNumA; atomIndex++) {
            Integer indexI = i_bond_setA.get(atomIndex * 3 + 0);
            Integer indexJ = i_bond_setA.get(atomIndex * 3 + 1);
            Integer order = i_bond_setA.get(atomIndex * 3 + 2);

            for (Integer unMappedAtomIndex : unmapped_atoms_molA) {
                if (unMappedAtomIndex.equals(indexI)) {
                    normal_bond = unMappedAtomsEqualsIndexJ(setNumA, setNumB, i_bond_setA, i_bond_setB, atomIndex,
                            counter, new_Mapping, indexI, indexJ, order);
                    bond_considered = true;
                } else if (unMappedAtomIndex.equals(indexJ)) {
                    normal_bond = unMappedAtomsEqualsIndexI(setNumA, setNumB, i_bond_setA, i_bond_setB, atomIndex,
                            counter, new_Mapping, indexI, indexJ, order);
                    bond_considered = true;
                }

                if (normal_bond && bond_considered) {
                    markNormalBonds(atomIndex, indexI, indexJ, order);
                    normal_bond = true;
                    break;
                }
            }
            bond_considered = false;
        }
    }

    private int searchCorrespondingAtom(int mapped_atoms_size, int atom_from_other_molecule, int molecule,
            List<Integer> mapped_atoms_org) {


        List<Integer> mapped_atoms = new ArrayList<Integer>(mapped_atoms_org);

        int corresponding_atom = 0;
        for (int a = 0; a < mapped_atoms_size; a++) {
            if ((molecule == 1)
                    && (mapped_atoms.get(a * 2 + 0).intValue() == atom_from_other_molecule)) {
                corresponding_atom = mapped_atoms.get(a * 2 + 1);
            }
            if ((molecule == 2)
                    && (mapped_atoms.get(a * 2 + 1).intValue() == atom_from_other_molecule)) {
                corresponding_atom = mapped_atoms.get(a * 2 + 0);
            }
        }
        return corresponding_atom;
    }

    private void markNormalBonds(int atomIndex,
            Integer indexI,
            Integer indexJ,
            Integer order) {
        newINeighborsA.add(indexI);
        newINeighborsA.add(indexJ);
        newINeighborsA.add(order);
        newCNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 0));
        newCNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 1));
        newCNeighborsA.add("X");
        newCNeighborsA.add("X");
        setBondNumA++;
    }

    private void step1(int atomIndex, int counter) {
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 0));
        cBondNeighborsA.add(signs[counter]);
        cBondNeighborsA.add("X");
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 1));
    }

    private void step2(int atomIndex) {
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 0));
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 1));
        cBondNeighborsA.add("X");
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 3));
    }

    private void step3(int atomIndex, int counter) {
        cBondNeighborsA.add(signs[counter]);
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 1));
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 0));
        cBondNeighborsA.add("X");
    }

    private void step4(int atomIndex) {
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 0));
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 1));
        cBondNeighborsA.add(cTab1Copy.get(atomIndex * 4 + 2));
        cBondNeighborsA.add("X");
    }

    private boolean unMappedAtomsEqualsIndexJ(
            IAtomContainer query,
            IAtomContainer target,
            int atomIndex,
            int counter,
            List<Integer> mapped_atoms,
            Integer indexI,
            Integer indexJ,
            Integer order) {
        boolean normal_bond = true;
        for (int c = 0; c < newNeighborNumA; c++) {

            if (mapped_atoms.get(c * 2).equals(indexJ)) {
                setBondNeighbors(indexI, indexJ, order);
                if (cTab1Copy.get(atomIndex * 4 + 3).compareToIgnoreCase("X") == 0) {

                    step1(atomIndex, counter);
                    McGregorChecks.changeCharBonds(indexJ, signs[counter], query.getBondCount(), query, cTab1Copy);

                    int cor_atom = searchCorrespondingAtom(newNeighborNumA, indexJ, 1, mapped_atoms);
                    McGregorChecks.changeCharBonds(cor_atom, signs[counter], target.getBondCount(), target, cTab2Copy);
                    counter++;
                } else {
                    step2(atomIndex);
                }
                normal_bond = false;
                neighborBondNumA++;
            }
        }
        return normal_bond;
    }

    private boolean unMappedAtomsEqualsIndexI(
            IAtomContainer query,
            IAtomContainer target,
            int atomIndex,
            int counter,
            List<Integer> mapped_atoms,
            Integer indexI,
            Integer indexJ,
            Integer order) {

        boolean normal_bond = true;
        for (int c = 0; c < newNeighborNumA; c++) {


            if (mapped_atoms.get(c * 2 + 0).equals(indexI)) {
                setBondNeighbors(indexI, indexJ, order);
                if (cTab1Copy.get(atomIndex * 4 + 2).compareToIgnoreCase("X") == 0) {
                    step3(atomIndex, counter);
                    McGregorChecks.changeCharBonds(indexI, signs[counter], query.getBondCount(), query, cTab1Copy);

                    int cor_atom = searchCorrespondingAtom(newNeighborNumA, indexI, 1, mapped_atoms);
                    McGregorChecks.changeCharBonds(cor_atom, signs[counter], target.getBondCount(), target, cTab2Copy);
                    counter++;
                } else {
                    step4(atomIndex);
                }
                normal_bond = false;
                neighborBondNumA++;
                //System.out.println("Neighbor");
                //System.out.println(neighborBondNumA);
            }
        }
        return normal_bond;
    }

    private boolean unMappedAtomsEqualsIndexJ(
            int setNumA,
            int setNumB,
            List<Integer> i_bond_setA,
            List<Integer> i_bond_setB,
            int atomIndex,
            int counter,
            List<Integer> new_Mapping,
            Integer indexI,
            Integer indexJ,
            Integer order) {
        boolean normal_bond = true;
        for (int c = 0; c < newNeighborNumA; c++) {

            if (new_Mapping.get(c * 2 + 0).equals(indexJ)) {

                setBondNeighbors(indexI, indexJ, order);
                if (cTab1Copy.get(atomIndex * 4 + 3).compareToIgnoreCase("X") == 0) {
                    step1(atomIndex, counter);
                    McGregorChecks.changeCharBonds(indexJ, signs[counter], setNumA, i_bond_setA, cTab1Copy);
                    int cor_atom = McGregorChecks.searchCorrespondingAtom(newNeighborNumA, indexJ, 1, new_Mapping);
                    McGregorChecks.changeCharBonds(cor_atom, signs[counter], setNumB, i_bond_setB, cTab2Copy);
                    counter++;

                } else {
                    step2(atomIndex);
                }
                normal_bond = false;
                neighborBondNumA++;
            }
        }
        return normal_bond;
    }

    private boolean unMappedAtomsEqualsIndexI(
            int setNumA,
            int setNumB,
            List<Integer> i_bond_setA,
            List<Integer> i_bond_setB,
            int atomIndex,
            int counter, List<Integer> new_Mapping,
            Integer indexI,
            Integer indexJ,
            Integer order) {
        boolean normal_bond = true;
        for (int c = 0; c < newNeighborNumA; c++) {

            if (new_Mapping.get(c * 2 + 0).equals(indexI)) {

                setBondNeighbors(indexI, indexJ, order);
                if (cTab1Copy.get(atomIndex * 4 + 2).compareToIgnoreCase("X") == 0) {
                    step3(atomIndex, counter);
                    McGregorChecks.changeCharBonds(indexI, signs[counter], setNumA, i_bond_setA, cTab1Copy);
                    int cor_atom = McGregorChecks.searchCorrespondingAtom(newNeighborNumA, indexI, 1, new_Mapping);
                    McGregorChecks.changeCharBonds(cor_atom, signs[counter], setNumB, i_bond_setB, cTab2Copy);
                    counter++;
                } else {
                    step4(atomIndex);
                }

                normal_bond = false;
                neighborBondNumA++;

            }
        }
        return normal_bond;
    }

    private void setBondNeighbors(Integer indexI,
            Integer indexJ,
            Integer order) {
        iBondNeighborsA.add(indexI);
        iBondNeighborsA.add(indexJ);
        iBondNeighborsA.add(order);
    }

    /**
     *
     * @return cTabQuery copy
     */
    protected List<String> getCTab1() {
        return this.cTab1Copy;
    }

    /**
     *
     * @return cTabTarget Copy
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
    protected int getNeighborBondNumA() {
        return this.neighborBondNumA;
    }

    /**
     *
     * @return number of remaining molecule A bonds after the clique search,
     * which aren't neighbors
     */
    protected int getBondNumA() {
        return this.setBondNumA;
    }

    List<Integer> getIBondNeighboursA() {
        return this.iBondNeighborsA;
    }

    List<String> getCBondNeighborsA() {
        return this.cBondNeighborsA;
    }
}
