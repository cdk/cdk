/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * @cdk.module test-extra
 */
class VASPReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(VASPReaderTest.class);

    @BeforeAll
    static void setup() {
        setSimpleChemObjectReader(new VASPReader(), "LiMoS2_optimisation_ISIF3.vasp");
    }

    @Test
    void testAccepts() {
        VASPReader reader = new VASPReader();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
    }

    @Test
    void testReading() throws Exception {
        String filename = "LiMoS2_optimisation_ISIF3.vasp";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        VASPReader reader = new VASPReader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        Assertions.assertNotNull(chemFile);
        org.openscience.cdk.interfaces.IChemSequence sequence = chemFile.getChemSequence(0);
        Assertions.assertNotNull(sequence);
        Assertions.assertEquals(6, sequence.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = sequence.getChemModel(0);
        Assertions.assertNotNull(model);
        org.openscience.cdk.interfaces.ICrystal crystal = model.getCrystal();
        Assertions.assertNotNull(crystal);
        Assertions.assertEquals(16, crystal.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = crystal.getAtom(0);
        Assertions.assertNotNull(atom);
        Assertions.assertNotNull(atom.getFractionalPoint3d());
    }
}
