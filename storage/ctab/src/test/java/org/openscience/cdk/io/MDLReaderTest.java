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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * TestCase for the reading MDL mol files using one test file.
 * A test case for SDF files is available as separate Class.
 *
 *
 * @see org.openscience.cdk.io.MDLReader
 * @see org.openscience.cdk.io.SDFReaderTest
 */
class MDLReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLReaderTest.class);

    @BeforeAll
    static void setup() {
        setSimpleChemObjectReader(new MDLReader(), "Strychnine_nichtOK.mol");
    }

    @Test
    void testAccepts() {
        MDLReader reader = new MDLReader();
        reader.setReaderMode(Mode.STRICT);
        Assertions.assertTrue(reader.accepts(ChemFile.class));
        Assertions.assertTrue(reader.accepts(ChemModel.class));
        Assertions.assertTrue(reader.accepts(IAtomContainer.class));
    }

    @Test
    void testReadFromStringReader() throws Exception {
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
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        Assertions.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);
        Assertions.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);

        IAtomContainerSet som = model.getMoleculeSet();
        Assertions.assertNotNull(som);
        Assertions.assertEquals(1, som.getAtomContainerCount());
        IAtomContainer m = som.getAtomContainer(0);
        Assertions.assertNotNull(m);
        Assertions.assertEquals(9, m.getAtomCount());
        Assertions.assertEquals(9, m.getBondCount());
    }
    
    @Test
    void testMassDiff() throws Exception {
        String mdl = "deuterium.mol\n" + "\n" + "\n" + "  1  0  0  0  0                 1\n"
                + "    0.0000    0.0000    0.0000 H  +1  0  0  0  0\n";
        try (MDLReader reader = new MDLReader(new StringReader(mdl), Mode.STRICT)) {
            IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            IAtom atom = mol.getAtom(0);
            Assertions.assertEquals(1, atom.getAtomicNumber().intValue());
            Assertions.assertEquals(2, atom.getMassNumber().intValue());
        }
    }

    /**
     * @cdk.bug 1542467
     */
    @Test
    void testBug1542467() throws Exception {
        String filename = "Strychnine_nichtOK.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertTrue((containersList.get(0)).getAtomCount() > 0);
        Assertions.assertTrue((containersList.get(0)).getBondCount() > 0);
    }

    @Test
    void testReadProton() throws Exception {
        String mdl = "proton.mol\n" + "\n" + "\n" + "  1  0  0  0  0                 1\n"
                + "   -0.0073   -0.5272    0.9655 H   0  3  0  0  0\n";
        MDLReader reader = new MDLReader(new StringReader(mdl), Mode.STRICT);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertEquals(0, mol.getBondCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals(1, atom.getFormalCharge().intValue());
    }

    /**
     * The corrupt file is really ok; it is just not V2000 material.
     */
    @Test
    void testSDF() throws Exception {
        String filename = "prev2000.sd";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(2, containersList.size());
        Assertions.assertEquals(39, (containersList.get(0)).getAtomCount());
        Assertions.assertEquals(41, (containersList.get(0)).getBondCount());
        Assertions.assertEquals(29, (containersList.get(1)).getAtomCount());
        Assertions.assertEquals(28, (containersList.get(1)).getBondCount());
    }

    /**
     * Tests that the '0' read from the bond block for bond stereo
     * is read is 'no stereochemistry involved'.
     */
    @Test
    void testStereoReadZeroDefault() throws Exception {
        String filename = "prev2000.sd";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(2, containersList.size());
        IAtomContainer container = containersList.get(0);
        Assertions.assertEquals(IBond.Display.Solid, container.getBond(0).getDisplay());
    }

    @Test
    void testEmptyString() throws Exception {
        String emptyString = "";
        MDLReader reader = new MDLReader(new StringReader(emptyString), Mode.STRICT);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        assertNull(mol);
    }

    @Test
    void testUndefinedStereo() throws Exception {
        String filename = "ChEBI_26120.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.RELAXED);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertEquals(IBond.Display.Crossed, mol.getBond(1).getDisplay());
        Assertions.assertEquals(IBond.Display.Crossed, mol.getBond(6).getDisplay());
        Assertions.assertEquals(IBond.Display.Crossed, mol.getBond(7).getDisplay());
        Assertions.assertEquals(IBond.Display.Crossed, mol.getBond(11).getDisplay());
    }

    @Test
    void testReadAtomAtomMapping() throws Exception {
        String filename = "a-pinene-with-atom-atom-mapping.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);

        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(1, ((Integer) mol.getAtom(0).getProperty(CDKConstants.ATOM_ATOM_MAPPING)).intValue());
        Assertions.assertEquals(15, ((Integer) mol.getAtom(1).getProperty(CDKConstants.ATOM_ATOM_MAPPING)).intValue());
        assertNull(mol.getAtom(2).getProperty(CDKConstants.ATOM_ATOM_MAPPING));
    }

    @Test
    void testHas2DCoordinates_With000() throws Exception {
        String filenameMol = "with000coordinate.mol";
        InputStream ins = this.getClass().getResourceAsStream(filenameMol);
        IAtomContainer molOne;
        MDLReader reader = new MDLReader(ins, Mode.RELAXED);
        molOne = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        // 0,0 or null no Coords, MDLV2000 will get this OK if there is 2D/3D
        // in the headerO
        assertNull(molOne.getAtom(0).getPoint2d());
    }

    /**
     * @cdk.bug 3485634
     */
    @Test
    void testMissingAtomProperties() throws Exception {
        InputStream in = getClass().getResourceAsStream("bug3485634.mol");
        MDLReader reader = new MDLReader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        Assertions.assertEquals(9, molecule.getAtomCount());
    }

    /**
     * @cdk.bug 1356
     */
    @Test
    void properties() throws Exception {
        InputStream in = getClass().getResourceAsStream("bug1356.sdf");
        MDLReader reader = new MDLReader(in);
        IChemFile chemfile = DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class);
        chemfile = reader.read(chemfile);
        IAtomContainer container = ChemFileManipulator.getAllAtomContainers(chemfile).iterator().next();
        Assertions.assertNotNull(container.getProperty("first"));
        Assertions.assertNotNull(container.getProperty("second"));
        reader.close();
            
    }

    @Test
    void wrongFormat() throws CDKException {
        InputStream in = getClass().getResourceAsStream("bug1356.sdf");
        MDLReader reader = new MDLReader(in, Mode.STRICT);
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    IChemFile chemfile = DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class);
                                    chemfile = reader.read(chemfile);
                                });
    }
}
