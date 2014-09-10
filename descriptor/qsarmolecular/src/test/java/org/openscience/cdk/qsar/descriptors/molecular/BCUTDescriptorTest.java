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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

public class BCUTDescriptorTest extends MolecularDescriptorTest {

    public BCUTDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(BCUTDescriptor.class);
    }

    @Test
    public void testBCUT() throws Exception {
        String filename = "data/hin/gravindex.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
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
        Assert.assertNotNull(retval);
        /* System.out.println("Num ret = "+retval.size()); */
        for (int i = 0; i < retval.length(); i++) {
            Assert.assertTrue("The returned value must be non-zero", Math.abs(0.0 - retval.get(i)) > 0.0000001);
        }

        String[] names = descriptorValue.getNames();
        for (String name : names)
            Assert.assertNotNull(name);

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
    public void testExtraEigenvalues() throws Exception {
        String filename = "data/hin/gravindex.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
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

        Assert.assertEquals(75, retval.length());
        for (int i = 0; i < nheavy; i++)
            Assert.assertTrue(retval.get(i) != Double.NaN);
        for (int i = nheavy; i < nheavy + 5; i++) {
            Assert.assertTrue("Extra eigenvalue should have been NaN", Double.isNaN(retval.get(i)));
        }

    }

    @Test
    public void testAromaticity() throws Exception {
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

        Assert.assertEquals(result1.length(), result2.length());
        for (int i = 0; i < result1.length(); i++) {
            Assert.assertEquals("element " + i + " does not match", result1.get(i), result2.get(i), 0.01);
        }
    }

    @Test
    public void testHAddition() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=1C=CC(=CC1)CNC2=CC=C(C=C2N(=O)=O)S(=O)(=O)C(Cl)(Cl)Br");
        DoubleArrayResult result1 = (DoubleArrayResult) descriptor.calculate(mol).getValue();
        for (int i = 0; i < result1.length(); i++)
            Assert.assertTrue(result1.get(i) != Double.NaN);
    }

    /**
     * @cdk.bug 3489559
     */
    @Test
    public void testUndefinedValues() throws Exception {
        String filename = "data/mdl/burden_undefined.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        Assert.assertNotNull(ac);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
        addExplicitHydrogens(ac);
        Aromaticity.cdkLegacy().apply(ac);

        Exception e = descriptor.calculate(ac).getException();
        Assert.assertNotNull(e);
        // make sure exception was a NPE etc.
        Assert.assertEquals("Could not calculate partial charges: Partial charge not-supported for element: 'As'.",
                e.getMessage());
    }
}
