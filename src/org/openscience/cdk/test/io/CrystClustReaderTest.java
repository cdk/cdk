/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@slists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.test.io;

import java.io.InputStream;

import javax.vecmath.Vector3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.io.CrystClustReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.CrystClustReader
 */
public class CrystClustReaderTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public CrystClustReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(CrystClustReaderTest.class);
    }

    public void testAccepts() {
    	CrystClustReader reader = new CrystClustReader();
    	assertTrue(reader.accepts(ChemFile.class));
    }
    
    public void testEstrone() {
        String filename = "data/crystclust/estron.crystclust";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CrystClustReader reader = new CrystClustReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(2, seq.getChemModelCount());
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            
            org.openscience.cdk.interfaces.ICrystal crystal = model.getCrystal();
            assertNotNull(crystal);
            assertEquals(42, crystal.getAtomCount());
            assertEquals(1, crystal.getZ());
            
            // test reading of partial charges
            org.openscience.cdk.interfaces.IAtom atom = crystal.getAtom(0);
            assertNotNull(atom);
            assertEquals("O", atom.getSymbol());
            assertEquals(-0.68264902, atom.getCharge(), 0.00000001);
            
            // test unit cell axes
            Vector3d a = crystal.getA();
            assertEquals(7.971030, a.x, 0.000001);
            assertEquals(0.0, a.y, 0.000001);
            assertEquals(0.0, a.z, 0.000001);
            Vector3d b = crystal.getB();
            assertEquals(0.0, b.x, 0.000001);
            assertEquals(18.772200, b.y, 0.000001);
            assertEquals(0.0, b.z, 0.000001);
            Vector3d c = crystal.getC();
            assertEquals(0.0, c.x, 0.000001);
            assertEquals(0.0, c.y, 0.000001);
            assertEquals(10.262220, c.z, 0.000001);
        } catch (Exception exception) {
            System.out.println("Error while reading file: " + exception.getMessage());
            exception.printStackTrace();
            fail(exception.toString());
        }
    }
}
