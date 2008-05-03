/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.qsar.descriptors.atomic;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.qsar.descriptors.atomic.IPAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.LonePairElectronChecker;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */
public class IPAtomicDescriptorTest extends CDKTestCase {
	IPAtomicDescriptor descriptor;
	private SmilesParser sp;
	/**
	 *  Constructor for the IPAtomicDescriptorTest object
	 *
	 */
    public  IPAtomicDescriptorTest() {
    	descriptor = new IPAtomicDescriptor();
    	sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    	
    }
    /**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
    public static Test suite() {
        return new TestSuite(IPAtomicDescriptorTest.class);
    }

    /**
	 *  A unit test for JUnit with C-Cl
	 */
    public void testIPDescriptor_1() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C-Cl");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(1),mol).getValue()).doubleValue();
        double resultAccordingNIST = 11.26; 
        
        assertEquals(resultAccordingNIST, result, 0.42);
    }
    /**
	 *  A unit test for JUnit with C-C-Br
	 */
    public void testIPDescriptor_2() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C-C-Br");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(2),mol).getValue()).doubleValue();
        double resultAccordingNIST = 11.29; 

        assertEquals(resultAccordingNIST, result, 1.95);
    }
    /**
	 *  A unit test for JUnit with C-C-C-I
	 */
    public void testIPDescriptor_3() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C-C-C-I");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(3), mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.27;

        assertEquals(resultAccordingNIST, result, 0.02);
    }
    /**
	 *  A unit test for JUnit with C-C-O
	 */
    public void testIPDescriptor_4() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C-C-O");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(2), mol).getValue()).doubleValue();
        double resultAccordingNIST = 10.48;

        assertEquals(resultAccordingNIST, result, 1.24);
    }/**
	 *  A unit test for JUnit with N1(C)CCC(C)(C)CC1
	 */
    public void testIPDescriptor_5() throws ClassNotFoundException, CDKException, java.lang.Exception{

    	IMolecule mol = sp.parseSmiles("N1(C)CCC(C)(C)CC1");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 7.77;

        assertEquals(resultAccordingNIST, result, 0.02);
    }
    /**
	 *  A unit test for JUnit with C-N-C
	 */
    public void testIPDescriptor_6() throws ClassNotFoundException, CDKException, java.lang.Exception{

    	IMolecule mol = sp.parseSmiles("C-N-C");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(1),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.24; 

        assertEquals(resultAccordingNIST, result, 0.09);
    }
    /**
	 *  A unit test for JUnit with C-C-N
	 */
    public void testIPDescriptor_7() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C-C-N");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(2),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.9; 

        assertEquals(resultAccordingNIST, result, 0.35);
    }
    /**
	 *  A unit test for JUnit with C-C-P-C-C
	 */
    public void testIPDescriptor_8() throws ClassNotFoundException, CDKException, java.lang.Exception{

    	IMolecule mol = sp.parseSmiles("C-C-P-C-C");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(2), mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.5; 

        assertEquals(resultAccordingNIST, result, 0.051);
    }

    /**
	 *  A unit test for JUnit with O=C(C)CC(C)C
	 */
    public void testIPDescriptor_9() throws ClassNotFoundException, CDKException, java.lang.Exception{

    	IMolecule mol = sp.parseSmiles("O=C(C)CC(C)C");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.3; 

        assertEquals(resultAccordingNIST, result, 0.051);
    }
    /**
	 *  A unit test for JUnit with O=C1C2CCC1CC2
	 */
    public void testIPDescriptor_10() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("O=C1C2CCC1CC2");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(0),mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.01; 

        assertEquals(resultAccordingNIST, result, 0.06);
    }

    /**
	 *  A unit test for JUnit with CCOCCCO
	 */
    public void testIPDescriptor_14() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("CCOCCCO");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(2), mol).getValue()).doubleValue();
//        assertNotNull(result);
        
        result= ((DoubleResult)descriptor.calculate(mol.getAtom(7), mol).getValue()).doubleValue();
//        assertNotNull(result);
        
    }
    /**
     * A unit test for JUnit with C-C-N
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPDescriptorReaction() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
    	IMolecule mol = sp.parseSmiles("C-C-N");
		assertEquals(3, mol.getAtomCount());
		addExplicitHydrogens(mol);
		assertEquals(10, mol.getAtomCount());
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		assertEquals("Unexpected number of lone pairs", 1, mol.getLonePairCount());
		
		assertEquals("N", mol.getAtom(2).getSymbol());
		descriptor.calculate(mol.getAtom(2), mol);
		IReactionSet reactionSet = descriptor.getReactionSet();
		
		assertNotNull("No reaction was found", reactionSet.getReaction(0));
		assertNotNull("The ionization energy was not set for the reaction", reactionSet.getReaction(0).getProperty("IonizationEnergy"));
        double result = ((Double) reactionSet.getReaction(0).getProperty("IonizationEnergy")).doubleValue();
        double resultAccordingNIST = 8.9; 

        assertEquals(1, reactionSet.getReactionCount());
        assertEquals(resultAccordingNIST, result, 0.5);
    }
    /**
     * A unit test for JUnit with CCCCCC
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPDescriptorReaction2() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CCCCCC");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
		descriptor.calculate(mol.getAtom(0), mol);
		IReactionSet reactionSet = descriptor.getReactionSet();
		
        assertEquals(0, reactionSet.getReactionCount());
    }

    /**
     * A unit test for JUnit with O(C=CC=C)C
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPPySystemWithHeteroatomDescriptor3() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("O(C=CC=C)C");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getAtom(0),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.03; 
        assertEquals(resultAccordingNIST, result, 0.11);
        
        IReactionSet reactionSet = descriptor.getReactionSet();
		assertEquals(5, reactionSet.getReactionCount());
        
    }
    /**
     * A unit test for JUnit with OC=CC
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPPySystemWithHeteroatomDescriptor2() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("OC=CC");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getAtom(0),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.64; 
        assertEquals(resultAccordingNIST, result, 0.21);
        
        IReactionSet reactionSet = descriptor.getReactionSet();
		assertEquals(3, reactionSet.getReactionCount());
        
    }
    /**
     * A unit test for JUnit with C1=C(C)CCS1
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPPySystemWithHeteroatomDescriptor1() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C1=C(C)CCS1");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getAtom(5),mol).getValue()).doubleValue();
        double resultAccordingNIST = 7.77; 
        assertEquals(resultAccordingNIST, result, 0.3);
        
        IReactionSet reactionSet = descriptor.getReactionSet();
		assertEquals(3, reactionSet.getReactionCount());
        
    }
    
    /**
     * A unit test for JUnit with OC(C#CC)(C)C
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIDescriptor5() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("OC(C#CC)(C)C");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
		descriptor.calculate(mol.getAtom(0),mol);
        
        IReactionSet reactionSet = descriptor.getReactionSet();
		assertEquals(1, reactionSet.getReactionCount());
        
    }
    
}
