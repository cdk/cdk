/* $Revision$ $Author$ $Date$
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
package org.openscience.cdk.test.io;

import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.nonotify.NNMolecule;
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
    	reader.setReaderMode(Mode.STRICT);
    	assertTrue(reader.accepts(ChemFile.class));
    	assertTrue(reader.accepts(ChemModel.class));
    	assertTrue(reader.accepts(Molecule.class));
    }

    public void testReadFromStringReader() throws Exception {
        String mdl =
                "cyclopropane.mol\n" +
                "\n" +
                "\n" +
                "  9  9  0  0\n" +
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
                "  3  9  1  0  0  0\n";
        MDLReader reader = new MDLReader(new StringReader(mdl), Mode.STRICT);
        ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
        assertNotNull(chemFile);
        assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        assertNotNull(model);

        org.openscience.cdk.interfaces.IMoleculeSet som = model.getMoleculeSet();
        assertNotNull(som);
        assertEquals(1, som.getMoleculeCount());
        org.openscience.cdk.interfaces.IMolecule m = som.getMolecule(0);
        assertNotNull(m);
        assertEquals(9, m.getAtomCount());
        assertEquals(9, m.getBondCount());
    }
    
    /**
     * @cdk.bug 1542467
     */
    public void testBug1542467() throws Exception {
        String filename = "data/mdl/Strychnine_nichtOK.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
        assertTrue(((IAtomContainer)containersList.get(0)).getAtomCount() > 0);
        assertTrue(((IAtomContainer)containersList.get(0)).getBondCount() > 0);
    }
    
    public void testReadProton() throws Exception {
    	String mdl =
            "proton.mol\n" +
            "\n" +
            "\n" +
            "  1  0  0  0  0                 1\n" +
            "   -0.0073   -0.5272    0.9655 H   0  3  0  0  0\n";
    	MDLReader reader = new MDLReader(new StringReader(mdl), Mode.STRICT);
    	Molecule mol = (Molecule)reader.read(new Molecule());
    	assertNotNull(mol);
    	assertEquals(1, mol.getAtomCount());
    	assertEquals(0, mol.getBondCount());
    	IAtom atom = mol.getAtom(0);
    	assertEquals(1, atom.getFormalCharge().intValue());
    }
    
    /**
     * The corrupt file is really ok; it is just not V2000 material.
     */
    public void testSDF() throws Exception {
        String filename = "data/mdl/prev2000.sd";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(2, containersList.size());
        assertEquals(39, ((IAtomContainer)containersList.get(0)).getAtomCount());
        assertEquals(41, ((IAtomContainer)containersList.get(0)).getBondCount());
        assertEquals(29, ((IAtomContainer)containersList.get(1)).getAtomCount());
        assertEquals(28, ((IAtomContainer)containersList.get(1)).getBondCount());
    }
    
    public void testEmptyString() throws Exception {
    	String emptyString = "";
    	MDLReader reader = new MDLReader(new StringReader(emptyString), Mode.STRICT);
    	IMolecule mol = (IMolecule)reader.read(new NNMolecule());
    	assertNull(mol);
    }
    
    /**
     * @cdk.bug 1826577
     */
    public void testHisotopes() throws Exception {
        String filename = "data/mdl/hisotopes.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertFalse(((IAtomContainer)containersList.get(0)).getAtom(1) instanceof PseudoAtom);
        assertFalse(((IAtomContainer)containersList.get(0)).getAtom(1) instanceof PseudoAtom);
    }        
}
