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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * @cdk.module test-standard
 */
public class ChemFileManipulatorTest extends CDKTestCase {
    
	private final static LoggingTool logger = new LoggingTool(ChemFileManipulatorTest.class);
	
	IMolecule molecule1 = null;
	IMolecule molecule2 = null;
	IAtom atomInMol1 = null;
	IBond bondInMol1 = null;
	IAtom atomInMol2 = null;
	IMoleculeSet moleculeSet = null;
	IReaction reaction = null;
	IReactionSet reactionSet = null;
	IChemModel chemModel = null;
	IChemSequence chemSequence1 = null;
	IChemSequence chemSequence2 = null;
	IChemFile chemFile = null;
	
    public ChemFileManipulatorTest(String name) {
        super(name);
    }
    
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
		chemModel = new ChemModel();
		chemModel.setMoleculeSet(moleculeSet);
		chemModel.setReactionSet(reactionSet);
		chemSequence1 = new ChemSequence();
		chemSequence1.addChemModel(chemModel);
		chemSequence2 = new ChemSequence();
		chemFile = new ChemFile();
		chemFile.addChemSequence(chemSequence1);
		chemFile.addChemSequence(chemSequence2);
	}
    
	public static Test suite() {
		return new TestSuite(ChemFileManipulatorTest.class);
	}

    public void testGetAllAtomContainers_IChemFile() throws Exception {
        String filename = "data/mdl/prev2000.sd";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        assertNotNull(chemFile);
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(2, containersList.size());
    }
    
    public void testGetAllIDs_IChemFile() {
    	assertEquals(0, ChemFileManipulator.getAllIDs(chemFile).size());
    	IDCreator.createIDs(chemFile);
    	List allIDs = ChemFileManipulator.getAllIDs(chemFile);
    	assertEquals(19, ChemFileManipulator.getAllIDs(chemFile).size());
    	Set uniq = new HashSet(allIDs);
    	assertEquals(13, uniq.size());
    }
    
    public void testGetAtomCount_IChemFile()
    {
    	int count = ChemFileManipulator.getAtomCount(chemFile);
    	assertEquals(6, count);
    }
    
    public void testGetBondCount_IChemFile()
    {
    	int count = ChemFileManipulator.getBondCount(chemFile);
    	assertEquals(2, count);
    }
    
    public void testGetAllChemObjects_IChemFile()
    {
    	List list = ChemFileManipulator.getAllChemObjects(chemFile);
    	assertEquals(8, list.size()); // not the file itself
    	int atomCount = 0;
    	int bondCount = 0;
    	int molCount = 0;
    	int molSetCount = 0;
    	int reactionCount = 0;
    	int reactionSetCount = 0;
    	int chemModelCount = 0;
    	int chemSequenceCount = 0;
    	for (Iterator iter = list.iterator(); iter.hasNext();) {
    		Object o = iter.next();
    		if (o instanceof IAtom) ++atomCount;
    		if (o instanceof IBond) ++bondCount;
    		if (o instanceof IMolecule) ++molCount;
    		else if (o instanceof IMoleculeSet) ++molSetCount;
    		else if (o instanceof IReaction) ++reactionCount;
    		else if (o instanceof IReactionSet) ++reactionSetCount;
    		else if (o instanceof IChemModel) ++chemModelCount;
    		else if (o instanceof IChemSequence) ++chemSequenceCount;
    		else fail("Unexpected Object of type " + o.getClass());
    	}
    	assertEquals(0, atomCount); /// it does not recurse into IAtomContainer
    	assertEquals(0, bondCount);
    	assertEquals(2, molCount);
    	assertEquals(1, molSetCount);
    	assertEquals(1, reactionCount);
    	assertEquals(1, reactionSetCount);
    	assertEquals(1, chemModelCount);
    	assertEquals(2, chemSequenceCount);
    }
    
    public void testGetAllChemModels_IChemFile()
    {
    	List list = ChemFileManipulator.getAllChemModels(chemFile);
    	assertEquals(1, list.size());
    }
    

}


