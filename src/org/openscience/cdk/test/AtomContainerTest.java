/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.test;

import java.util.Enumeration;

import javax.vecmath.Point2d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.*;
import org.openscience.cdk.event.ChemObjectChangeEvent;

/**
 * Checks the funcitonality of the AtomContainer.
 *
 * @cdk.module test
 */
public class AtomContainerTest extends TestCase {

    public AtomContainerTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(AtomContainerTest.class);
    }

    public void testSetAtoms_arrayAtom() {
        Atom[] atoms = new Atom[4];
        atoms[0] = new Atom("C");
        atoms[1] = new Atom("C");
        atoms[2] = new Atom("C");
        atoms[3] = new Atom("O");
        AtomContainer ac = new AtomContainer();
        ac.setAtoms(atoms);
        
        assertEquals(4, ac.getAtomCount());
        assertEquals(4, ac.getAtoms().length);
    }

    /**
     * Only test wether the atoms are correctly cloned.
     */
	public void testClone() {
        AtomContainer molecule = new AtomContainer();
        Object clone = molecule.clone();
        assertTrue(clone instanceof AtomContainer);
    }    
        
    public void testClone_Atom() {
		AtomContainer molecule = new AtomContainer();
		molecule.addAtom(new Atom("C")); // 1
		molecule.addAtom(new Atom("C")); // 2
		molecule.addAtom(new Atom("C")); // 3
		molecule.addAtom(new Atom("C")); // 4

		AtomContainer clonedMol = (AtomContainer)molecule.clone();
		assertEquals(molecule.getAtomCount(), clonedMol.getAtomCount());
		for (int f = 0; f < molecule.getAtomCount(); f++) {
			for (int g = 0; g < clonedMol.getAtomCount(); g++) {
				assertNotNull(molecule.getAtomAt(f));
				assertNotNull(clonedMol.getAtomAt(g));
				assertNotSame(molecule.getAtomAt(f), clonedMol.getAtomAt(g));
			}
		}        
    }
    
	public void testClone_Atom2() {
		Molecule molecule = new Molecule();
        Atom carbon = new Atom("C");
        carbon.setPoint2d(new Point2d(2, 4));
		molecule.addAtom(carbon); // 1

        // test cloning of Atoms
		Molecule clonedMol = (Molecule)molecule.clone();
        carbon.setPoint2d(new Point2d(3, 1));
		assertEquals(clonedMol.getAtomAt(0).getX2d(), 2.0, 0.001);
	}

    public void testClone_Bond() {
		AtomContainer molecule = new AtomContainer();
		molecule.addAtom(new Atom("C")); // 1
		molecule.addAtom(new Atom("C")); // 2
		molecule.addAtom(new Atom("C")); // 3
		molecule.addAtom(new Atom("C")); // 4

		molecule.addBond(0, 1, 2.0); // 1
		molecule.addBond(1, 2, 1.0); // 2
		molecule.addBond(2, 3, 1.0); // 3
		AtomContainer clonedMol = (AtomContainer)molecule.clone();
		assertEquals(molecule.getElectronContainerCount(), clonedMol.getElectronContainerCount());
		for (int f = 0; f < molecule.getElectronContainerCount(); f++) {
			for (int g = 0; g < clonedMol.getElectronContainerCount(); g++) {
				assertNotNull(molecule.getElectronContainerAt(f));
				assertNotNull(clonedMol.getElectronContainerAt(g));
				assertNotSame(molecule.getElectronContainerAt(f), clonedMol.getElectronContainerAt(g));
			}
		}
	}

    public void testClone_Bond2() {
		AtomContainer molecule = new AtomContainer();
        Atom atom1 = new Atom("C");
        Atom atom2 = new Atom("C");
		molecule.addAtom(atom1); // 1
		molecule.addAtom(atom2); // 2
		molecule.addBond(new Bond(atom1, atom2, 2.0)); // 1
        
        // test cloning of atoms in bonds
		AtomContainer clonedMol = (AtomContainer)molecule.clone();
        assertNotSame(atom1, clonedMol.getBondAt(0).getAtomAt(0));
        assertNotSame(atom2, clonedMol.getBondAt(0).getAtomAt(1));
	}

    public void testClone_Bond3() {
		AtomContainer molecule = new AtomContainer();
        Atom atom1 = new Atom("C");
        Atom atom2 = new Atom("C");
		molecule.addAtom(atom1); // 1
		molecule.addAtom(atom2); // 2
		molecule.addBond(new Bond(atom1, atom2, 2.0)); // 1
        
        // test that cloned bonds contain atoms from cloned atomcontainer
		AtomContainer clonedMol = (AtomContainer)molecule.clone();
        assertTrue(clonedMol.contains(clonedMol.getBondAt(0).getAtomAt(0)));
        assertTrue(clonedMol.contains(clonedMol.getBondAt(0).getAtomAt(1)));
	}

    public void testGetConnectedElectronContainers_Atom() {
        // acetone molecule
        Molecule acetone = new Molecule();
        
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(1, acetone.getConnectedElectronContainers(o).length);
        assertEquals(3, acetone.getConnectedElectronContainers(c1).length);
        assertEquals(1, acetone.getConnectedElectronContainers(c2).length);
        assertEquals(1, acetone.getConnectedElectronContainers(c3).length);
        
        // add lone pairs on oxygen
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);

        assertEquals(3, acetone.getConnectedElectronContainers(o).length);
        assertEquals(3, acetone.getConnectedElectronContainers(c1).length);
        assertEquals(1, acetone.getConnectedElectronContainers(c2).length);
        assertEquals(1, acetone.getConnectedElectronContainers(c3).length);

    }

    public void testGetConnectedBonds_Atom() {
        // acetone molecule
        Molecule acetone = new Molecule();
        
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(1, acetone.getConnectedBonds(o).length);
        assertEquals(3, acetone.getConnectedBonds(c1).length);
        assertEquals(1, acetone.getConnectedBonds(c2).length);
        assertEquals(1, acetone.getConnectedBonds(c3).length);
        
        // add lone pairs on oxygen
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);

        assertEquals(1, acetone.getConnectedBonds(o).length);
        assertEquals(3, acetone.getConnectedBonds(c1).length);
        assertEquals(1, acetone.getConnectedBonds(c2).length);
        assertEquals(1, acetone.getConnectedBonds(c3).length);
    }

    public void testGetConnectedBondsVector_Atom() {
        // acetone molecule
        Molecule acetone = new Molecule();
        
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(1, acetone.getConnectedBondsVector(o).size());
        assertEquals(3, acetone.getConnectedBondsVector(c1).size());
        assertEquals(1, acetone.getConnectedBondsVector(c2).size());
        assertEquals(1, acetone.getConnectedBondsVector(c3).size());
        
        // add lone pairs on oxygen
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);

        assertEquals(1, acetone.getConnectedBondsVector(o).size());
        assertEquals(3, acetone.getConnectedBondsVector(c1).size());
        assertEquals(1, acetone.getConnectedBondsVector(c2).size());
        assertEquals(1, acetone.getConnectedBondsVector(c3).size());
    }

    public void testGetLonePairs_Atom() {
        // acetone molecule
        Molecule acetone = new Molecule();
        
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(0, acetone.getLonePairs(o).length);
        assertEquals(0, acetone.getLonePairs(c1).length);
        assertEquals(0, acetone.getLonePairs(c2).length);
        assertEquals(0, acetone.getLonePairs(c3).length);

        // add lone pairs on oxygen
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);

        assertEquals(2, acetone.getLonePairs(o).length);
        assertEquals(0, acetone.getLonePairs(c1).length);
        assertEquals(0, acetone.getLonePairs(c2).length);
        assertEquals(0, acetone.getLonePairs(c3).length);

    }

    
    public void testRemoveAtomAndConnectedElectronContainers_Atom() {
        // acetone molecule
        Molecule acetone = new Molecule();
        
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);
        
        // remove the oxygen
        acetone.removeAtomAndConnectedElectronContainers(o);
        assertEquals(3, acetone.getAtomCount());
        assertEquals(2, acetone.getBondCount());
        assertEquals(0, acetone.getLonePairCount());
    }

    public void testGetElectronContainerAt_int() {
        // acetone molecule
        Molecule acetone = new Molecule();
        
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);
        
		for (int i = 0; i < acetone.getElectronContainerCount(); i++) {
            try {
                ElectronContainer ec = acetone.getElectronContainerAt(i);
                if (ec == null) {
                    fail("ElectronContainer is unexpectedly null!");
                }
            } catch (Exception e) {
                fail();
            }
		}
    }
    
    public void testGetAtomCount() {
        // acetone molecule
        Molecule acetone = new Molecule();
        assertEquals(0, acetone.getAtomCount());
        
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        
        assertEquals(4, acetone.getAtomCount());
    }
    
    public void testGetBondCount() {
        // acetone molecule
        Molecule acetone = new Molecule();
        assertEquals(0, acetone.getBondCount());
        
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(3, acetone.getBondCount());
    }
    
    public void testAtomContainer_int_int() {
        // create an empty container with predefined
        // array lengths
        AtomContainer ac = new AtomContainer(5,6);
        
        assertEquals(0, ac.getAtoms().length);
        assertEquals(0, ac.getElectronContainers().length);
        
        // test wether the ElectronContainer is correctly initialized
        ac.addBond(new Bond(new Atom("C"), new Atom("C"), 2));
        ac.addElectronContainer(new LonePair(new Atom("N")));
    }

    public void testAtomContainer() {
        // create an empty container with in the constructor defined array lengths
        AtomContainer container = new AtomContainer();
        
        assertEquals(0, container.getAtoms().length);
        assertEquals(0, container.getElectronContainers().length);
        
        // test wether the ElectronContainer is correctly initialized
        container.addBond(new Bond(new Atom("C"), new Atom("C"), 2));
        container.addElectronContainer(new LonePair(new Atom("N")));
    }

    public void testAtomContainer_AtomContainer() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        AtomContainer container = new AtomContainer(acetone);
        assertEquals(4, container.getAtomCount());
        assertEquals(3, container.getBondCount());
    }
    
    public void testAdd_AtomContainer() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        AtomContainer container = new AtomContainer();
        container.add(acetone);
        assertEquals(4, container.getAtomCount());
        assertEquals(3, container.getBondCount());
    }
    
    public void testRemove_AtomContainer() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        AtomContainer container = new AtomContainer();
        container.add(acetone);
        assertEquals(4, container.getAtomCount());
        assertEquals(3, container.getBondCount());
        container.remove((AtomContainer)acetone.clone());
        assertEquals(4, container.getAtomCount());
        assertEquals(3, container.getBondCount());
        container.remove(acetone);
        assertEquals(0, container.getAtomCount());
        assertEquals(0, container.getBondCount());
    }
    
    public void testRemoveAllElements() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        AtomContainer container = new AtomContainer();
        container.add(acetone);
        assertEquals(4, container.getAtomCount());
        assertEquals(3, container.getBondCount());
        container.removeAllElements();
        assertEquals(0, container.getAtomCount());
        assertEquals(0, container.getBondCount());
    }
    
    public void testRemoveAtom_int() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);

        assertEquals(4, acetone.getAtomCount());
        acetone.removeAtom(1);
        assertEquals(3, acetone.getAtomCount());
        assertEquals(c1, acetone.getAtomAt(0));
        assertEquals(c3, acetone.getAtomAt(1));
        assertEquals(o, acetone.getAtomAt(2));
    }
    
    public void testRemoveAtom_Atom() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);

        assertEquals(4, acetone.getAtomCount());
        acetone.removeAtom(c3);
        assertEquals(3, acetone.getAtomCount());
        assertEquals(c1, acetone.getAtomAt(0));
        assertEquals(c2, acetone.getAtomAt(1));
        assertEquals(o, acetone.getAtomAt(2));
    }
    
    public void testSetAtomAt_int_Atom() {
        AtomContainer container = new AtomContainer();
        Atom c = new Atom("C");
        container.setAtomAt(0, c);
        
        assertNotNull(container.getAtomAt(0));
        assertEquals("C", container.getAtomAt(0).getSymbol());
    }
    
    public void testGetIntersection_AtomContainer() {
        Atom c1 = new Atom("C");
        Atom o = new Atom("O");
        Atom c2 = new Atom("C");
        Atom c3 = new Atom("C");
        
        Bond b1 = new Bond(c1, o);
        Bond b2 = new Bond(o, c2);
        Bond b3 = new Bond(c2, c3);
        
        AtomContainer container1 = new AtomContainer();
        container1.addAtom(c1);
        container1.addAtom(o);
        container1.addAtom(c2);
        container1.addBond(b1);
        container1.addBond(b2);
        AtomContainer container2 = new AtomContainer();
        container2.addAtom(o);
        container2.addAtom(c3);
        container2.addAtom(c2);
        container2.addBond(b3);
        container2.addBond(b2);

        AtomContainer intersection = container1.getIntersection(container2);
        assertEquals(2, intersection.getAtomCount());
        assertEquals(1, intersection.getBondCount());
        assertTrue(intersection.contains(b2));
        assertTrue(intersection.contains(o));
        assertTrue(intersection.contains(c2));
    }
    
    public void testGetAtomAt_int() {
        AtomContainer acetone = new AtomContainer();
        
        Atom c = new Atom("C");
        Atom n = new Atom("N");
        Atom o = new Atom("O");
        Atom s = new Atom("S");
        acetone.addAtom(c);
        acetone.addAtom(n);
        acetone.addAtom(o);
        acetone.addAtom(s);
        
        Atom a1 = acetone.getAtomAt(0);
        assertNotNull(a1);
        assertEquals("C", a1.getSymbol());
        Atom a2 = acetone.getAtomAt(1);
        assertNotNull(a2);
        assertEquals("N", a2.getSymbol());
        Atom a3 = acetone.getAtomAt(2);
        assertNotNull(a3);
        assertEquals("O", a3.getSymbol());
        Atom a4 = acetone.getAtomAt(3);
        assertNotNull(a4);
        assertEquals("S", a4.getSymbol());
    }
    
    public void testGetBondAt_int() {
        // acetone molecule
        Molecule acetone = new Molecule();
        assertEquals(0, acetone.getBondCount());
        
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,3.0);
        Bond b2 = new Bond(c1, o, 2.0);
        Bond b3 = new Bond(c1, c3,1.0);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(3.0, acetone.getBondAt(0).getOrder(), 0.00001);
        assertEquals(2.0, acetone.getBondAt(1).getOrder(), 0.00001);
        assertEquals(1.0, acetone.getBondAt(2).getOrder(), 0.00001);
    }
    
    public void testSetElectronContainerAt_int_ElectronContainer() {
        AtomContainer container = new AtomContainer();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        container.addAtom(c1);
        container.addAtom(c2);
        Bond b = new Bond(c1, c2, 3);
        container.setElectronContainerAt(3, b);
        
        assertTrue(container.getElectronContainerAt(3) instanceof Bond);
        Bond bond = (Bond)container.getElectronContainerAt(3);
        assertEquals(3.0, bond.getOrder(), 0.00001);
    }
    
    public void testGetElectronContainerCount() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);
        
        assertEquals(3, acetone.getBondCount());
        assertEquals(2, acetone.getLonePairCount());
        assertEquals(5, acetone.getElectronContainerCount());
    }
    
    public void testRemoveAllBonds() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(3, acetone.getBondCount());
	
	acetone.removeAllBonds();
	assertEquals(0, acetone.getBondCount());
    }
    
    public void testRemoveAllElectronContainers() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(3, acetone.getElectronContainerCount());
	
	acetone.removeAllBonds();
        assertEquals(0, acetone.getElectronContainerCount());
    }
    
    public void testSetElectronContainerCount_int() {
        AtomContainer container = new AtomContainer();
        container.setElectronContainerCount(2);
        
        assertEquals(2, container.getElectronContainerCount());
    }
    
    public void testSetAtomCount_int() {
        AtomContainer container = new AtomContainer();
        container.setAtomCount(2);
        
        assertEquals(2, container.getAtomCount());
    }
    
    public void testGetAtoms() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        
        assertEquals(4, acetone.getAtoms().length);
    }
    
    public void testAddAtom_Atom() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        
        Enumeration atomEnum = acetone.atoms();
        int counter = 0;
        while (atomEnum.hasMoreElements()) {
            atomEnum.nextElement();
            counter++;
        }
        assertEquals(4, counter);
    }

    public void testAtoms() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        
        Enumeration atomEnum = acetone.atoms();
        assertNotNull(atomEnum);
        Atom next = (Atom)atomEnum.nextElement();
        assertTrue(next instanceof Atom);
        assertEquals(c1, next);
        next = (Atom)atomEnum.nextElement();
        assertTrue(next instanceof Atom);
        assertEquals(c2, next);
        next = (Atom)atomEnum.nextElement();
        assertTrue(next instanceof Atom);
        assertEquals(c3, next);
        next = (Atom)atomEnum.nextElement();
        assertTrue(next instanceof Atom);
        assertEquals(o, next);
        assertNull(atomEnum.nextElement());
    }

    public void testContains_Atom() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        
        assertTrue(acetone.contains(c1));
        assertTrue(acetone.contains(c2));
        assertTrue(acetone.contains(o));
        assertTrue(acetone.contains(c3));
    }

    public void testAddLonePair_int() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        acetone.addLonePair(2);
        acetone.addLonePair(2);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(3, acetone.getBondCount());
        assertEquals(5, acetone.getElectronContainerCount());
    }

    public void testGetMaximumBondOrder_Atom() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        acetone.addLonePair(2);
        acetone.addLonePair(2);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(2.0, acetone.getMaximumBondOrder(o) , 0.0001);
        assertEquals(2.0, acetone.getMaximumBondOrder(c1), 0.0001);
        assertEquals(1.0, acetone.getMaximumBondOrder(c2), 0.0001);
        assertEquals(1.0, acetone.getMaximumBondOrder(c3), 0.0001);
    }

    public void testGetMinimumBondOrder_Atom() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        acetone.addLonePair(2);
        acetone.addLonePair(2);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(2.0, acetone.getMinimumBondOrder(o) , 0.0001);
        assertEquals(1.0, acetone.getMinimumBondOrder(c1), 0.0001);
        assertEquals(1.0, acetone.getMinimumBondOrder(c2), 0.0001);
        assertEquals(1.0, acetone.getMinimumBondOrder(c3), 0.0001);
    }

    public void testRemoveElectronContainer_int() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        acetone.addLonePair(2);
        acetone.addLonePair(2);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(3, acetone.getBondCount());
        assertEquals(5, acetone.getElectronContainerCount());
        acetone.removeElectronContainer(0);
        assertEquals(3, acetone.getBondCount());
        assertEquals(4, acetone.getElectronContainerCount());
        acetone.removeElectronContainer(1); // first bond now
        assertEquals(2, acetone.getBondCount());
        assertEquals(3, acetone.getElectronContainerCount());
    }

    public void testRemoveElectronContainer_ElectronContainer() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        LonePair firstLP = new LonePair(o);
        acetone.addElectronContainer(firstLP);
        acetone.addElectronContainer(new LonePair(o));
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(3, acetone.getBondCount());
        assertEquals(5, acetone.getElectronContainerCount());
        acetone.removeElectronContainer(firstLP);
        assertEquals(3, acetone.getBondCount());
        assertEquals(4, acetone.getElectronContainerCount());
        acetone.removeElectronContainer(b1); // first bond now
        assertEquals(2, acetone.getBondCount());
        assertEquals(3, acetone.getElectronContainerCount());
    }

    public void testAddBond_Bond() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(3, acetone.getBondCount());
        Bond[] bonds = acetone.getBonds();
        for (int i=0; i<bonds.length; i++) {
            assertNotNull(bonds[i]);
        }
        assertEquals(b1, bonds[0]);
        assertEquals(b2, bonds[1]);
        assertEquals(b3, bonds[2]);
    }

    public void testSetElectronContainers_arrayElectronContainer() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        ElectronContainer[] electronContainers = new ElectronContainer[3];
        electronContainers[0] = new Bond(c1, c2,1);
        electronContainers[1] = new Bond(c1, o, 2);
        electronContainers[2] = new Bond(c1, c3,1);
        acetone.setElectronContainers(electronContainers);
        
        assertEquals(3, acetone.getBondCount());
        Bond[] bonds = acetone.getBonds();
        for (int i=0; i<bonds.length; i++) {
            assertNotNull(bonds[i]);
        }
        assertEquals(electronContainers[0], bonds[0]);
        assertEquals(electronContainers[1], bonds[1]);
        assertEquals(electronContainers[2], bonds[2]);
    }

    public void testAddElectronContainers_AtomContainer() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        ElectronContainer[] electronContainers = new ElectronContainer[3];
        electronContainers[0] = new Bond(c1, c2,1);
        electronContainers[1] = new Bond(c1, o, 2);
        electronContainers[2] = new Bond(c1, c3,1);
        acetone.setElectronContainers(electronContainers);
        
        AtomContainer tested = new AtomContainer();
        tested.addBond(new Bond(c2, c3));
        tested.addElectronContainers(acetone);
        
        assertEquals(0, tested.getAtomCount());
        assertEquals(4, tested.getBondCount());
        Bond[] bonds = tested.getBonds();
        for (int i=0; i<bonds.length; i++) {
            assertNotNull(bonds[i]);
        }
        assertEquals(electronContainers[0], bonds[1]);
        assertEquals(electronContainers[1], bonds[2]);
        assertEquals(electronContainers[2], bonds[3]);
    }

    public void testAddElectronContainer_ElectronContainer() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        acetone.addAtom(c);
        acetone.addAtom(o);
        Bond b1 = new Bond(c, o, 2.0);
        acetone.addElectronContainer(b1);
        acetone.addElectronContainer(new LonePair(o));
        acetone.addElectronContainer(new SingleElectron(c));

        assertEquals(3, acetone.getElectronContainerCount());
        assertEquals(1, acetone.getBondCount());
        assertEquals(1, acetone.getLonePairCount());
    }

    public void testGetSingleElectron_Atom() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        acetone.addAtom(c);
        acetone.addAtom(o);
        Bond b1 = new Bond(c, o, 2.0);
        acetone.addElectronContainer(b1);
        acetone.addElectronContainer(new LonePair(o));
        SingleElectron single = new SingleElectron(c);
        acetone.addElectronContainer(single);

        assertEquals(1, acetone.getSingleElectron(c).length);
        assertEquals(single, acetone.getSingleElectron(c)[0]);
    }

    public void testRemoveBond_Atom_Atom() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(3, acetone.getBondCount());
        acetone.removeBond(c1, o);
        assertEquals(2, acetone.getBondCount());
        assertEquals(b1, acetone.getBondAt(0));
        assertEquals(b3, acetone.getBondAt(1));
    }

    public void testAddBond_int_int_double() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        acetone.addBond(0, 1, 1);
        acetone.addBond(1, 3, 2);
        acetone.addBond(1, 2, 1);
        
        assertEquals(3, acetone.getBondCount());
        Bond[] bonds = acetone.getBonds();
        for (int i=0; i<bonds.length; i++) {
            assertNotNull(bonds[i]);
        }
        assertEquals(c1, bonds[0].getAtomAt(0));
        assertEquals(c2, bonds[0].getAtomAt(1));
        assertEquals(1.0, bonds[0].getOrder(), 0.001);
        assertEquals(c2, bonds[1].getAtomAt(0));
        assertEquals(o, bonds[1].getAtomAt(1));
        assertEquals(2.0, bonds[1].getOrder(), 0.001);
        assertEquals(c2, bonds[2].getAtomAt(0));
        assertEquals(c3, bonds[2].getAtomAt(1));
        assertEquals(1.0, bonds[2].getOrder(), 0.001);
    }

    public void testAddBond_int_int_double_int() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        acetone.addBond(0, 1, 1, CDKConstants.STEREO_BOND_UP); // yes this is crap
        acetone.addBond(1, 3, 2, CDKConstants.STEREO_BOND_DOWN);
        acetone.addBond(1, 2, 1, CDKConstants.STEREO_BOND_NONE);
        
        assertEquals(3, acetone.getBondCount());
        Bond[] bonds = acetone.getBonds();
        for (int i=0; i<bonds.length; i++) {
            assertNotNull(bonds[i]);
        }
        assertEquals(c1, bonds[0].getAtomAt(0));
        assertEquals(c2, bonds[0].getAtomAt(1));
        assertEquals(1.0, bonds[0].getOrder(), 0.001);
        assertEquals(CDKConstants.STEREO_BOND_UP, bonds[0].getStereo());
        assertEquals(c2, bonds[1].getAtomAt(0));
        assertEquals(o, bonds[1].getAtomAt(1));
        assertEquals(2.0, bonds[1].getOrder(), 0.001);
        assertEquals(CDKConstants.STEREO_BOND_DOWN, bonds[1].getStereo());
        assertEquals(c2, bonds[2].getAtomAt(0));
        assertEquals(c3, bonds[2].getAtomAt(1));
        assertEquals(1.0, bonds[2].getOrder(), 0.001);
        assertEquals(CDKConstants.STEREO_BOND_NONE, bonds[2].getStereo());
    }

    public void testContains_ElectronContainer() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);
        
        assertTrue(acetone.contains(b1));
        assertTrue(acetone.contains(b2));
        assertTrue(acetone.contains(b3));
        assertTrue(acetone.contains(lp1));
        assertTrue(acetone.contains(lp2));
    }

    public void testGetElectronContainers() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);
        
        assertEquals(5, acetone.getElectronContainers().length);
    }
    
    public void testGetLonePairs() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);
        
        assertNotNull(acetone.getLonePairs());
        assertEquals(2, acetone.getLonePairs().length);
    }
    
    public void testGetBonds() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(3, acetone.getBonds().length);
    }
    
    public void testGetFirstAtom() {
        AtomContainer container = new AtomContainer();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("O");
        Atom o = new Atom("H");
        container.addAtom(c1);
        container.addAtom(c2);
        container.addAtom(o);
        
        assertNotNull(container.getFirstAtom());
        assertEquals("C", container.getFirstAtom().getSymbol());
    }

    public void testGetLastAtom() {
        AtomContainer container = new AtomContainer();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("O");
        Atom o = new Atom("H");
        container.addAtom(c1);
        container.addAtom(c2);
        container.addAtom(o);
        
        assertNotNull(container.getLastAtom());
        assertEquals("H", container.getLastAtom().getSymbol());
    }
    
    public void testGetAtomNumber_Atom() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        
        assertEquals(0, acetone.getAtomNumber(c1));
        assertEquals(1, acetone.getAtomNumber(c2));
        assertEquals(2, acetone.getAtomNumber(c3));
        assertEquals(3, acetone.getAtomNumber(o));
    }
    
    public void testGetBondNumber_Bond() {
        // acetone molecule
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(0, acetone.getBondNumber(b1));
        assertEquals(1, acetone.getBondNumber(b2));
        assertEquals(2, acetone.getBondNumber(b3));
    }
    
    public void testGetBondNumber_Atom_Atom() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(0, acetone.getBondNumber(c1, c2));
        assertEquals(1, acetone.getBondNumber(c1, o));
        assertEquals(2, acetone.getBondNumber(c1, c3));
    }
    
    public void testGetBond_Atom_Atom() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertTrue(b1.equals(acetone.getBond(c1, c2)));        
        assertTrue(b2.equals(acetone.getBond(c1, o)));        
        assertTrue(b3.equals(acetone.getBond(c1, c3)));        
    }
    
    public void testGetConnectedAtoms_Atom() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(3, acetone.getConnectedAtoms(c1).length);
        assertEquals(1, acetone.getConnectedAtoms(c2).length);
        assertEquals(1, acetone.getConnectedAtoms(c3).length);
        assertEquals(1, acetone.getConnectedAtoms(o).length);
    }
    
    public void testGetConnectedAtomsVector_Atom() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        assertEquals(3, acetone.getConnectedAtomsVector(c1).size());
        assertEquals(1, acetone.getConnectedAtomsVector(c2).size());
        assertEquals(1, acetone.getConnectedAtomsVector(c3).size());
        assertEquals(1, acetone.getConnectedAtomsVector(o).size());
    }
    
    public void testGetLonePairCount() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);
        
        assertEquals(2, acetone.getLonePairCount());
    }

    public void testGetLonePairCount_Atom() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);
        
        assertEquals(2, acetone.getLonePairCount(o));
        assertEquals(0, acetone.getLonePairCount(c2));
        assertEquals(0, acetone.getLonePairCount(c3));
        assertEquals(0, acetone.getLonePairCount(c1));
    }

    public void testGetBondOrderSum_Atom() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);
        
        assertEquals(4.0, acetone.getBondOrderSum(c1), 0.00001);
        assertEquals(1.0, acetone.getBondOrderSum(c2), 0.00001);
        assertEquals(1.0, acetone.getBondOrderSum(c3), 0.00001);
        assertEquals(2.0, acetone.getBondOrderSum(o), 0.00001);
    }
    
    public void testGetBondCount_Atom() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);
        
        assertEquals(3, acetone.getBondCount(c1));
        assertEquals(1, acetone.getBondCount(c2));
        assertEquals(1, acetone.getBondCount(c3));
        assertEquals(1, acetone.getBondCount(o));
    }
    
    public void testGetBondCount_int() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        // add lone pairs on oxygen
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);
        
        assertEquals(3, acetone.getBondCount(0));
        assertEquals(1, acetone.getBondCount(1));
        assertEquals(1, acetone.getBondCount(2));
        assertEquals(1, acetone.getBondCount(3));
    }
    
    public void testGetAtomParity_Atom() {
        Atom carbon = new Atom("C");
        carbon.setID("central");
        Atom carbon1 = new Atom("C");
        carbon1.setID("c1");
        Atom carbon2 = new Atom("C");
        carbon2.setID("c2");
        Atom carbon3 = new Atom("C");
        carbon3.setID("c3");
        Atom carbon4 = new Atom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        AtomContainer container = new AtomContainer();
        container.addAtomParity(parity);
        AtomParity copy = container.getAtomParity(carbon);
        assertNotNull(copy);
        assertEquals(parity, copy);
    }

    /** Test for RFC #9 */
    public void testToString() {
        AtomContainer container = new AtomContainer();
        String description = container.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

    public void testStateChanged_ChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        AtomContainer chemObject = new AtomContainer();
        chemObject.addListener(listener);
        
        chemObject.addAtom(new Atom());
        assertTrue(listener.changed);
        
        listener.reset();
        assertFalse(listener.changed);
        chemObject.addBond(new Bond(new Atom(), new Atom()));
        assertTrue(listener.changed);
    }

    private class ChemObjectListenerImpl implements ChemObjectListener {
        private boolean changed;
        
        private ChemObjectListenerImpl() {
            changed = false;
        }
        
        public void stateChanged(ChemObjectChangeEvent e) {
            changed = true;
        }
        
        public void reset() {
            changed = false;
        }
    }
}
