/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.*;
import java.util.*;
import junit.framework.*;
import javax.vecmath.*;

/**
 * Checks the funcitonality of the AtomType class.
 *
 * @see org.openscience.cdk.AtomType
 */
public class AtomTypeTest extends TestCase {

    public AtomTypeTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(AtomTypeTest.class);
    }
    
    public void testAtomType_String() {
        AtomType at = new AtomType("C");
        assertEquals("C", at.getSymbol());
    }

    public void testAtomType_String_String() {
        AtomType at = new AtomType("C4", "C");
        assertEquals("C", at.getSymbol());
        assertEquals("C4", at.getAtomTypeName());
    }
    
    public void testSetAtomTypeName() {
        AtomType at = new AtomType("C");
        at.setAtomTypeName("C4");
        assertEquals("C4", at.getAtomTypeName());
    }

    public void testSetMaxBondOrder() {
        AtomType at = new AtomType("C");
        at.setMaxBondOrder(3.0);
        assertTrue(3.0 == at.getMaxBondOrder());
    }

    public void testSetMaxBondOrderSum() {
        AtomType at = new AtomType("C");
        at.setMaxBondOrder(4.0);
        assertTrue(4.0 == at.getMaxBondOrder());
    }
    
    public void testCompare() {
        AtomType at = new AtomType("C4", "C");
        AtomType at2 = new AtomType("C3", "C");
        assertTrue(!at.compare("C4"));
        assertTrue(!at.compare(at2));
    }
    
    public void testSetVanderwaalsRadius() {
        AtomType at = new AtomType("C");
        at.setVanderwaalsRadius(1.0);
        assertTrue(1.0 == at.getVanderwaalsRadius());
    }
    
    public void testSetCovalentRadius() {
        AtomType at = new AtomType("C");
        at.setCovalentRadius(1.0);
        assertTrue(1.0 == at.getCovalentRadius());
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone() {
        AtomType at = new AtomType("C");
        Object clone = at.clone();
        assertTrue(clone instanceof AtomType);
        AtomType copy = (AtomType)clone;
        assertTrue(at.compare(copy));
    }
    
    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        AtomType at = new AtomType("C");
        String description = at.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
