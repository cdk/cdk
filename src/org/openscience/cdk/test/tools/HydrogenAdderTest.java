/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.tools;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.templates.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.aromaticity.*;
import org.openscience.cdk.smiles.*;

import java.io.*;
import javax.vecmath.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;
import java.awt.*;

import junit.framework.*;

/**
 * Tests CDK's hydrogen adding capabilities in terms of
 * example molecules.
 *
 * @author     egonw
 * @created    2003-06-18
 */
public class HydrogenAdderTest extends TestCase {

    SaturationChecker satcheck = null;

    public HydrogenAdderTest(String name) {
        super(name);
    }

    /**
     * The JUnit setup method
     */
    public void setUp() {
        try {
            satcheck = new SaturationChecker();
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * A unit test suite for JUnit
     *
     * @return    The test suite
     */
    public static Test suite() {
        return new TestSuite(HydrogenAdderTest.class);
    }

    public void testMethane() {
        Molecule mol = new Molecule();
        Atom carbon = new Atom("C");
        mol.addAtom(carbon);
        
        try {
            satcheck.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            fail();
        }
        
        assertEquals(5, mol.getAtomCount());
        assertEquals(4, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(4, mol.getBondCount(carbon));
    }

    public void testAmmonia() {
        Molecule mol = new Molecule();
        Atom nitrogen = new Atom("N");
        mol.addAtom(nitrogen);
        
        try {
            satcheck.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
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
            satcheck.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
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
            satcheck.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
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
            satcheck.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
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
            satcheck.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
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
            satcheck.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
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
            satcheck.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            fail();
        }
        
        assertEquals(3, mol.getAtomCount());
        assertEquals(2, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(2, mol.getBondCount(atom));
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
            satcheck.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
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
            satcheck.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
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
            satcheck.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
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
            satcheck.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            fail();
        }
        
        assertEquals(4, mol.getAtomCount());
        assertEquals(3, mol.getBondCount());
        assertEquals(2, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(2, mol.getBondCount(carbon1));
        assertEquals(2, mol.getBondCount(carbon2));
    }
}

