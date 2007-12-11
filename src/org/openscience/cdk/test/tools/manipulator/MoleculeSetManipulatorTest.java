/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-04-06 19:24:31 +0200 (Thu, 06 Apr 2006) $
 * $Revision: 5897 $
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.MoleculeSetManipulator;

/**
 * @cdk.module test-standard
 *
 * @author     Kai Hartmann
 * @cdk.created    2004-02-20
 */
public class MoleculeSetManipulatorTest extends CDKTestCase {
	
	
	IMolecule mol1 = null;
	IMolecule mol2 = null;
	IAtom atomInMol1 = null;
	IBond bondInMol1 = null;
	IAtom atomInMol2 = null;
	IMoleculeSet som = new MoleculeSet();
	
	public MoleculeSetManipulatorTest(String name) {
		super(name);
	}
	
    public void setUp() {
		mol1 = new Molecule();
		atomInMol1 = new Atom("Cl");
		atomInMol1.setCharge(-1.0);
		atomInMol1.setFormalCharge(-1);
		atomInMol1.setHydrogenCount(1);
		mol1.addAtom(atomInMol1);
		mol1.addAtom(new Atom("Cl"));
		bondInMol1 = new Bond(atomInMol1, mol1.getAtom(1));
		mol1.addBond(bondInMol1);
		mol2 = new Molecule();
		atomInMol2 = new Atom("O");
		atomInMol2.setHydrogenCount(2);
		mol2.addAtom(atomInMol2);
		som.addMolecule(mol1);
		som.addMolecule(mol2);
	}
	
    public static Test suite() {
        TestSuite suite = new TestSuite(MoleculeSetManipulatorTest.class);
        return suite;
	}

    public void testGetAtomCount_IAtomContainerSet()
    {
    	int count = MoleculeSetManipulator.getAtomCount(som);
    	assertEquals(3, count);
    }
    
    public void testGetBondCount_IAtomContainerSet()
    {
    	int count = MoleculeSetManipulator.getBondCount(som);
    	assertEquals(1, count);
    }
    
    public void testRemoveElectronContainer_IMoleculeSet_IElectronContainer()
    {
    	IMoleculeSet ms = new MoleculeSet();
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("O"));
    	mol.addAtom(new Atom("O"));
    	mol.addBond(0, 1, IBond.Order.DOUBLE);
    	IBond bond = mol.getBond(0);
    	ms.addMolecule(mol);
    	IBond otherBond = new Bond(new Atom(), new Atom());
    	MoleculeSetManipulator.removeElectronContainer(ms, otherBond);
    	assertEquals(1, MoleculeSetManipulator.getBondCount(ms));
    	MoleculeSetManipulator.removeElectronContainer(ms, bond);
    	assertEquals(0, MoleculeSetManipulator.getBondCount(ms));
    }
    
    public void testRemoveAtomAndConnectedElectronContainers_IMoleculeSet_IAtom()
    {
    	IMoleculeSet ms = new MoleculeSet();
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("O"));
    	mol.addAtom(new Atom("O"));
    	mol.addBond(0, 1, IBond.Order.DOUBLE);
    	IAtom atom = mol.getAtom(0);
    	ms.addMolecule(mol);
    	IAtom otherAtom = new Atom("O");
    	MoleculeSetManipulator.removeAtomAndConnectedElectronContainers(ms, otherAtom);
    	assertEquals(1, MoleculeSetManipulator.getBondCount(ms));
    	assertEquals(2, MoleculeSetManipulator.getAtomCount(ms));
    	MoleculeSetManipulator.removeAtomAndConnectedElectronContainers(ms, atom);
    	assertEquals(0, MoleculeSetManipulator.getBondCount(ms));
    	assertEquals(1, MoleculeSetManipulator.getAtomCount(ms));
    }
    
    public void testGetTotalCharge_IMoleculeSet() {
        double charge = MoleculeSetManipulator.getTotalCharge(som);
		assertEquals(-1.0, charge, 0.000001);
    }
	
	public void testGetTotalFormalCharge_IMoleculeSet() {
        double charge = MoleculeSetManipulator.getTotalFormalCharge(som);
		assertEquals(-1.0, charge, 0.000001);
    }
	
	public void testGetTotalHydrogenCount_IMoleculeSet() {
		int hCount = MoleculeSetManipulator.getTotalHydrogenCount(som);
		assertEquals(3, hCount);
	}
	
	public void testGetAllIDs_IMoleculeSet()
	{
		som.setID("som");
		mol2.setID("mol");
		atomInMol2.setID("atom");
		bondInMol1.setID("bond");
		List list = MoleculeSetManipulator.getAllIDs(som);
		assertEquals(4, list.size());
	}
	
	public void testGetAllAtomContainers_IMoleculeSet()
	{
		List list = MoleculeSetManipulator.getAllAtomContainers(som);
		assertEquals(2, list.size());
	}
	
	public void testSetAtomProperties_IMoleculeSet_Object_Object()
	{
		String key = "key";
		String value = "value";
		MoleculeSetManipulator.setAtomProperties(som, key, value);
		assertEquals(value, atomInMol1.getProperty(key));
		assertEquals(value, atomInMol2.getProperty(key));
	}
	
	public void testGetRelevantAtomContainer_IMoleculeSet_IAtom()
	{
		IAtomContainer ac1 = MoleculeSetManipulator.getRelevantAtomContainer(som, atomInMol1);
		assertEquals(mol1, ac1);
		IAtomContainer ac2 = MoleculeSetManipulator.getRelevantAtomContainer(som, atomInMol2);
		assertEquals(mol2, ac2);
	}
	
	public void testGetRelevantAtomContainer_IMoleculeSet_IBond()
	{
		IAtomContainer ac1 = MoleculeSetManipulator.getRelevantAtomContainer(som, bondInMol1);
		assertEquals(mol1, ac1);
	}
	
	public void testGetAllChemObjects_IMoleculeSet()
	{
		List list = MoleculeSetManipulator.getAllChemObjects(som);
		assertEquals(3, list.size()); // only MoleculeSets and AtomContainers at the moment (see source code comment)
	}
}

