/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.qsar.descriptors.atomic.SigmaElectronegativityDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */
 
public class SigmaElectronegativityDescriptorTest extends CDKTestCase {
	
	public  SigmaElectronegativityDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(SigmaElectronegativityDescriptorTest.class);
	}
    
	public void testSigmaElectronegativityDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={8.7177,11.306};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor  = new SigmaElectronegativityDescriptor() ;
		Integer[] params = new Integer[1];
        
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule mol = sp.parseSmiles("CF"); 
        addExplicitHydrogens(mol);
        
        for (int i = 0 ; i < 2 ; i++){
			params[0] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.01);
		}
        
	}

	/**
	 *  A unit test for JUnit with Methyl chloride
	 */
	public void testSigmaElectronegativityDescriptor_Methyl_chloride() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={8.3293,10.491};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CCl");
		addExplicitHydrogens(mol);
		for (int i = 0 ; i < 2 ; i++){
			params[0] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.05);
		}
	}
	/**
	 *  A unit test for JUnit with Allyl bromide
	 */
	public void testSigmaElectronegativityDescriptor_Allyl_bromide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={7.8677,8.1073,8.4452,10.154}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=CCBr");
		addExplicitHydrogens(mol);
		
		for (int i = 0 ; i < 4 ; i++){
			params[0] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.02);
		}
	}
	/**
	 *  A unit test for JUnit with Isopentyl iodide
	 */
	public void testSigmaElectronegativityDescriptor_Isopentyl_iodide() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double testResult = 9.2264; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C(C)(C)CCI");
		addExplicitHydrogens(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getAtom(5),mol).getValue()).doubleValue();
		assertEquals(testResult,result,0.08);
	}
	/**
	 *  A unit test for JUnit with Ethoxy ethane
	 */
	public void testSigmaElectronegativityDescriptor_Ethoxy_ethane() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={7.6009,8.3948,9.4663,8.3948,7.6009}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CCOCC");
		addExplicitHydrogens(mol);
		
		for (int i = 0 ; i < 5 ; i++){
			params[0] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.002);
		}
	}
	/**
	 *  A unit test for JUnit with Ethanolamine
	 */
	public void testSigmaElectronegativityDescriptor_Ethanolamine() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={8.1395,8.1321,8.5049,9.3081}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("NCCO");
		addExplicitHydrogens(mol);
		
		for (int i = 0 ; i < 4 ; i++){
			params[0] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.002);
		}
	}
	/**
	 *  A unit test for JUnit with Allyl mercaptan
	 */
	public void testSigmaElectronegativityDescriptor_Allyl_mercaptan() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={7.8634,8.0467,8.061,8.5917}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=CCS");
		addExplicitHydrogens(mol);
		
		for (int i = 0 ; i < 4 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,0.01);
		}
	}
}

