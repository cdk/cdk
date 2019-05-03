/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * TestCase for reading CML files using a few test files
 * in data/cmltest as found in the Jmol distribution
 * (<a href="http://www.jmol.org/">http://www.jmol.org/</a>).
 *
 * @cdk.module test-io
 */
public class JmolTest extends CDKTestCase {

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(JmolTest.class);

    /**
     * Now come the actual tests...
     */

    /**
     * Special CML characteristics:
     * <ul><li> &lt;crystal></li></ul>
     */
    @Test
    public void testEstron() throws Exception {
        String filename = "data/cml/estron.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        // test the molecule
        ICrystal crystal = model.getCrystal();
        Assert.assertNotNull(crystal);
        Assert.assertEquals(4 * 42, crystal.getAtomCount());
        Assert.assertTrue(GeometryUtil.has3DCoordinates(crystal));
        // test the cell axes
        Vector3d a = crystal.getA();
        Assert.assertTrue(a.x != 0.0);
        Vector3d b = crystal.getB();
        Assert.assertTrue(b.y != 0.0);
        Vector3d c = crystal.getC();
        Assert.assertTrue(c.z != 0.0);
    }

    /**
     * Special CML characteristics:
     * - Jmol Animation
     */
    @Ignore("It is broken, but not used, AFAIK")
    public void testAnimation() throws Exception {
        String filename = "data/cml/SN1_reaction.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(34, seq.getChemModelCount());
        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        IAtomContainerSet som = model.getMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(1, som.getAtomContainerCount());

        // test the molecule
        IAtomContainer mol = som.getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(mol.getAtomCount(), 25);
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
    }

    /**
     * No special CML code, just regression test for Jmol releases
     */
    @Test
    public void testMethanolTwo() throws Exception {
        String filename = "data/cml/methanol2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        //logger.debug("NO sequences: " + chemFile.getChemSequenceCount());
        IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        //logger.debug("NO models: " + seq.getChemModelCount());
        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        Assert.assertEquals(1, model.getMoleculeSet().getAtomContainerCount());

        // test the molecule
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(mol.getAtomCount(), 6);
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
    }

    /**
     * No special CML code, just regression test for Jmol releases
     */
    @Test
    public void testMethanolOne() throws Exception {
        String filename = "data/cml/methanol1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        reader.close();

        // test the resulting ChemFile content
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        //logger.debug("NO sequences: " + chemFile.getChemSequenceCount());
        IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        //logger.debug("NO models: " + seq.getChemModelCount());
        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        IAtomContainerSet som = model.getMoleculeSet();
        Assert.assertEquals(1, som.getAtomContainerCount());

        // test the molecule
        IAtomContainer mol = som.getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(mol.getAtomCount(), 6);
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
    }

}
