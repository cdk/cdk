/* 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.io.ChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.qsar.descriptors.molecular.EccentricConnectivityIndexDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test
 */

public class EccentricConnectivityIndexDescriptorTest extends CDKTestCase {

    public EccentricConnectivityIndexDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(EccentricConnectivityIndexDescriptorTest.class);
    }

    public void testEccentricConnectivityIndex() throws ClassNotFoundException, CDKException, java.lang.Exception {
        String filename = "data/hin/gravindex.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ChemObjectReader reader = new ReaderFactory().createReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        AtomContainer[] c = ChemFileManipulator.getAllAtomContainers(content);
        AtomContainer ac = c[0];

        IDescriptor descriptor = new EccentricConnectivityIndexDescriptor();
        IntegerResult retval = (IntegerResult) descriptor.calculate(ac).getValue();
        //System.out.println(retval.intValue());

        assertEquals(254, retval.intValue(), 0);
    }
}

