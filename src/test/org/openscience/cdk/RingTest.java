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
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Checks the functionality of the Ring class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.Ring
 */
public class RingTest extends NewCDKTestCase {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testRing_int_String() {
        IRing r = builder.newRing(5, "C");
        Assert.assertEquals(5, r.getAtomCount());
        Assert.assertEquals(5, r.getBondCount());
    }
    
    @Test public void testRing_int() {
        IRing r = builder.newRing(5); // This does not create a ring!
        Assert.assertEquals(0, r.getAtomCount());
        Assert.assertEquals(0, r.getBondCount());
    }
    
    @Test public void testRing() {
        IRing ring = builder.newRing();
        Assert.assertNotNull(ring);
        Assert.assertEquals(0, ring.getAtomCount());
        Assert.assertEquals(0, ring.getBondCount());
    }

    @Test public void testRing_IAtomContainer() {
        IAtomContainer container = new org.openscience.cdk.AtomContainer();
        container.addAtom(builder.newAtom("C"));
        container.addAtom(builder.newAtom("C"));
        
        IRing ring = builder.newRing(container);
        Assert.assertNotNull(ring);
        Assert.assertEquals(2, ring.getAtomCount());
        Assert.assertEquals(0, ring.getBondCount());
    }

    @Test public void testGetBondOrderSum() {
        IRing r = builder.newRing(5, "C");
        Assert.assertEquals(5, r.getBondOrderSum());

        BondManipulator.increaseBondOrder(r.getBond(0));
        Assert.assertEquals(6, r.getBondOrderSum());

        BondManipulator.increaseBondOrder(r.getBond(0));
        Assert.assertEquals(7, r.getBondOrderSum());

        BondManipulator.increaseBondOrder(r.getBond(4));
        Assert.assertEquals(8, r.getBondOrderSum());
    }
    
    @Test public void testGetRingSize() {
        IRing r = builder.newRing(5, "C");
        Assert.assertEquals(5, r.getRingSize());
    }
    
    @Test public void testGetNextBond_IBond_IAtom() {
        IRing ring = builder.newRing();
        IAtom c1 = builder.newAtom("C");
        IAtom c2 = builder.newAtom("C");
        IAtom c3 = builder.newAtom("C");
        IBond b1 = builder.newBond(c1, c2, IBond.Order.SINGLE);
        IBond b2 = builder.newBond(c3, c2, IBond.Order.SINGLE);
        IBond b3 = builder.newBond(c1, c3, IBond.Order.SINGLE);
        ring.addAtom(c1);
        ring.addAtom(c2);
        ring.addAtom(c3);
        ring.addBond(b1);
        ring.addBond(b2);
        ring.addBond(b3);
        
        Assert.assertEquals(b1, ring.getNextBond(b2,c2));
        Assert.assertEquals(b1, ring.getNextBond(b3,c1));
        Assert.assertEquals(b2, ring.getNextBond(b1,c2));
        Assert.assertEquals(b2, ring.getNextBond(b3,c3));
        Assert.assertEquals(b3, ring.getNextBond(b1,c1));
        Assert.assertEquals(b3, ring.getNextBond(b2,c3));
    }
    
    @Test public void testToString() {
        IRing ring = builder.newRing(5, "C");
        String description = ring.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
}
