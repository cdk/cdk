/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.config;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the funcitonality of the IsotopeFactory
 *
 * @cdk.module test-extra
 */
public class IsotopeFactoryTest extends CDKTestCase
{
	boolean standAlone = false;
	
	public IsotopeFactoryTest(String name) {
		super(name);
	}
	
	public void setUp() {}
	
	public static Test suite() {
		return new TestSuite(IsotopeFactoryTest.class);
	}

    public void testGetInstance_IChemObjectBuilder() throws Exception {
        IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        assertNotNull(isofac);
    }
    
	public void testGetSize() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
		assertTrue(isofac.getSize() > 0);
    }
	
	public void testConfigure_IAtom() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
		Atom atom = new Atom("H");
        isofac.configure(atom);
        assertEquals(1, atom.getAtomicNumber());
    }
	
	public void testConfigure_IAtom_IIsotope() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
		Atom atom = new Atom("H");
        IIsotope isotope = new org.openscience.cdk.Isotope("H", 2);
        isofac.configure(atom, isotope);
        assertEquals(2, atom.getMassNumber());
    }
	
	public void testGetMajorIsotope_String() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        IIsotope isotope = isofac.getMajorIsotope("Te");
        if (standAlone) System.out.println("Isotope: " + isotope);
		assertEquals(129.906229, isotope.getExactMass(), 0.0001);
	}
    
	public void testGetMajorIsotope_int() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        IIsotope isotope = isofac.getMajorIsotope(17);
		assertEquals("Cl", isotope.getSymbol());
	}
    
    public void testGetElement_String() throws Exception {
		IsotopeFactory elfac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        IElement element = elfac.getElement("Br");
		assertEquals(35, element.getAtomicNumber());
	}    

    public void testGetElement_int() throws Exception {
		IsotopeFactory elfac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        IElement element = elfac.getElement(6);
		assertEquals("C", element.getSymbol());
	}    

    public void testGetElementSymbol_int() throws Exception {
		IsotopeFactory elfac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        String symbol = elfac.getElementSymbol(8);
		assertEquals("O", symbol);
	}    

    public void testGetIsotopes_String() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        IIsotope[] list = isofac.getIsotopes("He");
		assertEquals(2, list.length);
	}    

    public void testIsElement_String() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
		assertTrue(isofac.isElement("C"));
	}
    
    public void testConfigureAtoms_IAtomContainer() throws Exception {
        AtomContainer container = new org.openscience.cdk.AtomContainer();
        container.addAtom(new Atom("C"));
        container.addAtom(new Atom("H"));
        container.addAtom(new Atom("N"));
        container.addAtom(new Atom("O"));
        container.addAtom(new Atom("F"));
        container.addAtom(new Atom("Cl"));
		IsotopeFactory isofac = IsotopeFactory.getInstance(new ChemObject().getBuilder());
        isofac.configureAtoms(container);
        org.openscience.cdk.interfaces.IAtom[] atoms = container.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            assertTrue(0 < atoms[i].getAtomicNumber());
        }
    }

	public static void main(String[] args) {
		try {
			IsotopeFactoryTest ift = new IsotopeFactoryTest("IsotopeFactoryTest");
			ift.standAlone = true;
			ift.testGetInstance_IChemObjectBuilder();
		} catch(Exception exc) {
			exc.printStackTrace();
		}
	}
}
