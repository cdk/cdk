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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.SaturationChecker;

/**
 * @cdk.module test-extra
 *
 * @author     steinbeck
 * @cdk.created    2003-02-20
 */
public class SaturationCheckerTest extends CDKTestCase
{

	SaturationChecker satcheck = null;
	boolean standAlone = false;


	/**
	 *  Constructor for the SaturationCheckerTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public SaturationCheckerTest(String name)
	{
		super(name);
	}

    /**
    *  The JUnit setup method
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
        TestSuite suite = new TestSuite(SaturationCheckerTest.class);
        return suite;
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testAllSaturated() throws CDKException
	{
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
		assertTrue(satcheck.allSaturated(m));

		// test methane with implicit hydrogen
		m = new Molecule();
		c = new Atom("C");
		c.setHydrogenCount(4);
		m.addAtom(c);
		assertTrue(satcheck.allSaturated(m));
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testIsSaturated() throws CDKException
	{
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
	 *  A unit test for JUnit
	 */
	public void testSaturate() throws CDKException {
		// test ethene
		Atom c1 = new Atom("C");
		c1.setHydrogenCount(2);
		Atom c2 = new Atom("C");
		c2.setHydrogenCount(2);
		Bond b = new Bond(c1, c2, 1);
		// force single bond, saturate() must fix that
		Molecule m = new Molecule();
		m.addAtom(c1);
		m.addAtom(c2);
		m.addBond(b);
		satcheck.saturate(m);
		assertEquals(2.0, b.getOrder(), 0.0001);
	}

	/**
	 *  A unit test for JUnit
	 */
	public void testSaturate_Butene() throws CDKException {
		// test ethene
		Atom c1 = new Atom("C");
		c1.setHydrogenCount(2);
		Atom c2 = new Atom("C");
		c2.setHydrogenCount(1);
		Atom c3 = new Atom("C");
		c3.setHydrogenCount(1);
		Atom c4 = new Atom("C");
		c4.setHydrogenCount(2);
		Bond b1 = new Bond(c1, c2, 1);
		Bond b2 = new Bond(c3, c2, 1);
		Bond b3 = new Bond(c3, c4, 1);
		// force single bond, saturate() must fix that
		Molecule m = new Molecule();
		m.addAtom(c1);
		m.addAtom(c2);
		m.addAtom(c3);
		m.addAtom(c4);
		m.addBond(b1);
		m.addBond(b2);
		m.addBond(b3);
		satcheck.saturate(m);
		assertEquals(2.0, b1.getOrder(), 0.0001);
		assertEquals(1.0, b2.getOrder(), 0.0001);
		assertEquals(2.0, b3.getOrder(), 0.0001);
	}

    public void testSaturate_ParaDiOxygenBenzene() throws CDKException {
        Molecule mol = new Molecule();
        Atom a1 = new Atom("C");
        mol.addAtom(a1);
        Atom a2 = new Atom("O");
        mol.addAtom(a2);
        Atom a3 = new Atom("C");
        mol.addAtom(a3);
        Atom a4 = new Atom("C");
        mol.addAtom(a4);
        Atom a5 = new Atom("H");
        mol.addAtom(a5);
        Atom a6 = new Atom("C");
        mol.addAtom(a6);
        Atom a7 = new Atom("H");
        mol.addAtom(a7);
        Atom a8 = new Atom("C");
        mol.addAtom(a8);
        Atom a9 = new Atom("H");
        mol.addAtom(a9);
        Atom a10 = new Atom("C");
        mol.addAtom(a10);
        Atom a11 = new Atom("H");
        mol.addAtom(a11);
        Atom a12 = new Atom("O");
        mol.addAtom(a12);
        Bond b1 = new Bond(a2, a1, 1.0);
        mol.addBond(b1);
        Bond b2 = new Bond(a1, a3, 1.0);
        mol.addBond(b2);
        Bond b3 = new Bond(a1, a4, 1.0);
        mol.addBond(b3);
        Bond b4 = new Bond(a5, a3, 1.0);
        mol.addBond(b4);
        Bond b5 = new Bond(a3, a6, 1.0);
        mol.addBond(b5);
        Bond b6 = new Bond(a7, a4, 1.0);
        mol.addBond(b6);
        Bond b7 = new Bond(a4, a8, 1.0);
        mol.addBond(b7);
        Bond b8 = new Bond(a6, a9, 1.0);
        mol.addBond(b8);
        Bond b9 = new Bond(a6, a10, 1.0);
        mol.addBond(b9);
        Bond b10 = new Bond(a8, a10, 1.0);
        mol.addBond(b10);
        Bond b11 = new Bond(a8, a11, 1.0);
        mol.addBond(b11);
        Bond b12 = new Bond(a10, a12, 1.0);
        mol.addBond(b12);
        satcheck.saturate(mol);
        assertEquals(2.0, b1.getOrder(), 0.0001);
        assertEquals(1.0, b2.getOrder(), 0.0001);
        assertEquals(1.0, b3.getOrder(), 0.0001);
        assertEquals(2.0, b5.getOrder(), 0.0001);
        assertEquals(2.0, b7.getOrder(), 0.0001);
        assertEquals(1.0, b9.getOrder(), 0.0001);
        assertEquals(1.0, b10.getOrder(), 0.0001);
        assertEquals(2.0, b12.getOrder(), 0.0001);
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
    
    public void testBug777529() throws CDKException {
      Molecule m = new Molecule();
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("C"));
      m.addAtom(new Atom("O"));
      m.addAtom(new Atom("O"));
      m.addAtom(new Atom("F"));
      m.getAtomAt(0).setHydrogenCount(1);
      m.getAtomAt(2).setHydrogenCount(1);
      m.getAtomAt(3).setHydrogenCount(1);
      m.getAtomAt(6).setHydrogenCount(1);
      m.getAtomAt(7).setHydrogenCount(1);
      m.getAtomAt(8).setHydrogenCount(1);
      m.getAtomAt(9).setHydrogenCount(1);
      //m.getAtomAt(10).setHydrogenCount(1);
      //m.getAtomAt(12).setHydrogenCount(1);
      m.getAtomAt(14).setHydrogenCount(1);
      m.getAtomAt(15).setHydrogenCount(1);
      m.getAtomAt(17).setHydrogenCount(1);
      m.getAtomAt(18).setHydrogenCount(1);
      m.getAtomAt(19).setHydrogenCount(3);
      m.addBond(0, 1, 1);
      m.addBond(1, 2, 1);
      m.addBond(2, 3, 1);
      m.addBond(3, 4, 1);
      m.addBond(4, 5, 1);
      m.addBond(5, 6, 1);
      m.addBond(6, 7, 1);
      m.addBond(7, 8, 1);
      m.addBond(8, 9, 1);
      m.addBond(5, 10, 1);
      m.addBond(9, 10, 1);
      m.addBond(10, 11, 1);
      m.addBond(0, 12, 1);
      m.addBond(4, 12, 1);
      m.addBond(11, 12, 1);
      m.addBond(11, 13, 1);
      m.addBond(13, 14, 1);
      m.addBond(14, 15, 1);
      m.addBond(15, 16, 1);
      m.addBond(16, 17, 1);
      m.addBond(13, 18, 1);
      m.addBond(17, 18, 1);
      m.addBond(20, 16, 1);
      m.addBond(11, 21, 1);
      m.addBond(22, 1, 1);
      m.addBond(20, 19, 1);
      m.getAtomAt(0).setFlag(CDKConstants.ISAROMATIC,true);
      m.getAtomAt(1).setFlag(CDKConstants.ISAROMATIC,true);
      m.getAtomAt(2).setFlag(CDKConstants.ISAROMATIC,true);
      m.getAtomAt(3).setFlag(CDKConstants.ISAROMATIC,true);
      m.getAtomAt(4).setFlag(CDKConstants.ISAROMATIC,true);
      m.getAtomAt(12).setFlag(CDKConstants.ISAROMATIC,true);
      m.getAtomAt(5).setFlag(CDKConstants.ISAROMATIC,true);
      m.getAtomAt(6).setFlag(CDKConstants.ISAROMATIC,true);
      m.getAtomAt(7).setFlag(CDKConstants.ISAROMATIC,true);
      m.getAtomAt(8).setFlag(CDKConstants.ISAROMATIC,true);
      m.getAtomAt(9).setFlag(CDKConstants.ISAROMATIC,true);
      m.getAtomAt(10).setFlag(CDKConstants.ISAROMATIC,true);
      m.getBondAt(0).setFlag(CDKConstants.ISAROMATIC,true);
      m.getBondAt(1).setFlag(CDKConstants.ISAROMATIC,true);
      m.getBondAt(2).setFlag(CDKConstants.ISAROMATIC,true);
      m.getBondAt(3).setFlag(CDKConstants.ISAROMATIC,true);
      m.getBondAt(5).setFlag(CDKConstants.ISAROMATIC,true);
      m.getBondAt(6).setFlag(CDKConstants.ISAROMATIC,true);
      m.getBondAt(7).setFlag(CDKConstants.ISAROMATIC,true);
      m.getBondAt(8).setFlag(CDKConstants.ISAROMATIC,true);
      m.getBondAt(9).setFlag(CDKConstants.ISAROMATIC,true);
      m.getBondAt(10).setFlag(CDKConstants.ISAROMATIC,true);
      m.getBondAt(12).setFlag(CDKConstants.ISAROMATIC,true);
      m.getBondAt(13).setFlag(CDKConstants.ISAROMATIC,true);
      satcheck.saturate(m);
      assertTrue(m.getBondAt(4).getOrder()==1);
      assertTrue(m.getBondAt(9).getOrder()==2 ^ m.getBondAt(5).getOrder()==2);
      assertTrue(m.getBondAt(13).getOrder()==2 ^ m.getBondAt(3).getOrder()==2);
    }
    
    /**
	 *  The main program for the SaturationCheckerTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args) throws CDKException
	{
		SaturationCheckerTest sct = new SaturationCheckerTest("SaturationCheckerTest");
		sct.standAlone = true;
		sct.setUp();
		sct.testSaturate();
		sct.testIsSaturated();
		sct.testAllSaturated();
	}
}

