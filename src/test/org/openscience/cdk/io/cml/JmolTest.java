/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import javax.vecmath.Vector3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.CDKTestCase;

/**
 * TestCase for the reading CML files using a few test files
 * in data/cmltest as found in the Jmol distribution
 * (<a href="http://www.jmol.org/">http://www.jmol.org/</a>).
 *
 * @cdk.module test-io
 */
public class JmolTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public JmolTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(JmolTest.class);
    }

    /**
     * Now come the actual tests...
     */


    /**
     * Special CML characteristics:
     * <ul><li> &lt;crystal></li></ul>
     */
    public void testEstron() throws Exception {
        String filename = "data/cmltest/estron.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

        // test the resulting ChemFile content
        assertNotNull(chemFile);
        assertEquals(1, chemFile.getChemSequenceCount());
        IChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        assertEquals(1, seq.getChemModelCount());
        IChemModel model = seq.getChemModel(0);
        assertNotNull(model);

        // test the molecule
        ICrystal crystal = model.getCrystal();
        assertNotNull(crystal);
        assertEquals(4*42, crystal.getAtomCount());
        assertTrue(GeometryTools.has3DCoordinates(crystal));
        // test the cell axes
        Vector3d a = crystal.getA();
        assertTrue(a.x != 0.0);
        Vector3d b = crystal.getB();
        assertTrue(b.y != 0.0);
        Vector3d c = crystal.getC();
        assertTrue(c.z != 0.0);
    }

    /**
     * Special CML characteristics:
     * - Jmol Animation
     */
    public void testAnimation() throws Exception {
        String filename = "data/cmltest/SN1_reaction.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

        // test the resulting ChemFile content
        assertNotNull(chemFile);
        assertEquals(1, chemFile.getChemSequenceCount());
        IChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        assertEquals(34, seq.getChemModelCount());
        IChemModel model = seq.getChemModel(0);
        assertNotNull(model);
        IMoleculeSet som = model.getMoleculeSet();
        assertNotNull(som);
        assertEquals(1, som.getMoleculeCount());

        // test the molecule
        IMolecule mol = som.getMolecule(0);
        assertNotNull(mol);
        assertEquals(mol.getAtomCount(), 25);
        assertTrue(GeometryTools.has3DCoordinates(mol));
    }


    /**
     * No special CML code, just regression test for Jmol releases
     */
    public void testMethanolTwo() throws Exception {
        String filename = "data/cmltest/methanol2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

        // test the resulting ChemFile content
        assertNotNull(chemFile);
        assertEquals(1, chemFile.getChemSequenceCount());
        //logger.debug("NO sequences: " + chemFile.getChemSequenceCount());
        IChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        assertEquals(1, seq.getChemModelCount());
        //logger.debug("NO models: " + seq.getChemModelCount());
        IChemModel model = seq.getChemModel(0);
        assertNotNull(model);
        assertEquals(1, model.getMoleculeSet().getMoleculeCount());

        // test the molecule
        IMolecule mol = model.getMoleculeSet().getMolecule(0);
        assertNotNull(mol);
        assertEquals(mol.getAtomCount(), 6);
        assertTrue(GeometryTools.has3DCoordinates(mol));
    }

    /**
     * No special CML code, just regression test for Jmol releases
     */
    public void testMethanolOne() throws Exception {
        String filename = "data/cmltest/methanol1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

        // test the resulting ChemFile content
        assertNotNull(chemFile);
        assertEquals(1, chemFile.getChemSequenceCount());
        //logger.debug("NO sequences: " + chemFile.getChemSequenceCount());
        IChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        assertEquals(1, seq.getChemModelCount());
        //logger.debug("NO models: " + seq.getChemModelCount());
        IChemModel model = seq.getChemModel(0);
        assertNotNull(model);
        IMoleculeSet som = model.getMoleculeSet();
        assertEquals(1, som.getMoleculeCount());

        // test the molecule
        IMolecule mol = som.getMolecule(0);
        assertNotNull(mol);
        assertEquals(mol.getAtomCount(), 6);
        assertTrue(GeometryTools.has3DCoordinates(mol));
    }

}
