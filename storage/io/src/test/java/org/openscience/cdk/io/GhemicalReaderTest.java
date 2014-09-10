/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
import java.io.StringReader;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;

/**
 * TestCase for the reading Ghemical molecular dynamics files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.GhemicalReader
 */
public class GhemicalReaderTest extends SimpleChemObjectReaderTest {

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new GhemicalMMReader(), "data/ghemical/ethene.mm1gp");
    }

    @Test
    public void testAccepts() {
        Assert.assertTrue(chemObjectIO.accepts(ChemModel.class));
    }

    @Test
    public void testExample() throws Exception {
        String testfile = "!Header mm1gp 100\n" + "!Info 1\n" + "!Atoms 6\n" + "0 6 \n" + "1 6 \n" + "2 1 \n"
                + "3 1 \n" + "4 1 \n" + "5 1 \n" + "!Bonds 5\n" + "1 0 D \n" + "2 0 S \n" + "3 0 S \n" + "4 1 S \n"
                + "5 1 S \n" + "!Coord\n" + "0 0.06677 -0.00197151 4.968e-07 \n"
                + "1 -0.0667699 0.00197154 -5.19252e-07 \n" + "2 0.118917 -0.097636 2.03406e-06 \n"
                + "3 0.124471 0.0904495 -4.84021e-07 \n" + "4 -0.118917 0.0976359 -2.04017e-06 \n"
                + "5 -0.124471 -0.0904493 5.12591e-07 \n" + "!Charges\n" + "0 -0.2\n" + "1 -0.2\n" + "2 0.1\n"
                + "3 0.1\n" + "4 0.1\n" + "5 0.1\n" + "!End";
        StringReader stringReader = new StringReader(testfile);
        GhemicalMMReader reader = new GhemicalMMReader(stringReader);
        ChemModel model = (ChemModel) reader.read((ChemObject) new ChemModel());
        reader.close();

        Assert.assertNotNull(model);
        Assert.assertNotNull(model.getMoleculeSet());
        IAtomContainerSet som = model.getMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(1, som.getAtomContainerCount());
        IAtomContainer m = som.getAtomContainer(0);
        Assert.assertNotNull(m);
        Assert.assertEquals(6, m.getAtomCount());
        Assert.assertEquals(5, m.getBondCount());

        // test reading of formal charges
        org.openscience.cdk.interfaces.IAtom a = m.getAtom(0);
        Assert.assertNotNull(a);
        Assert.assertEquals(6, a.getAtomicNumber().intValue());
        Assert.assertEquals(-0.2, a.getCharge(), 0.01);
        Assert.assertEquals(0.06677, a.getPoint3d().x, 0.01);
    }

    @Test
    public void testEthene() throws Exception {
        String filename = "data/ghemical/ethene.mm1gp";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        GhemicalMMReader reader = new GhemicalMMReader(ins);
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
        Assert.assertEquals(6, m.getAtomCount());
        Assert.assertEquals(5, m.getBondCount());
    }
}
