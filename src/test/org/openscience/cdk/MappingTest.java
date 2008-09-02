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

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * Checks the functionality of the Mapping class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.Mapping
 */
public class MappingTest extends NewCDKTestCase {

    @Test public void testMapping_IChemObject_IChemObject() {
        Mapping mapping = new Mapping(new Atom(), new Atom());
        Assert.assertNotNull(mapping);
    }
    
    /**
     * Method to test whether the class complies with RFC #9.
     */
    public void testToString() {
        Mapping mapping = new Mapping(new Atom(), new Atom());
        String description = mapping.toString();
        for (int i=0; i< description.length(); i++) {
        	Assert.assertTrue(description.charAt(i) != '\n');
        	Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

	public void testClone() throws Exception {
        Mapping mapping = new Mapping(new Atom(), new Atom());
        Object clone = mapping.clone();
        Assert.assertTrue(clone instanceof Mapping);
    }    
        
	public void testGetChemObject_int() {
		Atom atom0 = new Atom();
		Atom atom1 = new Atom();
		Mapping mapping = new Mapping(atom0, atom1);
		Assert.assertEquals(atom0, mapping.getChemObject(0));
		Assert.assertEquals(atom1, mapping.getChemObject(1));
	}
	
    public void testRelatedChemObjects() {
    	Atom atom0 = new Atom();
		Atom atom1 = new Atom();
		Mapping mapping = new Mapping(atom0, atom1);

		Iterator<IChemObject> iter = mapping.relatedChemObjects().iterator();
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals(atom0, (Atom)iter.next());
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals(atom1, (Atom)iter.next());
		Assert.assertFalse(iter.hasNext());
    }

    public void testClone_ChemObject() throws Exception {
		Mapping mapping = new Mapping(new Atom(), new Atom());

		Mapping clone = (Mapping)mapping.clone();
        //IChemObject[] map = mapping.getRelatedChemObjects();
        //IChemObject[] mapClone = clone.getRelatedChemObjects();
        //assertEquals(map.length, mapClone.length);
		for (int f = 0; f < 2; f++) {
			for (int g = 0; g < 2; g++) {
				Assert.assertNotNull(mapping.getChemObject(f));
				Assert.assertNotNull(clone.getChemObject(g));
				Assert.assertNotSame(mapping.getChemObject(f), clone.getChemObject(g));
			}
		}        
    }
}
