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
package org.openscience.cdk.geometry.volume;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Values in the paper are inaccurate. The spreadsheet from the SI is better.
 *
 */
class VABCVolumeTest {

    private static SmilesParser smilesParser;

    @BeforeAll
    static void setup() {
        smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
    }

    @Test
    void testMethane() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("C");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(25.8524433266667, volume, 0.01);
    }

    @Test
    void testIronChloride() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("Cl[Fe]Cl");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        Assertions.assertThrows(CDKException.class, () -> {
            VABCVolume.calculate(methane);
        });
    }

    @Test
    void testOmeprazol() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("COc2ccc1[nH]c(nc1c2)S(=O)Cc3ncc(C)c(OC)c3C");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(292.23, volume, 0.01);
    }

    @Test
    void testSaccharin() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("O=C1NS(=O)(=O)c2ccccc12");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(139.35, volume, 0.01);
    }

    @Test
    void testAdeforir() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("Nc1ncnc2n(CCOCP(=O)(O)O)cnc12");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(199.84, volume, 0.01);
    }

    @Test
    void testMethaneWithExplicitHydrogens() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("[H]C([H])([H])[H]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(25.8524433266667, volume, 0.01);
    }

    @Test
    void testEthane() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(43.1484279525333, volume, 0.01);
    }

    @Test
    void testButane() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CCCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(77.7403972042667, volume, 0.01);
    }

    @Test
    void testAcetonitrile() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CC#N");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(48.8722707591, volume, 0.01);
    }

    @Test
    void testAceticAcid() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CC(=O)O");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(58.0924226528555, volume, 0.01);
    }

    @Test
    void testChloroFluoro() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CC(F)(F)Cl");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(70.4946134235795, volume, 0.01);
    }

    @Test
    void testCS2() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("S=C=S");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(57.5975740402667, volume, 0.01);
    }

    @Test
    void testTriEthylPhosphite() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CCOP(=O)(OCC)OCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(167.320526666244, volume, 0.01);
    }

    @Test
    void testBenzene() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("c1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(81.1665316528, volume, 0.01);
    }

    @Test
    void testPyrene() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("c1cc2ccc3cccc4ccc(c1)c2c34");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(171.174708305067, volume, 0.01);
    }

    @Test
    void testNicotine() throws CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CN1CCCC1c2cccnc2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assertions.assertEquals(159.9875318718, volume, 0.01);
    }

}
