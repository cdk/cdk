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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.HINReader;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;
import java.util.List;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class CPSADescriptorTest extends MolecularDescriptorTest {

    public CPSADescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(CPSADescriptor.class);
    }

    @Test
    public void testCPSA() throws Exception {
        String filename = "data/hin/benzene.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(ac).getValue();
        /*
         * System.out.println("Num ret = "+retval.size()); for (int i = 0; i <
         * retval.size(); i++) { System.out.println( retval.get(i) ); }
         */

        Assert.assertEquals(0, retval.get(28), 0.0001); // RPSA
        Assert.assertEquals(1, retval.get(27), 0.0001); // RHSA
        Assert.assertEquals(0, retval.get(26), 0.0001); // TPSA
        Assert.assertEquals(231.66182, retval.get(25), 0.0001); // THSA

    }

    @Test
    public void testChargedMolecule() throws Exception {
        String filename = "data/mdl/cpsa-charged.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(ac).getValue();
        int ndesc = retval.length();
        for (int i = 0; i < ndesc; i++)
            Assert.assertTrue(retval.get(i) != Double.NaN);
    }

    @Test
    public void testUnChargedMolecule() throws Exception {
        String filename = "data/mdl/cpsa-uncharged.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(ac).getValue();
        int ndesc = retval.length();
        for (int i = 0; i < ndesc; i++)
            Assert.assertTrue(retval.get(i) != Double.NaN);
    }
}
