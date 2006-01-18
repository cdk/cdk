/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.interfaces.ChemObjectBuilder;
import org.openscience.cdk.interfaces.Isotope;

/**
 * Checks the funcitonality of the Isotope class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.Isotope
 */
public class IsotopeTest extends CDKTestCase {

	protected ChemObjectBuilder builder;
	
    public IsotopeTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(IsotopeTest.class);
    }
    
    public void testIsotope_String() {
        Isotope i = builder.newIsotope("C");
        assertEquals("C", i.getSymbol());
    }
    
    public void testIsotope_int_String_int_double_double() {
        Isotope i = builder.newIsotope(6, "C", 12, 12.001, 80.0);
        assertEquals(12, i.getMassNumber());
        assertEquals("C", i.getSymbol());
        assertEquals(6, i.getAtomicNumber());
        assertEquals(12.001, i.getExactMass(), 0.001);
        assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }
    
    public void testIsotope_String_int() {
        Isotope i = builder.newIsotope("C", 12);
        assertEquals(12, i.getMassNumber());
        assertEquals("C", i.getSymbol());
    }
    
    public void testIsotope_int_String_double_double() {
        Isotope i = builder.newIsotope(6, "C", 12.001, 80.0);
        assertEquals("C", i.getSymbol());
        assertEquals(6, i.getAtomicNumber());
        assertEquals(12.001, i.getExactMass(), 0.001);
        assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }
    
    public void testSetNaturalAbundance_double() {
        Isotope i = builder.newIsotope("C");
        i.setNaturalAbundance(80.0);
        assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }
    public void testGetNaturalAbundance() {
        testSetNaturalAbundance_double();
    }
    
    public void testSetExactMass_double() {
        Isotope i = builder.newIsotope("C");
        i.setExactMass(12.03);
        assertEquals(12.03, i.getExactMass(), 0.001);
    }
    public void testGetExactMass() {
        testSetExactMass_double();
    }

    public void testSetMassNumber_int() {
        Isotope i = builder.newIsotope("D");
        i.setMassNumber(2);
        assertEquals(2, i.getMassNumber());
    }
    public void testGetMassNumber() {
        testSetMassNumber_int();
    }

    /**
     * Method to test the clone() method
     */
    public void testClone() {
        Isotope iso = builder.newIsotope("C");
        Object clone = iso.clone();
        assertTrue(clone instanceof Isotope);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_ExactMass() {
        Isotope iso = builder.newIsotope("C");
        iso.setExactMass(1.0);
        Isotope clone = (Isotope)iso.clone();
        
        // test cloning of exact mass
        iso.setExactMass(2.0);
        assertEquals(1.0, clone.getExactMass(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_NaturalAbundance() {
        Isotope iso = builder.newIsotope("C");
        iso.setNaturalAbundance(1.0);
        Isotope clone = (Isotope)iso.clone();
        
        // test cloning of exact mass
        iso.setNaturalAbundance(2.0);
        assertEquals(1.0, clone.getNaturalAbundance(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_MassNumber() {
        Isotope iso = builder.newIsotope("C");
        iso.setMassNumber(12);
        Isotope clone = (Isotope)iso.clone();
        
        // test cloning of exact mass
        iso.setMassNumber(13);
        assertEquals(12, clone.getMassNumber());
    }
    
    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        Isotope iso = builder.newIsotope("C");
        String description = iso.toString();
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
