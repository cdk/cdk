/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
 * Checks the funcitonality of the Mapping class.
 *
 * @cdk.module test-extra
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
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        Mapping mapping = new Mapping(new Atom(), new Atom());
        String description = mapping.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

	public void testClone() {
        Mapping mapping = new Mapping(new Atom(), new Atom());
        Object clone = mapping.clone();
        assertTrue(clone instanceof Mapping);
    }    
        
    public void testGetRelatedChemObjects() {
        Atom atom1 = new PseudoAtom("M");
        Bond ethene = new Bond(new Atom("C"), new Atom("C"), 2.0); // coordinated with metal
		Mapping mapping = new Mapping(atom1, ethene);

		IChemObject[] map = mapping.getRelatedChemObjects();
        assertNotNull(map[0]);
        assertNotNull(map[1]);
        assertEquals(atom1, map[0]);
        assertEquals(ethene, map[1]);
    }

    public void testClone_ChemObject() {
		Mapping mapping = new Mapping(new Atom(), new Atom());

		Mapping clone = (Mapping)mapping.clone();
        IChemObject[] map = mapping.getRelatedChemObjects();
        IChemObject[] mapClone = clone.getRelatedChemObjects();
        assertEquals(map.length, mapClone.length);
		for (int f = 0; f < map.length; f++) {
			for (int g = 0; g < mapClone.length; g++) {
				assertNotNull(map[f]);
				assertNotNull(mapClone[g]);
				assertNotSame(map[f], mapClone[g]);
			}
		}        
    }
}
