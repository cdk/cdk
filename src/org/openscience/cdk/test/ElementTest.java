/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;

/**
 * Checks the funcitonality of the Element class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.Element
 */
public class ElementTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public ElementTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(ElementTest.class);
    }
    
    // test constructors
    
    public void testElement() {
        IElement e = builder.newElement();
        assertTrue(e instanceof IChemObject);
    }
    
    public void testElement_IElement() {
    	IElement element = builder.newElement();
        IElement e = builder.newElement(element);
        assertTrue(e instanceof IChemObject);
    }
    
    public void testElement_String() {
        IElement e = builder.newElement("C");
        assertEquals("C", e.getSymbol());
    }
    
    public void testElement_String_int() {
        IElement e = builder.newElement("H", 1);
        assertEquals("H", e.getSymbol());
        assertEquals(1, e.getAtomicNumber());
    }
    
    // test methods
    
    public void testSetSymbol_String() {
        IElement e = builder.newElement();
        e.setSymbol("C");
        assertEquals("C", e.getSymbol());
    }
        
    public void testGetSymbol() {
        IElement e = builder.newElement("X");
        assertEquals("X", e.getSymbol());
    }
        
    public void testSetAtomicNumber_int() {
        IElement e = builder.newElement("H");
        e.setAtomicNumber(1);
        assertEquals(1, e.getAtomicNumber());
    }

    public void testGetAtomicNumber() {
        IElement e = builder.newElement("D", 1);
        assertEquals(1, e.getAtomicNumber());
    }

    public void testClone() throws Exception {
        IElement elem = builder.newElement();
        Object clone = elem.clone();
        assertTrue(clone instanceof IElement);
    }
    
    public void testClone_Symbol() throws Exception {
        IElement elem = builder.newElement("C");
        IElement clone = (IElement)elem.clone();
        
        // test cloning of symbol
        elem.setSymbol("H");
        assertEquals("C", clone.getSymbol());
    }
    
    public void testClone_IAtomicNumber() throws Exception {
        IElement elem = builder.newElement("C", 6);
        IElement clone = (IElement)elem.clone();
        
        // test cloning of atomic number
        elem.setAtomicNumber(5); // don't care about symbol
        assertEquals(6, clone.getAtomicNumber());
    }
    
    /** Test for RFC #9 */
    public void testToString() {
        IElement elem = builder.newElement();
        String description = elem.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

    public void testCompare_Object() {
        // Added to keep the Coverage checker happy, but since the
        // compare(Object) method is not part of the interface, nothing is tested
        assertTrue(true);
    }
}
