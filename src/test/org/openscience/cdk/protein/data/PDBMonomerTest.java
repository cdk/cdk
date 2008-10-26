/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
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
package org.openscience.cdk.protein.data;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPDBMonomer;
import org.openscience.cdk.protein.data.PDBMonomer;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.CDKTestCase;

/**
 * Checks the functionality of the PDBMonomer class.
 *
 * @cdk.module test-data
 *
 * @see PDBPolymer
 */
public class PDBMonomerTest extends CDKTestCase {
	
	protected static IChemObjectBuilder builder;

    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }
    
	@Test public void testPDBMonomer() {
		PDBMonomer monomer = new PDBMonomer();
		Assert.assertNotNull(monomer);
		Assert.assertEquals(monomer.getICode(), null);
	}
	
	@Test public void testSetICode_String() {
		PDBMonomer monomer = new PDBMonomer();
		monomer.setICode(null);
		Assert.assertNull(monomer.getICode());
	}
	
	@Test public void testGetICode() {
		PDBMonomer monomer = new PDBMonomer();
		Assert.assertNull(monomer.getICode());
		monomer.setICode("iCode");
		Assert.assertNotNull(monomer.getICode());
		Assert.assertEquals("iCode", monomer.getICode());
	}
	
	@Test public void testSetChainID_String() {
		PDBMonomer monomer = new PDBMonomer();
		monomer.setChainID(null);
		Assert.assertNull(monomer.getChainID());
	}
	
	@Test public void testGetChainID() {
		PDBMonomer monomer = new PDBMonomer();
		Assert.assertNull(monomer.getChainID());
		monomer.setChainID("chainA");
		Assert.assertNotNull(monomer.getChainID());
		Assert.assertEquals("chainA", monomer.getChainID());
	}
	
	@Test public void testSetResSeq_String() {
		PDBMonomer monomer = new PDBMonomer();
		monomer.setResSeq(null);
		Assert.assertNull(monomer.getResSeq());
	}
	
	@Test public void testGetResSeq() {
		PDBMonomer monomer = new PDBMonomer();
		Assert.assertNull(monomer.getResSeq());
		monomer.setResSeq("reqSeq");
		Assert.assertNotNull(monomer.getResSeq());
		Assert.assertEquals("reqSeq", monomer.getResSeq());
	}
	
	@Test public void testToString() {
		IPDBMonomer monomer = builder.newPDBMonomer();
        String description = monomer.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
	}

}
