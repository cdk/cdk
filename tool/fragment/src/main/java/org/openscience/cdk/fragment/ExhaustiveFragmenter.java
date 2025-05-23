/* Copyright (C) 2010  Rajarshi Guha <rajarshi.guha@gmail.com>
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
package org.openscience.cdk.fragment;

import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Performs exhaustive fragmentation of molecules by breaking single non-ring, non-terminal bonds in all
 * combinations.
 * <p>
 * Non-terminal meaning bonds connected to more than one single heavy atom (non-terminal bonds).
 * By default:
 * <ul>
 *     <li>Fragments smaller than 6 atoms (excluding implicit hydrogen) don't get returned.</li>
 *     <li>Fragments are returned with open valences, where a bond has been split.</li>
 * </ul>
 * However, users can modify these settings.
 * <p>
 * <strong>Example Usage:</strong>
 * <pre>{@code
 * // By default, returns unsaturated fragments with a minimum size of 6 atoms
 * ExhaustiveFragmenter fragmenter = new ExhaustiveFragmenter();
 * SmilesParser smiParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
 * IAtomContainer mol = smiParser.parseSmiles("C1CCC(C1)C1=CC=CC=C1");  //  Cyclopentylbenzene molecule
 * fragmenter.generateFragments(mol);
 *
 * // Retrieve SMILES representations of fragments
 * String[] smilesFragments = fragmenter.getFragments();
 * // Results: ["C1CCCCC1", "C1=CC=CC=C1"]
 * //
 *
 * // Retrieve AtomContainer representations of fragments
 * IAtomContainer[] atomContainerFragments = fragmenter.getFragmentsAsContainers();
 * }</pre>
 *
 * @author Rajarshi Guha
 * @author Tom Weiß
 * @cdk.module  fragment
 * @cdk.keyword fragment
 */
public class ExhaustiveFragmenter implements IFragmenter {

    /**
     * Specifies whether generated fragments should be saturated (hydrogens added) or unsaturated.
     */
    public enum Saturation {
        /**
         * Fragments will be returned in their saturated form (implicit hydrogen atoms added).
         */
        HYDROGEN_SATURATED_FRAGMENTS,

        /**
         * Fragments will be saturated with R atoms.
         */
        REST_SATURATED_FRAGMENTS,

        /**
         * Fragments will be returned in their unsaturated form (no additional hydrogen atoms).
         */
        UNSATURATED_FRAGMENTS
    }

    private static final int DEFAULT_MIN_FRAG_SIZE = 6;
    private static final Saturation DEFAULT_SATURATION = Saturation.UNSATURATED_FRAGMENTS;

    private final Map<String, IAtomContainer> fragMap;
    private final SmilesGenerator smilesGenerator;
    private int exclusiveMaxTreeDepth = Integer.SIZE;
    private int minFragSize;
    private Saturation saturationSetting;
    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(ExhaustiveFragmenter.class);

    /**
     * Constructs an ExhaustiveFragmenter with the default settings:
     * <ul>
     *     <li>Minimum fragment size: 6 atoms</li>
     *     <li>Unsaturated fragments</li>
     * </ul>
     */
    public ExhaustiveFragmenter() {
        this(DEFAULT_MIN_FRAG_SIZE, DEFAULT_SATURATION);
    }

    /**
     * Constructs an ExhaustiveFragmenter with a user-defined minimum fragment size and saturation setting.
     *
     * @param minFragSize       minimum number of atoms in a valid fragment.
     * @param saturationSetting determines whether fragments should be saturated or unsaturated.
     */
    public ExhaustiveFragmenter(int minFragSize, Saturation saturationSetting) {
        this.minFragSize = minFragSize;
        this.saturationSetting = saturationSetting;
        this.fragMap = new HashMap<>();
        this.smilesGenerator = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
    }

    /**
     * Constructs an ExhaustiveFragmenter with a user-defined minimum fragment size.
     * Saturation defaults to unsaturated fragments.
     *
     * @param minFragSize minimum number of atoms in a valid fragment.
     */
    public ExhaustiveFragmenter(int minFragSize) {
        this(minFragSize, DEFAULT_SATURATION);
    }

    /**
     * Sets the minimum allowed fragment size.
     *
     * @param minFragSize Minimum number of atoms in a valid fragment.
     */
    public void setMinimumFragmentSize(int minFragSize) {
        this.minFragSize = minFragSize;
    }

    /**
     * Sets whether fragments should be saturated or unsaturated.
     *
     * @param saturationSetting the saturation mode for generated fragments.
     */
    public void setSaturationSetting(Saturation saturationSetting) {
        this.saturationSetting = saturationSetting;
    }

    /**
     * Sets the maximum number of bonds that can be simultaneously split. Must be within the range
     * {@code 0 < exclusiveMaxTreeDepth < 32}. This is the limit of the maximal possible bonds to split,
     * caused by the combinatorial explosion of fragments when dealing with larger molecules. Because Java
     * indexes its common data structures with int32's and this algorithm scales with 2^n, this limit is
     * strictly necessary.
     *
     * @param exclusiveMaxTreeDepth maximum number of bonds that can be split in one atom container.
     */
    public void setExclusiveMaxTreeDepth(int exclusiveMaxTreeDepth) {
        this.exclusiveMaxTreeDepth = exclusiveMaxTreeDepth;
    }

    /**
     * Generates fragments for the given molecule.
     * The generated fragments are stored internally and can be retrieved via:
     * - {@link #getFragments()} (SMILES representation)
     * - {@link #getFragmentsAsContainers()} (IAtomContainer representation)
     *
     * @param atomContainer the input molecule.
     * @throws CDKException if fragmentation encounters an error.
     */
    @Override
    public void generateFragments(IAtomContainer atomContainer) throws CDKException {
        this.fragMap.clear();
        run(atomContainer, this.exclusiveMaxTreeDepth);
    }

    /**
     * Splits the molecule at all possible combinations of splittable bonds and saturates the open valences of the
     * resulting fragments if the Saturation setting is turned on.
     * Only non-ring and non-terminal single bonds are considered for splitting.
     *
     * @param atomContainer the molecule to be split.
     * @param maxTreeDepth  the maximum number of bond splits allowed per subset of bonds.
     * @throws CDKException if an error occurs during hydrogen addition or atom type perception.
     */
    private void run(IAtomContainer atomContainer, int maxTreeDepth) throws CDKException {

        // Return early if the molecule has fewer than 3 bonds (no meaningful splits possible)
        if (atomContainer.getBondCount() < 3) return;

        // Retrieve bonds that are eligible for splitting
        IBond[] splittableBonds = getSplitableBonds(atomContainer);

        // If no splittable bonds are found, return early
        if (splittableBonds.length == 0) return;
        logger.debug("Got " + splittableBonds.length + " splittable bonds");

        // Compute the number of possible bond subsets (excluding the empty set): 2^n - 1
        int numberOfIterations = (1 << splittableBonds.length) - 1;

        // Store indices of splittable bonds for subset generation
        int[] splittableBondIndices = new int[splittableBonds.length];
        for (int i = 0; i < splittableBonds.length; i++) {
            splittableBondIndices[i] = splittableBonds[i].getIndex();
        }

        // Iterate over all non-empty subsets of splittable bonds
        for (int i = 1; i <= numberOfIterations; i++) {
            int[] subset = generateSubset(i, splittableBondIndices);
            int subsetSize = subset.length;

            // Skip subsets exceeding the allowed depth
            if (subsetSize > this.exclusiveMaxTreeDepth) {
                continue;
            }

            // Convert subset indices back to bond objects
            IBond[] bondsToSplit = new IBond[subsetSize];
            for (int j = 0; j < subsetSize; j++) {
                bondsToSplit[j] = atomContainer.getBond(subset[j]);
            }

            // Split the molecule and retrieve the resulting fragments
            IAtomContainer[] parts = splitBondsWithCopy(atomContainer, bondsToSplit);

            // Process each fragment
            for (IAtomContainer partContainer : parts) {

                // Configure atom types and add implicit hydrogens
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(partContainer);

                // Apply aromaticity perception (legacy operation)
                // TODO: Investigate for the current method to do this.
                Aromaticity.cdkLegacy().apply(partContainer);

                // Generate a unique SMILES representation of the fragment
                String tmpSmiles = smilesGenerator.create(partContainer);
                int numberOfAtoms = partContainer.getAtomCount();

                // Store the fragment if it meets the size requirement and is unique
                if (numberOfAtoms >= minFragSize && !fragMap.containsKey(tmpSmiles)) {
                    fragMap.put(tmpSmiles, partContainer);
                }
            }
        }
    }

    /**
     * Detects and returns the bonds, which will be split by an exhaustive fragmentation. This method is especially useful
     * to determine if it is even possible to split a specific molecule exhaustively. The number of fragments is 2^n - 1 with n
     * being the number of splittable bonds. Therefore, it is impossible to entirely split a molecule with more than 31 splittable Bonds.
     * To mitigate this one cna check this with this function, for example:
     * <pre>
     *     {@code
     *     ExhaustiveFragmenter exhFragmenter = new Exhaustive Fragmenter;
     *     if (exhFragmenter.getSplittableBonds(mol) > 31) {
     *         // handle the case, where it is impossible to entirely split the molecule
     *     }}
     * </pre>
     *
     * @param atomContainer the container which contains the molecule in question.
     * @return the bonds which would be split by the exhaustive fragmentation.
     */
    public IBond[] getSplitableBonds(IAtomContainer atomContainer) {
        // do ring detection
        SpanningTree spanningTree = new SpanningTree(atomContainer);
        IRingSet allRings = spanningTree.getAllRings();

        // find the splitable bonds
        ArrayList<IBond> splitableBonds = new ArrayList<>();

        for (IBond bond : atomContainer.bonds()) {
            boolean isInRing = false;
            boolean isTerminal = false;

            // lets see if it's in a ring
            IRingSet rings = allRings.getRings(bond);
            if (rings.getAtomContainerCount() != 0) isInRing = true;

            // lets see if it is a terminal bond
            for (IAtom atom : bond.atoms()) {
                if (atomContainer.getConnectedBondsCount(atom) == 1) {
                    isTerminal = true;
                    break;
                }
            }

            if (!(isInRing || isTerminal)) splitableBonds.add(bond);
        }
        return splitableBonds.toArray(new IBond[0]);
    }

    /**
     * Generates a subset from the given array `nums`, determined by the binary representation of `index`.
     * Each bit in `index` indicates whether the corresponding element in `nums` is included in the subset.
     * The order of elements does not matter (i.e., `[1, 2]` and `[2, 1]` are equivalent).
     *
     * <p>The total number of possible subsets is (2^n) - 1, where `n` is the length of `nums`.
     * Subsets are generated using bitwise operations, where each `1` bit in `index` selects
     * the corresponding element from `nums`.</p>
     *
     * <p>Example output for `nums = [1, 2, 3]`:</p>
     * <pre>
     *   index = 1  → [1]
     *   index = 2  → [2]
     *   index = 3  → [1, 2]
     *   index = 4  → [3]
     *   index = 5  → [1, 3]
     *   index = 6  → [2, 3]
     *   index = 7  → [1, 2, 3]
     * </pre>
     *
     * <p>Example bitwise selection for `index = 5` (`101` in binary):</p>
     * <pre>
     * index (binary)   nums    result
     *      1        →   1   →  [1]
     *      0        →   2
     *      1        →   3   →  [1, 3]
     * </pre>
     *
     * @param index An integer whose binary representation determines the subset elements.
     *              A `1` bit at position `j` means `nums[j]` is included.
     * @param nums  The array from which to generate subsets.
     *              Duplicate values in `nums` may result in duplicate subset entries.
     * @return      An array containing the subset corresponding to `index`.
     */
    private static int[] generateSubset(int index, int[] nums) {
        // Allocate subset array based on the number of 1-bits in index.
        int[] subset = new int[Integer.bitCount(index)];
        int subsetIndex = 0;

        // Iterate through each bit position (up to 31 bits).
        for (int j = 0; j < Integer.SIZE; j++) {
            // If the j-th bit in index is set, include nums[j] in the subset.
            if (((index >> j) & 1) == 1) {
                subset[subsetIndex++] = nums[j];
            }
        }

        return subset;
    }

    /**
     * Add pseudo ("R") atoms to an atom in a molecule.
     *
     * @param atom the atom to add the pseudo atoms to
     * @param rcount the number of pseudo atoms to add
     * @param mol the molecule the atom belongs to
     */
    private void addRAtoms(IAtom atom, int rcount, IAtomContainer mol) {
        for (int i = 0; i < rcount; i++) {
            IPseudoAtom tmpRAtom = atom.getBuilder().newInstance(IPseudoAtom.class, "R");
            tmpRAtom.setAttachPointNum(1);
            tmpRAtom.setImplicitHydrogenCount(0);
            mol.addAtom(tmpRAtom);
            mol.addBond(atom.getBuilder().newInstance(IBond.class, atom, tmpRAtom, IBond.Order.SINGLE));
        }
    }

    /**
     * Creates a copy of an atom and adds it to the specified atom container.
     *
     * @param originalAtom  The atom to be copied.
     * @param atomContainer The destination container where the copied atom will be added.
     * @return A new atom with the same properties as `originalAtom`, added to `atomContainer`.
     */
    private static IAtom copyAtom(IAtom originalAtom, IAtomContainer atomContainer) {
        IAtom copiedAtom = atomContainer.newAtom(originalAtom.getAtomicNumber(),
                originalAtom.getImplicitHydrogenCount());
        copiedAtom.setIsAromatic(originalAtom.isAromatic());
        copiedAtom.setValency(originalAtom.getValency());
        copiedAtom.setAtomTypeName(originalAtom.getAtomTypeName());
        copiedAtom.setFormalCharge(originalAtom.getFormalCharge());
        return copiedAtom;
    }

    /**
     * Splits a molecule into multiple fragments by removing the specified bonds and making copies of the resulting fragments.
     *
     * @param origMol      The molecule to be split.
     * @param bondsToSplit The bonds that should be removed to create separate fragments.
     * @return An array of copied molecular fragments resulting from the split.
     */
    private IAtomContainer[] splitBondsWithCopy(IAtomContainer origMol, IBond[] bondsToSplit) {
        Set<Set<IAtom>> splitBondAtomPairs = new HashSet<>();
        for (IBond bond : bondsToSplit) {
            Set<IAtom> pair = new HashSet<>(2);
            pair.add(bond.getAtom(0));
            pair.add(bond.getAtom(1));
            splitBondAtomPairs.add(pair);
        }

        boolean[] visitedOriginalAtoms = new boolean[origMol.getAtomCount()];
        List<IAtomContainer> fragmentList = new ArrayList<>(bondsToSplit.length + 1);

        for (int i = 0; i < origMol.getAtomCount(); i++) {
            IAtom currPotentialStartAtom = origMol.getAtom(i);
            if (!visitedOriginalAtoms[origMol.indexOf(currPotentialStartAtom)]) {
                IAtomContainer fragmentContainer = origMol.getBuilder().newInstance(IAtomContainer.class);
                Map<IAtom, IAtom> origToCpyMap = new HashMap<>();
                Stack<IAtom> dfsStack = new Stack<>();
                // Store split counts specific to the atoms in the fragment being built
                Map<IAtom, Integer> splitCountsCpyAtoms = new HashMap<>();

                dfsStack.push(currPotentialStartAtom);
                visitedOriginalAtoms[origMol.indexOf(currPotentialStartAtom)] = true;
                IAtom cpyStartAtom = copyAtom(currPotentialStartAtom, fragmentContainer);
                origToCpyMap.put(currPotentialStartAtom, cpyStartAtom);

                while (!dfsStack.isEmpty()) {
                    IAtom origCurrAtom = dfsStack.pop();
                    IAtom copiedCurrentAtom = origToCpyMap.get(origCurrAtom);

                    for (IBond origBond : origMol.getConnectedBondsList(origCurrAtom)) {
                        IAtom origNbor = origBond.getOther(origCurrAtom);
                        Set<IAtom> currBondPair = new HashSet<>(2);
                        currBondPair.add(origCurrAtom);
                        currBondPair.add(origNbor);
                        boolean isThisABondToSplit = splitBondAtomPairs.contains(currBondPair);

                        if (!isThisABondToSplit) {
                            if (!origToCpyMap.containsKey(origNbor)) {
                                visitedOriginalAtoms[origMol.indexOf(origNbor)] = true;
                                IAtom cpyNbor = copyAtom(origNbor, fragmentContainer);
                                origToCpyMap.put(origNbor, cpyNbor);
                                fragmentContainer.addBond(copiedCurrentAtom.getIndex(), cpyNbor.getIndex(),
                                        origBond.getOrder(), origBond.getStereo());
                                dfsStack.push(origNbor);
                            } else {
                                IAtom cpyNbor = origToCpyMap.get(origNbor);
                                if (fragmentContainer.getBond(copiedCurrentAtom, cpyNbor) == null) {
                                    fragmentContainer.addBond(copiedCurrentAtom.getIndex(), cpyNbor.getIndex(),
                                            origBond.getOrder(), origBond.getStereo());
                                    // Add bond only if not already present
                                }
                            }
                        } else {
                            // This bond is being cut. The origCurrAtom is part of the fragment being built.
                            // Increment the cleavage count for its corresponding copied atom.
                            splitCountsCpyAtoms.put(copiedCurrentAtom,
                                    splitCountsCpyAtoms.getOrDefault(copiedCurrentAtom, 0) + 1);
                        }
                    }
                }

                // Apply saturation logic based on the number of splitting counts for this fragment
                if (this.saturationSetting != Saturation.UNSATURATED_FRAGMENTS) {
                    for (Map.Entry<IAtom, Integer> entry : splitCountsCpyAtoms.entrySet()) {
                        IAtom atom = entry.getKey();
                        int bondsCutCount = entry.getValue();

                        switch (this.saturationSetting) {
                            case HYDROGEN_SATURATED_FRAGMENTS:
                                Integer currImplHCount = atom.getImplicitHydrogenCount();
                                int newImplHCount = (currImplHCount == null ? 0 : currImplHCount) + bondsCutCount;
                                atom.setImplicitHydrogenCount(newImplHCount);
                                break;
                            case REST_SATURATED_FRAGMENTS:
                                addRAtoms(atom, bondsCutCount, fragmentContainer);
                                break;
                        }
                    }
                }
                fragmentList.add(fragmentContainer);
            }
        }
        return fragmentList.toArray(new IAtomContainer[0]);
    }


    /**
     * Get the fragments generated as SMILES strings.
     *
     * @return a String[] of the fragments.
     */
    @Override
    public String[] getFragments() {
        return (new ArrayList<>(fragMap.keySet())).toArray(new String[0]);
    }

    /**
     * Get the fragments generated as {@link IAtomContainer} objects.
     *
     * @return a IAtomContainer[] of the fragments.
     */
    @Override
    public IAtomContainer[] getFragmentsAsContainers() {
        return (new ArrayList<>(fragMap.values())).toArray(new IAtomContainer[0]);
    }

}
