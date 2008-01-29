/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
 *
 *  Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
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
package org.openscience.cdk.test.formula.rules;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.formula.IMolecularFormula;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.formula.rules.IRule;
import org.openscience.cdk.formula.rules.ToleranceRangeRule;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-formula
 */
public class ToleranceRangeRuleTest extends CDKTestCase {
	
	private DefaultChemObjectBuilder builder;
	/**
	 *  Constructor for the ChargeRuleTest object
	 *
	 */
	public  ToleranceRangeRuleTest(String name){
		
		super(name);
	}
	
	/**
    *  The JUnit setup method
    */
    public void setUp() throws Exception {
    	builder = DefaultChemObjectBuilder.getInstance();
    }
	
	/**
	 *  A unit test suite for JUnit.
	 *
	 *@return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(ToleranceRangeRuleTest.class);
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testDefault() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new ToleranceRangeRule();
		Object[] objects = rule.getParameters();
		assertEquals(2, objects.length);
		
		double mass = (Double) objects[0];
		assertEquals(0.0, mass, 0.00001);
		double tolerance = (Double) objects[1];
		assertEquals(0.05, tolerance, 0.00001);
		
		
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testSetParameters() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new ToleranceRangeRule();
		
		Object[] params = new Object[2];
		params[0] = 133.0;
        params[1] = 0.00005;
		rule.setParameters(params);
		
        Object[] objects = rule.getParameters();
		
        assertEquals(2, objects.length);
		
		double mass = (Double) objects[0];
		assertEquals(133.0, mass, 0.00001);
		double tolerance = (Double) objects[1];
		assertEquals(0.00005, tolerance, 0.00001);
		
	}
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testDefaultValidFalse() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new ToleranceRangeRule();
		
		IMolecularFormula formula = new MolecularFormula();
		IIsotope carb = builder.newIsotope("C");
    	carb.setExactMass(12.00);
    	IIsotope cl = builder.newIsotope("Cl");
        cl.setExactMass(34.96885268);
        formula.addIsotope(carb);
        formula.addIsotope(cl);
		
		assertEquals(0.0, rule.validate(formula),0.0001);
	}

	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testDefaultValidFalse_SetParam() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new ToleranceRangeRule();
		
		IMolecularFormula formula = new MolecularFormula();
		IIsotope carb = builder.newIsotope("C");
    	carb.setExactMass(12.00);
    	IIsotope cl = builder.newIsotope("Cl");
        cl.setExactMass(34.96885268);
        formula.addIsotope(carb);
        formula.addIsotope(cl);
		
        Object[] params = new Object[2];
		params[0] = 46.0; // real -> 46.96885268
        params[1] = 0.00005;
		rule.setParameters(params);

		assertEquals(0.0, rule.validate(formula),0.0001);
	}

	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testDefaultValidTrue() throws ClassNotFoundException, CDKException, Exception {
		
IRule rule  = new ToleranceRangeRule();
		
		IMolecularFormula formula = new MolecularFormula();
		IIsotope carb = builder.newIsotope("C");
    	carb.setExactMass(12.00);
    	IIsotope cl = builder.newIsotope("Cl");
        cl.setExactMass(34.96885268);
        formula.addIsotope(carb);
        formula.addIsotope(cl);
		
        Object[] params = new Object[2];
		params[0] = 46.96885268; 
        params[1] = 0.00005;
		rule.setParameters(params);

		assertEquals(1.0, rule.validate(formula),0.0001);
	}

}
