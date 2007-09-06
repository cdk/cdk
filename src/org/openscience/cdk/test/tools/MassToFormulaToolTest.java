/* $Revision: 8658 $ $Author: egonw $ $Date: 2007-08-03 15:20:28 +0200 (Fri, 03 Aug 2007) $
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.MassToFormulaTool;

/**
 * @cdk.module test-standard
 */
public class MassToFormulaToolTest extends CDKTestCase {

	/**
	 *  Constructor for the MassToFormulaToolTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public MassToFormulaToolTest(String name){
		
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
    	
        TestSuite suite = new TestSuite(MassToFormulaToolTest.class);
        return suite;
	}
    
    public void testMassToFormulaTool()	{
    	
		assertNotNull(new MassToFormulaTool(0.0));
	}
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testMassToFormulaTool_null(){
    	
		assertNull(new MassToFormulaTool(0.0).getMolecularFormula());
	}
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
	public void testMassToFormulaTool1(){
    	
		assertNotNull(new MassToFormulaTool(44.0032).getMolecularFormula());
	}
	/**
	 * A unit test suite for JUnit. Results contrasted with the page:
	 * http://www.ch.ic.ac.uk/java/applets/f2m2f/
	 *
	 * @return    The test suite
	 */
	public void testMassToFormulaTool2(){
		String[] results = {"O2C1","O1N2","O1N1C1H2","O1C2H4","N3H2","N2C1H4","N1C2H6"};
		ArrayList resultsMF = new MassToFormulaTool(44.0032).getMolecularFormula();
		for(int i = 0 ; i < resultsMF.size(); i++){
			assertEquals(results[i],(String)resultsMF.get(i));
		}
	}
	/**
	 * A unit test suite for JUnit. Results contrasted with the page:
	 * http://www.ch.ic.ac.uk/java/applets/f2m2f/
	 *
	 * @return    The test suite
	 */
	public void testMassToFormulaTool3(){
		String[] results = {"O2C1","O1N2","O1N1C1H2","O1C2H4","N3H2","N2C1H4","N1C2H6"};
		ArrayList resultsMF = new MassToFormulaTool(44.0032).getMoleculesFormulaOrned();
//		for(int i = 0 ; i < resultsMF.size(); i++){
//			System.out.println((String)resultsMF.get(i));
//		}
	}
	/**
	 * A unit test suite for JUnit. Results contrasted with the page:
	 * http://www.ch.ic.ac.uk/java/applets/f2m2f/
	 *
	 * @return    The test suite
	 */
	public void testMassToFormulaTool4(){
		
	}
}

