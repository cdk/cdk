/*
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.HINReader;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;
import java.util.List;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

class BCUTDescriptorTest extends MolecularDescriptorTest {

    BCUTDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(BCUTDescriptor.class);
    }

    @Test
    void testBCUT() throws Exception {
        String filename = "gravindex.hin";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        Object[] params = new Object[3];
        params[0] = 2;
        params[1] = 2;
        params[2] = true;
        descriptor.setParameters(params);
        DescriptorValue descriptorValue = descriptor.calculate(ac);

        DoubleArrayResult retval = (DoubleArrayResult) descriptorValue.getValue();
        Assertions.assertNotNull(retval);
        /* System.out.println("Num ret = "+retval.size()); */
        for (int i = 0; i < retval.length(); i++) {
            Assertions.assertTrue(Math.abs(0.0 - retval.get(i)) > 0.0000001, "The returned value must be non-zero");
        }

        String[] names = descriptorValue.getNames();
        for (String name : names)
            Assertions.assertNotNull(name);

        /*
         * Assert.assertEquals(1756.5060703860984,
         * ((Double)retval.get(0)).doubleValue(), 0.00000001);
         * Assert.assertEquals(41.91069159994975,
         * ((Double)retval.get(1)).doubleValue(), 0.00000001);
         * Assert.assertEquals(12.06562671430088,
         * ((Double)retval.get(2)).doubleValue(), 0.00000001);
         * Assert.assertEquals(1976.6432599699767,
         * ((Double)retval.get(3)).doubleValue(), 0.00000001);
         * Assert.assertEquals(44.45945636161082,
         * ((Double)retval.get(4)).doubleValue(), 0.00000001);
         * Assert.assertEquals(12.549972243701887,
         * ((Double)retval.get(5)).doubleValue(), 0.00000001);
         * Assert.assertEquals(4333.097373073368,
         * ((Double)retval.get(6)).doubleValue(), 0.00000001);
         * Assert.assertEquals(65.82626658920714,
         * ((Double)retval.get(7)).doubleValue(), 0.00000001);
         * Assert.assertEquals(16.302948232909483,
         * ((Double)retval.get(8)).doubleValue(), 0.00000001);
         */
    }

    @Test
    void testExtraEigenvalues() throws Exception {
        String filename = "gravindex.hin";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        Object[] params = new Object[3];
        params[0] = 0;
        params[1] = 25;
        params[2] = true;
        descriptor.setParameters(params);
        DescriptorValue descriptorValue = descriptor.calculate(ac);

        DoubleArrayResult retval = (DoubleArrayResult) descriptorValue.getValue();
        int nheavy = 20;

        Assertions.assertEquals(75, retval.length());
        for (int i = 0; i < nheavy; i++)
            Assertions.assertTrue(retval.get(i) != Double.NaN);
        for (int i = nheavy; i < nheavy + 5; i++) {
            Assertions.assertTrue(Double.isNaN(retval.get(i)), "Extra eigenvalue should have been NaN");
        }

    }

    @Test
    void testAromaticity() throws Exception {
        setDescriptor(BCUTDescriptor.class);

        String smiles1 = "c1ccccc1";
        String smiles2 = "C1=CC=CC=C1";

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = sp.parseSmiles(smiles1);
        IAtomContainer mol2 = sp.parseSmiles(smiles2);

        addExplicitHydrogens(mol1);
        addExplicitHydrogens(mol2);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        Aromaticity.cdkLegacy().apply(mol1);
        Aromaticity.cdkLegacy().apply(mol2);

        DoubleArrayResult result1 = (DoubleArrayResult) descriptor.calculate(mol1).getValue();
        DoubleArrayResult result2 = (DoubleArrayResult) descriptor.calculate(mol2).getValue();

        Assertions.assertEquals(result1.length(), result2.length());
        for (int i = 0; i < result1.length(); i++) {
            Assertions.assertEquals(result1.get(i), result2.get(i), 0.01, "element " + i + " does not match");
        }
    }

    @Test
    void testHAddition() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=1C=CC(=CC1)CNC2=CC=C(C=C2N(=O)=O)S(=O)(=O)C(Cl)(Cl)Br");
        DoubleArrayResult result1 = (DoubleArrayResult) descriptor.calculate(mol).getValue();
        for (int i = 0; i < result1.length(); i++)
            Assertions.assertTrue(result1.get(i) != Double.NaN);
    }

    /**
     * @cdk.bug 3489559
     */
    @Test
    void testUndefinedValues() throws Exception {
        String filename = "burden_undefined.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        Assertions.assertNotNull(ac);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
        addExplicitHydrogens(ac);
        Aromaticity.cdkLegacy().apply(ac);

        Exception e = descriptor.calculate(ac).getException();
        Assertions.assertNotNull(e);
        // make sure exception was a NPE etc.
        Assertions.assertEquals("Could not calculate partial charges: Partial charge not-supported for element: 'As'.", e.getMessage());
    }

    @Test
    void testBug735_ordering1() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C(C)(N)=O");
        DescriptorValue val = descriptor.calculate(mol);
        DoubleArrayResult res = (DoubleArrayResult) val.getValue();
        Assertions.assertEquals(11.8815865, res.get(0), 0.00001);
        Assertions.assertEquals(16.0059576, res.get(1), 0.00001);
        Assertions.assertEquals(-0.381844, res.get(2), 0.00001);
        Assertions.assertEquals(0.325509, res.get(3), 0.00001);
        Assertions.assertEquals(3.374638, res.get(4), 0.00001);
        Assertions.assertEquals(5.033583, res.get(5), 0.00001);
    }

    @Test
    void testBug735_ordering2() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC(=O)N");
        DescriptorValue val = descriptor.calculate(mol);
        DoubleArrayResult res = (DoubleArrayResult) val.getValue();
        Assertions.assertEquals(11.8815865, res.get(0), 0.00001);
        Assertions.assertEquals(16.0059576, res.get(1), 0.00001);
        Assertions.assertEquals(-0.381844, res.get(2), 0.00001);
        Assertions.assertEquals(0.325509, res.get(3), 0.00001);
        Assertions.assertEquals(3.374638, res.get(4), 0.00001);
        Assertions.assertEquals(5.033583, res.get(5), 0.00001);
    }
}
