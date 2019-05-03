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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * TestCase for the reading XYZ files using a test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.XYZReader
 */
public class XYZReaderTest extends SimpleChemObjectReaderTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(XYZReaderTest.class);

    @BeforeClass
    public static void setup() throws Exception {
        setSimpleChemObjectReader(new XYZReader(), "data/xyz/viagra.xyz");
    }

    @Test
    public void testAccepts() {
        XYZReader reader = new XYZReader();
        Assert.assertTrue(reader.accepts(ChemFile.class));
    }

    @Test
    public void testViagra() throws Exception {
        String filename = "data/xyz/viagra.xyz";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        XYZReader reader = new XYZReader(ins);
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
        Assert.assertEquals(63, m.getAtomCount());
        Assert.assertEquals(0, m.getBondCount());

        Assert.assertEquals("N", m.getAtom(0).getSymbol());
        Assert.assertNotNull(m.getAtom(0).getPoint3d());
        Assert.assertEquals(-3.4932, m.getAtom(0).getPoint3d().x, 0.0001);
        Assert.assertEquals(-1.8950, m.getAtom(0).getPoint3d().y, 0.0001);
        Assert.assertEquals(0.1795, m.getAtom(0).getPoint3d().z, 0.0001);
    }

    @Test
    public void testComment() throws Exception {
        String filename = "data/xyz/viagra_withComment.xyz";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        XYZReader reader = new XYZReader(ins);
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
        Assert.assertEquals(63, m.getAtomCount());
        Assert.assertEquals(0, m.getBondCount());

        // atom 63: H    3.1625    3.1270   -0.9362
        Assert.assertEquals("H", m.getAtom(62).getSymbol());
        Assert.assertNotNull(m.getAtom(62).getPoint3d());
        Assert.assertEquals(3.1625, m.getAtom(62).getPoint3d().x, 0.0001);
        Assert.assertEquals(3.1270, m.getAtom(62).getPoint3d().y, 0.0001);
        Assert.assertEquals(-0.9362, m.getAtom(62).getPoint3d().z, 0.0001);
    }

}
