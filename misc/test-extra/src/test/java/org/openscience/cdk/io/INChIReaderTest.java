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

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * TestCase for the reading INChI files using one test file.
 *
 * @cdk.module test-extra
 *
 * @see org.openscience.cdk.io.INChIReader
 * @cdk.require java1.4+
 */
class INChIReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(INChIReaderTest.class);

    @BeforeAll
    static void setup() {
        setSimpleChemObjectReader(new INChIReader(), "guanine.inchi.xml");
    }

    @Test
    void testAccepts() {
        Assertions.assertTrue(chemObjectIO.accepts(ChemFile.class));
    }

    /**
     * Test a INChI 1.1Beta file containing the two tautomers
     * of guanine.
     */
    @Test
    void testGuanine() throws Exception {
        String filename = "guanine.inchi.xml";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        INChIReader reader = new INChIReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());

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
    @Override
    public void testSetReader_Reader() throws Exception {
        // CDKException expected as these INChI files are XML, which must
        // be read via InputStreams
        Assertions.assertThrows(CDKException.class, () -> {
            super.testSetReader_Reader();
        });
    }

}
