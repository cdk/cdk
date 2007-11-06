/* $Revision: 8658 $ $Author: egonw $ $Date: 2007-08-03 15:20:28 +0200 (Fri, 03 Aug 2007) $
 * 
 *  Copyright (C) 2005-2007  Miguel Rojasch <miguelrojasch@users.sf.net>
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

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Element;
import org.openscience.cdk.MolecularFormula;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.MFValidator;

/**
 * @cdk.module test-experimental
 */
public class MFValidatorTest extends CDKTestCase {

	/**
	 *  Constructor for the MFValidatorTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public MFValidatorTest(String name){
		
		super(name);
	}

    /**
    *  The JUnit setup method
    */
    public void setUp() throws Exception {
    }

	/**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public static Test suite() {
    	
        TestSuite suite = new TestSuite(MFValidatorTest.class);
        return suite;
	}
    
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testMFValidator_Notnull(){
    	
		assertNotNull(new MFValidator());
	}
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testMFValidator_NOT_ELEMENTS(){
    	
    	IMolecularFormula mf = new MolecularFormula();
		assertEquals(MFValidator.NOT_ELEMENTS,new MFValidator().isValid(mf));
	}
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testMFValidator_Rule1_NOTVALID(){
		MolecularFormula mf = new MolecularFormula();
    	IElement carb = new Element("C");
    	IElement h1 = new Element("H");
        mf.addElement( carb );
        mf.addElement( h1 ,100);
        
        assertEquals(MFValidator.NO_VALID_1,new MFValidator().isValid(mf));

        assertFalse(new MFValidator().isValidRule_1(mf));
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testMFValidator_Rule1_remove_set(){
        
		MFValidator mf = new MFValidator();
		mf.removeRule_1();
        assertEquals(false,mf.isAppliedRule_1());
        
        mf.addRule_1();
        assertEquals(true,mf.isAppliedRule_1());
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testMFValidator_Rule1_VALID(){
		MolecularFormula mf = new MolecularFormula();
    	IElement carb = new Element("C");
    	IElement h1 = new Element("H");
        mf.addElement( carb,2);
        mf.addElement( h1 ,8);
        
        assertEquals(MFValidator.VALID,new MFValidator().isValid(mf));
        
        assertEquals(true,new MFValidator().isValidRule_1(mf));
	}
	
	/**
	 * A unit test suite for JUnit. Ratio C/H
	 *
	 * @return    The test suite
	 */
	public void testMFValidator_Rule2_Ratio_Novalid(){

		MolecularFormula mf = new MolecularFormula();
    	IElement carbone = new Element("C");
        mf.addElement( carbone );
        IElement hydrogen = new Element("H");
        mf.addElement( hydrogen, 10 );
		
		MFValidator mfV = new MFValidator();
		
		mfV.addRule_2();
		assertEquals(MFValidator.NO_VALID_2,mfV.isValid(mf));
	}
	

	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	public void testMFValidator_Rule3_dafault(){

		MFValidator mf = new MFValidator();
		
        assertEquals(false,mf.isAppliedRule_3());
        assertEquals(0.1,mf.getAccuracyAbundance());
        assertEquals(0.01,mf.getAccuracyMass());
        
        
	}
	
	/**
	 * A unit test suite for JUnit. Bromine
	 *
	 * @return    The test suite
	 */
	public void testMFValidator_Rule3_Bromine(){

		MolecularFormula mf1 = new MolecularFormula();
    	IElement bromine = new Element("Br");
        mf1.addElement( bromine,2 );
		
        MolecularFormula mf2 = new MolecularFormula();
    	IElement carb = new Element("CC");
        mf2.addElement( carb,2 );
        
		MFValidator mfV = new MFValidator();
		
		/** experimental results*/
		
		ArrayList<double[]> spectrum = new ArrayList<double[]>();
		spectrum.add(new double[]{157.8367,51.399});
		spectrum.add(new double[]{159.8346,100.00});
		spectrum.add(new double[]{161.8326,48.639});
		
		mfV.addRule_3(spectrum);
		assertEquals(MFValidator.VALID,mfV.isValid(mf1));

		assertEquals(MFValidator.NO_VALID_3,mfV.isValid(mf2));
	}
	
	/**
	 * A unit test suite for JUnit. Bromine
	 *
	 * @return    The test suite
	 */
	public void testMFValidator_Rule3_Orthinine(){

		MolecularFormula mf1 = new MolecularFormula();
    	IElement carb = new Element("C");
        mf1.addElement( carb,5 );
        IElement hyd = new Element("H");
        mf1.addElement( hyd,13 );
        IElement nit = new Element("N");
        mf1.addElement( nit,2 );
        IElement ox = new Element("O");
        mf1.addElement( ox,2 );
		
		MFValidator mfV = new MFValidator();
		
		/** experimental results*/
		
		ArrayList<double[]> spectrum = new ArrayList<double[]>();
		spectrum.add(new double[]{133.0964,100.00});
		spectrum.add(new double[]{133.0991,16.75});
		
		mfV.addRule_3(spectrum);
		assertEquals(MFValidator.VALID,mfV.isValid(mf1));

	}
}

