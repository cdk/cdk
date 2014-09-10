/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.MDLRXNReader;

/**
 * @cdk.module test-standard
 */
public class ReactionSetManipulatorTest extends CDKTestCase {

    private IChemObjectBuilder builder;
    private ReactionSet        set;

    public ReactionSetManipulatorTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        builder = DefaultChemObjectBuilder.getInstance();
        String filename1 = "data/mdl/reaction-1.rxn";
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename1);
        MDLRXNReader reader1 = new MDLRXNReader(ins1);
        set = (ReactionSet) reader1.read(new ReactionSet());
        reader1.close();
    }

    @Test
    public void testGetAllMolecules_IReactionSet() {
        IReactionSet reactionSet = builder.newInstance(IReactionSet.class);
        reactionSet.addReaction(builder.newInstance(IReaction.class)); // 1
        reactionSet.addReaction(builder.newInstance(IReaction.class)); // 2

        Assert.assertEquals(0, ReactionSetManipulator.getAllMolecules(reactionSet).getAtomContainerCount());

    }

    @Test
    public void testGetAllMolecules_IReactionSet2() {
        IReactionSet reactionSet = builder.newInstance(IReactionSet.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        reaction1.addProduct(molecule);
        reaction1.addReactant(builder.newInstance(IAtomContainer.class));
        reactionSet.addReaction(reaction1);
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.addProduct(builder.newInstance(IAtomContainer.class));
        reaction2.addReactant(molecule);
        reactionSet.addReaction(reaction2);

        Assert.assertEquals(3, ReactionSetManipulator.getAllMolecules(reactionSet).getAtomContainerCount());

    }

    @Test
    public void testGetAtomCount_IReactionSet() throws Exception {
        Assert.assertEquals(19, ReactionSetManipulator.getAtomCount(set));
    }

    @Test
    public void testGetBondCount_IReactionSet() throws Exception {
        Assert.assertEquals(18, ReactionSetManipulator.getBondCount(set));
    }

    @Test
    public void testGetAllAtomContainers_IReactionSet() throws Exception {
        Assert.assertEquals(3, ReactionSetManipulator.getAllAtomContainers(set).size());
    }

    @Test
    public void testGetRelevantReaction_IReactionSet_IAtom() {
        Iterator<IAtomContainer> atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
        while (atomContainers.hasNext()) {
            IAtomContainer container = atomContainers.next();
            IAtom anAtom = container.getAtom(0);
            Assert.assertEquals(set.getReaction(0), ReactionSetManipulator.getRelevantReaction(set, anAtom));
        }
    }

    @Test
    public void testGetRelevantReaction_IReactionSet_IBond() {
        Iterator<IAtomContainer> atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
        while (atomContainers.hasNext()) {
            IAtomContainer container = atomContainers.next();
            IBond aBond = container.getBond(0);
            Assert.assertEquals(set.getReaction(0), ReactionSetManipulator.getRelevantReaction(set, aBond));
        }
    }

    @Test
    public void testGetRelevantAtomContainer_IReactionSet_IAtom() {
        Iterator<IAtomContainer> atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
        while (atomContainers.hasNext()) {
            IAtomContainer container = atomContainers.next();
            IAtom anAtom = container.getAtom(0);
            Assert.assertEquals(container, ReactionSetManipulator.getRelevantAtomContainer(set, anAtom));
        }
    }

    @Test
    public void testGetRelevantAtomContainer_IReactionSet_IBond() {
        Iterator<IAtomContainer> atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
        while (atomContainers.hasNext()) {
            IAtomContainer container = atomContainers.next();
            IBond aBond = container.getBond(0);
            Assert.assertEquals(container, ReactionSetManipulator.getRelevantAtomContainer(set, aBond));
        }
    }

    @Test
    public void testSetAtomProperties_IReactionSet_Object_Object() throws Exception {
        ReactionSetManipulator.setAtomProperties(set, "test", "ok");
        Iterator<IAtomContainer> atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
        while (atomContainers.hasNext()) {
            IAtomContainer container = atomContainers.next();
            Iterator<IAtom> atoms = container.atoms().iterator();
            while (atoms.hasNext()) {
                IAtom atom = atoms.next();
                Assert.assertNotNull(atom.getProperty("test"));
                Assert.assertEquals("ok", atom.getProperty("test"));
            }
        }
        // reset things
        setUp();
    }

    @Test
    public void testGetAllChemObjects_IReactionSet() {
        List<IChemObject> allObjects = ReactionSetManipulator.getAllChemObjects(set);
        // does not recurse beyond the IAtomContainer, so:
        // set, reaction, 2xreactant, 1xproduct
        Assert.assertEquals(5, allObjects.size());
    }

    @Test
    public void testRemoveElectronContainer_IReactionSet_IElectronContainer() {
        IReactionSet set = builder.newInstance(IReactionSet.class);
        IReaction reaction = builder.newInstance(IReaction.class);
        set.addReaction(reaction);
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
        ReactionSetManipulator.removeElectronContainer(set, mol.getBond(0));

        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertEquals(0, mol.getBondCount());

    }

    @Test
    public void testRemoveAtomAndConnectedElectronContainers_IReactionSet_IAtom() {
        IReactionSet set = builder.newInstance(IReactionSet.class);
        IReaction reaction = builder.newInstance(IReaction.class);
        set.addReaction(reaction);
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
        ReactionSetManipulator.removeAtomAndConnectedElectronContainers(set, mol.getAtom(0));

        Assert.assertEquals(1, mol.getAtomCount());
        Assert.assertEquals(0, mol.getBondCount());
    }

    @Test
    public void testGetAllIDs_IReactionSet() {
        IReactionSet set = builder.newInstance(IReactionSet.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        set.addReaction(reaction1);
        reaction1.setID("r1");
        IAtomContainer water = new AtomContainer();
        water.setID("m1");
        Atom oxygen = new Atom("O");
        oxygen.setID("a1");
        water.addAtom(oxygen);
        reaction1.addReactant(water);
        reaction1.addProduct(water);
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.setID("r2");
        set.addReaction(reaction2);

        List<String> ids = ReactionSetManipulator.getAllIDs(set);
        Assert.assertNotNull(ids);
        Assert.assertEquals(6, ids.size());
    }

    @Test
    public void testGetRelevantReactions_IReactionSet_IAtomContainer() {
        IReactionSet set = builder.newInstance(IReactionSet.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        set.addReaction(reaction1);
        IAtomContainer mol1a = builder.newInstance(IAtomContainer.class);
        IAtomContainer mol1b = builder.newInstance(IAtomContainer.class);
        reaction1.addReactant(mol1a);
        reaction1.addReactant(mol1b);
        reaction1.addProduct(builder.newInstance(IAtomContainer.class));
        reaction1.addProduct(builder.newInstance(IAtomContainer.class));

        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.addReactant(mol1b);
        reaction2.addProduct(builder.newInstance(IAtomContainer.class));
        set.addReaction(reaction2);

        IReaction reaction3 = builder.newInstance(IReaction.class);
        reaction3.addReactant(builder.newInstance(IAtomContainer.class));
        reaction3.addProduct(builder.newInstance(IAtomContainer.class));
        set.addReaction(reaction3);

        Assert.assertEquals(3, set.getReactionCount());
        IReactionSet reactionSet2 = ReactionSetManipulator.getRelevantReactions(set, mol1b);
        Assert.assertEquals(2, reactionSet2.getReactionCount());
        Assert.assertEquals(reaction1, reactionSet2.getReaction(0));
        Assert.assertEquals(reaction2, reactionSet2.getReaction(1));
        IReactionSet reactionSet1 = ReactionSetManipulator.getRelevantReactions(set, mol1a);
        Assert.assertEquals(1, reactionSet1.getReactionCount());
        Assert.assertEquals(reaction1, reactionSet1.getReaction(0));

    }

    @Test
    public void testGetRelevantReactionsAsReactant_IReactionSet_IAtomContainer() {
        IReactionSet set = builder.newInstance(IReactionSet.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        set.addReaction(reaction1);
        IAtomContainer mol1a = builder.newInstance(IAtomContainer.class);
        IAtomContainer mol1b = builder.newInstance(IAtomContainer.class);
        reaction1.addReactant(mol1a);
        reaction1.addReactant(mol1b);
        reaction1.addProduct(builder.newInstance(IAtomContainer.class));
        reaction1.addProduct(builder.newInstance(IAtomContainer.class));

        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.addReactant(mol1b);
        reaction2.addProduct(builder.newInstance(IAtomContainer.class));
        set.addReaction(reaction2);

        IReaction reaction3 = builder.newInstance(IReaction.class);
        reaction3.addReactant(builder.newInstance(IAtomContainer.class));
        reaction3.addProduct(builder.newInstance(IAtomContainer.class));
        set.addReaction(reaction3);

        Assert.assertEquals(3, set.getReactionCount());
        IReactionSet reactionSet2 = ReactionSetManipulator.getRelevantReactionsAsReactant(set, mol1b);
        Assert.assertEquals(2, reactionSet2.getReactionCount());
        Assert.assertEquals(reaction1, reactionSet2.getReaction(0));
        Assert.assertEquals(reaction2, reactionSet2.getReaction(1));
        IReactionSet reactionSet1 = ReactionSetManipulator.getRelevantReactionsAsReactant(set, mol1a);
        Assert.assertEquals(1, reactionSet1.getReactionCount());
        Assert.assertEquals(reaction1, reactionSet1.getReaction(0));

    }

    @Test
    public void testGetRelevantReactionsAsProduct_IReactionSet_IAtomContainer() {
        IReactionSet set = builder.newInstance(IReactionSet.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        set.addReaction(reaction1);
        IAtomContainer mol1a = builder.newInstance(IAtomContainer.class);
        IAtomContainer mol1b = builder.newInstance(IAtomContainer.class);
        reaction1.addReactant(mol1a);
        reaction1.addReactant(mol1b);
        reaction1.addProduct(builder.newInstance(IAtomContainer.class));
        reaction1.addProduct(builder.newInstance(IAtomContainer.class));

        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.addReactant(mol1b);
        reaction2.addProduct(builder.newInstance(IAtomContainer.class));
        set.addReaction(reaction2);

        IReaction reaction3 = builder.newInstance(IReaction.class);
        reaction3.addReactant(builder.newInstance(IAtomContainer.class));
        reaction3.addProduct(mol1a);
        set.addReaction(reaction3);

        Assert.assertEquals(3, set.getReactionCount());
        IReactionSet reactionSet2 = ReactionSetManipulator.getRelevantReactionsAsProduct(set, mol1b);
        Assert.assertEquals(0, reactionSet2.getReactionCount());
        IReactionSet reactionSet1 = ReactionSetManipulator.getRelevantReactionsAsProduct(set, mol1a);
        Assert.assertEquals(1, reactionSet1.getReactionCount());
        Assert.assertEquals(reaction3, reactionSet1.getReaction(0));

    }

    @Test
    public void testGetReactionByReactionID_IReactionSet_String() {
        IReactionSet set = builder.newInstance(IReactionSet.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        reaction1.setID("1");
        set.addReaction(reaction1);
        IAtomContainer mol1a = builder.newInstance(IAtomContainer.class);
        IAtomContainer mol1b = builder.newInstance(IAtomContainer.class);
        reaction1.addReactant(mol1a);
        reaction1.addReactant(mol1b);
        reaction1.addProduct(builder.newInstance(IAtomContainer.class));
        reaction1.addProduct(builder.newInstance(IAtomContainer.class));

        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.setID("2");
        reaction2.addReactant(mol1b);
        reaction2.addProduct(builder.newInstance(IAtomContainer.class));
        set.addReaction(reaction2);

        IReaction reaction3 = builder.newInstance(IReaction.class);
        reaction3.setID("3");
        reaction3.addReactant(builder.newInstance(IAtomContainer.class));
        reaction3.addProduct(mol1a);
        set.addReaction(reaction3);
        Assert.assertEquals(reaction1, ReactionSetManipulator.getReactionByReactionID(set, "1"));
        Assert.assertNull(ReactionSetManipulator.getReactionByAtomContainerID(set, "4"));
    }

    @Test
    public void testGetReactionByAtomContainerID_IReactionSet_String() {
        IReactionSet set = builder.newInstance(IReactionSet.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        set.addReaction(reaction1);
        IAtomContainer mol1a = builder.newInstance(IAtomContainer.class);
        mol1a.setID("1");
        IAtomContainer mol1b = builder.newInstance(IAtomContainer.class);
        mol1b.setID("2");
        reaction1.addReactant(mol1a);
        reaction1.addReactant(builder.newInstance(IAtomContainer.class));
        reaction1.addProduct(builder.newInstance(IAtomContainer.class));
        reaction1.addProduct(builder.newInstance(IAtomContainer.class));

        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.addReactant(builder.newInstance(IAtomContainer.class));
        reaction2.addProduct(mol1b);
        set.addReaction(reaction2);

        IReaction reaction3 = builder.newInstance(IReaction.class);
        reaction3.addReactant(builder.newInstance(IAtomContainer.class));
        reaction3.addProduct(builder.newInstance(IAtomContainer.class));
        set.addReaction(reaction3);
        Assert.assertEquals(reaction1, ReactionSetManipulator.getReactionByAtomContainerID(set, "1"));
        Assert.assertEquals(reaction2, ReactionSetManipulator.getReactionByAtomContainerID(set, "2"));
        Assert.assertNull(ReactionSetManipulator.getReactionByAtomContainerID(set, "3"));
    }
}
