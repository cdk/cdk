/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
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
package org.openscience.cdk.formula;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.formula.IMolecularFormula;
import org.openscience.cdk.formula.IMolecularFormulaSet;
import org.openscience.cdk.formula.IsotopePatternGenerator;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.formula.MolecularFormulaManipulator;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.NewCDKTestCase;

/**
 * Checks the functionality of the IsotopePatternGenerator.
 *
 * @cdk.module test-formula
 *
 * @author         Miguel Rojas
 * @cdk.created    2007-03-01
 */
public class IsotopePatternGeneratorTest extends NewCDKTestCase{

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	/**
	 *  Constructor for the IsotopePatternGeneratorTest object
	 *
	 */
	public IsotopePatternGeneratorTest(){
		super();
	}
	
	/**
	 * A unit test for JUnit.
	 *
	 * @return    Description of the Return Value
	 */
    @Test public void testIsotopePatternGenerator() {
    	IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator();
		Assert.assertNotNull(isotopeGe);
    }
	/**
	 * A unit test for JUnit.
	 *
	 * @return    Description of the Return Value
	 */
    @Test public void testIsotopePatternGenerator_double() {
    	IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator();
		Assert.assertNotNull(isotopeGe);
    }
	
	/**
	 * A unit test for JUnit: Isotopes of the Bromine.
	 *
	 * @return    Description of the Return Value
	 */
    @Test 
	public void testGetIsotopes_IMolecularFormula() throws CDKException {
		IMolecularFormula molFor = new MolecularFormula();
		molFor.addIsotope(builder.newIsotope("Br"));
		molFor.addIsotope(builder.newIsotope("Br"));

		IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(10.0);
		IMolecularFormulaSet molFormSet = isotopeGe.getIsotopes(molFor);
		
		Assert.assertEquals(3, molFormSet.size());
		
	}
    /**
	 * A unit test for JUnit: Isotopes of the Bromine.
	 *
	 * @return    Description of the Return Value
	 */
    @Test 
	public void testCalculateIsotopesAllBromine() throws CDKException {
		IMolecularFormula molFor = new MolecularFormula();
		molFor.addIsotope(builder.newIsotope("Br"));
		molFor.addIsotope(builder.newIsotope("Br"));

		IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(10.0);
		IMolecularFormulaSet molFormSet = isotopeGe.getIsotopes(molFor);
		
		Assert.assertEquals(3, molFormSet.size());
		
		double mm = MolecularFormulaManipulator.getTotalExactMass(molFormSet.getMolecularFormula(0));
		Assert.assertEquals(157.8366742, mm, 0.0000001);
		mm = MolecularFormulaManipulator.getTotalExactMass(molFormSet.getMolecularFormula(1));
		Assert.assertEquals(159.8346277, mm, 0.0000001);
		mm = MolecularFormulaManipulator.getTotalExactMass(molFormSet.getMolecularFormula(2));
		Assert.assertEquals(161.8325812, mm, 0.0000001);
		

		double sum = 0.0;
		double occurrence = ((Double)molFormSet.getMolecularFormula(0).getProperty("occurrence"));
		double ab = MolecularFormulaManipulator.getTotalNaturalAbundance(molFormSet.getMolecularFormula(0));
		sum += ab;
		Assert.assertEquals(0.25694761, ab, 0.0000001);
		occurrence = ((Double)molFormSet.getMolecularFormula(1).getProperty("occurrence"));
		ab = MolecularFormulaManipulator.getTotalNaturalAbundance(molFormSet.getMolecularFormula(1))*occurrence;
		sum += ab;
		Assert.assertEquals(0.4999047, ab, 0.0000001);
		occurrence = ((Double)molFormSet.getMolecularFormula(2).getProperty("occurrence"));
		ab = MolecularFormulaManipulator.getTotalNaturalAbundance(molFormSet.getMolecularFormula(2));
		sum += ab;
		Assert.assertEquals(0.24314761, ab, 0.0000001);
		

		Assert.assertEquals(1.0, sum, 0.0000001);
		
	}
	
	/**
	 * A unit test for JUnit: Isotopes of the Iodemethylidyne.
	 *
	 * @return    Description of the Return Value
	 */
	@Test 
	public void testCalculateIsotopesIodemethylidyne() throws CDKException {
		IMolecularFormula molFor = new MolecularFormula();
		molFor.addIsotope(builder.newIsotope("C"));
		molFor.addIsotope(builder.newIsotope("I"));

		Assert.assertEquals(2, molFor.getIsotopeCount());
		
		IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(0.10);
		
		IMolecularFormulaSet molFormSet = isotopeGe.getIsotopes(molFor);
		
		Assert.assertEquals(2, molFormSet.size());
		
		double mm = MolecularFormulaManipulator.getTotalExactMass(molFormSet.getMolecularFormula(0));
		Assert.assertEquals(138.904473, mm, 0.0000001);
		mm = MolecularFormulaManipulator.getTotalExactMass(molFormSet.getMolecularFormula(1));
		Assert.assertEquals(139.9078278399, mm, 0.0000001);
		

		double sum = 0.0;
		double occurrence = ((Double)molFormSet.getMolecularFormula(0).getProperty("occurrence"));
		double ab = MolecularFormulaManipulator.getTotalNaturalAbundance(molFormSet.getMolecularFormula(0));
		sum += ab;
		Assert.assertEquals(0.9893, ab, 0.0000001);
		occurrence = ((Double)molFormSet.getMolecularFormula(1).getProperty("occurrence"));
		ab = MolecularFormulaManipulator.getTotalNaturalAbundance(molFormSet.getMolecularFormula(1))*occurrence;
		sum += ab;
		Assert.assertEquals(0.01070, ab, 0.0000001);
		
		

		Assert.assertEquals(1.0, sum, 0.0000001);
		
	}
	/**
	 * A unit test for JUnit: Isotopes of the n-Carbone.
	 *
	 * @return    Description of the Return Value
	 */
	@Test 
	public void testCalculateIsotopesnCarbono() throws CDKException {
		IMolecularFormula molFor = new MolecularFormula();
		molFor.addIsotope(builder.newIsotope("C"),10);

		
		IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(0.10);
		
		IMolecularFormulaSet molFormSet = isotopeGe.getIsotopes(molFor);
		
		Assert.assertEquals(3, molFormSet.size());

		double sum = 0.0;
		for(int i = 0; i < molFormSet.size();i++){
			double occurrence = ((Double)molFormSet.getMolecularFormula(i).getProperty("occurrence"));
			double ab = MolecularFormulaManipulator.getTotalNaturalAbundance(molFormSet.getMolecularFormula(i));
			sum += ab*occurrence;
			
		}
		Assert.assertEquals(1.0, sum, 0.001);
		
		
		double mm = MolecularFormulaManipulator.getTotalExactMass(molFormSet.getMolecularFormula(0));
		Assert.assertEquals(120.0, mm, 0.0000001);
		mm = MolecularFormulaManipulator.getTotalExactMass(molFormSet.getMolecularFormula(1));
		Assert.assertEquals(121.00335484, mm, 0.0000001);

		double ab = MolecularFormulaManipulator.getTotalNaturalAbundance(molFormSet.getMolecularFormula(0));
		double occurrence = ((Double)molFormSet.getMolecularFormula(0).getProperty("occurrence"));
		ab *= occurrence;
		Assert.assertEquals(0.898007762480552, ab, 0.0000001);
		ab = MolecularFormulaManipulator.getTotalNaturalAbundance(molFormSet.getMolecularFormula(1));
		occurrence = ((Double)molFormSet.getMolecularFormula(1).getProperty("occurrence"));
		ab *= occurrence;
		Assert.assertEquals(0.09712607963754075, ab, 0.0000001);
		
	}
	
	/**
	 * A unit test for JUnit.
	 *
	 * @return    Description of the Return Value
	 */
	@Test 
	public void testCalculateIsotopesOrthinine() throws CDKException {
		IMolecularFormula molFor = new MolecularFormula();
			molFor.addIsotope(builder.newIsotope("C"),5);
			molFor.addIsotope(builder.newIsotope("H"),13);
			molFor.addIsotope(builder.newIsotope("N"),2);
			molFor.addIsotope(builder.newIsotope("O"),2);

		IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(0.10);
		
		IMolecularFormulaSet molFormSet = isotopeGe.getIsotopes(molFor);
		
		Assert.assertEquals(5, molFormSet.size());
		
		double sum = 0.0;
		for(int i = 0; i < molFormSet.size();i++){
			double occurrence = ((Double)molFormSet.getMolecularFormula(i).getProperty("occurrence"));
			double ab = MolecularFormulaManipulator.getTotalNaturalAbundance(molFormSet.getMolecularFormula(i));
			sum += ab*occurrence;
		}
		Assert.assertEquals(1.0, sum, 0.01);
		
		
		double mm = MolecularFormulaManipulator.getTotalExactMass(molFormSet.getMolecularFormula(0));
		Assert.assertEquals(133.0977, mm, 0.01);
		mm = MolecularFormulaManipulator.getTotalExactMass(molFormSet.getMolecularFormula(1));
		Assert.assertEquals(134.0947, mm, 0.01);
		mm = MolecularFormulaManipulator.getTotalExactMass(molFormSet.getMolecularFormula(2));
		Assert.assertEquals(134.101079, mm, 0.01);
		
		double ab1 = MolecularFormulaManipulator.getTotalNaturalAbundance(molFormSet.getMolecularFormula(0));
		double occurrence = ((Double)molFormSet.getMolecularFormula(0).getProperty("occurrence"));
		ab1 *= occurrence;
		Assert.assertEquals(100.00, ab1/ab1*100, 0.01);
		double ab2 = MolecularFormulaManipulator.getTotalNaturalAbundance(molFormSet.getMolecularFormula(1));
		occurrence = ((Double)molFormSet.getMolecularFormula(1).getProperty("occurrence"));
		ab2 *= occurrence;
		Assert.assertEquals(0.7387184, ab2/ab1*100, 0.01);
		double ab3 = MolecularFormulaManipulator.getTotalNaturalAbundance(molFormSet.getMolecularFormula(2));
		occurrence = ((Double)molFormSet.getMolecularFormula(2).getProperty("occurrence"));
		ab3 *= occurrence;
		Assert.assertEquals(7.3819, (0.0690/ab1)*100, 0.01);
		
	}
    
}

