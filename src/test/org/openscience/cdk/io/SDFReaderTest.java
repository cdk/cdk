/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLReader
 */
public class SDFReaderTest extends SimpleChemObjectReaderTest {

    @BeforeClass public static void setup() throws Exception {
        setSimpleChemObjectReader(new MDLV2000Reader(), "data/mdl/test.sdf");
    }

    @Test public void testAccepts() {
    	MDLV2000Reader reader = new MDLV2000Reader();
    	Assert.assertTrue(reader.accepts(ChemFile.class));
    	Assert.assertTrue(reader.accepts(ChemModel.class));
    	Assert.assertTrue(reader.accepts(Molecule.class));
    }

    @Test public void testSDFFile() throws Exception {
        String filename = "data/mdl/test.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile fileContents = (ChemFile)reader.read(new ChemFile());
        Assert.assertEquals(1, fileContents.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
        Assert.assertNotNull(sequence);
        Assert.assertEquals(9, sequence.getChemModelCount());
        for (int i=0; i<sequence.getChemModelCount(); i++) {
        	Assert.assertNotNull(sequence.getChemModel(i));
        }
    }

    @Test public void testDataFromSDFReading() throws Exception {
        String filename = "data/mdl/test.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile fileContents = (ChemFile)reader.read(new ChemFile());
        Assert.assertEquals(1, fileContents.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
        Assert.assertNotNull(sequence);
        Assert.assertEquals(9, sequence.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = sequence.getChemModel(0);
        Assert.assertNotNull(model);

        org.openscience.cdk.interfaces.IMoleculeSet som = model.getMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(1, som.getMoleculeCount());
        org.openscience.cdk.interfaces.IMolecule m = som.getMolecule(0);
        Assert.assertNotNull(m);
        Assert.assertEquals("1", m.getProperty("E_NSC"));
        Assert.assertEquals("553-97-9", m.getProperty("E_CAS"));
    }
    
    @Test public void testSDFFile4() throws Exception {
        String filename = "data/mdl/test4.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile fileContents = (ChemFile)reader.read(new ChemFile());
        Assert.assertEquals(1, fileContents.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
        Assert.assertNotNull(sequence);
        Assert.assertEquals(2, sequence.getChemModelCount());
        for (int i=0; i<sequence.getChemModelCount(); i++) {
        	Assert.assertNotNull(sequence.getChemModel(i));
        }
    }

    @Test public void testSDFFile3() throws Exception {
        String filename = "data/mdl/test3.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile fileContents = (ChemFile)reader.read(new ChemFile());
        Assert.assertEquals(1, fileContents.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
        Assert.assertNotNull(sequence);
        Assert.assertEquals(2, sequence.getChemModelCount());
        for (int i=0; i<sequence.getChemModelCount(); i++) {
        	Assert.assertNotNull(sequence.getChemModel(i));
        }
    }

    @Test public void testSDFFile5() throws Exception {
        String filename = "data/mdl/test5.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile fileContents = (ChemFile)reader.read(new ChemFile());
        Assert.assertEquals(1, fileContents.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
        Assert.assertNotNull(sequence);
        Assert.assertEquals(2, sequence.getChemModelCount());
        for (int i=0; i<sequence.getChemModelCount(); i++) {
        	Assert.assertNotNull(sequence.getChemModel(i));
        }
    }
    
    /**
     * Test for bug 1974826
     * @throws Exception
     */
    @Test public void testSDFFile6() throws Exception {
        String filename = "data/mdl/test6.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile fileContents = (ChemFile)reader.read(new ChemFile());
        Assert.assertEquals(1, fileContents.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
        Assert.assertNotNull(sequence);
        Assert.assertEquals(3, sequence.getChemModelCount());
        for (int i=0; i<sequence.getChemModelCount(); i++) {
          Assert.assertNotNull(sequence.getChemModel(i));
        }
    }

}
