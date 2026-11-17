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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * TestCase for the reading XYZ files using a test file.
 *
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
    void testChargeRoundTrip() throws Exception {
        IAtomContainer molecule = new AtomContainer();
        Atom oxygen = new Atom("O", new Point3d(0, 0, 0));
        oxygen.setCharge(-0.834);
        molecule.addAtom(oxygen);
        Atom hydrogen = new Atom("H", new Point3d(1, 0, 0));
        hydrogen.setCharge(0.417);
        molecule.addAtom(hydrogen);
        StringWriter output = new StringWriter();
        try (XYZWriter writer = new XYZWriter(output)) {
            writer.write(molecule);
        }
        try (XYZReader reader = new XYZReader(new StringReader(output.toString()))) {
            ChemFile file = reader.read(new ChemFile());
            IAtomContainer result = file.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
            Assertions.assertEquals(-0.834, result.getAtom(0).getCharge());
            Assertions.assertEquals(0.417, result.getAtom(1).getCharge());
        }
    }

    @Test
    void testOptionalChargeAndVector() throws Exception {
        String xyz = "3\noptional charges and vectors\n"
                + "O 0 0 0 -0.834 1 2 3\n"
                + "H 1 0 0\n"
                + "H 0 1 0 4 5 6\n";
        try (XYZReader reader = new XYZReader(new StringReader(xyz))) {
            ChemFile file = reader.read(new ChemFile());
            IAtomContainer molecule = file.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
            Assertions.assertEquals(-0.834, molecule.getAtom(0).getCharge());
            Assertions.assertEquals(0.0, molecule.getAtom(1).getCharge());
            Assertions.assertEquals(0.0, molecule.getAtom(2).getCharge());
        }
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

    @Test
    void testSingleFrameWithTrailingBlankLinesStringReader() throws Exception {
        String xyz = "1\nhelium\nHe 0.0 0.0 0.0\n\n\n";
        try (XYZReader reader = new XYZReader(new StringReader(xyz))) {
            assertFrames(reader.read(new ChemFile()), 1);
        }
    }

    @Test
    void testTwoFramesWithTrailingBlankLinesStringReader() throws Exception {
        String xyz = "1\nhelium\nHe 0.0 0.0 0.0\n"
                   + "2\nhydrogen\nH 0.0 0.0 0.0\nH 0.0 0.0 0.74\n\n\n";
        try (XYZReader reader = new XYZReader(new StringReader(xyz))) {
            assertFrames(reader.read(new ChemFile()), 1, 2);
        }
    }

    @Test
    void testSingleFrameWithTrailingBlankLinesInputStream() throws Exception {
        String xyz = "1\nhelium\nHe 0.0 0.0 0.0\n\n\n";
        try (XYZReader reader = new XYZReader(new ByteArrayInputStream(xyz.getBytes(StandardCharsets.US_ASCII)))) {
            assertFrames(reader.read(new ChemFile()), 1);
        }
    }

    @Test
    void testTwoFramesWithTrailingBlankLinesInputStream() throws Exception {
        String xyz = "1\nhelium\nHe 0.0 0.0 0.0\n"
                   + "2\nhydrogen\nH 0.0 0.0 0.0\nH 0.0 0.0 0.74\n\n\n";
        try (XYZReader reader = new XYZReader(new ByteArrayInputStream(xyz.getBytes(StandardCharsets.US_ASCII)))) {
            assertFrames(reader.read(new ChemFile()), 1, 2);
        }
    }

    @Test
    void testBlankLinesAtFrameBoundaries() throws Exception {
        String xyz = "\n \t\n1\n\nHe 0.0 0.0 0.0\n\n \t\n"
                   + "2\nhydrogen\nH 0.0 0.0 0.0\nH 0.0 0.0 0.74\n \t\n";
        try (XYZReader reader = new XYZReader(new StringReader(xyz))) {
            ChemFile chemFile = reader.read(new ChemFile());
            assertFrames(chemFile, 1, 2);
            Assertions.assertEquals("", chemFile.getChemSequence(0).getChemModel(0)
                                               .getMoleculeSet().getAtomContainer(0).getTitle());
        }
    }

    @Test
    void testMalformedAtomRowStillFails() throws Exception {
        String xyz = "1\nhelium\nHe invalid 0.0 0.0\n";
        try (XYZReader reader = new XYZReader(new StringReader(xyz))) {
            Assertions.assertThrows(NumberFormatException.class, () -> reader.read(new ChemFile()));
        }
    }

    private void assertFrames(ChemFile chemFile, int... atomCounts) {
        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(1, chemFile.getChemSequenceCount());
        IChemSequence sequence = chemFile.getChemSequence(0);
        Assertions.assertEquals(atomCounts.length, sequence.getChemModelCount());
        for (int i = 0; i < atomCounts.length; i++) {
            IAtomContainerSet molecules = sequence.getChemModel(i).getMoleculeSet();
            Assertions.assertNotNull(molecules);
            Assertions.assertEquals(1, molecules.getAtomContainerCount());
            Assertions.assertEquals(atomCounts[i], molecules.getAtomContainer(0).getAtomCount());
        }
    }

}
