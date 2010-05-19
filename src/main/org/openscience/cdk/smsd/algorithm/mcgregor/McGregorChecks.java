/* Copyright (C) 2009-2010 Syed Asad Rahman {asad@ebi.ac.uk}
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
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.smsd.global.BondType;
import org.openscience.cdk.smsd.helper.BinaryTree;

/**
 * Class to perform check/methods for McGregor class.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.algorithm.mcgregor.McGregorChecksTest")
public class McGregorChecks {

    private static boolean bondTypeFlag = BondType.getInstance().isBondSensitive();

    /**
     *
     * @param source
     * @param target
     * @param neighborBondNumA
     * @param neighborBondNumB
     * @param i_bond_neighbor_atoms_A
     * @param i_bond_neighbor_atoms_B
     * @param cBondNeighborsA
     * @param cBondNeighborsB
     * @return
     */
    protected static boolean isFurtherMappingPossible(
            IAtomContainer source,
            IAtomContainer target,
            int neighborBondNumA,
            int neighborBondNumB,
            List<Integer> i_bond_neighbor_atoms_A,
            List<Integer> i_bond_neighbor_atoms_B,
            List<String> cBondNeighborsA,
            List<String> cBondNeighborsB) {

        for (int row = 0; row < neighborBondNumA; row++) {
//            System.out.println("i " + row);
            String G1A = cBondNeighborsA.get(row * 4 + 0);
            String G2A = cBondNeighborsA.get(row * 4 + 1);


            for (int column = 0; column < neighborBondNumB; column++) {

                String G1B = cBondNeighborsB.get(column * 4 + 0);
                String G2B = cBondNeighborsB.get(column * 4 + 1);

                if (isAtomMatch(G1A, G2A, G1B, G2B)) {
                    try {

                        int Index_I = i_bond_neighbor_atoms_A.get(row * 3 + 0);
                        int Index_IPlus1 = i_bond_neighbor_atoms_A.get(row * 3 + 1);


                        int Index_J = i_bond_neighbor_atoms_B.get(column * 3 + 0);
                        int Index_JPlus1 = i_bond_neighbor_atoms_B.get(column * 3 + 1);

                        IAtom R1_A = source.getAtom(Index_I);
                        IAtom R2_A = source.getAtom(Index_IPlus1);
                        IBond ReactantBond = source.getBond(R1_A, R2_A);

                        IAtom P1_B = target.getAtom(Index_J);
                        IAtom P2_B = target.getAtom(Index_JPlus1);
                        IBond ProductBond = target.getBond(P1_B, P2_B);

                        if (matches(ReactantBond, ProductBond)) {
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

    /**
     *
     * @param ReactantBond
     * @param ProductBond
     * @return
     */
    protected static boolean matches(IBond ReactantBond, IBond ProductBond) {
        if (bondTypeFlag) {
            return bondMatch(ReactantBond, ProductBond);
        } else if (ReactantBond != null && ProductBond != null) {
            return true;
        }
        return false;
    }
    
    /**
     *
     * @param queryBond
     * @param targetBond
     * @return
     */
    private static boolean bondMatch(IBond queryBond, IBond targetBond) {

        if (targetBond instanceof IQueryBond && queryBond instanceof IBond) {
            IQueryBond bond = (IQueryBond) targetBond;
            IQueryAtom atom1 = (IQueryAtom) (targetBond.getAtom(0));
            IQueryAtom atom2 = (IQueryAtom) (targetBond.getAtom(1));
            if (bond.matches(queryBond)) {
                // ok, bonds match
                if (atom1.matches(queryBond.getAtom(0)) && atom2.matches(queryBond.getAtom(1))
                        || atom1.matches(queryBond.getAtom(1)) && atom2.matches(queryBond.getAtom(0))) {
                    // ok, atoms match in either order
                    return true;
                }
            }
        } else if (queryBond instanceof IQueryBond && targetBond instanceof IBond) {
            IQueryBond bond = (IQueryBond) queryBond;
            IQueryAtom atom1 = (IQueryAtom) (queryBond.getAtom(0));
            IQueryAtom atom2 = (IQueryAtom) (queryBond.getAtom(1));
            if (bond.matches(targetBond)) {
                // ok, bonds match
                if (atom1.matches(targetBond.getAtom(0)) && atom2.matches(targetBond.getAtom(1))
                        || atom1.matches(targetBond.getAtom(1)) && atom2.matches(targetBond.getAtom(0))) {
                    // ok, atoms match in either order
                    return true;
                }
            }
        } else {

            int ReactantBondType = queryBond.getOrder().ordinal();
            int ProductBondType = targetBond.getOrder().ordinal();


            if ((queryBond.getFlag(CDKConstants.ISAROMATIC) == targetBond.getFlag(CDKConstants.ISAROMATIC))
                    && (ReactantBondType == ProductBondType)) {
                return true;
            }

            if (queryBond.getFlag(CDKConstants.ISAROMATIC) && targetBond.getFlag(CDKConstants.ISAROMATIC)) {
                return true;
            }

        }
        return false;
    }

    /**
     *
     * @param mappedAtomsSize
     * @param atomFromOtherMolecule
     * @param molecule
     * @param mapped_atoms_org
     * @return
     */
    protected static int searchCorrespondingAtom(int mappedAtomsSize, int atomFromOtherMolecule, int molecule, List<Integer> mapped_atoms_org) {

        List<Integer> mapped_atoms = new ArrayList<Integer>(mapped_atoms_org);

        int corresponding_atom = 0;
        for (int a = 0; a < mappedAtomsSize; a++) {
            if ((molecule == 1) && (mapped_atoms.get(a * 2 + 0).intValue() == atomFromOtherMolecule)) {
                corresponding_atom = mapped_atoms.get(a * 2 + 1);
            }
            if ((molecule == 2) && (mapped_atoms.get(a * 2 + 1).intValue() == atomFromOtherMolecule)) {
                corresponding_atom = mapped_atoms.get(a * 2 + 0);
            }
        }
        return corresponding_atom;
    }

    /**
     *
     * @param G1A
     * @param G2A
     * @param G1B
     * @param G2B
     * @return
     */
    protected static boolean isAtomMatch(String G1A, String G2A, String G1B, String G2B) {
        if ((G1A.compareToIgnoreCase(G1B) == 0 && G2A.compareToIgnoreCase(G2B) == 0)
                || (G1A.compareToIgnoreCase(G2B) == 0 && G2A.compareToIgnoreCase(G1B) == 0)) {
            return true;
        }
        return false;
    }

    /*
     * Modified function call by ASAD in Java have to check
     *
     */
    protected static int removeTreeStructure(BinaryTree cur_struc) {

        BinaryTree equal_struc = cur_struc.getEqual();
        BinaryTree not_equal_struc = cur_struc.getNotEqual();
        cur_struc = null;

        if (equal_struc != null) {
            removeTreeStructure(equal_struc);
        }

        if (not_equal_struc != null) {
            removeTreeStructure(not_equal_struc);
        }

        return 0;
    }

    //Function compaires a structure array with itself. Sometimes a mapping occurs several times within the array.
    //The function eliminates these recurring mappings. Function is called in function best_solution.
    //The function is called by itself as long as the last list element is processed.
    /**
     *
     * @param atom_mapping
     * @return
     */
    protected static List<Integer> removeRecurringMappings(List<Integer> atom_mapping) {

        boolean exist = true;
        List<Integer> temp_map = new ArrayList<Integer>();
        int temp_counter = 0;
        int atom_mapping_size = atom_mapping.size();
        for (int x = 0; x < atom_mapping_size; x += 2) {
            int atom = atom_mapping.get(x);
            for (int y = x + 2; y < atom_mapping_size; y += 2) {
                if (atom == atom_mapping.get(y)) {
                    exist = false;
                }
            }
            if (exist == true) {
                temp_map.add(atom_mapping.get(x + 0));
                temp_map.add(atom_mapping.get(x + 1));
                temp_counter += 2;
            }

            exist = true;
        }

        return temp_map;
    }

    /**
     * The function is called in function partsearch. The function is given a temporary matrix and a position (row/column)
     * within this matrix. First the function sets all entries to zero, which can be exlcuded in respect to the current
     * atom by atom matching. After this the function replaces all entries in the same row and column of the current
     * position by zeros. Only the entry of the current position is set to one.
     * Return value "count_arcsleft" counts the number of arcs, which are still in the matrix.
     * @param row
     * @param column
     * @param MARCS
     * @param mcGregorHelper
     */
    protected static void removeRedundantArcs(int row, int column, List<Integer> MARCS, McgregorHelper mcGregorHelper) {
        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();
        List<Integer> iBondNeighborAtomsA = mcGregorHelper.getiBondNeighborAtomsA();
        List<Integer> iBondNeighborAtomsB = mcGregorHelper.getiBondNeighborAtomsB();
        int G1_atom = iBondNeighborAtomsA.get(row * 3 + 0);
        int G2_atom = iBondNeighborAtomsA.get(row * 3 + 1);
        int G3_atom = iBondNeighborAtomsB.get(column * 3 + 0);
        int G4_atom = iBondNeighborAtomsB.get(column * 3 + 1);

        for (int x = 0; x < neighborBondNumA; x++) {
            int row_atom1 = iBondNeighborAtomsA.get(x * 3 + 0);
            int row_atom2 = iBondNeighborAtomsA.get(x * 3 + 1);

            for (int y = 0; y < neighborBondNumB; y++) {
                int column_atom3 = iBondNeighborAtomsB.get(y * 3 + 0);
                int column_atom4 = iBondNeighborAtomsB.get(y * 3 + 1);

                if (McGregorChecks.cases(G1_atom, G2_atom, G3_atom, G4_atom, row_atom1, row_atom2, column_atom3, column_atom4)) {
                    MARCS.set(x * neighborBondNumB + y, 0);
                }

            }
        }

        for (int v = 0; v < neighborBondNumA; v++) {
            MARCS.set(v * neighborBondNumB + column, 0);
        }

        for (int w = 0; w < neighborBondNumB; w++) {
            MARCS.set(row * neighborBondNumB + w, 0);
        }

        MARCS.set(row * neighborBondNumB + column, 1);
    }

    /**
     *
     * @param bond_number
     * @param c_set
     * @return
     */
    protected static List<String> generateCSetCopy(int bond_number, List<String> c_set) {
        List<String> cTabCopy = new ArrayList<String>();
        for (int a = 0; a < bond_number; a++) {
            cTabCopy.add(c_set.get(a * 4 + 0));
            cTabCopy.add(c_set.get(a * 4 + 1));
            cTabCopy.add("X");
            cTabCopy.add("X");
        }

        return cTabCopy;
    }

    /**
     *
     * @param G1_atom
     * @param G3_atom
     * @param G4_atom
     * @param row_atom1
     * @param row_atom2
     * @param column_atom3
     * @param column_atom4
     * @return
     */
    protected static boolean case1(int G1_atom, int G3_atom, int G4_atom, int row_atom1, int row_atom2, int column_atom3, int column_atom4) {
        if (((G1_atom == row_atom1) || (G1_atom == row_atom2))
                && (!(((column_atom3 == G3_atom) || (column_atom4 == G3_atom)) || ((column_atom3 == G4_atom) || (column_atom4 == G4_atom))))) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param G2_atom
     * @param G3_atom
     * @param G4_atom
     * @param row_atom1
     * @param row_atom2
     * @param column_atom3
     * @param column_atom4
     * @return
     */
    protected static boolean case2(int G2_atom, int G3_atom, int G4_atom, int row_atom1, int row_atom2, int column_atom3, int column_atom4) {
        if (((G2_atom == row_atom1)
                || (G2_atom == row_atom2))
                && (!(((column_atom3 == G3_atom) || (column_atom4 == G3_atom)) || ((column_atom3 == G4_atom) || (column_atom4 == G4_atom))))) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param G1_atom
     * @param G3_atom
     * @param G2_atom
     * @param row_atom1
     * @param row_atom2
     * @param column_atom3
     * @param column_atom4
     * @return
     */
    protected static boolean case3(int G1_atom, int G3_atom, int G2_atom, int row_atom1, int row_atom2, int column_atom3, int column_atom4) {
        if (((G3_atom == column_atom3) || (G3_atom == column_atom4))
                && (!(((row_atom1 == G1_atom) || (row_atom2 == G1_atom)) || ((row_atom1 == G2_atom) || (row_atom2 == G2_atom))))) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param G1_atom
     * @param G2_atom
     * @param G4_atom
     * @param row_atom1
     * @param row_atom2
     * @param column_atom3
     * @param column_atom4
     * @return
     */
    protected static boolean case4(int G1_atom, int G2_atom, int G4_atom, int row_atom1, int row_atom2, int column_atom3, int column_atom4) {
        if (((G4_atom == column_atom3) || (G4_atom == column_atom4))
                && (!(((row_atom1 == G1_atom) || (row_atom2 == G1_atom)) || ((row_atom1 == G2_atom) || (row_atom2 == G2_atom))))) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param G1_atom
     * @param G2_atom
     * @param G3_atom
     * @param G4_atom
     * @param row_atom1
     * @param row_atom2
     * @param column_atom3
     * @param column_atom4
     * @return
     */
    protected static boolean cases(int G1_atom, int G2_atom, int G3_atom, int G4_atom, int row_atom1, int row_atom2, int column_atom3, int column_atom4) {
        if (case1(G1_atom, G3_atom, G4_atom, row_atom1, row_atom2, column_atom3, column_atom4) || case2(G2_atom, G3_atom, G4_atom, row_atom1, row_atom2, column_atom3, column_atom4) || case3(G1_atom, G3_atom, G2_atom, row_atom1, row_atom2, column_atom3, column_atom4) || case4(G1_atom, G2_atom, G4_atom, row_atom1, row_atom2, column_atom3, column_atom4)) {
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
     * @param i_bond_neighbor_atoms_A
     * @param i_bond_neighbor_atoms_B
     * @param cBondNeighborsA
     * @param cBondNeighborsB
     * @param modifiedARCS
     * @return
     */
    protected static List<Integer> setArcs(IAtomContainer source,
            IAtomContainer target,
            int neighborBondNumA,
            int neighborBondNumB,
            List<Integer> i_bond_neighbor_atoms_A,
            List<Integer> i_bond_neighbor_atoms_B,
            List<String> cBondNeighborsA,
            List<String> cBondNeighborsB,
            List<Integer> modifiedARCS) {

        for (int row = 0; row < neighborBondNumA; row++) {
            for (int column = 0; column < neighborBondNumB; column++) {

                String G1A = cBondNeighborsA.get(row * 4 + 0);
                String G2A = cBondNeighborsA.get(row * 4 + 1);
                String G1B = cBondNeighborsB.get(column * 4 + 0);
                String G2B = cBondNeighborsB.get(column * 4 + 1);


                if (McGregorChecks.isAtomMatch(G1A, G2A, G1B, G2B)) {


                    int Index_I = i_bond_neighbor_atoms_A.get(row * 3 + 0);
                    int Index_IPlus1 = i_bond_neighbor_atoms_A.get(row * 3 + 1);

                    IAtom R1_A = source.getAtom(Index_I);
                    IAtom R2_A = source.getAtom(Index_IPlus1);
                    IBond ReactantBond = source.getBond(R1_A, R2_A);

                    int Index_J = i_bond_neighbor_atoms_B.get(column * 3 + 0);
                    int Index_JPlus1 = i_bond_neighbor_atoms_B.get(column * 3 + 1);

                    IAtom P1_B = target.getAtom(Index_J);
                    IAtom P2_B = target.getAtom(Index_JPlus1);
                    IBond ProductBond = target.getBond(P1_B, P2_B);
                    if (matches(ReactantBond, ProductBond)) {
                        modifiedARCS.set(row * neighborBondNumB + column, 1);
                    }
                }
            }
        }
        return modifiedARCS;
    }

    /**
     *
     * @param atomContainer
     * @return
     * @throws IOException
     */
    protected static List<String> generateCTabCopy(IAtomContainer atomContainer) throws IOException {
        List<String> c_tab_copy = new ArrayList<String>();
        for (int a = 0; a < atomContainer.getBondCount(); a++) {
            String AtomI = atomContainer.getBond(a).getAtom(0).getSymbol();
            String AtomJ = atomContainer.getBond(a).getAtom(1).getSymbol();
            c_tab_copy.add(AtomI);
            c_tab_copy.add(AtomJ);
            c_tab_copy.add("X");
            c_tab_copy.add("X");
        }

        return c_tab_copy;
    }

    /**
     *
     * @param TEMPMARCS
     * @param neighborBondNumA
     * @param neighborBondNumB
     * @return
     */
    protected static int countArcsLeft(List<Integer> TEMPMARCS, int neighborBondNumA, int neighborBondNumB) {
        int arcsleft = 0;

        for (int a = 0; a < neighborBondNumA; a++) {
            for (int b = 0; b < neighborBondNumB; b++) {

                if (TEMPMARCS.get(a * neighborBondNumB + b) == (1)) {
                    arcsleft++;
                }
            }
        }
        return arcsleft;
    }

    /**
     *
     * @param corresponding_atom
     * @param new_symbol
     * @param neighbor_bondnum
     * @param atomContainer
     * @param c_bond_neighbors
     * @return
     */
    protected static int changeCharBonds(int corresponding_atom, String new_symbol, int neighbor_bondnum,
            IAtomContainer atomContainer, List<String> c_bond_neighbors) {
        for (int atomIndex = 0; atomIndex < neighbor_bondnum; atomIndex++) {
            IBond bond = atomContainer.getBond(atomIndex);
            if ((atomContainer.getAtomNumber(bond.getAtom(0)) == corresponding_atom)
                    && (c_bond_neighbors.get(atomIndex * 4 + 2).compareToIgnoreCase("X") == 0)) {
                c_bond_neighbors.set(atomIndex * 4 + 2, c_bond_neighbors.get(atomIndex * 4 + 0));
                c_bond_neighbors.set(atomIndex * 4 + 0, new_symbol);
            }

            if ((atomContainer.getAtomNumber(bond.getAtom(1)) == corresponding_atom)
                    && (c_bond_neighbors.get(atomIndex * 4 + 3).compareToIgnoreCase("X") == 0)) {
                c_bond_neighbors.set(atomIndex * 4 + 3, c_bond_neighbors.get(atomIndex * 4 + 1));
                c_bond_neighbors.set(atomIndex * 4 + 1, new_symbol);
            }

        }

        return 0;
    }

    /**
     *
     * @param corresponding_atom
     * @param new_symbol
     * @param neighbor_bondnum
     * @param i_bond_neighbors
     * @param c_bond_neighbors
     * @return
     */
    protected static int changeCharBonds(int corresponding_atom, String new_symbol, int neighbor_bondnum,
            List<Integer> i_bond_neighbors, List<String> c_bond_neighbors) {

        for (int atomIndex = 0; atomIndex < neighbor_bondnum; atomIndex++) {
            if ((i_bond_neighbors.get(atomIndex * 3 + 0) == (corresponding_atom))
                    && (c_bond_neighbors.get(atomIndex * 4 + 2).compareToIgnoreCase("X") == 0)) {
                c_bond_neighbors.set(atomIndex * 4 + 2, c_bond_neighbors.get(atomIndex * 4 + 0));
                c_bond_neighbors.set(atomIndex * 4 + 0, new_symbol);
            }

            if ((i_bond_neighbors.get(atomIndex * 3 + 1) == (corresponding_atom))
                    && (c_bond_neighbors.get(atomIndex * 4 + 3).compareToIgnoreCase("X") == 0)) {
                c_bond_neighbors.set(atomIndex * 4 + 3, c_bond_neighbors.get(atomIndex * 4 + 1));
                c_bond_neighbors.set(atomIndex * 4 + 1, new_symbol);
            }

        }

        return 0;
    }

    static boolean isFurtherMappingPossible(IAtomContainer source, IAtomContainer target, McgregorHelper mcGregorHelper) {

        int neighborBondNumA = mcGregorHelper.getNeighborBondNumA();
        int neighborBondNumB = mcGregorHelper.getNeighborBondNumB();
        List<Integer> iBondNeighborAtomsA = mcGregorHelper.getiBondNeighborAtomsA();
        List<Integer> iBondNeighborAtomsB = mcGregorHelper.getiBondNeighborAtomsB();
        List<String> cBondNeighborsA = mcGregorHelper.getcBondNeighborsA();
        List<String> cBondNeighborsB = mcGregorHelper.getcBondNeighborsB();

        boolean moreMappingPossible = false;
        for (int row = 0; row < neighborBondNumA; row++) {
//            System.out.println("i " + row);
            String G1A = cBondNeighborsA.get(row * 4 + 0);
            String G2A = cBondNeighborsA.get(row * 4 + 1);


            for (int column = 0; column < neighborBondNumB; column++) {

                String G1B = cBondNeighborsB.get(column * 4 + 0);
                String G2B = cBondNeighborsB.get(column * 4 + 1);

                if (isAtomMatch(G1A, G2A, G1B, G2B)) {
                    try {

                        int Index_I = iBondNeighborAtomsA.get(row * 3 + 0);
                        int Index_IPlus1 = iBondNeighborAtomsA.get(row * 3 + 1);


                        int Index_J = iBondNeighborAtomsB.get(column * 3 + 0);
                        int Index_JPlus1 = iBondNeighborAtomsB.get(column * 3 + 1);

                        IAtom R1_A = source.getAtom(Index_I);
                        IAtom R2_A = source.getAtom(Index_IPlus1);
                        IBond ReactantBond = source.getBond(R1_A, R2_A);

                        IAtom P1_B = target.getAtom(Index_J);
                        IAtom P2_B = target.getAtom(Index_JPlus1);
                        IBond ProductBond = target.getBond(P1_B, P2_B);

                        if (matches(ReactantBond, ProductBond)) {
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

    static List<Integer> markUnMappedAtoms(boolean flag, IAtomContainer container, Map<Integer, Integer> present_Mapping) {
        List<Integer> unmappedMolAtoms = new ArrayList<Integer>();

        int unmapped_num = 0;
        boolean atom_is_unmapped = true;

        for (int a = 0; a < container.getAtomCount(); a++) {
            //Atomic list are only numbers from 1 to atom_number1

            if (flag) {
                for (Integer key : present_Mapping.keySet()) {
                    if (key == a) {
                        atom_is_unmapped = false;
                    }
                }
            } else {
                for (Integer value : present_Mapping.values()) {
                    if (value == a) {
                        atom_is_unmapped = false;
                    }
                }
            }

            if (atom_is_unmapped) {
                unmappedMolAtoms.add(unmapped_num, a);
                unmapped_num++;
            }
            atom_is_unmapped = true;
        }
        return unmappedMolAtoms;
    }

    static List<Integer> markUnMappedAtoms(boolean flag, IAtomContainer source, List<Integer> mapped_atoms, int clique_siz) {
        List<Integer> unmappedMolAtoms = new ArrayList<Integer>();
        int unmapped_num = 0;
        boolean atom_is_unmapped = true;

//        System.out.println("Mapped Atoms: " + mappedAtoms);

        for (int a = 0; a < source.getAtomCount(); a++) {
            //Atomic list are only numbers from 1 to atom_number1

            for (int b = 0; b < clique_siz; b++) {
                //the number of nodes == number of assigned pairs
                if ((flag && mapped_atoms.get(b * 2) == a)
                        || (!flag && mapped_atoms.get(b * 2 + 1) == a)) {
                    atom_is_unmapped = false;
                }
            }
            if (atom_is_unmapped == true) {
                unmappedMolAtoms.add(unmapped_num++, a);
            }
            atom_is_unmapped = true;
        }
        return unmappedMolAtoms;
    }
}
