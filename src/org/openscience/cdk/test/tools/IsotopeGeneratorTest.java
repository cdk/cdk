/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.test.tools;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.IsotopeGenerator;

/**
 * @cdk.module  test-extra
 *
 * @author         Miguel Rojas
 * @cdk.created    2007-03-01
 */
public class IsotopeGeneratorTest extends CDKTestCase
{

	IsotopeGenerator isotopeG = null;

	public IsotopeGeneratorTest(String name){
		super(name);
	}

    /**
     *  The JUnit setup method
     */
    public void setUp() throws Exception {
    	isotopeG = new IsotopeGenerator();
    }

	/**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public static Test suite() {
        TestSuite suite = new TestSuite(IsotopeGeneratorTest.class);
        return suite;
	}

    /**
	 * A unit test for JUnit: Isotopes of the Bromine.
	 *
	 * @return    Description of the Return Value
	 */
	public void testCalculateIsotopesBromine() throws CDKException {
		IAtomContainer m = new AtomContainer();
		Atom b1 = new Atom("Br");
		Atom b2 = new Atom("Br");
		m.addAtom(b1);
		m.addAtom(b2);
		m.addBond(new Bond(b1, b2));
		
		IAtomContainerSet containerSet = (IAtomContainerSet)isotopeG.getIsotopes(m);
		
		assertEquals(4, containerSet.getAtomContainerCount());
		
		double mm = Math.round((getMass(containerSet.getAtomContainer(0))));
		assertEquals(158.0, mm, 0.0);
		mm = Math.round((getMass(containerSet.getAtomContainer(1))));
		assertEquals(160.0, mm, 0.0);
		mm = Math.round((getMass(containerSet.getAtomContainer(2))));
		assertEquals(160.0, mm, 0.0);
		mm = Math.round((getMass(containerSet.getAtomContainer(3))));
		assertEquals(162.0, mm, 0.0);
	}
	/**
	 * A unit test for JUnit: Isotopes of the Chloromethylidyne.
	 *
	 * @return    Description of the Return Value
	 */
	public void testCalculateIsotopesChloromethylidyne() throws CDKException {
		IAtomContainer m = new AtomContainer();
		Atom b1 = new Atom("C");
		Atom b2 = new Atom("Cl");
		m.addAtom(b1);
		m.addAtom(b2);
		m.addBond(new Bond(b1, b2));
		
		IAtomContainerSet containerSet = (IAtomContainerSet)isotopeG.getIsotopes(m);
		
		assertEquals(2, containerSet.getAtomContainerCount());
		
		double mm = Math.round((getMass(containerSet.getAtomContainer(0))));
		assertEquals(47.0, mm, 0.0);
		mm = Math.round((getMass(containerSet.getAtomContainer(1))));
		assertEquals(49.0, mm, 0.0);
	}
	/**
	 * A unit test for JUnit: Isotopes of the Chloromethylidyne with a 
	 * restriction for those istopes which their abundance is higher than 50%.
	 *
	 * @return    Description of the Return Value
	 */
	public void testCalculateIsotopesChloromethylidyne2() throws CDKException {
		IAtomContainer m = new AtomContainer();
		Atom b1 = new Atom("C");
		Atom b2 = new Atom("Cl");
		m.addAtom(b1);
		m.addAtom(b2);
		m.addBond(new Bond(b1, b2));

		IsotopeGenerator isotopeG2 = new IsotopeGenerator(50.0);
		IAtomContainerSet containerSet = (IAtomContainerSet)isotopeG2.getIsotopes(m);
		
		assertEquals(1, containerSet.getAtomContainerCount());
		
		double mm = Math.round((getMass(containerSet.getAtomContainer(0))));
		assertEquals(47.0, mm, 0.0);
	}
	/**
	 * returns the exact mass for a given molecular formula, using mass already set
	 * for each element.
	 * 
	 * @param  ac The IAtomContiner
	 * @return The mass value
	 */
	private float getMass(IAtomContainer ac){
		float mass = 0;
		Isotope h = new Isotope(1, "H", 1.0, 1.0);
		Iterator it = ac.atoms();
		while(it.hasNext()){
			IAtom atom = (IAtom) it.next();
			mass += atom.getExactMass();

			mass += atom.getHydrogenCount() * h.exactMass;
		}
		return mass;
	}

	
    
}

