/* $Revision: 8973 $ $Author: egonw $ $Date: 2007-09-26 13:47:29 +0200 (Wed, 26 Sep 2007) $
 * 
 * Copyright (C) 2006-2007  The Chemistry Development Kit Project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.tools.manipulator;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * @cdk.module test-standard
 */
public class RingSetManipulatorTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
	private IRingSet ringset = null;
	private IAtom ring1Atom1 = null;
	private IAtom ring1Atom3 = null;
	private IAtom ring2Atom3 = null;
	private IBond bondRing2Ring3 = null;
	private IRing ring2 = null;
	private IRing ring3 = null;
	
	public RingSetManipulatorTest(String name) {
        super(name);
    }
	
	public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
       	ringset = builder.newRingSet();
       	ring1Atom1 = builder.newAtom("C"); // rather artificial molecule
        IAtom ring1Atom2 = builder.newAtom("C");
        ring1Atom3 = builder.newAtom("C");
        IAtom ring2Atom1 = builder.newAtom("C");
        IAtom ring2Atom2 = builder.newAtom("C");
        ring2Atom3 = builder.newAtom("C");
        IAtom ring3Atom3 = builder.newAtom("C");
        IAtom ring3Atom4 = builder.newAtom("C");
        
        IAtom ring4Atom1 = builder.newAtom("C");
        IAtom ring4Atom2 = builder.newAtom("C");
        
        IBond ring1Bond1 = builder.newBond(ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = builder.newBond(ring1Atom2, ring1Atom3);
        IBond ring1Bond3 = builder.newBond(ring1Atom3, ring1Atom1);
        bondRing2Ring3 = builder.newBond(ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = builder.newBond(ring2Atom2, ring2Atom3);
        IBond ring2Bond3 = builder.newBond(ring2Atom3, ring2Atom1, IBond.Order.DOUBLE);
        IBond ring3Bond2 = builder.newBond(ring2Atom2, ring3Atom3);
        IBond bondRing3Ring4 = builder.newBond(ring3Atom3, ring3Atom4);
        IBond ring3Bond4 = builder.newBond(ring3Atom4, ring2Atom1);
        IBond ring4Bond1 = builder.newBond(ring4Atom1, ring4Atom2);
        IBond ring4Bond2 = builder.newBond(ring4Atom2, ring3Atom3);
        IBond ring4Bond3 = builder.newBond(ring3Atom4, ring4Atom1);
        
        IRing ring1 = builder.newRing();
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(ring1Atom3);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);

        ring2 = builder.newRing();
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(ring2Atom3);
        ring2.addBond(bondRing2Ring3);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        
        ring3 = builder.newRing();
        ring3.addAtom(ring2Atom1);
        ring3.addAtom(ring2Atom2);
        ring3.addAtom(ring3Atom3);
        ring3.addAtom(ring3Atom4);
        ring3.addBond(bondRing2Ring3);
        ring3.addBond(ring3Bond2);
        ring3.addBond(bondRing3Ring4);
        ring3.addBond(ring3Bond4);
        
        IRing ring4 = builder.newRing();
        ring4.addAtom(ring4Atom1);
        ring4.addAtom(ring4Atom2);
        ring4.addAtom(ring3Atom3);
        ring4.addAtom(ring3Atom4);
        ring4.addBond(bondRing3Ring4);
        ring4.addBond(ring4Bond1);
        ring4.addBond(ring4Bond2);
        ring4.addBond(ring4Bond3);
        
        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);
        ringset.addAtomContainer(ring3);
        ringset.addAtomContainer(ring4);
    }
	
	public static Test suite() {
		return new TestSuite(RingSetManipulatorTest.class);
	}
	
    public void testIsSameRing_IRingSet_IAtom_IAtom() {
        assertTrue(RingSetManipulator.isSameRing(ringset, ring1Atom1, ring1Atom3));
        assertFalse(RingSetManipulator.isSameRing(ringset, ring1Atom1, ring2Atom3));
    }

    public void testRingAlreadyInSet_IRing_IRingSet() {
        IRing r1 = builder.newRing(5, "C");
        IRing r2 = builder.newRing(3, "C");
        
        IRingSet rs = builder.newRingSet();
        assertFalse(RingSetManipulator.ringAlreadyInSet(r1, rs));
        assertFalse(RingSetManipulator.ringAlreadyInSet(r2, rs));
        
        rs.addAtomContainer(r1);
        assertTrue(RingSetManipulator.ringAlreadyInSet(r1, rs));
        assertFalse(RingSetManipulator.ringAlreadyInSet(r2, rs));
        
        rs.addAtomContainer(r2);
        assertTrue(RingSetManipulator.ringAlreadyInSet(r1, rs));
        assertTrue(RingSetManipulator.ringAlreadyInSet(r2, rs));
    }
    
    public void testGetAllAtomContainers_IRingSet()
    {
    	IRingSet rs = builder.newRingSet();
    	rs.addAtomContainer(builder.newRing());
    	rs.addAtomContainer(builder.newRing());
    	List list = RingSetManipulator.getAllAtomContainers(rs);
    	assertEquals(2, list.size());
    }
    
    public void testGetAllInOneContainer_IRingSet()
    {
    	IRingSet rs = builder.newRingSet();
    	IAtomContainer ac1 = builder.newRing();
    	ac1.addAtom(builder.newAtom("O"));
    	rs.addAtomContainer(ac1);
    	IAtomContainer ac2 = builder.newRing();
    	ac2.addAtom(builder.newAtom("C"));
    	ac2.addAtom(builder.newAtom("C"));
    	ac2.addBond(0, 1, IBond.Order.DOUBLE);
    	rs.addAtomContainer(ac2);
    	assertEquals(3, RingSetManipulator.getAtomCount(rs));
    	assertEquals(1, RingSetManipulator.getBondCount(rs));
    }
    
    public void testGetHeaviestRing_IRingSet_IBond()
    {
    	IRing ring = RingSetManipulator.getHeaviestRing(ringset, bondRing2Ring3);
    	assertEquals(ring2, ring);
    }
    
    public void testGetMostComplexRing_IRingSet()
    {
    	IRing ring = RingSetManipulator.getMostComplexRing(ringset);
    	assertEquals(ring3, ring);
    }
    
    public void testSort_IRingSet()
    {
    	RingSetManipulator.sort(ringset);
    	assertEquals(4, ringset.getAtomContainerCount());
    	int currentSize = ringset.getAtomContainer(0).getAtomCount();
    	for (int i = 1; i < ringset.getAtomContainerCount(); ++i) {
    		assertTrue(ringset.getAtomContainer(i).getAtomCount() >= currentSize);
    		currentSize = ringset.getAtomContainer(i).getAtomCount();
    	}
    }
    
	
}
