/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.io.MDLRXNReader;

/**
 * @cdk.module test-standard
 */
public class ReactionSetManipulatorTest extends NewCDKTestCase {
    
    private IChemObjectBuilder builder;
    private ReactionSet set;
    
    public ReactionSetManipulatorTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
       	builder = DefaultChemObjectBuilder.getInstance();
		String filename1 = "data/mdl/reaction-1.rxn";
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename1);
        MDLRXNReader reader1 = new MDLRXNReader(ins1);
        set = (ReactionSet)reader1.read(new ReactionSet());
        reader1.close();
    }


    @Test public void testGetAllMolecules_IReactionSet() {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		
		Assert.assertEquals(0, ReactionSetManipulator.getAllMolecules(reactionSet).getMoleculeCount());
		
	}
    
    @Test public void testGetAllMolecules_IReactionSet2() {
		IReactionSet reactionSet = builder.newReactionSet();
		IReaction reaction1 = builder.newReaction();
		IMolecule molecule = builder.newMolecule();
		reaction1.addProduct(molecule);
		reaction1.addReactant(builder.newMolecule());
		reactionSet.addReaction(reaction1);
		IReaction reaction2 = builder.newReaction();
		reaction2.addProduct(builder.newMolecule());
		reaction2.addReactant(molecule);
		reactionSet.addReaction(reaction2);
		
		Assert.assertEquals(3, ReactionSetManipulator.getAllMolecules(reactionSet).getMoleculeCount());
		
	}
	
    @Test
    public void testGetAtomCount_IReactionSet() throws Exception {
        Assert.assertEquals(19, ReactionSetManipulator.getAtomCount(set));
	}
	
	@Test public void testGetBondCount_IReactionSet() throws Exception {
        Assert.assertEquals(18, ReactionSetManipulator.getBondCount(set));
	}
	
	@Test public void testGetAllAtomContainers_IReactionSet() throws Exception {
		Assert.assertEquals(3, ReactionSetManipulator.getAllAtomContainers(set).size());
	}
	
	@Test public void testGetRelevantReaction_IReactionSet_IAtom() {
		Iterator<IAtomContainer> atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
		while (atomContainers.hasNext()) {
			IAtomContainer container = atomContainers.next();
			IAtom anAtom = container.getAtom(0);
			Assert.assertEquals(
				set.getReaction(0), 
				ReactionSetManipulator.getRelevantReaction(set, anAtom)
			);
		}
	}
	
	@Test public void testGetRelevantReaction_IReactionSet_IBond() {
		Iterator<IAtomContainer> atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
		while (atomContainers.hasNext()) {
			IAtomContainer container = atomContainers.next();
			IBond aBond = container.getBond(0);
			Assert.assertEquals(
				set.getReaction(0), 
				ReactionSetManipulator.getRelevantReaction(set, aBond)
			);
		}
	}

	@Test public void testGetRelevantAtomContainer_IReactionSet_IAtom() {
		Iterator<IAtomContainer> atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
		while (atomContainers.hasNext()) {
			IAtomContainer container = atomContainers.next();
			IAtom anAtom = container.getAtom(0);
			Assert.assertEquals(
				container, 
				ReactionSetManipulator.getRelevantAtomContainer(set, anAtom)
			);
		}
	}
	
	@Test public void testGetRelevantAtomContainer_IReactionSet_IBond() {
		Iterator<IAtomContainer> atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
		while (atomContainers.hasNext()) {
			IAtomContainer container = atomContainers.next();
			IBond aBond = container.getBond(0);
			Assert.assertEquals(
				container, 
				ReactionSetManipulator.getRelevantAtomContainer(set, aBond)
			);
		}
	}
	
	@Test public void testSetAtomProperties_IReactionSet_Object_Object() throws Exception {
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

	@Test public void testGetAllChemObjects_IReactionSet() {
		List<IChemObject> allObjects = ReactionSetManipulator.getAllChemObjects(set);
		// does not recurse beyond the IAtomContainer, so:
		// set, reaction, 2xreactant, 1xproduct
		Assert.assertEquals(5, allObjects.size());
	}

	@Test public void testRemoveElectronContainer_IReactionSet_IElectronContainer() {
		IReactionSet set = builder.newReactionSet();
		IReaction reaction = builder.newReaction();
		set.addReaction(reaction);
		IMolecule mol = builder.newMolecule();
		mol.addAtom(builder.newAtom("C"));
		mol.addAtom(builder.newAtom("C"));
		mol.addBond(0, 1, Order.SINGLE);
		Assert.assertEquals(2,mol.getAtomCount());
		Assert.assertEquals(1,mol.getBondCount());
		reaction.addReactant(mol);
		reaction.addReactant(builder.newMolecule());
		reaction.addReactant(builder.newMolecule());
		reaction.addProduct(builder.newMolecule());
		reaction.addProduct(builder.newMolecule());
		ReactionSetManipulator.removeElectronContainer(set, mol.getBond(0));

		Assert.assertEquals(2,mol.getAtomCount());
		Assert.assertEquals(0,mol.getBondCount());
		
	}
	
	@Test public void testRemoveAtomAndConnectedElectronContainers_IReactionSet_IAtom() {
		IReactionSet set = builder.newReactionSet();
		IReaction reaction = builder.newReaction();
		set.addReaction(reaction);
		IMolecule mol = builder.newMolecule();
		mol.addAtom(builder.newAtom("C"));
		mol.addAtom(builder.newAtom("C"));
		mol.addBond(0, 1, Order.SINGLE);
		Assert.assertEquals(2,mol.getAtomCount());
		Assert.assertEquals(1,mol.getBondCount());
		reaction.addReactant(mol);
		reaction.addReactant(builder.newMolecule());
		reaction.addReactant(builder.newMolecule());
		reaction.addProduct(builder.newMolecule());
		reaction.addProduct(builder.newMolecule());
		ReactionSetManipulator.removeAtomAndConnectedElectronContainers(set,mol.getAtom(0));

		Assert.assertEquals(1,mol.getAtomCount());
		Assert.assertEquals(0,mol.getBondCount());
	}

    
    @Test public void testGetAllIDs_IReactionSet() {
    	IReactionSet set = builder.newReactionSet();
		IReaction reaction1 = builder.newReaction();
		set.addReaction(reaction1);
		reaction1.setID("r1");
        Molecule water = new Molecule();
        water.setID("m1");
        Atom oxygen = new Atom("O");
        oxygen.setID("a1");
        water.addAtom(oxygen);
        reaction1.addReactant(water);
        reaction1.addProduct(water);
        IReaction reaction2 = builder.newReaction();
		reaction2.setID("r2");
		set.addReaction(reaction2);
		
        List<String> ids = ReactionSetManipulator.getAllIDs(set);
        Assert.assertNotNull(ids);
        Assert.assertEquals(6, ids.size());
    }
}


