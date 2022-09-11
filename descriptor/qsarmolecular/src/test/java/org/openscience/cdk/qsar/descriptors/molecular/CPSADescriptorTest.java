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

class CPSADescriptorTest extends MolecularDescriptorTest {

    CPSADescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(CPSADescriptor.class);
    }

    @Test
    void testCPSA() throws Exception {
        String filename = "benzene.hin";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(ac).getValue();
        /*
         * System.out.println("Num ret = "+retval.size()); for (int i = 0; i <
         * retval.size(); i++) { System.out.println( retval.get(i) ); }
         */

        Assertions.assertEquals(0, retval.get(28), 0.0001); // RPSA
        Assertions.assertEquals(1, retval.get(27), 0.0001); // RHSA
        Assertions.assertEquals(0, retval.get(26), 0.0001); // TPSA
        Assertions.assertEquals(231.66182, retval.get(25), 0.0001); // THSA

    }

    @Test
    void testChargedMolecule() throws Exception {
        String filename = "cpsa-charged.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(ac).getValue();
        int ndesc = retval.length();
        for (int i = 0; i < ndesc; i++)
            Assertions.assertTrue(retval.get(i) != Double.NaN);
    }

    @Test
    void testUnChargedMolecule() throws Exception {
        String filename = "cpsa-uncharged.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(ac).getValue();
        int ndesc = retval.length();
        for (int i = 0; i < ndesc; i++)
            Assertions.assertTrue(retval.get(i) != Double.NaN);
    }
}
