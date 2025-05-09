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
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.test.CDKTestCase;
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
 */
class ReactionSetManipulatorTest extends CDKTestCase {

    private IChemObjectBuilder builder;
    private ReactionSet        set;

    ReactionSetManipulatorTest() {
        super();
    }

    @BeforeEach
    void setUp() throws Exception {
        builder = DefaultChemObjectBuilder.getInstance();
        String filename1 = "reaction-1.rxn";
        InputStream ins1 = this.getClass().getResourceAsStream(filename1);
        MDLRXNReader reader1 = new MDLRXNReader(ins1);
        set = reader1.read(new ReactionSet());
        reader1.close();
    }

    @Test
    void testGetAllMolecules_IReactionSet() {
        IReactionSet reactionSet = builder.newInstance(IReactionSet.class);
        reactionSet.addReaction(builder.newInstance(IReaction.class)); // 1
        reactionSet.addReaction(builder.newInstance(IReaction.class)); // 2

        Assertions.assertEquals(0, ReactionSetManipulator.getAllMolecules(reactionSet).getAtomContainerCount());

    }

    @Test
    void testGetAllMolecules_IReactionSet2() {
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

        Assertions.assertEquals(3, ReactionSetManipulator.getAllMolecules(reactionSet).getAtomContainerCount());

    }

    @Test
    void testGetAtomCount_IReactionSet() throws Exception {
        Assertions.assertEquals(19, ReactionSetManipulator.getAtomCount(set));
    }

    @Test
    void testGetBondCount_IReactionSet() throws Exception {
        Assertions.assertEquals(18, ReactionSetManipulator.getBondCount(set));
    }

    @Test
    void testGetAllAtomContainers_IReactionSet() throws Exception {
        Assertions.assertEquals(3, ReactionSetManipulator.getAllAtomContainers(set).size());
    }

    @Test
    void testGetRelevantReaction_IReactionSet_IAtom() {
        for (IAtomContainer container : ReactionSetManipulator.getAllAtomContainers(set)) {
            IAtom anAtom = container.getAtom(0);
            Assertions.assertEquals(set.getReaction(0), ReactionSetManipulator.getRelevantReaction(set, anAtom));
        }
    }

    @Test
    void testGetRelevantReaction_IReactionSet_IBond() {
        for (IAtomContainer container : ReactionSetManipulator.getAllAtomContainers(set)) {
            IBond aBond = container.getBond(0);
            Assertions.assertEquals(set.getReaction(0), ReactionSetManipulator.getRelevantReaction(set, aBond));
        }
    }

    @Test
    void testGetRelevantAtomContainer_IReactionSet_IAtom() {
        for (IAtomContainer container : ReactionSetManipulator.getAllAtomContainers(set)) {
            IAtom anAtom = container.getAtom(0);
            Assertions.assertEquals(container, ReactionSetManipulator.getRelevantAtomContainer(set, anAtom));
        }
    }

    @Test
    void testGetRelevantAtomContainer_IReactionSet_IBond() {
        for (IAtomContainer container : ReactionSetManipulator.getAllAtomContainers(set)) {
            IBond aBond = container.getBond(0);
            Assertions.assertEquals(container, ReactionSetManipulator.getRelevantAtomContainer(set, aBond));
        }
    }

    @Test
    void testSetAtomProperties_IReactionSet_Object_Object() throws Exception {
        ReactionSetManipulator.setAtomProperties(set, "test", "ok");
        for (IAtomContainer container : ReactionSetManipulator.getAllAtomContainers(set)) {
            for (IAtom atom : container.atoms()) {
                Assertions.assertNotNull(atom.getProperty("test"));
                Assertions.assertEquals("ok", atom.getProperty("test"));
            }
        }
        // reset things
        setUp();
    }

    @Test
    void testGetAllChemObjects_IReactionSet() {
        List<IChemObject> allObjects = ReactionSetManipulator.getAllChemObjects(set);
        // does not recurse beyond the IAtomContainer, so:
        // set, reaction, 2xreactant, 1xproduct
        Assertions.assertEquals(5, allObjects.size());
    }

    @Test
    void testRemoveElectronContainer_IReactionSet_IElectronContainer() {
        IReactionSet set = builder.newInstance(IReactionSet.class);
        IReaction reaction = builder.newInstance(IReaction.class);
        set.addReaction(reaction);
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, Order.SINGLE);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        reaction.addReactant(mol);
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        ReactionSetManipulator.removeElectronContainer(set, mol.getBond(0));

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(0, mol.getBondCount());

    }

    @Test
    void testRemoveAtomAndConnectedElectronContainers_IReactionSet_IAtom() {
        IReactionSet set = builder.newInstance(IReactionSet.class);
        IReaction reaction = builder.newInstance(IReaction.class);
        set.addReaction(reaction);
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addAtom(builder.newInstance(IAtom.class, "C"));
        mol.addBond(0, 1, Order.SINGLE);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        reaction.addReactant(mol);
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addReactant(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        reaction.addProduct(builder.newInstance(IAtomContainer.class));
        ReactionSetManipulator.removeAtomAndConnectedElectronContainers(set, mol.getAtom(0));

        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertEquals(0, mol.getBondCount());
    }

    @Test
    void testGetAllIDs_IReactionSet() {
        IReactionSet set = builder.newInstance(IReactionSet.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        set.addReaction(reaction1);
        reaction1.setID("r1");
        IAtomContainer water = DefaultChemObjectBuilder.getInstance().newAtomContainer();
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
        Assertions.assertNotNull(ids);
        Assertions.assertEquals(6, ids.size());
    }

    @Test
    void testGetRelevantReactions_IReactionSet_IAtomContainer() {
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

        Assertions.assertEquals(3, set.getReactionCount());
        IReactionSet reactionSet2 = ReactionSetManipulator.getRelevantReactions(set, mol1b);
        Assertions.assertEquals(2, reactionSet2.getReactionCount());
        Assertions.assertEquals(reaction1, reactionSet2.getReaction(0));
        Assertions.assertEquals(reaction2, reactionSet2.getReaction(1));
        IReactionSet reactionSet1 = ReactionSetManipulator.getRelevantReactions(set, mol1a);
        Assertions.assertEquals(1, reactionSet1.getReactionCount());
        Assertions.assertEquals(reaction1, reactionSet1.getReaction(0));

    }

    @Test
    void testGetRelevantReactionsAsReactant_IReactionSet_IAtomContainer() {
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

        Assertions.assertEquals(3, set.getReactionCount());
        IReactionSet reactionSet2 = ReactionSetManipulator.getRelevantReactionsAsReactant(set, mol1b);
        Assertions.assertEquals(2, reactionSet2.getReactionCount());
        Assertions.assertEquals(reaction1, reactionSet2.getReaction(0));
        Assertions.assertEquals(reaction2, reactionSet2.getReaction(1));
        IReactionSet reactionSet1 = ReactionSetManipulator.getRelevantReactionsAsReactant(set, mol1a);
        Assertions.assertEquals(1, reactionSet1.getReactionCount());
        Assertions.assertEquals(reaction1, reactionSet1.getReaction(0));

    }

    @Test
    void testGetRelevantReactionsAsProduct_IReactionSet_IAtomContainer() {
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

        Assertions.assertEquals(3, set.getReactionCount());
        IReactionSet reactionSet2 = ReactionSetManipulator.getRelevantReactionsAsProduct(set, mol1b);
        Assertions.assertEquals(0, reactionSet2.getReactionCount());
        IReactionSet reactionSet1 = ReactionSetManipulator.getRelevantReactionsAsProduct(set, mol1a);
        Assertions.assertEquals(1, reactionSet1.getReactionCount());
        Assertions.assertEquals(reaction3, reactionSet1.getReaction(0));

    }

    @Test
    void testGetReactionByReactionID_IReactionSet_String() {
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
        Assertions.assertEquals(reaction1, ReactionSetManipulator.getReactionByReactionID(set, "1"));
        Assertions.assertNull(ReactionSetManipulator.getReactionByAtomContainerID(set, "4"));
    }

    @Test
    void testGetReactionByAtomContainerID_IReactionSet_String() {
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
        Assertions.assertEquals(reaction1, ReactionSetManipulator.getReactionByAtomContainerID(set, "1"));
        Assertions.assertEquals(reaction2, ReactionSetManipulator.getReactionByAtomContainerID(set, "2"));
        Assertions.assertNull(ReactionSetManipulator.getReactionByAtomContainerID(set, "3"));
    }
}
