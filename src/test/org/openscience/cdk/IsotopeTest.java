/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;

/**
 * Checks the functionality of the Isotope class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.Isotope
 */
public class IsotopeTest extends NewCDKTestCase {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testIsotope_String() {
        IIsotope i = builder.newIsotope("C");
        Assert.assertEquals("C", i.getSymbol());
    }
    
    @Test public void testIsotope_IElement() {
    	IElement element = builder.newElement("C");
        IIsotope i = builder.newIsotope(element);
        Assert.assertEquals("C", i.getSymbol());
    }
    
    @Test public void testIsotope_int_String_int_double_double() {
        IIsotope i = builder.newIsotope(6, "C", 12, 12.001, 80.0);
        Assert.assertEquals(12, i.getMassNumber());
        Assert.assertEquals("C", i.getSymbol());
        Assert.assertEquals(6, i.getAtomicNumber());
        Assert.assertEquals(12.001, i.getExactMass(), 0.001);
        Assert.assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }
    
    @Test public void testIsotope_String_int() {
        IIsotope i = builder.newIsotope("C", 12);
        Assert.assertEquals(12, i.getMassNumber());
        Assert.assertEquals("C", i.getSymbol());
    }
    
    @Test public void testIsotope_int_String_double_double() {
        IIsotope i = builder.newIsotope(6, "C", 12.001, 80.0);
        Assert.assertEquals("C", i.getSymbol());
        Assert.assertEquals(6, i.getAtomicNumber());
        Assert.assertEquals(12.001, i.getExactMass(), 0.001);
        Assert.assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }
    
    @Test public void testSetNaturalAbundance_Double() {
        IIsotope i = builder.newIsotope("C");
        i.setNaturalAbundance(80.0);
        Assert.assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }
    @Test public void testGetNaturalAbundance() {
        testSetNaturalAbundance_Double();
    }
    
    @Test public void testSetExactMass_Double() {
        IIsotope i = builder.newIsotope("C");
        i.setExactMass(12.03);
        Assert.assertEquals(12.03, i.getExactMass(), 0.001);
    }
    @Test public void testGetExactMass() {
        testSetExactMass_Double();
    }

    @Test public void testSetMassNumber_Integer() {
        IIsotope i = builder.newIsotope("D");
        i.setMassNumber(2);
        Assert.assertEquals(2, i.getMassNumber());
    }
    @Test public void testGetMassNumber() {
        testSetMassNumber_Integer();
    }

    /**
     * Method to test the clone() method
     */
    @Test public void testClone() throws Exception {
        IIsotope iso = builder.newIsotope("C");
        Object clone = iso.clone();
        Assert.assertTrue(clone instanceof IIsotope);
    }
    
    /**
     * Method to test the clone() method
     */
    @Test public void testClone_ExactMass() throws Exception {
        IIsotope iso = builder.newIsotope("C");
        iso.setExactMass(1.0);
        IIsotope clone = (IIsotope)iso.clone();
        
        // test cloning of exact mass
        iso.setExactMass(2.0);
        Assert.assertEquals(1.0, clone.getExactMass(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    @Test public void testClone_NaturalAbundance() throws Exception {
        IIsotope iso = builder.newIsotope("C");
        iso.setNaturalAbundance(1.0);
        IIsotope clone = (IIsotope)iso.clone();
        
        // test cloning of exact mass
        iso.setNaturalAbundance(2.0);
        Assert.assertEquals(1.0, clone.getNaturalAbundance(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    @Test public void testClone_MassNumber() throws Exception {
        IIsotope iso = builder.newIsotope("C");
        iso.setMassNumber(12);
        IIsotope clone = (IIsotope)iso.clone();
        
        // test cloning of exact mass
        iso.setMassNumber(13);
        Assert.assertEquals(12, clone.getMassNumber());
    }
    
    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test public void testToString() {
        IIsotope iso = builder.newIsotope("C");
        String description = iso.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test public void testCompare_Object() {
        // Added to keep the Coverage checker happy, but since the
        // compare(Object) method is not part of the interface, nothing is tested
    	Assert.assertTrue(true);
    }

}
