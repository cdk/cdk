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

import java.util.BitSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @cdk.module test-fingerprint
 */
public class PubchemFingerprinterTest extends AbstractFixedLengthFingerprinterTest {

    SmilesParser parser;

    public IFingerprinter getFingerprinter() {
        return new PubchemFingerprinter();
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
    public void testCID2518130() throws CDKException {
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

    /**
     * Test case for Pubchem CID 5934166.
     *
     * @throws InvalidSmilesException
     * @cdk.inchi InChI=1S/C32H26N/c1-5-13-26(14-6-1)21-22-31-23-30(28-17-9-3-10-18-28)24-32(29-19-11-4-12-20-29)33(31)25-27-15-7-2-8-16-27/h1-24H,25H2/q+1/b22-21+
     */
    @Test
    public void testCID5934166() throws CDKException {
        IMolecule mol = parser.parseSmiles("C1=CC=C(C=C1)C[N+]2=C(C=C(C=C2C=CC3=CC=CC=C3)C4=CC=CC=C4)C5=CC=CC=C5");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(mol.getBuilder());
        adder.addImplicitHydrogens(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);

        IFingerprinter printer = new PubchemFingerprinter();
        BitSet fp = printer.getFingerprint(mol);
        BitSet ref = PubchemFingerprinter.decode("AAADceB+AAAAAAAAAAAAAAAAAAAAAAAAAAA8YMGCAAAAAAAB1AAAHAAAAAAADAjBHgQwgJMMEACgAyRiRACCgCAhAiAI2CA4ZJgIIOLAkZGEIAhggADIyAcQgMAOgAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==");

        Assert.assertEquals(ref, fp);
    }

    /**
       * Test case for Pubchem CID 25181289.
       *
       * @throws InvalidSmilesException
       * @cdk.inchi  InChI=1S/C14H10Cl3N3O3/c1-6(7-2-4-8(21)5-3-7)19-20-11-9(15)12(14(22)23)18-13(17)10(11)16/h2-5,19,21H,1H2,(H,18,20)(H,22,23)
       */
      @Test
      public void testCID25181289() throws CDKException {
          IMolecule mol = parser.parseSmiles("C=C(C1=CC=C(C=C1)O)NNC2=C(C(=NC(=C2Cl)Cl)C(=O)O)Cl");
          AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
          CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(mol.getBuilder());
          adder.addImplicitHydrogens(mol);
          AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
          CDKHueckelAromaticityDetector.detectAromaticity(mol);

          IFingerprinter printer = new PubchemFingerprinter();
          BitSet fp = printer.getFingerprint(mol);
          BitSet ref = PubchemFingerprinter.decode("AAADccBzMAAGAAAAAAAAAAAAAAAAAAAAAAA8QAAAAAAAAAABwAAAHgIYCAAADA6BniAwzpJqEgCoAyTyTASChCAnJiIYumGmTtgKJnLD1/PEdQhkwBHY3Qe82AAOIAAAAAAAAABAAAAAAAAAAAAAAAAAAA==");

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

        Assert.assertEquals(ref, fp);

    }

}