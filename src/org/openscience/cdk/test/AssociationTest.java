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

import org.openscience.cdk.Association;
import org.openscience.cdk.Atom;

/**
 * Checks the functionality of the Association class.
 *
 * @cdk.module test-extra
 *
 * @see org.openscience.cdk.Association
 */
public class AssociationTest extends CDKTestCase {

    public AssociationTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(AssociationTest.class);
    }
    
    public void testAssociation() {
        Association association = new Association();
        assertEquals(0, association.getElectronCount());
        assertEquals(0, association.getAtomCount());
    }
    
    public void testAssociation_IAtom_IAtom() {
        Association association = new Association(new Atom("C"), new Atom("C"));
        assertEquals(0, association.getElectronCount());
        assertEquals(2, association.getAtomCount());
    }
    
    /** Test for RFC #9 */
    public void testToString() {
        Association association = new Association();
        String description = association.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

    public void testContains() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Association association = new Association(c, o);
        
        assertTrue(association.contains(c));
        assertTrue(association.contains(o));
    }
    
    public void testGetAtomCount() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Association association = new Association(c, o);
        
        assertEquals(2, association.getAtomCount());
    }
    
    public void testGetAtoms() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        
        Association association = new Association(c, o);
        
        Atom[] atoms = association.getAtoms();
        assertEquals(2, atoms.length);
        assertNotNull(atoms[0]);
        assertNotNull(atoms[1]);
    }
}
