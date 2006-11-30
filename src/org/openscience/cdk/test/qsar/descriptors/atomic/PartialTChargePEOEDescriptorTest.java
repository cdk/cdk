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
import org.openscience.cdk.qsar.descriptors.atomic.PartialTChargePEOEDescriptor;
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
public class PartialTChargePEOEDescriptorTest extends CDKTestCase {
	/**
	 *  Constructor for the PartialTChargePEOEDescriptorTest object
	 *
	 */
	public  PartialTChargePEOEDescriptorTest() {}
	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(PartialTChargePEOEDescriptorTest.class);
	}
	/**
	 *  A unit test for JUnit with Ethyl Fluoride
	 */
	public void testPartialTChargeDescriptor_Methyl_Fluoride() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.2527,0.0795,0.0577,0.0577,0.0577};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("FC");
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			
			assertEquals(testResult[i],result, 0.01);
		}
	}
	/**
	 *  A unit test for JUnit with Fluoroethylene
	 */
	public void testPartialTChargeDescriptor_Fluoroethylene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.1839,0.079,-0.1019,0.0942,0.0563,0.0563};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("F-C=C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
	        
	        assertEquals(testResult[i],result, 0.02);
		}
	}
	/**
	 *  A unit test for JUnit with Formic Acid
	 */
	public void testPartialTChargeDescriptor_FormicAcid() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.2672,-0.3877,-0.2365,0.1367,0.2203};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C(=O)O");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			
			assertEquals(testResult[i],result, 0.05);
		}
	}
	/**
	 *  A unit test for JUnit with Fluorobenzene
	 */
	public void testPartialTChargeDescriptor_Fluorobenzene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.1785,0.1227,-0.0373,-0.0598,-0.0683};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("Fc1ccccc1");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < 5 ; i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();

			assertEquals(testResult[i],result, 0.012);
		}
	}
	/**
	 *  A unit test for JUnit with Methoxyethylene
	 */
	public void testPartialTChargeDescriptor_Methoxyethylene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.1211,0.0314,-0.3121,0.0429,0.056,0.056,0.0885,0.056,0.056,0.056};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=C-O-C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
	        
	        assertEquals(testResult[i],result, 0.03);
		}
	}
	/**
	 *  A unit test for JUnit with 1-Methoxybutadiene
	 */
	public void testPartialTChargeDescriptor_1_Methoxybutadiene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.1331,-0.0678,-0.0803,0.0385,-0.2822,0.0429,0.0541,0.0541,0.0619,0.0644,0.0891,0.0528,0.0528,0.0528,0.0528};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=C-C=C-O-C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//	        System.out.println(mol.getAtomAt(i).getSymbol()+" = "+result);
	        assertEquals(testResult[i],result, 0.09);
		}
	}
}

