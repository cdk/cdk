/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ISingleElectron;

/**
 * Checks the functionality of the SingleElectron class.
 *
 * @see org.openscience.cdk.SingleElectron
 *
 * @cdk.module test-data
 */
public class SingleElectronTest extends CDKTestCase {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testSingleElectron() {
        ISingleElectron radical = builder.newSingleElectron();
        Assert.assertTrue(radical.getAtom() == null);
        Assert.assertEquals(1, radical.getElectronCount().intValue());
    }
    
    @Test public void testSingleElectron_IAtom() {
        IAtom atom = builder.newAtom("N");
        ISingleElectron radical = builder.newSingleElectron(atom);
        Assert.assertEquals(1, radical.getElectronCount().intValue());
        Assert.assertEquals(atom, radical.getAtom());
        Assert.assertTrue(radical.contains(atom));
    }

    @Test public void testGetElectronCount() {
        ISingleElectron radical = builder.newSingleElectron();
        Assert.assertEquals(1, radical.getElectronCount().intValue());
    }

    @Test public void testContains_IAtom() {
        IAtom atom = builder.newAtom("N");
        ISingleElectron radical = builder.newSingleElectron(atom);
        Assert.assertTrue(radical.contains(atom));
    }
    
    @Test public void testSetAtom_IAtom() {
        IAtom atom = builder.newAtom("N");
        ISingleElectron radical = builder.newSingleElectron();
        Assert.assertNull(radical.getAtom());
        radical.setAtom(atom);
        Assert.assertEquals(atom, radical.getAtom());
    }

    @Test public void testGetAtom() {
        IAtom atom = builder.newAtom("N");
        ISingleElectron radical = builder.newSingleElectron(atom);
        Assert.assertEquals(atom, radical.getAtom());
    }
    
    @Test public void testClone() throws Exception {
        ISingleElectron radical = builder.newSingleElectron();
        Object clone = radical.clone();
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone instanceof ISingleElectron);
    }
    
    @Test public void testClone_IAtom() throws Exception {
        IAtom atom = builder.newAtom("N");
        ISingleElectron radical = builder.newSingleElectron();
        radical.setAtom(atom);
        
        // test cloning of atom
        ISingleElectron clone = (ISingleElectron)radical.clone();
        Assert.assertNotSame(atom, clone.getAtom());
    }
    
    /** Test for RFC #9 */
    @Test public void testToString() {
        ISingleElectron radical = builder.newSingleElectron();
        String description = radical.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
}
