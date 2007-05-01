/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-07-30 22:38:18 +0200 (Sun, 30 Jul 2006) $
 * $Revision: 6707 $
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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;

/**
 * @cdk.module test-standard
 *
 * @author     Kai Hartmann
 * @cdk.created    2004-02-20
 */
public class AtomContainerSetManipulatorTest extends CDKTestCase {
	
	IAtomContainer mol1 = null;
	IAtomContainer mol2 = null;
	IAtom atomInMol1 = null;
	IBond bondInMol1 = null;
	IAtom atomInMol2 = null;
	IAtomContainerSet som = new AtomContainerSet();
	
	public AtomContainerSetManipulatorTest(String name) {
		super(name);
	}
	
    public void setUp() {
		mol1 = new AtomContainer();
		atomInMol1 = new Atom("Cl");
		atomInMol1.setCharge(-1.0);
		atomInMol1.setFormalCharge(-1);
		atomInMol1.setHydrogenCount(1);
		mol1.addAtom(atomInMol1);
		mol1.addAtom(new Atom("Cl"));
		bondInMol1 = new Bond(atomInMol1, mol1.getAtom(1));
		mol1.addBond(bondInMol1);
		mol2 = new AtomContainer();
		atomInMol2 = new Atom("O");
		atomInMol2.setHydrogenCount(2);
		mol2.addAtom(atomInMol2);
		som.addAtomContainer(mol1);
		som.addAtomContainer(mol2);
	}
	
    public static Test suite() {
        TestSuite suite = new TestSuite(AtomContainerSetManipulatorTest.class);
        return suite;
	}

    public void testGetAtomCount_IAtomContainerSet()
    {
    	int count = AtomContainerSetManipulator.getAtomCount(som);
    	assertEquals(3, count);
    }
    
    public void testGetBondCount_IAtomContainerSet()
    {
    	int count = AtomContainerSetManipulator.getBondCount(som);
    	assertEquals(1, count);
    }
    
    public void testRemoveElectronContainer_IAtomContainerSet_IElectronContainer()
    {
    	IAtomContainerSet ms = new AtomContainerSet();
    	IAtomContainer mol = new AtomContainer();
    	mol.addAtom(new Atom("O"));
    	mol.addAtom(new Atom("O"));
    	mol.addBond(0, 1, 2.0);
    	IBond bond = mol.getBond(0);
    	ms.addAtomContainer(mol);
    	IBond otherBond = new Bond(new Atom(), new Atom());
    	AtomContainerSetManipulator.removeElectronContainer(ms, otherBond);
    	assertEquals(1, AtomContainerSetManipulator.getBondCount(ms));
    	AtomContainerSetManipulator.removeElectronContainer(ms, bond);
    	assertEquals(0, AtomContainerSetManipulator.getBondCount(ms));
    }
    
    public void testRemoveAtomAndConnectedElectronContainers_IAtomContainerSet_IAtom()
    {
    	IAtomContainerSet ms = new AtomContainerSet();
    	IAtomContainer mol = new AtomContainer();
    	mol.addAtom(new Atom("O"));
    	mol.addAtom(new Atom("O"));
    	mol.addBond(0, 1, 2.0);
    	IAtom atom = mol.getAtom(0);
    	ms.addAtomContainer(mol);
    	IAtom otherAtom = new Atom("O");
    	AtomContainerSetManipulator.removeAtomAndConnectedElectronContainers(ms, otherAtom);
    	assertEquals(1, AtomContainerSetManipulator.getBondCount(ms));
    	assertEquals(2, AtomContainerSetManipulator.getAtomCount(ms));
    	AtomContainerSetManipulator.removeAtomAndConnectedElectronContainers(ms, atom);
    	assertEquals(0, AtomContainerSetManipulator.getBondCount(ms));
    	assertEquals(1, AtomContainerSetManipulator.getAtomCount(ms));
    }
    
    public void testGetTotalCharge_IAtomContainerSet() {
        double charge = AtomContainerSetManipulator.getTotalCharge(som);
		assertEquals(-1.0, charge, 0.000001);
    }
	
	public void testGetTotalFormalCharge_IAtomContainerSet() {
        double charge = AtomContainerSetManipulator.getTotalFormalCharge(som);
		assertEquals(-1.0, charge, 0.000001);
    }
	
	public void testGetTotalHydrogenCount_IAtomContainerSet() {
		int hCount = AtomContainerSetManipulator.getTotalHydrogenCount(som);
		assertEquals(3, hCount);
	}
	
	public void testGetAllIDs_IAtomContainerSet()
	{
		som.setID("som");
		mol2.setID("mol");
		atomInMol2.setID("atom");
		bondInMol1.setID("bond");
		List list = AtomContainerSetManipulator.getAllIDs(som);
		assertEquals(4, list.size());
	}
	
	public void testGetAllAtomContainers_IAtomContainerSet()
	{
		List list = AtomContainerSetManipulator.getAllAtomContainers(som);
		assertEquals(2, list.size());
	}
	
	public void testSetAtomProperties_IAtomContainerSet_Object_Object()
	{
		String key = "key";
		String value = "value";
		AtomContainerSetManipulator.setAtomProperties(som, key, value);
		assertEquals(value, atomInMol1.getProperty(key));
		assertEquals(value, atomInMol2.getProperty(key));
	}
	
	public void testGetRelevantAtomContainer_IAtomContainerSet_IAtom()
	{
		IAtomContainer ac1 = AtomContainerSetManipulator.getRelevantAtomContainer(som, atomInMol1);
		assertEquals(mol1, ac1);
		IAtomContainer ac2 = AtomContainerSetManipulator.getRelevantAtomContainer(som, atomInMol2);
		assertEquals(mol2, ac2);
	}
	
	public void testGetRelevantAtomContainer_IAtomContainerSet_IBond()
	{
		IAtomContainer ac1 = AtomContainerSetManipulator.getRelevantAtomContainer(som, bondInMol1);
		assertEquals(mol1, ac1);
	}
	
	public void testGetAllChemObjects_IAtomContainerSet()
	{
		List list = AtomContainerSetManipulator.getAllChemObjects(som);
		assertEquals(3, list.size()); // only AtomContainerSets and AtomContainers at the moment (see source code comment)
	}
}

