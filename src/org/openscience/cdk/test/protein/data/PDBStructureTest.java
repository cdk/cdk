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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPDBStructure;
import org.openscience.cdk.protein.data.PDBStructure;
import org.openscience.cdk.test.NewCDKTestCase;

/**
 * Checks the functionality of the PDBStructure class.
 *
 * @cdk.module test-data
 *
 * @see PDBStructure
 */
public class PDBStructureTest extends NewCDKTestCase {
	
	protected static IChemObjectBuilder builder;

    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }
    
	@Test public void testPDBStructure() {
		IPDBStructure structure = builder.newPDBStructure();
		Assert.assertNotNull(structure);
	}
	
	@Test public void testGetEndChainID() {
		IPDBStructure structure = builder.newPDBStructure();
		Assert.assertNotNull(structure.getEndChainID());
		// FIXME: test the default value
    }

    @Test public void testSetEndChainID_char() {
    	IPDBStructure structure = builder.newPDBStructure();
    	char endChainID = 'x';
		structure.setEndChainID(endChainID);
		Assert.assertEquals(endChainID, structure.getEndChainID());
    }

    @Test public void testGetEndInsertionCode() {
		IPDBStructure structure = builder.newPDBStructure();
		Assert.assertNotNull(structure.getEndInsertionCode());
		// FIXME: test the default value
    }

    @Test public void testSetEndInsertionCode_char() {
    	IPDBStructure structure = builder.newPDBStructure();
    	char endInsertionCode = 'x';
		structure.setEndInsertionCode(endInsertionCode);
		Assert.assertEquals(endInsertionCode, structure.getEndInsertionCode());
    }

    @Test public void testGetEndSequenceNumber() {
		IPDBStructure structure = builder.newPDBStructure();
		Assert.assertNotNull(structure.getEndSequenceNumber());
		Assert.assertEquals(0, structure.getEndSequenceNumber());
    }

    @Test public void testSetEndSequenceNumber_int() {
    	IPDBStructure structure = builder.newPDBStructure();
    	int endSequenceNumber = 5;
		structure.setEndSequenceNumber(endSequenceNumber);
		Assert.assertEquals(endSequenceNumber, structure.getEndSequenceNumber());
    }

    @Test public void testGetStartChainID() {
		IPDBStructure structure = builder.newPDBStructure();
		Assert.assertNotNull(structure.getStartChainID());
		// FIXME: test the default value
    }

    @Test public void testSetStartChainID_char() {
    	IPDBStructure structure = builder.newPDBStructure();
    	char startChainID = 'x';
		structure.setStartChainID(startChainID);
		Assert.assertEquals(startChainID, structure.getStartChainID());
    }

    @Test public void testGetStartInsertionCode() {
		IPDBStructure structure = builder.newPDBStructure();
		Assert.assertNotNull(structure.getStartInsertionCode());
		// FIXME: test the default value
    }

    @Test public void testSetStartInsertionCode_char() {
    	IPDBStructure structure = builder.newPDBStructure();
    	char startInsertionCode = 'x';
		structure.setStartInsertionCode(startInsertionCode);
		Assert.assertEquals(startInsertionCode, structure.getStartInsertionCode());
    }

    @Test public void testGetStartSequenceNumber() {
		IPDBStructure structure = builder.newPDBStructure();
		Assert.assertNotNull(structure.getStartSequenceNumber());
		Assert.assertEquals(0, structure.getStartSequenceNumber());
    }

    @Test public void testSetStartSequenceNumber_int() {
    	IPDBStructure structure = builder.newPDBStructure();
    	int startSequenceNumber = 5;
		structure.setStartSequenceNumber(startSequenceNumber);
		Assert.assertEquals(startSequenceNumber, structure.getStartSequenceNumber());
    }

    @Test public void testGetStructureType() {
    	IPDBStructure structure = builder.newPDBStructure();
		String type = structure.getStructureType();
		Assert.assertNull(type);
    }

    @Test public void testSetStructureType_String() {
    	IPDBStructure structure = builder.newPDBStructure();
    	String type = "alpha-barrel";
		structure.setStructureType(type);
		Assert.assertEquals(type, structure.getStructureType());
    }

    /**
     * Method to test wether the class complies with RFC #9.
     */
    @Test public void testToString() {
    	PDBStructure structure = new PDBStructure();
        String description = structure.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
    }
}
