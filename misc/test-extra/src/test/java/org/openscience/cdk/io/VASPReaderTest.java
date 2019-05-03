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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * @cdk.module test-extra
 */
public class VASPReaderTest extends SimpleChemObjectReaderTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(VASPReaderTest.class);

    @BeforeClass
    public static void setup() throws Exception {
        setSimpleChemObjectReader(new VASPReader(), "data/vasp/LiMoS2_optimisation_ISIF3.vasp");
    }

    @Test
    public void testAccepts() {
        VASPReader reader = new VASPReader();
        Assert.assertTrue(reader.accepts(ChemFile.class));
    }

    @Test
    public void testReading() throws Exception {
        String filename = "data/vasp/LiMoS2_optimisation_ISIF3.vasp";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        VASPReader reader = new VASPReader(ins);
        ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        org.openscience.cdk.interfaces.IChemSequence sequence = chemFile.getChemSequence(0);
        Assert.assertNotNull(sequence);
        Assert.assertEquals(6, sequence.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = sequence.getChemModel(0);
        Assert.assertNotNull(model);
        org.openscience.cdk.interfaces.ICrystal crystal = model.getCrystal();
        Assert.assertNotNull(crystal);
        Assert.assertEquals(16, crystal.getAtomCount());
        org.openscience.cdk.interfaces.IAtom atom = crystal.getAtom(0);
        Assert.assertNotNull(atom);
        Assert.assertNotNull(atom.getFractionalPoint3d());
    }
}
