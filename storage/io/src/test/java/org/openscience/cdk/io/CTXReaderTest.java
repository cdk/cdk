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
 *  */
package org.openscience.cdk.io;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * TestCase for the reading CTX files using a test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.CrystClustReader
 */
class CTXReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(CTXReaderTest.class);

    @BeforeAll
    static void setup() {
        setSimpleChemObjectReader(new CTXReader(), "methanol_with_descriptors.ctx");
    }

    @Test
    void testAccepts() {
        CTXReader reader = new CTXReader();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
    }

    @Test
    void testMethanol() throws Exception {
        String filename = "methanol_with_descriptors.ctx";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        CTXReader reader = new CTXReader(ins);
        IChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(1, chemFile.getChemSequenceCount());
        IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(1, seq.getChemModelCount());
        IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);

        IAtomContainerSet moleculeSet = model.getMoleculeSet();
        Assertions.assertNotNull(moleculeSet);
        Assertions.assertEquals(1, moleculeSet.getAtomContainerCount());

        IAtomContainer container = moleculeSet.getAtomContainer(0);
        Assertions.assertNotNull(container);
        Assertions.assertEquals(6, container.getAtomCount(), "Incorrect atom count.");
        Assertions.assertEquals(5, container.getBondCount());

        Assertions.assertEquals("Petra", container.getID());

        Assertions.assertNotNull(container.getTitle());
        Assertions.assertEquals("CH4O", container.getTitle());
    }
}
