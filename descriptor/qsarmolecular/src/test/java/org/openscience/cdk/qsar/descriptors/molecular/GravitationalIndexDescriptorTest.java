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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.HINReader;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;
import java.util.List;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class GravitationalIndexDescriptorTest extends MolecularDescriptorTest {

    public GravitationalIndexDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(GravitationalIndexDescriptor.class);
    }

    @Test
    public void testGravitationalIndex() throws ClassNotFoundException, CDKException, java.lang.Exception {
        String filename = "data/hin/gravindex.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        Assert.assertEquals(1756.5060703860984, retval.get(0), 0.00000001);
        Assert.assertEquals(41.91069159994975, retval.get(1), 0.00000001);
        Assert.assertEquals(12.06562671430088, retval.get(2), 0.00000001);
        Assert.assertEquals(1976.6432599699767, retval.get(3), 0.00000001);
        Assert.assertEquals(44.45945636161082, retval.get(4), 0.00000001);
        Assert.assertEquals(12.549972243701887, retval.get(5), 0.00000001);
        Assert.assertEquals(4333.097373073368, retval.get(6), 0.00000001);
        Assert.assertEquals(65.82626658920714, retval.get(7), 0.00000001);
        Assert.assertEquals(16.302948232909483, retval.get(8), 0.00000001);
    }
}
