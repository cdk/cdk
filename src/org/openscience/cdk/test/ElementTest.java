/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Element;

/**
 * Checks the funcitonality of the Element class.
 *
 * @cdkPackage test
 *
 * @see org.openscience.cdk.Element
 */
public class ElementTest extends TestCase {

    public ElementTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(ElementTest.class);
    }
    
    // test constructors
    
    public void testElement() {
        Element e = new Element();
        assertTrue(e instanceof ChemObject);
    }
    
    public void testElement1() {
        Element e = new Element("C");
        assertEquals("C", e.getSymbol());
    }
    
    public void testElement2() {
        Element e = new Element("H", 1);
        assertEquals("H", e.getSymbol());
        assertEquals(1, e.getAtomicNumber());
    }
    
    // test methods
    
    public void testSetSymbol() {
        Element e = new Element();
        e.setSymbol("C");
        assertEquals("C", e.getSymbol());
    }
        
    public void testSetAtomicNumber() {
        Element e = new Element("H");
        e.setAtomicNumber(1);
        assertEquals(1, e.getAtomicNumber());
    }

    public void testClone() {
        Element elem = new Element();
        Object clone = elem.clone();
        assertTrue(clone instanceof Element);
    }
    
    /** Test for RFC #9 */
    public void testToString() {
        Element elem = new Element();
        String description = elem.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
