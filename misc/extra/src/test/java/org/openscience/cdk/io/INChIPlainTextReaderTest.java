/* Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

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
 * TestCase for the reading INChI plain text files.
 *
 *
 * @see org.openscience.cdk.io.INChIReader
 * @cdk.require java1.4+
 */
class INChIPlainTextReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(INChIPlainTextReaderTest.class);

    @BeforeAll
    static void setup() {
        setSimpleChemObjectReader(new INChIPlainTextReader(), "guanine.inchi");
    }

    @Test
    void testAccepts() throws IOException {
        INChIPlainTextReader reader = new INChIPlainTextReader();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
        reader.close();
    }

    /**
     * Test a INChI 1.12Beta file containing the two tautomers
     * of guanine.
     */
    @Test
    void testGuanine() throws Exception {
        String filename = "guanine.inchi";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        INChIPlainTextReader reader = new INChIPlainTextReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);
        IAtomContainerSet moleculeSet = model.getMoleculeSet();
        Assertions.assertNotNull(moleculeSet);
        IAtomContainer molecule = moleculeSet.getAtomContainer(0);
        Assertions.assertNotNull(molecule);

        Assertions.assertEquals(11, molecule.getAtomCount());
        Assertions.assertEquals(12, molecule.getBondCount());
    }

    @Test
    void testChebi26120() throws Exception {
        StringReader ins = new StringReader(
                "InChI=1/C40H62/c1-33(2)19-13-23-37(7)27-17-31-39(9)29-15-25-35(5)21-11-12-22-36(6)26-16-30-40(10)32-18-28-38(8)24-14-20-34(3)4/h11-12,15,19-22,25,27-30H,13-14,16-18,23-24,26,31-32H2,1-10H3");
        INChIPlainTextReader reader = new INChIPlainTextReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);
        IAtomContainerSet moleculeSet = model.getMoleculeSet();
        Assertions.assertNotNull(moleculeSet);
        IAtomContainer molecule = moleculeSet.getAtomContainer(0);
        Assertions.assertNotNull(molecule);

        Assertions.assertEquals(40, molecule.getAtomCount());
        Assertions.assertEquals(39, molecule.getBondCount());
    }

    @Test
    void testPlatinum() throws Exception {
        StringReader ins = new StringReader(
                "InChI=1S/Pt");
        INChIPlainTextReader reader = new INChIPlainTextReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);
        IAtomContainerSet moleculeSet = model.getMoleculeSet();
        Assertions.assertNotNull(moleculeSet);
        IAtomContainer molecule = moleculeSet.getAtomContainer(0);
        Assertions.assertNotNull(molecule);

        Assertions.assertEquals(1, molecule.getAtomCount());
        Assertions.assertEquals(0, molecule.getBondCount());
    }
}
