/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *                    2013  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.Element;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Checks the functionality of the IsotopeFactory
 *
 * @cdk.module test-core
 */
class IsotopesTest extends CDKTestCase {

    @Test
    void testGetInstance_IChemObjectBuilder() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Assertions.assertNotNull(isofac);
    }

    @Test
    void testGetSize() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Assertions.assertTrue(isofac.getSize() > 0);
    }

    @Test
    void testConfigure_IAtom() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Atom atom = new Atom("H");
        isofac.configure(atom);
        Assertions.assertEquals(1, atom.getAtomicNumber().intValue());
    }

    @Test
    void testConfigure_IAtom_IIsotope() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Atom atom = new Atom("H");
        IIsotope isotope = new org.openscience.cdk.Isotope("H", 2);
        isofac.configure(atom, isotope);
        Assertions.assertEquals(2, atom.getMassNumber().intValue());
    }

    @Test
    void testGetMajorIsotope_String() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope isotope = isofac.getMajorIsotope("Te");
        Assertions.assertEquals(129.9062244, isotope.getExactMass(), 0.0001);
    }

    @Test
    void testGetMajorIsotope_int() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope isotope = isofac.getMajorIsotope(17);
        Assertions.assertEquals("Cl", isotope.getSymbol());
    }

    @Test
    void testGetElement_String() throws Exception {
        IsotopeFactory elfac = Isotopes.getInstance();
        IElement element = elfac.getElement("Br");
        Assertions.assertEquals(35, element.getAtomicNumber().intValue());
    }

    @Test
    void testGetElement_int() throws Exception {
        IsotopeFactory elfac = Isotopes.getInstance();
        IElement element = elfac.getElement(6);
        Assertions.assertEquals("C", element.getSymbol());
    }

    @Test
    void testGetElementSymbol_int() throws Exception {
        IsotopeFactory elfac = Isotopes.getInstance();
        String symbol = elfac.getElementSymbol(8);
        Assertions.assertEquals("O", symbol);
    }

    @Test
    void testGetIsotopes_String() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope[] list = isofac.getIsotopes("He");
        Assertions.assertEquals(8, list.length);
    }

    @Test
    void testGetIsotopes() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope[] list = isofac.getIsotopes();
        Assertions.assertTrue(list.length > 200);
    }

    @Test
    void testGetIsotopes_double_double() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope[] list = isofac.getIsotopes(87.90, 0.01);
        //        should return:
        //        Isotope match: 88Sr has mass 87.9056121
        //        Isotope match: 88Y has mass 87.9095011
        Assertions.assertEquals(2, list.length);
        Assertions.assertEquals(88, list[0].getMassNumber().intValue());
        Assertions.assertEquals(88, list[1].getMassNumber().intValue());
    }

    @Test
    void testIsElement_String() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Assertions.assertTrue(isofac.isElement("C"));
    }

    @Test
    void testConfigureAtoms_IAtomContainer() throws Exception {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(new Atom("C"));
        container.addAtom(new Atom("H"));
        container.addAtom(new Atom("N"));
        container.addAtom(new Atom("O"));
        container.addAtom(new Atom("F"));
        container.addAtom(new Atom("Cl"));
        Isotopes isofac = Isotopes.getInstance();
        isofac.configureAtoms(container);
        for (int i = 0; i < container.getAtomCount(); i++) {
            Assertions.assertTrue(0 < container.getAtom(i).getAtomicNumber());
        }
    }

    @Test
    void testGetNaturalMass_IElement() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Assertions.assertEquals(1.0079760, isofac.getNaturalMass(new Element("H")), 0.1);
    }

    @Test
    void testGetIsotope() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Assertions.assertEquals(13.00335484, isofac.getIsotope("C", 13).getExactMass(), 0.0000001);
    }

    /**
     * Elements without a major isotope should return null.
     */
    @Test
    void testMajorUnstableIsotope() throws Exception {
        Isotopes isotopes = Isotopes.getInstance();
        Assertions.assertNull(isotopes.getMajorIsotope("Es"));
    }

    @Test
    void testGetIsotope_NonElement() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Assertions.assertNull(isofac.getIsotope("R", 13));
    }

    @Test
    void testGetIsotopeFromExactMass() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope carbon13 = isofac.getIsotope("C", 13);
        IIsotope match = isofac.getIsotope(carbon13.getSymbol(), carbon13.getExactMass(), 0.0001);
        Assertions.assertNotNull(match);
        Assertions.assertEquals(13, match.getMassNumber().intValue());
    }

    @Test
    void testGetIsotopeFromExactMass_NonElement() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope match = isofac.getIsotope("R", 13.00001, 0.0001);
        Assertions.assertNull(match);
    }

    @Test
    void testYeahSure() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope match = isofac.getIsotope("H", 13.00001, 0.0001);
        Assertions.assertNull(match);
    }

    @Test
    void testGetIsotopeFromExactMass_LargeTolerance() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope carbon13 = isofac.getIsotope("C", 13);
        IIsotope match = isofac.getIsotope(carbon13.getSymbol(), carbon13.getExactMass(), 2.0);
        Assertions.assertNotNull(match);
        Assertions.assertEquals(13, match.getMassNumber().intValue());
    }

    @Test
    void configureDoesNotSetMajorIsotope() throws Exception {
        IAtom    atom     = new Atom("CH4");
        Isotopes isotopes = Isotopes.getInstance();
        IIsotope major    = isotopes.getMajorIsotope(atom.getSymbol());
        assertThat(major, is(notNullValue()));
        assertThat(major.getMassNumber(),
                   is(12));
        isotopes.configure(atom);
        assertThat(atom.getMassNumber(), is(nullValue()));
    }

    /**
     * @cdk.bug 3534288
     */
    @Test
    void testNonexistingElement() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    Isotopes isofac = Isotopes.getInstance();
                                    IAtom xxAtom = new Atom("Xx");
                                    isofac.configure(xxAtom);
                                });
    }

    @Test
    void testGetIsotopes_Nonelement() throws Exception {
        IsotopeFactory isofac = Isotopes.getInstance();
        IIsotope[] list = isofac.getIsotopes("E");
        Assertions.assertNotNull(list);
        Assertions.assertEquals(0, list.length);
    }

    @Test
    void testGetElement_Nonelement() throws Exception {
        IsotopeFactory isofac = Isotopes.getInstance();
        IElement element = isofac.getElement("E");
        Assertions.assertNull(element);
    }

    @Test
    void testGetMajorIsotope_Nonelement() throws Exception {
        IsotopeFactory isofac = Isotopes.getInstance();
        IIsotope isotope = isofac.getMajorIsotope("E");
        Assertions.assertNull(isotope);
    }

}
