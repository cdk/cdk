/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.graph.invariant;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Checks the functionality of the ConjugatedPiSystemsCalculator.
 *
 * @cdk.module test-reaction
 */
public class ConjugatedPiSystemsDetectorTest extends CDKTestCase {

    private static IChemObjectBuilder      builder;
    private static LonePairElectronChecker lpcheck;

    private static ILoggingTool            logger = LoggingToolFactory
                                                          .createLoggingTool(ConjugatedPiSystemsDetectorTest.class);

    @BeforeClass
    public static void setup() {
        builder = SilentChemObjectBuilder.getInstance();
        lpcheck = new LonePairElectronChecker();
    }

    @Test
    public void testDetectButadiene() throws Exception {
        logger.info("Entering testDetectButadiene.");
        IAtomContainer mol = null;
        String filename = "data/cml/butadiene.cml";
        mol = readCMLMolecule(filename);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac.getAtomCount());
        Assert.assertEquals(3, ac.getBondCount());

        for (int i = 0; i < ac.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac.getAtom(i)));
        }

        for (int i = 0; i < ac.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac.getBond(i)));
        }
    }

    /**
     *  A unit test for JUnit
     *
     *@return    Description of the Return Value
     */
    @Test
    public void testDetectNaphtalene() throws Exception {
        logger.info("Entering testDetectNaphtalene.");
        IAtomContainer mol = null;
        String filename = "data/cml/naphtalene.cml";
        mol = readCMLMolecule(filename);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac = acSet.getAtomContainer(0);
        Assert.assertEquals(10, ac.getAtomCount());
        Assert.assertEquals(11, ac.getBondCount());

        for (int i = 0; i < ac.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac.getAtom(i)));
        }

        for (int i = 0; i < ac.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac.getBond(i)));
        }
    }

    /**
     *  A unit test for JUnit
     *
     *@return    Description of the Return Value
     */
    @Test
    public void testDetectToluene() throws Exception {
        logger.info("Entering testDetectToluene.");
        IAtomContainer mol = null;
        String filename = "data/cml/toluene.cml";
        mol = readCMLMolecule(filename);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac = acSet.getAtomContainer(0);
        Assert.assertEquals(6, ac.getAtomCount());
        Assert.assertEquals(6, ac.getBondCount());

        for (int i = 0; i < ac.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac.getAtom(i)));
        }

        for (int i = 0; i < ac.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac.getBond(i)));
        }
    }

    /**
     *  A unit test for JUnit
     *
     *@return    Description of the Return Value
     */
    @Test
    public void testNonConnectedPiSystems() throws Exception {
        logger.info("Entering testNonConnectedPiSystems.");
        IAtomContainer mol = null;
        String filename = "data/mdl/nonConnectedPiSystems.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read((ChemObject) new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(2, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac1.getAtomCount());
        Assert.assertEquals(3, ac1.getBondCount());

        for (int i = 0; i < ac1.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }

        for (int i = 0; i < ac1.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }

        IAtomContainer ac2 = acSet.getAtomContainer(1);
        Assert.assertEquals(4, ac2.getAtomCount());
        Assert.assertEquals(3, ac2.getBondCount());

        for (int i = 0; i < ac2.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac2.getAtom(i)));
        }

        for (int i = 0; i < ac2.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac2.getBond(i)));
        }
    }

    /**
     *  A unit test for JUnit
     *
     *@return    Description of the Return Value
     */
    @Test
    public void testPiSystemWithCarbokation() throws Exception {
        logger.info("Entering testPiSystemWithCarbokation.");
        IAtomContainer mol = null;
        String filename = "data/mdl/piSystemWithCarbokation.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read((ChemObject) new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(2, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac1.getAtomCount());
        Assert.assertEquals(3, ac1.getBondCount());

        for (int i = 0; i < ac1.getAtomCount(); i++)
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));

        for (int i = 0; i < ac1.getBondCount(); i++)
            Assert.assertTrue(mol.contains(ac1.getBond(i)));

        IAtomContainer ac2 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac2.getAtomCount());
        Assert.assertEquals(3, ac2.getBondCount());

        for (int i = 0; i < ac2.getAtomCount(); i++)
            Assert.assertTrue(mol.contains(ac2.getAtom(i)));

        for (int i = 0; i < ac2.getBondCount(); i++)
            Assert.assertTrue(mol.contains(ac2.getBond(i)));

    }

    /**
     *  A unit test for JUnit
     *
     *@return    Description of the Return Value
     */
    @Test
    public void testPiSystemWithCumulativeDB() throws Exception {
        logger.info("Entering testPiSystemWithCumulativeDB.");
        IAtomContainer mol = null;
        String filename = "data/mdl/piSystemCumulative.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read((ChemObject) new ChemFile());

        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(2, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac1.getAtomCount());
        Assert.assertEquals(3, ac1.getBondCount());

        for (int i = 0; i < ac1.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }

        for (int i = 0; i < ac1.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }

        IAtomContainer ac2 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac2.getAtomCount());
        Assert.assertEquals(3, ac2.getBondCount());

        for (int i = 0; i < ac2.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }

        for (int i = 0; i < ac2.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }

    }

    /**
     *  A unit test for JUnit
     *
     *  @cdk.inchi InChI=1/C2H4O2/c1-2(3)4/h1H3,(H,3,4)/f/h3
     *
     *@return    Description of the Return Value
     */
    @Test
    public void testAceticAcid() throws Exception {
        IAtomContainer mol = null;
        mol = (new SmilesParser(builder)).parseSmiles("CC(=O)O");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);
        lpcheck.saturate(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(3, ac1.getAtomCount());
        Assert.assertEquals(2, ac1.getBondCount());

        for (int i = 0; i < ac1.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }

        for (int i = 0; i < ac1.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }

    }

    /**
     *  A unit test for JUnit
     *
     *@return    Description of the Return Value
     */
    @Test
    public void testNN_dimethylaniline_cation() throws Exception {
        IAtomContainer mol = null;
        String filename = "data/mdl/NN_dimethylaniline.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = (IChemFile) reader.read((ChemObject) new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);
        lpcheck.saturate(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(6, ac1.getAtomCount());
        Assert.assertEquals(5, ac1.getBondCount());

    }

    /**
     *  A unit test for JUnit
     *
     *@return    Description of the Return Value
     */
    @Test
    public void test1_fluorobutadienene() throws Exception {
        IAtomContainer mol = (new SmilesParser(builder)).parseSmiles("FC=CC=C");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);
        lpcheck.saturate(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(5, ac1.getAtomCount());
        Assert.assertEquals(4, ac1.getBondCount());

    }

    /**
     *  A unit test for JUnit
     *
     *  @cdk.inchi  InChI=1/C2F2/c3-1-2-4
     *
     *@return    Description of the Return Value
     */
    @Test
    public void testEthyne_difluoro() throws Exception {
        IAtomContainer mol = null;
        mol = (new SmilesParser(builder)).parseSmiles("FC#CF");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);
        lpcheck.saturate(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac1.getAtomCount());
        Assert.assertEquals(3, ac1.getBondCount());

    }

    /**
     *  A unit test for JUnit
     *
     *  @cdk.inchi  InChI=1/C7H19N3/c1-8(2)7(9(3)4)10(5)6/h7H,1-6H3
     *
     *@return    Description of the Return Value
     */
    @Test
    public void test3Aminomethane_cation() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "N"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 2, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 3, Order.SINGLE);
        mol.getAtom(3).setFormalCharge(+1);
        mol.addAtom(builder.newInstance(IAtom.class, "N"));
        mol.addBond(3, 4, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(4, 5, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(4, 6, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "N"));
        mol.addBond(3, 7, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(7, 8, Order.SINGLE);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(7, 8, Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);
        lpcheck.saturate(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac1.getAtomCount());
        Assert.assertEquals(3, ac1.getBondCount());

    }

    /**
     *  A unit test for JUnit
     *
     *  @cdk.inchi
     *
     *@return    Description of the Return Value
     */
    private IAtomContainer readCMLMolecule(String filename) throws Exception {
        IAtomContainer mol = null;
        logger.debug("Filename: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);

        IChemFile file = (IChemFile) reader.read(new ChemFile());
        Assert.assertNotNull(file);
        Assert.assertEquals(1, file.getChemSequenceCount());
        IChemSequence sequence = file.getChemSequence(0);
        Assert.assertNotNull(sequence);
        Assert.assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        Assert.assertNotNull(chemModel);
        IAtomContainerSet moleculeSet = chemModel.getMoleculeSet();
        Assert.assertNotNull(moleculeSet);
        Assert.assertEquals(1, moleculeSet.getAtomContainerCount());
        mol = moleculeSet.getAtomContainer(0);
        Assert.assertNotNull(mol);

        return mol;

    }

    /**
     *  A unit test for JUnit: Cyanoallene
     *
     *  @cdk.inchi  InChI=1/C4H3N/c1-2-3-4-5/h3H,1H2
     *
     *@return    Description of the Return Value
     */
    @Test
    public void testCyanoallene() throws Exception {
        IAtomContainer mol = null;
        mol = (new SmilesParser(builder)).parseSmiles("C=C=CC#N");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);
        lpcheck.saturate(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(4, ac1.getAtomCount());
        Assert.assertEquals(3, ac1.getBondCount());

        for (int i = 0; i < ac1.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }

        for (int i = 0; i < ac1.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }

    }

    /**
     *  A unit test for JUnit with [H]C([H])=C([H])[C+]([H])[H]
     */
    @Test
    public void testChargeWithProtonExplicit() throws ClassNotFoundException, CDKException, java.lang.Exception {
        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer mol = sp.parseSmiles("[H]C([H])=C([H])[C+]([H])[H]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        lpcheck.saturate(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(3, ac1.getAtomCount());
        Assert.assertEquals(2, ac1.getBondCount());

        for (int i = 0; i < ac1.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }

        for (int i = 0; i < ac1.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }
    }

    /**
     *  A unit test for JUnit with [H]C([H])=C([H])[C+]([H])[H]
     */
    @Test
    public void testChargeWithProtonImplicit() throws ClassNotFoundException, CDKException, java.lang.Exception {
        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer mol = sp.parseSmiles("C=C[C+]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        lpcheck.saturate(mol);
        Aromaticity.cdkLegacy().apply(mol);

        IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(mol);

        Assert.assertEquals(1, acSet.getAtomContainerCount());
        IAtomContainer ac1 = acSet.getAtomContainer(0);
        Assert.assertEquals(3, ac1.getAtomCount());
        Assert.assertEquals(2, ac1.getBondCount());

        for (int i = 0; i < ac1.getAtomCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getAtom(i)));
        }

        for (int i = 0; i < ac1.getBondCount(); i++) {
            Assert.assertTrue(mol.contains(ac1.getBond(i)));
        }
    }
}
