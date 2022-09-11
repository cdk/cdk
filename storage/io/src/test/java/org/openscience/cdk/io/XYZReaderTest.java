/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.io;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * TestCase for the reading XYZ files using a test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.XYZReader
 */
class XYZReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(XYZReaderTest.class);

    @BeforeAll
    static void setup() throws Exception {
        setSimpleChemObjectReader(new XYZReader(), "viagra.xyz");
    }

    @Test
    void testAccepts() {
        XYZReader reader = new XYZReader();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
    }

    @Test
    void testViagra() throws Exception {
        String filename = "viagra.xyz";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        XYZReader reader = new XYZReader(ins);
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
        Assertions.assertEquals(63, m.getAtomCount());
        Assertions.assertEquals(0, m.getBondCount());

        Assertions.assertEquals("N", m.getAtom(0).getSymbol());
        Assertions.assertNotNull(m.getAtom(0).getPoint3d());
        Assertions.assertEquals(-3.4932, m.getAtom(0).getPoint3d().x, 0.0001);
        Assertions.assertEquals(-1.8950, m.getAtom(0).getPoint3d().y, 0.0001);
        Assertions.assertEquals(0.1795, m.getAtom(0).getPoint3d().z, 0.0001);
    }

    @Test
    void testComment() throws Exception {
        String filename = "viagra_withComment.xyz";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        XYZReader reader = new XYZReader(ins);
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
        Assertions.assertEquals(63, m.getAtomCount());
        Assertions.assertEquals(0, m.getBondCount());

        // atom 63: H    3.1625    3.1270   -0.9362
        Assertions.assertEquals("H", m.getAtom(62).getSymbol());
        Assertions.assertNotNull(m.getAtom(62).getPoint3d());
        Assertions.assertEquals(3.1625, m.getAtom(62).getPoint3d().x, 0.0001);
        Assertions.assertEquals(3.1270, m.getAtom(62).getPoint3d().y, 0.0001);
        Assertions.assertEquals(-0.9362, m.getAtom(62).getPoint3d().z, 0.0001);
    }

}
