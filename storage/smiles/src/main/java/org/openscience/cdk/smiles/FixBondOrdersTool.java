/*  Copyright (C)  2012  Kevin Lawson <kevin.lawson@syngenta.com>
 *                       Lucy Entwistle <lucy.entwistle@syngenta.com>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smiles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.tools.CDKHydrogenAdder;

/**
 * Class to Fix bond orders at present for Aromatic Rings only.
 *
 * Contains one public function: kekuliseAromaticRings(IAtomContainer molecule)
 * <ul>
 * <li>Analyses which rings are marked aromatic/SP2/Planar3
 * <li>Splits rings into groups containing independent sets of single/fused rings
 * <li>Loops over each ring group
 * <li>Uses an adjacency matrix of bonds (rows) and atoms (columns) to represent
 * each fused ring system
 * <li>Scans the adjacency matrix for bonds for which there
 * is no order choice (eg - both bonds to the NH of pyrrole must be single)
 * <li>All choices made to match valency against bonds used (including implicit H atoms)
 * <li>Solves other bonds as possible - dependent on previous choices - makes free
 * (random) choices only where necessary and possible
 * <li>Makes assumption that where there is a choice in bond order
 * (not forced by previous choices) - either choice is consistent with correct solution
 *
 * <li>Requires molecule with all rings to be solved being marked aromatic
 * (SP2/Planar3 atoms). All bonds to non-ring atoms need to be fully defined
 * (including implicit H atoms)
 * </ul>
 *
 * @author Kevin Lawson
 * @author Lucy Entwistle
 * @cdk.module smiles
 * @cdk.githash
 */
public class FixBondOrdersTool {

    private boolean interrupted;

    private static class Matrix {

        private int[] mArray;
        private int   rowCount;
        private int   columnCount;

        public Matrix(Integer rows, Integer cols) {

            //Single array of size rows * cols in matrix
            mArray = new int[rows * cols];

            //Record no of rows and number of columns
            rowCount = rows;
            columnCount = cols;
        }

        public void set(Integer rIndex, Integer cIndex, Integer val) {
            mArray[rIndex * columnCount + cIndex] = val;
        }

        public Integer get(Integer rIndex, Integer cIndex) {
            return mArray[rIndex * columnCount + cIndex];
        }

        public Integer colIndexOf(Integer colIndex, Integer val) {
            for (int i = 0; i < rowCount; i++) {
                if (mArray[i * columnCount + colIndex] == val) {
                    return i;
                }
            }
            return -1;
        }

        public Integer rowIndexOf(Integer rowIndex, Integer val) {
            for (int i = 0; i < columnCount; i++) {
                if (mArray[rowIndex * getCols() + i] == val) {
                    return i;
                }
            }
            return -1;
        }

        public Integer sumOfRow(Integer rowIndex) {
            Integer sumOfRow = 0;
            for (int i = 0; i < columnCount; i++) {
                sumOfRow += mArray[rowIndex * columnCount + i];
            }
            return sumOfRow;
        }

        public Integer getRows() {
            return rowCount;
        }

        public Integer getCols() {
            return columnCount;
        }
    }

    /**
     * Constructor for the FixBondOrdersTool object.
     */
    public FixBondOrdersTool() {}

    /**
     * kekuliseAromaticRings - function to add double/single bond order information for molecules having rings containing all atoms marked SP2 or Planar3 hybridisation.
     * @param molecule The {@link IAtomContainer} to kekulise
     * @return The {@link IAtomContainer} with kekule structure
     * @throws CDKException
     */
    public IAtomContainer kekuliseAromaticRings(IAtomContainer molecule) throws CDKException {
        IAtomContainer mNew = null;
        try {
            mNew = (IAtomContainer) molecule.clone();
        } catch (Exception e) {
            throw new CDKException("Failed to clone source molecule");
        }

        IRingSet ringSet;

        try {
            ringSet = removeExtraRings(mNew);
        } catch (CDKException x) {
            throw x;
        } catch (Exception x) {
            throw new CDKException("failure in SSSRFinder.findAllRings", x);
        }

        if (ringSet == null) {
            throw new CDKException("failure in SSSRFinder.findAllRings");
        }

        //We need to establish which rings share bonds and set up sets of such interdependant rings
        List<Integer[]> rBondsArray = null;
        List<List<Integer>> ringGroups = null;

        //Start by getting a list (same dimensions and ordering as ringset) of all the ring bond numbers in the reduced ring set
        rBondsArray = getRingSystem(mNew, ringSet);
        //Now find out which share a bond and assign them accordingly to groups
        ringGroups = assignRingGroups(rBondsArray);

        //Loop through each group of rings checking all choices of double bond combis and seeing if you can get a
        //proper molecule.
        for (int i = 0; i < ringGroups.size(); i++) {

            //Set all ring bonds with single order to allow Matrix solving to work
            setAllRingBondsSingleOrder(ringGroups.get(i), ringSet);

            //Set up  lists of atoms, bonds and atom pairs for this ringGroup
            List<Integer> atomNos = null;
            atomNos = getAtomNosForRingGroup(mNew, ringGroups.get(i), ringSet);

            List<Integer> bondNos = null;
            bondNos = getBondNosForRingGroup(mNew, ringGroups.get(i), ringSet);

            //Array of same dimensions as bondNos (cols in Matrix)
            List<Integer[]> atomNoPairs = null;
            atomNoPairs = getAtomNoPairsForRingGroup(mNew, bondNos);

            //Set up ajacency Matrix
            Matrix M = new Matrix(atomNos.size(), bondNos.size());
            for (int x = 0; x < M.getRows(); x++) {
                for (int y = 0; y < M.getCols(); y++) {
                    if (atomNos.get(x) == atomNoPairs.get(y)[0]) {
                        M.set(x, y, 1);
                    } else {
                        if (atomNos.get(x) == atomNoPairs.get(y)[1]) {
                            M.set(x, y, 1);
                        } else {
                            M.set(x, y, 0);
                        }
                    }
                }
            }

            //Array of same dimensions as atomNos (rows in Matrix)
            List<Integer> freeValencies = null;
            freeValencies = getFreeValenciesForRingGroup(mNew, atomNos, M, ringSet);

            //Array of "answers"
            List<Integer> bondOrders = new ArrayList<Integer>();
            for (int j = 0; j < bondNos.size(); j++) {
                bondOrders.add(0);
            }

            if (solveMatrix(M, atomNos, bondNos, freeValencies, atomNoPairs, bondOrders)) {
                for (int j = 0; j < bondOrders.size(); j++) {
                    mNew.getBond(bondNos.get(j)).setOrder(
                            bondOrders.get(j) == 1 ? IBond.Order.SINGLE : IBond.Order.DOUBLE);
                }
            } else {
                //                TODO Put any failure code here
            }
        }
        return mNew;
    }

    /**
     * Removes rings which do not have all sp2/planar3 aromatic atoms.
     * and also gets rid of rings that have more than 8 atoms in them.
     *
     * @param m The {@link IAtomContainer} from which we want to remove rings
     * @return The set of reduced rings
     */
    private IRingSet removeExtraRings(IAtomContainer m) throws Exception {

        IRingSet rs = Cycles.sssr(m).toRingSet();

        //remove rings which dont have all aromatic atoms (according to hybridization set by lower case symbols in smiles):
        Iterator<IAtomContainer> i = rs.atomContainers().iterator();
        while (i.hasNext()) {
            IRing r = (IRing) i.next();
            if (r.getAtomCount() > 8) {
                i.remove();
            } else {
                for (IAtom a : r.atoms()) {
                    Hybridization h = a.getHybridization();
                    if (h == CDKConstants.UNSET || !(h == Hybridization.SP2 || h == Hybridization.PLANAR3)) {
                        i.remove();
                        break;
                    }
                }
            }
        }
        return rs;
    }

    /**
     * Stores an {@link IRingSet} corresponding to a molecule using the bond numbers.
     *
     * @param mol The IAtomContainer for which to store the IRingSet.
     * @param ringSet The IRingSet to store
     * @return The List of Integer arrays for the bond numbers of each ringSet
     */

    private List<Integer[]> getRingSystem(IAtomContainer mol, IRingSet ringSet) {
        List<Integer[]> bondsArray;
        bondsArray = new ArrayList<Integer[]>();
        for (int r = 0; r < ringSet.getAtomContainerCount(); ++r) {
            IRing ring = (IRing) ringSet.getAtomContainer(r);
            Integer[] bondNumbers = new Integer[ring.getBondCount()];
            for (int i = 0; i < ring.getBondCount(); ++i) {
                bondNumbers[i] = mol.getBondNumber(ring.getBond(i));
            }
            bondsArray.add(bondNumbers);
        }
        return bondsArray;
    }

    /**
     * Assigns a set of rings to groups each sharing a bond.
     *
     * @param rBondsArray
     * @return A List of Lists each containing the ring indices of a set of fused rings
     */
    private List<List<Integer>> assignRingGroups(List<Integer[]> rBondsArray) {
        List<List<Integer>> ringGroups;
        ringGroups = new ArrayList<List<Integer>>();
        for (int i = 0; i < rBondsArray.size() - 1; i++) { //for each ring except the last in rBondsArray
            for (int j = 0; j < rBondsArray.get(i).length; j++) { //for each bond in each ring

                //check there's no shared bond with any other ring already in ringGroups
                for (int k = i + 1; k < rBondsArray.size(); k++) {
                    for (int l = 0; l < rBondsArray.get(k).length; l++) { //for each ring in each ring

                        //Is there a bond in common? Then add both rings
                        if (rBondsArray.get(i)[j] == rBondsArray.get(k)[l]) {
                            if (i != k) {
                                ringGroups.add(new ArrayList<Integer>());
                                ringGroups.get(ringGroups.size() - 1).add(i);
                                ringGroups.get(ringGroups.size() - 1).add(k);
                            }
                        }
                    }
                }
            }
        }
        while (combineGroups(ringGroups));

        //Anything not added yet is a singleton
        for (int i = 0; i < rBondsArray.size(); i++) {
            boolean found = false;
            for (int j = 0; j < ringGroups.size(); j++) {
                if (ringGroups.get(j).contains(i)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                ringGroups.add(new ArrayList<Integer>());
                ringGroups.get(ringGroups.size() - 1).add(i);
            }
        }
        return ringGroups;
    }

    private Boolean combineGroups(List<List<Integer>> ringGroups) {
        for (int i = 0; i < ringGroups.size() - 1; i++) {

            //Look for another group to combine with it
            for (int j = i + 1; j < ringGroups.size(); j++) {
                for (int k = 0; k < ringGroups.get(j).size(); k++) {
                    if (ringGroups.get(i).contains(ringGroups.get(j).get(k))) {

                        //Add all the new elements
                        for (int l = 0; l < ringGroups.get(j).size(); l++) {
                            if (!ringGroups.get(i).contains(ringGroups.get(j).get(l))) {
                                ringGroups.get(i).add(ringGroups.get(j).get(l));
                            }
                        }
                        ringGroups.remove(j);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sets all bonds in an {@link IRingSet} to single order.
     * @param ringGroup
     * @param ringSet
     * @return True for success
     */
    private Boolean setAllRingBondsSingleOrder(List<Integer> ringGroup, IRingSet ringSet) {
        for (Integer i : ringGroup) {
            for (IBond bond : ringSet.getAtomContainer(i).bonds()) {
                bond.setOrder(IBond.Order.SINGLE);
            }
        }
        return true;
    }

    /**
     * Gets the List of atom nos corresponding to a particular set of fused rings.
     *
     * @param {@link IAtomContainer} molecule
     * @param ringGroup
     * @param {@link IRingSet} ringSet
     * @return List of atom numbers for each set
     */
    private List<Integer> getAtomNosForRingGroup(IAtomContainer molecule, List<Integer> ringGroup, IRingSet ringSet) {
        List<Integer> atc = new ArrayList<Integer>();
        for (Integer i : ringGroup) {
            for (IAtom atom : ringSet.getAtomContainer(i).atoms()) {
                if (atc.size() > 0) {
                    if (!atc.contains(molecule.getAtomNumber(atom))) {
                        atc.add(molecule.getAtomNumber(atom));
                    }
                } else {
                    atc.add(molecule.getAtomNumber(atom));
                }
            }
        }
        return atc;
    }

    /**
     * Gets the List of bond nos corresponding to a particular set of fused rings.
     *
     * @param {@link IAtomContainer} molecule
     * @param ringGroup
     * @param {@link IRingSet} ringSet
     * @return List of bond numbers for each set
     */
    private List<Integer> getBondNosForRingGroup(IAtomContainer molecule, List<Integer> ringGroup, IRingSet ringSet) {
        List<Integer> btc = new ArrayList<Integer>();
        for (Integer i : ringGroup) {
            for (IBond bond : ringSet.getAtomContainer(i).bonds()) {
                if (btc.size() > 0) {
                    if (!btc.contains(molecule.getBondNumber(bond))) {
                        btc.add(molecule.getBondNumber(bond));
                    }
                } else {
                    btc.add(molecule.getBondNumber(bond));
                }
            }
        }
        return btc;
    }

    /**
     * Gets List of atom number pairs for each bond in a list of bonds for the molecule.
     *
     * @param {@link IAtomContainer} molecule
     * @param bondsToCheck
     * @return List of atom pairs
     */
    private List<Integer[]> getAtomNoPairsForRingGroup(IAtomContainer molecule, List<Integer> bondsToCheck) {
        List<Integer[]> aptc = new ArrayList<Integer[]>();
        for (Integer i : bondsToCheck) {
            Integer[] aps = new Integer[2];
            aps[0] = molecule.getAtomNumber(molecule.getBond(i).getAtom(0));
            aps[1] = molecule.getAtomNumber(molecule.getBond(i).getAtom(1));
            aptc.add(aps);
        }
        return aptc;
    }

    /**
     * Function to set up an array of integers corresponding to indicate how many free valencies need fulfilling for each atom through ring bonds.
     *
     * @param {@link IAtomContainer} molecule
     * @param atomsToCheck
     * @param M
     * @return The List of free valencies available for extra ring bonding
     */
    private List<Integer> getFreeValenciesForRingGroup(IAtomContainer molecule, List<Integer> atomsToCheck, Matrix M,
            IRingSet rs) {
        List<Integer> fvtc = new ArrayList<Integer>();
        for (int i = 0; i < atomsToCheck.size(); i++) {
            int j = atomsToCheck.get(i);

            //Put in an implicit hydrogen atom for Planar3 C- atoms in 5-membered rings (it doesn't get put in by the Smiles parser)
            if (("C".equals(molecule.getAtom(j).getSymbol()))
                    && (molecule.getAtom(j).getHybridization() == Hybridization.PLANAR3)) {

                //Check that ring containing the atom is five-membered
                for (IAtomContainer ac : rs.atomContainers()) {
                    if (ac.contains(molecule.getAtom(j))) {
                        if ((int) molecule.getBondOrderSum(molecule.getAtom(j)) == 2 && ac.getAtomCount() == 5) {
                            molecule.getAtom(j).setImplicitHydrogenCount(1);
                            break;
                        }
                    }
                }
            }
            int implicitH = 0;
            if (molecule.getAtom(j).getImplicitHydrogenCount() == null) {
                CDKHydrogenAdder ha = CDKHydrogenAdder.getInstance(molecule.getBuilder());
                try {
                    ha.addImplicitHydrogens(molecule, molecule.getAtom(j));
                    implicitH = molecule.getAtom(j).getImplicitHydrogenCount();
                } catch (CDKException e) {
                    //No need to do anything because implicitH already set to 0
                }

            } else {
                implicitH = molecule.getAtom(j).getImplicitHydrogenCount();
            }
            fvtc.add(molecule.getAtom(j).getValency()
                    - (implicitH + (int) molecule.getBondOrderSum(molecule.getAtom(j))) + M.sumOfRow(i));
        }
        return fvtc;
    }

    /**
     * Function to solve the adjacency Matrix.
     * Returns true/false on success/failure.
     * Passed a reference to an array of bond orders to be filled in.
     * Passed a setup Matrix M indicating the atoms that are part of each bond.
     * The system v = Mb represents the set of equations: valence[atomA] = SUM
     * OF ( M[A][B]*bondOrder[bondB] ) where M[A][B] = 1 if atom A is part of
     * bond B, and M[A][B] = 0 otherwise. Use the system to solve bondOrder. For
     * example if atom 1 has free valence 2, and is part of bonds 5 and 6, we
     * know that B5 = 1, B6 = 1 if then also, atom 2 has free valence 3, and is
     * part of bond 5 and bond 9, we know, from the solved equation above that
     * B9 = 2. And so forth.
     *
     * If nothing can be deduced from previously solved equations, the code
     * assigns a 1 to the first unknown bond it finds in the bondOrder array and
     * continues.
     *
     * @param M
     * @param atomNos
     * @param bondNos
     * @param freeValencies
     * @param atomNoPairs
     * @param bondOrder
     * @return True or false for success or failure
     */
    private Boolean solveMatrix(Matrix M, List<Integer> atomNos, List<Integer> bondNos, List<Integer> freeValencies,
            List<Integer[]> atomNoPairs, List<Integer> bondOrder) {

        // Look for bonds that need to be a certain order
        List<Integer> solved = new ArrayList<Integer>();
        List<Integer> solvedRow = new ArrayList<Integer>();
        for (int j = 0; j < atomNos.size(); j++) {

            // Count no.of bonds for this atom
            int sumOfRow = M.sumOfRow(j);

            // Atom with no of bonds equal to its valence - all must be single bonds.
            if (sumOfRow == freeValencies.get(j)) {
                for (int k = 0; k < bondNos.size(); k++) {
                    if (M.get(j, k) == 1) {
                        bondOrder.set(k, 1);
                        solved.add(k);
                    }
                }
                solvedRow.add(j);
            } // Atom with only one bond - bond must be equal to atom valence.
            else if (sumOfRow == 1) {
                for (int k = 0; k < bondNos.size(); k++) {
                    if (M.get(j, k) == 1) {
                        bondOrder.set(k, freeValencies.get(j));
                        solved.add(k);
                    }
                }
                solvedRow.add(j);
            }
        }

        /*
         * thisRun indicates whether any bonds have been solved on this run
         * through the Matrix. Loop continues until all bonds have been solved
         * or there is a run where no bonds were solved, showing that the
         * structure is unsolvable.
         */
        int thisRun = 1;
        while (solvedRow.size() != M.getRows() && thisRun == 1) {
            thisRun = 0;
            if (solved.size() > 0) {
                for (int j = 0; j < M.getRows(); j++) {
                    if (solvedRow.contains(j) == false) {
                        int unknownBonds = 0;
                        int knownBondTotal = 0;
                        for (int k = 0; k < bondNos.size(); k++) {
                            if (M.get(j, k) == 1) {
                                if (solved.contains(k)) {
                                    knownBondTotal += bondOrder.get(k);
                                } else {
                                    unknownBonds++;
                                }
                            }
                        }

                        // have any bonds for this atom been solved?
                        if (unknownBonds == 0) {
                            solvedRow.add(j);
                            thisRun = 1;
                        } else {
                            if (knownBondTotal != 0) {
                                if (unknownBonds == freeValencies.get(j) - knownBondTotal) {

                                    // all remaining bonds must be single
                                    for (int k = 0; k < bondNos.size(); k++) {
                                        if (M.get(j, k) == 1 && solved.contains(k) == false) {
                                            bondOrder.set(k, 1);
                                            solved.add(k);
                                        }
                                    }
                                    solvedRow.add(j);
                                    thisRun = 1;
                                } else if (unknownBonds == 1) {

                                    // only one unsolved bond, so must equal remaining free valence
                                    for (int k = 0; k < bondNos.size(); k++) {
                                        if (M.get(j, k) == 1 && solved.contains(k) == false) {
                                            bondOrder.set(k, freeValencies.get(j) - knownBondTotal);
                                            solved.add(k);
                                        }
                                    }
                                    solvedRow.add(j);
                                    thisRun = 1;
                                }
                            }
                        }
                    }

                }
            }

            // If we can't solve any bonds from the information we have so far, there must be a choice to make.
            // Pick a bond that is yet to be solved and set it as a single bond.
            if (thisRun == 0) {
                int ring = 1;
                int j = 0;
                while (ring == 1 && j < bondNos.size()) {
                    int badChoice = 0;
                    if (solvedRow.contains(atomNos.indexOf(atomNoPairs.get(j)[0]))) {
                        badChoice = 1;
                    }
                    if (solvedRow.contains(atomNos.indexOf(atomNoPairs.get(j)[1]))) {
                        badChoice = 1;
                    }
                    if (bondOrder.get(j) == 0 && badChoice == 0) {
                        //                            javax.swing.JOptionPane.showMessageDialog(null, j);
                        bondOrder.set(j, 1);
                        ring = 0;
                        thisRun = 1;
                        solved.add(j);
                    }
                    j++;
                }
            }
        }
        if (solvedRow.size() != M.getRows()) {
            return false;
        } else {
            int errorFound = 0;
            for (int j = 0; j < atomNos.size(); j++) {
                int checker = 0;
                for (int k = 0; k < bondNos.size(); k++) {
                    checker += M.get(j, k) * bondOrder.get(k);
                }
                if (checker != freeValencies.get(j)) {
                    errorFound = 1;
                }
            }
            if (errorFound == 1) {
                return false;
            } else {
                return true;
            }
        }

    }

    /**
     * Sets if the calculation should be interrupted.
     *
     * @param interrupted true, if the calculation should be cancelled
     */
    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    /**
     * Returns if the next or running calculation should be interrupted.
     *
     * @return true or false
     */
    public boolean isInterrupted() {
        return this.interrupted;
    }
}
