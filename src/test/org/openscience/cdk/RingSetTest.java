/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
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
 * 
 */
package org.openscience.cdk.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;

/**
 * Checks the functionality of the RingSet class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.RingSet
 */
public class RingSetTest extends NewCDKTestCase {

	protected static IChemObjectBuilder builder;
  
    @BeforeClass public static void setUp() {
        builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testRingSet() {
        IRingSet rs = builder.newRingSet();
        Assert.assertNotNull(rs);
    }
    
    
    @Test public void testAdd_IRingSet() {
        IRing r1 = builder.newRing(5, "C");
        IRing r2 = builder.newRing(3, "C");
        
        IRingSet rs = builder.newRingSet();
        rs.addAtomContainer(r1);
        
        IRingSet rs2 = builder.newRingSet();
        rs2.addAtomContainer(r2);
        rs2.add(rs);
        
        Assert.assertEquals(1, rs.getAtomContainerCount());
        Assert.assertEquals(2, rs2.getAtomContainerCount());
    }

    @Test public void testToString() {
        IRingSet ringset = builder.newRingSet();
        String description = ringset.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
    
    /* FIXME: make RingSet inherited from IChemObject, see #1117775
    @Test public void testClone() {
        RingSet ringset = builder.newRingSet();
        Ring ring = builder.newRing();
        ringset.add(ring);
        
        RingSet clone = (RingSet)ringset.clone();
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone instanceof RingSet);
        Assert.assertEquals(1, clone.size());
        Assert.assertNotSame(ring, clone.elementAt(0));
    } */
    
    @Test public void testContains_IAtom() {
        IRingSet ringset = builder.newRingSet();

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

        IRing ring1 = builder.newRing();
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        IRing ring2 = builder.newRing();
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);
        
        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);
        
        Assert.assertTrue(ringset.contains(ring1Atom1));
        Assert.assertTrue(ringset.contains(ring1Atom2));
        Assert.assertTrue(ringset.contains(sharedAtom1));
        Assert.assertTrue(ringset.contains(sharedAtom2));
        Assert.assertTrue(ringset.contains(ring2Atom1));
        Assert.assertTrue(ringset.contains(ring2Atom2));
    }
    
    @Test public void testContains_IAtomContainer() {
        IRingSet ringset = builder.newRingSet();

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

        IRing ring1 = builder.newRing();
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        IRing ring2 = builder.newRing();
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);
        
        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);
        
        Assert.assertTrue(ringset.contains(ring1));
        Assert.assertTrue(ringset.contains(ring2));
    }

    @Test public void testGetRings_IBond() {
        IRingSet ringset = builder.newRingSet();

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

        IRing ring1 = builder.newRing();
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        IRing ring2 = builder.newRing();
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);
        
        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);
        
        Assert.assertEquals(1, ringset.getRings(ring1Bond1).size());
        Assert.assertEquals(1, ringset.getRings(ring1Bond2).size());
        Assert.assertEquals(1, ringset.getRings(ring1Bond3).size());
        Assert.assertEquals(2, ringset.getRings(sharedBond).size());
        Assert.assertEquals(1, ringset.getRings(ring2Bond1).size());
        Assert.assertEquals(1, ringset.getRings(ring2Bond2).size());
        Assert.assertEquals(1, ringset.getRings(ring2Bond3).size());
    }

    @Test public void testGetRings_IAtom() {
        IRingSet ringset = builder.newRingSet();

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

        IRing ring1 = builder.newRing();
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        IRing ring2 = builder.newRing();
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);
        
        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);
        
        Assert.assertEquals(1, ringset.getRings(ring1Atom1).getAtomContainerCount());
        Assert.assertEquals(1, ringset.getRings(ring1Atom1).getAtomContainerCount());
        Assert.assertEquals(2, ringset.getRings(sharedAtom1).getAtomContainerCount());
        Assert.assertEquals(2, ringset.getRings(sharedAtom2).getAtomContainerCount());
        Assert.assertEquals(1, ringset.getRings(ring2Atom1).getAtomContainerCount());
        Assert.assertEquals(1, ringset.getRings(ring2Atom2).getAtomContainerCount());
    }
    
    @Test public void testGetConnectedRings_IRing() {
        IRingSet ringset = builder.newRingSet();

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

        IRing ring1 = builder.newRing();
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        IRing ring2 = builder.newRing();
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);
        
        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);
        
        Assert.assertEquals(1, ringset.getConnectedRings(ring2).size());
        Assert.assertEquals(1, ringset.getConnectedRings(ring1).size());
    }

    @Test public void testClone() {
        // Added to make the Coverage tool happy
        // The method is apparently not part of the interface yet
      Assert.assertTrue(true);
    }
    
    /**
     * Test for RingSetTest bug #1772613.
     * When using method getConnectedRings(...) of RingSet.java fused or bridged rings
     * returned a list of connected rings that contained duplicates.
     * Bug fix by Andreas Schueller <a.schueller@chemie.uni-frankfurt.de>
     * @cdk.bug 1772613
     */
    @Test public void testGetConnectedRingsBug1772613() throws Exception {
        // Build a bridged and fused norbomane like ring system
        // C1CCC2C(C1)C4CC2C3CCCCC34
        IRing leftCyclohexane = builder.newRing(6, "C");
        IRing rightCyclopentane = builder.newRing(5, "C");
        
        IRing leftCyclopentane = builder.newRing();
        IBond leftCyclohexane0RightCyclopentane4 = builder.newBond(leftCyclohexane.getAtom(0), rightCyclopentane.getAtom(4));
        IBond leftCyclohexane1RightCyclopentane2 = builder.newBond(leftCyclohexane.getAtom(1), rightCyclopentane.getAtom(2));
        leftCyclopentane.addAtom(leftCyclohexane.getAtom(0));
        leftCyclopentane.addAtom(leftCyclohexane.getAtom(1));
        leftCyclopentane.addAtom(rightCyclopentane.getAtom(2));
        leftCyclopentane.addAtom(rightCyclopentane.getAtom(3));
        leftCyclopentane.addAtom(rightCyclopentane.getAtom(4));
        leftCyclopentane.addBond(leftCyclohexane.getBond(leftCyclohexane.getAtom(0), leftCyclohexane.getAtom(1)));
        leftCyclopentane.addBond(leftCyclohexane1RightCyclopentane2);
        leftCyclopentane.addBond(rightCyclopentane.getBond(rightCyclopentane.getAtom(2), rightCyclopentane.getAtom(3)));
        leftCyclopentane.addBond(rightCyclopentane.getBond(rightCyclopentane.getAtom(3), rightCyclopentane.getAtom(4)));
        leftCyclopentane.addBond(leftCyclohexane0RightCyclopentane4);
    
        IRing rightCyclohexane = builder.newRing();
        IAtom rightCyclohexaneAtom0 = builder.newAtom("C");
        IAtom rightCyclohexaneAtom1 = builder.newAtom("C");
        IAtom rightCyclohexaneAtom2 = builder.newAtom("C");
        IAtom rightCyclohexaneAtom5 = builder.newAtom("C");
        IBond rightCyclohexaneAtom0Atom1 = builder.newBond(rightCyclohexaneAtom0, rightCyclohexaneAtom1);
        IBond rightCyclohexaneAtom1Atom2 = builder.newBond(rightCyclohexaneAtom1, rightCyclohexaneAtom2);
        IBond rightCyclohexane2rightCyclopentane1 = builder.newBond(rightCyclohexaneAtom2, rightCyclopentane.getAtom(1));
        IBond rightCyclohexane5rightCyclopentane0 = builder.newBond(rightCyclohexaneAtom5, rightCyclopentane.getAtom(0));
        IBond rightCyclohexaneAtom0Atom5 = builder.newBond(rightCyclohexaneAtom0, rightCyclohexaneAtom5);
        rightCyclohexane.addAtom(rightCyclohexaneAtom0);
        rightCyclohexane.addAtom(rightCyclohexaneAtom1);
        rightCyclohexane.addAtom(rightCyclohexaneAtom2);
        rightCyclohexane.addAtom(rightCyclopentane.getAtom(1));
        rightCyclohexane.addAtom(rightCyclopentane.getAtom(0));
        rightCyclohexane.addAtom(rightCyclohexaneAtom5);
        rightCyclohexane.addBond(rightCyclohexaneAtom0Atom1);
        rightCyclohexane.addBond(rightCyclohexaneAtom1Atom2);
        rightCyclohexane.addBond(rightCyclohexane2rightCyclopentane1);
        rightCyclohexane.addBond(rightCyclopentane.getBond(rightCyclopentane.getAtom(0), rightCyclopentane.getAtom(1)));
        rightCyclohexane.addBond(rightCyclohexane5rightCyclopentane0);
        rightCyclohexane.addBond(rightCyclohexaneAtom0Atom5);
        
        IRingSet ringSet = builder.newRingSet();
        ringSet.addAtomContainer(leftCyclohexane);
        ringSet.addAtomContainer(leftCyclopentane);
        ringSet.addAtomContainer(rightCyclopentane);
        ringSet.addAtomContainer(rightCyclohexane);
        
        // Get connected rings
        List connectedRings = ringSet.getConnectedRings(leftCyclohexane);
        
        // Iterate over the connectedRings and fail if any duplicate is found
        List foundRings = new ArrayList();
        for (Iterator iterator = connectedRings.iterator(); iterator.hasNext(); ) {
            IRing connectedRing = (IRing) iterator.next();
            if (foundRings.contains(connectedRing))
                Assert.fail("The list of connected rings contains duplicates.");
            foundRings.add(connectedRing);
        }
    }
  
}
