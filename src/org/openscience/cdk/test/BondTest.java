/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2003  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.*;
import java.util.*;
import junit.framework.*;
import javax.vecmath.*;

/**
 * Checks the functionality of the Bond class.
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
        assertEquals(CDKConstants.STEREO_BOND_UNDEFINED, bond.getStereo());
    }
    
    public void testBond_Atom_Atom() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        Bond bond = new Bond(c, o);
        
        assertEquals(2, bond.getAtomCount());
        assertTrue(bond.getAtomAt(0).compare(c));
        assertTrue(bond.getAtomAt(1).compare(o));
        assertTrue(bond.getOrder() == 1.0);
        assertEquals(CDKConstants.STEREO_BOND_UNDEFINED, bond.getStereo());
    }
    
    public void testBond_Atom_Atom_Double() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        Bond bond = new Bond(c, o, 2.0);
        
        assertEquals(2, bond.getAtomCount());
        assertTrue(bond.getAtomAt(0).compare(c));
        assertTrue(bond.getAtomAt(1).compare(o));
        assertTrue(bond.getOrder() == 2.0);
        assertEquals(CDKConstants.STEREO_BOND_UNDEFINED, bond.getStereo());
    }
    
    public void testBond_Atom_Atom_Double_Int() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        Bond bond = new Bond(c, o, 1.0, CDKConstants.STEREO_BOND_UP);
        
        assertEquals(2, bond.getAtomCount());
        assertTrue(bond.getAtomAt(0).compare(c));
        assertTrue(bond.getAtomAt(1).compare(o));
        assertTrue(bond.getOrder() == 1.0);
        assertEquals(CDKConstants.STEREO_BOND_UP, bond.getStereo());
    }
    
    public void testCompare() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        Bond b2 = new Bond(c, o, 2.0); // same C=O bond
        
        assertTrue(b.compare(b2));
    }
    
    public void testContains() {
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
        
        assertTrue(2.0 == b.getAtomCount());
    }
    
    public void testGetAtoms() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        Atom[] atoms = b.getAtoms();
        assertTrue(2.0 == atoms.length);
        assertEquals(c, atoms[0]);
        assertEquals(o, atoms[1]);
    }
    
    public void testGetConnectedAtom() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        assertEquals(c, b.getConnectedAtom(o));
        assertEquals(o, b.getConnectedAtom(c));
    }
    
    public void testGetOrder() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0); // C=O bond
        
        assertTrue(2.0 == b.getOrder());
        
        b.setOrder(1.0);
        assertTrue(1.0 == b.getOrder());
    }
    
    public void testSetStereo() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Bond b = new Bond(c, o, 2.0);
        
        b.setStereo(CDKConstants.STEREO_BOND_UP);
        assertEquals(CDKConstants.STEREO_BOND_UP, b.getStereo());
    }
    
    public void testget2DCenter() {
        Atom o = new Atom("O", new Point2d(0.0, 0.0));
        Atom c = new Atom("C", new Point2d(1.0, 1.0));
        Bond b = new Bond(c,o);
        
        assertTrue(0.5 == b.get2DCenter().x);
        assertTrue(0.5 == b.get2DCenter().y);
    }

    public void testget3DCenter() {
        Atom o = new Atom("O", new Point3d(0.0, 0.0, 0.0));
        Atom c = new Atom("C", new Point3d(1.0, 1.0, 1.0));
        Bond b = new Bond(c,o);
        
        assertTrue(0.5 == b.get3DCenter().x);
        assertTrue(0.5 == b.get3DCenter().y);
        assertTrue(0.5 == b.get3DCenter().z);
    }

    public void testGetLength() {
        Atom o = new Atom("O", new Point2d(0.0, 0.0));
        Atom c = new Atom("C", new Point2d(1.0, 0.0));
        Bond b = new Bond(c,o);
        
        assertTrue(1.0 == b.getLength());
    }
    
    public void testClone() {
        Bond bond = new Bond();
        Object clone = bond.clone();
        assertTrue(clone instanceof Bond);
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
