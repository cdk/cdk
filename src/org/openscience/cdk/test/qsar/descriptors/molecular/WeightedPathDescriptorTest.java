/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WeightedPathDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-extra
 */

public class WeightedPathDescriptorTest extends CDKTestCase {

    public WeightedPathDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(WeightedPathDescriptorTest.class);
    }

    public void testWeightedPathDescriptor() throws ClassNotFoundException, CDKException, Exception {
        IDescriptor descriptor = new WeightedPathDescriptor();

        SmilesParser sp = new SmilesParser();
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
        IChemObjectReader reader = new ReaderFactory().createReader(ins);
        IChemFile content = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        IAtomContainer[] c = ChemFileManipulator.getAllAtomContainers(content);
        mol = c[0];
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
        reader = new ReaderFactory().createReader(ins);
        content = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        c = ChemFileManipulator.getAllAtomContainers(content);
        mol = c[0];
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

