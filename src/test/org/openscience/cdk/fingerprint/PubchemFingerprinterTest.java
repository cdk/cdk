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
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.BitSet;

/**
 * @cdk.module test-fingerprint
 */
public class PubchemFingerprinterTest extends CDKTestCase {

    SmilesParser parser;
    private static LoggingTool logger = new LoggingTool(PubchemFingerprinterTest.class);

    public PubchemFingerprinterTest() {
        super();
    }

    @Before
    public void setup() {
        parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
    }

    @Test
    public void getsize() throws Exception {
        IFingerprinter printer = new PubchemFingerprinter();
        Assert.assertEquals(881, printer.getSize());
    }

    @Test
    public void testFingerprint() throws Exception {
        IFingerprinter printer = new PubchemFingerprinter();
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());

        IMolecule mol1 = parser.parseSmiles("c1ccccc1CCc1ccccc1");
        IMolecule mol2 = parser.parseSmiles("c1ccccc1CC");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);

        adder.addImplicitHydrogens(mol1);
        adder.addImplicitHydrogens(mol2);

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol1);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol2);
        
        CDKHueckelAromaticityDetector.detectAromaticity(mol1);
        CDKHueckelAromaticityDetector.detectAromaticity(mol2);

        BitSet bs1 = printer.getFingerprint(mol1);
        BitSet bs2 = printer.getFingerprint(mol2);

        Assert.assertEquals(881, printer.getSize());

        Assert.assertFalse("c1ccccc1CC was detected as a subset of c1ccccc1CCc1ccccc1",
                FingerprinterTool.isSubset(bs1, bs2));
    }

    @Test
    public void testfp2() throws Exception {
        IFingerprinter printer = new PubchemFingerprinter();

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
    public void testBits() throws CDKException {
        IMolecule mol = parser.parseSmiles("COC1C(C(C(C(O1)CO)OC2C(C(C(C(O2)CO)S)O)O)O)O");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(mol.getBuilder());
        adder.addImplicitHydrogens(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);

        IFingerprinter printer = new PubchemFingerprinter();
        BitSet fp = printer.getFingerprint(mol);
        BitSet ref = PubchemFingerprinter.decode("AAADceBwPABAAAAAAAAAAAAAAAAAAAAAAAAkSAAAAAAAAAAAAAAAGgQACAAACBS0wAOCCAAABgQAAAAAAAAAAAAAAAAAAAAAAAAREAIAAAAiQAAFAAAHAAHAYAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==");

        Assert.assertEquals(ref, fp);
    }

    @Test
    public void testBenzene() throws CDKException {
        IMolecule mol = parser.parseSmiles("c1ccccc1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(mol.getBuilder());
        adder.addImplicitHydrogens(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        IFingerprinter printer = new PubchemFingerprinter();
        BitSet fp = printer.getFingerprint(mol);
        BitSet ref = PubchemFingerprinter.decode("AAADcYBgAAAAAAAAAAAAAAAAAAAAAAAAAAAwAAAAAAAAAAABAAAAGAAAAAAACACAEAAwAIAAAACAACBCAAACAAAgAAAIiAAAAIgIICKAERCAIAAggAAIiAcAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==");

        System.out.println("bits on in ref but missing from code");
        for (int i = 0; i < printer.getSize(); i++) {
            if (ref.get(i) && !fp.get(i)) System.out.print(i + " ");
        }
        System.out.println("\n--");

        System.out.println("bits on in code but not set in ref");
        for (int i = 0; i < printer.getSize(); i++) {
            if (!ref.get(i) && fp.get(i)) System.out.print(i + " ");
        }
        
        Assert.assertEquals(ref, fp);

    }

}