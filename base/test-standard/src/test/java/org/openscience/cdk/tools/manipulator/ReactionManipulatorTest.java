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
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.MDLRXNReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module test-standard
 *
 * @author     Egon Willighagen
 * @cdk.created    2003-07-23
 */
public class ReactionManipulatorTest extends CDKTestCase {

    private IReaction          reaction;
    private IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

    public ReactionManipulatorTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        String filename1 = "data/mdl/reaction-1.rxn";
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename1);
        MDLRXNReader reader1 = new MDLRXNReader(ins1);
        ReactionSet set = (ReactionSet) reader1.read(new ReactionSet());
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
     *
     * @return    The test suite
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
     *
     * @return    The test suite
     */
    @Test
    public void testGetMappedChemObject_IReaction_IBond() throws ClassNotFoundException, CDKException,
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
        Iterator<IAtomContainer> atomContainers = ReactionManipulator.getAllAtomContainers(reaction).iterator();
        while (atomContainers.hasNext()) {
            IAtomContainer container = atomContainers.next();
            Iterator<IAtom> atoms = container.atoms().iterator();
            while (atoms.hasNext()) {
                IAtom atom = atoms.next();
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
        Iterator<IAtomContainer> atomContainers = ReactionManipulator.getAllAtomContainers(reaction).iterator();
        while (atomContainers.hasNext()) {
            IAtomContainer container = atomContainers.next();
            IAtom anAtom = container.getAtom(0);
            Assert.assertEquals(container, ReactionManipulator.getRelevantAtomContainer(reaction, anAtom));
        }
    }

    @Test
    public void testGetRelevantAtomContainer_IReaction_IBond() {
        Iterator<IAtomContainer> atomContainers = ReactionManipulator.getAllAtomContainers(reaction).iterator();
        while (atomContainers.hasNext()) {
            IAtomContainer container = atomContainers.next();
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

    @Test public void inliningReactions() throws CDKException {
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

    @Test public void inliningReactionsWithRadicals() throws CDKException {
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

}
