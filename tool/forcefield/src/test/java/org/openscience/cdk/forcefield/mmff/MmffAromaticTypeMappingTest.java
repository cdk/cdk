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

package org.openscience.cdk.forcefield.mmff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MmffAromaticTypeMappingTest {

    @Test
    void indexOfHetroAt0() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[0]] = 2;
        Assertions.assertEquals(0, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    void indexOfHetroAt1() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[1]] = 2;
        Assertions.assertEquals(1, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    void indexOfHetroAt2() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[2]] = 2;
        Assertions.assertEquals(2, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    void indexOfHetroAt3() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[3]] = 2;
        Assertions.assertEquals(3, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    void indexOfHetroAt4() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[4]] = 2;
        Assertions.assertEquals(4, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    void indexOfNoHetroAtom() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        Assertions.assertEquals(-1, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    void indexOfTwoHetroAtoms() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[0]] = 2;
        contr[cycle[4]] = 2;
        Assertions.assertEquals(-2, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    void normaliseNoHetro() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        Assertions.assertFalse(MmffAromaticTypeMapping.normaliseCycle(cycle, contr));
    }

    @Test
    void normaliseHetroAt3() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[3]] = 2;
        Assertions.assertTrue(MmffAromaticTypeMapping.normaliseCycle(cycle, contr));
        Assertions.assertArrayEquals(new int[]{4, 5, 3, 2, 1, 4}, cycle);
    }

    @Test
    void normaliseHetroAt2() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[2]] = 2;
        Assertions.assertTrue(MmffAromaticTypeMapping.normaliseCycle(cycle, contr));
        Assertions.assertArrayEquals(new int[]{1, 4, 5, 3, 2, 1}, cycle);
    }

    @Test
    void tetravalentCarbonContributesOneElectron() {
        assertThat(MmffAromaticTypeMapping.contribution(6, 3, 4), is(1));
    }

    @Test
    void tetravalentTricoordinateNitrogenContributesOneElectron() {
        assertThat(MmffAromaticTypeMapping.contribution(7, 3, 4), is(1));
    }

    @Test
    void trivalentBicoordinateNitrogenContributesOneElectron() {
        assertThat(MmffAromaticTypeMapping.contribution(7, 2, 3), is(1));
    }

    @Test
    void trivalentTricoordinateNitrogenContributesTwoElectrons() {
        assertThat(MmffAromaticTypeMapping.contribution(7, 3, 3), is(2));
    }

    @Test
    void bivalentBicoordinateNitrogenContributesTwoElectrons() {
        assertThat(MmffAromaticTypeMapping.contribution(7, 2, 2), is(2));
    }

    @Test
    void divalentSulphurContributesTwoElectrons() {
        assertThat(MmffAromaticTypeMapping.contribution(16, 2, 2), is(2));
    }

    @Test
    void divalentOxygenContributesTwoElectrons() {
        assertThat(MmffAromaticTypeMapping.contribution(8, 2, 2), is(2));
    }

    @Test
    void benzeneIsAromatic() {
        int[] cycle = new int[]{0, 1, 2, 3, 4, 5, 0};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        int[] dbs = new int[]{1, 0, 3, 2, 5, 4};
        boolean[] arom = new boolean[contr.length];
        Assertions.assertTrue(MmffAromaticTypeMapping.isAromaticRing(cycle, contr, dbs, arom));
    }

    @Test
    void pyrroleIsAromatic() {
        int[] cycle = new int[]{0, 1, 2, 3, 4, 0};
        int[] contr = new int[]{2, 1, 1, 1, 1};
        int[] dbs = new int[]{-1, 2, 1, 4, 3};
        boolean[] arom = new boolean[contr.length];
        Assertions.assertTrue(MmffAromaticTypeMapping.isAromaticRing(cycle, contr, dbs, arom));
    }

    @Test
    void exocyclicDoubleBondsBreakAromaticity() {
        int[] cycle = new int[]{0, 1, 2, 3, 4, 5, 0};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
        int[] dbs = new int[]{1, 0, 6, 7, 5, 4};
        boolean[] arom = new boolean[contr.length];
        Assertions.assertFalse(MmffAromaticTypeMapping.isAromaticRing(cycle, contr, dbs, arom));
    }

    @Test
    void delocalisedExocyclicDoubleBondsMaintainAromaticity() {
        int[] cycle = new int[]{0, 1, 2, 3, 4, 5, 0};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
        int[] dbs = new int[]{1, 0, 6, 7, 5, 4};
        boolean[] arom = new boolean[contr.length];
        arom[2] = arom[3] = arom[6] = arom[7] = true; // adjacent ring is aromatic
        Assertions.assertTrue(MmffAromaticTypeMapping.isAromaticRing(cycle, contr, dbs, arom));
    }

    @Test
    void updateN2OXtoNPOX() {
        int[] cycle = new int[]{2, 4, 3, 1, 0, 5, 2};
        String[] symbs = new String[10];
        Arrays.fill(symbs, "");
        symbs[cycle[1]] = "N2OX";
        MmffAromaticTypeMapping.updateAromaticTypesInSixMemberRing(cycle, symbs);
        assertThat(symbs[cycle[1]], is("NPOX"));
    }

    // NCN+,N+=C,N=+C -> NPD+
    @Test
    void updateToNPDPlus() {
        int[] cycle = new int[]{2, 4, 3, 1, 0, 5, 2};
        String[] symbs = new String[10];
        Arrays.fill(symbs, "");
        symbs[cycle[1]] = "NCN+";
        symbs[cycle[2]] = "N+=C";
        symbs[cycle[3]] = "N=+C";
        MmffAromaticTypeMapping.updateAromaticTypesInSixMemberRing(cycle, symbs);
        assertThat(symbs[cycle[1]], is("NPD+"));
        assertThat(symbs[cycle[2]], is("NPD+"));
        assertThat(symbs[cycle[3]], is("NPD+"));
    }

    // N* -> NPYD
    @Test
    void updateNStarToNPYD() {
        int[] cycle = new int[]{2, 4, 3, 1, 0, 5, 2};
        String[] symbs = new String[10];
        Arrays.fill(symbs, "");
        symbs[cycle[1]] = "N=C";
        symbs[cycle[2]] = "N=N";
        MmffAromaticTypeMapping.updateAromaticTypesInSixMemberRing(cycle, symbs);
        assertThat(symbs[cycle[1]], is("NPYD"));
        assertThat(symbs[cycle[2]], is("NPYD"));
    }

    // C* -> CB
    @Test
    void updateCStarToCB() {
        int[] cycle = new int[]{2, 4, 3, 1, 0, 5, 2};
        String[] symbs = new String[10];
        Arrays.fill(symbs, "");
        symbs[cycle[1]] = "C=C";
        symbs[cycle[2]] = "C=N";
        MmffAromaticTypeMapping.updateAromaticTypesInSixMemberRing(cycle, symbs);
        assertThat(symbs[cycle[1]], is("CB"));
        assertThat(symbs[cycle[2]], is("CB"));
    }

    @Test
    void imidazoleCarbonTypesAreNeitherAlphaOrBeta() {
        Map<String, String> map = Collections.singletonMap("CB", "C5A");
        assertThat(MmffAromaticTypeMapping.getAromaticType(map, 'A', "CB", true, false), is("C5"));
    }

    @Test
    void imidazoleNitrogenTypesAreNeitherAlphaOrBeta() {
        Map<String, String> map = Collections.singletonMap("N=C", "N5A");
        assertThat(MmffAromaticTypeMapping.getAromaticType(map, 'A', "N=C", true, false), is("N5"));
    }

    @Test
    void anionCarbonTypesAreNeitherAlphaOrBeta() {
        Map<String, String> map = Collections.singletonMap("CB", "C5A");
        assertThat(MmffAromaticTypeMapping.getAromaticType(map, 'A', "CB", false, true), is("C5"));
    }

    @Test
    void anionNitrogensAreAlwaysN5M() {
        Map<String, String> map = Collections.singletonMap("N=C", "N5A");
        assertThat(MmffAromaticTypeMapping.getAromaticType(map, 'A', "N=C", false, true), is("N5M"));
    }

    // IM = false + AN = false
    @Test
    void useMappingWhenNeitherFlagIsRaised() {
        Map<String, String> map = Collections.singletonMap("N=C", "N5A");
        assertThat(MmffAromaticTypeMapping.getAromaticType(map, 'A', "N=C", false, false), is("N5A"));
    }

    @Test
    void elementContributingOneElectronRejectWhenNoDoubleBond() throws Exception {
        int[] cycle = new int[]{0, 1, 2, 3, 4, 5, 0};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        int[] dbs = new int[]{1, 0, 3, -1, 5, 4};
        Assertions.assertFalse(MmffAromaticTypeMapping.isAromaticRing(cycle, contr, dbs, new boolean[contr.length]));
    }

    @Test
    void intractableNumberOfCycles() throws Exception {

        // to ensure intractable cycles are handled we create a complete graph
        // where every vertex is attached to every other vertex. K9 is sufficient
        // to trigger an abort when finding cycles for setting PubChem_994
        IAtomContainer container = Mockito.mock(IAtomContainer.class);
        int[][] graphK9 = new int[9][8];

        for (int i = 0; i < graphK9.length; i++) {
            int n = 0;
            for (int j = 0; j < graphK9.length; j++) {
                if (i == j) continue;
                graphK9[i][n++] = j;
            }
        }

        assertThat(MmffAromaticTypeMapping.cyclesOfSizeFiveOrSix(container, graphK9).length, is(0));
    }

    @Test
    void contributionOfThreeValentCarbon() {
        assertThat(MmffAromaticTypeMapping.contribution(6, 3, 3), is(-1));
    }

    @Test
    void contributionOfFiveValentNitrogen() {
        assertThat(MmffAromaticTypeMapping.contribution(7, 3, 5), is(-1));
    }
}
