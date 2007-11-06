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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.IsotopeGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

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
	public void testCalculateIsotopesAllBromine() throws CDKException {
		IAtomContainer m = new AtomContainer();
		Atom b1 = new Atom("Br");
		Atom b2 = new Atom("Br");
		m.addAtom(b1);
		m.addAtom(b2);
		m.addBond(new Bond(b1, b2));

		IsotopeGenerator isotopeGe = new IsotopeGenerator(0.01);
		IAtomContainerSet containerSet = (IAtomContainerSet)isotopeGe.getAllIsotopes(m);
		
		assertEquals(4, containerSet.getAtomContainerCount());
		
		double mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(0));
		assertEquals(157.8366742, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(1));
		assertEquals(159.8346277, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(2));
		assertEquals(159.8346277, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(3));
		assertEquals(161.8325812, mm, 0.0000001);
		

		double sum = 0.0;
		double ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(0));
		sum += ab;
		assertEquals(0.25694761, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(1));
		sum += ab;
		assertEquals(0.24995239, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(2));
		sum += ab;
		assertEquals(0.24995239, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(3));
		sum += ab;
		assertEquals(0.24314761, ab, 0.0000001);
		

		assertEquals(1.0, sum, 0.0000001);
		
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

		IsotopeGenerator isotopeGe = new IsotopeGenerator(0.01);
		IAtomContainerSet containerSet = (IAtomContainerSet)isotopeGe.getIsotopes(m);
		
		assertEquals(3, containerSet.getAtomContainerCount());
		
		double mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(0));
		assertEquals(157.8366742, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(1));
		assertEquals(159.8346277, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(2));
		assertEquals(161.8325812, mm, 0.0000001);
		
		double sum = 0.0;
		double ab = ((Double)containerSet.getAtomContainer(0).getProperty("abundanceTotal")).doubleValue();
		sum += ab;
		assertEquals(0.25694761, ab, 0.0000001);
		ab = ((Double)containerSet.getAtomContainer(1).getProperty("abundanceTotal")).doubleValue();
		sum += ab;
		assertEquals(0.4999047, ab, 0.0000001);
		ab = ((Double)containerSet.getAtomContainer(2).getProperty("abundanceTotal")).doubleValue();
		sum += ab;
		assertEquals(0.24314761, ab, 0.0000001);
		
		assertEquals(1.0, sum, 0.0000001);
	}
	/**
	 * A unit test for JUnit: Isotopes of the Iodemethylidyne.
	 *
	 * @return    Description of the Return Value
	 */
	public void testCalculateIsotopesIodemethylidyne() throws CDKException {
		IAtomContainer m = new AtomContainer();
		IAtom b1 = new Atom("C");
		IAtom b2 = new Atom("I");
		m.addAtom(b1);
		m.addAtom(b2);
		m.addBond(new Bond(b1, b2));
		IsotopeGenerator isotopeGe = new IsotopeGenerator(0.0000001);
		IAtomContainerSet containerSet = (IAtomContainerSet)isotopeGe.getAllIsotopes(m);
		
		assertEquals(2, containerSet.getAtomContainerCount());
		
		double mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(0));
		assertEquals(138.904473, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(1));
		assertEquals(139.9078278399, mm, 0.0000001);
		

		double sum = 0.0;
		double ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(0));
		sum += ab;
		assertEquals(0.9893, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(1));
		sum += ab;
		assertEquals(0.01070, ab, 0.0000001);
		

		assertEquals(1.0, sum, 0.0000001);
		
	}
	

	/**
	 * A unit test for JUnit: Isotopes of the DiBromomethylidyne.
	 *
	 * @return    Description of the Return Value
	 */
	public void testCalculateIsotopesDibromomethylidyne() throws CDKException {
		IAtomContainer m = new AtomContainer();
		IAtom b1 = new Atom("C");
		IAtom b2 = new Atom("Br");
		IAtom b3 = new Atom("Br");
		m.addAtom(b1);
		m.addAtom(b2);
		m.addAtom(b3);
		m.addBond(new Bond(b1, b2));
		m.addBond(new Bond(b1, b3));
		IsotopeGenerator isotopeGe = new IsotopeGenerator(0.01);
		IAtomContainerSet containerSet = (IAtomContainerSet)isotopeGe.getAllIsotopes(m);
		
		
		assertEquals(8, containerSet.getAtomContainerCount());
		
		double mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(0));
		assertEquals(169.8366742, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(1));
		assertEquals(171.8346277, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(2));
		assertEquals(171.8346277, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(3));
		assertEquals(173.8325812, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(4));
		assertEquals(170.84002904, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(5));
		assertEquals(172.837982539, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(6));
		assertEquals(172.8379825, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(7));
		assertEquals(174.8359360, mm, 0.0000001);
		

		double sum = 0.0;
		double ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(0));
		sum += ab;
		assertEquals(0.254198270, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(1));
		sum += ab;
		assertEquals(0.2472778994, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(2));
		sum += ab;
		assertEquals(0.247277899427, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(3));
		sum += ab;
		assertEquals(0.240545930573, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(4));
		sum += ab;
		assertEquals(0.0027493394270, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(5));
		sum += ab;
		assertEquals(0.00267449057, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(6));
		sum += ab;
		assertEquals(0.00267449057, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(7));
		sum += ab;
		assertEquals(0.002601679427, ab, 0.0000001);
		

		assertEquals(1.0, sum, 0.0000001);
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

		IsotopeGenerator isotopeG2 = new IsotopeGenerator(0.01);
		IAtomContainerSet containerSet = (IAtomContainerSet)isotopeG2.getAllIsotopes(m);
		
		assertEquals(4, containerSet.getAtomContainerCount());
		
		double mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(0));
		assertEquals(46.96885268, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(1));
		assertEquals(48.96590259, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(2));
		assertEquals(47.97220752, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(3));
		assertEquals(49.96925743, mm, 0.0000001);

		double sum = 0.0;
		double ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(0));
		sum += ab;
		assertEquals(0.74969154, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(1));
		sum += ab;
		assertEquals(0.23960846, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(2));
		sum += ab;
		assertEquals(0.00810846, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(3));
		sum += ab;
		assertEquals(0.00259154, ab, 0.0000001);

		assertEquals(1.0, sum, 0.0000001);
	}
	
	
	/**
	 * A unit test for JUnit: Isotopes of the n-Carbone
	 *
	 * @return    Description of the Return Value
	 */
	public void testCalculateIsotopesnCarbono() throws CDKException {
		IAtomContainer m = new AtomContainer();
		Atom b1 = new Atom("C");
		Atom b2 = new Atom("C");
		Atom b3 = new Atom("C");
		Atom b4 = new Atom("C");
		Atom b5 = new Atom("C");
		Atom b6 = new Atom("C");
		m.addAtom(b1);
		m.addAtom(b2);
		m.addAtom(b3);
		m.addAtom(b4);
		m.addAtom(b5);
		m.addAtom(b6);
		m.addBond(new Bond(b1, b2));
		m.addBond(new Bond(b2, b3));
		m.addBond(new Bond(b3, b4));
		m.addBond(new Bond(b4, b5));
		m.addBond(new Bond(b5, b6));

		IsotopeGenerator isotopeG2 = new IsotopeGenerator(0.01);
		IAtomContainerSet containerSet = (IAtomContainerSet)isotopeG2.getAllIsotopes(m);
		
		
		assertEquals(64, containerSet.getAtomContainerCount());
		
		double sum = 0.0;
		double mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(0));
		assertEquals(72.0, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(1));
		assertEquals(73.00335484, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(2));
		assertEquals(73.00335484, mm, 0.0000001);
		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(3));
		assertEquals(73.00335484, mm, 0.0000001);

		sum = 0.0;
		double ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(0));
		sum += ab;
		assertEquals(0.937493044919, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(1));
		sum += ab;
		assertEquals(0.0101396700, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(2));
		sum += ab;
		assertEquals(0.0101396700, ab, 0.0000001);
		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(3));
		sum += ab;
		assertEquals(0.0101396700, ab, 0.0000001);

		/*it needs more containers*/
//		assertEquals(1.0, sum, 0.0000001);
		
	}
	
	/**
	 * A unit test for JUnit: Isotopes of the n-Carbone
	 *
	 * @return    Description of the Return Value
	 */
	public void testCalculateIsotopesOrthinine() throws CDKException {
		IAtomContainer m = new AtomContainer();
		for(int i = 0 ; i < 5 ; i++)
			m.addAtom(new Atom("C"));
		for(int i = 0 ; i < 13 ; i++)
			m.addAtom(new Atom("H"));
		for(int i = 0 ; i < 2 ; i++)
			m.addAtom(new Atom("N"));
		for(int i = 0 ; i < 2 ; i++)
			m.addAtom(new Atom("O"));

		IsotopeGenerator isotopeG2 = new IsotopeGenerator(0.01);
		IAtomContainerSet containerSet = (IAtomContainerSet)isotopeG2.getIsotopes(m);
		
		
//		assertEquals(64, containerSet.getAtomContainerCount());
//		
//		double sum = 0.0;
//		double mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(0));
//		assertEquals(72.0, mm, 0.0000001);
//		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(1));
//		assertEquals(73.00335484, mm, 0.0000001);
//		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(2));
//		assertEquals(73.00335484, mm, 0.0000001);
//		mm = AtomContainerManipulator.getTotalExactMass(containerSet.getAtomContainer(3));
//		assertEquals(73.00335484, mm, 0.0000001);
//
//		sum = 0.0;
//		double ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(0));
//		sum += ab;
//		assertEquals(0.937493044919, ab, 0.0000001);
//		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(1));
//		sum += ab;
//		assertEquals(0.0101396700, ab, 0.0000001);
//		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(2));
//		sum += ab;
//		assertEquals(0.0101396700, ab, 0.0000001);
//		ab = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(3));
//		sum += ab;
//		assertEquals(0.0101396700, ab, 0.0000001);
//
//		/*it needs more containers*/
////		assertEquals(1.0, sum, 0.0000001);
		
	}
    
}

