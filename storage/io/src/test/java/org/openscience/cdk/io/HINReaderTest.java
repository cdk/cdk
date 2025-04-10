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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestCase for the reading HIN mol files using one test file.
 *
 *
 * @see org.openscience.cdk.io.HINReader
 */
class HINReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(HINReaderTest.class);

    @BeforeAll
    static void setup() {
        setSimpleChemObjectReader(new HINReader(), "org/openscience/cdk/io/benzene.hin");
    }

    @Test
    void testAccepts() {
        Assertions.assertTrue(chemObjectIO.accepts(ChemFile.class));
    }

    @Test
    void testBenzene() throws Exception {
        String filename = "benzene.hin";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        HINReader reader = new HINReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);

        IAtomContainerSet som = model.getMoleculeSet();
        Assertions.assertNotNull(som);
        Assertions.assertEquals(1, som.getAtomContainerCount());
        IAtomContainer m = som.getAtomContainer(0);
        Assertions.assertNotNull(m);
        Assertions.assertEquals(12, m.getAtomCount());
        // assertEquals(?, m.getBondCount());
    }

    @Test
    void testMoleculeTwo() throws Exception {
        String filename = "molecule2.hin";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        HINReader reader = new HINReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);

        IAtomContainerSet som = model.getMoleculeSet();
        Assertions.assertNotNull(som);
        Assertions.assertEquals(1, som.getAtomContainerCount());
        IAtomContainer m = som.getAtomContainer(0);
        Assertions.assertNotNull(m);
        Assertions.assertEquals(37, m.getAtomCount());
        // assertEquals(?, m.getBondCount());
    }

    @Test
    void testMultiple() throws Exception {
        String filename = "multiple.hin";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        HINReader reader = new HINReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);

        IAtomContainerSet som = model.getMoleculeSet();
        Assertions.assertNotNull(som);
        Assertions.assertEquals(3, som.getAtomContainerCount());
    }

    @Test
    void testIsConnectedFromHINFile() throws Exception {
        String filename = "connectivity1.hin";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        IChemFile content = reader.read(new ChemFile());
        reader.close();
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = cList.get(0);
        Assertions.assertEquals(57, ac.getAtomCount());
        Assertions.assertEquals(59, ac.getBondCount());
    }

    /**
     * @cdk.bug 2984581
     * @throws Exception
     */
    @Test
    void testAromaticRingsLine() throws Exception {
        String filename = "bug2984581.hin";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        IChemFile content = reader.read(new ChemFile());
        reader.close();
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        Assertions.assertEquals(1, cList.size());
    }

    /**
     * @cdk.bug 2984581
     * @throws Exception
     */
    @Test
    void testReadAromaticRingsKeyword() throws Exception {
        String filename = "arorings.hin";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        IChemFile content = reader.read(new ChemFile());
        reader.close();
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        Assertions.assertEquals(1, cList.size());

        IAtomContainer mol = cList.get(0);
        Assertions.assertTrue(mol.getAtom(0).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getAtom(2).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getAtom(3).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getAtom(5).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getAtom(4).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getAtom(1).getFlag(IChemObject.AROMATIC));

        Assertions.assertTrue(mol.getAtom(7).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getAtom(12).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getAtom(11).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getAtom(10).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getAtom(9).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getAtom(8).getFlag(IChemObject.AROMATIC));

        // make sure that only the phenyl C's were marked as aromatic
        for (IAtom atom : mol.atoms()) {
            if (atom.getAtomicNumber() == IElement.C)
                Assertions.assertTrue(atom.getFlag(IChemObject.AROMATIC), atom.getSymbol() + " (index " + mol.indexOf(atom)
                        + ") was wrongly marked as aromatic");
        }

    }
}
