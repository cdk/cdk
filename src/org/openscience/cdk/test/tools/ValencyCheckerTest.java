/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.ValencyChecker;

/**
 * @cdk.module test
 *
 * @author     Egon Willighagen
 * @cdk.created    2004-01-08
 */
public class ValencyCheckerTest extends CDKTestCase
{

	ValencyChecker satcheck = null;

	public ValencyCheckerTest(String name){
		super(name);
	}

    /**
     *  The JUnit setup method
     */
    public void setUp() {
        try {
            satcheck = new ValencyChecker();
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
        TestSuite suite = new TestSuite(ValencyCheckerTest.class);
        return suite;
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testAllSaturated() throws CDKException {
		// test methane with explicit hydrogen
		Molecule m = new Molecule();
		Atom c = new Atom("C");
		Atom h1 = new Atom("H");
		Atom h2 = new Atom("H");
		Atom h3 = new Atom("H");
		Atom h4 = new Atom("H");
		m.addAtom(c);
		m.addAtom(h1);
		m.addAtom(h2);
		m.addAtom(h3);
		m.addAtom(h4);
		m.addBond(new Bond(c, h1));
		m.addBond(new Bond(c, h2));
		m.addBond(new Bond(c, h3));
		m.addBond(new Bond(c, h4));
		assertTrue(satcheck.isSaturated(m));

		// test methane with implicit hydrogen
		m = new Molecule();
		c = new Atom("C");
		c.setHydrogenCount(4);
		m.addAtom(c);
		assertTrue(satcheck.isSaturated(m));
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testIsSaturated() throws CDKException {
		// test methane with explicit hydrogen
		Molecule m = new Molecule();
		Atom c = new Atom("C");
		Atom h1 = new Atom("H");
		Atom h2 = new Atom("H");
		Atom h3 = new Atom("H");
		Atom h4 = new Atom("H");
		m.addAtom(c);
		m.addAtom(h1);
		m.addAtom(h2);
		m.addAtom(h3);
		m.addAtom(h4);
		m.addBond(new Bond(c, h1));
		m.addBond(new Bond(c, h2));
		m.addBond(new Bond(c, h3));
		m.addBond(new Bond(c, h4));
		assertTrue(satcheck.isSaturated(c, m));
		assertTrue(satcheck.isSaturated(h1, m));
		assertTrue(satcheck.isSaturated(h2, m));
		assertTrue(satcheck.isSaturated(h3, m));
		assertTrue(satcheck.isSaturated(h4, m));
	}

    /**
     * Tests wether the saturation checker considers negative
     * charges.
     */
	public void testIsSaturated_NegativelyChargedOxygen() throws CDKException {
		// test methane with explicit hydrogen
		Molecule m = new Molecule();
		Atom c = new Atom("C");
		Atom h1 = new Atom("H");
		Atom h2 = new Atom("H");
		Atom h3 = new Atom("H");
		Atom o = new Atom("O");
        o.setFormalCharge(-1);
		m.addAtom(c);
		m.addAtom(h1);
		m.addAtom(h2);
		m.addAtom(h3);
		m.addAtom(o);
		m.addBond(new Bond(c, h1));
		m.addBond(new Bond(c, h2));
		m.addBond(new Bond(c, h3));
		m.addBond(new Bond(c, o));
		assertTrue(satcheck.isSaturated(c, m));
		assertTrue(satcheck.isSaturated(h1, m));
		assertTrue(satcheck.isSaturated(h2, m));
		assertTrue(satcheck.isSaturated(h3, m));
		assertTrue(satcheck.isSaturated(o, m));
	}
    
    /**
     * Tests wether the saturation checker gets a proton right.
     */
	public void testIsSaturated_Proton() throws CDKException {
		// test H+
		Molecule m = new Molecule();
		Atom h = new Atom("H");
        h.setFormalCharge(+1);
		m.addAtom(h);
		assertTrue(satcheck.isSaturated(h, m));
	}
    
    /**
     * Tests wether the saturation checker considers positive
     * charges.
     */
	public void testIsSaturated_PositivelyChargedNitrogen() throws CDKException {
		// test methane with explicit hydrogen
		Molecule m = new Molecule();
		Atom n = new Atom("N");
		Atom h1 = new Atom("H");
		Atom h2 = new Atom("H");
		Atom h3 = new Atom("H");
		Atom h4 = new Atom("H");
        n.setFormalCharge(+1);
		m.addAtom(n);
		m.addAtom(h1);
		m.addAtom(h2);
		m.addAtom(h3);
		m.addAtom(h4);
		m.addBond(new Bond(n, h1));
		m.addBond(new Bond(n, h2));
		m.addBond(new Bond(n, h3));
		m.addBond(new Bond(n, h4));
		assertTrue(satcheck.isSaturated(n, m));
		assertTrue(satcheck.isSaturated(h1, m));
		assertTrue(satcheck.isSaturated(h2, m));
		assertTrue(satcheck.isSaturated(h3, m));
		assertTrue(satcheck.isSaturated(h4, m));
	}

    /**
     * Test sulfuric acid.
     */
    public void testBug772316() throws CDKException {
		// test methane with explicit hydrogen
		Molecule m = new Molecule();
		Atom sulphur = new Atom("S");
		Atom o1 = new Atom("O");
		Atom o2 = new Atom("O");
		Atom o3 = new Atom("O");
		Atom o4 = new Atom("O");
		Atom h1 = new Atom("H");
		Atom h2 = new Atom("H");
		m.addAtom(sulphur);
		m.addAtom(o1);
		m.addAtom(o2);
		m.addAtom(o3);
		m.addAtom(o4);
		m.addAtom(h1);
		m.addAtom(h2);
		m.addBond(new Bond(sulphur, o1, 2));
		m.addBond(new Bond(sulphur, o2, 2));
		m.addBond(new Bond(sulphur, o3, 1));
		m.addBond(new Bond(sulphur, o4, 1));
		m.addBond(new Bond(h1, o3, 1));
		m.addBond(new Bond(h2, o4, 1));
		assertTrue(satcheck.isSaturated(sulphur, m));
		assertTrue(satcheck.isSaturated(o1, m));
		assertTrue(satcheck.isSaturated(o2, m));
		assertTrue(satcheck.isSaturated(o3, m));
		assertTrue(satcheck.isSaturated(o4, m));
		assertTrue(satcheck.isSaturated(h1, m));
		assertTrue(satcheck.isSaturated(h2, m));
    }

    public void testSaturate_Ethene() throws CDKException {
        Molecule ethene = new Molecule();
        Atom carbon1 = new Atom("C");
        carbon1.setHydrogenCount(2);
        Atom carbon2 = new Atom("C");
        carbon2.setHydrogenCount(2);
        ethene.addAtom(carbon1);
        ethene.addAtom(carbon2);
        ethene.addBond(new Bond(carbon1, carbon2, 1.0));
        
        satcheck.saturate(ethene); // fix bond orders
        assertEquals(2.0, (ethene.getBonds())[0].getOrder(), 0.0001);
    }
    
    public void testSaturate_13Butadiene() throws CDKException {
        Molecule butadiene = new Molecule();
        Atom carbon1 = new Atom("C");
        carbon1.setHydrogenCount(2);
        Atom carbon2 = new Atom("C");
        carbon2.setHydrogenCount(1);
        Atom carbon3 = new Atom("C");
        carbon3.setHydrogenCount(1);
        Atom carbon4 = new Atom("C");
        carbon4.setHydrogenCount(2);
        butadiene.addAtom(carbon1);
        butadiene.addAtom(carbon2);
        butadiene.addAtom(carbon3);
        butadiene.addAtom(carbon4);
        butadiene.addBond(new Bond(carbon2, carbon3, 1.0));
        butadiene.addBond(new Bond(carbon2, carbon1, 1.0));
        butadiene.addBond(new Bond(carbon3, carbon4, 1.0));
        
        satcheck.saturate(butadiene); // fix bond orders
        org.openscience.cdk.interfaces.IBond[] bonds = butadiene.getBonds();
        assertEquals(1.0, bonds[0].getOrder(), 0.0001);
        assertEquals(2.0, bonds[1].getOrder(), 0.0001);
        assertEquals(2.0, bonds[2].getOrder(), 0.0001);
    }
    
    public void testSaturate_Benzene() throws CDKException {
        Molecule benzene = new Molecule();
        for (int i=1; i<=6; i++) {
            Atom carbon = new Atom("C");
            carbon.setHydrogenCount(1);
            benzene.addAtom(carbon);
        }
        for (int i=0; i<5; i++) {
            benzene.addBond(i, i+1, 1.0);
        }
        benzene.addBond(5, 0, 1.0);
        
        satcheck.saturate(benzene); // fix bond orders
        
        // test for three single + three double bonds
        org.openscience.cdk.interfaces.IBond[] bonds = benzene.getBonds();
        double totalBondOrder = 0.0;
        for (int i=0; i<bonds.length; i++) {
            totalBondOrder += bonds[i].getOrder();
        }
        assertEquals(9.0, totalBondOrder, 0.0001);
        // test for each atom, total bond order is 3.0
        org.openscience.cdk.interfaces.IAtom[] atoms = benzene.getAtoms();
        for (int i=0; i<atoms.length; i++) {
        	org.openscience.cdk.interfaces.IAtom carbon = atoms[i];
            totalBondOrder = 0.0;
            bonds = benzene.getConnectedBonds(carbon);
            assertEquals(2, bonds.length); // two explicit neighbours
            for (int j=0; j<bonds.length; j++) {
                totalBondOrder += bonds[j].getOrder();
            }
            assertEquals(3.0, totalBondOrder, 0.0001);
        }
    }
    
}

