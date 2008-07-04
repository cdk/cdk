/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.io;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test-smiles
 *
 * @see org.openscience.cdk.io.MDLReader
 */
public class SMILESReaderTest extends ChemObjectIOTest {

    private static LoggingTool logger;

    @BeforeClass public static void setup() throws Exception {
        logger = new LoggingTool(SMILESReaderTest.class);
        setChemObjectIO(new SMILESReader());
    }

    @Test public void testAccepts() {
    	SMILESReader reader = new SMILESReader();
    	Assert.assertTrue(reader.accepts(ChemFile.class));
    	Assert.assertTrue(reader.accepts(MoleculeSet.class));
    }

    @Test public void testReading() throws Exception {
        String filename = "data/smiles/smiles.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        SMILESReader reader = new SMILESReader(ins);
        MoleculeSet som = (MoleculeSet)reader.read(new MoleculeSet());
        Assert.assertEquals(8, som.getMoleculeCount());
    }
    
    
    @Test public void testReadingSmiFile_1() throws Exception {
        String filename = "data/smiles/smiles.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        SMILESReader reader = new SMILESReader(ins);
        MoleculeSet som = (MoleculeSet)reader.read(new MoleculeSet());
        String name = null;
        org.openscience.cdk.interfaces.IMolecule thisMol = null;
	    
	    thisMol = som.getMolecule(0);
	    name = ( (String)thisMol.getProperty("SMIdbNAME") ).toString();
	    Assert.assertEquals("benzene", name);
    }
    
    @Test public void testReadingSmiFile_2() throws Exception {
        String filename = "data/smiles/smiles.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        SMILESReader reader = new SMILESReader(ins);
        MoleculeSet som = (MoleculeSet)reader.read(new MoleculeSet());
        org.openscience.cdk.interfaces.IMolecule thisMol = som.getMolecule(1);
        Assert.assertNull(thisMol.getProperty("SMIdbNAME"));
    }

    @Test public void testReadingSmiFile_3() throws Exception {
        String filename = "data/smiles/test3.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        SMILESReader reader = new SMILESReader(ins);
        MoleculeSet som = (MoleculeSet)reader.read(new MoleculeSet());
        Assert.assertEquals(5, som.getMoleculeCount());
    }
    
}
