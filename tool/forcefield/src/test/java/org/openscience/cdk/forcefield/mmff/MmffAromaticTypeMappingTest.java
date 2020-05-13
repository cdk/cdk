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

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;

public class MmffAromaticTypeMappingTest {

    @Test
    public void indexOfHetroAt0() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[0]] = 2;
        Assert.assertEquals(0, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    public void indexOfHetroAt1() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[1]] = 2;
        Assert.assertEquals(1, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    public void indexOfHetroAt2() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[2]] = 2;
        Assert.assertEquals(2, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    public void indexOfHetroAt3() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[3]] = 2;
        Assert.assertEquals(3, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    public void indexOfHetroAt4() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[4]] = 2;
        Assert.assertEquals(4, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    public void indexOfNoHetroAtom() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        Assert.assertEquals(-1, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    public void indexOfTwoHetroAtoms() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[0]] = 2;
        contr[cycle[4]] = 2;
        Assert.assertEquals(-2, MmffAromaticTypeMapping.indexOfHetro(cycle, contr));
    }

    @Test
    public void normaliseNoHetro() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        assertFalse(MmffAromaticTypeMapping.normaliseCycle(cycle, contr));
    }

    @Test
    public void normaliseHetroAt3() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[3]] = 2;
        Assert.assertTrue(MmffAromaticTypeMapping.normaliseCycle(cycle, contr));
        Assert.assertArrayEquals(new int[]{4, 5, 3, 2, 1, 4}, cycle);
    }

    @Test
    public void normaliseHetroAt2() {
        int[] cycle = new int[]{3, 2, 1, 4, 5, 3};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        contr[cycle[2]] = 2;
        Assert.assertTrue(MmffAromaticTypeMapping.normaliseCycle(cycle, contr));
        Assert.assertArrayEquals(new int[]{1, 4, 5, 3, 2, 1}, cycle);
    }

    @Test
    public void tetravalentCarbonContributesOneElectron() {
        assertThat(MmffAromaticTypeMapping.contribution(6, 3, 4), is(1));
    }

    @Test
    public void tetravalentTricoordinateNitrogenContributesOneElectron() {
        assertThat(MmffAromaticTypeMapping.contribution(7, 3, 4), is(1));
    }

    @Test
    public void trivalentBicoordinateNitrogenContributesOneElectron() {
        assertThat(MmffAromaticTypeMapping.contribution(7, 2, 3), is(1));
    }

    @Test
    public void trivalentTricoordinateNitrogenContributesTwoElectrons() {
        assertThat(MmffAromaticTypeMapping.contribution(7, 3, 3), is(2));
    }

    @Test
    public void bivalentBicoordinateNitrogenContributesTwoElectrons() {
        assertThat(MmffAromaticTypeMapping.contribution(7, 2, 2), is(2));
    }

    @Test
    public void divalentSulphurContributesTwoElectrons() {
        assertThat(MmffAromaticTypeMapping.contribution(16, 2, 2), is(2));
    }

    @Test
    public void divalentOxygenContributesTwoElectrons() {
        assertThat(MmffAromaticTypeMapping.contribution(8, 2, 2), is(2));
    }

    @Test
    public void benzeneIsAromatic() {
        int[] cycle = new int[]{0, 1, 2, 3, 4, 5, 0};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        int[] dbs = new int[]{1, 0, 3, 2, 5, 4};
        boolean[] arom = new boolean[contr.length];
        Assert.assertTrue(MmffAromaticTypeMapping.isAromaticRing(cycle, contr, dbs, arom));
    }

    @Test
    public void pyrroleIsAromatic() {
        int[] cycle = new int[]{0, 1, 2, 3, 4, 0};
        int[] contr = new int[]{2, 1, 1, 1, 1};
        int[] dbs = new int[]{-1, 2, 1, 4, 3};
        boolean[] arom = new boolean[contr.length];
        Assert.assertTrue(MmffAromaticTypeMapping.isAromaticRing(cycle, contr, dbs, arom));
    }

    @Test
    public void exocyclicDoubleBondsBreakAromaticity() {
        int[] cycle = new int[]{0, 1, 2, 3, 4, 5, 0};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
        int[] dbs = new int[]{1, 0, 6, 7, 5, 4};
        boolean[] arom = new boolean[contr.length];
        assertFalse(MmffAromaticTypeMapping.isAromaticRing(cycle, contr, dbs, arom));
    }

    @Test
    public void delocalisedExocyclicDoubleBondsMaintainAromaticity() {
        int[] cycle = new int[]{0, 1, 2, 3, 4, 5, 0};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
        int[] dbs = new int[]{1, 0, 6, 7, 5, 4};
        boolean[] arom = new boolean[contr.length];
        arom[2] = arom[3] = arom[6] = arom[7] = true; // adjacent ring is aromatic
        Assert.assertTrue(MmffAromaticTypeMapping.isAromaticRing(cycle, contr, dbs, arom));
    }

    @Test
    public void updateN2OXtoNPOX() {
        int[] cycle = new int[]{2, 4, 3, 1, 0, 5, 2};
        String[] symbs = new String[10];
        Arrays.fill(symbs, "");
        symbs[cycle[1]] = "N2OX";
        MmffAromaticTypeMapping.updateAromaticTypesInSixMemberRing(cycle, symbs);
        assertThat(symbs[cycle[1]], is("NPOX"));
    }

    // NCN+,N+=C,N=+C -> NPD+
    @Test
    public void updateToNPDPlus() {
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
    public void updateNStarToNPYD() {
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
    public void updateCStarToCB() {
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
    public void imidazoleCarbonTypesAreNeitherAlphaOrBeta() {
        Map<String, String> map = Collections.singletonMap("CB", "C5A");
        assertThat(MmffAromaticTypeMapping.getAromaticType(map, 'A', "CB", true, false), is("C5"));
    }

    @Test
    public void imidazoleNitrogenTypesAreNeitherAlphaOrBeta() {
        Map<String, String> map = Collections.singletonMap("N=C", "N5A");
        assertThat(MmffAromaticTypeMapping.getAromaticType(map, 'A', "N=C", true, false), is("N5"));
    }

    @Test
    public void anionCarbonTypesAreNeitherAlphaOrBeta() {
        Map<String, String> map = Collections.singletonMap("CB", "C5A");
        assertThat(MmffAromaticTypeMapping.getAromaticType(map, 'A', "CB", false, true), is("C5"));
    }

    @Test
    public void anionNitrogensAreAlwaysN5M() {
        Map<String, String> map = Collections.singletonMap("N=C", "N5A");
        assertThat(MmffAromaticTypeMapping.getAromaticType(map, 'A', "N=C", false, true), is("N5M"));
    }

    // IM = false + AN = false
    @Test
    public void useMappingWhenNeitherFlagIsRaised() {
        Map<String, String> map = Collections.singletonMap("N=C", "N5A");
        assertThat(MmffAromaticTypeMapping.getAromaticType(map, 'A', "N=C", false, false), is("N5A"));
    }

    @Test
    public void elementContributingOneElectronRejectWhenNoDoubleBond() throws Exception {
        int[] cycle = new int[]{0, 1, 2, 3, 4, 5, 0};
        int[] contr = new int[]{1, 1, 1, 1, 1, 1};
        int[] dbs = new int[]{1, 0, 3, -1, 5, 4};
        assertFalse(MmffAromaticTypeMapping.isAromaticRing(cycle, contr, dbs, new boolean[contr.length]));
    }

    @Test
    public void intractableNumberOfCycles() throws Exception {

        // to ensure intractable cycles are handled we create a complete graph
        // where every vertex is attached to every other vertex. K8 is sufficient
        // to trigger an abort when finding cycles
        IAtomContainer container = Mockito.mock(IAtomContainer.class);
        int[][] graphK8 = new int[8][7];

        for (int i = 0; i < graphK8.length; i++) {
            int n = 0;
            for (int j = 0; j < graphK8.length; j++) {
                if (i == j) continue;
                graphK8[i][n++] = j;
            }
        }

        assertThat(MmffAromaticTypeMapping.cyclesOfSizeFiveOrSix(container, graphK8).length, is(0));
    }

    @Test
    public void contributionOfThreeValentCarbon() {
        assertThat(MmffAromaticTypeMapping.contribution(6, 3, 3), is(-1));
    }

    @Test
    public void contributionOfFiveValentNitrogen() {
        assertThat(MmffAromaticTypeMapping.contribution(7, 3, 5), is(-1));
    }
}
