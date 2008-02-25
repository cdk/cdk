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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.HINReader;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;
import java.util.List;


/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class BCUTDescriptorTest extends MolecularDescriptorTest {

    public BCUTDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(BCUTDescriptorTest.class);
    }

    public void setUp() throws Exception {
        setDescriptor(BCUTDescriptor.class);
    }

    public void testBCUT() throws Exception {
        String filename = "data/hin/gravindex.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new HINReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        Object[] params = new Object[3];
        params[0] = 2;
        params[1] = 2;
        params[2] = true;
        descriptor.setParameters(params);
        DescriptorValue descriptorValue = descriptor.calculate(ac);

        DoubleArrayResult retval = (DoubleArrayResult) descriptorValue.getValue();
        assertNotNull(retval);
        /* System.out.println("Num ret = "+retval.size()); */
        for (int i = 0; i < retval.length(); i++) {
            assertTrue(
                    "The returned value must be non-zero",
                    Math.abs(0.0 - retval.get(i)) > 0.0000001
            );
        }

        String[] names = descriptorValue.getNames();
        for (String name : names) assertNotNull(name);

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

    public void testExtraEigenvalues() throws Exception {
        String filename = "data/hin/gravindex.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new HINReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);
                
        Object[] params = new Object[3];
        params[0] = 0;
        params[1] = 25;
        params[2] = true;
        descriptor.setParameters(params);
        DescriptorValue descriptorValue = descriptor.calculate(ac);

        DoubleArrayResult retval = (DoubleArrayResult) descriptorValue.getValue();
        int nheavy = 20;
       
        assertEquals(75, retval.length());        
        for (int i = 0; i < nheavy; i++) assertTrue(retval.get(i) != Double.NaN);
        for (int i = nheavy; i < nheavy+5; i++) {
            assertTrue("Extra eigenvalue should have been NaN", Double.isNaN(retval.get(i)));
        }

    }
}

