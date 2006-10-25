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
import org.openscience.cdk.qsar.descriptors.bond.BondPartialSigmaChargeDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */
 
public class BondPartialSigmaChargeDescriptorTest extends CDKTestCase {
	
	private IBondDescriptor descriptor;
	
	public  BondPartialSigmaChargeDescriptorTest() {
		descriptor  = new BondPartialSigmaChargeDescriptor() ;
	}
    
	public static Test suite() {
		return new TestSuite(BondPartialSigmaChargeDescriptorTest.class);
	}
	/**
	 *  A unit test for JUnit
	 */
	public void testBondSigmaElectronegativityDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.3323,0.0218	};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		 
        
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("CF"); 
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        
        for (int i = 0 ; i < 2 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getBond(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.01);
		}
        
	}

	/**
	 *  A unit test for JUnit with Methyl chloride
	 */
	public void testBondSigmaElectronegativityDescriptor_Methyl_chloride() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.2137,0.0075};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CCl");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		for (int i = 0 ; i < 2 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getBond(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.05);
		}
	}
	/**
	 *  A unit test for JUnit with Allyl bromide
	 */
	public void testBondSigmaElectronegativityDescriptor_Allyl_bromide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0265,0.1268,0.1872,0.1564,0.1564,0.1347,0.0013,0.0013}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C=CCBr");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 8 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getBond(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.03);
		}
	}
	/**
	 *  A unit test for JUnit with Isopentyl iodide
	 */
	public void testBondSigmaElectronegativityDescriptor_Isopentyl_iodide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double testResult = 0.0165	; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C(C)(C)CCI");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getBond(0),mol).getValue()).doubleValue();
		assertEquals(testResult,result,0.001);
	}
	/**
	 *  A unit test for JUnit with Ethoxy ethane
	 */
	public void testBondSigmaElectronegativityDescriptor_Ethoxy_ethane() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0864,0.4262,0.4262,0.0864,0.0662,0.0662,0.0662,0.0104,0.0104}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CCOCC");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 8 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getBond(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.002);
		}
	}
	/**
	 *  A unit test for JUnit with Ethanolamine
	 */
	public void testBondSigmaElectronegativityDescriptor_Ethanolamine() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.3463,0.0274,0.448,0.448,0.448}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("NCCO");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 5 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getBond(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.06);
		}
	}
	/**
	 *  A unit test for JUnit with Allyl mercaptan
	 */
	public void testBondSigmaElectronegativityDescriptor_Allyl_mercaptan() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0203,0.0921,0.1835,0.1569,0.3593,8.5917}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C=CCS");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 4 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getBond(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.005);
		}
	}
}

