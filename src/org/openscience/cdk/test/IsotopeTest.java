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
 * Checks the funcitonality of the Isotope class.
 *
 * @cdkPackage test
 *
 * @see org.openscience.cdk.Isotope
 */
public class IsotopeTest extends TestCase {

    public IsotopeTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(IsotopeTest.class);
    }
    
    public void testIsotope() {
        Isotope i = new Isotope("C");
        assertEquals("C", i.getSymbol());
    }
    
    public void testIsotope_int_String_int_double_double() {
        Isotope i = new Isotope(6, "C", 12, 12.001, 80.0);
        assertEquals(12, i.getMassNumber());
        assertEquals("C", i.getSymbol());
        assertEquals(6, i.getAtomicNumber());
        assertTrue(12.001 == i.getExactMass());
        assertTrue(80.0 == i.getNaturalAbundance());
    }
    
    public void testSetNaturalAbundance() {
        Isotope i = new Isotope("C");
        i.setNaturalAbundance(80.0);
        assertTrue(80.0 == i.getNaturalAbundance());
    }
    
    public void testSetExactMass() {
        Isotope i = new Isotope("C");
        i.setExactMass(12.03);
        assertTrue(12.03 == i.getExactMass());
    }

    public void testSetMassNumber() {
        Isotope i = new Isotope("D");
        i.setMassNumber(2);
        assertEquals(2, i.getMassNumber());
    }

    /**
     * Method to test the clone() method
     */
    public void testClone() {
        Isotope iso = new Isotope("C");
        Object clone = iso.clone();
        assertTrue(clone instanceof Isotope);
    }
    
    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        Isotope iso = new Isotope("C");
        String description = iso.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
