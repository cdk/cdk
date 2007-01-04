/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-07-30 22:38:18 +0200 (Sun, 30 Jul 2006) $
 * $Revision: 6707 $
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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;

/**
 * @cdk.module test-standard
 *
 * @author     Kai Hartmann
 * @cdk.created    2004-02-20
 */
public class AtomContainerSetManipulatorTest extends CDKTestCase {
	
	MoleculeSet som = new MoleculeSet();
	
	public AtomContainerSetManipulatorTest(String name) {
		super(name);
	}
	
    public static Test suite() {
        TestSuite suite = new TestSuite(AtomContainerSetManipulatorTest.class);
        return suite;
	}

    public void testRemoveAtomAndConnectedElectronContainers() {
        AtomContainer ac=new AtomContainer();
		ac.addAtom(new Atom("C")); // 1
		ac.addAtom(new Atom("C")); // 2
		ac.addAtom(new Atom("C")); // 3
		ac.addBond(new Bond(ac.getAtom(0),ac.getAtom(1),1));
		ac.addBond(new Bond(ac.getAtom(1),ac.getAtom(2),1));
		AtomContainerSet soac=new AtomContainerSet();
		soac.addAtomContainer(ac);
		AtomContainerSetManipulator.removeAtomAndConnectedElectronContainers(soac,ac.getAtom(1));
		assertEquals(2, soac.getAtomContainerCount(), 0.000001);
    }
}

