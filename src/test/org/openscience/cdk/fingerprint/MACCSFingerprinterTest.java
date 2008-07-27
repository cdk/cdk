/* $Revision: 10903 $ $Author: egonw $ $Date: 2008-05-07 09:48:07 -0400 (Wed, 07 May 2008) $    
 * 
 * Copyright (C) 2008 Rajarshi Guha
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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.BitSet;

/**
 * @cdk.module test-fingerprint
 */
public class MACCSFingerprinterTest extends NewCDKTestCase {

    private static LoggingTool logger = new LoggingTool(MACCSFingerprinterTest.class);

    public MACCSFingerprinterTest() {
        super();
    }

    @Test
    public void testFingerprint() throws Exception {
        SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
        IFingerprinter printer = new MACCSFingerprinter();

        IMolecule mol1 = parser.parseSmiles("c1ccccc1CCc1ccccc1");
        IMolecule mol2 = parser.parseSmiles("c1ccccc1CC");
        IMolecule mol3 = parser.parseSmiles("CCC.CCC");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol3);
        CDKHueckelAromaticityDetector.detectAromaticity(mol1);
        CDKHueckelAromaticityDetector.detectAromaticity(mol2);
        CDKHueckelAromaticityDetector.detectAromaticity(mol3);

        BitSet bs1 = printer.getFingerprint(mol1);
        BitSet bs2 = printer.getFingerprint(mol2);
        BitSet bs3 = printer.getFingerprint(mol3);

        Assert.assertEquals(166, printer.getSize());


        Assert.assertFalse(bs1.get(165));
        Assert.assertTrue(bs1.get(124));

        Assert.assertFalse(bs2.get(124));

        Assert.assertTrue(bs3.get(165));

        Assert.assertFalse(FingerprinterTool.isSubset(bs1, bs2));
    }

    @Test
    public void testfp2() throws Exception {
        SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
        IFingerprinter printer = new MACCSFingerprinter();

        IMolecule mol1 = parser.parseSmiles("CC(N)CCCN");
        IMolecule mol2 = parser.parseSmiles("CC(N)CCC");
        IMolecule mol3 = parser.parseSmiles("CCCC");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol3);

        CDKHueckelAromaticityDetector.detectAromaticity(mol1);
        CDKHueckelAromaticityDetector.detectAromaticity(mol2);
        CDKHueckelAromaticityDetector.detectAromaticity(mol3);

        BitSet bs1 = printer.getFingerprint(mol1);
        BitSet bs2 = printer.getFingerprint(mol2);
        BitSet bs3 = printer.getFingerprint(mol3);

        Assert.assertFalse(bs1.get(124));
        Assert.assertFalse(bs2.get(124));
        Assert.assertFalse(bs3.get(124));

        Assert.assertFalse(FingerprinterTool.isSubset(bs1, bs2));
        Assert.assertTrue(FingerprinterTool.isSubset(bs2, bs3));
    }

}