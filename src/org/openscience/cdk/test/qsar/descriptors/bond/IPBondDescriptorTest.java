/* $Revision: 1.0 $ $Author: miguelrojasch $ $Date: 2006-05-12 10:58:22 +0200 (Fr, 12 Mai 2006) $
 * 
 * Copyright (C) 2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.qsar.descriptors.bond.IPBondDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LonePairElectronChecker;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-weka
 */
public class IPBondDescriptorTest extends CDKTestCase {
	private IPBondDescriptor descriptor;
	/**
	 *  Constructor for the IPBondDescriptorTest object
	 *
	 */
    public  IPBondDescriptorTest() {
    	descriptor = new IPBondDescriptor();
    }
    /**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
    public static Test suite() {
        return new TestSuite(IPBondDescriptorTest.class);
    }

    /**
	 *  A unit test for JUnit with CCCC=CCCCC
	 */
    public void testIPDescriptor_1() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CCCC=CCCCC");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getBond(3),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.80; 
        assertEquals(result, resultAccordingNIST, 0.05);
    }
    /**
	 *  A unit test for JUnit with CC1CCC=C1
	 */
    public void testIPDescriptor_2() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CC1CCC=C1");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getBond(4),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.95; 
        assertEquals(result, resultAccordingNIST, 0.7);
    }
    /**
	 *  A unit test for JUnit with C=CCCCC
	 */
    public void testIPDescriptor_3() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=CCCCC");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getBond(0),mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.44; 
        assertEquals(result, resultAccordingNIST, 0.01);
    }
    /**
     * A unit test for JUnit with C=CCCCC
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPDescriptorReaction1() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=CCCCC");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		IReactionSet reactionSet = descriptor.getReactionSet(mol.getBond(0),mol);
        double resultAccordingNIST = 9.44; 
//        System.out.println(resultAccordingNIST+"="+reactionSet.getReaction(0).getProperty("IonizationEnergy"));
        double result = ((Double) reactionSet.getReaction(0).getProperty("IonizationEnergy")).doubleValue();

        assertEquals(2, reactionSet.getReactionCount());
        assertEquals(resultAccordingNIST, result, 2.1);
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

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		IReactionSet reactionSet = descriptor.getReactionSet(mol.getBond(0),mol);
        assertEquals(0, reactionSet.getReactionCount());
    }
    /**
     * A unit test for JUnit with C#CCC
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPTripleDescriptor1() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C#CCC");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getBond(0),mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.44; 
        assertEquals(resultAccordingNIST, result, 0.75);
    }
    /**
     * A unit test for JUnit with (#CC(C)(C)C)C(C)(C)C
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPTripleDescriptor2() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C(#CC(C)(C)C)C(C)(C)C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getBond(0),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.98; 
        assertEquals(resultAccordingNIST, result, 0.1);
    }
    /**
     * A unit test for JUnit with C=C(C=CC)C
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPConjugatedDescriptor1() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=C(C=CC)C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getBond(0),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.47; 
        assertEquals(resultAccordingNIST, result, 0.03);
        
        result= ((DoubleResult)descriptor.calculate(mol.getBond(2),mol).getValue()).doubleValue();
        resultAccordingNIST = 8.47; 
        assertEquals(resultAccordingNIST, result, 0.03);
    }
    /**
     * A unit test for JUnit with C#COC
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPPySystemWithHeteroatomDescriptor1() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C#COC");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getBond(0),mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.48; 
        assertEquals(resultAccordingNIST, result, 0.3);
        
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

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getBond(1),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.64; 
        assertEquals(resultAccordingNIST, result, 0.03);
        
    }
    

}
