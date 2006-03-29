/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *   *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.charges;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 *  Description of the Class
 *
 * @cdk.module test
 *
 *@author     chhoppe
 *@cdk.created    2004-11-04
 */
public class PolarizabilityTest extends CDKTestCase {
	
	boolean standAlone = false;
	
	/**
	 *  Constructor for the PolarizabilityTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public  PolarizabilityTest(){}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(PolarizabilityTest.class);
	}
	
	/**
	 *  Sets the standAlone attribute
	 *
	 *@param  standAlone  The new standAlone value
	 */
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	
	/**
	 *  A unit test for JUnit with n,n-dimethyl ethylendiamine
	 */
	public void testcalculateGHEffectiveAtomPolarizability(){
		double [] testResult={4.73,6.92};
		Polarizability pol=new Polarizability();
		try{
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles("NCCN(C)(C)");
			double result=pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtomAt(0),100);
			assertEquals(testResult[0],result,0.01);
			result=pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtomAt(3),100);
			assertEquals(testResult[1],result,0.01);
			
		} catch (Exception exc){
			if (standAlone)	{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
	
	
	/**
	 *  A unit test for JUnit with methane
	 */
	public void testCalculateKJMeanMolecularPolarizability(){
		double testResult=2.61;
		Polarizability pol=new Polarizability();
		try{
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles("C");
			double result=pol.calculateKJMeanMolecularPolarizability(mol);
			assertEquals(testResult,result,0.01);
		} catch (Exception exc){
			if (standAlone)	{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
}
