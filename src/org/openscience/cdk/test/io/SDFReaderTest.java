/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLReader
 */
public class SDFReaderTest extends CDKTestCase {

    public SDFReaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(SDFReaderTest.class);
    }

    public void testAccepts() {
    	MDLReader reader = new MDLReader();
    	assertTrue(reader.accepts(ChemFile.class));
    	assertTrue(reader.accepts(ChemModel.class));
    	assertTrue(reader.accepts(Molecule.class));
    }

    public void testSDFFile() {
        String filename = "data/mdl/test.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile fileContents = (ChemFile)reader.read(new ChemFile());
            assertEquals(1, fileContents.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
            assertNotNull(sequence);
            assertEquals(9, sequence.getChemModelCount());
            for (int i=0; i<sequence.getChemModelCount(); i++) {
                assertNotNull(sequence.getChemModel(i));
            }
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testDataFromSDFReading() {
        String filename = "data/mdl/test.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile fileContents = (ChemFile)reader.read(new ChemFile());
            assertEquals(1, fileContents.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
            assertNotNull(sequence);
            assertEquals(9, sequence.getChemModelCount());
            org.openscience.cdk.interfaces.IChemModel model = sequence.getChemModel(0);
            assertNotNull(model);
            
            org.openscience.cdk.interfaces.ISetOfMolecules som = model.getSetOfMolecules();
            assertNotNull(som);
            assertEquals(1, som.getMoleculeCount());
            org.openscience.cdk.interfaces.IMolecule m = som.getMolecule(0);
            assertNotNull(m);
            assertEquals("1", m.getProperty("E_NSC"));
            assertEquals("553-97-9", m.getProperty("E_CAS"));
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    
    public void testSDFFile4() {
        String filename = "data/mdl/test4.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile fileContents = (ChemFile)reader.read(new ChemFile());
            assertEquals(1, fileContents.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
            assertNotNull(sequence);
            assertEquals(2, sequence.getChemModelCount());
            for (int i=0; i<sequence.getChemModelCount(); i++) {
                assertNotNull(sequence.getChemModel(i));
            }
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testSDFFile3() {
        String filename = "data/mdl/test3.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile fileContents = (ChemFile)reader.read(new ChemFile());
            assertEquals(1, fileContents.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
            assertNotNull(sequence);
            assertEquals(2, sequence.getChemModelCount());
            for (int i=0; i<sequence.getChemModelCount(); i++) {
                assertNotNull(sequence.getChemModel(i));
            }
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testSDFFile5() {
        String filename = "data/mdl/test5.sdf"; // a multi molecule SDF file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile fileContents = (ChemFile)reader.read(new ChemFile());
            assertEquals(1, fileContents.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
            assertNotNull(sequence);
            assertEquals(2, sequence.getChemModelCount());
            for (int i=0; i<sequence.getChemModelCount(); i++) {
                assertNotNull(sequence.getChemModel(i));
            }
        } catch (Exception e) {
            fail(e.toString());
        }
    }
}
