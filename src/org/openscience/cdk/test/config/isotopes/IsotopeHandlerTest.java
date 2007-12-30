/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.config.isotopes;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.isotopes.IsotopeHandler;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the funcitonality of the IsotopeFactory
 *
 * @cdk.module test-core
 */
public class IsotopeHandlerTest extends CDKTestCase {
     
	public IsotopeHandlerTest(String name) {
		super(name);
	}
	
	public void setUp() {}
	
	public static Test suite() {
		return new TestSuite(IsotopeHandlerTest.class);
	}

    // serious testing is done in IsotopeFactoryTest; the factory
    // requires this class to work properly. But nevertheless:

    public void testIsotopeHandler_IChemObjectBuilder() {
        IsotopeHandler handler = new IsotopeHandler(new ChemObject().getBuilder());
        assertNotNull(handler);
    }
    
    public void testGetIsotopes() {
        IsotopeHandler handler = new IsotopeHandler(new ChemObject().getBuilder());
        // nothing is read
        assertNotNull(handler);
        assertNull(handler.getIsotopes());
    }
    
    public void testStartDocument() {
        IsotopeHandler handler = new IsotopeHandler(new ChemObject().getBuilder());
        // nothing is read, but Vector is initialized
        assertNotNull(handler);
        assertNull(handler.getIsotopes());
    }
    
    public void testCharacters_arraychar_int_int() {
        // nothing I can test here that IsotopeFactoryTest doesn't do
    	assertTrue(true);
    }

    
    public void testEndElement_String_String_String() {
        // nothing I can test here that IsotopeFactoryTest doesn't do
    	assertTrue(true);
    }
    
}
