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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk.test.config;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.*;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.config.IsotopeFactory;

/**
 * Checks the funcitonality of the IsotopeFactory
 *
 * @cdk.module test
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

    public void testGetInstance() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance();
    }
    
	public void testGetSize() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance();
		assertTrue(isofac.getSize() > 0);
    }
	
	public void testConfigure_Atom() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance();
		Atom atom = new Atom("H");
        isofac.configure(atom);
        assertEquals(1, atom.getAtomicNumber());
    }
	
	public void testConfigure_Atom_Isotope() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance();
		Atom atom = new Atom("H");
        Isotope isotope = new Isotope("H", 2);
        isofac.configure(atom, isotope);
        assertEquals(2, atom.getMassNumber());
    }
	
	public void testGetMajorIsotope_String() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance();
        Isotope isotope = isofac.getMajorIsotope("Te");
        if (standAlone) System.out.println("Isotope: " + isotope);
		assertEquals(129.906229, isotope.getExactMass(), 0.0001);
	}
    
	public void testGetMajorIsotope_int() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance();
        Isotope isotope = isofac.getMajorIsotope(17);
		assertEquals("Cl", isotope.getSymbol());
	}
    
    public void testGetElement_String() throws Exception {
		IsotopeFactory elfac = IsotopeFactory.getInstance();
        Element element = elfac.getElement("Br");
		assertEquals(35, element.getAtomicNumber());
	}    

    public void testGetElement_int() throws Exception {
		IsotopeFactory elfac = IsotopeFactory.getInstance();
        Element element = elfac.getElement(6);
		assertEquals("C", element.getSymbol());
	}    

    public void testGetElementSymbol_int() throws Exception {
		IsotopeFactory elfac = IsotopeFactory.getInstance();
        String symbol = elfac.getElementSymbol(8);
		assertEquals("O", symbol);
	}    

    public void testGetIsotopes_String() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance();
        Isotope[] list = isofac.getIsotopes("He");
		assertEquals(2, list.length);
	}    

    public void testIsElement_String() throws Exception {
		IsotopeFactory isofac = IsotopeFactory.getInstance();
		assertTrue(isofac.isElement("C"));
	}
    
    public void testConfigureAtoms_AtomContainer() throws Exception {
        AtomContainer container = new AtomContainer();
        container.addAtom(new Atom("C"));
        container.addAtom(new Atom("H"));
        container.addAtom(new Atom("N"));
        container.addAtom(new Atom("O"));
        container.addAtom(new Atom("F"));
        container.addAtom(new Atom("Cl"));
		IsotopeFactory isofac = IsotopeFactory.getInstance();
        isofac.configureAtoms(container);
        Atom[] atoms = container.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            assertTrue(0 < atoms[i].getAtomicNumber());
        }
    }

	public static void main(String[] args) {
		try {
			IsotopeFactoryTest ift = new IsotopeFactoryTest("IsotopeFactoryTest");
			ift.standAlone = true;
			ift.testGetInstance();
		} catch(Exception exc) {
			exc.printStackTrace();
		}
	}
}
