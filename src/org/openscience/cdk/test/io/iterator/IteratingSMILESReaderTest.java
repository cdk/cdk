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
package org.openscience.cdk.test.io.iterator;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading SMILES mol files using one test file.
 *
 * @cdk.module test-smiles
 *
 * @see org.openscience.cdk.io.SMILESReader
 */
public class IteratingSMILESReaderTest extends CDKTestCase {

    private LoggingTool logger;

    public IteratingSMILESReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(IteratingSMILESReaderTest.class);
    }

    public void testSMILESFileWithNames() {
        String filename = "data/smiles/test.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            IteratingSMILESReader reader = new IteratingSMILESReader(ins);
            
            int molCount = 0;
            while (reader.hasNext()) {
                Object object = reader.next();
                assertNotNull(object);
                assertTrue(object instanceof Molecule);
                molCount++;
            }
            
            assertEquals(5, molCount);
        } catch (Exception e) {
            logger.debug(e);
            fail(e.getMessage());
        }
    }

    public void testSMILESFile() {
        String filename = "data/smiles/test2.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            IteratingSMILESReader reader = new IteratingSMILESReader(ins);
            
            int molCount = 0;
            while (reader.hasNext()) {
                Object object = reader.next();
                assertNotNull(object);
                assertTrue(object instanceof Molecule);
                molCount++;
            }
            
            assertEquals(5, molCount);
        } catch (Exception e) {
            logger.debug(e);
            fail(e.getMessage());
        }
    }
}
