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
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */
class PetitjeanNumberDescriptorTest extends MolecularDescriptorTest {

    PetitjeanNumberDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(PetitjeanNumberDescriptor.class);
    }

    @Test
    void testPetitjeanNumberDescriptor() throws java.lang.Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=C(O)CC");
        AtomContainerManipulator.removeHydrogens(mol);
        Assertions.assertEquals(0.33333334, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.01);
    }

    @Test
    void testSingleAtomCase() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O");
        Assertions.assertEquals(0, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.01);
    }
}
