/* Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;

/**
 * TestCase for the reading Cerius<sup>2</sup> Polymorph Predictor files using a test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.PMPReader
 */
public class PMPReaderTest extends SimpleChemObjectReaderTest {

    @BeforeClass
    public static void setup() throws Exception {
        setSimpleChemObjectReader(new PMPReader(), "data/pmp/aceticacid.pmp");
    }

    @Test
    public void testAccepts() {
        PMPReader reader = new PMPReader();
        Assert.assertTrue(reader.accepts(ChemFile.class));
    }

    @Test
    public void testAceticAcid() throws Exception {
        String filename = "data/pmp/aceticacid.pmp";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        PMPReader reader = new PMPReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        ICrystal crystal = model.getCrystal();
        Assert.assertNotNull(crystal);
        Assert.assertEquals(32, crystal.getAtomCount());
        Assert.assertEquals(28, crystal.getBondCount());

        Assert.assertEquals("O", crystal.getAtom(6).getSymbol());
        Assert.assertEquals(1.4921997, crystal.getAtom(6).getPoint3d().x, 0.00001);
        Assert.assertEquals("O", crystal.getAtom(7).getSymbol());
        Assert.assertEquals(1.4922556, crystal.getAtom(7).getPoint3d().x, 0.00001);
    }

    @Test
    public void testTwoAceticAcid() throws Exception {
        String filename = "data/pmp/two_aceticacid.pmp";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        PMPReader reader = new PMPReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(2, seq.getChemModelCount());

        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        ICrystal crystal = model.getCrystal();
        Assert.assertNotNull(crystal);
        Assert.assertEquals(32, crystal.getAtomCount());
        Assert.assertEquals(28, crystal.getBondCount());

        model = seq.getChemModel(1);
        Assert.assertNotNull(model);
        crystal = model.getCrystal();
        Assert.assertNotNull(crystal);
        Assert.assertEquals(32, crystal.getAtomCount());
        Assert.assertEquals(28, crystal.getBondCount());
    }
}
