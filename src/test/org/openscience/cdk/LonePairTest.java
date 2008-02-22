/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ILonePair;

/**
 * Checks the functionality of the LonePair class.
 *
 * @see org.openscience.cdk.LonePair
 *
 * @cdk.module test-data
 */
public class LonePairTest extends NewCDKTestCase {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testLonePair() {
        ILonePair lp = builder.newLonePair();
        Assert.assertTrue(lp.getAtom() == null);
        Assert.assertEquals(2, lp.getElectronCount());
    }
    
    @Test public void testLonePair_IAtom() {
        IAtom atom = builder.newAtom("N");
        ILonePair lp = builder.newLonePair(atom);
        Assert.assertEquals(2, lp.getElectronCount());
        Assert.assertEquals(atom, lp.getAtom());
        Assert.assertTrue(lp.contains(atom));
    }
    
    @Test public void testSetAtom_IAtom() {
        IAtom atom = builder.newAtom("N");
        ILonePair lp = builder.newLonePair();
        lp.setAtom(atom);
        Assert.assertEquals(atom, lp.getAtom());
    }
    
    @Test public void testGetAtom() {
        IAtom atom = builder.newAtom("N");
        ILonePair lp = builder.newLonePair();
        Assert.assertNull(lp.getAtom());
        lp.setAtom(atom);
        Assert.assertEquals(atom, lp.getAtom());
    }
    
    @Test public void testGetElectronCount() {
        ILonePair lp = builder.newLonePair();
        Assert.assertEquals(2, lp.getElectronCount());
        
        lp = builder.newLonePair(builder.newAtom("N"));
        Assert.assertEquals(2, lp.getElectronCount());
    }
    
    @Test public void testContains_IAtom() {
        IAtom atom = builder.newAtom("N");
        ILonePair lp = builder.newLonePair();
        lp.setAtom(atom);
        Assert.assertTrue(lp.contains(atom));
    }
    
    @Test public void testClone() throws Exception {
        ILonePair lp = builder.newLonePair();
        Object clone = lp.clone();
        Assert.assertTrue(clone instanceof ILonePair);
    }
    
    @Test public void testClone_IAtom() throws Exception {
        IAtom atom = builder.newAtom("N");
        ILonePair lp = builder.newLonePair();
        lp.setAtom(atom);
        
        // test cloning of atom
        ILonePair clone = (ILonePair)lp.clone();
        Assert.assertNotSame(atom, clone.getAtom());
    }
    
    /** Test for RFC #9 */
    @Test public void testToString() {
        ILonePair lp = builder.newLonePair();
        String description = lp.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
}
