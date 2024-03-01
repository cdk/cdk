/*
 * Copyright (c) 2024 Sebastian Fritsch <>
 *                    Stefan Neumann <>
 *                    Jonas Schaub <jonas.schaub@uni-jena.de>
 *                    Christoph Steinbeck <christoph.steinbeck@uni-jena.de>
 *                    Achim Zielesny <achim.zielesny@w-hs.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.fragment;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

/**
 * Finds and extracts a molecule's functional groups in a purely rule-based
 * manner (it is not a classical functional group identification functionality
 * based on substructure matching!). This class implements Peter Ertl's
 * algorithm for the automated detection and extraction of functional groups in
 * organic molecules (<a href="https://doi.org/10.1186/s13321-017-0225-z">
 * [Ertl P. An algorithm to identify functional groups in organic molecules.
 * J Cheminform. 2017; 9:36.]</a>) and has been described in a scientific
 * publication (<a href="https://doi.org/10.1186/s13321-019-0361-8">[Fritsch,
 * S., Neumann, S., Schaub, J. et al. ErtlFunctionalGroupsFinder: automated
 * rule-based functional group detection with the Chemistry Development Kit
 * (CDK). J Cheminform. 2019; 11:37.]</a>).
 * <br/>
 * In brief, the algorithm iterates through all atoms in the input molecule and
 * marks hetero atoms and specific carbon atoms (i.a. those in non-aromatic
 * double or triple bonds etc.) as being part of a functional group. Connected
 * groups of marked atoms are extracted as individual functional groups,
 * together with their unmarked, "environmental" carbon atoms. These
 * environments can be important, e.g. to differentiate an alcohol from a
 * phenol, but are less important in other cases.
 * <br/>
 * To account for this, Ertl also devised a "generalization" scheme that
 * generalizes the functional group environments in a way that accounts for
 * their varying significance in different cases. Most environmental atoms are
 * exchanged with pseudo ("R") atoms there. All these functionalities are
 * available in FunctionalGroupsFinder. Additionally, only the marked atoms,
 * completely without their environments, can be extracted.
 * <br/>
 * To apply functional group detection to an input molecule, its atom types
 * need to be set and aromaticity needs to be detected beforehand:
 * <pre>{@code
 * //Prepare input
 * SmilesParser tmpSmiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
 * IAtomContainer tmpInputMol = tmpSmiPar.parseSmiles("C[C@@H]1CN(C[C@H](C)N1)C2=C(C(=C3C(=C2F)N(C=C(C3=O)C(=O)O)C4CC4)N)F"); //PubChem CID 5257
 * AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpInputMol);
 * Aromaticity tmpAromaticity = new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet());
 * tmpAromaticity.apply(tmpInputMol);
 * //Identify functional groups
 * FunctionalGroupsFinder tmpFGF = new FunctionalGroupsFinder(); //default: generalization turned on
 * List{@literal <}IAtomContainer{@literal >} tmpFunctionalGroupsList = tmpFGF.extractFunctionalGroups(tmpInputMol);
 * }</pre>
 * If you want to only identify functional groups in standardised, organic
 * structures, FunctionalGroupsFinder can be configured to only accept molecules
 * that do *not* contain any metal, metalloid, or pseudo (R) atoms or formal
 * charges.
 * <br/>
 * Also structures consisting of more than one unconnected component (e.g. ion
 * and counter-ion) are not accepted if(!) the  strict input restrictions are
 * turned on (they are turned off by default).
 * This can be done via a boolean parameter in a variant of the central
 * {@link #extract} method.
 * Please note that structural properties like formal charges and the others
 * mentioned above  are not expected to cause issues (exceptions) when processed
 * by this class, but they are not explicitly regarded by the Ertl algorithm and
 * hence this implementation, too. They might therefore cause unexpected
 * behaviour in functional group identification. For example, a formal charge
 * is not listed as a reason to mark a carbon atom and pseudo atoms are simply
 * ignored.
 * <br/>
 * To identify molecules that do not fulfill these constraints and should be
 * filtered or preprocessed/standardised, you can use CDK utilities like the
 * ConnectivityChecker class, utility methods in the Elements class, and query
 * IAtom instances for their formal charge. Pseudo atoms can be detected in
 * multiple ways, e.g. by checking for atomic numbers equal to 0 or checking
 * "instanceof IPseudoAtom".
 * <br/>
 * Note: this implementation is not thread-safe. Each parallel thread
 * should have its own instance of this class.
 *
 * @author Sebastian Fritsch
 * @author Jonas Schaub
 * @version 1.3
 */
public class FunctionalGroupsFinder {

    /**
     * Defines the level of detail environment
     */
    enum Environment {
        /**
         * Functional groups will only consist of atoms marked according to the
         * conditions defined by Ertl, environments will be completely ignored.
         */
        NONE,
        /**
         * Default mode including the generalization step.
         */
        GENERAL,
        /**
         * Skips the generalization step. Functional groups will keep their
         * full environment.
         */
        FULL;
    }

    /**
     * Defines whether an environmental carbon atom is aromatic or aliphatic.
     * Only for internal use for caching this info in the EnvironmentalC
     * instances (see private class below).
     */
    private enum EnvironmentalCType {
        /**
         * Aromatic environmental carbon.
         */
        C_AROMATIC,
        /**
         * Aliphatic environmental carbon.
         */
        C_ALIPHATIC;
    }

    /**
     * Describes one carbon atom in the environment of a marked atom. It can
     * either be aromatic or aliphatic and also contains a clone of its
     * connecting bond.
     */
    private static class EnvironmentalC {
        /**
         * Indicates whether carbon atom is aromatic or aliphatic.
         */
        private final EnvironmentalCType type;

        private final int bondIndex;

        private final IBond.Order bondOrder;

        /**
         * Stereo information of the bond connecting this environmental C
         * atom to the marked functional group atom.
         */
        private final IBond.Stereo bondStereo;

        /**
         * Flags of the bond connecting this environmental C atom to the marked
         * functional group atom. IChemObjecflags are properties defined by an
         * integer value (array position) and a boolean value.
         */
        private final boolean[] bondFlags;

        /**
         * Default constructor defining all fields. Order, stereo, and flags
         * are taken from the IBond object directly.
         *
         * @param aType aromatic or aliphatic
         * @param aConnectingBond bond instance connecting to the marked atom
         * @param anIndexInBond index of the atom in the connecting bond
         */
        EnvironmentalC(EnvironmentalCType aType, IBond aConnectingBond, int anIndexInBond) {
            this.type = aType;
            this.bondIndex = anIndexInBond;
            this.bondOrder = aConnectingBond.getOrder();
            this.bondStereo = aConnectingBond.getStereo();
            this.bondFlags = aConnectingBond.getFlags();
        }

        /**
         * Returns the type, i.e. whether this carbon atom is aromatic or
         * aliphatic.
         *
         * @return EnvironmentalCType enum constant
         */
        EnvironmentalCType getType() {
            return this.type;
        }

        /**
         * Method for translating this instance back into a "real" IAtom
         * instance when expanding the functional group environment,
         * transferring all the cached properties, except the type(!).
         *
         * @param aTargetAtom marked functional group atom
         * @param anEnvCAtom new carbon atom instance that should receive all
         *                   the cached properties except the type(!);
         *                   element, atom type "C" and implicit hydrogen
         *                   count = 0 should be set already; type can later
         *                   be set via .setIsAromatic(boolean);
         * @return new bond connecting marked FG atom and environment atom in
         *         the correct order and with the cached properties
         */
        IBond createBond(IAtom aTargetAtom, IAtom anEnvCAtom) {
            IBond tmpBond = aTargetAtom.getBuilder().newInstance(IBond.class);
            if (this.bondIndex == 0) {
                tmpBond.setAtoms(new IAtom[] {anEnvCAtom, aTargetAtom});
            }
            else {
                tmpBond.setAtoms(new IAtom[] {aTargetAtom, anEnvCAtom});
            }
            tmpBond.setOrder(this.bondOrder);
            tmpBond.setStereo(this.bondStereo);
            tmpBond.setFlags(this.bondFlags);
            return tmpBond;
        }
    }

    /**
     * Property name for marking carbonyl carbon atoms via IAtom properties.
     */
    private static final String CARBONYL_C_MARKER = "FGF-Carbonyl-C";

    /**
     * Environment mode setting, defining whether environments should be
     * generalized (default) or kept as whole.
     */
    private final Environment envEnvironment;

    /**
     * Encapsulate the state of the algorithm allows thread-safe calling.
     */
    private static final class State {

        private final Map<IAtom,IAtom> amap = new HashMap<>();

        private int[] hCounts;

        /**
         * Set for atoms marked as being part of a functional group, represented
         * by an internal index based on the atom count in the input molecule, cache(!).
         */
        private HashSet<Integer> markedAtomsCache;

        /**
         * HashMap for storing aromatic hetero-atom indices and whether they have
         * already been assigned to a larger functional group. If false, they form
         * single-atom FG by themselves, cache(!).
         * key: atom idx, value: isInGroup
         */
        private HashMap<Integer, Boolean> aromaticHeteroAtomIndicesToIsInGroupBoolMapCache;

        /**
         * HashMap for storing marked atom to connected environmental carbon atom
         * relations, cache(!).
         */
        private HashMap<IAtom, List<EnvironmentalC>> markedAtomToConnectedEnvCMapCache;


        /**
         * A saturated atom has only single (sigma) bonds.
         * @param atom the atom to test
         * @return the atom is saturated
         */
        private static boolean isSaturated(IAtom atom)
        {
            for (IBond bond : atom.bonds())
                if (bond.getOrder() != Order.SINGLE)
                    return false;
            return true;
        }

        /**
         * Add explicit hydrogen atoms to an atom in a molecule.
         *
         * @param anAtom the atom to add the explicit hydrogen atoms to
         * @param aNrOfHydrogenAtoms the number of explicit hydrogens atoms to add
         * @param aMolecule the molecule the atom belongs to
         */
        private void addHydrogens(IAtom anAtom, int aNrOfHydrogenAtoms, IAtomContainer aMolecule) {
            for (int i = 0; i < aNrOfHydrogenAtoms; i++) {
                IAtom tmpHydrogenAtom = anAtom.getBuilder().newInstance(IAtom.class, "H");
                tmpHydrogenAtom.setAtomTypeName("H");
                tmpHydrogenAtom.setImplicitHydrogenCount(0);
                aMolecule.addAtom(tmpHydrogenAtom);
                aMolecule.addBond(anAtom.getBuilder().newInstance(IBond.class, anAtom, tmpHydrogenAtom, Order.SINGLE));
            }
        }

        /**
         * Add pseudo ("R") atoms to an atom in a molecule.
         *
         * @param anAtom the atom to add the pseudo atoms to
         * @param aNrOfRAtoms the number of pseudo atoms to add
         * @param aMolecule the molecule the atom belongs to
         */
        private void addRAtoms(IAtom anAtom, int aNrOfRAtoms, IAtomContainer aMolecule) {
            for (int i = 0; i < aNrOfRAtoms; i++) {
                IPseudoAtom tmpRAtom = anAtom.getBuilder().newInstance(IPseudoAtom.class, "R");
                tmpRAtom.setAttachPointNum(1);
                tmpRAtom.setImplicitHydrogenCount(0);
                aMolecule.addAtom(tmpRAtom);
                aMolecule.addBond(anAtom.getBuilder().newInstance(IBond.class, anAtom, tmpRAtom, Order.SINGLE));
            }
        }

        /**
         * Checks whether the given atom is a pseudo atom. Very strict, any atom
         * whose atomic number is null or 0, whose symbol equals "R" or "*", or that
         * is an instance of an IPseudoAtom implementing class will be classified as
         * a pseudo atom.
         *
         * @param anAtom the atom to test
         * @return true if the given atom is identified as a pseudo (R) atom
         */
        private static boolean isPseudoAtom(IAtom anAtom) {
            Integer tmpAtomicNr = anAtom.getAtomicNumber();
            if (Objects.isNull(tmpAtomicNr)) {
                return true;
            }
            String tmpSymbol = anAtom.getSymbol();
            return tmpAtomicNr == IAtom.Wildcard ||
                    tmpSymbol.equals("R") ||
                    tmpSymbol.equals("*") ||
                    anAtom instanceof IPseudoAtom;
        }

        /**
         * Checks whether the given atom is a hetero-atom (i.e. non-carbon and
         * non-hydrogen). Pseudo (R) atoms will also return false.
         *
         * @param anAtom the atom to test
         * @return true if the given atom is neither a carbon nor a hydrogen or
         *         pseudo atom
         */
        private static boolean isHeteroatom(IAtom anAtom) {
            Integer tmpAtomicNr = anAtom.getAtomicNumber();
            if (Objects.isNull(tmpAtomicNr)) {
                return false;
            }
            int tmpAtomicNumberInt = tmpAtomicNr;
            return tmpAtomicNumberInt != IAtom.H && tmpAtomicNumberInt != IAtom.C
                    && !isPseudoAtom(anAtom);
        }

        /**
         * Checks whether the given atom is from an element in the organic subset,
         * i.e. not a metal or metalloid atom.
         * Pseudo (R) atoms will also return false.
         *
         * @param anAtom atom to check
         * @return true if the given atom is organic and not a metal or metalloid
         *         atom
         */
        private static boolean isNonmetal(IAtom anAtom) {
            Integer tmpAtomicNumber = anAtom.getAtomicNumber();
            if (Objects.isNull(tmpAtomicNumber)) {
                return false;
            }
            int tmpAtomicNumberInt = tmpAtomicNumber;
            return !Elements.isMetal(tmpAtomicNumberInt) && !Elements.isMetalloid(tmpAtomicNumberInt) && !isPseudoAtom(anAtom);
        }

        /**
         * Mark all atoms and store them in a set for further processing.
         *
         * @param mol molecule with atoms to mark
         */
        private void markAtoms(IAtomContainer mol) {

            hCounts = new int[mol.getAtomCount()];
            for (IAtom atom : mol.atoms()) {
                if (atom.getImplicitHydrogenCount() == null)
                    hCounts[atom.getIndex()] = 0;
                else
                    hCounts[atom.getIndex()] = atom.getImplicitHydrogenCount();
            }

            // store marked atoms
            markedAtomsCache = new HashSet<>((int) ((mol.getAtomCount() / 0.75f) + 2), 0.75f);
            // store aromatic heteroatoms
            aromaticHeteroAtomIndicesToIsInGroupBoolMapCache = new HashMap<>((int) ((mol.getAtomCount() / 0.75f) + 2), 0.75f);
            for (IAtom atom : mol.atoms()) {
                int idx = atom.getIndex();
                // skip atoms that were already marked in a previous iteration
                if (markedAtomsCache.contains(idx)) {
                    continue;
                }
                // skip aromatic atoms but add aromatic HETERO-atoms to map for later processing
                if (atom.isAromatic()) {
                    if (isHeteroatom(atom)) {
                        aromaticHeteroAtomIndicesToIsInGroupBoolMapCache.put(idx, false);
                    }
                    continue;
                }
                int tmpAtomicNr = atom.getAtomicNumber();
                // if C...
                if (tmpAtomicNr == IAtom.C) {
                    // to detect if for loop ran with or without marking the C atom
                    boolean tmpIsMarked = false;
                    // count for the number of connected O, N & S atoms to detect acetal carbons
                    int tmpConnectedONSatomsCounter = 0;
                    for (IBond bond : atom.bonds()) {
                        IAtom nbor = bond.getOther(atom);

                        // if connected to heteroatom or C in aliphatic double or triple bond... [CONDITIONS 2.1 & 2.2]
                        if (nbor.getAtomicNumber() != IAtom.H
                                && ((bond.getOrder() == Order.DOUBLE || bond.getOrder() == Order.TRIPLE)
                                && !bond.isAromatic())) {

                            // set the *connected* atom as marked (add() true if this set did not already contain the specified element)
                            markedAtomsCache.add(nbor.getIndex());
                            // set the *current* atom as marked and break out of connected atoms
                            tmpIsMarked = true;

                            // but check for carbonyl-C before break
                            if (nbor.getAtomicNumber() == IAtom.O
                                    && bond.getOrder() == Order.DOUBLE
                                    && atom.getBondCount() == 3) {
                                atom.setProperty(CARBONYL_C_MARKER, true);
                            }
                            // break out of connected atoms
                            break;
                        } else if ((nbor.getAtomicNumber() == IAtom.N
                                || nbor.getAtomicNumber() == IAtom.O
                                || nbor.getAtomicNumber() == IAtom.S)
                                && bond.getOrder() == Order.SINGLE) {
                            // if connected to O/N/S in single bond...
                            // if connected O/N/S is not aromatic...
                            if (!nbor.isAromatic()) {
                                // set the connected O/N/S atom as marked
                                markedAtomsCache.add(nbor.getIndex());

                                // if "acetal C" (2+ O/N/S in single bonds connected to sp3-C)... [CONDITION 2.3]
                                if (isSaturated(nbor)) {
                                    tmpConnectedONSatomsCounter++;
                                    if (tmpConnectedONSatomsCounter > 1 && atom.getBondCount() + hCounts[atom.getIndex()] == 4) {
                                        // set as marked and break out of connected atoms
                                        tmpIsMarked = true;
                                        break;
                                    }
                                }
                            }
                            // if part of 3-membered oxirane, aziridine, or thiirane ring... [CONDITION 2.4]
                            for (IBond bond2 : nbor.bonds()) {
                                if (bond2 == bond)
                                    continue;
                                IAtom nbor2 = bond2.getOther(nbor);
                                if (nbor2.getBond(atom) == null)
                                    continue;
                                // set connected atoms as marked
                                markedAtomsCache.add(nbor.getIndex());
                                markedAtomsCache.add(nbor2.getIndex());
                                // set current atom as marked and break out of connected atoms
                                tmpIsMarked = true;
                                break;
                            }
                        } // end of else if connected to O/N/S in single bond
                    } //end of for loop that iterates over all connected atoms of the carbon atom
                    if (tmpIsMarked) {
                        markedAtomsCache.add(idx);
                    }
                    // if none of the conditions 2.X apply, we have an unmarked C (not relevant here)
                } else if (tmpAtomicNr == IAtom.H) {
                    // if H...
                    for (IBond bond : atom.bonds()) {
                        IAtom nbor = bond.getOther(atom);
                        hCounts[nbor.getIndex()]++;
                    }
                } else if (isHeteroatom(atom)) {
                    // if heteroatom... (CONDITION 1)
                    markedAtomsCache.add(idx);
                } else {
                    //pseudo (R) atom, ignored
                }
            } //end of for loop that iterates over all atoms in the mol
        }

        /**
         * Partitions the marked atoms and their processed environments into
         * separate functional groups and builds atom containers for them as final
         * step before returning them. Transfers the respective atoms, bonds, single
         * electrons, and lone pairs from the source atom container to the new
         * functional group atom containers.
         *
         * @param mol molecule atom container to take atoms, bonds,
         *                         and electron objects from
         * @param fgroups array that maps atom indices (array positions)
         *                            to functional group indices that the atoms
         *                            belong to
         * @param aFunctionalGroupCount maximum functional group index (+1) to know
         *                              how many functional group atom containers
         *                              to build
         * @return list of partitioned functional group atom containers
         */
        private List<IAtomContainer> partitionIntoGroups(IAtomContainer mol, int[] fgroups, int aFunctionalGroupCount) {
            List<IAtomContainer> parts = new ArrayList<>(aFunctionalGroupCount);
            for (int i = 0; i < aFunctionalGroupCount; i++) {
                parts.add(mol.getBuilder().newInstance(IAtomContainer.class));
            }
            // atoms
            for (IAtom atom : mol.atoms()) {
                int fGroupIdx = fgroups[atom.getIndex()];
                if (fGroupIdx == -1)
                    continue;
                IAtomContainer part = parts.get(fGroupIdx);
                IAtom cpyAtom = part.newAtom(atom.getAtomicNumber(),
                                             hCounts[atom.getIndex()]);
                cpyAtom.setIsAromatic(atom.isAromatic());
                cpyAtom.setValency(atom.getValency());
                cpyAtom.setAtomTypeName(atom.getAtomTypeName());
                amap.put(atom, cpyAtom);
            }
            // bonds
            for (IBond tmpBond : mol.bonds()) {
                // check whether begin and end atom of the bond have been correctly assigned to the same FG
                IAtom beg = amap.get(tmpBond.getBegin());
                IAtom end = amap.get(tmpBond.getEnd());
                if (beg == null || end == null || beg.getContainer() != end.getContainer())
                    continue;
                beg.getContainer().newBond(beg, end, tmpBond.getOrder());
            }
            // single electrons
            for (ISingleElectron se : mol.singleElectrons()) {
                IAtom atom = amap.get(se.getAtom());
                if (!Objects.isNull(atom)) {
                    atom.getContainer().addSingleElectron(atom.getIndex());
                }
            }
            // lone pairs
            for (ILonePair lp : mol.lonePairs()) {
                IAtom atom = amap.get(lp.getAtom());
                if (!Objects.isNull(atom)) {
                    atom.getContainer().addLonePair(atom.getIndex());
                }
            }
            return parts;
        }

        /**
         * Searches the molecule for groups of connected marked atoms and extracts
         * each as a new functional group. The extraction process includes marked
         * atoms' "environments". Connected H's are captured implicitly.
         *
         * @param mol                    the molecule which contains the functional groups
         * @return number of functional groups
         */
        private int markGroups(int[] tmpAtomIdxToFGArray, IAtomContainer mol) {
            markedAtomToConnectedEnvCMapCache = new HashMap<>((int) ((mol.getAtomCount() / 0.75f) + 2), 0.75f);
            int funcGrpIdx = -1;
            Queue<IAtom> queue = new ArrayDeque<>();
            while (!markedAtomsCache.isEmpty()) {
                // search for another functional group
                funcGrpIdx++;
                // get next markedAtom as the starting node for the search
                // do a BFS from there
                queue.add(mol.getAtom(markedAtomsCache.iterator().next()));
                while (!queue.isEmpty()) {
                    IAtom atom = queue.poll();
                    // assert markedAtomsCache.contains(atom.getIndex());
                    markedAtomsCache.remove(atom.getIndex());
                    // add its index to the functional group
                    tmpAtomIdxToFGArray[atom.getIndex()] = funcGrpIdx;

                    // and take a look at the connected atoms
                    List<EnvironmentalC> environmentalCarbons = new ArrayList<>();
                    for (IBond bond : atom.bonds()) {
                        IAtom nbor = bond.getOther(atom);
                        // ignore already handled connected atoms
                        if (tmpAtomIdxToFGArray[nbor.getIndex()] >= 0)
                            continue;
                        // add connected marked atoms to queue
                        if (markedAtomsCache.contains(nbor.getIndex())) {
                            queue.add(nbor);
                            continue;
                        }

                        // add unmarked connected aromatic heteroatoms
                        if (isHeteroatom(nbor) && nbor.isAromatic()) {
                            tmpAtomIdxToFGArray[nbor.getIndex()] = funcGrpIdx;
                            // note that this aromatic heteroatom has been added to a group
                            aromaticHeteroAtomIndicesToIsInGroupBoolMapCache.put(nbor.getIndex(), true);
                        }
                        // add unmarked connected atoms to current marked atom's environment
                        if (nbor.getAtomicNumber() == IAtom.C) {
                            EnvironmentalCType carbonType;
                            if (nbor.isAromatic())
                                carbonType = EnvironmentalCType.C_AROMATIC;
                            else
                                carbonType = EnvironmentalCType.C_ALIPHATIC;
                            environmentalCarbons.add(new EnvironmentalC(
                                    carbonType,
                                    bond,
                                    bond.getBegin().equals(nbor) ? 0 : 1));
                        }

                    } //end of loop of connected atoms
                    markedAtomToConnectedEnvCMapCache.put(atom, environmentalCarbons);

                } // end of BFS

            } //markedAtoms is empty now

            // also create FG for lone aromatic heteroatoms, not connected to an FG yet.
            for (int tmpAtomIdx : aromaticHeteroAtomIndicesToIsInGroupBoolMapCache.keySet()) {
                if (!aromaticHeteroAtomIndicesToIsInGroupBoolMapCache.get(tmpAtomIdx)) {
                    tmpAtomIdxToFGArray[tmpAtomIdx] = ++funcGrpIdx;
                }
            }

            return funcGrpIdx+1;
        }

        /**
         * Generalizes the full environments of functional groups, according to the
         * Ertl generalization algorithm, providing a good balance between
         * preserving meaningful detail and generalization.
         *
         * @param parts  the list of functional groups including "environments"
         */
        private void expandGeneralizedEnvironments(List<IAtomContainer> parts) {
            Map<IAtom,IAtom> invMap = new HashMap<>();
            for (Map.Entry<IAtom,IAtom> e : amap.entrySet())
                invMap.put(e.getValue(), e.getKey());

            for (IAtomContainer part : parts) {
                int acount = part.getAtomCount();
                // pre-checking for special cases...
                if (part.getAtomCount() == 1) {
                    IAtom cpyAtom = part.getAtom(0);
                    IAtom orgAtom = invMap.get(cpyAtom);
                    List<EnvironmentalC> tmpEnvironment = markedAtomToConnectedEnvCMapCache.get(orgAtom);

                    if (!Objects.isNull(tmpEnvironment)) {
                        int tmpEnvCCount = tmpEnvironment.size();
                        // for H2N-C_env & HO-C_env -> do not replace H & C_env by R to differentiate primary/secondary/tertiary amine and alcohol vs. phenol
                        if ((cpyAtom.getAtomicNumber() == IAtom.O && tmpEnvCCount == 1)
                                || (cpyAtom.getAtomicNumber() == IAtom.N && tmpEnvCCount == 1)) {
                            expandEnvironment(orgAtom, part);
                            int hcount = cpyAtom.getImplicitHydrogenCount();
                            if (hcount != 0) {
                                addHydrogens(cpyAtom, hcount, part);
                                cpyAtom.setImplicitHydrogenCount(0);
                            }
                            continue;
                        }
                        // for HN-(C_env)-C_env & HS-C_env -> do not replace H by R! (only C_env!)
                        if ((cpyAtom.getAtomicNumber() == IAtom.N && tmpEnvCCount == 2)
                                || (cpyAtom.getAtomicNumber() == IAtom.S && tmpEnvCCount == 1)) {
                            int hcount = cpyAtom.getImplicitHydrogenCount();
                            if (hcount != 0) {
                                addHydrogens(cpyAtom, hcount, part);
                                cpyAtom.setImplicitHydrogenCount(0);
                            }
                            expandEnvironmentGeneralized(orgAtom, part);
                            continue;
                        }
                    } else if (isHeteroatom(cpyAtom)) {
                        // env is null and marked atoms is a hetero atom -> single aromatic heteroatom
                        int rcount = cpyAtom.getValency();
                        Integer hcount = cpyAtom.getImplicitHydrogenCount();
                        if (hcount != null) {
                            cpyAtom.setImplicitHydrogenCount(0);
                        }
                        String tmpAtomTypeName = cpyAtom.getAtomTypeName();
                        addRAtoms(cpyAtom, rcount, part);
                        continue;
                    }
                } // end of pre-check for special one-atom FG cases

                List<IAtom> atomToProcess = new ArrayList<>(part.getAtomCount());
                part.atoms().forEach(atomToProcess::add);
                // process individual functional group atoms...
                for (IAtom cpyAtom : atomToProcess) {
                    IAtom orgAtom = invMap.get(cpyAtom);
                    List<EnvironmentalC> tmpFGenvCs = markedAtomToConnectedEnvCMapCache.get(orgAtom);
                    if (tmpFGenvCs == null) {
                        if (hCounts[orgAtom.getIndex()] != 0) {
                            cpyAtom.setImplicitHydrogenCount(0);
                        }
                        int tmpRAtomCount = orgAtom.getValency() - 1;
                        addRAtoms(cpyAtom, tmpRAtomCount, part);
                    }
                    // processing carbons...
                    if (orgAtom.getAtomicNumber() == IAtom.C) {
                        if (Objects.isNull(orgAtom.getProperty(FunctionalGroupsFinder.CARBONYL_C_MARKER))) {
                            if (hCounts[orgAtom.getIndex()] != 0) {
                                cpyAtom.setImplicitHydrogenCount(0);
                            }
                            continue;
                        } else {
                            expandEnvironmentGeneralized(orgAtom, part);
                            continue;
                        }
                    } else { // processing heteroatoms...
                        expandEnvironmentGeneralized(orgAtom, part);
                        continue;
                    }
                }
            } //end of loop over given functional groups list
        }

        /**
         * Expands the full environments of functional groups, converted into atoms
         * and bonds.
         *
         * @param parts  the list of functional groups including
         *                               their "environments"
         */
        private void expandFullEnvironments(List<IAtomContainer> parts) {
            Map<IAtom,IAtom> invMap = new HashMap<>();
            for (Map.Entry<IAtom,IAtom> e : amap.entrySet())
                invMap.put(e.getValue(), e.getKey());

            for (IAtomContainer part : parts) {
                int acount = part.getAtomCount();
                for (int i = 0; i < acount; i++) {
                    IAtom cpyAtom = part.getAtom(i);
                    IAtom orgAtom = invMap.get(cpyAtom);

                    expandEnvironment(orgAtom, part);
                    int tmpImplicitHydrogenCount = cpyAtom.getImplicitHydrogenCount();
                    if (tmpImplicitHydrogenCount != 0) {
                        addHydrogens(cpyAtom, tmpImplicitHydrogenCount, part);
                        cpyAtom.setImplicitHydrogenCount(0);
                    }
                }
            }
        }

        /**
         * Expand the environment of one atom in a functional group. Takes all
         * environmental C atoms cached earlier and re-adds them to the atom as
         * environment.
         *
         * @param orgAtom the atom whose environment to expand
         * @param part     the functional group container that the atom is part of
         */
        private void expandEnvironment(IAtom orgAtom, IAtomContainer part) {
            List<EnvironmentalC> tmpEnvCAtomsList = markedAtomToConnectedEnvCMapCache.get(orgAtom);
            IAtom cpyAtom = (IAtom) amap.get(orgAtom);

            if (Objects.isNull(tmpEnvCAtomsList) || tmpEnvCAtomsList.isEmpty()) {
                return;
            }
            int tmpAromaticCAtomCount = 0;
            int tmpAliphaticCAtomCount = 0;
            for (EnvironmentalC tmpEnvCAtom : tmpEnvCAtomsList) {
                IAtom tmpCAtom = part.newAtom(IAtom.C);
                tmpCAtom.setAtomTypeName("C");
                tmpCAtom.setImplicitHydrogenCount(0);
                if (tmpEnvCAtom.getType() == EnvironmentalCType.C_AROMATIC) {
                    tmpCAtom.setIsAromatic(true);
                    tmpAromaticCAtomCount++;
                } else {
                    tmpAliphaticCAtomCount++;
                }
                IBond tmpBond = tmpEnvCAtom.createBond(cpyAtom, tmpCAtom);
                part.addAtom(tmpCAtom);
                part.addBond(tmpBond);
            }
        }

        /**
         * Expand the generalized environment of marked heteroatoms and carbonyl-Cs
         * in a functional group. Takes all environmental C atoms cached earlier
         * and re-adds them to the atom as environment. Note: only call this on
         * marked heteroatoms / carbonyl-C's!
         *
         * @param orgAtom the atom whose environment to expand
         * @param cpyPart the functional group container that the atom is part of
         */
        private void expandEnvironmentGeneralized(IAtom orgAtom, IAtomContainer cpyPart) {
            List<EnvironmentalC> tmpEnvironment = markedAtomToConnectedEnvCMapCache.get(orgAtom);
            IAtom cpyAtom = (IAtom) amap.get(orgAtom);
            if (Objects.isNull(tmpEnvironment)) {
                return;
            }
            int tmpRAtomCount = tmpEnvironment.size();
            int tmpRAtomsForCCount = tmpRAtomCount;
            if (cpyAtom.getAtomicNumber() == IAtom.O && cpyAtom.getImplicitHydrogenCount() == 1) {
                addHydrogens(cpyAtom, 1, cpyPart);
                cpyAtom.setImplicitHydrogenCount(0);
            } else if (isHeteroatom(cpyAtom)) {
                tmpRAtomCount += cpyAtom.getImplicitHydrogenCount();
            }
            addRAtoms(cpyAtom, tmpRAtomCount, cpyPart);
            if (cpyAtom.getImplicitHydrogenCount() != 0) {
                cpyAtom.setImplicitHydrogenCount(0);
            }
        }
    }

    /**
     * Constructor for FunctionalGroupsFinder that allows setting the treatment
     * of environments in the identified functional groups.
     *
     * @param anEnvEnvironment mode for treating functional group environments
     */
    FunctionalGroupsFinder(Environment anEnvEnvironment) {
        Objects.requireNonNull(anEnvEnvironment, "Given environment mode cannot be null.");
        this.envEnvironment = anEnvEnvironment;
    }

    /**
     * Constructs a new FunctionalGroupsFinder instance with generalization
     * of returned functional groups turned ON.
     *
     * @return new FunctionalGroupsFinder instance that generalizes returned
     *         functional groups
     */
    public static FunctionalGroupsFinder withGeneralEnvironment() {
        return new FunctionalGroupsFinder(Environment.GENERAL);
    }

    /**
     * Constructs a new FunctionalGroupsFinder instance with generalization of
     * returned functional groups turned OFF.
     * The FG will have their full environments.
     *
     * @return new FunctionalGroupsFinder instance that does NOT generalize
     *         returned functional groups
     */
    public static FunctionalGroupsFinder withFullEnvironment() {
        return new FunctionalGroupsFinder(Environment.FULL);
    }

    /**
     * Constructs a new FunctionalGroupsFinder instance that extracts only the
     * marked atoms of the functional groups, no attached environmental atoms.
     *
     * @return new FunctionalGroupsFinder instance that extracts only marked
     *         atoms
     */
    public static FunctionalGroupsFinder withNoEnvironment() {
        return new FunctionalGroupsFinder(Environment.NONE);
    }

    /**
     * Find all functional groups in a molecule. The input atom container
     * instance is cloned before processing to leave the input container intact.
     * The strict input restrictions (no charged atoms, pseudo atoms, metals,
     * metalloids or unconnected components) do not apply by default. They can
     * be turned on again in another variant of this method below.
     *
     * @param aMolecule the molecule to identify functional groups in
     * @throws IllegalArgumentException if the input molecule was not
     *                                  preprocessed correctly, i.e. implicit
     *                                  hydrogen counts are unset
     * @return a list with all functional groups found in the molecule
     */
    public List<IAtomContainer> extract(IAtomContainer aMolecule) {
        return this.extract(aMolecule, false);
    }

    /**
     * Find all functional groups in a molecule.
     *
     * @param mol the molecule to identify functional groups in
     * @throws IllegalArgumentException if the input molecule was not preprocessed correctly, i.e. implicit hydrogen
     *                                  counts are unset; or thrown if the strict input restrictions are turned on and
     *                                  the given molecule does not fulfill them
     * @return the number of functional groups
     */
    public int find(int[] funGroups, IAtomContainer mol) {
        if (funGroups.length < mol.getAtomCount())
            throw new IllegalArgumentException("Not enough space allocated in: funGroups!");
        State state = new State();
        state.markAtoms(mol);
        Arrays.fill(funGroups, -1);
        return state.markGroups(funGroups, mol);
    }

    /**
     * Find all functional groups in a molecule.
     *
     * @param mol the molecule to identify functional groups in
     * @param anAreInputRestrictionsApplied if true, the input must consist of one connected structure and must not
     *                                      contain charged atoms, pseudo atoms, metals or metalloids; a specific IllegalArgumentException will
     *                                      be thrown otherwise
     * @throws IllegalArgumentException if the input molecule was not preprocessed correctly, i.e. implicit hydrogen
     *                                  counts are unset; or thrown if the strict input restrictions are turned on and
     *                                  the given molecule does not fulfill them
     * @return a list with all functional groups found in the molecule
     */
    public List<IAtomContainer> extract(IAtomContainer mol, boolean anAreInputRestrictionsApplied) {
        if (anAreInputRestrictionsApplied) {
            // throws IllegalArgumentException if constraints are not met
            // only done now because adjacency list cache is needed in the method
            FunctionalGroupsFinder.checkConstraints(mol);
        }
        State state = new State();
        state.markAtoms(mol);
        int[] funGroups = new int[mol.getAtomCount()];
        Arrays.fill(funGroups, -1);
        int nFunGroups = state.markGroups(funGroups, mol);
        List<IAtomContainer> parts = state.partitionIntoGroups(mol, funGroups, nFunGroups);
        // handle environment
        if (this.envEnvironment == Environment.GENERAL) {
            state.expandGeneralizedEnvironments(parts);
        } else if (this.envEnvironment == Environment.FULL) {
            state.expandFullEnvironments(parts);
        }
        return parts;
    }

    /**
     * Checks whether the given molecule represented by an atom container can be
     * passed on to the FunctionalGroupsFinder.extractFunctionalGroups() method
     * without problems even if(!) the strict input restrictions are turned on
     * (turned off by default).
     * <br>This method will return false if the molecule contains any metal,
     * metalloid, pseudo, or charged atoms or consists of multiple unconnected
     * parts ('ConnectivityChecker.isConnected(aMolecule)' must be 'true').
     * Some of these issues can be solved by respective standardisation
     * routines.
     *
     * @param aMolecule the molecule to check
     * @return true if the given molecule is a valid parameter for
     *         FunctionalGroupsFinder.extractFunctionalGroups()
     *         method if(!) the input restrictions are turned on (turned off
     *         by default)
     * @throws NullPointerException if parameter is 'null'
     */
    public static boolean isValidInputMoleculeIfRestrictionsAreTurnedOn(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "given molecule is null");
        try {
            EdgeToBondMap tmpBondMap = EdgeToBondMap.withSpaceFor(aMolecule);
            //throws IllegalArgumentException if a bond was found which contained atoms not in the molecule
            int[][] tmpAdjList = GraphUtil.toAdjList(aMolecule);
            //throws specific IllegalArgumentException if one of the constraints is not met
            FunctionalGroupsFinder.checkConstraints(aMolecule);
            return true;
        } catch (IllegalArgumentException anException) {
            return false;
        }
    }

    /**
     * Checks input molecule for charged atoms, metal or metalloid atoms, and
     * whether it consists of more than one unconnected structures. The molecule
     * may be empty but not null. If one of the cases applies, an
     * IllegalArgumentException is thrown with a specific error message.
     * Given as static method here because it is used by static public utility
     * methods (developer's note).
     *
     * @param aMolecule the molecule to check
     * @throws IllegalArgumentException if one of the constraints is not met
     * @throws NullPointerException if a parameter is 'null'
     */
    private static void checkConstraints(IAtomContainer aMolecule) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(aMolecule, "given molecule is null");
        for (IAtom tmpAtom : aMolecule.atoms()) {
            if (tmpAtom.getFormalCharge() != null && tmpAtom.getFormalCharge() != 0) {
                throw new IllegalArgumentException("Input molecule must not contain any formal charges.");
            }
            if (!State.isNonmetal(tmpAtom)) {
                throw new IllegalArgumentException("Input molecule must not contain metal, metalloid, or pseudo atoms.");
            }
        }
        if (!ConnectivityChecker.isConnected(aMolecule)) {
            throw new IllegalArgumentException("Input molecule must consist of only a single connected structure.");
        }
    }
}
