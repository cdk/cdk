/* $Revision: 10995 $ $Author: miguelrojasch $ $Date: 2008-05-14 16:38:21 +0200 (Wed, 14 May 2008) $
 * 
 * Copyright (C) 2008  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarionpot
 */
public class IPMolecularLearningDescriptorTest extends MolecularDescriptorTest {
	
	private SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
	private IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
    private LonePairElectronChecker lpcheck = new LonePairElectronChecker();
	/**
	 *  Constructor for the IPMolecularLearningDescriptorTest object
	 *
	 */
    public  IPMolecularLearningDescriptorTest() {
    }
    

    @Before
    public void setUp() throws Exception {
    	super.setDescriptor(IPMolecularLearningDescriptor.class);
    }


    @Test
    public void testIPMolecularLearningDescriptor() throws Exception {
    	Assert.assertNotNull(descriptor);
    }

	/**
	 *  A unit test for JUnit with CC(C)C(C)C
	 *  
	 *  @cdk.inchi InChI=1/C6H14/c1-5(2)6(3)4/h5-6H,1-4H3
	 */
    @Test public void testIPDescriptor0() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
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
		
		IPMolecularLearningDescriptor descriptor = new IPMolecularLearningDescriptor();
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 0.0; 
        
        Assert.assertEquals(resultAccordingNIST, result, 0.0001);
    }

    /**
	 *  A unit test for JUnit with C-Cl
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
	 */
    @Test public void testIPDescriptor_1() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C-Cl");

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
		IPMolecularLearningDescriptor descriptor = new IPMolecularLearningDescriptor();
		double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 11.26; 

        Assert.assertEquals(resultAccordingNIST, result, 0.53);
    }
    /**
	 *  A unit test for JUnit with COCCCC=O
	 */
    @Test public void testIPDescriptor_2() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("COCCCC=O");

		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);

		IPMolecularLearningDescriptor descriptor = new IPMolecularLearningDescriptor();
		DoubleArrayResult dar = ((DoubleArrayResult)(descriptor).calculatePlus(mol).getValue());
        
        double resultAccordingNIST = 9.37; 
        
        Assert.assertEquals(2, dar.length());
        Assert.assertEquals(resultAccordingNIST, dar.get(0), 0.3);
    }
    /**
	 *  A unit test for JUnit with C=CCC(=O)CC
	 */
    @Test public void testCalculatePlus_IAtomContainer() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		IMolecule mol = sp.parseSmiles("C=CCCC(=O)C");
		
		addExplicitHydrogens(mol);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		lpcheck.saturate(mol);
		
		IPMolecularLearningDescriptor descriptor = new IPMolecularLearningDescriptor();
		DoubleArrayResult dar = ((DoubleArrayResult)((IPMolecularLearningDescriptor)descriptor).calculatePlus(mol).getValue());

        double resultAccordingNIST = 9.50; 
        Assert.assertEquals(2, dar.length());
        Assert.assertEquals(resultAccordingNIST, dar.get(0), 0.6);
        
    }
//    /**
//     * A unit test for JUnit with C-Cl
//     * 
//     * @throws ClassNotFoundException
//     * @throws CDKException
//     * @throws java.lang.Exception
//     */
//    @Test public void testIPDescriptorReaction() throws ClassNotFoundException, CDKException, java.lang.Exception{
//    	IMolecule mol = sp.parseSmiles("C-Cl");
//
//		addExplicitHydrogens(mol);
//		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
//		lpcheck.saturate(mol);
//		
//		descriptor.calculate(mol);
//		
//		IReactionSet reactionSet = ((IPMolecularLearningDescriptor)descriptor).getReactionSet();
//		double resultAccordingNIST = 11.26; 
//
//		double result = ((Double) reactionSet.getReaction(0).getProperty("IonizationEnergy")).doubleValue();
//        Assert.assertEquals(1, reactionSet.getReactionCount());
//        Assert.assertEquals(resultAccordingNIST, result, 0.53);
//    }
//    /**
//     * A unit test for JUnit with CCCC
//     * 
//     * @throws ClassNotFoundException
//     * @throws CDKException
//     * @throws java.lang.Exception
//     */
//    @Test public void testIPDescriptorReaction2() throws ClassNotFoundException, CDKException, java.lang.Exception{
//    	IMolecule mol = sp.parseSmiles("CCCC");
//
//		addExplicitHydrogens(mol);
//		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
//		lpcheck.saturate(mol);
//		
//		descriptor.calculate(mol);
//		
//		IReactionSet reactionSet = ((IPMolecularLearningDescriptor)descriptor).getReactionSet();
//		
//        Assert.assertEquals(0, reactionSet.getReactionCount());
//    }
//    /**
//     * A unit test for JUnit with CCC#CCCO
//     * 
//     * @throws ClassNotFoundException
//     * @throws CDKException
//     * @throws java.lang.Exception
//     */
//    @Test public void testIPDescriptorReaction3() throws ClassNotFoundException, CDKException, java.lang.Exception{
//    	IMolecule mol = sp.parseSmiles("CCC#CCCO");
//
//		addExplicitHydrogens(mol);
//		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
//		lpcheck.saturate(mol);
//		
//		descriptor.calculate(mol);
//		
//		IReactionSet reactionSet = ((IPMolecularLearningDescriptor)descriptor).getReactionSet();
//		
//        Assert.assertEquals(3, reactionSet.getReactionCount());
//    }
//    /**
//     * A unit test for JUnit with CCC#CCCO
//     * 
//     * @throws ClassNotFoundException
//     * @throws CDKException
//     * @throws java.lang.Exception
//     */
//    @Test public void testIPDescriptorReaction4() throws ClassNotFoundException, CDKException, java.lang.Exception{
//    	IMolecule mol = sp.parseSmiles("CCCCC=CO");
//
//		addExplicitHydrogens(mol);
//		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
//		lpcheck.saturate(mol);
//		
//		descriptor.calculate(mol);
//		
//		IReactionSet reactionSet = ((IPMolecularLearningDescriptor)descriptor).getReactionSet();
//		
//        Assert.assertEquals(3, reactionSet.getReactionCount());
//    }
}
