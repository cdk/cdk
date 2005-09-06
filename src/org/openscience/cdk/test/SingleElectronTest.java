/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.SingleElectron;

/**
 * Checks the funcitonality of the SingleElectron class.
 *
 * @see org.openscience.cdk.SingleElectron
 *
 * @cdk.module test
 */
public class SingleElectronTest extends CDKTestCase {

    public SingleElectronTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(SingleElectronTest.class);
    }
    
    public void testSingleElectron() {
        SingleElectron radical = new SingleElectron();
        assertTrue(radical.getAtom() == null);
        assertEquals(1, radical.getElectronCount());
    }
    
    public void testSingleElectron_Atom() {
        Atom atom = new Atom("N");
        SingleElectron radical = new SingleElectron(atom);
        assertEquals(1, radical.getElectronCount());
        assertEquals(atom, radical.getAtom());
        assertTrue(radical.contains(atom));
    }

    public void testGetElectronCount() {
        SingleElectron radical = new SingleElectron();
        assertEquals(1, radical.getElectronCount());
    }

    public void testContains_Atom() {
        Atom atom = new Atom("N");
        SingleElectron radical = new SingleElectron(atom);
        assertTrue(radical.contains(atom));
    }
    
    public void testSetAtom_Atom() {
        Atom atom = new Atom("N");
        SingleElectron radical = new SingleElectron();
        assertNull(radical.getAtom());
        radical.setAtom(atom);
        assertEquals(atom, radical.getAtom());
    }

    public void testGetAtom() {
        Atom atom = new Atom("N");
        SingleElectron radical = new SingleElectron(atom);
        assertEquals(atom, radical.getAtom());
    }
    
    public void testClone() {
        SingleElectron radical = new SingleElectron();
        Object clone = radical.clone();
        assertNotNull(clone);
        assertTrue(clone instanceof SingleElectron);
    }
    
    public void testClone_Atom() {
        Atom atom = new Atom("N");
        SingleElectron radical = new SingleElectron();
        radical.setAtom(atom);
        
        // test cloning of atom
        SingleElectron clone = (SingleElectron)radical.clone();
        assertNotSame(atom, clone.getAtom());
    }
    
    /** Test for RFC #9 */
    public void testToString() {
        SingleElectron radical = new SingleElectron();
        String description = radical.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
