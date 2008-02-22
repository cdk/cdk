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

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Mapping;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * Checks the functionality of the Mapping class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.Mapping
 */
public class MappingTest extends CDKTestCase {

    public MappingTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(MappingTest.class);
    }
    
    public void testMapping_IChemObject_IChemObject() {
        Mapping mapping = new Mapping(new Atom(), new Atom());
        assertNotNull(mapping);
    }
    
    /**
     * Method to test whether the class complies with RFC #9.
     */
    public void testToString() {
        Mapping mapping = new Mapping(new Atom(), new Atom());
        String description = mapping.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

	public void testClone() throws Exception {
        Mapping mapping = new Mapping(new Atom(), new Atom());
        Object clone = mapping.clone();
        assertTrue(clone instanceof Mapping);
    }    
        
	public void testGetChemObject_int() {
		Atom atom0 = new Atom();
		Atom atom1 = new Atom();
		Mapping mapping = new Mapping(atom0, atom1);
		assertEquals(atom0, mapping.getChemObject(0));
		assertEquals(atom1, mapping.getChemObject(1));
	}
	
    public void testRelatedChemObjects() {
    	Atom atom0 = new Atom();
		Atom atom1 = new Atom();
		Mapping mapping = new Mapping(atom0, atom1);

		java.util.Iterator iter = mapping.relatedChemObjects();
		assertTrue(iter.hasNext());
		assertEquals(atom0, (Atom)iter.next());
		assertTrue(iter.hasNext());
		assertEquals(atom1, (Atom)iter.next());
        assertFalse(iter.hasNext());
    }

    public void testClone_ChemObject() throws Exception {
		Mapping mapping = new Mapping(new Atom(), new Atom());

		Mapping clone = (Mapping)mapping.clone();
        //IChemObject[] map = mapping.getRelatedChemObjects();
        //IChemObject[] mapClone = clone.getRelatedChemObjects();
        //assertEquals(map.length, mapClone.length);
		for (int f = 0; f < 2; f++) {
			for (int g = 0; g < 2; g++) {
				assertNotNull(mapping.getChemObject(f));
				assertNotNull(clone.getChemObject(g));
				assertNotSame(mapping.getChemObject(f), clone.getChemObject(g));
			}
		}        
    }
}
