/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.tools;

import java.io.InputStream;

import javax.vecmath.Point2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.tools.SaturationChecker;

/**
 * Tests CDK's hydrogen adding capabilities in terms of
 * example molecules.
 *
 * @cdk.module test-valencycheck
 *
 * @author     egonw
 * @cdk.created    2003-06-18
 */
public class HydrogenAdderTest extends CDKTestCase {

    protected HydrogenAdder adder = null;

    public HydrogenAdderTest(String name) {
        super(name);
    }

    /**
     * The JUnit setup method
     */
    public void setUp() {
        adder = new HydrogenAdder();
    }

    /**
     * A unit test suite for JUnit
     *
     * @return    The test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(HydrogenAdderTest.class);
        return suite;
    }

    public void testProton() {
        Molecule mol = new Molecule();
        Atom proton = new Atom("H");
        proton.setFormalCharge(+1);
        mol.addAtom(proton);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(1, mol.getAtomCount());
        assertEquals(1, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(0, mol.getBondCount(proton));
        assertEquals(0, proton.getHydrogenCount());
    }
    
    public void testHydrogen() {
        Molecule mol = new Molecule();
        Atom proton = new Atom("H");
        mol.addAtom(proton);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(2, mol.getAtomCount());
        assertEquals(2, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(1, mol.getBondCount(proton));
        assertEquals(0, proton.getHydrogenCount());
    }
    
    public void testMethane() {
        Molecule mol = new Molecule();
        Atom carbon = new Atom("C");
        mol.addAtom(carbon);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(5, mol.getAtomCount());
        assertEquals(4, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(4, mol.getBondCount(carbon));
    }
    
    public void testAminomethane()
    {
        Molecule aminomethane = new Molecule();
        Atom carbon = new Atom("C");
        Point2d carbonPos = new Point2d(0.0,0.0);
        carbon.setPoint2d(carbonPos);
        Atom nitrogen = new Atom("N");
        Point2d nitrogenPos = new Point2d(1.0,1.0);
        nitrogen.setPoint2d(nitrogenPos);
        aminomethane.addAtom(carbon);
	aminomethane.addAtom(nitrogen);
        aminomethane.addBond(new Bond(carbon, nitrogen));
        
        // generate new coords
       try {
            adder.addExplicitHydrogensToSatisfyValency(aminomethane);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }	

        assertEquals(7, aminomethane.getAtomCount());
	
    }    

    public void testAmmonia() {
        Molecule mol = new Molecule();
        Atom nitrogen = new Atom("N");
        mol.addAtom(nitrogen);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(4, mol.getAtomCount());
        assertEquals(3, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(3, mol.getBondCount(nitrogen));
    }

    public void testAmmonium() {
        Molecule mol = new Molecule();
        Atom nitrogen = new Atom("N");
        nitrogen.setFormalCharge(+1);
        mol.addAtom(nitrogen);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(5, mol.getAtomCount());
        assertEquals(4, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(4, mol.getBondCount(nitrogen));
    }

    public void testWater() {
        Molecule mol = new Molecule();
        Atom oxygen = new Atom("O");
        mol.addAtom(oxygen);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(3, mol.getAtomCount());
        assertEquals(2, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(2, mol.getBondCount(oxygen));
    }

    public void testHydroxyl() {
        Molecule mol = new Molecule();
        Atom oxygen = new Atom("O");
        oxygen.setFormalCharge(-1);
        mol.addAtom(oxygen);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(2, mol.getAtomCount());
        assertEquals(1, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(1, mol.getBondCount(oxygen));
    }

    public void testHydroxonium() {
        Molecule mol = new Molecule();
        Atom oxygen = new Atom("O");
        oxygen.setFormalCharge(+1);
        mol.addAtom(oxygen);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(4, mol.getAtomCount());
        assertEquals(3, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(3, mol.getBondCount(oxygen));
    }
    
    public void testHalogens() {
        halogenTest("I");
        halogenTest("F");
        halogenTest("Cl");
        halogenTest("Br");
    }
    
    private void halogenTest(String halogen) {
        Molecule mol = new Molecule();
        Atom atom = new Atom(halogen);
        mol.addAtom(atom);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(2, mol.getAtomCount());
        assertEquals(1, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(1, mol.getBondCount(atom));
    }
    
    public void testSulphur() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("S");
        mol.addAtom(atom);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(3, mol.getAtomCount());
        assertEquals(2, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(2, mol.getBondCount(atom));
    }
    
    public void testSulfite() {
        Molecule mol = new Molecule();
        Atom s = new Atom("S");
        Atom o1 = new Atom("O");
        Atom o2 = new Atom("O");
        Atom o3 = new Atom("O");
        mol.addAtom(s);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(o3);
        Bond b1 = new Bond(s, o1, 1.0);
        Bond b2 = new Bond(s, o2, 1.0);
        Bond b3 = new Bond(s, o3, 2.0);
        mol.addBond(b1);
        mol.addBond(b2);
        mol.addBond(b3);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(6, mol.getAtomCount());
        assertEquals(5, mol.getBondCount());
        assertEquals(2, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(3, mol.getBondCount(s));
    }
    
    public void testAceticAcid() {
        Molecule mol = new Molecule();
        Atom carbonylOxygen = new Atom("O");
        Atom hydroxylOxygen = new Atom("O");
        Atom methylCarbon = new Atom("C");
        Atom carbonylCarbon = new Atom("C");
        mol.addAtom(carbonylOxygen);
        mol.addAtom(hydroxylOxygen);
        mol.addAtom(methylCarbon);
        mol.addAtom(carbonylCarbon);
        Bond b1 = new Bond(methylCarbon, carbonylCarbon, 1.0);
        Bond b2 = new Bond(carbonylOxygen, carbonylCarbon, 2.0);
        Bond b3 = new Bond(hydroxylOxygen, carbonylCarbon, 1.0);
        mol.addBond(b1);
        mol.addBond(b2);
        mol.addBond(b3);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(8, mol.getAtomCount());
        assertEquals(7, mol.getBondCount());
        assertEquals(4, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(1, mol.getBondCount(carbonylOxygen));
        assertEquals(2, mol.getBondCount(hydroxylOxygen));
        assertEquals(4, mol.getBondCount(methylCarbon));
        assertEquals(3, mol.getBondCount(carbonylCarbon));
    }
    
    public void testEthane() {
        Molecule mol = new Molecule();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, 1.0);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(8, mol.getAtomCount());
        assertEquals(7, mol.getBondCount());
        assertEquals(6, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(4, mol.getBondCount(carbon1));
        assertEquals(4, mol.getBondCount(carbon2));
    }

    public void testEthene() {
        Molecule mol = new Molecule();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, 2.0);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
           fail();
        }
        
        assertEquals(6, mol.getAtomCount());
        assertEquals(5, mol.getBondCount());
        assertEquals(4, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(3, mol.getBondCount(carbon1));
        assertEquals(3, mol.getBondCount(carbon2));
    }

    public void testEthyne() {
        Molecule mol = new Molecule();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, 3.0);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(4, mol.getAtomCount());
        assertEquals(3, mol.getBondCount());
        assertEquals(2, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(2, mol.getBondCount(carbon1));
        assertEquals(2, mol.getBondCount(carbon2));
    }

    public void testAromaticSaturation() {
        Molecule mol = new Molecule();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        
        
        mol.addBond(0, 1, 1.0); // 1
        mol.addBond(1, 2, 1.0); // 2
        mol.addBond(2, 3, 1.0); // 3
        mol.addBond(3, 4, 1.0); // 4
        mol.addBond(4, 5, 1.0); // 5
        mol.addBond(5, 0, 1.0); // 6
        mol.addBond(0, 6, 1.0); // 7
        mol.addBond(6, 7, 3.0); // 8
        
        for (int f = 0; f < 6; f++) {
            mol.getAtom(f).setFlag(CDKConstants.ISAROMATIC, true);
            mol.getAtom(f).setHybridization(CDKConstants.HYBRIDIZATION_SP2);
            mol.getBond(f).setFlag(CDKConstants.ISAROMATIC, true);
        }
        try {
            adder.addHydrogensToSatisfyValency(mol);
            new SaturationChecker().saturate(mol);
        } catch(Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();	
        }
        MFAnalyser mfa = new MFAnalyser(mol);
        assertEquals(6, mfa.getAtomCount("H"));
    }
    
    public void testAddImplicitHydrogens() {
        Molecule molecule = null;
        try {
            String filename = "data/mdl/saturationcheckertest.mol";
            InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
            MDLReader reader = new MDLReader(ins);
            molecule = (Molecule)reader.read((ChemObject)new Molecule());
            adder.addHydrogensToSatisfyValency(molecule);
        } catch (Exception exc) {
            exc.printStackTrace();
            fail();
        } 
    }
    
    /**
     * Tests wether the it actually resets an old value if zero missing hydrogens
     * were calculated (bug found 2004-01-09 by egonw).
     */
    public void testaddImplicitHydrogensToSatisfyValency_OldValue() {
        Molecule mol = new Molecule();
        mol.addAtom(new Atom("C"));
        Atom oxygen = new Atom("O");
        mol.addAtom(oxygen);
        mol.addAtom(new Atom("C"));
        
        mol.addBond(0, 1, 1.0);
        mol.addBond(1, 2, 1.0);
        
        oxygen.setHydrogenCount(2); /* e.g. caused by the fact that the element symbol
                                       was changed from C to O (=actual bug) */
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }

        assertEquals(0, oxygen.getHydrogenCount());
    }

    public void testRadical()
    {
    	Molecule mol = new Molecule();
    	mol.addAtom(new Atom("C"));
    	mol.addAtom(new Atom("C"));
    	mol.addElectronContainer(mol.getBuilder().newSingleElectron(mol.getAtom(0)));
    	mol.addBond(0,1,1);
    	try {
    		adder.addImplicitHydrogensToSatisfyValency(mol);
    	} catch (Exception e) {
    		e.printStackTrace();
    		fail();
    	}
    	assertEquals(3, mol.getAtom(1).getHydrogenCount());
    	assertEquals(2, mol.getAtom(0).getHydrogenCount());
    	
    }

}

