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

import javax.vecmath.Vector3d;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test-extra
 *
 * @see org.openscience.cdk.io.CrystClustReader
 */
public class CrystClustReaderTest extends SimpleChemObjectReaderTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(CrystClustReaderTest.class);

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new CrystClustReader(), "data/crystclust/estron.crystclust");
    }

    @Test
    public void testAccepts() {
        Assert.assertTrue(chemObjectIO.accepts(ChemFile.class));
        Assert.assertFalse(chemObjectIO.accepts(AtomContainer.class));
    }

    @Test
    public void testEstrone() throws Exception {
        String filename = "data/crystclust/estron.crystclust";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CrystClustReader reader = new CrystClustReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(2, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        org.openscience.cdk.interfaces.ICrystal crystal = model.getCrystal();
        Assert.assertNotNull(crystal);
        Assert.assertEquals(42, crystal.getAtomCount());
        Assert.assertEquals(1, crystal.getZ().intValue());

        // test reading of partial charges
        org.openscience.cdk.interfaces.IAtom atom = crystal.getAtom(0);
        Assert.assertNotNull(atom);
        Assert.assertEquals("O", atom.getSymbol());
        Assert.assertEquals(-0.68264902, atom.getCharge(), 0.00000001);

        // test unit cell axes
        Vector3d a = crystal.getA();
        Assert.assertEquals(7.971030, a.x, 0.000001);
        Assert.assertEquals(0.0, a.y, 0.000001);
        Assert.assertEquals(0.0, a.z, 0.000001);
        Vector3d b = crystal.getB();
        Assert.assertEquals(0.0, b.x, 0.000001);
        Assert.assertEquals(18.772200, b.y, 0.000001);
        Assert.assertEquals(0.0, b.z, 0.000001);
        Vector3d c = crystal.getC();
        Assert.assertEquals(0.0, c.x, 0.000001);
        Assert.assertEquals(0.0, c.y, 0.000001);
        Assert.assertEquals(10.262220, c.z, 0.000001);
    }
}
