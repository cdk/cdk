/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Hardware Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Hardware
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.atomic;

import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 * @cdk.module test-qsaratomic
 */
public class InductiveAtomicHardnessDescriptorTest extends AtomicDescriptorTest {

    public InductiveAtomicHardnessDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(InductiveAtomicHardnessDescriptor.class);
    }

    @Test
    public void testInductiveAtomicHardnessDescriptor() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {1.28};

        Point3d c_coord = new Point3d(1.392, 0.0, 0.0);
        Point3d f_coord = new Point3d(0.0, 0.0, 0.0);
        Point3d h1_coord = new Point3d(1.7439615035767404, 1.0558845107302222, 0.0);
        Point3d h2_coord = new Point3d(1.7439615035767404, -0.5279422553651107, 0.914422809754875);
        Point3d h3_coord = new Point3d(1.7439615035767402, -0.5279422553651113, -0.9144228097548747);

        IAtomContainer mol = new AtomContainer(); // molecule is CF

        Atom c = new Atom("C");
        mol.addAtom(c);
        c.setPoint3d(c_coord);

        Atom f = new Atom("F");
        mol.addAtom(f);
        f.setPoint3d(f_coord);

        Atom h1 = new Atom("H");
        mol.addAtom(h1);
        h1.setPoint3d(h1_coord);

        Atom h2 = new Atom("H");
        mol.addAtom(h2);
        h2.setPoint3d(h2_coord);

        Atom h3 = new Atom("H");
        mol.addAtom(h3);
        h3.setPoint3d(h3_coord);

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(0, 2, IBond.Order.SINGLE); // 1
        mol.addBond(0, 3, IBond.Order.SINGLE); // 1
        mol.addBond(0, 4, IBond.Order.SINGLE); // 1

        IAtomicDescriptor descriptor = new InductiveAtomicHardnessDescriptor();

        double retval = ((DoubleResult) descriptor.calculate(mol.getAtom(0), mol).getValue()).doubleValue();
        Assert.assertEquals(testResult[0], retval, 0.1);
    }
}
