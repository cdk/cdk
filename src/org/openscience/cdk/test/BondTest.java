/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;

/**
 * Checks the functionality of the Bond class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.Bond
 */
public class BondTest extends TestCase {

    public BondTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(BondTest.class);
    }
    
    public void testBond() {
        Bond bond = new Bond();
        assertEquals(2, bond.getAtomCount());
        assertTrue(bond.getAtomAt(0) == null);
        assertTrue(bond.getAtomAt(1) == null);
        assertTrue(bond.getOrder() == 0.0);
        assertEquals(CDKConstants.STEREO_BOND_NONE, bond.getStereo());
    }
    
    public void testBond_Atom_Atom() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        Bond bond = new Bond(c, o);
        
        assertEquals(2, bond.getAtomCount());
        assertTrue(bond.getAtomAt(0).compare(c));
        assertTrue(bond.getAtomAt(1).compare(o));
        assertTrue(bond.getOrder() == 1.0);
        assertEquals(CDKConstants.STEREO_BOND_NONE, bond.getStereo());
    }
    
    public void testBond_Atom_Atom_double() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        Bond bond = new Bond(c, o, 2.0);
        
        assertEquals(2, bond.getAtomCount());
        assertTrue(bond.getAtomAt(0).compare(c));
        assertTrue(bond.getAtomAt(1).compare(o));
        assertTrue(bond.getOrder() == 2.0);
        assertEquals(CDKConstants.STEREO_BOND_NONE, bond.getStereo());
    }
    
    public void testBond_Atom_Atom_double_int() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        Bond bond = new Bond(c, o, 1.0, CDKConstants.STEREO_BOND_UP);
        
        assertEquals(2, bond.getAtomCount());
        assertTrue(bond.getAtomAt(0).compare(c));
        assertTrue(bond.getAtomAt(1).compare(o));
        assertTrue(bond.getOrder() == 1.0);
        assertEquals(CDKConstants.STEREO_BOND_UP, bond.getStereo());
    }
    
    public void testCompare_Object() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        Bond b2 = new Bond(c, o, 2.0); // same C=O bond
        
        assertTrue(b.compare(b2));
    }
    
    public void testContains_Atom() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        assertTrue(b.contains(c));
        assertTrue(b.contains(o));
    }
    
    public void testGetAtomCount() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        assertEquals(2.0, b.getAtomCount(), 0.001);
    }
    
    public void testGetAtoms() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        Atom[] atoms = b.getAtoms();
        assertEquals(2.0, atoms.length, 0.001);
        assertEquals(c, atoms[0]);
        assertEquals(o, atoms[1]);
    }
    
    public void testGetAtomAt_int() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        assertEquals(c, b.getAtomAt(0));
        assertEquals(o, b.getAtomAt(1));
    }
    
    public void testGetConnectedAtom_Atom() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        assertEquals(c, b.getConnectedAtom(o));
        assertEquals(o, b.getConnectedAtom(c));
    }
    
    public void testGetOrder() {
        Bond b = new Bond(new Atom("C"), new Atom("O"), 2.0); // C=O bond
        
        assertEquals(2.0, b.getOrder(), 0.001);
    }
    public void testSetOrder_double() {
        Bond b = new Bond(new Atom("C"), new Atom("O"), 2.0); // C=O bond
        
        assertEquals(2.0, b.getOrder(), 0.001);
        
        b.setOrder(1.0);
        assertEquals(1.0, b.getOrder(), 0.001);
    }
    
    public void testSetStereo_int() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0);
        
        b.setStereo(CDKConstants.STEREO_BOND_UP);
        assertEquals(CDKConstants.STEREO_BOND_UP, b.getStereo());
    }
    
    public void testGet2DCenter() {
        Atom o = new Atom("O", new Point2d(0.0, 0.0));
        Atom c = new Atom("C", new Point2d(1.0, 1.0));
        Bond b = new Bond(c,o);
        
        assertEquals(0.5, b.get2DCenter().x, 0.001);
        assertEquals(0.5, b.get2DCenter().y, 0.001);
    }

    public void testGet3DCenter() {
        Atom o = new Atom("O", new Point3d(0.0, 0.0, 0.0));
        Atom c = new Atom("C", new Point3d(1.0, 1.0, 1.0));
        Bond b = new Bond(c,o);
        
        assertEquals(0.5, b.get3DCenter().x, 0.001);
        assertEquals(0.5, b.get3DCenter().y, 0.001);
        assertEquals(0.5, b.get3DCenter().z, 0.001);
    }

    public void testClone() {
        Bond bond = new Bond();
        Object clone = bond.clone();
        assertNotNull(clone);
        assertTrue(clone instanceof Bond);
    }

    public void testClone_Atom() {
        Atom atom1 = new Atom("C");
        Atom atom2 = new Atom("O");
        Bond bond = new Bond(atom1, atom2);
        Bond clone = (Bond)bond.clone();
        
        // test cloning of atoms
        assertNotSame(atom1, clone.getAtomAt(0));
        assertNotSame(atom2, clone.getAtomAt(1));
    }

    public void testClone_Order() {
        Atom atom1 = new Atom("C");
        Atom atom2 = new Atom("O");
        Bond bond = new Bond(atom1, atom2, 1.0);
        Bond clone = (Bond)bond.clone();
        
        // test cloning of bond order
        bond.setOrder(2.0);
        assertEquals(1.0, clone.getOrder(), 0.01);
    }

    public void testClone_Stereo() {
        Atom atom1 = new Atom("C");
        Atom atom2 = new Atom("O");
        Bond bond = new Bond(atom1, atom2, 1.0, 1);
        Bond clone = (Bond)bond.clone();
        
        // test cloning of bond order
        bond.setStereo(2);
        assertEquals(1, clone.getStereo());
    }

    /** Test for RFC #9 */
    public void testToString() {
        Bond bond = new Bond();
        String description = bond.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
