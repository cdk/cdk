/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.openscience.cdk.test.libio.joelib;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.libio.joelib.*;
import org.openscience.cdk.smiles.*;
import joelib.molecule.JOEMol;
import joelib.molecule.JOEAtom;
import joelib.molecule.JOEBond;
import junit.framework.*;
import org.openscience.cdk.isomorphism.IsomorphismTester;

/**
 * @cdkPackage test-libio
 */
public class JOELibIOTest extends TestCase {

    public JOELibIOTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(JOELibIOTest.class);
    }


    public void testAtom() {
        Atom a = new Atom("C");
        a.setX3D(1.0);
        a.setY3D(2.0);
        a.setZ3D(3.0);

        JOEAtom converted = Convertor.convert(a);
        Atom reverted = Convertor.convert(converted);

        assertTrue(a.getX3D() == reverted.getX3D());
        assertTrue(a.getY3D() == reverted.getY3D());
        assertTrue(a.getZ3D() == reverted.getZ3D());
    }

    public void testBond() {
        Atom a = new Atom("C");
        Atom b = new Atom("O");
        Bond bond = new Bond(a,b,2);

        JOEBond converted = Convertor.convert(bond);
        Bond reverted = Convertor.convert(converted);

        assertTrue(bond.getOrder() == reverted.getOrder());
    }

    public void testBenzene() {
        Molecule mol = new Molecule();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5

        mol.addBond(0, 1, 1); // 1
        mol.addBond(1, 2, 2); // 2
        mol.addBond(2, 3, 1); // 3
        mol.addBond(3, 4, 2); // 4
        mol.addBond(4, 5, 1); // 5
        mol.addBond(5, 0, 2); // 6

        JOEMol converted = Convertor.convert(mol);
        Molecule reverted = Convertor.convert(converted);

        assertEquals(mol.getAtomCount(), reverted.getAtomCount());
        assertEquals(mol.getBondCount(), reverted.getBondCount());

        try {
            IsomorphismTester it = new IsomorphismTester(mol);
            assertTrue(it.isIsomorphic(reverted));
        } catch (NoSuchAtomException e) {
            assertTrue(false);
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(JOELibIOTest.class));
    }
}
