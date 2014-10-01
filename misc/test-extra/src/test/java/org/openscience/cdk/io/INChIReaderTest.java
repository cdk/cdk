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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
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
public class INChIReaderTest extends SimpleChemObjectReaderTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(INChIReaderTest.class);

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new INChIReader(), "data/inchi/guanine.inchi.xml");
    }

    @Test
    public void testAccepts() {
        Assert.assertTrue(chemObjectIO.accepts(ChemFile.class));
    }

    /**
     * Test a INChI 1.1Beta file containing the two tautomers
     * of guanine.
     */
    @Test
    public void testGuanine() throws Exception {
        String filename = "data/inchi/guanine.inchi.xml";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        INChIReader reader = new INChIReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        IAtomContainerSet moleculeSet = model.getMoleculeSet();
        Assert.assertNotNull(moleculeSet);
        IAtomContainer molecule = moleculeSet.getAtomContainer(0);
        Assert.assertNotNull(molecule);

        Assert.assertEquals(11, molecule.getAtomCount());
        Assert.assertEquals(12, molecule.getBondCount());
    }

    @Test(expected = CDKException.class)
    @Override
    public void testSetReader_Reader() throws Exception {
        // CDKException expected as these INChI files are XML, which must
        // be read via InputStreams
        super.testSetReader_Reader();
    }

}
