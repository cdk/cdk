/* Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.tools.manipulator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.*;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.io.MDLRXNReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Egon Willighagen
 * @author uli-f
 * @cdk.module test-standard
 * @cdk.created 2003-07-23
 */
public class ReactionManipulatorTest extends CDKTestCase {

    private IReaction reaction;
    private final IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

    public ReactionManipulatorTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        String filename1 = "reaction-1.rxn";
        InputStream ins1 = this.getClass().getResourceAsStream(filename1);
        MDLRXNReader reader1 = new MDLRXNReader(ins1);
        ReactionSet set = reader1.read(new ReactionSet());
        reaction = set.getReaction(0);
        reader1.close();
    }

    @Test
    public void testReverse_IReaction() {
        Reaction reaction = new Reaction();
        reaction.setDirection(IReaction.Direction.BACKWARD);
        IAtomContainer water = new AtomContainer();
        reaction.addReactant(water, 3.0);
        reaction.addReactant(new AtomContainer());
        reaction.addProduct(new AtomContainer());

        Reaction reversedReaction = (Reaction) ReactionManipulator.reverse(reaction);
        Assert.assertEquals(IReaction.Direction.FORWARD, reversedReaction.getDirection());
        Assert.assertEquals(2, reversedReaction.getProductCount());
        Assert.assertEquals(1, reversedReaction.getReactantCount());
        Assert.assertEquals(3.0, reversedReaction.getProductCoefficient(water), 0.00001);
    }

    @Test
    public void testGetAllIDs_IReaction() {
        Reaction reaction = new Reaction();
        reaction.setID("r1");
        IAtomContainer water = new AtomContainer();
        water.setID("m1");
        Atom oxygen = new Atom("O");
        oxygen.setID("a1");
        water.addAtom(oxygen);
        reaction.addReactant(water);
        reaction.addProduct(water);

        List<String> ids = ReactionManipulator.getAllIDs(reaction);
        Assert.assertNotNull(ids);
        Assert.assertEquals(5, ids.size());
    }

    /**
     * A unit test suite for JUnit. Test of mapped IAtoms
     */
    @Test
    public void testGetMappedChemObject_IReaction_IAtom() throws Exception {
        IReaction reaction = builder.newInstance(IReaction.class);
        IAtomContainer reactant = (new SmilesParser(builder)).parseSmiles("[C+]-C=C");
        IAtomContainer product = (new SmilesParser(builder)).parseSmiles("C=C=C");

        IMapping mapping = builder.newInstance(IMapping.class, reactant.getAtom(0), product.getAtom(0));
        reaction.addMapping(mapping);
        mapping = builder.newInstance(IMapping.class, reactant.getAtom(1), product.getAtom(1));
        reaction.addMapping(mapping);
        mapping = builder.newInstance(IMapping.class, reactant.getAtom(2), product.getAtom(2));
        reaction.addMapping(mapping);

        reaction.addReactant(reactant);
        reaction.addProduct(product);

        IAtom mappedAtom = (IAtom) ReactionManipulator.getMappedChemObject(reaction, reactant.getAtom(0));
        Assert.assertEquals(mappedAtom, product.getAtom(0));

        mappedAtom = (IAtom) ReactionManipulator.getMappedChemObject(reaction, product.getAtom(1));
        Assert.assertEquals(mappedAtom, reactant.getAtom(1));

    }

    /**
     * A unit test suite for JUnit. Test of mapped IBond
     */
    @Test
    public void testGetMappedChemObject_IReaction_IBond() throws
            java.lang.Exception {
        IReaction reaction = builder.newInstance(IReaction.class);
        IAtomContainer reactant = (new SmilesParser(builder)).parseSmiles("[C+]-C=C");
        IAtomContainer product = (new SmilesParser(builder)).parseSmiles("C=C=C");

        IMapping mapping = builder.newInstance(IMapping.class, reactant.getAtom(0), product.getAtom(0));
        reaction.addMapping(mapping);
        mapping = builder.newInstance(IMapping.class, reactant.getBond(0), product.getBond(0));
        reaction.addMapping(mapping);
        mapping = builder.newInstance(IMapping.class, reactant.getBond(1), product.getBond(1));
        reaction.addMapping(mapping);

        reaction.addReactant(reactant);
        reaction.addProduct(product);

        IBond mappedBond = (IBond) ReactionManipulator.getMappedChemObject(reaction, reactant.getBond(0));
        Assert.assertEquals(mappedBond, product.getBond(0));

        mappedBond = (IBond) ReactionManipulator.getMappedChemObject(reaction, product.getBond(1));
        Assert.assertEquals(mappedBond, reactant.getBond(1));
    }

    @Test
    public void testGetAtomCount_IReaction() throws Exception {
        Assert.assertEquals(19, ReactionManipulator.getAtomCount(reaction));
    }

    @Test
    public void testGetBondCount_IReaction() throws Exception {
        Assert.assertEquals(18, ReactionManipulator.getBondCount(reaction));
    }

    @Test
    public void testGetAllAtomContainers_IReaction() throws Exception {
        Assert.assertEquals(3, ReactionManipulator.getAllAtomContainers(reaction).size());
    }

    @Test
    public void testSetAtomProperties_IReactionSet_Object_Object() throws Exception {
        ReactionManipulator.setAtomProperties(reaction, "test", "ok");
        for (IAtomContainer container : ReactionManipulator.getAllAtomContainers(reaction)) {
            for (IAtom atom : container.atoms()) {
                Assert.assertNotNull(atom.getProperty("test"));
                Assert.assertEquals("ok", atom.getProperty("test"));
            }
        }
    }

    @Test
    public void testGetAllChemObjects_IReactionSet() {
        List<IChemObject> allObjects = ReactionManipulator.getAllChemObjects(reaction);
        // does not recurse beyond the IAtomContainer, so:
        // reaction, 2xreactant, 1xproduct
        Assert.assertEquals(4, allObjects.size());
    }

    @Test
    public void testGetRelevantAtomContainer_IReaction_IAtom() {
        for (IAtomContainer container : ReactionManipulator.getAllAtomContainers(reaction)) {
            IAtom anAtom = container.getAtom(0);
            Assert.assertEquals(container, ReactionManipulator.getRelevantAtomContainer(reaction, anAtom));
        }
    }

    @Test
    public void testGetRelevantAtomContainer_IReaction_IBond() {
        for (IAtomContainer container : ReactionManipulator.getAllAtomContainers(reaction)) {
            IBond aBond = container.getBond(0);
            Assert.assertEquals(container, ReactionManipulator.getRelevantAtomContainer(reaction, aBond));
        }
    }

    @Test
    public void testRemoveElectronContainer_IReaction_IElectronContainer() {
        IReaction reaction = builder.newInstance(IReaction.class);
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, Order.SINGLE);
        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertEquals(1, mol.getBondCount());
        reaction.addReactant(mol);
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        ReactionManipulator.removeElectronContainer(reaction, mol.getBond(0));

        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertEquals(0, mol.getBondCount());

    }

    @Test
    public void testRemoveAtomAndConnectedElectronContainers_IReaction_IAtom() {
        IReaction reaction = builder.newInstance(IReaction.class);
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, Order.SINGLE);
        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertEquals(1, mol.getBondCount());
        reaction.addReactant(mol);
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        ReactionManipulator.removeAtomAndConnectedElectronContainers(reaction, mol.getAtom(0));

        Assert.assertEquals(1, mol.getAtomCount());
        Assert.assertEquals(0, mol.getBondCount());
    }

    @Test
    public void testGetAllMolecules_IReaction() {
        IReaction reaction = builder.newInstance(IReaction.class);
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        Assert.assertEquals(5, ReactionManipulator.getAllMolecules(reaction).getAtomContainerCount());
    }

    @Test
    public void testGetAllProducts_IReaction() {
        IReaction reaction = builder.newInstance(IReaction.class);
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        Assert.assertEquals(3, ReactionManipulator.getAllReactants(reaction).getAtomContainerCount());
    }

    @Test
    public void testGetAllReactants_IReaction() {
        IReaction reaction = builder.newInstance(IReaction.class);
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        Assert.assertEquals(2, ReactionManipulator.getAllProducts(reaction).getAtomContainerCount());
    }

    @Test
    public void inliningReactions() throws CDKException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IReaction reaction = smipar.parseReactionSmiles("CCO.CC(=O)O>[H+]>CCOC(=O)C.O ethyl esterification");
        SmilesGenerator smigen = SmilesGenerator.isomeric();
        // convert to molecule
        IAtomContainer mol = ReactionManipulator.toMolecule(reaction);
        assertThat(smigen.create(mol),
                is("CCO.CC(=O)O.[H+].CCOC(=O)C.O"));
        assertThat(smigen.createReactionSMILES(ReactionManipulator.toReaction(mol)),
                is("CCO.CC(=O)O>[H+]>CCOC(=O)C.O"));
    }

    @Test
    public void inliningReactionsWithRadicals() throws CDKException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IReaction reaction = smipar.parseReactionSmiles("[CH2]CO.CC(=O)O>[H+]>CCOC(=O)C.O |^1:0| ethyl esterification");
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.CxSmiles);
        // convert to molecule
        IAtomContainer mol = ReactionManipulator.toMolecule(reaction);
        assertThat(smigen.create(mol),
                is("[CH2]CO.CC(=O)O.[H+].CCOC(=O)C.O |^1:0|"));
        assertThat(smigen.createReactionSMILES(ReactionManipulator.toReaction(mol)),
                is("[CH2]CO.CC(=O)O>[H+]>CCOC(=O)C.O |^1:0|"));
    }

    @Test(expected = Test.None.class)
    public void perceiveAtomTypesAndConfigureAtomsReactionNullTest() throws CDKException {
        ReactionManipulator.perceiveAtomTypesAndConfigureAtoms(null);
    }

    @Test(expected = Test.None.class)
    public void perceiveAtomTypesAndConfigureAtomsUnknownAtomTypeTest() throws CDKException {
        // arrange
        IAtomContainer reactant = new AtomContainer();
        reactant.addAtom(new Atom("R"));
        IAtomContainer product = new AtomContainer();
        reactant.addAtom(new Atom("C"));
        IReaction reaction = new Reaction();
        reaction.addReactant(reactant);
        reaction.addProduct(product);

        // act
        ReactionManipulator.perceiveAtomTypesAndConfigureAtoms(reaction);

        // assert
        // nothing to do, no exception should be thrown if unknown atom types are encountered
    }

    @Test
    public void perceiveAtomTypesAndConfigureAtomsSimpleReactionTest() throws CDKException {
        // arrange
        // reactant one: CC=C
        IAtom reactantOneAtomOne = new Atom("C");
        IAtom reactantOneAtomTwo = new Atom("C");
        IAtom reactantOneAtomThree = new Atom("C");
        IBond reactantOneBondOne = new Bond(reactantOneAtomOne, reactantOneAtomTwo, Order.SINGLE);
        IBond reactantOneBondTwo = new Bond(reactantOneAtomTwo, reactantOneAtomThree, Order.DOUBLE);
        IAtomContainer reactantOne = new AtomContainer();
        reactantOne.addAtom(reactantOneAtomOne);
        reactantOne.addAtom(reactantOneAtomTwo);
        reactantOne.addAtom(reactantOneAtomThree);
        reactantOne.addBond(reactantOneBondOne);
        reactantOne.addBond(reactantOneBondTwo);

        // reactant two: Br
        IAtom reactantTwoAtom1 = new Atom("Br");
        IAtomContainer reactantTwo = new AtomContainer();
        reactantTwo.addAtom(reactantTwoAtom1);

        // agent one: O
        IAtom agentOneAtomOne = new Atom("O");
        IAtomContainer agentOne = new AtomContainer();
        agentOne.addAtom(agentOneAtomOne);

        // product one: CC(Br)C
        IAtom productOneAtomOne = new Atom("C");
        IAtom productOneAtomTwo = new Atom("C");
        IAtom productOneAtomThree = new Atom("Br");
        IAtom productOneAtomFour = new Atom("C");
        IBond productOneBondOne = new Bond(productOneAtomOne, productOneAtomTwo, Order.SINGLE);
        IBond productOneBondTwo = new Bond(productOneAtomTwo, productOneAtomThree, Order.SINGLE);
        IBond productOneBondThree = new Bond(productOneAtomTwo, productOneAtomFour, Order.SINGLE);
        IAtomContainer productOne = new AtomContainer();
        productOne.addAtom(productOneAtomOne);
        productOne.addAtom(productOneAtomTwo);
        productOne.addAtom(productOneAtomThree);
        productOne.addAtom(productOneAtomFour);
        productOne.addBond(productOneBondOne);
        productOne.addBond(productOneBondTwo);
        productOne.addBond(productOneBondThree);

        // add reactant, agent and product to the reaction
        IReaction reaction = new Reaction();
        reaction.addReactant(reactantOne);
        reaction.addReactant(reactantTwo);
        reaction.addAgent(agentOne);
        reaction.addProduct(productOne);

        // collect all IAtomContainers of the reaction in a single IAtomContainerSet
        IAtomContainerSet atomContainerSet = new AtomContainerSet();
        atomContainerSet.add(reaction.getProducts());
        atomContainerSet.add(reaction.getAgents());
        atomContainerSet.add(reaction.getAgents());

        // verify that atom types are not configured
        for (IAtomContainer atomContainer : atomContainerSet.atomContainers()) {
            for (IAtom atom : atomContainer.atoms()) {
                if (atom.getAtomTypeName() != CDKConstants.UNSET ||
                        atom.getMaxBondOrder() != CDKConstants.UNSET ||
                        atom.getBondOrderSum() != CDKConstants.UNSET ||
                        atom.getValency() != CDKConstants.UNSET ||
                        atom.getHybridization() != CDKConstants.UNSET ||
                        atom.getFormalNeighbourCount() != CDKConstants.UNSET
                ) {
                    Assert.fail("The atom types should not be configured.");
                }
            }
        }

        // act
        ReactionManipulator.perceiveAtomTypesAndConfigureAtoms(reaction);

        // assert that atom types are now configured
        for (IAtomContainer atomContainer : atomContainerSet.atomContainers()) {
            for (IAtom atom : atomContainer.atoms()) {
                if (atom.getAtomTypeName() == CDKConstants.UNSET ||
                        atom.getMaxBondOrder() == CDKConstants.UNSET ||
                        atom.getBondOrderSum() == CDKConstants.UNSET ||
                        atom.getValency() == CDKConstants.UNSET ||
                        atom.getHybridization() == CDKConstants.UNSET ||
                        atom.getFormalNeighbourCount() == CDKConstants.UNSET
                ) {
                    Assert.fail("The atom types should be configured after calling the method ReactionManipulator.perceiveAtomTypesAndConfigureAtoms(IReaction).");
                }
            }
        }
    }

    @Test(expected = Test.None.class)
    public void perceiveAtomTypesAndConfigureUnsetPropertiesReactionNullTest() throws CDKException {
        ReactionManipulator.perceiveAtomTypesAndConfigureAtoms(null);
    }

    @Test
    public void perceiveAtomTypesAndConfigureUnsetPropertiesSimpleReactionTest() throws CDKException {
        // arrange
        // reactant one: CC=C
        IAtom reactantOneAtomOne = new Atom("C");
        IAtom reactantOneAtomTwo = new Atom("C");
        IAtom reactantOneAtomThree = new Atom("C");
        // set a property and then assess later whether this property has been changed
        reactantOneAtomThree.setFormalNeighbourCount(2);
        IBond reactantOneBondOne = new Bond(reactantOneAtomOne, reactantOneAtomTwo, Order.SINGLE);
        IBond reactantOneBondTwo = new Bond(reactantOneAtomTwo, reactantOneAtomThree, Order.DOUBLE);
        IAtomContainer reactantOne = new AtomContainer();
        reactantOne.addAtom(reactantOneAtomOne);
        reactantOne.addAtom(reactantOneAtomTwo);
        reactantOne.addAtom(reactantOneAtomThree);
        reactantOne.addBond(reactantOneBondOne);
        reactantOne.addBond(reactantOneBondTwo);

        // reactant two: Br
        IAtom reactantTwoAtom1 = new Atom("Br");
        IAtomContainer reactantTwo = new AtomContainer();
        reactantTwo.addAtom(reactantTwoAtom1);

        // agent one: O
        IAtom agentOneAtomOne = new Atom("O");
        IAtomContainer agentOne = new AtomContainer();
        agentOne.addAtom(agentOneAtomOne);

        // product one: CC(Br)C
        IAtom productOneAtomOne = new Atom("C");
        IAtom productOneAtomTwo = new Atom("C");
        IAtom productOneAtomThree = new Atom("Br");
        IAtom productOneAtomFour = new Atom("C");
        // set a property and then assess later whether this property has been changed
        productOneAtomFour.setFormalNeighbourCount(2);
        IBond productOneBondOne = new Bond(productOneAtomOne, productOneAtomTwo, Order.SINGLE);
        IBond productOneBondTwo = new Bond(productOneAtomTwo, productOneAtomThree, Order.SINGLE);
        IBond productOneBondThree = new Bond(productOneAtomTwo, productOneAtomFour, Order.SINGLE);
        IAtomContainer productOne = new AtomContainer();
        productOne.addAtom(productOneAtomOne);
        productOne.addAtom(productOneAtomTwo);
        productOne.addAtom(productOneAtomThree);
        productOne.addAtom(productOneAtomFour);
        productOne.addBond(productOneBondOne);
        productOne.addBond(productOneBondTwo);
        productOne.addBond(productOneBondThree);

        // add reactant, agent and product to the reaction
        IReaction reaction = new Reaction();
        reaction.addReactant(reactantOne);
        reaction.addReactant(reactantTwo);
        reaction.addAgent(agentOne);
        reaction.addProduct(productOne);

        // collect all IAtomContainers of the reaction in a single IAtomContainerSet
        IAtomContainerSet atomContainerSet = new AtomContainerSet();
        atomContainerSet.add(reaction.getProducts());
        atomContainerSet.add(reaction.getAgents());
        atomContainerSet.add(reaction.getAgents());

        // verify that atom types are not configured
        for (IAtomContainer atomContainer : atomContainerSet.atomContainers()) {
            for (IAtom atom : atomContainer.atoms()) {
                if (atom.equals(reactantOneAtomThree) || atom.equals(productOneAtomFour)) {
                    if (atom.getAtomTypeName() != CDKConstants.UNSET ||
                            atom.getMaxBondOrder() != CDKConstants.UNSET ||
                            atom.getBondOrderSum() != CDKConstants.UNSET ||
                            atom.getValency() != CDKConstants.UNSET ||
                            atom.getHybridization() != CDKConstants.UNSET
                    ) {
                        Assert.fail("The atom types should not be configured.");
                    }
                } else {
                    if (atom.getAtomTypeName() != CDKConstants.UNSET ||
                            atom.getMaxBondOrder() != CDKConstants.UNSET ||
                            atom.getBondOrderSum() != CDKConstants.UNSET ||
                            atom.getValency() != CDKConstants.UNSET ||
                            atom.getHybridization() != CDKConstants.UNSET ||
                            atom.getFormalNeighbourCount() != CDKConstants.UNSET
                    ) {
                        Assert.fail("The atom types should not be configured.");
                    }
                }
            }
        }

        // act
        ReactionManipulator.perceiveAtomTypesAndConfigureUnsetProperties(reaction);

        // assert that atom types are now configured
        for (IAtomContainer atomContainer : atomContainerSet.atomContainers()) {
            for (IAtom atom : atomContainer.atoms()) {

                // assert that the atom property of the two atoms that has been pre-configured hasn't been changed
                if (atom.equals(reactantOneAtomThree) || atom.equals(productOneAtomFour)) {
                    if (atom.getFormalNeighbourCount() != 2) {
                        Assert.fail("An already configured atom property should not have been modified.");
                    }
                }

                if (atom.getAtomTypeName() == CDKConstants.UNSET ||
                        atom.getMaxBondOrder() == CDKConstants.UNSET ||
                        atom.getBondOrderSum() == CDKConstants.UNSET ||
                        atom.getValency() == CDKConstants.UNSET ||
                        atom.getHybridization() == CDKConstants.UNSET ||
                        atom.getFormalNeighbourCount() == CDKConstants.UNSET
                ) {
                    Assert.fail("The atom types should be configured after calling the method ReactionManipulator.perceiveAtomTypesAndConfigureAtoms(IReaction).");
                }
            }
        }
    }

    @Test(expected = Test.None.class)
    public void clearAtomConfigurationsReactionNullTest() throws CDKException {
        ReactionManipulator.perceiveAtomTypesAndConfigureAtoms(null);
    }

    @Test
    public void clearAtomConfigurationsSimpleReactionTest() throws CDKException {
        // arrange
        // reactant one: CC=C
        IAtom reactantOneAtomOne = new Atom("C");
        IAtom reactantOneAtomTwo = new Atom("C");
        IAtom reactantOneAtomThree = new Atom("C");
        IBond reactantOneBondOne = new Bond(reactantOneAtomOne, reactantOneAtomTwo, Order.SINGLE);
        IBond reactantOneBondTwo = new Bond(reactantOneAtomTwo, reactantOneAtomThree, Order.DOUBLE);
        IAtomContainer reactantOne = new AtomContainer();
        reactantOne.addAtom(reactantOneAtomOne);
        reactantOne.addAtom(reactantOneAtomTwo);
        reactantOne.addAtom(reactantOneAtomThree);
        reactantOne.addBond(reactantOneBondOne);
        reactantOne.addBond(reactantOneBondTwo);

        // reactant two: Br
        IAtom reactantTwoAtom1 = new Atom("Br");
        IAtomContainer reactantTwo = new AtomContainer();
        reactantTwo.addAtom(reactantTwoAtom1);

        // agent one: O
        IAtom agentOneAtomOne = new Atom("O");
        IAtomContainer agentOne = new AtomContainer();
        agentOne.addAtom(agentOneAtomOne);

        // product one: CC(Br)C
        IAtom productOneAtomOne = new Atom("C");
        IAtom productOneAtomTwo = new Atom("C");
        IAtom productOneAtomThree = new Atom("Br");
        IAtom productOneAtomFour = new Atom("C");
        IBond productOneBondOne = new Bond(productOneAtomOne, productOneAtomTwo, Order.SINGLE);
        IBond productOneBondTwo = new Bond(productOneAtomTwo, productOneAtomThree, Order.SINGLE);
        IBond productOneBondThree = new Bond(productOneAtomTwo, productOneAtomFour, Order.SINGLE);
        IAtomContainer productOne = new AtomContainer();
        productOne.addAtom(productOneAtomOne);
        productOne.addAtom(productOneAtomTwo);
        productOne.addAtom(productOneAtomThree);
        productOne.addAtom(productOneAtomFour);
        productOne.addBond(productOneBondOne);
        productOne.addBond(productOneBondTwo);
        productOne.addBond(productOneBondThree);

        // add reactant, agent and product to the reaction
        IReaction reaction = new Reaction();
        reaction.addReactant(reactantOne);
        reaction.addReactant(reactantTwo);
        reaction.addAgent(agentOne);
        reaction.addProduct(productOne);

        // collect all IAtomContainers of the reaction in a single IAtomContainerSet
        IAtomContainerSet atomContainerSet = new AtomContainerSet();
        atomContainerSet.add(reaction.getProducts());
        atomContainerSet.add(reaction.getAgents());
        atomContainerSet.add(reaction.getAgents());

        // perceive atom types and configure the atoms of all reaction components
        ReactionManipulator.perceiveAtomTypesAndConfigureAtoms(reaction);

        // assert that atom types are now configured
        for (IAtomContainer atomContainer : atomContainerSet.atomContainers()) {
            for (IAtom atom : atomContainer.atoms()) {
                if (atom.getAtomTypeName() == CDKConstants.UNSET ||
                        atom.getMaxBondOrder() == CDKConstants.UNSET ||
                        atom.getBondOrderSum() == CDKConstants.UNSET ||
                        atom.getValency() == CDKConstants.UNSET ||
                        atom.getHybridization() == CDKConstants.UNSET ||
                        atom.getFormalNeighbourCount() == CDKConstants.UNSET
                ) {
                    Assert.fail("The atom types should be configured after calling the method ReactionManipulator.perceiveAtomTypesAndConfigureAtoms(IReaction).");
                }
            }
        }

        // act
        ReactionManipulator.clearAtomConfigurations(reaction);

        // verify that atom types were cleared
        for (IAtomContainer atomContainer : atomContainerSet.atomContainers()) {
            for (IAtom atom : atomContainer.atoms()) {
                if (atom.getAtomTypeName() != CDKConstants.UNSET ||
                        atom.getMaxBondOrder() != CDKConstants.UNSET ||
                        atom.getBondOrderSum() != CDKConstants.UNSET ||
                        atom.getValency() != CDKConstants.UNSET ||
                        atom.getHybridization() != CDKConstants.UNSET ||
                        atom.getFormalNeighbourCount() != CDKConstants.UNSET
                ) {
                    Assert.fail("The atom types should have been cleared.");
                }
            }
        }
    }

}
