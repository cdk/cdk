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
import org.openscience.cdk.Molecule;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.tools.ValencyHybridChecker;

/**
 * Tests CDK's hydrogen adding capabilities in terms of
 * example molecules.
 *
 * @cdk.module test
 *
 * @author      Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2004-06-13
 */
public class HydrogenAdder3Test extends HydrogenAdderTest {

    public HydrogenAdder3Test(String name) {
        super(name);
    }

    /**
     * The JUnit setup method
     */
    public void setUp() {
        try {
            ValencyHybridChecker checker = new ValencyHybridChecker();
            adder = new HydrogenAdder(checker);
        } catch (Exception exception) {
            fail("Could not setup HydrogenAdder3Test: " + exception.getMessage());
        }
    }

    /**
     * A unit test suite for JUnit
     *
     * @return    The test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(HydrogenAdder3Test.class);
        return suite;
    }

    public void testNaCl() {
        Molecule mol = new Molecule();
        Atom cl = new Atom("Cl");
        cl.setFormalCharge(-1);
        mol.addAtom(cl);
        Atom na = new Atom("Na");
        na.setFormalCharge(+1);
        mol.addAtom(na);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(2, mol.getAtomCount());
        assertEquals(0, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(0, mol.getBondCount(cl));
        assertEquals(0, mol.getBondCount(na));
    }
}

