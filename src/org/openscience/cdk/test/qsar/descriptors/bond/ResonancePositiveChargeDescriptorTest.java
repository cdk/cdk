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
package org.openscience.cdk.test.qsar.descriptors.bond;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.ResonancePositiveChargeDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LonePairElectronChecker;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */
public class ResonancePositiveChargeDescriptorTest extends CDKTestCase {
	/**
	 *  Constructor for the ResonancePositiveChargeDescriptorTest object
	 *
	 */
	public  ResonancePositiveChargeDescriptorTest() {}
	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(ResonancePositiveChargeDescriptorTest.class);
	}
	/**
	 *  A unit test for JUnit with C=CCC
	 */
	public void testResonancePositiveCharge_1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0, 5.5925,0.0,0.0,0.0,0.0,0.0,0.0,5.5925,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IMolecularDescriptor descriptor = new ResonancePositiveChargeDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C=CF");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		params[0] = new Integer(0);
        descriptor.setParameters(params);
        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
        
		assertEquals(testResult[0],dar.get(0), 0.01);
		assertEquals(testResult[1],dar.get(1), 1.3);
		assertFalse(0.0 == dar.get(1));

		params[0] = new Integer(1);
		descriptor = new ResonancePositiveChargeDescriptor();
        descriptor.setParameters(params);
        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
		assertEquals(testResult[2],dar.get(0), 0.0001);
		assertEquals(testResult[3],dar.get(1), 0.0001);
		
        params[0] = new Integer(2);
        descriptor = new ResonancePositiveChargeDescriptor();
        descriptor.setParameters(params);
        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
		assertEquals(testResult[4],dar.get(0), 0.00001);
		assertEquals(testResult[5],dar.get(1), 0.00001);
		
		params[0] = new Integer(3);
        descriptor.setParameters(params);
        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
		assertEquals(testResult[6],dar.get(0), 0.00001);
		assertEquals(testResult[7],dar.get(1), 0.00001);

		params[0] = new Integer(4);
		descriptor = new ResonancePositiveChargeDescriptor();
        descriptor.setParameters(params);
        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
		assertEquals(testResult[8],dar.get(0), 1.53);
		assertFalse(0.0 == dar.get(0));
		assertEquals(testResult[9],dar.get(1), 0.00001);

	        
	}
	/**
	 *  A unit test for JUnit with C=CCC
	 */
	public void testResonancePositiveCharge_2() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={4.6593,0.0, 0.0,0.0,3.5752,0.0,3.5752,0.0,4.6593,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IMolecularDescriptor descriptor = new ResonancePositiveChargeDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C(=O)C=C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
			params[0] = new Integer(0);
	        descriptor.setParameters(params);
	        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
	        
			assertEquals(testResult[1],dar.get(0), 0.0001);
	        assertEquals(testResult[0],dar.get(1), 4.0);
			assertFalse(0.0 == dar.get(1));

			params[0] = new Integer(1);
			descriptor = new ResonancePositiveChargeDescriptor();
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
			assertEquals(testResult[2],dar.get(0), 0.0001);
			assertEquals(testResult[3],dar.get(1), 0.0001);

			params[0] = new Integer(2);
			descriptor = new ResonancePositiveChargeDescriptor();
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
			assertEquals(testResult[4],dar.get(0), 2.5);
			assertFalse(0.0 == dar.get(0));
			/*assertEquals(testResult[5],dar.get(1), 0.0001); <= it should be null*/
			
			params[0] = new Integer(3);
			descriptor = new ResonancePositiveChargeDescriptor();
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
			assertEquals(testResult[6],dar.get(0), 1.7);
			assertFalse(0.0 == dar.get(0));
			assertEquals(testResult[7],dar.get(1), 0.0001);
			
			params[0] = new Integer(4);
			descriptor = new ResonancePositiveChargeDescriptor();
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
			assertEquals(testResult[8],dar.get(0), 2);
			assertFalse(0.0 == dar.get(0));
			assertEquals(testResult[9],dar.get(1),  0.0001);

	        
	}
	/**
	 *  A unit test for JUnit with C(=O)O
	 */
	public void testResonancePositiveCharge_3() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={5.5662, 0.0,0.0,0.0,5.5662,0.0,3.6611,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IMolecularDescriptor descriptor = new ResonancePositiveChargeDescriptor();
		Integer[] params = new Integer[1];
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C(=O)O");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
			params[0] = new Integer(0);
	        descriptor.setParameters(params);
	        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
			
	        assertEquals(testResult[1],dar.get(0), 0.0001);
	        assertEquals(testResult[0],dar.get(1), 4.5);
			assertFalse(0.0 == dar.get(1));

			params[0] = new Integer(1);
			descriptor = new ResonancePositiveChargeDescriptor();
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
			assertEquals(testResult[2],dar.get(0), 0.0001);
			assertEquals(testResult[3],dar.get(1), 0.0001);

	        params[0] = new Integer(2);
	        descriptor = new ResonancePositiveChargeDescriptor();
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
			assertEquals(testResult[4],dar.get(0), 4.0);
			assertFalse(0.0 == dar.get(0));
			assertEquals(testResult[5],dar.get(1), 0.00001);
			
			params[0] = new Integer(3);
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
			
			assertEquals(testResult[7],dar.get(0), 0.00001);
			/*assertEquals(testResult[6],dar.get(1), 1.2); <= Expect */

	        
	}
	/**
	 *  A unit test for JUnit with C=CC
	 */
	public void testResonancePositiveCharge_4() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0, 3.9498,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IMolecularDescriptor descriptor = new ResonancePositiveChargeDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C=CC");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
			params[0] = new Integer(0);
	        descriptor.setParameters(params);
	        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
			assertEquals(testResult[0],dar.get(0), 0.001);
			assertEquals(testResult[1],dar.get(1), 1.8);
			assertFalse(0.0 == dar.get(1));

			params[0] = new Integer(1);
			descriptor = new ResonancePositiveChargeDescriptor();
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
			assertEquals(testResult[2],dar.get(0), 0.0001);
			assertEquals(testResult[3],dar.get(1), 0.0001);

	        params[0] = new Integer(2);
	        descriptor = new ResonancePositiveChargeDescriptor();
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
			assertEquals(testResult[4],dar.get(0), 0.00001);
			assertEquals(testResult[5],dar.get(1), 0.00001);
			
			params[0] = new Integer(3);
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
			assertEquals(testResult[6],dar.get(0), 0.00001);
			assertEquals(testResult[7],dar.get(1), 0.00001);

	        
	}
	/**
	 *  A unit test for JUnit with CCOCCCO
	 */
	public void testResonancePositiveCharge_5() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0, 3.9498,3.9235,3.5005,3.5212,3.75,3.5149,11.658,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IMolecularDescriptor descriptor = new ResonancePositiveChargeDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CCOCCCO");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
			params[0] = new Integer(0);
	        descriptor.setParameters(params);
	        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
	        assertEquals(testResult[0],dar.get(0), 0.001);
			assertEquals(testResult[1],dar.get(1), 2.0);

			params[0] = new Integer(1);
			descriptor = new ResonancePositiveChargeDescriptor();
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
//	        System.out.println(dar.get(0)+" ; "+dar.get(1) );
			/*assertEquals(testResult[2],dar.get(0), 0.0001); The result should not be null*/
			assertEquals(testResult[3],dar.get(1), 1.0);

			params[0] = new Integer(2);
	        descriptor = new ResonancePositiveChargeDescriptor();
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
//	        System.out.println(dar.get(0)+" ; "+dar.get(1) );
	        /*assertEquals(testResult[4],dar.get(0), 0.0001); The result should not be null*/
			assertEquals(testResult[5],dar.get(1), 8.0);
			
			params[0] = new Integer(3);
	        descriptor.setParameters(params);
	        dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
//	        System.out.println(dar.get(0)+" ; "+dar.get(1) );
			assertEquals(testResult[6],dar.get(0), 1.5);
			assertEquals(testResult[7],dar.get(1), 4.0);

	        
	}
	/**
	 *  A unit test for JUnit with CCOCCCO
	 */
	public void testResonancePositiveCharge_6() throws ClassNotFoundException, CDKException, java.lang.Exception {
//		double [] testResult={0.0, 0.0, 0.0,0.0,0.0,3.5725};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IMolecularDescriptor descriptor = new ResonancePositiveChargeDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("[H]C([H])=C([H])C([H])([H])C(=[O+])C([H])([H])C([H])([H])[H]");

		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for(int i = 0; i < mol.getBondCount(); i++){	
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
	        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
		}
	}
	/**
	 *  A unit test for JUnit with CCOCCCO
	 */
	public void testResonancePositiveCharge_7() throws ClassNotFoundException, CDKException, java.lang.Exception {
//		double [] testResult={0.0, 0.0, 0.0,0.0,0.0,3.5725};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IMolecularDescriptor descriptor = new ResonancePositiveChargeDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("[H]C([H])[C+]([H])C([H])([H])C(=O)C([H])([H])C([H])([H])[H]");
		mol.addElectronContainer(new SingleElectron(mol.getAtom(1)));
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for(int i = 6; i < 8; i++){	
			params[0] = new Integer(i);
	        descriptor.setParameters(params);
	        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
		}
	}

}

