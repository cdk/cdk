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
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs a test for the AtomCountDescriptor.
 *
 * @cdk.module test-qsarmolecular
 */

class AtomCountDescriptorTest extends MolecularDescriptorTest {

    AtomCountDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(AtomCountDescriptor.class);
    }

    @Test
    void testCarbonCount() throws java.lang.Exception {
        Object[] params = {"C"};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCO"); // ethanol
        DescriptorValue value = descriptor.calculate(mol);
        Assertions.assertEquals(2, ((IntegerResult) value.getValue()).intValue());
        Assertions.assertEquals(1, value.getNames().length);
        Assertions.assertEquals("nC", value.getNames()[0]);
        Assertions.assertEquals(descriptor.getDescriptorNames()[0], value.getNames()[0]);
    }

    @Test
    void testImplicitExplicitH() throws Exception {
        Object[] params = {"*"};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C"); // ethanol
        DescriptorValue value = descriptor.calculate(mol);
        Assertions.assertEquals(5, ((IntegerResult) value.getValue()).intValue());

        mol = sp.parseSmiles("[C]"); // ethanol
        value = descriptor.calculate(mol);
        Assertions.assertEquals(1, ((IntegerResult) value.getValue()).intValue());
    }
}
