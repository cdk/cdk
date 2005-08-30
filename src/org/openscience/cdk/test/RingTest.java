/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Ring;

/**
 * Checks the funcitonality of the Ring class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.Ring
 */
public class RingTest extends CDKTestCase {

    public RingTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(RingTest.class);
    }
    
    public void testRing_int_String() {
        Ring r = new Ring(5, "C");
        assertEquals(5, r.getAtomCount());
        assertEquals(5, r.getBondCount());
    }
    
    public void testRing_int() {
        Ring r = new Ring(5); // This does not create a ring!
        assertEquals(0, r.getAtomCount());
        assertEquals(0, r.getBondCount());
    }
    
    public void testRing() {
        Ring ring = new Ring();
        assertNotNull(ring);
        assertEquals(0, ring.getAtomCount());
        assertEquals(0, ring.getBondCount());
    }

    public void testRing_AtomContainer() {
        AtomContainer container = new org.openscience.cdk.AtomContainer();
        container.addAtom(new Atom("C"));
        container.addAtom(new Atom("C"));
        
        Ring ring = new Ring(container);
        assertNotNull(ring);
        assertEquals(2, ring.getAtomCount());
        assertEquals(0, ring.getBondCount());
    }

    public void testGetOrderSum() {
        Ring r = new Ring(5, "C");
        assertEquals(5, r.getOrderSum());
    }
    
    public void testGetRingSize() {
        Ring r = new Ring(5, "C");
        assertEquals(5, r.getRingSize());
    }
    
    public void testGetNextBond_Bond_Atom() {
        Ring ring = new Ring();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom c3 = new Atom("C");
        Bond b1 = new Bond(c1, c2, 1.0);
        Bond b2 = new Bond(c3, c2, 1.0);
        Bond b3 = new Bond(c1, c3, 1.0);
        ring.addAtom(c1);
        ring.addAtom(c2);
        ring.addAtom(c3);
        ring.addBond(b1);
        ring.addBond(b2);
        ring.addBond(b3);
        
        assertEquals(b1, ring.getNextBond(b2,c2));
        assertEquals(b1, ring.getNextBond(b3,c1));
        assertEquals(b2, ring.getNextBond(b1,c2));
        assertEquals(b2, ring.getNextBond(b3,c3));
        assertEquals(b3, ring.getNextBond(b1,c1));
        assertEquals(b3, ring.getNextBond(b2,c3));
    }
    
    public void testToString() {
        Ring ring = new Ring(5, "C");
        String description = ring.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
