/*
 * Copyright (C) 2010 Rajarshi Guha <rajarshi.guha@gmail.com>
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
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

/**
 * Test exhaustive fragmenter.
 *
 * @cdk.module test-fragment
 */
class ExhaustiveFragmenterTest extends CDKTestCase {

    private static ExhaustiveFragmenter fragmenterSaturated;
    private static ExhaustiveFragmenter fragmenterUnsaturated;
    private static SmilesParser         smilesParser;

    @BeforeAll
    static void setup() {
        fragmenterSaturated = new ExhaustiveFragmenter(ExhaustiveFragmenter.Saturation.SATURATED_FRAGMENTS);
        fragmenterUnsaturated = new ExhaustiveFragmenter(ExhaustiveFragmenter.Saturation.UNSATURATED_FRAGMENTS);
        smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    }

    @Test
    void testEF1Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("CCC");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertEquals(0, frags.length);
    }

    @Test
    void testEF1Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("CCC");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertEquals(0, frags.length);
    }

    @Test
    void testEF2Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCC1");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertEquals(0, frags.length);
    }

    @Test
    void testEF2Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCC1");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertEquals(0, frags.length);
    }

    @Test
    void testEF3Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCCC1CC");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        MatcherAssert.assertThat(frags, is(new String[]{"[CH]1CCCCC1"}));
    }

    @Test
    void testEF3Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCCC1CC");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        org.hamcrest.MatcherAssert.assertThat(frags, is(new String[]{"C1CCCCC1"}));
    }

    @Test
    void testEF4Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1CC");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertNotNull(frags);
        MatcherAssert.assertThat(frags, is(new String[]{"[c]1ccccc1"}));
    }

    @Test
    void testEF4Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1CC");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertNotNull(frags);
        org.hamcrest.MatcherAssert.assertThat(frags, is(new String[]{"c1ccccc1"}));
    }


    @Test
    void testEF5Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1Cc1ccccc1");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertNotNull(frags);
        MatcherAssert.assertThat(Arrays.asList(frags), hasItems("[CH2]c1ccccc1", "[c]1ccccc1"));
        Assertions.assertNotNull(fragmenterUnsaturated.getFragmentsAsContainers());
        Assertions.assertEquals(2, fragmenterUnsaturated.getFragmentsAsContainers().length);
    }

    @Test
    void testEF5Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1Cc1ccccc1");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertNotNull(frags);
        org.hamcrest.MatcherAssert.assertThat(Arrays.asList(frags), hasItems("c1ccc(cc1)C", "c1ccccc1"));
        Assertions.assertNotNull(fragmenterSaturated.getFragmentsAsContainers());
        Assertions.assertEquals(2, fragmenterSaturated.getFragmentsAsContainers().length);
    }


    @Test
    void testEF6Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1c1ccccc1");
        fragmenterUnsaturated.generateFragments(mol);
        String[] frags = fragmenterUnsaturated.getFragments();
        Assertions.assertNotNull(frags);
        MatcherAssert.assertThat(frags, is(new String[]{"[c]1ccccc1"}));

        Assertions.assertNotNull(fragmenterUnsaturated.getFragmentsAsContainers());
        Assertions.assertEquals(1, fragmenterUnsaturated.getFragmentsAsContainers().length);

    }

    @Test
    void testEF6Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1c1ccccc1");
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertNotNull(frags);
        org.hamcrest.MatcherAssert.assertThat(frags, is(new String[]{"c1ccccc1"}));

        Assertions.assertNotNull(fragmenterSaturated.getFragmentsAsContainers());
        Assertions.assertEquals(1, fragmenterSaturated.getFragmentsAsContainers().length);
    }

    @Test
    void testEF7Unsaturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1(c2ccccc2)(CC(CC1)CCc1ccccc1)CC1C=CC=C1");
        fragmenterUnsaturated.generateFragments(mol);
        List<String> frags = Arrays.asList(fragmenterUnsaturated.getFragments());
        Assertions.assertNotNull(frags);
        // There is one additional fragment in comparison to the saturated version because there are following fragments:
        // [C]1CCC([CH2])C1
        // [CH2][C]1C[CH]CC1
        // these fragments only differ in the number of hydrogens bonded to their respective carbon atoms. So these
        // fragments would show up as one if saturated.
        Assertions.assertEquals(26, frags.size());

        Assertions.assertNotNull(fragmenterUnsaturated.getFragmentsAsContainers());
        Assertions.assertEquals(26, fragmenterUnsaturated.getFragmentsAsContainers().length);

        MatcherAssert.assertThat(frags, hasItems("[c]1ccccc1", "[CH2]CC1CCC(c2ccccc2)(CC3C=CC=C3)C1", "[CH2]C1CCC([CH2])(c2ccccc2)C1"));
    }

    @Test
    void testEF7Saturated() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1(c2ccccc2)(CC(CC1)CCc1ccccc1)CC1C=CC=C1");
        fragmenterSaturated.generateFragments(mol);
        List<String> frags = Arrays.asList(fragmenterSaturated.getFragments());
        Assertions.assertNotNull(frags);
        Assertions.assertEquals(25, frags.size());

        Assertions.assertNotNull(fragmenterSaturated.getFragmentsAsContainers());
        Assertions.assertEquals(25, fragmenterSaturated.getFragmentsAsContainers().length);

        org.hamcrest.MatcherAssert.assertThat(frags, hasItems("c1ccccc1", "c1ccc(cc1)C2(CCC(CC)C2)CC3C=CC=C3", "c1ccc(cc1)C2(C)CCC(C)C2"));
    }

    @Test
    void testMinSize() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCC1C2CCCCC2");
        fragmenterSaturated.setMinimumFragmentSize(6);
        fragmenterSaturated.generateFragments(mol);
        String[] frags = fragmenterSaturated.getFragments();
        Assertions.assertNotNull(frags);
        Assertions.assertEquals(1, frags.length);
        Assertions.assertTrue(frags[0].equals("C1CCCCC1"));
    }

    @Test
    void testEqualityOfSmilesAndContainers() throws Exception {
        SmilesGenerator smilesGenerator = new SmilesGenerator(SmiFlavor.UseAromaticSymbols | SmiFlavor.Unique);
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1CC(N)C(=O)O");
        fragmenterSaturated.generateFragments(mol);
        List<String> smilesFrags = Arrays.asList(fragmenterSaturated.getFragments());
        IAtomContainer[] containerFrags = fragmenterSaturated.getFragmentsAsContainers();
        for (IAtomContainer frag : containerFrags) {
            org.hamcrest.MatcherAssert.assertThat(smilesFrags, hasItems(smilesGenerator.create(frag)));
        }
    }
}
