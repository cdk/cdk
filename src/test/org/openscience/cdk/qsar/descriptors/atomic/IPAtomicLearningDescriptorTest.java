/* $Revision: 10995 $ $Author: miguelrojasch $ $Date: 2008-05-14 16:38:21 +0200 (Wed, 14 May 2008) $
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

import org.junit.Assert;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarionpot
 */
public class IPAtomicLearningDescriptorTest extends AtomicDescriptorTest {
	IPAtomicLearningDescriptor descriptor;
	private SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
	private IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
    private LonePairElectronChecker lpcheck = new LonePairElectronChecker();
	/**
	 *  Constructor for the IPAtomicLearningDescriptorTest object
	 *
	 */
    public  IPAtomicLearningDescriptorTest() {
    	descriptor = new IPAtomicLearningDescriptor();
    }
    
    public void setUp() throws Exception {
    	setDescriptor(IPAtomicLearningDescriptor.class);
    }
    
    public static Test suite() {
        return new TestSuite(IPAtomicLearningDescriptorTest.class);
    }

    /**
	 *  A unit test for JUnit
	 */
	public void testIPAtomicLearningDescriptor() throws Exception {
		IAtomicDescriptor descriptor = new IPAtomicLearningDescriptor();
		Assert.assertNotNull(descriptor);
	}
	/**
	 *  A unit test for JUnit with CC(C)C(C)C
	 *  
	 *  @cdk.inchi InChI=1/C6H14/c1-5(2)6(3)4/h5-6H,1-4H3
	 */
    public void testIPDescriptor0() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
    	IMolecule mol = builder.newMolecule();
		mol.addAtom(builder.newAtom("C"));
		mol.addAtom(builder.newAtom("C"));
		mol.addBond(0, 1, IBond.Order.SINGLE);
		mol.addAtom(builder.newAtom("C"));
		mol.addBond(1, 2, IBond.Order.SINGLE);
		mol.addAtom(builder.newAtom("C"));
		mol.addBond(1, 3, IBond.Order.SINGLE);
		mol.addAtom(builder.newAtom("C"));
		mol.addBond(3, 4, IBond.Order.SINGLE);
		mol.addAtom(builder.newAtom("C"));
		mol.addBond(3, 5, IBond.Order.SINGLE);

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(3),mol).getValue()).doubleValue();
        double resultAccordingNIST = 0.0; 
        
        Assert.assertEquals(resultAccordingNIST, result, 0.0001);
    }

	/**
	 *  A unit test for JUnit with CCCCl
	 *  
	 *  @cdk.inchi InChI=1/C3H7Cl/c1-2-3-4/h2-3H2,1H3
	 */
    public void testIPDescriptor1() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
    	IMolecule mol = builder.newMolecule();
		mol.addAtom(builder.newAtom("C"));
		mol.addAtom(builder.newAtom("C"));
		mol.addBond(0, 1, IBond.Order.SINGLE);
		mol.addAtom(builder.newAtom("C"));
		mol.addBond(1, 2, IBond.Order.SINGLE);
		mol.addAtom(builder.newAtom("Cl"));
		mol.addBond(2, 3, IBond.Order.SINGLE);

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(3),mol).getValue()).doubleValue();
        double resultAccordingNIST = 10.8; 
        Assert.assertEquals(resultAccordingNIST, result, 0.4);
    }
    /**
	 *  A unit test for JUnit with CC(C)Cl
	 *  
	 *  @cdk.inchi InChI=1/C3H7Cl/c1-3(2)4/h3H,1-2H3
	 */
    public void testIPDescriptor2() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("CC(CC)Cl"); // not in db
		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(4),mol).getValue()).doubleValue();
        double resultAccordingNIST = 10.57; //value for CC(C)Cl 
        
        Assert.assertEquals(resultAccordingNIST, result, 0.35);
    }

    /**
	 *  A unit test for JUnit with C=CCCl
	 *  
	 *  @cdk.inchi InChI=1/C3H5Cl/c1-2-3-4/h2H,1,3H2
	 */
    public void testNotDB() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C=CCCl"); // not in db
		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(3),mol).getValue()).doubleValue();
        double resultAccordingNIST = 10.05; //value for CCCCl aprox. 
        
        Assert.assertEquals(resultAccordingNIST, result, 0.5);
    }
    /**
	 *  A unit test for JUnit with C-Cl
	 *  
	 *  @cdk.inchi InChI=1/CH3F/c1-2/h1H3
	 */
    public void testIPDescriptor_1() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C-Cl");

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(1),mol).getValue()).doubleValue();
        double resultAccordingNIST = 11.26; 
        
        Assert.assertEquals(resultAccordingNIST, result, 0.3);
    }
    /**
	 *  A unit test for JUnit with C-C-Br
	 *  
	 */
    public void testIPDescriptor_2() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C-C-Br");

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(2),mol).getValue()).doubleValue();
        double resultAccordingNIST = 10.29; 

        Assert.assertEquals(resultAccordingNIST, result, 0.8);
    }
    /**
	 *  A unit test for JUnit with C-C-C-I
	 *  
	 */
    public void testIPDescriptor_3() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C-C-C-I");
		addExplicitHydrogens(mol);
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(3), mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.27;

        Assert.assertEquals(resultAccordingNIST, result, 0.4);
    }
    /**
	 *  A unit test for JUnit with C-C-O
	 *  
	 *  @cdk.inchi InChI=1/C2H6O/c1-2-3/h3H,2H2,1H3
	 */
    public void testIPDescriptor_4() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C-C-O");

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(2), mol).getValue()).doubleValue();
        double resultAccordingNIST = 10.48;

        Assert.assertEquals(resultAccordingNIST, result, 0.5);
    }
    
    /**
	 *  A unit test for JUnit with N1(C)CCC(C)(C)CC1
	 *  
	 */
    public void testIPDescriptor_5() throws ClassNotFoundException, CDKException, java.lang.Exception{

    	IMolecule mol = sp.parseSmiles("N1(C)CCC(C)(C)CC1");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 7.77;

        Assert.assertEquals(resultAccordingNIST, result, 0.3);
    }
    /**
	 *  A unit test for JUnit with C-N-C
	 *  
	 *  @cdk.inchi InChI=1/C2H7N/c1-3-2/h3H,1-2H3
	 */
    public void testIPDescriptor_6() throws ClassNotFoundException, CDKException, java.lang.Exception{

    	IMolecule mol = sp.parseSmiles("C-N-C");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(1),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.24; 

        Assert.assertEquals(resultAccordingNIST, result, 0.5);
    }
    /**
	 *  A unit test for JUnit with C-C-N
	 *  
	 *  @cdk.inchi InChI=1/C2H7N/c1-2-3/h2-3H2,1H3
	 */
    public void testIPDescriptor_7() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C-C-N");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(2),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.9; 

        Assert.assertEquals(resultAccordingNIST, result, 0.35);
    }
    /**
	 *  A unit test for JUnit with C-C-P-C-C
	 *  
	 *  @cdk.inchi InChI=1/C4H11P/c1-3-5-4-2/h5H,3-4H2,1-2H3
	 */
    public void testIPDescriptor_8() throws ClassNotFoundException, CDKException, java.lang.Exception{

    	IMolecule mol = sp.parseSmiles("C-C-P-C-C");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(2), mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.5; 

        Assert.assertEquals(resultAccordingNIST, result, 0.38);
    }

    /**
	 *  A unit test for JUnit with O=C(C)CC(C)C
	 *  
	 *  @cdk.inchi InChI=1/C6H12O/c1-5(2)4-6(3)7/h5H,4H2,1-3H3
	 */
    public void testIPDescriptor_9() throws ClassNotFoundException, CDKException, java.lang.Exception{

    	IMolecule mol = sp.parseSmiles("O=C(C)CC(C)C");

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(0), mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.3; 

        Assert.assertEquals(resultAccordingNIST, result, 0.4);
    }
    /**
	 *  A unit test for JUnit with O=C1C2CCC1CC2
	 *  
	 *  @cdk.inchi InChI=1/C7H10O/c8-7-5-1-2-6(7)4-3-5/h5-6H,1-4H2
	 */
    public void testIPDescriptor_10() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("O=C1C2CCC1CC2");

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(0),mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.01; 

        Assert.assertEquals(resultAccordingNIST, result, 0.3);
    }

    /**
	 *  A unit test for JUnit with CCOCCCO
	 *  
	 *  @cdk.inchi InChI=1/C5H12O2/c1-2-7-5-3-4-6/h6H,2-5H2,1H3
	 */
    public void testIPDescriptor_14() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("CCOCCCO");

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(2), mol).getValue()).doubleValue();
        Assert.assertNotNull(result);
        
        result= ((DoubleResult)descriptor.calculate(mol.getAtom(7), mol).getValue()).doubleValue();
        Assert.assertNotNull(result);
        
    }
//    /**
//     * A unit test for JUnit with C-C-N
//     * 
//	 *  @cdk.inchi  InChI=1/C2H7N/c1-2-3/h2-3H2,1H3
//     * 
//     * @throws ClassNotFoundException
//     * @throws CDKException
//     * @throws java.lang.Exception
//     */
//    public void testIPDescriptorReaction() throws ClassNotFoundException, CDKException, java.lang.Exception{
//        
//    	IMolecule mol = sp.parseSmiles("C-C-N");
//		assertEquals(3, mol.getAtomCount());
//		addExplicitHydrogens(mol);
//		assertEquals(10, mol.getAtomCount());
//		
//		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
//		lpcheck.saturate(mol);
//		
//		assertEquals("N", mol.getAtom(2).getSymbol());
//		descriptor.calculate(mol.getAtom(2), mol);
//		IReactionSet reactionSet = descriptor.getReactionSet();
//		
//		assertNotNull("No reaction was found", reactionSet.getReaction(0));
//		assertNotNull("The ionization energy was not set for the reaction", reactionSet.getReaction(0).getProperty("IonizationEnergy"));
//        double result = ((Double) reactionSet.getReaction(0).getProperty("IonizationEnergy")).doubleValue();
//        double resultAccordingNIST = 8.9; 
//
//        assertEquals(1, reactionSet.getReactionCount());
//        assertEquals(resultAccordingNIST, result, 0.5);
//    }
//    /**
//     * A unit test for JUnit with CCCCCC
//     * 
//	 *  @cdk.inchi InChI=1/C6H14/c1-3-5-6-4-2/h3-6H2,1-2H3
//	 *  
//     * @throws ClassNotFoundException
//     * @throws CDKException
//     * @throws java.lang.Exception
//     */
//    public void testIPDescriptorReaction2() throws ClassNotFoundException, CDKException, java.lang.Exception{
//        
//		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
//		IMolecule mol = sp.parseSmiles("CCCCCC");
//
//		addExplicitHydrogens(mol);
//		
//		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
//		lpcheck.saturate(mol);
//		
//		descriptor.calculate(mol.getAtom(0), mol);
//		IReactionSet reactionSet = descriptor.getReactionSet();
//		
//        assertEquals(0, reactionSet.getReactionCount());
//    }

    /**
     * A unit test for JUnit with O(C=CC=C)C
     * 
	 *  @cdk.inchi InChI=1/C5H8O/c1-3-4-5-6-2/h3-5H,1H2,2H3
	 *  
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPPySystemWithHeteroatomDescriptor3() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("O(C=CC=C)C");

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getAtom(0),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.03; 
        Assert.assertEquals(resultAccordingNIST, result, 0.8);
        
//        IReactionSet reactionSet = descriptor.getReactionSet();
//		assertEquals(5, reactionSet.getReactionCount());
        
    }
    /**
     * A unit test for JUnit with OC=CC
     * 
	 *  @cdk.inchi InChI=1/C3H6O/c1-2-3-4/h2-4H,1H3
	 *  
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPPySystemWithHeteroatomDescriptor2() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("OC=CC");

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getAtom(0),mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.64; 
        Assert.assertEquals(resultAccordingNIST, result, 0.21);
        
//        IReactionSet reactionSet = descriptor.getReactionSet();
//		assertEquals(3, reactionSet.getReactionCount());
        
    }
    /**
     * A unit test for JUnit with C1=C(C)CCS1
     * 
	 *  @cdk.inchi  InChI=1/C5H8S/c1-5-2-3-6-4-5/h4H,2-3H2,1H3
	 *  
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPPySystemWithHeteroatomDescriptor1() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C1=C(C)CCS1");

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
		double result= ((DoubleResult)descriptor.calculate(mol.getAtom(5),mol).getValue()).doubleValue();
        double resultAccordingNIST = 7.77; 
        Assert.assertEquals(resultAccordingNIST, result, 0.7);
        
//        IReactionSet reactionSet = descriptor.getReactionSet();
//		assertEquals(3, reactionSet.getReactionCount());
        
    }
////    
////    /**
////     * A unit test for JUnit with OC(C#CC)(C)C
////     * 
////	 *  @cdk.inchi InChI=1/C6H10O/c1-4-5-6(2,3)7/h7H,1-3H3
////	 *  
////     * @throws ClassNotFoundException
////     * @throws CDKException
////     * @throws java.lang.Exception
////     */
////    public void testIDescriptor5() throws ClassNotFoundException, CDKException, java.lang.Exception{
////        
////		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
////		IMolecule mol = sp.parseSmiles("OC(C#CC)(C)C");
////
////		addExplicitHydrogens(mol);
////		
////		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
////		lpcheck.saturate(mol);
////		
////		descriptor.calculate(mol.getAtom(0),mol);
////        
////        IReactionSet reactionSet = descriptor.getReactionSet();
////		assertEquals(1, reactionSet.getReactionCount());
////        
////    }
////    
	/**
	 * A unit test suite for JUnit: Resonance Fluorobenzene  Fc1ccccc1 <=> ...
	 *
	 * @cdk.inchi InChI=1/C6H5F/c7-6-4-2-1-3-5-6/h1-5H
	 *
	 * @return    The test suite
	 */
	public void testFluorobenzene() throws Exception {

		 IMolecule molecule = builder.newMolecule();
		 molecule.addAtom(builder.newAtom("F"));
		 molecule.addAtom(builder.newAtom("C"));
		 molecule.addBond(0, 1, IBond.Order.SINGLE);
		 molecule.addAtom(builder.newAtom("C"));
		 molecule.addBond(1, 2, IBond.Order.DOUBLE);
		 molecule.addAtom(builder.newAtom("C"));
		 molecule.addBond(2, 3, IBond.Order.SINGLE);
		 molecule.addAtom(builder.newAtom("C"));
		 molecule.addBond(3, 4, IBond.Order.DOUBLE);
		 molecule.addAtom(builder.newAtom("C"));
		 molecule.addBond(4, 5, IBond.Order.SINGLE);
		 molecule.addAtom(builder.newAtom("C"));
		 molecule.addBond(5, 6, IBond.Order.DOUBLE);
		 molecule.addBond(6, 1, IBond.Order.SINGLE);
		
		addExplicitHydrogens(molecule);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);
		
		double result= ((DoubleResult)descriptor.calculate(molecule.getAtom(0),molecule).getValue()).doubleValue();
        double resultAccordingNIST = 9.20; 
        Assert.assertEquals(resultAccordingNIST, result, 0.2);
	}

}
