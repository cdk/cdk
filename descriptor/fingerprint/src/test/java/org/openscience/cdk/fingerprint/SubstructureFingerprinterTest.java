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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
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
    public void testSize() throws Exception {
        SubstructureFingerprinter fp = new SubstructureFingerprinter();
        Assert.assertEquals(307, fp.getSize());

        fp = new SubstructureFingerprinter(StandardSubstructureSets.getFunctionalGroupSMARTS());
        Assert.assertEquals(307, fp.getSize());

        fp = new SubstructureFingerprinter(StandardSubstructureSets.getCountableMACCSSMARTS());
        Assert.assertEquals(142, fp.getSize());
    }

    @Test
    @Override
    public void testBug706786() throws Exception {

        IAtomContainer superStructure = bug706786_1();
        IAtomContainer subStructure = bug706786_2();

        addImplicitHydrogens(superStructure);
        addImplicitHydrogens(subStructure);

        IFingerprinter fpr = getBitFingerprinter();
        IBitFingerprint superBits = fpr.getBitFingerprint(superStructure);
        IBitFingerprint subBits = fpr.getBitFingerprint(subStructure);

        assertThat(superBits.asBitSet(),
                is(asBitSet(0, 11, 13, 17, 40, 48, 136, 273, 274, 278, 286, 294, 299, 301, 304, 306)));
        assertThat(subBits.asBitSet(), is(asBitSet(1, 17, 273, 274, 278, 294, 306)));
    }

    @Test
    public void testUserFunctionalGroups() throws Exception {
        String[] smarts = {"c1ccccc1", "[CX4H3][#6]", "[CX2]#[CX2]"};
        IFingerprinter printer = new SubstructureFingerprinter(smarts);
        Assert.assertEquals(3, printer.getSize());

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = sp.parseSmiles("c1ccccc1CCC");
        IBitFingerprint fp = printer.getBitFingerprint(mol1);
        Assert.assertNotNull(fp);

        Assert.assertTrue(fp.get(0));
        Assert.assertTrue(fp.get(1));
        Assert.assertFalse(fp.get(2));

        mol1 = sp.parseSmiles("C=C=C");
        fp = printer.getBitFingerprint(mol1);
        Assert.assertNotNull(fp);
        Assert.assertFalse(fp.get(0));
        Assert.assertFalse(fp.get(1));
        Assert.assertFalse(fp.get(2));
    }

    @Test
    public void testFunctionalGroupsBinary() throws Exception {
        IFingerprinter printer = new SubstructureFingerprinter();
        Assert.assertEquals(307, printer.getSize());

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = sp.parseSmiles("c1ccccc1CCC");
        IBitFingerprint fp = printer.getBitFingerprint(mol1);
        Assert.assertNotNull(fp);
        Assert.assertTrue(fp.get(273));
        Assert.assertTrue(fp.get(0));
        Assert.assertTrue(fp.get(1));
        Assert.assertFalse(fp.get(100));
    }

    @Test
    public void testFunctionalGroupsCount() throws Exception {
        // TODO: Implement tests
    }

    @Test
    public void testCountableMACCSBinary() throws Exception {
        // Tests are modified copy of the test included in the MACCS-FPs class

        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new SubstructureFingerprinter(StandardSubstructureSets.getCountableMACCSSMARTS());
        Assert.assertEquals(142, printer.getSize());

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
        Assert.assertFalse(bs0.get(111));
        Assert.assertTrue(bs1.get(111));
        Assert.assertTrue(bs2.get(111));
        Assert.assertFalse(bs3.get(111));
        Assert.assertFalse(bs4.get(111));

        // Check for the fingerprints being subsets
        Assert.assertFalse(FingerprinterTool.isSubset(bs1, bs2));
        Assert.assertFalse(FingerprinterTool.isSubset(bs0, bs3));
        Assert.assertTrue(FingerprinterTool.isSubset(bs3, bs4));
    }

    @Test
    public void testCountableMACCSBinary2() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new SubstructureFingerprinter(StandardSubstructureSets.getCountableMACCSSMARTS());
        IAtomContainer mol;
        BitSet bs;

        // Test molecule 1
        mol = parser.parseSmiles("C([S](O)(=O)=O)C1=C(C=CC=C1)CCCC[N+](=O)[O-]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        bs = printer.getBitFingerprint(mol).asBitSet();

        Assert.assertTrue(bs.get(46));
        Assert.assertTrue(bs.get(27));
        Assert.assertTrue(bs.get(59));
        Assert.assertTrue(bs.get(49));
        Assert.assertTrue(bs.get(111));
        Assert.assertTrue(bs.get(129));
        Assert.assertTrue(bs.get(115));
        Assert.assertTrue(bs.get(120));
        Assert.assertTrue(bs.get(41));

        Assert.assertFalse(bs.get(93));
        Assert.assertFalse(bs.get(91));
        Assert.assertFalse(bs.get(24));

        // Test molecule 2: Diatrizoic acid
        mol = parser.parseSmiles("CC(=O)NC1=C(C(=C(C(=C1I)C(=O)O)I)NC(=O)C)I");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        bs = printer.getBitFingerprint(mol).asBitSet();

        Assert.assertTrue(bs.get(15));
        Assert.assertTrue(bs.get(135));
        Assert.assertTrue(bs.get(139));
        Assert.assertTrue(bs.get(93));
        Assert.assertTrue(bs.get(73));

        Assert.assertFalse(bs.get(91));
    }

    @Test
    public void testGetCountFingerprint() throws Exception {
        // See other function for specific test cases
    }

    @Test
    public void testCountableMACCSCount2() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new SubstructureFingerprinter(StandardSubstructureSets.getCountableMACCSSMARTS());
        IAtomContainer mol;
        ICountFingerprint cfp;

        // Test molecule 1
        mol = parser.parseSmiles("C([S](O)(=O)=O)C1=C(C=CC=C1)CCCC[N+](=O)[O-]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        cfp = printer.getCountFingerprint(mol);

        Assert.assertEquals(cfp.getCountForHash(46), 2);
        Assert.assertEquals(cfp.getCountForHash(27), 1);
        Assert.assertEquals(cfp.getCountForHash(59), 2);
        Assert.assertEquals(cfp.getCountForHash(49), 1);
        Assert.assertEquals(cfp.getCountForHash(111), 1);
        Assert.assertEquals(cfp.getCountForHash(129), 3);
        Assert.assertEquals(cfp.getCountForHash(115), 2);
        Assert.assertEquals(cfp.getCountForHash(120), 3);
        Assert.assertEquals(cfp.getCountForHash(41), 3);

        Assert.assertEquals(cfp.getCountForHash(93), 0);
        Assert.assertEquals(cfp.getCountForHash(91), 0);
        Assert.assertEquals(cfp.getCountForHash(24), 0);

        // Test molecule 2: Diatrizoic acid
        mol = parser.parseSmiles("CC(=O)NC1=C(C(=C(C(=C1I)C(=O)O)I)NC(=O)C)I");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        cfp = printer.getCountFingerprint(mol);

        Assert.assertEquals(cfp.getCountForHash(15), 3);
        Assert.assertEquals(cfp.getCountForHash(135), 3);
        Assert.assertEquals(cfp.getCountForHash(139), 4);
        Assert.assertEquals(cfp.getCountForHash(93), 3);
        Assert.assertEquals(cfp.getCountForHash(73), 6);

        Assert.assertEquals(cfp.getCountForHash(91), 0);
    }

    @Test
    public void testCountableMACCSCount_Rings() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new SubstructureFingerprinter(StandardSubstructureSets.getCountableMACCSSMARTS());
        IAtomContainer mol;
        ICountFingerprint cfp;

        // Aromatic 6-rings
        mol = parser.parseSmiles("C1=CC=CC(=C1)CCCC2=CC=CC=C2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        cfp = printer.getCountFingerprint(mol);

        Assert.assertEquals(cfp.getCountForHash(128), 2); // 6-ring
        Assert.assertEquals(cfp.getCountForHash(111), 2); // aromaticity

        Assert.assertEquals(cfp.getCountForHash(7), 0); // 7-ring
        Assert.assertEquals(cfp.getCountForHash(82), 0); // 5-ring

        // Non-aromatic 6-rings
        mol = parser.parseSmiles("C1CC(CCC1)CCCCC2CCCCC2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        cfp = printer.getCountFingerprint(mol);

        Assert.assertEquals(cfp.getCountForHash(128), 2); // 6-ring

        Assert.assertEquals(cfp.getCountForHash(111), 0); // aromaticity
        Assert.assertEquals(cfp.getCountForHash(7), 0); // 7-ring
        Assert.assertEquals(cfp.getCountForHash(82), 0); // 5-ring

        // Aromatic 6-ring, 3-ring and 4-ring
        mol = parser.parseSmiles("C1CC1C(CCC2CCC2)CC3=CC=CC=C3");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        cfp = printer.getCountFingerprint(mol);

        Assert.assertEquals(cfp.getCountForHash(128), 1); // 6-ring
        Assert.assertEquals(cfp.getCountForHash(111), 1); // aromaticity
        Assert.assertEquals(cfp.getCountForHash(10), 1); // 3-ring
        Assert.assertEquals(cfp.getCountForHash(1), 1); // 4-ring

        Assert.assertEquals(cfp.getCountForHash(7), 0); // 7-ring
        Assert.assertEquals(cfp.getCountForHash(82), 0); // 5-ring

        // Aromatic 6-ring, 3-ring and 4-ring
        mol = parser.parseSmiles("C1(CC1C(CCC2CCC2)CC3=CC=CC=C3)C(C(C(C4CC4)C5CC5)C6CC6)C7CC7");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        cfp = printer.getCountFingerprint(mol);

        Assert.assertEquals(cfp.getCountForHash(128), 1); // 6-ring
        Assert.assertEquals(cfp.getCountForHash(111), 1); // aromaticity
        Assert.assertEquals(cfp.getCountForHash(10), 5); // 3-ring
        Assert.assertEquals(cfp.getCountForHash(1), 1); // 4-ring

        Assert.assertEquals(cfp.getCountForHash(7), 0); // 7-ring
        Assert.assertEquals(cfp.getCountForHash(82), 0); // 5-ring
    }

    @Test
    public void testCountableMACCSBinary_Rings() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new SubstructureFingerprinter(StandardSubstructureSets.getCountableMACCSSMARTS());
        IAtomContainer mol;
        BitSet bs;

        // Aromatic 6-rings
        mol = parser.parseSmiles("C1=CC=CC(=C1)CCCC2=CC=CC=C2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        bs = printer.getBitFingerprint(mol).asBitSet();

        Assert.assertTrue(bs.get(128)); // 6-ring
        Assert.assertTrue(bs.get(111)); // aromaticity

        Assert.assertFalse(bs.get(7)); // 7-ring
        Assert.assertFalse(bs.get(82)); // 5-ring

        // Non-aromatic 6-rings
        mol = parser.parseSmiles("C1CC(CCC1)CCCCC2CCCCC2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        bs = printer.getBitFingerprint(mol).asBitSet();

        Assert.assertTrue(bs.get(128)); // 6-ring

        Assert.assertFalse(bs.get(111)); // aromaticity
        Assert.assertFalse(bs.get(7)); // 7-ring
        Assert.assertFalse(bs.get(82)); // 5-ring

        // Aromatic 6-ring, 3-ring and 4-ring
        mol = parser.parseSmiles("C1CC1C(CCC2CCC2)CC3=CC=CC=C3");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        bs = printer.getBitFingerprint(mol).asBitSet();

        Assert.assertTrue(bs.get(128)); // 6-ring
        Assert.assertTrue(bs.get(111)); // aromaticity
        Assert.assertTrue(bs.get(10)); // 3-ring
        Assert.assertTrue(bs.get(1)); // 4-ring

        Assert.assertFalse(bs.get(7)); // 7-ring
        Assert.assertFalse(bs.get(82)); // 5-ring

        // Aromatic 6-ring, 3-ring and 4-ring
        mol = parser.parseSmiles("C1(CC1C(CCC2CCC2)CC3=CC=CC=C3)C(C(C(C4CC4)C5CC5)C6CC6)C7CC7");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        bs = printer.getBitFingerprint(mol).asBitSet();

        Assert.assertTrue(bs.get(128)); // 6-ring
        Assert.assertTrue(bs.get(111)); // aromaticity
        Assert.assertTrue(bs.get(10)); // 3-ring
        Assert.assertTrue(bs.get(1)); // 4-ring

        Assert.assertFalse(bs.get(7)); // 7-ring
        Assert.assertFalse(bs.get(82)); // 5-ring
    }

    /**
     * @cdk.bug 2871303
     *
     * While this test fails, Daylight says that the
     * SMARTS pattern used for vinylogous ester should
     * match benzaldehyde twice. So according to the
     * supplied definition this answer is actually correct.
     */
    @Ignore("the SMARTS pattern vinylogous ester is not strict enough - we can not fix this")
    public void testVinylogousEster() throws Exception {
        String benzaldehyde = "c1ccccc1C=O";
        IFingerprinter fprinter = new SubstructureFingerprinter();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IBitFingerprint fp = fprinter.getBitFingerprint(sp.parseSmiles(benzaldehyde));
        Assert.assertFalse("Bit 136 (vinylogous ester) is set to true", fp.get(136));
    }

    @Test
    public void testGetSubstructure() throws Exception {
        String[] smarts = {"c1ccccc1", "[CX4H3][#6]", "[CX2]#[CX2]"};
        SubstructureFingerprinter printer = new SubstructureFingerprinter(smarts);
        Assert.assertEquals(printer.getSubstructure(1), smarts[1]);
    }
}
