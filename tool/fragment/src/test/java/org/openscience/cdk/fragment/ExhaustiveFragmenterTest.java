/* Copyright (C) 2025  Rajarshi Guha <rajarshi.guha@gmail.com>
 *                     Tom Weiß <tom.weiss@uni-jena.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test exhaustive fragmenter.
 * This test class covers various scenarios for the {@link ExhaustiveFragmenter},
 * including different saturation settings (unsaturated, hydrogen-saturated, R-group saturated)
 * and minimum fragment size.
 *
 * @see ExhaustiveFragmenter
 */
class ExhaustiveFragmenterTest extends CDKTestCase {

    private static ExhaustiveFragmenter fragmenterSaturated;
    private static ExhaustiveFragmenter fragmenterUnsaturated;
    private static ExhaustiveFragmenter fragmenterRestSaturated;
    private static SmilesParser         smilesParser;

    @BeforeAll
    static void setup() {
        fragmenterSaturated = new ExhaustiveFragmenter();
        fragmenterSaturated.setSaturationSetting(ExhaustiveFragmenter.Saturation.HYDROGEN_SATURATED_FRAGMENTS);
        fragmenterUnsaturated = new ExhaustiveFragmenter();
        fragmenterUnsaturated.setSaturationSetting(ExhaustiveFragmenter.Saturation.UNSATURATED_FRAGMENTS);
        fragmenterRestSaturated = new ExhaustiveFragmenter();
        fragmenterRestSaturated.setSaturationSetting(ExhaustiveFragmenter.Saturation.R_SATURATED_FRAGMENTS);
        smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
    }

    // --- Unsaturated Fragments Tests ---

    /**
     * Tests that a simple linear alkane (propane) with no splittable bonds
     * yields no fragments when using the unsaturated setting.
     */
    @Test
    void testEF1Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("CCC");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertEquals(0, frags.length);
    }

    /**
     * Tests that a simple cycloalkane (cyclopentane) with no non-ring, non-terminal bonds
     * yields no fragments when using the unsaturated setting.
     */
    @Test
    void testEF2Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCC1");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertEquals(0, frags.length);
    }

    /**
     * Tests fragmentation of ethylcyclohexane with unsaturated fragments.
     * Expects "[CH]1CCCCC1" as a fragment, representing the cyclohexyl radical.
     */
    @Test
    void testEF3Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCCC1CC");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertArrayEquals(frags, new String[]{"[CH]1CCCCC1"});
    }

    /**
     * Tests fragmentation of ethylbenzene with unsaturated fragments.
     * Expects "[c]1ccccc1" as a fragment, representing the phenyl radical.
     */
    @Test
    void testEF4Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1CC");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertArrayEquals(frags, new String[]{"[c]1ccccc1"});
    }

    /**
     * Tests fragmentation of diphenylmethane with unsaturated fragments.
     * Expects "[CH2]c1ccccc1" (benzyl radical) and "[c]1ccccc1" (phenyl radical).
     */
    @Test
    void testEF5Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1Cc1ccccc1");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertTrue(
                hasItems(frags, new String[] {
                        "[CH2]c1ccccc1",
                        "[c]1ccccc1"
                })
        );
        Assertions.assertNotNull(fragmenterUnsaturated.getFragmentsAsContainers());
        Assertions.assertEquals(2, fragmenterUnsaturated.getFragmentsAsContainers().length);
    }

    /**
     * Tests fragmentation of biphenyl with unsaturated fragments.
     * Expects only "[c]1ccccc1" as the fragment.
     */
    @Test
    void testEF6Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1c1ccccc1");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertArrayEquals(frags, new String[]{"[c]1ccccc1"});

        Assertions.assertNotNull(fragmenterUnsaturated.getFragmentsAsContainers());
        Assertions.assertEquals(1, fragmenterUnsaturated.getFragmentsAsContainers().length);

    }

    /**
     * Tests a complex molecule with unsaturated fragments.
     * Expected fragments include phenyl and various complex radical fragments.
     * Note: The number of fragments (26) is higher than the saturated version (25)
     * because unsaturated fragments explicitly show radical centers, which can lead to
     * unique SMILES for fragments that would be canonicalized identically when saturated
     * due to differences in hydrogen counts or explicit radical representation.
     */
    @Test
    void testEF7Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1(c2ccccc2)(CC(CC1)CCc1ccccc1)CC1C=CC=C1");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertNotNull(frags);
        // There is one additional fragment in comparison to the saturated version because there are following fragments:
        // [C]1CCC([CH2])C1
        // [CH2][C]1C[CH]CC1
        // these fragments only differ in the number of hydrogen's bonded to their respective carbon atoms. So these
        // fragments would show up as one if saturated.
        Assertions.assertEquals(26, frags.length);

        Assertions.assertNotNull(fragmenterUnsaturated.getFragmentsAsContainers());
        Assertions.assertEquals(26, fragmenterUnsaturated.getFragmentsAsContainers().length);

        Assertions.assertTrue(
                hasItems(frags, new String[] {
                        "[c]1ccccc1",
                        "[CH2]CC1CCC(c2ccccc2)(CC3C=CC=C3)C1",
                        "[CH2]C1CCC([CH2])(c2ccccc2)C1"
                })
        );
    }

    // --- Hydrogen-Saturated Fragments Tests ---

    /**
     * Tests that a simple linear alkane (propane) with no splittable bonds
     * yields no fragments when using the hydrogen-saturated setting.
     */
    @Test
    void testEF1Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("CCC");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertEquals(0, frags.length);
    }

    /**
     * Tests that a simple cycloalkane (cyclopentane) with no non-ring, non-terminal bonds
     * yields no fragments when using the hydrogen-saturated setting.
     */
    @Test
    void testEF2Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCC1");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertEquals(0, frags.length);
    }

    /**
     * Tests fragmentation of methylcyclohexane with hydrogen-saturated fragments.
     * Expects "C1CCCCC1" as a fragment, representing cyclohexane.
     */
    @Test
    void testEF3Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCCC1CC");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertArrayEquals(frags, new String[]{"C1CCCCC1"});
    }

    /**
     * Tests fragmentation of ethylbenzene with hydrogen-saturated fragments.
     * Expects "c1ccccc1" as a fragment, representing benzene.
     */
    @Test
    void testEF4Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1CC");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertArrayEquals(frags, new String[]{"c1ccccc1"});
    }

    /**
     * Tests fragmentation of diphenylmethane with hydrogen-saturated fragments.
     * Expects "c1ccc(cc1)C" (toluene) and "c1ccccc1" (benzene).
     * Note: "c1ccc(cc1)C" might also be canonicalized as "Cc1ccccc1".
     */
    @Test
    void testEF5Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1Cc1ccccc1");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertTrue(
                hasItems(frags, new String[] {
                        "c1ccc(cc1)C",
                        "c1ccccc1"
                })
        );
        Assertions.assertNotNull(fragmenterSaturated.getFragmentsAsContainers());
        Assertions.assertEquals(2, fragmenterSaturated.getFragmentsAsContainers().length);
    }

    /**
     * Tests fragmentation of biphenyl with hydrogen-saturated fragments.
     * Expects only "c1ccccc1" (benzene) as the fragment.
     */
    @Test
    void testEF6Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1c1ccccc1");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertArrayEquals(frags, new String[]{"c1ccccc1"});

        Assertions.assertNotNull(fragmenterSaturated.getFragmentsAsContainers());
        Assertions.assertEquals(1, fragmenterSaturated.getFragmentsAsContainers().length);
    }

    /**
     * Tests a complex molecule with hydrogen-saturated fragments.
     * Expected fragments include benzene and various complex saturated fragments.
     * Compared to the unsaturated version, some fragments might canonicalize to the same SMILES
     * after saturation, resulting in a slightly lower count (25 vs 26).
     */
    @Test
    void testEF7Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1(c2ccccc2)(CC(CC1)CCc1ccccc1)CC1C=CC=C1");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertEquals(25, frags.length);

        Assertions.assertNotNull(fragmenterSaturated.getFragmentsAsContainers());
        Assertions.assertEquals(25, fragmenterSaturated.getFragmentsAsContainers().length);

        Assertions.assertTrue(
                hasItems(frags, new String[] {
                        "c1ccccc1",
                        "c1ccc(cc1)C2(CCC(CC)C2)CC3C=CC=C3",
                        "c1ccc(cc1)C2(C)CCC(C)C2"
                })
        );
    }

    // --- R-Group Saturated Fragments Tests ---

    /**
     * Tests fragmentation of ethylcyclohexane with R-group saturated fragments.
     * Expects "*C1CCCCC1" as a fragment, representing the cyclohexyl group with an R-atom.
     */
    @Test
    void testEF3RestSaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCCC1CC");
        fragmenterRestSaturated.generateFragments(mol);
        String[] frags = fragmenterRestSaturated.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertArrayEquals(frags, new String[]{"*C1CCCCC1"});
    }

    /**
     * Tests fragmentation of toluene with R-group saturated fragments.
     * Expects "*c1ccccc1" (phenyl with R-atom) and "*Cc1ccccc1" (benzyl with R-atom).
     */
    @Test
    void testEF5RestSaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1Cc1ccccc1");
        fragmenterRestSaturated.generateFragments(mol);
        String[] frags = fragmenterRestSaturated.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertTrue(
                hasItems(frags, new String[] {
                        "*c1ccccc1",
                        "*Cc1ccccc1"
                })
        );
        Assertions.assertEquals(2, fragmenterRestSaturated.getFragmentsAsContainers().length);
    }

    /**
     * Tests fragmentation of biphenyl with R-group saturated fragments.
     * Expects only "*c1ccccc1" as the fragment.
     */
    @Test
    void testEF6RestSaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1c1ccccc1");
        fragmenterRestSaturated.generateFragments(mol);
        String[] frags = fragmenterRestSaturated.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertArrayEquals(frags, new String[]{"*c1ccccc1"});
        Assertions.assertEquals(1, fragmenterRestSaturated.getFragmentsAsContainers().length);
    }

    /**
     * Tests a complex molecule with R-group saturated fragments.
     * The number of fragments can differ from hydrogen-saturated or unsaturated versions
     * due to the R-group affecting the size of the fragments.
     */
    @Test
    void testEF7RestSaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1(c2ccccc2)(CC(CC1)CCc1ccccc1)CC1C=CC=C1");
        fragmenterRestSaturated.generateFragments(mol);
        String[] frags = fragmenterRestSaturated.getFragments();
        Assertions.assertNotNull(frags);
        // Needs to have the same number of fragments as the unsaturated version.
        Assertions.assertEquals(26, fragmenterRestSaturated.getFragmentsAsContainers().length);
        Assertions.assertTrue(
                hasItems(frags, new String[] {
                    "*c1ccccc1",
                    "*C1CCC(c2ccccc2)(CC3C=CC=C3)C1",
                    "*C1CCC(*)(c2ccccc2)C1"
                })
        );
    }

    // --- General Fragmenter Tests ---

    /**
     * Tests the minimum fragment size setting.
     * With a minimum size of 6, only the larger ring (cyclohexane) should be returned
     * from a molecule composed of a cyclopentane and a cyclohexane connected by a single bond.
     */
    @Test
    void testMinSize() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCC1C2CCCCC2");
        fragmenterSaturated.setMinimumFragmentSize(6);
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertEquals(1, frags.length);
        Assertions.assertEquals("C1CCCCC1", frags[0]);
    }

    /**
     * Tests that lowering the minimum fragment size allows smaller fragments to be returned.
     * For "C1CCCC1C2CCCCC2", setting min size to 5 should yield both rings.
     */
    @Test
    void testMinSizeLowered() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCC1C2CCCCC2");
        ExhaustiveFragmenter localFragmenter = new ExhaustiveFragmenter();
        localFragmenter.setSaturationSetting(ExhaustiveFragmenter.Saturation.HYDROGEN_SATURATED_FRAGMENTS);
        localFragmenter.setMinimumFragmentSize(5);
        localFragmenter.generateFragments(mol);
        String[] frags = localFragmenter.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertEquals(2, frags.length);
        Assertions.assertTrue(
                hasItems(frags, new String[] {
                        "C1CCCCC1",
                        "C1CCCC1"
                })
        );
    }

    /**
     * Verifies that the SMILES representations obtained from fragments match
     * the SMILES generated directly from their corresponding {@link IAtomContainer} objects.
     */
    @Test
    void testEqualityOfSmilesAndContainers() throws Exception {
        SmilesGenerator smilesGenerator = new SmilesGenerator(SmiFlavor.UseAromaticSymbols | SmiFlavor.Unique);
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1CC(N)C(=O)O"); // Phenylalanine
        fragmenterSaturated.generateFragments(mol);
        String[] smilesFrags = fragmenterSaturated.getFragments();
        IAtomContainer[] containerFrags = fragmenterSaturated.getFragmentsAsContainers();
        for (IAtomContainer frag : containerFrags) {
            Assertions.assertTrue(hasItems(smilesFrags, new String[] {
                    smilesGenerator.create(frag)
            }));
        }
    }

    /**
     * Tests the {@link ExhaustiveFragmenter#getSplittableBonds(IAtomContainer)} method
     * for a linear alkane (propane), which should have no splittable bonds.
     */
    @Test
    void testGetSplittableBondsLinearMolecule() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("CCC"); // Propane
        IBond[] splittableBonds = ExhaustiveFragmenter.getSplittableBonds(mol);
        Assertions.assertEquals(0, splittableBonds.length);
    }

    /**
     * Tests the {@link ExhaustiveFragmenter#getSplittableBonds(IAtomContainer)} method
     * for a cyclic alkane (cyclopentane), which should have no splittable bonds (all bonds are in a ring).
     */
    @Test
    void testGetSplittableBondsCyclicMolecule() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCC1"); // Cyclopentane
        IBond[] splittableBonds = ExhaustiveFragmenter.getSplittableBonds(mol);
        Assertions.assertEquals(0, splittableBonds.length);
    }

    /**
     * Tests the {@link ExhaustiveFragmenter#getSplittableBonds(IAtomContainer)} method
     * for ethylbenzene, which should have one splittable bond (the bond between the phenyl and ethyl groups).
     */
    @Test
    void testGetSplittableBondsBenzeneWithSideChain() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1CC"); // Ethylbenzene
        IBond[] splittableBonds = ExhaustiveFragmenter.getSplittableBonds(mol);
        Assertions.assertEquals(1, splittableBonds.length);
    }

    /**
     * Tests the {@link ExhaustiveFragmenter#getSplittableBonds(IAtomContainer)} method
     * for biphenyl, which should have one splittable bond (the bond connecting the two phenyl rings).
     */
    @Test
    void testGetSplittableBondsBiphenyl() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1c1ccccc1"); // Biphenyl
        IBond[] splittableBonds = ExhaustiveFragmenter.getSplittableBonds(mol);
        Assertions.assertEquals(1, splittableBonds.length);
    }

    /**
     * Tests the internal helper method `generateSubset` which creates subsets
     * based on the bit representation of an index.
     * This ensures the combinatorial generation of bond subsets works correctly.
     */
    @Test
    void testGenerateSubset() {
        int[] nums = new int[]{10, 20, 30, 40};

        // index = 1 (0001) -> {nums[0]}
        Assertions.assertArrayEquals(new int[]{10}, ExhaustiveFragmenter.generateSubset(1, nums));

        // index = 2 (0010) -> {nums[1]}
        Assertions.assertArrayEquals(new int[]{20}, ExhaustiveFragmenter.generateSubset(2, nums));

        // index = 3 (0011) -> {nums[0], nums[1]}
        Assertions.assertArrayEquals(new int[]{10, 20}, ExhaustiveFragmenter.generateSubset(3, nums));

        // index = 4 (0100) -> {nums[2]}
        Assertions.assertArrayEquals(new int[]{30}, ExhaustiveFragmenter.generateSubset(4, nums));

        // index = 5 (0101) -> {nums[0], nums[2]}
        Assertions.assertArrayEquals(new int[]{10, 30}, ExhaustiveFragmenter.generateSubset(5, nums));

        // index = 7 (0111) -> {nums[0], nums[1], nums[2]}
        Assertions.assertArrayEquals(new int[]{10, 20, 30}, ExhaustiveFragmenter.generateSubset(7, nums));

        // index = 15 (1111) -> {nums[0], nums[1], nums[2], nums[3]}
        Assertions.assertArrayEquals(new int[]{10, 20, 30, 40}, ExhaustiveFragmenter.generateSubset(15, nums));
    }

    /**
     * Tests the functionality of providing a custom SmilesGenerator to the ExhaustiveFragmenter.
     * This test uses a SmilesGenerator that does NOT use aromatic symbols, expecting kekulized SMILES.
     */
    @Test
    void testCustomSmilesGenerator() throws Exception {
        SmilesGenerator customSmilesGen = new SmilesGenerator(SmiFlavor.Unique); // No SmiFlavor.UseAromaticSymbols
        ExhaustiveFragmenter customFragmenter = new ExhaustiveFragmenter(
                customSmilesGen, 6, ExhaustiveFragmenter.Saturation.HYDROGEN_SATURATED_FRAGMENTS, Integer.SIZE - 1);
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1Cc1ccccc1"); // Diphenylmethane
        customFragmenter.generateFragments(mol);
        String[] frags = customFragmenter.getFragments();

        Assertions.assertNotNull(frags);
        Assertions.assertTrue(
                hasItems(frags, new String[] {
                        "C=1C=CC=CC1",
                        "C=1C=CC(=CC1)C"
                })
        );
        Assertions.assertEquals(2, frags.length);
    }

    /**
     * Tests the setInclusiveMaxTreeDepth method using 1,4-dibutylbenzene.
     * By varying `inclusiveMaxTreeDepth`, we can observe how the number of generated fragments changes.
     *
     * <pre>
     * Molecule: 1,4-dibutylbenzene (CCCCc1ccc(CCCC)cc1)
     * Splittable bonds: 6 (the three C-C bonds for each butyl chain, from the ring until the the second last C-atom).
     * Fragmenter setup: minFragSize = 4 (to include butyl and benzene fragments), hydrogen-saturated fragments.
     *
     * Expected fragments for different inclusiveMaxTreeDepth settings:
     *
     * 1.  inclusiveMaxTreeDepth = 0 (allows 0 simultaneous cuts):
     * - Expected fragments: 0
     *
     * 2.  inclusiveMaxTreeDepth = 1 (allows up to 1 simultaneous cut):
     * - Considers all subsets of splittable bonds of size 1.
     * - Expected unique fragments: 4 (
     * c1ccc(cc1)CCCC
     * c1cc(ccc1C)CCCC
     * c1cc(ccc1CC)CCCC
     * CCCC"
     * )
     *
     * 3.  inclusiveMaxTreeDepth = 2 (allows up to 2 simultaneous cuts):
     * - Considers all subsets of splittable bonds of size 1 and 2.
     * - Includes fragments from 1-cut operations, plus fragments from 2-cut operations:
     * - Expected unique fragments: 10 (
     * c1ccc(cc1)C
     * c1ccc(cc1)CC
     * c1ccc(cc1)CCCC
     * c1cc(ccc1C)C
     * c1cc(ccc1C)CC
     * c1cc(ccc1C)CCCC
     * c1cc(ccc1CC)CC
     * c1cc(ccc1CC)CCCC
     * c1ccccc1
     * CCCC
     * )
     *
     * 4.  inclusiveMaxTreeDepth = 3 (allows up to 3 simultaneous cuts):
     * - Since there are only combinations of 2 splittable bonds that allow a fragment size bigger the 6, allowing up
     *  to 3 cuts (or more) will yield the same set of fragments as allowing up to 2 cuts.
     * - Expected unique fragments: 10 (
     * c1ccc(cc1)C
     * c1ccc(cc1)CC
     * c1ccc(cc1)CCCC
     * c1cc(ccc1C)C
     * c1cc(ccc1C)CC
     * c1cc(ccc1C)CCCC
     * c1cc(ccc1CC)CC
     * c1cc(ccc1CC)CCCC
     * c1ccccc1
     * CCCC
     * )
     * </pre>
     */
    @Test
    void testSetExclusiveMaxTreeDepth() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("CCCCc1ccc(CCCC)cc1");

        // Define a standard SmilesGenerator for fragmenter instantiation
        SmilesGenerator standardSmilesGen = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);

        ExhaustiveFragmenter localFragmenter;

        localFragmenter = new ExhaustiveFragmenter(standardSmilesGen, 4, ExhaustiveFragmenter.Saturation.HYDROGEN_SATURATED_FRAGMENTS, Integer.SIZE - 1);
        localFragmenter.setInclusiveMaxTreeDepth(1);
        localFragmenter.generateFragments(mol);
        String[] fragsDepth1 = localFragmenter.getFragments();
        Assertions.assertEquals(0, fragsDepth1.length,
                "Expected 0 fragments when inclusiveMaxTreeDepth is 0 (allows 0 cuts) for 1,4-dibutylbenzene");

        localFragmenter = new ExhaustiveFragmenter(standardSmilesGen, 4, ExhaustiveFragmenter.Saturation.HYDROGEN_SATURATED_FRAGMENTS, Integer.SIZE - 1);
        localFragmenter.setInclusiveMaxTreeDepth(2);
        localFragmenter.generateFragments(mol);
        String[] fragsDepth2 = localFragmenter.getFragments();
        Assertions.assertEquals(4, fragsDepth2.length,
                "Expected 4 fragments when inclusiveMaxTreeDepth is 1 (allows up to 1 cut)");
        Assertions.assertTrue(
                hasItems(fragsDepth2, new String[] {
                        "CCCC",
                        "c1ccc(cc1)CCCC"
                })
        );

        localFragmenter = new ExhaustiveFragmenter(standardSmilesGen, 4, ExhaustiveFragmenter.Saturation.HYDROGEN_SATURATED_FRAGMENTS, Integer.SIZE - 1);
        localFragmenter.setInclusiveMaxTreeDepth(3);
        localFragmenter.generateFragments(mol);
        String[] fragsDepth3 = localFragmenter.getFragments();
        Assertions.assertEquals(10, fragsDepth3.length,
                "Expected 10 fragments when inclusiveMaxTreeDepth is 2 (allows up to 2 cuts)");
        Assertions.assertTrue(
                hasItems(fragsDepth3, new String[] {
                        "CCCC",
                        "c1ccc(cc1)CCCC",
                        "c1ccccc1"
                })
        );

        localFragmenter = new ExhaustiveFragmenter(standardSmilesGen, 4, ExhaustiveFragmenter.Saturation.HYDROGEN_SATURATED_FRAGMENTS, Integer.SIZE - 1);
        localFragmenter.setInclusiveMaxTreeDepth(4);
        localFragmenter.generateFragments(mol);
        String[] fragsDepth4 = localFragmenter.getFragments();
        Assertions.assertEquals(10, fragsDepth4.length,
                "Expected 10 fragments when inclusiveMaxTreeDepth is 3 (allows up to 3 cuts), same as max 2 cuts");
        Assertions.assertTrue(
                hasItems(fragsDepth4, new String[]{
                        "CCCC",
                        "c1ccc(cc1)CCCC",
                        "c1ccccc1"
                })
        );
    }

    // --- Complementary Molecule Tests ---

    /**
     * Tests correct functional group identification on an example molecule with
     * a disconnected structure.
     * This was not allowed in a previous version.
     */
    @Test
    void testDisconnectedMolecules() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C(CN(CC(=O)[O-])CC(=O)[O-])N(CC(=O)[O-])CC(=O)[O-].[Na+].[Na+].[Na+].[Na+]"); //Sodium edetate
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertTrue(
                hasItems(frags, new String[]{
                                "O=C([O-])CNCCNCC(=O)[O-]",
                                "O=C([O-])CNCC(=O)[O-]",
                                "O=C([O-])CN(C)CCN(C)C",
                                "O=C([O-])CNCCNC",
                                "O=C([O-])CN(CC(=O)[O-])CC"
                        }
                )
        );
    }

    /**
     * Testing a bigger molecule
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    void testBigMolecule1() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CC1=C(C(=CC=C1)NC2=CC=CC=C2C" +
                "(=O)NC(CCS(=O)C)C(=O)NC(C)C3=CC=C(C=C3)F)C"); //PubChem CID 118705975

        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertTrue(
                hasItems(
                        frags, new String[]{
                                "O=C(NCC)CCC",
                                "NC=1C=CC=CC1",
                                "O=C(N)CCCS(=O)C",
                                "FC=1C=CC(=CC1)C(N)C"
                        }
                )
        );
    }

    /**
     * Testing a bigger molecule
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    void testTestMoleculeUnsaturated() throws Exception {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("C1CCCCC1c1ccccc1"); //PubChem CID
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertTrue(hasItems(frags, new String[] {
                "[CH]1CCCCC1",
                "[c]1ccccc1"
        }));
    }

//    /**
//     * Testing a molecule with 31 splittable bonds (takes extremely long, maybe days)
//     *
//     * @throws Exception if anything goes wrong
//     */
//    @Test
//    void testIndexBigMolecule2() throws Exception {
//        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
//        IAtomContainer mol = smiPar.parseSmiles("C[C@]12CC[C@](CC1C3=CC(=O)C4[C@]5(CCC(C(C5CC[C@]4([C@@]3(CC2)C)C)(C)C)" +
//                "OC6C(C(C(C(O6)C(=O)N[C@H](CCC(=O)OC)C(=O)OC)O)O)OC7C(C(C(C(O7)C(=O)N[C@H](CCC(=O)OC)C(=O)OC)O)O)O)C)(C)C" +
//                "(=O)N[C@H](CCC(=O)OC)C(=O)OC"); // Pubchem CID 16396833
//        System.out.println(fragmenterSaturated.getSplitableBonds(mol).length);
//        fragmenterSaturated.generateFragments(mol);
//        String[] frags = fragmenterSaturated.getFragments();
//        Assertions.assertEquals(
//                Arrays.asList(frags),
//                hasItems(
//                        "O=CCNC(=O)c1ccccc1",
//                        "O=C(N)CNC(=O)c1ccccc1N",
//                        "O=C(NC)c1ccccc1N",
//                        "O=C(NCCC)c1ccccc1N",
//                        "O=CCNC(=O)c1ccccc1Nc2cccc(c2C)C",
//                        "O=C(N)CCCS(=O)C",
//                        "O=C(N)C(NC(=O)c1ccccc1)CCS(=O)C"
//                )
//        );
//    }

    // --utility --
    static boolean hasItems(String[] allFragments, String[] requiredFragments) {
        Set<String> allFragmentsSet = new HashSet<>(Arrays.asList(allFragments));
        for (String frag : requiredFragments) {
            if (!allFragmentsSet.contains(frag)) {
                return false;
            }
        }
        return true;
    }
}
