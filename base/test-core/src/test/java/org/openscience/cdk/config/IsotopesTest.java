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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.Element;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Checks the functionality of the IsotopeFactory
 *
 * @cdk.module test-core
 */
public class IsotopesTest extends CDKTestCase {

    @Test
    public void testGetInstance_IChemObjectBuilder() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Assert.assertNotNull(isofac);
    }

    @Test
    public void testGetSize() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Assert.assertTrue(isofac.getSize() > 0);
    }

    @Test
    public void testConfigure_IAtom() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Atom atom = new Atom("H");
        isofac.configure(atom);
        Assert.assertEquals(1, atom.getAtomicNumber().intValue());
    }

    @Test
    public void testConfigure_IAtom_IIsotope() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Atom atom = new Atom("H");
        IIsotope isotope = new org.openscience.cdk.Isotope("H", 2);
        isofac.configure(atom, isotope);
        Assert.assertEquals(2, atom.getMassNumber().intValue());
    }

    @Test
    public void testGetMajorIsotope_String() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope isotope = isofac.getMajorIsotope("Te");
        Assert.assertEquals(129.9062244, isotope.getExactMass(), 0.0001);
    }

    @Test
    public void testGetMajorIsotope_int() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope isotope = isofac.getMajorIsotope(17);
        Assert.assertEquals("Cl", isotope.getSymbol());
    }

    @Test
    public void testGetElement_String() throws Exception {
        IsotopeFactory elfac = Isotopes.getInstance();
        IElement element = elfac.getElement("Br");
        Assert.assertEquals(35, element.getAtomicNumber().intValue());
    }

    @Test
    public void testGetElement_int() throws Exception {
        IsotopeFactory elfac = Isotopes.getInstance();
        IElement element = elfac.getElement(6);
        Assert.assertEquals("C", element.getSymbol());
    }

    @Test
    public void testGetElementSymbol_int() throws Exception {
        IsotopeFactory elfac = Isotopes.getInstance();
        String symbol = elfac.getElementSymbol(8);
        Assert.assertEquals("O", symbol);
    }

    @Test
    public void testGetIsotopes_String() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope[] list = isofac.getIsotopes("He");
        Assert.assertEquals(8, list.length);
    }

    @Test
    public void testGetIsotopes() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope[] list = isofac.getIsotopes();
        Assert.assertTrue(list.length > 200);
    }

    @Test
    public void testGetIsotopes_double_double() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope[] list = isofac.getIsotopes(87.90, 0.01);
        //        should return:
        //        Isotope match: 88Sr has mass 87.9056121
        //        Isotope match: 88Y has mass 87.9095011
        Assert.assertEquals(2, list.length);
        Assert.assertEquals(88, list[0].getMassNumber().intValue());
        Assert.assertEquals(88, list[1].getMassNumber().intValue());
    }

    @Test
    public void testIsElement_String() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Assert.assertTrue(isofac.isElement("C"));
    }

    @Test
    public void testConfigureAtoms_IAtomContainer() throws Exception {
        AtomContainer container = new org.openscience.cdk.AtomContainer();
        container.addAtom(new Atom("C"));
        container.addAtom(new Atom("H"));
        container.addAtom(new Atom("N"));
        container.addAtom(new Atom("O"));
        container.addAtom(new Atom("F"));
        container.addAtom(new Atom("Cl"));
        Isotopes isofac = Isotopes.getInstance();
        isofac.configureAtoms(container);
        for (int i = 0; i < container.getAtomCount(); i++) {
            Assert.assertTrue(0 < container.getAtom(i).getAtomicNumber());
        }
    }

    @Test
    public void testGetNaturalMass_IElement() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Assert.assertEquals(1.0079760, isofac.getNaturalMass(new Element("H")), 0.1);
    }

    @Test
    public void testGetIsotope() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        Assert.assertEquals(13.00335484, isofac.getIsotope("C", 13).getExactMass(), 0.0000001);
    }

    /**
     * Elements without a major isotope should return null.
     */
    @Test
    public void testMajorUnstableIsotope() throws Exception {
        Isotopes isotopes = Isotopes.getInstance();
        assertNull(isotopes.getMajorIsotope("Es"));
    }

    @Test
    public void testGetIsotope_NonElement() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        assertNull(isofac.getIsotope("R", 13));
    }

    @Test
    public void testGetIsotopeFromExactMass() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope carbon13 = isofac.getIsotope("C", 13);
        IIsotope match = isofac.getIsotope(carbon13.getSymbol(), carbon13.getExactMass(), 0.0001);
        Assert.assertNotNull(match);
        Assert.assertEquals(13, match.getMassNumber().intValue());
    }

    @Test
    public void testGetIsotopeFromExactMass_NonElement() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope match = isofac.getIsotope("R", 13.00001, 0.0001);
        assertNull(match);
    }

    @Test
    public void testYeahSure() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope match = isofac.getIsotope("H", 13.00001, 0.0001);
        assertNull(match);
    }

    @Test
    public void testGetIsotopeFromExactMass_LargeTolerance() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IIsotope carbon13 = isofac.getIsotope("C", 13);
        IIsotope match = isofac.getIsotope(carbon13.getSymbol(), carbon13.getExactMass(), 2.0);
        Assert.assertNotNull(match);
        Assert.assertEquals(13, match.getMassNumber().intValue());
    }

    @Test
    public void configureDoesNotSetMajorIsotope() throws Exception {
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
    @Test(expected = IllegalArgumentException.class)
    public void testNonexistingElement() throws Exception {
        Isotopes isofac = Isotopes.getInstance();
        IAtom xxAtom = new Atom("Xx");
        isofac.configure(xxAtom);
    }

    @Test
    public void testGetIsotopes_Nonelement() throws Exception {
        IsotopeFactory isofac = Isotopes.getInstance();
        IIsotope[] list = isofac.getIsotopes("E");
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.length);
    }

    @Test
    public void testGetElement_Nonelement() throws Exception {
        IsotopeFactory isofac = Isotopes.getInstance();
        IElement element = isofac.getElement("E");
        assertNull(element);
    }

    @Test
    public void testGetMajorIsotope_Nonelement() throws Exception {
        IsotopeFactory isofac = Isotopes.getInstance();
        IIsotope isotope = isofac.getMajorIsotope("E");
        assertNull(isotope);
    }

}
