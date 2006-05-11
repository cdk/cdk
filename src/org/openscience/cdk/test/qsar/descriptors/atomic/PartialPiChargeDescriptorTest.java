/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-03-30 00:42:34 +0200 (Thu, 30 Mar 2006) $
 * $Revision: 5865 $
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
package org.openscience.cdk.test.qsar.descriptors.atomic;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialPiChargeDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LonePairElectronChecker;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-extra
 */
public class PartialPiChargeDescriptorTest extends CDKTestCase {
	/**
	 *  Constructor for the PartialPiChargeDescriptorTest object
	 *
	 */
	public  PartialPiChargeDescriptorTest() {}
	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(PartialPiChargeDescriptorTest.class);
	}
	/**
	 *  A unit test for JUnit with Ethyl Fluoride
	 */
	public void testPartialSigmaChargeDescriptor_Methyl_Fluoride() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IMolecularDescriptor descriptor = new PartialPiChargeDescriptor();
		Integer[] params = new Integer[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("FC");
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			params[0] = new Integer(i);
			params[1] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			double factor = 0.0;
			if(testResult[i] > 0)
				factor = 1;
			else if(testResult[i] < 0)
				factor = -1;
			
			assertEquals(factor,result, 0.00001);
		}
	}
	/**
	 *  A unit test for JUnit with Fluoroethylene
	 */
	public void testPartialSigmaChargeDescriptor_Fluoroethylene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0299,0.0,-0.0299,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IMolecularDescriptor descriptor = new PartialPiChargeDescriptor();
		Integer[] params = new Integer[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("F-C=C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			params[0] = new Integer(i);
			params[1] = new Integer(6);
	        descriptor.setParameters(params);
	        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
	        double factor = 0.0;
			if(testResult[i] > 0)
				factor = 1;
			else if(testResult[i] < 0)
				factor = -1;
			
			assertEquals(factor,result, 0.00001);
		}
	}
	/**
	 *  A unit test for JUnit with Formic Acid
	 */
	public void testPartialSigmaChargeDescriptor_FormicAcid() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0221,-0.1193,0.0972,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IMolecularDescriptor descriptor = new PartialPiChargeDescriptor();
		Integer[] params = new Integer[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C(=O)O");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			params[0] = new Integer(i);
			params[1] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			double factor = 0.0;
			if(testResult[i] > 0)
				factor = 1;
			else if(testResult[i] < 0)
				factor = -1;
			
			assertEquals(factor,result, 0.00001);
		}
	}
	/**
	 *  A unit test for JUnit with Fluorobenzene
	 */
	public void testPartialSigmaChargeDescriptor_Fluorobenzene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0262,0.0,-0.0101,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,-0.006,0.0,-0.0101,0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IMolecularDescriptor descriptor = new PartialPiChargeDescriptor();
		Integer[] params = new Integer[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("Fc1ccccc1");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			params[0] = new Integer(i);
			params[1] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			double factor = 0.0;
			if(testResult[i] > 0)
				factor = 1;
			else if(testResult[i] < 0)
				factor = -1;
			assertEquals(factor,result, 0.00001);
		}
	}
}

