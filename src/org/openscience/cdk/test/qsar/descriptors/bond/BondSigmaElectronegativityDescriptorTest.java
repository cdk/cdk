/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-05-04 21:29:58 +0200 (Do, 04 Mai 2006) $
 * $Revision: 6171 $
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.BondSigmaElectronegativityDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-extra
 */
 
public class BondSigmaElectronegativityDescriptorTest extends CDKTestCase {
	
	private IMolecularDescriptor descriptor;
	
	public  BondSigmaElectronegativityDescriptorTest() {
		descriptor  = new BondSigmaElectronegativityDescriptor() ;
	}
    
	public static Test suite() {
		return new TestSuite(BondSigmaElectronegativityDescriptorTest.class);
	}
	/**
	 *  A unit test for JUnit
	 */
	public void testBondSigmaElectronegativityDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={2.5882,1.1894};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		 
		Integer[] params = new Integer[2];
        
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("CF"); 
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        
        for (int i = 0 ; i < 2 ; i++){
			params[0] = new Integer(i);
			params[1] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.01);
		}
        
	}

	/**
	 *  A unit test for JUnit with Methyl chloride
	 */
	public void testBondSigmaElectronegativityDescriptor_Methyl_chloride() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={2.1612,0.8751};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		
		Integer[] params = new Integer[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CCl");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		for (int i = 0 ; i < 2 ; i++){
			params[0] = new Integer(i);
			params[1] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.05);
		}
	}
	/**
	 *  A unit test for JUnit with Allyl bromide
	 */
	public void testBondSigmaElectronegativityDescriptor_Allyl_bromide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.2396,0.3635,1.7086,0.3635,0.338,0.574,0.969,0.969}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		
		Integer[] params = new Integer[2];
        
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C=CCBr");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 8 ; i++){
			params[0] = new Integer(i);
			params[1] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.03);
		}
	}
	/**
	 *  A unit test for JUnit with Isopentyl iodide
	 */
	public void testBondSigmaElectronegativityDescriptor_Isopentyl_iodide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double testResult = 0.1482; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		
		Object[] params = {new Integer(0)};
        descriptor.setParameters(params);
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C(C)(C)CCI");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
		assertEquals(testResult,result,0.001);
	}
	/**
	 *  A unit test for JUnit with Ethoxy ethane
	 */
	public void testBondSigmaElectronegativityDescriptor_Ethoxy_ethane() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.7939,1.0715,1.0715,0.7939,0.2749,0.2749,0.2749,0.8796,0.8796}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		
		Integer[] params = new Integer[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CCOCC");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 8 ; i++){
			params[0] = new Integer(i);
			params[1] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.002);
		}
	}
	/**
	 *  A unit test for JUnit with Ethanolamine
	 */
	public void testBondSigmaElectronegativityDescriptor_Ethanolamine() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0074,0.3728,0.8547,0.2367,0.2367}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		
		Integer[] params = new Integer[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("NCCO");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 5 ; i++){
			params[0] = new Integer(i);
			params[1] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.06);
		}
	}
	/**
	 *  A unit test for JUnit with Allyl mercaptan
	 */
	public void testBondSigmaElectronegativityDescriptor_Allyl_mercaptan() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.1832,0.0143,0.5307,0.3593,0.3593,8.5917}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C=CCS");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 4 ; i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.005);
		}
	}
}

