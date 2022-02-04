/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class WeightedPathDescriptorTest extends MolecularDescriptorTest {

    public WeightedPathDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(WeightedPathDescriptor.class);
    }

    private void assertWeights(IAtomContainer mol, double ... expected) {
        DescriptorValue value = descriptor.calculate(mol);
        DoubleArrayResult actual = (DoubleArrayResult) value.getValue();
        Assert.assertEquals(expected[0], actual.get(0), 0.00001);
        Assert.assertEquals(expected[1], actual.get(1), 0.00001);
        Assert.assertEquals(expected[2], actual.get(2), 0.00001);
        Assert.assertEquals(expected[3], actual.get(3), 0.00001);
        Assert.assertEquals(expected[4], actual.get(4), 0.00001);
    }

    @Test
    public void testButane() throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCC");
        assertWeights(mol, 6.871320, 1.717830, 0.0, 0.0, 0.0);
    }

    @Test
    public void testWpo() throws ClassNotFoundException, CDKException, Exception {
        try (InputStream ins = this.getClass().getResourceAsStream("wpo.sdf");
             MDLV2000Reader reader = new MDLV2000Reader(ins)) {
            IChemFile content = reader.read(new org.openscience.cdk.ChemFile());
            List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
            IAtomContainer mol = cList.get(0);
            AtomContainerManipulator.suppressHydrogens(mol);
            assertWeights(mol, 18.42026, 1.842026, 13.45733, 13.45733, 0);
        }
    }

    @Test
    public void testWPN() throws ClassNotFoundException, CDKException, Exception {
        try (InputStream ins = this.getClass().getResourceAsStream("wpn.sdf");
             MDLV2000Reader reader = new MDLV2000Reader(ins)) {
            IChemFile content = reader.read(new org.openscience.cdk.ChemFile());
            List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
            IAtomContainer mol = cList.get(0);
            AtomContainerManipulator.suppressHydrogens(mol);
            assertWeights(mol, 26.14843, 1.867746, 19.02049, 0, 19.02049);
        }
    }
}
