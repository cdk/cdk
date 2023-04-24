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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @cdk.module test-fingerprint
 */
public class PubchemFingerprinterTest extends AbstractFixedLengthFingerprinterTest {

    private SmilesParser parser;

    @Override
    public IFingerprinter getBitFingerprinter() {
        return new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
    }

    @BeforeEach
    void setup() {
        parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
    }

    @Test
    void testGetSize() throws Exception {
        IFingerprinter printer = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(881, printer.getSize());
    }

    @Test
    void testFingerprint() throws Exception {
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

        Assertions.assertEquals(881, printer.getSize());

        Assertions.assertFalse(FingerprinterTool.isSubset(bs1, bs2), "c1ccccc1CC was detected as a subset of c1ccccc1CCc1ccccc1");
    }

    @Test
    void testfp2() throws Exception {
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

        Assertions.assertTrue(FingerprinterTool.isSubset(bs1, bs2));
        Assertions.assertTrue(FingerprinterTool.isSubset(bs2, bs3));
    }

    /**
     * Test case for Pubchem CID 25181308.
     *
     * @throws InvalidSmilesException
     * @cdk.inchi InChI=1S/C13H24O10S/c1-20-12-8(18)6(16)10(4(2-14)21-12)23-13-9(19)7(17)11(24)5(3-15)22-13/h4-19,24H,2-3H2,1H3/t4-,5-,6-,7-,8-,9-,10-,11-,12-,13+/m1/s1
     */
    @Test
    void testCID2518130() throws CDKException {
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

        Assertions.assertEquals(ref, fp);
    }

    /**
     * Test case for Pubchem CID 5934166.
     *
     * @throws InvalidSmilesException
     * @cdk.inchi InChI=1S/C32H26N/c1-5-13-26(14-6-1)21-22-31-23-30(28-17-9-3-10-18-28)24-32(29-19-11-4-12-20-29)33(31)25-27-15-7-2-8-16-27/h1-24H,25H2/q+1/b22-21+
     */
    @Test
    void testCID5934166() throws CDKException {
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

        Assertions.assertEquals(ref, fp);
    }

    /**
       * Test case for Pubchem CID 25181289.
       *
       * @throws InvalidSmilesException
       * @cdk.inchi  InChI=1S/C14H10Cl3N3O3/c1-6(7-2-4-8(21)5-3-7)19-20-11-9(15)12(14(22)23)18-13(17)10(11)16/h2-5,19,21H,1H2,(H,18,20)(H,22,23)
       */
    @Test
    void testCID25181289() throws CDKException {
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

        Assertions.assertEquals(ref, fp);
    }

    @Test
    void testGetFingerprintAsBytes() throws CDKException {

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

        Assertions.assertArrayEquals(expected, actual);

    }

    // adapted from: http://stackoverflow.com/questions/6197411/converting-from-bitset-to-byte-array
    static byte[] toByteArray(BitSet bits) {
        byte[] bytes = new byte[bits.length() / 8 + 1];
        for (int i = 0; i < bits.length(); i++) {
            if (bits.get(i)) {
                bytes[i / 8] |= 1 << (7 - i % 8);
            }
        }
        return bytes;
    }

    @Test
    void testDecode_invalid()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            PubchemFingerprinter.decode("a");
        });
    }

    @Test
    void testDecode() {
        BitSet bitSet = PubchemFingerprinter
                .decode("AAADcYBgAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAABAAAAGAAAAAAACACAEAAwAIAAAACAACBCAAACAAAgAAAIiAAAAIgIICKAERCAIAAggAAIiAcAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==");
        int[] setBits = new int[]{0, 9, 10, 178, 179, 255, 283, 284, 332, 344, 355, 370, 371, 384, 416, 434, 441, 446,
                470, 490, 516, 520, 524, 552, 556, 564, 570, 578, 582, 584, 595, 599, 603, 608, 618, 634, 640, 660,
                664, 668, 677, 678, 679};
        for (int set : setBits) {
            Assertions.assertTrue(bitSet.get(set), "bit " + set + " was not set");
        }
    }

    @Test
    void testBenzene() throws CDKException {
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

        Assertions.assertEquals(ref, fp);

    }

    /**
     * @throws Exception
     * @cdk.bug 3510588
     */
    @Test
    void testMultithreadedUsage() throws Exception {
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

            final IAtomContainer mol;

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
                    LoggingToolFactory.createLoggingTool(PubchemFingerprinterTest.class)
                                      .warn("FP Error:", e);
                }
                return fp;
            }
        }

        // now lets run some threads
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<FpRunner> tasks = new ArrayList<>();
        tasks.add(new FpRunner(mol1));
        tasks.add(new FpRunner(mol2));
        List<Future<BitSet>> ret = executor.invokeAll(tasks);

        BitSet fb1 = ret.get(0).get();
        Assertions.assertNotNull(fb1);

        BitSet fb2 = ret.get(1).get();
        Assertions.assertNotNull(fb2);

        Assertions.assertEquals(bs1, fb1);
        Assertions.assertEquals(bs2, fb2);
    }

    private String explain(BitSet expected, BitSet actual) {
        BitSet tmp1 = new BitSet();
        tmp1.or(expected);
        tmp1.andNot(actual);
        BitSet tmp2 = new BitSet();
        tmp2.or(actual);
        tmp2.andNot(expected);
        return "missed=" + tmp1 + " extra=" + tmp2;
    }

    // Compare a SMILES string to the expected "CACTVS_SUBSTRUCTURE_KEYS" field
    // used in PubChem
    private void assertBase64Fingerprint(String expected,
                                         String smiles) throws CDKException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(smiles);
        BitSet expectedBits = PubchemFingerprinter.decode(expected);
        PubchemFingerprinter fpr = new PubchemFingerprinter(SilentChemObjectBuilder.getInstance());
        BitSet actualBits = fpr.getBitFingerprint(mol).asBitSet();
        Assertions.assertEquals(expectedBits, actualBits,
                                explain(expectedBits, actualBits));
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
    void testBug934819() throws Exception {
        assertBase64Fingerprint("AAADcQBiMABAAAAAAAAAAAAAAAAAASAAAAAAAAAAAAAAAAABgAAAHAQEAAAACACFUACwgYAQQAiFACBCQwCDAIBgChBoiBgAZIoIIAAggYEAAABAAAAgQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==",
                                "[O-][N+](=O)C1=CC=CS1 bug934819_1");
        assertBase64Fingerprint("AAADccByMABgAAAAAAAAAAAAAAAAASJAAAAAAAAAAAAAAAAB4AAAHAQEAAAACACFUACygYAQQAjFECBCQwiDAYBgChBoiBgAZIoIICKgkJGAAABggAAoyAYQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==",
                                "CCCCSC1=CC=C(S1)C#CC1=CC=C(S1)[N+]([O-])=O bug934819_2");
    }

    /**
     * Overloads underlying AbstractTest, PC fingerprint can not be used for
     * substructure queries. We check we match the PubChem values for these
     * molecules.
     * @cdk.bug 853254
     */
    @Test
    void testBug853254() throws Exception {
        // bug853254-1.mol
        assertBase64Fingerprint("AAADcYBwMAAAAAAAAAAAAAAAAAAAASAAAAAwAAAAAAAAAEgBAAAAGgAAAAAADACAmAAwCIAABACIAiDSCAACAAAkAAAIiAEACMgIJjKANRiCMQAkwAEIqYeLyKCOgAAAAAAQAAAAAAAAACAAAAAAAAAAAA==",
                                "c1cccc2c1c(=O)oc(=O)2");
        // bug853254-2.mol
        assertBase64Fingerprint("AAADcQBwMAAEAAAAAAAAAAAAAAAAASAAAAAwAAAAAAAAAEgBAAAAGgIAAAAADAKAmCAwCIAABACIAiDSCAACAAAkBQAIiAEACsgIJjKBNxiCMQAkwAEIrYeLyKCOgAAAIAARAAAAAABAACIAAAAAAAAAAA==",
                                "c1ccc(Cl)c2c1c(=O)oc(=O)2");
    }

    // ensuring ESSSR is used, in this case SMARTS can not be used as it is not
    // considered to have a 7 membered ring (e.g. bit213).
    @Test
    void testCID6249() throws Exception {
        assertBase64Fingerprint("AAADccBgAAAAAAAAAAAAAAAAGDAAAYAAAAAwYAAAAAAAAAAAAAAAGAAAAAAADQCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAEAgAAOgAAAAAAAAAAAAAAAAAAAAQAACAAAAA==",
                                "C1C2CC2C3C1C3");
    }
}
