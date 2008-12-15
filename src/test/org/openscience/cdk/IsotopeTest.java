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
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IIsotopeTest;

/**
 * Checks the functionality of the Isotope class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.Isotope
 */
public class IsotopeTest extends IIsotopeTest {

    @BeforeClass public static void setUp() {
    	setBuilder(DefaultChemObjectBuilder.getInstance());
    }

    @Test public void testIsotope_String() {
        IIsotope i = new Isotope("C");
        Assert.assertEquals("C", i.getSymbol());
    }
    
    @Test public void testIsotope_IElement() {
    	IElement element = new Element("C");
        IIsotope i = getBuilder().newIsotope(element);
        Assert.assertEquals("C", i.getSymbol());
    }
    
    @Test public void testIsotope_int_String_int_double_double() {
        IIsotope i = new Isotope(6, "C", 12, 12.001, 80.0);
        Assert.assertEquals(12, i.getMassNumber().intValue());
        Assert.assertEquals("C", i.getSymbol());
        Assert.assertEquals(6, i.getAtomicNumber().intValue());
        Assert.assertEquals(12.001, i.getExactMass(), 0.001);
        Assert.assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }
    
    @Test public void testIsotope_String_int() {
        IIsotope i = new Isotope("C", 12);
        Assert.assertEquals(12, i.getMassNumber().intValue());
        Assert.assertEquals("C", i.getSymbol());
    }
    
    @Test public void testIsotope_int_String_double_double() {
        IIsotope i = new Isotope(6, "C", 12.001, 80.0);
        Assert.assertEquals("C", i.getSymbol());
        Assert.assertEquals(6, i.getAtomicNumber().intValue());
        Assert.assertEquals(12.001, i.getExactMass(), 0.001);
        Assert.assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }

}
