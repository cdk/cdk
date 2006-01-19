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
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.IValencyChecker;
import org.openscience.cdk.tools.ValencyHybridChecker;

/**
 * @cdk.module test
 *
 * @author      Egon Willighagen
 * @cdk.created 2004-06-12
 */
public class ValencyHybridCheckerTest extends CDKTestCase
{

	IValencyChecker satcheck = null;

	public ValencyHybridCheckerTest(String name){
		super(name);
	}

    /**
     *  The JUnit setup method
     */
    public void setUp() {
        try {
            satcheck = new ValencyHybridChecker();
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
        TestSuite suite = new TestSuite(ValencyHybridCheckerTest.class);
        return suite;
	}

	public void testCalculateMissingHydrogen_Ethene() throws CDKException {
		// test ethane with explicit hydrogen
		Molecule m = new Molecule();
		Atom c1 = new Atom("C");
		Atom c2 = new Atom("C");
        c1.setHybridization(CDKConstants.HYBRIDIZATION_SP2);
        c2.setHybridization(CDKConstants.HYBRIDIZATION_SP2);
		m.addAtom(c1);
		m.addAtom(c2);
		m.addBond(new Bond(c1, c2));
		assertEquals(2, satcheck.calculateNumberOfImplicitHydrogens(c1, m));
		assertEquals(2, satcheck.calculateNumberOfImplicitHydrogens(c2, m));
	}

	public void testCalculateMissingHydrogen_Ethane() throws CDKException {
		// test ethane with explicit hydrogen
		Molecule m = new Molecule();
		Atom c1 = new Atom("C");
		Atom c2 = new Atom("C");
        c1.setHybridization(CDKConstants.HYBRIDIZATION_SP3);
        c2.setHybridization(CDKConstants.HYBRIDIZATION_SP3);
		m.addAtom(c1);
		m.addAtom(c2);
		m.addBond(new Bond(c1, c2));
		assertEquals(3, satcheck.calculateNumberOfImplicitHydrogens(c1, m));
		assertEquals(3, satcheck.calculateNumberOfImplicitHydrogens(c2, m));
	}

}

