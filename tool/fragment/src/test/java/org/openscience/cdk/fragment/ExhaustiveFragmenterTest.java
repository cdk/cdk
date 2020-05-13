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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
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
public class ExhaustiveFragmenterTest extends CDKTestCase {

    static ExhaustiveFragmenter fragmenter;
    static SmilesParser         smilesParser;

    @BeforeClass
    public static void setup() {
        fragmenter = new ExhaustiveFragmenter();
        smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    }

    @Test
    public void testEF1() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("CCC");
        fragmenter.generateFragments(mol);
        String[] frags = fragmenter.getFragments();
        Assert.assertEquals(0, frags.length);
    }

    @Test
    public void testEF2() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCC1");
        fragmenter.generateFragments(mol);
        String[] frags = fragmenter.getFragments();
        Assert.assertEquals(0, frags.length);
    }

    @Test
    public void testEF3() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCCC1CC");
        fragmenter.generateFragments(mol);
        String[] frags = fragmenter.getFragments();
        org.hamcrest.MatcherAssert.assertThat(frags, is(new String[]{"C1CCCCC1"}));
    }

    @Test
    public void testEF4() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1CC");
        fragmenter.generateFragments(mol);
        String[] frags = fragmenter.getFragments();
        Assert.assertNotNull(frags);
        org.hamcrest.MatcherAssert.assertThat(frags, is(new String[]{"c1ccccc1"}));
    }

    @Test
    public void testEF5() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1Cc1ccccc1");
        fragmenter.generateFragments(mol);
        String[] frags = fragmenter.getFragments();
        Assert.assertNotNull(frags);
        org.hamcrest.MatcherAssert.assertThat(Arrays.asList(frags), hasItems("c1ccc(cc1)C", "c1ccccc1"));
        Assert.assertNotNull(fragmenter.getFragmentsAsContainers());
        Assert.assertEquals(2, fragmenter.getFragmentsAsContainers().length);

    }

    @Test
    public void testEF6() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1c1ccccc1");
        fragmenter.generateFragments(mol);
        String[] frags = fragmenter.getFragments();
        Assert.assertNotNull(frags);
        org.hamcrest.MatcherAssert.assertThat(frags, is(new String[]{"c1ccccc1"}));

        Assert.assertNotNull(fragmenter.getFragmentsAsContainers());
        Assert.assertEquals(1, fragmenter.getFragmentsAsContainers().length);

    }

    @Test
    public void testEF7() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1(c2ccccc2)(CC(CC1)CCc1ccccc1)CC1C=CC=C1");
        fragmenter.generateFragments(mol);
        List<String> frags = Arrays.asList(fragmenter.getFragments());
        Assert.assertNotNull(frags);
        Assert.assertEquals(25, frags.size());

        Assert.assertNotNull(fragmenter.getFragmentsAsContainers());
        Assert.assertEquals(25, fragmenter.getFragmentsAsContainers().length);

        org.hamcrest.MatcherAssert.assertThat(frags, hasItems("c1ccccc1", "c1ccc(cc1)C2(CCC(CC)C2)CC3C=CC=C3", "c1ccc(cc1)C2(C)CCC(C)C2"));
    }

    @Test
    public void testMinSize() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1CCCC1C2CCCCC2");
        fragmenter.setMinimumFragmentSize(6);
        fragmenter.generateFragments(mol);
        String[] frags = fragmenter.getFragments();
        Assert.assertNotNull(frags);
        Assert.assertEquals(1, frags.length);
        Assert.assertTrue(frags[0].equals("C1CCCCC1"));
    }
}
