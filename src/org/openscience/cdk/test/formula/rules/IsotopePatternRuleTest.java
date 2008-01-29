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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.formula.IMolecularFormula;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.formula.rules.IRule;
import org.openscience.cdk.formula.rules.IsotopePatternRule;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-formula
 */
public class IsotopePatternRuleTest extends CDKTestCase {
	
	private DefaultChemObjectBuilder builder;
	private IsotopeFactory ifac;
	/**
	 *  Constructor for the IsotopePatternRuleTest object
	 *
	 */
	public  IsotopePatternRuleTest(String name){
		
		super(name);
	}
	
	/**
    *  The JUnit setup method
    */
    public void setUp() throws Exception {
    	builder = DefaultChemObjectBuilder.getInstance();
    	ifac = IsotopeFactory.getInstance(builder);
    }
	
	/**
	 *  A unit test suite for JUnit.
	 *
	 *@return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(IsotopePatternRuleTest.class);
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testDefault() throws ClassNotFoundException, CDKException, Exception {
		
		IRule rule  = new IsotopePatternRule();
		Object[] objects = rule.getParameters();
		
		assertNull(objects[0]);
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testSetParameters() throws ClassNotFoundException, CDKException, Exception {

		IRule rule  = new IsotopePatternRule();
		
		Object[] params = new Object[2];
		
		params[0] = new ArrayList<Double[]>();
		params[1] = 0.0001;
		rule.setParameters(params);
		
		Object[] objects = rule.getParameters();
		
		
		assertNotNull(objects[0]);
		assertEquals(2,objects.length);
	}
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testValid_Bromine() throws ClassNotFoundException, CDKException, Exception {
		
		List<double[]> spectrum = new ArrayList<double[]>();
		spectrum.add(new double[]{157.8367,51.399});
		spectrum.add(new double[]{159.8346,100.00});
		spectrum.add(new double[]{161.8326,48.639});
		
		IRule rule  = new IsotopePatternRule();
		Object[] params = new Object[2];
		params[0] = spectrum;
		params[1] = 0.001;
		rule.setParameters(params);
		
		IMolecularFormula formula = new MolecularFormula();
		formula.addIsotope(ifac.getMajorIsotope("C"),2);
		formula.addIsotope( ifac.getMajorIsotope("Br"),2);
        formula.setCharge(0.0);

		assertEquals(0.0, rule.validate(formula),0.0001);
	}
	

	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testDefaultValidTrue() throws ClassNotFoundException, CDKException, Exception {

		IMolecularFormula formula = new MolecularFormula();
		formula.addIsotope(ifac.getMajorIsotope("C"),5);
		formula.addIsotope( ifac.getMajorIsotope("H"),13);
		formula.addIsotope( ifac.getMajorIsotope("N"),2);
		formula.addIsotope( ifac.getMajorIsotope("O"),2);
        formula.setCharge(0.0);
        
		
		/** experimental results*/
		
		List<double[]> spectrum = new ArrayList<double[]>();
		spectrum.add(new double[]{133.0977 ,100.00});
		spectrum.add(new double[]{134.09475,0.6});
		spectrum.add(new double[]{134.1010 ,5.4});
		
		IRule rule  = new IsotopePatternRule();
		Object[] params = new Object[2];
		params[0] = spectrum;
		params[1] = 0.001;
		rule.setParameters(params);

		assertEquals(0.81103, rule.validate(formula),0.0001);
	
	}
}
