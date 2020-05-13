/* Copyright (C) 2009 Rajarshi Guha
 *               2009,2011 Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 * Contact: Rajarshi Guha <rajarshi@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.fingerprint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.CoreMatchers.is;

/**
 * @cdk.module test-fingerprint
 */
public class PubchemFingerprinterTest extends AbstractFixedLengthFingerprinterTest {

    SmilesParser parser;

    @Override
    public IFingerprinter getBitFingerprinter() {
        return new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
    }

    @Before
    public void setup() {
        parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
    }

    @Test
    public void testGetSize() throws Exception {
        IFingerprinter printer = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
        Assert.assertEquals(881, printer.getSize());
    }

    @Test
    public void testFingerprint() throws Exception {
        IFingerprinter printer = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());

        IAtomContainer mol1 = parser.parseSmiles("c1ccccc1CCc1ccccc1");
        IAtomContainer mol2 = parser.parseSmiles("c1ccccc1CC");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);

        adder.addImplicitHydrogens(mol1);
        adder.addImplicitHydrogens(mol2);

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol1);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol2);

        Aromaticity.cdkLegacy().apply(mol1);
        Aromaticity.cdkLegacy().apply(mol2);

        BitSet bs1 = printer.getBitFingerprint(mol1).asBitSet();
        BitSet bs2 = printer.getBitFingerprint(mol2).asBitSet();

        Assert.assertEquals(881, printer.getSize());

        Assert.assertFalse("c1ccccc1CC was detected as a subset of c1ccccc1CCc1ccccc1",
                FingerprinterTool.isSubset(bs1, bs2));
    }

    @Test
    public void testfp2() throws Exception {
        IFingerprinter printer = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());

        IAtomContainer mol1 = parser.parseSmiles("CC(N)CCCN");
        IAtomContainer mol2 = parser.parseSmiles("CC(N)CCC");
        IAtomContainer mol3 = parser.parseSmiles("CCCC");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol3);

        Aromaticity.cdkLegacy().apply(mol1);
        Aromaticity.cdkLegacy().apply(mol2);
        Aromaticity.cdkLegacy().apply(mol3);

        BitSet bs1 = printer.getBitFingerprint(mol1).asBitSet();
        BitSet bs2 = printer.getBitFingerprint(mol2).asBitSet();
        BitSet bs3 = printer.getBitFingerprint(mol3).asBitSet();

        Assert.assertTrue(FingerprinterTool.isSubset(bs1, bs2));
        Assert.assertTrue(FingerprinterTool.isSubset(bs2, bs3));
    }

    /**
     * Test case for Pubchem CID 25181308.
     *
     * @throws InvalidSmilesException
     * @cdk.inchi InChI=1S/C13H24O10S/c1-20-12-8(18)6(16)10(4(2-14)21-12)23-13-9(19)7(17)11(24)5(3-15)22-13/h4-19,24H,2-3H2,1H3/t4-,5-,6-,7-,8-,9-,10-,11-,12-,13+/m1/s1
     */
    @Test
    public void testCID2518130() throws CDKException {
        IAtomContainer mol = parser.parseSmiles("COC1C(C(C(C(O1)CO)OC2C(C(C(C(O2)CO)S)O)O)O)O");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(mol.getBuilder());
        adder.addImplicitHydrogens(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IFingerprinter printer = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
        BitSet fp = printer.getBitFingerprint(mol).asBitSet();
        BitSet ref = PubchemFingerprinter
                .decode("AAADceBwPABAAAAAAAAAAAAAAAAAAAAAAAAkSAAAAAAAAAAAAAAAGgQACAAACBS0wAOCCAAABgQAAAAAAAAAAAAAAAAAAAAAAAAREAIAAAAiQAAFAAAHAAHAYAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==");

        Assert.assertEquals(ref, fp);
    }

    /**
     * Test case for Pubchem CID 5934166.
     *
     * @throws InvalidSmilesException
     * @cdk.inchi InChI=1S/C32H26N/c1-5-13-26(14-6-1)21-22-31-23-30(28-17-9-3-10-18-28)24-32(29-19-11-4-12-20-29)33(31)25-27-15-7-2-8-16-27/h1-24H,25H2/q+1/b22-21+
     */
    @Test
    public void testCID5934166() throws CDKException {
        IAtomContainer mol = parser.parseSmiles("C1=CC=C(C=C1)C[N+]2=C(C=C(C=C2C=CC3=CC=CC=C3)C4=CC=CC=C4)C5=CC=CC=C5");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(mol.getBuilder());
        adder.addImplicitHydrogens(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IFingerprinter printer = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
        BitSet fp = printer.getBitFingerprint(mol).asBitSet();
        BitSet ref = PubchemFingerprinter
                .decode("AAADceB+AAAAAAAAAAAAAAAAAAAAAAAAAAA8YMGCAAAAAAAB1AAAHAAAAAAADAjBHgQwgJMMEACgAyRiRACCgCAhAiAI2CA4ZJgIIOLAkZGEIAhggADIyAcQgMAOgAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==");

        Assert.assertEquals(ref, fp);
    }

    /**
       * Test case for Pubchem CID 25181289.
       *
       * @throws InvalidSmilesException
       * @cdk.inchi  InChI=1S/C14H10Cl3N3O3/c1-6(7-2-4-8(21)5-3-7)19-20-11-9(15)12(14(22)23)18-13(17)10(11)16/h2-5,19,21H,1H2,(H,18,20)(H,22,23)
       */
    @Test
    public void testCID25181289() throws CDKException {
        IAtomContainer mol = parser.parseSmiles("C=C(C1=CC=C(C=C1)O)NNC2=C(C(=NC(=C2Cl)Cl)C(=O)O)Cl");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(mol.getBuilder());
        adder.addImplicitHydrogens(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IFingerprinter printer = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
        BitSet fp = printer.getBitFingerprint(mol).asBitSet();
        BitSet ref = PubchemFingerprinter
                .decode("AAADccBzMAAGAAAAAAAAAAAAAAAAAAAAAAA8QAAAAAAAAAABwAAAHgIYCAAADA6BniAwzpJqEgCoAyTyTASChCAnJiIYumGmTtgKJnLD1/PEdQhkwBHY3Qe82AAOIAAAAAAAAABAAAAAAAAAAAAAAAAAAA==");

        Assert.assertEquals(ref, fp);
    }

    @Test
    public void testGetFingerprintAsBytes() throws CDKException {

        IAtomContainer mol = parser.parseSmiles("C=C(C1=CC=C(C=C1)O)NNC2=C(C(=NC(=C2Cl)Cl)C(=O)O)Cl");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(mol.getBuilder());
        adder.addImplicitHydrogens(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Aromaticity.cdkLegacy().apply(mol);

        PubchemFingerprinter printer = new PubchemFingerprinter(mol.getBuilder());
        BitSet fp = printer.getBitFingerprint(mol).asBitSet();

        byte[] actual = printer.getFingerprintAsBytes();
        byte[] expected = Arrays.copyOf(toByteArray(fp), actual.length);

        Assert.assertArrayEquals(expected, actual);

    }

    // adapted from: http://stackoverflow.com/questions/6197411/converting-from-bitset-to-byte-array
    public static byte[] toByteArray(BitSet bits) {
        byte[] bytes = new byte[bits.length() / 8 + 1];
        for (int i = 0; i < bits.length(); i++) {
            if (bits.get(i)) {
                bytes[i / 8] |= 1 << (7 - i % 8);
            }
        }
        return bytes;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecode_invalid() {
        PubchemFingerprinter.decode("a");
    }

    @Test
    public void testDecode() {
        BitSet bitSet = PubchemFingerprinter
                .decode("AAADcYBgAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAABAAAAGAAAAAAACACAEAAwAIAAAACAACBCAAACAAAgAAAIiAAAAIgIICKAERCAIAAggAAIiAcAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==");
        int[] setBits = new int[]{0, 9, 10, 178, 179, 255, 283, 284, 332, 344, 355, 370, 371, 384, 416, 434, 441, 446,
                470, 490, 516, 520, 524, 552, 556, 564, 570, 578, 582, 584, 595, 599, 603, 608, 618, 634, 640, 660,
                664, 668, 677, 678, 679};
        for (int set : setBits) {
            Assert.assertTrue("bit " + set + " was not set", bitSet.get(set));
        }
    }

    @Test
    public void testBenzene() throws CDKException {
        IAtomContainer mol = parser.parseSmiles("c1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(mol.getBuilder());
        adder.addImplicitHydrogens(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);

        Aromaticity.cdkLegacy().apply(mol);
        IFingerprinter printer = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
        BitSet fp = printer.getBitFingerprint(mol).asBitSet();
        BitSet ref = PubchemFingerprinter
                .decode("AAADcYBgAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAABAAAAGAAAAAAACACAEAAwAIAAAACAACBCAAACAAAgAAAIiAAAAIgIICKAERCAIAAggAAIiAcAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==");

        Assert.assertEquals(ref, fp);

    }

    /**
     * @throws Exception
     * @cdk.bug 3510588
     */
    @Test
    public void testMultithreadedUsage() throws Exception {
        IAtomContainer mol1 = parser.parseSmiles("C=C(C1=CC=C(C=C1)O)NNC2=C(C(=NC(=C2Cl)Cl)C(=O)O)Cl");
        IAtomContainer mol2 = parser
                .parseSmiles("C1=CC=C(C=C1)C[N+]2=C(C=C(C=C2C=CC3=CC=CC=C3)C4=CC=CC=C4)C5=CC=CC=C5");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);

        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(mol1.getBuilder());
        adder.addImplicitHydrogens(mol1);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol1);
        Aromaticity.cdkLegacy().apply(mol1);

        adder.addImplicitHydrogens(mol2);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol2);
        Aromaticity.cdkLegacy().apply(mol2);

        IFingerprinter fp = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
        BitSet bs1 = fp.getBitFingerprint(mol1).asBitSet();
        BitSet bs2 = fp.getBitFingerprint(mol2).asBitSet();

        class FpRunner implements Callable<BitSet> {

            IAtomContainer mol;

            FpRunner(IAtomContainer mol) {
                this.mol = mol;
            }

            @Override
            public BitSet call() throws Exception {
                BitSet fp = null;
                IFingerprinter fpr = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
                try {
                    fp = fpr.getBitFingerprint(mol).asBitSet();
                } catch (CDKException e) {
                    e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
                }
                return fp;
            }
        }

        // now lets run some threads
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<FpRunner> tasks = new ArrayList<FpRunner>();
        tasks.add(new FpRunner(mol1));
        tasks.add(new FpRunner(mol2));
        List<Future<BitSet>> ret = executor.invokeAll(tasks);

        BitSet fb1 = ret.get(0).get();
        Assert.assertNotNull(fb1);

        BitSet fb2 = ret.get(1).get();
        Assert.assertNotNull(fb2);

        Assert.assertEquals(bs1, fb1);
        Assert.assertEquals(bs2, fb2);
    }

    /**
     * Using PubChem/CACTVS Substr keys, these molecules are not considered
     * substructures and should only be used for similarity. This is because the
     * PubChem fragments match hydrogen counts. In this case the {@code 599}
     * bit ({@code [#1]-C-C=C-[#1]}) is found in the substructure but not the
     * superstructure.
     */
    @Test
    @Override
    public void testBug934819() throws Exception {

        IAtomContainer subStructure = bug934819_1();
        IAtomContainer superStructure = bug934819_2();

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(superStructure);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(subStructure);
        addImplicitHydrogens(superStructure);
        addImplicitHydrogens(subStructure);

        IFingerprinter fpr = new PubchemFingerprinter(SilentChemObjectBuilder.getInstance());
        IBitFingerprint superBits = fpr.getBitFingerprint(superStructure);
        IBitFingerprint subBits = fpr.getBitFingerprint(subStructure);

        org.hamcrest.MatcherAssert.assertThat(
                subBits.asBitSet(),
                is(asBitSet(9, 10, 14, 18, 19, 33, 143, 146, 255, 256, 283, 284, 285, 293, 301, 332, 344, 349, 351,
                        353, 355, 368, 370, 371, 376, 383, 384, 395, 401, 412, 416, 421, 423, 434, 441, 446, 449, 454,
                        455, 464, 470, 471, 480, 489, 490, 500, 502, 507, 513, 514, 516, 520, 524, 531, 532, 545, 546,
                        549, 552, 556, 558, 564, 570, 586, 592, 599, 600, 607, 633, 658, 665)));
        org.hamcrest.MatcherAssert.assertThat(
                superBits.asBitSet(),
                is(asBitSet(9, 10, 11, 14, 18, 19, 33, 34, 143, 146, 150, 153, 255, 256, 257, 258, 283, 284, 285, 293,
                        301, 332, 344, 349, 351, 353, 355, 368, 370, 371, 374, 376, 383, 384, 395, 401, 412, 416, 417,
                        421, 423, 427, 434, 441, 446, 449, 454, 455, 460, 464, 470, 471, 479, 480, 489, 490, 500, 502,
                        507, 513, 514, 516, 520, 524, 531, 532, 545, 546, 549, 552, 556, 558, 564, 570, 578, 582, 584,
                        586, 592, 595, 600, 603, 607, 608, 633, 634, 640, 658, 660, 664, 665, 668, 677, 678, 683)));
    }

}
