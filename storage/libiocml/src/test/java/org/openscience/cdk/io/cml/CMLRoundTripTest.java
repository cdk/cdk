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
package org.openscience.cdk.io.cml;

import java.io.InputStream;
import java.util.Iterator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.BondManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestCase for reading CML 2 files using a few test files
 * in data/cmltest.
 *
 * @cdk.require xom-1.0.jar
 * @cdk.require java1.5+
 */
class CMLRoundTripTest extends CDKTestCase {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(CMLRoundTripTest.class);
    private static Convertor    convertor;

    @BeforeAll
    static void setup() {
        convertor = new Convertor(false, "");
    }

    @Test
    void testAtom() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("N");
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertEquals(atom.getSymbol(), roundTrippedAtom.getSymbol());
    }

    @Test
    void testAtomId() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("N");
        atom.setID("N1");
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertEquals(atom.getID(), roundTrippedAtom.getID());
    }

    @Test
    void testAtom2D() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("N");
        Point2d p2d = new Point2d(1.3, 1.4);
        atom.setPoint2d(p2d);
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getPoint2d(), roundTrippedAtom.getPoint2d(), 0.00001);
    }

    @Test
    void testAtom3D() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("N");
        Point3d p3d = new Point3d(1.3, 1.4, 0.9);
        atom.setPoint3d(p3d);
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getPoint3d(), roundTrippedAtom.getPoint3d(), 0.00001);
    }

    @Test
    void testAtom2DAnd3D() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("N");
        Point2d p2d = new Point2d(1.3, 1.4);
        atom.setPoint2d(p2d);
        Point3d p3d = new Point3d(1.3, 1.4, 0.9);
        atom.setPoint3d(p3d);
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getPoint2d(), roundTrippedAtom.getPoint2d(), 0.00001);
        assertEquals(atom.getPoint3d(), roundTrippedAtom.getPoint3d(), 0.00001);
    }

    @Test
    void testAtomFract3D() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("N");
        Point3d p3d = new Point3d(0.3, 0.4, 0.9);
        atom.setFractionalPoint3d(p3d);
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getFractionalPoint3d(), roundTrippedAtom.getFractionalPoint3d(), 0.00001);
    }

    @Test
    void testPseudoAtom() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        PseudoAtom atom = new PseudoAtom("N");
        atom.setLabel("Glu55");
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertNotNull(roundTrippedAtom);
        Assertions.assertTrue(roundTrippedAtom instanceof IPseudoAtom);
        Assertions.assertEquals("Glu55", ((IPseudoAtom) roundTrippedAtom).getLabel());
    }

    /**
     * @cdk.bug 1455346
     */
    @Test
    void testChemModel() throws Exception {
        ChemModel model = new ChemModel();
        IAtomContainerSet moleculeSet = new AtomContainerSet();
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        PseudoAtom atom = new PseudoAtom("N");
        mol.addAtom(atom);
        moleculeSet.addAtomContainer(mol);
        model.setMoleculeSet(moleculeSet);

        IChemModel roundTrippedModel = CMLRoundTripTool.roundTripChemModel(convertor, model);

        IAtomContainerSet roundTrippedMolSet = roundTrippedModel.getMoleculeSet();
        Assertions.assertNotNull(roundTrippedMolSet);
        Assertions.assertEquals(1, roundTrippedMolSet.getAtomContainerCount());
        IAtomContainer roundTrippedMolecule = roundTrippedMolSet.getAtomContainer(0);
        Assertions.assertNotNull(roundTrippedMolecule);
        Assertions.assertEquals(1, roundTrippedMolecule.getAtomCount());
    }

    @Test
    void testAtomFormalCharge() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("N");
        int formalCharge = +1;
        atom.setFormalCharge(formalCharge);
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertEquals(atom.getFormalCharge(), roundTrippedAtom.getFormalCharge());
    }

    /**
     * @cdk.bug 1713398
     */
    @Test
    void testHydrogenCount() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("N");
        atom.setImplicitHydrogenCount(3);
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertEquals(atom.getImplicitHydrogenCount(), roundTrippedAtom.getImplicitHydrogenCount());
    }

    /**
     * @cdk.bug 1713398
     */
    @Test
    void testHydrogenCount_UNSET() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("N");
        atom.setImplicitHydrogenCount((Integer) CDKConstants.UNSET);
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertEquals(CDKConstants.UNSET, roundTrippedAtom.getImplicitHydrogenCount());
    }

    @Disabled("Have to figure out how to store partial charges in CML2")
    void testAtomPartialCharge() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("N");
        double partialCharge = 0.5;
        atom.setCharge(partialCharge);
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertEquals(atom.getCharge(), roundTrippedAtom.getCharge(), 0.0001);
    }

    @Disabled("Have to figure out how to store atom parity in CML2")
    void testAtomStereoParity() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("C");
        int stereo = CDKConstants.STEREO_ATOM_PARITY_PLUS;
        atom.setStereoParity(stereo);
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertEquals(atom.getStereoParity(), roundTrippedAtom.getStereoParity());
    }

    @Test
    void testIsotope() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("C");
        atom.setMassNumber(13);
        mol.addAtom(atom);
        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertEquals(atom.getMassNumber(), roundTrippedAtom.getMassNumber());
    }

    /**
     * @cdk.bug 1014344
     */
    @Disabled("Functionality not yet implemented - exact mass can not be written/read")
    void testIsotope_ExactMass() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("C");
        atom.setExactMass(13.0);
        mol.addAtom(atom);
        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertNotNull(atom.getExactMass());
        Assertions.assertNotNull(roundTrippedAtom.getExactMass());
        Assertions.assertEquals(atom.getExactMass(), roundTrippedAtom.getExactMass(), 0.01);
    }

    /**
     * @cdk.bug 1014344
     */
    @Disabled("Functionality not yet implemented - natural abundance can not be written/read")
    void testIsotope_Abundance() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("C");
        atom.setNaturalAbundance(1.0);
        mol.addAtom(atom);
        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertNotNull(atom.getNaturalAbundance());
        Assertions.assertNotNull(roundTrippedAtom.getNaturalAbundance());
        Assertions.assertEquals(atom.getNaturalAbundance(), roundTrippedAtom.getNaturalAbundance(), 0.01);
    }

    /**
     * Test roundtripping of MassNumber.
     * @throws Exception
     */
    @Test
    void testMassNumber() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("C");
        atom.setMassNumber(12);
        mol.addAtom(atom);
        Assertions.assertEquals(12, atom.getMassNumber().intValue());

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertEquals(atom.getMassNumber(), roundTrippedAtom.getMassNumber());
    }

    @Test
    void testBond() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, IBond.Order.SINGLE);
        mol.addBond(bond);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(2, roundTrippedMol.getAtomCount());
        Assertions.assertEquals(1, roundTrippedMol.getBondCount());
        IBond roundTrippedBond = roundTrippedMol.getBond(0);
        Assertions.assertEquals(2, roundTrippedBond.getAtomCount());
        Assertions.assertEquals("C", roundTrippedBond.getBegin().getSymbol()); // preserved direction?
        Assertions.assertEquals("O", roundTrippedBond.getEnd().getSymbol());
        Assertions.assertEquals(bond.getOrder(), roundTrippedBond.getOrder());
    }

    @Test
    void testBondID() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, IBond.Order.SINGLE);
        bond.setID("b1");
        mol.addBond(bond);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        IBond roundTrippedBond = roundTrippedMol.getBond(0);
        Assertions.assertEquals(bond.getID(), roundTrippedBond.getID());
    }

    @Test
    void testBondStereo() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, IBond.Order.SINGLE);
        IBond.Display stereo = IBond.Display.WedgeBegin;
        bond.setDisplay(stereo);
        mol.addBond(bond);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(2, roundTrippedMol.getAtomCount());
        Assertions.assertEquals(1, roundTrippedMol.getBondCount());
        IBond roundTrippedBond = roundTrippedMol.getBond(0);
        Assertions.assertEquals(bond.getDisplay(), roundTrippedBond.getDisplay());
    }

    @Test
    void testBondAromatic() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        // surely, this bond is not aromatic... but fortunately, file formats do not care about chemistry
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, IBond.Order.SINGLE);
        bond.setFlag(IChemObject.AROMATIC, true);
        mol.addBond(bond);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(2, roundTrippedMol.getAtomCount());
        Assertions.assertEquals(1, roundTrippedMol.getBondCount());
        IBond roundTrippedBond = roundTrippedMol.getBond(0);
        Assertions.assertEquals(bond.getFlag(IChemObject.AROMATIC), roundTrippedBond.getFlag(IChemObject.AROMATIC));
        Assertions.assertEquals(bond.getOrder(), roundTrippedBond.getOrder());
    }

    /**
     * @cdk.bug 1713398
     */
    @Test
    void testBondAromatic_Double() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        // surely, this bond is not aromatic... but fortunately, file formats do not care about chemistry
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, IBond.Order.DOUBLE);
        bond.setFlag(IChemObject.AROMATIC, true);
        mol.addBond(bond);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(2, roundTrippedMol.getAtomCount());
        Assertions.assertEquals(1, roundTrippedMol.getBondCount());
        IBond roundTrippedBond = roundTrippedMol.getBond(0);
        Assertions.assertEquals(bond.getFlag(IChemObject.AROMATIC), roundTrippedBond.getFlag(IChemObject.AROMATIC));
        Assertions.assertEquals(bond.getOrder(), roundTrippedBond.getOrder());
    }

    @Test
    void testPartialCharge() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("C");
        mol.addAtom(atom);
        double charge = -0.267;
        atom.setCharge(charge);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertEquals(charge, roundTrippedAtom.getCharge(), 0.0001);
    }

    @Test
    void testInChI() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        String inchi = "InChI=1/CH2O2/c2-1-3/h1H,(H,2,3)";
        mol.setProperty(CDKConstants.INCHI, inchi);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        Assertions.assertNotNull(roundTrippedMol);

        Assertions.assertEquals(inchi, roundTrippedMol.getProperty(CDKConstants.INCHI));
    }

    @Test
    void testSpinMultiplicity() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("C");
        mol.addAtom(atom);
        mol.addSingleElectron(new SingleElectron(atom));

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        Assertions.assertEquals(1, roundTrippedMol.getElectronContainerCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertEquals(1, roundTrippedMol.getConnectedSingleElectronsCount(roundTrippedAtom));
    }

    @Test
    void testReaction() throws Exception {
        logger.debug("********** TEST REACTION **********");
        IReaction reaction = new Reaction();
        reaction.setID("reaction.1");
        IAtomContainer reactant = reaction.getBuilder().newInstance(IAtomContainer.class);
        reactant.setID("react");
        IAtom atom = reaction.getBuilder().newInstance(IAtom.class, "C");
        reactant.addAtom(atom);
        reaction.addReactant(reactant);

        IAtomContainer product = reaction.getBuilder().newInstance(IAtomContainer.class);
        product.setID("product");
        atom = reaction.getBuilder().newInstance(IAtom.class, "R");
        product.addAtom(atom);
        reaction.addProduct(product);

        IAtomContainer agent = reaction.getBuilder().newInstance(IAtomContainer.class);
        agent.setID("water");
        atom = reaction.getBuilder().newInstance(IAtom.class, "H");
        agent.addAtom(atom);
        reaction.addAgent(agent);

        IReaction roundTrippedReaction = CMLRoundTripTool.roundTripReaction(convertor, reaction);
        Assertions.assertNotNull(roundTrippedReaction);
        Assertions.assertEquals("reaction.1", roundTrippedReaction.getID());

        Assertions.assertNotNull(roundTrippedReaction);
        IAtomContainerSet reactants = roundTrippedReaction.getReactants();
        Assertions.assertNotNull(reactants);
        Assertions.assertEquals(1, reactants.getAtomContainerCount());
        IAtomContainer roundTrippedReactant = reactants.getAtomContainer(0);
        Assertions.assertEquals("react", roundTrippedReactant.getID());
        Assertions.assertEquals(1, roundTrippedReactant.getAtomCount());

        IAtomContainerSet products = roundTrippedReaction.getProducts();
        Assertions.assertNotNull(products);
        Assertions.assertEquals(1, products.getAtomContainerCount());
        IAtomContainer roundTrippedProduct = products.getAtomContainer(0);
        Assertions.assertEquals("product", roundTrippedProduct.getID());
        Assertions.assertEquals(1, roundTrippedProduct.getAtomCount());

        IAtomContainerSet agents = roundTrippedReaction.getAgents();
        Assertions.assertNotNull(agents);
        Assertions.assertEquals(1, agents.getAtomContainerCount());
        IAtomContainer roundTrippedAgent = agents.getAtomContainer(0);
        Assertions.assertEquals("water", roundTrippedAgent.getID());
        Assertions.assertEquals(1, roundTrippedAgent.getAtomCount());
    }

    @Test
    void testDescriptorValue() throws Exception {
        IAtomContainer molecule = TestMoleculeFactory.makeBenzene();

        String[] propertyName = {"testKey1", "testKey2"};
        String[] propertyValue = {"testValue1", "testValue2"};

        for (int i = 0; i < propertyName.length; i++)
            molecule.setProperty(propertyName[i], propertyValue[i]);
        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, molecule);

        for (int i = 0; i < propertyName.length; i++) {
            Assertions.assertNotNull(roundTrippedMol.getProperty(propertyName[i]));
            Assertions.assertEquals(propertyValue[i], roundTrippedMol.getProperty(propertyName[i]));
        }
    }

    /**
     * Tests of bond order information is stored even when aromaticity is given.
     *
     * @throws Exception
     */
    @Test
    void testAromaticity() throws Exception {
        IAtomContainer molecule = TestMoleculeFactory.makeBenzene();
        for (IBond bond : molecule.bonds()) {
            bond.setFlag(IChemObject.AROMATIC, true);
        }

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, molecule);
        Iterator<IBond> bonds = roundTrippedMol.bonds().iterator();
        double orderSum = BondManipulator.getSingleBondEquivalentSum(bonds);
        while (bonds.hasNext()) {
            Assertions.assertTrue(bonds.next().getFlag(IChemObject.AROMATIC));
        }
        Assertions.assertEquals(9.0, orderSum, 0.001);
    }

    @Test
    void testAtomAromaticity() throws Exception {
        IAtomContainer molecule = TestMoleculeFactory.makeBenzene();
        for (IAtom atom : molecule.atoms()) {
            atom.setFlag(IChemObject.AROMATIC, true);
        }

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, molecule);
        for (IAtom atom : roundTrippedMol.atoms()) {
            Assertions.assertTrue(atom.getFlag(IChemObject.AROMATIC));
        }
    }

    /**
     * Tests whether the custom atom properties survive the CML round-trip
     * @throws Exception
     *
     * @cdk.bug 1930029
     */
    @Test
    void testAtomProperty() throws Exception {
        String[] key = {"customAtomProperty1", "customAtomProperty2"};
        String[] value = {"true", "false"};

        IAtomContainer mol = TestMoleculeFactory.makeBenzene();
        for (IAtom a : mol.atoms()) {
            for (int i = 0; i < key.length; i++)
                a.setProperty(key[i], value[i]);
        }

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        //Assert.assertEquals(convertor.cdkMoleculeToCMLMolecule(mol).toXML(),
        //	   convertor.cdkMoleculeToCMLMolecule(roundTrippedMol).toXML());

        for (IAtom a : roundTrippedMol.atoms()) {
            for (int i = 0; i < key.length; i++) {
                Object actual = a.getProperty(key[i]);
                Assertions.assertNotNull(actual);
                Assertions.assertEquals(value[i], actual);
            }
        }
    }

    /**
     * Tests whether the custom bond properties survive the CML round-trip
     * @throws Exception
     *
     * @cdk.bug 1930029
     */
    @Test
    void testBondProperty() throws Exception {
        String[] key = {"customBondProperty1", "customBondProperty2"};
        String[] value = {"true", "false"};
        IAtomContainer mol = TestMoleculeFactory.makeBenzene();
        for (IBond b : mol.bonds()) {
            for (int i = 0; i < key.length; i++)
                b.setProperty(key[i], value[i]);
        }

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        //Assert.assertEquals(convertor.cdkMoleculeToCMLMolecule(mol).toXML(),
        //	   convertor.cdkMoleculeToCMLMolecule(roundTrippedMol).toXML());

        for (IBond b : roundTrippedMol.bonds()) {
            for (int i = 0; i < key.length; i++) {
                Object actual = b.getProperty(key[i]);
                Assertions.assertNotNull(actual);
                Assertions.assertEquals(value[i], actual);
            }
        }
    }

    /**
     * Tests whether the custom molecule properties survive the CML round-trip
     * @throws Exception
     *
     * @cdk.bug 1930029
     */
    @Test
    void testMoleculeProperty() throws Exception {
        String[] key = {"customMoleculeProperty1", "customMoleculeProperty2"};
        String[] value = {"true", "false"};

        IAtomContainer mol = TestMoleculeFactory.makeAdenine();
        for (int i = 0; i < key.length; i++) {
            mol.setProperty(key[i], value[i]);
        }
        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        //Assert.assertEquals(convertor.cdkMoleculeToCMLMolecule(mol).toXML(),
        //	   convertor.cdkMoleculeToCMLMolecule(roundTrippedMol).toXML());
        for (int i = 0; i < key.length; i++) {
            Object actual = roundTrippedMol.getProperty(key[i]);
            Assertions.assertNotNull(actual);
            Assertions.assertEquals(value[i], actual);
        }
    }

    @Test
    void testMoleculeSet() throws Exception {
        IAtomContainerSet list = new AtomContainerSet();
        list.addAtomContainer(DefaultChemObjectBuilder.getInstance().newAtomContainer());
        list.addAtomContainer(DefaultChemObjectBuilder.getInstance().newAtomContainer());
        IChemModel model = new ChemModel();
        model.setMoleculeSet(list);

        IChemModel roundTripped = CMLRoundTripTool.roundTripChemModel(convertor, model);
        IAtomContainerSet newList = roundTripped.getMoleculeSet();
        Assertions.assertNotNull(newList);
        Assertions.assertEquals(2, newList.getAtomContainerCount());
        Assertions.assertNotNull(newList.getAtomContainer(0));
        Assertions.assertNotNull(newList.getAtomContainer(1));
    }

    /**
     * @cdk.bug 1930029
     */
    @Test
    void testAtomProperties() throws Exception {
        String filename = "custompropertiestest.cml";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        IAtomContainer container = ChemFileManipulator.getAllAtomContainers(chemFile).get(0);
        for (int i = 0; i < container.getAtomCount(); i++) {
            Assertions.assertEquals(2, container.getAtom(i).getProperties().size());
        }
    }

    /**
     * Test roundtripping of Unset property (Hydrogencount).
     * @throws Exception
     */
    @Test
    void testUnsetHydrogenCount() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("C");
        atom.setImplicitHydrogenCount(null);
        Assertions.assertNull(atom.getImplicitHydrogenCount());
        mol.addAtom(atom);

        IAtomContainer roundTrippedMol = CMLRoundTripTool.roundTripMolecule(convertor, mol);

        Assertions.assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        Assertions.assertNull(roundTrippedAtom.getImplicitHydrogenCount());
    }

}
