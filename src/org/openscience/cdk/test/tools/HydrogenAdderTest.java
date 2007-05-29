/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
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
    public void setUp() throws Exception {
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

    public void testProton() throws Exception {
        Molecule mol = new Molecule();
        Atom proton = new Atom("H");
        proton.setFormalCharge(+1);
        mol.addAtom(proton);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(1, mol.getAtomCount());
        assertEquals(1, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(0, mol.getConnectedBondsCount(proton));
        assertEquals(0, proton.getHydrogenCount());
    }
    
    public void testHydrogen() throws Exception {
        Molecule mol = new Molecule();
        Atom proton = new Atom("H");
        mol.addAtom(proton);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(2, mol.getAtomCount());
        assertEquals(2, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(1, mol.getConnectedBondsCount(proton));
        assertEquals(0, proton.getHydrogenCount());
    }
    
    public void testMethane() throws Exception {
        Molecule mol = new Molecule();
        Atom carbon = new Atom("C");
        mol.addAtom(carbon);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(5, mol.getAtomCount());
        assertEquals(4, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(4, mol.getConnectedBondsCount(carbon));
    }
    
    public void testAminomethane() throws Exception 
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
        
        adder.addExplicitHydrogensToSatisfyValency(aminomethane);

        assertEquals(7, aminomethane.getAtomCount());
	
    }    

    public void testAmmonia() throws Exception {
        Molecule mol = new Molecule();
        Atom nitrogen = new Atom("N");
        mol.addAtom(nitrogen);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(4, mol.getAtomCount());
        assertEquals(3, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(3, mol.getConnectedBondsCount(nitrogen));
    }

    public void testAmmonium() throws Exception {
        Molecule mol = new Molecule();
        Atom nitrogen = new Atom("N");
        nitrogen.setFormalCharge(+1);
        mol.addAtom(nitrogen);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(5, mol.getAtomCount());
        assertEquals(4, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(4, mol.getConnectedBondsCount(nitrogen));
    }

    public void testWater() throws Exception {
        Molecule mol = new Molecule();
        Atom oxygen = new Atom("O");
        mol.addAtom(oxygen);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(3, mol.getAtomCount());
        assertEquals(2, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(2, mol.getConnectedBondsCount(oxygen));
    }

    public void testHydroxyl() throws Exception {
        Molecule mol = new Molecule();
        Atom oxygen = new Atom("O");
        oxygen.setFormalCharge(-1);
        mol.addAtom(oxygen);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(2, mol.getAtomCount());
        assertEquals(1, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(1, mol.getConnectedBondsCount(oxygen));
    }

    public void testHydroxonium() throws Exception {
        Molecule mol = new Molecule();
        Atom oxygen = new Atom("O");
        oxygen.setFormalCharge(+1);
        mol.addAtom(oxygen);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(4, mol.getAtomCount());
        assertEquals(3, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(3, mol.getConnectedBondsCount(oxygen));
    }
    
    public void testHalogens() throws Exception {
        halogenTest("I");
        halogenTest("F");
        halogenTest("Cl");
        halogenTest("Br");
    }
    
    private void halogenTest(String halogen) throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom(halogen);
        mol.addAtom(atom);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(2, mol.getAtomCount());
        assertEquals(1, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(1, mol.getConnectedBondsCount(atom));
    }
    
    public void testSulphur() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("S");
        mol.addAtom(atom);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(3, mol.getAtomCount());
        assertEquals(2, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(2, mol.getConnectedBondsCount(atom));
    }
    
    public void testSulfite() throws Exception {
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
 
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(6, mol.getAtomCount());
        assertEquals(5, mol.getBondCount());
        assertEquals(2, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(3, mol.getConnectedBondsCount(s));
    }
    
    public void testAceticAcid() throws Exception {
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
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(8, mol.getAtomCount());
        assertEquals(7, mol.getBondCount());
        assertEquals(4, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(1, mol.getConnectedBondsCount(carbonylOxygen));
        assertEquals(2, mol.getConnectedBondsCount(hydroxylOxygen));
        assertEquals(4, mol.getConnectedBondsCount(methylCarbon));
        assertEquals(3, mol.getConnectedBondsCount(carbonylCarbon));
    }
    
    public void testEthane() throws Exception {
        Molecule mol = new Molecule();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, 1.0);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(8, mol.getAtomCount());
        assertEquals(7, mol.getBondCount());
        assertEquals(6, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(4, mol.getConnectedBondsCount(carbon1));
        assertEquals(4, mol.getConnectedBondsCount(carbon2));
    }

    public void testEthene() throws Exception {
        Molecule mol = new Molecule();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, 2.0);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(6, mol.getAtomCount());
        assertEquals(5, mol.getBondCount());
        assertEquals(4, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(3, mol.getConnectedBondsCount(carbon1));
        assertEquals(3, mol.getConnectedBondsCount(carbon2));
    }

    public void testEthyne() throws Exception {
        Molecule mol = new Molecule();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, 3.0);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(4, mol.getAtomCount());
        assertEquals(3, mol.getBondCount());
        assertEquals(2, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(2, mol.getConnectedBondsCount(carbon1));
        assertEquals(2, mol.getConnectedBondsCount(carbon2));
    }

    public void testAromaticSaturation() throws Exception {
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
        adder.addHydrogensToSatisfyValency(mol);
        new SaturationChecker().saturate(mol);
        MFAnalyser mfa = new MFAnalyser(mol);
        assertEquals(6, mfa.getAtomCount("H"));
    }
    
    public void testAddImplicitHydrogens() throws Exception {
        Molecule molecule = null;
        String filename = "data/mdl/saturationcheckertest.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
        molecule = (Molecule)reader.read((ChemObject)new Molecule());
        adder.addHydrogensToSatisfyValency(molecule);
    }
    
    /**
     * Tests wether the it actually resets an old value if zero missing hydrogens
     * were calculated (bug found 2004-01-09 by egonw).
     */
    public void testaddImplicitHydrogensToSatisfyValency_OldValue() throws Exception {
        Molecule mol = new Molecule();
        mol.addAtom(new Atom("C"));
        Atom oxygen = new Atom("O");
        mol.addAtom(oxygen);
        mol.addAtom(new Atom("C"));
        
        mol.addBond(0, 1, 1.0);
        mol.addBond(1, 2, 1.0);
        
        oxygen.setHydrogenCount(2); /* e.g. caused by the fact that the element symbol
                                       was changed from C to O (=actual bug) */
        adder.addExplicitHydrogensToSatisfyValency(mol);
        
        assertEquals(0, oxygen.getHydrogenCount());
    }

    public void testRadical() throws Exception 
    {
    	Molecule mol = new Molecule();
    	mol.addAtom(new Atom("C"));
    	mol.addAtom(new Atom("C"));
    	mol.addSingleElectron(mol.getBuilder().newSingleElectron(mol.getAtom(0)));
    	mol.addBond(0,1,1);
    	adder.addImplicitHydrogensToSatisfyValency(mol);
    	assertEquals(3, mol.getAtom(1).getHydrogenCount());
    	assertEquals(2, mol.getAtom(0).getHydrogenCount());
    	
    }
    /**
     * @cdk.bug 1575269
     *
     */
    public void testAdenine() throws Exception 
    {
    	IMolecule mol = new Molecule(); // Adenine
    	IAtom a1 = mol.getBuilder().newAtom("C");
    	a1.setPoint2d(new Point2d(21.0223, -17.2946));  mol.addAtom(a1);
    	IAtom a2 = mol.getBuilder().newAtom("C");
    	a2.setPoint2d(new Point2d(21.0223, -18.8093));  mol.addAtom(a2);
    	IAtom a3 = mol.getBuilder().newAtom("C");
    	a3.setPoint2d(new Point2d(22.1861, -16.6103));  mol.addAtom(a3);
    	IAtom a4 = mol.getBuilder().newAtom("N");
    	a4.setPoint2d(new Point2d(19.8294, -16.8677));  mol.addAtom(a4);
    	IAtom a5 = mol.getBuilder().newAtom("N");
    	a5.setPoint2d(new Point2d(22.2212, -19.5285));  mol.addAtom(a5);
    	IAtom a6 = mol.getBuilder().newAtom("N");
    	a6.setPoint2d(new Point2d(19.8177, -19.2187));  mol.addAtom(a6);
    	IAtom a7 = mol.getBuilder().newAtom("N");
    	a7.setPoint2d(new Point2d(23.4669, -17.3531));  mol.addAtom(a7);
    	IAtom a8 = mol.getBuilder().newAtom("N");
    	a8.setPoint2d(new Point2d(22.1861, -15.2769));  mol.addAtom(a8);
    	IAtom a9 = mol.getBuilder().newAtom("C");
    	a9.setPoint2d(new Point2d(18.9871, -18.0139));  mol.addAtom(a9);
    	IAtom a10 = mol.getBuilder().newAtom("C");
    	a10.setPoint2d(new Point2d(23.4609, -18.8267));  mol.addAtom(a10);
    	IBond b1 = mol.getBuilder().newBond(a1, a2, 2.0);
    	mol.addBond(b1);
    	IBond b2 = mol.getBuilder().newBond(a1, a3, 1.0);
    	mol.addBond(b2);
    	IBond b3 = mol.getBuilder().newBond(a1, a4, 1.0);
    	mol.addBond(b3);
    	IBond b4 = mol.getBuilder().newBond(a2, a5, 1.0);
    	mol.addBond(b4);
    	IBond b5 = mol.getBuilder().newBond(a2, a6, 1.0);
    	mol.addBond(b5);
    	IBond b6 = mol.getBuilder().newBond(a3, a7, 2.0);
    	mol.addBond(b6);
    	IBond b7 = mol.getBuilder().newBond(a3, a8, 1.0);
    	mol.addBond(b7);
    	IBond b8 = mol.getBuilder().newBond(a4, a9, 2.0);
    	mol.addBond(b8);
    	IBond b9 = mol.getBuilder().newBond(a5, a10, 2.0);
    	mol.addBond(b9);
    	IBond b10 = mol.getBuilder().newBond(a6, a9, 1.0);
    	mol.addBond(b10);
    	IBond b11 = mol.getBuilder().newBond(a7, a10, 1.0);
    	mol.addBond(b11);
    	HydrogenAdder ha = new HydrogenAdder();
    	HueckelAromaticityDetector.detectAromaticity(mol);
    	ha.addExplicitHydrogensToSatisfyValency(mol);
    	MFAnalyser mfa1 = new MFAnalyser(mol);
    	assertEquals(5, mfa1.getAtomCount("H"));
    	
    }
    
    /**
     * @cdk.bug 1727373
     *
     */
    public void testBug1727373() throws Exception {
        Molecule molecule = null;
        String filename = "data/mdl/carbocations.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
        molecule = (Molecule)reader.read((ChemObject)new Molecule());
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        assertEquals(2,molecule.getAtom(0).getHydrogenCount());
        assertEquals(0,molecule.getAtom(1).getHydrogenCount());
        assertEquals(1,molecule.getAtom(2).getHydrogenCount());
        assertEquals(2,molecule.getAtom(3).getHydrogenCount());
    }
    
}

