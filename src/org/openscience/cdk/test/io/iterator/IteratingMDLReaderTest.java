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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLReader
 */
public class IteratingMDLReaderTest extends CDKTestCase {

    private LoggingTool logger;

    public IteratingMDLReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(IteratingMDLReaderTest.class);
    }

    public void testSDF() {
        String filename = "data/mdl/test2.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());
            
            int molCount = 0;
            while (reader.hasNext()) {
                Object object = reader.next();
                assertNotNull(object);
                assertTrue(object instanceof Molecule);
                molCount++;
            }
            
            assertEquals(MDLV2000Format.getInstance(), reader.getFormat());
            assertEquals(6, molCount);
        } catch (Exception e) {
            logger.debug(e);
            fail(e.getMessage());
        }
    }

    public void testReadTitle() {
        String filename = "data/mdl/test.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());

            //int molCount = 0;
            assertTrue(reader.hasNext());
            Object object = reader.next();
            assertNotNull(object);
            assertTrue(object instanceof Molecule);
            assertEquals("2-methylbenzo-1,4-quinone", ((Molecule)object).getProperty(CDKConstants.TITLE));
            assertEquals(MDLV2000Format.getInstance(), reader.getFormat());
        } catch (Exception e) {
            logger.debug(e);
            fail(e.getMessage());
        }
    }

    public void testReadDataItems() {
        String filename = "data/mdl/test.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());

            //int molCount = 0;
            assertTrue(reader.hasNext());
            Object object = reader.next();
            assertNotNull(object);
            assertTrue(object instanceof Molecule);
            Molecule m = (Molecule)object;
            assertEquals("1", m.getProperty("E_NSC"));
            assertEquals("553-97-9", m.getProperty("E_CAS"));
        } catch (Exception e) {
            logger.debug(e);
            fail(e.getMessage());
        }
    }

    public void testOnMDLMolfile() {
        String filename = "data/mdl/bug682233.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());
            
            int molCount = 0;
            while (reader.hasNext()) {
                Object object = reader.next();
                assertNotNull(object);
                assertTrue(object instanceof Molecule);
                molCount++;
            }
            
            assertEquals(1, molCount);
        } catch (Exception e) {
            logger.debug(e);
            fail(e.getMessage());
        }
    }

    public void testOnSingleEntrySDFile() {
        String filename = "data/mdl/singleMol.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());
            
            int molCount = 0;
            while (reader.hasNext()) {
                Object object = reader.next();
                assertNotNull(object);
                assertTrue(object instanceof Molecule);
                molCount++;
            }
            
            assertEquals(1, molCount);
        } catch (Exception e) {
            logger.debug(e);
            fail(e.getMessage());
        }
    }

    public void testPreV2000() {
        String filename = "data/mdl/prev2000.sd";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());
            
            // two compounds
            assertTrue(reader.hasNext());
            assertNotNull(reader.next()); 
            assertEquals(MDLFormat.getInstance(), reader.getFormat());
            assertTrue(reader.hasNext());
            assertNotNull(reader.next()); 
        } catch (Exception exception) {
        	exception.printStackTrace();
            fail(exception.getMessage());
        }
    }
}
