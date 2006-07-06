/* $RCSfile$
 * $Author$
 * $Date$
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
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.descriptors.atomic.AtomHybridizationVSEPRDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LonePairElectronChecker;


/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class AtomHybridizationVSEPRDescriptorTest extends CDKTestCase {

    public  AtomHybridizationVSEPRDescriptorTest() {}

    public static Test suite() {
        return new TestSuite(AtomHybridizationVSEPRDescriptorTest.class);
    }

    /**
	 *  A unit test for JUnit with O-C
	 */
    public void testAtomHybridizationVSEPRDescriptorTest_1() throws ClassNotFoundException, CDKException, java.lang.Exception
    {
    	AtomHybridizationVSEPRDescriptor descriptor  = new AtomHybridizationVSEPRDescriptor();

        //O=CC
        Molecule molecule = new Molecule();
        Atom O1 = new Atom("O");
        Atom c2 = new Atom("C");
        c2.setHydrogenCount(1);
        Atom c3 = new Atom("C");
        c3.setHydrogenCount(3);
        molecule.addAtom(O1);
        molecule.addAtom(c2);
        molecule.addAtom(c3);
        Bond b1 = new Bond(c2, O1, 2);
        Bond b2 = new Bond(c2, c3, 1);
        molecule.addBond(b1);
        molecule.addBond(b2);

        assertEquals(CDKConstants.HYBRIDIZATION_SP2, ((IntegerResult)descriptor.calculate(molecule.getAtomAt(0),molecule).getValue()).intValue());

        assertEquals(CDKConstants.HYBRIDIZATION_SP2, ((IntegerResult)descriptor.calculate(molecule.getAtomAt(1),molecule).getValue()).intValue());

        assertEquals(CDKConstants.HYBRIDIZATION_SP3, ((IntegerResult)descriptor.calculate(molecule.getAtomAt(2),molecule).getValue()).intValue());
    }

    /**
	 *  A unit test for JUnit with [O+]=C-C
	 */
    public void testAtomHybridizationVSEPRDescriptorTest_2() throws ClassNotFoundException, CDKException, java.lang.Exception
    {
    	AtomHybridizationVSEPRDescriptor descriptor  = new AtomHybridizationVSEPRDescriptor();

        //[O+]#CC
        Molecule molecule = new Molecule();
        Atom O1 = new Atom("O");
        O1.setFormalCharge(1);
        Atom c2 = new Atom("C");
        Atom c3 = new Atom("C");
        c3.setHydrogenCount(3);
        molecule.addAtom(O1);
        molecule.addAtom(c2);
        molecule.addAtom(c3);
        Bond b1 = new Bond(c2, O1, 3);
        Bond b2 = new Bond(c2, c3, 1);
        molecule.addBond(b1);
        molecule.addBond(b2);

        assertEquals(CDKConstants.HYBRIDIZATION_SP1, ((IntegerResult)descriptor.calculate(molecule.getAtomAt(0),molecule).getValue()).intValue());

        assertEquals(CDKConstants.HYBRIDIZATION_SP1, ((IntegerResult)descriptor.calculate(molecule.getAtomAt(1),molecule).getValue()).intValue());

        assertEquals(CDKConstants.HYBRIDIZATION_SP3, ((IntegerResult)descriptor.calculate(molecule.getAtomAt(2),molecule).getValue()).intValue());
    }

    /**
	 *  A unit test for JUnit
	 */
    public void testAtomHybridizationVSEPRDescriptorTest_3() throws ClassNotFoundException, CDKException, java.lang.Exception
    {
    	AtomHybridizationVSEPRDescriptor descriptor  = new AtomHybridizationVSEPRDescriptor();

        //[C+]CC
        Molecule molecule = new Molecule();
        Atom c1 = new Atom("C");
        c1.setFormalCharge(1);
        c1.setHydrogenCount(2);
        Atom c2 = new Atom("C");
        c2.setHydrogenCount(2);
        Atom c3 = new Atom("C");
        c3.setHydrogenCount(3);
        molecule.addAtom(c1);
        molecule.addAtom(c2);
        molecule.addAtom(c3);
        Bond b1 = new Bond(c1, c2, 1);
        Bond b2 = new Bond(c2, c3, 1);
        molecule.addBond(b1);
        molecule.addBond(b2);

        assertEquals(CDKConstants.HYBRIDIZATION_SP2, ((IntegerResult)descriptor.calculate(molecule.getAtomAt(0),molecule).getValue()).intValue());

        assertEquals(CDKConstants.HYBRIDIZATION_SP3, ((IntegerResult)descriptor.calculate(molecule.getAtomAt(1),molecule).getValue()).intValue());

        assertEquals(CDKConstants.HYBRIDIZATION_SP3, ((IntegerResult)descriptor.calculate(molecule.getAtomAt(2),molecule).getValue()).intValue());
    }

    /**
	 *  A unit test for JUnit
	 */
    public void testAtomHybridizationVSEPRDescriptorTest_4() throws ClassNotFoundException, CDKException, java.lang.Exception
    {
    	AtomHybridizationVSEPRDescriptor descriptor  = new AtomHybridizationVSEPRDescriptor();

        //SO3
        Molecule molecule = new Molecule();
        Atom S1 = new Atom("S");
        Atom O2 = new Atom("O");
        Atom O3 = new Atom("O");
        Atom O4 = new Atom("O");
        molecule.addAtom(S1);
        molecule.addAtom(O2);
        molecule.addAtom(O3);
        molecule.addAtom(O4);
        Bond b1 = new Bond(S1, O2, 2);
        Bond b2 = new Bond(S1, O3, 2);
        Bond b3 = new Bond(S1, O4, 2);
        molecule.addBond(b1);
        molecule.addBond(b2);
        molecule.addBond(b3);

        assertEquals(CDKConstants.HYBRIDIZATION_SP2, ((IntegerResult)descriptor.calculate(molecule.getAtomAt(0),molecule).getValue()).intValue());

    }

    /**
	 *  A unit test for JUnit
	 */
    public void testAtomHybridizationVSEPRDescriptorTest_5() throws ClassNotFoundException, CDKException, java.lang.Exception
    {
    	AtomHybridizationVSEPRDescriptor descriptor  = new AtomHybridizationVSEPRDescriptor();

        //XeF4
        Molecule molecule = new Molecule();
        Atom Xe1 = new Atom("Xe");
        Atom F2 = new Atom("F");
        Atom F3 = new Atom("F");
        Atom F4 = new Atom("F");
        Atom F5 = new Atom("F");
        molecule.addAtom(Xe1);
        molecule.addAtom(F2);
        molecule.addAtom(F3);
        molecule.addAtom(F4);
        molecule.addAtom(F5);
        Bond b1 = new Bond(Xe1, F2, 1);
        Bond b2 = new Bond(Xe1, F3, 1);
        Bond b3 = new Bond(Xe1, F4, 1);
        Bond b4 = new Bond(Xe1, F5, 1);
        molecule.addBond(b1);
        molecule.addBond(b2);
        molecule.addBond(b3);
        molecule.addBond(b4);

        assertEquals(CDKConstants.HYBRIDIZATION_SP3D2, ((IntegerResult)descriptor.calculate(molecule.getAtomAt(0),molecule).getValue()).intValue());

    }

    /**
	 *  A unit test for JUnit with F-[I-]-F
	 */
    public void testAtomHybridizationVSEPRDescriptorTest_6() throws ClassNotFoundException, CDKException, java.lang.Exception
    {
    	AtomHybridizationVSEPRDescriptor descriptor  = new AtomHybridizationVSEPRDescriptor();

        //IF2-
        Molecule molecule = new Molecule();
        Atom I1 = new Atom("I");
        I1.setFormalCharge(-1);
        Atom F2 = new Atom("F");
        Atom F3 = new Atom("F");
        molecule.addAtom(I1);
        molecule.addAtom(F2);
        molecule.addAtom(F3);
        Bond b1 = new Bond(I1, F2, 1);
        Bond b2 = new Bond(I1, F3, 1);
        molecule.addBond(b1);
        molecule.addBond(b2);

        assertEquals(CDKConstants.HYBRIDIZATION_SP3D1, ((IntegerResult)descriptor.calculate(molecule.getAtomAt(0),molecule).getValue()).intValue());

    }
    /**
	 *  A unit test for JUnit with F-C=C
	 */
    public void testAtomHybridizationVSEPRDescriptorTest_7() throws ClassNotFoundException, CDKException, java.lang.Exception
    {
    	int[] testResult = {CDKConstants.HYBRIDIZATION_SP3,CDKConstants.HYBRIDIZATION_SP2,CDKConstants.HYBRIDIZATION_SP2};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
    	AtomHybridizationVSEPRDescriptor descriptor  = new AtomHybridizationVSEPRDescriptor();
        
        SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("F-C=C");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for(int i = 0 ; i < 3; i++){
	        assertEquals(testResult[i], ((IntegerResult)descriptor.calculate(mol.getAtomAt(i),mol).getValue()).intValue());
		}
    }
    /**
	 *  A unit test for JUnit with [F+]=C-[C-]
	 */
    public void testAtomHybridizationVSEPRDescriptorTest_8() throws ClassNotFoundException, CDKException, java.lang.Exception
    {
    	int[] testResult = {CDKConstants.HYBRIDIZATION_SP2,CDKConstants.HYBRIDIZATION_SP2,CDKConstants.HYBRIDIZATION_SP3};
    	
    	AtomHybridizationVSEPRDescriptor descriptor  = new AtomHybridizationVSEPRDescriptor();
        
        SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("[F+]=C-[C-]");

		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addImplicitHydrogensToSatisfyValency(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(mol);
		
		for(int i = 0 ; i < 3; i++){
	        assertEquals(testResult[i], ((IntegerResult)descriptor.calculate(mol.getAtomAt(i),mol).getValue()).intValue());

		}
    }
}
