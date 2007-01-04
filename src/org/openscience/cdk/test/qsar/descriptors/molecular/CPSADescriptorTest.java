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
package org.openscience.cdk.test.qsar.descriptors.molecular;

import java.io.InputStream;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;


/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class CPSADescriptorTest extends CDKTestCase {

    public CPSADescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(CPSADescriptorTest.class);
    }

    public void testCPSA() throws ClassNotFoundException, CDKException, java.lang.Exception {
        String filename = "data/hin/benzene.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new ReaderFactory().createReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        IMolecularDescriptor descriptor = new CPSADescriptor();
        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(ac).getValue();
        /*
        System.out.println("Num ret = "+retval.size());
        for (int i = 0; i < retval.size(); i++) {
            System.out.println( retval.get(i) );
        }
        */

        assertEquals(0, retval.get(28), 0.0001);
        assertEquals(1, retval.get(27), 0.0001);
        assertEquals(0, retval.get(26), 0.0001);
        assertEquals(356.8849, retval.get(25), 0.0001);

    }
}

