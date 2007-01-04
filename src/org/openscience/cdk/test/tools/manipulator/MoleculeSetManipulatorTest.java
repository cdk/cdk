/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-04-06 19:24:31 +0200 (Thu, 06 Apr 2006) $
 * $Revision: 5897 $
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.tools.manipulator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.MoleculeSetManipulator;

/**
 * @cdk.module test-standard
 *
 * @author     Kai Hartmann
 * @cdk.created    2004-02-20
 */
public class MoleculeSetManipulatorTest extends CDKTestCase {
	
	MoleculeSet som = new MoleculeSet();
	
	public MoleculeSetManipulatorTest(String name) {
		super(name);
	}
	
    public void setUp() {
		Molecule mol1 = new Molecule();
		Atom atom1 = new Atom("Cl");
		atom1.setCharge(-1.0);
		atom1.setFormalCharge(-1);
		atom1.setHydrogenCount(1);
		mol1.addAtom(atom1);
		Molecule mol2 = new Molecule();
		Atom atom2 = new Atom("O");
		atom2.setHydrogenCount(2);
		mol2.addAtom(atom2);
		som.addMolecule(mol1);
		som.addMolecule(mol2);
	}
	
    public static Test suite() {
        TestSuite suite = new TestSuite(MoleculeSetManipulatorTest.class);
        return suite;
	}

    public void testGetTotalCharge() {
        double charge = MoleculeSetManipulator.getTotalCharge(som);
		assertEquals(-1.0, charge, 0.000001);
    }
	
	public void testGetTotalFormalCharge() {
        double charge = MoleculeSetManipulator.getTotalFormalCharge(som);
		assertEquals(-1.0, charge, 0.000001);
    }
	
	public void testGetTotalHydrogenCount() {
		int hCount = MoleculeSetManipulator.getTotalHydrogenCount(som);
		assertEquals(3, hCount);
	}
}

