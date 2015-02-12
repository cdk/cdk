/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
import java.io.StringReader;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import static org.junit.Assert.assertNotNull;

/**
 * TestCase for the reading MDL mol files using one test file.
 * A test case for SDF files is available as separate Class.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLReader
 * @see org.openscience.cdk.io.SDFReaderTest
 */
public class MDLReaderTest extends SimpleChemObjectReaderTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLReaderTest.class);

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new MDLReader(), "data/mdl/Strychnine_nichtOK.mol");
    }

    @Test
    public void testAccepts() {
        MDLReader reader = new MDLReader();
        reader.setReaderMode(Mode.STRICT);
        Assert.assertTrue(reader.accepts(ChemFile.class));
        Assert.assertTrue(reader.accepts(ChemModel.class));
        Assert.assertTrue(reader.accepts(AtomContainer.class));
    }

    @Test
    public void testReadFromStringReader() throws Exception {
        String mdl = "cyclopropane.mol\n" + "\n" + "\n" + "  9  9  0  0\n"
                + "   -0.0073   -0.5272    0.9655 C   0  0  0  0  0\n"
                + "   -0.6776   -0.7930   -0.3498 C   0  0  0  0  0\n"
                + "    0.2103    0.4053   -0.1891 C   0  0  0  0  0\n"
                + "    0.8019   -1.1711    1.2970 H   0  0  0  0  0\n"
                + "   -0.6000   -0.2021    1.8155 H   0  0  0  0  0\n"
                + "   -1.7511   -0.6586   -0.4435 H   0  0  0  0  0\n"
                + "   -0.3492   -1.6277   -0.9620 H   0  0  0  0  0\n"
                + "    1.1755    0.4303   -0.6860 H   0  0  0  0  0\n"
                + "   -0.2264    1.3994   -0.1675 H   0  0  0  0  0\n" + "  1  2  1  6  0  0\n"
                + "  1  3  1  6  0  0\n" + "  1  4  1  0  0  0\n" + "  1  5  1  1  0  0\n" + "  2  3  1  0  0  0\n"
                + "  2  6  1  0  0  0\n" + "  2  7  1  6  0  0\n" + "  3  8  1  6  0  0\n" + "  3  9  1  0  0  0\n";
        MDLReader reader = new MDLReader(new StringReader(mdl), Mode.STRICT);
        ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
        reader.close();
        assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        assertNotNull(model);

        IAtomContainerSet som = model.getMoleculeSet();
        assertNotNull(som);
        Assert.assertEquals(1, som.getAtomContainerCount());
        IAtomContainer m = som.getAtomContainer(0);
        assertNotNull(m);
        Assert.assertEquals(9, m.getAtomCount());
        Assert.assertEquals(9, m.getBondCount());
    }

    /**
     * @cdk.bug 1542467
     */
    @Test
    public void testBug1542467() throws Exception {
        String filename = "data/mdl/Strychnine_nichtOK.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();
        assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertTrue((containersList.get(0)).getAtomCount() > 0);
        Assert.assertTrue((containersList.get(0)).getBondCount() > 0);
    }

    @Test
    public void testReadProton() throws Exception {
        String mdl = "proton.mol\n" + "\n" + "\n" + "  1  0  0  0  0                 1\n"
                + "   -0.0073   -0.5272    0.9655 H   0  3  0  0  0\n";
        MDLReader reader = new MDLReader(new StringReader(mdl), Mode.STRICT);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        assertNotNull(mol);
        Assert.assertEquals(1, mol.getAtomCount());
        Assert.assertEquals(0, mol.getBondCount());
        IAtom atom = mol.getAtom(0);
        Assert.assertEquals(1, atom.getFormalCharge().intValue());
    }

    /**
     * The corrupt file is really ok; it is just not V2000 material.
     */
    @Test
    public void testSDF() throws Exception {
        String filename = "data/mdl/prev2000.sd";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();
        assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(2, containersList.size());
        Assert.assertEquals(39, (containersList.get(0)).getAtomCount());
        Assert.assertEquals(41, (containersList.get(0)).getBondCount());
        Assert.assertEquals(29, (containersList.get(1)).getAtomCount());
        Assert.assertEquals(28, (containersList.get(1)).getBondCount());
    }

    /**
     * Tests that the '0' read from the bond block for bond stereo
     * is read is 'no stereochemistry involved'.
     */
    @Test
    public void testStereoReadZeroDefault() throws Exception {
        String filename = "data/mdl/prev2000.sd";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();
        assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(2, containersList.size());
        IAtomContainer container = containersList.get(0);
        Assert.assertEquals(IBond.Stereo.NONE, container.getBond(0).getStereo());
    }

    @Test
    public void testEmptyString() throws Exception {
        String emptyString = "";
        MDLReader reader = new MDLReader(new StringReader(emptyString), Mode.STRICT);
        IAtomContainer mol = (IAtomContainer) reader.read(new AtomContainer());
        reader.close();
        Assert.assertNull(mol);
    }

    @Test
    public void testUndefinedStereo() throws Exception {
        String filename = "data/mdl/ChEBI_26120.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.RELAXED);
        IAtomContainer mol = (IAtomContainer) reader.read(new AtomContainer());
        reader.close();
        Assert.assertEquals(IBond.Stereo.E_OR_Z, mol.getBond(1).getStereo());
        Assert.assertEquals(IBond.Stereo.E_OR_Z, mol.getBond(6).getStereo());
        Assert.assertEquals(IBond.Stereo.E_OR_Z, mol.getBond(7).getStereo());
        Assert.assertEquals(IBond.Stereo.E_OR_Z, mol.getBond(11).getStereo());
    }

    @Test
    public void testReadAtomAtomMapping() throws Exception {
        String filename = "data/mdl/a-pinene-with-atom-atom-mapping.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);

        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        assertNotNull(mol);
        Assert.assertEquals(1, ((Integer) mol.getAtom(0).getProperty(CDKConstants.ATOM_ATOM_MAPPING)).intValue());
        Assert.assertEquals(15, ((Integer) mol.getAtom(1).getProperty(CDKConstants.ATOM_ATOM_MAPPING)).intValue());
        Assert.assertNull(mol.getAtom(2).getProperty(CDKConstants.ATOM_ATOM_MAPPING));
    }

    @Test(expected = AssertionError.class)
    public void testHas2DCoordinates_With000() throws Exception {
        String filenameMol = "data/mdl/with000coordinate.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filenameMol);
        IAtomContainer molOne = null;
        MDLReader reader = new MDLReader(ins, Mode.RELAXED);
        molOne = reader.read(new AtomContainer());
        reader.close();
        assertNotNull(molOne.getAtom(0).getPoint2d());
    }

    /**
     * @cdk.bug 3485634
     */
    @Test
    public void testMissingAtomProperties() throws Exception {
        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/bug3485634.mol");
        MDLReader reader = new MDLReader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        Assert.assertEquals(9, molecule.getAtomCount());
    }

    /**
     * @cdk.bug 1356
     */
    @Test
    public void properties() throws Exception {
        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/bug1356.sdf");
        MDLReader reader = new MDLReader(in);
        IChemFile chemfile = DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class);
        chemfile = reader.read(chemfile);
        IAtomContainer container = ChemFileManipulator.getAllAtomContainers(chemfile).iterator().next();
        assertNotNull(container.getProperty("first"));
        assertNotNull(container.getProperty("second"));
        reader.close();
            
    }

    @Test(expected=CDKException.class)
    public void wrongFormat() throws CDKException {
        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/bug1356.sdf");
        MDLReader reader = new MDLReader(in, Mode.STRICT);
        IChemFile chemfile = DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class);
        chemfile = reader.read(chemfile);
    }
}
