/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.libio.jmol;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.libio.jmol.Convertor;
import junit.framework.*;
import org.openscience.cdk.isomorphism.IsomorphismTester;

/**
 * @cdkPackage test-libio
 */
public class JmolLibIOTest extends TestCase {

    public JmolLibIOTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(JmolLibIOTest.class);
    }


    public void testAtom() {
        Atom a = new Atom("C");
        a.setX3D(1.0);
        a.setY3D(2.0);
        a.setZ3D(3.0);

        org.openscience.jmol.Atom converted = Convertor.convert(a);
        Atom reverted = Convertor.convert(converted);

        assertTrue(a.getX3D() == reverted.getX3D());
        assertTrue(a.getY3D() == reverted.getY3D());
        assertTrue(a.getZ3D() == reverted.getZ3D());
    }

    public void testMolecule() {
        Molecule mol = new Molecule();
        mol.addAtom(new Atom("C", new javax.vecmath.Point3d(0.0, 0.0, 0.0)));
        mol.addAtom(new Atom("O", new javax.vecmath.Point3d(1.0, 2.0, 3.0)));
        mol.addAtom(new Atom("N", new javax.vecmath.Point3d(2.0, 1.0, 1.5)));

        org.openscience.jmol.ChemFrame converted = Convertor.convert(mol);
        AtomContainer reverted = Convertor.convert(converted);

        assertEquals(mol.getAtomCount(), reverted.getAtomCount());
        int NOatoms = mol.getAtomCount();
        for (int i=0; i<NOatoms; i++) {
            System.out.println("i: " + i);
            Atom a = mol.getAtomAt(i);
            Atom b = reverted.getAtomAt(i);
            assertTrue(a.getX3D() == b.getX3D());
            assertTrue(a.getY3D() == b.getY3D());
            assertTrue(a.getZ3D() == b.getZ3D());
        }
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(JmolLibIOTest.class));
    }
}
