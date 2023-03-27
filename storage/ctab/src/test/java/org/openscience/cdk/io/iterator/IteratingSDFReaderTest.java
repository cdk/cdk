/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test-io
 * @see org.openscience.cdk.io.MDLReader
 */
class IteratingSDFReaderTest extends CDKTestCase {

    private final ILoggingTool logger = LoggingToolFactory.createLoggingTool(IteratingSDFReaderTest.class);

    @Test
    void testSDF() throws Exception {
        String filename = "test2.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, DefaultChemObjectBuilder.getInstance());

        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assertions.assertNotNull(object);
            assertTrue(object instanceof IAtomContainer);
            molCount++;
            Assertions.assertEquals(MDLV2000Format.getInstance(), reader.getFormat(), "Molecule # was not in MDL V2000 format: " + molCount);
        }

        Assertions.assertEquals(6, molCount);
        reader.close();
    }

    @Test
    void testSDF_broken_stream() throws Exception {
        String filename = "test2.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        InputStreamReader streamReader = new InputStreamReader(ins) {

            @Override
            public boolean ready() throws IOException {
                return false;
            }
        };

        IteratingSDFReader reader = new IteratingSDFReader(streamReader, DefaultChemObjectBuilder.getInstance());

        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assertions.assertNotNull(object);
            assertTrue(object instanceof IAtomContainer);
            molCount++;
            Assertions.assertEquals(MDLV2000Format.getInstance(), reader.getFormat(), "Molecule # was not in MDL V2000 format: " + molCount);
        }

        Assertions.assertEquals(6, molCount);
        reader.close();
    }

    @Test
    void testReadTitle() throws Exception {
        String filename = "test.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, DefaultChemObjectBuilder.getInstance());

        //int molCount = 0;
        assertTrue(reader.hasNext());
        Object object = reader.next();
        Assertions.assertNotNull(object);
        assertTrue(object instanceof IAtomContainer);
        Assertions.assertEquals("2-methylbenzo-1,4-quinone", ((IAtomContainer) object).getTitle());
        Assertions.assertEquals(MDLV2000Format.getInstance(), reader.getFormat());
        reader.close();
    }

    @Test
    void testReadDataItems() throws Exception {
        String filename = "test.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, DefaultChemObjectBuilder.getInstance());

        //int molCount = 0;
        assertTrue(reader.hasNext());
        Object object = reader.next();
        Assertions.assertNotNull(object);
        assertTrue(object instanceof IAtomContainer);
        IAtomContainer m = (IAtomContainer) object;
        Assertions.assertEquals("1", m.getProperty("E_NSC"));
        Assertions.assertEquals("553-97-9", m.getProperty("E_CAS"));
        reader.close();
    }

    @Test
    void testMultipleEntryFields() throws Exception {
        String filename = "test.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, DefaultChemObjectBuilder.getInstance());

        IAtomContainer m = reader.next();
        Assertions.assertEquals("553-97-9", m.getProperty("E_CAS"));
        m = reader.next();
        Assertions.assertEquals("120-78-5", m.getProperty("E_CAS"));
        reader.close();
    }

    @Test
    void testOnMDLMolfile() throws Exception {
        String filename = "bug682233.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, DefaultChemObjectBuilder.getInstance());

        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assertions.assertNotNull(object);
            assertTrue(object instanceof IAtomContainer);
            molCount++;
        }

        Assertions.assertEquals(1, molCount);
        reader.close();
    }

    @Test
    void testOnSingleEntrySDFile() throws Exception {
        String filename = "singleMol.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, DefaultChemObjectBuilder.getInstance());

        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assertions.assertNotNull(object);
            assertTrue(object instanceof IAtomContainer);
            molCount++;
        }

        Assertions.assertEquals(1, molCount);
        reader.close();
    }

    @Test
    void testEmptyEntryIteratingReader() throws IOException {
        String filename = "emptyStructures.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, DefaultChemObjectBuilder.getInstance());
        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assertions.assertNotNull(object);
            assertTrue(object instanceof IAtomContainer);
            molCount++;

            if (molCount == 2) {
                IAtomContainer mol = (IAtomContainer) object;
                String s = mol.getProperty("Species");
                Assertions.assertEquals("rat", s);
            }
        }

        Assertions.assertEquals(2, molCount);
        reader.close();
    }

    /**
     * @cdk.bug 2692107
     */
    @Test
    void testZeroZCoordinates() throws Exception {
        String filename = "nozcoord.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        Properties prop = new Properties();
        prop.setProperty("ForceReadAs3DCoordinates", "true");
        PropertiesListener listener = new PropertiesListener(prop);
        IteratingSDFReader reader = new IteratingSDFReader(ins, DefaultChemObjectBuilder.getInstance());
        reader.addChemObjectIOListener(listener);
        reader.customizeJob();
        int molCount = 0;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assertions.assertNotNull(object);
            assertTrue(object instanceof IAtomContainer);
            molCount++;
            boolean has3d = GeometryUtil.has3DCoordinates((IAtomContainer) object);
            assertTrue(has3d);
        }
        Assertions.assertNotSame(0, molCount);
        reader.close();
    }

    @Test
    void testNo3DCoordsButForcedAs() throws IOException {
        // First test unforced 3D coordinates
        String filename = "no3dStructures.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        IteratingSDFReader reader = new IteratingSDFReader(ins, DefaultChemObjectBuilder.getInstance());
        int molCount = 0;
        IAtomContainer mol = null;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assertions.assertNotNull(object);
            assertTrue(object instanceof IAtomContainer);
            molCount++;
            mol = (IAtomContainer) object;
        }

        Assertions.assertEquals(2, molCount);
        Assertions.assertNotNull(mol.getAtom(0).getPoint2d());
        Assertions.assertNull(mol.getAtom(0).getPoint3d());
        reader.close();

        // Now test forced 3D coordinates
        logger.info("Testing: " + filename);
        ins = this.getClass().getResourceAsStream(filename);
        reader = new IteratingSDFReader(ins, DefaultChemObjectBuilder.getInstance());
        reader.addChemObjectIOListener(new MyListener());
        reader.customizeJob();
        molCount = 0;
        mol = null;
        while (reader.hasNext()) {
            Object object = reader.next();
            Assertions.assertNotNull(object);
            assertTrue(object instanceof IAtomContainer);
            molCount++;
            mol = (IAtomContainer) object;
        }

        Assertions.assertEquals(2, molCount);
        Assertions.assertNull(mol.getAtom(0).getPoint2d());
        Assertions.assertNotNull(mol.getAtom(0).getPoint3d());
        reader.close();
    }

    class MyListener implements IChemObjectIOListener {

        @Override
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

    /**
     * @cdk.bug 3488307
     */
    @Test
    void testBrokenSDF() throws IOException, CDKException {

        String path = "bug3488307.sdf";
        InputStream in = getClass().getResourceAsStream(path);
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IteratingSDFReader reader = new IteratingSDFReader(in, builder);

        reader.setSkip(true); // skip over null entries and keep reading until EOF

        int count = 0;

        while (reader.hasNext()) {
            reader.next();
            count++;
        }

        reader.close();

        Assertions.assertEquals(3, count);

    }

    @Test
    void testV3000MolfileFormat() throws IOException, CDKException {

        String path = "molV3000.mol";
        InputStream in = getClass().getResourceAsStream(path);
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IteratingSDFReader reader = new IteratingSDFReader(in, builder);

        reader.setSkip(true); // skip over null entries and keep reading until EOF

        int count = 0;

        while (reader.hasNext()) {
            reader.next();
            count++;
        }

        reader.close();

        Assertions.assertEquals(1, count);

    }

    // extra spaces from the ChEMBL API
    @Test
    void testExtraSpaces() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("chemblApiExamples.sdf")) {
            IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
            IteratingSDFReader reader = new IteratingSDFReader(in, builder);
            reader.setSkip(true); // skip over null entries and keep reading until EOF
            assertTrue(reader.hasNext());
            IAtomContainer mol = reader.next();
            Assertions.assertEquals(mol.<String>getProperty("chembl_id"), "CHEMBL564829");
            Assertions.assertEquals(mol.<String>getProperty("chembl_pref_name"), "MILCICLIB");
            assertTrue(reader.hasNext());
            mol = reader.next();
            Assertions.assertEquals(mol.<String>getProperty("chembl_id"), "CHEMBL603469");
            Assertions.assertEquals(mol.<String>getProperty("chembl_pref_name"), "LESTAURTINIB");
            assertTrue(reader.hasNext());
            mol = reader.next();
            Assertions.assertEquals(mol.<String>getProperty("chembl_id"), "CHEMBL1946170");
            Assertions.assertEquals(mol.<String>getProperty("chembl_pref_name"), "REGORAFENIB");
        }
    }

}
