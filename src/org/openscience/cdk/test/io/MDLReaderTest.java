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

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestCase for the reading MDL mol files using one test file.
 * A test case for SDF files is available as separate Class.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLReader
 * @see org.openscience.cdk.test.io.SDFReaderTest
 */
public class MDLReaderTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public MDLReaderTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(MDLReaderTest.class);
    }

    public void testAccepts() {
    	MDLReader reader = new MDLReader();
    	assertTrue(reader.accepts(ChemFile.class));
    	assertTrue(reader.accepts(ChemModel.class));
    	assertTrue(reader.accepts(Molecule.class));
    }
    
    public void testBug682233() {
        String filename = "data/mdl/bug682233.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            
            org.openscience.cdk.interfaces.IMoleculeSet som = model.getSetOfMolecules();
            assertNotNull(som);
            assertEquals(1, som.getMoleculeCount());
            org.openscience.cdk.interfaces.IMolecule m = som.getMolecule(0);
            assertNotNull(m);
            assertEquals(4, m.getAtomCount());
            assertEquals(2, m.getBondCount());
            
            // test reading of formal charges
            org.openscience.cdk.interfaces.IAtom a = m.getAtom(0);
            assertNotNull(a);
            assertEquals("Na", a.getSymbol());
            assertEquals(1, a.getFormalCharge());
            a = m.getAtom(2); 
            assertNotNull(a);
            assertEquals("O", a.getSymbol());
            assertEquals(-1, a.getFormalCharge());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testAPinene() {
        String filename = "data/mdl/a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            assertNotNull(chemFile);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getAtomCount() > 0);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getBondCount() > 0);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testReadTitle() {
        String filename = "data/mdl/a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            Molecule mol = (Molecule)reader.read(new Molecule());
            assertEquals("a-pinen.mol", mol.getProperty(CDKConstants.TITLE));
        } catch (Exception exception) {
            logger.debug(exception);
            fail(exception.getMessage());
        }
    }

    public void testFourRing() {
        String filename = "data/mdl/four-ring-5x10.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            assertNotNull(chemFile);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getAtomCount() > 0);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getBondCount() > 0);
        } catch (Exception e) {
            fail(e.toString());
        }
    }


    public void testHydrozyamino() {
        String filename = "data/mdl/hydroxyamino.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            assertNotNull(chemFile);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getAtomCount() > 0);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getBondCount() > 0);
        } catch (Exception e) {
            fail(e.toString());
        }
    }


    public void testMethylBenzol() {
        String filename = "data/mdl/methylbenzol.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            assertNotNull(chemFile);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getAtomCount() > 0);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getBondCount() > 0);
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    

    public void testPolycarpol() {
        String filename = "data/mdl/polycarpol.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            assertNotNull(chemFile);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getAtomCount() > 0);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getBondCount() > 0);
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    
    public void testReserpine() {
        String filename = "data/mdl/reserpine.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            assertNotNull(chemFile);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getAtomCount() > 0);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getBondCount() > 0);
        } catch (Exception e) {
            fail(e.toString());
        }
    }    


    public void testSixRing() {
        String filename = "data/mdl/six-ring-4x4.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            assertNotNull(chemFile);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getAtomCount() > 0);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getBondCount() > 0);
        } catch (Exception e) {
            fail(e.toString());
        }
    }


    public void testSuperspiro() {
        String filename = "data/mdl/superspiro.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            assertNotNull(chemFile);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getAtomCount() > 0);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getBondCount() > 0);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testGhemicalOutput() {
        String filename = "data/mdl/butanoic_acid.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            assertNotNull(chemFile);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getAtomCount() > 0);
            assertTrue(ChemFileManipulator.getAllInOneContainer(chemFile).getBondCount() > 0);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testUsesGivenMolecule() {
        String filename = "data/mdl/superspiro.mol"; // just a random file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            Molecule superspiro = new Molecule();
            superspiro.setID("superspiro");
            Molecule result = (Molecule)reader.read(superspiro);
            assertEquals(superspiro.getID(), result.getID());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /** 
     * Problem was filed as bug #835571
     */
    public void testReadFromStringReader () {
        String mdl =
                "cyclopropane.mol\n" +
                "\n" +
                "\n" +
                "  9  9  0  0  0                 1 V2000\n" +
                "   -0.0073   -0.5272    0.9655 C   0  0  0  0  0\n" +
                "   -0.6776   -0.7930   -0.3498 C   0  0  0  0  0\n" +
                "    0.2103    0.4053   -0.1891 C   0  0  0  0  0\n" +
                "    0.8019   -1.1711    1.2970 H   0  0  0  0  0\n" +
                "   -0.6000   -0.2021    1.8155 H   0  0  0  0  0\n" +
                "   -1.7511   -0.6586   -0.4435 H   0  0  0  0  0\n" +
                "   -0.3492   -1.6277   -0.9620 H   0  0  0  0  0\n" +
                "    1.1755    0.4303   -0.6860 H   0  0  0  0  0\n" +
                "   -0.2264    1.3994   -0.1675 H   0  0  0  0  0\n" +
                "  1  2  1  6  0  0\n" +
                "  1  3  1  6  0  0\n" +
                "  1  4  1  0  0  0\n" +
                "  1  5  1  1  0  0\n" +
                "  2  3  1  0  0  0\n" +
                "  2  6  1  0  0  0\n" +
                "  2  7  1  6  0  0\n" +
                "  3  8  1  6  0  0\n" +
                "  3  9  1  0  0  0\n" +
                "M  END\n";
        try {
            MDLReader reader = new MDLReader(new StringReader(mdl));
            ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            
            org.openscience.cdk.interfaces.IMoleculeSet som = model.getSetOfMolecules();
            assertNotNull(som);
            assertEquals(1, som.getMoleculeCount());
            org.openscience.cdk.interfaces.IMolecule m = som.getMolecule(0);
            assertNotNull(m);
            assertEquals(9, m.getAtomCount());
            assertEquals(9, m.getBondCount());
        } catch (Throwable problem) {
            problem.printStackTrace();
            fail();
        }
    }
    
    public void testRGroup() {
        String filename = "data/mdl/SARGROUPTEST.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            Molecule mol = (Molecule)reader.read(new Molecule());
            assertEquals("R2",((PseudoAtom)mol.getAtom(19)).getLabel());
             } catch (Exception e) {
            fail(e.toString());
        }
    }
    public void testAliasPropertyGroup() {
        String filename = "data/mdl/AliasPropertyRGroup.sdf";
        File file = new File(filename);
        String fi = file.getAbsolutePath();
        boolean a = file.exists();
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            Molecule mol = (Molecule)reader.read(new Molecule());
            assertEquals("R1",((PseudoAtom)mol.getAtom(3)).getLabel());
             } catch (Exception e) {
            fail(e.toString());
        }
    }
}
