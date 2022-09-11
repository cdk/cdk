/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.qsar.descriptors.atomic;

import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 */
class DistanceToAtomDescriptorTest extends AtomicDescriptorTest {

    DistanceToAtomDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(DistanceToAtomDescriptor.class);
    }

    @Test
    void testDistanceToAtomDescriptor() throws java.lang.Exception {
        IAtomicDescriptor descriptor = new DistanceToAtomDescriptor();
        Object[] params = {2};
        descriptor.setParameters(params);

        IAtomContainer mol = new AtomContainer();
        Atom a0 = new Atom("C");
        mol.addAtom(a0);
        a0.setPoint3d(new Point3d(1.2492, -0.2810, 0.0000));
        Atom a1 = new Atom("C");
        mol.addAtom(a1);
        a1.setPoint3d(new Point3d(0.0000, 0.6024, -0.0000));
        Atom a2 = new Atom("C");
        mol.addAtom(a2);
        a2.setPoint3d(new Point3d(-1.2492, -0.2810, 0.0000));
        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        // mol.addBond(2, 3, IBond.Order.SINGLE); // 3 // jwmay: there is no atom at index 3

        Assertions.assertEquals(2.46, ((DoubleResult) descriptor.calculate(mol.getAtom(0), mol).getValue()).doubleValue(), 0.1);
    }
}
