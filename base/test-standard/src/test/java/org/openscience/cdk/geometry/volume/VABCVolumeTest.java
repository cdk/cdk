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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Values in the paper are inaccurate. The spreadsheet from the SI is better.
 *
 * @cdk.module test-standard
 */
public class VABCVolumeTest {

    private static SmilesParser smilesParser;

    @BeforeClass
    public static void setup() {
        smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
    }

    @Test
    public void testMethane() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("C");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(25.8524433266667, volume, 0.01);
    }

    @Test(expected = CDKException.class)
    public void testIronChloride() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("Cl[Fe]Cl");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        VABCVolume.calculate(methane);
    }

    @Test
    public void testOmeprazol() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("COc2ccc1[nH]c(nc1c2)S(=O)Cc3ncc(C)c(OC)c3C");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(292.23, volume, 0.01);
    }

    @Test
    public void testSaccharin() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("O=C1NS(=O)(=O)c2ccccc12");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(139.35, volume, 0.01);
    }

    @Test
    public void testAdeforir() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("Nc1ncnc2n(CCOCP(=O)(O)O)cnc12");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(199.84, volume, 0.01);
    }

    @Test
    public void testMethaneWithExplicitHydrogens() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("[H]C([H])([H])[H]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(25.8524433266667, volume, 0.01);
    }

    @Test
    public void testEthane() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(43.1484279525333, volume, 0.01);
    }

    @Test
    public void testButane() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CCCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(77.7403972042667, volume, 0.01);
    }

    @Test
    public void testAcetonitrile() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CC#N");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(48.8722707591, volume, 0.01);
    }

    @Test
    public void testAceticAcid() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CC(=O)O");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(58.0924226528555, volume, 0.01);
    }

    @Test
    public void testChloroFluoro() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CC(F)(F)Cl");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(70.4946134235795, volume, 0.01);
    }

    @Test
    public void testCS2() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("S=C=S");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(57.5975740402667, volume, 0.01);
    }

    @Test
    public void testTriEthylPhosphite() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CCOP(=O)(OCC)OCC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(167.320526666244, volume, 0.01);
    }

    @Test
    public void testBenzene() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("c1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(81.1665316528, volume, 0.01);
    }

    @Test
    public void testPyrene() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("c1cc2ccc3cccc4ccc(c1)c2c34");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(171.174708305067, volume, 0.01);
    }

    @Test
    public void testNicotine() throws InvalidSmilesException, CDKException {
        IAtomContainer methane = smilesParser.parseSmiles("CN1CCCC1c2cccnc2");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(159.9875318718, volume, 0.01);
    }

}
