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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

class KappaShapeIndicesDescriptorTest extends MolecularDescriptorTest {

    KappaShapeIndicesDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(KappaShapeIndicesDescriptor.class);
    }

    @Test
    void testKappaShapeIndicesDescriptor() throws java.lang.Exception {
        double[] testResult = {5, 2.25, 4};
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=C(O)CC");
        AtomContainerManipulator.removeHydrogens(mol);
        DoubleArrayResult retval = (DoubleArrayResult) descriptor.calculate(mol).getValue();
        // position 0 =  kier1
        // positions 1 = kier2
        // THIS IS OK: Assert.assertEquals(testResult[1], ((Double)retval.get(1)).doubleValue(), 0.0001);
        // THIS IS OK: Assert.assertEquals(testResult[0], ((Double)retval.get(0)).doubleValue(), 0.0001);
        Assertions.assertEquals(testResult[2], retval.get(2), 0.0001);
    }
}
