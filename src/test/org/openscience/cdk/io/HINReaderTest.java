/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestCase for the reading HIN mol files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.HINReader
 */
public class HINReaderTest extends SimpleChemObjectReaderTest {

    private static LoggingTool logger;
    
    @BeforeClass public static void setup() {
        logger = new LoggingTool(HINReaderTest.class);
        setSimpleChemObjectReader(new HINReader(), "data/hin/benzene.hin");
    }

    @Test public void testAccepts() {
    	Assert.assertTrue(chemObjectIO.accepts(ChemFile.class));
    }

    @Test public void testBenzene() throws Exception {
        String filename = "data/hin/benzene.hin";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        HINReader reader = new HINReader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        org.openscience.cdk.interfaces.IMoleculeSet som = model.getMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(1, som.getMoleculeCount());
        org.openscience.cdk.interfaces.IMolecule m = som.getMolecule(0);
        Assert.assertNotNull(m);
        Assert.assertEquals(12, m.getAtomCount());
        // assertEquals(?, m.getBondCount());
    }

    @Test public void testMoleculeTwo() throws Exception {
        String filename = "data/hin/molecule2.hin";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        HINReader reader = new HINReader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        org.openscience.cdk.interfaces.IMoleculeSet som = model.getMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(1, som.getMoleculeCount());
        org.openscience.cdk.interfaces.IMolecule m = som.getMolecule(0);
        Assert.assertNotNull(m);
        Assert.assertEquals(37, m.getAtomCount());
        // assertEquals(?, m.getBondCount());
    }

    @Test public void testMultiple() throws Exception {
        String filename = "data/hin/multiple.hin";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        HINReader reader = new HINReader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        org.openscience.cdk.interfaces.IMoleculeSet som = model.getMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(3, som.getMoleculeCount());
    }

    @Test public void testIsConnectedFromHINFile() throws CDKException {
        String filename = "data/hin/connectivity1.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        IChemFile content = (IChemFile) reader.read(new ChemFile());
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = cList.get(0);
        Assert.assertEquals(57, ac.getAtomCount());
        Assert.assertEquals(59, ac.getBondCount());
    }
}
