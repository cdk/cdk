/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;

import java.io.InputStream;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLReader
 */
public class SDFReaderTest extends SimpleChemObjectReaderTest {

    @BeforeAll
    public static void setup() throws Exception {
        setSimpleChemObjectReader(new MDLV2000Reader(), "org/openscience/cdk/io/iterator/test.sdf");
    }

    @Test
    public void testAccepts() {
        MDLV2000Reader reader = new MDLV2000Reader();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
        Assertions.assertTrue(reader.accepts(ChemModel.class));
        Assertions.assertTrue(reader.accepts(AtomContainer.class));
    }

    @Test
    public void testSDFFile() throws Exception {
        String filename = "iterator/test.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile fileContents = reader.read(new ChemFile());
        reader.close();
        Assertions.assertEquals(1, fileContents.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
        Assertions.assertNotNull(sequence);
        Assertions.assertEquals(9, sequence.getChemModelCount());
        for (int i = 0; i < sequence.getChemModelCount(); i++) {
            Assertions.assertNotNull(sequence.getChemModel(i));
        }
    }

    @Test
    public void testDataFromSDFReading() throws Exception {
        String filename = "iterator/test.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile fileContents = reader.read(new ChemFile());
        reader.close();
        Assertions.assertEquals(1, fileContents.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
        Assertions.assertNotNull(sequence);
        Assertions.assertEquals(9, sequence.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = sequence.getChemModel(0);
        Assertions.assertNotNull(model);

        IAtomContainerSet som = model.getMoleculeSet();
        Assertions.assertNotNull(som);
        Assertions.assertEquals(1, som.getAtomContainerCount());
        IAtomContainer m = som.getAtomContainer(0);
        Assertions.assertNotNull(m);
        Assertions.assertEquals("1", m.getProperty("E_NSC"));
        Assertions.assertEquals("553-97-9", m.getProperty("E_CAS"));
    }

    @Test
    public void testMultipleDataFields() throws Exception {
        String filename = "bug1587283.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile fileContents = reader.read(new ChemFile());
        reader.close();
        Assertions.assertEquals(1, fileContents.getChemSequenceCount());
        IChemSequence sequence = fileContents.getChemSequence(0);
        Assertions.assertNotNull(sequence);
        Assertions.assertEquals(1, sequence.getChemModelCount());
        IChemModel model = sequence.getChemModel(0);
        Assertions.assertNotNull(model);
        IAtomContainerSet som = model.getMoleculeSet();
        Assertions.assertNotNull(som);
        Assertions.assertEquals(1, som.getAtomContainerCount());
        IAtomContainer m = som.getAtomContainer(0);
        Assertions.assertNotNull(m);
        Assertions.assertEquals("B02", m.getProperty("id_no"));
        Assertions.assertEquals("2-2", m.getProperty("eductkey"));
        Assertions.assertEquals("1", m.getProperty("Step"));
        Assertions.assertEquals("2", m.getProperty("Pos"));
        Assertions.assertEquals("B02", m.getProperty("Tag"));
    }

    @Test
    public void testSDFFile4() throws Exception {
        String filename = "test4.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile fileContents = reader.read(new ChemFile());
        reader.close();
        Assertions.assertEquals(1, fileContents.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
        Assertions.assertNotNull(sequence);
        Assertions.assertEquals(2, sequence.getChemModelCount());
        for (int i = 0; i < sequence.getChemModelCount(); i++) {
            Assertions.assertNotNull(sequence.getChemModel(i));
        }
    }

    @Test
    public void testSDFFile3() throws Exception {
        String filename = "test3.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile fileContents = reader.read(new ChemFile());
        reader.close();
        Assertions.assertEquals(1, fileContents.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
        Assertions.assertNotNull(sequence);
        Assertions.assertEquals(2, sequence.getChemModelCount());
        for (int i = 0; i < sequence.getChemModelCount(); i++) {
            Assertions.assertNotNull(sequence.getChemModel(i));
        }
    }

    @Test
    public void testSDFFile5() throws Exception {
        String filename = "test5.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile fileContents = reader.read(new ChemFile());
        reader.close();
        Assertions.assertEquals(1, fileContents.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
        Assertions.assertNotNull(sequence);
        Assertions.assertEquals(2, sequence.getChemModelCount());
        for (int i = 0; i < sequence.getChemModelCount(); i++) {
            Assertions.assertNotNull(sequence.getChemModel(i));
        }
    }

    /**
     * Test for bug 1974826.
     *
     * @cdk.bug 1974826
     */
    @Test
    public void testSDFFile6() throws Exception {
        String filename = "test6.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile fileContents = reader.read(new ChemFile());
        reader.close();
        Assertions.assertEquals(1, fileContents.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
        Assertions.assertNotNull(sequence);
        Assertions.assertEquals(3, sequence.getChemModelCount());
        for (int i = 0; i < sequence.getChemModelCount(); i++) {
            Assertions.assertNotNull(sequence.getChemModel(i));
        }

        IChemModel model = sequence.getChemModel(0);
        Assertions.assertNotNull(model);
        IAtomContainerSet som = model.getMoleculeSet();
        Assertions.assertNotNull(som);
        Assertions.assertEquals(1, som.getAtomContainerCount());
        IAtomContainer m = som.getAtomContainer(0);
        Assertions.assertNotNull(m);
        Assertions.assertEquals("ola11", m.getProperty("STRUCTURE ID"));
    }

    /**
     * Tests that data fields starting with a '>' are allowed.
     *
     * @cdk.bug 2911300
     */
    @Test
    public void testBug2911300() throws Exception {
        String filename = "bug2911300.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile fileContents = reader.read(new ChemFile());
        reader.close();
        Assertions.assertEquals(1, fileContents.getChemSequenceCount());
        IChemSequence sequence = fileContents.getChemSequence(0);
        IChemModel model = sequence.getChemModel(0);
        Assertions.assertNotNull(model);
        IAtomContainerSet som = model.getMoleculeSet();
        Assertions.assertNotNull(som);
        Assertions.assertEquals(1, som.getAtomContainerCount());
        IAtomContainer m = som.getAtomContainer(0);
        Assertions.assertNotNull(m);
        Assertions.assertEquals(">1", m.getProperty("IC50_uM"));
    }

}
