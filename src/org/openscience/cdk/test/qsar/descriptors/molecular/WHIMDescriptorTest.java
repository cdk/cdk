/*
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
import org.openscience.cdk.qsar.descriptors.molecular.WHIMDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;


/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class WHIMDescriptorTest extends CDKTestCase {

    public WHIMDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(WHIMDescriptorTest.class);
    }

    public void testWHIM() throws ClassNotFoundException, CDKException, java.lang.Exception {
        String filename = "data/hin/gravindex.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new ReaderFactory().createReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        IMolecularDescriptor descriptor = new WHIMDescriptor();
        Object[] params = new Object[1];
        params[0] = new String("unity");
        descriptor.setParameters(params);
        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(ac).getValue();
        //logger.debug("Num ret = "+retval.size());
        for (int i = 0; i < retval.size(); i++) {
            //logger.debug( retval.get(i) );
        }

        /*
        assertEquals(1756.5060703860984, ((Double)retval.get(0)).doubleValue(), 0.00000001);
        assertEquals(41.91069159994975,  ((Double)retval.get(1)).doubleValue(), 0.00000001);
        assertEquals(12.06562671430088,  ((Double)retval.get(2)).doubleValue(), 0.00000001);
        assertEquals(1976.6432599699767, ((Double)retval.get(3)).doubleValue(), 0.00000001);
        assertEquals(44.45945636161082,  ((Double)retval.get(4)).doubleValue(), 0.00000001);
        assertEquals(12.549972243701887, ((Double)retval.get(5)).doubleValue(), 0.00000001);
        assertEquals(4333.097373073368,  ((Double)retval.get(6)).doubleValue(), 0.00000001);
        assertEquals(65.82626658920714,  ((Double)retval.get(7)).doubleValue(), 0.00000001);
        assertEquals(16.302948232909483, ((Double)retval.get(8)).doubleValue(), 0.00000001);
        */
    }
}

