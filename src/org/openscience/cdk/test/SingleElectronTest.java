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
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Checks the functionality of the SingleElectron class.
 *
 * @see org.openscience.cdk.SingleElectron
 *
 * @cdk.module test-data
 */
public class SingleElectronTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public SingleElectronTest(String name) {
        super(name);
    }

    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(SingleElectronTest.class);
    }
    
    public void testSingleElectron() {
        ISingleElectron radical = builder.newSingleElectron();
        assertTrue(radical.getAtom() == null);
        assertEquals(1, radical.getElectronCount());
    }
    
    public void testSingleElectron_IAtom() {
        IAtom atom = builder.newAtom("N");
        ISingleElectron radical = builder.newSingleElectron(atom);
        assertEquals(1, radical.getElectronCount());
        assertEquals(atom, radical.getAtom());
        assertTrue(radical.contains(atom));
    }

    public void testGetElectronCount() {
        ISingleElectron radical = builder.newSingleElectron();
        assertEquals(1, radical.getElectronCount());
    }

    public void testContains_IAtom() {
        IAtom atom = builder.newAtom("N");
        ISingleElectron radical = builder.newSingleElectron(atom);
        assertTrue(radical.contains(atom));
    }
    
    public void testSetAtom_IAtom() {
        IAtom atom = builder.newAtom("N");
        ISingleElectron radical = builder.newSingleElectron();
        assertNull(radical.getAtom());
        radical.setAtom(atom);
        assertEquals(atom, radical.getAtom());
    }

    public void testGetAtom() {
        IAtom atom = builder.newAtom("N");
        ISingleElectron radical = builder.newSingleElectron(atom);
        assertEquals(atom, radical.getAtom());
    }
    
    public void testClone() throws Exception {
        ISingleElectron radical = builder.newSingleElectron();
        Object clone = radical.clone();
        assertNotNull(clone);
        assertTrue(clone instanceof ISingleElectron);
    }
    
    public void testClone_IAtom() throws Exception {
        IAtom atom = builder.newAtom("N");
        ISingleElectron radical = builder.newSingleElectron();
        radical.setAtom(atom);
        
        // test cloning of atom
        ISingleElectron clone = (ISingleElectron)radical.clone();
        assertNotSame(atom, clone.getAtom());
    }
    
    /** Test for RFC #9 */
    public void testToString() {
        ISingleElectron radical = builder.newSingleElectron();
        String description = radical.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
