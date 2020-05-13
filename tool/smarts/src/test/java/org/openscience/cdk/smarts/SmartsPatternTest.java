/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.smarts;

import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.VentoFoggia;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 */
public class SmartsPatternTest {

    IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();

    @Test
    public void isotopes() throws Exception {
        // FIXME SMARTS Grammar needs fixing/replacing [12] is not considered valid

        assertFalse(SmartsPattern.create("[12*]", bldr).matches(smi("C")));
        assertFalse(SmartsPattern.create("[12*]", bldr).matches(smi("[CH4]")));
        assertTrue(SmartsPattern.create("[12*]", bldr).matches(smi("[12CH4]")));
        assertFalse(SmartsPattern.create("[12*]", bldr).matches(smi("[13CH4]")));

        assertFalse(SmartsPattern.create("[13*]", bldr).matches(smi("C")));
        assertFalse(SmartsPattern.create("[13*]", bldr).matches(smi("[CH4]")));
        assertFalse(SmartsPattern.create("[13*]", bldr).matches(smi("[12CH4]")));
        assertTrue(SmartsPattern.create("[13*]", bldr).matches(smi("[13CH4]")));

        assertTrue(SmartsPattern.create("[0*]", bldr).matches(smi("C")));
        assertTrue(SmartsPattern.create("[0*]", bldr).matches(smi("[CH4]")));
        assertFalse(SmartsPattern.create("[0*]", bldr).matches(smi("[12CH4]")));
        assertFalse(SmartsPattern.create("[0*]", bldr).matches(smi("[13CH4]")));

//      Not possible with current grammar
//        assertFalse(SmartsPattern.create("[!0*]", bldr).matches(smi("C")));
//        assertFalse(SmartsPattern.create("[!0*]", bldr).matches(smi("[CH4]")));
//        assertTrue(SmartsPattern.create("[!0*]", bldr).matches(smi("[12CH4]")));
//        assertTrue(SmartsPattern.create("[!0*]", bldr).matches(smi("[13CH4]")));
    }

    @Test
    public void components() throws Exception {
        assertTrue(SmartsPattern.create("(O).(O)", bldr).matches(smi("O.O")));
        assertFalse(SmartsPattern.create("(O).(O)", bldr).matches(smi("OO")));
    }

    @Test
    public void stereochemistry() throws Exception {
        assertTrue(SmartsPattern.create("C[C@H](O)CC", bldr).matches(smi("C[C@H](O)CC")));
        assertFalse(SmartsPattern.create("C[C@H](O)CC", bldr).matches(smi("C[C@@H](O)CC")));
        assertFalse(SmartsPattern.create("C[C@H](O)CC", bldr).matches(smi("CC(O)CC")));
    }

    @Test
    public void smartsMatchingReaction() throws Exception {
        assertTrue(SmartsPattern.create("CC", bldr).matches(rsmi("CC>>")));
        assertTrue(SmartsPattern.create("CC", bldr).matches(rsmi(">>CC")));
        assertTrue(SmartsPattern.create("CC", bldr).matches(rsmi(">CC>")));
        assertFalse(SmartsPattern.create("CO", bldr).matches(rsmi(">>CC")));
    }

    @Test
    public void reactionSmartsMatchingReaction() throws Exception {
        assertTrue(SmartsPattern.create("CC>>", bldr).matches(rsmi("CC>>")));
        assertFalse(SmartsPattern.create("CC>>", bldr).matches(rsmi(">>CC")));
        assertFalse(SmartsPattern.create("CC>>", bldr).matches(rsmi(">CC>")));
    }

    @Test
    public void reactionGrouping() throws Exception {
        assertTrue(SmartsPattern.create("[Na+].[OH-]>>", bldr).matches(rsmi("[Na+].[OH-]>>")));
        assertTrue(SmartsPattern.create("[Na+].[OH-]>>", bldr).matches(rsmi("[Na+].[OH-]>> |f:0.1|")));
        assertTrue(SmartsPattern.create("([Na+].[OH-])>>", bldr).matches(rsmi("[Na+].[OH-]>> |f:0.1|")));
        // this one can't match because we don't know if NaOH is one component from the input smiles
        assertFalse(SmartsPattern.create("([Na+].[OH-])>>", bldr).matches(rsmi("[Na+].[OH-]>>")));
    }

    @Test public void noMaps() throws Exception {
        assertThat(SmartsPattern.create("C>>C", null).matchAll(rsmi("CC>>CC")).count(),
                   is(4));
    }

    @Test public void noMapsInQueryMapsInTargetIgnored() throws Exception {
        assertThat(SmartsPattern.create("C>>C", null).matchAll(rsmi("[C:7][C:8]>>[C:7][C:8]")).count(),
                   is(4));
    }

    @Test public void unpairedMapIsQueryIsIgnored() throws Exception {
        assertThat(SmartsPattern.create("[C:1]>>C", null).matchAll(rsmi("[CH3:7][CH3:8]>>[CH3:7][CH3:8]")).count(),
                   is(4));
        assertThat(SmartsPattern.create("C>>[C:1]", null).matchAll(rsmi("[CH3:7][CH3:8]>>[CH3:7][CH3:8]")).count(),
                   is(4));
    }

    @Test
    public void noMapsInTarget() throws Exception {
        assertThat(SmartsPattern.create("[C:1]>>[C:1]", null).matchAll(rsmi("C>>C")).count(),
                   is(0));
    }

    @Ignore("Not supported yet")
    public void optionalMapping() throws Exception {
        assertThat(SmartsPattern.create("[C:?1]>>[C:?1]", null).matchAll(rsmi("[CH3:7][CH3:8]>>[CH3:7][CH3:8]")).count(),
                   is(2));
        assertThat(SmartsPattern.create("[C:?1]>>[C:?1]", null).matchAll(rsmi("CC>>CC")).count(),
                   is(4));
    }
    @Test
    public void mappedMatch() throws Exception {
        assertThat(SmartsPattern.create("[C:1]>>[C:1]", null).matchAll(rsmi("[CH3:7][CH3:8]>>[CH3:7][CH3:8]")).count(),
                   is(2));
    }

    @Test
    public void mismatchedQueryMapsIgnored() throws Exception {
        assertThat(SmartsPattern.create("[C:1]>>[C:2]", null).matchAll(rsmi("[CH3:7][CH3:8]>>[CH3:7][CH3:8]")).count(),
                   is(4));
    }

    // map :1 in query binds only to :7 in target
    @Test public void atomMapsWithOrLogic1() throws Exception {
        assertThat(SmartsPattern.create("[C:1][C:1]>>[C:1]", null).matchAll(rsmi("[CH3:7][CH3:7]>>[CH3:7][CH3:7]")).count(),
                   is(4));
    }

    // map :1 in query binds to :7 or :8 in target
    @Test public void atomMapsWithOrLogic2() throws Exception {
        assertThat(SmartsPattern.create("[C:1][C:1]>>[C:1]", null).matchAll(rsmi("[CH3:7][CH3:8]>>[CH3:7][CH3:8]")).count(),
                   is(4));
    }

    // map :1 in query binds only to :7 in target
    @Test public void atomMapsWithOrLogic3() throws Exception {
        assertThat(SmartsPattern.create("[C:1][C:1]>>[C:1]", null).matchAll(rsmi("[CH3:7][CH3:7]>>[CH3:7][CH3:8]")).count(),
                   is(2));
    }

    @Test public void CCBondForming() throws Exception {
        assertThat(SmartsPattern.create("([C:1]).([C:2])>>[C:1][C:2]", null)
                                .matchAll(rsmi("[C-:13]#[N:14].[K+].[CH:3]1=[CH:4][C:5](=[CH:11][CH:12]=[C:2]1[CH2:1]Br)[C:6](=[O:10])[CH:7]2[CH2:8][CH2:9]2>>[CH:3]1=[CH:4][C:5](=[CH:11][CH:12]=[C:2]1[CH2:1][C:13]#[N:14])[C:6](=[O:10])[CH:7]2[CH2:8][CH2:9]2 |f:0.1|")).count(),
                   is(2));
    }

    @Test
    public void matchProductStereo() throws Exception {
        assertThat(SmartsPattern.create(">>C[C@H](CC)[C@H](CC)O")
                                .matchAll(rsmi(">>C[C@H](CC)[C@H](CC)O"))
                                .countUnique(),
                   is(1));
    }

    @Test
    public void stereo_ring_closures() throws Exception {
        Pattern ptrn = SmartsPattern.create("[C@@]1(O[C@@]([C@@]([C@]([C@]1(C)O)(C)O)(O)C)(O)C)(O)C");
        assertTrue(ptrn.matches(smi("[C@@]1(O[C@@]([C@@]([C@]([C@]1(C)O)(C)O)(O)C)(O)C)(O)C")));
    }

    @Test
    public void hasIsotope() throws Exception {
        Pattern ptrn = SmartsPattern.create("[!0]");
        assertFalse(ptrn.matches(smi("C")));
        assertTrue(ptrn.matches(smi("[12C]")));
        assertTrue(ptrn.matches(smi("[13C]")));
    }

    @Test
    public void hIsotope() throws Exception {
        Pattern ptrn = SmartsPattern.create("[2#1,3#1]");
        assertFalse(ptrn.matches(smi("[H][H]")));
        assertTrue(ptrn.matches(smi("[2H]")));
        assertTrue(ptrn.matches(smi("[3H]")));
    }

    /**
     * Ensure a class cast exception is not thrown when matching stereochemistry.
     * @cdk.bug 1358
     */
    @Test
    public void bug1358() throws Exception {
        Pattern ptrn = SmartsPattern.create("[$([*@](~*)(~*)(*)*),$([*@H](*)(*)*),$([*@](~*)(*)*)]");
        assertFalse(ptrn.matches(smi("N#CN/C(=N/CCSCC=1N=CNC1C)NC")));
    }

    private void assertMatch(String sma, String smiles, int numHits, int uniqNumHits) throws Exception {
        Pattern  ptrn     = SmartsPattern.create(sma);
        Mappings mappings = ptrn.matchAll(smi(smiles));
        assertThat(mappings.count(), is(numHits));
        assertThat(mappings.countUnique(), is(uniqNumHits));
    }

    @Test
    public void recursiveGeometric_trans() throws Exception {
        assertMatch("[$(*/C=C/*)]", "C/C=C/C", 2, 2);
        assertMatch("[$(*/C=C/*)]", "F/C=C/Cl", 2, 2);
        assertMatch("[$(*/C=C/*)]", "CC=CC", 0, 0);
        assertMatch("[$(*/C=C/*)]", "FC=CCl", 0, 0);
        assertMatch("[$(*/C=C/*)]", "C/C=C\\C", 0, 0);
        assertMatch("[$(*/C=C/*)]", "F/C=C\\Cl", 0, 0);
    }

    @Test
    public void recursiveGeometric_cis() throws Exception {
        assertMatch("[$(C(/*)=C/*)]", "C/C=C/C", 0, 0);
        assertMatch("[$(C(/*)=C/*)]", "F/C=C/Cl", 0, 0);
        assertMatch("[$(C(/*)=C/*)]", "CC=CC", 0, 0);
        assertMatch("[$(C(/*)=C/*)]", "FC=CCl", 0, 0);
        assertMatch("[$(C(/*)=C/*)]", "C/C=C\\C", 2, 2);
        assertMatch("[$(C(/*)=C/*)]", "F/C=C\\Cl", 2, 2);
    }

    @Test
    public void recursiveTetrahedral() throws Exception {
        assertMatch("[$([C@](C)(CC)(N)O)]", "C[C@@](N)(CC)O", 1, 1);
        assertMatch("[$([C@](C)(CC)(N)O)]", "C[C@](N)(CC)O", 0, 0);
        assertMatch("[$([C@](C)(CC)(N)O)]", "CC(N)(CC)O", 0, 0);
    }

    IAtomContainer smi(String smi) throws Exception {
        return new SmilesParser(bldr).parseSmiles(smi);
    }

    IReaction rsmi(String smi) throws Exception {
        return new SmilesParser(bldr).parseReactionSmiles(smi);
    }
}
