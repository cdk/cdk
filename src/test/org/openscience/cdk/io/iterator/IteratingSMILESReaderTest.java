/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.SMILESFormat;
import org.openscience.cdk.tools.LoggingTool;

import java.io.InputStream;

/**
 * TestCase for the reading SMILES mol files using one test file.
 *
 * @cdk.module test-smiles
 * @see org.openscience.cdk.io.SMILESReader
 */
public class IteratingSMILESReaderTest extends NewCDKTestCase {

    private static LoggingTool logger;

    @Before
    public static void setup() {
       logger = new LoggingTool(IteratingSMILESReaderTest.class);
    }

    @Test
    public void testSMILESFileWithNames() throws Exception {
        String filename = "data/smiles/test.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingSMILESReader reader = new IteratingSMILESReader(ins);

        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof Molecule);
            molCount++;
        }

        Assert.assertEquals(5, molCount);
    }

    @Test
    public void testSMILESFileWithSpacesAndTabs() throws Exception {
        String filename = "data/smiles/tabs.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingSMILESReader reader = new IteratingSMILESReader(ins);

        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof Molecule);
            molCount++;
        }

        Assert.assertEquals(5, molCount);
    }

    @Test
    public void testSMILESTitles() throws Exception {
        String filename = "data/smiles/tabs.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingSMILESReader reader = new IteratingSMILESReader(ins);

        while (reader.hasNext()) {

            IMolecule mol = (IMolecule) reader.next();
            String title = (String) mol.getProperty(CDKConstants.TITLE);
            Assert.assertNotNull(title);
        }

    }

    @Test
    public void testSMILESFile() {
        String filename = "data/smiles/test2.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingSMILESReader reader = new IteratingSMILESReader(ins);

        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof Molecule);
            molCount++;
        }

        Assert.assertEquals(5, molCount);
    }

    @Test
    public void testGetFormat() {
       String filename = "data/smiles/test2.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingSMILESReader reader = new IteratingSMILESReader(ins);
        IResourceFormat format = reader.getFormat();
        Assert.assertTrue(format instanceof SMILESFormat);
    }
}
