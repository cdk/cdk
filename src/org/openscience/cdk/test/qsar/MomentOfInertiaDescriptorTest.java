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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.test.qsar;

import org.openscience.cdk.qsar.*;
import org.openscience.cdk.qsar.result.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.ChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.util.ArrayList;
import java.io.*;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test
 */

public class MomentOfInertiaDescriptorTest extends TestCase {
	
	public MomentOfInertiaDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(MomentOfInertiaDescriptorTest.class);
	}
    
        public void testMomentOfInertia() throws ClassNotFoundException, CDKException, java.lang.Exception {
            String filename = "data/gravindex.hin";
            File input = new File(filename);
            ChemObjectReader reader = new ReaderFactory().createReader(new FileReader(input));
            ChemFile content = (ChemFile)reader.read((ChemObject)new ChemFile());
            AtomContainer[] c = ChemFileManipulator.getAllAtomContainers(content);
            AtomContainer ac = c[0];

            Descriptor descriptor = new MomentOfInertiaDescriptor();
            DoubleArrayResult retval = (DoubleArrayResult)descriptor.calculate(ac).getValue();

            assertEquals(1044.55881, retval.get(0), 0.00001);
            assertEquals(1277.10448, retval.get(1), 0.00001);
            assertEquals(1888.95941, retval.get(2), 0.00001);
            assertEquals(0.81791, retval.get(3), 0.00001);
            assertEquals(0.55298, retval.get(4), 0.00001);
            assertEquals(0.67608, retval.get(5), 0.00001);
            assertEquals(5.50528, retval.get(6), 0.00001);
        }
}

