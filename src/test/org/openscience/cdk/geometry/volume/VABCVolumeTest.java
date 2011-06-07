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

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * Values in the paper are inaccurate. The spreadsheet from the SI is better.
 */
public class VABCVolumeTest {

    private static SmilesParser smilesParser;

    @BeforeClass
    public static void setup() {
        smilesParser = new SmilesParser(
            NoNotificationChemObjectBuilder.getInstance()
        );
    }

    @Test
    public void testMethane() throws InvalidSmilesException, CDKException {
        IMolecule methane = smilesParser.parseSmiles("C");
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(25.8524433266667, volume, 0.01);
    }

    @Test
    public void testMethaneWithExplicitHydrogens() throws InvalidSmilesException, CDKException {
        IMolecule methane = smilesParser.parseSmiles("[H]C([H])([H])[H]");
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(25.8524433266667, volume, 0.01);
    }

    @Test
    public void testEthane() throws InvalidSmilesException, CDKException {
        IMolecule methane = smilesParser.parseSmiles("CC");
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(43.1484279525333, volume, 0.01);
    }

    @Test
    public void testButane() throws InvalidSmilesException, CDKException {
        IMolecule methane = smilesParser.parseSmiles("CCCC");
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(77.7403972042667, volume, 0.01);
    }

    @Test
    public void testAcetonitrile() throws InvalidSmilesException, CDKException {
        IMolecule methane = smilesParser.parseSmiles("CC#N");
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(48.8722707591, volume, 0.01);
    }

    @Test
    public void testAceticAcid() throws InvalidSmilesException, CDKException {
        IMolecule methane = smilesParser.parseSmiles("CC(=O)O");
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(58.0924226528555, volume, 0.01);
    }

    @Test
    public void testChloroFluoro() throws InvalidSmilesException, CDKException {
        IMolecule methane = smilesParser.parseSmiles("CC(F)(F)Cl");
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(70.4946134235795, volume, 0.01);
    }

    @Test
    public void testCS2() throws InvalidSmilesException, CDKException {
        IMolecule methane = smilesParser.parseSmiles("S=C=S");
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(57.5975740402667, volume, 0.01);
    }

    @Test
    public void testTriEthylPhosphite() throws InvalidSmilesException, CDKException {
        IMolecule methane = smilesParser.parseSmiles("CCOP(=O)(OCC)OCC");
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(167.320526666244, volume, 0.01);
    }

}
