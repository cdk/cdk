/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-03-30 00:42:34 +0200 (Thu, 30 Mar 2006) $
 * $Revision: 5865 $
 * 
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
import org.openscience.cdk.LonePair;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialPiChargeDescriptor;
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
	public void testPartialPiChargeDescriptor_Methyl_Fluoride() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("FC");
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			
			if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result, 0.0001);
		}
	}
	/**
	 *  A unit test for JUnit with Fluoroethylene
	 */
	public void testPartialPiChargeDescriptor_Fluoroethylene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0299,0.0,-0.0299,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("F-C=C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
	        
	        
	        if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result, 0.025);
		}
	}
	/**
	 *  A unit test for JUnit with Formic Acid
	 */
	public void testPartialPiChargeDescriptor_FormicAcid() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0221,-0.1193,0.0972,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C(=O)O");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();

			if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result, 0.05);
		}
	}
	/**
	 *  A unit test for JUnit with Fluorobenzene
	 */
	public void testPartialPiChargeDescriptor_Fluorobenzene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0262,0.0,-0.0101,0.0,-0.006,0.0,-0.0101,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
		Integer[] params = new Integer[1];
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("Fc1ccccc1");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			params[0] = new Integer(6);
	        descriptor.setParameters(params);
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			/* test sign*/

			if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result, 0.01);
		}
	}
	/**
	 *  A unit test for JUnit with Methoxyethylene
	 */
	public void testPartialPiChargeDescriptor_Methoxyethylene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.044,0.0,0.044,0.0,0.0,0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
		Integer[] params = new Integer[1];
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=C-O-C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < 4/*mol.getAtomCount() */; i++){
			params[0] = new Integer(6);
	        descriptor.setParameters(params);
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();

	        if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result, 0.025);
		}
	}
	/**
	 *  A unit test for JUnit with 1-Methoxybutadiene
	 */
	public void testPartialPiChargeDescriptor_1_Methoxybutadiene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.0333,0.0,-0.0399,0.0,0.0733,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=C-C=C-O-C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();

	        if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result, 0.09);
		}
	}
	/**
	 * get the sign of a value
	 */
	private double getSign(double d) {
		double sign = 0.0;
		if(d > 0)
			sign = 1;
		else if(d < 0)
			sign = -1;
		return sign;
	}
	/**
	 *  A unit test for JUnit 
	 */
	public void testPartialPiChargeDescriptoCharge_1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0613,-0.0554,0.0,-0.0059,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("F[C+]([H])[C-]([H])[H]");

		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < 6; i++){
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//	        logger.debug(mol.getAtom(i).getSymbol()+"-result: "+result);
	        
	        if(i == 3)/*not get same signus like Gasteiger*/
	        	continue;
	        if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result, 0.02);
			
		}
	}
	/**
	 *  A unit test for JUnit 
	 */
	public void testPartialPiChargeDescriptoCharge_2() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.0822,0.02,0.0,0.0423,0.0,0.02,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		double [] testResultMy={-0.1090,0.04283,-0.0139,0.05130,-0.01397,0.042839,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};/* my */
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("n1ccccc1");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//	        logger.debug(mol.getAtom(i).getSymbol()+"-result: "+result);
	        
	        if(testResultMy[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResultMy[i]),getSign(result), 0.00001);
			}
			assertEquals(testResultMy[i],result, 0.03);
		}
	}
	/**
	 *  A unit test for JUnit. This molecule break. With PETRA as well.
	 */
	public void testPartialPiChargeDescriptoCharge_3() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.0379,-0.0032,0.0,-0.0078,0.0,0.0488,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("O=C([H])[C+]([H])[C-]([H])[H]");
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//	        logger.debug(mol.getAtom(i).getSymbol()+"-result: "+result);

	        if(i == 0 || i == 1 || i == 3)/*not get same signus like Gasteiger*/
	        	continue;
	        
	        if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result, 0.15);
		}
	}

	/**
	 *  A unit test for JUnit. This molecule break. With PETRA as well.
	 */
	public void testPartialPiChargeDescripto4() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CCOCCCO");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount(); i++){
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
//	        logger.debug(mol.getAtom(i).getSymbol()+"-result: "+result);

			assertEquals(testResult[0],result, 0.0001);
		}
	}
	/**
	 *  A unit test for JUnit with 
	 */
	public void testArticle1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        double[] testResult = {0.0,0.0216,-0.1644,0.1428,0.0,0.0,0.0,0.0,0.0,0.0}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CC(=O)N");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		Object[] object = {new Integer(6),new Boolean(true)};
		descriptor.setParameters(object);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			logger.debug(mol.getAtom(i).getSymbol()+",result: "+result);

			if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result,0.1);
		}
	}
	/**
	 *  A unit test for JUnit with FC(=O)CCc1cccc(F)c1(C)
	 */
	public void testSousa() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        double[] testResult = {0.0914,0.0193,-0.1107,0.0,0.0,0.0,-0.0063,0.0,-0.0101,0.0,0.0262,-0.0098,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("FC(=O)CCc1cccc(F)c1(C)");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		Object[] object = {new Integer(6),new Boolean(true)};
		descriptor.setParameters(object);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			logger.debug(mol.getAtom(i).getSymbol()+",result: "+result);

			if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result,0.07);
		}
	}
	/**
	 *  A unit test for JUnit with [H]C([H])=C([H])C([H])([H])C([H])=O
	 */
	public void testBondNotConjugated() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        double[] testResult = {0.0,0.0004,0.0,-0.0004,0.0,0.0,0.0,0.0,0.0277,0.0,-0.0277}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("[H]C([H])=C([H])C([H])([H])C([H])=O");
		Object[] object = {new Integer(6),new Boolean(true)};
		descriptor.setParameters(object);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			logger.debug(mol.getAtom(i).getSymbol()+",result: "+result);
			
	        if(i == 1 || i == 3)/*not get same signus like Gasteiger*/
	        	continue;
			
	        if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result,0.02);
		}
	}
	/**
	 *  A unit test for JUnit with [H]C([H])=C([H])C([H])([H])C([H])=O
	 */
	public void testBondNotConjugated1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        double[] testResult = {0.0,-0.0009,0.0,0.0009,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("[H]C([H])=C([H])C([H])([H])[H]");
		Object[] object = {new Integer(6),new Boolean(true)};
		descriptor.setParameters(object);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			logger.debug(mol.getAtom(i).getSymbol()+",result: "+result);

	        if(i == 8)/*not get same signus like Gasteiger*/
	        	continue;
			
			if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result,0.02);
		}
	}
	/**
	 *  A unit test for JUnit with [H]C([H])=C([H])[C+]([H])[H]
	 */
	public void testBondNotConjugated2() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        double[] testResult = {0.0,0.25,0.0,0.0,0.0,0.25,0.0,0.0,0.0,0.0,0.0,0.0,}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("[H]C([H])=C([H])[C+]([H])[H]");
		Object[] object = {new Integer(6),new Boolean(true)};
		descriptor.setParameters(object);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			logger.debug(mol.getAtom(i).getSymbol()+",result: "+result);

	        if(i == 5)/*not get same signus like Gasteiger*/
	        	continue;
	        
			if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result,0.21);
		}
	}


	/**
	 *  A unit test for JUnit with [C]=C=C=O
	 */
	public void testWithRadical() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
        double[] testResult = {0.0,0.25,0.0,0.0,0.0,0.25,0.0,0.0,0.0,0.0,0.0,0.0,}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("[C]=C=C=O");
//		mol.addElectronContainer(new SingleElectron(mol.getAtom(0)));
		mol.addLonePair(new LonePair(mol.getAtom(3)));
		Object[] object = {new Integer(6),new Boolean(false)};
		descriptor.setParameters(object);

		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			logger.debug(mol.getAtom(i).getSymbol()+",result: "+result);
			
	        if(i == 1 || i == 2 || i == 3)/*not get same signus like Gasteiger*/
	        	continue;
			
			if(testResult[i] == 0.0)
				assertTrue(result == 0.0);
			else {
				assertTrue(result != 0.0);
				assertEquals(getSign(testResult[i]),getSign(result), 0.00001);
			}
			assertEquals(testResult[i],result,0.21);
		}
	}
	
	/**
	 *  A unit test for JUnit with c1ccc(cc1)n3c4ccccc4(c2ccccc23)
	 */
	public void testLangCalculation() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IAtomicDescriptor descriptor = new PartialPiChargeDescriptor();
//        double[] testResult = {0.0,0.25,0.0,0.0,0.0,0.25,0.0,0.0,0.0,0.0,0.0,0.0,}; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("c1ccc(cc1)n3c4ccccc4(c2ccccc23)");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		Object[] object = {new Integer(6),new Boolean(false)};
		descriptor.setParameters(object);

		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
//			logger.debug(mol.getAtom(i).getSymbol()+",result_: "+result);
			
		}
	}

}

