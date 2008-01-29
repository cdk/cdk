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
import org.openscience.cdk.Isotope;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.formula.IMolecularFormula;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.formula.MolecularFormulaRange;
import org.openscience.cdk.formula.rules.ElementRule;
import org.openscience.cdk.formula.rules.IRule;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-formula
 */
public class ElementRuleTest extends CDKTestCase {
	
	private DefaultChemObjectBuilder builder;
	/**
	 *  Constructor for the ElementRuleTest object
	 *
	 */
	public  ElementRuleTest(String name){
		
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
		return new TestSuite(ElementRuleTest.class);
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testDefault() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new ElementRule();
		Object[] objects = rule.getParameters();
		assertEquals(1, objects.length);
		
		MolecularFormulaRange mfRange = (MolecularFormulaRange) objects[0];
		assertEquals(93, mfRange.getIsotopeCount());
		assertEquals(0, mfRange.getIsotopeCountMin(new Isotope("C")));
		assertEquals(50, mfRange.getIsotopeCountMax(new Isotope("C")));
		
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testSetParameters() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new ElementRule();
		
		Object[] params = new Object[1];
		
		MolecularFormulaRange mfRange = new MolecularFormulaRange();
    	mfRange.addIsotope( builder.newIsotope("C"), 1, 10);
    	mfRange.addIsotope( builder.newIsotope("H"), 1, 10);
    	params[0] = mfRange;
		
    	rule.setParameters(params);
		
        Object[] objects = rule.getParameters();
		assertEquals(1, objects.length);
		
		MolecularFormulaRange mfRange2 = (MolecularFormulaRange) objects[0];
		assertEquals(mfRange.getIsotopeCount(), mfRange2.getIsotopeCount());
		assertEquals(mfRange.getIsotopeCountMin(new Isotope("C")), mfRange2.getIsotopeCountMin(new Isotope("C")));
		assertEquals(mfRange.getIsotopeCountMax(new Isotope("C")), mfRange2.getIsotopeCountMax(new Isotope("C")));
		
	}
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testDefaultValidFalse() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new ElementRule();
		
		IMolecularFormula formula = new MolecularFormula();
		formula.addIsotope(builder.newIsotope("C"),2);
		formula.addIsotope(builder.newIsotope("H"),200);
		
		assertEquals(0.0, rule.validate(formula),0.0001);
	}

	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testDefaultValidFalse_SetParam() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new ElementRule();
		
		IMolecularFormula formula = new MolecularFormula();
		formula.addIsotope(builder.newIsotope("C"),2);
		formula.addIsotope(builder.newIsotope("H"),6);
		
		
		Object[] params = new Object[1];
		
		MolecularFormulaRange mfRange = new MolecularFormulaRange();
    	mfRange.addIsotope( builder.newIsotope("C"), 1, 2);
    	mfRange.addIsotope( builder.newIsotope("H"), 1, 2);
    	params[0] = mfRange;
		
    	rule.setParameters(params);

		assertEquals(0.0, rule.validate(formula),0.0001);
	}

	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testDefaultValidTrue() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new ElementRule();
		
		IMolecularFormula formula = new MolecularFormula();
		formula.addIsotope(builder.newIsotope("C"),2);
		formula.addIsotope(builder.newIsotope("H"),6);

		assertEquals(1.0, rule.validate(formula),0.0001);
	}

}
