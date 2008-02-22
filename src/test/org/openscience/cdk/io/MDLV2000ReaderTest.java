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
package org.openscience.cdk.test.io;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestCase for the reading MDL mol files using one test file.
 * A test case for SDF files is available as separate Class.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLV2000Reader
 * @see org.openscience.cdk.test.io.SDFReaderTest
 */
public class MDLV2000ReaderTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public MDLV2000ReaderTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(MDLV2000ReaderTest.class);
    }

    public void testAccepts() {
    	MDLV2000Reader reader = new MDLV2000Reader();
    	assertTrue(reader.accepts(ChemFile.class));
    	assertTrue(reader.accepts(ChemModel.class));
    	assertTrue(reader.accepts(Molecule.class));
    }
    
    /**
     * @cdk.bug 682233
     */
    public void testBug682233() throws Exception {
        String filename = "data/mdl/bug682233.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

        assertNotNull(chemFile);
        assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        assertNotNull(model);

        org.openscience.cdk.interfaces.IMoleculeSet som = model.getMoleculeSet();
        assertNotNull(som);
        assertEquals(1, som.getMoleculeCount());
        org.openscience.cdk.interfaces.IMolecule m = som.getMolecule(0);
        assertNotNull(m);
        assertEquals(4, m.getAtomCount());
        assertEquals(2, m.getBondCount());

        // test reading of formal charges
        org.openscience.cdk.interfaces.IAtom a = m.getAtom(0);
        assertNotNull(a);
        assertEquals("Na", a.getSymbol());
        assertEquals(1, a.getFormalCharge().intValue());
        a = m.getAtom(2); 
        assertNotNull(a);
        assertEquals("O", a.getSymbol());
        assertEquals(-1, a.getFormalCharge().intValue());
    }

    public void testAPinene() throws Exception {
        String filename = "data/mdl/a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
        assertTrue(((IAtomContainer)containersList.get(0)).getAtomCount() > 0);
        assertTrue(((IAtomContainer)containersList.get(0)).getBondCount() > 0);
    }

    public void testAlkane() throws Exception {
        String filename = "data/mdl/shortest_path_test.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
        IAtomContainer container = (IAtomContainer)containersList.get(0); 
        assertEquals(10, container.getAtomCount());
        assertEquals(9, container.getBondCount());
        Iterator<IAtom> atoms = container.atoms();
        while (atoms.hasNext()) {
        	assertEquals("C", atoms.next().getSymbol());
        }
        Iterator<IBond> bonds = container.bonds();
        while (bonds.hasNext()) {
        	assertEquals(CDKConstants.BONDORDER_SINGLE, bonds.next().getOrder());
        }
    }

    public void testReadTitle() throws Exception {
        String filename = "data/mdl/a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Molecule mol = (Molecule)reader.read(new Molecule());
        assertEquals("a-pinen.mol", mol.getProperty(CDKConstants.TITLE));
    }

    public void testFourRing() throws Exception {
        String filename = "data/mdl/four-ring-5x10.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
        assertTrue(((IAtomContainer)containersList.get(0)).getAtomCount() > 0);
        assertTrue(((IAtomContainer)containersList.get(0)).getBondCount() > 0);
    }


    public void testHydrozyamino() throws Exception {
        String filename = "data/mdl/hydroxyamino.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
        assertTrue(((IAtomContainer)containersList.get(0)).getAtomCount() > 0);
        assertTrue(((IAtomContainer)containersList.get(0)).getBondCount() > 0);
    }


    public void testMethylBenzol() throws Exception {
        String filename = "data/mdl/methylbenzol.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
        assertTrue(((IAtomContainer)containersList.get(0)).getAtomCount() > 0);
        assertTrue(((IAtomContainer)containersList.get(0)).getBondCount() > 0);
    }
    

    public void testPolycarpol() throws Exception {
        String filename = "data/mdl/polycarpol.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
        assertTrue(((IAtomContainer)containersList.get(0)).getAtomCount() > 0);
        assertTrue(((IAtomContainer)containersList.get(0)).getBondCount() > 0);
    }
    
    public void testReserpine() throws Exception {
        String filename = "data/mdl/reserpine.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
        assertTrue(((IAtomContainer)containersList.get(0)).getAtomCount() > 0);
        assertTrue(((IAtomContainer)containersList.get(0)).getBondCount() > 0);
    }    


    public void testSixRing() throws Exception {
        String filename = "data/mdl/six-ring-4x4.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
        assertTrue(((IAtomContainer)containersList.get(0)).getAtomCount() > 0);
        assertTrue(((IAtomContainer)containersList.get(0)).getBondCount() > 0);
    }


    public void testSuperspiro() throws Exception {
        String filename = "data/mdl/superspiro.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
        assertTrue(((IAtomContainer)containersList.get(0)).getAtomCount() > 0);
        assertTrue(((IAtomContainer)containersList.get(0)).getBondCount() > 0);
    }

    public void testGhemicalOutput() throws Exception {
        String filename = "data/mdl/butanoic_acid.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
        assertTrue(((IAtomContainer)containersList.get(0)).getAtomCount() > 0);
        assertTrue(((IAtomContainer)containersList.get(0)).getBondCount() > 0);
    }

    public void testUsesGivenMolecule() throws Exception {
        String filename = "data/mdl/superspiro.mol"; // just a random file
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Molecule superspiro = new Molecule();
        superspiro.setID("superspiro");
        Molecule result = (Molecule)reader.read(superspiro);
        assertEquals(superspiro.getID(), result.getID());
    }

    /** 
     * @cdk.bug 835571
     */
    public void testReadFromStringReader () throws Exception {
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
        ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
        assertNotNull(chemFile);
        assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        assertNotNull(model);

        org.openscience.cdk.interfaces.IMoleculeSet som = model.getMoleculeSet();
        assertNotNull(som);
        assertEquals(1, som.getMoleculeCount());
        org.openscience.cdk.interfaces.IMolecule m = som.getMolecule(0);
        assertNotNull(m);
        assertEquals(9, m.getAtomCount());
        assertEquals(9, m.getBondCount());
    }
    
    public void testRGroup() throws Exception {
        String filename = "data/mdl/SARGROUPTEST.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Molecule mol = (Molecule)reader.read(new Molecule());
        assertEquals("R2",((PseudoAtom)mol.getAtom(19)).getLabel());
    }

    public void testAliasPropertyGroup() throws Exception {
        String filename = "data/mdl/AliasPropertyRGroup.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Molecule mol = (Molecule)reader.read(new Molecule());
        IAtom atom = mol.getAtom(3);
        assertTrue(atom instanceof PseudoAtom);
        assertEquals("R1", ((PseudoAtom)atom).getLabel());
    }

    /**
     * @cdk.bug 1587283
     */
    public void testBug1587283() throws Exception {
        String filename = "data/mdl/bug1587283.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
        assertEquals(15, ((IAtomContainer)containersList.get(0)).getAtomCount());
        assertEquals(16, ((IAtomContainer)containersList.get(0)).getBondCount());
    }
    
    public void testReadProton() throws Exception {
    	String mdl =
            "proton.mol\n" +
            "\n" +
            "\n" +
            "  1  0  0  0  0                 1 V2000\n" +
            "   -0.0073   -0.5272    0.9655 H   0  0  0  0  0\n" +
            "M  CHG  1   1   1\n" +
            "M  END\n";
    	MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdl));
    	Molecule mol = (Molecule)reader.read(new Molecule());
    	assertNotNull(mol);
    	assertEquals(1, mol.getAtomCount());
    	assertEquals(0, mol.getBondCount());
    	assertEquals(1, AtomContainerManipulator.getTotalFormalCharge(mol));
    	IAtom atom = mol.getAtom(0);
    	assertEquals(1, atom.getFormalCharge().intValue());
    }

    public void testReadingCharges() throws CDKException {
    	String filename = "data/mdl/withcharges.mol";
    	logger.info("Testing: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = (IChemFile) reader.read((IChemObject) new org.openscience.cdk.ChemFile());
        assertEquals(1, chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0).getAtom(6).getFormalCharge().intValue());
        assertEquals(-1, chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0).getAtom(8).getFormalCharge().intValue());
    }

    public void testEmptyString() throws Exception {
        String emptyString = "";
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(emptyString));
        IMolecule mol = (IMolecule) reader.read(new NNMolecule());
        assertNull(mol);
    }

    public void testNoAtomCase() throws CDKException {
        String filename = "data/mdl/emptyStructure.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());

        IAtomContainer container = (IAtomContainer) containersList.get(0);
        assertNotNull(container);
        assertEquals(0, container.getAtomCount());
        assertEquals(0, container.getBondCount());


        Map<Object,Object> props = container.getProperties();
        Iterator<Object> keys = props.keySet().iterator();
        ArrayList<String> obsKeys = new ArrayList<String>();
        while (keys.hasNext()) {
            String s = (String) keys.next();
            obsKeys.add(s);
        }

        assertEquals("SubstanceType", obsKeys.get(0));
        assertEquals("TD50 Rat", obsKeys.get(5));
        assertEquals("ChemCount", obsKeys.get(11));

    }

    /**
     * @cdk.bug 1732307

     */
    public void testZeroZCoordinates() throws CDKException {
        String filename = "data/mdl/nozcoord.sdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        Properties prop = new Properties();
        prop.setProperty("ForceReadAs3DCoordinates","true");
        PropertiesListener listener = new PropertiesListener(prop);
        reader.addChemObjectIOListener(listener);
        reader.customizeJob();

        IMolecule mol = (IMolecule) reader.read(DefaultChemObjectBuilder.getInstance().newMolecule());
        assertNotNull(mol);
        assertEquals(5, mol.getAtomCount());

        boolean has3d = GeometryTools.has3DCoordinates(mol);
        assertTrue(has3d);
    }

    /**
     * @cdk.bug 1826577
     */
    public void testHisotopes_Strict() throws Exception {
    	String filename = "data/mdl/hisotopes.mol";
    	logger.info("Testing: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	try {
    		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
    		reader.read((ChemObject)new ChemFile());
    		fail("Expected a CDKException");
    	} catch (Exception exception) {
    		// OK, that's what's is supposed to happen
    	}
    }        

    /**
     * @cdk.bug 1826577
     */
    public void testHisotopes_Relaxed() throws Exception {
    	String filename = "data/mdl/hisotopes.mol";
    	logger.info("Testing: " + filename);
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.RELAXED);
    	IChemFile chemFile = (IChemFile)reader.read((ChemObject)new ChemFile());
    	assertNotNull(chemFile);
    	List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
    	assertFalse(((IAtomContainer)containersList.get(0)).getAtom(1) instanceof PseudoAtom);
    	assertFalse(((IAtomContainer)containersList.get(0)).getAtom(1) instanceof PseudoAtom);
    }        

}
