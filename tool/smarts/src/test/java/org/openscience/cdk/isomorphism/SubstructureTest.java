/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
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

package org.openscience.cdk.isomorphism;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openscience.cdk.CDKConstants.TITLE;

/**
 * A collection of substructure integration tests. These give a high-level view
 * of what we expect to match. To run these tests the subclass should {@link
 * #create(org.openscience.cdk.interfaces.IAtomContainer)} a pattern for the
 * input.
 *
 * @author John May
 * @cdk.module test-smarts
 */
public abstract class SubstructureTest {

    abstract Pattern create(IAtomContainer container);

    // ensure edges can be absent in the target
    @Test
    public void monomorphism1() throws Exception {
        assertMatch(smi("CCC"), smi("C1CC1"), 6);
    }

    @Test
    public void monomorphism2() throws Exception {
        assertMatch(smi("C1CCCCCCCCC1"), smi("C1CCC2CCCCC2C1"), 20);
    }

    @Test
    public void cyclopropane() throws Exception {
        assertMismatch(smi("C1CC1"), smi("CC(C)C"));
    }

    @Test
    public void symmetric() throws Exception {
        assertMatch(sma("C**C"), smi("CSSC"));
        assertMismatch(sma("C**C"), smi("SCCS"));
    }

    @Test
    public void disconnectedQuery() throws Exception {
        assertMatch(smi("C.C"), smi("CC"), 2);
    }

    @Test
    public void disconnectedTarget() throws Exception {
        assertMatch(smi("C1CC1"), smi("C1CC1.C1CC1"), 12);
    }

    @Test
    public void disconnected() throws Exception {
        assertMatch(smi("C1CC1.C1CC1"), smi("C1CC1.C1CC1"), 72);
    }

    // original VF algorithm can't find both of these
    @Test
    public void disconnected2() throws Exception {
        assertMatch(smi("O.O"), smi("OO"), 2);
        assertMatch(smi("O.O"), smi("OCO"), 2);
        assertMatch(smi("O.O"), smi("OCCO"), 2);
        assertMatch(smi("O.O"), smi("OCCCO"), 2);
    }

    @Test
    public void tetrahedral_match() throws Exception {
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@](C)(N)(O)CC"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@](C)(O)(CC)N"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@](C)(CC)(N)(O)"));

        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@@](C)(O)(N)CC"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@@](C)(CC)(O)N"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@@](C)(N)(CC)(O)"));

        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@](N)(O)(C)CC"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@](N)(CC)(O)C"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@](N)(C)(CC)O"));

        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@@](N)(C)(O)CC"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@@](N)(O)(CC)C"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@@](N)(CC)(C)O"));

        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@](O)(CC)(C)N"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@](O)(N)(CC)C"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@](O)(C)(N)(CC)"));

        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@@](O)(C)(CC)N"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@@](O)(CC)(N)C"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@@](O)(N)(C)(CC)"));

        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@](CC)(C)(O)N"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@](CC)(N)(C)O"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@](CC)(O)(N)C"));

        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@@](CC)(O)(C)N"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@@](CC)(C)(N)O"));
        assertMatch(smi("[C@](C)(N)(O)CC"), smi("[C@@](CC)(N)(O)C"));
    }

    @Test
    public void tetrahedral_mismatch() throws Exception {
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@](C)(N)(O)CC"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@](C)(O)(CC)N"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@](C)(CC)(N)(O)"));

        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@@](C)(O)(N)CC"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@@](C)(CC)(O)N"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@@](C)(N)(CC)(O)"));

        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@](N)(O)(C)CC"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@](N)(CC)(O)C"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@](N)(C)(CC)O"));

        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@@](N)(C)(O)CC"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@@](N)(O)(CC)C"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@@](N)(CC)(C)O"));

        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@](O)(CC)(C)N"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@](O)(N)(CC)C"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@](O)(C)(N)(CC)"));

        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@@](O)(C)(CC)N"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@@](O)(CC)(N)C"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@@](O)(N)(C)(CC)"));

        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@](CC)(C)(O)N"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@](CC)(N)(C)O"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@](CC)(O)(N)C"));

        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@@](CC)(O)(C)N"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@@](CC)(C)(N)O"));
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("[C@@](CC)(N)(O)C"));
    }

    @Test
    public void tetrahedral_match_implicit_h() throws Exception {
        assertMatch(smi("[C@H](C)(N)(O)"), smi("[C@H](C)(N)(O)"));
        assertMatch(smi("[C@H](C)(N)(O)"), smi("[C@]([H])(C)(N)(O)"));
        assertMatch(smi("[C@H](C)(N)(O)"), smi("[C@@](C)([H])(N)(O)"));
    }

    @Test
    public void tetrahedral_mismatch_implicit_h() throws Exception {
        assertMismatch(smi("[C@H](C)(N)(O)"), smi("[C@@H](C)(N)(O)"));
        assertMismatch(smi("[C@H](C)(N)(O)"), smi("[C@@]([H])(C)(N)(O)"));
        assertMismatch(smi("[C@H](C)(N)(O)"), smi("[C@](C)([H])(N)(O)"));
    }

    @Test
    public void tetrahedral_match_sulfoxide() throws Exception {
        assertMatch(smi("[S@](=O)(C)CC"), smi("[S@](=O)(C)CC"));
        assertMatch(smi("[S@](=O)(C)CC"), smi("[S@](C)(CC)(=O)"));
        assertMatch(smi("[S@](=O)(C)CC"), smi("[S@](CC)(=O)C"));
        assertMatch(smi("[S@](=O)(C)CC"), smi("[S@@](C)(=O)CC"));
        assertMatch(smi("[S@](=O)(C)CC"), smi("[S@@](=O)(CC)C"));
        assertMatch(smi("[S@](=O)(C)CC"), smi("[S@@](CC)(C)=O"));
    }

    @Test
    public void tetrahedral_mismatch_sulfoxide() throws Exception {
        assertMismatch(smi("[S@@](=O)(C)CC"), smi("[S@](=O)(C)CC"));
        assertMismatch(smi("[S@@](=O)(C)CC"), smi("[S@](C)(CC)(=O)"));
        assertMismatch(smi("[S@@](=O)(C)CC"), smi("[S@](CC)(=O)C"));
        assertMismatch(smi("[S@@](=O)(C)CC"), smi("[S@@](C)(=O)CC"));
        assertMismatch(smi("[S@@](=O)(C)CC"), smi("[S@@](=O)(CC)C"));
        assertMismatch(smi("[S@@](=O)(C)CC"), smi("[S@@](CC)(C)=O"));
    }

    @Test
    public void tetrahedral_missing_in_query() throws Exception {
        assertMatch(smi("C(C)(N)(O)CC"), smi("[C@@](C)(N)(O)CC"));
    }

    @Test
    public void tetrahedral_missing_in_target() throws Exception {
        assertMismatch(smi("[C@@](C)(N)(O)CC"), smi("C(C)(N)(O)CC"));
    }

    @Test
    public void tetrahedral_count() throws Exception {
        // we can map any witch way 4 neighbours but 2 configuration so (4!/2) = 12
        assertMatch(smi("[C@](C)(C)(C)C"), smi("[C@](C)(CC)(CCC)CCCC"), 12);
        assertMatch(smi("[C@@](C)(C)(C)C"), smi("[C@](C)(CC)(CCC)CCCC"), 12);
        assertMatch(smi("[C@](C)(C)(C)C"), smi("[C@@](C)(CC)(CCC)CCCC"), 12);
        assertMatch(smi("[C@@](C)(C)(C)C"), smi("[C@@](C)(CC)(CCC)CCCC"), 12);
    }

    @Test
    public void geometric_trans_match() throws Exception {
        assertMatch(smi("F/C=C/F"), smi("F/C=C/F"));
        assertMatch(smi("F/C=C/F"), smi("F\\C=C\\F"));
        // shouldn't mater which substituents are used
        assertMatch(smi("F/C=C/F"), smi("F/C(/[H])=C/F"));
        assertMatch(smi("F/C=C/F"), smi("FC(/[H])=C/F"));
        assertMatch(smi("F/C=C/F"), smi("F/C=C([H])/F"));
        assertMatch(smi("F/C=C/F"), smi("F/C=C(\\[H])F"));
        assertMatch(smi("F/C=C/F"), smi("FC(/[H])=C(\\[H])F"));
        // or the order is different
        assertMatch(smi("F/C=C/F"), smi("C(\\F)=C/F"));
        assertMatch(smi("F/C=C/F"), smi("C(/F)=C\\F"));
    }

    @Test
    public void geometric_cis_match() throws Exception {
        assertMatch(smi("F/C=C\\F"), smi("F/C=C\\F"));
        assertMatch(smi("F/C=C\\F"), smi("F\\C=C/F"));
        assertMatch(smi("F\\C=C/F"), smi("F/C=C\\F"));
        assertMatch(smi("F\\C=C/F"), smi("F\\C=C/F"));
        // shouldn't mater which substituents are used
        assertMatch(smi("F/C=C\\F"), smi("F/C(/[H])=C\\F"));
        assertMatch(smi("F/C=C\\F"), smi("FC(/[H])=C\\F"));
        assertMatch(smi("F/C=C\\F"), smi("F/C=C([H])\\F"));
        assertMatch(smi("F/C=C\\F"), smi("F/C=C(/[H])F"));
        assertMatch(smi("F/C=C\\F"), smi("FC(/[H])=C(/[H])F"));
        // or the order is different
        assertMatch(smi("F/C=C\\F"), smi("C(\\F)=C\\F"));
        assertMatch(smi("F/C=C\\F"), smi("C(/F)=C/F"));
    }

    @Test
    public void geometric_trans_mismatch() throws Exception {
        assertMismatch(smi("F/C=C/F"), smi("F/C=C\\F"));
        assertMismatch(smi("F/C=C/F"), smi("F\\C=C/F"));
        assertMismatch(smi("F\\C=C\\F"), smi("F/C=C\\F"));
        assertMismatch(smi("F\\C=C\\F"), smi("F\\C=C/F"));
    }

    @Test
    public void geometric_cis_mismatch() throws Exception {
        assertMismatch(smi("F/C=C\\F"), smi("F/C=C/F"));
        assertMismatch(smi("F/C=C\\F"), smi("F\\C=C\\F"));
        assertMismatch(smi("F\\C=C/F"), smi("F/C=C/F"));
        assertMismatch(smi("F\\C=C/F"), smi("F\\C=C\\F"));
    }

    @Test
    public void geometric_missing_in_query() throws Exception {
        assertMatch(smi("FC=CF"), smi("F/C=C/F"));
        assertMatch(smi("FC=CF"), smi("F\\C=C\\F"));
        assertMatch(smi("FC=CF"), smi("F\\C=C/F"));
        assertMatch(smi("FC=CF"), smi("F/C=C\\F"));
    }

    @Test
    public void geometric_missing_in_target() throws Exception {
        assertMismatch(smi("F/C=C/F"), smi("FC=CF"));
        assertMismatch(smi("F/C=C\\F"), smi("FC=CF"));
        assertMismatch(smi("F\\C=C/F"), smi("FC=CF"));
        assertMismatch(smi("F\\C=C\\F"), smi("FC=CF"));
    }

    @Test
    public void geometric_count() throws Exception {
        assertMatch(smi("C/C=C/C"), smi("CC(/CC)=C(/CC)C"), 4);
        assertMatch(smi("C/C=C\\C"), smi("CC(/CC)=C(/CC)C"), 4);
        assertMatch(smi("C\\C=C\\C"), smi("CC(/CC)=C(/CC)C"), 4);
        assertMatch(smi("C\\C=C/C"), smi("CC(/CC)=C(/CC)C"), 4);
    }

    @Test
    public void cubane_automorphisms() throws Exception {
        assertMatch(smi("C12C3C4C1C1C2C3C41"), smi("C12C3C4C1C1C2C3C41"), 48);
    }

    @Test
    public void fullerene_c60() throws Exception {
        assertMatch(
                smi("C1CCCCC1"),
                smi("C12C3C4C5C1C1C6C7C2C2C8C3C3C9C4C4C%10C5C5C1C1C6C6C%11C7C2C2C7C8C3C3C8C9C4C4C9C%10C5C5C1C1C6C6C%11C2C2C7C3C3C8C4C4C9C5C1C1C6C2C3C41"),
                240);
    }

    @Test
    public void fullerene_c70() throws Exception {
        assertMatch(
                smi("C1CCCCC1"),
                smi("C12C3C4C5C1C1C6C7C5C5C8C4C4C9C3C3C%10C2C2C1C1C%11C%12C%13C%14C%15C%16C%17C%18C%19C%20C%16C%16C%14C%12C%12C%14C%21C%22C(C%20C%16%14)C%14C%19C%16C(C4C8C(C%18%16)C4C%17C%15C(C7C54)C%13C61)C1C%14C%22C(C3C91)C1C%21C%12C%11C2C%101"),
                300);
    }

    @Category(SlowTest.class)
    @Test
    public void fullerene_c70_automorphisms() throws Exception {
        assertMatch(
                smi("C12C3C4C5C1C1C6C7C5C5C8C4C4C9C3C3C%10C2C2C1C1C%11C%12C%13C%14C%15C%16C%17C%18C%19C%20C%16C%16C%14C%12C%12C%14C%21C%22C(C%20C%16%14)C%14C%19C%16C(C4C8C(C%18%16)C4C%17C%15C(C7C54)C%13C61)C1C%14C%22C(C3C91)C1C%21C%12C%11C2C%101"),
                smi("C12C3C4C5C1C1C6C7C5C5C8C4C4C9C3C3C%10C2C2C1C1C%11C%12C%13C%14C%15C%16C%17C%18C%19C%20C%16C%16C%14C%12C%12C%14C%21C%22C(C%20C%16%14)C%14C%19C%16C(C4C8C(C%18%16)C4C%17C%15C(C7C54)C%13C61)C1C%14C%22C(C3C91)C1C%21C%12C%11C2C%101"),
                20);
    }

    @Test
    public void ferrocene_automorphisms_disconnected() throws Exception {
        assertMatch(smi("[Fe].C1CCCC1.C1CCCC1"), smi("[Fe].C1CCCC1.C1CCCC1"), 200);
    }

    @Test
    public void ferrocene_automorphisms() throws Exception {
        assertMatch(smi("[Fe]123456789C%10C1C2C3C4%10.C51C6C7C8C91"), smi("[Fe]123456789C%10C1C2C3C4%10.C51C6C7C8C91"),
                200);
    }

    @Test
    public void butanoylurea() throws Exception {
        assertMatch(smi("CCCC(=O)NC(N)=O"), smi("CCC(Br)(CC)C(=O)NC(=O)NC(C)=O"), 2);
    }

    @Test
    public void upgradeHydrogen() throws Exception {
        assertMatch(smi("CC[C@@H](C)O"), smi("CC[C@](C)([H])O"), 1);
    }

    @Test
    public void erm() throws Exception {
        assertMismatch(smi("CC[C@@H](C)O"), smi("CC[C@](C)(N)O"));
    }

    // doesn't matter if the match takes place but it should not cause and error
    // if the query is larger than the target
    @Test
    public void largerQuery() throws Exception {
        assertMismatch(smi("CCCC"), smi("CC"));
    }

    @Test
    public void emptyQuery() throws Exception {
        assertMismatch(smi(""), smi("[H][H]"));
    }

    @Test
    public void emptyTarget() throws Exception {
        assertMismatch(smi("[H][H]"), smi(""));
    }

    void assertMatch(IAtomContainer query, IAtomContainer target, int count) {
        assertThat(query.getProperty(TITLE) + " should match " + target.getProperty(TITLE) + " " + count + " times",
                create(query).matchAll(target).stereochemistry().count(), is(count));
    }

    void assertMatch(IAtomContainer query, IAtomContainer target) {
        assertTrue(query.getProperty(TITLE) + " should match " + target.getProperty(TITLE),
                create(query).matches(target));
    }

    void assertMismatch(IAtomContainer query, IAtomContainer target) {
        assertFalse(query.getProperty(TITLE) + " should not matched " + target.getProperty(TITLE), create(query)
                .matches(target));
    }

    private static final SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());

    // create a container from a smiles string
    IAtomContainer smi(String smi) throws Exception {
        IAtomContainer container = sp.parseSmiles(smi);
        container.setProperty(TITLE, smi);
        return container;
    }

    // create a query container from a smarts pattern
    // Note: only use simple constructs! the target properties will not
    // currently be initialised. avoid aromaticity, rings etc.
    IAtomContainer sma(String sma) throws Exception {
        IAtomContainer container = SMARTSParser.parse(sma, SilentChemObjectBuilder.getInstance());
        container.setProperty(TITLE, sma);
        return container;
    }
}
