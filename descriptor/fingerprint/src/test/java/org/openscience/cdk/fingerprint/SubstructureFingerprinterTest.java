/* Copyright (C) 1997-2007  The Chemistry Development Kit (CKD) project
 *               2009,2011  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.BitSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module test-fingerprint
 */
public class SubstructureFingerprinterTest extends AbstractFixedLengthFingerprinterTest {

    @Override
    public IFingerprinter getBitFingerprinter() {
        return new SubstructureFingerprinter();
    }

    @Test
    void testSize() throws Exception {
        SubstructureFingerprinter fp = new SubstructureFingerprinter();
        Assertions.assertEquals(307, fp.getSize());

        fp = new SubstructureFingerprinter(StandardSubstructureSets.getFunctionalGroupSMARTS());
        Assertions.assertEquals(307, fp.getSize());

        fp = new SubstructureFingerprinter(StandardSubstructureSets.getCountableMACCSSMARTS());
        Assertions.assertEquals(142, fp.getSize());
    }

    @Test
    @Override
    void testBug706786() throws Exception {

        IAtomContainer superStructure = bug706786_1();
        IAtomContainer subStructure = bug706786_2();

        addImplicitHydrogens(superStructure);
        addImplicitHydrogens(subStructure);

        // SMARTS is now correct and D will include H atoms, CDK had this wrong
        // for years (had it has non-H count). Whilst you can set the optional
        // SMARTS flavor CDK_LEGACY this is not correct
        AtomContainerManipulator.suppressHydrogens(superStructure);
        AtomContainerManipulator.suppressHydrogens(subStructure);

        IFingerprinter fpr = getBitFingerprinter();
        IBitFingerprint superBits = fpr.getBitFingerprint(superStructure);
        IBitFingerprint subBits = fpr.getBitFingerprint(subStructure);

        assertThat(superBits.asBitSet(),
                is(asBitSet(0, 11, 13, 17, 40, 48, 136, 273, 274, 278, 286, 294, 299, 301, 304, 306)));
        assertThat(subBits.asBitSet(), is(asBitSet(1, 17, 273, 274, 278, 294, 306)));
    }

    @Test
    void testUserFunctionalGroups() throws Exception {
        String[] smarts = {"c1ccccc1", "[CX4H3][#6]", "[CX2]#[CX2]"};
        IFingerprinter printer = new SubstructureFingerprinter(smarts);
        Assertions.assertEquals(3, printer.getSize());

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = sp.parseSmiles("c1ccccc1CCC");
        IBitFingerprint fp = printer.getBitFingerprint(mol1);
        Assertions.assertNotNull(fp);

        Assertions.assertTrue(fp.get(0));
        Assertions.assertTrue(fp.get(1));
        Assertions.assertFalse(fp.get(2));

        mol1 = sp.parseSmiles("C=C=C");
        fp = printer.getBitFingerprint(mol1);
        Assertions.assertNotNull(fp);
        Assertions.assertFalse(fp.get(0));
        Assertions.assertFalse(fp.get(1));
        Assertions.assertFalse(fp.get(2));
    }

    @Test
    void testFunctionalGroupsBinary() throws Exception {
        IFingerprinter printer = new SubstructureFingerprinter();
        Assertions.assertEquals(307, printer.getSize());

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = sp.parseSmiles("c1ccccc1CCC");
        IBitFingerprint fp = printer.getBitFingerprint(mol1);
        Assertions.assertNotNull(fp);
        Assertions.assertTrue(fp.get(273));
        Assertions.assertTrue(fp.get(0));
        Assertions.assertTrue(fp.get(1));
        Assertions.assertFalse(fp.get(100));
    }

    @Test
    void testFunctionalGroupsCount() throws Exception {
        // TODO: Implement tests
    }

    @Test
    void testCountableMACCSBinary() throws Exception {
        // Tests are modified copy of the test included in the MACCS-FPs class

        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new SubstructureFingerprinter(StandardSubstructureSets.getCountableMACCSSMARTS());
        Assertions.assertEquals(142, printer.getSize());

        IAtomContainer mol0 = parser.parseSmiles("CC(N)CCCN");
        IAtomContainer mol1 = parser.parseSmiles("c1ccccc1CCc1ccccc1");
        IAtomContainer mol2 = parser.parseSmiles("c1ccccc1CC");
        IAtomContainer mol3 = parser.parseSmiles("CC(N)CCC");
        IAtomContainer mol4 = parser.parseSmiles("CCCC");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol0);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol3);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol4);

        Aromaticity.cdkLegacy().apply(mol0);
        Aromaticity.cdkLegacy().apply(mol1);
        Aromaticity.cdkLegacy().apply(mol2);
        Aromaticity.cdkLegacy().apply(mol3);
        Aromaticity.cdkLegacy().apply(mol4);

        BitSet bs0 = printer.getBitFingerprint(mol0).asBitSet();
        BitSet bs1 = printer.getBitFingerprint(mol1).asBitSet();
        BitSet bs2 = printer.getBitFingerprint(mol2).asBitSet();
        BitSet bs3 = printer.getBitFingerprint(mol3).asBitSet();
        BitSet bs4 = printer.getBitFingerprint(mol4).asBitSet();

        // Check for the aromatic 6M rings
        Assertions.assertFalse(bs0.get(111));
        Assertions.assertTrue(bs1.get(111));
        Assertions.assertTrue(bs2.get(111));
        Assertions.assertFalse(bs3.get(111));
        Assertions.assertFalse(bs4.get(111));

        // Check for the fingerprints being subsets
        Assertions.assertFalse(FingerprinterTool.isSubset(bs1, bs2));
        Assertions.assertFalse(FingerprinterTool.isSubset(bs0, bs3));
        Assertions.assertTrue(FingerprinterTool.isSubset(bs3, bs4));
    }

    @Test
    void testCountableMACCSBinary2() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new SubstructureFingerprinter(StandardSubstructureSets.getCountableMACCSSMARTS());
        IAtomContainer mol;
        BitSet bs;

        // Test molecule 1
        mol = parser.parseSmiles("C([S](O)(=O)=O)C1=C(C=CC=C1)CCCC[N+](=O)[O-]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        bs = printer.getBitFingerprint(mol).asBitSet();

        Assertions.assertTrue(bs.get(46));
        Assertions.assertTrue(bs.get(27));
        Assertions.assertTrue(bs.get(59));
        Assertions.assertTrue(bs.get(49));
        Assertions.assertTrue(bs.get(111));
        Assertions.assertTrue(bs.get(129));
        Assertions.assertTrue(bs.get(115));
        Assertions.assertTrue(bs.get(120));
        Assertions.assertTrue(bs.get(41));

        Assertions.assertFalse(bs.get(93));
        Assertions.assertFalse(bs.get(91));
        Assertions.assertFalse(bs.get(24));

        // Test molecule 2: Diatrizoic acid
        mol = parser.parseSmiles("CC(=O)NC1=C(C(=C(C(=C1I)C(=O)O)I)NC(=O)C)I");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        bs = printer.getBitFingerprint(mol).asBitSet();

        Assertions.assertTrue(bs.get(15));
        Assertions.assertTrue(bs.get(135));
        Assertions.assertTrue(bs.get(139));
        Assertions.assertTrue(bs.get(93));
        Assertions.assertTrue(bs.get(73));

        Assertions.assertFalse(bs.get(91));
    }

    @Test
    public void testGetCountFingerprint() throws Exception {
        // See other function for specific test cases
    }

    @Test
    void testCountableMACCSCount2() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new SubstructureFingerprinter(StandardSubstructureSets.getCountableMACCSSMARTS());
        IAtomContainer mol;
        ICountFingerprint cfp;

        // Test molecule 1
        mol = parser.parseSmiles("C([S](O)(=O)=O)C1=C(C=CC=C1)CCCC[N+](=O)[O-]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        cfp = printer.getCountFingerprint(mol);

        Assertions.assertEquals(cfp.getCountForHash(46), 2);
        Assertions.assertEquals(cfp.getCountForHash(27), 1);
        Assertions.assertEquals(cfp.getCountForHash(59), 2);
        Assertions.assertEquals(cfp.getCountForHash(49), 1);
        Assertions.assertEquals(cfp.getCountForHash(111), 1);
        Assertions.assertEquals(cfp.getCountForHash(129), 3);
        Assertions.assertEquals(cfp.getCountForHash(115), 2);
        Assertions.assertEquals(cfp.getCountForHash(120), 3);
        Assertions.assertEquals(cfp.getCountForHash(41), 3);

        Assertions.assertEquals(cfp.getCountForHash(93), 0);
        Assertions.assertEquals(cfp.getCountForHash(91), 0);
        Assertions.assertEquals(cfp.getCountForHash(24), 0);

        // Test molecule 2: Diatrizoic acid
        mol = parser.parseSmiles("CC(=O)NC1=C(C(=C(C(=C1I)C(=O)O)I)NC(=O)C)I");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        cfp = printer.getCountFingerprint(mol);

        Assertions.assertEquals(cfp.getCountForHash(15), 3);
        Assertions.assertEquals(cfp.getCountForHash(135), 3);
        Assertions.assertEquals(cfp.getCountForHash(139), 4);
        Assertions.assertEquals(cfp.getCountForHash(93), 3);
        Assertions.assertEquals(cfp.getCountForHash(73), 6);

        Assertions.assertEquals(cfp.getCountForHash(91), 0);
    }

    @Test
    void testCountableMACCSCount_Rings() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new SubstructureFingerprinter(StandardSubstructureSets.getCountableMACCSSMARTS());
        IAtomContainer mol;
        ICountFingerprint cfp;

        // Aromatic 6-rings
        mol = parser.parseSmiles("C1=CC=CC(=C1)CCCC2=CC=CC=C2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        cfp = printer.getCountFingerprint(mol);

        Assertions.assertEquals(cfp.getCountForHash(128), 2); // 6-ring
        Assertions.assertEquals(cfp.getCountForHash(111), 2); // aromaticity

        Assertions.assertEquals(cfp.getCountForHash(7), 0); // 7-ring
        Assertions.assertEquals(cfp.getCountForHash(82), 0); // 5-ring

        // Non-aromatic 6-rings
        mol = parser.parseSmiles("C1CC(CCC1)CCCCC2CCCCC2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        cfp = printer.getCountFingerprint(mol);

        Assertions.assertEquals(cfp.getCountForHash(128), 2); // 6-ring

        Assertions.assertEquals(cfp.getCountForHash(111), 0); // aromaticity
        Assertions.assertEquals(cfp.getCountForHash(7), 0); // 7-ring
        Assertions.assertEquals(cfp.getCountForHash(82), 0); // 5-ring

        // Aromatic 6-ring, 3-ring and 4-ring
        mol = parser.parseSmiles("C1CC1C(CCC2CCC2)CC3=CC=CC=C3");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        cfp = printer.getCountFingerprint(mol);

        Assertions.assertEquals(cfp.getCountForHash(128), 1); // 6-ring
        Assertions.assertEquals(cfp.getCountForHash(111), 1); // aromaticity
        Assertions.assertEquals(cfp.getCountForHash(10), 1); // 3-ring
        Assertions.assertEquals(cfp.getCountForHash(1), 1); // 4-ring

        Assertions.assertEquals(cfp.getCountForHash(7), 0); // 7-ring
        Assertions.assertEquals(cfp.getCountForHash(82), 0); // 5-ring

        // Aromatic 6-ring, 3-ring and 4-ring
        mol = parser.parseSmiles("C1(CC1C(CCC2CCC2)CC3=CC=CC=C3)C(C(C(C4CC4)C5CC5)C6CC6)C7CC7");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        cfp = printer.getCountFingerprint(mol);

        Assertions.assertEquals(cfp.getCountForHash(128), 1); // 6-ring
        Assertions.assertEquals(cfp.getCountForHash(111), 1); // aromaticity
        Assertions.assertEquals(cfp.getCountForHash(10), 5); // 3-ring
        Assertions.assertEquals(cfp.getCountForHash(1), 1); // 4-ring

        Assertions.assertEquals(cfp.getCountForHash(7), 0); // 7-ring
        Assertions.assertEquals(cfp.getCountForHash(82), 0); // 5-ring
    }

    @Test
    void testCountableMACCSBinary_Rings() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new SubstructureFingerprinter(StandardSubstructureSets.getCountableMACCSSMARTS());
        IAtomContainer mol;
        BitSet bs;

        // Aromatic 6-rings
        mol = parser.parseSmiles("C1=CC=CC(=C1)CCCC2=CC=CC=C2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        bs = printer.getBitFingerprint(mol).asBitSet();

        Assertions.assertTrue(bs.get(128)); // 6-ring
        Assertions.assertTrue(bs.get(111)); // aromaticity

        Assertions.assertFalse(bs.get(7)); // 7-ring
        Assertions.assertFalse(bs.get(82)); // 5-ring

        // Non-aromatic 6-rings
        mol = parser.parseSmiles("C1CC(CCC1)CCCCC2CCCCC2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        bs = printer.getBitFingerprint(mol).asBitSet();

        Assertions.assertTrue(bs.get(128)); // 6-ring

        Assertions.assertFalse(bs.get(111)); // aromaticity
        Assertions.assertFalse(bs.get(7)); // 7-ring
        Assertions.assertFalse(bs.get(82)); // 5-ring

        // Aromatic 6-ring, 3-ring and 4-ring
        mol = parser.parseSmiles("C1CC1C(CCC2CCC2)CC3=CC=CC=C3");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        bs = printer.getBitFingerprint(mol).asBitSet();

        Assertions.assertTrue(bs.get(128)); // 6-ring
        Assertions.assertTrue(bs.get(111)); // aromaticity
        Assertions.assertTrue(bs.get(10)); // 3-ring
        Assertions.assertTrue(bs.get(1)); // 4-ring

        Assertions.assertFalse(bs.get(7)); // 7-ring
        Assertions.assertFalse(bs.get(82)); // 5-ring

        // Aromatic 6-ring, 3-ring and 4-ring
        mol = parser.parseSmiles("C1(CC1C(CCC2CCC2)CC3=CC=CC=C3)C(C(C(C4CC4)C5CC5)C6CC6)C7CC7");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        bs = printer.getBitFingerprint(mol).asBitSet();

        Assertions.assertTrue(bs.get(128)); // 6-ring
        Assertions.assertTrue(bs.get(111)); // aromaticity
        Assertions.assertTrue(bs.get(10)); // 3-ring
        Assertions.assertTrue(bs.get(1)); // 4-ring

        Assertions.assertFalse(bs.get(7)); // 7-ring
        Assertions.assertFalse(bs.get(82)); // 5-ring
    }

    /**
     * @cdk.bug 2871303
     *
     * While this test fails, Daylight says that the
     * SMARTS pattern used for vinylogous ester should
     * match benzaldehyde twice. So according to the
     * supplied definition this answer is actually correct.
     */
    @Disabled("the SMARTS pattern vinylogous ester is not strict enough - we can not fix this")
    void testVinylogousEster() throws Exception {
        String benzaldehyde = "c1ccccc1C=O";
        IFingerprinter fprinter = new SubstructureFingerprinter();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IBitFingerprint fp = fprinter.getBitFingerprint(sp.parseSmiles(benzaldehyde));
        Assertions.assertFalse(fp.get(136), "Bit 136 (vinylogous ester) is set to true");
    }

    @Test
    void testGetSubstructure() throws Exception {
        String[] smarts = {"c1ccccc1", "[CX4H3][#6]", "[CX2]#[CX2]"};
        SubstructureFingerprinter printer = new SubstructureFingerprinter(smarts);
        Assertions.assertEquals(printer.getSubstructure(1), smarts[1]);
    }
}
