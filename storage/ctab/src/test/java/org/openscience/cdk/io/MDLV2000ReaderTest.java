/*
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
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

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomRef;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupBracket;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * TestCase for the reading MDL mol files using one test file.
 * A test case for SDF files is available as separate Class.
 *
 *
 * @see org.openscience.cdk.io.MDLV2000Reader
 * @see org.openscience.cdk.io.SDFReaderTest
 */
class MDLV2000ReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLV2000ReaderTest.class);

    @BeforeAll
    static void setup() {
        setSimpleChemObjectReader(new MDLV2000Reader(), "org/openscience/cdk/io/iterator/bug682233.mol");
    }

    @Test
    void testAccepts() {
        MDLV2000Reader reader = new MDLV2000Reader();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
        Assertions.assertTrue(reader.accepts(ChemModel.class));
        Assertions.assertTrue(reader.accepts(IAtomContainer.class));
    }

    /**
     * @cdk.bug 3084064
     */
    @Test
    void testBug3084064() throws Exception {
        String filename = "weirdprops.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();

        Assertions.assertNotNull(chemFile);

        List<IAtomContainer> mols = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(10, mols.size());

        IAtomContainer mol = mols.get(0);
        Map<Object, Object> props = mol.getProperties();
        Assertions.assertNotNull(props);
        Assertions.assertEquals(5, props.size());

        String[] keys = {"DatabaseID", "cdk:Title", "PeaksExplained", "cdk:Remark", "Score"};
        for (String s : keys) {
            boolean found = false;
            for (Object key : props.keySet()) {
                if (s.equals(key)) {
                    found = true;
                    break;
                }
            }
            Assertions.assertTrue(found, s + " was not read from the file");
        }
    }

    /**
     * @cdk.bug 682233
     */
    @Test
    void testBug682233() throws Exception {
        String filename = "iterator/bug682233.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
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
        Assertions.assertEquals(4, m.getAtomCount());
        Assertions.assertEquals(2, m.getBondCount());

        // test reading of formal charges
        org.openscience.cdk.interfaces.IAtom a = m.getAtom(0);
        Assertions.assertNotNull(a);
        Assertions.assertEquals("Na", a.getSymbol());
        Assertions.assertEquals(1, a.getFormalCharge().intValue());
        a = m.getAtom(2);
        Assertions.assertNotNull(a);
        Assertions.assertEquals("O", a.getSymbol());
        Assertions.assertEquals(-1, a.getFormalCharge().intValue());
    }

    @Test
    void testAPinene() throws Exception {
        String filename = "a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assertions.assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    void testReadingMISOLines() throws Exception {
        String filename = "ChEBI_37340.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assertions.assertEquals(210, containersList.get(0).getAtom(0).getMassNumber().intValue());
    }

    /**
     * @cdk.bug 2234820
     */
    @Test
    void testMassNumber() throws Exception {
        String filename = "massnumber.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertEquals(3, containersList.get(0).getAtomCount());
        Assertions.assertEquals(2, containersList.get(0).getAtom(1).getMassNumber().intValue());
        Assertions.assertEquals(3, containersList.get(0).getAtom(2).getMassNumber().intValue());
    }

    @Test
    void testAlkane() throws Exception {
        String filename = "shortest_path_test.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        IAtomContainer container = containersList.get(0);
        Assertions.assertEquals(10, container.getAtomCount());
        Assertions.assertEquals(9, container.getBondCount());
        for (IAtom iAtom : container.atoms()) {
            Assertions.assertEquals("C", iAtom.getSymbol());
        }
        for (IBond iBond : container.bonds()) {
            Assertions.assertEquals(Order.SINGLE, iBond.getOrder());
        }
    }

    @Test
    void testReadTitle() throws Exception {
        String filename = "a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertEquals("a-pinen.mol", mol.getTitle());
    }

    @Test
    void testFourRing() throws Exception {
        String filename = "four-ring-5x10.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assertions.assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    void testHydrozyamino() throws Exception {
        String filename = "hydroxyamino.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assertions.assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    void testMethylBenzol() throws Exception {
        String filename = "methylbenzol.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assertions.assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    void testPolycarpol() throws Exception {
        String filename = "polycarpol.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assertions.assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    void testReserpine() throws Exception {
        String filename = "reserpine.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assertions.assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    void testSixRing() throws Exception {
        String filename = "six-ring-4x4.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assertions.assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    void testSuperspiro() throws Exception {
        String filename = "superspiro.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assertions.assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    void testGhemicalOutput() throws Exception {
        String filename = "butanoic_acid.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assertions.assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    void testUsesGivenMolecule() throws Exception {
        String filename = "superspiro.mol"; // just a random file
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer superspiro = SilentChemObjectBuilder.getInstance().newAtomContainer();
        superspiro.setID("superspiro");
        IAtomContainer result = reader.read(superspiro);
        reader.close();
        Assertions.assertEquals(superspiro.getID(), result.getID());
    }

    /**
     * @cdk.bug 835571
     */
    @Test
    void testReadFromStringReader() throws Exception {
        String mdl = "cyclopropane.mol\n" + "\n" + "\n" + "  9  9  0  0  0                 1 V2000\n"
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
                + "  2  6  1  0  0  0\n" + "  2  7  1  6  0  0\n" + "  3  8  1  6  0  0\n" + "  3  9  1  0  0  0\n"
                + "M  END\n";
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdl));
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
    void testRGroup() throws Exception {
        String filename = "SARGROUPTEST.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertEquals("R2", ((IPseudoAtom) mol.getAtom(19)).getLabel());
    }

    @Test
    void testAliasPropertyGroup() throws Exception {
        String filename = "AliasPropertyRGroup.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        IAtom atom = mol.getAtom(0);
        Assertions.assertTrue(atom instanceof IPseudoAtom);
        Assertions.assertEquals("R\\1", ((IPseudoAtom) atom).getLabel());
    }

    /**
     * @cdk.bug 1587283
     */
    @Test
    void testBug1587283() throws Exception {
        String filename = "bug1587283.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertEquals(15, containersList.get(0).getAtomCount());
        Assertions.assertEquals(16, containersList.get(0).getBondCount());
    }

    @Test
    void testReadProton() throws Exception {
        String mdl = "proton.mol\n" + "\n" + "\n" + "  1  0  0  0  0                 1 V2000\n"
                + "   -0.0073   -0.5272    0.9655 H   0  0  0  0  0\n" + "M  CHG  1   1   1\n" + "M  END\n";
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdl));
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertEquals(0, mol.getBondCount());
        Assertions.assertEquals(1, AtomContainerManipulator.getTotalFormalCharge(mol));
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals(1, atom.getFormalCharge().intValue());
    }

    @Test
    void testReadingCharges() throws Exception {
        String filename = "withcharges.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        IAtomContainer container = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertEquals(1, container.getAtom(6).getFormalCharge().intValue());
        Assertions.assertEquals(-1, container.getAtom(8).getFormalCharge().intValue());
    }

    @Test
    void testEmptyString() throws Exception {
        String emptyString = "";
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(emptyString));
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertNull(mol);
    }

    @Test
    void testNoAtomCase() throws Exception {
        String filename = "emptyStructure.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());

        IAtomContainer container = containersList.get(0);
        Assertions.assertNotNull(container);
        Assertions.assertEquals(0, container.getAtomCount());
        Assertions.assertEquals(0, container.getBondCount());

        Map<Object, Object> props = container.getProperties();
        Set<Object> keys = props.keySet();

        Assertions.assertTrue(keys.contains("SubstanceType"));
        Assertions.assertTrue(keys.contains("TD50 Rat"));
        Assertions.assertTrue(keys.contains("ChemCount"));
    }

    /**
     * @cdk.bug 1732307
     */
    @Test
    void testZeroZCoordinates() throws Exception {
        String filename = "iterator/nozcoord.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Properties prop = new Properties();
        prop.setProperty("ForceReadAs3DCoordinates", "true");
        PropertiesListener listener = new PropertiesListener(prop);
        reader.addChemObjectIOListener(listener);
        reader.customizeJob();

        IAtomContainer mol = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        reader.close();
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(5, mol.getAtomCount());

        boolean has3d = GeometryUtil.has3DCoordinates(mol);
        Assertions.assertTrue(has3d);
    }

    /**
     * @cdk.bug 1732307
     */
    @Test
    void testZeroZCoordinates3DMarked() throws Exception {
        String filename = "iterator/nozcoord.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        reader.close();
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(5, mol.getAtomCount());

        boolean has3d = GeometryUtil.has3DCoordinates(mol);
        Assertions.assertTrue(has3d);
    }

    /**
     * Don't accept hydrogen isotopes D/T in strict mode.
     * @cdk.bug 1826577
     */
    @Test
    void testHisotopes_Strict() {
        String filename = "hisotopes.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    try (MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT)) {
                                        reader.read(new ChemFile());
                                    }
                                });
    }

                                /**
     * @cdk.bug 1826577
     */
    @Test
    void testHisotopes_Relaxed() throws Exception {
        String filename = "hisotopes.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.RELAXED);
        IChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertNotNull(containersList.get(0));
        Assertions.assertFalse((containersList.get(0)).getAtom(1) instanceof IPseudoAtom);
        Assertions.assertFalse((containersList.get(0)).getAtom(2) instanceof IPseudoAtom);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    void testReadRadical() throws Exception {
        String filename = "332727182.radical.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        Assertions.assertTrue((containersList.get(0)).getAtomCount() > 0);
        Assertions.assertTrue((containersList.get(0)).getBondCount() > 0);
        Assertions.assertTrue((containersList.get(0)).getSingleElectronCount() > 0);
    }

    /**
     * @cdk.bug 2604888
     */
    @Test
    void testNoCoordinates() throws Exception {
        String mdl = "cyclopropane.mol\n" + "\n" + "\n" + "  9  9  0  0  0 0 0 0 0 0 0 0 0 1 V2000\n"
                + "    0.0000    0.0000    0.0000 C   0  0  0  0  0\n"
                + "    0.0000    0.0000    0.0000 C   0  0  0  0  0\n"
                + "    0.0000    0.0000    0.0000 C   0  0  0  0  0\n"
                + "    0.0000    0.0000    0.0000 H   0  0  0  0  0\n"
                + "    0.0000    0.0000    0.0000 H   0  0  0  0  0\n"
                + "    0.0000    0.0000    0.0000 H   0  0  0  0  0\n"
                + "    0.0000    0.0000    0.0000 H   0  0  0  0  0\n"
                + "    0.0000    0.0000    0.0000 H   0  0  0  0  0\n"
                + "    0.0000    0.0000    0.0000 H   0  0  0  0  0\n" + "  1  2  1  6  0  0\n"
                + "  1  3  1  6  0  0\n" + "  1  4  1  0  0  0\n" + "  1  5  1  1  0  0\n" + "  2  3  1  0  0  0\n"
                + "  2  6  1  0  0  0\n" + "  2  7  1  6  0  0\n" + "  3  8  1  6  0  0\n" + "  3  9  1  0  0  0\n"
                + "M  END\n";
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdl));
        IAtomContainer molecule = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertNotNull(molecule);
        Assertions.assertEquals(9, molecule.getAtomCount());
        Assertions.assertEquals(9, molecule.getBondCount());
        for (IAtom atom : molecule.atoms()) {
            Assertions.assertNull(atom.getPoint2d());
            Assertions.assertNull(atom.getPoint2d());
        }
    }

    @Test
    void testUndefinedStereo() throws Exception {
        String filename = "ChEBI_26120.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertEquals(IBond.Stereo.E_OR_Z, mol.getBond(1).getStereo());
        Assertions.assertEquals(IBond.Stereo.E_OR_Z, mol.getBond(6).getStereo());
        Assertions.assertEquals(IBond.Stereo.E_OR_Z, mol.getBond(7).getStereo());
        Assertions.assertEquals(IBond.Stereo.E_OR_Z, mol.getBond(11).getStereo());
    }

    @Test
    void testUndefinedStereo2() throws Exception {
        String filename = "a-pinene-with-undefined-stereo.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertEquals(IBond.Stereo.UP_OR_DOWN, mol.getBond(1).getStereo());
    }

    /**
     * Tests that the '0' read from the bond block for bond stereo
     * is read is 'no stereochemistry involved'.
     */
    @Test
    void testStereoReadZeroDefault() throws Exception {
        String filename = "withcharges.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        IAtomContainer container = containersList.get(0);
        Assertions.assertEquals(IBond.Stereo.NONE, container.getBond(0).getStereo());
    }

    @Test
    void testReadStereoBonds() throws Exception {
        String mdl = "cyclopropane.mol\n" + "\n" + "\n" + "  9  9  0  0  0                 1 V2000\n"
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
                + "  2  6  1  0  0  0\n" + "  2  7  1  6  0  0\n" + "  3  8  1  6  0  0\n" + "  3  9  1  0  0  0\n"
                + "M  END\n";
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdl));
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(9, mol.getAtomCount());
        Assertions.assertEquals(9, mol.getBondCount());
        Assertions.assertEquals(IBond.Stereo.DOWN, mol.getBond(0).getStereo());
        Assertions.assertEquals(IBond.Stereo.UP, mol.getBond(3).getStereo());
    }

    @Test
    void testStereoDoubleBonds() throws Exception {
        String filename = "butadiene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());
        IAtomContainer container = containersList.get(0);
        Assertions.assertEquals(IBond.Stereo.E_Z_BY_COORDINATES, container.getBond(0).getStereo());
        Assertions.assertEquals(IBond.Stereo.E_OR_Z, container.getBond(2).getStereo());
    }

    /**
     * Tests numbering of R# elements according to RGP line.
     * @throws Exception
     */
    @Test
    void testRGroupHashNumbering() throws Exception {
        String filename = "rgroups.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        for (IBond bond : mol.bonds()) {
            IPseudoAtom rGroup;
            IAtom partner;
            if (bond.getBegin() instanceof IPseudoAtom) {
                rGroup = (IPseudoAtom) bond.getBegin();
                partner = bond.getEnd();
            } else {
                partner = bond.getBegin();
                rGroup = (IPseudoAtom) bond.getEnd();
            }
            if (partner.getAtomicNumber() == IElement.N) {
                Assertions.assertEquals(rGroup.getLabel(), "R4");
            } else if (partner.getAtomicNumber() == IElement.P) {
                Assertions.assertEquals(rGroup.getLabel(), "R1");
            } else if (partner.getAtomicNumber() == IElement.As) {
                Assertions.assertEquals(rGroup.getLabel(), "R4");
            } else if (partner.getAtomicNumber() == IElement.Si) {
                Assertions.assertEquals(rGroup.getLabel(), "R");
            }
        }
    }

    /**
     * Test for hard coded R-group numbers in the Atom block.
     * Hard coding is accepted but should not be done really, instead use
     * a hash (#) conform the CTFile spec.
     * @throws Exception
     */
    @Test
    void testRGroupHardcodedNumbering() throws Exception {
        String filename = "rgroupsNumbered.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        reader.close();
        for (IBond bond : mol.bonds()) {
            IPseudoAtom rGroup;
            if (bond.getBegin() instanceof IPseudoAtom)
                rGroup = (IPseudoAtom) bond.getBegin();
            else
                rGroup = (IPseudoAtom) bond.getEnd();

            if (bond.getOrder() == IBond.Order.DOUBLE) {
                Assertions.assertEquals(rGroup.getLabel(), "R32");
            } else if (bond.getStereo() == IBond.Stereo.DOWN) {
                Assertions.assertEquals(rGroup.getLabel(), "R2");
            } else if (bond.getStereo() == IBond.Stereo.UP) {
                Assertions.assertEquals(rGroup.getLabel(), "R20");
            } else
                Assertions.assertEquals(rGroup.getLabel(), "R5");
        }
    }

    @Test
    void testReadValence() throws Exception {
        String filename = "a-pinene-with-valence.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);

        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(2, mol.getAtom(0).getValency().intValue());
        Assertions.assertEquals(3, mol.getAtom(1).getValency().intValue());
        org.hamcrest.MatcherAssert.assertThat(mol.getAtom(2).getValency(), is(not(0)));
        org.hamcrest.MatcherAssert.assertThat(mol.getAtom(2).getValency(), is(4));
        Assertions.assertEquals(0, mol.getAtom(3).getValency().intValue());
    }

    @Test
    void testShortLines() throws Exception {
        logger.info("Testing short lines Mode.RELAXED");
        testShortLinesForMode(Mode.RELAXED);
        logger.info("Testing short lines Mode.STRICT");
        testShortLinesForMode(Mode.STRICT);
    }

    private void testShortLinesForMode(IChemObjectReader.Mode mode) throws Exception {
        String filename = "glycine-short-lines.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, mode);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(mol.getAtomCount(), 5);
        Assertions.assertEquals(mol.getBondCount(), 4);
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
        Assertions.assertNull(mol.getAtom(2).getProperty(CDKConstants.ATOM_ATOM_MAPPING));
    }

    /**
     * @cdk.bug 2936440
     */
    @Test
    void testHas2DCoordinates_With000() throws Exception {
        String filenameMol = "with000coordinate.mol";
        InputStream ins = this.getClass().getResourceAsStream(filenameMol);
        IAtomContainer molOne;
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molOne = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        Assertions.assertNotNull(molOne.getAtom(0).getPoint2d());
        Assertions.assertNotNull(molOne.getAtom(0).getPoint3d());
    }

    @Test
    void testAtomValueLines() throws Exception {
        String filename = "atomValueLines.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer testMolecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtomContainer result = reader.read(testMolecule);
        reader.close();
        IAtom oxygen = result.getAtom(0);
        Assertions.assertEquals(IElement.O, (int) oxygen.getAtomicNumber());
        Assertions.assertEquals(oxygen.getProperty(CDKConstants.COMMENT), "Oxygen comment");
    }

    @Test
    void testDeuterium() throws Exception {
        String filename = "chemblMolregno5369.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.RELAXED);

        Properties prop = new Properties();
        prop.setProperty("InterpretHydrogenIsotopes", "true");
        PropertiesListener listener = new PropertiesListener(prop);
        reader.addChemObjectIOListener(listener);
        reader.customizeJob();

        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        molecule = reader.read(molecule);
        reader.close();
        int deuteriumCount = 0;
        for (IAtom atom : molecule.atoms())
            if (atom.getAtomicNumber() == IElement.H && atom.getMassNumber() != null && atom.getMassNumber() == 2)
                deuteriumCount++;
        Assertions.assertEquals(3, deuteriumCount);
    }

    @Test
    void testDeuteriumProperties() throws Exception {
        String filename = "chemblMolregno5369.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.RELAXED);
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        molecule = reader.read(molecule);
        reader.close();
        IAtom deuterium = molecule.getAtom(molecule.getAtomCount() - 1);
        Assertions.assertEquals(1, (int) deuterium.getAtomicNumber());
        Assertions.assertEquals(2, (int) deuterium.getMassNumber());
    }

    @Test
    void testTritium() throws Exception {
        String filename = "chemblMolregno7039.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        molecule = reader.read(molecule);
        reader.close();
        int tritiumCount = 0;
        for (IAtom atom : molecule.atoms())
            if (atom.getAtomicNumber() == IElement.H && atom.getMassNumber() != null && atom.getMassNumber() == 3)
                tritiumCount++;
        Assertions.assertEquals(1, tritiumCount);
    }

    @Test
    void testTritiumProperties() throws Exception {
        String filename = "chemblMolregno7039.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        molecule = reader.read(molecule);
        reader.close();
        IAtom tritium = molecule.getAtom(molecule.getAtomCount() - 1);
        Assertions.assertEquals(1, (int) tritium.getAtomicNumber());
        Assertions.assertEquals(3, (int) tritium.getMassNumber());
    }

    /**
     * Tests a molfile with 'query' bond types (in this case bond type == 8 (any)).
     */
    @Test
    void testQueryBondType8() throws Exception {
        String filename = "iridiumCoordination.chebi52748.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer atc = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();

        int queryBondCount = 0;
        for (IAtom atom : atc.atoms()) {
            if (atom.getAtomicNumber() == IElement.Ir) {
                for (IBond bond : atc.getConnectedBondsList(atom)) {
                    if (bond instanceof QueryBond) {
                      if (((QueryBond) bond).getExpression().type() == Expr.Type.TRUE) {
                        queryBondCount++;
                      }
                    }
                }
            }
        }
        Assertions.assertEquals(3, queryBondCount, "Expecting three 'query' bond types to 'Ir'");
    }

    /**
     * Tests a molfile with 'query' bond types (in this case bond type == 6).
     */
    @Test
    void testQueryBondType6() throws Exception {
        String filename = "chebi.querybond.51736.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer atc = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        int queryBondCount = 0;

        for (IBond bond : atc.bonds()) {
            if (bond instanceof QueryBond) {
                if (((QueryBond) bond).getExpression().type() == Expr.Type.SINGLE_OR_AROMATIC) {
                    queryBondCount++;
                }
            }
        }
        Assertions.assertEquals(6, queryBondCount, "Expecting six 'query' bond types");
    }

    /**
     * Test that R-groups at higher atom numbers (>9) are read correctly
     */
    @Test
    void testRGroupHighAtomNumber() throws Exception {
        InputStream in = getClass().getResourceAsStream("brenda_molfile_rgroup.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        reader.read(molecule);
        reader.close();
        IAtom atom = molecule.getAtom(55);
        org.hamcrest.MatcherAssert.assertThat(atom, CoreMatchers.instanceOf(IPseudoAtom.class));
        Assertions.assertEquals("R", ((IPseudoAtom)atom).getLabel());
    }

    @Test
    void testAliasAtomNaming() throws Exception {
        InputStream in = getClass().getResourceAsStream("mol_testAliasAtomNaming.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        reader.read(molecule);
        reader.close();

        IAtom[] atoms = AtomContainerManipulator.getAtomArray(molecule);

        int r1Count = 0;
        for (IAtom atom : atoms) {
            if (atom instanceof IPseudoAtom) {
                Assertions.assertEquals("R1", ((IPseudoAtom) atom).getLabel());
                r1Count++;
            }
        }
        Assertions.assertEquals(2, r1Count);
    }

    @Test
    void testPseudoAtomLabels() throws Exception {
        InputStream in = getClass().getResourceAsStream("pseudoatoms.sdf");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        Assertions.assertTrue(molecule.getAtom(4) instanceof IPseudoAtom);
        Assertions.assertEquals("R", molecule.getAtom(4).getSymbol());
        IPseudoAtom pa = (IPseudoAtom) molecule.getAtom(4);
        Assertions.assertEquals("Gln", pa.getLabel());
    }

    /**
     * @cdk.bug 3485634
     */
    @Test
    void testMissingAtomProperties() throws Exception {
        InputStream in = getClass().getResourceAsStream("bug3485634.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        Assertions.assertEquals(9, molecule.getAtomCount());
    }

    @Test
    void testBondOrderFour() throws Exception {
        InputStream in = getClass().getResourceAsStream("mdlWithBond4.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        Assertions.assertEquals(9, molecule.getAtomCount());
        Assertions.assertEquals(Order.UNSET, molecule.getBond(0).getOrder());
        Assertions.assertTrue(molecule.getBond(0).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertEquals(Order.SINGLE, molecule.getBond(1).getOrder());
        Assertions.assertFalse(molecule.getBond(1).getFlag(IChemObject.SINGLE_OR_DOUBLE));
    }

    @Test
    void testAtomParity() throws CDKException, IOException {

        InputStream in = getClass().getResourceAsStream("mol_testAtomParity.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        Assertions.assertEquals(6, molecule.getAtomCount());
        boolean chiralCentre = false;
        IAtom[] atoms = AtomContainerManipulator.getAtomArray(molecule);
        for (IAtom atom : atoms) {
            Integer parity = atom.getStereoParity();
            if (parity == 1) {
                chiralCentre = true;
            }
        }

        Assertions.assertTrue(chiralCentre);

    }

    @Test
    void testSingleSingletRadical() throws Exception {

        InputStream in = getClass().getResourceAsStream("singleSingletRadical.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(1)), is(2));
    }

    @Test
    void testSingleDoubletRadical() throws Exception {

        InputStream in = getClass().getResourceAsStream("singleDoubletRadical.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(1)), is(1));
    }

    @Test
    void testSingleTripletRadical() throws Exception {

        InputStream in = getClass().getResourceAsStream("singleTripletRadical.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(1)), is(2));
    }

    @Test
    void testMultipleRadicals() throws Exception {

        InputStream in = getClass().getResourceAsStream("multipleRadicals.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(0)), is(1));
        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(1)), is(1));
        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(2)), is(1));
        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(3)), is(1));
        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(4)), is(1));
        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(5)), is(1));
        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(6)), is(1));
        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(7)), is(1));
        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(8)), is(1));
    }

    @Test
    void fe_iii_valence() throws Exception {
        InputStream in = getClass().getResourceAsStream("iron-iii.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        assertThat(molecule.getAtom(0).getImplicitHydrogenCount(), is(1));
        assertThat(molecule.getAtom(1).getImplicitHydrogenCount(), is(0));
        assertThat(molecule.getAtom(2).getImplicitHydrogenCount(), is(0));
    }

    @Test
    void bismuth_ion_valence() throws Exception {
        InputStream in = getClass().getResourceAsStream("bismuth-ion.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        assertThat(molecule.getAtom(0).getImplicitHydrogenCount(), is(3));
    }

    @Test
    void e_butene_2D() throws Exception {
        InputStream in = getClass().getResourceAsStream("e_butene_2d.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        Assertions.assertTrue(molecule.stereoElements().iterator().hasNext());
    }

    // when there are no coordinates stereo perception should not be done
    @Test
    void e_butene_0D() throws Exception {
        InputStream in = getClass().getResourceAsStream("e_butene_0d.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        Assertions.assertNotNull(molecule);
        Assertions.assertFalse(molecule.stereoElements().iterator().hasNext());
    }

    // forcing as 3D is problematic for stereo perception as we put 2D coordinates
    // in to 3D as we then no longer know to check wedge/hatch labels.
    @Test
    void e_butene_2D_force3D() throws Exception {
        InputStream in = getClass().getResourceAsStream("e_butene_2d.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);

        // yuk!
        Properties prop = new Properties();
        prop.setProperty("ForceReadAs3DCoordinates", "true");
        PropertiesListener listener = new PropertiesListener(prop);
        reader.addChemObjectIOListener(listener);
        reader.customizeJob();

        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        Assertions.assertNotNull(molecule);
        Assertions.assertFalse(molecule.stereoElements().iterator().hasNext());
    }

    @Test
    void e_butene_3D() throws Exception {
        InputStream in = getClass().getResourceAsStream("e_butene_3d.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);

        Properties prop = new Properties();
        prop.setProperty("ForceReadAs3DCoordinates", "true");
        PropertiesListener listener = new PropertiesListener(prop);
        reader.addChemObjectIOListener(listener);
        reader.customizeJob();

        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        Assertions.assertNotNull(molecule);
        Assertions.assertTrue(molecule.stereoElements().iterator().hasNext());
    }

    // turn off adding stereoelements
    @Test
    void e_butene_2D_optOff() throws Exception {
        InputStream in = getClass().getResourceAsStream("e_butene_2d.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);

        Properties prop = new Properties();
        prop.setProperty("AddStereoElements", "false");
        PropertiesListener listener = new PropertiesListener(prop);
        reader.addChemObjectIOListener(listener);
        reader.customizeJob();

        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        Assertions.assertNotNull(molecule);
        Assertions.assertFalse(molecule.stereoElements().iterator().hasNext());
    }

    @Test
    void dataHeader_1() {
        assertThat(MDLV2000Reader.dataHeader("> 29 <DENSITY> "), is("DENSITY"));
    }

    @Test
    void dataHeader_2() {
        assertThat(MDLV2000Reader.dataHeader("> <MELTING.POINT> "), is("MELTING.POINT"));
    }

    @Test
    void dataHeader_3() {
        assertThat(MDLV2000Reader.dataHeader("> 55 (MD-08974) <BOILING.POINT> DT12"), is("BOILING.POINT"));
    }

    @Test
    void readNonStructuralData() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("> 29 <DENSITY>").append('\n');
        sb.append("0.9132 - 20.0").append('\n');
        sb.append('\n');
        sb.append("> 29 <BOILING.POINT>").append('\n');
        sb.append("63.0 (737 MM)").append('\n');
        sb.append("79.0 (42 MM)").append('\n');
        sb.append('\n');
        sb.append("> 29 <ALTERNATE.NAMES>").append('\n');
        sb.append("SYLVAN").append('\n');
        sb.append('\n');
        sb.append("> 29 <DATE>").append('\n');
        sb.append("09-23-1980").append('\n');
        sb.append('\n');
        sb.append("> 29 <CRC.NUMBER>").append('\n');
        sb.append("F-0213").append('\n');
        sb.append('\n');

        BufferedReader input = new BufferedReader(new StringReader(sb.toString()));
        IAtomContainer mock = mock(IAtomContainer.class);

        MDLV2000Reader.readNonStructuralData(input, mock);

        verify(mock).setProperty("DENSITY", "0.9132 - 20.0");
        verify(mock).setProperty("BOILING.POINT", "63.0 (737 MM)\n79.0 (42 MM)");
        verify(mock).setProperty("ALTERNATE.NAMES", "SYLVAN");
        verify(mock).setProperty("DATE", "09-23-1980");
        verify(mock).setProperty("CRC.NUMBER", "F-0213");
    }

    @Test
    void readNonStructuralData_emtpy() throws Exception {
        // a single space is read as a property
        StringBuilder sb = new StringBuilder();
        sb.append("> <ONE_SPACE>").append('\n');
        sb.append(" ").append('\n');
        sb.append('\n');
        // empty entries are read as non-null - so m.getProperty() does not
        // return null
        sb.append("> <EMTPY_LINES>").append('\n');
        sb.append('\n');
        sb.append('\n');
        sb.append('\n');

        BufferedReader input = new BufferedReader(new StringReader(sb.toString()));
        IAtomContainer mock = mock(IAtomContainer.class);

        MDLV2000Reader.readNonStructuralData(input, mock);

        verify(mock).setProperty("ONE_SPACE", " ");
        verify(mock).setProperty("EMTPY_LINES", "");
    }

    @Test
    void readNonStructuralData_wrap() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("> <LONG_PROPERTY>").append('\n');
        sb.append("This is a long property which should be wrapped when stored as field in an SDF D");
        sb.append('\n');
        sb.append("ata entry");
        sb.append('\n');

        BufferedReader input = new BufferedReader(new StringReader(sb.toString()));
        IAtomContainer mock = mock(IAtomContainer.class);

        MDLV2000Reader.readNonStructuralData(input, mock);

        verify(mock).setProperty("LONG_PROPERTY",
                "This is a long property which should be wrapped when stored as field in an SDF Data entry");

    }

    /**
     * Ensure having a property with 2 new line lines will still allow 2 entries
     * to be read - a bug from the mailing list.
     */
    @Test
    void testMultipleNewlinesInSDFProperty() throws Exception {
        InputStream in = getClass().getResourceAsStream("multiplenewline-property.sdf");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        assertThat(ChemFileManipulator.getAllAtomContainers(chemFile).size(), is(2));
    }

    @Test
    void testAliasAfterRgroup() throws Exception {
        InputStream in = getClass().getResourceAsStream("r-group-with-alias.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer container = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        assertThat(container.getAtom(6), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) container.getAtom(6)).getLabel(), is("R6"));
        assertThat(container.getAtom(7), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) container.getAtom(7)).getLabel(), is("Protein"));
    }

    @Test
    void keepAtomicNumberOfAlias() throws Exception {
        InputStream in = getClass().getResourceAsStream("element-with-alias.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer container = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        assertThat(container.getAtom(6), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) container.getAtom(6)).getLabel(), is("N1"));
        assertThat(container.getAtom(6).getAtomicNumber(), is(7));
    }

    @Test
    void v2000Version() {
        assertThat(MDLV2000Reader.CTabVersion.ofHeader("  5  5  0  0  0  0            999 V2000"),
                is(MDLV2000Reader.CTabVersion.V2000));
        assertThat(MDLV2000Reader.CTabVersion.ofHeader("  5  5  0  0  0  0            999 v2000"),
                is(MDLV2000Reader.CTabVersion.V2000));
    }

    @Test
    void v3000Version() {
        assertThat(MDLV2000Reader.CTabVersion.ofHeader("  0  0  0  0  0  0            999 V3000"),
                is(MDLV2000Reader.CTabVersion.V3000));
        assertThat(MDLV2000Reader.CTabVersion.ofHeader("  0  0  0  0  0  0            999 v3000"),
                is(MDLV2000Reader.CTabVersion.V3000));
    }

    @Test
    void unspecVersion() {
        assertThat(MDLV2000Reader.CTabVersion.ofHeader("  5  5  0  0  0  0            999"),
                is(MDLV2000Reader.CTabVersion.UNSPECIFIED));
        assertThat(MDLV2000Reader.CTabVersion.ofHeader("  5  5  0  0  0  0            999      "),
                is(MDLV2000Reader.CTabVersion.UNSPECIFIED));
    }

    @Test
    void radicalsReflectedInHydrogenCount() throws Exception {
        MDLV2000Reader r = new MDLV2000Reader(getClass().getResourceAsStream("structure-with-radical.mol"));
        IAtomContainer m = r.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        r.close();
        assertThat(m.getAtom(0).getAtomicNumber(), is(8));
        assertThat(m.getAtom(0).getImplicitHydrogenCount(), is(0));
    }

    /**
     * @cdk.bug 1326
     */
    @Test
    void nonNegativeHydrogenCount() throws Exception {
        InputStream in = getClass().getResourceAsStream("ChEBI_30668.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer container = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        for (IAtom atom : container.atoms()) {
            assertThat(atom.getImplicitHydrogenCount(), is(greaterThanOrEqualTo(0)));
            assertThat(atom.getValency(), is(notNullValue()));
        }
    }

    /**
     * @cdk.bug 1343
     */
    @Test
    void nonNegativeHydrogenCountOnHydrogenRadical() throws Exception {
        InputStream in = getClass().getResourceAsStream("ChEBI_29293.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer container = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();
        assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(0));
        assertThat(container.getAtom(1).getImplicitHydrogenCount(), is(0));
    }

    /**
     * The non-standard ACDLabs atom label property should throw a CDKException in STRICT mode.
     */
    @Test
    void testAcdChemSketchLabel_Strict() {

        String filename = "chemsketch-all-labelled.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
                                });
    }

    /**
     * Test a simple ChemSketch label containing an integer.
     * @throws Exception
     */
    @Test
    void testAcdChemSketchLabel() throws Exception {
        String filename = "chemsketch-one-label.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();

        assertThat(mol.getAtom(1).getProperty(CDKConstants.ACDLABS_LABEL), is("6"));
    }

    /**
     * Test ChemSketch labels containing all non-whitespace printable ASCII characters.
     * @throws Exception
     */
    @Test
    void testAcdChemSketchLabel_PrintableAscii() throws Exception {
        String filename = "chemsketch-printable-ascii.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();

        // Printable ASCII characters, excluding whitespace. Note each string contains an atom number
        String[] expected =
        {
            "!\"#$%&'()*+,-./0123456789:;<=>?@[\\]^_`{|}~",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ1abcdefghijklmnopqrstuvwxyz",
            "012345678901234567890123456789012345678901234567890"
        };
        assertThat(mol.getAtom(0).getProperty(CDKConstants.ACDLABS_LABEL), is(expected[0]));
        assertThat(mol.getAtom(1).getProperty(CDKConstants.ACDLABS_LABEL), is(expected[1]));
        assertThat(mol.getAtom(2).getProperty(CDKConstants.ACDLABS_LABEL), is(expected[2]));
    }

    /**
     * Check that multiple atom labels are all read.
     * @throws Exception
     */
    @Test
    void testAcdChemSketchLabel_AllAtomsLabelled() throws Exception {
        String filename = "chemsketch-all-labelled.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();

        Iterable<IAtom> atoms = mol.atoms();
        for (IAtom atom : atoms){
            Assertions.assertNotNull(atom.getProperty(CDKConstants.ACDLABS_LABEL));
        }
    }

    /**
     * Check that leading and trailing whitespace in atom labels is preserved on reading.
     * @throws Exception
     */
    @Test
    void testAcdChemSketchLabel_LeadingTrailingWhitespace() throws Exception {
        String filename = "chemsketch-leading-trailing-space.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();

        // Leading and trailing whitespace in both prefix and suffix
        String expected = " a 1 b ";
        assertThat(mol.getAtom(0).getProperty(CDKConstants.ACDLABS_LABEL), is(expected));
    }

    /**
     * Check that embedded whitespace in atom labels is preserved on reading.
     * @throws Exception
     */
    @Test
    void testAcdChemSketchLabel_EmbeddedWhitespace() throws Exception {
        String filename = "chemsketch-embedded-space.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();

        // Embedded whitespace in both prefix and suffix
        String expected = "a b1c d";
        assertThat(mol.getAtom(0).getProperty(CDKConstants.ACDLABS_LABEL), is(expected));
    }

    /**
     * Check reading of largest permissible label (50 char prefix + 3 digits + 50 char suffix).
     * @throws Exception
     */
    @Test
    void testAcdChemSketchLabel_MaxSizeLabel() throws Exception {
        String filename = "chemsketch-longest-label.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        reader.close();

        // Longest allowed atom label is 103 characters
        String prefix = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwx";
        String digits = "999";
        String suffix = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwx";
        String expected = prefix + digits + suffix;

        assertThat(mol.getAtom(0).getProperty(CDKConstants.ACDLABS_LABEL), is(expected));
    }

    @Test
    void testSgroupAbbreviation() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-abbrv.mol"))) {
            final IAtomContainer container = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
            Assertions.assertNotNull(sgroups);
            assertThat(sgroups.size(), is(1));
            Sgroup sgroup = sgroups.get(0);
            assertThat(sgroup.getType(), is(SgroupType.CtabAbbreviation));
            assertThat(sgroup.getSubscript(), is("Cs2CO3"));
            assertThat(sgroup.getAtoms().size(), is(6));
        }
    }

    @Test
    void testSgroupRepeatUnit() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-sru.mol"))) {
            IAtomContainer container = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
            Assertions.assertNotNull(sgroups);
            assertThat(sgroups.size(), is(1));
            Sgroup sgroup = sgroups.get(0);
            assertThat(sgroup.getType(), is(SgroupType.CtabStructureRepeatUnit));
            assertThat(sgroup.getSubscript(), is("n"));
            assertThat(sgroup.getValue(SgroupKey.CtabConnectivity), is("HT"));
            assertThat(sgroup.getAtoms().size(), is(10));
            assertThat(sgroup.getBonds().size(), is(2));
            List<SgroupBracket> brackets = sgroup.getValue(SgroupKey.CtabBracket);
            assertThat(brackets.size(), is(2));
            // M  SDI   1  4    2.2579   -0.8756    1.7735   -1.6600
            assertThat(brackets.get(0).getFirstPoint().x, closeTo(2.2579, 0.001));
            assertThat(brackets.get(0).getFirstPoint().y, closeTo(-0.8756, 0.001));
            assertThat(brackets.get(0).getSecondPoint().x, closeTo(1.7735, 0.001));
            assertThat(brackets.get(0).getSecondPoint().y, closeTo(-1.6600, 0.001));
            // M  SDI   1  4   -0.9910   -1.7247   -0.4960   -0.8673
            assertThat(brackets.get(1).getFirstPoint().x, closeTo(-0.9910, 0.001));
            assertThat(brackets.get(1).getFirstPoint().y, closeTo(-1.7247, 0.001));
            assertThat(brackets.get(1).getSecondPoint().x, closeTo(-0.4960, 0.001));
            assertThat(brackets.get(1).getSecondPoint().y, closeTo(-0.8673, 0.001));

        }
    }

    @Test
    void testSgroupUnorderedMixture() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-unord-mixture.mol"))) {
            IAtomContainer container = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
            Assertions.assertNotNull(sgroups);
            assertThat(sgroups.size(), is(3));
            // first sgroup
            Sgroup sgroup = sgroups.get(0);
            assertThat(sgroup.getType(), is(SgroupType.CtabComponent));
            assertThat(sgroup.getParents().size(), is(1));
            assertThat(sgroup.getParents(), hasItem(sgroups.get(2)));
            // second sgroup
            sgroup = sgroups.get(1);
            assertThat(sgroup.getType(), is(SgroupType.CtabComponent));
            assertThat(sgroup.getParents().size(), is(1));
            assertThat(sgroup.getParents(), hasItem(sgroups.get(2)));
            // third sgroup
            sgroup = sgroups.get(2);
            assertThat(sgroup.getType(), is(SgroupType.CtabMixture));
            assertThat(sgroup.getParents().size(), is(0));
        }
    }

    @Test
    void testSgroupExpandedAbbreviation() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("triphenyl-phosphate-expanded.mol"))) {
            IAtomContainer container = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
            Assertions.assertNotNull(sgroups);
            assertThat(sgroups.size(), is(3));
            // first sgroup
            Sgroup sgroup = sgroups.get(0);
            assertThat(sgroup.getType(), is(SgroupType.CtabAbbreviation));
            assertThat(sgroup.getSubscript(), is("Ph"));
            Assertions.assertNotNull(sgroup.getValue(SgroupKey.CtabExpansion));
            // second sgroup
            sgroup = sgroups.get(1);
            assertThat(sgroup.getType(), is(SgroupType.CtabAbbreviation));
            assertThat(sgroup.getSubscript(), is("Ph"));
            Assertions.assertNotNull(sgroup.getValue(SgroupKey.CtabExpansion));
            // third sgroup
            sgroup = sgroups.get(2);
            assertThat(sgroup.getType(), is(SgroupType.CtabAbbreviation));
            assertThat(sgroup.getSubscript(), is("Ph"));
            Assertions.assertNotNull(sgroup.getValue(SgroupKey.CtabExpansion));
        }
    }

    @Test
    void testSgroupInvalidConnectInStrictMode() {
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-sru-bad-scn.mol"))) {
                                        mdlr.setReaderMode(Mode.STRICT);
                                        mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
                                    }
                                });
    }

    @Test
    void testSgroupDefOrderInStrictMode() {
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-sru-bad-def.mol"))) {
                                        mdlr.setReaderMode(Mode.STRICT);
                                        mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
                                    }
                                });
    }

    @Test
    void testSgroupBracketStyle() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-sru-bracketstyles.mol"))) {
            IAtomContainer container = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
            Assertions.assertNotNull(sgroups);
            assertThat(sgroups.size(), is(2));
            Sgroup sgroup = sgroups.get(0);
            assertThat(sgroup.getType(), is(SgroupType.CtabStructureRepeatUnit));
            assertThat(sgroup.getValue(SgroupKey.CtabBracketStyle), is(1));
            sgroup = sgroups.get(1);
            assertThat(sgroup.getType(), is(SgroupType.CtabStructureRepeatUnit));
            assertThat(sgroup.getValue(SgroupKey.CtabBracketStyle), is(1));
        }
    }

    @Test
    void testReading0DStereochemistry() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("tetrahedral-parity-withImplH.mol"))) {
            IAtomContainer container = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            Iterable<IStereoElement> selements = container.stereoElements();
            Iterator<IStereoElement> siter = selements.iterator();
            Assertions.assertTrue(siter.hasNext());
            IStereoElement se = siter.next();
            assertThat(se, is(instanceOf(ITetrahedralChirality.class)));
            assertThat(((ITetrahedralChirality) se).getStereo(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
            assertThat(((ITetrahedralChirality) se).getLigands(), is(new IAtom[]{container.getAtom(1), container.getAtom(3), container.getAtom(4), container.getAtom(0)}));
            Assertions.assertFalse(siter.hasNext());
        }
    }

    // explicit Hydrogen can reverse winding
    @Test
    void testReading0DStereochemistryWithHydrogen() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("tetrahedral-parity-withExpH.mol"))) {
            IAtomContainer container = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            Iterable<IStereoElement> selements = container.stereoElements();
            Iterator<IStereoElement> siter = selements.iterator();
            Assertions.assertTrue(siter.hasNext());
            IStereoElement se = siter.next();
            assertThat(se, is(instanceOf(ITetrahedralChirality.class)));
            assertThat(((ITetrahedralChirality) se).getStereo(), is(ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
            assertThat(((ITetrahedralChirality) se).getLigands(), is(new IAtom[]{container.getAtom(0), container.getAtom(2), container.getAtom(3), container.getAtom(4)}));
            Assertions.assertFalse(siter.hasNext());
        }
    }

    /**
     * When atomic mass is defined as a delta some atoms don't have a reasonable
     * default. Most tools will output an 'M  ISO' property, so can be specified
     */
    @Test
    void seaborgiumMassDelta() {
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    try (InputStream in = getClass().getResourceAsStream("seaborgium.mol");
                                         MDLV2000Reader mdlr = new MDLV2000Reader(in, Mode.STRICT)) {
                                        mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
                                    }
                                });
    }

    @Test
    void seaborgiumAbsMass() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("seaborgium_abs.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in, Mode.STRICT)) {
            IAtomContainer mol = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            assertThat(mol.getAtom(0).getMassNumber(), is(261));
        }
    }

	@Test
    void testMassDiff() throws Exception {
        String mdl = "deuterium.mol\n" + "\n" + "\n" + "  1  0  0  0  0                 1 V2000\n"
                + "    0.0000    0.0000    0.0000 H  +1  0  0  0  0\n"
                + "M  END\n";
        try (MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdl), Mode.STRICT)) {
            IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            IAtom atom = mol.getAtom(0);
            Assertions.assertEquals(1, atom.getAtomicNumber().intValue());
            Assertions.assertEquals(2, atom.getMassNumber().intValue());
        }
    }

    @Test
    void testBadAtomCoordinateFormat() throws Exception {

        final String mol = "\n" +
                "\n" +
                "\n" +
                " 17 18  0  0  0  0              1 V2000\n" +
                "  -2.31474   0.50167  -3.30968 C   0  0  0  0  0  0\n" +
                "  -2.43523  -0.19508  -2.16895 C   0  0  0  0  0  0\n" +
                "  -1.14825   1.44681  -3.13364 C   0  0  0  0  0  0\n" +
                "  -1.34937   0.28780  -1.23468 C   0  0  0  0  0  0\n" +
                "  -1.31596   1.76332  -1.64443 C   0  0  0  0  0  0\n" +
                "   0.00411  -0.20225  -1.79002 C   0  0  0  0  0  0\n" +
                "   0.14340   0.60304  -3.11198 C   0  0  0  0  0  0\n" +
                "  -1.14836   2.30981  -3.79997 H   0  0  0  0  0  0\n" +
                "   0.23461  -0.05636  -3.98172 H   0  0  0  0  0  0\n" +
                "   1.03045   1.24486  -3.06787 H   0  0  0  0  0  0\n" +
                "   0.82300   0.05318  -1.10813 H   0  0  0  0  0  0\n" +
                "   0.02443  -1.28271  -1.96799 H   0  0  0  0  0  0\n" +
                "  -1.53255   0.09851  -0.17666 H   0  0  0  0  0  0\n" +
                "  -0.46658   2.31096  -1.22026 H   0  0  0  0  0  0\n" +
                "  -2.24286   2.30318  -1.41306 H   0  0  0  0  0  0\n" +
                "  -3.13662  -0.99036  -1.96785 H   0  0  0  0  0  0\n" +
                "  -2.90004   0.37818  -4.20802 H   0  0  0  0  0  0\n" +
                "  1  2  2  0  0  0\n" +
                "  1  3  1  0  0  0\n" +
                "  2  4  1  0  0  0\n" +
                "  4  5  1  0  0  0\n" +
                "  3  5  1  0  0  0\n" +
                "  4  6  1  0  0  0\n" +
                "  6  7  1  0  0  0\n" +
                "  7  3  1  0  0  0\n" +
                "  3  8  1  0  0  0\n" +
                "  7  9  1  0  0  0\n" +
                "  7 10  1  0  0  0\n" +
                "  6 11  1  0  0  0\n" +
                "  6 12  1  0  0  0\n" +
                "  4 13  1  0  0  0\n" +
                "  5 14  1  0  0  0\n" +
                "  5 15  1  0  0  0\n" +
                "  2 16  1  0  0  0\n" +
                "  1 17  1  0  0  0\n" +
                "M  END\n" +
                "\n";
        final MDLV2000Reader mdlv2000Reader = new MDLV2000Reader(new ByteArrayInputStream(mol.getBytes(StandardCharsets.UTF_8)));
        mdlv2000Reader.setReaderMode(IChemObjectReader.Mode.RELAXED);
        final IAtomContainer atomContainer = mdlv2000Reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        Assertions.assertEquals(17, atomContainer.getAtomCount());
    }

    @Test
    void test() throws Exception {
        String input = "\n" +
                       "Structure query\n" +
                       "\n" +
                       "  1  0  0  0  0  0  0  0  0  0999 V2000\n" +
                       " 2430.7100 2427.0000    0.0000 C   0  0  0  0  0  0\n" +
                       "A   1\n" +
                       "Blah\n" +
                       "M  END";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(new StringReader(input))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            assertThat(mol.getAtom(0),
                       instanceOf(IPseudoAtom.class));
            assertThat(((IPseudoAtom)mol.getAtom(0)).getLabel(),
                       is("Blah"));
        }
    }


    @Test
    void atomList() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("query_atomlist.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in)) {
            IQueryAtomContainer mol       = mdlr.read(new QueryAtomContainer(SilentChemObjectBuilder.getInstance()));
            IAtom               deref     = AtomRef.deref(mol.getAtom(0));
            assertThat(deref, CoreMatchers.instanceOf(QueryAtom.class));
            QueryAtom           queryAtom = (QueryAtom) deref;
            Expr expr = queryAtom.getExpression();
            Expr expected = new Expr(Expr.Type.ELEMENT, 9) // F
                .or(new Expr(Expr.Type.ELEMENT, 7)) // N
                .or(new Expr(Expr.Type.ELEMENT, 8)); // O
            assertThat(expr, is(expected));
        }
    }
    @Test
    void legacyAtomList() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("query_legacyatomlist.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in)) {
            IQueryAtomContainer mol       = mdlr.read(new QueryAtomContainer(SilentChemObjectBuilder.getInstance()));
            IAtom               deref     = AtomRef.deref(mol.getAtom(0));
            assertThat(deref, CoreMatchers.instanceOf(QueryAtom.class));
            QueryAtom           queryAtom = (QueryAtom) deref;
            Expr expr = queryAtom.getExpression();
            Expr expected = new Expr(Expr.Type.ELEMENT, 9) // F
                    .or(new Expr(Expr.Type.ELEMENT, 7)) // N
                    .or(new Expr(Expr.Type.ELEMENT, 8)); // O
            assertThat(expr, is(expected));
        }
    }

    @Test
    void notatomList() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("query_notatomlist.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in)) {
            IQueryAtomContainer mol       = mdlr.read(new QueryAtomContainer(SilentChemObjectBuilder.getInstance()));
            IAtom               deref     = AtomRef.deref(mol.getAtom(0));
            assertThat(deref, CoreMatchers.instanceOf(QueryAtom.class));
            QueryAtom           queryAtom = (QueryAtom) deref;
            Expr expr = queryAtom.getExpression();
            Expr expected = new Expr(Expr.Type.ELEMENT, 9) // F
                                   .or(new Expr(Expr.Type.ELEMENT, 7)) // N
                                   .or(new Expr(Expr.Type.ELEMENT, 8)); // O
            expected.negate();
            assertThat(expr, is(expected));
        }
    }
    @Test
    void legacynotatomList() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("query_legacynotatomlist.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in)) {
            IQueryAtomContainer mol       = mdlr.read(new QueryAtomContainer(SilentChemObjectBuilder.getInstance()));
            IAtom               deref     = AtomRef.deref(mol.getAtom(0));
            assertThat(deref, CoreMatchers.instanceOf(QueryAtom.class));
            QueryAtom           queryAtom = (QueryAtom) deref;
            Expr expr = queryAtom.getExpression();
            Expr expected = new Expr(Expr.Type.ELEMENT, 9) // F
                    .or(new Expr(Expr.Type.ELEMENT, 7)) // N
                    .or(new Expr(Expr.Type.ELEMENT, 8)); // O
            expected.negate();
            assertThat(expr, is(expected));
        }
    }

    @Test
    void sgroupsAbbrRoundTrip() throws IOException, CDKException {
        StringWriter sw = new StringWriter();
        try (InputStream in = getClass().getResourceAsStream("sgroup-sup.mol3");
             MDLV3000Reader mdlr = new MDLV3000Reader(in);
             MDLV2000Writer mdlw = new MDLV2000Writer(sw)) {
            IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
            mol = mdlr.read(mol);
            mdlw.write(mol);
        }
        assertThat(sw.toString(), containsString("M  SAL   1  2   2   3"));
    }

    @Test
    void checkFuseBondWithFewerBondsThanAtoms() throws IOException, CDKException {
        try (InputStream in = getClass().getResourceAsStream("potentialLateFuse.mol");
            MDLV2000Reader reader = new MDLV2000Reader(in)) {
            IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            assertThat(mol.getAtomCount(), is(108));
        }
    }

    @Test
    void atomlistWithAtomContainer() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("query_notatomlist.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in)) {

            IAtomContainer mol   = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            IAtom          deref = AtomRef.deref(mol.getAtom(0));
            assertThat(deref, CoreMatchers.instanceOf(QueryAtom.class));
        }
    }

    @Test
    void dataSgroup() {
        String path = "hbr_acoh_mix.mol";
        try (InputStream in = getClass().getResourceAsStream(path)) {
            MDLV2000Reader     mdlr     = new MDLV2000Reader(in);
            IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
            IAtomContainer mol = mdlr.read(builder.newAtomContainer());
            List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
            Sgroup dataSgroup = null;
            for (Sgroup sgroup : sgroups) {
                if (sgroup.getType() == SgroupType.CtabData) {
                    dataSgroup = sgroup;
                    break;
                }
            }
            Assertions.assertNotNull(dataSgroup);
            assertThat(dataSgroup.getValue(SgroupKey.DataFieldName),
                       CoreMatchers.is("WEIGHT_PERCENT"));
            // note it looks like MDL/Accelys/BIOVIA simply omit units/format
            // but check we pass it okay
            assertThat(dataSgroup.getValue(SgroupKey.DataFieldUnits),
                       CoreMatchers.is("%"));
            assertThat(dataSgroup.getValue(SgroupKey.DataFieldFormat),
                       CoreMatchers.is("N"));
            assertThat(dataSgroup.getValue(SgroupKey.Data),
                       CoreMatchers.is("33%"));
        } catch (IOException | CDKException e) {
            LoggingToolFactory.createLoggingTool(MDLV2000ReaderTest.class)
                              .warn("Read Error:", e);
        }
    }

    @Test
    void testNoChiralFlag() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052112282D          \n" +
                "\n" +
                "  7  7  0  0  0  0            999 V2000\n" +
                "   -1.1468    6.5972    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n" +
                "   -1.8613    6.1847    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.8613    5.3597    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.1468    4.9472    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.4323    5.3597    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.4323    6.1847    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.1468    7.4222    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  1  6  1  0  0  0  0\n" +
                "  1  7  1  1  0  0  0\n" +
                "M  END\n";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(new StringReader(input))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            Iterable<IStereoElement> iter = mol.stereoElements();
            Assertions.assertTrue(iter.iterator().hasNext());
            for (IStereoElement<?,?> se : iter) {
                assertThat(se.getGroupInfo(), is(IStereoElement.GRP_RAC1));
            }
        }
    }

    @Test
    void testChiralFlag() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052112282D          \n" +
                "\n" +
                "  7  7  0  0  1  0            999 V2000\n" +
                "   -1.1468    6.5972    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n" +
                "   -1.8613    6.1847    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.8613    5.3597    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.1468    4.9472    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.4323    5.3597    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.4323    6.1847    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.1468    7.4222    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  1  6  1  0  0  0  0\n" +
                "  1  7  1  1  0  0  0\n" +
                "M  END\n";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(new StringReader(input))) {
            IAtomContainer mol = mdlr.read(bldr.newAtomContainer());
            Iterable<IStereoElement> iter = mol.stereoElements();
            Assertions.assertTrue(iter.iterator().hasNext());
            for (IStereoElement<?,?> se : iter) {
                // Grp Abs is actually just 0
                assertThat(se.getGroupInfo(), is(IStereoElement.GRP_ABS));
            }
        }
    }

    @Test
    void testAtomIndexBelowValidRangeInBondsBlock() throws Exception {
        // arrange
        final String input = "\n" +
                "  Mrv2221 01162322282D          \n" +
                "\n" +
                " 40 46  0  0  0  0            999 V2000\n" +
                "    6.8727   -0.3790    0.0000 C   0  0  0  0  0  0  0  0  0  1  0  0\n" +
                "    6.8727   -1.2041    0.0000 C   0  0  0  0  0  0  0  0  0  2  0  0\n" +
                "    7.5870   -1.6166    0.0000 N   0  0  0  0  0  0  0  0  0  3  0  0\n" +
                "    8.3015   -1.2041    0.0000 C   0  0  0  0  0  0  0  0  0  4  0  0\n" +
                "    8.3015   -0.3790    0.0000 C   0  0  0  0  0  0  0  0  0  5  0  0\n" +
                "    7.5870    0.0333    0.0000 C   0  0  0  0  0  0  0  0  0  6  0  0\n" +
                "    9.0861   -1.4590    0.0000 N   0  0  0  0  0  0  0  0  0  7  0  0\n" +
                "    9.5711   -0.7916    0.0000 C   0  0  0  0  0  0  0  0  0 27  0  0\n" +
                "    9.0861   -0.1242    0.0000 C   0  0  0  0  0  0  0  0  0 28  0  0\n" +
                "    9.3411    0.6604    0.0000 C   0  0  0  0  0  0  0  0  0 25  0  0\n" +
                "   10.3960   -0.7916    0.0000 C   0  0  0  0  0  0  0  0  0 29  0  0\n" +
                "   10.1479    0.8320    0.0000 C   0  0  0  0  0  5  0  0  0 20  0  0\n" +
                "   10.8085   -1.5060    0.0000 C   0  0  0  0  0  0  0  0  0 30  0  0\n" +
                "   11.6335   -1.5060    0.0000 C   0  0  0  0  0  0  0  0  0 31  0  0\n" +
                "   12.0459   -0.7916    0.0000 C   0  0  0  0  0  0  0  0  0 32  0  0\n" +
                "   11.6335   -0.0771    0.0000 C   0  0  0  0  0  0  0  0  0 33  0  0\n" +
                "   10.8085   -0.0771    0.0000 C   0  0  0  0  0  0  0  0  0 34  0  0\n" +
                "   12.8709   -0.7916    0.0000 Cl  0  0  0  0  0  0  0  0  0 42  0  0\n" +
                "    6.1582    0.0333    0.0000 Cl  0  0  0  0  0  0  0  0  0  9  0  0\n" +
                "   10.4029    1.6166    0.0000 O   0  0  0  0  0  0  0  0  0 21  0  0\n" +
                "   10.7000    0.2189    0.0000 O   0  0  0  0  0  0  0  0  0 22  0  0\n" +
                "   11.4144    0.6313    0.0000 C   0  0  0  0  0  0  0  0  0 23  0  0\n" +
                "    6.8727   -0.3790    0.0000 C   0  0  0  0  0  0  0  0  0  1  0  0\n" +
                "    6.8727   -1.2041    0.0000 C   0  0  0  0  0  0  0  0  0  2  0  0\n" +
                "    7.5870   -1.6166    0.0000 N   0  0  0  0  0  0  0  0  0  3  0  0\n" +
                "    8.3015   -1.2041    0.0000 C   0  0  0  0  0  0  0  0  0  4  0  0\n" +
                "    8.3015   -0.3790    0.0000 C   0  0  0  0  0  0  0  0  0  5  0  0\n" +
                "    7.5870    0.0333    0.0000 C   0  0  0  0  0  0  0  0  0  6  0  0\n" +
                "    9.0861   -1.4590    0.0000 N   0  0  0  0  0  0  0  0  0  7  0  0\n" +
                "    9.5711   -0.7916    0.0000 C   0  0  0  0  0  0  0  0  0 27  0  0\n" +
                "    9.0861   -0.1242    0.0000 C   0  0  0  0  0  0  0  0  0 28  0  0\n" +
                "    9.3411    0.6604    0.0000 C   0  0  0  0  0  0  0  0  0 25  0  0\n" +
                "   10.3960   -0.7916    0.0000 C   0  0  0  0  0  0  0  0  0 29  0  0\n" +
                "   10.8085   -1.5060    0.0000 C   0  0  0  0  0  0  0  0  0 30  0  0\n" +
                "   11.6335   -1.5060    0.0000 C   0  0  0  0  0  0  0  0  0 31  0  0\n" +
                "   12.0459   -0.7916    0.0000 C   0  0  0  0  0  0  0  0  0 32  0  0\n" +
                "   11.6335   -0.0771    0.0000 C   0  0  0  0  0  0  0  0  0 33  0  0\n" +
                "   10.8085   -0.0771    0.0000 C   0  0  0  0  0  0  0  0  0 34  0  0\n" +
                "   12.8709   -0.7916    0.0000 Cl  0  0  0  0  0  0  0  0  0 42  0  0\n" +
                "    6.1582    0.0333    0.0000 Cl  0  0  0  0  0  0  0  0  0  9  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  1  6  2  0  0  0  0\n" +
                "  1 19  1  0  0  0  0\n" +
                "  2  3  2  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  2  0  0  0  0\n" +
                "  4  7  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  5  9  1  0  0  0  0\n" +
                "  7  8  1  0  0  0  0\n" +
                "  8  9  2  0  0  0  0\n" +
                "  8 11  1  0  0  0  0\n" +
                "  9 10  1  0  0  0  0\n" +
                " 11 13  1  0  0  0  0\n" +
                " 11 17  2  0  0  0  0\n" +
                " 12 20  2  0  0  0  0\n" +
                " 12 21  1  0  0  0  0\n" +
                "  0  0  1  0  0  0  0\n" +
                " 13 14  2  0  0  0  0\n" +
                " 14 15  1  0  0  0  0\n" +
                " 15 16  2  0  0  0  0\n" +
                " 15 18  1  0  0  0  0\n" +
                " 16 17  1  0  0  0  0\n" +
                " 21 22  1  0  0  0  0\n" +
                " 23 24  1  0  0  0  0\n" +
                " 23 28  2  0  0  0  0\n" +
                " 23 40  1  0  0  0  0\n" +
                " 24 25  2  0  0  0  0\n" +
                " 25 26  1  0  0  0  0\n" +
                " 26 27  2  0  0  0  0\n" +
                " 26 29  1  0  0  0  0\n" +
                " 27 28  1  0  0  0  0\n" +
                " 27 31  1  0  0  0  0\n" +
                " 29 30  1  0  0  0  0\n" +
                " 30 31  2  0  0  0  0\n" +
                " 30 33  1  0  0  0  0\n" +
                " 31 32  1  0  0  0  0\n" +
                " 33 34  1  0  0  0  0\n" +
                " 33 38  2  0  0  0  0\n" +
                " 34 35  2  0  0  0  0\n" +
                " 35 36  1  0  0  0  0\n" +
                " 36 37  2  0  0  0  0\n" +
                " 36 39  1  0  0  0  0\n" +
                " 37 38  1  0  0  0  0\n" +
                " 40 10  1  0  0  0  0\n" +
                " 12 32  1  0  0  0  0\n" +
                "M  END\n";
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        MDLV2000Reader mdlv2000Reader = new MDLV2000Reader(new StringReader(input));

        // act & assert
        CDKException cdkException = Assertions.assertThrowsExactly(CDKException.class, () -> mdlv2000Reader.read(builder.newAtomContainer()));
        assertThat(cdkException.getMessage(), is("Invalid atom index in bond block in line 62:   0  0  1  0  0  0  0"));

        // tear down
        mdlv2000Reader.close();
    }

    @Test
    void testAtomIndexAboveValidRangeInBondsBlock() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052112282D          \n" +
                "\n" +
                "  7  7  0  0  1  0            999 V2000\n" +
                "   -1.1468    6.5972    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n" +
                "   -1.8613    6.1847    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.8613    5.3597    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.1468    4.9472    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.4323    5.3597    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.4323    6.1847    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.1468    7.4222    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  1  8  1  0  0  0  0\n" +
                "  1  7  1  0  0  0  0\n" +
                "M  END\n";

        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        MDLV2000Reader mdlv2000Reader = new MDLV2000Reader(new StringReader(input));

        // act & assert
        CDKException cdkException = Assertions.assertThrowsExactly(CDKException.class, () -> mdlv2000Reader.read(builder.newAtomContainer()));
        assertThat(cdkException.getMessage(), is("Invalid atom index in bond block in line 17:   1  8  1  0  0  0  0"));

        // tear down
        mdlv2000Reader.close();
    }

    /**
     * WebMolKit and Collaborative Drug Discovery (CDD) have incorrect MOLfiles,
     * that need some "fixing" to work correctly.
     * @throws Exception
     */
    @Test
    void testBadSgroup() throws Exception {
        final String molfile = "\n" +
                "\n" +
                "\n" +
                "  9  9  0  0  0  0  0  0  0  0999 V2000\n" +
                "    6.2366   -3.9875    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    4.6978   -4.4875    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    5.6488   -4.7965    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    4.6978   -3.4875    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    5.6488   -3.1785    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    7.2366   -3.9875    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    7.7366   -3.1215    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    7.7366   -4.8535    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    8.2366   -3.9875    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  5  1  0     0  0\n" +
                "  5  4  2  0     0  0\n" +
                "  4  2  1  0     0  0\n" +
                "  2  3  2  0     0  0\n" +
                "  3  1  1  0     0  0\n" +
                "  1  6  1  0     0  0\n" +
                "  6  7  1  0     0  0\n" +
                "  6  8  1  0     0  0\n" +
                "  6  9  1  0     0  0\n" +
                "M  STY  1   1 SUP\n" +
                "M  SAL   1  1   7\n" +
                "M  SMT   1 Me\n" +
                "M  STY  1   2 SUP\n" +
                "M  SAL   2  1   8\n" +
                "M  SMT   2 Me\n" +
                "M  STY  1   3 SUP\n" +
                "M  SAL   3  1   9\n" +
                "M  SMT   3 Me\n" +
                "M  END\n";

        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        try (MDLV2000Reader mdlv2000Reader = new MDLV2000Reader(new StringReader(molfile))) {
            IAtomContainer mol = mdlv2000Reader.read(builder.newAtomContainer());
            List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
            Assertions.assertEquals(3, sgroups.size());
            for (Sgroup sgroup : sgroups) {
                Assertions.assertEquals(1, sgroup.getBonds().size());
            }
        }
    }

    @Test
    void testNoSuchAtom() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("chebi_48572.sdf"))) {
            mdlr.read(bldr.newAtomContainer());
        }
    }
}
