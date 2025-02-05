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
 * Generate fragments exhaustively.
 * <p>
 * This fragmentation scheme simply breaks single non-ring bonds. By default,
 * fragments smaller than 6 atoms (without implicit hydrogen) in size are not
 * considered and the returned fragments are not saturated, but this can be changed by the user.
 * Side chains are retained.
 *
 * <p>Example Usage</p>
 *
 * <pre>{@code
 * ExhaustiveFragmenter fragmenter = new ExhaustiveFragmenter(); // per default this returns unsaturated fragments with a minimum size of 6
 * SmilesParser smiParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
 * IAtomContainer mol = smiParser.parseSmiles(c1ccccc1CC(N)C(=O)O);
 * fragmenter.generateFragments(mol);
 * // if you want the SMILES representation of the fragments
 * String[] smilesFragments = fragmenter.getFragments();
 * // if you want the Atom containers
 * IAtomContainer[] atomContainerFragments = fragmenter.getFragmentsAsContainers();
 * }</pre>
 *
 * @author Rajarshi Guha
 * @cdk.module  fragment
 * @cdk.githash
 * @cdk.keyword fragment
 */
public class ExhaustiveFragmenter implements IFragmenter {

    /**
     * Defines the saturation of the returned fragments.
     */
    public enum Saturation {
        /**
         * Fragments will get returned saturated.
         */
        SATURATED_FRAGMENTS,

        /**
         * Fragments will get returned unsaturated.
         */
        UNSATURATED_FRAGMENTS
    }

    private static final int DEFAULT_MIN_FRAG_SIZE = 6;
    private static final Saturation DEFAULT_SATURATION = Saturation.UNSATURATED_FRAGMENTS;
    private static final int maxTreeDepth = 31;

    final Map<String, IAtomContainer> fragMap;
    final SmilesGenerator smilesGenerator;
    int minFragSize;
    Saturation saturationSetting;
    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(ExhaustiveFragmenter.class);

    /**
     * Instantiate fragmenter with default minimum fragment size and unsaturated fragments.
     */
    public ExhaustiveFragmenter() {
        this(DEFAULT_MIN_FRAG_SIZE, DEFAULT_SATURATION);
    }

    /**
     * Instantiate fragmenter with user specified minimum fragment size.
     *
     * @param minFragSize the minimum fragment size desired.
     * @param saturationSetting setting to specify if the returned fragments should be saturated or not.
     */
    public ExhaustiveFragmenter(int minFragSize, Saturation saturationSetting) {
        this.minFragSize = minFragSize;
        this.saturationSetting = saturationSetting;
        fragMap = new HashMap<>();
        smilesGenerator = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);
    }

    /**
     * Instantiate fragmenter with user specified minimum fragment size and default saturation (saturated fragments).
     *
     * @param minFragSize the minimum fragment size desired.
     */
    public ExhaustiveFragmenter(int minFragSize) {
        this(minFragSize, DEFAULT_SATURATION);
    }

    /**
     * Instantiate fragmenter with default minimum fragment size and user specified saturation setting.
     *
     * @param saturationSetting setting to specify if the returned fragments should be saturated or not.
     */
    public ExhaustiveFragmenter(Saturation saturationSetting) {
        this(DEFAULT_MIN_FRAG_SIZE, saturationSetting);
    }

    /**
     * Set the minimum fragment size.
     *
     * @param minFragSize the smallest size fragment that will be returned
     */
    public void setMinimumFragmentSize(int minFragSize) {
        this.minFragSize = minFragSize;
    }

    /**
     * Set the saturation setting of the returned fragments.
     *
     * @param saturationSetting setting to specify if the returned fragments should be saturated or not.
     */
    public void setSaturationSetting(Saturation saturationSetting) {
        this.saturationSetting = saturationSetting;
    }

    /**
     * Generate fragments for the input molecule.
     *
     * @param atomContainer The input molecule.
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
     * Splits the `atomContainer` and adds Hydrogen atoms to the atoms that get splitted.
     * @param atomContainer molecule to split
     * @throws CDKException
     */
    private void runSaturated(IAtomContainer atomContainer, int maxTreeDepth) throws CDKException {

        if (atomContainer.getBondCount() < 3) return;
        IBond[] splittableBonds = getSplitableBonds(atomContainer);
        int splittableBondsLength = splittableBonds.length;
        if (splittableBondsLength == 0) return;
        logger.debug("Got " + splittableBondsLength + " splittable bonds");

        // If we want to check all unique combinations of splittings we calculate the power set of the splittable bonds.
        // which is 2^n and without considering the empty set we can say it is 2^n - 1.
        // example:
        // if we have a set of splittable bonds here represented as numbers {1, 2, 3}, we can describe all unique
        // subsets as follows:
        // {1}
        // {2}
        // {3}
        // {1,2}
        // {1,3}
        // {2,3}
        // {1,2,3}
        int numberOfIterations = (1 << splittableBondsLength) - 1;

        int[] splittableBondIndices = new int[splittableBondsLength];
        for (int i = 0; i < splittableBondsLength; i++) {
            splittableBondIndices[i] = splittableBonds[i].getIndex();
        }
        for (int i = 1; i <= numberOfIterations; i ++){
            int[] subset = generateSubset(i, splittableBondIndices);
            int subsetSize = subset.length;
            if (subsetSize > maxTreeDepth) {
                continue;
            }
            IBond[] bondsToSplit = new IBond[subsetSize];
            for (int j = 0; j < subsetSize; j++) {
                bondsToSplit[j] = atomContainer.getBond(subset[j]);
            }
            IAtomContainer[] parts = splitBondsWithCopy(atomContainer, bondsToSplit);
            for (IAtomContainer partContainer : parts) {
                AtomContainerManipulator.clearAtomConfigurations(partContainer);
                for (IAtom atom : partContainer.atoms()) {
                    atom.setImplicitHydrogenCount(0);
                }
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(partContainer);
                CDKHydrogenAdder.getInstance(partContainer.getBuilder()).addImplicitHydrogens(partContainer);
                // todo: find the appropriate replacement for the legacy operation
                Aromaticity.cdkLegacy().apply(partContainer);
                String tmpSmiles = smilesGenerator.create(partContainer);
                int numberOfAtoms = partContainer.getAtomCount();
                if (numberOfAtoms >= minFragSize && !fragMap.containsKey(tmpSmiles)) {
                    fragMap.put(tmpSmiles, partContainer);
                }
            }

        }
    }

    private void runUnsaturated(IAtomContainer atomContainer, int maxTreeDepth) throws CDKException {

        if (atomContainer.getBondCount() < 3) return;
        IBond[] splittableBonds = getSplitableBonds(atomContainer);
        int splittableBondsLength = splittableBonds.length;
        if (splittableBondsLength == 0) return;
        logger.debug("Got " + splittableBondsLength + " splittable bonds");

        // If we want to check all unique combinations of splittings we calculate the power set of the splittable bonds.
        // which is 2^n and without considering the empty set we can say it is 2^n - 1.
        // example:
        // if we have a set of splittable bonds here represented as numbers {1, 2, 3}, we can describe all unique
        // subsets as follows:
        // {1}
        // {2}
        // {3}
        // {1,2}
        // {1,3}
        // {2,3}
        // {1,2,3}
        int numberOfIterations = (1 << splittableBondsLength) - 1;

        int[] splittableBondIndices = new int[splittableBondsLength];
        for (int i = 0; i < splittableBondsLength; i++) {
            splittableBondIndices[i] = splittableBonds[i].getIndex();
        }
        // we start from one to disregard the empty set from generateSubset
        for (int i = 1; i <= numberOfIterations; i ++){
            int[] subset = generateSubset(i, splittableBondIndices);
            int subsetSize = subset.length;
            if (subsetSize > maxTreeDepth) {
                continue;
            }
            IBond[] bondsToSplit = new IBond[subsetSize];
            for (int j = 0; j < subsetSize; j++) {
                bondsToSplit[j] = atomContainer.getBond(subset[j]);
            }
            // TODO: is a copy here really needed what do the other fragmenters use or return ? Otherwise either make a
            // function that doesn't copy or find a way to use the existing splitMolecule from FragmentUtils.
            IAtomContainer[] parts = splitBondsWithCopy(atomContainer, bondsToSplit);
            for (IAtomContainer partContainer : parts) {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(partContainer);
                // todo: find the appropriate replacement for the legacy operation
                Aromaticity.cdkLegacy().apply(partContainer);
                String tmpSmiles = smilesGenerator.create(partContainer);
                int numberOfAtoms = partContainer.getAtomCount();
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
     * Generates a subset of the numbers given in `nums`, determined by the binary representation of the
     * provided `index`.Each bit in the binary value of the `index` represents whether the corresponding element
     * in the `nums` array is included in the subset. The order of the elements does not matter
     * (i.e., [1, 2] and [2, 1] are considered identical).
     * <p>
     * The total number of possible subsets is (2^n) - 1, where n is the length of the `nums` array.
     * The subsets are generated based on bit manipulation, and the order of subsets may vary depending on
     * the internal bit shifts.
     * <p>
     * Example output for nums = [1, 2, 3] (2^3 - 1 = 7):
     *   [1]        for index = 1
     *   [2]        for index = 2
     *   [1, 2]     for index = 3
     *   [3]        for index = 4
     *   [1, 3]     for index = 5
     *   [2, 3]     for index = 6
     *   [1, 2, 3]  for index = 7
     * <p>
     * It works like follows:
     * index is here represented as binary and each place where the binary representation has a one results in the
     * respective place of the `nums` array being returned.
     * index (5):       nums:      result:
     *      1    -->     1          1
     *      0            2                  ---> [1, 3]
     *      1    -->     3          3
     *
     *
     *
     * @param index The index, represented as an integer, where each bit corresponds to whether an element in
     *              `nums` should be included in the subset. A bit value of `1` means the corresponding element
     *              is included, and `0` means it is not.
     * @param nums  An array of integers from which to generate the subset. The presence of duplicate values in
     *              `nums` will not result in an exception, but may lead to repeated values in the generated subsets.
     * @return      An array containing the subset corresponding to the binary representation of the provided `index`.
     * @author Tom Weiß
     */
    private static int[] generateSubset(int index, int[] nums) {

        // Create a new array to hold the subset, size based on the number of 1-bits in the index.
        int[] subset = new int[Integer.bitCount(index)];
        int subsetIndex = 0;

        // Iterate through each bit in the binary representation of the index.
        for (int j = 0; j < 32; j++) {
            // Check if the current bit (at position 'j') is set to 1.
            if (((index >> j) & 1) == 1) {
                // If the bit is set, add the corresponding number from nums to the subset.
                subset[subsetIndex] = nums[j];
                subsetIndex++;
            }
        }

        // Return the generated subset.
        return subset;
    }

    /**
     * Copies an atom into a new atom container.
     * @param originalAtom the atom to be copied
     * @param atomContainer the destination atom container
     * @return the copy of the atom
     */
    private static IAtom copyAtom(IAtom originalAtom, IAtomContainer atomContainer) {
        IAtom cpyAtom = atomContainer.newAtom(originalAtom.getAtomicNumber(),
                originalAtom.getImplicitHydrogenCount());
        cpyAtom.setIsAromatic(originalAtom.isAromatic());
        cpyAtom.setValency(originalAtom.getValency());
        cpyAtom.setAtomTypeName(originalAtom.getAtomTypeName());
        return cpyAtom;
    }

    /**
     * Split a molecule on all specified bonds by making a copy of the fragments.
     * @param mol the molecule to split.
     * @param bondsToSplit the bonds that should be removed.
     * @return the resulting copied fragments.
     */
    private IAtomContainer[] splitBondsWithCopy(IAtomContainer mol, IBond[] bondsToSplit) {
        // keep track of visited atoms and bonds
        boolean[] visitedAtoms = new boolean[mol.getAtomCount()];
        boolean[] visitedBonds = new boolean[mol.getBondCount()];
        // set all values of already visited atoms and bonds to false
        Arrays.fill(visitedAtoms, false);
        Arrays.fill(visitedBonds, false);
        // the number of fragments is always the number of splits + 1
        // example: 1 split results in 2 fragments, 2 splits in 3 and so on
        int numberOfFragments = bondsToSplit.length + 1;
        IAtomContainer[] fragments = new IAtomContainer[numberOfFragments];
        Map<IAtom, List<IAtom>> atomsToSplit = new HashMap<>((int) Math.ceil((bondsToSplit.length * 2) / 0.75));
        for (IBond bond : bondsToSplit) {
            IAtom beg = bond.getBegin();
            IAtom end = bond.getEnd();
            if (atomsToSplit.containsKey(beg)) {
                atomsToSplit.get(beg).add(end);
            } else {
                List<IAtom> endList = new ArrayList<>();
                endList.add(end);
                atomsToSplit.put(beg, endList);
            }
        }
        // stack to keep track of the next starting atom for the next fragment. If a bond to split is
        // noticed the atom of the next fragment gets added here
        Stack<IAtom> startingAtoms = new Stack<>();
        // start at an arbitrary starting atom
        startingAtoms.add(mol.getAtom(0));
        // for each fragment we iterate through the molecule, except for the bonds that we want to split
        for (int i = 0; i < numberOfFragments; i++) {
            // map to keep track of the original atoms and the copies thereof
            Map<IAtom, IAtom> origToCpyMap = new HashMap<>((int) Math.ceil(mol.getAtomCount() / 0.75));
            IAtomContainer fragmentContainer = mol.getBuilder().newInstance(IAtomContainer.class);
            // a stack to make a DFS through the molecule
            Stack<IAtom> atomStack = new Stack<>();

            // we start iterating at the most recent starting atom
            atomStack.add(startingAtoms.pop());

            // we need to copy the first atom separately
            IAtom firstAtom = atomStack.peek();
            IAtom atomCpy = copyAtom(firstAtom, fragmentContainer);
            origToCpyMap.put(firstAtom, atomCpy);

            while (!atomStack.isEmpty()) {
                // get the last atom and its copy
                IAtom atom = atomStack.pop();
                atomCpy = origToCpyMap.get(atom);
                visitedAtoms[atom.getIndex()] = true;
                for (IAtom nbor: atom.neighbors()) {
                    // if the bond to the current neighbour was already added we go to the next neighbour
                    if (visitedBonds[mol.getBond(atom, nbor).getIndex()]) {
                        continue;
                    }
                    // if the neighbour is not to be split we either copy the neighbour and make a bond from the copy
                    //  of the last atom to its copy of the neighbour or if we already visited the atom we know this is
                    // a cycle connection, and we make only a bond from the last atom to the already copied neighbour
                    if (!atomsToSplit.containsKey(atom) || !atomsToSplit.get(atom).contains(nbor)) {
                        if (!visitedAtoms[nbor.getIndex()]) {
                            IAtom nborCpy = copyAtom(nbor, fragmentContainer);
                            fragmentContainer.newBond(atomCpy, nborCpy, mol.getBond(atom, nbor).getOrder());
                            visitedBonds[mol.getBond(atom, nbor).getIndex()] = true;
                            atomStack.add(nbor);
                            origToCpyMap.put(nbor, nborCpy);
                            visitedAtoms[nbor.getIndex()] = true;
                        } else {
                            IAtom nborCpy = origToCpyMap.get(nbor);
                            if (nborCpy == null) {
                                continue;
                            }
                            fragmentContainer.newBond(
                                    atomCpy,
                                    nborCpy,
                                    mol.getBond(atom, nbor).getOrder()
                            );
                            visitedBonds[mol.getBond(atom, nbor).getIndex()] = true;
                        }
                    } else {
                        startingAtoms.add(nbor);
                    }
                }
            }
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
