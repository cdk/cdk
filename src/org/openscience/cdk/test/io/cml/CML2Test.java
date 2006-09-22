/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.test.io.cml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.nonotify.NNChemFile;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestCase for the reading CML 2 files using a few test files
 * in data/cmltest.
 *
 * @cdk.module test-io
 * @cdk.require java1.5+
 */
public class CML2Test extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public CML2Test(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(CML2Test.class);
    }

    public void testCOONa() {
        String filename = "data/cml/COONa.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(4, mol.getAtomCount());
            assertEquals(2, mol.getBondCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
            assertTrue(!GeometryTools.has2DCoordinates(mol));
            
            org.openscience.cdk.interfaces.IAtom[] atoms = mol.getAtoms();
            for (int i=0; i<atoms.length; i++) {
            	org.openscience.cdk.interfaces.IAtom atom = atoms[i];
                if (atom.getSymbol().equals("Na")) 
                    assertEquals(+1, atom.getFormalCharge()); 
            }
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testNitrate() {
        String filename = "data/cml/nitrate.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(4, mol.getAtomCount());
            assertEquals(3, mol.getBondCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
            assertTrue(!GeometryTools.has2DCoordinates(mol));
            
            org.openscience.cdk.interfaces.IAtom[] atoms = mol.getAtoms();
            for (int i=0; i<atoms.length; i++) {
            	org.openscience.cdk.interfaces.IAtom atom = atoms[i];
                if (atom.getSymbol().equals("N")) 
                    assertEquals(+1, atom.getFormalCharge()); 
            }
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testCMLOK1() {
        String filename = "data/cml/cs2a.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(38, mol.getAtomCount());
            assertEquals(48, mol.getBondCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
            assertFalse(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testCMLOK2() {
        String filename = "data/cml/cs2a.mol.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(38, mol.getAtomCount());
            assertEquals(29, mol.getBondCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
            assertFalse(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testCMLOK3() {
        String filename = "data/cml/nsc2dmol.1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(13, mol.getAtomCount());
            assertEquals(12, mol.getBondCount());
            assertFalse(GeometryTools.has3DCoordinates(mol));
            assertTrue(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testCMLOK4() {
        String filename = "data/cml/nsc2dmol.2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(13, mol.getAtomCount());
            assertEquals(12, mol.getBondCount());
            assertFalse(GeometryTools.has3DCoordinates(mol));
            assertTrue(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testCMLOK5() {
        String filename = "data/cml/nsc2dmol.a1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(13, mol.getAtomCount());
            assertEquals(12, mol.getBondCount());
            assertFalse(GeometryTools.has3DCoordinates(mol));
            assertTrue(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testCMLOK6() {
        String filename = "data/cml/nsc2dmol.a2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(13, mol.getAtomCount());
            assertEquals(12, mol.getBondCount());
            assertFalse(GeometryTools.has3DCoordinates(mol));
            assertTrue(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testCMLOK7() {
        String filename = "data/cml/nsc3dcml.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(27, mol.getAtomCount());
            assertEquals(27, mol.getBondCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
            assertFalse(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testCMLOK8() {
        String filename = "data/cml/nsc2dcml.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(15, mol.getAtomCount());
            assertEquals(14, mol.getBondCount());
            assertFalse(GeometryTools.has3DCoordinates(mol));
            assertTrue(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testCMLOK9() {
        String filename = "data/cml/nsc3dmol.1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(15, mol.getAtomCount());
            assertEquals(15, mol.getBondCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
            assertFalse(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testCMLOK10() {
        String filename = "data/cml/nsc3dmol.2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(15, mol.getAtomCount());
            assertEquals(15, mol.getBondCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
            assertFalse(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testCMLOK11() {
        String filename = "data/cml/nsc3dmol.a1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(15, mol.getAtomCount());
            assertEquals(15, mol.getBondCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
            assertFalse(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testCMLOK12() {
        String filename = "data/cml/nsc3dmol.a2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(15, mol.getAtomCount());
            assertEquals(15, mol.getBondCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
            assertFalse(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    /**
     * This test tests wether the CMLReader is able to ignore the CMLSpect part
     * of a CML file, while extracting the molecule.
     */
    public void testCMLSpectMolExtraction() {
        String filename = "data/cml/molAndspect.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getMoleculeSet().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(17, mol.getAtomCount());
            assertEquals(18, mol.getBondCount());
            assertFalse(GeometryTools.has3DCoordinates(mol));
            assertTrue(GeometryTools.has2DCoordinates(mol));
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
    /**
     * This test tests wether the CMLReader is able to ignore the CMLReaction part
     * of a CML file, while extracting the reaction.
     */
    public void testCMLReaction() {
        String filename = "data/cml/reaction.1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getReactionSet().getReactionCount(), 1);

            // test the reaction
            IReaction reaction = model.getReactionSet().getReaction(0);
            assertNotNull(reaction);
            assertEquals(6, reaction.getProducts().getAtomContainer(0).getAtomCount());
            assertEquals(6, reaction.getReactants().getAtomContainer(0).getAtomCount());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    /**
     * @cdk.bug 1560486
     */
    public void testCMLWithFormula() {
        String filename = "data/cml/cmlWithFormula.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);

            IMolecule mol = model.getMoleculeSet().getMolecule(0);
            assertNotNull(mol);
            assertEquals(27, mol.getAtomCount());
            assertEquals(32, mol.getBondCount());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    /**
	 * @cdk.bug 1085912
	 */
	public void testSFBug1085912_1() throws Exception {
		String filename_pdb = "data/pdb/1CKV.pdb";
		String filename_cml = "data/cml/1CKV_1.cml";
	    InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename_pdb);
	    InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(filename_cml);
	    
	    try {
	    	/*1*/
		      IChemObjectReader reader = new PDBReader(ins1);
		      IChemFile chemFile1 = (IChemFile) reader.read(new NNChemFile());
		      IChemSequence seq1 = chemFile1.getChemSequence(0);
		      IChemModel model1 = seq1.getChemModel(0);
		      IAtomContainer container = model1.getMoleculeSet().getMolecule(0);
		      IBioPolymer polymer1 = (IBioPolymer)container;
		      int countchemFile1 = chemFile1.getChemSequenceCount();
		      int countseq1 = seq1.getChemModelCount();
		      int countmodel1 = model1.getMoleculeSet().getAtomContainerCount();
		      int countpolymer1 = polymer1.getAtomCount();
		      
	          
		      StringWriter writer = new StringWriter();
		      CMLWriter cmlWriter = new CMLWriter(writer);
		      cmlWriter.write(polymer1);
		      String cmlContent1 = writer.toString();
		      
		      
		    /*2*/
	          CMLReader reader2 = new CMLReader(new ByteArrayInputStream(cmlContent1.getBytes()));
		      IChemFile chemFil2 = (IChemFile)reader2.read(new NNChemFile());
		      IChemSequence seq2 = chemFil2.getChemSequence(0);
		      IChemModel model2 = seq2.getChemModel(0);
		      PDBPolymer polymer2 =  (PDBPolymer) model2.getMoleculeSet().getAtomContainer(0);


		      int countchemFile2 = chemFil2.getChemSequenceCount();
		      int countseq2 = seq2.getChemModelCount();
		      int countmodel2 = model2.getMoleculeSet().getAtomContainerCount();
		      int countpolymer2 = polymer2.getAtomCount();
		      
		      assertEquals(countchemFile1, countchemFile2);
//		      assertEquals(countseq1,countseq2); /*not the same because the pdb file has more models*/
		      assertEquals(countmodel1,countmodel2);
		      assertEquals(countpolymer1,countpolymer2);
		      

		     writer = new StringWriter();
		     cmlWriter = new CMLWriter(writer);
		      cmlWriter.write(polymer2);
		      String cmlContent2 = writer.toString();
		      
		      String conte1 = cmlContent1.substring(0, 1000);
		      String conte2 = cmlContent2.substring(0, 1000);
		      assertEquals(conte1,conte2);
		      
	    } catch (Exception ex) {
            fail(ex.getMessage());
		}
	    
	   
    }
    
}
