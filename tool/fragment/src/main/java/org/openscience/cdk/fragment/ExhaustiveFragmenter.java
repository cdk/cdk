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
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.IntStream;

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

    private static final int    DEFAULT_MIN_FRAG_SIZE = 6;
    private static final Saturation DEFAULT_SATURATION = Saturation.UNSATURATED_FRAGMENTS;

    final Map<String, IAtomContainer> fragMap;
    final SmilesGenerator             smilesGenerator;
    int                         minFragSize;
    Saturation saturationSetting;
    private static final ILoggingTool logger                = LoggingToolFactory
                                                              .createLoggingTool(ExhaustiveFragmenter.class);

    /**
     * Instantiate fragmenter with default minimum fragment size.
     */
    public ExhaustiveFragmenter() {
        this(DEFAULT_MIN_FRAG_SIZE, DEFAULT_SATURATION);
    }

    /**
     * Instantiate fragmenter with user specified minimum fragment size and default saturation (saturated fragments).
     *
     * @param minFragSize the minimum fragment size desired.
     */
    public ExhaustiveFragmenter(int minFragSize) {
        this.minFragSize = minFragSize;
        this.saturationSetting = DEFAULT_SATURATION;
        fragMap = new HashMap<>();
        smilesGenerator = new SmilesGenerator(SmiFlavor.UseAromaticSymbols | SmiFlavor.Unique);
    }

    /**
     * Instantiate fragmenter with default minimum fragment size and user specified saturation setting.
     *
     * @param saturationSetting setting to specify if the returned fragments should be saturated or not.
     */
    public ExhaustiveFragmenter(Saturation saturationSetting) {
        this.minFragSize = DEFAULT_MIN_FRAG_SIZE;
        this.saturationSetting = saturationSetting;
        fragMap = new HashMap<>();
        smilesGenerator = new SmilesGenerator(SmiFlavor.UseAromaticSymbols | SmiFlavor.Unique);
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
        smilesGenerator = new SmilesGenerator(SmiFlavor.UseAromaticSymbols | SmiFlavor.Unique);
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
            runUnsaturated(atomContainer);
        } else {
            runSaturated(atomContainer);
        }
    }

    private void runSaturated(IAtomContainer atomContainer) throws CDKException {

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
        BigInteger numberOfIterations = BigInteger.ONE.shiftLeft(splittableBondsLength).subtract(BigInteger.ONE);

        List<List<Integer>> allSubsets = generateSubsets(IntStream.rangeClosed(0, splittableBondsLength).toArray());
        int[] splittableBondIndices = new int[splittableBondsLength];
        for (int i = 0; i < splittableBondsLength; i++) {
            splittableBondIndices[i] = splittableBonds[i].getIndex();
        }

        for (BigInteger i = BigInteger.ZERO; i.compareTo(numberOfIterations) < 0; i = i.add(BigInteger.ONE)){
            int subsetSize = allSubsets.get(i.intValue()).size();
            IBond[] bondsToRemove = new IBond[subsetSize];
            for (int j = 0; j < subsetSize; j++) {
                bondsToRemove[j] = atomContainer.getBond(splittableBondIndices[j]);
            }
//                List<IAtomContainer> parts = FragmentUtils.splitMolecule(molToSplit, bondToSplit);
            IAtomContainer[] parts = splitMoleculeWithCopy(atomContainer, bondsToRemove);
            for (IAtomContainer partContainer : parts) {
                AtomContainerManipulator.clearAtomConfigurations(partContainer);
                for (IAtom atom : partContainer.atoms()) {
                    atom.setImplicitHydrogenCount(0);
                }
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(partContainer);
                CDKHydrogenAdder.getInstance(partContainer.getBuilder()).addImplicitHydrogens(partContainer);
                Aromaticity.cdkLegacy().apply(partContainer);
                String tmpSmiles = smilesGenerator.create(partContainer);
                int numberOfAtoms = partContainer.getAtomCount();
                if (numberOfAtoms >= minFragSize && !fragMap.containsKey(tmpSmiles)) {
                    fragMap.put(tmpSmiles, partContainer);
                }
                if (numberOfAtoms < minFragSize) {
                    break;
                }
            }

        }
    }

    private void runUnsaturated(IAtomContainer atomContainer) throws CDKException {

        if (atomContainer.getBondCount() < 3) return;
        IBond[] splitableBonds = getSplitableBonds(atomContainer);
        if (splitableBonds.length == 0) return;
        logger.debug("Got " + splitableBonds.length + " splittable bonds");

        String tmpSmiles;
        for (IBond bond : splitableBonds) {
            List<IAtomContainer> parts = FragmentUtils.splitMolecule(atomContainer, bond);
            // make sure we don't add the same fragment twice
            for (IAtomContainer partContainer : parts) {
                tmpSmiles = smilesGenerator.create(partContainer);
                int fragmentSize = partContainer.getAtomCount();
                if (fragmentSize >= minFragSize && !fragMap.containsKey(tmpSmiles)) {
                    fragMap.put(tmpSmiles, partContainer);
                    if (fragmentSize > minFragSize) {
                        runUnsaturated(partContainer);
                    }
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

    private static List<List<Integer>> generateSubsets(int[] nums) {
        int n = nums.length;
        // Just in case n > 32, we make an integer of arbitrary size.
        List<List<Integer>> result;
        BigInteger numOfSubsets = BigInteger.ONE.shiftLeft(n);
        if (n > 32) {
            result = new ArrayList<>(Integer.MAX_VALUE);
        }
        else {
            result = new ArrayList<>(numOfSubsets.intValue());
        }

        // we can collect all subsets if we iterate from one (to disregard the empty set) to the number
        // of possible subsets and check for each number which bits are set to one and replace this
        // index by the respective number at the same index from the given nums list.
        // Example:
        // nums = [1, 2, 3]
        // first iteration:
        // 0b001 (1)
        // -> [1]
        // second iteration:
        // 0b010 (2)
        // -> [2]
        // third iteration:
        // 0b011 (3)
        // -> [1, 2]
        // ...
        for (BigInteger i = BigInteger.ONE; i.compareTo(numOfSubsets) < 0; i = i.add(BigInteger.ONE)) {
            List<Integer> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (i.testBit(j)) {
                    subset.add(nums[j]);
                }
            }
            result.add(subset);
        }
        return result;
    }

    private static IAtom copyAtom(IAtom originalAtom, IAtomContainer atomContainer) {
        IAtom cpyAtom = atomContainer.newAtom(originalAtom.getAtomicNumber(),
                originalAtom.getImplicitHydrogenCount());
        cpyAtom.setIsAromatic(originalAtom.isAromatic());
        cpyAtom.setValency(originalAtom.getValency());
        cpyAtom.setAtomTypeName(originalAtom.getAtomTypeName());
        return cpyAtom;
    }

    private static IAtomContainer[] splitMoleculeWithCopy(IAtomContainer mol, IBond[] bondsToSplit) {
        boolean[] alreadyVisited = new boolean[mol.getAtomCount()];
        // set all values of already visited to false
        Arrays.fill(alreadyVisited, false);
        int numberOfFragments = bondsToSplit.length + 1;
        IAtomContainer[] fragments = new IAtomContainer[numberOfFragments];
        for (IBond bond : bondsToSplit) {
            mol.removeBond(bond);
        }
        for (int i = 0; i < numberOfFragments; i++) {
            // new container to hold a fragment
            IAtomContainer fragmentContainer = mol.getBuilder().newInstance(IAtomContainer.class);

            // a stack to make a DFS through the subgraph
            IAtom firstAtom;
            Stack<IAtom> atomStack = new Stack<>();
            if (i == 0) {
                atomStack.add(bondsToSplit[0].getBegin());
                firstAtom = copyAtom(atomStack.peek(), fragmentContainer);
                for (IAtom nbor : firstAtom.neighbors()) {
                    IAtom cpyNbor = copyAtom(nbor, fragmentContainer);
                    fragmentContainer.newBond(firstAtom, cpyNbor, mol.getBond(atomStack.peek(), nbor).getOrder());
                    atomStack.add(nbor);
                }
            } else {
                atomStack.add(bondsToSplit[i - 1].getEnd());
                firstAtom = copyAtom(atomStack.peek(), fragmentContainer);
                for (IAtom nbor : firstAtom.neighbors()) {
                    IAtom cpyNbor = copyAtom(nbor, fragmentContainer);
                    fragmentContainer.newBond(firstAtom, cpyNbor, mol.getBond(atomStack.peek(), nbor).getOrder());
                    atomStack.add(nbor);
                }
            }
            while (!atomStack.isEmpty()) {
                IAtom lastAtom = atomStack.pop();
                IAtom cpyAtom = copyAtom(lastAtom, fragmentContainer);
                alreadyVisited[lastAtom.getIndex()] = true;
                //FIXME: Add cycle connections together !!!!
                for (IAtom neighbor: lastAtom.neighbors()) {
                    if (alreadyVisited[neighbor.getIndex()] == false) {
                        alreadyVisited[neighbor.getIndex()] = true;
                        IAtom cpyNeighbor = copyAtom(neighbor, fragmentContainer);
                        fragmentContainer.newBond(cpyAtom, cpyNeighbor, mol.getBond(lastAtom, neighbor).getOrder());
                        atomStack.add(neighbor);
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
     * Get the fragments generated as {@link IAtomContainer} objects..
     *
     * @return a IAtomContainer[] of the fragments.
     */
    @Override
    public IAtomContainer[] getFragmentsAsContainers() {
        return (new ArrayList<>(fragMap.values())).toArray(new IAtomContainer[0]);
    }

}
