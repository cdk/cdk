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
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Performs exhaustive fragmentation of molecules by breaking single non-ring, non-terminal bonds.
 * <p>
 * This fragmentation method avoids splitting bonds connected to single heavy atoms (non-terminal bonds).
 * By default:
 * - Fragments smaller than 6 atoms (excluding implicit hydrogen) are ignored.
 * - Fragments are returned unsaturated.
 * However, users can modify these settings.
 * <p>
 * <strong>Example Usage:</strong>
 * <pre>{@code
 * // By default, returns unsaturated fragments with a minimum size of 6 atoms
 * ExhaustiveFragmenter fragmenter = new ExhaustiveFragmenter();
 * SmilesParser smiParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
 * IAtomContainer mol = smiParser.parseSmiles("c1ccccc1C");  // Benzyl molecule
 * fragmenter.generateFragments(mol);
 *
 * // Retrieve SMILES representations of fragments
 * String[] smilesFragments = fragmenter.getFragments();
 *
 * // Retrieve AtomContainer representations of fragments
 * IAtomContainer[] atomContainerFragments = fragmenter.getFragmentsAsContainers();
 * }</pre>
 *
 * @author Rajarshi Guha
 * @cdk.module  fragment
 * @cdk.keyword fragment
 */
public class ExhaustiveFragmenter implements IFragmenter {

    /**
     * Specifies whether generated fragments should be saturated (hydrogens added) or unsaturated.
     */
    public enum Saturation {
        // Fragments will be returned in their saturated form (implicit hydrogen atoms added).
        SATURATED_FRAGMENTS,

        // Fragments will be returned in their unsaturated form (no additional hydrogen atoms).
        UNSATURATED_FRAGMENTS
    }

    private static final int DEFAULT_MIN_FRAG_SIZE = 6;
    private static final Saturation DEFAULT_SATURATION = Saturation.UNSATURATED_FRAGMENTS;

    private final Map<String, IAtomContainer> fragMap;
    private final SmilesGenerator smilesGenerator;
    private int maxTreeDepth = 31;
    private int minFragSize;
    private Saturation saturationSetting;
    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(ExhaustiveFragmenter.class);

    /**
     * Constructs an ExhaustiveFragmenter with the default settings:
     * - Minimum fragment size: 6 atoms
     * - Unsaturated fragments
     */
    public ExhaustiveFragmenter() {
        this(DEFAULT_MIN_FRAG_SIZE, DEFAULT_SATURATION);
    }

    /**
     * Constructs an ExhaustiveFragmenter with a user-defined minimum fragment size and saturation setting.
     *
     * @param minFragSize       Minimum number of atoms in a valid fragment.
     * @param saturationSetting Determines whether fragments should be saturated or unsaturated.
     */
    public ExhaustiveFragmenter(int minFragSize, Saturation saturationSetting) {
        this.minFragSize = minFragSize;
        this.saturationSetting = saturationSetting;
        fragMap = new HashMap<>();
        smilesGenerator = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
    }

    /**
     * Constructs an ExhaustiveFragmenter with a user-defined minimum fragment size.
     * Saturation defaults to unsaturated fragments.
     *
     * @param minFragSize Minimum number of atoms in a valid fragment.
     */
    public ExhaustiveFragmenter(int minFragSize) {
        this(minFragSize, DEFAULT_SATURATION);
    }

    /**
     * Constructs an ExhaustiveFragmenter with a user-defined saturation setting.
     * The minimum fragment size defaults to 6 atoms.
     *
     * @param saturationSetting Determines whether fragments should be saturated or unsaturated.
     */
    public ExhaustiveFragmenter(Saturation saturationSetting) {
        this(DEFAULT_MIN_FRAG_SIZE, saturationSetting);
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
     * @param saturationSetting The saturation mode for generated fragments.
     */
    public void setSaturationSetting(Saturation saturationSetting) {
        this.saturationSetting = saturationSetting;
    }

    /**
     * Sets the maximum number of bonds that can be simultaneously split.
     * Must be within the range `0 < maxTreeDepth < 32`.
     *
     * @param maxTreeDepth Maximum number of bonds that can be split at once.
     */
    public void setMaxTreeDepth(int maxTreeDepth) {
        this.maxTreeDepth = maxTreeDepth;
    }

    /**
     * Generates fragments for the given molecule.
     * <p>
     * Based on the saturation setting:
     * - **Unsaturated mode**: Fragments are returned without additional hydrogen atoms.
     * - **Saturated mode**: Hydrogen atoms are explicitly added to atoms where bonds are broken.
     * <p>
     * The generated fragments are stored internally and can be retrieved via:
     * - {@link #getFragments()} (SMILES representation)
     * - {@link #getFragmentsAsContainers()} (IAtomContainer representation)
     *
     * @param atomContainer The input molecule.
     * @throws CDKException If fragmentation encounters an error.
     */
    @Override
    public void generateFragments(IAtomContainer atomContainer) throws CDKException {
        fragMap.clear();
        if (this.saturationSetting == Saturation.UNSATURATED_FRAGMENTS) {
            runUnsaturated(atomContainer, maxTreeDepth);
        } else {
            runSaturated(atomContainer, maxTreeDepth);
        }
    }

    /**
     * Splits the molecule at all possible combinations of splittable bonds and adds implicit hydrogen atoms
     * to atoms that were originally involved in the split.
     *
     * @param atomContainer The molecule to be split.
     * @param maxTreeDepth  The maximum number of bond splits allowed per subset.
     * @throws CDKException If an error occurs during hydrogen addition or atom type perception.
     */
    private void runSaturated(IAtomContainer atomContainer, int maxTreeDepth) throws CDKException {

        // Return early if the molecule has fewer than 3 bonds (no meaningful splits possible)
        if (atomContainer.getBondCount() < 3) return;

        // Retrieve bonds that are eligible for splitting
        IBond[] splittableBonds = getSplitableBonds(atomContainer);
        int splittableBondsLength = splittableBonds.length;

        // If no splittable bonds are found, return early
        if (splittableBondsLength == 0) return;
        logger.debug("Got " + splittableBondsLength + " splittable bonds");

        // Compute the number of possible bond subsets (excluding the empty set): 2^n - 1
        int numberOfIterations = (1 << splittableBondsLength) - 1;

        // Store indices of splittable bonds for subset generation
        int[] splittableBondIndices = new int[splittableBondsLength];
        for (int i = 0; i < splittableBondsLength; i++) {
            splittableBondIndices[i] = splittableBonds[i].getIndex();
        }

        // Iterate over all non-empty subsets of splittable bonds
        for (int i = 1; i <= numberOfIterations; i++) {
            int[] subset = generateSubset(i, splittableBondIndices);
            int subsetSize = subset.length;

            // Skip subsets exceeding the allowed depth
            if (subsetSize > maxTreeDepth) {
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
                AtomContainerManipulator.clearAtomConfigurations(partContainer);

                // Reset implicit hydrogen count before recalculating
                for (IAtom atom : partContainer.atoms()) {
                    atom.setImplicitHydrogenCount(0);
                }

                // Configure atom types and add implicit hydrogens
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(partContainer);
                CDKHydrogenAdder.getInstance(partContainer.getBuilder()).addImplicitHydrogens(partContainer);

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
     * Splits the molecule at all possible combinations of splittable bonds without adding implicit hydrogens.
     *
     * @param atomContainer The molecule to be split.
     * @param maxTreeDepth  The maximum number of bond splits allowed per subset.
     * @throws CDKException If an error occurs during atom type perception.
     */
    private void runUnsaturated(IAtomContainer atomContainer, int maxTreeDepth) throws CDKException {

        // Return early if the molecule has fewer than 3 bonds (no meaningful splits possible)
        if (atomContainer.getBondCount() < 3) return;

        // Retrieve bonds that are eligible for splitting
        IBond[] splittableBonds = getSplitableBonds(atomContainer);
        int splittableBondsLength = splittableBonds.length;

        // If no splittable bonds are found, return early
        if (splittableBondsLength == 0) return;
        logger.debug("Got " + splittableBondsLength + " splittable bonds");

        // Compute the number of possible bond subsets (excluding the empty set): 2^n - 1
        int numberOfIterations = (1 << splittableBondsLength) - 1;

        // Store indices of splittable bonds for subset generation
        int[] splittableBondIndices = new int[splittableBondsLength];
        for (int i = 0; i < splittableBondsLength; i++) {
            splittableBondIndices[i] = splittableBonds[i].getIndex();
        }

        // Iterate over all non-empty subsets of splittable bonds
        for (int i = 1; i <= numberOfIterations; i++) {
            int[] subset = generateSubset(i, splittableBondIndices);
            int subsetSize = subset.length;

            // Skip subsets exceeding the allowed depth
            if (subsetSize > maxTreeDepth) {
                continue;
            }

            // Convert subset indices back to bond objects
            IBond[] bondsToSplit = new IBond[subsetSize];
            for (int j = 0; j < subsetSize; j++) {
                bondsToSplit[j] = atomContainer.getBond(subset[j]);
            }

            // TODO: Investigate whether copying is necessary. Consider using FragmentUtils.splitMolecule instead.
            IAtomContainer[] parts = splitBondsWithCopy(atomContainer, bondsToSplit);

            // Process each fragment
            for (IAtomContainer partContainer : parts) {
                // Configure atom types (no hydrogen addition in unsaturated mode)
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

    private IBond[] getSplitableBonds(IAtomContainer atomContainer) {
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

        // Iterate through each bit position (up to 32 bits).
        for (int j = 0; j < 32; j++) {
            // If the j-th bit in index is set, include nums[j] in the subset.
            if (((index >> j) & 1) == 1) {
                subset[subsetIndex++] = nums[j];
            }
        }

        return subset;
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
        return copiedAtom;
    }

    /**
     * Splits a molecule into multiple fragments by removing the specified bonds and making copies of the resulting fragments.
     *
     * @param mol          The molecule to be split.
     * @param bondsToSplit The bonds that should be removed to create separate fragments.
     * @return An array of copied molecular fragments resulting from the split.
     */
    private IAtomContainer[] splitBondsWithCopy(IAtomContainer mol, IBond[] bondsToSplit) {
        // Track visited atoms and bonds during traversal
        boolean[] visitedAtoms = new boolean[mol.getAtomCount()];
        boolean[] visitedBonds = new boolean[mol.getBondCount()];

        // Initialize visited markers to false
        Arrays.fill(visitedAtoms, false);
        Arrays.fill(visitedBonds, false);

        // The number of fragments is always the number of bonds removed + 1
        int numberOfFragments = bondsToSplit.length + 1;
        IAtomContainer[] fragments = new IAtomContainer[numberOfFragments];

        // Map atoms to the atoms they should be disconnected from
        Map<IAtom, List<IAtom>> atomsToSplit = new HashMap<>((int) Math.ceil((bondsToSplit.length * 2) / 0.75));
        for (IBond bond : bondsToSplit) {
            IAtom beg = bond.getBegin();
            IAtom end = bond.getEnd();
            atomsToSplit.computeIfAbsent(beg, k -> new ArrayList<>()).add(end);
        }

        // Stack to track starting atoms for fragment creation
        Stack<IAtom> startingAtoms = new Stack<>();

        // Start traversal from the first atom of the molecule
        startingAtoms.add(mol.getAtom(0));

        // Iterate to create each fragment while avoiding split bonds
        for (int i = 0; i < numberOfFragments; i++) {
            // Map to associate original atoms with their copied versions
            Map<IAtom, IAtom> origToCpyMap = new HashMap<>((int) Math.ceil(mol.getAtomCount() / 0.75));
            IAtomContainer fragmentContainer = mol.getBuilder().newInstance(IAtomContainer.class);

            // Stack for depth-first search (DFS) traversal
            Stack<IAtom> atomStack = new Stack<>();

            // Start DFS from the next available atom
            atomStack.add(startingAtoms.pop());

            // Copy the first atom and store the mapping
            IAtom firstAtom = atomStack.peek();
            IAtom atomCpy = copyAtom(firstAtom, fragmentContainer);
            origToCpyMap.put(firstAtom, atomCpy);

            while (!atomStack.isEmpty()) {
                // Retrieve the current atom and its copy
                IAtom atom = atomStack.pop();
                atomCpy = origToCpyMap.get(atom);
                visitedAtoms[atom.getIndex()] = true;

                // Iterate over neighboring atoms
                for (IAtom nbor : atom.neighbors()) {
                    IBond bond = mol.getBond(atom, nbor);
                    int bondIndex = bond.getIndex();

                    // Skip if the bond was already processed
                    if (visitedBonds[bondIndex]) {
                        continue;
                    }

                    // If the neighbor is not part of a split bond, copy it and create a bond
                    if (!atomsToSplit.containsKey(atom) || !atomsToSplit.get(atom).contains(nbor)) {
                        if (!visitedAtoms[nbor.getIndex()]) {
                            IAtom nborCpy = copyAtom(nbor, fragmentContainer);
                            fragmentContainer.newBond(atomCpy, nborCpy, bond.getOrder());
                            visitedBonds[bondIndex] = true;
                            atomStack.add(nbor);
                            origToCpyMap.put(nbor, nborCpy);
                            visitedAtoms[nbor.getIndex()] = true;
                        } else {
                            // If the neighbor was already copied, establish a bond to maintain cyclic structures
                            IAtom nborCpy = origToCpyMap.get(nbor);
                            if (nborCpy != null) {
                                fragmentContainer.newBond(atomCpy, nborCpy, bond.getOrder());
                                visitedBonds[bondIndex] = true;
                            }
                        }
                    } else {
                        // If the neighbor is part of a split bond, mark it as a starting atom for a new fragment
                        startingAtoms.add(nbor);
                    }
                }
            }
            // Store the created fragment
            fragments[i] = fragmentContainer;
        }
        return fragments;
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
