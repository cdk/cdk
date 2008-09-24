/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision: 1.0
 $
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
package org.openscience.cdk.qsar.descriptors.atomic;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LonePairElectronChecker;


/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 */
public class AtomHybridizationVSEPRDescriptorTest extends AtomicDescriptorTest {

    public  AtomHybridizationVSEPRDescriptorTest() {}

    public static Test suite() {
        return new TestSuite(AtomHybridizationVSEPRDescriptorTest.class);
    }

    public void setUp() throws Exception {
    	setDescriptor(AtomHybridizationVSEPRDescriptor.class);
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
        Bond b1 = new Bond(c2, O1, IBond.Order.DOUBLE);
        Bond b2 = new Bond(c2, c3, IBond.Order.SINGLE);
        molecule.addBond(b1);
        molecule.addBond(b2);

        assertEquals(IAtomType.Hybridization.SP2.ordinal(), ((IntegerResult)descriptor.calculate(molecule.getAtom(0),molecule).getValue()).intValue());

        assertEquals(IAtomType.Hybridization.SP2.ordinal(), ((IntegerResult)descriptor.calculate(molecule.getAtom(1),molecule).getValue()).intValue());

        assertEquals(IAtomType.Hybridization.SP3.ordinal(), ((IntegerResult)descriptor.calculate(molecule.getAtom(2),molecule).getValue()).intValue());
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
        Bond b1 = new Bond(c2, O1, IBond.Order.TRIPLE);
        Bond b2 = new Bond(c2, c3, IBond.Order.SINGLE);
        molecule.addBond(b1);
        molecule.addBond(b2);

        assertEquals(IAtomType.Hybridization.SP1.ordinal(), ((IntegerResult)descriptor.calculate(molecule.getAtom(0),molecule).getValue()).intValue());

        assertEquals(IAtomType.Hybridization.SP1.ordinal(), ((IntegerResult)descriptor.calculate(molecule.getAtom(1),molecule).getValue()).intValue());

        assertEquals(IAtomType.Hybridization.SP3.ordinal(), ((IntegerResult)descriptor.calculate(molecule.getAtom(2),molecule).getValue()).intValue());
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
        Bond b1 = new Bond(c1, c2, IBond.Order.SINGLE);
        Bond b2 = new Bond(c2, c3, IBond.Order.SINGLE);
        molecule.addBond(b1);
        molecule.addBond(b2);

        assertEquals(IAtomType.Hybridization.PLANAR3.ordinal(), ((IntegerResult)descriptor.calculate(molecule.getAtom(0),molecule).getValue()).intValue());

        assertEquals(IAtomType.Hybridization.SP3.ordinal(), ((IntegerResult)descriptor.calculate(molecule.getAtom(1),molecule).getValue()).intValue());

        assertEquals(IAtomType.Hybridization.SP3.ordinal(), ((IntegerResult)descriptor.calculate(molecule.getAtom(2),molecule).getValue()).intValue());
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
        Bond b1 = new Bond(S1, O2, IBond.Order.DOUBLE);
        Bond b2 = new Bond(S1, O3, IBond.Order.DOUBLE);
        Bond b3 = new Bond(S1, O4, IBond.Order.DOUBLE);
        molecule.addBond(b1);
        molecule.addBond(b2);
        molecule.addBond(b3);

        assertEquals(IAtomType.Hybridization.SP2.ordinal(), ((IntegerResult)descriptor.calculate(molecule.getAtom(0),molecule).getValue()).intValue());

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
        Bond b1 = new Bond(Xe1, F2, IBond.Order.SINGLE);
        Bond b2 = new Bond(Xe1, F3, IBond.Order.SINGLE);
        Bond b3 = new Bond(Xe1, F4, IBond.Order.SINGLE);
        Bond b4 = new Bond(Xe1, F5, IBond.Order.SINGLE);
        molecule.addBond(b1);
        molecule.addBond(b2);
        molecule.addBond(b3);
        molecule.addBond(b4);

        assertEquals(IAtomType.Hybridization.SP3D2.ordinal(), ((IntegerResult)descriptor.calculate(molecule.getAtom(0),molecule).getValue()).intValue());

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
        Bond b1 = new Bond(I1, F2, IBond.Order.SINGLE);
        Bond b2 = new Bond(I1, F3, IBond.Order.SINGLE);
        molecule.addBond(b1);
        molecule.addBond(b2);

        assertEquals(IAtomType.Hybridization.SP3D1.ordinal(), ((IntegerResult)descriptor.calculate(molecule.getAtom(0),molecule).getValue()).intValue());

    }
    /**
	 *  A unit test for JUnit with F-C=C
	 */
    public void testAtomHybridizationVSEPRDescriptorTest_7() throws ClassNotFoundException, CDKException, java.lang.Exception
    {
        int[] testResult = {
            IAtomType.Hybridization.SP3.ordinal(),
            IAtomType.Hybridization.SP2.ordinal(),
            IAtomType.Hybridization.SP2.ordinal()
        }; /* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
        
    	AtomHybridizationVSEPRDescriptor descriptor  = new AtomHybridizationVSEPRDescriptor();
        
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("F-C=C");

		addExplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
		for(int i = 0 ; i < 3; i++){
	        assertEquals(testResult[i], ((IntegerResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).intValue());
		}
    }
    /**
	 *  A unit test for JUnit with [F+]=C-[C-]
	 */
    public void testAtomHybridizationVSEPRDescriptorTest_8() throws ClassNotFoundException, CDKException, java.lang.Exception
    {
        int[] testResult = {
            IAtomType.Hybridization.SP2.ordinal(),
            IAtomType.Hybridization.SP2.ordinal(),
            IAtomType.Hybridization.SP3.ordinal()
        };
    	
    	AtomHybridizationVSEPRDescriptor descriptor  = new AtomHybridizationVSEPRDescriptor();
        
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("[F+]=C-[C-]");

		addImplicitHydrogens(mol);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(mol);
		
		for(int i = 0 ; i < 3; i++){
	        assertEquals(testResult[i], ((IntegerResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).intValue());

		}
    }
}
