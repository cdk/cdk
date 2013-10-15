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
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Element;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;

/**
 * Checks the functionality of the IsotopeFactory
 *
 * @cdk.module test-core
 */
public class BODRIsotopesTest extends CDKTestCase {

	@Test
    public void testGetInstance_IChemObjectBuilder() throws Exception {
		BODRIsotopes isofac = BODRIsotopes.getInstance();
        Assert.assertNotNull(isofac);
    }

    @Test
    public void testGetSize() throws Exception {
    	BODRIsotopes isofac = BODRIsotopes.getInstance();
		Assert.assertTrue(isofac.getSize() > 0);
    }

    @Test
    public void testConfigure_IAtom() throws Exception {
    	BODRIsotopes isofac = BODRIsotopes.getInstance();
		Atom atom = new Atom("H");
        isofac.configure(atom);
        Assert.assertEquals(1, atom.getAtomicNumber().intValue());
    }

    @Test
    public void testConfigure_IAtom_IIsotope() throws Exception {
		BODRIsotopes isofac = BODRIsotopes.getInstance();
		Atom atom = new Atom("H");
        IIsotope isotope = new org.openscience.cdk.Isotope("H", 2);
        isofac.configure(atom, isotope);
        Assert.assertEquals(2, atom.getMassNumber().intValue());
    }

    @Test
    public void testGetMajorIsotope_String() throws Exception {
		BODRIsotopes isofac = BODRIsotopes.getInstance();
        IIsotope isotope = isofac.getMajorIsotope("Te");
		Assert.assertEquals(129.9062244, isotope.getExactMass(), 0.0001);
	}

    @Test
    public void testGetMajorIsotope_int() throws Exception {
		BODRIsotopes isofac = BODRIsotopes.getInstance();
        IIsotope isotope = isofac.getMajorIsotope(17);
		Assert.assertEquals("Cl", isotope.getSymbol());
	}

    @Test
    public void testGetElement_String() throws Exception {
		XMLIsotopeFactory elfac = XMLIsotopeFactory.getInstance(new ChemObject().getBuilder());
        IElement element = elfac.getElement("Br");
		Assert.assertEquals(35, element.getAtomicNumber().intValue());
	}    

    @Test
    public void testGetElement_int() throws Exception {
		XMLIsotopeFactory elfac = XMLIsotopeFactory.getInstance(new ChemObject().getBuilder());
        IElement element = elfac.getElement(6);
		Assert.assertEquals("C", element.getSymbol());
	}    

    @Test
    public void testGetElementSymbol_int() throws Exception {
		XMLIsotopeFactory elfac = XMLIsotopeFactory.getInstance(new ChemObject().getBuilder());
        String symbol = elfac.getElementSymbol(8);
		Assert.assertEquals("O", symbol);
	}    

    @Test
    public void testGetIsotopes_String() throws Exception {
		BODRIsotopes isofac = BODRIsotopes.getInstance();
        IIsotope[] list = isofac.getIsotopes("He");
		Assert.assertEquals(8, list.length);
	}    

    @Test
    public void testGetIsotopes() throws Exception {
		BODRIsotopes isofac = BODRIsotopes.getInstance();
        IIsotope[] list = isofac.getIsotopes();
		Assert.assertTrue(list.length > 200);
	}    

    @Test
    public void testGetIsotopes_double_double() throws Exception {
		BODRIsotopes isofac = BODRIsotopes.getInstance();
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
		BODRIsotopes isofac = BODRIsotopes.getInstance();
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
		BODRIsotopes isofac = BODRIsotopes.getInstance();
        isofac.configureAtoms(container);
        for (int i=0; i<container.getAtomCount(); i++) {
            Assert.assertTrue(0 < container.getAtom(i).getAtomicNumber());
        }
    }

    @Test public void testGetNaturalMass_IElement() throws Exception {
		BODRIsotopes isofac = BODRIsotopes.getInstance();
        Assert.assertEquals(1.0079760, isofac.getNaturalMass(new Element("H")), 0.1);
    }

    @Test public void testGetIsotope() throws Exception {
        BODRIsotopes isofac = BODRIsotopes.getInstance();
        Assert.assertEquals(13.00335484, isofac.getIsotope("C", 13).getExactMass(), 0.0000001);
    }

    @Test public void testGetIsotopeFromExactMass() throws Exception {
        BODRIsotopes isofac = BODRIsotopes.getInstance();
        IIsotope carbon13 = isofac.getIsotope("C", 13);
        IIsotope match = isofac.getIsotope(carbon13.getSymbol(), carbon13.getExactMass(), 0.0001);
        Assert.assertNotNull(match);
        Assert.assertEquals(13, match.getMassNumber().intValue());
    }

    @Test public void testYeahSure() throws Exception {
        BODRIsotopes isofac = BODRIsotopes.getInstance();
        IIsotope match = isofac.getIsotope("H", 13.00001, 0.0001);
        Assert.assertNull(match);
    }

    @Test public void testGetIsotopeFromExactMass_LargeTolerance() throws Exception {
        BODRIsotopes isofac = BODRIsotopes.getInstance();
        IIsotope carbon13 = isofac.getIsotope("C", 13);
        IIsotope match = isofac.getIsotope(carbon13.getSymbol(), carbon13.getExactMass(), 2.0);
        Assert.assertNotNull(match);
        Assert.assertEquals(13, match.getMassNumber().intValue());
    }

    /**
     * @cdk.bug 3534288
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNonexistingElement() throws Exception {
        BODRIsotopes isofac = BODRIsotopes.getInstance();
        IAtom xxAtom = new Atom("Xx");
        isofac.configure(xxAtom);
    }

}
