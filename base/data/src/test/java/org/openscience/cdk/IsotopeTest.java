/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *                    2012  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.test.interfaces.AbstractIsotopeTest;
import org.openscience.cdk.test.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the Isotope class.
 *
 *
 * @see org.openscience.cdk.Isotope
 */
class IsotopeTest extends AbstractIsotopeTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new Isotope("C");
            }
        });
    }

    @Test
    void testIsotope_String() {
        IIsotope i = new Isotope("C");
        Assertions.assertEquals("C", i.getSymbol());
    }

    @Test
    void testIsotope_IElement() {
        IElement element = new Element("C");
        IIsotope i = new Isotope(element);
        Assertions.assertEquals("C", i.getSymbol());
    }

    @Test
    void testIsotope_int_String_int_double_double() {
        IIsotope i = new Isotope(6, "C", 12, 12.001, 80.0);
        Assertions.assertEquals(12, i.getMassNumber().intValue());
        Assertions.assertEquals("C", i.getSymbol());
        Assertions.assertEquals(6, i.getAtomicNumber().intValue());
        Assertions.assertEquals(12.001, i.getExactMass(), 0.001);
        Assertions.assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }

    @Test
    void testIsotope_String_int() {
        IIsotope i = new Isotope("C", 12);
        Assertions.assertEquals(12, i.getMassNumber().intValue());
        Assertions.assertEquals("C", i.getSymbol());
    }

    @Test
    void testIsotope_int_String_double_double() {
        IIsotope i = new Isotope(6, "C", 12.001, 80.0);
        Assertions.assertEquals("C", i.getSymbol());
        Assertions.assertEquals(6, i.getAtomicNumber().intValue());
        Assertions.assertEquals(12.001, i.getExactMass(), 0.001);
        Assertions.assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }

    @Test
    void testCompare_MassNumber() {
        Isotope iso = new Isotope("C");
        iso.setMassNumber(12);
        Isotope iso2 = new Isotope("C");
        iso2.setMassNumber((int) 12.0);
        Assertions.assertTrue(iso.compare(iso2));
    }

    @Test
    void testCompare_MassNumberIntegers() {
        Isotope iso = new Isotope("C");
        iso.setMassNumber(12);
        Isotope iso2 = new Isotope("C");
        iso2.setMassNumber(12);
        Assertions.assertTrue(iso.compare(iso2));
    }

    @Test
    void testCompare_MassNumberIntegers_ValueOf() {
        Isotope iso = new Isotope("C");
        iso.setMassNumber(12);
        Isotope iso2 = new Isotope("C");
        iso2.setMassNumber(12);
        Assertions.assertTrue(iso.compare(iso2));
    }

    @Test
    void testCompare_ExactMass() {
        Isotope iso = new Isotope("C");
        iso.setExactMass(12.000000);
        Isotope iso2 = new Isotope("C");
        iso2.setExactMass(12.0);
        Assertions.assertTrue(iso.compare(iso2));
    }

    @Test
    void testCompare_NaturalAbundance() {
        Isotope iso = new Isotope("C");
        iso.setNaturalAbundance(12.000000);
        Isotope iso2 = new Isotope("C");
        iso2.setNaturalAbundance(12.0);
        Assertions.assertTrue(iso.compare(iso2));
    }
}
