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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
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
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupBracket;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * TestCase for the reading MDL mol files using one test file.
 * A test case for SDF files is available as separate Class.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLV2000Reader
 * @see org.openscience.cdk.io.SDFReaderTest
 */
public class MDLV2000ReaderTest extends SimpleChemObjectReaderTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLV2000ReaderTest.class);

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new MDLV2000Reader(), "data/mdl/bug682233.mol");
    }

    @Test
    public void testAccepts() {
        MDLV2000Reader reader = new MDLV2000Reader();
        assertTrue(reader.accepts(ChemFile.class));
        assertTrue(reader.accepts(ChemModel.class));
        assertTrue(reader.accepts(AtomContainer.class));
    }

    /**
     * @cdk.bug 3084064
     */
    @Test
    public void testBug3084064() throws Exception {
        String filename = "data/mdl/weirdprops.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();

        Assert.assertNotNull(chemFile);

        List<IAtomContainer> mols = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(10, mols.size());

        IAtomContainer mol = mols.get(0);
        Map<Object, Object> props = mol.getProperties();
        Assert.assertNotNull(props);
        Assert.assertEquals(5, props.size());

        String[] keys = {"DatabaseID", "cdk:Title", "PeaksExplained", "cdk:Remark", "Score"};
        for (String s : keys) {
            boolean found = false;
            for (Object key : props.keySet()) {
                if (s.equals(key)) {
                    found = true;
                    break;
                }
            }
            assertTrue(s + " was not read from the file", found);
        }
    }

    /**
     * @cdk.bug 682233
     */
    @Test
    public void testBug682233() throws Exception {
        String filename = "data/mdl/bug682233.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
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
        Assert.assertEquals(4, m.getAtomCount());
        Assert.assertEquals(2, m.getBondCount());

        // test reading of formal charges
        org.openscience.cdk.interfaces.IAtom a = m.getAtom(0);
        Assert.assertNotNull(a);
        Assert.assertEquals("Na", a.getSymbol());
        Assert.assertEquals(1, a.getFormalCharge().intValue());
        a = m.getAtom(2);
        Assert.assertNotNull(a);
        Assert.assertEquals("O", a.getSymbol());
        Assert.assertEquals(-1, a.getFormalCharge().intValue());
    }

    @Test
    public void testAPinene() throws Exception {
        String filename = "data/mdl/a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        assertTrue(containersList.get(0).getAtomCount() > 0);
        assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    public void testReadingMISOLines() throws Exception {
        String filename = "data/mdl/ChEBI_37340.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        assertTrue(containersList.get(0).getAtomCount() > 0);
        Assert.assertEquals(210, containersList.get(0).getAtom(0).getMassNumber().intValue());
    }

    /**
     * @cdk.bug 2234820
     */
    @Test
    public void testMassNumber() throws Exception {
        String filename = "data/mdl/massnumber.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertEquals(3, containersList.get(0).getAtomCount());
        Assert.assertEquals(2, containersList.get(0).getAtom(1).getMassNumber().intValue());
        Assert.assertEquals(3, containersList.get(0).getAtom(2).getMassNumber().intValue());
    }

    @Test
    public void testAlkane() throws Exception {
        String filename = "data/mdl/shortest_path_test.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        IAtomContainer container = containersList.get(0);
        Assert.assertEquals(10, container.getAtomCount());
        Assert.assertEquals(9, container.getBondCount());
        Iterator<IAtom> atoms = container.atoms().iterator();
        while (atoms.hasNext()) {
            Assert.assertEquals("C", atoms.next().getSymbol());
        }
        Iterator<IBond> bonds = container.bonds().iterator();
        while (bonds.hasNext()) {
            Assert.assertEquals(Order.SINGLE, bonds.next().getOrder());
        }
    }

    @Test
    public void testReadTitle() throws Exception {
        String filename = "data/mdl/a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        Assert.assertEquals("a-pinen.mol", mol.getTitle());
    }

    @Test
    public void testFourRing() throws Exception {
        String filename = "data/mdl/four-ring-5x10.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        assertTrue(containersList.get(0).getAtomCount() > 0);
        assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    public void testHydrozyamino() throws Exception {
        String filename = "data/mdl/hydroxyamino.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        assertTrue(containersList.get(0).getAtomCount() > 0);
        assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    public void testMethylBenzol() throws Exception {
        String filename = "data/mdl/methylbenzol.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        assertTrue(containersList.get(0).getAtomCount() > 0);
        assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    public void testPolycarpol() throws Exception {
        String filename = "data/mdl/polycarpol.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        assertTrue(containersList.get(0).getAtomCount() > 0);
        assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    public void testReserpine() throws Exception {
        String filename = "data/mdl/reserpine.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        assertTrue(containersList.get(0).getAtomCount() > 0);
        assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    public void testSixRing() throws Exception {
        String filename = "data/mdl/six-ring-4x4.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        assertTrue(containersList.get(0).getAtomCount() > 0);
        assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    public void testSuperspiro() throws Exception {
        String filename = "data/mdl/superspiro.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        assertTrue(containersList.get(0).getAtomCount() > 0);
        assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    public void testGhemicalOutput() throws Exception {
        String filename = "data/mdl/butanoic_acid.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        assertTrue(containersList.get(0).getAtomCount() > 0);
        assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test
    public void testUsesGivenMolecule() throws Exception {
        String filename = "data/mdl/superspiro.mol"; // just a random file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer superspiro = new AtomContainer();
        superspiro.setID("superspiro");
        IAtomContainer result = reader.read(superspiro);
        reader.close();
        Assert.assertEquals(superspiro.getID(), result.getID());
    }

    /**
     * @cdk.bug 835571
     */
    @Test
    public void testReadFromStringReader() throws Exception {
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
        Assert.assertEquals(9, m.getAtomCount());
        Assert.assertEquals(9, m.getBondCount());
    }

    @Test
    public void testRGroup() throws Exception {
        String filename = "data/mdl/SARGROUPTEST.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        Assert.assertEquals("R2", ((IPseudoAtom) mol.getAtom(19)).getLabel());
    }

    @Test
    public void testAliasPropertyGroup() throws Exception {
        String filename = "data/mdl/AliasPropertyRGroup.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        IAtom atom = mol.getAtom(0);
        assertTrue(atom instanceof IPseudoAtom);
        Assert.assertEquals("R\\1", ((IPseudoAtom) atom).getLabel());
    }

    /**
     * @cdk.bug 1587283
     */
    @Test
    public void testBug1587283() throws Exception {
        String filename = "data/mdl/bug1587283.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertEquals(15, containersList.get(0).getAtomCount());
        Assert.assertEquals(16, containersList.get(0).getBondCount());
    }

    @Test
    public void testReadProton() throws Exception {
        String mdl = "proton.mol\n" + "\n" + "\n" + "  1  0  0  0  0                 1 V2000\n"
                + "   -0.0073   -0.5272    0.9655 H   0  0  0  0  0\n" + "M  CHG  1   1   1\n" + "M  END\n";
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdl));
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        Assert.assertNotNull(mol);
        Assert.assertEquals(1, mol.getAtomCount());
        Assert.assertEquals(0, mol.getBondCount());
        Assert.assertEquals(1, AtomContainerManipulator.getTotalFormalCharge(mol));
        IAtom atom = mol.getAtom(0);
        Assert.assertEquals(1, atom.getFormalCharge().intValue());
    }

    @Test
    public void testReadingCharges() throws Exception {
        String filename = "data/mdl/withcharges.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        IAtomContainer container = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertEquals(1, container.getAtom(6).getFormalCharge().intValue());
        Assert.assertEquals(-1, container.getAtom(8).getFormalCharge().intValue());
    }

    @Test
    public void testEmptyString() throws Exception {
        String emptyString = "";
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(emptyString));
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        Assert.assertNull(mol);
    }

    @Test
    public void testNoAtomCase() throws Exception {
        String filename = "data/mdl/emptyStructure.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());

        IAtomContainer container = containersList.get(0);
        Assert.assertNotNull(container);
        Assert.assertEquals(0, container.getAtomCount());
        Assert.assertEquals(0, container.getBondCount());

        Map<Object, Object> props = container.getProperties();
        Set<Object> keys = props.keySet();

        assertTrue(keys.contains("SubstanceType"));
        assertTrue(keys.contains("TD50 Rat"));
        assertTrue(keys.contains("ChemCount"));
    }

    /**
     * @cdk.bug 1732307
     */
    @Test
    public void testZeroZCoordinates() throws Exception {
        String filename = "data/mdl/nozcoord.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Properties prop = new Properties();
        prop.setProperty("ForceReadAs3DCoordinates", "true");
        PropertiesListener listener = new PropertiesListener(prop);
        reader.addChemObjectIOListener(listener);
        reader.customizeJob();

        IAtomContainer mol = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        reader.close();
        Assert.assertNotNull(mol);
        Assert.assertEquals(5, mol.getAtomCount());

        boolean has3d = GeometryUtil.has3DCoordinates(mol);
        assertTrue(has3d);
    }

    /**
     * @cdk.bug 1732307
     */
    @Test
    public void testZeroZCoordinates3DMarked() throws Exception {
        String filename = "data/mdl/nozcoord.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        reader.close();
        Assert.assertNotNull(mol);
        Assert.assertEquals(5, mol.getAtomCount());

        boolean has3d = GeometryUtil.has3DCoordinates(mol);
        assertTrue(has3d);
    }

    /**
     * @cdk.bug 1826577
     */
    @Test
    public void testHisotopes_Strict() throws Exception {
        String filename = "data/mdl/hisotopes.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
            reader.read(new ChemFile());
            reader.close();
            Assert.fail("Expected a CDKException");
        } catch (CDKException | IOException exception) {
            // OK, that's what's is supposed to happen
        }
    }

    /**
     * @cdk.bug 1826577
     */
    @Test
    public void testHisotopes_Relaxed() throws Exception {
        String filename = "data/mdl/hisotopes.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.RELAXED);
        IChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertNotNull(containersList.get(0));
        assertFalse((containersList.get(0)).getAtom(1) instanceof IPseudoAtom);
        assertFalse((containersList.get(0)).getAtom(2) instanceof IPseudoAtom);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testReadRadical() throws Exception {
        String filename = "data/mdl/332727182.radical.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        assertTrue((containersList.get(0)).getAtomCount() > 0);
        assertTrue((containersList.get(0)).getBondCount() > 0);
        assertTrue((containersList.get(0)).getSingleElectronCount() > 0);
    }

    /**
     * @cdk.bug 2604888
     */
    @Test
    public void testNoCoordinates() throws Exception {
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
        IAtomContainer molecule = reader.read(new AtomContainer());
        reader.close();
        Assert.assertNotNull(molecule);
        Assert.assertEquals(9, molecule.getAtomCount());
        Assert.assertEquals(9, molecule.getBondCount());
        for (IAtom atom : molecule.atoms()) {
            Assert.assertNull(atom.getPoint2d());
            Assert.assertNull(atom.getPoint2d());
        }
    }

    @Test
    public void testUndefinedStereo() throws Exception {
        String filename = "data/mdl/ChEBI_26120.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        Assert.assertEquals(IBond.Stereo.E_OR_Z, mol.getBond(1).getStereo());
        Assert.assertEquals(IBond.Stereo.E_OR_Z, mol.getBond(6).getStereo());
        Assert.assertEquals(IBond.Stereo.E_OR_Z, mol.getBond(7).getStereo());
        Assert.assertEquals(IBond.Stereo.E_OR_Z, mol.getBond(11).getStereo());
    }

    @Test
    public void testUndefinedStereo2() throws Exception {
        String filename = "data/mdl/a-pinene-with-undefined-stereo.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        Assert.assertEquals(IBond.Stereo.UP_OR_DOWN, mol.getBond(1).getStereo());
    }

    /**
     * Tests that the '0' read from the bond block for bond stereo
     * is read is 'no stereochemistry involved'.
     */
    @Test
    public void testStereoReadZeroDefault() throws Exception {
        String filename = "data/mdl/withcharges.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        IAtomContainer container = containersList.get(0);
        Assert.assertEquals(IBond.Stereo.NONE, container.getBond(0).getStereo());
    }

    @Test
    public void testReadStereoBonds() throws Exception {
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
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        Assert.assertNotNull(mol);
        Assert.assertEquals(9, mol.getAtomCount());
        Assert.assertEquals(9, mol.getBondCount());
        Assert.assertEquals(IBond.Stereo.DOWN, mol.getBond(0).getStereo());
        Assert.assertEquals(IBond.Stereo.UP, mol.getBond(3).getStereo());
    }

    @Test
    public void testStereoDoubleBonds() throws Exception {
        String filename = "data/mdl/butadiene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        IAtomContainer container = containersList.get(0);
        Assert.assertEquals(IBond.Stereo.E_Z_BY_COORDINATES, container.getBond(0).getStereo());
        Assert.assertEquals(IBond.Stereo.E_OR_Z, container.getBond(2).getStereo());
    }

    /**
     * Tests numbering of R# elements according to RGP line.
     * @throws Exception
     */
    @Test
    public void testRGroupHashNumbering() throws Exception {
        String filename = "data/mdl/rgroups.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        for (IBond bond : mol.bonds()) {
            IPseudoAtom rGroup = null;
            IAtom partner = null;
            if (bond.getBegin() instanceof IPseudoAtom) {
                rGroup = (IPseudoAtom) bond.getBegin();
                partner = bond.getEnd();
            } else {
                partner = bond.getBegin();
                rGroup = (IPseudoAtom) bond.getEnd();
            }
            if (partner.getSymbol().equals("N")) {
                Assert.assertEquals(rGroup.getLabel(), "R4");
            } else if (partner.getSymbol().equals("P")) {
                Assert.assertEquals(rGroup.getLabel(), "R1");
            } else if (partner.getSymbol().equals("As")) {
                Assert.assertEquals(rGroup.getLabel(), "R4");
            } else if (partner.getSymbol().equals("Si")) {
                Assert.assertEquals(rGroup.getLabel(), "R");
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
    public void testRGroupHardcodedNumbering() throws Exception {
        String filename = "data/mdl/rgroupsNumbered.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
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
                Assert.assertEquals(rGroup.getLabel(), "R32");
            } else if (bond.getStereo() == IBond.Stereo.DOWN) {
                Assert.assertEquals(rGroup.getLabel(), "R2");
            } else if (bond.getStereo() == IBond.Stereo.UP) {
                Assert.assertEquals(rGroup.getLabel(), "R20");
            } else
                Assert.assertEquals(rGroup.getLabel(), "R5");
        }
    }

    @Test
    public void testReadValence() throws Exception {
        String filename = "data/mdl/a-pinene-with-valence.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);

        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        Assert.assertNotNull(mol);
        Assert.assertEquals(2, mol.getAtom(0).getValency().intValue());
        Assert.assertEquals(3, mol.getAtom(1).getValency().intValue());
        org.hamcrest.MatcherAssert.assertThat(mol.getAtom(2).getValency(), is(not(0)));
        org.hamcrest.MatcherAssert.assertThat(mol.getAtom(2).getValency(), is(4));
        Assert.assertEquals(0, mol.getAtom(3).getValency().intValue());
    }

    @Test
    public void testShortLines() throws Exception {
        logger.info("Testing short lines Mode.RELAXED");
        testShortLinesForMode(Mode.RELAXED);
        logger.info("Testing short lines Mode.STRICT");
        testShortLinesForMode(Mode.STRICT);
    }

    private void testShortLinesForMode(IChemObjectReader.Mode mode) throws Exception {
        String filename = "data/mdl/glycine-short-lines.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, mode);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        Assert.assertNotNull(mol);
        Assert.assertEquals(mol.getAtomCount(), 5);
        Assert.assertEquals(mol.getBondCount(), 4);
    }

    @Test
    public void testReadAtomAtomMapping() throws Exception {
        String filename = "data/mdl/a-pinene-with-atom-atom-mapping.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();
        Assert.assertNotNull(mol);
        Assert.assertEquals(1, ((Integer) mol.getAtom(0).getProperty(CDKConstants.ATOM_ATOM_MAPPING)).intValue());
        Assert.assertEquals(15, ((Integer) mol.getAtom(1).getProperty(CDKConstants.ATOM_ATOM_MAPPING)).intValue());
        Assert.assertNull(mol.getAtom(2).getProperty(CDKConstants.ATOM_ATOM_MAPPING));
    }

    /**
     * @cdk.bug 2936440
     */
    @Test
    public void testHas2DCoordinates_With000() throws Exception {
        String filenameMol = "data/mdl/with000coordinate.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filenameMol);
        IAtomContainer molOne = null;
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molOne = reader.read(new AtomContainer());
        reader.close();
        Assert.assertNotNull(molOne.getAtom(0).getPoint2d());
        Assert.assertNotNull(molOne.getAtom(0).getPoint3d());
    }

    @Test
    public void testAtomValueLines() throws Exception {
        String filename = "data/mdl/atomValueLines.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer testMolecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtomContainer result = reader.read(testMolecule);
        reader.close();
        IAtom oxygen = result.getAtom(0);
        assertTrue(oxygen.getSymbol().equals("O"));
        Assert.assertEquals(oxygen.getProperty(CDKConstants.COMMENT), "Oxygen comment");
    }

    @Test
    public void testDeuterium() throws Exception {
        String filename = "data/mdl/chemblMolregno5369.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.RELAXED);

        Properties prop = new Properties();
        prop.setProperty("InterpretHydrogenIsotopes", "true");
        PropertiesListener listener = new PropertiesListener(prop);
        reader.addChemObjectIOListener(listener);
        reader.customizeJob();

        IAtomContainer molecule = new AtomContainer();
        molecule = reader.read(molecule);
        reader.close();
        int deuteriumCount = 0;
        for (IAtom atom : molecule.atoms())
            if (atom.getSymbol().equals("H") && atom.getMassNumber() != null && atom.getMassNumber() == 2)
                deuteriumCount++;
        Assert.assertEquals(3, deuteriumCount);
    }

    @Test
    public void testDeuteriumProperties() throws Exception {
        String filename = "data/mdl/chemblMolregno5369.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.RELAXED);
        IAtomContainer molecule = new AtomContainer();
        molecule = reader.read(molecule);
        reader.close();
        IAtom deuterium = molecule.getAtom(molecule.getAtomCount() - 1);
        assertTrue(1 == deuterium.getAtomicNumber());
        assertTrue(2 == deuterium.getMassNumber());
    }

    @Test
    public void testTritium() throws Exception {
        String filename = "data/mdl/chemblMolregno7039.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer molecule = new AtomContainer();
        molecule = reader.read(molecule);
        reader.close();
        int tritiumCount = 0;
        for (IAtom atom : molecule.atoms())
            if (atom.getSymbol().equals("H") && atom.getMassNumber() != null && atom.getMassNumber() == 3)
                tritiumCount++;
        Assert.assertEquals(1, tritiumCount);
    }

    @Test
    public void testTritiumProperties() throws Exception {
        String filename = "data/mdl/chemblMolregno7039.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer molecule = new AtomContainer();
        molecule = reader.read(molecule);
        reader.close();
        IAtom tritium = molecule.getAtom(molecule.getAtomCount() - 1);
        assertTrue(1 == tritium.getAtomicNumber());
        assertTrue(3 == tritium.getMassNumber());
    }

    /**
     * Tests a molfile with 'query' bond types (in this case bond type == 8 (any)).
     */
    @Test
    public void testQueryBondType8() throws Exception {
        String filename = "data/mdl/iridiumCoordination.chebi52748.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer atc = reader.read(new AtomContainer());
        reader.close();

        int queryBondCount = 0;
        for (IAtom atom : atc.atoms()) {
            if (atom.getSymbol().equals("Ir")) {
                for (IBond bond : atc.getConnectedBondsList(atom)) {
                    if (bond instanceof QueryBond) {
                        queryBondCount++;
                        assertSame(((QueryBond) bond).getExpression().type(), Expr.Type.TRUE);
                    }
                }
            }
        }
        Assert.assertEquals("Expecting three 'query' bond types to 'Ir'", 3, queryBondCount);
    }

    /**
     * Tests a molfile with 'query' bond types (in this case bond type == 6).
     */
    @Test
    public void testQueryBondType6() throws Exception {
        String filename = "data/mdl/chebi.querybond.51736.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer atc = reader.read(new AtomContainer());
        reader.close();
        int queryBondCount = 0;

        for (IBond bond : atc.bonds()) {
            if (bond instanceof QueryBond) {
                queryBondCount++;
                assertSame(((QueryBond) bond).getExpression().type(), Expr.Type.SINGLE_OR_AROMATIC);
            }
        }
        Assert.assertEquals("Expecting six 'query' bond types", 6, queryBondCount);
    }

    /**
     * Test that R-groups at higher atom numbers (>9) are read correctly
     */
    @Test
    public void testRGroupHighAtomNumber() throws Exception {
        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/brenda_molfile_rgroup.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        reader.read(molecule);
        reader.close();
        IAtom atom = molecule.getAtom(55);
        org.hamcrest.MatcherAssert.assertThat(atom, CoreMatchers.<IAtom>instanceOf(IPseudoAtom.class));
        Assert.assertEquals("R", ((IPseudoAtom)atom).getLabel());
    }

    @Test
    public void testAliasAtomNaming() throws Exception {
        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/mol_testAliasAtomNaming.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        reader.read(molecule);
        reader.close();

        IAtom[] atoms = AtomContainerManipulator.getAtomArray(molecule);

        int r1Count = 0;
        for (IAtom atom : atoms) {
            if (atom instanceof IPseudoAtom) {
                Assert.assertEquals("R1", ((IPseudoAtom) atom).getLabel());
                r1Count++;
            }
        }
        Assert.assertEquals(2, r1Count);
    }

    @Test
    public void testPseudoAtomLabels() throws Exception {
        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/pseudoatoms.sdf");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        assertTrue(molecule.getAtom(4) instanceof IPseudoAtom);
        Assert.assertEquals("R", molecule.getAtom(4).getSymbol());
        IPseudoAtom pa = (IPseudoAtom) molecule.getAtom(4);
        Assert.assertEquals("Gln", pa.getLabel());
    }

    /**
     * @cdk.bug 3485634
     */
    @Test
    public void testMissingAtomProperties() throws Exception {
        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/bug3485634.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        Assert.assertEquals(9, molecule.getAtomCount());
    }

    @Test
    public void testBondOrderFour() throws Exception {
        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/mdlWithBond4.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        Assert.assertEquals(9, molecule.getAtomCount());
        Assert.assertEquals(IBond.Order.UNSET, molecule.getBond(0).getOrder());
        assertTrue(molecule.getBond(0).getFlag(CDKConstants.SINGLE_OR_DOUBLE));
        Assert.assertEquals(IBond.Order.SINGLE, molecule.getBond(1).getOrder());
        assertFalse(molecule.getBond(1).getFlag(CDKConstants.SINGLE_OR_DOUBLE));
    }

    @Test
    public void testAtomParity() throws CDKException, IOException {

        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/mol_testAtomParity.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        Assert.assertEquals(6, molecule.getAtomCount());
        boolean chiralCentre = false;
        IAtom[] atoms = AtomContainerManipulator.getAtomArray(molecule);
        for (IAtom atom : atoms) {
            Integer parity = atom.getStereoParity();
            if (parity == 1) {
                chiralCentre = true;
            }
        }

        assertTrue(chiralCentre);

    }

    @Test
    public void testSingleSingletRadical() throws Exception {

        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/singleSingletRadical.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(1)), is(2));
    }

    @Test
    public void testSingleDoubletRadical() throws Exception {

        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/singleDoubletRadical.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(1)), is(1));
    }

    @Test
    public void testSingleTripletRadical() throws Exception {

        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/singleTripletRadical.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();

        assertThat(molecule.getConnectedSingleElectronsCount(molecule.getAtom(1)), is(2));
    }

    @Test
    public void testMultipleRadicals() throws Exception {

        InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/multipleRadicals.mol");
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
    public void fe_iii_valence() throws Exception {
        InputStream in = getClass().getResourceAsStream("/data/mdl/iron-iii.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        assertThat(molecule.getAtom(0).getImplicitHydrogenCount(), is(1));
        assertThat(molecule.getAtom(1).getImplicitHydrogenCount(), is(0));
        assertThat(molecule.getAtom(2).getImplicitHydrogenCount(), is(0));
    }

    @Test
    public void bismuth_ion_valence() throws Exception {
        InputStream in = getClass().getResourceAsStream("/data/mdl/bismuth-ion.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        assertThat(molecule.getAtom(0).getImplicitHydrogenCount(), is(3));
    }

    @Test
    public void e_butene_2D() throws Exception {
        InputStream in = getClass().getResourceAsStream("/data/mdl/e_butene_2d.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        assertTrue(molecule.stereoElements().iterator().hasNext());
    }

    // when there are no coordinates stereo perception should not be done
    @Test
    public void e_butene_0D() throws Exception {
        InputStream in = getClass().getResourceAsStream("/data/mdl/e_butene_0d.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        assertNotNull(molecule);
        assertFalse(molecule.stereoElements().iterator().hasNext());
    }

    // forcing as 3D is problematic for stereo perception as we put 2D coordinates
    // in to 3D as we then no longer know to check wedge/hatch labels.
    @Test
    public void e_butene_2D_force3D() throws Exception {
        InputStream in = getClass().getResourceAsStream("/data/mdl/e_butene_2d.mol");
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
        assertNotNull(molecule);
        assertFalse(molecule.stereoElements().iterator().hasNext());
    }

    @Test
    public void e_butene_3D() throws Exception {
        InputStream in = getClass().getResourceAsStream("/data/mdl/e_butene_3d.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);

        Properties prop = new Properties();
        prop.setProperty("ForceReadAs3DCoordinates", "true");
        PropertiesListener listener = new PropertiesListener(prop);
        reader.addChemObjectIOListener(listener);
        reader.customizeJob();

        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        assertNotNull(molecule);
        assertTrue(molecule.stereoElements().iterator().hasNext());
    }

    // turn off adding stereoelements
    @Test
    public void e_butene_2D_optOff() throws Exception {
        InputStream in = getClass().getResourceAsStream("/data/mdl/e_butene_2d.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);

        Properties prop = new Properties();
        prop.setProperty("AddStereoElements", "false");
        PropertiesListener listener = new PropertiesListener(prop);
        reader.addChemObjectIOListener(listener);
        reader.customizeJob();

        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        molecule = reader.read(molecule);
        reader.close();
        assertNotNull(molecule);
        assertFalse(molecule.stereoElements().iterator().hasNext());
    }

    @Test
    public void dataHeader_1() {
        assertThat(MDLV2000Reader.dataHeader("> 29 <DENSITY> "), is("DENSITY"));
    }

    @Test
    public void dataHeader_2() {
        assertThat(MDLV2000Reader.dataHeader("> <MELTING.POINT> "), is("MELTING.POINT"));
    }

    @Test
    public void dataHeader_3() {
        assertThat(MDLV2000Reader.dataHeader("> 55 (MD-08974) <BOILING.POINT> DT12"), is("BOILING.POINT"));
    }

    @Test
    public void readNonStructuralData() throws Exception {
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
    public void readNonStructuralData_emtpy() throws Exception {
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
    public void readNonStructuralData_wrap() throws Exception {
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
    public void testMultipleNewlinesInSDFProperty() throws Exception {
        InputStream in = getClass().getResourceAsStream("/data/mdl/multiplenewline-property.sdf");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        assertThat(ChemFileManipulator.getAllAtomContainers(chemFile).size(), is(2));
    }

    @Test
    public void testAliasAfterRgroup() throws Exception {
        InputStream in = getClass().getResourceAsStream("/data/mdl/r-group-with-alias.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer container = reader.read(new AtomContainer());
        reader.close();
        assertThat(container.getAtom(6), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) container.getAtom(6)).getLabel(), is("R6"));
        assertThat(container.getAtom(7), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) container.getAtom(7)).getLabel(), is("Protein"));
    }

    @Test
    public void keepAtomicNumberOfAlias() throws Exception {
        InputStream in = getClass().getResourceAsStream("/data/mdl/element-with-alias.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer container = reader.read(new AtomContainer());
        reader.close();
        assertThat(container.getAtom(6), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) container.getAtom(6)).getLabel(), is("N1"));
        assertThat(((IPseudoAtom) container.getAtom(6)).getAtomicNumber(), is(7));
    }

    @Test
    public void v2000Version() throws Exception {
        assertThat(MDLV2000Reader.CTabVersion.ofHeader("  5  5  0  0  0  0            999 V2000"),
                is(MDLV2000Reader.CTabVersion.V2000));
        assertThat(MDLV2000Reader.CTabVersion.ofHeader("  5  5  0  0  0  0            999 v2000"),
                is(MDLV2000Reader.CTabVersion.V2000));
    }

    @Test
    public void v3000Version() throws Exception {
        assertThat(MDLV2000Reader.CTabVersion.ofHeader("  0  0  0  0  0  0            999 V3000"),
                is(MDLV2000Reader.CTabVersion.V3000));
        assertThat(MDLV2000Reader.CTabVersion.ofHeader("  0  0  0  0  0  0            999 v3000"),
                is(MDLV2000Reader.CTabVersion.V3000));
    }

    @Test
    public void unspecVersion() throws Exception {
        assertThat(MDLV2000Reader.CTabVersion.ofHeader("  5  5  0  0  0  0            999"),
                is(MDLV2000Reader.CTabVersion.UNSPECIFIED));
        assertThat(MDLV2000Reader.CTabVersion.ofHeader("  5  5  0  0  0  0            999      "),
                is(MDLV2000Reader.CTabVersion.UNSPECIFIED));
    }

    @Test
    public void radicalsReflectedInHydrogenCount() throws Exception {
        MDLV2000Reader r = new MDLV2000Reader(getClass().getResourceAsStream("structure-with-radical.mol"));
        IAtomContainer m = r.read(new AtomContainer());
        r.close();
        assertThat(m.getAtom(0).getAtomicNumber(), is(8));
        assertThat(m.getAtom(0).getImplicitHydrogenCount(), is(0));
    }

    /**
     * @cdk.bug 1326
     */
    @Test
    public void nonNegativeHydrogenCount() throws Exception {
        InputStream in = getClass().getResourceAsStream("/data/mdl/ChEBI_30668.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer container = reader.read(new AtomContainer());
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
    public void nonNegativeHydrogenCountOnHydrogenRadical() throws Exception {
        InputStream in = getClass().getResourceAsStream("/data/mdl/ChEBI_29293.mol");
        MDLV2000Reader reader = new MDLV2000Reader(in);
        IAtomContainer container = reader.read(new AtomContainer());
        reader.close();
        assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(0));
        assertThat(container.getAtom(1).getImplicitHydrogenCount(), is(0));
    }

    /**
     * The non-standard ACDLabs atom label property should throw a CDKException in STRICT mode.
     * @throws Exception
     */
    @Test(expected=CDKException.class)
    public void testAcdChemSketchLabel_Strict() throws Exception {

        String filename = "data/mdl/chemsketch-all-labelled.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(new AtomContainer());
    }

    /**
     * Test a simple ChemSketch label containing an integer.
     * @throws Exception
     */
    @Test
    public void testAcdChemSketchLabel() throws Exception {
        String filename = "data/mdl/chemsketch-one-label.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();

        assertThat((String) mol.getAtom(1).getProperty(CDKConstants.ACDLABS_LABEL), is("6"));
    }

    /**
     * Test ChemSketch labels containing all non-whitespace printable ASCII characters.
     * @throws Exception
     */
    @Test
    public void testAcdChemSketchLabel_PrintableAscii() throws Exception {
        String filename = "data/mdl/chemsketch-printable-ascii.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();

        // Printable ASCII characters, excluding whitespace. Note each string contains an atom number
        String[] expected =
        {
            "!\"#$%&'()*+,-./0123456789:;<=>?@[\\]^_`{|}~",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ1abcdefghijklmnopqrstuvwxyz",
            "012345678901234567890123456789012345678901234567890"
        };
        assertThat((String) mol.getAtom(0).getProperty(CDKConstants.ACDLABS_LABEL), is(expected[0]));
        assertThat((String) mol.getAtom(1).getProperty(CDKConstants.ACDLABS_LABEL), is(expected[1]));
        assertThat((String) mol.getAtom(2).getProperty(CDKConstants.ACDLABS_LABEL), is(expected[2]));
    }

    /**
     * Check that multiple atom labels are all read.
     * @throws Exception
     */
    @Test
    public void testAcdChemSketchLabel_AllAtomsLabelled() throws Exception {
        String filename = "data/mdl/chemsketch-all-labelled.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();

        Iterable<IAtom> atoms = mol.atoms();
        for (IAtom atom : atoms){
            Assert.assertNotNull(atom.getProperty(CDKConstants.ACDLABS_LABEL));
        }
    }

    /**
     * Check that leading and trailing whitespace in atom labels is preserved on reading.
     * @throws Exception
     */
    @Test
    public void testAcdChemSketchLabel_LeadingTrailingWhitespace() throws Exception {
        String filename = "data/mdl/chemsketch-leading-trailing-space.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();

        // Leading and trailing whitespace in both prefix and suffix
        String expected = " a 1 b ";
        assertThat((String) mol.getAtom(0).getProperty(CDKConstants.ACDLABS_LABEL), is(expected));
    }

    /**
     * Check that embedded whitespace in atom labels is preserved on reading.
     * @throws Exception
     */
    @Test
    public void testAcdChemSketchLabel_EmbeddedWhitespace() throws Exception {
        String filename = "data/mdl/chemsketch-embedded-space.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();

        // Embedded whitespace in both prefix and suffix
        String expected = "a b1c d";
        assertThat((String) mol.getAtom(0).getProperty(CDKConstants.ACDLABS_LABEL), is(expected));
    }

    /**
     * Check reading of largest permissible label (50 char prefix + 3 digits + 50 char suffix).
     * @throws Exception
     */
    @Test
    public void testAcdChemSketchLabel_MaxSizeLabel() throws Exception {
        String filename = "data/mdl/chemsketch-longest-label.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = reader.read(new AtomContainer());
        reader.close();

        // Longest allowed atom label is 103 characters
        String prefix = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwx";
        String digits = "999";
        String suffix = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwx";
        String expected = prefix + digits + suffix;

        assertThat((String) mol.getAtom(0).getProperty(CDKConstants.ACDLABS_LABEL), is(expected));
    }

    @Test
    public void testSgroupAbbreviation() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/sgroup-abbrv.mol"))) {
            final IAtomContainer container = mdlr.read(new AtomContainer());
            List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
            assertNotNull(sgroups);
            assertThat(sgroups.size(), is(1));
            Sgroup sgroup = sgroups.get(0);
            assertThat(sgroup.getType(), is(SgroupType.CtabAbbreviation));
            assertThat(sgroup.getSubscript(), is("Cs2CO3"));
            assertThat(sgroup.getAtoms().size(), is(6));
        }
    }

    @Test
    public void testSgroupRepeatUnit() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/sgroup-sru.mol"))) {
            IAtomContainer container = mdlr.read(new AtomContainer());
            List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
            assertNotNull(sgroups);
            assertThat(sgroups.size(), is(1));
            Sgroup sgroup = sgroups.get(0);
            assertThat(sgroup.getType(), is(SgroupType.CtabStructureRepeatUnit));
            assertThat(sgroup.getSubscript(), is("n"));
            assertThat((String) sgroup.getValue(SgroupKey.CtabConnectivity), is("HT"));
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
    public void testSgroupUnorderedMixture() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/sgroup-unord-mixture.mol"))) {
            IAtomContainer container = mdlr.read(new AtomContainer());
            List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
            assertNotNull(sgroups);
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
    public void testSgroupExpandedAbbreviation() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/triphenyl-phosphate-expanded.mol"))) {
            IAtomContainer container = mdlr.read(new AtomContainer());
            List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
            assertNotNull(sgroups);
            assertThat(sgroups.size(), is(3));
            // first sgroup
            Sgroup sgroup = sgroups.get(0);
            assertThat(sgroup.getType(), is(SgroupType.CtabAbbreviation));
            assertThat(sgroup.getSubscript(), is("Ph"));
            assertNotNull(sgroup.getValue(SgroupKey.CtabExpansion));
            // second sgroup
            sgroup = sgroups.get(1);
            assertThat(sgroup.getType(), is(SgroupType.CtabAbbreviation));
            assertThat(sgroup.getSubscript(), is("Ph"));
            assertNotNull(sgroup.getValue(SgroupKey.CtabExpansion));
            // third sgroup
            sgroup = sgroups.get(2);
            assertThat(sgroup.getType(), is(SgroupType.CtabAbbreviation));
            assertThat(sgroup.getSubscript(), is("Ph"));
            assertNotNull(sgroup.getValue(SgroupKey.CtabExpansion));
        }
    }

    @Test(expected = CDKException.class)
    public void testSgroupInvalidConnectInStrictMode() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/sgroup-sru-bad-scn.mol"))) {
            mdlr.setReaderMode(Mode.STRICT);
            IAtomContainer container = mdlr.read(new AtomContainer());
        }
    }

    @Test(expected = CDKException.class)
    public void testSgroupDefOrderInStrictMode() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/sgroup-sru-bad-def.mol"))) {
            mdlr.setReaderMode(Mode.STRICT);
            IAtomContainer container = mdlr.read(new AtomContainer());
        }
    }

    @Test
    public void testSgroupBracketStyle() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/sgroup-sru-bracketstyles.mol"))) {
            IAtomContainer container = mdlr.read(new AtomContainer());
            List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
            assertNotNull(sgroups);
            assertThat(sgroups.size(), is(2));
            Sgroup sgroup = sgroups.get(0);
            assertThat(sgroup.getType(), is(SgroupType.CtabStructureRepeatUnit));
            assertThat((Integer) sgroup.getValue(SgroupKey.CtabBracketStyle), is(1));
            sgroup = sgroups.get(1);
            assertThat(sgroup.getType(), is(SgroupType.CtabStructureRepeatUnit));
            assertThat((Integer) sgroup.getValue(SgroupKey.CtabBracketStyle), is(1));
        }
    }

    @Test public void testReading0DStereochemistry() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/tetrahedral-parity-withImplH.mol"))) {
            IAtomContainer container = mdlr.read(new AtomContainer());
            Iterable<IStereoElement> selements = container.stereoElements();
            Iterator<IStereoElement> siter = selements.iterator();
            assertTrue(siter.hasNext());
            IStereoElement se = siter.next();
            assertThat(se, is(instanceOf(ITetrahedralChirality.class)));
            assertThat(((ITetrahedralChirality) se).getStereo(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
            assertThat(((ITetrahedralChirality) se).getLigands(), is(new IAtom[]{container.getAtom(1), container.getAtom(3), container.getAtom(4), container.getAtom(0)}));
            assertFalse(siter.hasNext());
        }
    }

    // explicit Hydrogen can reverse winding
    @Test public void testReading0DStereochemistryWithHydrogen() throws Exception {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/tetrahedral-parity-withExpH.mol"))) {
            IAtomContainer container = mdlr.read(new AtomContainer());
            Iterable<IStereoElement> selements = container.stereoElements();
            Iterator<IStereoElement> siter = selements.iterator();
            assertTrue(siter.hasNext());
            IStereoElement se = siter.next();
            assertThat(se, is(instanceOf(ITetrahedralChirality.class)));
            assertThat(((ITetrahedralChirality) se).getStereo(), is(ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
            assertThat(((ITetrahedralChirality) se).getLigands(), is(new IAtom[]{container.getAtom(0), container.getAtom(2), container.getAtom(3), container.getAtom(4)}));
            assertFalse(siter.hasNext());
        }
    }

    /**
     * When atomic mass is defined as a delta some atoms don't have a reasonable
     * default. Most tools will output an 'M  ISO' property, so can be specified
     * @throws Exception expected format error
     */
    @Test(expected = CDKException.class)
    public void seaborgiumMassDelta() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("seaborgium.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in, Mode.STRICT)) {
            IAtomContainer mol = mdlr.read(new AtomContainer());
        }
    }

    @Test
    public void seaborgiumAbsMass() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("seaborgium_abs.mol");
             MDLV2000Reader mdlr = new MDLV2000Reader(in, Mode.STRICT)) {
            IAtomContainer mol = mdlr.read(new AtomContainer());
            assertThat(mol.getAtom(0).getMassNumber(), is(261));
        }
    }

	@Test
    public void testMassDiff() throws Exception {
        String mdl = "deuterium.mol\n" + "\n" + "\n" + "  1  0  0  0  0                 1 V2000\n"
                + "    0.0000    0.0000    0.0000 H  +1  0  0  0  0\n"
                + "M  END\n";
        try (StringReader in = new StringReader(mdl)) {
            MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdl), Mode.STRICT);
            IAtomContainer mol = reader.read(new AtomContainer());
            IAtom atom = mol.getAtom(0);
            Assert.assertEquals(1, atom.getAtomicNumber().intValue());
            Assert.assertEquals(2, atom.getMassNumber().intValue());
        }
    }
	
    @Test
    public void testBadAtomCoordinateFormat() throws Exception {

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
        final org.openscience.cdk.silent.AtomContainer atomContainer = mdlv2000Reader.read(new org.openscience.cdk.silent.AtomContainer());
        Assert.assertEquals(17, atomContainer.getAtomCount());
    }

    @Test public void test() throws Exception {
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
}
