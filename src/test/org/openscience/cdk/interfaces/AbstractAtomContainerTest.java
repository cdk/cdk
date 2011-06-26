/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.interfaces;

import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of the AtomContainer.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractAtomContainerTest extends AbstractChemObjectTest {

    @Test public void testSetAtoms_arrayIAtom() {
        IAtomContainer ac = (IAtomContainer)newChemObject();
        IAtom[] atoms = new IAtom[4];
        atoms[0] = ac.getBuilder().newInstance(IAtom.class,"C");
        atoms[1] = ac.getBuilder().newInstance(IAtom.class,"C");
        atoms[2] = ac.getBuilder().newInstance(IAtom.class,"C");
        atoms[3] = ac.getBuilder().newInstance(IAtom.class,"O");
        ac.setAtoms(atoms);
        
        Assert.assertEquals(4, ac.getAtomCount());
        //Assert.assertEquals(4, ac.getAtoms().length);
    }

    /**
     * @cdk.bug 2993609
     */
    @Test public void testSetAtoms_removeListener() {
        IAtomContainer ac = (IAtomContainer)newChemObject();

        IAtom[] atoms = new IAtom[4];
        atoms[0] = ac.getBuilder().newInstance(IAtom.class,"C");
        atoms[1] = ac.getBuilder().newInstance(IAtom.class,"C");
        atoms[2] = ac.getBuilder().newInstance(IAtom.class,"C");
        atoms[3] = ac.getBuilder().newInstance(IAtom.class,"O");
        ac.setAtoms(atoms);

        // if an atom changes, the atomcontainer will throw a change event too
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        ac.addListener(listener);
        Assert.assertFalse(listener.changed);

        // ok, change the atom, and make sure we do get an event
        atoms[0].setAtomTypeName("C.sp2");
        Assert.assertTrue(listener.changed);

        // reset the listener, overwrite the atoms, and change an old atom.
        // if all is well, we should not get a change event this time
        ac.setAtoms(new IAtom[0]);
        listener.reset(); // reset here, because the setAtoms() triggers a change even too
        Assert.assertFalse(listener.changed); // make sure the reset worked
        atoms[1].setAtomTypeName("C.sp2"); // make a change to an old atom
        Assert.assertFalse(listener.changed); // but no change event should happen
    }

    /**
     * Only test whether the atoms are correctly cloned.
     */
	@Test public void testClone() throws Exception {
        IAtomContainer molecule = (IAtomContainer)newChemObject();
        Object clone = molecule.clone();
        Assert.assertTrue(clone instanceof IAtomContainer);
    }    
        
    @Test public void testClone_IAtom() throws Exception {
		IAtomContainer molecule = (IAtomContainer)newChemObject();
		molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class,"C")); // 1
		molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class,"C")); // 2
		molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class,"C")); // 3
		molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class,"C")); // 4

		IAtomContainer clonedMol = (IAtomContainer)molecule.clone();
		Assert.assertEquals(molecule.getAtomCount(), clonedMol.getAtomCount());
		for (int f = 0; f < molecule.getAtomCount(); f++) {
			for (int g = 0; g < clonedMol.getAtomCount(); g++) {
				Assert.assertNotNull(molecule.getAtom(f));
				Assert.assertNotNull(clonedMol.getAtom(g));
				Assert.assertNotSame(molecule.getAtom(f), clonedMol.getAtom(g));
			}
		}        
    }

    @Test public void testCloneButKeepOriginalsIntact() throws Exception {
        IAtomContainer molecule = (IAtomContainer)newChemObject();
        IAtom atom = molecule.getBuilder().newInstance(IAtom.class);
        molecule.addAtom(atom);
        Assert.assertEquals(atom, molecule.getAtom(0));
        Object clone = molecule.clone();
        Assert.assertNotSame(molecule, clone);
        // after the cloning the IAtom on the original IMolecule should be unchanged
        Assert.assertEquals(atom, molecule.getAtom(0));
    }

    @Test public void testCloneButKeepOriginalsIntact_IBond() throws Exception {
        IAtomContainer molecule = (IAtomContainer)newChemObject();
        molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class));
        molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class));
        IBond bond = molecule.getBuilder().newInstance(IBond.class,
            molecule.getAtom(0),
            molecule.getAtom(1),
            IBond.Order.SINGLE
        );
        molecule.addBond(bond);
        Assert.assertEquals(bond, molecule.getBond(0));
        Object clone = molecule.clone();
        Assert.assertNotSame(molecule, clone);
        // after the cloning the IBond on the original IMolecule should be unchanged
        Assert.assertEquals(bond, molecule.getBond(0));
    }

    @Test public void testCloneButKeepOriginalsIntact_ILonePair() throws Exception {
        IAtomContainer molecule = (IAtomContainer)newChemObject();
        molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class));
        ILonePair lonePair = molecule.getBuilder().newInstance(ILonePair.class,
            molecule.getAtom(0)
        );
        molecule.addLonePair(lonePair);
        Assert.assertEquals(lonePair, molecule.getLonePair(0));
        Object clone = molecule.clone();
        Assert.assertNotSame(molecule, clone);
        // after the cloning the ILonePair on the original IMolecule should be unchanged
        Assert.assertEquals(lonePair, molecule.getLonePair(0));
    }

    @Test public void testCloneButKeepOriginalsIntact_ISingleElectron() throws Exception {
        IAtomContainer molecule = (IAtomContainer)newChemObject();
        molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class));
        ISingleElectron singleElectron = molecule.getBuilder().newInstance(ISingleElectron.class,
            molecule.getAtom(0)
        );
        molecule.addSingleElectron(singleElectron);
        Assert.assertEquals(singleElectron, molecule.getSingleElectron(0));
        Object clone = molecule.clone();
        Assert.assertNotSame(molecule, clone);
        // after the cloning the ISingleElectron on the original IMolecule should be unchanged
        Assert.assertEquals(singleElectron, molecule.getSingleElectron(0));
    }

	@Test public void testClone_IAtom2() throws Exception {
		IAtomContainer molecule = (IAtomContainer)newChemObject();
        IAtom carbon = molecule.getBuilder().newInstance(IAtom.class,"C");
        carbon.setPoint2d(new Point2d(2, 4));
		molecule.addAtom(carbon); // 1

        // test cloning of Atoms
		IAtomContainer clonedMol = (IAtomContainer)molecule.clone();
        carbon.setPoint2d(new Point2d(3, 1));
		Assert.assertEquals(clonedMol.getAtom(0).getPoint2d().x, 2.0, 0.001);
	}

    @Test public void testClone_IBond() throws Exception {
		IAtomContainer molecule = (IAtomContainer)newChemObject();
		molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class,"C")); // 1
		molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class,"C")); // 2
		molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class,"C")); // 3
		molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class,"C")); // 4

		molecule.addBond(0, 1, IBond.Order.DOUBLE); // 1
		molecule.addBond(1, 2, IBond.Order.SINGLE); // 2
		molecule.addBond(2, 3, IBond.Order.SINGLE); // 3
		IAtomContainer clonedMol = (IAtomContainer)molecule.clone();
		Assert.assertNotNull(clonedMol);
		Assert.assertEquals(molecule.getBondCount(), clonedMol.getBondCount());
		for (int f = 0; f < molecule.getElectronContainerCount(); f++) {
			for (int g = 0; g < clonedMol.getElectronContainerCount(); g++) {
				Assert.assertNotNull(molecule.getBond(f));
				Assert.assertNotNull(clonedMol.getBond(g));
				Assert.assertNotSame(molecule.getBond(f), clonedMol.getBond(g));
			}
		}
	}

    @Test public void testClone_IBond2() throws Exception {
		IAtomContainer molecule = (IAtomContainer)newChemObject();
        IAtom atom1 = molecule.getBuilder().newInstance(IAtom.class,"C");
        IAtom atom2 = molecule.getBuilder().newInstance(IAtom.class,"C");
		molecule.addAtom(atom1); // 1
		molecule.addAtom(atom2); // 2
		molecule.addBond(molecule.getBuilder().newInstance(IBond.class,atom1, atom2, IBond.Order.DOUBLE)); // 1
        
        // test cloning of atoms in bonds
		IAtomContainer clonedMol = (IAtomContainer)molecule.clone();
		Assert.assertNotNull(clonedMol);
        Assert.assertNotSame(atom1, clonedMol.getBond(0).getAtom(0));
        Assert.assertNotSame(atom2, clonedMol.getBond(0).getAtom(1));
	}

    @Test public void testClone_IBond3() throws Exception {
		IAtomContainer molecule = (IAtomContainer)newChemObject();
        IAtom atom1 = molecule.getBuilder().newInstance(IAtom.class,"C");
        IAtom atom2 = molecule.getBuilder().newInstance(IAtom.class,"C");
		molecule.addAtom(atom1); // 1
		molecule.addAtom(atom2); // 2
		molecule.addBond(molecule.getBuilder().newInstance(IBond.class,atom1, atom2, IBond.Order.DOUBLE)); // 1
        
        // test that cloned bonds contain atoms from cloned atomcontainer
		IAtomContainer clonedMol = (IAtomContainer)molecule.clone();
		Assert.assertNotNull(clonedMol);
        Assert.assertTrue(clonedMol.contains(clonedMol.getBond(0).getAtom(0)));
        Assert.assertTrue(clonedMol.contains(clonedMol.getBond(0).getAtom(1)));
	}

    @Test public void testClone_AtomlessIBond() throws Exception {
        IAtomContainer molecule = (IAtomContainer)newChemObject();
        IBond bond = molecule.getBuilder().newInstance(IBond.class);
        molecule.addBond(bond);
        Assert.assertEquals(bond, molecule.getBond(0));
        IAtomContainer clone = (IAtomContainer)molecule.clone();
        Assert.assertEquals(0, clone.getBond(0).getAtomCount());
    }

    @Test public void testClone_AtomlessILonePair() throws Exception {
        IAtomContainer molecule = (IAtomContainer)newChemObject();
        ILonePair lonePair = molecule.getBuilder().newInstance(ILonePair.class);
        molecule.addLonePair(lonePair);
        Assert.assertEquals(lonePair, molecule.getLonePair(0));
        IAtomContainer clone = (IAtomContainer)molecule.clone();
        Assert.assertNotNull(clone.getLonePair(0));
    }

    @Test public void testClone_AtomlessISingleElectron() throws Exception {
        IAtomContainer molecule = (IAtomContainer)newChemObject();
        ISingleElectron singleElectron = molecule.getBuilder().newInstance(ISingleElectron.class);
        molecule.addSingleElectron(singleElectron);
        Assert.assertEquals(singleElectron, molecule.getSingleElectron(0));
        IAtomContainer clone = (IAtomContainer)molecule.clone();
        Assert.assertNotNull(clone.getSingleElectron(0));
    }

    @Test public void testClone_ILonePair() throws Exception {
		IAtomContainer molecule = (IAtomContainer)newChemObject();
        IAtom atom1 = molecule.getBuilder().newInstance(IAtom.class,"C");
        IAtom atom2 = molecule.getBuilder().newInstance(IAtom.class,"C");
		molecule.addAtom(atom1); // 1
		molecule.addAtom(atom2); // 2
		molecule.addLonePair(0); 
        
        // test that cloned bonds contain atoms from cloned atomcontainer
		IAtomContainer clonedMol = (IAtomContainer)molecule.clone();
		Assert.assertNotNull(clonedMol);
        Assert.assertEquals(1, clonedMol.getConnectedLonePairsCount(clonedMol.getAtom(0)));
	}

    @Test public void testGetConnectedElectronContainersList_IAtom() {
        // acetone molecule
        IAtomContainer acetone = (IAtomContainer)newChemObject();
        
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(1, acetone.getConnectedElectronContainersList(o).size());
        Assert.assertEquals(3, acetone.getConnectedElectronContainersList(c1).size());
        Assert.assertEquals(1, acetone.getConnectedElectronContainersList(c2).size());
        Assert.assertEquals(1, acetone.getConnectedElectronContainersList(c3).size());
        
        // add lone pairs on oxygen
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);

        Assert.assertEquals(3, acetone.getConnectedElectronContainersList(o).size());
        Assert.assertEquals(3, acetone.getConnectedElectronContainersList(c1).size());
        Assert.assertEquals(1, acetone.getConnectedElectronContainersList(c2).size());
        Assert.assertEquals(1, acetone.getConnectedElectronContainersList(c3).size());

    }

    @Test public void testGetConnectedBondsList_IAtom() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(1, acetone.getConnectedBondsList(o).size());
        Assert.assertEquals(3, acetone.getConnectedBondsList(c1).size());
        Assert.assertEquals(1, acetone.getConnectedBondsList(c2).size());
        Assert.assertEquals(1, acetone.getConnectedBondsList(c3).size());
        
        // add lone pairs on oxygen
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);

        Assert.assertEquals(1, acetone.getConnectedBondsList(o).size());
        Assert.assertEquals(3, acetone.getConnectedBondsList(c1).size());
        Assert.assertEquals(1, acetone.getConnectedBondsList(c2).size());
        Assert.assertEquals(1, acetone.getConnectedBondsList(c3).size());
    }

//    @Test public void testGetConnectedBonds_IAtom() {
//        // acetone molecule
//        IMolecule acetone = getNewBuilder().newInstance(IMolecule.class);
//        
//        IAtom c1 = getNewBuilder().newInstance(IAtom.class,"C");
//        IAtom c2 = getNewBuilder().newInstance(IAtom.class,"C");
//        IAtom o = getNewBuilder().newInstance(IAtom.class,"O");
//        IAtom c3 = getNewBuilder().newInstance(IAtom.class,"C");
//        acetone.addAtom(c1);
//        acetone.addAtom(c2);
//        acetone.addAtom(c3);
//        acetone.addAtom(o);
//        IBond b1 = getNewBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
//        IBond b2 = getNewBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
//        IBond b3 = getNewBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
//        acetone.addBond(b1);
//        acetone.addBond(b2);
//        acetone.addBond(b3);
//        
//        Assert.assertEquals(1, acetone.getConnectedBondsVector(o).size());
//        Assert.assertEquals(3, acetone.getConnectedBondsVector(c1).size());
//        Assert.assertEquals(1, acetone.getConnectedBondsVector(c2).size());
//        Assert.assertEquals(1, acetone.getConnectedBondsVector(c3).size());
//        
//        // add lone pairs on oxygen
//        ILonePair lp1 = getNewBuilder().newInstance(ILonePair.class,o);
//        ILonePair lp2 = getNewBuilder().newInstance(ILonePair.class,o);
//        acetone.addElectronContainer(lp1);
//        acetone.addElectronContainer(lp2);
//
//        Assert.assertEquals(1, acetone.getConnectedBondsVector(o).size());
//        Assert.assertEquals(3, acetone.getConnectedBondsVector(c1).size());
//        Assert.assertEquals(1, acetone.getConnectedBondsVector(c2).size());
//        Assert.assertEquals(1, acetone.getConnectedBondsVector(c3).size());
//    }

    @Test public void testGetConnectedLonePairsList_IAtom() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(0, acetone.getConnectedLonePairsList(o).size());
        Assert.assertEquals(0, acetone.getConnectedLonePairsList(c1).size());
        Assert.assertEquals(0, acetone.getConnectedLonePairsList(c2).size());
        Assert.assertEquals(0, acetone.getConnectedLonePairsList(c3).size());

        // add lone pairs on oxygen
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);

        Assert.assertEquals(2, acetone.getConnectedLonePairsList(o).size());
        Assert.assertEquals(0, acetone.getConnectedLonePairsList(c1).size());
        Assert.assertEquals(0, acetone.getConnectedLonePairsList(c2).size());
        Assert.assertEquals(0, acetone.getConnectedLonePairsList(c3).size());

    }

    
    @Test public void testRemoveAtomAndConnectedElectronContainers_IAtom() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);
        
        // remove the oxygen
        acetone.removeAtomAndConnectedElectronContainers(o);
        Assert.assertEquals(3, acetone.getAtomCount());
        Assert.assertEquals(2, acetone.getBondCount());
        Assert.assertEquals(0, acetone.getLonePairCount());
    }

    @Test public void testGetAtomCount() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        Assert.assertEquals(0, acetone.getAtomCount());
        
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        
        Assert.assertEquals(4, acetone.getAtomCount());
    }
    
    @Test public void testGetBondCount() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        Assert.assertEquals(0, acetone.getBondCount());
        
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(3, acetone.getBondCount());
    }

    @Test public void testAdd_IAtomContainer() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        IAtomContainer container = (IAtomContainer)newChemObject();
        container.add(acetone);
        Assert.assertEquals(4, container.getAtomCount());
        Assert.assertEquals(3, container.getBondCount());
    }
    
    @Test public void testRemove_IAtomContainer() throws Exception {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        IAtomContainer container = (IAtomContainer)newChemObject();
        container.add(acetone);
        Assert.assertEquals(4, container.getAtomCount());
        Assert.assertEquals(3, container.getBondCount());
        container.remove((IAtomContainer)acetone.clone());
        Assert.assertEquals(4, container.getAtomCount());
        Assert.assertEquals(3, container.getBondCount());
        container.remove(acetone);
        Assert.assertEquals(0, container.getAtomCount());
        Assert.assertEquals(0, container.getBondCount());
    }
    
    @Test public void testRemoveAllElements() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        IAtomContainer container = (IAtomContainer)newChemObject();
        container.add(acetone);
        Assert.assertEquals(4, container.getAtomCount());
        Assert.assertEquals(3, container.getBondCount());
        container.removeAllElements();
        Assert.assertEquals(0, container.getAtomCount());
        Assert.assertEquals(0, container.getBondCount());
    }
    
    @Test public void testRemoveAtom_int() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);

        Assert.assertEquals(4, acetone.getAtomCount());
        acetone.removeAtom(1);
        Assert.assertEquals(3, acetone.getAtomCount());
        Assert.assertEquals(c1, acetone.getAtom(0));
        Assert.assertEquals(c3, acetone.getAtom(1));
        Assert.assertEquals(o, acetone.getAtom(2));
    }
    
    @Test public void testRemoveAtom_IAtom() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);

        Assert.assertEquals(4, acetone.getAtomCount());
        acetone.removeAtom(c3);
        Assert.assertEquals(3, acetone.getAtomCount());
        Assert.assertEquals(c1, acetone.getAtom(0));
        Assert.assertEquals(c2, acetone.getAtom(1));
        Assert.assertEquals(o, acetone.getAtom(2));
    }
    
    @Test public void testSetAtom_int_IAtom() {
        IAtomContainer container = (IAtomContainer)newChemObject();
        IAtom c = container.getBuilder().newInstance(IAtom.class,"C");
        container.setAtom(0, c);
        
        Assert.assertNotNull(container.getAtom(0));
        Assert.assertEquals("C", container.getAtom(0).getSymbol());
    }
    
    @Test public void testGetAtom_int() {
        IAtomContainer acetone = (IAtomContainer)newChemObject();
        
        IAtom c = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom n = acetone.getBuilder().newInstance(IAtom.class,"N");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom s = acetone.getBuilder().newInstance(IAtom.class,"S");
        acetone.addAtom(c);
        acetone.addAtom(n);
        acetone.addAtom(o);
        acetone.addAtom(s);
        
        org.openscience.cdk.interfaces.IAtom a1 = acetone.getAtom(0);
        Assert.assertNotNull(a1);
        Assert.assertEquals("C", a1.getSymbol());
        org.openscience.cdk.interfaces.IAtom a2 = acetone.getAtom(1);
        Assert.assertNotNull(a2);
        Assert.assertEquals("N", a2.getSymbol());
        org.openscience.cdk.interfaces.IAtom a3 = acetone.getAtom(2);
        Assert.assertNotNull(a3);
        Assert.assertEquals("O", a3.getSymbol());
        org.openscience.cdk.interfaces.IAtom a4 = acetone.getAtom(3);
        Assert.assertNotNull(a4);
        Assert.assertEquals("S", a4.getSymbol());
    }
    
    @Test public void testGetBond_int() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        Assert.assertEquals(0, acetone.getBondCount());
        
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.TRIPLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(IBond.Order.TRIPLE, acetone.getBond(0).getOrder());
        Assert.assertEquals(IBond.Order.DOUBLE, acetone.getBond(1).getOrder());
        Assert.assertEquals(IBond.Order.SINGLE, acetone.getBond(2).getOrder());
    }
    
//    @Test public void testSetElectronContainer_int_IElectronContainer() {
//        IAtomContainer container = (IAtomContainer)newChemObject();
//        IAtom c1 = getNewBuilder().newInstance(IAtom.class,"C");
//        IAtom c2 = getNewBuilder().newInstance(IAtom.class,"C");
//        container.addAtom(c1);
//        container.addAtom(c2);
//        IBond b = getNewBuilder().newInstance(IBond.class,c1, c2, 3);
//        container.setElectronContainer(3, b);
//        
//        Assert.assertTrue(container.getElectronContainer(3) instanceof org.openscience.cdk.interfaces.IBond);
//        IBond bond = (IBond)container.getElectronContainer(3);
//        Assert.assertEquals(3.0, bond.getOrder());;
//    }
    
    @Test public void testGetElectronContainerCount() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);
        
        Assert.assertEquals(3, acetone.getBondCount());
        Assert.assertEquals(2, acetone.getLonePairCount());
        Assert.assertEquals(5, acetone.getElectronContainerCount());
    }
    
    @Test public void testRemoveAllBonds() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(3, acetone.getBondCount());
	
	acetone.removeAllBonds();
	Assert.assertEquals(0, acetone.getBondCount());
    }
    
    @Test public void testRemoveAllElectronContainers() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(3, acetone.getElectronContainerCount());
	
        acetone.removeAllElectronContainers();
        Assert.assertEquals(0, acetone.getElectronContainerCount());
    }
    
//    @Test public void testSetElectronContainerCount_int() {
//        IAtomContainer container = (IAtomContainer)newChemObject();
//        container.setElectronContainerCount(2);
//        
//        Assert.assertEquals(2, container.getElectronContainerCount());
//    }
    
//    @Test public void testSetAtomCount_int() {
//        IAtomContainer container = (IAtomContainer)newChemObject();
//        container.setAtomCount(2);
//        
//        Assert.assertEquals(2, container.getAtomCount());
//    }
    
//    @Test public void testGetAtoms() {
//        // acetone molecule
//        IMolecule acetone = getNewBuilder().newInstance(IMolecule.class);
//        IAtom c1 = getNewBuilder().newInstance(IAtom.class,"C");
//        IAtom c2 = getNewBuilder().newInstance(IAtom.class,"C");
//        IAtom o = getNewBuilder().newInstance(IAtom.class,"O");
//        IAtom c3 = getNewBuilder().newInstance(IAtom.class,"C");
//        acetone.addAtom(c1);
//        acetone.addAtom(c2);
//        acetone.addAtom(c3);
//        acetone.addAtom(o);
//        
//        Assert.assertEquals(4, acetone.getAtoms().length);
//    }
    
    @Test public void testAddAtom_IAtom() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        
        java.util.Iterator atomIter = acetone.atoms().iterator();
        int counter = 0;
        while (atomIter.hasNext()) {
            atomIter.next();
            counter++;
        }
        Assert.assertEquals(4, counter);
        
        // test force growing of default arrays
        for (int i=0; i<11; i++) {
        	acetone.addAtom(acetone.getBuilder().newInstance(IAtom.class));
        	acetone.addBond(acetone.getBuilder().newInstance(IBond.class));
        }
    }

    @Test public void testAtoms() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        
        java.util.Iterator atomIter = acetone.atoms().iterator();
        Assert.assertNotNull(atomIter);
        Assert.assertTrue(atomIter.hasNext());
        IAtom next = (IAtom)atomIter.next();
        Assert.assertTrue(next instanceof IAtom);
        Assert.assertEquals(c1, next);
        Assert.assertTrue(atomIter.hasNext());
        next = (IAtom)atomIter.next();
        Assert.assertTrue(next instanceof IAtom);
        Assert.assertEquals(c2, next);
        Assert.assertTrue(atomIter.hasNext());
        next = (IAtom)atomIter.next();
        Assert.assertTrue(next instanceof IAtom);
        Assert.assertEquals(c3, next);
        Assert.assertTrue(atomIter.hasNext());
        next = (IAtom)atomIter.next();
        Assert.assertTrue(next instanceof IAtom);
        Assert.assertEquals(o, next);
        
        Assert.assertFalse(atomIter.hasNext());
    }

    @Test public void testBonds() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);

        IBond bond1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond bond2 = acetone.getBuilder().newInstance(IBond.class,c2, o, IBond.Order.DOUBLE);
        IBond bond3 = acetone.getBuilder().newInstance(IBond.class,c2, c3, IBond.Order.SINGLE);
        acetone.addBond(bond1);
        acetone.addBond(bond2);
        acetone.addBond(bond3);

        java.util.Iterator bonds = acetone.bonds().iterator();
        Assert.assertNotNull(bonds);
        Assert.assertTrue(bonds.hasNext());

        IBond next =  (IBond) bonds.next();
        Assert.assertTrue(next instanceof IBond);
        Assert.assertEquals(bond1, next);

        next = (IBond) bonds.next();
        Assert.assertTrue(next instanceof IBond);
        Assert.assertEquals(bond2, next);

        next = (IBond) bonds.next();
        Assert.assertTrue(next instanceof IBond);
        Assert.assertEquals(bond3, next);

        Assert.assertFalse(bonds.hasNext());
    }
    
    @Test public void testLonePairs() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);

        IBond bond1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond bond2 = acetone.getBuilder().newInstance(IBond.class,c2, o, IBond.Order.DOUBLE);
        IBond bond3 = acetone.getBuilder().newInstance(IBond.class,c2, c3, IBond.Order.SINGLE);
        acetone.addBond(bond1);
        acetone.addBond(bond2);
        acetone.addBond(bond3);
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);

        java.util.Iterator lonePairs = acetone.lonePairs().iterator();
        Assert.assertNotNull(lonePairs);
        Assert.assertTrue(lonePairs.hasNext());

        ILonePair next =  (ILonePair) lonePairs.next();
        Assert.assertTrue(next instanceof ILonePair);
        Assert.assertEquals(lp1, next);

        next =  (ILonePair) lonePairs.next();
        Assert.assertTrue(next instanceof ILonePair);
        Assert.assertEquals(lp2, next);

        Assert.assertFalse(lonePairs.hasNext());
    }

    @Test public void testSingleElectrons() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);

        IBond bond1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond bond2 = acetone.getBuilder().newInstance(IBond.class,c2, o, IBond.Order.DOUBLE);
        IBond bond3 = acetone.getBuilder().newInstance(IBond.class,c2, c3, IBond.Order.SINGLE);
        acetone.addBond(bond1);
        acetone.addBond(bond2);
        acetone.addBond(bond3);
        ISingleElectron se1 = acetone.getBuilder().newInstance(ISingleElectron.class, o);
        ISingleElectron se2 = acetone.getBuilder().newInstance(ISingleElectron.class, c1);
        acetone.addSingleElectron(se1);
        acetone.addSingleElectron(se2);

        java.util.Iterator singleElectrons = acetone.singleElectrons().iterator();
        Assert.assertNotNull(singleElectrons);
        Assert.assertTrue(singleElectrons.hasNext());

        ISingleElectron next =  (ISingleElectron) singleElectrons.next();
        Assert.assertTrue(next instanceof ISingleElectron);
        Assert.assertEquals(se1, next);

        next =  (ISingleElectron) singleElectrons.next();
        Assert.assertTrue(next instanceof ISingleElectron);
        Assert.assertEquals(se2, next);

        Assert.assertFalse(singleElectrons.hasNext());
    }
    
    @Test public void testElectronContainers() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);

        IBond bond1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond bond2 = acetone.getBuilder().newInstance(IBond.class,c2, o, IBond.Order.DOUBLE);
        IBond bond3 = acetone.getBuilder().newInstance(IBond.class,c2, c3, IBond.Order.SINGLE);
        acetone.addBond(bond1);
        acetone.addBond(bond2);
        acetone.addBond(bond3);
        ISingleElectron se1 = acetone.getBuilder().newInstance(ISingleElectron.class, c1);
        ISingleElectron se2 = acetone.getBuilder().newInstance(ISingleElectron.class, c2);
        acetone.addSingleElectron(se1);
        acetone.addSingleElectron(se2);
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);

        java.util.Iterator electronContainers = acetone.electronContainers().iterator();
        Assert.assertNotNull(electronContainers);
        Assert.assertTrue(electronContainers.hasNext());
        electronContainers.next();
        electronContainers.next();
        IElectronContainer ec = (IElectronContainer)electronContainers.next();
        Assert.assertTrue(ec instanceof IBond);
        Assert.assertEquals(bond3, ec);
        electronContainers.next();
        ILonePair lp = (ILonePair)electronContainers.next();
        Assert.assertTrue(lp instanceof ILonePair);
        Assert.assertEquals(lp2, lp);
        electronContainers.remove();
        ISingleElectron se =  (ISingleElectron)electronContainers.next();
        Assert.assertTrue(se instanceof ISingleElectron);
        Assert.assertEquals(se1, se);
        Assert.assertTrue(electronContainers.hasNext());
        se =  (ISingleElectron)electronContainers.next();
        Assert.assertTrue(se instanceof ISingleElectron);
        Assert.assertEquals(se2, se);
        
        Assert.assertFalse(electronContainers.hasNext());
    }
    
    @Test public void testContains_IAtom() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        
        Assert.assertTrue(acetone.contains(c1));
        Assert.assertTrue(acetone.contains(c2));
        Assert.assertTrue(acetone.contains(o));
        Assert.assertTrue(acetone.contains(c3));
    }

    @Test public void testAddLonePair_int() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        acetone.addLonePair(2);
        acetone.addLonePair(2);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(3, acetone.getBondCount());
        Assert.assertEquals(5, acetone.getElectronContainerCount());
    }

    @Test public void testGetMaximumBondOrder_IAtom() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        acetone.addLonePair(2);
        acetone.addLonePair(2);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(IBond.Order.DOUBLE, acetone.getMaximumBondOrder(o));
        Assert.assertEquals(IBond.Order.DOUBLE, acetone.getMaximumBondOrder(c1));
        Assert.assertEquals(IBond.Order.SINGLE, acetone.getMaximumBondOrder(c2));
        Assert.assertEquals(IBond.Order.SINGLE, acetone.getMaximumBondOrder(c3));
    }

    @Test public void testGetMinimumBondOrder_IAtom() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        acetone.addLonePair(2);
        acetone.addLonePair(2);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(IBond.Order.DOUBLE, acetone.getMinimumBondOrder(o));
        Assert.assertEquals(IBond.Order.SINGLE, acetone.getMinimumBondOrder(c1));
        Assert.assertEquals(IBond.Order.SINGLE, acetone.getMinimumBondOrder(c2));
        Assert.assertEquals(IBond.Order.SINGLE, acetone.getMinimumBondOrder(c3));
    }

    @Test public void testRemoveElectronContainer_int() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        acetone.addLonePair(2);
        acetone.addLonePair(2);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(3, acetone.getBondCount());
        Assert.assertEquals(5, acetone.getElectronContainerCount());
        acetone.removeElectronContainer(3);
        Assert.assertEquals(3, acetone.getBondCount());
        Assert.assertEquals(4, acetone.getElectronContainerCount());
        acetone.removeElectronContainer(0); // first bond now
        Assert.assertEquals(2, acetone.getBondCount());
        Assert.assertEquals(3, acetone.getElectronContainerCount());
    }

    @Test public void testRemoveElectronContainer_IElectronContainer() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        ILonePair firstLP = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addElectronContainer(firstLP);
        acetone.addElectronContainer(acetone.getBuilder().newInstance(ILonePair.class,o));
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(3, acetone.getBondCount());
        Assert.assertEquals(5, acetone.getElectronContainerCount());
        acetone.removeElectronContainer(firstLP);
        Assert.assertEquals(3, acetone.getBondCount());
        Assert.assertEquals(4, acetone.getElectronContainerCount());
        acetone.removeElectronContainer(b1); // first bond now
        Assert.assertEquals(2, acetone.getBondCount());
        Assert.assertEquals(3, acetone.getElectronContainerCount());
    }

    @Test public void testAddBond_IBond() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(3, acetone.getBondCount());
        Iterator bonds = acetone.bonds().iterator();
        while (bonds.hasNext()) Assert.assertNotNull(bonds.next());
        Assert.assertEquals(b1, acetone.getBond(0));
        Assert.assertEquals(b2, acetone.getBond(1));
        Assert.assertEquals(b3, acetone.getBond(2));
    }

//    @Test public void testSetElectronContainers_arrayIElectronContainer() {
//        // acetone molecule
//        IMolecule acetone = getNewBuilder().newInstance(IMolecule.class);
//        IAtom c1 = getNewBuilder().newInstance(IAtom.class,"C");
//        IAtom c2 = getNewBuilder().newInstance(IAtom.class,"C");
//        IAtom o = getNewBuilder().newInstance(IAtom.class,"O");
//        IAtom c3 = getNewBuilder().newInstance(IAtom.class,"C");
//        acetone.addAtom(c1);
//        acetone.addAtom(c2);
//        acetone.addAtom(c3);
//        acetone.addAtom(o);
//        IElectronContainer[] electronContainers = new IElectronContainer[3];
//        electronContainers[0] = getNewBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
//        electronContainers[1] = getNewBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
//        electronContainers[2] = getNewBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
//        acetone.setElectronContainers(electronContainers);
//        
//        Assert.assertEquals(3, acetone.getBondCount());
//        org.openscience.cdk.interfaces.IBond[] bonds = acetone.getBonds();
//        for (int i=0; i<bonds.length; i++) {
//            Assert.assertNotNull(bonds[i]);
//        }
//        Assert.assertEquals(electronContainers[0], bonds[0]);
//        Assert.assertEquals(electronContainers[1], bonds[1]);
//        Assert.assertEquals(electronContainers[2], bonds[2]);
//    }

//    @Test public void testAddElectronContainers_IAtomContainer() {
//        // acetone molecule
//        IMolecule acetone = getNewBuilder().newInstance(IMolecule.class);
//        IAtom c1 = getNewBuilder().newInstance(IAtom.class,"C");
//        IAtom c2 = getNewBuilder().newInstance(IAtom.class,"C");
//        IAtom o = getNewBuilder().newInstance(IAtom.class,"O");
//        IAtom c3 = getNewBuilder().newInstance(IAtom.class,"C");
//        acetone.addAtom(c1);
//        acetone.addAtom(c2);
//        acetone.addAtom(c3);
//        acetone.addAtom(o);
//        IElectronContainer[] electronContainers = new IElectronContainer[3];
//        electronContainers[0] = getNewBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
//        electronContainers[1] = getNewBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
//        electronContainers[2] = getNewBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
//        acetone.setElectronContainers(electronContainers);
//        
//        IAtomContainer tested = (IAtomContainer)newChemObject();
//        tested.addBond(getNewBuilder().newInstance(IBond.class,c2, c3));
//        tested.addElectronContainers(acetone);
//        
//        Assert.assertEquals(0, tested.getAtomCount());
//        Assert.assertEquals(4, tested.getBondCount());
//        org.openscience.cdk.interfaces.IBond[] bonds = tested.getBonds();
//        for (int i=0; i<bonds.length; i++) {
//            Assert.assertNotNull(bonds[i]);
//        }
//        Assert.assertEquals(electronContainers[0], bonds[1]);
//        Assert.assertEquals(electronContainers[1], bonds[2]);
//        Assert.assertEquals(electronContainers[2], bonds[3]);
//    }

    @Test public void testAddElectronContainer_IElectronContainer() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        acetone.addAtom(c);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c, o, IBond.Order.DOUBLE);
        acetone.addElectronContainer(b1);
        acetone.addElectronContainer(acetone.getBuilder().newInstance(ILonePair.class,o));
        acetone.addElectronContainer(acetone.getBuilder().newInstance(ISingleElectron.class, c));

        Assert.assertEquals(3, acetone.getElectronContainerCount());
        Assert.assertEquals(1, acetone.getBondCount());
        Assert.assertEquals(1, acetone.getLonePairCount());
    }

    @Test public void testGetSingleElectron_IAtom() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        acetone.addAtom(c);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c, o, IBond.Order.DOUBLE);
        acetone.addBond(b1);
        acetone.addLonePair(acetone.getBuilder().newInstance(ILonePair.class,o));
        ISingleElectron single = acetone.getBuilder().newInstance(ISingleElectron.class, c);
        acetone.addSingleElectron(single);

        Assert.assertEquals(1, acetone.getConnectedSingleElectronsCount(c));
        Assert.assertEquals(single, (ISingleElectron)acetone.getConnectedSingleElectronsList(c).get(0));
    }

    @Test public void testRemoveBond_IAtom_IAtom() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(3, acetone.getBondCount());
        acetone.removeBond(c1, o);
        Assert.assertEquals(2, acetone.getBondCount());
        Assert.assertEquals(b1, acetone.getBond(0));
        Assert.assertEquals(b3, acetone.getBond(1));
    }

    @Test public void testAddBond_int_int_IBond_Order() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        acetone.addBond(0, 1, IBond.Order.SINGLE);
        acetone.addBond(1, 3, IBond.Order.DOUBLE);
        acetone.addBond(1, 2, IBond.Order.SINGLE);
        
        Assert.assertEquals(3, acetone.getBondCount());
        Iterator bonds = acetone.bonds().iterator();
        while (bonds.hasNext()) Assert.assertNotNull(bonds.next());

        Assert.assertEquals(c1, acetone.getBond(0).getAtom(0));
        Assert.assertEquals(c2, acetone.getBond(0).getAtom(1));
        Assert.assertEquals(IBond.Order.SINGLE, acetone.getBond(0).getOrder());
        Assert.assertEquals(c2, acetone.getBond(1).getAtom(0));
        Assert.assertEquals(o, acetone.getBond(1).getAtom(1));
        Assert.assertEquals(IBond.Order.DOUBLE, acetone.getBond(1).getOrder());
        Assert.assertEquals(c2, acetone.getBond(2).getAtom(0));
        Assert.assertEquals(c3, acetone.getBond(2).getAtom(1));
        Assert.assertEquals(IBond.Order.SINGLE, acetone.getBond(2).getOrder());
    }

    @Test public void testAddBond_int_int_IBond_Order_IBond_Stereo() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        acetone.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.UP); // yes this is crap
        acetone.addBond(1, 3, IBond.Order.DOUBLE, IBond.Stereo.DOWN);
        acetone.addBond(1, 2, IBond.Order.SINGLE, IBond.Stereo.NONE);
        
        Assert.assertEquals(3, acetone.getBondCount());
        Iterator bonds = acetone.bonds().iterator();
        while (bonds.hasNext()) Assert.assertNotNull(bonds.next());

        Assert.assertEquals(c1, acetone.getBond(0).getAtom(0));
        Assert.assertEquals(c2, acetone.getBond(0).getAtom(1));
        Assert.assertEquals(IBond.Order.SINGLE, acetone.getBond(0).getOrder());
        Assert.assertEquals(IBond.Stereo.UP, acetone.getBond(0).getStereo());
        Assert.assertEquals(c2, acetone.getBond(1).getAtom(0));
        Assert.assertEquals(o, acetone.getBond(1).getAtom(1));
        Assert.assertEquals(IBond.Order.DOUBLE, acetone.getBond(1).getOrder());
        Assert.assertEquals(IBond.Stereo.DOWN, acetone.getBond(1).getStereo());
        Assert.assertEquals(c2, acetone.getBond(2).getAtom(0));
        Assert.assertEquals(c3, acetone.getBond(2).getAtom(1));
        Assert.assertEquals(IBond.Order.SINGLE, acetone.getBond(2).getOrder());
        Assert.assertEquals(IBond.Stereo.NONE, acetone.getBond(2).getStereo());
    }

    @Test public void testContains_IElectronContainer() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);
        
        Assert.assertTrue(acetone.contains(b1));
        Assert.assertTrue(acetone.contains(b2));
        Assert.assertTrue(acetone.contains(b3));
        Assert.assertTrue(acetone.contains(lp1));
        Assert.assertTrue(acetone.contains(lp2));
    }
    
    @Test public void testGetFirstAtom() {
        IAtomContainer container = (IAtomContainer)newChemObject();
        IAtom c1 = container.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = container.getBuilder().newInstance(IAtom.class,"O");
        IAtom o = container.getBuilder().newInstance(IAtom.class,"H");
        container.addAtom(c1);
        container.addAtom(c2);
        container.addAtom(o);
        
        Assert.assertNotNull(container.getFirstAtom());
        Assert.assertEquals("C", container.getFirstAtom().getSymbol());
    }

    @Test public void testGetLastAtom() {
        IAtomContainer container = (IAtomContainer)newChemObject();
        IAtom c1 = container.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = container.getBuilder().newInstance(IAtom.class,"O");
        IAtom o = container.getBuilder().newInstance(IAtom.class,"H");
        container.addAtom(c1);
        container.addAtom(c2);
        container.addAtom(o);
        
        Assert.assertNotNull(container.getLastAtom());
        Assert.assertEquals("H", container.getLastAtom().getSymbol());
    }
    
    @Test public void testGetAtomNumber_IAtom() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        
        Assert.assertEquals(0, acetone.getAtomNumber(c1));
        Assert.assertEquals(1, acetone.getAtomNumber(c2));
        Assert.assertEquals(2, acetone.getAtomNumber(c3));
        Assert.assertEquals(3, acetone.getAtomNumber(o));
    }
    
    @Test public void testGetBondNumber_IBond() {
        // acetone molecule
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(0, acetone.getBondNumber(b1));
        Assert.assertEquals(1, acetone.getBondNumber(b2));
        Assert.assertEquals(2, acetone.getBondNumber(b3));
        
        // test the default return value
        Assert.assertEquals(-1, acetone.getBondNumber(acetone.getBuilder().newInstance(IBond.class)));
    }
    
    @Test public void testGetBondNumber_IAtom_IAtom() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(0, acetone.getBondNumber(c1, c2));
        Assert.assertEquals(1, acetone.getBondNumber(c1, o));
        Assert.assertEquals(2, acetone.getBondNumber(c1, c3));
    }
    
    @Test public void testGetBond_IAtom_IAtom() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(acetone.getBond(c1, c2), b1);
        Assert.assertEquals(acetone.getBond(c1, o), b2);        
        Assert.assertEquals(acetone.getBond(c1, c3), b3);
        
        // test the default return value
        Assert.assertNull(acetone.getBond(acetone.getBuilder().newInstance(IAtom.class), acetone.getBuilder().newInstance(IAtom.class)));
    }
    
//    @Test public void testGetConnectedAtoms_IAtom() {
//        IMolecule acetone = acetone.getNewBuilder().newInstance(IMolecule.class);
//        IAtom c1 = acetone.getNewBuilder().newInstance(IAtom.class,"C");
//        IAtom c2 = acetone.getNewBuilder().newInstance(IAtom.class,"C");
//        IAtom o = acetone.getNewBuilder().newInstance(IAtom.class,"O");
//        IAtom c3 = acetone.getNewBuilder().newInstance(IAtom.class,"C");
//        acetone.addAtom(c1);
//        acetone.addAtom(c2);
//        acetone.addAtom(c3);
//        acetone.addAtom(o);
//        IBond b1 = acetone.getNewBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
//        IBond b2 = acetone.getNewBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
//        IBond b3 = acetone.getNewBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
//        acetone.addBond(b1);
//        acetone.addBond(b2);
//        acetone.addBond(b3);
//        
//        Assert.assertEquals(3, acetone.getConnectedAtomsList(c1).length);
//        Assert.assertEquals(1, acetone.getConnectedAtoms(c2).length);
//        Assert.assertEquals(1, acetone.getConnectedAtoms(c3).length);
//        Assert.assertEquals(1, acetone.getConnectedAtoms(o).length);
//    }
    
    @Test public void testGetConnectedAtomsList_IAtom() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(3, acetone.getConnectedAtomsList(c1).size());
        Assert.assertEquals(1, acetone.getConnectedAtomsList(c2).size());
        Assert.assertEquals(1, acetone.getConnectedAtomsList(c3).size());
        Assert.assertEquals(1, acetone.getConnectedAtomsList(o).size());
    }
    
    @Test public void testGetConnectedAtomsCount_IAtom() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Assert.assertEquals(3, acetone.getConnectedAtomsCount(c1));
        Assert.assertEquals(1, acetone.getConnectedAtomsCount(c2));
        Assert.assertEquals(1, acetone.getConnectedAtomsCount(c3));
        Assert.assertEquals(1, acetone.getConnectedAtomsCount(o));
    }
    
    @Test public void testGetLonePairCount() {
        IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);
        
        Assert.assertEquals(2, acetone.getLonePairCount());
    }

    @Test public void testGetConnectedLonePairsCount_IAtom() {
        IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);
        
        Assert.assertEquals(2, acetone.getConnectedLonePairsCount(o));
        Assert.assertEquals(0, acetone.getConnectedLonePairsCount(c2));
        Assert.assertEquals(0, acetone.getConnectedLonePairsCount(c3));
        Assert.assertEquals(0, acetone.getConnectedLonePairsCount(c1));
    }

    @Test public void testGetBondOrderSum_IAtom() {
        IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);
        
        Assert.assertEquals(4.0, acetone.getBondOrderSum(c1), 0.00001);
        Assert.assertEquals(1.0, acetone.getBondOrderSum(c2), 0.00001);
        Assert.assertEquals(1.0, acetone.getBondOrderSum(c3), 0.00001);
        Assert.assertEquals(2.0, acetone.getBondOrderSum(o), 0.00001);
    }
    
    @Test public void testGetBondCount_IAtom() {
        IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);
        
        Assert.assertEquals(3, acetone.getConnectedBondsCount(c1));
        Assert.assertEquals(1, acetone.getConnectedBondsCount(c2));
        Assert.assertEquals(1, acetone.getConnectedBondsCount(c3));
        Assert.assertEquals(1, acetone.getConnectedBondsCount(o));
    }
    
    @Test public void testGetBondCount_int() {
        IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class,"C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class,c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);
        
        Assert.assertEquals(3, acetone.getConnectedBondsCount(0));
        Assert.assertEquals(1, acetone.getConnectedBondsCount(1));
        Assert.assertEquals(1, acetone.getConnectedBondsCount(2));
        Assert.assertEquals(1, acetone.getConnectedBondsCount(3));
    }
    
    @Test public void testStereoElements() {
        IAtomContainer container = (IAtomContainer)newChemObject();
        IAtom carbon = container.getBuilder().newInstance(IAtom.class,"C");
        carbon.setID("central");
        IAtom carbon1 = container.getBuilder().newInstance(IAtom.class,"C");
        carbon1.setID("c1");
        IAtom carbon2 = container.getBuilder().newInstance(IAtom.class,"C");
        carbon2.setID("c2");
        IAtom carbon3 = container.getBuilder().newInstance(IAtom.class,"C");
        carbon3.setID("c3");
        IAtom carbon4 = container.getBuilder().newInstance(IAtom.class,"C");
        carbon4.setID("c4");
        int parityInt = 1;
        IAtomParity parity = container.getBuilder().newInstance(
            IAtomParity.class, carbon, carbon1, carbon2, carbon3, carbon4, parityInt
        );
        container.addStereoElement(parity);
        
        Iterator<IStereoElement> stereoElements = container.stereoElements().iterator();
        Assert.assertTrue(stereoElements.hasNext());
        IStereoElement element = stereoElements.next();
        Assert.assertNotNull(element);
        Assert.assertTrue(element instanceof IAtomParity);
        Assert.assertEquals(carbon, ((IAtomParity)element).getAtom());
        Assert.assertFalse(stereoElements.hasNext());
    }

    /** Test for RFC #9 */
    @Test public void testToString() {
        IAtomContainer container = (IAtomContainer)newChemObject();
        String description = container.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        IAtomContainer chemObject = (IAtomContainer)newChemObject();
        chemObject.addListener(listener);
        
        chemObject.addAtom(chemObject.getBuilder().newInstance(IAtom.class));
        Assert.assertTrue(listener.changed);
        
        listener.reset();
        Assert.assertFalse(listener.changed);
        chemObject.addBond(chemObject.getBuilder().newInstance(IBond.class,chemObject.getBuilder().newInstance(IAtom.class), chemObject.getBuilder().newInstance(IAtom.class)));
        Assert.assertTrue(listener.changed);
    }

    private class ChemObjectListenerImpl implements IChemObjectListener {
        private boolean changed;
        
        private ChemObjectListenerImpl() {
            changed = false;
        }
        
        @Test public void stateChanged(IChemObjectChangeEvent e) {
            changed = true;
        }
        
        @Test public void reset() {
            changed = false;
        }
    }

    @Test public void testAddStereoElement_IStereoElement() {
        testStereoElements();
    }
    
    @Test public void testGetConnectedSingleElectronsCount_IAtom() {
        // another rather artifial example
        IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        acetone.addAtom(c);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c, o, IBond.Order.DOUBLE);
        acetone.addBond(b1);
        ISingleElectron single1 = acetone.getBuilder().newInstance(ISingleElectron.class, c);
        ISingleElectron single2 = acetone.getBuilder().newInstance(ISingleElectron.class, c);
        ISingleElectron single3 = acetone.getBuilder().newInstance(ISingleElectron.class, o);
        acetone.addSingleElectron(single1);
        acetone.addSingleElectron(single2);
        acetone.addSingleElectron(single3);

        Assert.assertEquals(2, acetone.getConnectedSingleElectronsCount(c));
        Assert.assertEquals(1, acetone.getConnectedSingleElectronsCount(o));
        Assert.assertEquals(single1, (ISingleElectron)acetone.getConnectedSingleElectronsList(c).get(0));
        Assert.assertEquals(single2, (ISingleElectron)acetone.getConnectedSingleElectronsList(c).get(1));
        Assert.assertEquals(single3, (ISingleElectron)acetone.getConnectedSingleElectronsList(o).get(0));
        
        Assert.assertEquals(2, acetone.getConnectedSingleElectronsCount(c));
        Assert.assertEquals(1, acetone.getConnectedSingleElectronsCount(o));
    }
    
    @Test public void testAddLonePair_ILonePair() {
    	 IAtomContainer acetone = (IAtomContainer)newChemObject();
         IAtom c = acetone.getBuilder().newInstance(IAtom.class,"C");
         IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
         acetone.addAtom(c);
         acetone.addAtom(o);
         IBond b1 = acetone.getBuilder().newInstance(IBond.class,c, o, IBond.Order.DOUBLE);
         acetone.addBond(b1);
         ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class,o);
         ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class,o);
         acetone.addLonePair(lp1);
         acetone.addLonePair(lp2);
         Assert.assertEquals(2, acetone.getConnectedLonePairsCount(o));
         Assert.assertEquals(0, acetone.getConnectedLonePairsCount(c));
    }
    
    @Test public void testAddSingleElectron_ISingleElectron() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        acetone.addAtom(c);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c, o, IBond.Order.DOUBLE);
        acetone.addBond(b1);
        ISingleElectron single1 = acetone.getBuilder().newInstance(ISingleElectron.class, c);
        ISingleElectron single2 = acetone.getBuilder().newInstance(ISingleElectron.class, c);
        ISingleElectron single3 = acetone.getBuilder().newInstance(ISingleElectron.class, o);
        acetone.addSingleElectron(single1);
        acetone.addSingleElectron(single2);
        acetone.addSingleElectron(single3);
        Assert.assertEquals(single1, (ISingleElectron)acetone.getConnectedSingleElectronsList(c).get(0));
        Assert.assertEquals(single2, (ISingleElectron)acetone.getConnectedSingleElectronsList(c).get(1));
        Assert.assertEquals(single3, (ISingleElectron)acetone.getConnectedSingleElectronsList(o).get(0));
    }
    
    @Test public void testRemoveBond_int() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
    	IAtom c = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        acetone.addAtom(c);
        acetone.addAtom(o);
        IBond b = acetone.getBuilder().newInstance(IBond.class,c, o, IBond.Order.DOUBLE);
        acetone.addBond(b);
        acetone.addAtom(c1);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c, c1, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addAtom(c2);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c, c2, IBond.Order.SINGLE);
        acetone.addBond(b2);
        acetone.removeBond(2);
        Assert.assertEquals(2, acetone.getBondCount());
        Assert.assertEquals(b, acetone.getBond(0));
        Assert.assertEquals(b1, acetone.getBond(1));
        acetone.removeBond(0);
        Assert.assertEquals(1, acetone.getBondCount());
        Assert.assertEquals(b1, acetone.getBond(0));
    }
    
    @Test public void testContains_IBond() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
        IAtom c = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        acetone.addAtom(c);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c, o, IBond.Order.DOUBLE);
        acetone.addBond(b1);
        IBond falseBond = acetone.getBuilder().newInstance(IBond.class);
        Assert.assertTrue(acetone.contains(b1));
        Assert.assertFalse(acetone.contains(falseBond));
    }
    
    @Test public void testAddSingleElectron_int() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        mol.addSingleElectron(1);
        mol.addSingleElectron(1);
        Assert.assertEquals(2, mol.getSingleElectronCount());
        Assert.assertNotNull(mol.getSingleElectron(1));
        Iterator<ISingleElectron> singles = mol.singleElectrons().iterator();
        ISingleElectron singleElectron = singles.next();
        Assert.assertNotNull(singleElectron);
        Assert.assertEquals(c1, singleElectron.getAtom());
        Assert.assertTrue(singleElectron.contains(c1));
        singleElectron = singles.next();
        Assert.assertNotNull(singleElectron);
        Assert.assertEquals(c1, singleElectron.getAtom());
        Assert.assertTrue(singleElectron.contains(c1));
    }
    
    @Test public void testGetConnectedSingleElectronsList_IAtom() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        mol.addSingleElectron(1);
        mol.addSingleElectron(1);
        List list = mol.getConnectedSingleElectronsList(c1);
        Assert.assertEquals(2, list.size());
    }
    
    @Test public void testRemoveBond_IBond() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        Assert.assertEquals(1, mol.getBondCount());
        IBond bond = mol.getBond(0);
        mol.removeBond(bond);
        Assert.assertEquals(0, mol.getBondCount());
    }
    
    @Test public void testGetConnectedBondsCount_IAtom() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
    	IAtom c = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        acetone.addAtom(c);
        acetone.addAtom(o);
        IBond b = acetone.getBuilder().newInstance(IBond.class,c, o, IBond.Order.DOUBLE);
        acetone.addBond(b);
        acetone.addAtom(c1);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c, c1, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addAtom(c2);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c, c2, IBond.Order.SINGLE);
        acetone.addBond(b2);
        Assert.assertEquals(1, acetone.getConnectedBondsCount(o));
        Assert.assertEquals(3, acetone.getConnectedBondsCount(c));
        Assert.assertEquals(1, acetone.getConnectedBondsCount(c1));
        Assert.assertEquals(1, acetone.getConnectedBondsCount(c2));
    }
    
    @Test public void testGetConnectedBondsCount_int() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
    	IAtom c = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        acetone.addAtom(c);
        acetone.addAtom(o);
        IBond b = acetone.getBuilder().newInstance(IBond.class,c, o, IBond.Order.DOUBLE);
        acetone.addBond(b);
        acetone.addAtom(c1);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c, c1, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addAtom(c2);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c, c2, IBond.Order.SINGLE);
        acetone.addBond(b2);
        Assert.assertEquals(1, acetone.getConnectedBondsCount(1));
        Assert.assertEquals(3, acetone.getConnectedBondsCount(0));
        Assert.assertEquals(1, acetone.getConnectedBondsCount(2));
        Assert.assertEquals(1, acetone.getConnectedBondsCount(3));
    }
    
    @Test public void testSetBonds_arrayIBond() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
    	IAtom c = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        acetone.addAtom(c);
        acetone.addAtom(o);
        IBond b = acetone.getBuilder().newInstance(IBond.class,c, o, IBond.Order.DOUBLE);
        //acetone.addBond(b);
        acetone.addAtom(c1);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c, c1, IBond.Order.SINGLE);
        //acetone.addBond(b1);
        acetone.addAtom(c2);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c, c2, IBond.Order.SINGLE);
        //acetone.addBond(b2);
        IBond[] bonds = new IBond[3];
        bonds[0] = b;
        bonds[1] = b1;
        bonds[2] = b2;
        acetone.setBonds(bonds);
        Assert.assertEquals(3, acetone.getBondCount());
        Assert.assertEquals(acetone.getBond(2), b2);
    }
    
    @Test public void testGetLonePair_int() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        mol.addLonePair(1);
        ILonePair lp = mol.getBuilder().newInstance(ILonePair.class,c);
        mol.addLonePair(lp);
        Assert.assertEquals(lp, mol.getLonePair(1));
    }
    
    @Test public void testGetSingleElectron_int() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        mol.addSingleElectron(1);
        ISingleElectron se = mol.getBuilder().newInstance(ISingleElectron.class, c);
        mol.addSingleElectron(se);
        Assert.assertEquals(se, mol.getSingleElectron(1));
    }
    
    @Test public void testGetLonePairNumber_ILonePair() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        mol.addLonePair(1);
        ILonePair lp = mol.getBuilder().newInstance(ILonePair.class,c);
        mol.addLonePair(lp);
        Assert.assertEquals(1, mol.getLonePairNumber(lp));
    }
    
    @Test public void testGetSingleElectronNumber_ISingleElectron() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        mol.addSingleElectron(1);
        ISingleElectron se = mol.getBuilder().newInstance(ISingleElectron.class, c);
        mol.addSingleElectron(se);
        Assert.assertEquals(1, mol.getSingleElectronNumber(se));
    }
    
    @Test public void testGetElectronContainer_int() {
    	IAtomContainer acetone = (IAtomContainer)newChemObject();
    	IAtom c = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class,"C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class,"O");
        acetone.addAtom(c);
        acetone.addAtom(o);
        IBond b = acetone.getBuilder().newInstance(IBond.class,c, o, IBond.Order.DOUBLE);
        acetone.addBond(b);
        acetone.addAtom(c1);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class,c, c1, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addAtom(c2);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class,c, c2, IBond.Order.SINGLE);
        acetone.addBond(b2);
        acetone.addLonePair(1);
        acetone.addLonePair(1);
        Assert.assertTrue(acetone.getElectronContainer(2) instanceof IBond);
        Assert.assertTrue(acetone.getElectronContainer(4) instanceof ILonePair);
    }
    
    @Test public void testGetSingleElectronCount() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        mol.addSingleElectron(1);
        mol.addSingleElectron(1);
        Assert.assertEquals(2, mol.getSingleElectronCount());
    }
    
    @Test public void testRemoveLonePair_int() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        mol.addLonePair(1);
        ILonePair lp = mol.getBuilder().newInstance(ILonePair.class,c);
        mol.addLonePair(lp);
        mol.removeLonePair(0);
        Assert.assertEquals(1, mol.getLonePairCount());
        Assert.assertEquals(lp, mol.getLonePair(0));
    }
    
    @Test public void testRemoveLonePair_ILonePair() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        ILonePair lp = mol.getBuilder().newInstance(ILonePair.class,c1);
        mol.addLonePair(lp);
        ILonePair lp1 = mol.getBuilder().newInstance(ILonePair.class,c);
        mol.addLonePair(lp1);
        mol.removeLonePair(lp);
        Assert.assertEquals(1, mol.getLonePairCount());
        Assert.assertEquals(lp1, mol.getLonePair(0));
    }
    
    @Test public void testRemoveSingleElectron_int() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        mol.addSingleElectron(1);
        ISingleElectron se = mol.getBuilder().newInstance(ISingleElectron.class, c);
        mol.addSingleElectron(se);
        mol.removeSingleElectron(0);
        Assert.assertEquals(1, mol.getSingleElectronCount());
        Assert.assertEquals(se, mol.getSingleElectron(0));
    }
    
    @Test public void testRemoveSingleElectron_ISingleElectron() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        ISingleElectron se1 = mol.getBuilder().newInstance(ISingleElectron.class, c1);
        mol.addSingleElectron(se1);
        ISingleElectron se = mol.getBuilder().newInstance(ISingleElectron.class, c);
        mol.addSingleElectron(se);
        Assert.assertEquals(2, mol.getSingleElectronCount());
        mol.removeSingleElectron(se);
        Assert.assertEquals(1, mol.getSingleElectronCount());
        Assert.assertEquals(se1,mol.getSingleElectron(0));
    }
    
    @Test public void testContains_ILonePair() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        ILonePair lp = mol.getBuilder().newInstance(ILonePair.class,c1);
        mol.addLonePair(lp);
        ILonePair lp1 = mol.getBuilder().newInstance(ILonePair.class,c);
        Assert.assertTrue(mol.contains(lp));
        Assert.assertFalse(mol.contains(lp1));
    }
    
    @Test public void testContains_ISingleElectron() {
    	IAtomContainer mol = (IAtomContainer)newChemObject();
        IAtom c = mol.getBuilder().newInstance(IAtom.class,"C");
        IAtom c1 = mol.getBuilder().newInstance(IAtom.class,"C");
        mol.addAtom(c);
        mol.addAtom(c1);
        ISingleElectron se = mol.getBuilder().newInstance(ISingleElectron.class, c1);
        mol.addSingleElectron(se);
        ISingleElectron se1 = mol.getBuilder().newInstance(ISingleElectron.class, c1);
        Assert.assertTrue(mol.contains(se));
        Assert.assertFalse(mol.contains(se1));
    }
}
