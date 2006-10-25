/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-05-04 21:29:58 +0200 (Do, 04 Mai 2006) $
 * $Revision: 6171 $
 * 
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.test.qsar.descriptors.bond;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.BondPartialTChargeDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LonePairElectronChecker;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */
 
public class BondPartialTChargeDescriptorTest extends CDKTestCase {
	
	private IBondDescriptor descriptor;
	/**
	 *  Constructor for the BondPartialTChargeDescriptorTest object
	 *
	 */
	public  BondPartialTChargeDescriptorTest() {
		descriptor  = new BondPartialTChargeDescriptor() ;
	}
	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(BondPartialTChargeDescriptorTest.class);
	}
	/**
	 *  A unit test for JUnit
	 */
	public void testBondTElectronegativityDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.3323,0.0218	};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		 
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("CF"); 
        
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
        for (int i = 0 ; i < 2 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getBond(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.01);
		}
        
	}
	/**
	 *  A unit test for JUnit with Allyl bromide
	 */
	public void testBondTElectronegativityDescriptor_Allyl_bromide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0243,0.1279,0.1872,0.1553,0.1553,0.1358,0.0013,0.0013}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C=CCBr");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < 8 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getBond(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.035);
		}
	}
	/**
	 *  A unit test for JUnit with Isopentyl iodide
	 */
	public void testBondTElectronegativityDescriptor_Isopentyl_iodide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double testResult = 0.0165	; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C(C)(C)CCI");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		double result= ((DoubleResult)descriptor.calculate(mol.getBond(0),mol).getValue()).doubleValue();
			assertEquals(testResult,result,0.001);
	}
	/**
	 *  A unit test for JUnit with Allyl mercaptan
	 */
	public void testBondTElectronegativityDescriptor_Allyl_mercaptan() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0197,0.0924,0.1835,0.1566,0.1566,0.1412,0.0323,0.0323,0.2761}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C=CCS");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 9 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getBond(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.03);
		}
	}
}

