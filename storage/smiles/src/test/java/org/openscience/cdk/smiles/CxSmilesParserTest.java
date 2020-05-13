/*
 * Copyright (c) 2016 John May <jwmay@users.sf.net>
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

package org.openscience.cdk.smiles;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.MatcherAssert.assertThat;

public class CxSmilesParserTest {

    @Test
    public void atomLabels() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|$;;;Het;;;;;A$|", state), is(not(-1)));
        assertThat(state.atomLabels, hasEntry(3, "Het"));
        assertThat(state.atomLabels, hasEntry(8, "A"));
    }

    @Test
    public void escapedAtomLabels() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|$R&#39;;;;;;;$|", state), is(not(-1)));
        assertThat(state.atomLabels, hasEntry(0, "R'"));
    }

    @Test
    public void escapedAtomLabels2() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|$;;;&#40;C&#40;R41&#41;&#40;R41&#41;&#41;n;;R41;R41;R41;;_AP1;R41;R41;;_AP1$|", state), is(not(-1)));
        assertThat(state.atomLabels, hasEntry(3, "(C(R41)(R41))n"));
    }

    @Test
    public void atomValues() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|$_AV:;;;5;;;;;8$|", state), is(not(-1)));
        assertThat(state.atomValues, hasEntry(3, "5"));
        assertThat(state.atomValues, hasEntry(8, "8"));
    }

    @Test
    public void atomLabelsEmpty() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|$$|", state), is(not(-1)));
        assertThat(state.atomLabels.size(), is(0));
    }

    @Test
    public void atomLabelsTruncated1() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|$;;;Het", state), is(-1));
        assertThat(CxSmilesParser.processCx("|$;;;Het;", state), is(-1));
    }

    @Test
    public void atomLabelsTruncated3() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|$;;;Het;$", state), is(-1));
    }

    @Test
    public void removeUnderscore() {
        CxSmilesState state = new CxSmilesState();
        CxSmilesParser.processCx("|$;;;_R1;$|", state);
        assertThat(state.atomLabels.get(3), is("R1"));
    }

    @Test
    public void skipCis() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|c:1,4,5|", state), is(not(-1)));
    }

    @Test
    public void skipTrans() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|t:1,4,5|", state), is(not(-1)));
    }

    @Test
    public void skipCisTrans() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|c:2,6,8,t:1,4,5|", state), is(not(-1)));
    }

    @Test
    public void skipCisTransUnspec() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|c:2,6,8,ctu:10,t:1,4,5|", state), is(not(-1)));
    }

    @Test
    public void skipLonePairDefinitions() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|c:6,8,t:4,lp:2:2,4:1,11:1,m:1:8.9|", state), is(not(-1)));
    }

    @Test
    public void fragmentGrouping() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|f:0.1.2.3,4.5.6|", state), is(not(-1)));
        assertThat(state.fragGroups, is(Arrays.asList(Arrays.asList(0, 1, 2, 3),
                                                      Arrays.asList(4, 5, 6))));
    }

    @Test
    public void fragmentGroupingFollowedByAtomLabels() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|f:0.1.2.3,4.5.6,$;;;R$|", state), is(not(-1)));
        assertThat(state.fragGroups, is(Arrays.asList(Arrays.asList(0, 1, 2, 3),
                                                      Arrays.asList(4, 5, 6))));
        assertThat(state.atomLabels, hasEntry(3, "R"));
    }

    @Test
    public void coords() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|(.0,-1.5,;-1.3,-.75,;-2.6,-1.5,;-3.9,-.75,;-3.9,.75,)|", state), is(not(-1)));
        assertThat(state.atomCoords.get(0), new AprxDoubleArray(0, -1.5, 0));
        assertThat(state.atomCoords.get(1), new AprxDoubleArray(-1.3, -.75, 0));
        assertThat(state.atomCoords.get(2), new AprxDoubleArray(-2.6, -1.5, 0));
        assertThat(state.atomCoords.get(3), new AprxDoubleArray(-3.9, -.75, 0));
        assertThat(state.atomCoords.get(4), new AprxDoubleArray(-3.9, .75, 0));
    }

    @Test
    public void hydrogenBondingSkipped() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|H:0.1,2.3|", state), is(not(-1)));
    }

    @Test
    public void hydrogenBondingTruncated() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|H:0.1,2.3", state), is(-1));
        assertThat(CxSmilesParser.processCx("|H:0.1,2.", state), is(-1));
    }

    @Test
    public void hydrogenAndCoordinationBondingSkipped() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|H:0.1,2.3,C:6.7,3.4|", state), is(not(-1)));
    }

    @Test public void positionalVariation() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|m:2:5.6.7.8.9.10,m:4:5.6.7.8.9|", state), is(not(-1)));
        assertThat(state.positionVar, hasEntry(2, Arrays.asList(5,6,7,8,9,10)));
        assertThat(state.positionVar, hasEntry(4, Arrays.asList(5,6,7,8,9)));
    }

    @Test public void positionalVariationImpliedLayer() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|m:2:5.6.7.8.9.10,4:5.6.7.8.9|", state), is(not(-1)));
        assertThat(state.positionVar, hasEntry(2, Arrays.asList(5,6,7,8,9,10)));
        assertThat(state.positionVar, hasEntry(4, Arrays.asList(5,6,7,8,9)));
    }

    @Test public void multiAtomSRU() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|Sg:n:1,2,3:m:ht|", state), is(not(-1)));
        assertThat(state.sgroups,
                   hasItem(new CxSmilesState.PolymerSgroup("n", Arrays.asList(1, 2, 3), "m", "ht")));
    }

    @Test public void dataSgroups() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|SgD::cdk&#58;ReactionConditions:Heat&#10;Hv|", state), is(not(-1)));
        assertThat(state.dataSgroups,
                   hasItem(new CxSmilesState.DataSgroup(new ArrayList<Integer>(), "cdk:ReactionConditions", "Heat\nHv", "", "", "")));
    }

    @Test public void unescape() {
        assertThat(CxSmilesParser.unescape("&#36;"), is("$"));
        assertThat(CxSmilesParser.unescape("&#127;"), is("\u007F")); // DEL
        assertThat(CxSmilesParser.unescape("&#9;"), is("\t")); // TAB
    }

    @Test public void relativeStereoMolecule() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|r|", state), is(not(-1)));
        assertThat(CxSmilesParser.processCx("|r,$_R1$|", state), is(not(-1)));
        assertThat(CxSmilesParser.processCx("|$_R1$,r|", state), is(not(-1)));
    }


    @Test public void relativeStereoReaction() {
        CxSmilesState state = new CxSmilesState();
        assertThat(CxSmilesParser.processCx("|r:2,4,5|", state), is(not(-1)));
    }

    /**
     * Custom matcher for checking an array of doubles closely matches (epsilon=0.01)
     * an expected value.
     */
    private static class AprxDoubleArray extends BaseMatcher<double[]> {

        double[] expected;
        double epsilon = 0.01;

        public AprxDoubleArray(double ... expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object o) {
            assert o instanceof double[];
            double[] actual = (double[]) o;
            if (expected.length != actual.length)
                return false;
            for (int i = 0; i < expected.length; i++) {
                if (Math.abs(expected[i]-actual[i]) > epsilon)
                    return false;
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(Arrays.toString(expected));
        }
    }

}
