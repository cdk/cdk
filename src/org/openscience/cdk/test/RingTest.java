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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Ring;

/**
 * Checks the funcitonality of the Ring class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.Ring
 */
public class RingTest extends TestCase {

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

    public void testGetOrderSum() {
        Ring r = new Ring(5, "C");
        assertEquals(5, r.getOrderSum());
    }
    
    public void testRingSize() {
        Ring r = new Ring(5, "C");
	assertNotNull(r);
    }
    
    public void testGetRingSize() {
        Ring r = new Ring(5, "C");
        assertEquals(5, r.getRingSize());
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
