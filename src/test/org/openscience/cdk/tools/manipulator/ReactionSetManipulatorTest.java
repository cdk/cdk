/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.MDLRXNReader;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

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
			Iterator<IAtom> atoms = container.atoms();
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
}


