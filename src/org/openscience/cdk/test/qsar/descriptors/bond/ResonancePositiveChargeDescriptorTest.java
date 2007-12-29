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
package org.openscience.cdk.test.qsar.descriptors.bond;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.ResonancePositiveChargeDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LonePairElectronChecker;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */
public class ResonancePositiveChargeDescriptorTest extends CDKTestCase {
	private IBondDescriptor descriptor;
	/**
	 *  Constructor for the ResonancePositiveChargeDescriptorTest object
	 *
	 */
	public  ResonancePositiveChargeDescriptorTest() {
		descriptor = new ResonancePositiveChargeDescriptor();
		
	}
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
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=CF");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
    	
        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate( mol.getBond(0),mol).getValue());
        
		assertEquals(testResult[0],dar.get(0), 0.01);
		assertFalse(0.0 == dar.get(1));
		assertEquals(testResult[1],dar.get(1), 1.6);

		descriptor = new ResonancePositiveChargeDescriptor();
        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(1),mol).getValue());
		assertEquals(testResult[2],dar.get(0), 0.0001);
		assertEquals(testResult[3],dar.get(1), 0.0001);
		
        descriptor = new ResonancePositiveChargeDescriptor();
        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(2),mol).getValue());
		assertEquals(testResult[4],dar.get(0), 0.00001);
		assertEquals(testResult[5],dar.get(1), 0.00001);
		
        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(3),mol).getValue());
		assertEquals(testResult[6],dar.get(0), 0.00001);
		assertEquals(testResult[7],dar.get(1), 0.00001);

        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(4),mol).getValue());
		assertFalse(0.0 == dar.get(0));
		assertEquals(testResult[8],dar.get(0), 2.0);
		assertEquals(testResult[9],dar.get(1), 0.00001);

	        
	}
	/**
	 *  A unit test for JUnit with C=CCC
	 */
	public void testResonancePositiveCharge_2() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={4.6593,0.0, 0.0,0.0,3.5752,0.0,3.5752,0.0,4.6593,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C(=O)C=C");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
	        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(0),mol).getValue());
	        
			assertEquals(testResult[1],dar.get(0), 0.0001);
			assertFalse(0.0 == dar.get(1));
	        assertEquals(testResult[0],dar.get(1), 4.0);

	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(1),mol).getValue());
			assertEquals(testResult[2],dar.get(0), 0.0001);
			assertEquals(testResult[3],dar.get(1), 0.0001);

	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(2),mol).getValue());
			
	        assertFalse(0.0 == dar.get(0));
	        assertEquals(testResult[4],dar.get(0), 2.5);
			assertFalse(0.0 == dar.get(0));
			/*assertEquals(testResult[5],dar.get(1), 0.0001); <= it should be null*/
			
	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(3),mol).getValue());
	        assertFalse(0.0 == dar.get(0));
	        assertEquals(testResult[6],dar.get(0), 2.6);
			assertEquals(testResult[7],dar.get(1), 0.0001);
			
	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(4),mol).getValue());
	        assertFalse(0.0 == dar.get(0));
	        assertEquals(testResult[8],dar.get(0), 2);
			assertEquals(testResult[9],dar.get(1),  0.0001);

	        
	}
	/**
	 *  A unit test for JUnit with C(=O)O
	 */
	public void testResonancePositiveCharge_3() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={5.5662, 0.0,0.0,0.0,5.5662,0.0,3.6611,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C(=O)O");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
	        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(0),mol).getValue());
			
	        assertEquals(testResult[1],dar.get(0), 0.0001);
	        assertFalse(0.0 == dar.get(1));
	        assertEquals(testResult[0],dar.get(1), 4.6);

	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(1),mol).getValue());
			assertEquals(testResult[2],dar.get(0), 0.0001);
			assertEquals(testResult[3],dar.get(1), 0.0001);

	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(2),mol).getValue());
	        assertFalse(0.0 == dar.get(0));
	        assertEquals(testResult[4],dar.get(0), 4.3);
			assertEquals(testResult[5],dar.get(1), 0.00001);
			
	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(3),mol).getValue());

	        assertFalse(0.0 == dar.get(0));
	        assertEquals(testResult[6],dar.get(0), 1.7); 
			assertEquals(testResult[7],dar.get(1), 0.00001);

	        
	}
	/**
	 *  A unit test for JUnit with C=CC
	 */
	public void testResonancePositiveCharge_4() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0, 3.9498,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=CC");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
	        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(0),mol).getValue());
			assertEquals(testResult[0],dar.get(0), 0.001);
			assertFalse(0.0 == dar.get(1));
			assertEquals(testResult[1],dar.get(1), 1.8);

	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(1),mol).getValue());
			assertEquals(testResult[2],dar.get(0), 0.0001);
			assertEquals(testResult[3],dar.get(1), 0.0001);

	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(2),mol).getValue());
			assertEquals(testResult[4],dar.get(0), 0.00001);
			assertEquals(testResult[5],dar.get(1), 0.00001);
			
	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(3),mol).getValue());
			assertEquals(testResult[6],dar.get(0), 0.00001);
			assertEquals(testResult[7],dar.get(1), 0.00001);

	        
	}
	/**
	 *  A unit test for JUnit with CCOCCCO
	 */
	public void testResonancePositiveCharge_5() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.0, 3.9498,3.9235,3.5005,3.5212,3.75,3.5149,11.658,0.0,0.0};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CCOCCCO");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
	        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(0),mol).getValue());
	        assertEquals(testResult[0],dar.get(0), 0.001);
			assertFalse(0.0 == dar.get(1));
			assertEquals(testResult[1],dar.get(1), 2.1);

	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(1),mol).getValue());
//	        logger.debug(dar.get(0)+" ; "+dar.get(1));
			/*assertEquals(testResult[2],dar.get(0), 0.0001); The result should not be null*/
			assertFalse(0.0 == dar.get(1));
			assertEquals(testResult[3],dar.get(1), 2.0);

	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(2),mol).getValue());
//	        logger.debug(dar.get(0)+" ; "+dar.get(1) );
	        /*assertEquals(testResult[4],dar.get(0), 0.0001); The result should not be null*/
			assertFalse(0.0 == dar.get(1));
			assertEquals(testResult[5],dar.get(1), 4.3);
			
	        dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(3),mol).getValue());
			assertFalse(0.0 == dar.get(0));
//	        logger.debug(dar.get(0)+" ; "+dar.get(1) );
			assertEquals(testResult[6],dar.get(0), 2.2);
			assertFalse(0.0 == dar.get(1));
			assertEquals(testResult[7],dar.get(1), 7.4);

	        
	}
	/**
	 *  A unit test for JUnit with CCOCCCO
	 */
	public void testResonancePositiveCharge_6() throws ClassNotFoundException, CDKException, java.lang.Exception {
//		double [] testResult={0.0, 0.0, 0.0,0.0,0.0,3.5725};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
//		IMolecule mol = sp.parseSmiles("[H]C([H])=C([H])C([H])([H])C(=[O+])C([H])([H])C([H])([H])[H]");
		IMolecule mol = sp.parseSmiles("C=CCC(=[O+])CC");

		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
		for(int i = 0; i < mol.getBondCount(); i++){	
	        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(i),mol).getValue());
		}
	}
	/**
	 *  A unit test for JUnit with CCOCCCO
	 */
	public void testResonancePositiveCharge_7() throws ClassNotFoundException, CDKException, java.lang.Exception {
//		double [] testResult={0.0, 0.0, 0.0,0.0,0.0,3.5725};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("[H]C([C+]([H])[H])C([H])([H])C(=O)C([H])([H])C([H])([H])[H]");
		mol.addSingleElectron(new SingleElectron(mol.getAtom(1)));
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
		for(int i = 6; i < 8; i++){	
	        DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(i),mol).getValue());
		}
	}
	/**
	 *  A unit test for JUnit with c1ccc(cc1)n3c4ccccc4(c2ccccc23)
	 */
	public void testResonancePositiveCharge_8() throws ClassNotFoundException, CDKException, java.lang.Exception {
//		double [] testResult={0.0, 0.0, 0.0,0.0,0.0,3.5725};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("c1ccc(cc1)n3c4ccccc4(c2ccccc23)");
		
		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);

		for(int i = 0; i < mol.getBondCount(); i++){
			DoubleArrayResult dar;
			if(mol.getBond(i).getOrder() != IBond.Order.SINGLE)
				dar = ((DoubleArrayResult)descriptor.calculate(mol.getBond(i),mol).getValue());
		}
	}

}

