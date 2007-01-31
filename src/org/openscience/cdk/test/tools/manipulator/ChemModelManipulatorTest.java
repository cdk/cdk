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

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * @cdk.module test-standard
 */
public class ChemModelManipulatorTest extends CDKTestCase {
    
	private final static LoggingTool logger = new LoggingTool(ChemModelManipulatorTest.class);
	
	IMolecule molecule1 = null;
	IMolecule molecule2 = null;
	IAtom atomInMol1 = null;
	IBond bondInMol1 = null;
	IAtom atomInMol2 = null;
	IMoleculeSet moleculeSet = null;
	IReaction reaction = null;
	IReactionSet reactionSet = null;
	IChemModel chemModel = null;
	
	public ChemModelManipulatorTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(ChemModelManipulatorTest.class);
	}
	
	public void setUp() {
		molecule1 = new Molecule();
		atomInMol1 = new Atom("Cl");
		atomInMol1.setCharge(-1.0);
		atomInMol1.setFormalCharge(-1);
		atomInMol1.setHydrogenCount(1);
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
	}

    public void testGetAllAtomContainers_IChemModel() throws Exception {
        String filename = "data/mdl/a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemModel chemFile = (ChemModel)reader.read((ChemObject)new ChemModel());
        assertNotNull(chemFile);
        List containersList = ChemModelManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());
    }
    
    public void testNewChemModel_IAtomContainer()
    {
    	IAtomContainer ac = new AtomContainer();
    	IChemModel model = ChemModelManipulator.newChemModel(ac);
    	assertEquals(ac, model.getMoleculeSet().getAtomContainer(0));
    }
    
    public void testGetAtomCount_IChemModel()
    {
    	int count = ChemModelManipulator.getAtomCount(chemModel);
    	assertEquals(6, count);
    }
    
    public void testGetBondCount_IChemModel()
    {
    	int count = ChemModelManipulator.getBondCount(chemModel);
    	assertEquals(2, count);
    }
    
    public void testRemoveElectronContainer_IChemModel_IElectronContainer()
    {
    	IMolecule mol1 = new Molecule();
		mol1.addAtom(new Atom("Cl"));
		mol1.addAtom(new Atom("Cl"));
		IBond bond1 = new Bond(mol1.getAtom(0), mol1.getAtom(1));
		mol1.addBond(bond1);
		IMolecule mol2 = new Molecule();
		mol2.addAtom(new Atom("I"));
		mol2.addAtom(new Atom("I"));
		IBond bond2 = new Bond(mol2.getAtom(0), mol2.getAtom(1));
		mol2.addBond(bond2);
		IMoleculeSet molSet = new MoleculeSet();
		molSet.addAtomContainer(mol1);
		IReaction r = new Reaction();
		r.addProduct(mol2);
		IReactionSet rSet = new ReactionSet();
		rSet.addReaction(r);
		IChemModel model = new ChemModel();
		model.setMoleculeSet(molSet);
		model.setReactionSet(rSet);
		IBond otherBond = new Bond();
		assertEquals(2, ChemModelManipulator.getBondCount(model));
		ChemModelManipulator.removeElectronContainer(model, otherBond);
		assertEquals(2, ChemModelManipulator.getBondCount(model));
		ChemModelManipulator.removeElectronContainer(model, bond1);
		assertEquals(1, ChemModelManipulator.getBondCount(model));
		ChemModelManipulator.removeElectronContainer(model, bond2);
		assertEquals(0, ChemModelManipulator.getBondCount(model));
    }
    
    public void testRemoveAtomAndConnectedElectronContainers_IChemModel_IAtom()
    {
    	IMolecule mol1 = new Molecule();
    	IAtom atom1 = new Atom("Cl");
		mol1.addAtom(atom1);
		mol1.addAtom(new Atom("Cl"));
		IBond bond1 = new Bond(mol1.getAtom(0), mol1.getAtom(1));
		mol1.addBond(bond1);
		IMolecule mol2 = new Molecule();
		IAtom atom2 = new Atom("I");
		mol2.addAtom(atom2);
		mol2.addAtom(new Atom("I"));
		IBond bond2 = new Bond(mol2.getAtom(0), mol2.getAtom(1));
		mol2.addBond(bond2);
		IMoleculeSet molSet = new MoleculeSet();
		molSet.addAtomContainer(mol1);
		IReaction r = new Reaction();
		r.addProduct(mol2);
		IReactionSet rSet = new ReactionSet();
		rSet.addReaction(r);
		IChemModel model = new ChemModel();
		model.setMoleculeSet(molSet);
		model.setReactionSet(rSet);
		IAtom otherAtom = new Atom("Cl");
		assertEquals(2, ChemModelManipulator.getBondCount(model));
		assertEquals(4, ChemModelManipulator.getAtomCount(model));
		ChemModelManipulator.removeAtomAndConnectedElectronContainers(model, otherAtom);
		assertEquals(2, ChemModelManipulator.getBondCount(model));
		assertEquals(4, ChemModelManipulator.getAtomCount(model));
		ChemModelManipulator.removeAtomAndConnectedElectronContainers(model, atom1);
		assertEquals(1, ChemModelManipulator.getBondCount(model));
		assertEquals(3, ChemModelManipulator.getAtomCount(model));
		ChemModelManipulator.removeAtomAndConnectedElectronContainers(model, atom2);
		assertEquals(0, ChemModelManipulator.getBondCount(model));
		assertEquals(2, ChemModelManipulator.getAtomCount(model));
    }
    
    public void testGetAllInOneContainer_IChemModel()
    {
    	IAtomContainer ac = ChemModelManipulator.getAllInOneContainer(chemModel);
    	assertEquals(3, ac.getAtomCount());
    	assertEquals(1, ac.getBondCount());
    }
    
    public void testSetAtomProperties_IChemModel_Object_Object()
    {
    	String key = "key";
    	String value = "value";
    	ChemModelManipulator.setAtomProperties(chemModel, key, value);
		assertEquals(value, atomInMol1.getProperty(key));
		assertEquals(value, atomInMol2.getProperty(key));
    }
    
    public void testGetRelevantAtomContainer_IChemModel_IAtom()
    {
    	IAtomContainer ac1 = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInMol1);
		assertEquals(molecule1, ac1);
		IAtomContainer ac2 = ChemModelManipulator.getRelevantAtomContainer(chemModel, atomInMol2);
		assertEquals(molecule2, ac2);
    }
    
    public void testGetRelevantAtomContainer_IChemModel_IBond()
    {
    	IAtomContainer ac1 = ChemModelManipulator.getRelevantAtomContainer(chemModel, bondInMol1);
		assertEquals(molecule1, ac1);
    }
    
    public void testGetAllChemObjects_IChemModel()
    {
    	List list = ChemModelManipulator.getAllChemObjects(chemModel);
    	assertEquals(5, list.size());
    	//int atomCount = 0; // not traversed
    	//int bondCount = 0; // not traversed
    	int molCount = 0;
    	int molSetCount = 0;
    	int reactionCount = 0;
    	int reactionSetCount = 0;
    	for (Iterator iter = list.iterator(); iter.hasNext();) {
    		Object o = iter.next();
    		//if (o instanceof IAtom) ++atomCount;
    		//if (o instanceof IBond) ++bondCount;
    		if (o instanceof IMolecule) ++molCount;
    		else if (o instanceof IMoleculeSet) ++molSetCount;
    		else if (o instanceof IReaction) ++reactionCount;
    		else if (o instanceof IReactionSet) ++reactionSetCount;
    		else System.out.println(o.getClass());
    	}
    	//assertEquals(3, atomCount);
    	//assertEquals(1, bondCount);
    	assertEquals(2, molCount);
    	assertEquals(1, molSetCount);
    	assertEquals(1, reactionCount);
    	assertEquals(1, reactionSetCount);
    }
    
    public void testCreateNewMolecule_IChemModel()
    {
    	IChemModel model = new ChemModel();
    	IAtomContainer ac = ChemModelManipulator.createNewMolecule(model);
    	assertEquals(1, model.getMoleculeSet().getAtomContainerCount());
    	assertEquals(ac, model.getMoleculeSet().getAtomContainer(0));
    }
    
    public void testGetRelevantReaction_IChemModel_IAtom()
    {
    	IReaction r = ChemModelManipulator.getRelevantReaction(chemModel, atomInMol1);
    	assertNotNull(r);
    	assertEquals(reaction, r);
    }
    
}


