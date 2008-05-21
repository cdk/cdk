/* $Revision$ $Author$ $Date$
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
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

/**
 * Checks the functionality of the MolecularFormulaRangeManipulator.
 *
 * @cdk.module test-formula
 */
public class MolecularFormulaRangeManipulatorTest extends NewCDKTestCase {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();

	/**
	 *  Constructor for the MolecularFormulaRangeManipulatorTest object.
	 *
	 */
	public MolecularFormulaRangeManipulatorTest(){
		
		super();
	}

    /**
	 * A unit test suite for JUnit. 
	 *
	 * @return    The test suite
	 */
    @Test 
	public void testGetRange_IMolecularFormulaSet() {
		IMolecularFormula mf1 = new MolecularFormula(); /*C4H12NO4*/
		mf1.addIsotope(builder.newIsotope("C"),4);
		mf1.addIsotope(builder.newIsotope("H"),12);
		mf1.addIsotope(builder.newIsotope("N"),1);
		mf1.addIsotope(builder.newIsotope("O"),4);
	

		IMolecularFormula mf2 = new MolecularFormula(); /*C7H20N4O2*/
		mf2.addIsotope(builder.newIsotope("C"),7);
		mf2.addIsotope(builder.newIsotope("H"),20);
		mf2.addIsotope(builder.newIsotope("N"),4);
		mf2.addIsotope(builder.newIsotope("O"),2);
		

		IMolecularFormula mf3 = new MolecularFormula(); /*C9H5O7*/
		mf3.addIsotope(builder.newIsotope("C"),9);
		mf3.addIsotope(builder.newIsotope("H"),5);
		mf3.addIsotope(builder.newIsotope("O"),7);
		
		IMolecularFormulaSet mfSet = new MolecularFormulaSet();
		mfSet.addMolecularFormula(mf1);
		mfSet.addMolecularFormula(mf2);
		mfSet.addMolecularFormula(mf3);
		
		MolecularFormulaRange mfRange = MolecularFormulaRangeManipulator.getRange(mfSet);
		
		
		/* Result: C4-9H5-20N0-4O2-7 */

		Assert.assertEquals(4, mfRange.getIsotopeCount());
		Assert.assertEquals(4, mfRange.getIsotopeCountMin(builder.newIsotope("C")));
		Assert.assertEquals(9, mfRange.getIsotopeCountMax(builder.newIsotope("C")));
		Assert.assertEquals(5, mfRange.getIsotopeCountMin(builder.newIsotope("H")));
		Assert.assertEquals(20, mfRange.getIsotopeCountMax(builder.newIsotope("H")));
		Assert.assertEquals(0, mfRange.getIsotopeCountMin(builder.newIsotope("N")));
		Assert.assertEquals(4, mfRange.getIsotopeCountMax(builder.newIsotope("N")));
		Assert.assertEquals(2, mfRange.getIsotopeCountMin(builder.newIsotope("O")));
		Assert.assertEquals(7, mfRange.getIsotopeCountMax(builder.newIsotope("O")));
		
	}	
	/**
	 * A unit test suite for JUnit. 
	 *
	 * @return    The test suite
	 */
    @Test 
	public void testGetMaximalFormula_MolecularFormulaRange() {
		IMolecularFormula mf1 = new MolecularFormula(); /*C4H12NO4*/
		mf1.addIsotope(builder.newIsotope("C"),4);
		mf1.addIsotope(builder.newIsotope("H"),12);
		mf1.addIsotope(builder.newIsotope("N"),1);
		mf1.addIsotope(builder.newIsotope("O"),4);
	

		IMolecularFormula mf2 = new MolecularFormula(); /*C7H20N4O2*/
		mf2.addIsotope(builder.newIsotope("C"),7);
		mf2.addIsotope(builder.newIsotope("H"),20);
		mf2.addIsotope(builder.newIsotope("N"),4);
		mf2.addIsotope(builder.newIsotope("O"),2);
		

		IMolecularFormula mf3 = new MolecularFormula(); /*C9H5O7*/
		mf3.addIsotope(builder.newIsotope("C"),9);
		mf3.addIsotope(builder.newIsotope("H"),5);
		mf3.addIsotope(builder.newIsotope("O"),7);
		
		IMolecularFormulaSet mfSet = new MolecularFormulaSet();
		mfSet.addMolecularFormula(mf1);
		mfSet.addMolecularFormula(mf2);
		mfSet.addMolecularFormula(mf3);

		MolecularFormulaRange mfRange = MolecularFormulaRangeManipulator.getRange(mfSet);
		IMolecularFormula formula = MolecularFormulaRangeManipulator.getMaximalFormula(mfRange, builder);
		
		/* Result: C4-9H5-20N0-4O2-7 */

		Assert.assertEquals(4, mfRange.getIsotopeCount());
		Assert.assertEquals(formula.getIsotopeCount(builder.newIsotope("C")), mfRange.getIsotopeCountMax(builder.newIsotope("C")));
		Assert.assertEquals(formula.getIsotopeCount(builder.newIsotope("H")), mfRange.getIsotopeCountMax(builder.newIsotope("H")));
		Assert.assertEquals(formula.getIsotopeCount(builder.newIsotope("N")), mfRange.getIsotopeCountMax(builder.newIsotope("N")));
		Assert.assertEquals(formula.getIsotopeCount(builder.newIsotope("O")), mfRange.getIsotopeCountMax(builder.newIsotope("O")));
		
	}	
	/**
	 * A unit test suite for JUnit. 
	 *
	 * @return    The test suite
	 */
    @Test 
	public void testGetMinimalFormula_MolecularFormulaRange() {
		IMolecularFormula mf1 = new MolecularFormula(); /*C4H12NO4*/
		mf1.addIsotope(builder.newIsotope("C"),4);
		mf1.addIsotope(builder.newIsotope("H"),12);
		mf1.addIsotope(builder.newIsotope("N"),1);
		mf1.addIsotope(builder.newIsotope("O"),4);
	

		IMolecularFormula mf2 = new MolecularFormula(); /*C7H20N4O2*/
		mf2.addIsotope(builder.newIsotope("C"),7);
		mf2.addIsotope(builder.newIsotope("H"),20);
		mf2.addIsotope(builder.newIsotope("N"),4);
		mf2.addIsotope(builder.newIsotope("O"),2);
		

		IMolecularFormula mf3 = new MolecularFormula(); /*C9H5O7*/
		mf3.addIsotope(builder.newIsotope("C"),9);
		mf3.addIsotope(builder.newIsotope("H"),5);
		mf3.addIsotope(builder.newIsotope("O"),7);
		
		IMolecularFormulaSet mfSet = new MolecularFormulaSet();
		mfSet.addMolecularFormula(mf1);
		mfSet.addMolecularFormula(mf2);
		mfSet.addMolecularFormula(mf3);
		
		MolecularFormulaRange mfRange = MolecularFormulaRangeManipulator.getRange(mfSet);
		IMolecularFormula formula = MolecularFormulaRangeManipulator.getMinimalFormula(mfRange, builder);
		
		/* Result: C4-9H5-20N0-4O2-7 */

		Assert.assertEquals(4, mfRange.getIsotopeCount());
		Assert.assertEquals(formula.getIsotopeCount(builder.newIsotope("C")), mfRange.getIsotopeCountMin(builder.newIsotope("C")));
		Assert.assertEquals(formula.getIsotopeCount(builder.newIsotope("H")), mfRange.getIsotopeCountMin(builder.newIsotope("H")));
		Assert.assertEquals(formula.getIsotopeCount(builder.newIsotope("N")), mfRange.getIsotopeCountMin(builder.newIsotope("N")));
		Assert.assertEquals(formula.getIsotopeCount(builder.newIsotope("O")), mfRange.getIsotopeCountMin(builder.newIsotope("O")));
		
	}	
}

