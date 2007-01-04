/* $RCSfile$
 * $Author: egonw $    
 * $Date: 2006-09-20 10:48:23 +0000 (Wed, 20 Sep 2006) $    
 * $Revision: 6963 $
 * 
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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

package org.openscience.cdk.test.protein.data;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPDBMonomer;
import org.openscience.cdk.protein.data.PDBMonomer;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of the PDBMonomer class.
 *
 * @cdk.module test-data
 *
 * @see PDBPolymer
 */
public class PDBMonomerTest extends CDKTestCase {
	
	protected IChemObjectBuilder builder;

	public PDBMonomerTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(PDBMonomerTest.class);
    }
    
	public void testPDBMonomer() {
		PDBMonomer monomer = new PDBMonomer();
		assertNotNull(monomer);
		assertEquals(monomer.getICode(), null);
		
	}
	
	public void testSetICode_String() {
	}
	
	public void testGetICode() {
	}
	
	public void testSetChainID_String() {
	}
	
	public void testGetChainID() {
	}
	
	public void testToString() {
		IPDBMonomer monomer = builder.newPDBMonomer();
        String description = monomer.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue('\n' != description.charAt(i));
            assertTrue('\r' != description.charAt(i));
        }
	}

}
