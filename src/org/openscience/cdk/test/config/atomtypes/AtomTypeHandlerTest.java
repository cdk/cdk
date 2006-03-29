/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2005-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.config.atomtypes;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.atomtypes.AtomTypeHandler;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the funcitonality of the IsotopeFactory
 *
 * @cdk.module test
 */
public class AtomTypeHandlerTest extends CDKTestCase {
     
	public AtomTypeHandlerTest(String name) {
		super(name);
	}
	
	public void setUp() {}
	
	public static Test suite() {
		return new TestSuite(AtomTypeHandlerTest.class);
	}

    // serious testing is done in AtomTypeFactoryTest; the factory
    // requires this class to work properly. But nevertheless:

    public void testAtomTypeHandler_IChemObjectBuilder() {
        AtomTypeHandler handler = new AtomTypeHandler(new ChemObject().getBuilder());
    }
    
    public void testGetAtomTypes() {
        AtomTypeHandler handler = new AtomTypeHandler(new ChemObject().getBuilder());
        // nothing is read
        assertNull(handler.getAtomTypes());
    }
    
    public void testStartDocument() {
        AtomTypeHandler handler = new AtomTypeHandler(new ChemObject().getBuilder());
        // nothing is read, but Vector is initialized
        assertNull(handler.getAtomTypes());
    }
    
    public void testCharacters_arraychar_int_int() {
        // nothing I can test here that AtomTypeFactoryTest doesn't do
    }
    
    public void testStartElement_String_String_String_Attributes() {
        // nothing I can test here that AtomTypeFactoryTest doesn't do
    }
    
    public void testEndElement_String_String_String() {
        // nothing I can test here that AtomTypeFactoryTest doesn't do
    }
    
}
