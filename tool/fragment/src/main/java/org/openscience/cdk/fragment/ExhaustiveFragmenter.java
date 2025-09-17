/* Copyright (C) 2025  Rajarshi Guha <rajarshi.guha@gmail.com>
 *                     Tom Weiß <tom.weiss@uni-jena.de>
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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Performs exhaustive fragmentation of molecules by breaking single non-ring,
 * non-terminal bonds in all combinations. If it is not possible to generate
 * fragments, an empty list is returned. Non-terminal bonds are those connected
 * to heavy atoms that respectively have another bond to a heavy atom.
 * <p>
 * By default:
 * <ul>
 * <li>Fragments smaller than 6 atoms (excluding implicit hydrogen) are not
 *     returned.</li>
 * <li>Fragments are returned with open valences, where a bond has been split.</li>
 * <li>The fragmentation splits at a maximum tree depth of 31, meaning that
 *     maximum 31 bonds are split in one run.</li>
 * <li>The SMILES code of the fragments is generated with {@link SmiFlavor#Unique}
 *     and {@link SmiFlavor#UseAromaticSymbols}.</li>
 * <li>Stereo information is disregarded</li>
 * </ul>
 * However, users can modify these settings.
 * <p>
 * <strong>Warning on preservation of stereo information:</strong> This process
 * is not reliable and can lead to incorrect stereochemistry in the resulting
 * fragments. When a chiral center is broken during fragmentation, the new
 * fragment may be incorrectly assigned as chiral even if it is not
 * anymore because some of its substituents are now equal.
 * <p>
 * <strong>Example:</strong>the chiral molecule {@code CC[C@@H](Cl)CCCC} will i.a.
 * produce the fragment {@code CC[C@@H](Cl)CC} where the stereo configuration is
 * preserved but the structure is actually not chiral anymore.
 * </p>
 * </p>
 * <strong>Note on Stereochemistry and SMILES:</strong>
 * For stereochemical information to be included in the SMILES strings
 * returned by {@link #getFragments()}, the `smilesGenerator` used by this
 * fragmenter must be configured with the {@link SmiFlavor#Stereo} flag.
 * If the flag is not set, the SMILES will not contain stereochemistry,
 * even if this setting is enabled and the underlying {@code IAtomContainer}
 * objects have stereo elements.
 * </p>
 * <p>
 * <strong>Fragment Deduplication:</strong>
 * The `ExhaustiveFragmenter` uses unique SMILES strings for internal
 * deduplication of generated fragments. This means that after a fragment is
 * generated, its unique SMILES representation is computed (using the default or
 * user specified {@link SmilesGenerator}). Be aware that stereo information is
 * only copied and checked for deduplication if
 * {@link ExhaustiveFragmenter#setPreserveStereo} is set to true and the
 * specified {@link SmilesGenerator} has {@link SmiFlavor#Stereo}. If a fragment
 * with the same canonical SMILES has already been generated and stored, the new
 * fragment is considered a duplicate and is not added to the results.
 * <p>
 * This deduplication strategy is particularly important when considering the
 * {@link Saturation} setting:
 * <ul>
 * <li>If fragments are {@link Saturation#HYDROGEN_SATURATED_FRAGMENTS}, the
 * saturation process might lead to a canonical SMILES that is identical to a
 * fragment obtained via a different bond cleavage, or a fragment that appears
 * different due to explicit hydrogen representation but becomes identical when
 * canonicalized.</li>
 * <li>For example, an unsaturated fragment like `[CH]1CCCCC1` (cyclohexyl
 * radical) might deduplicate with a saturated `C1CCCCC1` (cyclohexane) if
 * `HYDROGEN_SATURATED_FRAGMENTS` is enabled and both forms canonicalize to the
 * same SMILES depending on the exact SMILES generator and atom properties.</li>
 * <li>It is crucial to understand that the uniqueness is based solely on the
 * canonical SMILES string, not on the exact atom-by-atom identity or origin
 * within the original molecule.</li>
 * </ul>
 * <p>
 * <strong>Example Usage:</strong>
 * <pre>{@code
 * // By default, returns unsaturated fragments with a minimum size of 6 atoms
 * ExhaustiveFragmenter fragmenter = new ExhaustiveFragmenter();
 * SmilesParser smiParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
 * // Cyclopentylbenzene
 * IAtomContainer mol = smiParser.parseSmiles("C1CCCC1C1=CC=CC=C1");
 * fragmenter.generateFragments(mol);
 *
 * // Retrieve SMILES representations of fragments
 * String[] smilesFragments = fragmenter.getFragments();
 * // Example Result (depending on exact fragmentation points and min size):
 * // "[C]1=CC=CC=C1"
 *
 * // Retrieve AtomContainer representations of fragments
 * IAtomContainer[] atomContainerFragments = fragmenter.getFragmentsAsContainers();
 *
 * // Example: Configuring for hydrogen-saturated fragments with a minimum size of 5
 * ExhaustiveFragmenter saturatedFragmenter = new ExhaustiveFragmenter(
 *      5,
 *      ExhaustiveFragmenter.Saturation.HYDROGEN_SATURATED_FRAGMENTS
 * );
 * saturatedFragmenter.generateFragments(mol);
 * String[] saturatedSmilesFragments = saturatedFragmenter.getFragments();
 * // "C1CCCC1", "C1=CC=CC=C1"
 * }</pre>
 *
 * @author Rajarshi Guha
 * @author Tom Weiß
 * @cdk.module  fragment
 * @cdk.keyword fragment
 */
public class ExhaustiveFragmenter implements IFragmenter {

    /**
     * Specifies whether generated fragments should be saturated (hydrogens added)
     * or unsaturated.
     */
    public enum Saturation {
        /**
         * Fragments will be returned in their saturated form
         * (implicit hydrogen atoms added).
         */
        HYDROGEN_SATURATED_FRAGMENTS,

        /**
         * Fragments will be saturated with R atoms.
         */
        R_SATURATED_FRAGMENTS,

        /**
         * Fragments will be returned in their unsaturated form
         * (no additional hydrogen atoms). The unsaturated atoms are the atoms
         * of the split bonds.
         */
        UNSATURATED_FRAGMENTS
    }

    private static final int DEFAULT_MIN_FRAG_SIZE = 6;
    private static final Saturation DEFAULT_SATURATION =
            Saturation.UNSATURATED_FRAGMENTS;
    private static final SmilesGenerator DEFAULT_SMILES_GENERATOR =
            new SmilesGenerator(
                    SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols
            );
    private static final int DEFAULT_INCLUSIVE_MAX_TREE_DEPTH = 31;
    private static final boolean DEFAULT_COPY_STEREO_INFO = false;

    private Map<String, IAtomContainer> fragMap;
    private final SmilesGenerator smilesGenerator;
    private int inclusiveMaxTreeDepth;
    private int minFragSize;
    private Saturation saturationSetting;
    private boolean preserveStereo;
    private static final ILoggingTool logger =
            LoggingToolFactory.createLoggingTool(ExhaustiveFragmenter.class);

    /**
     * Constructs an ExhaustiveFragmenter with the default settings:
     * <ul>
     * <li>Minimum fragment size: 6 atoms (excluding implicit hydrogen)</li>
     * <li>{@link Saturation#UNSATURATED_FRAGMENTS}</li>
     * <li>Default {@link SmilesGenerator}
     *     ({@link SmiFlavor#Unique} | {@link SmiFlavor#UseAromaticSymbols})</li>
     * <li>inclusive maximum tree depth of 31</li>
     * <li>stereo information is not preserved</li>
     * </ul>
     */
    public ExhaustiveFragmenter() {
        this(
                DEFAULT_SMILES_GENERATOR,
                DEFAULT_MIN_FRAG_SIZE,
                DEFAULT_SATURATION,
                DEFAULT_INCLUSIVE_MAX_TREE_DEPTH,
                DEFAULT_COPY_STEREO_INFO
        );
    }

    /**
     * Constructs an ExhaustiveFragmenter with a user-defined minimum fragment
     * size and saturation setting. Uses the default {@link SmilesGenerator} and
     * default inclusive maximum tree depth of 31. Stereo information is not
     * preserved.
     *
     * @param minFragSize minimum number of atoms in a valid fragment
     *                    (excluding implicit hydrogen).
     * @param saturationSetting determines whether fragments should be saturated
     *                          (with hydrogens or R-atoms) or unsaturated.
     */
    public ExhaustiveFragmenter(int minFragSize, Saturation saturationSetting) {
        this(
                DEFAULT_SMILES_GENERATOR,
                minFragSize,
                saturationSetting,
                DEFAULT_INCLUSIVE_MAX_TREE_DEPTH,
                DEFAULT_COPY_STEREO_INFO
        );
    }

    /**
     * Constructs an ExhaustiveFragmenter with a user-defined minimum fragment
     * size. Saturation defaults to {@link Saturation#UNSATURATED_FRAGMENTS}.
     * Uses the default {@link SmilesGenerator} and the default
     * inclusive maximum tree depth of 31. Stereo information is not
     * preserved.
     *
     * @param minFragSize minimum number of atoms in a valid fragment
     *                    (excluding implicit hydrogen).
     */
    public ExhaustiveFragmenter(int minFragSize) {
        this(
                DEFAULT_SMILES_GENERATOR,
                minFragSize,
                DEFAULT_SATURATION,
                DEFAULT_INCLUSIVE_MAX_TREE_DEPTH,
                DEFAULT_COPY_STEREO_INFO
        );
    }

    /**
     * Constructs an ExhaustiveFragmenter with a user-defined saturation setting.
     * Minimum fragment size defaults to 6. Uses the default {@link SmilesGenerator}
     * and the default inclusive maximum tree depth of 31. Stereo information is
     * not preserved.
     *
     * @param saturation how open valences should be treated after the
     *                   fragmentation.
     */
    public ExhaustiveFragmenter(Saturation saturation) {
        this(
                DEFAULT_SMILES_GENERATOR,
                DEFAULT_MIN_FRAG_SIZE,
                saturation,
                DEFAULT_INCLUSIVE_MAX_TREE_DEPTH,
                DEFAULT_COPY_STEREO_INFO
        );
    }

    /**
     * Constructs an ExhaustiveFragmenter with a user-provided
     * {@link SmilesGenerator} and user defined:
     * <ul>
     * <li>minimum fragment size</li>
     * <li>inclusive max tree depth</li>
     * <li>saturation setting</li>
     * <li>preservation of stereochemistry information</li>
     * <ul/>
     *
     * @param smilesGenerator the {@link SmilesGenerator} instance to use for
     *                        creating SMILES strings
     *                        for fragment deduplication and retrieval.
     * @param minFragSize minimum number of atoms in a valid fragment
     *                    (excluding implicit hydrogen).
     * @param saturationSetting determines whether fragments should be saturated
     *                          (with hydrogens or R-atoms) or unsaturated.
     * @param inclusiveMaxTreeDepth represents the maximum number of bonds that
     *                              will be split for a fragmentation.
     * @param preserveStereo signals whether to attempt to copy stereochemical
     *                       information from the original molecule to the
     *                       generated fragments. <strong>Warning:</strong> This
     *                       process is not reliable and can lead to incorrect
     *                       stereochemistry in the resulting fragments.
     */
    public ExhaustiveFragmenter(
            SmilesGenerator smilesGenerator,
            int minFragSize,
            Saturation saturationSetting,
            int inclusiveMaxTreeDepth,
            boolean preserveStereo
    ) {
        if (saturationSetting == null) {
            throw new NullPointerException(
                    "The given SaturationSetting can not be null"
            );
        }
        this.saturationSetting = saturationSetting;
        if (smilesGenerator == null) {
            throw new NullPointerException(
                    "The given SmilesGenerator can not be null"
            );
        }
        this.smilesGenerator = smilesGenerator;
        this.preserveStereo = preserveStereo;
        this.setInclusiveMaxTreeDepth(inclusiveMaxTreeDepth);
        this.setMinimumFragmentSize(minFragSize);
        this.fragMap = null;
    }

    /**
     * Sets the minimum allowed fragment size. This has to be greater than zero.
     *
     * @param minFragSize minimum number of atoms in a valid fragment.
     */
    public void setMinimumFragmentSize(int minFragSize) {
        if (minFragSize <= 0) {
            throw new IllegalArgumentException(
                    "Minimum fragment size must be a positive integer (>= 1)" +
                    " Provided: " + minFragSize
            );
        }
        this.minFragSize = minFragSize;
    }

    /**
     * Sets whether fragments should be saturated or unsaturated.
     *
     * @param saturationSetting the saturation mode for generated fragments.
     */
    public void setSaturationSetting(Saturation saturationSetting) {
        if (saturationSetting == null) {
            throw new NullPointerException(
                    "The given SaturationSetting can not be null"
            );
        }
        this.saturationSetting = saturationSetting;
    }

    /**
     * Sets the maximum number of bonds that can be simultaneously split in a
     * single fragmentation event.
     * <p>
     * This is a practical limit to prevent combinatorial explosion and
     * out-of-memory errors for very large molecules. The value must be
     * a positive integer.
     * </p>
     *
     * @param inclusiveMaxTreeDepth the exclusive maximum number of bonds that
     *                              can be split in one atom container.
     */
    public void setInclusiveMaxTreeDepth(int inclusiveMaxTreeDepth) {
        if (inclusiveMaxTreeDepth <= 0) {
            throw new IllegalArgumentException(
                    "Inclusive max tree depth must be greater than zero"
            );
        }
        this.inclusiveMaxTreeDepth = inclusiveMaxTreeDepth;
    }

    /**
     * Sets whether stereochemical information from the original molecule should
     * be copied to the generated fragments.
     *
     * <p>
     * <strong>Warning:</strong> The copying process is not reliable and can
     * result in fragments with incorrect stereochemistry. This method copies
     * elements based on the presence of atoms and bonds, but it does not perform
     * a chemical validation check on the resulting fragment.
     * For example, a chiral center might be copied even if the new fragment does
     * not contain the minimum four different substituents required for chirality.
     * Use caution and consider a separate validation step.
     * </p>
     * <p>
     * <strong>Note on Stereochemistry and SMILES:</strong>
     * For stereochemical information to be included in the SMILES strings
     * returned by {@link #getFragments()}, the `smilesGenerator` used by this
     * fragmenter must be configured with the {@link SmiFlavor#Stereo} flag.
     * If the flag is not set, the SMILES will not contain stereochemistry,
     * even if this setting is enabled and the underlying `IAtomContainer` objects
     * have stereo elements.
     * </p>
     *
     * @param preserve {@code true} to enable attempting to copy
     *                 stereo information; {@code false} otherwise.
     */
    public void setPreserveStereo(boolean preserve) {
        this.preserveStereo = preserve;
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
        if (this.fragMap != null) {
            this.fragMap.clear();
        }
        run(atomContainer);
    }

    /**
     * Splits the molecule at all possible combinations of splittable bonds and
     * saturates the open valences of the resulting fragments according to the
     * {@link ExhaustiveFragmenter#saturationSetting}. Only non-ring and
     * non-terminal single bonds are considered for splitting.
     *
     * @param atomContainer the molecule to be split.
     * @throws CDKException if an error occurs during hydrogen addition or atom
     *                      type perception.
     */
    private void run(IAtomContainer atomContainer) throws CDKException {
        if (atomContainer == null) {
            throw new NullPointerException("No molecule provided");
        }

        // Return early if the molecule has fewer than 3 bonds
        // (no meaningful splits possible)
        if (atomContainer.getBondCount() < 3 ||
                atomContainer.getAtomCount() < this.minFragSize ||
                atomContainer.isEmpty()) {
            this.fragMap = new HashMap<>(0);
            return;
        }

        // Retrieve bonds that are eligible for splitting
        IBond[] splittableBonds = getSplittableBonds(atomContainer);

        // If no splittable bonds are found, return early
        if (splittableBonds.length == 0) {
            logger.debug("no splittable bonds found");
            this.fragMap = new HashMap<>(0);
            return;
        }
        if (splittableBonds.length > this.inclusiveMaxTreeDepth) {
            logger.debug(
                    "Got " + splittableBonds.length + " splittable bonds" +
                    " but only " + this.inclusiveMaxTreeDepth + " tree depth. " +
                    "This means only a maximum of " + this.inclusiveMaxTreeDepth +
                    " bonds can be split at once during a fragmentation step"
            );
        }
        logger.debug("Got " + splittableBonds.length + " splittable bonds");

        // Compute the number of possible bond subsets (excluding the empty set):
        // 2^n - 1
        BigInteger numberOfIterations = BigInteger.ONE.shiftLeft(
                splittableBonds.length
        ).subtract(BigInteger.ONE);

        // Store indices of splittable bonds for subset generation
        int[] splittableBondIndices = new int[splittableBonds.length];
        for (int i = 0; i < splittableBonds.length; i++) {
            splittableBondIndices[i] = splittableBonds[i].getIndex();
        }

        if (numberOfIterations.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) >= 0) {
            this.fragMap = new HashMap<>(Integer.MAX_VALUE);
        } else {
            this.fragMap = new HashMap<>(numberOfIterations.intValue());
        }

        // Iterate over all non-empty subsets of splittable bonds
        for (BigInteger i = BigInteger.ONE;
             i.compareTo(numberOfIterations) <= 0;
             i = i.add(BigInteger.ONE)
        ) {
            int[] subset = generateSubset(i, splittableBondIndices);
            int subsetSize = subset.length;

            // Skip subsets exceeding the allowed depth
            if (subsetSize > this.inclusiveMaxTreeDepth) {
                continue;
            }

            // Convert subset indices back to bond objects
            IBond[] bondsToSplit = new IBond[subsetSize];
            for (int j = 0; j < subsetSize; j++) {
                bondsToSplit[j] = atomContainer.getBond(subset[j]);
            }

            // Split the molecule and retrieve the resulting fragments
            IAtomContainer[] parts = splitBondsWithCopy(
                    atomContainer, bondsToSplit
            );

            // Process each fragment
            for (IAtomContainer partContainer : parts) {

                // Generate a unique SMILES representation of the fragment
                String tmpSmiles = this.smilesGenerator.create(partContainer);

                int numberOfAtoms = 0;
                for (IAtom atom : partContainer.atoms()) {

                    if (atom instanceof IPseudoAtom) {
                        continue;
                    }
                    numberOfAtoms++;
                }

                // Store the fragment if it meets the size requirement and is
                // unique
                if (numberOfAtoms >= minFragSize) {
                    fragMap.putIfAbsent(tmpSmiles, partContainer);
                }
            }
        }
    }

    /**
     * Detects and returns the bonds, which will be split by an exhaustive
     * fragmentation. This method is especially useful to determine if it is
     * even possible to split a specific molecule exhaustively. The number of
     * fragments is 2^n - 1 with n being the number of splittable bonds.
     *
     * <p>
     * Due to the combinatorial explosion, the number of fragments can grow
     * extremely large, potentially leading to out-of-memory errors or
     * excessively long processing times. Consider using the
     * {@link #setInclusiveMaxTreeDepth(int)} method to limit the number of
     * simultaneous bond splits.
     * </p>
     *
     * @param atomContainer the container which contains the molecule in question.
     * @return the bonds which would be split by the exhaustive fragmentation.
     */
    public static IBond[] getSplittableBonds(IAtomContainer atomContainer) {
        if (atomContainer == null) {
            throw new NullPointerException("The atom container must not be null");
        }
        if (atomContainer.isEmpty()) {
            throw new IllegalArgumentException("The atom container must contain " +
                    "an actual molecule");
        }

        // do ring detection
        RingSearch ringSearch = new RingSearch(atomContainer);
        IAtomContainer allRingsContainer = ringSearch.ringFragments();

        // find the splittable bonds
        ArrayList<IBond> splittableBondSet = new ArrayList<>(
                atomContainer.getBondCount() / 3
        );

        for (IBond bond : atomContainer.bonds()) {

            // only single bonds are candidates for splitting
            if (bond.getOrder() == IBond.Order.SINGLE) {
                boolean isInRing = false;
                boolean isTerminal = false;

                // lets see if it's in a ring
                if (allRingsContainer.contains(bond)) isInRing = true;

                // lets see if it is a terminal bond
                for (IAtom atom : bond.atoms()) {
                    if (atomContainer.getConnectedBondsCount(atom) == 1) {
                        isTerminal = true;
                        break;
                    }
                }

                if (!(isInRing || isTerminal)) splittableBondSet.add(bond);
            }
        }
        return splittableBondSet.toArray(new IBond[0]);
    }

    /**
     * Generates a subset from the given array `nums`, determined by the binary
     * representation of `index`. Each bit in `index` indicates whether the
     * corresponding element in `nums` is included in the subset. The order of
     * elements does not matter (i.e., `[1, 2]` and `[2, 1]` are equivalent).
     *
     * <p>The total number of possible subsets is (2^n) - 1, where `n` is the
     * length of `nums`. Subsets are generated using bitwise operations on the
     * provided {@link BigInteger}, where each `1` bit in `index` selects the
     * corresponding element from `nums`.</p>
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
     * @param index an integer whose binary representation determines the subset
     *              elements. A `1` bit at position `j` means `nums[j]` is
     *              included.
     * @param nums the array from which to generate subsets. Duplicate values
     *             in `nums` may result in duplicate subset entries.
     * @return An array containing the subset corresponding to `index`.
     */
    protected static int[] generateSubset(BigInteger index, int[] nums) {
        // allocate subset array based on the number of 1-bits in index.
        int[] subset = new int[index.bitCount()];
        int subsetIndex = 0;

        // only iterate through set bits
        while (index.compareTo(BigInteger.ZERO) != 0) {
            // find position of lowest set bit
            int lowestBitPos = index.getLowestSetBit();

            // add the corresponding element from nums if within bounds
            if (lowestBitPos < nums.length) {
                subset[subsetIndex] = nums[lowestBitPos];
                subsetIndex++;
            }

            // clear the lowest set bit and continue
            index = index.clearBit(lowestBitPos);
        }

        return subset;
    }

    /**
     * Add pseudo ("R") atoms to an atom in a molecule.
     *
     * @param atom   the atom to add the pseudo atoms to
     * @param rcount the number of pseudo atoms to add
     * @param mol    the molecule the atom belongs to
     */
    private void addRAtoms(IAtom atom, int rcount, IAtomContainer mol) {
        for (int i = 0; i < rcount; i++) {
            IPseudoAtom tmpRAtom = atom.getBuilder().newInstance(
                    IPseudoAtom.class, "R"
            );
            tmpRAtom.setAttachPointNum(1);
            tmpRAtom.setImplicitHydrogenCount(0);
            mol.addAtom(tmpRAtom);
            mol.addBond(atom.getBuilder().newInstance(
                    IBond.class,
                    atom, tmpRAtom,
                    IBond.Order.SINGLE
            ));
        }
    }

    /**
     * Creates a copy of an atom and adds it to the specified atom container.
     *
     * @param originalAtom the atom to be copied.
     * @param atomContainer the destination container where the copied atom will
     *                      be added.
     * @return A new atom with the same properties as `originalAtom`, added to
     *         `atomContainer`.
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
     * Creates a copy of a bond and adds it to the specified atom container.
     *
     * @param cpyCurrentAtom atom in the new atom container that is connected by
     *                       the bond to be copied.
     * @param cpyNbor the neighbour of `cpyCurrentAtom` that is connected by the
     *                bond one wants to copy.
     * @param origBond the bond in the original molecule.
     * @param atomContainer the new atom container to which the bond is to
     *                      be copied.
     * @return The bond in the new atom container.
     */
    private static IBond copyBond(
            IAtom cpyCurrentAtom,
            IAtom cpyNbor,
            IBond origBond,
            IAtomContainer atomContainer
    ) {
        IBond cpyBond = atomContainer.newBond(
                cpyCurrentAtom,
                cpyNbor,
                origBond.getOrder());
        cpyBond.setDisplay(origBond.getDisplay());
        cpyBond.setIsAromatic(origBond.isAromatic());
        // Setting is in ring is possible here because we always detect rings
        // in the process of detecting the splittable bonds.
        cpyBond.setIsInRing(origBond.isInRing());
        return cpyBond;
    }

    /**
     * Copies a subset of stereochemical information from a source molecule
     * to a new fragment.
     *
     * <p>
     * This method iterates through the stereochemical elements of the original
     * molecule (e.g., chiral centers, cis/trans bonds) and copies only those
     * that are fully contained within the new fragment. A stereochemical element
     * is considered fully contained if all of its defining atoms and bonds
     * are present in the fragment, based on the provided atom and bond maps.
     * </p>
     * <strong>Warning:</strong> This process is not reliable and can lead to
     * incorrect stereochemistry in the fragment. When a chiral
     * center is broken during fragmentation, the new fragment may be
     * incorrectly assigned as chiral even if it is not anymore because some of
     * its substituents are now equal.
     * <p>
     * <strong>Example:</strong>the chiral molecule {@code CC[C@@H](Cl)CCCC} will i.a.
     * produce the fragment {@code CC[C@@H](Cl)CC} where the stereo configuration is
     * preserved but the structure is actually not chiral anymore.
     * </p>
     *
     * @param origMol the original molecule containing the stereochemical
     *                information.
     * @param fragmentContainer the new fragment where the stereochemical
     *                          information will be added.
     * @param origToCpyAtomMap a mapping of atoms from the original molecule to
     *                         their corresponding atoms in the new fragment.
     * @param origToCpyBondMap a mapping of bonds from the original molecule to
     *                         their corresponding bonds in the new fragment.
     */
    private void attemptCopyStereoInformation(
            IAtomContainer origMol,
            IAtomContainer fragmentContainer,
            Map<IAtom, IAtom> origToCpyAtomMap,
            Map<IBond, IBond> origToCpyBondMap
    ) {
        // adding stereo information if all elements are present in the
        // new fragment
        for (IStereoElement<?, ?> elem : origMol.stereoElements()) {
            boolean focusIsPresent = true;
            boolean carriersArePresent = true;
            final IChemObject origFocus = elem.getFocus();
            if (origFocus instanceof IAtom) {
                if (!origToCpyAtomMap.containsKey(origFocus)) {
                    focusIsPresent = false;
                }
            } else if (origFocus instanceof IBond) {
                if (!origToCpyBondMap.containsKey(origFocus)) {
                    focusIsPresent = false;
                }
            }

            for (IChemObject iChemObject : elem.getCarriers()) {
                if (iChemObject instanceof IAtom) {
                    if (!origToCpyAtomMap.containsKey(iChemObject)) {
                        carriersArePresent = false;
                        break;
                    }
                } else if (iChemObject instanceof IBond) {
                    if (!origToCpyBondMap.containsKey(iChemObject)) {
                        carriersArePresent = false;
                        break;
                    }
                }
            }

            if (focusIsPresent && carriersArePresent) {
                fragmentContainer.addStereoElement(elem.map(origToCpyAtomMap, origToCpyBondMap));
            }
        }
    }

    /**
     * Splits and saturates (if specified via {@link #saturationSetting}) a
     * molecule into multiple fragments by removing the specified bonds and
     * making copies of the resulting fragments.
     *
     * @param origMol the molecule to be split.
     * @param bondsToSplit the bonds that should be removed to create
     *                     separate fragments.
     * @return An array of copied molecular fragments resulting from the split.
     */
    private IAtomContainer[] splitBondsWithCopy(
            IAtomContainer origMol,
            IBond[] bondsToSplit
    ) {
        Set<IBond> bondsToSplitSet = new HashSet<>(
                (int) Math.ceil(bondsToSplit.length / 0.75)
        );
        // for a faster lookup the hashset is used here.
        bondsToSplitSet.addAll(Arrays.asList(bondsToSplit));
        boolean[] visitedOriginalAtoms = new boolean[origMol.getAtomCount()];
        List<IAtomContainer> fragmentList = new ArrayList<>(bondsToSplit.length + 1);

        for (int i = 0; i < origMol.getAtomCount(); i++) {
            IAtom currPotentialStartAtom = origMol.getAtom(i);
            if (!visitedOriginalAtoms[origMol.indexOf(currPotentialStartAtom)]) {
                IAtomContainer fragmentContainer =
                        origMol.getBuilder().newInstance(IAtomContainer.class);
                Map<IAtom, IAtom> origToCpyAtomMap = new HashMap<>(
                        (int) Math.ceil(origMol.getAtomCount() / 0.75)
                );
                Map<IBond, IBond> origToCpyBondMap = new HashMap<>(
                        (int) Math.ceil(origMol.getBondCount() / 0.75)
                );
                Deque<IAtom> dfsStack = new ArrayDeque<>();
                // Store split counts specific to the atoms in the fragment being built
                Map<IAtom, Integer> splitCountsCpyAtoms = new HashMap<>();

                dfsStack.push(currPotentialStartAtom);
                visitedOriginalAtoms[origMol.indexOf(currPotentialStartAtom)] = true;
                IAtom cpyStartAtom = copyAtom(currPotentialStartAtom, fragmentContainer);
                origToCpyAtomMap.put(currPotentialStartAtom, cpyStartAtom);

                while (!dfsStack.isEmpty()) {
                    IAtom origCurrAtom = dfsStack.pop();
                    IAtom cpyCurrentAtom = origToCpyAtomMap.get(origCurrAtom);

                    for (IBond origBond : origMol.getConnectedBondsList(origCurrAtom)) {
                        IAtom origNbor = origBond.getOther(origCurrAtom);
                        boolean isThisABondToSplit = bondsToSplitSet.contains(origBond);

                        if (!isThisABondToSplit) {
                            if (!origToCpyAtomMap.containsKey(origNbor)) {
                                visitedOriginalAtoms[origMol.indexOf(origNbor)] = true;
                                IAtom cpyNbor = copyAtom(origNbor, fragmentContainer);
                                origToCpyAtomMap.put(origNbor, cpyNbor);
                                IBond cpyBond = copyBond(
                                        cpyCurrentAtom,
                                        cpyNbor,
                                        origBond,
                                        fragmentContainer
                                );
                                origToCpyBondMap.put(origBond, cpyBond);
                                dfsStack.push(origNbor);
                            } else {
                                IAtom cpyNbor = origToCpyAtomMap.get(origNbor);
                                // Add bond only if not already present
                                if (fragmentContainer.getBond(cpyCurrentAtom, cpyNbor) == null) {
                                    IBond cpyBond = copyBond(
                                            cpyCurrentAtom,
                                            cpyNbor,
                                            origBond,
                                            fragmentContainer
                                    );
                                    origToCpyBondMap.put(origBond, cpyBond);
                                }
                            }
                        } else {
                            // This bond is being cut. The origCurrAtom is part of the fragment being built.
                            // Increment the cleavage count for its corresponding copied atom.
                            splitCountsCpyAtoms.put(cpyCurrentAtom,
                                    splitCountsCpyAtoms.getOrDefault(cpyCurrentAtom, 0) + 1);
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
                                int newImplHCount =
                                        (currImplHCount == null ? 0 : currImplHCount) + bondsCutCount;
                                atom.setImplicitHydrogenCount(newImplHCount);
                                break;
                            case R_SATURATED_FRAGMENTS:
                                addRAtoms(atom, bondsCutCount, fragmentContainer);
                                break;
                            default:
                                throw new UnsupportedOperationException(
                                        "no treatment defined yet for this new enum constant"
                                );
                        }
                    }
                }
                if (this.preserveStereo) {
                    attemptCopyStereoInformation(
                            origMol,
                            fragmentContainer,
                            origToCpyAtomMap,
                            origToCpyBondMap
                    );
                }
                fragmentList.add(fragmentContainer);
            }
        }
        return fragmentList.toArray(new IAtomContainer[0]);
    }

    /**
     * Get the fragments generated as SMILES strings.
     * <p>
     * <strong>Note on Stereochemistry:</strong>
     * Stereochemistry information will only be included in the returned SMILES
     * strings if the `SmilesGenerator` used by this fragmenter was configured
     * with the {@link SmiFlavor#Stereo} flag.
     * </p>
     *
     * @return a String[] of the fragments.
     */
    @Override
    public String[] getFragments() {
        if (fragMap == null) {
            throw new NullPointerException("It is mandatory to generate " +
                    "fragments before getting them");
        }
        return (new ArrayList<>(fragMap.keySet())).toArray(new String[0]);
    }

    /**
     * Get the fragments generated as {@link IAtomContainer} objects.
     *
     * @return a IAtomContainer[] of the fragments.
     */
    @Override
    public IAtomContainer[] getFragmentsAsContainers() {
        if (fragMap == null) {
            throw new NullPointerException("It is mandatory to generate " +
                    "fragments before getting them");
        }
        return (new ArrayList<>(fragMap.values())).toArray(new IAtomContainer[0]);
    }

}
