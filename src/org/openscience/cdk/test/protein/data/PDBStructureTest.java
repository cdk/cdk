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
		PDBStructure structure = new PDBStructure();
		Assert.assertNotNull(structure);
	}
	
	@Test public void testGetEndChainID() {
		Assert.fail("Not tested yet");
    }

    @Test public void testSetEndChainID_char() {
		Assert.fail("Not tested yet");
    }

    @Test public void testGetEndInsertionCode() {
		Assert.fail("Not tested yet");
    }

    @Test public void testSetEndInsertionCode_char() {
		Assert.fail("Not tested yet");
    }

    @Test public void testGetEndSequenceNumber() {
		Assert.fail("Not tested yet");
    }

    @Test public void testSetEndSequenceNumber_int() {
		Assert.fail("Not tested yet");
    }

    @Test public void testGetStartChainID() {
		Assert.fail("Not tested yet");
    }

    @Test public void testSetStartChainID_char() {
		Assert.fail("Not tested yet");
    }

    @Test public void testGetStartInsertionCode() {
		Assert.fail("Not tested yet");
    }

    @Test public void testSetStartInsertionCode_char() {
		Assert.fail("Not tested yet");
    }

    @Test public void testGetStartSequenceNumber() {
		Assert.fail("Not tested yet");
    }

    @Test public void testSetStartSequenceNumber_int() {
		Assert.fail("Not tested yet");
    }

    @Test public void testGetStructureType() {
		Assert.fail("Not tested yet");
    }

    @Test public void testSetStructureType_String() {
		Assert.fail("Not tested yet");
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
