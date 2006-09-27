/* $RCSfile$
 * $Author: egonw $    
 * $Date: 2006-09-20 10:48:23 +0000 (Wed, 20 Sep 2006) $    
 * $Revision: 6963 $
 * 
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
import org.openscience.cdk.protein.data.PDBStructure;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of the PDBStructure class.
 *
 * @cdk.module test-data
 *
 * @see PDBStructure
 */
public class PDBStructureTest extends CDKTestCase {
	
	protected IChemObjectBuilder builder;

	public PDBStructureTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(PDBStructureTest.class);
    }
    
	public void testPDBStructure() {
		PDBStructure structure = new PDBStructure();
		assertNotNull(structure);
	}
	
	public void testGetEndChainID() {
    }

    public void testSetEndChainID_char(char endChainID) {
    }

    public void testGetEndInsertionCode() {
    }

    public void testSetEndInsertionCode_char(char endInsertionCode) {
    }

    public void testGetEndSequenceNumber() {
    }

    public void testSetEndSequenceNumber_int(int endSequenceNumber) {
    }

    public void testGetStartChainID() {
    }

    public void testSetStartChainID_char(char startChainID) {
    }

    public void testGetStartInsertionCode() {
    }

    public void testSetStartInsertionCode_char(char startInsertionCode) {
    }

    public void testGetStartSequenceNumber() {
    }

    public void testSetStartSequenceNumber_int(int startSequenceNumber) {
    }

    public void testGetStructureType() {
    }

    public void testSetStructureType_String(String structureType) {
    }
    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
    	PDBStructure structure = new PDBStructure();
        String description = structure.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue('\n' != description.charAt(i));
            assertTrue('\r' != description.charAt(i));
        }
    }
}
