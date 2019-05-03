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
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
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

    @Test
    public void testWeightedPathDescriptor() throws ClassNotFoundException, CDKException, Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = null;
        DescriptorValue value = null;
        DoubleArrayResult result = null;

        mol = sp.parseSmiles("CCCC");
        value = descriptor.calculate(mol);
        result = (DoubleArrayResult) value.getValue();
        Assert.assertEquals(6.871320, result.get(0), 0.000001);
        Assert.assertEquals(1.717830, result.get(1), 0.000001);
        Assert.assertEquals(0.0, result.get(2), 0.000001);
        Assert.assertEquals(0.0, result.get(3), 0.000001);
        Assert.assertEquals(0.0, result.get(4), 0.000001);

        String filename = "data/mdl/wpo.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        IChemFile content = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        mol = (IAtomContainer) cList.get(0);
        mol = AtomContainerManipulator.removeHydrogens(mol);

        value = descriptor.calculate(mol);
        result = (DoubleArrayResult) value.getValue();
        Assert.assertEquals(18.42026, result.get(0), 0.00001);
        Assert.assertEquals(1.842026, result.get(1), 0.00001);
        Assert.assertEquals(13.45733, result.get(2), 0.00001);
        Assert.assertEquals(13.45733, result.get(3), 0.00001);
        Assert.assertEquals(0, result.get(4), 0.00001);

        filename = "data/mdl/wpn.sdf";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins);
        content = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        cList = ChemFileManipulator.getAllAtomContainers(content);
        mol = (IAtomContainer) cList.get(0);
        mol = AtomContainerManipulator.removeHydrogens(mol);
        value = descriptor.calculate(mol);
        result = (DoubleArrayResult) value.getValue();
        Assert.assertEquals(26.14844, result.get(0), 0.00001);
        Assert.assertEquals(1.867746, result.get(1), 0.00001);
        Assert.assertEquals(19.02049, result.get(2), 0.00001);
        Assert.assertEquals(0, result.get(3), 0.000001);
        Assert.assertEquals(19.02049, result.get(4), 0.00001);

    }
}
