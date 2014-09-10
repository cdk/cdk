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

package org.openscience.cdk.smiles.smarts.parser;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Verifies stereo matching. We check the counts to ensure that
 * tetrahedral/geometric stereo isn't matching absolute values (i.e. R/S or
 * odd/even // parity from MDL molfile)
 *
 * @author John May
 * @cdk.module test-smarts
 */
public class SmartsStereoTest {

    @Test
    public void nonAbsoluteGeometric_trans() throws Exception {
        assertMatch("C/C=C/C", "C/C(CC)=C(CC)/C", 4, 2);
    }

    @Test
    public void nonAbsoluteGeometric_cis() throws Exception {
        assertMatch("C(/C)=C/C", "C/C(CC)=C(CC)/C", 4, 2);
    }

    @Test
    public void unspecifiedGeometric() throws Exception {
        assertMatch("C/C=C/?Cl", "CC=CCl", 1, 1);
        assertMatch("C/C=C/?Cl", "C/C=C/Cl", 1, 1);
        assertMatch("C/?C=C/Cl", "CC=CCl", 1, 1);
        assertMatch("C/?C=C/Cl", "C/C=C/Cl", 1, 1);
        assertMatch("C/C=C/?Cl", "CC=CCl", 1, 1);
        assertMatch("C/C=C/?Cl", "C/C=C/Cl", 1, 1);
    }

    @Test
    public void nonAbsoluteTetrahedral() throws Exception {
        assertMatch("C[C@](C)(C)C", "C[C@](CC)(CCC)CCCC", 12, 1);
        assertMatch("C[C@](C)(C)C", "C[C@@](CC)(CCC)CCCC", 12, 1);
    }

    @Test
    public void tetrahedralNegation_anticlockwise() throws Exception {
        assertMatch("[!@](C)(N)(O)CC", "C(C)(N)(O)CC", 1, 1);
        assertMatch("[!@](C)(N)(O)CC", "[C@@](C)(N)(O)CC", 1, 1);
        assertMatch("[!@](C)(N)(O)CC", "[C@](C)(N)(O)CC", 0, 0);
    }

    @Test
    public void tetrahedralNegation_clockwise() throws Exception {
        assertMatch("[!@@](C)(N)(O)CC", "C(C)(N)(O)CC", 1, 1);
        assertMatch("[!@@](C)(N)(O)CC", "[C@@](C)(N)(O)CC", 0, 0);
        assertMatch("[!@@](C)(N)(O)CC", "[C@](C)(N)(O)CC", 1, 1);
    }

    @Test
    public void tetrahedralUnspecified_clockwise() throws Exception {
        assertMatch("[@@?](C)(N)(O)CC", "C(C)(N)(O)CC", 1, 1);
        assertMatch("[@@?](C)(N)(O)CC", "[C@@](C)(N)(O)CC", 1, 1);
        assertMatch("[@@?](C)(N)(O)CC", "[C@](C)(N)(O)CC", 0, 0);
    }

    @Test
    public void tetrahedralUnspecified_anticlockwise() throws Exception {
        assertMatch("[@?](C)(N)(O)CC", "C(C)(N)(O)CC", 1, 1);
        assertMatch("[@?](C)(N)(O)CC", "[C@@](C)(N)(O)CC", 0, 0);
        assertMatch("[@?](C)(N)(O)CC", "[C@](C)(N)(O)CC", 1, 1);
    }

    @Test
    public void tetrahedral_or() throws Exception {
        assertMatch("C[@,@@](C)(C)C", "CC(CC)(CCC)CCCC", 0, 0);
        assertMatch("C[@,@@](C)(C)C", "C[C@](CC)(CCC)CCCC", 24, 1);
        assertMatch("C[@,@@](C)(C)C", "C[C@](CC)(CCC)CCCC", 24, 1);
    }

    @Test
    public void tetrahedral_and() throws Exception {
        assertMatch("C[@&@@](C)(C)C", "CC(CC)(CCC)CCCC", 0, 0);
        assertMatch("C[@&@@](C)(C)C", "C[C@](CC)(CCC)CCCC", 0, 0);
        assertMatch("C[@&@@](C)(C)C", "C[C@@](CC)(CCC)CCCC", 0, 0);
    }

    @Test
    public void tetrahedralAndSymbol_or() throws Exception {
        assertMatch("C[C@,Si@@](CC)(CCC)CCCC", "CC(CC)(CCC)CCCCC", 0, 0);
        assertMatch("C[C@,Si@@](CC)(CCC)CCCC", "C[Si](CC)(CCC)CCCCC", 0, 0);
        assertMatch("C[C@,Si@@](CC)(CCC)CCCC", "C[C@](CC)(CCC)CCCC", 1, 1);
        assertMatch("C[C@,Si@@](CC)(CCC)CCCC", "C[C@@](CC)(CCC)CCCC", 0, 0);
        assertMatch("C[C@,Si@@](CC)(CCC)CCCC", "C[Si@](CC)(CCC)CCCC", 0, 0);
        assertMatch("C[C@,Si@@](CC)(CCC)CCCC", "C[Si@@](CC)(CCC)CCCC", 1, 1);
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

    @Test
    public void tetrahedralImplicitH() throws Exception {
        assertMatch("[C@H](C)(N)O", "[C@@H](N)(C)O", 1, 1);
        assertMatch("[C@H](C)(N)O", "[C@H](N)(C)O", 0, 0);
        assertMatch("[C@H](C)(N)O", "C(N)(C)O", 0, 0);
    }

    @Test
    public void tetrahedralImplicitH_unspec() throws Exception {
        assertMatch("[C@?H](C)(N)O", "[C@@H](N)(C)O", 1, 1);
        assertMatch("[C@?H](C)(N)O", "[C@H](N)(C)O", 0, 0);
        assertMatch("[C@?H](C)(N)O", "C(N)(C)O", 1, 1);
    }

    static void assertMatch(SMARTSQueryTool sqt, IAtomContainer m, int hits, int usaHits) throws Exception {
        sqt.matches(m);
        assertThat(sqt.getMatchingAtoms().size(), is(hits));
        assertThat(sqt.getUniqueMatchingAtoms().size(), is(usaHits));
    }

    static void assertMatch(String smarts, String smiles, int hits, int usaHits) throws Exception {
        assertMatch(smarts(smarts), smiles(smiles), hits, usaHits);
    }

    static IAtomContainer smiles(String smiles) throws Exception {
        return sp.parseSmiles(smiles);
    }

    static SMARTSQueryTool smarts(String smarts) {
        return new SMARTSQueryTool(smarts, DefaultChemObjectBuilder.getInstance());
    }

    private static final SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());

}
