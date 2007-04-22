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
import org.openscience.cdk.io.HINReader;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.EccentricConnectivityIndexDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
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
        IChemObjectReader reader = new HINReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        IMolecularDescriptor descriptor = new EccentricConnectivityIndexDescriptor();
        IntegerResult retval = (IntegerResult) descriptor.calculate(ac).getValue();
        //logger.debug(retval.intValue());

        assertEquals(254, retval.intValue(), 0);
    }
}

