/* Copyright (C) 2013  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IIsotope;

/**
 */
class BODRIsotopeTest {

    @Test
    void testConstructor() {
        IIsotope isotope = new BODRIsotope("C", 6, 12, 12.0, 99.0);
        Assertions.assertEquals("C", isotope.getSymbol());
        Assertions.assertEquals(6, isotope.getAtomicNumber().intValue());
        Assertions.assertEquals(12, isotope.getMassNumber().intValue());
        Assertions.assertEquals(12.0, isotope.getExactMass(), 0.001);
        Assertions.assertEquals(99.0, isotope.getNaturalAbundance(), 0.001);
    }

    @Test
    void testNonclonable() throws CloneNotSupportedException {
        IIsotope isotope = new BODRIsotope("C", 6, 12, 12.0, 99.0);
        IIsotope clone = (IIsotope) isotope.clone();
        Assertions.assertEquals(isotope, clone);
    }

    @Test
    void testImmutable() {
        IIsotope isotope = new BODRIsotope("C", 6, 12, 12.0, 99.0);
        // try mutations
        isotope.setSymbol("N");
        isotope.setAtomicNumber(5);
        isotope.setMassNumber(15);
        isotope.setExactMass(15.000);
        isotope.setNaturalAbundance(0.364);
        // check if original
        Assertions.assertEquals(6, isotope.getAtomicNumber().intValue());
        Assertions.assertEquals(12, isotope.getMassNumber().intValue());
        Assertions.assertEquals(12.0, isotope.getExactMass(), 0.001);
        Assertions.assertEquals(99.0, isotope.getNaturalAbundance(), 0.001);
    }

    @Test
    void untested() {
        Assertions.assertTrue(true); // keep PMD from complaining
    }
}
