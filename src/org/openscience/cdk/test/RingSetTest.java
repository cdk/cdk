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

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;

/**
 * Checks the funcitonality of the RingSet class.
 *
 * @cdkPackage test
 *
 * @see org.openscience.cdk.RingSet
 */
public class RingSetTest extends TestCase {

    public RingSetTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(RingSetTest.class);
    }
    
    public void testRingSet() {
        RingSet rs = new RingSet();
    }
    
    public void testRingAlreadyInSet() {
        Ring r1 = new Ring(5, "C");
        Ring r2 = new Ring(3, "C");
        
        RingSet rs = new RingSet();
        assertTrue(!rs.ringAlreadyInSet(r1));
        assertTrue(!rs.ringAlreadyInSet(r2));
        
        rs.add(r1);
        assertTrue(rs.ringAlreadyInSet(r1));
        assertTrue(!rs.ringAlreadyInSet(r2));
        
        rs.add(r2);
        assertTrue(rs.ringAlreadyInSet(r1));
        assertTrue(rs.ringAlreadyInSet(r2));
    }
    
    public void testGetRingSetInAtomContainer() {
        Ring r1 = new Ring(5, "C");
        Ring r2 = new Ring(3, "C");
        
        RingSet rs = new RingSet();
        rs.add(r1);
        rs.add(r2);
        
        AtomContainer ac = rs.getRingSetInAtomContainer();
        assertTrue(ac != null);
        assertEquals(8, ac.getAtomCount());
        assertEquals(8, ac.getBondCount());
    }
    
    public void testAdd_RingSet() {
        Ring r1 = new Ring(5, "C");
        Ring r2 = new Ring(3, "C");
        
        RingSet rs = new RingSet();
        rs.add(r1);
        
        RingSet rs2 = new RingSet();
        rs2.add(r2);
        rs2.add(rs);
        
        assertEquals(1, rs.size());
        assertEquals(2, rs2.size());
    }
}
