/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.isomorphism.matchers.CTFileQueryBond;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;


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

    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(MDLV2000ReaderTest.class);

    @BeforeClass public static void setup() {
        setSimpleChemObjectReader(new MDLV2000Reader(), "data/mdl/bug682233.mol");
    }

    @Test public void testAccepts() {
    	MDLV2000Reader reader = new MDLV2000Reader();
    	Assert.assertTrue(reader.accepts(ChemFile.class));
    	Assert.assertTrue(reader.accepts(ChemModel.class));
    	Assert.assertTrue(reader.accepts(Molecule.class));
    }

    /**
     * @cdk.bug 3084064
     */
    @Test public void testBug3084064() throws Exception {
        String filename = "data/mdl/weirdprops.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());

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
            Assert.assertTrue(s +" was not read from the file", found);
        }
    }
    
    /**
     * @cdk.bug 682233
     */
    @Test public void testBug682233() throws Exception {
        String filename = "data/mdl/bug682233.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        org.openscience.cdk.interfaces.IMoleculeSet som = model.getMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(1, som.getMoleculeCount());
        org.openscience.cdk.interfaces.IMolecule m = som.getMolecule(0);
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

    @Test public void testAPinene() throws Exception {
        String filename = "data/mdl/a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assert.assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test public void testReadingMISOLines() throws Exception {
        String filename = "data/mdl/ChEBI_37340.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assert.assertEquals(210, containersList.get(0).getAtom(0).getMassNumber().intValue());
    }

    /**
     * @cdk.bug 2234820
     */
    @Test public void testMassNumber() throws Exception {
        String filename = "data/mdl/massnumber.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertEquals(3, containersList.get(0).getAtomCount());
        Assert.assertEquals(2, containersList.get(0).getAtom(1).getMassNumber().intValue());
        Assert.assertEquals(3, containersList.get(0).getAtom(2).getMassNumber().intValue());
    }

    @Test public void testAlkane() throws Exception {
        String filename = "data/mdl/shortest_path_test.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
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
        	Assert.assertEquals(CDKConstants.BONDORDER_SINGLE, bonds.next().getOrder());
        }
    }

    @Test public void testReadTitle() throws Exception {
        String filename = "data/mdl/a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Molecule mol = reader.read(new Molecule());
        Assert.assertEquals("a-pinen.mol", mol.getProperty(CDKConstants.TITLE));
    }

    @Test public void testFourRing() throws Exception {
        String filename = "data/mdl/four-ring-5x10.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assert.assertTrue(containersList.get(0).getBondCount() > 0);
    }


    @Test public void testHydrozyamino() throws Exception {
        String filename = "data/mdl/hydroxyamino.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assert.assertTrue(containersList.get(0).getBondCount() > 0);
    }


    @Test public void testMethylBenzol() throws Exception {
        String filename = "data/mdl/methylbenzol.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assert.assertTrue(containersList.get(0).getBondCount() > 0);
    }
    

    @Test public void testPolycarpol() throws Exception {
        String filename = "data/mdl/polycarpol.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assert.assertTrue(containersList.get(0).getBondCount() > 0);
    }
    
    @Test public void testReserpine() throws Exception {
        String filename = "data/mdl/reserpine.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assert.assertTrue(containersList.get(0).getBondCount() > 0);
    }    


    @Test public void testSixRing() throws Exception {
        String filename = "data/mdl/six-ring-4x4.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assert.assertTrue(containersList.get(0).getBondCount() > 0);
    }


    @Test public void testSuperspiro() throws Exception {
        String filename = "data/mdl/superspiro.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assert.assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test public void testGhemicalOutput() throws Exception {
        String filename = "data/mdl/butanoic_acid.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertTrue(containersList.get(0).getAtomCount() > 0);
        Assert.assertTrue(containersList.get(0).getBondCount() > 0);
    }

    @Test public void testUsesGivenMolecule() throws Exception {
        String filename = "data/mdl/superspiro.mol"; // just a random file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Molecule superspiro = new Molecule();
        superspiro.setID("superspiro");
        Molecule result = reader.read(superspiro);
        Assert.assertEquals(superspiro.getID(), result.getID());
    }

    /** 
     * @cdk.bug 835571
     */
    @Test public void testReadFromStringReader () throws Exception {
        String mdl =
                "cyclopropane.mol\n" +
                "\n" +
                "\n" +
                "  9  9  0  0  0                 1 V2000\n" +
                "   -0.0073   -0.5272    0.9655 C   0  0  0  0  0\n" +
                "   -0.6776   -0.7930   -0.3498 C   0  0  0  0  0\n" +
                "    0.2103    0.4053   -0.1891 C   0  0  0  0  0\n" +
                "    0.8019   -1.1711    1.2970 H   0  0  0  0  0\n" +
                "   -0.6000   -0.2021    1.8155 H   0  0  0  0  0\n" +
                "   -1.7511   -0.6586   -0.4435 H   0  0  0  0  0\n" +
                "   -0.3492   -1.6277   -0.9620 H   0  0  0  0  0\n" +
                "    1.1755    0.4303   -0.6860 H   0  0  0  0  0\n" +
                "   -0.2264    1.3994   -0.1675 H   0  0  0  0  0\n" +
                "  1  2  1  6  0  0\n" +
                "  1  3  1  6  0  0\n" +
                "  1  4  1  0  0  0\n" +
                "  1  5  1  1  0  0\n" +
                "  2  3  1  0  0  0\n" +
                "  2  6  1  0  0  0\n" +
                "  2  7  1  6  0  0\n" +
                "  3  8  1  6  0  0\n" +
                "  3  9  1  0  0  0\n" +
                "M  END\n";
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdl));
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        Assert.assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        org.openscience.cdk.interfaces.IMoleculeSet som = model.getMoleculeSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(1, som.getMoleculeCount());
        org.openscience.cdk.interfaces.IMolecule m = som.getMolecule(0);
        Assert.assertNotNull(m);
        Assert.assertEquals(9, m.getAtomCount());
        Assert.assertEquals(9, m.getBondCount());
    }
    
    @Test public void testRGroup() throws Exception {
        String filename = "data/mdl/SARGROUPTEST.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Molecule mol = reader.read(new Molecule());
        Assert.assertEquals("R2",((PseudoAtom)mol.getAtom(19)).getLabel());
    }

    @Test public void testAliasPropertyGroup() throws Exception {
        String filename = "data/mdl/AliasPropertyRGroup.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Molecule mol = reader.read(new Molecule());
        IAtom atom = mol.getAtom(3);
        Assert.assertTrue(atom instanceof PseudoAtom);
        Assert.assertEquals("R1", ((PseudoAtom)atom).getLabel());
    }

    /**
     * @cdk.bug 1587283
     */
    @Test public void testBug1587283() throws Exception {
        String filename = "data/mdl/bug1587283.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertEquals(15, containersList.get(0).getAtomCount());
        Assert.assertEquals(16, containersList.get(0).getBondCount());
    }
    
    @Test public void testReadProton() throws Exception {
    	String mdl =
            "proton.mol\n" +
            "\n" +
            "\n" +
            "  1  0  0  0  0                 1 V2000\n" +
            "   -0.0073   -0.5272    0.9655 H   0  0  0  0  0\n" +
            "M  CHG  1   1   1\n" +
            "M  END\n";
    	MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdl));
    	Molecule mol = reader.read(new Molecule());
    	Assert.assertNotNull(mol);
    	Assert.assertEquals(1, mol.getAtomCount());
    	Assert.assertEquals(0, mol.getBondCount());
    	Assert.assertEquals(1, AtomContainerManipulator.getTotalFormalCharge(mol));
    	IAtom atom = mol.getAtom(0);
    	Assert.assertEquals(1, atom.getFormalCharge().intValue());
    }

    @Test public void testReadingCharges() throws Exception {
    	String filename = "data/mdl/withcharges.mol";
    	logger.info("Testing: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = reader.read(new ChemFile());
        Assert.assertEquals(1, chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0).getAtom(6).getFormalCharge().intValue());
        Assert.assertEquals(-1, chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0).getAtom(8).getFormalCharge().intValue());
    }

    @Test public void testEmptyString() throws Exception {
        String emptyString = "";
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(emptyString));
        IMolecule mol = reader.read(new NNMolecule());
        Assert.assertNull(mol);
    }

    @Test public void testNoAtomCase() throws Exception {
        String filename = "data/mdl/emptyStructure.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());

        IAtomContainer container = containersList.get(0);
        Assert.assertNotNull(container);
        Assert.assertEquals(0, container.getAtomCount());
        Assert.assertEquals(0, container.getBondCount());


        Map<Object,Object> props = container.getProperties();
        Set<Object> keys = props.keySet();

        Assert.assertTrue(keys.contains("SubstanceType"));
        Assert.assertTrue(keys.contains("TD50 Rat"));
        Assert.assertTrue(keys.contains("ChemCount"));
    }

    /**
     * @cdk.bug 1732307
     */
    @Test public void testZeroZCoordinates() throws Exception {
        String filename = "data/mdl/nozcoord.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Properties prop = new Properties();
        prop.setProperty("ForceReadAs3DCoordinates","true");
        PropertiesListener listener = new PropertiesListener(prop);
        reader.addChemObjectIOListener(listener);
        reader.customizeJob();

        IMolecule mol = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class));
        Assert.assertNotNull(mol);
        Assert.assertEquals(5, mol.getAtomCount());

        boolean has3d = GeometryTools.has3DCoordinates(mol);
        Assert.assertTrue(has3d);
    }

    /**
     * @cdk.bug 1826577
     */
    @Test public void testHisotopes_Strict() throws Exception {
    	String filename = "data/mdl/hisotopes.mol";
    	logger.info("Testing: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	try {
    		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
    		reader.read(new ChemFile());
    		Assert.fail("Expected a CDKException");
    	} catch (Exception exception) {
    		// OK, that's what's is supposed to happen
    	}
    }        

    /**
     * @cdk.bug 1826577
     */
    @Test public void testHisotopes_Relaxed() throws Exception {
    	String filename = "data/mdl/hisotopes.mol";
    	logger.info("Testing: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.RELAXED);
    	IChemFile chemFile = reader.read(new ChemFile());
    	Assert.assertNotNull(chemFile);
    	List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
    	Assert.assertNotNull(containersList.get(0));
    	Assert.assertFalse((containersList.get(0)).getAtom(1) instanceof IPseudoAtom);
    	Assert.assertFalse((containersList.get(0)).getAtom(2) instanceof IPseudoAtom);
    }        

    /**
     * 
     * @throws Exception
     */
    @Test public void testReadRadical() throws Exception {
    	String filename = "data/mdl/332727182.radical.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        Assert.assertTrue((containersList.get(0)).getAtomCount() > 0);
        Assert.assertTrue((containersList.get(0)).getBondCount() > 0);
        Assert.assertTrue((containersList.get(0)).getSingleElectronCount() > 0);
    }

    /**
     * @cdk.bug 2604888
     */
    @Test public void testNoCoordinates() throws Exception {
        String mdl =
            "cyclopropane.mol\n" +
            "\n" +
            "\n" +
            "  9  9  0  0  0 0 0 0 0 0 0 0 0 1 V2000\n" +
            "    0.0000    0.0000    0.0000 C   0  0  0  0  0\n" +
            "    0.0000    0.0000    0.0000 C   0  0  0  0  0\n" +
            "    0.0000    0.0000    0.0000 C   0  0  0  0  0\n" +
            "    0.0000    0.0000    0.0000 H   0  0  0  0  0\n" +
            "    0.0000    0.0000    0.0000 H   0  0  0  0  0\n" +
            "    0.0000    0.0000    0.0000 H   0  0  0  0  0\n" +
            "    0.0000    0.0000    0.0000 H   0  0  0  0  0\n" +
            "    0.0000    0.0000    0.0000 H   0  0  0  0  0\n" +
            "    0.0000    0.0000    0.0000 H   0  0  0  0  0\n" +
            "  1  2  1  6  0  0\n" +
            "  1  3  1  6  0  0\n" +
            "  1  4  1  0  0  0\n" +
            "  1  5  1  1  0  0\n" +
            "  2  3  1  0  0  0\n" +
            "  2  6  1  0  0  0\n" +
            "  2  7  1  6  0  0\n" +
            "  3  8  1  6  0  0\n" +
            "  3  9  1  0  0  0\n" +
            "M  END\n";
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdl));
        IMolecule molecule = reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        Assert.assertEquals(9, molecule.getAtomCount());
        Assert.assertEquals(9, molecule.getBondCount());
        for (IAtom atom : molecule.atoms()) {
            Assert.assertNull(atom.getPoint2d());
            Assert.assertNull(atom.getPoint2d());
        }
    }
    
    @Test public void testUndefinedStereo() throws Exception {
        String filename = "data/mdl/ChEBI_26120.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule mol = reader.read(new NNMolecule());
        Assert.assertEquals(IBond.Stereo.E_OR_Z,mol.getBond(1).getStereo());
        Assert.assertEquals(IBond.Stereo.E_OR_Z,mol.getBond(6).getStereo());
        Assert.assertEquals(IBond.Stereo.E_OR_Z,mol.getBond(7).getStereo());
        Assert.assertEquals(IBond.Stereo.UP_OR_DOWN,mol.getBond(11).getStereo());
    }

    /**
     * Tests that the '0' read from the bond block for bond stereo
     * is read is 'no stereochemistry involved'.
     */
    @Test public void testStereoReadZeroDefault() throws Exception {
        String filename = "data/mdl/withcharges.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader()
            .getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList =
        	ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        IAtomContainer container = containersList.get(0);
        Assert.assertEquals(
        	IBond.Stereo.NONE,
        	container.getBond(0).getStereo()
        );
    }

    @Test public void testReadStereoBonds() throws Exception {
        String mdl =
                "cyclopropane.mol\n" +
                "\n" +
                "\n" +
                "  9  9  0  0  0                 1 V2000\n" +
                "   -0.0073   -0.5272    0.9655 C   0  0  0  0  0\n" +
                "   -0.6776   -0.7930   -0.3498 C   0  0  0  0  0\n" +
                "    0.2103    0.4053   -0.1891 C   0  0  0  0  0\n" +
                "    0.8019   -1.1711    1.2970 H   0  0  0  0  0\n" +
                "   -0.6000   -0.2021    1.8155 H   0  0  0  0  0\n" +
                "   -1.7511   -0.6586   -0.4435 H   0  0  0  0  0\n" +
                "   -0.3492   -1.6277   -0.9620 H   0  0  0  0  0\n" +
                "    1.1755    0.4303   -0.6860 H   0  0  0  0  0\n" +
                "   -0.2264    1.3994   -0.1675 H   0  0  0  0  0\n" +
                "  1  2  1  6  0  0\n" +
                "  1  3  1  6  0  0\n" +
                "  1  4  1  0  0  0\n" +
                "  1  5  1  1  0  0\n" +
                "  2  3  1  0  0  0\n" +
                "  2  6  1  0  0  0\n" +
                "  2  7  1  6  0  0\n" +
                "  3  8  1  6  0  0\n" +
                "  3  9  1  0  0  0\n" +
                "M  END\n";
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdl));
        IMolecule mol = reader.read(new Molecule());
        Assert.assertNotNull(mol);
        Assert.assertEquals(9, mol.getAtomCount());
        Assert.assertEquals(9, mol.getBondCount());
        Assert.assertEquals(IBond.Stereo.DOWN, mol.getBond(0).getStereo());
        Assert.assertEquals(IBond.Stereo.UP, mol.getBond(3).getStereo());
    }

    @Test public void testStereoDoubleBonds() throws Exception {
        String filename = "data/mdl/butadiene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader()
            .getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList =
        	ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        IAtomContainer container = containersList.get(0);
        Assert.assertEquals(
            IBond.Stereo.E_Z_BY_COORDINATES,
            container.getBond(0).getStereo()
        );
        Assert.assertEquals(
        	IBond.Stereo.E_OR_Z,
        	container.getBond(2).getStereo()
        );
    }

    /**
     * Tests numbering of R# elements according to RGP line.
     * @throws Exception
     */
    @Test public void testRGroupHashNumbering() throws Exception {
        String filename = "data/mdl/rgroups.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Molecule mol = (Molecule)reader.read(new Molecule());
        for(IBond bond: mol.bonds() ) {
            PseudoAtom rGroup = null;
            IAtom partner=null;
            if (bond.getAtom(0) instanceof PseudoAtom )  {
                rGroup = (PseudoAtom)bond.getAtom(0);
                partner = bond.getAtom(1);
            }
            else {
                partner = bond.getAtom(0);
                rGroup = (PseudoAtom)bond.getAtom(1);
            }
            if (partner.getSymbol().equals("N"))  {
                Assert.assertEquals(rGroup.getLabel(),"R4");
            }
            else
            if (partner.getSymbol().equals("P"))  {
                Assert.assertEquals(rGroup.getLabel(),"R1");
            }
            else
            if (partner.getSymbol().equals("As"))  {
                Assert.assertEquals(rGroup.getLabel(),"R4");
            }
            else
            if (partner.getSymbol().equals("Si"))  {
                Assert.assertEquals(rGroup.getLabel(),"R");
            }
        }
    }


    /**
     * Test for hard coded R-group numbers in the Atom block. 
     * Hard coding is accepted but should not be done really, instead use 
     * a hash (#) conform the CTFile spec.
     * @throws Exception
     */
    @Test public void testRGroupHardcodedNumbering() throws Exception {
        String filename = "data/mdl/rgroupsNumbered.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Molecule mol = (Molecule)reader.read(new Molecule());
        for(IBond bond: mol.bonds() ) {
            PseudoAtom rGroup = null;
            if (bond.getAtom(0) instanceof PseudoAtom )  
                rGroup = (PseudoAtom)bond.getAtom(0);
            else 
                rGroup = (PseudoAtom)bond.getAtom(1);

            if (bond.getOrder()== IBond.Order.DOUBLE)  {
                Assert.assertEquals(rGroup.getLabel(),"R32");
            }
            else
            if (bond.getStereo()==IBond.Stereo.DOWN)  {
                Assert.assertEquals(rGroup.getLabel(),"R2");
            }
            else
                if (bond.getStereo()==IBond.Stereo.UP)  {
                Assert.assertEquals(rGroup.getLabel(),"R20");
            }
            else
                Assert.assertEquals(rGroup.getLabel(),"R5");
        }
    }

    @Test public void testReadValence() throws Exception {
        String filename = "data/mdl/a-pinene-with-valence.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
     
        IMolecule mol = reader.read(new Molecule());
        Assert.assertNotNull(mol);
        Assert.assertEquals(2, mol.getAtom(0).getValency().intValue());
        Assert.assertEquals(3, mol.getAtom(1).getValency().intValue());
        Assert.assertNull(mol.getAtom(2).getValency());
        Assert.assertEquals(0, mol.getAtom(3).getValency().intValue());
    }

    @Test public void testShortLines() throws Exception {
        logger.info("Testing short lines Mode.RELAXED");
        testShortLinesForMode(Mode.RELAXED);
        logger.info("Testing short lines Mode.STRICT");
        testShortLinesForMode(Mode.STRICT);
    }
    
    private void testShortLinesForMode (IChemObjectReader.Mode mode) throws Exception {
        String filename = "data/mdl/glycine-short-lines.mol";
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, mode);
        Molecule mol = reader.read(new Molecule());
        Assert.assertNotNull(mol);
        Assert.assertEquals(mol.getAtomCount(), 5);
        Assert.assertEquals(mol.getBondCount(), 4);
    }

    @Test public void testReadAtomAtomMapping() throws Exception {
        String filename = "data/mdl/a-pinene-with-atom-atom-mapping.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IMolecule mol = reader.read(new Molecule());
        Assert.assertNotNull(mol);
        Assert.assertEquals(1, ((Integer)mol.getAtom(0).getProperty(CDKConstants.ATOM_ATOM_MAPPING)).intValue());
        Assert.assertEquals(15, ((Integer)mol.getAtom(1).getProperty(CDKConstants.ATOM_ATOM_MAPPING)).intValue());
        Assert.assertNull(mol.getAtom(2).getProperty(CDKConstants.ATOM_ATOM_MAPPING));
    }

    /**
     * @cdk.bug 2936440
     */
    @Test public void testHas2DCoordinates_With000() throws CDKException {
        String filenameMol = "data/mdl/with000coordinate.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filenameMol);
        Molecule molOne=null;
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molOne = (Molecule)reader.read(new Molecule());
        Assert.assertNotNull(molOne.getAtom(0).getPoint2d());
        Assert.assertNotNull(molOne.getAtom(0).getPoint3d());
    }

    @Test public void testAtomValueLines() throws Exception {
    	String filename = "data/mdl/atomValueLines.mol";
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	MDLV2000Reader reader = new MDLV2000Reader(ins);
    	Molecule testMolecule = new Molecule();
    	Molecule result = reader.read(testMolecule);
    	IAtom oxygen = result.getAtom(0);
    	Assert.assertTrue(oxygen.getSymbol().equals("O"));
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

        Molecule molecule = new Molecule();
        molecule = reader.read(molecule);
        int deuteriumCount = 0;
        for (IAtom atom : molecule.atoms())
            if (atom.getSymbol().equals("H") && atom.getMassNumber() != null && atom.getMassNumber() == 2)
                deuteriumCount++;
        Assert.assertEquals(3, deuteriumCount);
    }

    @Test public void testTritium() throws Exception {
        String filename = "data/mdl/chemblMolregno7039.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Molecule molecule = new Molecule();
        molecule = reader.read(molecule);
        int tritiumCount=0;
        for (IAtom atom : molecule.atoms())
            if(atom.getSymbol().equals("H") && atom.getMassNumber()!=null && atom.getMassNumber()==3)
                tritiumCount++;
        Assert.assertEquals(1, tritiumCount);
    }

    /**
     * Tests a molfile with 'query' bond types (in this case bond type == 8 (any)).
     */
    @Test
    public void testQueryBondType8() throws Exception {
        String filename = "data/mdl/iridiumCoordination.chebi52748.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer atc = reader.read(new Molecule());

        int queryBondCount = 0;
        for (IAtom atom : atc.atoms()) {
            if (atom.getSymbol().equals("Ir")) {
                for (IBond bond : atc.getConnectedBondsList(atom)) {
                    if (bond instanceof CTFileQueryBond) {
                        queryBondCount++;
                        Assert.assertTrue(((CTFileQueryBond)bond).getType() == CTFileQueryBond.Type.ANY);
                        Assert.assertEquals(bond.getOrder(), null);
                    }
                }
            }
        }
        Assert.assertTrue("Expecting three 'query' bond types to 'Ir'", queryBondCount == 3);
    }

    /**
     * Tests a molfile with 'query' bond types (in this case bond type == 6).
     */
    @Test
    public void testQueryBondType6() throws Exception {
        String filename = "data/mdl/chebi.querybond.51736.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer atc = reader.read(new Molecule());
        int queryBondCount = 0;

        for (IBond bond : atc.bonds()) {
            if (bond instanceof CTFileQueryBond) {
                queryBondCount++;
                Assert.assertTrue(((CTFileQueryBond)bond).getType() == CTFileQueryBond.Type.SINGLE_OR_AROMATIC);
                Assert.assertEquals(bond.getOrder(), null);
            }
        }
        Assert.assertTrue("Expecting six 'query' bond types", queryBondCount == 6);
    }

}
