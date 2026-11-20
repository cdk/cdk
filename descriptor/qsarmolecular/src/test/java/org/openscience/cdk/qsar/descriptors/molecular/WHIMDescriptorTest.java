/*
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.qsar.descriptors.molecular;

import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;

/**
 * TestSuite that runs all QSAR tests.
 *
 */

class WHIMDescriptorTest extends MolecularDescriptorTest {

    WHIMDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(WHIMDescriptor.class);
    }

    @Test
    void testSymmetryWithNoSymmetricAtoms() {
        // AGLYSL01 glycine with explicit hydrogens from the MMFF94 validation suite.
        String[] symbols = {"C", "C", "H", "H", "H", "H", "H", "N", "O", "O"};
        double[][] coordinates = {
                {-1.6234, 1.6965, 8.8431},
                {-1.5438, 0.1710, 8.8960},
                {-1.5827, 1.7640, 6.8094},
                {-0.1271, 1.8630, 7.4736},
                {-2.6707, 1.9987, 8.9343},
                {-1.0462, 2.1092, 9.6756},
                {-2.3655, 0.3289, 10.6732},
                {-1.0818, 2.2182, 7.5784},
                {-2.0344, -0.3437, 10.0478},
                {-1.0893, -0.5326, 8.0048}
        };
        IAtomContainer mol = new AtomContainer();
        for (int i = 0; i < symbols.length; i++) {
            Atom atom = new Atom(symbols[i], new Point3d(coordinates[i]));
            atom.setImplicitHydrogenCount(0);
            mol.addAtom(atom);
        }
        int[][] singleBonds = {{0, 1}, {0, 4}, {0, 5}, {0, 7}, {1, 8}, {2, 7}, {3, 7}, {6, 8}};
        for (int[] bond : singleBonds)
            mol.addBond(bond[0], bond[1], IBond.Order.SINGLE);
        mol.addBond(1, 9, IBond.Order.DOUBLE);

        // ns = 0, na = 10 on each axis: gamma = 1 / (1 + log2(10)).
        assertSymmetryDescriptors(mol, 0.2313782131597592);
    }

    @Test
    void testSymmetryWithNonzeroClassProbabilities() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C", new Point3d(0, 0, 0)));
        double[][] coordinates = {{0.5, 0.75, 1}, {0.5, -0.75, -1},
                {-0.5, 0.75, -1}, {-0.5, -0.75, 1}};
        for (double[] point : coordinates) {
            mol.addAtom(new Atom("H", new Point3d(point)));
            mol.addBond(0, mol.getAtomCount() - 1, IBond.Order.SINGLE);
        }

        // Diagonal covariance: four H atoms have opposite scores; the lone C at zero does not.
        // ns = 4, na = 1: gamma = 1 / (1 - (4/5) log2(4/5) - (1/5) log2(1/5)).
        assertSymmetryDescriptors(mol, 0.5807443429078922);
    }

    private void assertSymmetryDescriptors(IAtomContainer mol, double expected) {
        DescriptorValue value = descriptor.calculate(mol);
        Assertions.assertNull(value.getException());
        DoubleArrayResult result = (DoubleArrayResult) value.getValue();
        String[] names = value.getNames();
        for (int i : new int[]{5, 6, 7, 15}) {
            Assertions.assertTrue(Double.isFinite(result.get(i)), names[i]);
            Assertions.assertEquals(expected, result.get(i), 1e-10, names[i]);
        }
    }
}
