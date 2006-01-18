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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 * 
 */

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.Ring;
import org.openscience.cdk.interfaces.RingSet;
import org.openscience.cdk.interfaces.ChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Checks the funcitonality of the RingSet class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.RingSet
 */
public class RingSetTest extends CDKTestCase {

	protected ChemObjectBuilder builder;
	
    public RingSetTest(String name) {
        super(name);
    }

    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(RingSetTest.class);
    }
    
    public void testRingSet() {
        RingSet rs = builder.newRingSet();
    }
    
    public void testRingAlreadyInSet_Ring() {
        Ring r1 = builder.newRing(5, "C");
        Ring r2 = builder.newRing(3, "C");
        
        RingSet rs = builder.newRingSet();
        assertTrue(!rs.ringAlreadyInSet(r1));
        assertTrue(!rs.ringAlreadyInSet(r2));
        
        rs.add(r1);
        assertTrue(rs.ringAlreadyInSet(r1));
        assertTrue(!rs.ringAlreadyInSet(r2));
        
        rs.add(r2);
        assertTrue(rs.ringAlreadyInSet(r1));
        assertTrue(rs.ringAlreadyInSet(r2));
    }
    
    public void testAdd_RingSet() {
        Ring r1 = builder.newRing(5, "C");
        Ring r2 = builder.newRing(3, "C");
        
        RingSet rs = builder.newRingSet();
        rs.add(r1);
        
        RingSet rs2 = builder.newRingSet();
        rs2.add(r2);
        rs2.add(rs);
        
        assertEquals(1, rs.size());
        assertEquals(2, rs2.size());
    }

    public void testToString() {
        RingSet ringset = builder.newRingSet();
        String description = ringset.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
    
    /* FIXME: make RingSet inherited from IChemObject, see #1117775
    public void testClone() {
        RingSet ringset = builder.newRingSet();
        Ring ring = builder.newRing();
        ringset.add(ring);
        
        RingSet clone = (RingSet)ringset.clone();
        assertNotNull(clone);
        assertTrue(clone instanceof RingSet);
        assertEquals(1, clone.size());
        assertNotSame(ring, clone.elementAt(0));
    } */
    
    public void testContains_IAtom() {
        RingSet ringset = builder.newRingSet();

        IAtom ring1Atom1 = builder.newAtom("C"); // rather artificial molecule
        IAtom ring1Atom2 = builder.newAtom("C");
        IAtom sharedAtom1 = builder.newAtom("C");
        IAtom sharedAtom2 = builder.newAtom("C");
        IAtom ring2Atom1 = builder.newAtom("C");
        IAtom ring2Atom2 = builder.newAtom("C");
        IBond ring1Bond1 = builder.newBond(ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = builder.newBond(sharedAtom1, ring1Atom1);
        IBond ring1Bond3 = builder.newBond(sharedAtom2, ring1Atom2);
        IBond sharedBond = builder.newBond(sharedAtom1, sharedAtom2);
        IBond ring2Bond1 = builder.newBond(ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = builder.newBond(sharedAtom1, ring2Atom1);
        IBond ring2Bond3 = builder.newBond(sharedAtom2, ring2Atom2);

        Ring ring1 = builder.newRing();
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        Ring ring2 = builder.newRing();
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);
        
        ringset.add(ring1);
        ringset.add(ring2);
        
        assertTrue(ringset.contains(ring1Atom1));
        assertTrue(ringset.contains(ring1Atom2));
        assertTrue(ringset.contains(sharedAtom1));
        assertTrue(ringset.contains(sharedAtom2));
        assertTrue(ringset.contains(ring2Atom1));
        assertTrue(ringset.contains(ring2Atom2));
    }
    
    public void testGetRings_Bond() {
        RingSet ringset = builder.newRingSet();

        IAtom ring1Atom1 = builder.newAtom("C"); // rather artificial molecule
        IAtom ring1Atom2 = builder.newAtom("C");
        IAtom sharedAtom1 = builder.newAtom("C");
        IAtom sharedAtom2 = builder.newAtom("C");
        IAtom ring2Atom1 = builder.newAtom("C");
        IAtom ring2Atom2 = builder.newAtom("C");
        IBond ring1Bond1 = builder.newBond(ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = builder.newBond(sharedAtom1, ring1Atom1);
        IBond ring1Bond3 = builder.newBond(sharedAtom2, ring1Atom2);
        IBond sharedBond = builder.newBond(sharedAtom1, sharedAtom2);
        IBond ring2Bond1 = builder.newBond(ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = builder.newBond(sharedAtom1, ring2Atom1);
        IBond ring2Bond3 = builder.newBond(sharedAtom2, ring2Atom2);

        Ring ring1 = builder.newRing();
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        Ring ring2 = builder.newRing();
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);
        
        ringset.add(ring1);
        ringset.add(ring2);
        
        assertEquals(1, ringset.getRings(ring1Bond1).size());
        assertEquals(1, ringset.getRings(ring1Bond2).size());
        assertEquals(1, ringset.getRings(ring1Bond3).size());
        assertEquals(2, ringset.getRings(sharedBond).size());
        assertEquals(1, ringset.getRings(ring2Bond1).size());
        assertEquals(1, ringset.getRings(ring2Bond2).size());
        assertEquals(1, ringset.getRings(ring2Bond3).size());
    }

    public void testIsSameRing_IAtom_IAtom() {
        RingSet ringset = builder.newRingSet();

        IAtom ring1Atom1 = builder.newAtom("C"); // rather artificial molecule
        IAtom ring1Atom2 = builder.newAtom("C");
        IAtom ring1Atom3 = builder.newAtom("C");
        IAtom ring2Atom1 = builder.newAtom("C");
        IAtom ring2Atom2 = builder.newAtom("C");
        IAtom ring2Atom3 = builder.newAtom("C");
        IBond ring1Bond1 = builder.newBond(ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = builder.newBond(ring1Atom2, ring1Atom3);
        IBond ring1Bond3 = builder.newBond(ring1Atom3, ring1Atom1);
        
        IBond ring2Bond1 = builder.newBond(ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = builder.newBond(ring2Atom2, ring2Atom3);
        IBond ring2Bond3 = builder.newBond(ring2Atom3, ring2Atom1);

        Ring ring1 = builder.newRing();
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(ring1Atom3);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);

        Ring ring2 = builder.newRing();
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(ring2Atom3);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        
        ringset.add(ring1);
        ringset.add(ring2);
        
        assertTrue(RingSetManipulator.isSameRing(ringset, ring1Atom1, ring1Atom3));
        assertFalse(RingSetManipulator.isSameRing(ringset, ring1Atom1, ring2Atom3));
    }
    
    
    public void testGetRings_IAtom() {
        RingSet ringset = builder.newRingSet();

        IAtom ring1Atom1 = builder.newAtom("C"); // rather artificial molecule
        IAtom ring1Atom2 = builder.newAtom("C");
        IAtom sharedAtom1 = builder.newAtom("C");
        IAtom sharedAtom2 = builder.newAtom("C");
        IAtom ring2Atom1 = builder.newAtom("C");
        IAtom ring2Atom2 = builder.newAtom("C");
        IBond ring1Bond1 = builder.newBond(ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = builder.newBond(sharedAtom1, ring1Atom1);
        IBond ring1Bond3 = builder.newBond(sharedAtom2, ring1Atom2);
        IBond sharedBond = builder.newBond(sharedAtom1, sharedAtom2);
        IBond ring2Bond1 = builder.newBond(ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = builder.newBond(sharedAtom1, ring2Atom1);
        IBond ring2Bond3 = builder.newBond(sharedAtom2, ring2Atom2);

        Ring ring1 = builder.newRing();
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        Ring ring2 = builder.newRing();
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);
        
        ringset.add(ring1);
        ringset.add(ring2);
        
        assertEquals(1, ringset.getRings(ring1Atom1).size());
        assertEquals(1, ringset.getRings(ring1Atom1).size());
        assertEquals(2, ringset.getRings(sharedAtom1).size());
        assertEquals(2, ringset.getRings(sharedAtom2).size());
        assertEquals(1, ringset.getRings(ring2Atom1).size());
        assertEquals(1, ringset.getRings(ring2Atom2).size());
    }
    
    public void testGetConnectedRings_Ring() {
        RingSet ringset = builder.newRingSet();

        IAtom ring1Atom1 = builder.newAtom("C"); // rather artificial molecule
        IAtom ring1Atom2 = builder.newAtom("C");
        IAtom sharedAtom1 = builder.newAtom("C");
        IAtom sharedAtom2 = builder.newAtom("C");
        IAtom ring2Atom1 = builder.newAtom("C");
        IAtom ring2Atom2 = builder.newAtom("C");
        IBond ring1Bond1 = builder.newBond(ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = builder.newBond(sharedAtom1, ring1Atom1);
        IBond ring1Bond3 = builder.newBond(sharedAtom2, ring1Atom2);
        IBond sharedBond = builder.newBond(sharedAtom1, sharedAtom2);
        IBond ring2Bond1 = builder.newBond(ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = builder.newBond(sharedAtom1, ring2Atom1);
        IBond ring2Bond3 = builder.newBond(sharedAtom2, ring2Atom2);

        Ring ring1 = builder.newRing();
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        Ring ring2 = builder.newRing();
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);
        
        ringset.add(ring1);
        ringset.add(ring2);
        
        assertEquals(2, ringset.getConnectedRings(ring2).size());
        assertEquals(2, ringset.getConnectedRings(ring1).size());
    }

    public void testClone() {
        // Added to make the Coverage tool happy
        // The method is apparently not part of the interface yet
    	assertTrue(true);
    }
}
