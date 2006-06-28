/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-12 10:58:22 +0200 (Fr, 12 Mai 2006) $
 * $Revision: 1.0
 $
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.IPAtomicDescriptor;
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

public class IPAtomicDescriptorTest extends CDKTestCase {
	/**
	 *  Constructor for the IPAtomicDescriptorTest object
	 *
	 */
    public  IPAtomicDescriptorTest() {}
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
    	IMolecularDescriptor descriptor = new IPAtomicDescriptor();
		Object[] params = new Object[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C-Cl");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		params[0] = new Integer(1);
		params[1] = new String(IPAtomicDescriptor.AtomicTarget);
        descriptor.setParameters(params);
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 11.26; 
        assertEquals(result, resultAccordingNIST, 0.05);
    }
    /**
	 *  A unit test for JUnit with C-C-Br
	 */
    public void testIPDescriptor_2() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	IMolecularDescriptor descriptor = new IPAtomicDescriptor();
    	Object[] params = new Object[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C-C-Br");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		params[0] = new Integer(2);
		params[1] = IPAtomicDescriptor.AtomicTarget;
        descriptor.setParameters(params);
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 11.29; 
        assertEquals(result, resultAccordingNIST, 1.2);
    }
    /**
	 *  A unit test for JUnit with C-C-C-I
	 */
    public void testIPDescriptor_3() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	IMolecularDescriptor descriptor = new IPAtomicDescriptor();
    	Object[] params = new Object[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C-C-C-I");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		params[0] = new Integer(3);
		params[1] = IPAtomicDescriptor.AtomicTarget;
        descriptor.setParameters(params);
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.27;
        assertEquals(result, resultAccordingNIST, 0.03);
    }
    /**
	 *  A unit test for JUnit with C-C-O
	 */
    public void testIPDescriptor_4() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	IMolecularDescriptor descriptor = new IPAtomicDescriptor();
    	Object[] params = new Object[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C-C-O");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		params[0] = new Integer(2);
		params[1] = IPAtomicDescriptor.AtomicTarget;
        descriptor.setParameters(params);
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 10.48;
        assertEquals(result, resultAccordingNIST, 0.05);
    }/**
	 *  A unit test for JUnit with C-O-C
	 */
    public void testIPDescriptor_5() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	IMolecularDescriptor descriptor = new IPAtomicDescriptor();
    	Object[] params = new Object[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C-O-C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		params[0] = new Integer(1);
		params[1] = IPAtomicDescriptor.AtomicTarget;
        descriptor.setParameters(params);
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 10.025;
        assertEquals(result, resultAccordingNIST, 0.03);
    }
    /**
	 *  A unit test for JUnit with C-N-C
	 */
    public void testIPDescriptor_6() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	IMolecularDescriptor descriptor = new IPAtomicDescriptor();
    	Object[] params = new Object[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C-N-C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		params[0] = new Integer(1);
		params[1] = IPAtomicDescriptor.AtomicTarget;
        descriptor.setParameters(params);
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.24; 
        assertEquals(result, resultAccordingNIST, 3.1);
    }
    /**
	 *  A unit test for JUnit with C-C-N
	 */
    public void testIPDescriptor_7() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	IMolecularDescriptor descriptor = new IPAtomicDescriptor();
    	Object[] params = new Object[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C-C-N");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		params[0] = new Integer(2);
		params[1] = IPAtomicDescriptor.AtomicTarget;
        descriptor.setParameters(params);
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.9; 
        assertEquals(result, resultAccordingNIST, 0.5);
    }
    /**
	 *  A unit test for JUnit with C-C-P-C-C
	 */
    public void testIPDescriptor_8() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	IMolecularDescriptor descriptor = new IPAtomicDescriptor();
    	Object[] params = new Object[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("C-C-P-C-C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		params[0] = new Integer(2);
		params[1] = IPAtomicDescriptor.AtomicTarget;
        descriptor.setParameters(params);
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.5; 
        assertEquals(result, resultAccordingNIST, 0.4);
    }

    /**
	 *  A unit test for JUnit with CCCCC(=O)CC
	 */
    public void testIPDescriptor_9() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	IMolecularDescriptor descriptor = new IPAtomicDescriptor();
    	Object[] params = new Object[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CCCCC(=O)CC");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		params[0] = new Integer(4);
		params[1] = IPAtomicDescriptor.BondTarget;
        descriptor.setParameters(params);
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.15; 
        assertEquals(result, resultAccordingNIST, 1);
    }
    /**
	 *  A unit test for JUnit with O=C1CCCC1
	 */
    public void testIPDescriptor_10() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	IMolecularDescriptor descriptor = new IPAtomicDescriptor();
    	Object[] params = new Object[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("O=C1CCCC1");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		params[0] = new Integer(0);
		params[1] = IPAtomicDescriptor.BondTarget;
        descriptor.setParameters(params);
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 9.26; 
        assertEquals(result, resultAccordingNIST, 1.2);
    }
    /**
	 *  A unit test for JUnit with CCCC=CCCCC
	 */
    public void testIPDescriptor_11() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	IMolecularDescriptor descriptor = new IPAtomicDescriptor();
    	Object[] params = new Object[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CCCC=CCCCC");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		params[0] = new Integer(3);
		params[1] = IPAtomicDescriptor.BondTarget;
        descriptor.setParameters(params);
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.80; 
        assertEquals(result, resultAccordingNIST, 0.05);
    }
    /**
	 *  A unit test for JUnit with CC1CCC=C1
	 */
    public void testIPDescriptor_12() throws ClassNotFoundException, CDKException, java.lang.Exception{
    	IMolecularDescriptor descriptor = new IPAtomicDescriptor();
    	Object[] params = new Object[2];
        
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CC1CCC=C1");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		params[0] = new Integer(4);
		params[1] = IPAtomicDescriptor.BondTarget;
        descriptor.setParameters(params);
        double result= ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
        double resultAccordingNIST = 8.95; 
        assertEquals(result, resultAccordingNIST, 0.11);
    }


}
