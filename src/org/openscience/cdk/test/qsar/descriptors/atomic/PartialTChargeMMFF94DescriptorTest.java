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
import org.openscience.cdk.qsar.descriptors.atomic.PartialTChargeMMFF94Descriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */
public class PartialTChargeMMFF94DescriptorTest extends CDKTestCase {
	/**
	 *  Constructor for the PartialTChargeMMFF94DescriptorTest object
	 *
	 */
	public  PartialTChargeMMFF94DescriptorTest() {}
	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(PartialTChargeMMFF94DescriptorTest.class);
	}
	/**
	 *  A unit test for JUnit with Methanol
	 */
	public void testPartialTotalChargeDescriptor_Methanol() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.28,-0.67,0.0,0.0,0.0,0.4};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IMolecularDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CO");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.011);
		}
	}
	/**
	 *  A unit test for JUnit with Methylamine
	 */
	public void testPartialTotalChargeDescriptor_Methylamine() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.27,-0.99,0.0,0.0,0.0,0.36};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IMolecularDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CN");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 6 ; i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.02);
		}
	}
	/**
	 *  A unit test for JUnit with Methane
	 */
	public void testPartialTotalChargeDescriptor_Methane() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.28,-0.56,0.28,};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IMolecularDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("COC");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 3 ; i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.021);
		}
	}
	/**
	 *  A unit test for JUnit with Methanethiol
	 */
	public void testPartialTotalChargeDescriptor_Methanethiol() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.23,-0.41,0.0,};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IMolecularDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CS");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 3 ; i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.04);
		}
	}
	/**
	 *  A unit test for JUnit with Chloromethane
	 */
	public void testPartialTotalChargeDescriptor_Chloromethane() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.29,-0.29,0.0};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IMolecularDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CCl");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 3 ; i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.001);
		}
	}
	/**
	 *  A unit test for JUnit with Benzene
	 */
	public void testPartialTotalChargeDescriptor_Benzene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.15,-0.15,-0.15,-0.15,-0.15,-0.15,0.15,0.15,0.15};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IMolecularDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("c1ccccc1");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 9 ; i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.001);
		}
	}
	/**
	 *  A unit test for JUnit with Water
	 */
	public void testPartialTotalChargeDescriptor_Water() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.86,0.43,0.43};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IMolecularDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("o");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 3 ; i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.06);
		}
	}
}

