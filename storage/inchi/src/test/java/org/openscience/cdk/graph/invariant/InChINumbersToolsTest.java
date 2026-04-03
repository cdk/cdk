/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.graph.invariant;

import io.github.dan2097.jnainchi.InchiFlag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openscience.cdk.Atom;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.Arrays;
import java.util.stream.LongStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

class InChINumbersToolsTest extends CDKTestCase {

    @Test
    void testSimpleNumbering() throws CDKException {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(new Atom("O"));
        container.addAtom(new Atom("C"));
        container.addBond(0, 1, IBond.Order.SINGLE);
        long[] numbers = InChINumbersTools.getNumbers(container);
        Assertions.assertEquals(2, numbers.length);
        Assertions.assertEquals(2, numbers[0]);
        Assertions.assertEquals(1, numbers[1]);
    }

    @Test
    void testHydrogens() throws CDKException {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(new Atom("H"));
        container.addAtom(new Atom("C"));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addAtom(new Atom("H"));
        container.addBond(1, 2, IBond.Order.SINGLE);
        container.addAtom(new Atom("H"));
        container.addBond(1, 3, IBond.Order.SINGLE);
        container.addAtom(new Atom("H"));
        container.addBond(1, 4, IBond.Order.SINGLE);
        long[] numbers = InChINumbersTools.getNumbers(container);
        Assertions.assertEquals(5, numbers.length);
        Assertions.assertEquals(0, numbers[0]);
        Assertions.assertEquals(1, numbers[1]);
        Assertions.assertEquals(0, numbers[2]);
        Assertions.assertEquals(0, numbers[3]);
        Assertions.assertEquals(0, numbers[4]);
    }

    @Test
    void testGlycine() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = parser.parseSmiles("C(C(=O)O)N");
        long[] numbers = InChINumbersTools.getNumbers(atomContainer);
        Assertions.assertEquals(5, numbers.length);
        Assertions.assertEquals(1, numbers[0]);
        Assertions.assertEquals(2, numbers[1]);
        Assertions.assertEquals(4, numbers[2]);
        Assertions.assertEquals(5, numbers[3]);
        Assertions.assertEquals(3, numbers[4]);
    }

    @Test
    void testGlycine_uSmiles() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = parser.parseSmiles("C(C(=O)O)N");
        long[] numbers = InChINumbersTools.getNumbers(atomContainer);
        Assertions.assertEquals(5, numbers.length);
        Assertions.assertEquals(1, numbers[0]);
        Assertions.assertEquals(2, numbers[1]);
        Assertions.assertEquals(4, numbers[2]);
        Assertions.assertEquals(5, numbers[3]);
        Assertions.assertEquals(3, numbers[4]);
    }

    @Test
    void fixedH() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = parser.parseSmiles("N1C=NC2=CC=CC=C12");
        String auxInfo = InChINumbersTools.auxInfo(atomContainer, InchiFlag.FixedH);
        String expected = "AuxInfo=1/1/" + "N:6,7,5,8,2,4,9,3,1/" + "E:(1,2)(3,4)(6,7)(8,9)/" + "F:7,6,8,5,2,9,4,1,3/"
                + "rA:9nNCNCCCCCC/" + "rB:s1;d2;s3;d4;s5;d6;s7;s1s4d8;/" + "rC:;;;;;;;;;";
        assertThat(auxInfo, is(expected));
    }

    @Test
    void parseStandard() {
        assertThat(InChINumbersTools.parseUSmilesNumbers("AuxInfo=1/0/N:3,2,1/rA:3OCC/rB:s1;s2;/rC:;;;", mock(3)),
                is(new long[]{3, 2, 1}));
    }

    @Test
    void parseRecMet() {

        // C(=O)O[Pt](N)(N)Cl
        assertThat(
                InChINumbersTools.parseUSmilesNumbers(
                        "AuxInfo=1/1/N:3,2,4;7;5;6;1/E:(2,3);;;;/F:5m/E:m;;;;/CRV:;;2*1-1;/rA:7PtOCONNCl/rB:s1;s2;d3;s1;s1;s1;/rC:;;;;;;;/R:/0/N:3,7,5,6,4,2,1/E:(3,4)",
                        mock(7)), is(new long[]{7, 6, 1, 5, 3, 4, 2}));
    }

    @Test
    void parseFixedH() {
        // N1C=NC=C1
        assertThat(InChINumbersTools.parseUSmilesNumbers(
                "AuxInfo=1/1/N:4,5,2,3,1/E:(1,2)(4,5)/F:5,4,2,1,3/rA:5NCNCC/rB:s1;d2;s3;s1d4;/rC:;;;;;", mock(5)),
                is(new long[]{4, 3, 5, 2, 1}));
    }

    @Test
    void parseDisconnected() {
        // O.N1C=NC=C1
        assertThat(InChINumbersTools.parseUSmilesNumbers(
                "AuxInfo=1/1/N:5,6,3,4,2;1/E:(1,2)(4,5);/F:6,5,3,2,4;m/rA:6ONCNCC/rB:;s2;d3;s4;s2d5;/rC:;;;;;;",
                mock(6)), is(new long[]{6, 4, 3, 5, 2, 1}));
    }

    @Test
    void parseMultipleDisconnected() {
        // O.N1C=NC=C1.O.O=O
        assertThat(
                InChINumbersTools.parseUSmilesNumbers(
                        "AuxInfo=1/1/N:5,6,3,4,2;8,9;1;7/E:(1,2)(4,5);(1,2);;/F:6,5,3,2,4;3m/E:;m;;/rA:9ONCNCCOOO/rB:;s2;d3;s4;s2d5;;;d8;/rC:;;;;;;;;;",
                        mock(9)), is(new long[]{8, 4, 3, 5, 2, 1, 9, 6, 7}));
    }

    // if '[O-]' is first start at '=O' instead
    @Test
    void favorCarbonyl() throws Exception {
        IAtomContainer container = new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles("P([O-])=O");
        assertThat(InChINumbersTools.getUSmilesNumbers(container), is(new long[]{3, 2, 1}));
    }

    @Test
    void unlabelledHydrogens() throws Exception {
        IAtomContainer container = new SmilesParser(SilentChemObjectBuilder.getInstance())
                .parseSmiles("[H]C([H])([H])[H]");
        assertThat(InChINumbersTools.getUSmilesNumbers(container), is(new long[]{2, 1, 3, 4, 5}));
    }

    @Test
    void bug1370() throws Exception {
        IAtomContainer container = new SmilesParser(SilentChemObjectBuilder.getInstance())
                .parseSmiles("O=[Bi]Cl");
        assertThat(InChINumbersTools.getUSmilesNumbers(container), is(new long[]{3, 1, 2}));
    }

    @Test
    void protons() throws Exception {
        IAtomContainer container = new SmilesParser(SilentChemObjectBuilder.getInstance())
            .parseSmiles("[H+].[H+].F[Si-2](F)(F)(F)(F)F");
        assertThat(InChINumbersTools.getUSmilesNumbers(container), is(new long[]{8, 9, 1, 7, 2, 3, 4, 5, 6}));
    }

    @Test
    void emptyAtomContainer_test() throws Exception {
        IAtomContainer container = SilentChemObjectBuilder.getInstance().newAtomContainer();
        assertThat(InChINumbersTools.getUSmilesNumbers(container), is(new long[0]));
    }

    @Test
    void fixedHydrogenLayerWithMixedMEntries() throws Exception {
        // This aux string provides an /F: layer with mixed m-entries and explicit
        // orderings: "4m;18,19,13,15,14,20,17,16;2m"
        // The middle component overrides order in /N: layer  [...18,19,13,15,14,20,16,17...],
        // swapping atoms 16 and 17. The resulting array must:
        //   (a) label all 74 atoms with unique values 1..74
        //   (b) respect the /F: override: atom 17 must receive an earlier label than atom 16
        long[] numbers = InChINumbersTools.parseUSmilesNumbers(
                "AuxInfo=1/1/N:62,68,67,69,64,63,65,66,71,72,70,73,74;1,3,7,6,4,8,2,5,10,9,11,12;" +
                        "42,51,50,47,45,48,49,44,53,52,46,43;31,36,33,32,34,35,37,41,40,38,39;" +
                        "18,19,13,15,14,20,16,17;54,57,59,56,60,61,58,55;24,23,21,30,22,27,28,29,25,26" +
                        "/E:(12,13);(1,2)(4,5)(11,12);;(10,11);(7,8);(5,6);(6,7,8)" +
                        "/F:4m;18,19,13,15,14,20,17,16;2m" +
                        "/E:;(1,2)(4,5);;;;;m" +
                        "/rA:74nCCCCCCCCOCOOCNCOOCCOCOCCOPOO-O-OCCCCCCCOOONCSCCOCCCCCONCSCCOCOOCCCCCCCCOCNOO" +
                        "/rB:d1;s2;d3;s4;s1d5;s2;d+7;s5;s8;s10;d10;;s13;s13;w15;s15;s13;s18;d19;;d21;s21;" +
                        "s21;s23;s25;w26;s26;s26;s24;;d31;s32;d33;s34;s31d35;s32;s37;d37;s35;s34;;V42;s43;" +
                        "s44;d44;d45;s47;d48;s49;s45d50;s49;s48;;V54;s55;s56;d56;s57;s59;d59;;d62;s63;d64;" +
                        "s65;s62d66;s63;d+68;s66;s69;s65;s71;d71;" +
                        "/rC:482.8625,-229.1854,0;482.8625,-240.6946,0;492.8291,-246.4492,0;502.7958,-240.6946,0;" +
                        "502.7958,-229.1854,0;492.8291,-223.4308,0;472.8958,-246.4492,0;462.9372,-240.6946,0;" +
                        "512.7624,-223.4308,0;452.9706,-246.4492,0;443.0039,-240.6946,0;452.9706,-257.9584,0;" +
                        "31.078,-340.7873,0;41.0446,-335.0327,0;21.1113,-335.0327,0;11.1846,-340.7713,0;" +
                        "21.1113,-323.5635,0;31.078,-352.2965,0;41.0446,-358.0511,0;41.0446,-369.5603,0;" +
                        "24.2524,-404.5035,0;34.227,-398.7489,0;14.2857,-398.7489,0;24.2524,-416.0126,0;" +
                        "14.2857,-387.2397,0;4.3185,-381.4851,0;-5.6487,-387.2397,0;14.2857,-375.7305,0;" +
                        "4.3185,-369.9759,0;34.227,-421.7672,0;117.5567,-348.1564,0;117.5567,-359.6656,0;" +
                        "127.5234,-365.4202,0;137.49,-359.6656,0;137.49,-348.1564,0;127.5234,-342.4018,0;" +
                        "107.5901,-365.4202,0;97.6234,-359.6656,0;107.5901,-376.9293,0;147.4566,-342.4018,0;" +
                        "147.4566,-365.4202,0;211.0037,-365.3318,0;211.0037,-376.841,0;205.2491,-386.8077,0;" +
                        "211.0037,-396.7743,0;193.7399,-386.8077,0;205.2491,-406.7409,0;211.0037,-416.7076,0;" +
                        "222.5048,-416.7076,0;228.2594,-406.7409,0;222.5048,-396.7743,0;228.2594,-426.6742,0;" +
                        "205.2491,-426.6742,0;276.8385,-412.3802,0;276.8385,-423.8893,0;271.0839,-433.856,0;" +
                        "276.8385,-443.8226,0;259.5747,-433.856,0;271.0839,-453.7893,0;276.8385,-463.7559,0;" +
                        "259.5747,-453.7893,0;335.3282,-348.3642,0;335.3282,-359.8734,0;345.2948,-365.628,0;" +
                        "355.2615,-359.8734,0;355.2615,-348.3642,0;345.2948,-342.6096,0;325.3615,-365.628,0;" +
                        "315.4029,-359.8734,0;365.2281,-342.6096,0;305.4363,-365.628,0;365.2281,-365.628,0;" +
                        "295.4696,-359.8734,0;305.4363,-377.1371,0;",
                mock(74));
        long[] sorted = Arrays.stream(numbers).sorted().toArray();
        long[] expected = LongStream.rangeClosed(1, 74).toArray();
        Assertions.assertArrayEquals(expected, sorted);
        Assertions.assertTrue(numbers[16]<numbers[15],
                "atom 17 should be labelled before atom 16 due to /F: override");
    }

    static IAtomContainer mock(int nAtoms) {
        IAtomContainer container = Mockito.mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(nAtoms);
        for (int i = 0; i < nAtoms; i++) {
            IAtom atom = Mockito.mock(IAtom.class);
            when(atom.getSymbol()).thenReturn("C");
            when(atom.getAtomicNumber()).thenReturn(6);
            when(container.getAtom(i)).thenReturn(atom);
        }
        return container;
    }
}
