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
    public void testFingerprint() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = new MACCSFingerprinter();

        IAtomContainer mol1 = parser.parseSmiles("c1ccccc1CCc1ccccc1");
        IAtomContainer mol2 = parser.parseSmiles("c1ccccc1CC");
        IAtomContainer mol3 = parser.parseSmiles("CCC.CCC");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol3);
        Aromaticity.cdkLegacy().apply(mol1);
        Aromaticity.cdkLegacy().apply(mol2);
        Aromaticity.cdkLegacy().apply(mol3);

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
                is(asBitSet(53, 56, 65, 71, 73, 88, 97, 100, 104, 111, 112, 126, 130, 136, 138, 139, 140, 142, 143,
                        144, 145, 148, 149, 151, 153, 156, 158, 159, 162, 163, 164)));
        assertThat(
                subBits.asBitSet(),
                is(asBitSet(56, 97, 100, 104, 108, 112, 117, 127, 131, 136, 143, 144, 146, 151, 152, 156, 162, 163, 164)));
    }

}
