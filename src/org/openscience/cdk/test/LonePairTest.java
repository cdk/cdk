/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Checks the funcitonality of the LonePair class.
 *
 * @see org.openscience.cdk.LonePair
 *
 * @cdk.module test
 */
public class LonePairTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public LonePairTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(LonePairTest.class);
    }
    
    public void testLonePair() {
        ILonePair lp = builder.newLonePair();
        assertTrue(lp.getAtom() == null);
        assertEquals(2, lp.getElectronCount());
    }
    
    public void testLonePair_IAtom() {
        IAtom atom = builder.newAtom("N");
        ILonePair lp = builder.newLonePair(atom);
        assertEquals(2, lp.getElectronCount());
        assertEquals(atom, lp.getAtom());
        assertTrue(lp.contains(atom));
    }
    
    public void testSetAtom_IAtom() {
        IAtom atom = builder.newAtom("N");
        ILonePair lp = builder.newLonePair();
        lp.setAtom(atom);
        assertEquals(atom, lp.getAtom());
    }
    
    public void testGetAtom() {
        IAtom atom = builder.newAtom("N");
        ILonePair lp = builder.newLonePair();
        assertNull(lp.getAtom());
        lp.setAtom(atom);
        assertEquals(atom, lp.getAtom());
    }
    
    public void testGetElectronCount() {
        ILonePair lp = builder.newLonePair();
        assertEquals(2, lp.getElectronCount());
        
        lp = builder.newLonePair(builder.newAtom("N"));
        assertEquals(2, lp.getElectronCount());
    }
    
    public void testContains_IAtom() {
        IAtom atom = builder.newAtom("N");
        ILonePair lp = builder.newLonePair();
        lp.setAtom(atom);
        assertTrue(lp.contains(atom));
    }
    
    public void testClone() {
        ILonePair lp = builder.newLonePair();
        Object clone = lp.clone();
        assertTrue(clone instanceof ILonePair);
    }
    
    public void testClone_IAtom() {
        IAtom atom = builder.newAtom("N");
        ILonePair lp = builder.newLonePair();
        lp.setAtom(atom);
        
        // test cloning of atom
        ILonePair clone = (ILonePair)lp.clone();
        assertNotSame(atom, clone.getAtom());
    }
    
    /** Test for RFC #9 */
    public void testToString() {
        ILonePair lp = builder.newLonePair();
        String description = lp.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
