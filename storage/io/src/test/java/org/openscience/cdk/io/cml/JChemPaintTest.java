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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * TestCase for reading CML files using a few test files
 * in data/cmltest as found in the JChemPaint distribution
 * (http://jchempaint.sf.org/).
 *
 * @cdk.module test-io
 */
public class JChemPaintTest extends CDKTestCase {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(JChemPaintTest.class);

    /**
     * This one tests a CML2 file.
     */
    @Test
    public void testSalt() throws Exception {
        String filename = "data/cml/COONa.cml";
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
        Assert.assertEquals(4, mol.getAtomCount());
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
    }

    /**
     * This one tests reading of output from the WWMM matrix (KEGG collection).
     */
    @Test
    public void testWWMMOutput() throws Exception {
        String filename = "data/cml/keggtest.cml";
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
        IAtomContainerSet moleculeSet = model.getMoleculeSet();
        Assert.assertNotNull(moleculeSet);
        Assert.assertEquals(1, moleculeSet.getAtomContainerCount());

        // test the molecule
        IAtomContainer mol = moleculeSet.getAtomContainer(0);
        Assert.assertNotNull(mol);
        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertTrue(GeometryUtil.has3DCoordinates(mol));
    }
}
