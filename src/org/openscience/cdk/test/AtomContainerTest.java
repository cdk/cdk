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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.LonePair;
import org.openscience.cdk.Molecule;

/**
 * Checks the funcitonality of the AtomContainer.
 *
 * @cdkPackage test
 */
public class AtomContainerTest extends TestCase {

    public AtomContainerTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(AtomContainerTest.class);
    }

    public void testSetAtoms() {
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
		Molecule molecule = new Molecule();
		molecule.addAtom(new Atom("C")); // 1
		molecule.addAtom(new Atom("C")); // 2
		molecule.addAtom(new Atom("C")); // 3
		molecule.addAtom(new Atom("C")); // 4

		molecule.addBond(0, 1, 2.0); // 1
		molecule.addBond(1, 2, 1.0); // 2
		molecule.addBond(2, 3, 1.0); // 3
		Molecule clonedMol = (Molecule)molecule.clone();
		assertTrue(molecule.getAtomCount() == clonedMol.getAtomCount());
		for (int f = 0; f < molecule.getAtomCount(); f++) {
			for (int g = 0; g < clonedMol.getAtomCount(); g++) {
				assertNotNull(molecule.getAtomAt(f));
				assertNotNull(clonedMol.getAtomAt(g));
				assertTrue(molecule.getAtomAt(f) != clonedMol.getAtomAt(g));
			}
		}
	}

    public void testGetConnectedElectronContainers() {
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

    public void testGetConnectedBonds() {
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

    
    public void testRemoveAtomAndConnectedElectronContainers() {
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

    public void testGetElectronContainerAt() {
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
        assertEquals(3, container.getBondCount());
    }
    
    public void testSetAtomAt() {
        AtomContainer container = new AtomContainer();
        Atom c = new Atom("C");
        container.setAtomAt(0, c);
        
        assertNotNull(container.getAtomAt(0));
        assertEquals("C", container.getAtomAt(0).getSymbol());
    }
    
    public void testGetAtomAt() {
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
    
    public void testGetBondAt() {
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
    
    public void testSetElectronContainerAt() {
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
    
    public void testSetElectronContainerCount() {
        AtomContainer container = new AtomContainer();
        container.setElectronContainerCount(2);
        
        assertEquals(2, container.getElectronContainerCount());
    }
    
    public void testSetAtomCount() {
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
        int counter = 0;
        while (atomEnum.hasMoreElements()) {
            atomEnum.nextElement();
            counter++;
        }
        assertEquals(4, counter);
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
    
    public void testLonePairs_Atom() {
        Molecule acetone = new Molecule();
        Atom c1 = new Atom("C");
        Atom o = new Atom("O");
        acetone.addAtom(c1);
        acetone.addAtom(o);
        LonePair lp1 = new LonePair(o);
        LonePair lp2 = new LonePair(o);
        acetone.addElectronContainer(lp1);
        acetone.addElectronContainer(lp2);
        
        assertEquals(2, acetone.getLonePairs(o).length);
        assertEquals(0, acetone.getLonePairs(c1).length);
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
    
    public void testGetAtomNumber() {
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
    
    public void testGetBond() {
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
    
    public void testGetConnectedAtoms() {
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
        
        assertEquals(3, acetone.getBondCount(c1));
        assertEquals(1, acetone.getBondCount(c2));
        assertEquals(1, acetone.getBondCount(c3));
        assertEquals(1, acetone.getBondCount(o));
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

}
