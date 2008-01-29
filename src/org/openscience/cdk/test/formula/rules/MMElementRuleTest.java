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
import org.openscience.cdk.formula.rules.MMElementRule;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-formula
 */
public class MMElementRuleTest extends CDKTestCase {
	
	private DefaultChemObjectBuilder builder;
	/**
	 *  Constructor for the MMElementRuleTest object
	 *
	 */
	public  MMElementRuleTest(String name){
		
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
		return new TestSuite(MMElementRuleTest.class);
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testDefault() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new MMElementRule();
		Object[] objects = rule.getParameters();
		
		assertSame(MMElementRule.Database.WILEY, objects[0]);
		assertSame(MMElementRule.RangeMass.Minus500, objects[1]);
		
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testSetParameters() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new MMElementRule();
		
		Object[] params = new Object[2];
		
		params[0] = MMElementRule.Database.DNP;
		params[1] = MMElementRule.RangeMass.Minus1000;
		
		rule.setParameters(params);
		Object[] objects = rule.getParameters();
		
		
		assertSame(MMElementRule.Database.DNP, objects[0]);
		assertSame(MMElementRule.RangeMass.Minus1000, objects[1]);
		
	}
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testDefaultValidFalse() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new MMElementRule();
		
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
	public void testDefaultValidTrue() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new MMElementRule();
		
		IMolecularFormula formula = new MolecularFormula();
		formula.addIsotope(builder.newIsotope("C"),2);
		formula.addIsotope(builder.newIsotope("H"),6);

		assertEquals(1.0, rule.validate(formula),0.0001);
	}
}
