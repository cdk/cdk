/* Copyright (C) 2008 Rajarshi Guha
 *               2009,2011 Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 * Contact: rajarshi@users.sourceforge.net
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

import java.util.BitSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @cdk.module test-fingerprint
 */
public class MACCSFingerprinterTest extends AbstractFixedLengthFingerprinterTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(MACCSFingerprinterTest.class);
    private static String[] uniqueMACCSKeys = {
            "(*).(*)",
            "*(~*)(~*)(~*)~*",
            "Br",
            "Cl",
            "F",
            "[!#1;!#6;!#7;!#8;!#9;!#14;!#15;!#16;!#17;!#35;!#53]",
            "[!#6;!#1;!H0]",
            "[!#6;!#1;!H0]~*~*~*~[!#6;!#1;!H0]",
            "[!#6;!#1;!H0]~*~*~[!#6;!#1;!H0]",
            "[!#6;!#1;!H0]~*~[CH2]~*",
            "[!#6;!#1;!H0]~[!#6;!#1;!H0]",
            "[!#6;!#1](~*)(~*)~*",
            "[!#6;!#1]1~*~*~*~*~1",
            "[!#6;!#1]1~*~*~*~1",
            "[!#6;!#1]~*(~[!#6;!#1])~[!#6;!#1]",
            "[!#6;!#1]~[!#6;!#1;!H0]",
            "[!#6;!#1]~[!#6;!#1]",
            "[!#6;!#1]~[#16]",
            "[!#6;!#1]~[#16]~[!#6;!#1]",
            "[!#6;!#1]~[#7]",
            "[!#6;!#1]~[#7]~[!#6;!#1]",
            "[!#6;!#1]~[#8]",
            "[!#6;!#1]~[CH2]~*",
            "[!#6;!#1]~[CH2]~[!#6;!#1]",
            "[!#6;!#1]~[CH3]",
            "[!#6;!#1]~[F,Cl,Br,I]",
            "[!#6;R]",
            "[!*]",
            "[!+0]",
            "[!0]",
            "[!C;!c;!#1;!H0]~*~[!C;!c;!#1;!H0]",
            "[!C;!c;R]",
            "[#104,#105,#106,#107,#108,#109,#110,#111,#112]",
            "[#15]",
            "[#16R]",
            "[#16]",
            "[#16]!:*:*",
            "[#16]!@*@*",
            "[#16](~*)(~*)~*",
            "[#16]-[#16]",
            "[#16]-[#8]",
            "[#16]=*",
            "[#16]=[#8]",
            "[#16]~*(~*)~*",
            "[#16]~*~[#7]",
            "[#5,Al,Ga,In,Tl]",
            "[#6H2]([#6H2]*)*",
            "[#6H2](~*~*~*~[!#6!#1!H0])~*",
            "[#6H2](~*~*~*~[#6H2]~*)~*",
            "[#6H2](~*~*~[!#6!#1!H0])~*",
            "[#6H2](~*~*~[#6H2]~*)~*",
            "[#6H2](~[#6H2]~*)~*",
            "[#6H3]~*~*~[#6H2]~*",
            "[#6]#[#6]",
            "[#6]#[#7]",
            "[#6]-[#7]",
            "[#6]-[#8]",
            "[#6]=;@[#6](@*)@*",
            "[#6]=[#6]",
            "[#6]=[#6](~*)~*",
            "[#6]=[#6](~[!#6;!#1])~[!#6;!#1]",
            "[#6]=[#6](~[#6])~[#6]",
            "[#6]=[#6]~[#7]",
            "[#6]=[#7]",
            "[#6]=[#8]",
            "[#6]~[!#6;!#1](~[#6])(~[#6])~*",
            "[#6]~[#16]~[#7]",
            "[#6]~[#16]~[#8]",
            "[#6]~[#6](~[#6])(~[#6])~*",
            "[#6]~[#7](~[#6])~[#6]",
            "[#7;!H0]",
            "[#7;R]",
            "[#7]",
            "[#7]!:*:*",
            "[#7]!@*@*",
            "[#7](!@*)@*",
            "[#7](~*)(~*)~*",
            "[#7]-[#8]",
            "[#7]=*",
            "[#7]=[#8]",
            "[#7]~*(~*)~*",
            "[#7]~*~*~*~[#7]",
            "[#7]~*~*~[#7]",
            "[#7]~*~*~[#8]",
            "[#7]~*~[#7]",
            "[#7]~*~[#8]",
            "[#7]~*~[CH2]~*",
            "[#7]~[#16]",
            "[#7]~[#6](~[#6])~[#7]",
            "[#7]~[#6](~[#7])~[#7]",
            "[#7]~[#6](~[#8])~[#7]",
            "[#7]~[#6](~[#8])~[#8]",
            "[#7]~[#7]",
            "[#7]~[#8]",
            "[#7]~[CH2]~*",
            "[#8R]",
            "[#8]",
            "[#8]!:*:*",
            "[#8]!@[R]@*",
            "[#8](!@*)!@*",
            "[#8]=*",
            "[#8]~*~*~*~[#7]",
            "[#8]~*~*~*~[#8]",
            "[#8]~*~*~[#8]",
            "[#8]~*~[CH2]~*",
            "[#8]~[!#6;!#1](~[#8])(~[#8])",
            "[#8]~[#16](~[#8])~[#8]",
            "[#8]~[#16]~[#8]",
            "[#8]~[#6](~[#6])~[#6]",
            "[#8]~[#6](~[#7])~[#6]",
            "[#8]~[#6](~[#8])~[#8]",
            "[#8]~[#6]~[#7]",
            "[#8]~[#6]~[#8]",
            "[#8]~[#7](~[#6])~[#6]",
            "[#8]~[#7](~[#8])~[#6]",
            "[#8]~[CH2]~*",
            "[Ac,Th,Pa,U,Np,Pu,Am,Cm,Bk,Cf,Es,Fm,Md,No,Lr]",
            "[Be,Mg,Ca,Sr,Ba,Ra]",
            "[C;H2,H3][!#6;!#1][C;H2,H3]",
            "[C;H3,H4]",
            "[CH2](!@*)!@*",
            "[CH2](~*)~[!#6;!#1;!H0]",
            "[CH2]=*",
            "[CH3]",
            "[CH3]~*~*~*~[CH2]~*",
            "[CH3]~*~[CH2]~*",
            "[CH3]~*~[CH3]",
            "[CH3]~[CH2]~*",
            "[Cu,Zn,Ag,Cd,Au,Hg]",
            "[F,Cl,Br,I]",
            "[F,Cl,Br,I]!@*@*",
            "[F,Cl,Br,I]~*(~*)~*",
            "[Fe,Co,Ni,Ru,Rh,Pd,Os,Ir,Pt]",
            "[Ge,As,as,Se,se,Sn,Sb,Te,Tl,Pb,Bi]",
            "[I]",
            "[La,Ce,Pr,Nd,Pm,Sm,Eu,Gd,Tb,Dy,Ho,Er,Tm,Yb,Lu]",
            "[Li,Na,K,Rb,Cs,Fr]",
            "[NH2]",
            "[O;!H0]",
            "[R!#6!#1]1@*@*@*@*@*@1",
            "[R!#6!#1]1~*~*~1",
            "[R!x2](@*)(@*)@*",
            "[R]",
            "[R]!@[R]",
            "[R](!@*)@[R]!@*",
            "[R]1@*@*@*@*@*@*@*@1",
            "[R]1@*@*@*@*@*@*@1",
            "[R]1@*@*@*@*@*@1",
            "[R]1@*@*@*@*@1",
            "[R]1@*@*@*@1",
            "[R]1@*@*@1",
            "[Sc,Ti,Y,Zr,Hf]",
            "[Si]",
            "[V,Cr,Mn,Nb,Mo,Tc,Ta,W,Re]",
            "a",
            "a(!:*):a!:*",
            "n:c"};

    @Override
    public IFingerprinter getBitFingerprinter() {
        return new MACCSFingerprinter();
    }

    @Test
    public void getsize() throws Exception {
        IFingerprinter printer = new MACCSFingerprinter(SilentChemObjectBuilder.getInstance());
        Assert.assertEquals(166, printer.getSize());
    }

    @Test
    public void testGetCountFingerprint() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new MACCSFingerprinter();

        IAtomContainer mol = parser.parseSmiles("");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        ICountFingerprint fp = printer.getCountFingerprint(mol);

        Assert.assertEquals(fp.numOfPopulatedbins(), 157);
        Assert.assertEquals(fp.size(), 157);
        for (int i = 0; i < fp.size(); i++) {
            Assert.assertEquals(fp.getCount(i), 0);
            Assert.assertEquals(fp.getCountForHash(i), 0);
            Assert.assertEquals(fp.getHash(i), i);
        }
    }

    @Test
    public void testRawFingerprintCornerCases() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new MACCSFingerprinter();

        IAtomContainer mol = parser.parseSmiles("");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        Map<String, Integer> fp = printer.getRawFingerprint(mol);

        Assert.assertTrue(fp.size() == 157); // Number of unique keys in the MACCS fp definition
        for (Integer value : fp.values()) {
            Assert.assertTrue(value == 0); // All keys are initialized with count = 0
        }
    }

    @Test
    public void testRawFingerprintsIsotopes() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new MACCSFingerprinter();

        // Isotopic molecule: Carbon C-13
        IAtomContainer mol = parser.parseSmiles("[13CH4]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        Map<String, Integer> fp = printer.getRawFingerprint(mol);

        Assert.assertTrue(fp.get("[!0]") == 1);

        // Non-isotopic molecule: Carbon C-13
        mol = parser.parseSmiles("c1ccccc1CCc1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        fp = printer.getRawFingerprint(mol);

        Assert.assertTrue(fp.get("[!0]") == 0);
    }

    @Test
    public void testRawFingerprintsGroups() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new MACCSFingerprinter();

        IAtomContainer mol = parser.parseSmiles("P(#[Fe])=[Fe]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        Map<String, Integer> fp = printer.getRawFingerprint(mol);

        Assert.assertTrue(fp.get("[Fe,Co,Ni,Ru,Rh,Pd,Os,Ir,Pt]") == 1);
        Assert.assertTrue(fp.get("[Ge,As,as,Se,se,Sn,Sb,Te,Tl,Pb,Bi]") == 0);
    }

    @Test
    public void testRawFingerprintsRings() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new MACCSFingerprinter();
        IAtomContainer mol;
        Map<String, Integer> fp;

        // Aromatic 6-rings
        mol = parser.parseSmiles("C1=CC=CC(=C1)CCCC2=CC=CC=C2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        fp = printer.getRawFingerprint(mol);

        Assert.assertTrue(fp.get("[R]1@*@*@*@*@*@1") == 2); // 6-ring
        Assert.assertTrue(fp.get("[!*]") == 2); // aromaticity

        Assert.assertTrue(fp.get("[R]1@*@*@*@*@*@*@1") == 0); // 7-ring
        Assert.assertTrue(fp.get("[R]1@*@*@*@*@1") == 0); // 5-ring

        // Non-aromatic 6-rings
        mol = parser.parseSmiles("C1CC(CCC1)CCCCC2CCCCC2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        fp = printer.getRawFingerprint(mol);

        Assert.assertTrue(fp.get("[R]1@*@*@*@*@*@1") == 2); // 6-ring

        Assert.assertTrue(fp.get("[!*]") == 0); // aromaticity
        Assert.assertTrue(fp.get("[R]1@*@*@*@*@*@*@1") == 0); // 7-ring
        Assert.assertTrue(fp.get("[R]1@*@*@*@*@1") == 0); // 5-ring

        // Aromatic 6-ring, 3-ring and 4-ring
        mol = parser.parseSmiles("C1CC1C(CCC2CCC2)CC3=CC=CC=C3");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        fp = printer.getRawFingerprint(mol);

        Assert.assertTrue(fp.get("[R]1@*@*@*@*@*@1") == 1); // 6-ring
        Assert.assertTrue(fp.get("[!*]") == 1); // aromaticity
        Assert.assertTrue(fp.get("[R]1@*@*@1") == 1); // 3-ring
        Assert.assertTrue(fp.get("[R]1@*@*@*@1") == 1); // 4-ring

        Assert.assertTrue(fp.get("[R]1@*@*@*@*@*@*@1") == 0); // 7-ring
        Assert.assertTrue(fp.get("[R]1@*@*@*@*@1") == 0); // 5-ring

        // Aromatic 6-ring, 3-ring and 4-ring
        mol = parser.parseSmiles("C1(CC1C(CCC2CCC2)CC3=CC=CC=C3)C(C(C(C4CC4)C5CC5)C6CC6)C7CC7");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        fp = printer.getRawFingerprint(mol);

        Assert.assertTrue(fp.get("[R]1@*@*@*@*@*@1") == 1); // 6-ring
        Assert.assertTrue(fp.get("[!*]") == 1); // aromaticity
        Assert.assertTrue(fp.get("[R]1@*@*@1") == 5); // 3-ring
        Assert.assertTrue(fp.get("[R]1@*@*@*@1") == 1); // 4-ring

        Assert.assertTrue(fp.get("[R]1@*@*@*@*@*@*@1") == 0); // 7-ring
        Assert.assertTrue(fp.get("[R]1@*@*@*@*@1") == 0); // 5-ring
    }

    @Test
    public void testRawFingerprintsFragments() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new MACCSFingerprinter();

        IAtomContainer mol_0 = parser.parseSmiles("");
        IAtomContainer mol_1_s = parser.parseSmiles("C");
        IAtomContainer mol_1 = parser.parseSmiles("CCC");
        IAtomContainer mol_2 = parser.parseSmiles("CCC.CCC");
        IAtomContainer mol_3 = parser.parseSmiles("CCC.CCC.CCC");
        IAtomContainer mol_8 = parser.parseSmiles("[SH-].[SH-].[SH-].[SH-].[Fe].[Fe].[Fe].[Fe]");

        // Molecule with 0 fragment
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol_0);
        Aromaticity.cdkLegacy().apply(mol_0);
        Map<String, Integer> fp_0 = printer.getRawFingerprint(mol_0);
        Assert.assertTrue(fp_0.get("(*).(*)") == 0);

        // Molecule with a single atom
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol_1_s);
        Aromaticity.cdkLegacy().apply(mol_1_s);
        Map<String, Integer> fp_1_s = printer.getRawFingerprint(mol_1_s);
        Assert.assertTrue(fp_1_s.get("(*).(*)") == 1);

        // Molecule with 1 fragment
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol_1);
        Aromaticity.cdkLegacy().apply(mol_1);
        Map<String, Integer> fp_1 = printer.getRawFingerprint(mol_1);
        Assert.assertTrue(fp_1.get("(*).(*)") == 1);

        // Molecule with 2 fragment
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol_2);
        Aromaticity.cdkLegacy().apply(mol_2);
        Map<String, Integer> fp_2 = printer.getRawFingerprint(mol_2);
        Assert.assertTrue(fp_2.get("(*).(*)") == 2);

        // Molecule with 1 fragment
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol_3);
        Aromaticity.cdkLegacy().apply(mol_3);
        Map<String, Integer> fp_3 = printer.getRawFingerprint(mol_3);
        Assert.assertTrue(fp_3.get("(*).(*)") == 3);

        // Molecule with 1 fragment
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol_8);
        Aromaticity.cdkLegacy().apply(mol_8);
        Map<String, Integer> fp_8 = printer.getRawFingerprint(mol_8);
        Assert.assertTrue(fp_8.get("(*).(*)") == 8);
        Assert.assertTrue(fp_8.get("[Fe,Co,Ni,Ru,Rh,Pd,Os,Ir,Pt]") == 1);
    }

    @Test
    public void testGetRawFingerprint() throws Exception {
        // NOTE: See further tests on specific substructures and properties, e.g. rings, isotopic, ...

        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new MACCSFingerprinter();
        IAtomContainer mol;
        Map<String, Integer> fp;

        // Test molecule 1
        mol = parser.parseSmiles("C([S](O)(=O)=O)C1=C(C=CC=C1)CCCC[N+](=O)[O-]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        fp = printer.getRawFingerprint(mol);

        Assert.assertTrue(fp.get("[#16]=[#8]") == 2);
        Assert.assertTrue(fp.get("[#16]-[#8]") == 1);
        Assert.assertTrue(fp.get("[#16]=*") == 2);
        Assert.assertTrue(fp.get("[#7]=[#8]") == 1);
        Assert.assertTrue(fp.get("[!*]") == 1);
        Assert.assertTrue(fp.get("[#6H2](~[#6H2]~*)~*") == 3);
        Assert.assertTrue(fp.get("[#6H2](~*~*~[#6H2]~*)~*") == 2);
        Assert.assertTrue(fp.get("[#8]=*") == 3);
        Assert.assertTrue(fp.get("[#8]~[#16]~[#8]") == 3);

        Assert.assertTrue(fp.get("[F,Cl,Br,I]~*(~*)~*") == 0);
        Assert.assertTrue(fp.get("[R!x2](@*)(@*)@*") == 0);
        Assert.assertTrue(fp.get("[F,Cl,Br,I]") == 0);

        // Test molecule 2: Diatrizoic acid
        mol = parser.parseSmiles("CC(=O)NC1=C(C(=C(C(=C1I)C(=O)O)I)NC(=O)C)I");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        fp = printer.getRawFingerprint(mol);

        Assert.assertTrue(fp.get("[I]") == 3);
        Assert.assertTrue(fp.get("[#6]=[#8]") == 3);
        Assert.assertTrue(fp.get("[#6]-[#7]") == 4);
        Assert.assertTrue(fp.get("[F,Cl,Br,I]~*(~*)~*") == 3);
        Assert.assertTrue(fp.get("[F,Cl,Br,I]!@*@*") == 6);
        Assert.assertTrue(fp.get("[F,Cl,Br,I]") == 1);

        Assert.assertTrue(fp.get("[R!x2](@*)(@*)@*") == 0);
    }

    @Test
    public void testFingerprint() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new MACCSFingerprinter();

        IAtomContainer mol0 = parser.parseSmiles("C1=CC=CC(=C1)CCCCC2=CC=CC=C2");
        IAtomContainer mol1 = parser.parseSmiles("c1ccccc1CCc1ccccc1");
        IAtomContainer mol2 = parser.parseSmiles("c1ccccc1CC");
        IAtomContainer mol3 = parser.parseSmiles("CCC.CCC");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol0);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol3);
        Aromaticity.cdkLegacy().apply(mol0);
        Aromaticity.cdkLegacy().apply(mol1);
        Aromaticity.cdkLegacy().apply(mol2);
        Aromaticity.cdkLegacy().apply(mol3);

        BitSet bs0 = printer.getBitFingerprint(mol0).asBitSet();
        BitSet bs1 = printer.getBitFingerprint(mol1).asBitSet();
        BitSet bs2 = printer.getBitFingerprint(mol2).asBitSet();
        BitSet bs3 = printer.getBitFingerprint(mol3).asBitSet();

        Assert.assertEquals(166, printer.getSize());

        Assert.assertFalse(bs1.get(165));
        Assert.assertTrue(bs1.get(124));

        Assert.assertFalse(bs2.get(124));

        Assert.assertTrue(bs3.get(165));

        Assert.assertFalse(FingerprinterTool.isSubset(bs1, bs2));
    }

    @Test
    public void testfp2() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new MACCSFingerprinter();

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

        Assert.assertFalse(bs1.get(124));
        Assert.assertFalse(bs2.get(124));
        Assert.assertFalse(bs3.get(124));

        Assert.assertFalse(FingerprinterTool.isSubset(bs1, bs2));
        Assert.assertTrue(FingerprinterTool.isSubset(bs2, bs3));
    }

    /**
     * Using MACCS keys, these molecules are not considered substructures
     * and should only be used for similarity. This is because the MACCS
     * fragments match hydrogen counts.
     */
    @Test
    @Override
    public void testBug706786() throws Exception {

        IAtomContainer superStructure = bug706786_1();
        IAtomContainer subStructure = bug706786_2();

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(superStructure);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(subStructure);
        addImplicitHydrogens(superStructure);
        addImplicitHydrogens(subStructure);

        IFingerprinter fpr = new MACCSFingerprinter();
        IBitFingerprint superBits = fpr.getBitFingerprint(superStructure);
        IBitFingerprint subBits = fpr.getBitFingerprint(subStructure);

        assertThat(
                superBits.asBitSet(),
                is(asBitSet(53, 56, 65, 71, 73, 88, 97, 104, 111, 112, 126, 130, 136, 138, 139, 140, 142, 143,
                        144, 145, 148, 149, 151, 153, 156, 158, 159, 161, 162, 163, 164)));
        assertThat(
                subBits.asBitSet(),
                is(asBitSet(56, 97, 104, 108, 112, 117, 131, 136, 143, 144, 146, 151, 152, 156, 161, 162, 163, 164)));
    }

}
