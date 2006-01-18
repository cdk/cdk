/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
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
 * 
 */
package org.openscience.cdk.test;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ChemObjectBuilder;

/**
 * Checks the functionality of the Bond class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.Bond
 */
public class BondTest extends CDKTestCase {

	protected ChemObjectBuilder builder;
	
    public BondTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(BondTest.class);
    }
    
    public void testBond() {
        Bond bond = builder.newBond();
        assertEquals(2, bond.getAtomCount());
        assertTrue(bond.getAtomAt(0) == null);
        assertTrue(bond.getAtomAt(1) == null);
        assertTrue(bond.getOrder() == 0.0);
        assertEquals(CDKConstants.STEREO_BOND_NONE, bond.getStereo());
    }
    
    public void testBond_IAtom_IAtom() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        Bond bond = builder.newBond(c, o);
        
        assertEquals(2, bond.getAtomCount());
        assertEquals(c, bond.getAtomAt(0));
        assertEquals(o, bond.getAtomAt(1));
        assertEquals(1.0, bond.getOrder(), 0.0001);
        assertEquals(CDKConstants.STEREO_BOND_NONE, bond.getStereo());
    }
    
    public void testBond_IAtom_IAtom_double() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        Bond bond = builder.newBond(c, o, 2.0);
        
        assertEquals(2, bond.getAtomCount());
        assertEquals(c, bond.getAtomAt(0));
        assertEquals(o, bond.getAtomAt(1));
        assertTrue(bond.getOrder() == 2.0);
        assertEquals(CDKConstants.STEREO_BOND_NONE, bond.getStereo());
    }
    
    public void testBond_IAtom_IAtom_double_int() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        Bond bond = builder.newBond(c, o, 1.0, CDKConstants.STEREO_BOND_UP);
        
        assertEquals(2, bond.getAtomCount());
        assertEquals(c, bond.getAtomAt(0));
        assertEquals(o, bond.getAtomAt(1));
        assertTrue(bond.getOrder() == 1.0);
        assertEquals(CDKConstants.STEREO_BOND_UP, bond.getStereo());
    }
    
    public void testCompare_Object() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        
        Bond b = builder.newBond(c, o, 2.0); // C=O bond
        Bond b2 = builder.newBond(c, o, 2.0); // same C=O bond
        
        assertTrue(b.compare(b2));
    }
    
    public void testContains_IAtom() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        
        Bond b = builder.newBond(c, o, 2.0); // C=O bond
        
        assertTrue(b.contains(c));
        assertTrue(b.contains(o));
    }
    
    public void testGetAtomCount() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        
        Bond b = builder.newBond(c, o, 2.0); // C=O bond
        
        assertEquals(2.0, b.getAtomCount(), 0.001);
    }
    
    public void testSetAtoms_arrayAtom() {
        IAtom[] atomsToAdd = new IAtom[2];
        atomsToAdd[0] = builder.newAtom("C");
        atomsToAdd[1] = builder.newAtom("O");
        
        Bond b = builder.newBond();
        b.setAtoms(atomsToAdd);
        
        org.openscience.cdk.interfaces.IAtom[] atoms = b.getAtoms();
        assertEquals(2, atoms.length);
        assertEquals(atomsToAdd[0], atoms[0]);
        assertEquals(atomsToAdd[1], atoms[1]);
    }
    
    public void testGetAtoms() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        
        Bond b = builder.newBond(c, o, 2.0); // C=O bond
        
        org.openscience.cdk.interfaces.IAtom[] atoms = b.getAtoms();
        assertEquals(2, atoms.length);
        assertEquals(c, atoms[0]);
        assertEquals(o, atoms[1]);
    }
    
    public void testGetAtomsVector() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        
        Bond b = builder.newBond(c, o, 2.0); // C=O bond
        
        IAtom[] atoms = b.getAtoms();
        assertEquals(2, atoms.length);
        assertEquals(c, atoms[0]);
        assertEquals(o, atoms[1]);
    }
    
    public void testGetAtomAt_int() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        
        Bond b = builder.newBond(c, o, 2.0); // C=O bond
        
        assertEquals(c, b.getAtomAt(0));
        assertEquals(o, b.getAtomAt(1));
    }
    
    public void testSetAtomAt_IAtom_int() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        
        Bond b = builder.newBond();
        b.setAtomAt(c, 0);
        b.setAtomAt(o, 1);
        
        assertEquals(c, b.getAtomAt(0));
        assertEquals(o, b.getAtomAt(1));
    }
    
    public void testGetConnectedAtom_IAtom() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        
        Bond b = builder.newBond(c, o, 2.0); // C=O bond
        
        assertEquals(c, b.getConnectedAtom(o));
        assertEquals(o, b.getConnectedAtom(c));
    }
    
    public void testIsConnectedTo_Bond() {
        IAtom c1 = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        IAtom c2 = builder.newAtom("C");
        IAtom c3 = builder.newAtom("C");
        
        Bond b1 = builder.newBond(c1, o);
        Bond b2 = builder.newBond(o, c2);
        Bond b3 = builder.newBond(c2, c3);
        
        assertTrue(b1.isConnectedTo(b2));
        assertTrue(b2.isConnectedTo(b1));
        assertTrue(b2.isConnectedTo(b3));
        assertTrue(b3.isConnectedTo(b2));
        assertFalse(b1.isConnectedTo(b3));
        assertFalse(b3.isConnectedTo(b1));
    }
    
    public void testGetOrder() {
        Bond b = builder.newBond(builder.newAtom("C"), builder.newAtom("O"), 2.0); // C=O bond
        
        assertEquals(2.0, b.getOrder(), 0.001);
    }
    public void testSetOrder_double() {
        Bond b = builder.newBond(builder.newAtom("C"), builder.newAtom("O"), 2.0); // C=O bond
        
        assertEquals(2.0, b.getOrder(), 0.001);
        
        b.setOrder(1.0);
        assertEquals(1.0, b.getOrder(), 0.001);
    }
    
    public void testSetStereo_int() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        
        Bond b = builder.newBond(c, o, 2.0, CDKConstants.STEREO_BOND_DOWN);
        
        b.setStereo(CDKConstants.STEREO_BOND_UP);
        assertEquals(CDKConstants.STEREO_BOND_UP, b.getStereo());
    }
    public void testGetStereo() {
        IAtom c = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        
        Bond b = builder.newBond(c, o, 2.0, CDKConstants.STEREO_BOND_UP);
        assertEquals(CDKConstants.STEREO_BOND_UP, b.getStereo());
    }
    
    public void testGet2DCenter() {
        IAtom o = builder.newAtom("O", new Point2d(0.0, 0.0));
        IAtom c = builder.newAtom("C", new Point2d(1.0, 1.0));
        Bond b = builder.newBond(c,o);
        
        assertEquals(0.5, b.get2DCenter().x, 0.001);
        assertEquals(0.5, b.get2DCenter().y, 0.001);
    }

    public void testGet3DCenter() {
        IAtom o = builder.newAtom("O", new Point3d(0.0, 0.0, 0.0));
        IAtom c = builder.newAtom("C", new Point3d(1.0, 1.0, 1.0));
        Bond b = builder.newBond(c,o);
        
        assertEquals(0.5, b.get3DCenter().x, 0.001);
        assertEquals(0.5, b.get3DCenter().y, 0.001);
        assertEquals(0.5, b.get3DCenter().z, 0.001);
    }

    public void testClone() {
        Bond bond = builder.newBond();
        Object clone = bond.clone();
        assertNotNull(clone);
        assertTrue(clone instanceof org.openscience.cdk.interfaces.Bond);
    }

    public void testClone_IAtom() {
        IAtom atom1 = builder.newAtom("C");
        IAtom atom2 = builder.newAtom("O");
        Bond bond = builder.newBond(atom1, atom2);
        Bond clone = (Bond)bond.clone();
        
        // test cloning of atoms
        assertNotSame(atom1, clone.getAtomAt(0));
        assertNotSame(atom2, clone.getAtomAt(1));
    }

    public void testClone_Order() {
        IAtom atom1 = builder.newAtom("C");
        IAtom atom2 = builder.newAtom("O");
        Bond bond = builder.newBond(atom1, atom2, 1.0);
        Bond clone = (Bond)bond.clone();
        
        // test cloning of bond order
        bond.setOrder(2.0);
        assertEquals(1.0, clone.getOrder(), 0.01);
    }

    public void testClone_Stereo() {
        IAtom atom1 = builder.newAtom("C");
        IAtom atom2 = builder.newAtom("O");
        Bond bond = builder.newBond(atom1, atom2, 1.0, 1);
        Bond clone = (Bond)bond.clone();
        
        // test cloning of bond order
        bond.setStereo(2);
        assertEquals(1, clone.getStereo());
    }

    /** Test for RFC #9 */
    public void testToString() {
        Bond bond = builder.newBond();
        String description = bond.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
