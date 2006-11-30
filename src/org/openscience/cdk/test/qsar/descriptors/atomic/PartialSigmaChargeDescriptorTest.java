/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-03-30 00:42:34 +0200 (Thu, 30 Mar 2006) $
 * $Revision: 5865 $
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

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialSigmaChargeDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */
public class PartialSigmaChargeDescriptorTest extends CDKTestCase {
	
	private IAtomicDescriptor descriptor = null;
	
	/**
	 *  Constructor for the PartialSigmaChargeDescriptorTest object
	 *
	 */
	public  PartialSigmaChargeDescriptorTest() {}
	
	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(PartialSigmaChargeDescriptorTest.class);
	}
	
	public void setUp() throws CDKException {
		descriptor = new PartialSigmaChargeDescriptor();
		Integer[] params = new Integer[1];
		params[0] = new Integer(6);
        descriptor.setParameters(params);
	}
	
	/**
	 *  A unit test for JUnit with Fluoroethylene
	 */
	public void testPartialSigmaChargeDescriptor_Fluoroethylene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.2138,0.079,0.0942,-0.072,0.0563,0.0563};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("F-C([H])=C([H])[H]");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.003);
		}
	}
	/**
	 *  A unit test for JUnit with Ethyl chloride
	 */
	public void testPartialSigmaChargeDescriptor_Methyl_Floride() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.07915,-0.25264,0.05783,0.05783,0.05783};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CF");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.001);
		}
	}
	/**
	 *  A unit test for JUnit with Methyl chloride
	 */
	public void testPartialSigmaChargeDescriptor_Methyl_chloride() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0382,-0.1755,0.0457,0.0457,0.0457};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CCl");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.001);
		}
	}
	/**
	 *  A unit test for JUnit with Methyl chloride
	 */
	public void testPartialSigmaChargeDescriptor_Methyl_bromide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.021,-0.1448,0.0413,0.0413,0.0413};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CBr");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.01);
		}
	}
	/**
	 *  A unit test for JUnit with Methyl iodide
	 */
	public void testPartialSigmaChargeDescriptor_Methyl_iodide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.0116,-0.0892,0.0336,0.0336,0.0336};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CI");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.001);
		}
	}
	/**
	 *  A unit test for JUnit with Allyl bromide
	 */
	public void testPartialSigmaChargeDescriptor_Allyl_bromide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double testResult = -0.1366;/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=CCBr");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getAtom(3), mol).getValue()).doubleValue();
		assertEquals(testResult,result,0.01);
	}
	/**
	 *  A unit test for JUnit with Isopentyl iodide
	 */
	public void testPartialSigmaChargeDescriptor_Isopentyl_iodide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] testResult = {-0.0458,-0.0623,-0.0623,-0.0415,0.0003,-0.0855}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C(C)(C)CCI");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 6 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.001);
		}
	}
	/**
	 *  A unit test for JUnit with Ethoxy ethane
	 */
	public void testPartialSigmaChargeDescriptor_Ethoxy_ethane() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double testResult = -0.3809; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CCOCC");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getAtom(2), mol).getValue()).doubleValue();
		assertEquals(testResult,result,0.01);
	}
	/**
	 *  A unit test for JUnit with Ethanolamine
	 */
	public void testPartialSigmaChargeDescriptor_Ethanolamine() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.3293,0.017,0.057,-0.3943}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("NCCO");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
        
		for (int i = 0 ; i < 4 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.01);
		}
	}
	/**
	 *  A unit test for JUnit with Allyl mercaptan
	 */
	public void testPartialSigmaChargeDescriptor_Allyl_mercaptan() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] testResult = {-0.1031,-0.0828,0.0093,-0.1742}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=CCS");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 4 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.015);
		}
	}
	/**
	 *  A unit test for JUnit with
	 */
	public void testPartialSigmaChargeDescriptor1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] testResult = {-0.2138,0.079,0.0942,-0.072,0.0563,0.0563}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("[F+]=C([H])[C-]([H])[H]");
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			System.out.println(mol.getAtom(i).getSymbol()+",result: "+result);
			assertEquals(testResult[i],result,0.003);
		}
	}
	/**
	 *  A unit test for JUnit with
	 */
	public void testPartialSigmaChargeDescriptor2() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] testResult = {-0.3855,-0.0454,0.0634,-0.0544,-0.0391,-0.0391}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("O=C([H])[C-]([H])[H]");
		Integer[] object = {new Integer(6)};
		descriptor.setParameters(object);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			System.out.println(mol.getAtom(i).getSymbol()+",result: "+result);
			assertEquals(testResult[i],result,0.2);
		}
	}
	/**
	 *  A unit test for JUnit with
	 */
	public void testPartialSigmaChargeDescriptor3() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] testResult = {-0.3855,-0.0454,0.0634,-0.0544,-0.0391,-0.0391}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("[O-]C([H])=C([H])[H]");
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			System.out.println(mol.getAtom(i).getSymbol()+",result: "+result);
			assertEquals(testResult[i],result,0.2);
		}
	}
	/**
	 *  A unit test for JUnit with
	 */
	public void testPartialSigmaChargeDescriptor4() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] testResult = {-0.3041,0.1055,0.0993,0.0993}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("O=C([H])([H])");
		Integer[] object = {new Integer(6)};
		descriptor.setParameters(object);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			System.out.println(mol.getAtom(i).getSymbol()+",result: "+result);
			assertEquals(testResult[i],result,0.003);
		}
	}
	/**
	 *  A unit test for JUnit with
	 */
	public void testPartialSigmaChargeDescriptor5() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] testResult = {-0.3291,0.144,0.1028,-0.0084,0.0303,0.0303,0.0303}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("O=C([H])C([H])([H])[H]");
		Integer[] object = {new Integer(6)};
		descriptor.setParameters(object);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			System.out.println(mol.getAtom(i).getSymbol()+",result: "+result);
			assertEquals(testResult[i],result,0.03);
		}
	}
	/**
	 *  A unit test for JUnit with
	 */
	public void testPartialSigmaChargeDescriptor6() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] testResult = {-0.4331,-0.1067,0.0133,0.0133,0.0133}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("[O-]C([H])([H])[H]");
		Integer[] object = {new Integer(6)};
		descriptor.setParameters(object);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			System.out.println(mol.getAtom(i).getSymbol()+",result: "+result);
			assertEquals(testResult[i],result,0.3);
		}
	}
	/**
	 *  A unit test for JUnit with [H]c1[n-][c+]([H])c([H])c([H])c1([H])
	 */
	public void testPartialSigmaChargeDescriptor7() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] testResult = {0.0835,0.0265,-0.2622,0.0265,0.0835,-0.0444,0.064,-0.0596,0.0626,-0.0444,0.064}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("[H]c1[n-][c+]([H])c([H])c([H])c1([H])");
		Integer[] object = {new Integer(6)};
		descriptor.setParameters(object);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			System.out.println(mol.getAtom(i).getSymbol()+",result: "+result);
			assertEquals(testResult[i],result,0.05);
		}
	}
}

