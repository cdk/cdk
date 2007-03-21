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
package org.openscience.cdk.test.tools.manipulator;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.MDLRXNReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;

/**
 * @cdk.module test-standard
 */
public class ReactionSetManipulatorTest extends CDKTestCase {
    
    private IChemObjectBuilder builder;
    private ReactionSet set;
    
    public ReactionSetManipulatorTest(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
       	builder = DefaultChemObjectBuilder.getInstance();
		String filename1 = "data/mdl/reaction-1.rxn";
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename1);
        MDLRXNReader reader1 = new MDLRXNReader(ins1);
        set = (ReactionSet)reader1.read(new ReactionSet());
        reader1.close();
    }

    public static Test suite() {
		return new TestSuite(ReactionSetManipulatorTest.class);
	}

	public void testGetAllMolecules_IReactionSet() {
		IReactionSet reactionSet = builder.newReactionSet();
		reactionSet.addReaction(builder.newReaction()); // 1
		reactionSet.addReaction(builder.newReaction()); // 2
		assertEquals(0, ReactionSetManipulator.getAllMolecules(reactionSet).getMoleculeCount());
		// FIXME: should test something with actual molecules in it
	}
	
	public void testGetAtomCount_IReactionSet() throws Exception {
        assertEquals(19, ReactionSetManipulator.getAtomCount(set));
	}
	
	public void testGetBondCount_IReactionSet() throws Exception {
        assertEquals(18, ReactionSetManipulator.getBondCount(set));
	}
	
	public void testGetAllAtomContainers_IReactionSet() throws Exception {
		assertEquals(3, ReactionSetManipulator.getAllAtomContainers(set).size());
	}
	
	public void testGetRelevantReaction_IReactionSet_IAtom() {
		Iterator atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
		while (atomContainers.hasNext()) {
			IAtomContainer container = (IAtomContainer)atomContainers.next();
			IAtom anAtom = container.getAtom(0);
			assertEquals(
				set.getReaction(0), 
				ReactionSetManipulator.getRelevantReaction(set, anAtom)
			);
		}
	}
	
	public void testGetRelevantReaction_IReactionSet_IBond() {
		Iterator atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
		while (atomContainers.hasNext()) {
			IAtomContainer container = (IAtomContainer)atomContainers.next();
			IBond aBond = container.getBond(0);
			assertEquals(
				set.getReaction(0), 
				ReactionSetManipulator.getRelevantReaction(set, aBond)
			);
		}
	}

	public void testGetRelevantAtomContainer_IReactionSet_IAtom() {
		Iterator atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
		while (atomContainers.hasNext()) {
			IAtomContainer container = (IAtomContainer)atomContainers.next();
			IAtom anAtom = container.getAtom(0);
			assertEquals(
				container, 
				ReactionSetManipulator.getRelevantAtomContainer(set, anAtom)
			);
		}
	}
	
	public void testGetRelevantAtomContainer_IReactionSet_IBond() {
		Iterator atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
		while (atomContainers.hasNext()) {
			IAtomContainer container = (IAtomContainer)atomContainers.next();
			IBond aBond = container.getBond(0);
			assertEquals(
				container, 
				ReactionSetManipulator.getRelevantAtomContainer(set, aBond)
			);
		}
	}
	
	public void testSetAtomProperties_IReactionSet_Object_Object() throws Exception {
		ReactionSetManipulator.setAtomProperties(set, "test", "ok");
		Iterator atomContainers = ReactionSetManipulator.getAllAtomContainers(set).iterator();
		while (atomContainers.hasNext()) {
			IAtomContainer container = (IAtomContainer)atomContainers.next();
			Iterator atoms = container.atoms();
			while (atoms.hasNext()) {
				IAtom atom = (IAtom)atoms.next();
				assertNotNull(atom.getProperty("test"));
				assertEquals("ok", atom.getProperty("test"));
			}
		}
		// reset things
		setUp();
	}

	public void testGetAllChemObjects_IReactionSet() {
		List allObjects = ReactionSetManipulator.getAllChemObjects(set);
		// does not recurse beyond the IAtomContainer, so:
		// set, reaction, 2xreactant, 1xproduct
		assertEquals(5, allObjects.size());
	}
}


