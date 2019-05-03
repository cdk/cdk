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

package org.openscience.cdk.graph;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.BitSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Maximum matching is not specific to kekulisation but it serves as a good
 * demonstration. The provission of a subset to the matching inicates the atom
 * indicies we know must be adjacent to a pi bond.
 *
 * @author John May
 * @cdk.module test-standard
 */
public final class EdmondsMaximumMatchingTest {

    private IChemObjectBuilder bldr   = SilentChemObjectBuilder.getInstance();
    private SmilesParser       smipar = new SmilesParser(bldr);

    @Test
    public void benzene() throws Exception {
        Matching m = matching("c1ccccc1");
        assertMatch(m, 0, 1);
        assertMatch(m, 2, 3);
        assertMatch(m, 4, 5);
    }

    @Test
    public void fulvelene() throws Exception {
        Matching m = matching("c1cccc1c1cccc1");
        assertMatch(m, 0, 1);
        assertMatch(m, 2, 3);
        assertMatch(m, 4, 5);
        assertMatch(m, 6, 7);
        assertMatch(m, 8, 9);
    }

    @Test
    public void quinone() throws Exception {
        Matching m = matching("oc1ccc(o)cc1");
        assertMatch(m, 0, 1);
        assertMatch(m, 2, 3);
        assertMatch(m, 4, 5);
        assertMatch(m, 6, 7);
    }

    @Test
    public void azulene() throws Exception {
        Matching m = matching("c1cc2cccccc2c1");
        assertMatch(m, 0, 1);
        assertMatch(m, 2, 3);
        assertMatch(m, 4, 5);
        assertMatch(m, 6, 7);
        assertMatch(m, 8, 9);
    }

    @Test
    public void pyrrole() throws Exception {
        // the nitrogen (index=0) does not need any double bonds
        Matching m = matching("[nH]1cccc1", 1, 2, 3, 4);
        assertMatch(m, 1, 2);
        assertMatch(m, 3, 4);
    }

    @Test
    public void furane() throws Exception {
        // the oxygen (index=0) does not need any double bonds
        Matching m = matching("o1cccc1", 1, 2, 3, 4);
        assertMatch(m, 1, 2);
        assertMatch(m, 3, 4);
    }

    @Test
    public void acyclic() throws Exception {
        Matching m = matching("cccccc");
        assertMatch(m, 0, 1);
        assertMatch(m, 2, 3);
        assertMatch(m, 4, 5);
    }

    @Test
    public void adenine() throws Exception {
        // the nitroges (index 0 and 6) do need any double bonds
        Matching m = matching("Nc1ncnc2[nH]cnc12", 1, 2, 3, 4, 5, 7, 8, 9);
        assertMatch(m, 1, 2);
        assertMatch(m, 3, 4);
        assertMatch(m, 5, 9);
        assertMatch(m, 7, 8);
    }

    @Test
    public void caffeine() throws Exception {
        // 0, 1, 5, 9, 10 do not need any double bonds
        Matching m = matching("Cn1cnc2n(C)c(=O)n(C)c(=O)c12", 2, 3, 4, 7, 8, 11, 12, 13);
        assertMatch(m, 2, 3);
        assertMatch(m, 4, 13);
        assertMatch(m, 7, 8); // C=O was refound
        assertMatch(m, 11, 12); // C=O was refound
    }

    /*
     * These two large cases show why it's benifical to seed the matching with
     * and arbitary matching first before maximising it. All matched edges are
     * succesive.
     */

    @Test
    public void fullerene_C60() throws Exception {
        Matching m = matching("c12c3c4c5c1c1c6c7c2c2c8c3c3c9c4c4c%10c5c5c1c1c6c6c%11c7c2c2c7c8c3c3c8c9c4c4c9c%10c5c5c1c1c6c6c%11c2c2c7c3c3c8c4c4c9c5c1c1c6c2c3c41");
        for (int i = 0; i < 60; i += 2)
            assertMatch(m, i, i + 1);
    }

    @Test
    public void graphene() throws Exception {
        Matching m = matching("c1cc2cc3cc4cc5cc6cc7cc8cc9cc%10cc%11cc%12cc%13cc%14cc%15cc%16ccc%17ccc%18c%19ccc%20c%21ccc%22c%23ccc%24c%25ccc%26c%27ccc%28c%29ccc%30c%31cccc%32cc%33cc%34cc%35cc%36cc%37cc%38cc%39cc%40cc%41cc%42cc%43cc%44cc%45cc%46ccc%47ccc%48c%49ccc%50c%51ccc%52c%53ccc%54c%55ccc%56c%57ccc%58c%59ccc%60c(c1)c2c1c3c2c4c3c5c4c6c5c7c6c8c7c9c8c%10c9c%11c%10c%12c%11c%13c%12c%14c%13c%15c%14c%16c%17c%18c%15c%16c%19c%20c%17c%18c%21c%22c%19c%20c%23c%24c%21c%22c%25c%26c%23c%24c%27c%28c%25c%26c%29c%30c%27c(c%31%32)c%33c%28c%34c%29c%35c%30c%36c%31c%37c%32c%38c%33c%39c%34c%40c%35c%41c%36c%42c%37c%43c%38c%44c%39c%45c%40c%46c%47c%48c%41c%42c%49c%50c%43c%44c%51c%52c%45c%46c%53c%54c%47c%48c%55c%56c%49c%50c%57c%58c%51c%52c%59c%60c1c1c2c2c3c3c4c4c5c5c6c6c7c7c8c8c9c9c%10c%10c%11c%11c%12c%12c%13c(c%14%15)c%13c%16c%17c%14c%15c%18c%19c%16c%17c%20c%21c%18c%19c%22c%23c%20c%21c%24c%25c%22c%23c%26c%27c%28c%24c%29c%25c%30c%26c%31c%27c%32c%28c%33c%29c%34c%30c%35c%31c%36c%32c%37c%33c%38c%34c%39c(c%40%41)c%35c%42c%43c%36c%37c%44c%45c%38c%39c%46c%47c%40c%41c%48c%49c%42c%43c%50c%51c%44c(c%521)c2c1c3c2c4c3c5c4c6c5c7c6c8c7c9c8c%10c9c%11c%10c%12c%13c%14c%11c%12c%15c%16c%13c%14c%17c%18c%15c%16c%19c%20c%17c%18c%21c%22c%19c(c%23%24)c%25c%20c%26c%21c%27c%22c%28c%23c%29c%24c%30c%25c%31c%26c%32c%27c%33c%28c%34c%35c%36c%29c%30c%37c%38c%31c%32c%39c%40c%33c%34c%41c%42c%35c%36c%43c%44c1c1c2c2c3c3c4c4c5c5c6c6c7c7c8c8c9c(c%10%11)c9c%12c%13c%10c%11c%14c%15c%12c%13c%16c%17c%14c%15c%18c%19c%20c%16c%21c%17c%22c%18c%23c%19c%24c%20c%25c%21c%26c%22c%27c(c%28%29)c%23c%30c%31c%24c%25c%32c%33c%26c%27c%34c%35c%28c(c%361)c2c1c3c2c4c3c5c4c6c5c7c6c8c9c%10c7c8c%11c%12c9c%10c%13c%14c%11c(c%15%16)c%17c%12c%18c%13c%19c%14c%20c%15c%21c%16c%22c%23c%24c%17c%18c%25c%26c%19c%20c%27c%28c1c1c2c2c3c3c4c4c5c(c67)c5c8c9c6c7c%10c%11c%12c8c%13c9c%14c%10c%15c(c%16%17)c%11c%18c%19c%12c(c%201)c2c1c3c2c4c5c6c3c(c78)c9c4c%10c%11c%12c1c4c23");
        for (int i = 0; i < 576; i += 2)
            assertMatch(m, i, i + 1);
    }

    // tougher than C60 due to odd cycles
    @Test
    public void fullerene_C70() throws Exception {
        Matching m = matching("c12c3c4c5c1c1c6c7c2c2c8c3c3c9c4c4c%10c5c5c1c1c6c6c%11c%12c%13c%14c%15c%16c%17c%14c%14c%18c%13c%11c1c1c5c%10c5c(c%14c%10c%17c%11c%13c%16c%14c%16c%15c%12c%12c%16c(c2c7c6%12)c2c8c3c(c%13c%142)c2c9c4c5c%10c%112)c%181");
        assertMatch(m, 0, 1);
        assertMatch(m, 2, 3);
        assertMatch(m, 4, 5);
        assertMatch(m, 6, 7);
        assertMatch(m, 8, 9);
        assertMatch(m, 10, 11);
        assertMatch(m, 12, 13);
        assertMatch(m, 14, 15);
        assertMatch(m, 16, 17);
        assertMatch(m, 18, 19);
        assertMatch(m, 20, 21);
        assertMatch(m, 22, 56);
        assertMatch(m, 23, 24);
        assertMatch(m, 25, 33);
        assertMatch(m, 26, 27);
        assertMatch(m, 28, 29);
        assertMatch(m, 30, 31);
        assertMatch(m, 32, 69);
        assertMatch(m, 34, 35);
        assertMatch(m, 36, 37);
        assertMatch(m, 38, 39);
        assertMatch(m, 40, 41);
        assertMatch(m, 42, 43);
        assertMatch(m, 44, 45);
        assertMatch(m, 46, 47);
        assertMatch(m, 48, 49);
        assertMatch(m, 50, 51);
        assertMatch(m, 52, 53);
        assertMatch(m, 54, 55);
        assertMatch(m, 57, 58);
        assertMatch(m, 59, 60);
        assertMatch(m, 61, 62);
        assertMatch(m, 63, 64);
        assertMatch(m, 65, 66);
        assertMatch(m, 67, 68);
    }

    void assertMatch(Matching m, int u, int v) {
        assertTrue(m.matched(u));
        assertTrue(m.matched(v));
        assertThat(m.other(u), is(v));
    }

    private Matching matching(String smi, int... xs) throws Exception {
        return matching(smipar.parseSmiles(smi), xs);
    }

    private Matching matching(IAtomContainer container, int... xs) {
        BitSet subset = new BitSet();
        if (xs.length == 0) {
            subset.flip(0, container.getAtomCount());
        } else {
            for (int x : xs)
                subset.set(x);
        }
        Matching m = Matching.withCapacity(container.getAtomCount());
        return EdmondsMaximumMatching.maxamise(m, GraphUtil.toAdjList(container), subset);
    }

}
