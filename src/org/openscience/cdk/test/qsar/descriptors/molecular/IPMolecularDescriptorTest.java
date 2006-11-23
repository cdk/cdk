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
package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.qsar.descriptors.molecular.IPMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
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
public class IPMolecularDescriptorTest extends CDKTestCase {
	IPMolecularDescriptor descriptor;
	private SmilesParser sp;
	/**
	 *  Constructor for the IPMolecularDescriptorTest object
	 *
	 */
    public  IPMolecularDescriptorTest() {
    	descriptor = new IPMolecularDescriptor();
    	sp = new SmilesParser();
    	
    }
    /**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
    public static Test suite() {
        return new TestSuite(IPMolecularDescriptorTest.class);
    }

    /**
	 *  A unit test for JUnit with C-Cl
	 */
    public void testIPDescriptor_1() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		Molecule mol = sp.parseSmiles("C-Cl");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
        double resultAccordingNIST = 11.26; 
//        System.out.println(resultAccordingNIST+"="+dar.get(0));

        assertEquals(1, dar.size());
        assertEquals(resultAccordingNIST, dar.get(0), 0.02);
    }
    /**
	 *  A unit test for JUnit with COCCCC=O
	 */
    public void testIPDescriptor_2() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		Molecule mol = sp.parseSmiles("COCCCC=O");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());
        
        double resultAccordingNIST = 9.37; 
        assertEquals(2, dar.size());
        assertEquals(resultAccordingNIST, dar.get(0), 0.3);
    }
    /**
	 *  A unit test for JUnit with C=CCC(=O)CC
	 */
    public void testIPDescriptor_3() throws ClassNotFoundException, CDKException, java.lang.Exception{
        
		Molecule mol = sp.parseSmiles("C=CCC(=O)CC");
		
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		DoubleArrayResult dar = ((DoubleArrayResult)descriptor.calculate(mol).getValue());

//		System.out.println(dar.get(0)+", "+dar.get(1));
        double resultAccordingNIST = 9.37; 
        assertEquals(2, dar.size());
        assertEquals(resultAccordingNIST, dar.get(0), 1.4);
    }
    /**
     * A unit test for JUnit with C-Cl
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPDescriptorReaction() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	Molecule mol = sp.parseSmiles("C-Cl");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		IReactionSet reactionSet = ((IPMolecularDescriptor)descriptor).getReactionSet(mol);
		double resultAccordingNIST = 11.26; 
//        System.out.println(resultAccordingNIST+"="+reactionSet.getReaction(0).getProperty("IonizationEnergy"));
        double result = ((Double) reactionSet.getReaction(0).getProperty("IonizationEnergy")).doubleValue();
        assertEquals(1, reactionSet.getReactionCount());
        assertEquals(resultAccordingNIST, result, 2.1);
    }
    /**
     * A unit test for JUnit with CCCC
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     */
    public void testIPDescriptorReaction2() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	Molecule mol = sp.parseSmiles("CCCC");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		IReactionSet reactionSet = ((IPMolecularDescriptor)descriptor).getReactionSet(mol);
        assertEquals(0, reactionSet.getReactionCount());
    }

}
