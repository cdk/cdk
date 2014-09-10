/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.cml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestCase for the reading CML 2 files using a few test files
 * in data/cmltest.
 *
 * @cdk.module test-libiocml
 * @cdk.require java1.5+
 */
public class CML2Test extends CDKTestCase {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(CML2Test.class);

    @Test
    public void testFile3() throws Exception {
        String filename = "data/cml/3.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(chemFile).get(0);

        for (int i = 0; i <= 3; i++) {
            Assert.assertFalse("Bond " + (i + 1) + " is not aromatic in the file",
                    mol.getBond(i).getFlag(CDKConstants.ISAROMATIC));
        }
        for (int i = 4; i <= 9; i++) {
            Assert.assertTrue("Bond " + (i + 1) + " is aromatic in the file",
                    mol.getBond(i).getFlag(CDKConstants.ISAROMATIC));
        }
    }

    /**
     * @cdk.bug 2114987
     */
    @Test
    public void testCMLTestCase() throws Exception {
        String filename = "data/cml/olaCmlAtomType.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = new ChemFile();
        chemFile = (IChemFile) reader.read(chemFile);
        reader.close();
        IAtomContainer container = ChemFileManipulator.getAllAtomContainers(chemFile).get(0);
        for (IAtom atom : container.atoms()) {
            Assert.assertEquals(CDKConstants.UNSET, atom.getImplicitHydrogenCount());
        }
    }

    @Test
    public void testCOONa() throws Exception {
        String filename = "data/cml/COONa.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(4, mol.getAtomCount());
        Assert.assertEquals(2, mol.getBondCount());
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
        Assert.assertTrue(!GeometryUtil.has2DCoordinates(mol));

        Iterator<IAtom> atoms = mol.atoms().iterator();
        while (atoms.hasNext()) {
            org.openscience.cdk.interfaces.IAtom atom = atoms.next();
            if (atom.getSymbol().equals("Na")) Assert.assertEquals(+1, atom.getFormalCharge().intValue());
        }
    }

    @Test
    public void testNitrate() throws Exception {
        String filename = "data/cml/nitrate.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(4, mol.getAtomCount());
        Assert.assertEquals(3, mol.getBondCount());
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
        Assert.assertTrue(!GeometryUtil.has2DCoordinates(mol));

        Iterator<IAtom> atoms = mol.atoms().iterator();
        while (atoms.hasNext()) {
            org.openscience.cdk.interfaces.IAtom atom = atoms.next();
            if (atom.getSymbol().equals("N")) Assert.assertEquals(+1, atom.getFormalCharge().intValue());
        }
    }

    @Test
    public void testCMLOK1() throws Exception {
        String filename = "data/cml/cs2a.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(38, mol.getAtomCount());
        Assert.assertEquals(48, mol.getBondCount());
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
        Assert.assertFalse(GeometryUtil.has2DCoordinates(mol));
    }

    @Test
    public void testCMLOK2() throws Exception {
        String filename = "data/cml/cs2a.mol.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(38, mol.getAtomCount());
        Assert.assertEquals(29, mol.getBondCount());
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
        Assert.assertFalse(GeometryUtil.has2DCoordinates(mol));
    }

    @Test
    public void testCMLOK3() throws Exception {
        String filename = "data/cml/nsc2dmol.1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(13, mol.getAtomCount());
        Assert.assertEquals(12, mol.getBondCount());
        Assert.assertFalse(GeometryUtil.has3DCoordinates(mol));
        Assert.assertTrue(GeometryUtil.has2DCoordinates(mol));
    }

    @Test
    public void testCMLOK4() throws Exception {
        String filename = "data/cml/nsc2dmol.2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(13, mol.getAtomCount());
        Assert.assertEquals(12, mol.getBondCount());
        Assert.assertFalse(GeometryUtil.has3DCoordinates(mol));
        Assert.assertTrue(GeometryUtil.has2DCoordinates(mol));
    }

    @Test
    public void testCMLOK5() throws Exception {
        String filename = "data/cml/nsc2dmol.a1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(13, mol.getAtomCount());
        Assert.assertEquals(12, mol.getBondCount());
        Assert.assertFalse(GeometryUtil.has3DCoordinates(mol));
        Assert.assertTrue(GeometryUtil.has2DCoordinates(mol));
    }

    @Test
    public void testCMLOK6() throws Exception {
        String filename = "data/cml/nsc2dmol.a2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(13, mol.getAtomCount());
        Assert.assertEquals(12, mol.getBondCount());
        Assert.assertFalse(GeometryUtil.has3DCoordinates(mol));
        Assert.assertTrue(GeometryUtil.has2DCoordinates(mol));
    }

    @Test
    public void testCMLOK7() throws Exception {
        String filename = "data/cml/nsc3dcml.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(27, mol.getAtomCount());
        Assert.assertEquals(27, mol.getBondCount());
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
        Assert.assertFalse(GeometryUtil.has2DCoordinates(mol));
    }

    @Test
    public void testCMLOK8() throws Exception {
        String filename = "data/cml/nsc2dcml.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(15, mol.getAtomCount());
        Assert.assertEquals(14, mol.getBondCount());
        Assert.assertFalse(GeometryUtil.has3DCoordinates(mol));
        Assert.assertTrue(GeometryUtil.has2DCoordinates(mol));
    }

    @Test
    public void testCMLOK9() throws Exception {
        String filename = "data/cml/nsc3dmol.1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(15, mol.getAtomCount());
        Assert.assertEquals(15, mol.getBondCount());
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
        Assert.assertFalse(GeometryUtil.has2DCoordinates(mol));
    }

    @Test
    public void testCMLOK10() throws Exception {
        String filename = "data/cml/nsc3dmol.2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(15, mol.getAtomCount());
        Assert.assertEquals(15, mol.getBondCount());
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
        Assert.assertFalse(GeometryUtil.has2DCoordinates(mol));
    }

    @Test
    public void testCMLOK11() throws Exception {
        String filename = "data/cml/nsc3dmol.a1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(15, mol.getAtomCount());
        Assert.assertEquals(15, mol.getBondCount());
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
        Assert.assertFalse(GeometryUtil.has2DCoordinates(mol));
    }

    @Test
    public void testCMLOK12() throws Exception {
        String filename = "data/cml/nsc3dmol.a2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(15, mol.getAtomCount());
        Assert.assertEquals(15, mol.getBondCount());
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
        Assert.assertFalse(GeometryUtil.has2DCoordinates(mol));
    }

    /**
     * This test tests whether the CMLReader is able to ignore the CMLSpect part
     * of a CML file, while extracting the molecule.
     */
    @Test
    public void testCMLSpectMolExtraction() throws Exception {
        String filename = "data/cml/molAndspect.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getMoleculeSet().getAtomContainerCount(), 1);

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(17, mol.getAtomCount());
        Assert.assertEquals(18, mol.getBondCount());
        Assert.assertFalse(GeometryUtil.has3DCoordinates(mol));
        Assert.assertTrue(GeometryUtil.has2DCoordinates(mol));
    }

    /**
     * This test tests whether the CMLReader is able to ignore the CMLReaction part
     * of a CML file, while extracting the reaction.
     */
    @Test
    public void testCMLReaction() throws Exception {
        String filename = "data/cml/reaction.2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getReactionSet().getReactionCount(), 1);

        // test the reaction
        IReaction reaction = model.getReactionSet().getReaction(0);
        Assert.assertNotNull(reaction);
        Assert.assertEquals("react", reaction.getReactants().getAtomContainer(0).getID());
        Assert.assertEquals("product", reaction.getProducts().getAtomContainer(0).getID());
        Assert.assertEquals("a14293164", reaction.getReactants().getAtomContainer(0).getAtom(0).getID());
        Assert.assertEquals(6, reaction.getProducts().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(6, reaction.getReactants().getAtomContainer(0).getAtomCount());
    }

    /**
     * This test tests whether the CMLReader is able to ignore the CMLReaction part
     * of a CML file, while extracting the reaction.
     */
    @Test
    public void testCMLReactionWithAgents() throws Exception {
        String filename = "data/cml/reaction.1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getReactionSet().getReactionCount(), 1);

        // test the reaction
        IReaction reaction = model.getReactionSet().getReaction(0);
        Assert.assertNotNull(reaction);
        Assert.assertEquals("react", reaction.getReactants().getAtomContainer(0).getID());
        Assert.assertEquals("product", reaction.getProducts().getAtomContainer(0).getID());
        Assert.assertEquals("water", reaction.getAgents().getAtomContainer(0).getID());
        Assert.assertEquals("H+", reaction.getAgents().getAtomContainer(1).getID());
        Assert.assertEquals(6, reaction.getProducts().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(6, reaction.getReactants().getAtomContainer(0).getAtomCount());
    }

    /**
     * This test tests whether the CMLReader is able to ignore the CMLReaction part
     * of a CML file, while extracting the reaction.
     */
    @Test
    public void testCMLReactionList() throws Exception {
        String filename = "data/cml/reactionList.1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(2, model.getReactionSet().getReactionCount());
        Assert.assertEquals("1.3.2", model.getReactionSet().getReaction(0).getID());

        // test the reaction
        IReaction reaction = model.getReactionSet().getReaction(0);
        Assert.assertNotNull(reaction);
        Assert.assertEquals("actey", reaction.getReactants().getAtomContainer(0).getID());
        Assert.assertEquals("a14293164", reaction.getReactants().getAtomContainer(0).getAtom(0).getID());
        Assert.assertEquals(6, reaction.getProducts().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(6, reaction.getReactants().getAtomContainer(0).getAtomCount());
    }

    /**
     * @cdk.bug 1560486
     */
    @Test
    public void testCMLWithFormula() throws Exception {
        String filename = "data/cml/cmlWithFormula.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals("a", mol.getID());
        Assert.assertEquals("a1", mol.getAtom(0).getID());
        Assert.assertEquals(27, mol.getAtomCount());
        Assert.assertEquals(32, mol.getBondCount());
    }

    /**
     * Only Molecule with concise MolecularFormula
     */
    @Test
    public void testCMLConciseFormula() throws Exception {
        String filename = "data/cml/cmlConciseFormula.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);

        // FIXME: REACT: It should return two different formulas
        Assert.assertEquals("[C 18 H 21 Cl 2 Mn 1 N 5 O 1]", mol.getProperty(CDKConstants.FORMULA).toString());
    }

    /**
     * Only Molecule with concise MolecularFormula
     */
    @Test
    public void testCMLConciseFormula2() throws Exception {
        String filename = "data/cml/cmlConciseFormula2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(seq.getChemModelCount(), 1);
        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);

        // FIXME: REACT: It should return two different formulas
        Assert.assertEquals("[C 18 H 21 Cl 2 Mn 1 N 5 O 1, C 4 H 10]", mol.getProperty(CDKConstants.FORMULA).toString());
    }

    /**
     * This test tests whether the CMLReader is able to ignore the CMLReaction part
     * of a CML file, while extracting the reaction.
     */
    @Test
    public void testCMLScheme1() throws Exception {
        String filename = "data/cml/reactionScheme.1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        // test reaction
        Assert.assertEquals(4, model.getReactionSet().getReactionCount());
        String[] idReaction = {"r1", "r2", "r3", "r4"};
        String[] idReactants = {"A", "B", "A", "F"};
        String[] idProducts = {"B", "C", "F", "G"};
        for (int i = 0; i < idReaction.length; i++) {
            IReaction reaction = model.getReactionSet().getReaction(i);
            Assert.assertEquals(idReaction[i], reaction.getID());
            // test molecule
            Assert.assertEquals(1, reaction.getProducts().getAtomContainerCount());
            Assert.assertEquals(idProducts[i], reaction.getProducts().getAtomContainer(0).getID());

            Assert.assertEquals(1, reaction.getReactants().getAtomContainerCount());
            Assert.assertEquals(idReactants[i], reaction.getReactants().getAtomContainer(0).getID());
        }
    }

    /**
     * This test tests whether the CMLReader is able to ignore the CMLReaction part
     * of a CML file, while extracting the reaction.
     */
    @Test
    public void testCMLScheme2() throws Exception {
        String filename = "data/cml/reactionScheme.2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        // test reaction
        Assert.assertEquals(2, model.getReactionSet().getReactionCount());
        String[] idReaction = {"r1", "r2"};
        String[] idReactants = {"A", "B"};
        String[] idProducts = {"B", "C"};
        for (int i = 0; i < idReaction.length; i++) {
            IReaction reaction = model.getReactionSet().getReaction(i);
            Assert.assertEquals(idReaction[i], reaction.getID());
            // test molecule
            Assert.assertEquals(1, reaction.getProducts().getAtomContainerCount());
            Assert.assertEquals(idProducts[i], reaction.getProducts().getAtomContainer(0).getID());

            Assert.assertEquals(1, reaction.getReactants().getAtomContainerCount());
            Assert.assertEquals(idReactants[i], reaction.getReactants().getAtomContainer(0).getID());
        }
    }

    /**
     * This test tests whether the CMLReader is able to ignore the CMLReaction part
     * of a CML file, while extracting the reaction.
     */
    @Test
    public void testCMLSchemeStepList1() throws Exception {
        String filename = "data/cml/reactionSchemeStepList.1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        // test reaction
        Assert.assertEquals(4, model.getReactionSet().getReactionCount());
        String[] idReaction = {"r1.1", "r1.2", "r2.1", "r2.2"};
        String[] idReactants = {"A", "B", "A", "D"};
        String[] idProducts = {"B", "C", "D", "E"};
        for (int i = 0; i < idReaction.length; i++) {
            IReaction reaction = model.getReactionSet().getReaction(i);
            Assert.assertEquals(idReaction[i], reaction.getID());
            // test molecule
            Assert.assertEquals(1, reaction.getProducts().getAtomContainerCount());
            Assert.assertEquals(idProducts[i], reaction.getProducts().getAtomContainer(0).getID());

            Assert.assertEquals(1, reaction.getReactants().getAtomContainerCount());
            Assert.assertEquals(idReactants[i], reaction.getReactants().getAtomContainer(0).getID());
        }

    }

    /**
     * This test tests whether the CMLReader is able to ignore the CMLReaction part
     * of a CML file, while extracting the reaction.
     */
    @Test
    public void testCMLStepList() throws Exception {
        String filename = "data/cml/reactionStepList.1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        // test reaction
        Assert.assertEquals(3, model.getReactionSet().getReactionCount());
        String[] idReaction = {"r1", "r2", "r3"};
        String[] idReactants = {"A", "B", "C"};
        String[] idProducts = {"B", "C", "D"};
        for (int i = 0; i < idReaction.length; i++) {
            IReaction reaction = model.getReactionSet().getReaction(i);
            Assert.assertEquals(idReaction[i], reaction.getID());
            // test molecule
            Assert.assertEquals(1, reaction.getProducts().getAtomContainerCount());
            Assert.assertEquals(idProducts[i], reaction.getProducts().getAtomContainer(0).getID());

            Assert.assertEquals(1, reaction.getReactants().getAtomContainerCount());
            Assert.assertEquals(idReactants[i], reaction.getReactants().getAtomContainer(0).getID());
        }

    }

    /**
     * This test tests whether the CMLReader is able to read a reactionscheme object with
     * references to list of molecules.
     */
    @Test
    public void testCMLSchemeMoleculeSet() throws Exception {
        String filename = "data/cml/reactionSchemeMoleculeSet.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        // test reaction
        Assert.assertEquals(1, model.getReactionSet().getReactionCount());
        String[] idReaction = {"react_1"};
        String[] idReactants = {"A"};
        String[] idProducts = {"B", "C"};

        IReaction reaction = model.getReactionSet().getReaction(0);
        Assert.assertEquals(idReaction[0], reaction.getID());
        // test molecule
        Assert.assertEquals(2, reaction.getProducts().getAtomContainerCount());
        Assert.assertEquals(idProducts[0], reaction.getProducts().getAtomContainer(0).getID());
        Assert.assertEquals("C 9 H 20 N 1", ((ArrayList<String>) reaction.getProducts().getAtomContainer(0)
                .getProperty(CDKConstants.FORMULA)).get(0));
        Assert.assertEquals(idProducts[1], reaction.getProducts().getAtomContainer(1).getID());

        Assert.assertEquals(1, reaction.getReactants().getAtomContainerCount());
        Assert.assertEquals(idReactants[0], reaction.getReactants().getAtomContainer(0).getID());
        Assert.assertEquals("C 28 H 60 N 1", ((ArrayList<String>) reaction.getReactants().getAtomContainer(0)
                .getProperty(CDKConstants.FORMULA)).get(0));
    }

    /**
     * @cdk.bug 2697568
     */
    @Test
    public void testReadReactionWithPointersToMoleculeSet() throws Exception {
        String filename = "data/cml/AlanineTree.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = new ChemFile();
        chemFile = (IChemFile) reader.read(chemFile);
        reader.close();
        Assert.assertSame(chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0), chemFile
                .getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0).getReactants().getAtomContainer(0));
    }

    /**
     * @cdk.bug 2697568
     */
    @Test
    public void testBug2697568() throws Exception {
        String filename = "data/cml/AlanineTreeReverse.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = new ChemFile();
        chemFile = (IChemFile) reader.read(chemFile);
        reader.close();
        Assert.assertSame(chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0), chemFile
                .getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0).getReactants().getAtomContainer(0));
    }

    /**
     */
    @Test
    public void testReactionProperties() throws Exception {
        String filename = "data/cml/reaction.2.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = new ChemFile();
        chemFile = (IChemFile) reader.read(chemFile);
        reader.close();
        IReaction reaction = chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0);

        Assert.assertEquals("3", (String) reaction.getProperty("Ka"));
    }
}
