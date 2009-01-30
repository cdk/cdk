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

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test-io
 * @see org.openscience.cdk.io.MDLReader
 */
public class IteratingMDLReaderTest extends CDKTestCase {

    private LoggingTool logger = new LoggingTool(this);

    @Test public void testSDF() throws Exception {
        String filename = "data/mdl/test2.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());

        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof Molecule);
            molCount++;
            Assert.assertEquals("Molecule # was not in MDL V2000 format: " + molCount,
                    MDLV2000Format.getInstance(), reader.getFormat());
        }

        Assert.assertEquals(6, molCount);
    }

    @Test public void testReadTitle() throws Exception {
        String filename = "data/mdl/test.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());

        //int molCount = 0;
        Assert.assertTrue(reader.hasNext());
        Object object = reader.next();
        Assert.assertNotNull(object);
        Assert.assertTrue(object instanceof Molecule);
        Assert.assertEquals("2-methylbenzo-1,4-quinone", ((Molecule) object).getProperty(CDKConstants.TITLE));
        Assert.assertEquals(MDLV2000Format.getInstance(), reader.getFormat());
    }

    @Test public void testReadDataItems() throws Exception {
        String filename = "data/mdl/test.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());

        //int molCount = 0;
        Assert.assertTrue(reader.hasNext());
        Object object = reader.next();
        Assert.assertNotNull(object);
        Assert.assertTrue(object instanceof Molecule);
        Molecule m = (Molecule) object;
        Assert.assertEquals("1", m.getProperty("E_NSC"));
        Assert.assertEquals("553-97-9", m.getProperty("E_CAS"));
    }

    @Test public void testOnMDLMolfile() throws Exception {
        String filename = "data/mdl/bug682233.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());

        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof Molecule);
            molCount++;
        }

        Assert.assertEquals(1, molCount);
    }

    @Test public void testOnSingleEntrySDFile() throws Exception {
        String filename = "data/mdl/singleMol.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());

        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof Molecule);
            molCount++;
        }

        Assert.assertEquals(1, molCount);
    }

    @Test public void testEmptyEntryIteratingReader() {
        String filename = "data/mdl/emptyStructures.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());
        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof Molecule);
            molCount++;

            if (molCount == 2) {
                IMolecule mol = (IMolecule) object;
                String s = (String) mol.getProperty("Species");
                Assert.assertEquals("rat", s);
            }
        }

        Assert.assertEquals(2, molCount);

    }

    @Test public void testNo3DCoordsButForcedAs() {
    	// First test unforced 3D coordinates
        String filename = "data/mdl/no3dStructures.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());
        int molCount = 0;
        IMolecule mol = null; 
        while (reader.hasNext()) {
            Object object = reader.next();
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof IMolecule);
            molCount++;
            mol = (IMolecule)object;
        }

        Assert.assertEquals(2, molCount);
        Assert.assertNotNull(mol.getAtom(0).getPoint2d());
        Assert.assertNull(mol.getAtom(0).getPoint3d());

    	// Now test forced 3D coordinates
        logger.info("Testing: " + filename);
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());
        reader.addChemObjectIOListener(new MyListener());
        reader.customizeJob();
        molCount = 0;
        mol = null; 
        while (reader.hasNext()) {
            Object object = reader.next();
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof IMolecule);
            molCount++;
            mol = (IMolecule)object;
        }

        Assert.assertEquals(2, molCount);
        Assert.assertNull(mol.getAtom(0).getPoint2d());
        Assert.assertNotNull(mol.getAtom(0).getPoint3d());
    }
    
    class MyListener implements IChemObjectIOListener {

		public void processIOSettingQuestion(IOSetting setting) {
		    if ("ForceReadAs3DCoordinates".equals(setting.getName())) {
		    	try {
		            setting.setSetting("true");
	            } catch (CDKException e) {
		            logger.error("Could not set forceReadAs3DCoords setting: ", e.getMessage());
		            logger.debug(e);
	            }
		    }
        }
    	
    }

}
