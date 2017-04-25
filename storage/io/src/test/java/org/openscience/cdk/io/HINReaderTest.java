/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestCase for the reading HIN mol files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.HINReader
 */
public class HINReaderTest extends SimpleChemObjectReaderTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(HINReaderTest.class);

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new HINReader(), "data/hin/benzene.hin");
    }

    @Test
    public void testAccepts() {
        Assert.assertTrue(chemObjectIO.accepts(ChemFile.class));
    }

    @Test
    public void testBenzene() throws Exception {
        String filename = "data/hin/benzene.hin";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        HINReader reader = new HINReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        IAtomContainerSet som = model.getMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(1, som.getAtomContainerCount());
        IAtomContainer m = som.getAtomContainer(0);
        Assert.assertNotNull(m);
        Assert.assertEquals(12, m.getAtomCount());
        // assertEquals(?, m.getBondCount());
    }

    @Test
    public void testMoleculeTwo() throws Exception {
        String filename = "data/hin/molecule2.hin";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        HINReader reader = new HINReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        IAtomContainerSet som = model.getMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(1, som.getAtomContainerCount());
        IAtomContainer m = som.getAtomContainer(0);
        Assert.assertNotNull(m);
        Assert.assertEquals(37, m.getAtomCount());
        // assertEquals(?, m.getBondCount());
    }

    @Test
    public void testMultiple() throws Exception {
        String filename = "data/hin/multiple.hin";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        HINReader reader = new HINReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        IAtomContainerSet som = model.getMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(3, som.getAtomContainerCount());
    }

    @Test
    public void testIsConnectedFromHINFile() throws Exception {
        String filename = "data/hin/connectivity1.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        IChemFile content = reader.read(new ChemFile());
        reader.close();
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = cList.get(0);
        Assert.assertEquals(57, ac.getAtomCount());
        Assert.assertEquals(59, ac.getBondCount());
    }

    /**
     * @cdk.bug 2984581
     * @throws Exception
     */
    @Test
    public void testAromaticRingsLine() throws Exception {
        String filename = "data/hin/bug2984581.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        IChemFile content = reader.read(new ChemFile());
        reader.close();
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        Assert.assertEquals(1, cList.size());
    }

    /**
     * @cdk.bug 2984581
     * @throws Exception
     */
    @Test
    public void testReadAromaticRingsKeyword() throws Exception {
        String filename = "data/hin/arorings.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        IChemFile content = reader.read(new ChemFile());
        reader.close();
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        Assert.assertEquals(1, cList.size());

        IAtomContainer mol = cList.get(0);
        Assert.assertTrue(mol.getAtom(0).getFlag(CDKConstants.ISAROMATIC));
        Assert.assertTrue(mol.getAtom(2).getFlag(CDKConstants.ISAROMATIC));
        Assert.assertTrue(mol.getAtom(3).getFlag(CDKConstants.ISAROMATIC));
        Assert.assertTrue(mol.getAtom(5).getFlag(CDKConstants.ISAROMATIC));
        Assert.assertTrue(mol.getAtom(4).getFlag(CDKConstants.ISAROMATIC));
        Assert.assertTrue(mol.getAtom(1).getFlag(CDKConstants.ISAROMATIC));

        Assert.assertTrue(mol.getAtom(7).getFlag(CDKConstants.ISAROMATIC));
        Assert.assertTrue(mol.getAtom(12).getFlag(CDKConstants.ISAROMATIC));
        Assert.assertTrue(mol.getAtom(11).getFlag(CDKConstants.ISAROMATIC));
        Assert.assertTrue(mol.getAtom(10).getFlag(CDKConstants.ISAROMATIC));
        Assert.assertTrue(mol.getAtom(9).getFlag(CDKConstants.ISAROMATIC));
        Assert.assertTrue(mol.getAtom(8).getFlag(CDKConstants.ISAROMATIC));

        // make sure that only the phenyl C's were marked as aromatic
        for (IAtom atom : mol.atoms()) {
            if (atom.getSymbol().equals("C"))
                Assert.assertTrue(atom.getSymbol() + " (index " + mol.indexOf(atom)
                        + ") was wrongly marked as aromatic", atom.getFlag(CDKConstants.ISAROMATIC));
        }

    }
}
