/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
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
package org.openscience.cdk.test.qsar.descriptors.atomic;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PiElectronegativityDescriptor;
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
 
public class PiElectronegativityDescriptorTest extends CDKTestCase {
	/**
	 *  Constructor for the PiElectronegativityDescriptorTest object
	 *
	 */
	public  PiElectronegativityDescriptorTest() {}
	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(PiElectronegativityDescriptorTest.class);
	}
    
	/**
	 *  A unit test for JUnit with Methyl Fluoride
	 */
	public void testPiElectronegativityDescriptor_Methyl_Fluoride() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={3.9608,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("FC");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
			params[0] = new Integer(10);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//			System.out.println("result: "+result);
			if(result == 0.0)
				assertEquals(testResult[i],result, 0.0001);
			else {
				assertTrue(result != 0.0);
				assertEquals(testResult[i],result, 4);
			}
		}
	}
	/**
	 *  A unit test for JUnit with Methyl Chloride
	 */
	public void testPiElectronegativityDescriptor_Methyl_Chloride() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={4.7054,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("ClC");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
			params[0] = new Integer(10);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//			System.out.println("result: "+result);
			if(result == 0.0)
				assertEquals(testResult[i],result, 0.0001);
			else {
				assertTrue(result != 0.0);
				assertEquals(testResult[i],result, 2);
			}
		}
	}
	/**
	 *  A unit test for JUnit with Methyl iodide
	 */
	public void testPiElectronegativityDescriptor_Methyl_Iodide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={4.1951,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("IC");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
			params[0] = new Integer(10);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//			System.out.println("result: "+result);
			if(result == 0.0)
				assertEquals(testResult[i],result, 0.0001);
			else {
				assertTrue(result != 0.0);
				assertEquals(testResult[i],result, 1);
			}
		}
	}
	/**
	 *  A unit test for JUnit with Methyl Bromide
	 */
	public void testPiElectronegativityDescriptor_Methyl_Bromide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={3.8922,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("BrC");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
			params[0] = new Integer(10);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//			System.out.println("result: "+result);
			if(result == 0.0)
				assertEquals(testResult[i],result, 0.0001);
			else {
				assertTrue(result != 0.0);
				assertEquals(testResult[i],result, 2);
			}
		}
	}
	/**
	 *  A unit test for JUnit with Methyl Alcohol
	 */
	public void testPiElectronegativityDescriptor_Methyl_Alcohol() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={3.1138,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("OC");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < 4; i++){
			params[0] = new Integer(10);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//			System.out.println("result: "+result);
			if(result == 0.0)
				assertEquals(testResult[i],result, 0.0001);
			else {
				assertTrue(result != 0.0);
				assertEquals(testResult[i],result, 5);
			}
		}
	}
	/**
	 *  A unit test for JUnit with Formaldehyde
	 */
	public void testPiElectronegativityDescriptor_Formaldehyde() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={6.3012,8.0791,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C=O");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
			params[0] = new Integer(10);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//			System.out.println("result: "+result);
			if(result == 0.0)
				assertEquals(testResult[i],result, 0.0001);
			else {
				assertTrue(result != 0.0);
				assertEquals(testResult[i],result, 2.7);
			}
		}
	}
	/**
	 *  A unit test for JUnit with Ethylene
	 */
	public void testPiElectronegativityDescriptor_Ethylene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		
		double [] testResult={5.1519,5.1519,0.0,0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C=C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		for (int i = 0 ; i < 3 ; i++){
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();

//	        System.out.println("result: "+result);
			if(result == 0.0)
				assertEquals(testResult[i],result, 0.0001);
			else {
				assertTrue(result != 0.0);
				assertEquals(testResult[i],result, 1);
			}
		}
	}

	/**
	 *  A unit test for JUnit with Fluoroethylene
	 */
	public void testPiElectronegativityDescriptor_Fluoroethylene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={4.7796,5.9414,5.0507,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("F-C=C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < 3 ; i++){
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//	        System.out.println("result: "+result);
			if(result == 0.0)
				assertEquals(testResult[i],result, 0.0001);
			else {
				assertTrue(result != 0.0);
				assertEquals(testResult[i],result, 3.5);
			}
		}
	}
	/**
	 *  A unit test for JUnit with Formic Acid
	 */
	public void testPiElectronegativityDescriptor_FormicAcid() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={6.8954,7.301,4.8022,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C(=O)O");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//			System.out.println("result: "+result);
			if(result == 0.0)
				assertEquals(testResult[i],result, 0.0001);
			else {
				assertTrue(result != 0.0);
				assertEquals(testResult[i],result, 5);
			}
		}
	}
	/**
	 *  A unit test for JUnit with Methoxyethylene
	 */
	public void testPiElectronegativityDescriptor_Methoxyethylene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={4.916,5.7345,3.971,0.0,0.0,0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C=C-O-C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
			params[0] = new Integer(10);
	        descriptor.setParameters(params);
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//	        System.out.println("result: "+result);
			if(result == 0.0)
				assertEquals(testResult[i],result, 0.0001);
			else {
				assertTrue(result != 0.0);
				assertEquals(testResult[i],result, 5);
			}
		}
	}
	/**
	 *  A unit test for JUnit with F[C+][C-]
	 */
	public void testPiElectronegativity1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={5.1788,5.465,5.2475,0.0,0.0,0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("F[C+][C-]");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
			params[0] = new Integer(6);
	        descriptor.setParameters(params);
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//	        System.out.println(mol.getAtomAt(i).getSymbol()+"-result: "+result);
			if(result == 0.0)
				assertEquals(testResult[i],result, 0.0001);
			else {
				assertTrue(result != 0.0);
				assertEquals(testResult[i],result, 3);
			}
		}
	}
	/**
	 *  A unit test for JUnit with CCOCCCO
	 */
	public void testPiElectronegativity2() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0,0.0,3.2849,0.0,0.0,0.0,3.2849,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PiElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CCOCCCO");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
			params[0] = new Integer(6);
	        descriptor.setParameters(params);
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//	        System.out.println(mol.getAtom(i).getSymbol()+"-result: "+result);
			if(result == 0.0)
				assertEquals(testResult[i],result, 0.0001);
			else {
				assertTrue(result != 0.0);
				assertEquals(testResult[i],result, 5);
			}
		}
	}
}

