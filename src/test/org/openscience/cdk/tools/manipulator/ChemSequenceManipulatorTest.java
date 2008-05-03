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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.*;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.tools.manipulator.ChemSequenceManipulator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @cdk.module test-standard
 */
public class ChemSequenceManipulatorTest extends NewCDKTestCase {
    
	IMolecule molecule1 = null;
	IMolecule molecule2 = null;
	IAtom atomInMol1 = null;
	IBond bondInMol1 = null;
	IAtom atomInMol2 = null;
	IMoleculeSet moleculeSet = null;
	IReaction reaction = null;
	IReactionSet reactionSet = null;
	IChemModel chemModel1 = null;
	IChemModel chemModel2 = null;
	IChemSequence chemSequence = null;

	
    public ChemSequenceManipulatorTest() {
        super();
    }

    @Before
    public void setUp() {
		molecule1 = new Molecule();
		atomInMol1 = new Atom("Cl");
		molecule1.addAtom(atomInMol1);
		molecule1.addAtom(new Atom("Cl"));
		bondInMol1 = new Bond(atomInMol1, molecule1.getAtom(1));
		molecule1.addBond(bondInMol1);
		molecule2 = new Molecule();
		atomInMol2 = new Atom("O");
		atomInMol2.setHydrogenCount(2);
		molecule2.addAtom(atomInMol2);
		moleculeSet = new MoleculeSet();
		moleculeSet.addAtomContainer(molecule1);
		moleculeSet.addAtomContainer(molecule2);
		reaction = new Reaction();
		reaction.addReactant(molecule1);
		reaction.addProduct(molecule2);
		reactionSet = new ReactionSet();
		reactionSet.addReaction(reaction);
		chemModel1 = new ChemModel();
		chemModel1.setMoleculeSet(moleculeSet);
		chemModel2 = new ChemModel();
		chemModel2.setReactionSet(reactionSet);
		chemSequence = new ChemSequence();
		chemSequence.addChemModel(chemModel1);
		chemSequence.addChemModel(chemModel2);
	}

	@Test
    public void testGetAtomCount_IChemSequence() {
		int count = ChemSequenceManipulator.getAtomCount(chemSequence);
    	Assert.assertEquals(6, count);
	}
	
	@Test public void testGetBondCount_IChemSequence()
	{
		int count = ChemSequenceManipulator.getBondCount(chemSequence);
    	Assert.assertEquals(2, count);
	}
	
	@Test public void testGetAllAtomContainers_IChemSequence()
	{
		List list = ChemSequenceManipulator.getAllAtomContainers(chemSequence);
		Assert.assertEquals(4, list.size());
	}
	
	@Test public void testGetAllChemObjects_IChemSequence()
	{
		List list = ChemSequenceManipulator.getAllChemObjects(chemSequence);
    	int molCount = 0;
    	int molSetCount = 0;
    	int reactionCount = 0;
    	int reactionSetCount = 0;
    	int chemModelCount = 0;
        for (Object o : list) {
            //if (o instanceof IAtom) ++atomCount;
            //if (o instanceof IBond) ++bondCount;
            if (o instanceof IMolecule) ++molCount;
            else if (o instanceof IMoleculeSet) ++molSetCount;
            else if (o instanceof IReaction) ++reactionCount;
            else if (o instanceof IReactionSet) ++reactionSetCount;
            else if (o instanceof IChemModel) ++chemModelCount;
            else Assert.fail("Unexpected Object of type " + o.getClass());
        }
    	//Assert.assertEquals(3, atomCount);
    	//Assert.assertEquals(1, bondCount);
    	Assert.assertEquals(2, molCount);
    	Assert.assertEquals(1, molSetCount);
    	Assert.assertEquals(1, reactionCount);
    	Assert.assertEquals(1, reactionSetCount);
    	Assert.assertEquals(2, chemModelCount);
	}

    @Test
    public void testGetAllIDs_IChemSequence() {
        Assert.assertEquals(0, ChemSequenceManipulator.getAllIDs(chemSequence).size());
        IDCreator.createIDs(chemSequence);
        List allIDs = ChemSequenceManipulator.getAllIDs(chemSequence);
        Assert.assertEquals(18, ChemSequenceManipulator.getAllIDs(chemSequence).size());
        Set uniq = new HashSet(allIDs);
        Assert.assertEquals(12, uniq.size());
    }
	
	
}


